package com.science.gtnl.common.render.tile;

import static tectech.rendering.EOH.EOHTileEntitySR.STAR_LAYER_0;
import static tectech.rendering.EOH.EOHTileEntitySR.STAR_LAYER_1;
import static tectech.rendering.EOH.EOHTileEntitySR.STAR_LAYER_2;

import java.nio.FloatBuffer;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.gtnewhorizon.gtnhlib.client.renderer.shader.ShaderProgram;
import com.gtnewhorizon.gtnhlib.client.renderer.vao.IVertexArrayObject;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.machine.multiblock.wireless.NanoPhagocytosisPlant;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.GTMod;
import gregtech.api.GregTechAPI;
import tectech.Reference;
import tectech.rendering.EOH.EOHRenderingUtils;
import tectech.rendering.EOH.EOHTileEntitySR;
import tectech.util.StructureVBO;
import tectech.util.TextureUpdateRequester;

@SideOnly(Side.CLIENT)
public class NanoPhagocytosisPlantRenderer extends TileEntitySpecialRenderer {

    private static ShaderProgram starProgram;
    private static final float STAR_RADIUS = 0.003f * 20f * 74f;

    private static boolean initialized = false;
    private static boolean failedInit = false;
    private static int u_Color = -1, u_ModelMatrix = -1, u_Gamma = -1;
    private final Matrix4fStack starModelMatrix = new Matrix4fStack(3);

    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    private IVertexArrayObject ringOne, ringTwo, ringThree;
    // These are nudges/translations for each ring to align with the structure
    private static final Vector3f ringOneNudge = new Vector3f(0, -1, 0);
    private static final Vector3f ringTwoNudge = new Vector3f(0, -1, 0);
    private static final Vector3f ringThreeNudge = new Vector3f(.5f, -1, 0);

    private static ShaderProgram fadeBypassProgram;

    private final Vector4f reusableStarColor = new Vector4f();
    private final Vector3f reusableRotationAxis = new Vector3f();

    private void init() {
        try {
            starProgram = new ShaderProgram(Reference.MODID, "shaders/star.vert.glsl", "shaders/star.frag.glsl");

            u_Color = starProgram.getUniformLocation("u_Color");
            u_Gamma = starProgram.getUniformLocation("u_Gamma");
            u_ModelMatrix = starProgram.getUniformLocation("u_ModelMatrix");

        } catch (Exception e) {
            ScienceNotLeisure.LOG.error("[NanoPhagocytosisPlantRenderer] Shader init failed", e);
            return;
        }

        ShaderProgram.clear();
        initialized = true;
    }

    private void initRings() {
        StructureVBO ringStructure = (new StructureVBO()).addMapping('I', GregTechAPI.sBlockCasings10, 8)
            .addMapping('N', GregTechAPI.sBlockCasings8, 10)
            .addMapping('P', GregTechAPI.sBlockCasings9, 12)
            .addMapping('W', GregTechAPI.sBlockCasings4, 7)
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

        TextureUpdateRequester textureUpdater = ringStructure.getTextureUpdateRequestor();
        textureUpdater.requestUpdate();
    }

    public void RenderStarLayer(Vector4f color, ResourceLocation texture, float size, Vector3f rotationAxis,
        float degrees) {
        starModelMatrix.pushMatrix();
        starModelMatrix.rotate((degrees / 180f * ((float) Math.PI)), rotationAxis.x, rotationAxis.y, rotationAxis.z);
        starModelMatrix.scale(size, size, size);

        this.bindTexture(texture);

        matrixBuffer.clear();
        GL20.glUniformMatrix4(u_ModelMatrix, false, starModelMatrix.get(matrixBuffer));
        GL20.glUniform4f(u_Color, color.x, color.y, color.z, color.w);
        EOHRenderingUtils.renderTessellatedSphere(128, 128, 1);

        starModelMatrix.popMatrix();
    }

    public void RenderEntireStar(TileEntityNanoPhagocytosisPlant tile, double x, double y, double z, float timer) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        starProgram.use();

        float cx = (float) x + .5f;
        float cy = (float) y + .5f;
        float cz = (float) z + .5f;
        starModelMatrix.clear();
        starModelMatrix.translate(cx, cy, cz);

        timer *= 10;

        float r = tile.getColorR(), g = tile.getColorG(), b = tile.getColorB();
        GL20.glUniform1f(u_Gamma, tile.getGamma());

        // Render OPAQUE layer
        RenderStarLayer(
            new Vector4f(r, g, b, 1f),
            EOHTileEntitySR.STAR_LAYER_0,
            STAR_RADIUS,
            new Vector3f(0F, 1F, 1).normalize(),
            130 + (timer) % 360000);

        // Setup for TRANSPARENT layers
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        // Render for TRANSPARENT layers
        RenderStarLayer(
            reusableStarColor.set(r, g, b, 0.4f),
            STAR_LAYER_1,
            STAR_RADIUS * 1.02f,
            reusableRotationAxis.set(1F, 1F, 0F)
                .normalize(),
            -49 + (timer) % 360000);
        RenderStarLayer(
            new Vector4f(r, g, b, 0.2f),
            EOHTileEntitySR.STAR_LAYER_2,
            STAR_RADIUS * 1.04f,
            new Vector3f(1F, 0F, 1F).normalize(),
            67 + (timer) % 360000);

        ShaderProgram.clear();
        GL11.glPopAttrib();
    }

    private void renderRings(TileEntityNanoPhagocytosisPlant tile, double x, double y, double z, float timer) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);

        GL11.glDisable(GL11.GL_LIGHTING);

        // Critical: Rings must participate in depth properly
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        bindTexture(TextureMap.locationBlocksTexture);
        fadeBypassProgram.use();

        GL11.glPushMatrix();
        GL11.glTranslated(x + .5f, y + .5f, z + .5f);
        GL11.glRotatef(tile.getRotAngle(), tile.getRotAxisX(), tile.getRotAxisY(), tile.getRotAxisZ());
        GL11.glRotatef(timer / 6 * 7, 1, 0, 0);
        GL11.glTranslated(ringOneNudge.x, ringOneNudge.y, ringOneNudge.z);
        ringOne.render();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x + .5f, y + .5f, z + .5f);
        GL11.glRotatef(tile.getRotAngle(), tile.getRotAxisX(), tile.getRotAxisY(), tile.getRotAxisZ());
        GL11.glRotatef(-timer / 4 * 5, 1, 0, 0);
        GL11.glTranslated(ringTwoNudge.x, ringTwoNudge.y, ringTwoNudge.z);
        ringTwo.render();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x + .5f, y + .5f, z + .5f);
        GL11.glRotatef(tile.getRotAngle(), tile.getRotAxisX(), tile.getRotAxisY(), tile.getRotAxisZ());
        GL11.glRotatef(timer * 3, 1, 0, 0);
        GL11.glTranslated(ringThreeNudge.x, ringThreeNudge.y, ringThreeNudge.z);
        ringThree.render();
        GL11.glPopMatrix();

        ShaderProgram.clear();
        GL11.glPopAttrib();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (failedInit) return;
        if (!(tile instanceof TileEntityNanoPhagocytosisPlant nanoTile)) return;

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
                ScienceNotLeisure.LOG.error("[NanoPhagocytosisPlantRenderer] Ring init failed", e);
                failedInit = true;
                return;
            }
        }

        nanoTile.incrementColors();

        float timer = GTMod.clientProxy()
            .getAnimationRenderTicks();

        // Correct order for transparency/depth:
        // 1) Opaque star writes depth
        renderStarOpaquePass(nanoTile, x, y, z, timer);

        // 2) Rings render next and write depth
        renderRings(nanoTile, x, y, z, timer);

        // 3) Transparent star shells render last and blend correctly (no depth write)
        renderStarTransparentPass(nanoTile, x, y, z, timer);
    }

    private void renderStarOpaquePass(TileEntityNanoPhagocytosisPlant tile, double x, double y, double z, float timer) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        starProgram.use();

        float cx = (float) x + .5f;
        float cy = (float) y + .5f;
        float cz = (float) z + .5f;

        starModelMatrix.clear();
        starModelMatrix.translate(cx, cy, cz);

        float r = tile.getColorR(), g = tile.getColorG(), b = tile.getColorB();
        GL20.glUniform1f(u_Gamma, tile.getGamma());

        // Render OPAQUE layer (writes to depth)
        RenderStarLayer(
            reusableStarColor.set(r, g, b, 1f),
            STAR_LAYER_0,
            STAR_RADIUS,
            reusableRotationAxis.set(0F, 1F, 1F)
                .normalize(),
            130 + (timer) % 360000);

        ShaderProgram.clear();
        GL11.glPopAttrib();
    }

    private void renderStarTransparentPass(TileEntityNanoPhagocytosisPlant tile, double x, double y, double z,
        float timer) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);

        GL11.glDisable(GL11.GL_LIGHTING);

        // Transparent shells should depth-test but not write depth
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        starProgram.use();

        float cx = (float) x + .5f;
        float cy = (float) y + .5f;
        float cz = (float) z + .5f;

        starModelMatrix.clear();
        starModelMatrix.translate(cx, cy, cz);

        float r = tile.getColorR(), g = tile.getColorG(), b = tile.getColorB();
        GL20.glUniform1f(u_Gamma, tile.getGamma());

        // Render TRANSPARENT layers last, so they correctly blend over rings when in front
        RenderStarLayer(
            reusableStarColor.set(r, g, b, 0.4f),
            STAR_LAYER_1,
            STAR_RADIUS * 1.02f,
            reusableRotationAxis.set(1F, 1F, 0F)
                .normalize(),
            -49 + (timer) % 360000);

        RenderStarLayer(
            reusableStarColor.set(r, g, b, 0.2f),
            STAR_LAYER_2,
            STAR_RADIUS * 1.04f,
            reusableRotationAxis.set(1F, 0F, 1F)
                .normalize(),
            67 + (timer) % 360000);

        ShaderProgram.clear();
        GL11.glPopAttrib();
    }
}
