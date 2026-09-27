/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
package com.science.gtnl.common.render.model;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position.ModelDisplay;
import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc.ModelLoc;
import com.gtnewhorizon.gtnhlib.client.model.loading.TexHelper.BaseCheckedTex;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.render.model.JsonBlockModel.Geometry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client-side model cache for the Pigmee Fumo: one baked geometry per horizontal facing, plus the display
 * variants used by the item renderer.
 *
 * <p>
 * Loading and baking happen around the texture stitch so the atlas sprites exist when the quads are baked.
 * The caches are published as whole arrays/immutable maps and never mutated in place, so a render thread
 * reading a snapshot can never observe a half-built state.
 */
@SideOnly(Side.CLIENT)
public class PigmeeFumoModel {

    public static final PigmeeFumoModel INSTANCE = new PigmeeFumoModel();

    private static final ModelLoc MODEL = ModelLoc.fromStr(ScienceNotLeisure.RESOURCE_ROOT_ID + ":block/pigmee_fumo");

    /** Display contexts consumed by the item renderer. */
    private static final Position[] ITEM_POSITIONS = { Position.GUI, Position.FIRSTPERSON_RIGHTHAND,
        Position.THIRDPERSON_RIGHTHAND, Position.GROUND, Position.FIXED };

    private JsonBlockModel pending;
    private volatile Geometry[] orientations;
    private volatile Map<Position, Geometry> itemModels;

    private PigmeeFumoModel() {}

    /**
     * @param front horizontal facing
     * @return the immutable geometry for that facing with UP as its up axis, or null while unloaded; an
     *         invalid direction falls back to NORTH. Callers must not modify the returned array or quads.
     */
    public Geometry get(ForgeDirection front) {
        Geometry[] models = orientations;
        if (models == null) return null;
        int ordinal = front == null ? ForgeDirection.NORTH.ordinal() : front.ordinal();
        if (ordinal < 2 || ordinal > 5) ordinal = ForgeDirection.NORTH.ordinal();
        return models[ordinal];
    }

    /**
     * @param position one of GUI/FIRSTPERSON_RIGHTHAND/THIRDPERSON_RIGHTHAND/GROUND/FIXED
     * @return the display-baked geometry, or null when unloaded or the key is absent
     */
    public Geometry getItem(Position position) {
        Map<Position, Geometry> models = itemModels;
        return models == null ? null : models.get(position);
    }

    /**
     * Clears stale caches, loads the model and registers its textures with the block atlas.
     *
     * @param event texture atlas Pre event; only atlas type 0 is handled. Failures are logged and leave the
     *              cache empty rather than propagating to the event bus.
     */
    @SubscribeEvent
    public void beforeStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return;
        orientations = null;
        itemModels = null;
        pending = null;
        try {
            pending = new JsonBlockModel(
                JsonModelResources.load(
                    MODEL,
                    Minecraft.getMinecraft()
                        .getResourceManager(),
                    new HashMap<>(),
                    new HashSet<>()));
            // Register exactly the texture names the model itself resolves sprites by. getAtlasSprite matches
            // by exact key, so the atlas key and the model's texture value must be the same string. The model
            // JSON already carries the blocks/ segment the block atlas requires.
            for (String texture : pending.getTextures()
                .values()) {
                if (texture == null || texture.startsWith("#")) continue;
                if (!texture.contains(":")) {
                    ScienceNotLeisure.LOG.warn("Pigmee Fumo model texture {} is not namespaced", texture);
                    continue;
                }
                ScienceNotLeisure.LOG.debug("Pigmee Fumo registering atlas icon {}", texture);
                if (event.map.registerIcon(texture) instanceof BaseCheckedTex sprite) {
                    sprite.nhlib$setHasBase(false);
                }
            }
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("Cannot load Pigmee Fumo model", exception);
        }
    }

    /**
     * Bakes the four facings and five display variants, then publishes the caches atomically.
     *
     * @param event texture atlas Post event; on failure no partial cache is published
     */
    @SubscribeEvent
    public void afterStitch(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() != 0 || pending == null) return;
        try {
            Geometry[] models = new Geometry[6];
            for (ForgeDirection front : new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH,
                ForgeDirection.WEST, ForgeDirection.EAST }) {
                models[front.ordinal()] = pending.bake(front, ForgeDirection.UP);
            }
            Geometry source = models[ForgeDirection.NORTH.ordinal()];
            Map<Position, Geometry> items = new EnumMap<>(Position.class);
            for (Position position : ITEM_POSITIONS) {
                ModelDisplay display = pending.getDisplay(position);
                items.put(position, JsonModelResources.bakeItem(source, display, position == Position.GUI));
            }
            orientations = models;
            itemModels = items;
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("Cannot bake Pigmee Fumo model", exception);
        } finally {
            pending = null;
        }
    }
}
