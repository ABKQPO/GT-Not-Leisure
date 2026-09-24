package com.science.gtnl.utils.detrav;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import galacticgreg.api.enums.DimensionDef;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.StoneType;
import gregtech.api.enums.TextureSet;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.IOreMaterial;
import gregtech.client.renderer.BillboardRenderHelper;
import gregtech.client.renderer.BillboardRenderHelper.Plane;
import gregtech.common.config.Client;
import gtneioreplugin.util.DimensionHelper;
import lombok.Getter;

@SideOnly(Side.CLIENT)
public class DetravOreMarkerRenderer {

    private static final double MAX_RENDER_DISTANCE = 256d;
    private static final Vector3d VERTEX = new Vector3d();

    @Getter
    private static final List<DetravOreMarker> markers = new ArrayList<>();

    public static void toggleMarker(int dimensionId, int x, int y, int z, String name, int color,
        @Nullable String materialName) {
        for (Iterator<DetravOreMarker> iterator = markers.iterator(); iterator.hasNext();) {
            DetravOreMarker marker = iterator.next();
            if (marker.dimensionId == dimensionId && marker.x == x && marker.y == y && marker.z == z) {
                iterator.remove();
                return;
            }
        }
        int timeoutSeconds = Client.render.detravOreMarkerTimeout;
        long expiresAt = timeoutSeconds > 0 ? System.currentTimeMillis() + timeoutSeconds * 1000L : 0L;
        markers.add(new DetravOreMarker(dimensionId, x, y, z, name, color, resolveIcon(materialName), expiresAt));
    }

    public static void clear() {
        markers.clear();
    }

    @Nullable
    private static IIconContainer resolveIcon(@Nullable String materialName) {
        if (materialName == null || materialName.isEmpty()) {
            return null;
        }
        IOreMaterial material = IOreMaterial.findMaterial(materialName);
        if (material == null) {
            return null;
        }
        TextureSet textureSet = material.getTextureSet();
        if (textureSet == null) {
            return null;
        }
        int index = OrePrefixes.ore.getTextureIndex();
        if (index < 0 || index >= textureSet.mTextures.length) {
            return null;
        }
        return textureSet.mTextures[index];
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) {
            clear();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (markers.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        EntityLivingBase viewEntity = minecraft.renderViewEntity;
        if (player == null || viewEntity == null) {
            return;
        }

        int dimensionId = player.worldObj.provider.dimensionId;
        Vec3 eyeVec = viewEntity.getPosition(event.partialTicks);
        Vector3d eye = new Vector3d(eyeVec.xCoord, eyeVec.yCoord, eyeVec.zCoord);
        Vector3d centre = new Vector3d();
        long now = System.currentTimeMillis();

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator.instance.setTranslation(0, 0, 0);
        IIcon background = resolveBackground(player.worldObj);

        for (Iterator<DetravOreMarker> iterator = markers.iterator(); iterator.hasNext();) {
            DetravOreMarker marker = iterator.next();

            if (marker.expiresAt > 0 && now > marker.expiresAt) {
                iterator.remove();
                continue;
            }
            if (marker.dimensionId != dimensionId) {
                continue;
            }

            double distance = eye.distance(marker.x + 0.5d, marker.y + 0.5d, marker.z + 0.5d);
            if (distance > MAX_RENDER_DISTANCE) {
                continue;
            }

            Plane plane = Plane.lookingAt(centre.set(marker.x + 0.5d, marker.y + 0.5d, marker.z + 0.5d), eye);
            float half = (float) Math.max(0.25d, distance * 0.03d);

            IIconContainer ore = marker.ore;
            if (ore == null || !renderIcon(plane, half, marker.color, ore, background)) {
                renderDiamond(plane, half, marker);
            }
            renderLabel(marker.x + 0.5d - eye.x, marker.y + 0.5d - eye.y, marker.z + 0.5d - eye.z, distance, marker);
        }

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopAttrib();
    }

    private static boolean renderIcon(Plane plane, float half, int color, IIconContainer ore, IIcon background) {
        IIcon icon = ore.getIcon();
        ResourceLocation textureFile = ore.getTextureFile();
        if (icon == null || textureFile == null) {
            return false;
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        Minecraft minecraft = Minecraft.getMinecraft();

        minecraft.getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        iconQuad(plane, background, half, 0xFFFFFF);

        minecraft.getTextureManager()
            .bindTexture(textureFile);
        iconQuad(plane, icon, half, color);
        IIcon overlay = ore.getOverlayIcon();
        if (overlay != null) {
            iconQuad(plane, overlay, half, 0xFFFFFF);
        }
        return true;
    }

    private static IIcon resolveBackground(World world) {
        StoneType stone = StoneType.Stone;
        try {
            String dimensionName = DimensionDef.getDimensionName(world);
            if (dimensionName != null && DimensionHelper.INTERNAL_TO_FULL.containsKey(dimensionName)) {
                List<StoneType> stoneTypes = DimensionHelper.getStoneTypes(dimensionName);
                if (!stoneTypes.isEmpty() && stoneTypes.get(0) != null) {
                    stone = stoneTypes.get(0);
                }
            }
        } catch (RuntimeException ignored) {
            // Dimensions without a stone mapping simply fall back to vanilla stone.
        }
        IIcon icon = stone.getIcon(1);
        return icon != null ? icon : Blocks.stone.getIcon(0, 0);
    }

    private static void renderDiamond(Plane plane, float half, DetravOreMarker marker) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        diamond(plane, half * 1.3F, 0x000000, 160);
        diamond(plane, half, marker.color & 0xFFFFFF, 255);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static void renderLabel(double dx, double dy, double dz, double distance, DetravOreMarker marker) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        float scale = (float) Math.max(0.02d, distance * 0.0025d);
        float lift = (float) Math.max(0.4d, distance * 0.045d);

        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy + lift, dz);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0F, 1F, 0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1F, 0F, 0F);
        GL11.glScalef(-scale, -scale, scale);

        int width = fontRenderer.getStringWidth(marker.name);
        GL11.glDepthMask(false);
        fontRenderer.drawString(marker.name, -width / 2, 0, 0xFFFFFFFF);
        GL11.glDepthMask(true);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
    }

    private static void iconQuad(Plane plane, IIcon icon, float half, int color) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(color & 0xFFFFFF);
        BillboardRenderHelper.addTexturedQuad(tessellator, plane, icon, half, VERTEX);
        tessellator.draw();
    }

    private static void diamond(Plane plane, float half, int rgb, int alpha) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        tessellator.setColorRGBA_I(rgb & 0xFFFFFF, alpha);
        plane.get(0, 0, VERTEX);
        tessellator.addVertex(VERTEX.x, VERTEX.y, VERTEX.z);
        plane.get(0, half, VERTEX);
        tessellator.addVertex(VERTEX.x, VERTEX.y, VERTEX.z);
        plane.get(half, 0, VERTEX);
        tessellator.addVertex(VERTEX.x, VERTEX.y, VERTEX.z);
        plane.get(0, -half, VERTEX);
        tessellator.addVertex(VERTEX.x, VERTEX.y, VERTEX.z);
        plane.get(-half, 0, VERTEX);
        tessellator.addVertex(VERTEX.x, VERTEX.y, VERTEX.z);
        plane.get(0, half, VERTEX);
        tessellator.addVertex(VERTEX.x, VERTEX.y, VERTEX.z);
        tessellator.draw();
    }
}
