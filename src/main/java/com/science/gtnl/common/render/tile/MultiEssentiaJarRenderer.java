package com.science.gtnl.common.render.tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.tile.TileJarRenderer;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileJar;
import thaumcraft.common.tiles.TileJarFillable;

@SideOnly(Side.CLIENT)
public class MultiEssentiaJarRenderer extends TileJarRenderer {

    private static final double LIQUID_MIN = 0.0625D;
    private static final double LIQUID_HEIGHT = 0.625D;
    private static final double LIQUID_MIN_XZ = 0.25D;
    private static final double LIQUID_MAX_XZ = 0.75D;
    private static final double MIN_VISIBLE_LAYER_HEIGHT = 0.015625D;

    @Override
    public void renderTileEntityAt(TileJar tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityMultiEssentiaJar)) {
            super.renderTileEntityAt(tile, x, y, z, partialTicks);
            return;
        }

        GL11.glPushMatrix();
        GL11.glDisable(2884);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.01F, (float) z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        TileJarFillable fillable = (TileJarFillable) tile;
        if (fillable.amount > 0) {
            renderLiquid(fillable, x, y, z, partialTicks);
        }

        if (fillable.aspectFilter != null) {
            renderLabel(fillable);
        }

        bindTexture(tile.getTexture());
        GL11.glEnable(2884);
        GL11.glPopMatrix();
    }

    private void renderLabel(TileJarFillable tile) {
        GL11.glPushMatrix();
        switch (tile.facing) {
            case 3 -> GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            case 4 -> GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
            case 5 -> GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            default -> {}
        }

        float rot = (float) ((tile.aspectFilter.getTag()
            .hashCode() + tile.xCoord + tile.facing) % 4 - 2);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -0.4F, 0.315F);
        if (Config.crooked) {
            GL11.glRotatef(rot, 0.0F, 0.0F, 1.0F);
        }
        UtilsFX.renderQuadCenteredFromTexture("textures/models/label.png", 0.5F, 1.0F, 1.0F, 1.0F, -99, 771, 1.0F);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -0.4F, 0.316F);
        if (Config.crooked) {
            GL11.glRotatef(rot, 0.0F, 0.0F, 1.0F);
        }
        GL11.glScaled(0.021D, 0.021D, 0.021D);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        UtilsFX.drawTag(-8, -8, tile.aspectFilter, 0.0F, 0, 0.0D);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    @Override
    public void renderLiquid(TileJarFillable tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityMultiEssentiaJar jar)) {
            super.renderLiquid(tile, x, y, z, partialTicks);
            return;
        }
        if (field_147501_a.field_147553_e == null || jar.getTotalAmount() <= 0) return;

        AspectList storedAspects = jar.getAspects();
        List<Aspect> renderOrder = getRenderOrder(storedAspects, jar.getActiveAspect());
        if (renderOrder.isEmpty()) return;

        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);

        RenderBlocks renderBlocks = new RenderBlocks();
        Tessellator tessellator = Tessellator.instance;
        IIcon liquidIcon = ((BlockJar) ConfigBlocks.blockJar).iconLiquid;
        field_147501_a.field_147553_e.bindTexture(TextureMap.locationBlocksTexture);

        int brightness = 200;
        if (jar.getWorldObj() != null) {
            brightness = Math.max(
                200,
                ConfigBlocks.blockJar
                    .getMixedBrightnessForBlock(jar.getWorldObj(), jar.xCoord, jar.yCoord, jar.zCoord));
        }

        int totalAmount = jar.getTotalAmount();
        double actualLiquidHeight = LIQUID_HEIGHT * totalAmount / TileEntityMultiEssentiaJar.MAX_CAPACITY;

        double visibleLiquidHeight = Math
            .min(LIQUID_HEIGHT, Math.max(actualLiquidHeight, renderOrder.size() * MIN_VISIBLE_LAYER_HEIGHT));

        double minimumLayerHeight = Math
            .min(MIN_VISIBLE_LAYER_HEIGHT, visibleLiquidHeight / (renderOrder.size() * 2.0D));

        double distributableHeight = visibleLiquidHeight - minimumLayerHeight * renderOrder.size();

        double liquidTop = LIQUID_MIN + visibleLiquidHeight;
        double layerBottom = LIQUID_MIN;

        tessellator.startDrawingQuads();
        tessellator.setBrightness(brightness);

        for (int index = 0; index < renderOrder.size(); index++) {
            Aspect aspect = renderOrder.get(index);
            int amount = storedAspects.getAmount(aspect);
            if (amount <= 0) continue;

            double layerHeight = minimumLayerHeight + distributableHeight * amount / totalAmount;

            double layerTop = index == renderOrder.size() - 1 ? liquidTop
                : Math.min(liquidTop, layerBottom + layerHeight);
            renderBlocks
                .setRenderBounds(LIQUID_MIN_XZ, layerBottom, LIQUID_MIN_XZ, LIQUID_MAX_XZ, layerTop, LIQUID_MAX_XZ);
            tessellator.setColorOpaque_I(aspect.getColor());

            if (index == 0) {
                renderBlocks.renderFaceYNeg(ConfigBlocks.blockJar, -0.5D, 0.0D, -0.5D, liquidIcon);
            }
            renderBlocks.renderFaceYPos(ConfigBlocks.blockJar, -0.5D, 0.0D, -0.5D, liquidIcon);
            renderBlocks.renderFaceZNeg(ConfigBlocks.blockJar, -0.5D, 0.0D, -0.5D, liquidIcon);
            renderBlocks.renderFaceZPos(ConfigBlocks.blockJar, -0.5D, 0.0D, -0.5D, liquidIcon);
            renderBlocks.renderFaceXNeg(ConfigBlocks.blockJar, -0.5D, 0.0D, -0.5D, liquidIcon);
            renderBlocks.renderFaceXPos(ConfigBlocks.blockJar, -0.5D, 0.0D, -0.5D, liquidIcon);

            layerBottom = layerTop;
        }

        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    private static List<Aspect> getRenderOrder(AspectList storedAspects, Aspect activeAspect) {
        List<Aspect> renderOrder = new ArrayList<>();
        for (Aspect aspect : storedAspects.getAspects()) {
            if (aspect != null && storedAspects.getAmount(aspect) > 0) {
                renderOrder.add(aspect);
            }
        }

        renderOrder.sort(Comparator.comparing(Aspect::getTag));
        if (activeAspect != null && renderOrder.remove(activeAspect)) {
            renderOrder.add(activeAspect);
        }
        return renderOrder;
    }
}
