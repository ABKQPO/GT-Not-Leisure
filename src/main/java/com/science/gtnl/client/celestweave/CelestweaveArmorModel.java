package com.science.gtnl.client.celestweave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveArmorModel extends ModelBiped {

    private static final CelestweaveArmorModel[] MODELS = new CelestweaveArmorModel[4];
    private static final float RAD_TO_DEG = (float) (180D / Math.PI);

    private final int type;
    private final ModelRenderer[] bones;

    private CelestweaveArmorModel(int type) {
        super(0F, 0F, 64, 32);
        this.type = type;
        this.bones = new ModelRenderer[] { bipedHead, bipedBody, bipedRightArm, bipedLeftArm, bipedRightLeg,
            bipedLeftLeg };
    }

    public static CelestweaveArmorModel forType(int type) {
        int index = Math.max(0, Math.min(3, type));
        if (MODELS[index] == null) MODELS[index] = new CelestweaveArmorModel(index);
        return MODELS[index];
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbAmount, float age, float yaw, float pitch,
        float scale) {
        for (ModelRenderer bone : bones) {
            bone.showModel = true;
            bone.isHidden = false;
        }
        setRotationAngles(limbSwing, limbAmount, age, yaw, pitch, scale, entity);
        if (!(entity instanceof EntityLivingBase living) || entity.isInvisible()) return;
        CwFrames frames = CwModel.INSTANCE.frames();
        if (frames == null) return;
        CwClientState state = CwClientState.of(entity);
        CwLook look = CelestweaveClientEvents.look(living, state);
        if (!look.wears(type)) return;
        CwPose pose = CwAnimator.pose(living, state, look, frames);
        double distance = CelestweaveClientEvents.distanceToViewer(entity);
        boolean lod = distance > MainConfig.client.celestweaveDetailDistance;
        boolean overlay = (living.hurtTime > 0 || living.deathTime > 0) && !GL11.glIsEnabled(GL11.GL_TEXTURE_2D);

        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_POLYGON_BIT
                | GL11.GL_LIGHTING_BIT);
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        try {
            if (isChild) {
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(0F, 24F * scale, 0F);
            }
            if (overlay) renderOverlay(frames, look, pose, scale, lod);
            else renderLayers(entity, frames, state, look, pose, scale, lod, distance);
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            if (!overlay) GL11.glColor4f(1F, 1F, 1F, 1F);
        }
    }

    private void renderOverlay(CwFrames frames, CwLook look, CwPose pose, float scale, boolean lod) {
        for (CwFrames.Group group : frames.groups(type)) {
            if (group.layerKind != CwFrames.Layer.SOLID && (group.layerKind != CwFrames.Layer.SOLID_DETAIL || lod))
                continue;
            if (pieceShown(group.pieceKind, look)) draw(group, pose, scale);
        }
    }

    private void renderLayers(Entity entity, CwFrames frames, CwClientState state, CwLook look, CwPose pose,
        float scale, boolean lod, double distance) {
        TextureManager textures = Minecraft.getMinecraft()
            .getTextureManager();
        var groups = frames.groups(type);
        textures.bindTexture(CwModel.BASE);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        for (CwFrames.Group group : groups) {
            if (group.layerKind != CwFrames.Layer.SOLID && (group.layerKind != CwFrames.Layer.SOLID_DETAIL || lod))
                continue;
            if (pieceShown(group.pieceKind, look)) draw(group, pose, scale);
        }
        if (!look.glowOn(type)) return;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.004F);
        GL11.glDepthMask(false);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1F, -1F);
        textures.bindTexture(CwModel.GLOW);
        for (CwFrames.Group group : groups) {
            if (group.layerKind != CwFrames.Layer.GLOW_FACES && group.layerKind != CwFrames.Layer.GLOW) continue;
            if (!pieceShown(group.pieceKind, look) || lod && lodSkip(group)) continue;
            applyColor(group, pose);
            draw(group, pose, scale);
        }
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

        if (!lod && type == look.arcOwner() && CelestweaveClientEvents.fxVisible(entity, distance))
            CwArcs.render(this, frames, state, look, pose, scale);
        if (!lod && type == CwLook.CHEST) CwCoreBurst.render(this, frames, state, look, pose, scale);

        textures.bindTexture(CwModel.FX);
        GL11.glDisable(GL11.GL_CULL_FACE);
        for (CwFrames.Group group : groups) {
            if (group.layerKind != CwFrames.Layer.PANE || !pieceShown(group.pieceKind, look) || lod && lodSkip(group))
                continue;
            if (group.additive) GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            else GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            applyColor(group, pose);
            draw(group, pose, scale);
        }
    }

    private static boolean lodSkip(CwFrames.Group group) {
        return switch (group.pieceKind) {
            case CHEST -> group.layerKind == CwFrames.Layer.PANE
                || group.frameRef != null && group.layerKind == CwFrames.Layer.GLOW;
            case HOLO -> group.layerKind == CwFrames.Layer.PANE;
            default -> false;
        };
    }

    private boolean pieceShown(CwFrames.Piece piece, CwLook look) {
        return switch (piece) {
            case HELMET -> type == CwLook.HEAD;
            case CHEST -> type == CwLook.CHEST;
            case LEGS -> type == CwLook.LEGS;
            case BOOTS -> type == CwLook.BOOTS;
            case GOGGLES -> type == CwLook.HEAD && look.goggles;
            case HOLO -> type == CwLook.HEAD && look.holo;
            case PACK -> type == CwLook.CHEST && look.pack;
            case COILS -> type == CwLook.CHEST && !look.pack;
            case WINGS -> type == CwLook.LEGS && look.wings;
        };
    }

    private void applyColor(CwFrames.Group group, CwPose pose) {
        CwFrames.Frame frame = group.frameRef;
        if (frame != null && pose.colored[frame.index]) {
            int i = frame.index;
            GL11.glColor4f(pose.r[i], pose.g[i], pose.b[i], pose.a[i]);
            return;
        }
        if (group.layerKind == CwFrames.Layer.PANE) {
            GL11.glColor4f(1F, 1F, 1F, 1F);
            return;
        }
        float k = pose.brightness;
        if (group.tintKind == CwFrames.Tint.THEME)
            GL11.glColor4f(clamp(pose.themeR * k), clamp(pose.themeG * k), clamp(pose.themeB * k), 1F);
        else GL11.glColor4f(clamp(group.rgb[0] * k), clamp(group.rgb[1] * k), clamp(group.rgb[2] * k), 1F);
    }

    private void draw(CwFrames.Group group, CwPose pose, float scale) {
        CwFrames.Frame frame = group.frameRef;
        if (frame != null && !pose.visible(frame)) return;
        GL11.glPushMatrix();
        enterAnchor(group.bone, frame, pose, scale);
        int texFrame = frame != null ? pose.texFrame[frame.index] : 0;
        boolean shifted = group.strip != null && texFrame > 0;
        if (shifted) {
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glTranslatef(texFrame * group.strip.strideU, 0F, 0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }
        CwModel.INSTANCE.call(group);
        if (shifted) {
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }
        GL11.glPopMatrix();
    }

    void enterAnchor(int bone, CwFrames.Frame frame, CwPose pose, float scale) {
        bones[bone].postRender(scale);
        GL11.glScalef(scale, scale, scale);
        if (frame != null) applyFrame(frame, pose);
    }

    private void applyFrame(CwFrames.Frame frame, CwPose pose) {
        if (frame.parentFrame != null) applyFrame(frame.parentFrame, pose);
        if (frame.chainBuffer != null) {
            frame.chainBuffer.rewind();
            GL11.glMultMatrix(frame.chainBuffer);
        }
        int i = frame.index;
        GL11.glTranslatef(frame.pivot[0] + pose.dx[i], frame.pivot[1] + pose.dy[i], frame.pivot[2] + pose.dz[i]);
        float rz = frame.rot[2] + pose.rz[i], ry = frame.rot[1] + pose.ry[i], rx = frame.rot[0] + pose.rx[i];
        if (rz != 0F) GL11.glRotatef(rz * RAD_TO_DEG, 0F, 0F, 1F);
        if (ry != 0F) GL11.glRotatef(ry * RAD_TO_DEG, 0F, 1F, 0F);
        if (rx != 0F) GL11.glRotatef(rx * RAD_TO_DEG, 1F, 0F, 0F);
        if (pose.sx[i] != 1F || pose.sy[i] != 1F || pose.sz[i] != 1F)
            GL11.glScalef(Math.max(0.001F, pose.sx[i]), Math.max(0.001F, pose.sy[i]), Math.max(0.001F, pose.sz[i]));
    }

    private static float clamp(float value) {
        return Math.max(0F, Math.min(1F, value));
    }
}
