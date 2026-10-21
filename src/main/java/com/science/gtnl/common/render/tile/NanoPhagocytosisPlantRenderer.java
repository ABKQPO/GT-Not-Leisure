package com.science.gtnl.common.render.tile;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.gtnewhorizon.gtnhlib.client.renderer.shader.ShaderProgram;
import com.gtnewhorizon.gtnhlib.client.renderer.vao.IVertexArrayObject;
import com.gtnewhorizon.gtnhlib.client.renderer.vertex.VertexFormat;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.machine.multiblock.wireless.NanoPhagocytosisPlant;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.GTMod;
import gregtech.api.GregTechAPI;
import gregtech.common.render.shader.ShaderHandle;
import gregtech.common.render.shader.ShaderRecipe;
import gregtech.common.render.shader.Uniform;
import gregtech.common.render.shader.VertexAttribute;
import tectech.Reference;
import tectech.rendering.EOH.EOHRenderingUtils;
import tectech.rendering.EOH.EOHTileEntitySR;
import tectech.util.StructureVBO;

@SideOnly(Side.CLIENT)
public class NanoPhagocytosisPlantRenderer extends TileEntitySpecialRenderer {

    private static final float STAR_RADIUS = 0.003f * 20f * 74f;
    private static final int STAR_TESSELLATION = 128;

    private static final ShaderRecipe STAR = ShaderRecipe.of(Reference.MODID, "star")
        .required("u_Color", "u_Gamma")
        .sampler("u_Texture", 0)
        .modelUniform("u_ModelMatrix")
        .attribute("a_Position", VertexAttribute.POSITION)
        .attribute("a_UV", VertexAttribute.UV);

    private static final Uniform STAR_COLOR = STAR.uniform("u_Color");
    private static final Uniform STAR_GAMMA = STAR.uniform("u_Gamma");

    private static final ShaderRecipe FADE_BYPASS = ShaderRecipe.of(Reference.MODID, "fadebypass")
        .sampler("u_Texture", 0)
        .modelUniform("u_ModelMatrix")
        .attribute("a_Position", VertexAttribute.POSITION)
        .attribute("a_UV", VertexAttribute.UV);

    private static ShaderHandle starShader;
    private static ShaderHandle fadeBypassShader;
    private static IVertexArrayObject starSphere;
    private static IVertexArrayObject ringOne, ringTwo, ringThree;

    private static boolean initialized = false;

    // These are nudges/translations for each ring to align with the structure
    private static final Vector3f ringOneNudge = new Vector3f(0, -1, 0);
    private static final Vector3f ringTwoNudge = new Vector3f(0, -1, 0);
    private static final Vector3f ringThreeNudge = new Vector3f(.5f, -1, 0);

    private final Matrix4fStack starModelMatrix = new Matrix4fStack(3);
    private final Matrix4f ringMatrix = new Matrix4f();

    private final Vector4f reusableStarColor = new Vector4f();
    private final Vector3f reusableRotationAxis = new Vector3f();

    public static void reload() {
        initialized = false;
        release();
        try {
            init();
            initialized = true;
        } catch (Exception e) {
            ScienceNotLeisure.LOG.error("[NanoPhagocytosisPlantRenderer] Init failed", e);
            release();
        }
    }

    private static void release() {
        if (starShader != null) {
            starShader.release();
            starShader = null;
        }
        if (fadeBypassShader != null) {
            fadeBypassShader.release();
            fadeBypassShader = null;
        }
        if (starSphere != null) {
            starSphere.delete();
            starSphere = null;
        }
        if (ringOne != null) {
            ringOne.delete();
            ringOne = null;
        }
        if (ringTwo != null) {
            ringTwo.delete();
            ringTwo = null;
        }
        if (ringThree != null) {
            ringThree.delete();
            ringThree = null;
        }
    }

    private static void init() {
        starShader = STAR.bake();
        if (!starShader.isValid()) throw new IllegalStateException("star shader did not bake");
        starSphere = EOHRenderingUtils.buildSphere(starShader, STAR_TESSELLATION, STAR_TESSELLATION);

        fadeBypassShader = FADE_BYPASS.bake();
        if (!fadeBypassShader.isValid()) throw new IllegalStateException("fadebypass shader did not bake");

        StructureVBO ringStructure = (new StructureVBO()).addMapping('I', GregTechAPI.sBlockCasings10, 8)
            .addMapping('N', GregTechAPI.sBlockCasings8, 10)
            .addMapping('P', GregTechAPI.sBlockCasings9, 12)
            .addMapping('W', GregTechAPI.sBlockCasings4, 7)
            .addMapping('X', Loaders.compactFusionCoil, 2)
            .addMapping('Y', Loaders.compactFusionCoil, 0);

        final VertexFormat format = fadeBypassShader.vertexFormat();
        ringOne = ringStructure.assignStructure(NanoPhagocytosisPlant.shapeRingOne)
            .build(format);
        ringTwo = ringStructure.assignStructure(NanoPhagocytosisPlant.shapeRingTwo)
            .build(format);
        ringThree = ringStructure.assignStructure(NanoPhagocytosisPlant.shapeRingThree)
            .build(format);

        ringStructure.getTextureUpdateRequestor()
            .requestUpdate();
    }

    public void RenderStarLayer(Vector4f color, ResourceLocation texture, float size, Vector3f rotationAxis,
        float degrees) {
        starModelMatrix.pushMatrix();
        starModelMatrix.rotate((degrees / 180f * ((float) Math.PI)), rotationAxis.x, rotationAxis.y, rotationAxis.z);
        starModelMatrix.scale(size, size, size);

        this.bindTexture(texture);

        starShader.uploadModel(starModelMatrix);
        GL20.glUniform4f(starShader.loc(STAR_COLOR), color.x, color.y, color.z, color.w);
        starSphere.draw();

        starModelMatrix.popMatrix();
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
        fadeBypassShader.use();

        renderRing(tile, ringOne, x, y, z, timer / 6 * 7, 0, ringOneNudge);
        renderRing(tile, ringTwo, x, y, z, -timer / 4 * 5, 90, ringTwoNudge);
        renderRing(tile, ringThree, x, y, z, timer * 3, 0, ringThreeNudge);

        ShaderProgram.clear();
        GL11.glPopAttrib();
    }

    private void renderRing(TileEntityNanoPhagocytosisPlant tile, IVertexArrayObject ring, double x, double y, double z,
        float spinDegrees, float yawDegrees, Vector3f nudge) {
        ringMatrix.translation((float) x + .5f, (float) y + .5f, (float) z + .5f)
            .rotate(
                tile.getRotAngle() / 180f * (float) Math.PI,
                tile.getRotAxisX(),
                tile.getRotAxisY(),
                tile.getRotAxisZ())
            .rotateY(yawDegrees / 180f * (float) Math.PI)
            .rotate(spinDegrees / 180f * (float) Math.PI, 1, 0, 0)
            .translate(nudge);

        fadeBypassShader.uploadModel(ringMatrix);
        ring.render();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (!initialized) return;
        if (!(tile instanceof TileEntityNanoPhagocytosisPlant nanoTile)) return;

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

        starShader.use();
        final long cullWas = EOHRenderingUtils.beginSphereCull(false);
        starSphere.bind();

        starModelMatrix.clear();
        starModelMatrix.translate((float) x + .5f, (float) y + .5f, (float) z + .5f);

        float r = tile.getColorR(), g = tile.getColorG(), b = tile.getColorB();
        GL20.glUniform1f(starShader.loc(STAR_GAMMA), tile.getGamma());

        // Render OPAQUE layer (writes to depth)
        RenderStarLayer(
            reusableStarColor.set(r, g, b, 1f),
            EOHTileEntitySR.STAR_LAYER_0,
            STAR_RADIUS,
            reusableRotationAxis.set(0F, 1F, 1F)
                .normalize(),
            130 + (timer) % 360000);

        starSphere.unbind();
        EOHRenderingUtils.endSphereCull(cullWas);
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

        starShader.use();
        final long cullWas = EOHRenderingUtils.beginSphereCull(false);
        starSphere.bind();

        starModelMatrix.clear();
        starModelMatrix.translate((float) x + .5f, (float) y + .5f, (float) z + .5f);

        float r = tile.getColorR(), g = tile.getColorG(), b = tile.getColorB();
        GL20.glUniform1f(starShader.loc(STAR_GAMMA), tile.getGamma());

        // Render TRANSPARENT layers last, so they correctly blend over rings when in front
        RenderStarLayer(
            reusableStarColor.set(r, g, b, 0.4f),
            EOHTileEntitySR.STAR_LAYER_1,
            STAR_RADIUS * 1.02f,
            reusableRotationAxis.set(1F, 1F, 0F)
                .normalize(),
            -49 + (timer) % 360000);

        RenderStarLayer(
            reusableStarColor.set(r, g, b, 0.2f),
            EOHTileEntitySR.STAR_LAYER_2,
            STAR_RADIUS * 1.04f,
            reusableRotationAxis.set(1F, 0F, 1F)
                .normalize(),
            67 + (timer) % 360000);

        starSphere.unbind();
        EOHRenderingUtils.endSphereCull(cullWas);
        ShaderProgram.clear();
        GL11.glPopAttrib();
    }
}
