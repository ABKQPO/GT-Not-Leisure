package com.science.gtnl.loader;

import static com.science.gtnl.common.item.items.SuspiciousStew.registerFlower;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.ItemInfinityCell;
import com.science.gtnl.common.item.ItemInfinityItem;
import com.science.gtnl.common.item.ItemRecord;
import com.science.gtnl.common.item.MetaItemAdder;
import com.science.gtnl.common.item.items.CircuitIntegratedPlus;
import com.science.gtnl.common.item.items.DebugItem;
import com.science.gtnl.common.item.items.DireCraftPattern;
import com.science.gtnl.common.item.items.ElectricProspectorTool;
import com.science.gtnl.common.item.items.FakeItemSiren;
import com.science.gtnl.common.item.items.GTNLItemBucket;
import com.science.gtnl.common.item.items.ItemPartActiveFormationPlane;
import com.science.gtnl.common.item.items.ItemPartBeamFormer;
import com.science.gtnl.common.item.items.ItemPartDenseEnergyCell;
import com.science.gtnl.common.item.items.ItemPartEnergyAcceptor;
import com.science.gtnl.common.item.items.ItemPartEnergyCell;
import com.science.gtnl.common.item.items.ItemPartMECellDock;
import com.science.gtnl.common.item.items.ItemPartSuperDenseEnergyCell;
import com.science.gtnl.common.item.items.ItemPartSuperDualInterface;
import com.science.gtnl.common.item.items.ItemPartSuperInterface;
import com.science.gtnl.common.item.items.KFCFamily;
import com.science.gtnl.common.item.items.NetherTeleporter;
import com.science.gtnl.common.item.items.NullPointerException;
import com.science.gtnl.common.item.items.PortableCellWorkbenchItem;
import com.science.gtnl.common.item.items.PortableItem;
import com.science.gtnl.common.item.items.SlimeSaddle;
import com.science.gtnl.common.item.items.SteamRocket;
import com.science.gtnl.common.item.items.Stick;
import com.science.gtnl.common.item.items.SuspiciousStew;
import com.science.gtnl.common.item.items.TestItem;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.item.items.TwilightSword;
import com.science.gtnl.common.item.items.VeinMiningPickaxe;
import com.science.gtnl.common.item.items.WirelessUpgradeChip;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionBauble;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionType;
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
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.common.render.items.InfinityMetaItemRenderer;

public class ItemLoader {

    public static SteamRocket steamRocket = new SteamRocket();
    public static NullPointerException nullPointerException = new NullPointerException();
    public static FakeItemSiren fakeItemSiren = new FakeItemSiren();
    public static NetherTeleporter netherTeleporter = new NetherTeleporter();
    public static TestItem testItem = new TestItem();
    public static DebugItem debugItem = new DebugItem();
    public static KFCFamily KFCFamily = new KFCFamily();
    public static TwilightSword twilightSword = new TwilightSword();
    public static CircuitIntegratedPlus circuitIntegratedPlus = new CircuitIntegratedPlus();
    public static TimeStopPocketWatch timeStopPocketWatch = new TimeStopPocketWatch();
    public static Item recordSus = new ItemRecord("sus");

    public static ItemInfinityItem infinityTorch = new ItemInfinityItem(
        "infinity_torch",
        "gtnl.infinity_torch",
        Blocks.torch,
        GTNLItemList.InfinityTorch);

    public static ItemInfinityItem infinityWaterBucket = new ItemInfinityItem(
        "infinity_water_bucket",
        "gtnl.infinity_water_bucket",
        Blocks.water,
        FluidRegistry.getFluid("water"),
        GTNLItemList.InfinityWaterBucket);

    public static ItemInfinityItem infinityLavaBucket = new ItemInfinityItem(
        "infinity_lava_bucket",
        "gtnl.infinity_lava_bucket",
        Blocks.lava,
        FluidRegistry.getFluid("lava"),
        GTNLItemList.InfinityLavaBucket);

    public static ItemInfinityItem infinityHoneyBucket = new ItemInfinityItem(
        "infinity_honey_bucket",
        "gtnl.infinity_honey_bucket",
        BlockLoader.honeyFluidBlock,
        BlockLoader.honeyFluid,
        GTNLItemList.InfinityHoneyBucket);

    public static ItemInfinityItem infinityShimmerBucket = new ItemInfinityItem(
        "infinity_shimmer_bucket",
        "gtnl.infinity_shimmer_bucket",
        BlockLoader.shimmerFluidBlock,
        BlockLoader.shimmerFluid,
        GTNLItemList.InfinityShimmerBucket);

    public static ItemInfinityItem superstrongSponge = new ItemInfinityItem(
        "superstrong_sponge",
        "gtnl.superstrong_sponge",
        (Block) null,
        null,
        false,
        GTNLItemList.SuperstrongSponge);

    public static FuelRodDepleted infinityFuelRodDepleted = new FuelRodDepleted("infinity_fuel_rod_depleted", 2000);

    public static FuelRod infinityFuelRod = new FuelRod(
        "infinity_fuel_rod",
        1,
        491520,
        500,
        15000,
        160000,
        70F,
        new ItemStack(infinityFuelRodDepleted, 1));

    public static MetaItemAdder metaItem = new MetaItemAdder("meta_item", GTNLCreativeTabs.GTNotLeisureItem);

    public static GTNLItemBucket honeyBucket;
    public static GTNLItemBucket shimmerBucket;

    public static ItemInfinityCell infinityCell = new ItemInfinityCell();
    public static DireCraftPattern direCraftPattern = new DireCraftPattern();

    public static WirelessUpgradeChip wirelessUpgradeChip = new WirelessUpgradeChip();
    public static SuspiciousStew suspiciousStew = new SuspiciousStew();
    public static PortableItem portableItem = new PortableItem();
    public static PortableCellWorkbenchItem portableCellWorkbenchItem = new PortableCellWorkbenchItem();
    public static ElectricProspectorTool electricProspectorTool = new ElectricProspectorTool();

    public static SlimeSaddle slimeSaddle = new SlimeSaddle();

    public static SuperReachRing superReachRing = new SuperReachRing();
    public static SatietyRing satietyRing = new SatietyRing();
    public static RejectionRing rejectionRing = new RejectionRing();

    public static PhysicsCape physicsCape = new PhysicsCape();
    public static RoyalGel royalGel = new RoyalGel();
    public static LuckyHorseshoe luckyHorseshoe = new LuckyHorseshoe();
    public static DraconicArmorProjectionBauble wyvernProjectionNecklace = new DraconicArmorProjectionBauble(
        "wyvern_projection_necklace",
        "gtnl.wyvern_projection_necklace",
        DraconicArmorProjectionType.WYVERN,
        GTNLItemList.WyvernProjectionNecklace);
    public static DraconicArmorProjectionBauble draconicProjectionNecklace = new DraconicArmorProjectionBauble(
        "draconic_projection_necklace",
        "gtnl.draconic_projection_necklace",
        DraconicArmorProjectionType.DRACONIC,
        GTNLItemList.DraconicProjectionNecklace);

    public static Stick stick = new Stick();

    public static VeinMiningPickaxe veinMiningPickaxe = new VeinMiningPickaxe();

    public static ItemPartSuperInterface superInterface = new ItemPartSuperInterface();
    public static ItemPartSuperDualInterface superDualInterface = new ItemPartSuperDualInterface();
    public static ItemPartActiveFormationPlane activeFormationPlane = new ItemPartActiveFormationPlane();
    public static ItemPartBeamFormer beamFormer = new ItemPartBeamFormer();
    public static ItemPartMECellDock meCellDock = new ItemPartMECellDock();
    public static ItemPartEnergyAcceptor energyAcceptor = new ItemPartEnergyAcceptor();
    public static ItemPartEnergyCell energyCell = new ItemPartEnergyCell();
    public static ItemPartDenseEnergyCell denseEnergyCell = new ItemPartDenseEnergyCell();
    public static ItemPartSuperDenseEnergyCell superDenseEnergyCell = new ItemPartSuperDenseEnergyCell();

    public static ItemStack infinityDyeCell;
    public static ItemStack infinityDyeFluidCell;
    public static ItemStack infinityStoneCell;

    public static void registryItems() {
        honeyBucket = GTNLItemBucket.create(BlockLoader.honeyFluid);
        shimmerBucket = GTNLItemBucket.create(BlockLoader.shimmerFluid);

        var subDyeItems = new ItemInfinityCell.SubItem[16];
        for (short i = 0; i < 16; i++) {
            subDyeItems[i] = ItemInfinityCell.SubItem.getInstance(ItemList.DYE_ONLY_ITEMS[i].get(1));
        }
        infinityDyeCell = ItemInfinityCell
            .getSubItem(StorageChannel.ITEMS, "item.gtnl.infinity_cell.dye", "infinity_dye_cell", subDyeItems);

        String[] colors = { "Black", "Pink", "Red", "Orange", "Yellow", "Green", "Lime", "Blue", "LightBlue", "Cyan",
            "Brown", "Magenta", "Purple", "Gray", "LightGray", "White" };
        var subDyeFluid = new ItemInfinityCell.SubItem[colors.length];
        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            String fluidName = "dye.chemical.dye" + color.toLowerCase();
            subDyeFluid[i] = ItemInfinityCell.SubItem.getInstance(FluidRegistry.getFluid(fluidName));
        }
        infinityDyeFluidCell = ItemInfinityCell.getSubItem(
            StorageChannel.FLUIDS,
            "item.gtnl.infinity_cell.dye_fluid",
            "infinity_dye_fluid_cell",
            subDyeFluid);

        List<ItemInfinityCell.SubItem> infinityStoneCell = new ArrayList<>();
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(Blocks.stone));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(Blocks.cobblestone));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(Blocks.netherrack));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(Blocks.end_stone));

        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(new ItemStack(GregTechAPI.sBlockGranites, 1, 0)));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(new ItemStack(GregTechAPI.sBlockGranites, 1, 1)));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(new ItemStack(GregTechAPI.sBlockGranites, 1, 8)));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(new ItemStack(GregTechAPI.sBlockGranites, 1, 9)));

        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(new ItemStack(GregTechAPI.sBlockStones, 1, 0)));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(new ItemStack(GregTechAPI.sBlockStones, 1, 1)));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(new ItemStack(GregTechAPI.sBlockStones, 1, 8)));
        infinityStoneCell.add(ItemInfinityCell.SubItem.getInstance(new ItemStack(GregTechAPI.sBlockStones, 1, 9)));

        if (Mods.EtFuturumRequiem.isModLoaded()) {
            infinityStoneCell.add(
                ItemInfinityCell.SubItem
                    .getInstance(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "deepslate", 1)));
            infinityStoneCell.add(
                ItemInfinityCell.SubItem
                    .getInstance(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "cobbled_deepslate", 1)));
            infinityStoneCell.add(
                ItemInfinityCell.SubItem
                    .getInstance(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "blackstone", 1)));
        }

        if (Mods.Botania.isModLoaded()) {
            infinityStoneCell
                .add(ItemInfinityCell.SubItem.getInstance(GTModHandler.getModItem(Mods.Botania.ID, "stone", 1, 0)));
            infinityStoneCell
                .add(ItemInfinityCell.SubItem.getInstance(GTModHandler.getModItem(Mods.Botania.ID, "stone", 1, 1)));
            infinityStoneCell
                .add(ItemInfinityCell.SubItem.getInstance(GTModHandler.getModItem(Mods.Botania.ID, "stone", 1, 2)));
            infinityStoneCell
                .add(ItemInfinityCell.SubItem.getInstance(GTModHandler.getModItem(Mods.Botania.ID, "stone", 1, 3)));
        }

        ItemLoader.infinityStoneCell = ItemInfinityCell.getSubItem(
            StorageChannel.ITEMS,
            "item.gtnl.infinity_cell.stone",
            "infinity_stone_cell",
            infinityStoneCell);

        GameRegistry.registerItem(recordSus, "record_sus");

        GTNLItemList.RecordSus.set(recordSus);
        GTNLItemList.InfinityFuelRodDepleted.set(infinityFuelRodDepleted);
        GTNLItemList.InfinityFuelRod.set(infinityFuelRod);
        GTNLItemList.HoneyBucket.set(honeyBucket);
        GTNLItemList.ShimmerBucket.set(shimmerBucket);
        GTNLItemList.InfinityCell.set(infinityCell);
        GTNLItemList.InfinityDyeCell.set(infinityDyeCell);
        GTNLItemList.InfinityDyeFluidCell.set(infinityDyeFluidCell);
        GTNLItemList.InfinityStoneCell.set(ItemLoader.infinityStoneCell);
    }

    public static void registryMetaItems() {
        GTNLItemList.TrollFace.set(
            MetaItemAdder
                .initItem(0, new String[] { StatCollector.translateToLocal("item.gtnl.troll_face.tooltip.0") }));
        GTNLItemList.DepletedExcitedNaquadahFuelRod.set(
            MetaItemAdder.initItem(
                1,
                new String[] {
                    StatCollector.translateToLocal("item.gtnl.depleted_excited_naquadah_fuel_rod.tooltip.0") }));
        GTNLItemList.BlazeCube.set(
            MetaItemAdder
                .initItem(2, new String[] { StatCollector.translateToLocal("item.gtnl.blaze_cube.tooltip.0") }));
        GTNLItemList.EnhancementCore.set(
            MetaItemAdder
                .initItem(3, new String[] { StatCollector.translateToLocal("item.gtnl.enhancement_core.tooltip.0") }));
        GTNLItemList.WaterCover.set(MetaItemAdder.initItem(4));
        GTNLItemList.ActivatedGaiaPylon.set(MetaItemAdder.initItem(5));
        GTNLItemList.PrecisionSteamMechanism.set(MetaItemAdder.initItem(6));
        GTNLItemList.MeteorMinerSchematic1.set(
            MetaItemAdder.initItem(
                7,
                new String[] { StatCollector.translateToLocal("item.gtnl.meteor_miner_schematic_1.tooltip.0") }));
        GTNLItemList.MeteorMinerSchematic2.set(
            MetaItemAdder.initItem(
                8,
                new String[] { StatCollector.translateToLocal("item.gtnl.meteor_miner_schematic_2.tooltip.0") }));
        GTNLItemList.CircuitResonaticULV.set(
            MetaItemAdder.initItem(
                9,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_ulv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_ulv.tooltip.1") }));
        GTNLItemList.CircuitResonaticLV.set(
            MetaItemAdder.initItem(
                10,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_lv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_lv.tooltip.1") }));
        GTNLItemList.CircuitResonaticMV.set(
            MetaItemAdder.initItem(
                11,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_mv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_mv.tooltip.1") }));
        GTNLItemList.CircuitResonaticHV.set(
            MetaItemAdder.initItem(
                12,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_hv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_hv.tooltip.1") }));
        GTNLItemList.CircuitResonaticEV.set(
            MetaItemAdder.initItem(
                13,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_ev.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_ev.tooltip.1") }));
        GTNLItemList.CircuitResonaticIV.set(
            MetaItemAdder.initItem(
                14,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_iv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_iv.tooltip.1") }));
        GTNLItemList.CircuitResonaticLuV.set(
            MetaItemAdder.initItem(
                15,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_luv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_luv.tooltip.1") }));
        GTNLItemList.CircuitResonaticZPM.set(
            MetaItemAdder.initItem(
                16,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_zpm.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_zpm.tooltip.1") }));
        GTNLItemList.CircuitResonaticUV.set(
            MetaItemAdder.initItem(
                17,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_uv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_uv.tooltip.1") }));
        GTNLItemList.CircuitResonaticUHV.set(
            MetaItemAdder.initItem(
                18,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_uhv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_uhv.tooltip.1") }));
        GTNLItemList.CircuitResonaticUEV.set(
            MetaItemAdder.initItem(
                19,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_uev.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_uev.tooltip.1") }));
        GTNLItemList.CircuitResonaticUIV.set(
            MetaItemAdder.initItem(
                20,
                new String[] { StatCollector.translateToLocal("item.gtnl.circuit_resonatic_uiv.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.circuit_resonatic_uiv.tooltip.1") }));
        GTNLItemList.VerySimpleCircuit.set(
            MetaItemAdder.initItem(
                21,
                new String[] { StatCollector.translateToLocal("item.gtnl.very_simple_circuit.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.very_simple_circuit.tooltip.1") }));
        GTNLItemList.SimpleCircuit.set(
            MetaItemAdder.initItem(
                22,
                new String[] { StatCollector.translateToLocal("item.gtnl.simple_circuit.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.simple_circuit.tooltip.1") }));
        GTNLItemList.BasicCircuit.set(
            MetaItemAdder.initItem(
                23,
                new String[] { StatCollector.translateToLocal("item.gtnl.basic_circuit.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.basic_circuit.tooltip.1") }));
        GTNLItemList.AdvancedCircuit.set(
            MetaItemAdder.initItem(
                24,
                new String[] { StatCollector.translateToLocal("item.gtnl.advanced_circuit.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.advanced_circuit.tooltip.1") }));
        GTNLItemList.EliteCircuit.set(
            MetaItemAdder.initItem(
                25,
                new String[] { StatCollector.translateToLocal("item.gtnl.elite_circuit.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.elite_circuit.tooltip.1") }));
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
            MetaItemAdder
                .initItem(75, new String[] { StatCollector.translateToLocal("item.gtnl.neutronium_boule.tooltip.0") }));
        GTNLItemList.NeutroniumWafer.set(
            MetaItemAdder
                .initItem(76, new String[] { StatCollector.translateToLocal("item.gtnl.neutronium_wafer.tooltip.0") }));
        GTNLItemList.HighlyAdvancedSocWafer.set(
            MetaItemAdder.initItem(
                77,
                new String[] { StatCollector.translateToLocal("item.gtnl.highly_advanced_soc_wafer.tooltip.0") }));
        GTNLItemList.HighlyAdvancedSoc.set(
            MetaItemAdder.initItem(
                78,
                new String[] { StatCollector.translateToLocal("item.gtnl.highly_advanced_soc.tooltip.0") }));
        GTNLItemList.ZnFeAlClCatalyst.set(MetaItemAdder.initItem(79));
        GTNLItemList.BlackLight.set(
            MetaItemAdder
                .initItem(80, new String[] { StatCollector.translateToLocal("item.gtnl.black_light.tooltip.0") }));
        GTNLItemList.SteamgateDialingDevice.set(
            MetaItemAdder.initItem(
                81,
                new String[] { StatCollector.translateToLocal("item.gtnl.steamgate_dialing_device.tooltip.0") }));
        GTNLItemList.SteamgateChevron.set(MetaItemAdder.initItem(82));
        GTNLItemList.SteamgateChevronUpgrade.set(MetaItemAdder.initItem(83));
        GTNLItemList.SteamgateIrisBlade.set(MetaItemAdder.initItem(84));
        GTNLItemList.SteamgateIrisUpgrade.set(MetaItemAdder.initItem(85));
        GTNLItemList.SteamgateHeatContainmentPlate.set(
            MetaItemAdder.initItem(
                86,
                new String[] {
                    StatCollector.translateToLocal("item.gtnl.steamgate_heat_containment_plate.tooltip.0") }));
        GTNLItemList.SteamgateFrame.set(
            MetaItemAdder
                .initItem(87, new String[] { StatCollector.translateToLocal("item.gtnl.steamgate_frame.tooltip.0") }));
        GTNLItemList.SteamgateCoreCrystal.set(
            MetaItemAdder.initItem(
                88,
                new String[] { StatCollector.translateToLocal("item.gtnl.steamgate_core_crystal.tooltip.0") }));
        GTNLItemList.HydraulicMotor.set(MetaItemAdder.initItem(89));
        GTNLItemList.HydraulicPiston.set(MetaItemAdder.initItem(90));
        GTNLItemList.HydraulicPump.set(
            MetaItemAdder
                .initItem(91, new String[] { StatCollector.translateToLocal("item.gtnl.hydraulic_pump.tooltip.0") }));
        GTNLItemList.HydraulicArm.set(MetaItemAdder.initItem(92));
        GTNLItemList.HydraulicConveyor.set(
            MetaItemAdder.initItem(
                93,
                new String[] { StatCollector.translateToLocal("item.gtnl.hydraulic_conveyor.tooltip.0") }));
        GTNLItemList.HydraulicRegulator.set(
            MetaItemAdder.initItem(
                94,
                new String[] { StatCollector.translateToLocal("item.gtnl.hydraulic_regulator.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.hydraulic_regulator.tooltip.1"),
                    StatCollector.translateToLocal("item.gtnl.hydraulic_regulator.tooltip.2") }));
        GTNLItemList.HydraulicVaporGenerator.set(MetaItemAdder.initItem(95));
        GTNLItemList.HydraulicSteamJetSpewer.set(MetaItemAdder.initItem(96));
        GTNLItemList.HydraulicSteamReceiver.set(MetaItemAdder.initItem(97));
        GTNLItemList.HydraulicSteamValve.set(
            MetaItemAdder.initItem(
                98,
                new String[] { StatCollector.translateToLocal("item.gtnl.hydraulic_steam_valve.tooltip.0") }));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.HydraulicSteamValve.get(1),
            AnimatedTooltipHandler.buildTextWithAnimatedEnd(AnimatedTooltipHandler.text("Tips: 瑶光Alkaid要的")));
        GTNLItemList.HydraulicSteamRegulator.set(
            MetaItemAdder.initItem(
                99,
                new String[] { StatCollector.translateToLocal("item.gtnl.hydraulic_steam_regulator.tooltip.0") }));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.HydraulicSteamRegulator.get(1),
            AnimatedTooltipHandler.buildTextWithAnimatedEnd(AnimatedTooltipHandler.text("Tips: 瑶光Alkaid要的")));
        GTNLItemList.SadBapyCatToken.set(
            MetaItemAdder.initItem(
                100,
                new String[] { StatCollector.translateToLocal("item.gtnl.sad_bapy_cat_token.tooltip.0") }));
        GTNLItemList.CompressedSteamTurbine.set(
            MetaItemAdder.initItem(
                101,
                new String[] { StatCollector.translateToLocal("item.gtnl.compressed_steam_turbine.tooltip.0") }));
        GTNLItemList.SteelTurbine.set(
            MetaItemAdder
                .initItem(102, new String[] { StatCollector.translateToLocal("item.gtnl.steel_turbine.tooltip.0") }));
        GTNLItemList.PipelessSteamCover.set(
            MetaItemAdder.initItem(
                103,
                new String[] { StatCollector.translateToLocal("gtnl.cover.pipeless_steam.tooltip.0"),
                    StatCollector.translateToLocal("gtnl.cover.pipeless_steam.tooltip.1"),
                    StatCollector.translateToLocal("gtnl.cover.pipeless_steam.tooltip.2"),
                    StatCollector.translateToLocal("gtnl.cover.pipeless_steam.tooltip.3"),
                    StatCollector.translateToLocal("gtnl.cover.pipeless_steam.tooltip.4") }));
        GTNLItemList.IronTurbine.set(
            MetaItemAdder
                .initItem(104, new String[] { StatCollector.translateToLocal("item.gtnl.iron_turbine.tooltip.0") }));
        GTNLItemList.BronzeTurbine.set(
            MetaItemAdder
                .initItem(105, new String[] { StatCollector.translateToLocal("item.gtnl.bronze_turbine.tooltip.0") }));
        GTNLItemList.VoidCover.set(
            MetaItemAdder.initItem(
                106,
                new String[] { StatCollector.translateToLocal("gtnl.cover.void.tooltip.0"),
                    StatCollector.translateToLocal("gtnl.cover.void.tooltip.1"),
                    StatCollector.translateToLocal("gtnl.cover.void.tooltip.2"),
                    StatCollector.translateToLocal("gtnl.cover.void.tooltip.3") }));

        for (int i = 0; i < 14; i++) {
            GTNLItemList.WIRELESS_ENERGY_COVER[i].set(ItemList.WIRELESS_ENERGY_COVERS[i].get(1));

            GTNLItemList.WIRELESS_ENERGY_COVER_4A[i].set(
                MetaItemAdder.initItem(
                    107 + i,
                    new String[] { StatCollector.translateToLocal("gtnl.cover.wireless_energy_4a.tooltip.0"),
                        StatCollector.translateToLocal("gtnl.cover.wireless_energy_4a.tooltip.1"),
                        StatCollector.translateToLocal("gtnl.cover.wireless_energy_4a.tooltip.2"),
                        StatCollector.translateToLocal("gtnl.cover.wireless_energy_4a.tooltip.3"),
                        StatCollector.translateToLocalFormatted(
                            "gtnl.cover.wireless_energy_4a.tooltip.4",
                            NumberFormatUtil.formatNumber(GTValues.V[i + 1]),
                            GTValues.VN[i + 1]) }));
        }

        GTNLItemList.ExoticCircuitBoards.set(MetaItemAdder.initItem(121));
        GTNLItemList.ExoticSurroundingCPU.set(MetaItemAdder.initItem(122));
        GTNLItemList.ExoticWafer.set(MetaItemAdder.initItem(123));
        GTNLItemList.ExoticChip.set(MetaItemAdder.initItem(124));
        GTNLItemList.ExoticRAMWafer.set(MetaItemAdder.initItem(125));
        GTNLItemList.ShatteredSingularity.set(MetaItemAdder.initItem(126));
        GTNLItemList.TransdimensionalMnemonicMatrix.set(
            MetaItemAdder.initItem(
                127,
                new String[] { StatCollector.translateToLocal("item.gtnl.transdimensional_mnemonic_matrix.tooltip.0"),
                    StatCollector.translateToLocal("item.gtnl.transdimensional_mnemonic_matrix.tooltip.1") }));
        GTNLItemList.EssentiaUpgradeEmpty.set(MetaItemAdder.initItem(128));
        GTNLItemList.EssentiaUpgradeAir.set(MetaItemAdder.initItem(129));
        GTNLItemList.EssentiaUpgradeThermal.set(MetaItemAdder.initItem(130));
        GTNLItemList.EssentiaUpgradeUnstable.set(MetaItemAdder.initItem(131));
        GTNLItemList.EssentiaUpgradeVictus.set(MetaItemAdder.initItem(132));
        GTNLItemList.EssentiaUpgradeTainted.set(MetaItemAdder.initItem(133));
        GTNLItemList.EssentiaUpgradeMechanics.set(MetaItemAdder.initItem(134));
        GTNLItemList.EssentiaUpgradeSpirit.set(MetaItemAdder.initItem(135));
        GTNLItemList.EssentiaUpgradeRadiation.set(MetaItemAdder.initItem(136));
        GTNLItemList.EssentiaUpgradeElectric.set(MetaItemAdder.initItem(137));

        GTNLItemList.ManaElectricProspectorTool.set(ElectricProspectorTool.initItem(0, 10, 9999));
        GTNLItemList.DebugElectricProspectorTool.set(ElectricProspectorTool.initItem(1, 50, Integer.MAX_VALUE));
    }

    public static void registry() {
        registryItems();
        registryMetaItems();
        registryOreBlackList();
        registrySuspiciousStewFlower();

        AnimatedTooltipHandler.addItemTooltip(
            new ItemStack(ItemLoader.satietyRing, 1),
            AnimatedTooltipHandler
                .buildTextWithAnimatedEnd(AnimatedTooltipHandler.text("Most machine recipe by zero_CM")));
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
