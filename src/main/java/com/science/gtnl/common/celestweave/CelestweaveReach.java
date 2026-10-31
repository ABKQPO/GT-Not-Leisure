package com.science.gtnl.common.celestweave;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.config.ItemConfig;

import vazkii.botania.common.Botania;

public class CelestweaveReach {

    private static final Map<UUID, Float> SERVER = new ConcurrentHashMap<>();
    private static final Map<UUID, Float> CLIENT = new ConcurrentHashMap<>();

    private static Map<UUID, Float> applied(EntityPlayer player) {
        return player.worldObj.isRemote ? CLIENT : SERVER;
    }

    public static float wanted(EntityPlayer player) {
        ItemStack chest = CelestweaveData.piece(player, CelestweaveArmor.CHEST);
        if (chest == null || !CelestweaveData.active(chest, CelestweaveModule.REACH)
            || !CelestweaveEnergy.hasEnergy(player, CelestweaveArmor.CHEST, chest)) return 0F;
        var config = ItemConfig.celestweave;
        return switch (CelestweaveModuleSetting.REACH_LEVEL.get(chest)) {
            case 2 -> config.reachLevel3;
            case 1 -> config.reachLevel2;
            default -> config.reachLevel1;
        };
    }

    public static void update(EntityPlayer player) {
        float want = wanted(player);
        Map<UUID, Float> applied = applied(player);
        float had = applied.getOrDefault(player.getUniqueID(), 0F);
        if (want == had) return;
        Botania.proxy.setExtraReach(player, want - had);
        if (want == 0F) applied.remove(player.getUniqueID());
        else applied.put(player.getUniqueID(), want);
    }

    public static void reset(EntityPlayer player) {
        Float had = applied(player).remove(player.getUniqueID());
        if (had != null && had != 0F) Botania.proxy.setExtraReach(player, -had);
    }

    public static void forget(EntityPlayer player) {
        applied(player).remove(player.getUniqueID());
    }

    public static void clearClient() {
        CLIENT.clear();
    }
}
