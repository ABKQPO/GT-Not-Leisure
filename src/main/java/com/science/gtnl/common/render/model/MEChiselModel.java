package com.science.gtnl.common.render.model;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.joml.Matrix4f;

import com.gtnewhorizon.gtnhlib.api.IBlockModelProvider;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
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
public class MEChiselModel implements IBlockModelProvider {

    public static final MEChiselModel INSTANCE = new MEChiselModel();

    private static final ModelLoc MODEL = ModelLoc.fromStr(ScienceNotLeisure.RESOURCE_ROOT_ID + ":block/me_chisel");
    private volatile BakedModel[] orientations;

    @Override
    public BakedModel getModel(BakedModelQuadContext context) {
        BakedModel[] models = orientations;
        if (models == null) return ModelRegistry.getBakedModel(context.getBlockState());
        Orientation orientation = context.getBlockState()
            .getPropertyValue("orientation");
        BakedModel model = orientation == null ? null : models[orientation.ordinal()];
        return model == null ? models[Orientation.NORTH_UP.ordinal()] : model;
    }

    @SubscribeEvent
    public void afterStitch(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() != 0) return;
        orientations = null;
        try {
            JSONModel source = ModelRegistry.getJSONModel(MODEL);
            Orientation[] values = Orientation.values();
            BakedModel[] models = new BakedModel[values.length];
            for (Orientation orientation : values) {
                ForgeDirection forward = orientation.a;
                ForgeDirection up = orientation.b;
                if (orientation == Orientation.UNKNOWN || forward == up || forward == up.getOpposite()) continue;

                // Map the model's north and top faces to AE2's forward and up directions.
                Matrix4f transform = new Matrix4f().translation(0.5F, 0.5F, 0.5F)
                    .rotateTowards(
                        -forward.offsetX,
                        -forward.offsetY,
                        -forward.offsetZ,
                        up.offsetX,
                        up.offsetY,
                        up.offsetZ)
                    .translate(-0.5F, -0.5F, -0.5F);
                models[orientation.ordinal()] = source.bake(() -> transform);
            }
            orientations = models;
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("Cannot bake ME Chisel orientations", exception);
        }
    }
}
