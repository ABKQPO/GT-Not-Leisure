// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.render.model.pigmeeModel;

import net.minecraftforge.client.event.TextureStitchEvent;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc.ModelLoc;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;
import com.gtnewhorizon.gtnhlib.geometry.Orientation;
import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PigmeeFumoModel {

    public static final PigmeeFumoModel INSTANCE = new PigmeeFumoModel();

    public static final ModelLoc MODEL = ModelLoc.fromStr(ScienceNotLeisure.RESOURCE_ROOT_ID + ":block/pigmee_fumo");

    public static final Orientation DEFAULT_ORIENTATION = Orientation.NORTH_UP;

    public volatile BakedModel[] orientations;

    public BakedModel get(Orientation orientation) {
        BakedModel[] models = orientations;
        if (models == null) return null;
        if (orientation == null || orientation == Orientation.UNKNOWN
            || orientation.a == orientation.b
            || orientation.a == orientation.b.getOpposite()) {
            return models[DEFAULT_ORIENTATION.ordinal()];
        }
        BakedModel model = models[orientation.ordinal()];
        return model == null ? models[DEFAULT_ORIENTATION.ordinal()] : model;
    }

    public static void applyIconDisplay() {
        GL11.glScalef(0.85F, 0.85F, 0.85F);
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    public static void applyHandDisplay() {
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(1.2F, 1.2F, 1.2F);
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    @SubscribeEvent
    public void beforeStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return;
        orientations = null;
    }

    @SubscribeEvent
    public void afterStitch(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() != 0) return;
        try {
            JSONModel source = ModelRegistry.getJSONModel(MODEL);
            Orientation[] values = Orientation.values();
            BakedModel[] models = new BakedModel[values.length];
            for (Orientation orientation : values) {
                if (orientation == Orientation.UNKNOWN) continue;
                models[orientation.ordinal()] = source.bake(
                    () -> new Matrix4f().translation(0.5F, 0.5F, 0.5F)
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
