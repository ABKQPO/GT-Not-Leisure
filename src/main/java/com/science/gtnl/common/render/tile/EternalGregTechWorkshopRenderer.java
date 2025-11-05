package com.science.gtnl.common.render.tile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.shader.ShaderProgram;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VertexBuffer;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EternalGregTechWorkshop;
import com.science.gtnl.config.MainConfig;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import tectech.Reference;
import tectech.thing.casing.TTCasingsContainer;
import tectech.util.StructureVBO;
import tectech.util.TextureUpdateRequester;

public class EternalGregTechWorkshopRenderer extends TileEntitySpecialRenderer {

    private static boolean initialized = false;
    private static boolean failedInit = false;

    private VertexBuffer ring;

    private static ShaderProgram fadeBypassProgram;
    private static TextureUpdateRequester textureUpdater;

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

        textureUpdater = ringStructure.getTextureUpdateRequestor();
    }

    private void RenderRings(TileEntityEternalGregTechWorkshop tile, double x, double y, double z, float timer) {
        bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        textureUpdater.requestUpdate();
        fadeBypassProgram.use();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);

        int renderCount = tile.getRenderCount();
        boolean spiralEnabled = MainConfig.enableEternalGregTechWorkshopSpiralRender;

        float rotAngle = tile.getRotAngle();
        float rotAxisX = tile.getRotAxisX();
        float rotAxisY = tile.getRotAxisY();
        float rotAxisZ = tile.getRotAxisZ();
        float offsetX = tile.getOffsetX();
        float offsetY = tile.getOffsetY();
        float offsetZ = tile.getOffsetZ();
        int rotation = tile.getRotation();

        float angle = (rotation * 90f + 90f) % 360f;
        if (offsetY == 0) {
            GL11.glRotatef(90.0f, offsetX, 0, offsetZ);
            GL11.glRotatef(angle, 0, 1, 0);
        } else {
            GL11.glRotatef(90.0f, 0, 0, 1);
            GL11.glRotatef(angle, 1, 0, 0);
        }

        for (int i = 0; i < renderCount; i++) {
            int layerOffset = i * 22;
            float baseRotation = ((i % 2) == 0 || spiralEnabled ? 1 : -1) * (timer / 6 * 7);
            float spiralRotation = spiralEnabled ? (i * 5.0f) % 360.0f : 0.0f;
            double offsetMagnitude = 11 + layerOffset;

            GL11.glPushMatrix();
            GL11.glTranslated(offsetX * offsetMagnitude, offsetY * offsetMagnitude, offsetZ * offsetMagnitude);
            GL11.glRotatef(rotAngle, rotAxisX, rotAxisY, rotAxisZ);
            GL11.glRotatef((spiralEnabled ? spiralRotation + baseRotation : baseRotation), 1, 0, 0);
            GL11.glTranslated(0, -1, 0);
            ring.render();
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslated(offsetX * (-offsetMagnitude), offsetY * (-offsetMagnitude), offsetZ * (-offsetMagnitude));
            GL11.glRotatef(rotAngle, rotAxisX, rotAxisY, rotAxisZ);
            GL11.glRotatef((spiralEnabled ? -5f - spiralRotation + baseRotation : -baseRotation), 1, 0, 0);
            GL11.glTranslated(0, -1, 0);
            ring.render();
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
        ShaderProgram.clear();
        GL11.glPopAttrib();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (failedInit) return;
        if (!(tile instanceof TileEntityEternalGregTechWorkshop egtwRender)) return;

        // If something ever fails, just early return and never try again this session
        if (!initialized) {
            try {
                initRings();
                initialized = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                failedInit = true;
                initialized = true;
                return;
            }
        }

        // Based on system time to prevent tps issues from causing stutters
        // Need to look into different timing system to prevent stutters based on tps issues
        // But prevent bypassing the pause menu
        long millis = System.currentTimeMillis() % (1000 * 36000);
        float timer = millis / (50f); // to ticks
        RenderRings(egtwRender, x, y, z, timer);

    }
}
