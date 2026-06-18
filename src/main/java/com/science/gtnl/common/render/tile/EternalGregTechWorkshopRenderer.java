package com.science.gtnl.common.render.tile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.shader.ShaderProgram;
import com.gtnewhorizon.gtnhlib.client.renderer.vao.IVertexArrayObject;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EternalGregTechWorkshop;
import com.science.gtnl.config.MainConfig;

import goodgenerator.loader.Loaders;
import gregtech.GTMod;
import gregtech.api.GregTechAPI;
import tectech.Reference;
import tectech.thing.casing.TTCasingsContainer;
import tectech.util.StructureVBO;

public class EternalGregTechWorkshopRenderer extends TileEntitySpecialRenderer {

    private static boolean initialized = false;
    private static boolean failedInit = false;

    private IVertexArrayObject ring;

    private static ShaderProgram fadeBypassProgram;

    private void initRings() {
        StructureVBO ringStructure = (new StructureVBO()).addMapping('C', GregTechAPI.sBlockCasings1, 13)
            .addMapping('E', GregTechAPI.sBlockCasings10, 11)
            .addMapping('H', GregTechAPI.sBlockCasings1, 14)
            .addMapping('L', GregTechAPI.sBlockGlass1, 2)
            .addMapping('P', TTCasingsContainer.sBlockCasingsBA0, 10)
            .addMapping('Q', TTCasingsContainer.GodforgeCasings, 7)
            .addMapping('T', Loaders.gravityStabilizationCasing, 0)
            .addMapping('V', TTCasingsContainer.TimeAccelerationFieldGenerator, 8)
            .addMapping('W', TTCasingsContainer.sBlockCasingsBA0, 11);

        ring = ringStructure.assignStructure(EternalGregTechWorkshop.shapeExtra)
            .build();

        fadeBypassProgram = new ShaderProgram(
            Reference.MODID,
            "shaders/fadebypass.vert.glsl",
            "shaders/fadebypass.frag.glsl");
        ringStructure.getTextureUpdateRequestor()
            .requestUpdate();
    }

    private void renderRings(TileEntityEternalGregTechWorkshop tile, double x, double y, double z, float timer) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        bindTexture(TextureMap.locationBlocksTexture);
        fadeBypassProgram.use();
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);
        applyBaseOrientation(tile);

        int renderCount = tile.getRenderCount();
        boolean spiralEnabled = MainConfig.machine.eternal_gregtech_workshop.spiralRender;
        for (int i = 0; i < renderCount; i++) {
            renderRingPair(tile, timer, i, spiralEnabled);
        }

        GL11.glPopMatrix();
        ShaderProgram.clear();
        GL11.glPopAttrib();
    }

    private void applyBaseOrientation(TileEntityEternalGregTechWorkshop tile) {
        float angle = (tile.getRotation() * 90f + 90f) % 360f;
        if (tile.getOffsetY() == 0) {
            GL11.glRotatef(90.0f, tile.getOffsetX(), 0, tile.getOffsetZ());
            GL11.glRotatef(angle, 0, 1, 0);
            return;
        }
        GL11.glRotatef(90.0f, 0, 0, 1);
        GL11.glRotatef(angle, 1, 0, 0);
    }

    private void renderRingPair(TileEntityEternalGregTechWorkshop tile, float timer, int layerIndex,
        boolean spiralEnabled) {
        int layerOffset = layerIndex * 22;
        float baseRotation = ((layerIndex % 2) == 0 || spiralEnabled ? 1 : -1) * (timer / 6 * 7);
        float spiralRotation = spiralEnabled ? (layerIndex * 5.0f) % 360.0f : 0.0f;
        double offsetMagnitude = 11 + layerOffset;

        renderRing(tile, offsetMagnitude, spiralEnabled ? spiralRotation + baseRotation : baseRotation);
        renderRing(tile, -offsetMagnitude, spiralEnabled ? -5f - spiralRotation + baseRotation : -baseRotation);
    }

    /**
     * This is a dumb renderer. The caller is responsible for configuring GL state beforehand.
     */
    private void renderRing(TileEntityEternalGregTechWorkshop tile, double offsetMagnitude, float axialRotation) {
        GL11.glPushMatrix();
        GL11.glTranslated(
            tile.getOffsetX() * offsetMagnitude,
            tile.getOffsetY() * offsetMagnitude,
            tile.getOffsetZ() * offsetMagnitude);
        GL11.glRotatef(tile.getRotAngle(), tile.getRotAxisX(), tile.getRotAxisY(), tile.getRotAxisZ());
        GL11.glRotatef(axialRotation, 1, 0, 0);
        GL11.glTranslated(0, -1, 0);
        ring.render();
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (failedInit) return;
        if (!(tile instanceof TileEntityEternalGregTechWorkshop egtwRender)) return;

        if (!initialized) {
            try {
                initRings();
                initialized = true;
            } catch (Exception e) {
                ScienceNotLeisure.LOG.error("[EternalGregTechWorkshopRenderer] Ring init failed", e);
                failedInit = true;
                initialized = true;
                return;
            }
        }

        float timer = GTMod.clientProxy()
            .getAnimationRenderTicks();
        renderRings(egtwRender, x, y, z, timer);
    }
}
