package com.science.gtnl.common.render.tile;

import static tectech.Reference.MODID;
import static tectech.rendering.EOH.EOHRenderingUtils.*;
import static tectech.rendering.EOH.EOHTileEntitySR.*;
import static tectech.rendering.EOH.EOHTileEntitySR.STAR_LAYER_2;
import static tectech.thing.block.TileEntityEyeOfHarmony.*;

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

import com.science.gtnl.common.machine.multiblock.MeteorMiner;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gtneioreplugin.plugin.block.ModBlocks;
import lombok.Getter;
import tectech.thing.block.TileEntityEyeOfHarmony;

@SideOnly(Side.CLIENT)
public class MeteorMinerRenderer {

    public static double GRAVITY = -0.001d;
    public static double BOUNCE_DAMPING = 0.9d;
    public static double DAMPING = 0.98d;
    public static double PUSH_RADIUS = 0.8;
    public static double IMPULSE_STRENGTH = 0.04d;

    @Getter
    public static ArrayList<TileEntityEyeOfHarmony.OrbitingObject> orbitingObjects = new ArrayList<>();

    public static Map<MeteorMiner, VisualState> visualStateMap = new WeakHashMap<>();

    public static void renderTileEntity(MeteorMiner meteorMiner, double x, double y, double z, float partialTicks) {
        VisualState state = visualStateMap.computeIfAbsent(meteorMiner, te -> new VisualState());
        updateVisualKick(meteorMiner, state);

        GL11.glPushMatrix();

        GL11.glTranslated(x + 0.5 + state.offsetX, y + 0.5 + state.offsetY, z + 0.5 + state.offsetZ);
        GL11.glRotated(state.rotation, 0, 1, 0);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        renderOuterSpaceShell();
        renderOrbitObjects(meteorMiner.getRenderAngle());
        renderStar(IItemRenderer.ItemRenderType.INVENTORY, 1);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void updateVisualKick(MeteorMiner meteorMiner, VisualState state) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        IGregTechTileEntity tileEntity = meteorMiner.getBaseMetaTileEntity();
        World world = Minecraft.getMinecraft().theWorld;

        double centerX = tileEntity.getXCoord() + state.offsetX + 0.5;
        double centerY = tileEntity.getYCoord() + state.offsetY + 0.5;
        double centerZ = tileEntity.getZCoord() + state.offsetZ + 0.5;

        double dx = player.posX - centerX;
        double dy = player.boundingBox.minY - centerY;
        double dz = player.posZ - centerZ;

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist > 1e-4 && dist < PUSH_RADIUS) {
            dx /= dist;
            dz /= dist;

            state.velocityX += -dx * IMPULSE_STRENGTH;
            state.velocityZ += -dz * IMPULSE_STRENGTH;

            state.angularVelocity += dz * 10 - dx * 10;
        }

        boolean isSupported = false;
        if (world != null) {
            int belowX = (int) (tileEntity.getXCoord() + state.offsetX);
            int belowZ = (int) (tileEntity.getZCoord() + state.offsetZ);
            double renderY = tileEntity.getYCoord() + state.offsetY;

            int blockY = (int) (renderY - 0.5);
            Block blockBelow = world.getBlock(belowX, blockY, belowZ);
            if (blockBelow != null && !blockBelow.isAir(world, belowX, blockY, belowZ)) {
                AxisAlignedBB aabb = blockBelow.getCollisionBoundingBoxFromPool(world, belowX, blockY, belowZ);
                if (aabb != null) {
                    if (renderY <= aabb.maxY + 0.1) {
                        isSupported = true;
                    }
                }
            }
        }

        if (!isSupported) {
            state.velocityY += GRAVITY;
        } else {
            state.velocityY = -state.velocityY * BOUNCE_DAMPING;
        }

        state.offsetX += state.velocityX;
        state.offsetZ += state.velocityZ;
        state.offsetY += state.velocityY;
        state.rotation += state.angularVelocity;

        state.velocityX *= DAMPING;
        state.velocityZ *= DAMPING;
        state.angularVelocity *= DAMPING;
        state.velocityY *= DAMPING;

        if (tileEntity.getYCoord() + state.offsetY < -128 || state.offsetZ > 256
            || state.offsetZ < -256
            || state.offsetX > 256
            || state.offsetX < -256) {
            state.offsetX = 0;
            state.offsetY = 0;
            state.offsetZ = 0;
            state.velocityX = 0;
            state.velocityY = 0;
            state.velocityZ = 0;
        }
    }

    public static void renderStar(IItemRenderer.ItemRenderType type, Color color, int size) {
        GL11.glPushMatrix();
        GL11.glScalef(0.05f, 0.05f, 0.05f);

        if (type == IItemRenderer.ItemRenderType.INVENTORY) GL11.glRotated(180, 0, 1, 0);
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED
            || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glTranslated(0.5, 0.5, 0.5);
                if (type == IItemRenderer.ItemRenderType.EQUIPPED) GL11.glRotated(90, 0, 1, 0);
            }

        renderStarLayer(0, STAR_LAYER_0, color, 1.0f, size);
        renderStarLayer(1, STAR_LAYER_1, color, 0.4f, size);
        renderStarLayer(2, STAR_LAYER_2, color, 0.2f, size);

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
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(new ResourceLocation(MODID, "models/spaceLayer.png"));

        float scale = 0.01f * 17.5f;
        GL11.glScalef(scale, scale, scale);

        GL11.glColor4f(1, 1, 1, 1);

        spaceModel.renderAll();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
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

    public static void renderOrbitObjects(float angle) {
        if (orbitingObjects != null) {
            if (orbitingObjects.isEmpty()) {
                generateImportantInfo();
            }
            for (TileEntityEyeOfHarmony.OrbitingObject t : orbitingObjects) {
                renderOrbit(t, angle);
            }
        }
    }

    public static void renderOrbit(final TileEntityEyeOfHarmony.OrbitingObject orbitingObject, float angle) {
        GL11.glPushMatrix();
        GL11.glScalef(0.05f, 0.05f, 0.05f);

        GL11.glRotatef(orbitingObject.zAngle, 0, 0, 1);
        GL11.glRotatef(orbitingObject.xAngle, 1, 0, 0);
        GL11.glRotatef((orbitingObject.rotationSpeed * 1.5f * angle) % 360.0f, 0F, 1F, 0F);
        float STAR_RESCALE = 0.2f;
        GL11.glTranslated(-0.2 - orbitingObject.distance - STAR_RESCALE * 10, 0, 0);
        GL11.glRotatef((orbitingObject.orbitSpeed * 1.5f * angle) % 360.0f, 0F, 1F, 0F);

        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        renderBlockInWorld(orbitingObject.block, 0, orbitingObject.scale);

        GL11.glPopMatrix();
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

        double offsetX = 0;
        double offsetZ = 0;
        double offsetY = 0;
        double velocityX = 0;
        double velocityZ = 0;
        double velocityY = 0;
        double rotation = 0;
        double angularVelocity = 0;
    }
}
