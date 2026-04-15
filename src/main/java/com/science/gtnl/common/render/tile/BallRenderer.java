package com.science.gtnl.common.render.tile;

import static tectech.Reference.MODID;
import static tectech.thing.block.TileEntityEyeOfHarmony.generateRandomFloat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.api.IRenderAngle;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gtneioreplugin.plugin.block.ModBlocks;
import lombok.Getter;
import tectech.rendering.EOH.EOHRenderingUtils;
import tectech.rendering.EOH.EOHTileEntitySR;
import tectech.thing.block.TileEntityEyeOfHarmony;

@SideOnly(Side.CLIENT)
public class BallRenderer {

    public static double GRAVITY = -0.009d;
    public static double BOUNCE_DAMPING = 0.7d;
    public static double DAMPING = 0.985d;
    public static double FRICTION = 0.92d;

    public static double ANGULAR_DAMPING = 0.85d;

    public static double PUSH_RADIUS = 0.4;
    public static double IMPULSE_STRENGTH = 0.08d;
    public static double LIFT_STRENGTH = 0.12d;

    public static double BALL_RADIUS = 0.7;

    @Getter
    public static ArrayList<TileEntityEyeOfHarmony.OrbitingObject> orbitingObjects = new ArrayList<>();

    public static Map<IMetaTileEntity, VisualState> visualStateMap = new WeakHashMap<>();

    public static void renderTileEntity(IMetaTileEntity metaTile, double x, double y, double z, float partialTicks) {
        VisualState state = visualStateMap.computeIfAbsent(metaTile, te -> new VisualState());

        updatePhysics(metaTile, state);

        GL11.glPushMatrix();

        GL11.glTranslated(x + 0.5 + state.offsetX, y + 0.5 + state.offsetY, z + 0.5 + state.offsetZ);
        GL11.glRotated(state.rotation, 0, 1, 0);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_LIGHTING);

        renderOuterSpaceShell();
        renderOrbitObjects((float) ((IRenderAngle) metaTile).getRenderAngle());
        renderStar(IItemRenderer.ItemRenderType.INVENTORY, 1);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void updatePhysics(IMetaTileEntity metaTile, VisualState state) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        World world = mc.theWorld;
        IGregTechTileEntity te = metaTile.getBaseMetaTileEntity();

        double px = te.getXCoord() + 0.5 + state.offsetX;
        double py = te.getYCoord() + 0.5 + state.offsetY;
        double pz = te.getZCoord() + 0.5 + state.offsetZ;

        AxisAlignedBB ballBox = AxisAlignedBB.getBoundingBox(
            px - BALL_RADIUS,
            py - BALL_RADIUS,
            pz - BALL_RADIUS,
            px + BALL_RADIUS,
            py + BALL_RADIUS,
            pz + BALL_RADIUS);

        AxisAlignedBB playerBox = player.boundingBox;

        if (ballBox.expand(PUSH_RADIUS, 0.1, PUSH_RADIUS)
            .intersectsWith(playerBox)) {

            double dx = px - player.posX;
            double dz = pz - player.posZ;
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > 0.001) {
                dx /= dist;
                dz /= dist;

                double speed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
                double impulse = IMPULSE_STRENGTH + speed * 0.25;

                state.velocityX += dx * impulse;
                state.velocityZ += dz * impulse;

                state.velocityY += LIFT_STRENGTH * (0.5 + speed);

                state.angularVelocity += (dz - dx) * 20;
            }
        }

        state.velocityY += GRAVITY;

        state.offsetX += state.velocityX;
        state.offsetY += state.velocityY;
        state.offsetZ += state.velocityZ;

        px = te.getXCoord() + 0.5 + state.offsetX;
        py = te.getYCoord() + 0.5 + state.offsetY;
        pz = te.getZCoord() + 0.5 + state.offsetZ;

        ballBox = AxisAlignedBB.getBoundingBox(
            px - BALL_RADIUS,
            py - BALL_RADIUS,
            pz - BALL_RADIUS,
            px + BALL_RADIUS,
            py + BALL_RADIUS,
            pz + BALL_RADIUS);

        int minX = (int) Math.floor(px - BALL_RADIUS - 1);
        int maxX = (int) Math.floor(px + BALL_RADIUS + 1);
        int minY = (int) Math.floor(py - BALL_RADIUS - 1);
        int maxY = (int) Math.floor(py + BALL_RADIUS + 1);
        int minZ = (int) Math.floor(pz - BALL_RADIUS - 1);
        int maxZ = (int) Math.floor(pz + BALL_RADIUS + 1);

        for (int bx = minX; bx <= maxX; bx++) {
            for (int by = minY; by <= maxY; by++) {
                for (int bz = minZ; bz <= maxZ; bz++) {

                    Block block = world.getBlock(bx, by, bz);
                    if (block == null || block.isAir(world, bx, by, bz)) continue;

                    AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(world, bx, by, bz);
                    if (aabb == null) continue;

                    if (ballBox.intersectsWith(aabb)) {

                        resolveCollision(state, ballBox, aabb);

                        px = te.getXCoord() + 0.5 + state.offsetX;
                        py = te.getYCoord() + 0.5 + state.offsetY;
                        pz = te.getZCoord() + 0.5 + state.offsetZ;

                        ballBox = AxisAlignedBB.getBoundingBox(
                            px - BALL_RADIUS,
                            py - BALL_RADIUS,
                            pz - BALL_RADIUS,
                            px + BALL_RADIUS,
                            py + BALL_RADIUS,
                            pz + BALL_RADIUS);
                    }
                }
            }
        }

        state.rotation += state.angularVelocity;

        state.velocityX *= DAMPING;
        state.velocityZ *= DAMPING;
        state.velocityY *= DAMPING;

        state.angularVelocity *= ANGULAR_DAMPING;

        if (Math.abs(state.velocityY) < 0.01) {
            state.velocityX *= FRICTION;
            state.velocityZ *= FRICTION;
        }
    }

    private static void resolveCollision(VisualState state, AxisAlignedBB ball, AxisAlignedBB block) {
        double dx1 = block.maxX - ball.minX;
        double dx2 = ball.maxX - block.minX;
        double dz1 = block.maxZ - ball.minZ;
        double dz2 = ball.maxZ - block.minZ;
        double dy1 = block.maxY - ball.minY;
        double dy2 = ball.maxY - block.minY;

        double min = Math.min(Math.min(Math.min(dx1, dx2), Math.min(dz1, dz2)), Math.min(dy1, dy2));

        if (min == dx1) {
            state.offsetX += dx1;
            state.velocityX = Math.max(0, state.velocityX) * -BOUNCE_DAMPING;
        } else if (min == dx2) {
            state.offsetX -= dx2;
            state.velocityX = Math.min(0, state.velocityX) * -BOUNCE_DAMPING;
        } else if (min == dz1) {
            state.offsetZ += dz1;
            state.velocityZ = Math.max(0, state.velocityZ) * -BOUNCE_DAMPING;
        } else if (min == dz2) {
            state.offsetZ -= dz2;
            state.velocityZ = Math.min(0, state.velocityZ) * -BOUNCE_DAMPING;
        } else if (min == dy1) {
            state.offsetY += dy1;
            if (state.velocityY < 0) {
                state.velocityY = -state.velocityY * BOUNCE_DAMPING;
            }
        } else if (min == dy2) {
            state.offsetY -= dy2;
            state.velocityY = Math.min(0, state.velocityY) * -BOUNCE_DAMPING;
        }
    }

    public static void renderStar(IItemRenderer.ItemRenderType type, Color color, int size) {
        GL11.glPushMatrix();
        GL11.glScalef(0.05f, 0.05f, 0.05f);

        if (type == IItemRenderer.ItemRenderType.INVENTORY) GL11.glRotated(180, 0, 1, 0);

        EOHRenderingUtils.renderStarLayer(0, EOHTileEntitySR.STAR_LAYER_0, color, 1.0f, size);
        EOHRenderingUtils.renderStarLayer(1, EOHTileEntitySR.STAR_LAYER_1, color, 0.4f, size);
        EOHRenderingUtils.renderStarLayer(2, EOHTileEntitySR.STAR_LAYER_2, color, 0.2f, size);

        GL11.glPopMatrix();
    }

    public static void renderStar(IItemRenderer.ItemRenderType type, int size) {
        renderStar(type, new Color(1.0f, 0.4f, 0.05f, 1.0f), size);
    }

    public static void renderOuterSpaceShell() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glPushMatrix();
        GL11.glScalef(0.05f, 0.05f, 0.05f);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);

        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(new ResourceLocation(MODID, "models/spaceLayer.png"));

        float scale = 0.01f * 17.5f;
        GL11.glScalef(scale, scale, scale);

        GL11.glColor4f(1, 1, 1, 1);

        EOHTileEntitySR.spaceModel.renderAll();

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void renderOrbitObjects(float angle) {
        if (orbitingObjects.isEmpty()) generateImportantInfo();
        for (TileEntityEyeOfHarmony.OrbitingObject t : orbitingObjects) {
            renderOrbit(t, angle);
        }
    }

    public static void renderOrbit(final TileEntityEyeOfHarmony.OrbitingObject orbitingObject, float angle) {
        GL11.glPushMatrix();
        GL11.glScalef(0.05f, 0.05f, 0.05f);

        GL11.glRotatef(orbitingObject.zAngle, 0, 0, 1);
        GL11.glRotatef(orbitingObject.xAngle, 1, 0, 0);
        GL11.glRotatef((orbitingObject.rotationSpeed * angle) % 360, 0, 1, 0);

        GL11.glTranslated(-orbitingObject.distance, 0, 0);

        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        EOHRenderingUtils.renderBlockInWorld(orbitingObject.block, 0, orbitingObject.scale);

        GL11.glPopMatrix();
    }

    public static void generateImportantInfo() {
        int index = 0;
        for (Block block : selectNRandomElements(ModBlocks.blocks.values(), 10)) {
            float MAX_ANGLE = 30;
            float xAngle = generateRandomFloat(-MAX_ANGLE, MAX_ANGLE);
            float zAngle = generateRandomFloat(-MAX_ANGLE, MAX_ANGLE);
            index += 1;
            float distance = index + generateRandomFloat(-0.2f, 0.2f);
            float scale = generateRandomFloat(0.2f, 0.9f);
            float rotationSpeed = generateRandomFloat(0.5f, 1.5f);
            float orbitSpeed = generateRandomFloat(0.5f, 1.5f);
            orbitingObjects.add(
                new TileEntityEyeOfHarmony.OrbitingObject(
                    block,
                    distance,
                    rotationSpeed,
                    orbitSpeed,
                    xAngle,
                    zAngle,
                    scale));
        }
    }

    public static <T> ArrayList<T> selectNRandomElements(Collection<T> inputList, long n) {
        ArrayList<T> randomElements = new ArrayList<>((int) n);
        ArrayList<T> inputArray = new ArrayList<>(inputList);
        Random rand = new Random();

        IntStream.range(0, (int) n)
            .forEach(i -> {
                int randomIndex = rand.nextInt(inputArray.size());
                randomElements.add(inputArray.get(randomIndex));
                inputArray.remove(randomIndex);
            });

        return randomElements;
    }

    public static class VisualState {

        double offsetX, offsetY, offsetZ;
        double velocityX, velocityY, velocityZ;
        double rotation;
        double angularVelocity;
    }
}
