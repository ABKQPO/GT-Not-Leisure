// Pigmee Fumo port from AE2 Lightning Tech Reborn.
// Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
// License: LGPL-3.0. Model author: TedXenon.
// Original model credit: "Made with Blockbench, made by TedXenon".
// Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.render.model;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.client.event.TextureStitchEvent;

import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc.ModelLoc;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;
import com.gtnewhorizon.gtnhlib.geometry.Orientation;
import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Client-side model cache for the Pigmee Fumo: one baked geometry per orientation.
// Loading, caching and parent resolution are delegated to GTNHLib's ModelRegistry, which also injects the
// model's textures into the block atlas because ScienceNotLeisure.MODID is registered through
// ModelRegistry.registerModid. Nothing here parses JSON or touches the atlas by hand.
// The caches are published as a whole array and never mutated in place, so a render thread reading a snapshot can
// never observe a half-built state.
@SideOnly(Side.CLIENT)
public class PigmeeFumoModel {

    public static final PigmeeFumoModel INSTANCE = new PigmeeFumoModel();

    private static final ModelLoc MODEL = ModelLoc.fromStr(ScienceNotLeisure.RESOURCE_ROOT_ID + ":block/pigmee_fumo");

    // The orientation a freshly placed or head-worn doll uses.
    public static final Orientation DEFAULT_ORIENTATION = Orientation.NORTH_UP;

    private volatile BakedModel[] orientations;

    private PigmeeFumoModel() {}

    // orientation: requested orientation, may be null
    // Returns the baked model for that orientation, or null while unloaded; an unknown or invalid orientation
    // falls back to DEFAULT_ORIENTATION
    public BakedModel get(Orientation orientation) {
        BakedModel[] models = orientations;
        if (models == null) return null;
        if (orientation == null || orientation == Orientation.UNKNOWN || orientation.a == orientation.b
            || orientation.a == orientation.b.getOpposite()) {
            return models[DEFAULT_ORIENTATION.ordinal()];
        }
        BakedModel model = models[orientation.ordinal()];
        return model == null ? models[DEFAULT_ORIENTATION.ordinal()] : model;
    }

    // Places the model in the inventory icon pose: recentred on the origin so the slot frame can centre it, tilted so
    // the top faces the viewer, turned to show the face, then scaled to fit.
    // The model's gui display entry is deliberately not read: it is authored for GTNHLib's own item renderer,
    // which applies it against an identity matrix, whereas a Forge IItemRenderer is
    // handed an already-positioned frame and would double-count it.
    // The trailing translate recentres the model on the origin, and there is no +0.5 to match it. OpenGL
    // right-multiplies, so this runs before the inventory frame's glScalef(16, -16, 16), and a +0.5
    // here would be multiplied by sixteen and push the doll to the corner of the slot. The two angles were settled
    // against the running game.
    // The caller must have the model view matrix pushed and positioned.
    public static void applyIconDisplay() {
        GL11.glScalef(0.85F, 0.85F, 0.85F);
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    // Places the model in the held pose: sized for the hand and turned so the snout faces the holder.
    // Unlike applyIconDisplay this keeps the model inside its 0..1 cell, because Forge's equipped-item
    // path already applies a translate(-0.5, -0.5, -0.5) of its own. The scale and both rotations sit inside a
    // +0.5 / -0.5 bracket so they act about the model's centre rather than about the cell corner; that
    // pair cancels out and leaves no net offset. Outside the bracket the scale would be applied about the corner, which
    // multiplies the model's centre into (0.5 * scale, ...) and pushes the doll off the hand by one tenth of a
    // block per tenth of scale over 1.
    // Both angles were tuned against the running game, and they do not mirror the icon's: the icon frame flips Y
    // (glScalef(16, -16, 16)) and the held frame does not.
    // The caller must have the model view matrix pushed and positioned.
    public static void applyHandDisplay() {
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(1.2F, 1.2F, 1.2F);
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    // Clears the cache so the next bake starts clean.
    // event: texture atlas Pre event; only atlas type 0 is handled
    @SubscribeEvent
    public void beforeStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return;
        orientations = null;
    }

    // Bakes one model per Orientation and publishes the cache.
    // event: texture atlas Post event; on failure no partial cache is published
    @SubscribeEvent
    public void afterStitch(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() != 0) return;
        try {
            JSONModel source = ModelRegistry.getJSONModel(MODEL);
            Orientation[] values = Orientation.values();
            BakedModel[] models = new BakedModel[values.length];
            for (Orientation orientation : values) {
                if (orientation == Orientation.UNKNOWN) continue;
                models[orientation.ordinal()] = source.bake(() -> new Matrix4f().translation(0.5F, 0.5F, 0.5F)
                    .rotateTowards(
                        -orientation.a.offsetX,
                        -orientation.a.offsetY,
                        -orientation.a.offsetZ,
                        orientation.b.offsetX,
                        orientation.b.offsetY,
                        orientation.b.offsetZ)
                    .translate(-0.5F, -0.5F, -0.5F));
            }
            orientations = models;
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("Cannot bake Pigmee Fumo model", exception);
        }
    }
}
