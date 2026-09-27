package com.science.gtnl.common.render.beamformer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position.ModelDisplay;
import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc.ModelLoc;
import com.gtnewhorizon.gtnhlib.client.model.loading.TexHelper.BaseCheckedTex;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.render.model.JsonBlockModel;
import com.science.gtnl.common.render.model.JsonBlockModel.Geometry;
import com.science.gtnl.common.render.model.JsonModelResources;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BeamFormerModel {

    public static final BeamFormerModel INSTANCE = new BeamFormerModel();
    private static final ModelLoc MODEL = ModelLoc.fromStr(ScienceNotLeisure.RESOURCE_ROOT_ID + ":block/beam_former");
    private JsonBlockModel pending;
    private volatile Geometry[] orientations;
    private volatile Map<Position, Geometry> itemModels;

    public Geometry get(ForgeDirection front, ForgeDirection up) {
        Geometry[] models = orientations;
        if (models == null || front == ForgeDirection.UNKNOWN || up == ForgeDirection.UNKNOWN) return null;
        return models[front.ordinal() * 6 + up.ordinal()];
    }

    public Geometry getItem(Position position) {
        Map<Position, Geometry> models = itemModels;
        return models == null ? null : models.get(position);
    }

    @SubscribeEvent
    public void beforeStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return;
        orientations = null;
        itemModels = null;
        pending = null;
        try {
            pending = new JsonBlockModel(
                load(
                    MODEL,
                    Minecraft.getMinecraft()
                        .getResourceManager(),
                    new HashMap<>(),
                    new HashSet<>()));
            for (String texture : pending.getTextures()
                .values()) {
                if (event.map.registerIcon(texture) instanceof BaseCheckedTex sprite) {
                    sprite.nhlib$setHasBase(false);
                }
            }
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("Cannot load Beam Former model", exception);
        }
    }

    @SubscribeEvent
    public void afterStitch(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() != 0 || pending == null) return;
        try {
            Geometry[] models = new Geometry[36];
            for (ForgeDirection front : ForgeDirection.VALID_DIRECTIONS) {
                for (ForgeDirection up : ForgeDirection.VALID_DIRECTIONS) {
                    if (front != up && front != up.getOpposite()) {
                        models[front.ordinal() * 6 + up.ordinal()] = pending.bake(front, up);
                    }
                }
            }
            Map<Position, Geometry> items = new EnumMap<>(Position.class);
            Geometry source = models[ForgeDirection.NORTH.ordinal() * 6 + ForgeDirection.UP.ordinal()];
            for (Position position : new Position[] { Position.GUI, Position.FIRSTPERSON_RIGHTHAND,
                Position.THIRDPERSON_RIGHTHAND, Position.GROUND, Position.FIXED }) {
                items.put(position, bakeItem(source, pending.getDisplay(position), position == Position.GUI));
            }
            orientations = models;
            itemModels = items;
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("Cannot bake Beam Former model", exception);
        } finally {
            pending = null;
        }
    }

    private static Geometry bakeItem(Geometry source, ModelDisplay display, boolean gui) {
        return JsonModelResources.bakeItem(source, display, gui);
    }

    private JSONModel load(ModelLoc location, IResourceManager manager, Map<ModelLoc, JSONModel> models,
        Set<ModelLoc> resolving) {
        return JsonModelResources.load(location, manager, models, resolving);
    }
}
