package com.science.gtnl.common.render.tile;

import static gregtech.api.GregTechAPI.*;
import static tectech.rendering.EOH.EOHTileEntitySR.*;

import java.nio.FloatBuffer;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.gtnewhorizon.gtnhlib.client.renderer.shader.ShaderProgram;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VertexBuffer;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.machine.multiblock.wireless.NanoPhagocytosisPlant;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import tectech.Reference;
import tectech.util.StructureVBO;
import tectech.util.TextureUpdateRequester;

@SideOnly(Side.CLIENT)
public class NanoPhagocytosisPlantRenderer extends TileEntitySpecialRenderer {

    private static ShaderProgram starProgram;
    private static IModelCustom starModel;
    private static final float modelNormalize = 0.003f;

    private static boolean initialized = false;
    private static boolean failedInit = false;
    private static int u_Color = -1, u_ModelMatrix = -1, u_Gamma = -1;
    private final Matrix4fStack starModelMatrix = new Matrix4fStack(3);

    private VertexBuffer ringOne, ringTwo, ringThree;

    private static ShaderProgram fadeBypassProgram;
    private static TextureUpdateRequester textureUpdater;

    private void init() {
        try {
            starProgram = new ShaderProgram(Reference.MODID, "shaders/star.vert.glsl", "shaders/star.frag.glsl");

            u_Color = starProgram.getUniformLocation("u_Color");
            u_Gamma = starProgram.getUniformLocation("u_Gamma");
            u_ModelMatrix = starProgram.getUniformLocation("u_ModelMatrix");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        starModel = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.MODID, "models/Star.obj"));
        ShaderProgram.clear();
        initialized = true;
    }

    private void initRings() {
        StructureVBO ringStructure = (new StructureVBO()).addMapping('I', sBlockCasings10, 8)
            .addMapping('N', sBlockCasings8, 10)
            .addMapping('P', sBlockCasings9, 12)
            .addMapping('W', sBlockCasings4, 7)
            .addMapping('X', Loaders.compactFusionCoil, 2)
            .addMapping('Y', Loaders.compactFusionCoil, 0);

        ringOne = ringStructure.assignStructure(NanoPhagocytosisPlant.shapeRingOne)
            .build();
        ringTwo = ringStructure.assignStructure(NanoPhagocytosisPlant.shapeRingTwo)
            .build();
        ringThree = ringStructure.assignStructure(NanoPhagocytosisPlant.shapeRingThree)
            .build();

        fadeBypassProgram = new ShaderProgram(
            Reference.MODID,
            "shaders/fadebypass.vert.glsl",
            "shaders/fadebypass.frag.glsl");

        textureUpdater = ringStructure.getTextureUpdateRequestor();
    }

    public void RenderStarLayer(Vector4f color, ResourceLocation texture, float size, Vector3f rotationAxis,
        float degrees) {
        starModelMatrix.pushMatrix();
        starModelMatrix.rotate((degrees / 180f * ((float) Math.PI)), rotationAxis.x, rotationAxis.y, rotationAxis.z);
        starModelMatrix.scale(size, size, size);

        this.bindTexture(texture);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Enable transparency if needed
        if (color.w < 1 && color.w > 0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false); // Disable depth writing for transparency
        }

        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        GL20.glUniformMatrix4(u_ModelMatrix, false, starModelMatrix.get(matrixBuffer));
        GL20.glUniform4f(u_Color, color.x, color.y, color.z, color.w);
        starModel.renderAll();
        GL11.glPopAttrib();
        starModelMatrix.popMatrix();
    }

    public void RenderEntireStar(TileEntityNanoPhagocytosisPlant tile, double x, double y, double z, float timer) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glDisable(GL11.GL_LIGHTING);

        starProgram.use();

        float cx = (float) x + .5f;
        float cy = (float) y + .5f;
        float cz = (float) z + .5f;
        float size = modelNormalize;
        starModelMatrix.clear();
        starModelMatrix.translate(cx, cy, cz);

        size *= 20;

        timer *= 10;

        float r = tile.getColorR(), g = tile.getColorG(), b = tile.getColorB();
        GL20.glUniform1f(u_Gamma, tile.getGamma());
        RenderStarLayer(
            new Vector4f(r, g, b, 1f),
            STAR_LAYER_0,
            size,
            new Vector3f(0F, 1F, 1).normalize(),
            130 + (timer) % 360000);
        RenderStarLayer(
            new Vector4f(r, g, b, 0.4f),
            STAR_LAYER_1,
            size * 1.02f,
            new Vector3f(1F, 1F, 0F).normalize(),
            -49 + (timer) % 360000);
        RenderStarLayer(
            new Vector4f(r, g, b, 0.2f),
            STAR_LAYER_2,
            size * 1.04f,
            new Vector3f(1F, 0F, 1F).normalize(),
            67 + (timer) % 360000);

        ShaderProgram.clear();
        GL11.glPopAttrib();
    }

    private void RenderRings(TileEntityNanoPhagocytosisPlant tile, double x, double y, double z, float timer) {
        bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        textureUpdater.requestUpdate();
        fadeBypassProgram.use();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);
        GL11.glRotatef(tile.getRotAngle(), tile.getRotAxisX(), tile.getRotAxisY(), tile.getRotAxisZ());
        GL11.glRotatef(timer / 6 * 7, 1, 0, 0);
        GL11.glTranslated(0, -1, 0);
        ringOne.render();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);
        GL11.glRotatef(tile.getRotAngle(), tile.getRotAxisX(), tile.getRotAxisY(), tile.getRotAxisZ());
        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-timer / 4 * 5, 0, 0, 1);
        GL11.glTranslated(0, -1, 0);
        ringTwo.render();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);
        GL11.glRotatef(tile.getRotAngle(), tile.getRotAxisX(), tile.getRotAxisY(), tile.getRotAxisZ());
        GL11.glRotatef(timer * 3, 1, 0, 0);
        GL11.glTranslated(0, -1, 0);
        ringThree.render();
        GL11.glPopMatrix();

        ShaderProgram.clear();
        GL11.glPopAttrib();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (failedInit) return;
        if (!(tile instanceof TileEntityNanoPhagocytosisPlant forgeTile)) return;

        // If something ever fails, just early return and never try again this session
        if (!initialized) {
            init();
            if (!initialized) {
                failedInit = true;
                return;
            }
            try {
                initRings();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                failedInit = true;
                return;
            }
        }

        // Based on system time to prevent tps issues from causing stutters
        // Need to look into different timing system to prevent stutters based on tps issues
        // But prevent bypassing the pause menu
        long millis = System.currentTimeMillis() % (1000 * 36000);
        float timer = millis / (50f); // to ticks

        forgeTile.incrementColors();

        RenderEntireStar(forgeTile, x, y, z, timer);
        RenderRings(forgeTile, x, y, z, timer);

    }
}
