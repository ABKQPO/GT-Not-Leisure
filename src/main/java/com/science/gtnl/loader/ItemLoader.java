package com.science.gtnl.loader;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.item.items.SuspiciousStew.*;
import static com.science.gtnl.utils.text.AnimatedTooltipHandler.*;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.ItemDebug;
import com.science.gtnl.common.item.ItemInfinityCell;
import com.science.gtnl.common.item.ItemInfinityItem;
import com.science.gtnl.common.item.ItemRecord;
import com.science.gtnl.common.item.MetaItemAdder;
import com.science.gtnl.common.item.items.CircuitIntegratedPlus;
import com.science.gtnl.common.item.items.ElectricProspectorTool;
import com.science.gtnl.common.item.items.FakeItemSiren;
import com.science.gtnl.common.item.items.GTNLItemBucket;
import com.science.gtnl.common.item.items.KFCFamily;
import com.science.gtnl.common.item.items.NetherTeleporter;
import com.science.gtnl.common.item.items.NullPointerException;
import com.science.gtnl.common.item.items.PortableItem;
import com.science.gtnl.common.item.items.SlimeSaddle;
import com.science.gtnl.common.item.items.SteamRocketItem;
import com.science.gtnl.common.item.items.Stick;
import com.science.gtnl.common.item.items.SuspiciousStew;
import com.science.gtnl.common.item.items.TestItem;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.item.items.TwilightSword;
import com.science.gtnl.common.item.items.VeinMiningPickaxe;
import com.science.gtnl.common.item.items.WirelessUpgradeChip;
import com.science.gtnl.common.item.items.bauble.LuckyHorseshoe;
import com.science.gtnl.common.item.items.bauble.PhysicsCape;
import com.science.gtnl.common.item.items.bauble.RejectionRing;
import com.science.gtnl.common.item.items.bauble.RoyalGel;
import com.science.gtnl.common.item.items.bauble.SatietyRing;
import com.science.gtnl.common.item.items.bauble.SuperReachRing;
import com.science.gtnl.common.item.items.fuelRod.FuelRod;
import com.science.gtnl.common.item.items.fuelRod.FuelRodDepleted;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.text.AnimatedTooltipHandler;

import appeng.api.storage.StorageChannel;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.common.render.items.InfinityMetaItemRenderer;

public class ItemLoader {

    public static Item steamRocket;
    public static Item nullPointerException;
    public static Item fakeItemSiren;
    public static Item netherTeleporter;
    public static Item testItem;
    public static Item KFCFamily;
    public static Item twilightSword;
    public static Item circuitIntegratedPlus;
    public static Item timeStopPocketWatch;
    public static Item infinityTorch;
    public static Item infinityWaterBucket;
    public static Item infinityLavaBucket;
    public static Item infinityHoneyBucket;
    public static Item infinityShimmerBucket;
    public static Item superstrongSponge;
    public static Item slimeSaddle;
    public static Item infinityFuelRodDepleted;
    public static Item infinityFuelRod;
    public static Item metaItem;
    public static ItemBucket honeyBucket;
    public static ItemBucket shimmerBucket;
    public static ItemInfinityCell infinityCell;
    public static Item wirelessUpgradeChip;
    public static Item stick;
    public static Item suspiciousStew;
    public static Item veinMiningPickaxe;
    public static Item portableItem;

    public static Item electricProspectorTool;

    public static Item satietyRing;
    public static Item rejectionRing;
    public static Item superReachRing;
    public static Item physicsCape;
    public static Item royalGel;
    public static Item luckyHorseshoe;

    public static ItemStack infinityItemCell;
    public static ItemStack infinityDyeCell;
    public static ItemStack infinityDyeFluidCell;
    public static ItemStack infinityCobblestoneCell;

    public static void registryItems() {
        portableItem = new PortableItem();
        veinMiningPickaxe = new VeinMiningPickaxe();
        suspiciousStew = new SuspiciousStew();
        steamRocket = new SteamRocketItem();
        nullPointerException = new NullPointerException();
        fakeItemSiren = new FakeItemSiren();
        wirelessUpgradeChip = new WirelessUpgradeChip();
        testItem = new TestItem();
        netherTeleporter = new NetherTeleporter();
        electricProspectorTool = new ElectricProspectorTool();
        KFCFamily = new KFCFamily(20, 20, true);
        twilightSword = new TwilightSword();
        circuitIntegratedPlus = new CircuitIntegratedPlus();
        timeStopPocketWatch = new TimeStopPocketWatch();
        infinityTorch = new ItemInfinityItem("InfinityTorch", Blocks.torch, GTNLItemList.InfinityTorch);
        infinityWaterBucket = new ItemInfinityItem(
            "InfinityWaterBucket",
            Blocks.water,
            FluidRegistry.getFluid("water"),
            GTNLItemList.InfinityWaterBucket);
        infinityLavaBucket = new ItemInfinityItem(
            "InfinityLavaBucket",
            Blocks.lava,
            FluidRegistry.getFluid("lava"),
            GTNLItemList.InfinityLavaBucket);
        infinityHoneyBucket = new ItemInfinityItem(
            "InfinityHoneyBucket",
            BlockLoader.honeyFluidBlock,
            BlockLoader.honeyFluid,
            GTNLItemList.InfinityHoneyBucket);
        infinityShimmerBucket = new ItemInfinityItem(
            "InfinityShimmerBucket",
            BlockLoader.shimmerFluidBlock,
            BlockLoader.shimmerFluid,
            GTNLItemList.InfinityShimmerBucket);
        superstrongSponge = new ItemInfinityItem(
            "SuperstrongSponge",
            (Block) null,
            false,
            GTNLItemList.SuperstrongSponge);
        RecordLoader.recordSus = new ItemRecord("sus").setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        RecordLoader.recordNewHorizons = new ItemRecord("new_horizons")
            .setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        infinityFuelRodDepleted = new FuelRodDepleted("InfinityFuelRodDepleted", 2000);
        infinityFuelRod = new FuelRod(
            "InfinityFuelRod",
            1,
            491520,
            500,
            15000,
            160000,
            70F,
            new ItemStack(infinityFuelRodDepleted, 1));
        metaItem = new MetaItemAdder("MetaItem", GTNLCreativeTabs.GTNotLeisureItem)
            .setTextureName(RESOURCE_ROOT_ID + ":" + "MetaItem/0");
        honeyBucket = GTNLItemBucket.create(BlockLoader.honeyFluid);
        shimmerBucket = GTNLItemBucket.create(BlockLoader.shimmerFluid);
        superReachRing = new SuperReachRing();
        satietyRing = new SatietyRing();
        rejectionRing = new RejectionRing();
        slimeSaddle = new SlimeSaddle();
        physicsCape = new PhysicsCape();
        royalGel = new RoyalGel();
        luckyHorseshoe = new LuckyHorseshoe();
        infinityCell = new ItemInfinityCell();
        stick = new Stick();

        var subDyeItems = new ItemInfinityCell.SubItem[16];
        for (short i = 0; i < 16; i++) {
            subDyeItems[i] = ItemInfinityCell.SubItem.getInstance(ItemList.DYE_ONLY_ITEMS[i].get(1));
        }
        infinityDyeCell = ItemInfinityCell
            .getSubItem(StorageChannel.ITEMS, "InfinityCell.dye.name", "InfinityDyeCell", subDyeItems);

        String[] colors = { "Black", "Pink", "Red", "Orange", "Yellow", "Green", "Lime", "Blue", "LightBlue", "Cyan",
            "Brown", "Magenta", "Purple", "Gray", "LightGray", "White" };
        var subDyeFluid = new ItemInfinityCell.SubItem[colors.length];
        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            String fluidName = "dye.chemical.dye" + color.toLowerCase();
            subDyeFluid[i] = ItemInfinityCell.SubItem.getInstance(FluidRegistry.getFluid(fluidName));
        }
        infinityDyeFluidCell = ItemInfinityCell
            .getSubItem(StorageChannel.FLUIDS, "InfinityCell.dye.fluid.name", "InfinityDyeFluidCell", subDyeFluid);

        infinityCobblestoneCell = ItemInfinityCell.getSubItem(
            StorageChannel.ITEMS,
            "InfinityCobblestoneCell",
            ItemInfinityCell.SubItem.getInstance(Blocks.cobblestone));

        GameRegistry.registerItem(wirelessUpgradeChip, "WirelessUpgradeChip");
        GameRegistry.registerItem(stick, "Stick");
        GameRegistry.registerItem(portableItem, "PortableItem");
        GameRegistry.registerItem(veinMiningPickaxe, "VeinMiningPickaxe");
        GameRegistry.registerItem(suspiciousStew, "SuspiciousStew");
        GameRegistry.registerItem(steamRocket, "SteamRocket");
        GameRegistry.registerItem(nullPointerException, "NullPointerException");
        GameRegistry.registerItem(netherTeleporter, "NetherTeleporter");
        GameRegistry.registerItem(fakeItemSiren, "FakeItemSiren");
        GameRegistry.registerItem(testItem, "TestItem");
        GameRegistry.registerItem(electricProspectorTool, "ElectricProspectorTool");
        GameRegistry.registerItem(KFCFamily, "KFCFamily");
        GameRegistry.registerItem(twilightSword, "TwilightSword");
        GameRegistry.registerItem(circuitIntegratedPlus, "CircuitIntegratedPlus");
        GameRegistry.registerItem(timeStopPocketWatch, "TimeStopPocketWatch");
        GameRegistry.registerItem(infinityTorch, "InfinityTorch");
        GameRegistry.registerItem(infinityWaterBucket, "InfinityWaterBucket");
        GameRegistry.registerItem(infinityLavaBucket, "InfinityLavaBucket");
        GameRegistry.registerItem(infinityHoneyBucket, "InfinityHoneyBucket");
        GameRegistry.registerItem(infinityShimmerBucket, "InfinityShimmerBucket");
        GameRegistry.registerItem(superstrongSponge, "SuperstrongSponge");
        GameRegistry.registerItem(slimeSaddle, "SlimeSaddle");
        GameRegistry.registerItem(RecordLoader.recordSus, "record_sus");
        GameRegistry.registerItem(RecordLoader.recordNewHorizons, "record_new_horizons");
        GameRegistry.registerItem(infinityFuelRodDepleted, "InfinityFuelRodDepleted");
        GameRegistry.registerItem(infinityFuelRod, "InfinityFuelRod");
        GameRegistry.registerItem(metaItem, "MetaItem");

        GameRegistry.registerItem(satietyRing, "SatietyRing");
        GameRegistry.registerItem(rejectionRing, "RejectionRing");
        GameRegistry.registerItem(superReachRing, "SuperReachRing");
        GameRegistry.registerItem(royalGel, "RoyalGel");
        GameRegistry.registerItem(physicsCape, "PhysicsCape");
        GameRegistry.registerItem(luckyHorseshoe, "LuckyHorseshoe");
        GameRegistry.registerItem(infinityCell, "InfinityCell");
        GameRegistry.registerItem(ItemDebug.INSTANCE, "debug");

        GTNLItemList.RecordSus.set(new ItemStack(RecordLoader.recordSus, 1));
        GTNLItemList.RecordNewHorizons.set(new ItemStack(RecordLoader.recordNewHorizons, 1));
        GTNLItemList.RecordLavaChicken.set(new ItemStack(RecordLoader.recordLavaChicken, 1));
        GTNLItemList.InfinityFuelRodDepleted.set(new ItemStack(infinityFuelRodDepleted, 1));
        GTNLItemList.InfinityFuelRod.set(new ItemStack(infinityFuelRod, 1));
        GTNLItemList.HoneyBucket.set(new ItemStack(honeyBucket, 1));
        GTNLItemList.ShimmerBucket.set(new ItemStack(shimmerBucket, 1));
        GTNLItemList.InfinityCell.set(new ItemStack(infinityCell, 1));
        GTNLItemList.InfinityDyeCell.set(infinityDyeCell);
        GTNLItemList.InfinityDyeFluidCell.set(infinityDyeFluidCell);
        GTNLItemList.InfinityCobblestoneCell.set(infinityCobblestoneCell);
    }

    public static void registryMetaItems() {
        GTNLItemList.TrollFace.set(
            MetaItemAdder
                .initItem("TrollFace", 0, new String[] { StatCollector.translateToLocal("Tooltip_TrollFace") }));
        GTNLItemList.DepletedExcitedNaquadahFuelRod.set(
            MetaItemAdder.initItem(
                "§bDepleted Excited Naquadah Fuel Rod",
                1,
                new String[] { StatCollector.translateToLocal("Tooltip_DepletedExcitedNaquadahFuelRod") }));
        GTNLItemList.BlazeCube.set(
            MetaItemAdder
                .initItem("§eBlaze Cube", 2, new String[] { StatCollector.translateToLocal("Tooltip_BlazeCube") }));
        GTNLItemList.EnhancementCore.set(
            MetaItemAdder.initItem(
                "Enhancement Core",
                3,
                new String[] { StatCollector.translateToLocal("Tooltip_EnhancementCore") }));
        GTNLItemList.StellarConstructionFrameMaterial.set(
            MetaItemAdder.initItem(
                "Stellar Construction Frame Material",
                4,
                new String[] { StatCollector.translateToLocal("Tooltip_TestItem0.line1") }));
        GTNLItemList.ActivatedGaiaPylon.set(MetaItemAdder.initItem(5));
        GTNLItemList.PrecisionSteamMechanism.set(MetaItemAdder.initItem(6));
        GTNLItemList.MeteorMinerSchematic1.set(
            MetaItemAdder.initItem(
                "Meteor Miner Tier 1 Schematic",
                7,
                new String[] { StatCollector.translateToLocal("Tooltip_MeteorMinerSchematic1") }));
        GTNLItemList.MeteorMinerSchematic2.set(
            MetaItemAdder.initItem(
                "Meteor Miner Tier 2 Schematic",
                8,
                new String[] { StatCollector.translateToLocal("Tooltip_MeteorMinerSchematic2") }));
        GTNLItemList.CircuitResonaticULV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic ULV",
                9,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticULV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticULV_01") }));
        GTNLItemList.CircuitResonaticLV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic LV",
                10,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticLV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticLV_01") }));
        GTNLItemList.CircuitResonaticMV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic MV",
                11,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticMV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticMV_01") }));
        GTNLItemList.CircuitResonaticHV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic HV",
                12,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticHV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticHV_01") }));
        GTNLItemList.CircuitResonaticEV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic EV",
                13,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticEV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticEV_01") }));
        GTNLItemList.CircuitResonaticIV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic IV",
                14,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticIV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticIV_01") }));
        GTNLItemList.CircuitResonaticLuV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic LuV",
                15,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticLuV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticLuV_01") }));
        GTNLItemList.CircuitResonaticZPM.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic ZPM",
                16,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticZPM_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticZPM_01") }));
        GTNLItemList.CircuitResonaticUV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic UV",
                17,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticUV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticUV_01") }));
        GTNLItemList.CircuitResonaticUHV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic UHV",
                18,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticUHV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticUHV_01") }));
        GTNLItemList.CircuitResonaticUEV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic UEV",
                19,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticUEV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticUEV_01") }));
        GTNLItemList.CircuitResonaticUIV.set(
            MetaItemAdder.initItem(
                "Circuit Resonatic UIV",
                20,
                new String[] { StatCollector.translateToLocal("Tooltip_CircuitResonaticUIV_00"),
                    StatCollector.translateToLocal("Tooltip_CircuitResonaticUIV_01") }));
        GTNLItemList.VerySimpleCircuit.set(
            MetaItemAdder.initItem(
                "Very Simple Circuit",
                21,
                new String[] { StatCollector.translateToLocal("Tooltip_VerySimpleCircuit_00"),
                    StatCollector.translateToLocal("Tooltip_VerySimpleCircuit_01") }));
        GTNLItemList.SimpleCircuit.set(
            MetaItemAdder.initItem(
                "Simple Circuit",
                22,
                new String[] { StatCollector.translateToLocal("Tooltip_SimpleCircuit_00"),
                    StatCollector.translateToLocal("Tooltip_SimpleCircuit_01") }));
        GTNLItemList.BasicCircuit.set(
            MetaItemAdder.initItem(
                "Basic Circuit",
                23,
                new String[] { StatCollector.translateToLocal("Tooltip_BasicCircuit_00"),
                    StatCollector.translateToLocal("Tooltip_BasicCircuit_01") }));
        GTNLItemList.AdvancedCircuit.set(
            MetaItemAdder.initItem(
                "Advanced Circuit",
                24,
                new String[] { StatCollector.translateToLocal("Tooltip_AdvancedCircuit_00"),
                    StatCollector.translateToLocal("Tooltip_AdvancedCircuit_01") }));
        GTNLItemList.EliteCircuit.set(
            MetaItemAdder.initItem(
                "Elite Circuit",
                25,
                new String[] { StatCollector.translateToLocal("Tooltip_EliteCircuit_00"),
                    StatCollector.translateToLocal("Tooltip_EliteCircuit_01") }));
        GTNLItemList.StargateSingularity.set(MetaItemAdder.initItem(26))
            .setRender(new InfinityMetaItemRenderer());
        GTNLItemList.StargateCompressedSingularity.set(MetaItemAdder.initItem(27))
            .setRender(new InfinityMetaItemRenderer());
        GTNLItemList.BiowareSMDCapacitor.set(MetaItemAdder.initItem(28));
        GTNLItemList.BiowareSMDDiode.set(MetaItemAdder.initItem(29));
        GTNLItemList.BiowareSMDInductor.set(MetaItemAdder.initItem(30));
        GTNLItemList.BiowareSMDResistor.set(MetaItemAdder.initItem(31));
        GTNLItemList.BiowareSMDTransistor.set(MetaItemAdder.initItem(32));
        GTNLItemList.CosmicSMDCapacitor.set(MetaItemAdder.initItem(33));
        GTNLItemList.CosmicSMDDiode.set(MetaItemAdder.initItem(34));
        GTNLItemList.CosmicSMDInductor.set(MetaItemAdder.initItem(35));
        GTNLItemList.CosmicSMDResistor.set(MetaItemAdder.initItem(36));
        GTNLItemList.CosmicSMDTransistor.set(MetaItemAdder.initItem(37));
        GTNLItemList.ExoticSMDCapacitor.set(MetaItemAdder.initItem(38));
        GTNLItemList.ExoticSMDDiode.set(MetaItemAdder.initItem(39));
        GTNLItemList.ExoticSMDInductor.set(MetaItemAdder.initItem(40));
        GTNLItemList.ExoticSMDResistor.set(MetaItemAdder.initItem(41));
        GTNLItemList.ExoticSMDTransistor.set(MetaItemAdder.initItem(42));
        GTNLItemList.TemporallySMDCapacitor.set(MetaItemAdder.initItem(43));
        GTNLItemList.TemporallySMDDiode.set(MetaItemAdder.initItem(44));
        GTNLItemList.TemporallySMDInductor.set(MetaItemAdder.initItem(45));
        GTNLItemList.TemporallySMDResistor.set(MetaItemAdder.initItem(46));
        GTNLItemList.TemporallySMDTransistor.set(MetaItemAdder.initItem(47));
        GTNLItemList.LVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "LV Parallel Controller Core",
                48,
                new String[] { StatCollector.translateToLocal("Tooltip_LVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_LVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_LVParallelControllerCore_02") }));

        GTNLItemList.MVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "MV Parallel Controller Core",
                49,
                new String[] { StatCollector.translateToLocal("Tooltip_MVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_MVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_MVParallelControllerCore_02") }));

        GTNLItemList.HVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "HV Parallel Controller Core",
                50,
                new String[] { StatCollector.translateToLocal("Tooltip_HVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_HVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_HVParallelControllerCore_02") }));

        GTNLItemList.EVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "EV Parallel Controller Core",
                51,
                new String[] { StatCollector.translateToLocal("Tooltip_EVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_EVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_EVParallelControllerCore_02") }));

        GTNLItemList.IVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "IV Parallel Controller Core",
                52,
                new String[] { StatCollector.translateToLocal("Tooltip_IVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_IVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_IVParallelControllerCore_02") }));

        GTNLItemList.LuVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "LuV Parallel Controller Core",
                53,
                new String[] { StatCollector.translateToLocal("Tooltip_LuVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_LuVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_LuVParallelControllerCore_02") }));

        GTNLItemList.ZPMParallelControllerCore.set(
            MetaItemAdder.initItem(
                "ZPM Parallel Controller Core",
                54,
                new String[] { StatCollector.translateToLocal("Tooltip_ZPMParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_ZPMParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_ZPMParallelControllerCore_02") }));

        GTNLItemList.UVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "UV Parallel Controller Core",
                55,
                new String[] { StatCollector.translateToLocal("Tooltip_UVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_UVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_UVParallelControllerCore_02") }));

        GTNLItemList.UHVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "UHV Parallel Controller Core",
                56,
                new String[] { StatCollector.translateToLocal("Tooltip_UHVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_UHVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_UHVParallelControllerCore_02") }));

        GTNLItemList.UEVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "UEV Parallel Controller Core",
                57,
                new String[] { StatCollector.translateToLocal("Tooltip_UEVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_UEVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_UEVParallelControllerCore_02") }));

        GTNLItemList.UIVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "UIV Parallel Controller Core",
                58,
                new String[] { StatCollector.translateToLocal("Tooltip_UIVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_UIVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_UIVParallelControllerCore_02") }));

        GTNLItemList.UMVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "UMV Parallel Controller Core",
                59,
                new String[] { StatCollector.translateToLocal("Tooltip_UMVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_UMVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_UMVParallelControllerCore_02") }));

        GTNLItemList.UXVParallelControllerCore.set(
            MetaItemAdder.initItem(
                "UXV Parallel Controller Core",
                60,
                new String[] { StatCollector.translateToLocal("Tooltip_UXVParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_UXVParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_UXVParallelControllerCore_02") }));

        GTNLItemList.MAXParallelControllerCore.set(
            MetaItemAdder.initItem(
                "MAX Parallel Controller Core",
                61,
                new String[] { StatCollector.translateToLocal("Tooltip_MAXParallelControllerCore_00"),
                    StatCollector.translateToLocal("Tooltip_MAXParallelControllerCore_01"),
                    StatCollector.translateToLocal("Tooltip_MAXParallelControllerCore_02") }));

        GTNLItemList.NagaBook.set(MetaItemAdder.initItem(62));
        GTNLItemList.TwilightForestBook.set(MetaItemAdder.initItem(63));
        GTNLItemList.LichBook.set(MetaItemAdder.initItem(64));
        GTNLItemList.MinotaurBook.set(MetaItemAdder.initItem(65));
        GTNLItemList.HydraBook.set(MetaItemAdder.initItem(66));
        GTNLItemList.KnightPhantomBook.set(MetaItemAdder.initItem(67));
        GTNLItemList.UrGhastBook.set(MetaItemAdder.initItem(68));
        GTNLItemList.AlphaYetiBook.set(MetaItemAdder.initItem(69));
        GTNLItemList.SnowQueenBook.set(MetaItemAdder.initItem(70));
        GTNLItemList.FinalBook.set(MetaItemAdder.initItem(71));
        GTNLItemList.GiantBook.set(MetaItemAdder.initItem(72));
        GTNLItemList.ClayedGlowstone.set(MetaItemAdder.initItem(73));
        GTNLItemList.QuantumDisk.set(MetaItemAdder.initItem(74));
        GTNLItemList.NeutroniumBoule.set(
            MetaItemAdder.initItem(
                "Neutronium Boule",
                75,
                new String[] { StatCollector.translateToLocal("Tooltip_NeutroniumBoule_00") }));
        GTNLItemList.NeutroniumWafer.set(
            MetaItemAdder.initItem(
                "Neutronium Wafer",
                76,
                new String[] { StatCollector.translateToLocal("Tooltip_NeutroniumWafer_00") }));
        GTNLItemList.HighlyAdvancedSocWafer.set(
            MetaItemAdder.initItem(
                "Highly Advanced Soc Wafer",
                77,
                new String[] { StatCollector.translateToLocal("Tooltip_HighlyAdvancedSocWafer_00") }));
        GTNLItemList.HighlyAdvancedSoc.set(
            MetaItemAdder.initItem(
                "Highly Advanced Soc",
                78,
                new String[] { StatCollector.translateToLocal("Tooltip_HighlyAdvancedSoc_00") }));
        GTNLItemList.ZnFeAlClCatalyst.set(MetaItemAdder.initItem(79));
        GTNLItemList.BlackLight.set(
            MetaItemAdder
                .initItem("Black Light", 80, new String[] { StatCollector.translateToLocal("Tooltip_BlackLight_00") }));
        GTNLItemList.SteamgateDialingDevice.set(
            MetaItemAdder.initItem(
                "Steamgate Dialing Device",
                81,
                new String[] { StatCollector.translateToLocal("Tooltip_SteamgateDialingDevice_00") }));
        GTNLItemList.SteamgateChevron.set(MetaItemAdder.initItem(82));
        GTNLItemList.SteamgateChevronUpgrade.set(MetaItemAdder.initItem(83));
        GTNLItemList.SteamgateIrisBlade.set(MetaItemAdder.initItem(84));
        GTNLItemList.SteamgateIrisUpgrade.set(MetaItemAdder.initItem(85));
        GTNLItemList.SteamgateHeatContainmentPlate.set(
            MetaItemAdder.initItem(
                "Steamgate Heat Containment Plate",
                86,
                new String[] { StatCollector.translateToLocal("Tooltip_SteamgateHeatContainmentPlate_00") }));
        GTNLItemList.SteamgateFrame.set(
            MetaItemAdder.initItem(
                "Steamgate Frame",
                87,
                new String[] { StatCollector.translateToLocal("Tooltip_SteamgateFrame_00") }));
        GTNLItemList.SteamgateCoreCrystal.set(
            MetaItemAdder.initItem(
                "Steamgate Core Crystal",
                88,
                new String[] { StatCollector.translateToLocal("Tooltip_SteamgateCoreCrystal_00") }));
        GTNLItemList.HydraulicMotor.set(MetaItemAdder.initItem(89));
        GTNLItemList.HydraulicPiston.set(MetaItemAdder.initItem(90));
        GTNLItemList.HydraulicPump.set(
            MetaItemAdder.initItem(
                "Hydraulic Pump",
                91,
                new String[] { StatCollector.translateToLocal("Tooltip_HydraulicPump_00") }));
        GTNLItemList.HydraulicArm.set(MetaItemAdder.initItem(92));
        GTNLItemList.HydraulicConveyor.set(
            MetaItemAdder.initItem(
                "Hydraulic Conveyor",
                93,
                new String[] { StatCollector.translateToLocal("Tooltip_HydraulicConveyor_00") }));
        GTNLItemList.HydraulicRegulator.set(
            MetaItemAdder.initItem(
                "Hydraulic Regulator",
                94,
                new String[] { StatCollector.translateToLocal("Tooltip_HydraulicRegulator_00"),
                    StatCollector.translateToLocal("Tooltip_HydraulicRegulator_01"),
                    StatCollector.translateToLocal("Tooltip_HydraulicRegulator_02") }));
        GTNLItemList.HydraulicVaporGenerator.set(MetaItemAdder.initItem(95));
        GTNLItemList.HydraulicSteamJetSpewer.set(MetaItemAdder.initItem(96));
        GTNLItemList.HydraulicSteamReceiver.set(MetaItemAdder.initItem(97));
        GTNLItemList.HydraulicSteamValve.set(
            MetaItemAdder.initItem(
                "Hydraulic Steam Valve",
                98,
                new String[] { StatCollector.translateToLocal("Tooltip_HydraulicSteamValve_00") }));
        addItemTooltip(
            GTNLItemList.HydraulicSteamValve.get(1),
            AnimatedTooltipHandler.buildTextWithAnimatedEnd(text("Tips: 瑶光Alkaid要的")));
        GTNLItemList.HydraulicSteamRegulator.set(
            MetaItemAdder.initItem(
                "Hydraulic Steam Regulator",
                99,
                new String[] { StatCollector.translateToLocal("Tooltip_HydraulicSteamRegulator_00") }));
        addItemTooltip(
            GTNLItemList.HydraulicSteamRegulator.get(1),
            AnimatedTooltipHandler.buildTextWithAnimatedEnd(text("Tips: 瑶光Alkaid要的")));
        GTNLItemList.SadBapyCatToken.set(
            MetaItemAdder.initItem(
                "SadBapyCat Token",
                100,
                new String[] { StatCollector.translateToLocal("Tooltip_SadBapyCatToken_00") }));
        GTNLItemList.CompressedSteamTurbine.set(
            MetaItemAdder.initItem(
                "Compressed Steam Turbine",
                101,
                new String[] { StatCollector.translateToLocal("Tooltip_CompressedSteamTurbine_00") }));
        GTNLItemList.SteelTurbine.set(
            MetaItemAdder.initItem(
                "Steel Turbine",
                102,
                new String[] { StatCollector.translateToLocal("Tooltip_SteelTurbine_00") }));
        GTNLItemList.PipelessSteamCover.set(
            MetaItemAdder.initItem(
                "Pipeless Steam Cover",
                103,
                new String[] { StatCollector.translateToLocal("Tooltip_WirelessSteamCover_00"),
                    StatCollector.translateToLocal("Tooltip_WirelessSteamCover_01"),
                    StatCollector.translateToLocal("Tooltip_WirelessSteamCover_02"),
                    StatCollector.translateToLocal("Tooltip_WirelessSteamCover_03"),
                    StatCollector.translateToLocal("Tooltip_WirelessSteamCover_04") }));
        GTNLItemList.IronTurbine.set(
            MetaItemAdder.initItem(
                "Iron Turbine",
                104,
                new String[] { StatCollector.translateToLocal("Tooltip_IronTurbine_00") }));
        GTNLItemList.BronzeTurbine.set(
            MetaItemAdder.initItem(
                "Bronze Turbine",
                105,
                new String[] { StatCollector.translateToLocal("Tooltip_BronzeTurbine_00") }));
        GTNLItemList.VoidCover.set(
            MetaItemAdder.initItem(
                "Void Cover",
                106,
                new String[] { StatCollector.translateToLocal("Tooltip_VoidCover_00"),
                    StatCollector.translateToLocal("Tooltip_VoidCover_01"),
                    StatCollector.translateToLocal("Tooltip_VoidCover_02"),
                    StatCollector.translateToLocal("Tooltip_VoidCover_03") }));

        GTNLItemList.ManaElectricProspectorTool.set(ElectricProspectorTool.initItem(0, 25, 9999));

        for (int i = 0; i < 14; i++) {
            GTNLItemList.WIRELESS_ENERGY_COVER[i].set(ItemList.WIRELESS_ENERGY_COVERS[i].get(1));

            GTNLItemList.WIRELESS_ENERGY_COVER_4A[i].set(
                MetaItemAdder.initItem(
                    "4A " + GTValues.VN[i + 1] + " Wireless Energy Cover",
                    107 + i,
                    new String[] { StatCollector.translateToLocal("Tooltip_WirelessEnergyCover4A_00"),
                        StatCollector.translateToLocal("Tooltip_WirelessEnergyCover4A_01"),
                        StatCollector.translateToLocal("Tooltip_WirelessEnergyCover4A_02"),
                        StatCollector.translateToLocal("Tooltip_WirelessEnergyCover4A_03"),
                        StatCollector.translateToLocalFormatted(
                            "Tooltip_WirelessEnergyCover4A_04",
                            GTUtility.formatNumbers(GTValues.V[i + 1]),
                            GTValues.VN[i + 1]) }));
        }
    }

    public static void registry() {
        registryItems();
        registryMetaItems();
        registryOreDictionary();
        registryOreBlackList();
        registrySuspiciousStewFlower();

        addItemTooltip(
            new ItemStack(ItemLoader.satietyRing, 1),
            AnimatedTooltipHandler.buildTextWithAnimatedEnd(text("Most machine recipe by zero_CM")));
    }

    public static void registrySuspiciousStewFlower() {
        // 蒲公英
        registerFlower(Blocks.yellow_flower, 0, new PotionEffect(Potion.field_76443_y.id, 7));
        // 罂粟
        registerFlower(Blocks.red_flower, 0, new PotionEffect(Potion.nightVision.id, 100));
        // 兰花
        registerFlower(Blocks.red_flower, 1, new PotionEffect(Potion.field_76443_y.id, 7));
        // 绒球葱
        registerFlower(Blocks.red_flower, 2, new PotionEffect(Potion.fireResistance.id, 60));
        // 蓝花美耳草
        registerFlower(Blocks.red_flower, 3, new PotionEffect(Potion.blindness.id, 220));
        // 郁金香
        registerFlower(Blocks.red_flower, 4, new PotionEffect(Potion.weakness.id, 140));
        registerFlower(Blocks.red_flower, 5, new PotionEffect(Potion.weakness.id, 140));
        registerFlower(Blocks.red_flower, 6, new PotionEffect(Potion.weakness.id, 140));
        registerFlower(Blocks.red_flower, 7, new PotionEffect(Potion.weakness.id, 140));
        // 滨菊
        registerFlower(Blocks.red_flower, 8, new PotionEffect(Potion.regeneration.id, 140));
    }

    public static void registryOreDictionary() {

        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.ULV), GTNLItemList.CircuitResonaticULV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.LV), GTNLItemList.CircuitResonaticLV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.MV), GTNLItemList.CircuitResonaticMV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.HV), GTNLItemList.CircuitResonaticHV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.EV), GTNLItemList.CircuitResonaticEV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.IV), GTNLItemList.CircuitResonaticIV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.LuV), GTNLItemList.CircuitResonaticLuV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.ZPM), GTNLItemList.CircuitResonaticZPM.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.UV), GTNLItemList.CircuitResonaticUV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UHV), GTNLItemList.CircuitResonaticUHV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UEV), GTNLItemList.CircuitResonaticUEV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UIV), GTNLItemList.CircuitResonaticUIV.get(1));

        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.ULV), GTNLItemList.VerySimpleCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.LV), GTNLItemList.SimpleCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.MV), GTNLItemList.BasicCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.HV), GTNLItemList.AdvancedCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.EV), GTNLItemList.EliteCircuit.get(1));

        GTOreDictUnificator.registerOre("record", GTNLItemList.RecordSus.get(1));
        GTOreDictUnificator.registerOre("record", GTNLItemList.RecordLavaChicken.get(1));
        GTOreDictUnificator.registerOre("record", GTNLItemList.RecordNewHorizons.get(1));
    }

    public static void registryOreBlackList() {
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticULV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticLV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticMV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticHV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticEV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticIV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticLuV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticZPM.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticUV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticUHV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticUEV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticUIV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.VerySimpleCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.SimpleCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.BasicCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.AdvancedCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.EliteCircuit.get(1));
    }
}
