package com.reavaritia.common;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;

import com.reavaritia.common.items.BlazeAxe;
import com.reavaritia.common.items.BlazeHoe;
import com.reavaritia.common.items.BlazePickaxe;
import com.reavaritia.common.items.BlazeShovel;
import com.reavaritia.common.items.BlazeSword;
import com.reavaritia.common.items.ChronarchsClock;
import com.reavaritia.common.items.CrystalAxe;
import com.reavaritia.common.items.CrystalHoe;
import com.reavaritia.common.items.CrystalPickaxe;
import com.reavaritia.common.items.CrystalShovel;
import com.reavaritia.common.items.CrystalSword;
import com.reavaritia.common.items.InfinityAxe;
import com.reavaritia.common.items.InfinityBucket;
import com.reavaritia.common.items.InfinityElytra;
import com.reavaritia.common.items.InfinityHoe;
import com.reavaritia.common.items.InfinityPickaxe;
import com.reavaritia.common.items.InfinityShovel;
import com.reavaritia.common.items.InfinitySword;
import com.reavaritia.common.items.InfinityTotem;
import com.reavaritia.common.items.MatterCluster;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import gregtech.api.enums.Mods;

public class ItemLoader {

    public static Item CrystalSword = new CrystalSword();
    public static Item CrystalAxe = new CrystalAxe();
    public static Item CrystalPickaxe = new CrystalPickaxe();
    public static Item CrystalShovel = new CrystalShovel();
    public static Item CrystalHoe = new CrystalHoe();
    public static Item BlazeSword = new BlazeSword();
    public static Item BlazeAxe = new BlazeAxe();
    public static Item BlazePickaxe = new BlazePickaxe();
    public static Item BlazeShovel = new BlazeShovel();
    public static Item BlazeHoe = new BlazeHoe();
    public static Item InfinitySword = new InfinitySword();
    public static Item InfinityPickaxe = new InfinityPickaxe();
    public static Item InfinityAxe = new InfinityAxe();
    public static Item InfinityShovel = new InfinityShovel();
    public static Item InfinityHoe = new InfinityHoe();
    public static Item InfinityTotem = new InfinityTotem();
    public static Item InfinityBucket = new InfinityBucket();
    public static Item MatterCluster = new MatterCluster();
    public static Item ChronarchsClock = new ChronarchsClock();
    public static Item InfinityElytra;

    public static void registerItems() {
        IRegistry(CrystalPickaxe, "CrystalPickaxe");
        IRegistry(CrystalHoe, "CrystalHoe");
        IRegistry(CrystalShovel, "CrystalShovel");
        IRegistry(CrystalAxe, "CrystalAxe");
        IRegistry(CrystalSword, "CrystalSword");
        IRegistry(BlazePickaxe, "BlazePickaxe");
        IRegistry(BlazeAxe, "BlazeAxe");
        IRegistry(BlazeHoe, "BlazeHoe");
        IRegistry(BlazeSword, "BlazeSword");
        IRegistry(BlazeShovel, "BlazeShovel");
        IRegistry(InfinitySword, "InfinitySword");
        IRegistry(InfinityAxe, "InfinityAxe");
        IRegistry(InfinityPickaxe, "InfinityPickaxe");
        IRegistry(InfinityShovel, "InfinityShovel");
        IRegistry(InfinityHoe, "InfinityHoe");
        IRegistry(InfinityTotem, "InfinityTotem");
        IRegistry(InfinityBucket, "InfinityBucket");
        IRegistry(MatterCluster, "MatterCluster");
        IRegistry(ChronarchsClock, "ChronarchsClock");

        BlockDispenser.dispenseBehaviorRegistry.putObject(ChronarchsClock, ChronarchsClock);

        if (Mods.EtFuturumRequiem.isModLoaded()) {
            registerInfinityElytra();
        }
    }

    @Optional.Method(modid = "etfuturum")
    public static void registerInfinityElytra() {
        if (!ConfigMixins.enableElytra) return;
        InfinityElytra = new InfinityElytra();
    }

    public static void IRegistry(Item item, String name) {
        GameRegistry.registerItem(item, name);
    }

}
