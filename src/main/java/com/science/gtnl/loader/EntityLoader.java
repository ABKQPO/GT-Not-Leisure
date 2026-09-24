package com.science.gtnl.loader;

import com.science.gtnl.common.entity.EntityArrowCustom;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.common.entity.EntityPlayerLeashKnot;
import com.science.gtnl.common.entity.EntitySaddleSlime;
import com.science.gtnl.common.entity.EntitySteamRocket;
import com.science.gtnl.utils.enums.ModList;

import Forge.NullPointerException;
import cpw.mods.fml.common.registry.EntityRegistry;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class EntityLoader {

    public static void registry() {
        EntityRegistry
            .registerModEntity(NullPointerException.class, "NullPointerException", 1024, "Forge", 64, 2, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntitySteamRocket.class, "SteamRocket", 150, 1, false);
        EntityRegistry
            .registerModEntity(EntityArrowCustom.class, "ArrowCustom", 0, ModList.ScienceNotLeisure.ID, 64, 2, true);
        EntityRegistry
            .registerModEntity(EntitySaddleSlime.class, "SaddleSlime", 1, ModList.ScienceNotLeisure.ID, 64, 2, true);
        EntityRegistry.registerModEntity(
            EntityPlayerLeashKnot.class,
            "PlayerLeashKnot",
            2,
            ModList.ScienceNotLeisure.ID,
            64,
            2,
            true);
        EntityRegistry.registerModEntity(EntityMajoBroom.class, "MajoBroom", 3, ModList.ScienceNotLeisure.ID, 80, 1, false);
    }
}
