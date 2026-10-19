package com.science.gtnl.client.celestweave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveArmorModel extends ModelBiped {

    private static final CelestweaveArmorModel[] MODELS = new CelestweaveArmorModel[CelestweaveArmor.PIECES];
    private static final float RAD_TO_DEG = (float) (180D / Math.PI);
    private static final int ALL_BONES = -1;

    private final int piece;
    private final ModelRenderer[] bones;

    private CelestweaveArmorModel(int piece) {
        super(0F, 0F, 64, 32);
        this.piece = piece;
        this.bones = new ModelRenderer[] { bipedHead, bipedBody, bipedRightArm, bipedLeftArm, bipedRightLeg,
            bipedLeftLeg };
    }

    public static CelestweaveArmorModel forPiece(int piece) {
        int index = Math.max(0, Math.min(CelestweaveArmor.PIECES - 1, piece));
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
        if (!look.wears(piece)) return;
        CwPose pose = CwAnimator.pose(living, state, look, frames);
        double distance = CelestweaveClientEvents.distanceToViewer(entity);
        boolean lod = distance > MainConfig.client.celestweaveDetailDistance;
        boolean overlay = (living.hurtTime > 0 || living.deathTime > 0) && !GL11.glIsEnabled(GL11.GL_TEXTURE_2D);

        isolated(() -> {
            if (isChild) {
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(0F, 24F * scale, 0F);
            }
            if (overlay) renderOverlay(frames, look, pose, scale, lod);
            else renderLayers(entity, frames, state, look, pose, scale, lod, distance, ALL_BONES);
        });
        if (!overlay) GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public static void renderFirstPersonArm(EntityPlayer player, ModelRenderer arm) {
        if (!MainConfig.client.enableCelestweaveFirstPersonArm || player == null || player.isInvisible()) return;
        CwFrames frames = CwModel.INSTANCE.frames();
        if (frames == null) return;
        CwClientState state = CwClientState.of(player);
        CwLook look = CelestweaveClientEvents.look(player, state);
        if (!look.wears(CelestweaveArmor.CHEST)) return;
        CelestweaveArmorModel model = forPiece(CelestweaveArmor.CHEST);
        ModelRenderer bone = model.bones[CwFrames.RIGHT_ARM];
        bone.showModel = true;
        bone.isHidden = false;
        bone.rotationPointX = arm.rotationPointX;
        bone.rotationPointY = arm.rotationPointY;
        bone.rotationPointZ = arm.rotationPointZ;
        bone.rotateAngleX = arm.rotateAngleX;
        bone.rotateAngleY = arm.rotateAngleY;
        bone.rotateAngleZ = arm.rotateAngleZ;
        CwPose pose = CwAnimator.pose(player, state, look, frames);

        isolated(() -> model.renderLayers(player, frames, state, look, pose, 0.0625F, false, 0D, CwFrames.RIGHT_ARM));
        GL11.glColor4f(1F, 1F, 1F, 1F);
        if (player instanceof AbstractClientPlayer client) Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(client.getLocationSkin());
    }

    private static void isolated(Runnable draw) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_POLYGON_BIT
                | GL11.GL_LIGHTING_BIT);
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        try {
            draw.run();
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    private void renderOverlay(CwFrames frames, CwLook look, CwPose pose, float scale, boolean lod) {
        for (CwFrames.Group group : frames.groups(piece)) {
            if (group.layerKind != CwFrames.Layer.SOLID && group.layerKind != CwFrames.Layer.GLOW_FACES
                && (group.layerKind != CwFrames.Layer.SOLID_DETAIL || lod)) continue;
            if (partVisible(group.partKind, look)) draw(group, pose, scale);
        }
    }

    private void renderLayers(Entity entity, CwFrames frames, CwClientState state, CwLook look, CwPose pose,
        float scale, boolean lod, double distance, int onlyBone) {
        TextureManager textures = Minecraft.getMinecraft()
            .getTextureManager();
        var groups = frames.groups(piece);
        textures.bindTexture(CwModel.BASE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        for (CwFrames.Group group : groups) {
            if (group.layerKind != CwFrames.Layer.SOLID && (group.layerKind != CwFrames.Layer.SOLID_DETAIL || lod))
                continue;
            if (onBone(group, onlyBone) && partVisible(group.partKind, look)) draw(group, pose, scale);
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.3F, 0.318F, 0.408F, 1F);
        for (CwFrames.Group group : groups) if (group.layerKind == CwFrames.Layer.GLOW_FACES && onBone(group, onlyBone)
            && partVisible(group.partKind, look)) draw(group, pose, scale);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        if (!look.glowOn(piece)) return;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.004F);
        GL11.glDepthMask(false);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1F, -1F);
        textures.bindTexture(CwModel.GLOW);
        CwStripEffect strip = CwStripStyles.of(look.cosmetic.stripStyle());
        float seconds = entity instanceof EntityLivingBase living ? CwAnimator.seconds(living) : 0F;
        for (CwFrames.Group group : groups) {
            if (group.layerKind != CwFrames.Layer.GLOW_FACES && group.layerKind != CwFrames.Layer.GLOW) continue;
            if (!onBone(group, onlyBone) || !partVisible(group.partKind, look) || lod && lodSkip(group)) continue;
            if (group.layerKind == CwFrames.Layer.GLOW_FACES && group.tintKind == CwFrames.Tint.GLOW) {
                boolean textured = strip.begin(pose, look.cosmetic, seconds, bones[group.bone].rotationPointY);
                draw(group, pose, scale);
                strip.end();
                if (textured) textures.bindTexture(CwModel.GLOW);
                continue;
            }
            applyColor(group, pose);
            draw(group, pose, scale);
        }
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

        if (onlyBone == ALL_BONES && !lod
            && piece == look.arcOwner()
            && CelestweaveClientEvents.fxVisible(entity, distance))
            CwArcs.render(this, frames, state, look, pose, scale);
        if (onlyBone == ALL_BONES && !lod && piece == CelestweaveArmor.CHEST)
            CwCoreBurst.render(this, frames, state, look, pose, scale);

        textures.bindTexture(CwModel.FX);
        GL11.glDisable(GL11.GL_CULL_FACE);
        for (CwFrames.Group group : groups) {
            if (group.layerKind != CwFrames.Layer.PANE || !onBone(group, onlyBone)
                || !partVisible(group.partKind, look)
                || lod && lodSkip(group)) continue;
            if (group.additive) GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            else GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            applyColor(group, pose);
            draw(group, pose, scale);
        }
    }

    private static boolean onBone(CwFrames.Group group, int onlyBone) {
        return onlyBone == ALL_BONES || group.bone == onlyBone;
    }

    private static boolean lodSkip(CwFrames.Group group) {
        return switch (group.partKind) {
            case CHEST -> group.layerKind == CwFrames.Layer.PANE
                || group.frameRef != null && group.layerKind == CwFrames.Layer.GLOW;
            case HOLO -> group.layerKind == CwFrames.Layer.PANE;
            default -> false;
        };
    }

    private boolean partVisible(CwFrames.Part part, CwLook look) {
        return part.piece == piece && part.visible(look);
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
        if (group.tintKind == CwFrames.Tint.GLOW)
            GL11.glColor4f(clamp(pose.glowR * k), clamp(pose.glowG * k), clamp(pose.glowB * k), 1F);
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
