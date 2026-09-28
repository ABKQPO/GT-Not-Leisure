/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
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

/**
 * Client-side model cache for the Pigmee Fumo: one baked geometry per orientation.
 *
 * <p>
 * Loading, caching and parent resolution are delegated to GTNHLib's {@link ModelRegistry}, which also injects the
 * model's textures into the block atlas because {@code ScienceNotLeisure.MODID} is registered through
 * {@code ModelRegistry.registerModid}. Nothing here parses JSON or touches the atlas by hand.
 *
 * <p>
 * The caches are published as a whole array and never mutated in place, so a render thread reading a snapshot can
 * never observe a half-built state.
 */
@SideOnly(Side.CLIENT)
public class PigmeeFumoModel {

    public static final PigmeeFumoModel INSTANCE = new PigmeeFumoModel();

    private static final ModelLoc MODEL = ModelLoc.fromStr(ScienceNotLeisure.RESOURCE_ROOT_ID + ":block/pigmee_fumo");

    /** The orientation a freshly placed or head-worn doll uses. */
    public static final Orientation DEFAULT_ORIENTATION = Orientation.NORTH_UP;

    private volatile BakedModel[] orientations;

    private PigmeeFumoModel() {}

    /**
     * @param orientation requested orientation, may be null
     * @return the baked model for that orientation, or null while unloaded; an unknown or invalid orientation
     *         falls back to {@link #DEFAULT_ORIENTATION}
     */
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

    /**
     * Places the model in an isometric icon pose: recentred on its own origin, tilted so the top faces the viewer,
     * turned to show the face, then scaled to fit the slot.
     *
     * <p>
     * Deliberately does not read the model's {@code gui} display entry. That entry is authored for GTNHLib's own item
     * renderer, which applies it against an identity matrix; a Forge {@link net.minecraftforge.client.IItemRenderer}
     * instead receives an already-positioned frame, so reusing the entry there double-counts the transform.
     *
     * <p>
     * Only the recentring translate is present, with no matching {@code +0.5} afterwards. OpenGL right-multiplies, so
     * this whole sequence is applied to the vertices <em>before</em> the inventory frame's {@code glScalef(16, -16,
     * 16)}; a leading {@code +0.5} would therefore be multiplied by sixteen and push the model off to the corner of
     * the slot instead of its centre. Recentring to the origin and letting the frame's own {@code translate(8, 8, 0)}
     * do the centring is what keeps it in the middle.
     *
     * <p>
     * The two angles were settled against the running game rather than derived: the elevation is positive because the
     * inventory frame's {@code -16} flips vertical space, and the bearing is 135 rather than 45 because 45 showed the
     * doll's back. Elevation controls how much of the top is visible and the bearing only spins about the vertical, so
     * the two are independent.
     *
     * <p>
     * The caller must have the model view matrix pushed and positioned.
     *
     * @param model the model to size the transform for; may be null, in which case nothing is applied
     */
    public static void applyIconDisplay(BakedModel model) {
        if (model == null) return;
        GL11.glScalef(0.625F, 0.625F, 0.625F);
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    /**
     * Clears the cache so the next bake starts clean.
     *
     * @param event texture atlas Pre event; only atlas type 0 is handled
     */
    @SubscribeEvent
    public void beforeStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return;
        orientations = null;
    }

    /**
     * Bakes one model per {@link Orientation} and publishes the cache.
     *
     * @param event texture atlas Post event; on failure no partial cache is published
     */
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
