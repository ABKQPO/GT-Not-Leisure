package com.science.gtnl.utils.enums;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;

/**
 * Centralizes fixed external item and block registry references used by GT: Not Leisure.
 * <p>
 * Lookups happen when {@link #get(long)} is called, after the owning mod has registered its items. A fresh stack is
 * returned for each call so callers may safely change its size or NBT. Missing optional items return {@code null},
 * matching the previous direct registry lookups.
 */
public enum Itemlist {

    // 强化玻璃透镜
    ReinforcedGlassLens(ModList.NewHorizonsCoreMod.ID, "ReinforcedGlassLense"),
    // 神秘水晶透镜
    MysteriousCrystalLens(ModList.NewHorizonsCoreMod.ID, "MysteriousCrystalLens"),
    // 拉多克斯聚合物透镜
    RadoxPolymerLens(ModList.NewHorizonsCoreMod.ID, "RadoxPolymerLens"),
    // 彩色透镜
    ChromaticLens(ModList.NewHorizonsCoreMod.ID, "ChromaticLens"),

    // 运算压印模板
    CalculationPress(ModList.AppliedEnergistics.ID, "item.ItemMultiMaterial", 13),
    // 工程压印模板
    EngineeringPress(ModList.AppliedEnergistics.ID, "item.ItemMultiMaterial", 14),
    // 逻辑压印模板
    LogicPress(ModList.AppliedEnergistics.ID, "item.ItemMultiMaterial", 15),
    // 硅压印模板
    SiliconPress(ModList.AppliedEnergistics.ID, "item.ItemMultiMaterial", 19),
    // ME 接口
    MEInterface(ModList.AppliedEnergistics.ID, "tile.BlockInterface"),

    // 无限增幅卡
    InfinityBoosterCard(Mods.AE2WCT.ID, "infinityBoosterCard", true),
    // 量子桥接卡
    QuantumBridgeCard(Mods.AE2FluidCraft.ID, "quantum_bridge_card", true),
    // 能量卡
    EnergyCard(Mods.AE2FluidCraft.ID, "energy_card", true),

    // Railcraft 普通轨道（旧元数据 0）
    RailcraftTrack(Mods.Railcraft.ID, "track", 0),
    // Railcraft 普通轨道（旧元数据 736）
    RailcraftTrack736(Mods.Railcraft.ID, "track", 736),
    // Railcraft 普通轨道（旧元数据 816）
    RailcraftTrack816(Mods.Railcraft.ID, "track", 816),
    // 集水器壁板
    WaterTankWall(Mods.Railcraft.ID, "machine.alpha", 14),

    // 低压变压器
    IC2LVTransformer(Mods.IndustrialCraft2.ID, "blockElectric", 3),
    // 中压变压器
    IC2MVTransformer(Mods.IndustrialCraft2.ID, "blockElectric", 4),
    // 高压变压器
    IC2HVTransformer(Mods.IndustrialCraft2.ID, "blockElectric", 5),
    // 超高压变压器
    IC2EVTransformer(Mods.IndustrialCraft2.ID, "blockElectric", 6),
    // 防辐射板
    RadiationProtectionPlate(Mods.GoodGenerator.ID, "radiationProtectionPlate"),

    // 合金锭
    MixedMetalIngot(Mods.IndustrialCraft2.ID, "mixedMetalIngot", false, true),

    // AE2FluidCraft：GT-Not-Leisure 固定外部物品引用
    // ME二合一接口
    UP_AE2FLUIDCRAFT_FLUID_INTERFACE(Mods.AE2FluidCraft.ID, "fluid_interface", 0),
    // 256k-ME流体存储组件（元数据 4）
    UP_AE2FLUIDCRAFT_FLUID_PART_M4(Mods.AE2FluidCraft.ID, "fluid_part", 4),
    // 4096k-ME流体存储组件（元数据 6）
    UP_AE2FLUIDCRAFT_FLUID_PART_M6(Mods.AE2FluidCraft.ID, "fluid_part", 6),
    // 16384k-ME流体存储组件（元数据 7）
    UP_AE2FLUIDCRAFT_FLUID_PART_M7(Mods.AE2FluidCraft.ID, "fluid_part", 7),
    // ME人造宇宙流体存储元件
    UP_AE2FLUIDCRAFT_FLUID_STORAGE_UNIVERSE(Mods.AE2FluidCraft.ID, "fluid_storage.Universe", 0),
    // ME高级多流体存储外壳（元数据 3）
    UP_AE2FLUIDCRAFT_FLUID_STORAGE_HOUSING_M3(Mods.AE2FluidCraft.ID, "fluid_storage_housing", 3),
    // ME二合一接口
    UP_AE2FLUIDCRAFT_PART_FLUID_INTERFACE(Mods.AE2FluidCraft.ID, "part_fluid_interface", 0),
    // ME流体存储总线
    UP_AE2FLUIDCRAFT_PART_FLUID_STORAGE_BUS(Mods.AE2FluidCraft.ID, "part_fluid_storage_bus", 0),

    // Avaritia：GT-Not-Leisure 固定外部物品引用
    // 阿卡西记录
    UP_AVARITIA_AKASHIC_RECORD(Mods.Avaritia.ID, "Akashic_Record", 0),
    // 寰宇肉丸
    UP_AVARITIA_COSMIC_MEATBALLS(Mods.Avaritia.ID, "Cosmic_Meatballs", 0),
    // 水晶矩阵
    UP_AVARITIA_CRYSTAL_MATRIX(Mods.Avaritia.ID, "Crystal_Matrix", 0),
    // 终望珍珠
    UP_AVARITIA_ENDEST_PEARL(Mods.Avaritia.ID, "Endest_Pearl", 0),
    // 无尽胸甲
    UP_AVARITIA_INFINITY_CHEST(Mods.Avaritia.ID, "Infinity_Chest", 0),
    // 无尽头盔
    UP_AVARITIA_INFINITY_HELM(Mods.Avaritia.ID, "Infinity_Helm", 0),
    // 无尽护腿
    UP_AVARITIA_INFINITY_PANTS(Mods.Avaritia.ID, "Infinity_Pants", 0),
    // 世界崩解之镐
    UP_AVARITIA_INFINITY_PICKAXE(Mods.Avaritia.ID, "Infinity_Pickaxe", 0),
    // 无尽靴子
    UP_AVARITIA_INFINITY_SHOES(Mods.Avaritia.ID, "Infinity_Shoes", 0),
    // 寰宇支配之剑
    UP_AVARITIA_INFINITY_SWORD(Mods.Avaritia.ID, "Infinity_Sword", 0),
    // 阿蒙克气血宝珠
    UP_AVARITIA_ORB_ARMOK(Mods.Avaritia.ID, "Orb_Armok", 0),
    // 钻石晶格
    UP_AVARITIA_RESOURCE(Mods.Avaritia.ID, "Resource", 0),
    // 水晶矩阵锭（元数据 1）
    UP_AVARITIA_RESOURCE_M1(Mods.Avaritia.ID, "Resource", 1),
    // 无尽催化剂（元数据 5）
    UP_AVARITIA_RESOURCE_M5(Mods.Avaritia.ID, "Resource", 5),
    // 无尽之锭（元数据 6）
    UP_AVARITIA_RESOURCE_M6(Mods.Avaritia.ID, "Resource", 6),
    // 唱片碎片（元数据 7）
    UP_AVARITIA_RESOURCE_M7(Mods.Avaritia.ID, "Resource", 7),
    // 恒星燃料（元数据 8）
    UP_AVARITIA_RESOURCE_M8(Mods.Avaritia.ID, "Resource", 8),
    // 铁奇点
    UP_AVARITIA_SINGULARITY(Mods.Avaritia.ID, "Singularity", 0),
    // 金奇点（元数据 1）
    UP_AVARITIA_SINGULARITY_M1(Mods.Avaritia.ID, "Singularity", 1),
    // 末影锭奇点（元数据 10）
    UP_AVARITIA_SINGULARITY_M10(Mods.Avaritia.ID, "Singularity", 10),
    // 黏土奇点（元数据 11）
    UP_AVARITIA_SINGULARITY_M11(Mods.Avaritia.ID, "Singularity", 11),
    // 青金石奇点（元数据 2）
    UP_AVARITIA_SINGULARITY_M2(Mods.Avaritia.ID, "Singularity", 2),
    // 红石奇点（元数据 3）
    UP_AVARITIA_SINGULARITY_M3(Mods.Avaritia.ID, "Singularity", 3),
    // 下界石英奇点（元数据 4）
    UP_AVARITIA_SINGULARITY_M4(Mods.Avaritia.ID, "Singularity", 4),
    // 铜奇点（元数据 5）
    UP_AVARITIA_SINGULARITY_M5(Mods.Avaritia.ID, "Singularity", 5),
    // 锡奇点（元数据 6）
    UP_AVARITIA_SINGULARITY_M6(Mods.Avaritia.ID, "Singularity", 6),
    // 铅奇点（元数据 7）
    UP_AVARITIA_SINGULARITY_M7(Mods.Avaritia.ID, "Singularity", 7),
    // 银奇点（元数据 8）
    UP_AVARITIA_SINGULARITY_M8(Mods.Avaritia.ID, "Singularity", 8),
    // 镍奇点（元数据 9）
    UP_AVARITIA_SINGULARITY_M9(Mods.Avaritia.ID, "Singularity", 9),
    // 超级煲
    UP_AVARITIA_ULTIMATE_STEW(Mods.Avaritia.ID, "Ultimate_Stew", 0),
    // 物质团解压器
    UP_AVARITIA_CLUSTER_OPENER(Mods.Avaritia.ID, "cluster_opener", 0),

    // AvaritiaAddons：GT-Not-Leisure 固定外部物品引用
    // 压缩箱子
    UP_AVARITIAADDONS_COMPRESSEDCHEST(Mods.AvaritiaAddons.ID, "CompressedChest", 0),
    // 梦魇工作台
    UP_AVARITIAADDONS_EXTREMEAUTOCRAFTER(Mods.AvaritiaAddons.ID, "ExtremeAutoCrafter", 0),
    // 无尽箱子
    UP_AVARITIAADDONS_INFINITYCHEST(Mods.AvaritiaAddons.ID, "InfinityChest", 0),

    // BiomesOPlenty：GT-Not-Leisure 固定外部物品引用
    // 松树树叶（元数据 1）
    UP_BIOMESOPLENTY_COLORIZEDLEAVES2_M1(Mods.BiomesOPlenty.ID, "colorizedLeaves2", 1),
    // 松树树苗（元数据 5）
    UP_BIOMESOPLENTY_COLORIZEDSAPLINGS_M5(Mods.BiomesOPlenty.ID, "colorizedSaplings", 5),
    // 粉珊瑚（元数据 12）
    UP_BIOMESOPLENTY_CORAL1_M12(Mods.BiomesOPlenty.ID, "coral1", 12),
    // 橙珊瑚（元数据 13）
    UP_BIOMESOPLENTY_CORAL1_M13(Mods.BiomesOPlenty.ID, "coral1", 13),
    // 蓝珊瑚（元数据 14）
    UP_BIOMESOPLENTY_CORAL1_M14(Mods.BiomesOPlenty.ID, "coral1", 14),
    // 夜光珊瑚（元数据 15）
    UP_BIOMESOPLENTY_CORAL1_M15(Mods.BiomesOPlenty.ID, "coral1", 15),
    // 大型睡莲
    UP_BIOMESOPLENTY_LILYBOP(Mods.BiomesOPlenty.ID, "lilyBop", 0),
    // 中型睡莲（元数据 1）
    UP_BIOMESOPLENTY_LILYBOP_M1(Mods.BiomesOPlenty.ID, "lilyBop", 1),
    // 小型睡莲（元数据 2）
    UP_BIOMESOPLENTY_LILYBOP_M2(Mods.BiomesOPlenty.ID, "lilyBop", 2),
    // 松树原木
    UP_BIOMESOPLENTY_LOGS4(Mods.BiomesOPlenty.ID, "logs4", 0),
    // 松果（元数据 13）
    UP_BIOMESOPLENTY_MISC_M13(Mods.BiomesOPlenty.ID, "misc", 13),

    // BloodArsenal：GT-Not-Leisure 固定外部物品引用
    // 血之TNT
    UP_BLOODARSENAL_BLOOD_TNT(Mods.BloodArsenal.ID, "blood_tnt", 0),
    // 生命注入器
    UP_BLOODARSENAL_LIFE_INFUSER(Mods.BloodArsenal.ID, "life_infuser", 0),
    // 生命能量具现器
    UP_BLOODARSENAL_LP_MATERIALIZER(Mods.BloodArsenal.ID, "lp_materializer", 0),

    // BloodMagic：GT-Not-Leisure 固定外部物品引用
    // 血之祭坛
    UP_BLOODMAGIC_ALTAR(Mods.BloodMagic.ID, "Altar", 0),
    // [觉醒]激活水晶（元数据 1）
    UP_BLOODMAGIC_ACTIVATIONCRYSTAL_M1(Mods.BloodMagic.ID, "activationCrystal", 1),
    // 学徒气血宝珠
    UP_BLOODMAGIC_APPRENTICEBLOODORB(Mods.BloodMagic.ID, "apprenticeBloodOrb", 0),
    // 贤者气血宝珠
    UP_BLOODMAGIC_ARCHMAGEBLOODORB(Mods.BloodMagic.ID, "archmageBloodOrb", 0),
    // 炼金术台
    UP_BLOODMAGIC_BLOCKWRITINGTABLE(Mods.BloodMagic.ID, "blockWritingTable", 0),
    // 测试宝珠
    UP_BLOODMAGIC_CREATIVEFILLER(Mods.BloodMagic.ID, "creativeFiller", 0),
    // 献祭刀
    UP_BLOODMAGIC_DAGGEROFSACRIFICE(Mods.BloodMagic.ID, "daggerOfSacrifice", 0),
    // 仪式推测杖（元数据 2）
    UP_BLOODMAGIC_ITEMRITUALDIVINER_M2(Mods.BloodMagic.ID, "itemRitualDiviner", 2),
    // 法师气血宝珠
    UP_BLOODMAGIC_MAGICIANBLOODORB(Mods.BloodMagic.ID, "magicianBloodOrb", 0),
    // 导师气血宝珠
    UP_BLOODMAGIC_MASTERBLOODORB(Mods.BloodMagic.ID, "masterBloodOrb", 0),
    // 主仪式石
    UP_BLOODMAGIC_MASTERSTONE(Mods.BloodMagic.ID, "masterStone", 0),
    // 卓越气血宝珠
    UP_BLOODMAGIC_TRANSCENDENTBLOODORB(Mods.BloodMagic.ID, "transcendentBloodOrb", 0),
    // 虚弱气血宝珠
    UP_BLOODMAGIC_WEAKBLOODORB(Mods.BloodMagic.ID, "weakBloodOrb", 0),

    // Botania：GT-Not-Leisure 固定外部物品引用
    // 魔力钢锭
    UP_BOTANIA_MANARESOURCE(Mods.Botania.ID, "manaResource", 0),
    // 魔力珍珠（元数据 1）
    UP_BOTANIA_MANARESOURCE_M1(Mods.Botania.ID, "manaResource", 1),
    // 魔力钻石（元数据 2）
    UP_BOTANIA_MANARESOURCE_M2(Mods.Botania.ID, "manaResource", 2),
    // 炼金催化器
    UP_BOTANIA_ALCHEMYCATALYST(Mods.Botania.ID, "alchemyCatalyst", 0),
    // 精灵门核心
    UP_BOTANIA_ALFHEIMPORTAL(Mods.Botania.ID, "alfheimPortal", 0),
    // 艾琳的意志
    UP_BOTANIA_ANCIENTWILL(Mods.Botania.ID, "ancientWill", 0),
    // 达洛克的意志（元数据 1）
    UP_BOTANIA_ANCIENTWILL_M1(Mods.Botania.ID, "ancientWill", 1),
    // 古赞的意志（元数据 2）
    UP_BOTANIA_ANCIENTWILL_M2(Mods.Botania.ID, "ancientWill", 2),
    // 托拉格的意志（元数据 3）
    UP_BOTANIA_ANCIENTWILL_M3(Mods.Botania.ID, "ancientWill", 3),
    // 威拉克的意志（元数据 4）
    UP_BOTANIA_ANCIENTWILL_M4(Mods.Botania.ID, "ancientWill", 4),
    // 卡瑞的意志（元数据 5）
    UP_BOTANIA_ANCIENTWILL_M5(Mods.Botania.ID, "ancientWill", 5),
    // 彩虹桥方块
    UP_BOTANIA_BIFROSTPERM(Mods.Botania.ID, "bifrostPerm", 0),
    // 彩虹玻璃板
    UP_BOTANIA_BIFROSTPERMPANE(Mods.Botania.ID, "bifrostPermPane", 0),
    // 黑莲花
    UP_BOTANIA_BLACKLOTUS(Mods.Botania.ID, "blackLotus", 0),
    // 暗黑莲花（元数据 1）
    UP_BOTANIA_BLACKLOTUS_M1(Mods.Botania.ID, "blackLotus", 1),
    // 炼造催化器
    UP_BOTANIA_CONJURATIONCATALYST(Mods.Botania.ID, "conjurationCatalyst", 0),
    // 多媒体火花
    UP_BOTANIA_CORPOREASPARK(Mods.Botania.ID, "corporeaSpark", 0),
    // 主媒体火花（元数据 1）
    UP_BOTANIA_CORPOREASPARK_M1(Mods.Botania.ID, "corporeaSpark", 1),
    // 命运骰子
    UP_BOTANIA_DICE(Mods.Botania.ID, "dice", 0),
    // 精灵玻璃
    UP_BOTANIA_ELFGLASS(Mods.Botania.ID, "elfGlass", 0),
    // 盖亚守护者的头
    UP_BOTANIA_GAIAHEAD(Mods.Botania.ID, "gaiaHead", 0),
    // 魔力透镜：传送（元数据 18）
    UP_BOTANIA_LENS_M18(Mods.Botania.ID, "lens", 18),
    // 魔力透镜：反射（元数据 5）
    UP_BOTANIA_LENS_M5(Mods.Botania.ID, "lens", 5),
    // 植物魔法辞典
    UP_BOTANIA_LEXICON(Mods.Botania.ID, "lexicon", 0),
    // 磁化指环
    UP_BOTANIA_MAGNETRING(Mods.Botania.ID, "magnetRing", 0),
    // 不稳定信标
    UP_BOTANIA_MANABEACON(Mods.Botania.ID, "manaBeacon", 0),
    // 魔力钢锭
    UP_BOTANIA_MANARESOURCE_2(Mods.Botania.ID, "manaResource", 0),
    // 魔力珍珠（元数据 1）
    UP_BOTANIA_MANARESOURCE_M1_2(Mods.Botania.ID, "manaResource", 1),
    // 盖亚魂锭（元数据 14）
    UP_BOTANIA_MANARESOURCE_M14(Mods.Botania.ID, "manaResource", 14),
    // 瓶装末地空气（元数据 15）
    UP_BOTANIA_MANARESOURCE_M15(Mods.Botania.ID, "manaResource", 15),
    // 魔力钻石（元数据 2）
    UP_BOTANIA_MANARESOURCE_M2_2(Mods.Botania.ID, "manaResource", 2),
    // 泰拉钢锭（元数据 4）
    UP_BOTANIA_MANARESOURCE_M4(Mods.Botania.ID, "manaResource", 4),
    // 盖亚之魂（元数据 5）
    UP_BOTANIA_MANARESOURCE_M5(Mods.Botania.ID, "manaResource", 5),
    // 源质钢锭（元数据 7）
    UP_BOTANIA_MANARESOURCE_M7(Mods.Botania.ID, "manaResource", 7),
    // 精灵尘（元数据 8）
    UP_BOTANIA_MANARESOURCE_M8(Mods.Botania.ID, "manaResource", 8),
    // 龙石（元数据 9）
    UP_BOTANIA_MANARESOURCE_M9(Mods.Botania.ID, "manaResource", 9),
    // 增生之种
    UP_BOTANIA_OVERGROWTHSEED(Mods.Botania.ID, "overgrowthSeed", 0),
    // 增生之种（元数据 3）
    UP_BOTANIA_OVERGROWTHSEED_M3(Mods.Botania.ID, "overgrowthSeed", 3),
    // 粉色手炮
    UP_BOTANIA_PINKINATOR(Mods.Botania.ID, "pinkinator", 0),
    // 力量传递器
    UP_BOTANIA_PISTONRELAY(Mods.Botania.ID, "pistonRelay", 0),
    // 永恒魔力池（元数据 1）
    UP_BOTANIA_POOL_M1(Mods.Botania.ID, "pool", 1),
    // 神话魔力池（元数据 3）
    UP_BOTANIA_POOL_M3(Mods.Botania.ID, "pool", 3),
    // 魔力泵
    UP_BOTANIA_PUMP(Mods.Botania.ID, "pump", 0),
    // 魔法水晶
    UP_BOTANIA_PYLON(Mods.Botania.ID, "pylon", 0),
    // 自然水晶（元数据 1）
    UP_BOTANIA_PYLON_M1(Mods.Botania.ID, "pylon", 1),
    // 盖亚水晶（元数据 2）
    UP_BOTANIA_PYLON_M2(Mods.Botania.ID, "pylon", 2),
    // 触物指环
    UP_BOTANIA_REACHRING(Mods.Botania.ID, "reachRing", 0),
    // 破损的唱片
    UP_BOTANIA_RECORDGAIA2(Mods.Botania.ID, "recordGaia2", 0),
    // 魔力转换器
    UP_BOTANIA_RFGENERATOR(Mods.Botania.ID, "rfGenerator", 0),
    // 水之符文
    UP_BOTANIA_RUNE(Mods.Botania.ID, "rune", 0),
    // 火之符文（元数据 1）
    UP_BOTANIA_RUNE_M1(Mods.Botania.ID, "rune", 1),
    // 暴食符文（元数据 10）
    UP_BOTANIA_RUNE_M10(Mods.Botania.ID, "rune", 10),
    // 贪婪符文（元数据 11）
    UP_BOTANIA_RUNE_M11(Mods.Botania.ID, "rune", 11),
    // 懒惰符文（元数据 12）
    UP_BOTANIA_RUNE_M12(Mods.Botania.ID, "rune", 12),
    // 暴怒符文（元数据 13）
    UP_BOTANIA_RUNE_M13(Mods.Botania.ID, "rune", 13),
    // 嫉妒符文（元数据 14）
    UP_BOTANIA_RUNE_M14(Mods.Botania.ID, "rune", 14),
    // 傲慢符文（元数据 15）
    UP_BOTANIA_RUNE_M15(Mods.Botania.ID, "rune", 15),
    // 地之符文（元数据 2）
    UP_BOTANIA_RUNE_M2(Mods.Botania.ID, "rune", 2),
    // 风之符文（元数据 3）
    UP_BOTANIA_RUNE_M3(Mods.Botania.ID, "rune", 3),
    // 春之符文（元数据 4）
    UP_BOTANIA_RUNE_M4(Mods.Botania.ID, "rune", 4),
    // 夏之符文（元数据 5）
    UP_BOTANIA_RUNE_M5(Mods.Botania.ID, "rune", 5),
    // 秋之符文（元数据 6）
    UP_BOTANIA_RUNE_M6(Mods.Botania.ID, "rune", 6),
    // 冬之符文（元数据 7）
    UP_BOTANIA_RUNE_M7(Mods.Botania.ID, "rune", 7),
    // 魔力符文（元数据 8）
    UP_BOTANIA_RUNE_M8(Mods.Botania.ID, "rune", 8),
    // 欲望符文（元数据 9）
    UP_BOTANIA_RUNE_M9(Mods.Botania.ID, "rune", 9),
    // 符文祭坛
    UP_BOTANIA_RUNEALTAR(Mods.Botania.ID, "runeAltar", 0),
    // 火花
    UP_BOTANIA_SPARK(Mods.Botania.ID, "spark", 0),
    // 植物魔法花
    UP_BOTANIA_SPECIALFLOWER(Mods.Botania.ID, "specialFlower", 0),
    // 盖亚魔力发射器（元数据 3）
    UP_BOTANIA_SPREADER_M3(Mods.Botania.ID, "spreader", 3),
    // 安山岩
    UP_BOTANIA_STONE(Mods.Botania.ID, "stone", 0),
    // 玄武岩（元数据 1）
    UP_BOTANIA_STONE_M1(Mods.Botania.ID, "stone", 1),
    // 闪长岩（元数据 2）
    UP_BOTANIA_STONE_M2(Mods.Botania.ID, "stone", 2),
    // 花岗岩（元数据 3）
    UP_BOTANIA_STONE_M3(Mods.Botania.ID, "stone", 3),
    // 魔力钢块
    UP_BOTANIA_STORAGE(Mods.Botania.ID, "storage", 0),
    // 泰拉粉碎者
    UP_BOTANIA_TERRAPICK(Mods.Botania.ID, "terraPick", 0),
    // 泰拉凝聚板
    UP_BOTANIA_TERRAPLATE(Mods.Botania.ID, "terraPlate", 0),

    // BuildCraftFactory：GT-Not-Leisure 固定外部物品引用
    // 储罐
    UP_BUILDCRAFTFACTORY_TANKBLOCK(Mods.BuildCraftFactory.ID, "tankBlock", 0),

    // Chisel：GT-Not-Leisure 固定外部物品引用
    // 钻石凿子
    UP_CHISEL_DIAMONDCHISEL(Mods.Chisel.ID, "diamondChisel", 0),

    // Computronics：GT-Not-Leisure 固定外部物品引用
    // 创造模式内存
    UP_COMPUTRONICS_COMPUTRONICS_OCSPECIALPARTS(Mods.Computronics.ID, "computronics.ocSpecialParts", 0),

    // DraconicEvolution：GT-Not-Leisure 固定外部物品引用
    // 觉醒核心
    UP_DRACONICEVOLUTION_AWAKENEDCORE(Mods.DraconicEvolution.ID, "awakenedCore", 0),
    // 小的混沌残片（元数据 1）
    UP_DRACONICEVOLUTION_CHAOSFRAGMENT_M1(Mods.DraconicEvolution.ID, "chaosFragment", 1),
    // 混沌碎片
    UP_DRACONICEVOLUTION_CHAOSSHARD(Mods.DraconicEvolution.ID, "chaosShard", 0),
    // 混沌核心
    UP_DRACONICEVOLUTION_CHAOTICCORE(Mods.DraconicEvolution.ID, "chaoticCore", 0),
    // Dezil的棉花糖
    UP_DRACONICEVOLUTION_DEZILSMARSHMALLOW(Mods.DraconicEvolution.ID, "dezilsMarshmallow", 0),
    // 龙芯
    UP_DRACONICEVOLUTION_DRACONICCORE(Mods.DraconicEvolution.ID, "draconicCore", 0),
    // 龙块
    UP_DRACONICEVOLUTION_DRACONIUM(Mods.DraconicEvolution.ID, "draconium", 0),
    // 充能龙块（元数据 2）
    UP_DRACONICEVOLUTION_DRACONIUM_M2(Mods.DraconicEvolution.ID, "draconium", 2),
    // 龙之心
    UP_DRACONICEVOLUTION_DRAGONHEART(Mods.DraconicEvolution.ID, "dragonHeart", 0),
    // 物品错位器
    UP_DRACONICEVOLUTION_MAGNET(Mods.DraconicEvolution.ID, "magnet", 0),
    // 物品错位器（元数据 1）
    UP_DRACONICEVOLUTION_MAGNET_M1(Mods.DraconicEvolution.ID, "magnet", 1),
    // 反应堆核心
    UP_DRACONICEVOLUTION_REACTORCORE(Mods.DraconicEvolution.ID, "reactorCore", 0),
    // 高级错位宝石
    UP_DRACONICEVOLUTION_TELEPORTERMKII(Mods.DraconicEvolution.ID, "teleporterMKII", 0),

    // Dreamcraft：GT-Not-Leisure 固定外部物品引用
    // 琼脂（元数据 2）
    UP_DREAMCRAFT_GTNHBIOITEMS_M2(ModList.NewHorizonsCoreMod.ID, "GTNHBioItems", 2),

    // ENDER_IO：GT-Not-Leisure 固定外部物品引用
    // 电容库
    UP_ENDER_IO_BLOCKCAPBANK(Mods.EnderIO.ID, "blockCapBank", 0),
    // 玄钢砧
    UP_ENDER_IO_BLOCKDARKSTEELANVIL(Mods.EnderIO.ID, "blockDarkSteelAnvil", 0),
    // 末影人头颅
    UP_ENDER_IO_BLOCKENDERMANSKULL(Mods.EnderIO.ID, "blockEndermanSkull", 0),
    // 末影人头颅（元数据 2）
    UP_ENDER_IO_BLOCKENDERMANSKULL_M2(Mods.EnderIO.ID, "blockEndermanSkull", 2),
    // 种植站
    UP_ENDER_IO_BLOCKFARMSTATION(Mods.EnderIO.ID, "blockFarmStation", 0),
    // 红石合金块（元数据 3）
    UP_ENDER_IO_BLOCKINGOTSTORAGE_M3(Mods.EnderIO.ID, "blockIngotStorage", 3),
    // 玄钢块（元数据 6）
    UP_ENDER_IO_BLOCKINGOTSTORAGE_M6(Mods.EnderIO.ID, "blockIngotStorage", 6),
    // 电动刷怪笼
    UP_ENDER_IO_BLOCKPOWEREDSPAWNER(Mods.EnderIO.ID, "blockPoweredSpawner", 0),
    // 光伏板
    UP_ENDER_IO_BLOCKSOLARPANEL(Mods.EnderIO.ID, "blockSolarPanel", 0),
    // 高级光伏板（元数据 1）
    UP_ENDER_IO_BLOCKSOLARPANEL_M1(Mods.EnderIO.ID, "blockSolarPanel", 1),
    // 脉冲光伏板（元数据 2）
    UP_ENDER_IO_BLOCKSOLARPANEL_M2(Mods.EnderIO.ID, "blockSolarPanel", 2),
    // 电容
    UP_ENDER_IO_ITEMBASICCAPACITOR(Mods.EnderIO.ID, "itemBasicCapacitor", 0),
    // 僵尸电极
    UP_ENDER_IO_ITEMFRANKENSKULL(Mods.EnderIO.ID, "itemFrankenSkull", 0),
    // Z-逻辑控制器（元数据 1）
    UP_ENDER_IO_ITEMFRANKENSKULL_M1(Mods.EnderIO.ID, "itemFrankenSkull", 1),
    // 人造僵尸（元数据 2）
    UP_ENDER_IO_ITEMFRANKENSKULL_M2(Mods.EnderIO.ID, "itemFrankenSkull", 2),
    // 末影谐振器（元数据 3）
    UP_ENDER_IO_ITEMFRANKENSKULL_M3(Mods.EnderIO.ID, "itemFrankenSkull", 3),
    // 意识末影谐振器（元数据 4）
    UP_ENDER_IO_ITEMFRANKENSKULL_M4(Mods.EnderIO.ID, "itemFrankenSkull", 4),
    // 守卫者二极管（元数据 6）
    UP_ENDER_IO_ITEMFRANKENSKULL_M6(Mods.EnderIO.ID, "itemFrankenSkull", 6),
    // 预知晶体（元数据 13）
    UP_ENDER_IO_ITEMMATERIAL_M13(Mods.EnderIO.ID, "itemMaterial", 13),
    // 脉冲晶体粉（元数据 14）
    UP_ENDER_IO_ITEMMATERIAL_M14(Mods.EnderIO.ID, "itemMaterial", 14),
    // 末影晶体粉（元数据 16）
    UP_ENDER_IO_ITEMMATERIAL_M16(Mods.EnderIO.ID, "itemMaterial", 16),
    // 预知晶体粉（元数据 17）
    UP_ENDER_IO_ITEMMATERIAL_M17(Mods.EnderIO.ID, "itemMaterial", 17),
    // 脉冲晶体（元数据 5）
    UP_ENDER_IO_ITEMMATERIAL_M5(Mods.EnderIO.ID, "itemMaterial", 5),
    // 末影晶体（元数据 8）
    UP_ENDER_IO_ITEMMATERIAL_M8(Mods.EnderIO.ID, "itemMaterial", 8),
    // 诱引晶体（元数据 9）
    UP_ENDER_IO_ITEMMATERIAL_M9(Mods.EnderIO.ID, "itemMaterial", 9),
    // 能量导管
    UP_ENDER_IO_ITEMPOWERCONDUIT(Mods.EnderIO.ID, "itemPowerConduit", 0),

    // ElectroMagicTools：GT-Not-Leisure 固定外部物品引用
    // 混沌注魔八重压缩太阳能（元数据 14）
    UP_ELECTROMAGICTOOLS_EMTSOLARS4_M14(Mods.ElectroMagicTools.ID, "EMTSolars4", 14),
    // 风注魔八重压缩太阳能（元数据 15）
    UP_ELECTROMAGICTOOLS_EMTSOLARS4_M15(Mods.ElectroMagicTools.ID, "EMTSolars4", 15),
    // 水注魔八重压缩太阳能（元数据 1）
    UP_ELECTROMAGICTOOLS_EMTSOLARS5_M1(Mods.ElectroMagicTools.ID, "EMTSolars5", 1),
    // 火注魔八重压缩太阳能（元数据 2）
    UP_ELECTROMAGICTOOLS_EMTSOLARS5_M2(Mods.ElectroMagicTools.ID, "EMTSolars5", 2),
    // 能量源质发电机
    UP_ELECTROMAGICTOOLS_ESSENTIAGENERATORS(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 0),
    // 火之源质发电机（元数据 1）
    UP_ELECTROMAGICTOOLS_ESSENTIAGENERATORS_M1(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 1),
    // 灵气源质发电机（元数据 2）
    UP_ELECTROMAGICTOOLS_ESSENTIAGENERATORS_M2(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 2),
    // 木之源质发电机（元数据 3）
    UP_ELECTROMAGICTOOLS_ESSENTIAGENERATORS_M3(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 3),
    // 风之源质发电机（元数据 4）
    UP_ELECTROMAGICTOOLS_ESSENTIAGENERATORS_M4(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 4),
    // 贪婪源质发电机（元数据 5）
    UP_ELECTROMAGICTOOLS_ESSENTIAGENERATORS_M5(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 5),

    // EnderStorage：GT-Not-Leisure 固定外部物品引用
    // 末影箱子/储罐
    UP_ENDERSTORAGE_ENDERCHEST(Mods.EnderStorage.ID, "enderChest", 0),

    // EtFuturumRequiem：GT-Not-Leisure 固定外部物品引用
    // 黑石
    UP_ETFUTURUMREQUIEM_BLACKSTONE(Mods.EtFuturumRequiem.ID, "blackstone", 0),
    // 蓝冰
    UP_ETFUTURUMREQUIEM_BLUE_ICE(Mods.EtFuturumRequiem.ID, "blue_ice", 0),
    // 深板岩圆石
    UP_ETFUTURUMREQUIEM_COBBLED_DEEPSLATE(Mods.EtFuturumRequiem.ID, "cobbled_deepslate", 0),
    // 深板岩
    UP_ETFUTURUMREQUIEM_DEEPSLATE(Mods.EtFuturumRequiem.ID, "deepslate", 0),
    // 鞘翅
    UP_ETFUTURUMREQUIEM_ELYTRA(Mods.EtFuturumRequiem.ID, "elytra", 0),
    // 岩浆块
    UP_ETFUTURUMREQUIEM_MAGMA(Mods.EtFuturumRequiem.ID, "magma", 0),
    // 下界合金斧
    UP_ETFUTURUMREQUIEM_NETHERITE_AXE(Mods.EtFuturumRequiem.ID, "netherite_axe", 0),
    // 下界合金靴子
    UP_ETFUTURUMREQUIEM_NETHERITE_BOOTS(Mods.EtFuturumRequiem.ID, "netherite_boots", 0),
    // 下界合金胸甲
    UP_ETFUTURUMREQUIEM_NETHERITE_CHESTPLATE(Mods.EtFuturumRequiem.ID, "netherite_chestplate", 0),
    // 下界合金头盔
    UP_ETFUTURUMREQUIEM_NETHERITE_HELMET(Mods.EtFuturumRequiem.ID, "netherite_helmet", 0),
    // 下界合金锄
    UP_ETFUTURUMREQUIEM_NETHERITE_HOE(Mods.EtFuturumRequiem.ID, "netherite_hoe", 0),
    // 下界合金护腿
    UP_ETFUTURUMREQUIEM_NETHERITE_LEGGINGS(Mods.EtFuturumRequiem.ID, "netherite_leggings", 0),
    // 下界合金镐
    UP_ETFUTURUMREQUIEM_NETHERITE_PICKAXE(Mods.EtFuturumRequiem.ID, "netherite_pickaxe", 0),
    // 下界合金碎片
    UP_ETFUTURUMREQUIEM_NETHERITE_SCRAP(Mods.EtFuturumRequiem.ID, "netherite_scrap", 0),
    // 下界合金锹
    UP_ETFUTURUMREQUIEM_NETHERITE_SPADE(Mods.EtFuturumRequiem.ID, "netherite_spade", 0),
    // 下界合金剑
    UP_ETFUTURUMREQUIEM_NETHERITE_SWORD(Mods.EtFuturumRequiem.ID, "netherite_sword", 0),
    // 潜影壳
    UP_ETFUTURUMREQUIEM_SHULKER_SHELL(Mods.EtFuturumRequiem.ID, "shulker_shell", 0),
    // 黏液块
    UP_ETFUTURUMREQUIEM_SLIME(Mods.EtFuturumRequiem.ID, "slime", 0),
    // 灵魂火把
    UP_ETFUTURUMREQUIEM_SOUL_TORCH(Mods.EtFuturumRequiem.ID, "soul_torch", 0),
    // 海绵
    UP_ETFUTURUMREQUIEM_SPONGE(Mods.EtFuturumRequiem.ID, "sponge", 0),
    // 湿海绵（元数据 1）
    UP_ETFUTURUMREQUIEM_SPONGE_M1(Mods.EtFuturumRequiem.ID, "sponge", 1),
    // 不死图腾
    UP_ETFUTURUMREQUIEM_TOTEM_OF_UNDYING(Mods.EtFuturumRequiem.ID, "totem_of_undying", 0),

    // EternalSingularity：GT-Not-Leisure 固定外部物品引用
    // 闪瞬奇点
    UP_ETERNALSINGULARITY_COMBINED_SINGULARITY(Mods.EternalSingularity.ID, "combined_singularity", 0),
    // 圣灵奇点（元数据 1）
    UP_ETERNALSINGULARITY_COMBINED_SINGULARITY_M1(Mods.EternalSingularity.ID, "combined_singularity", 1),
    // 静空奇点（元数据 15）
    UP_ETERNALSINGULARITY_COMBINED_SINGULARITY_M15(Mods.EternalSingularity.ID, "combined_singularity", 15),
    // 意面奇点（元数据 2）
    UP_ETERNALSINGULARITY_COMBINED_SINGULARITY_M2(Mods.EternalSingularity.ID, "combined_singularity", 2),
    // 大气奇点（元数据 3）
    UP_ETERNALSINGULARITY_COMBINED_SINGULARITY_M3(Mods.EternalSingularity.ID, "combined_singularity", 3),
    // 神秘奇点（元数据 4）
    UP_ETERNALSINGULARITY_COMBINED_SINGULARITY_M4(Mods.EternalSingularity.ID, "combined_singularity", 4),
    // 史诗奇点（元数据 5）
    UP_ETERNALSINGULARITY_COMBINED_SINGULARITY_M5(Mods.EternalSingularity.ID, "combined_singularity", 5),
    // 星耀奇点（元数据 6）
    UP_ETERNALSINGULARITY_COMBINED_SINGULARITY_M6(Mods.EternalSingularity.ID, "combined_singularity", 6),
    // 永恒奇点
    UP_ETERNALSINGULARITY_ETERNAL_SINGULARITY(Mods.EternalSingularity.ID, "eternal_singularity", 0),

    // ExtraUtilities：GT-Not-Leisure 固定外部物品引用
    // 七重压缩圆石（元数据 6）
    UP_EXTRAUTILITIES_COBBLESTONE_COMPRESSED_M6(Mods.ExtraUtilities.ID, "cobblestone_compressed", 6),
    // 八重压缩圆石（元数据 7）
    UP_EXTRAUTILITIES_COBBLESTONE_COMPRESSED_M7(Mods.ExtraUtilities.ID, "cobblestone_compressed", 7),
    // 漆黑之门
    UP_EXTRAUTILITIES_DARK_PORTAL(Mods.ExtraUtilities.ID, "dark_portal", 0),
    // 不稳定金属方块（元数据 5）
    UP_EXTRAUTILITIES_DECORATIVEBLOCK1_M5(Mods.ExtraUtilities.ID, "decorativeBlock1", 5),
    // 荧石玻璃（元数据 7）
    UP_EXTRAUTILITIES_DECORATIVEBLOCK2_M7(Mods.ExtraUtilities.ID, "decorativeBlock2", 7),
    // 钻石锥刺
    UP_EXTRAUTILITIES_SPIKE_BASE_DIAMOND(Mods.ExtraUtilities.ID, "spike_base_diamond", 0),
    // 垃圾桶（流体）（元数据 1）
    UP_EXTRAUTILITIES_TRASHCAN_M1(Mods.ExtraUtilities.ID, "trashcan", 1),
    // 不稳定金属锭
    UP_EXTRAUTILITIES_UNSTABLEINGOT(Mods.ExtraUtilities.ID, "unstableingot", 0),

    // ForbiddenMagic：GT-Not-Leisure 固定外部物品引用
    // 邪术气血宝珠
    UP_FORBIDDENMAGIC_ELDRITCHORB(Mods.ForbiddenMagic.ID, "EldritchOrb", 0),

    // Forestry：GT-Not-Leisure 固定外部物品引用
    // 蜂箱组组件
    UP_FORESTRY_ALVEARY(Mods.Forestry.ID, "alveary", 0),
    // 蜂箱组克隆盒（元数据 2）
    UP_FORESTRY_ALVEARY_M2(Mods.Forestry.ID, "alveary", 2),
    // 蜂箱组稳定器（元数据 6）
    UP_FORESTRY_ALVEARY_M6(Mods.Forestry.ID, "alveary", 6),
    // 蜂蜡
    UP_FORESTRY_BEESWAX(Mods.Forestry.ID, "beeswax", 0),
    // 农场齿轮箱（元数据 2）
    UP_FORESTRY_FFARM_M2(Mods.Forestry.ID, "ffarm", 2),
    // 农场水阀（元数据 4）
    UP_FORESTRY_FFARM_M4(Mods.Forestry.ID, "ffarm", 4),
    // 农场控制盒（元数据 5）
    UP_FORESTRY_FFARM_M5(Mods.Forestry.ID, "ffarm", 5),
    // 树叶
    UP_FORESTRY_LEAVES(Mods.Forestry.ID, "leaves", 0),
    // 松树原木（元数据 20）
    UP_FORESTRY_LOGS_M20(Mods.Forestry.ID, "logs", 20),
    // 花粉
    UP_FORESTRY_POLLEN(Mods.Forestry.ID, "pollen", 0),
    // 蜂王浆
    UP_FORESTRY_ROYALJELLY(Mods.Forestry.ID, "royalJelly", 0),

    // GTPlusPlus：GT-Not-Leisure 固定外部物品引用
    // 脱水线圈 []（元数据 3）
    UP_GTPLUSPLUS_ITEMDEHYDRATORCOIL_M3(Mods.GTPlusPlus.ID, "itemDehydratorCoil", 3),
    // 能量核心 [UHV]
    UP_GTPLUSPLUS_ITEM_ITEMBUFFERCORE10(Mods.GTPlusPlus.ID, "item.itemBufferCore10", 0),

    // Gadomancy：GT-Not-Leisure 固定外部物品引用
    // 天域使魔
    UP_GADOMANCY_ITEMETHEREALFAMILIAR(Mods.Gadomancy.ID, "ItemEtherealFamiliar", 0),

    // GalacticraftAmunRa：GT-Not-Leisure 固定外部物品引用
    // 轻质合金板（元数据 15）
    UP_GALACTICRAFTAMUNRA_ITEM_BASEITEM_M15(Mods.GalacticraftAmunRa.ID, "item.baseItem", 15),
    // 暗物质碎片（元数据 26）
    UP_GALACTICRAFTAMUNRA_ITEM_BASEITEM_M26(Mods.GalacticraftAmunRa.ID, "item.baseItem", 26),
    // 穿梭机图纸
    UP_GALACTICRAFTAMUNRA_ITEM_SCHEMATIC(Mods.GalacticraftAmunRa.ID, "item.schematic", 0),
    // 暗物质（元数据 14）
    UP_GALACTICRAFTAMUNRA_TILE_BASEBLOCKROCK_M14(Mods.GalacticraftAmunRa.ID, "tile.baseBlockRock", 14),

    // GalacticraftCore：GT-Not-Leisure 固定外部物品引用
    // 高级晶圆（元数据 14）
    UP_GALACTICRAFTCORE_ITEM_BASICITEM_M14(Mods.GalacticraftCore.ID, "item.basicItem", 14),
    // 无限氧气罐
    UP_GALACTICRAFTCORE_ITEM_INFINITEOXYGEN(Mods.GalacticraftCore.ID, "item.infiniteOxygen", 0),
    // 2阶火箭图纸（元数据 1）
    UP_GALACTICRAFTCORE_ITEM_SCHEMATIC_M1(Mods.GalacticraftCore.ID, "item.schematic", 1),
    // 1阶火箭
    UP_GALACTICRAFTCORE_ITEM_SPACESHIP(Mods.GalacticraftCore.ID, "item.spaceship", 0),
    // NASA工作台
    UP_GALACTICRAFTCORE_TILE_ROCKETWORKBENCH(Mods.GalacticraftCore.ID, "tile.rocketWorkbench", 0),

    // GalacticraftMars：GT-Not-Leisure 固定外部物品引用
    // 3阶火箭
    UP_GALACTICRAFTMARS_ITEM_ITEMTIER3ROCKET(Mods.GalacticraftMars.ID, "item.itemTier3Rocket", 0),
    // 3阶火箭图纸
    UP_GALACTICRAFTMARS_ITEM_SCHEMATIC(Mods.GalacticraftMars.ID, "item.schematic", 0),
    // 2阶火箭
    UP_GALACTICRAFTMARS_ITEM_SPACESHIPTIER2(Mods.GalacticraftMars.ID, "item.spaceshipTier2", 0),

    // GalaxySpace：GT-Not-Leisure 固定外部物品引用
    // 巴纳德C树木原木
    UP_GALAXYSPACE_BARNARDACLOG(Mods.GalaxySpace.ID, "barnardaClog", 0),
    // 火箭控制电脑（元数据 4）
    UP_GALAXYSPACE_ITEM_ROCKETCONTROLCOMPUTER_M4(Mods.GalaxySpace.ID, "item.RocketControlComputer", 4),
    // 火箭控制电脑（元数据 7）
    UP_GALAXYSPACE_ITEM_ROCKETCONTROLCOMPUTER_M7(Mods.GalaxySpace.ID, "item.RocketControlComputer", 7),
    // NASA工作台图纸
    UP_GALAXYSPACE_ITEM_SCHEMATICTIER4(Mods.GalaxySpace.ID, "item.SchematicTier4", 0),
    // NASA工作台图纸
    UP_GALAXYSPACE_ITEM_SCHEMATICTIER5(Mods.GalaxySpace.ID, "item.SchematicTier5", 0),
    // NASA工作台图纸
    UP_GALAXYSPACE_ITEM_SCHEMATICTIER6(Mods.GalaxySpace.ID, "item.SchematicTier6", 0),
    // NASA工作台图纸
    UP_GALAXYSPACE_ITEM_SCHEMATICTIER7(Mods.GalaxySpace.ID, "item.SchematicTier7", 0),
    // NASA工作台图纸
    UP_GALAXYSPACE_ITEM_SCHEMATICTIER8(Mods.GalaxySpace.ID, "item.SchematicTier8", 0),
    // 4阶火箭
    UP_GALAXYSPACE_ITEM_TIER4ROCKET(Mods.GalaxySpace.ID, "item.Tier4Rocket", 0),
    // 5阶火箭
    UP_GALAXYSPACE_ITEM_TIER5ROCKET(Mods.GalaxySpace.ID, "item.Tier5Rocket", 0),
    // 6阶火箭
    UP_GALAXYSPACE_ITEM_TIER6ROCKET(Mods.GalaxySpace.ID, "item.Tier6Rocket", 0),
    // 7阶火箭
    UP_GALAXYSPACE_ITEM_TIER7ROCKET(Mods.GalaxySpace.ID, "item.Tier7Rocket", 0),
    // 8阶火箭
    UP_GALAXYSPACE_ITEM_TIER8ROCKET(Mods.GalaxySpace.ID, "item.Tier8Rocket", 0),
    // 鲸鱼座T星E藻类
    UP_GALAXYSPACE_TCETIEDANDELIONS(Mods.GalaxySpace.ID, "tcetiedandelions", 0),
    // 鲸鱼座T星E藻类（元数据 1）
    UP_GALAXYSPACE_TCETIEDANDELIONS_M1(Mods.GalaxySpace.ID, "tcetiedandelions", 1),
    // 鲸鱼座T星E藻类（元数据 2）
    UP_GALAXYSPACE_TCETIEDANDELIONS_M2(Mods.GalaxySpace.ID, "tcetiedandelions", 2),
    // 鲸鱼座T星E藻类（元数据 3）
    UP_GALAXYSPACE_TCETIEDANDELIONS_M3(Mods.GalaxySpace.ID, "tcetiedandelions", 3),
    // 鲸鱼座T星E藻类（元数据 4）
    UP_GALAXYSPACE_TCETIEDANDELIONS_M4(Mods.GalaxySpace.ID, "tcetiedandelions", 4),
    // 鲸鱼座T星E藻类（元数据 5）
    UP_GALAXYSPACE_TCETIEDANDELIONS_M5(Mods.GalaxySpace.ID, "tcetiedandelions", 5),

    // GraviSuite：GT-Not-Leisure 固定外部物品引用
    // 喷射引擎（元数据 6）
    UP_GRAVISUITE_ITEMSIMPLEITEM_M6(Mods.GraviSuite.ID, "itemSimpleItem", 6),

    // HardcoreEnderExpansion：GT-Not-Leisure 固定外部物品引用
    // 末影粉末
    UP_HARDCOREENDEREXPANSION_END_POWDER(Mods.HardcoreEnderExpansion.ID, "end_powder", 0),
    // 末影粉末矿石
    UP_HARDCOREENDEREXPANSION_END_POWDER_ORE(Mods.HardcoreEnderExpansion.ID, "end_powder_ore", 0),

    // IWillFindYou：GT-Not-Leisure 固定外部物品引用
    // 寻矿魔杖
    UP_IWILLFINDYOU_IFU_BUILDINGKIT(Mods.IWillFindYou.ID, "ifu_buildingKit", 0),

    // IndustrialCraft2：GT-Not-Leisure 固定外部物品引用
    // 核反应堆（元数据 5）
    UP_INDUSTRIALCRAFT2_BLOCKGENERATOR_M5(Mods.IndustrialCraft2.ID, "blockGenerator", 5),
    // 工业TNT
    UP_INDUSTRIALCRAFT2_BLOCKITNT(Mods.IndustrialCraft2.ID, "blockITNT", 0),
    // 铁炉（元数据 1）
    UP_INDUSTRIALCRAFT2_BLOCKMACHINE_M1(Mods.IndustrialCraft2.ID, "blockMachine", 1),
    // 核弹
    UP_INDUSTRIALCRAFT2_BLOCKNUKE(Mods.IndustrialCraft2.ID, "blockNuke", 0),
    // 核反应仓（元数据 通配）
    UP_INDUSTRIALCRAFT2_BLOCKREACTORCHAMBER_MANY(Mods.IndustrialCraft2.ID, "blockReactorChamber", OreDictionary.WILDCARD_VALUE),
    // 橡胶树原木
    UP_INDUSTRIALCRAFT2_BLOCKRUBWOOD(Mods.IndustrialCraft2.ID, "blockRubWood", 0),
    // 能量水晶（元数据 26）
    UP_INDUSTRIALCRAFT2_ITEMBATCRYSTAL_M26(Mods.IndustrialCraft2.ID, "itemBatCrystal", 26),
    // 兰波顿水晶（元数据 26）
    UP_INDUSTRIALCRAFT2_ITEMBATLAMACRYSTAL_M26(Mods.IndustrialCraft2.ID, "itemBatLamaCrystal", 26),
    // 空单元（元数据 13）
    UP_INDUSTRIALCRAFT2_ITEMCELLEMPTY_M13(Mods.IndustrialCraft2.ID, "itemCellEmpty", 13),
    // 粘性树脂
    UP_INDUSTRIALCRAFT2_ITEMHARZ(Mods.IndustrialCraft2.ID, "itemHarz", 0),
    // 生碳纤维
    UP_INDUSTRIALCRAFT2_ITEMPARTCARBONFIBRE(Mods.IndustrialCraft2.ID, "itemPartCarbonFibre", 0),
    // 电路板
    UP_INDUSTRIALCRAFT2_ITEMPARTCIRCUIT(Mods.IndustrialCraft2.ID, "itemPartCircuit", 0),
    // 高级电路板
    UP_INDUSTRIALCRAFT2_ITEMPARTCIRCUITADV(Mods.IndustrialCraft2.ID, "itemPartCircuitAdv", 0),
    // 加厚中子反射板（元数据 1）
    UP_INDUSTRIALCRAFT2_REACTORREFLECTORTHICK_M1(Mods.IndustrialCraft2.ID, "reactorReflectorThick", 1),

    // IronChests：GT-Not-Leisure 固定外部物品引用
    // 铁箱子
    UP_IRONCHESTS_BLOCKIRONCHEST(Mods.IronChests.ID, "BlockIronChest", 0),
    // 金箱子（元数据 1）
    UP_IRONCHESTS_BLOCKIRONCHEST_M1(Mods.IronChests.ID, "BlockIronChest", 1),
    // 钻石箱子（元数据 2）
    UP_IRONCHESTS_BLOCKIRONCHEST_M2(Mods.IronChests.ID, "BlockIronChest", 2),
    // 铜箱子（元数据 3）
    UP_IRONCHESTS_BLOCKIRONCHEST_M3(Mods.IronChests.ID, "BlockIronChest", 3),
    // 钢箱子（元数据 4）
    UP_IRONCHESTS_BLOCKIRONCHEST_M4(Mods.IronChests.ID, "BlockIronChest", 4),
    // 水晶箱子（元数据 5）
    UP_IRONCHESTS_BLOCKIRONCHEST_M5(Mods.IronChests.ID, "BlockIronChest", 5),
    // 黑曜石箱子（元数据 6）
    UP_IRONCHESTS_BLOCKIRONCHEST_M6(Mods.IronChests.ID, "BlockIronChest", 6),
    // 下界合金箱子（元数据 8）
    UP_IRONCHESTS_BLOCKIRONCHEST_M8(Mods.IronChests.ID, "BlockIronChest", 8),
    // 玄钢箱子（元数据 9）
    UP_IRONCHESTS_BLOCKIRONCHEST_M9(Mods.IronChests.ID, "BlockIronChest", 9),

    // IronTanks：GT-Not-Leisure 固定外部物品引用
    // 钻石储罐
    UP_IRONTANKS_DIAMONDTANK(Mods.IronTanks.ID, "diamondTank", 0),

    // KekzTech：GT-Not-Leisure 固定外部物品引用
    // 兰波顿机械方块/电容
    UP_KEKZTECH_KEKZTECH_LAPOTRONICENERGYUNIT_BLOCK(Mods.KekzTech.ID, "kekztech_lapotronicenergyunit_block", 0),

    // MineAndBladeBattleGear2：GT-Not-Leisure 固定外部物品引用
    // 穿刺箭（元数据 3）
    UP_MINEANDBLADEBATTLEGEAR2_MB_ARROW_M3(Mods.MineAndBladeBattleGear2.ID, "mb.arrow", 3),

    // Minecraft：GT-Not-Leisure 固定外部物品引用
    // 纸
    UP_MINECRAFT_PAPER(Mods.Minecraft.ID, "paper", 0),
    // 红石粉
    UP_MINECRAFT_REDSTONE(Mods.Minecraft.ID, "redstone", 0),

    // Natura：GT-Not-Leisure 固定外部物品引用
    // 黑云（元数据 1）
    UP_NATURA_CLOUD_M1(Mods.Natura.ID, "Cloud", 1),
    // 灰云（元数据 2）
    UP_NATURA_CLOUD_M2(Mods.Natura.ID, "Cloud", 2),
    // 硫云（元数据 3）
    UP_NATURA_CLOUD_M3(Mods.Natura.ID, "Cloud", 3),

    // OpenBlocks：GT-Not-Leisure 固定外部物品引用
    // 电梯
    UP_OPENBLOCKS_ELEVATOR(Mods.OpenBlocks.ID, "elevator", 0),
    // 海绵
    UP_OPENBLOCKS_SPONGE(Mods.OpenBlocks.ID, "sponge", 0),

    // OpenComputers：GT-Not-Leisure 固定外部物品引用
    // T3加速处理器（APU）（元数据 103）
    UP_OPENCOMPUTERS_ITEM_M103(Mods.OpenComputers.ID, "item", 103),
    // 创造模式组件总线（元数据 114）
    UP_OPENCOMPUTERS_ITEM_M114(Mods.OpenComputers.ID, "item", 114),
    // T4服务器（元数据 69）
    UP_OPENCOMPUTERS_ITEM_M69(Mods.OpenComputers.ID, "item", 69),
    // T3微控制器外壳（元数据 90）
    UP_OPENCOMPUTERS_ITEM_M90(Mods.OpenComputers.ID, "item", 90),
    // T3无人机外壳（元数据 91）
    UP_OPENCOMPUTERS_ITEM_M91(Mods.OpenComputers.ID, "item", 91),
    // T3平板电脑外壳（元数据 93）
    UP_OPENCOMPUTERS_ITEM_M93(Mods.OpenComputers.ID, "item", 93),

    // PamsHarvestCraft：GT-Not-Leisure 固定外部物品引用
    // 生鳀鱼
    UP_PAMSHARVESTCRAFT_ANCHOVYRAWITEM(Mods.PamsHarvestCraft.ID, "anchovyrawItem", 0),
    // 生鲈鱼
    UP_PAMSHARVESTCRAFT_BASSRAWITEM(Mods.PamsHarvestCraft.ID, "bassrawItem", 0),
    // 生鱿鱼
    UP_PAMSHARVESTCRAFT_CALAMARIRAWITEM(Mods.PamsHarvestCraft.ID, "calamarirawItem", 0),
    // 生鲤鱼
    UP_PAMSHARVESTCRAFT_CARPRAWITEM(Mods.PamsHarvestCraft.ID, "carprawItem", 0),
    // 生鲶鱼
    UP_PAMSHARVESTCRAFT_CATFISHRAWITEM(Mods.PamsHarvestCraft.ID, "catfishrawItem", 0),
    // 生嘉鱼
    UP_PAMSHARVESTCRAFT_CHARRRAWITEM(Mods.PamsHarvestCraft.ID, "charrrawItem", 0),
    // 生蛤蜊
    UP_PAMSHARVESTCRAFT_CLAMRAWITEM(Mods.PamsHarvestCraft.ID, "clamrawItem", 0),
    // 生螃蟹
    UP_PAMSHARVESTCRAFT_CRABRAWITEM(Mods.PamsHarvestCraft.ID, "crabrawItem", 0),
    // 蔓越莓
    UP_PAMSHARVESTCRAFT_CRANBERRYITEM(Mods.PamsHarvestCraft.ID, "cranberryItem", 0),
    // 生龙虾
    UP_PAMSHARVESTCRAFT_CRAYFISHRAWITEM(Mods.PamsHarvestCraft.ID, "crayfishrawItem", 0),
    // 生鳗鱼
    UP_PAMSHARVESTCRAFT_EELRAWITEM(Mods.PamsHarvestCraft.ID, "eelrawItem", 0),
    // 生青蛙
    UP_PAMSHARVESTCRAFT_FROGRAWITEM(Mods.PamsHarvestCraft.ID, "frograwItem", 0),
    // 绿心鱼
    UP_PAMSHARVESTCRAFT_GREENHEARTFISHITEM(Mods.PamsHarvestCraft.ID, "greenheartfishItem", 0),
    // 生石斑鱼
    UP_PAMSHARVESTCRAFT_GROUPERRAWITEM(Mods.PamsHarvestCraft.ID, "grouperrawItem", 0),
    // 生青鱼
    UP_PAMSHARVESTCRAFT_HERRINGRAWITEM(Mods.PamsHarvestCraft.ID, "herringrawItem", 0),
    // 香辣鸡翅
    UP_PAMSHARVESTCRAFT_HOTWINGSITEM(Mods.PamsHarvestCraft.ID, "hotwingsItem", 0),
    // 生海蜇
    UP_PAMSHARVESTCRAFT_JELLYFISHRAWITEM(Mods.PamsHarvestCraft.ID, "jellyfishrawItem", 0),
    // 生泥鱼
    UP_PAMSHARVESTCRAFT_MUDFISHRAWITEM(Mods.PamsHarvestCraft.ID, "mudfishrawItem", 0),
    // 生章鱼
    UP_PAMSHARVESTCRAFT_OCTOPUSRAWITEM(Mods.PamsHarvestCraft.ID, "octopusrawItem", 0),
    // 生鲈鱼
    UP_PAMSHARVESTCRAFT_PERCHRAWITEM(Mods.PamsHarvestCraft.ID, "perchrawItem", 0),
    // 水稻
    UP_PAMSHARVESTCRAFT_RICEITEM(Mods.PamsHarvestCraft.ID, "riceItem", 0),
    // 蜂王浆
    UP_PAMSHARVESTCRAFT_ROYALJELLYITEM(Mods.PamsHarvestCraft.ID, "royaljellyItem", 0),
    // 生扇贝
    UP_PAMSHARVESTCRAFT_SCALLOPRAWITEM(Mods.PamsHarvestCraft.ID, "scalloprawItem", 0),
    // 海带
    UP_PAMSHARVESTCRAFT_SEAWEEDITEM(Mods.PamsHarvestCraft.ID, "seaweedItem", 0),
    // 生虾
    UP_PAMSHARVESTCRAFT_SHRIMPRAWITEM(Mods.PamsHarvestCraft.ID, "shrimprawItem", 0),
    // 生蜗牛
    UP_PAMSHARVESTCRAFT_SNAILRAWITEM(Mods.PamsHarvestCraft.ID, "snailrawItem", 0),
    // 生鲷鱼
    UP_PAMSHARVESTCRAFT_SNAPPERRAWITEM(Mods.PamsHarvestCraft.ID, "snapperrawItem", 0),
    // 生罗非鱼
    UP_PAMSHARVESTCRAFT_TILAPIARAWITEM(Mods.PamsHarvestCraft.ID, "tilapiarawItem", 0),
    // 生鳟鱼
    UP_PAMSHARVESTCRAFT_TROUTRAWITEM(Mods.PamsHarvestCraft.ID, "troutrawItem", 0),
    // 生金枪鱼
    UP_PAMSHARVESTCRAFT_TUNARAWITEM(Mods.PamsHarvestCraft.ID, "tunarawItem", 0),
    // 生海龟
    UP_PAMSHARVESTCRAFT_TURTLERAWITEM(Mods.PamsHarvestCraft.ID, "turtlerawItem", 0),
    // 生碧古鱼
    UP_PAMSHARVESTCRAFT_WALLEYERAWITEM(Mods.PamsHarvestCraft.ID, "walleyerawItem", 0),
    // 荸荠
    UP_PAMSHARVESTCRAFT_WATERCHESTNUTITEM(Mods.PamsHarvestCraft.ID, "waterchestnutItem", 0),

    // ProjectRedFabrication：GT-Not-Leisure 固定外部物品引用
    // 创造模式IC芯片（元数据 1）
    UP_PROJECTREDFABRICATION_PROJECTRED_FABRICATION_ICCHIP_M1(Mods.ProjectRedFabrication.ID, "projectred.fabrication.icchip", 1),

    // Railcraft：GT-Not-Leisure 固定外部物品引用
    // 高级焦炉砖块（元数据 12）
    UP_RAILCRAFT_MACHINE_ALPHA_M12(Mods.Railcraft.ID, "machine.alpha", 12),
    // 民科蒸汽引擎（元数据 7）
    UP_RAILCRAFT_MACHINE_BETA_M7(Mods.Railcraft.ID, "machine.beta", 7),

    // RandomThings：GT-Not-Leisure 固定外部物品引用
    // 沃土
    UP_RANDOMTHINGS_FERTILIZEDDIRT(Mods.RandomThings.ID, "fertilizedDirt", 0),
    // 灵气（元数据 3）
    UP_RANDOMTHINGS_INGREDIENT_M3(Mods.RandomThings.ID, "ingredient", 3),

    // SGCraft：GT-Not-Leisure 固定外部物品引用
    // 吊炸天电容
    UP_SGCRAFT_IC2CAPACITOR(Mods.SGCraft.ID, "ic2Capacitor", 0),
    // RF星门能量单元
    UP_SGCRAFT_RFPOWERUNIT(Mods.SGCraft.ID, "rfPowerUnit", 0),
    // 星门导标升级
    UP_SGCRAFT_SGCHEVRONUPGRADE(Mods.SGCraft.ID, "sgChevronUpgrade", 0),
    // 星门控制水晶
    UP_SGCRAFT_SGCONTROLLERCRYSTAL(Mods.SGCraft.ID, "sgControllerCrystal", 0),
    // 星门核心水晶
    UP_SGCRAFT_SGCORECRYSTAL(Mods.SGCraft.ID, "sgCoreCrystal", 0),
    // 星门虹膜升级
    UP_SGCRAFT_SGIRISUPGRADE(Mods.SGCraft.ID, "sgIrisUpgrade", 0),
    // 星门底座方块
    UP_SGCRAFT_STARGATEBASE(Mods.SGCraft.ID, "stargateBase", 0),
    // 星门控制器
    UP_SGCRAFT_STARGATECONTROLLER(Mods.SGCraft.ID, "stargateController", 0),
    // 星门外环段
    UP_SGCRAFT_STARGATERING(Mods.SGCraft.ID, "stargateRing", 0),
    // 星门导标方块（元数据 1）
    UP_SGCRAFT_STARGATERING_M1(Mods.SGCraft.ID, "stargateRing", 1),

    // StevesCarts2：GT-Not-Leisure 固定外部物品引用
    // 标准车壳（元数据 38）
    UP_STEVESCARTS2_CARTMODULE_M38(Mods.StevesCarts2.ID, "CartModule", 38),
    // 无尽引擎（元数据 61）
    UP_STEVESCARTS2_CARTMODULE_M61(Mods.StevesCarts2.ID, "CartModule", 61),
    // 升级：创造模式（元数据 14）
    UP_STEVESCARTS2_UPGRADE_M14(Mods.StevesCarts2.ID, "upgrade", 14),

    // StorageDrawers：GT-Not-Leisure 固定外部物品引用
    // 抽屉管理器
    UP_STORAGEDRAWERS_CONTROLLER(Mods.StorageDrawers.ID, "controller", 0),
    // 抽屉容量升级（II）（元数据 2）
    UP_STORAGEDRAWERS_UPGRADE_M2(Mods.StorageDrawers.ID, "upgrade", 2),
    // 抽屉容量升级（III）（元数据 3）
    UP_STORAGEDRAWERS_UPGRADE_M3(Mods.StorageDrawers.ID, "upgrade", 3),
    // 抽屉容量升级（IV）（元数据 4）
    UP_STORAGEDRAWERS_UPGRADE_M4(Mods.StorageDrawers.ID, "upgrade", 4),
    // 抽屉容量升级（V）（元数据 5）
    UP_STORAGEDRAWERS_UPGRADE_M5(Mods.StorageDrawers.ID, "upgrade", 5),
    // 抽屉容量升级（VI）（元数据 6）
    UP_STORAGEDRAWERS_UPGRADE_M6(Mods.StorageDrawers.ID, "upgrade", 6),
    // 抽屉容量升级（VII）（元数据 7）
    UP_STORAGEDRAWERS_UPGRADE_M7(Mods.StorageDrawers.ID, "upgrade", 7),
    // 抽屉容量升级（VIII）（元数据 8）
    UP_STORAGEDRAWERS_UPGRADE_M8(Mods.StorageDrawers.ID, "upgrade", 8),
    // 升级模板
    UP_STORAGEDRAWERS_UPGRADETEMPLATE(Mods.StorageDrawers.ID, "upgradeTemplate", 0),

    // StructureLib：GT-Not-Leisure 固定外部物品引用
    // 多方块机器全息投影仪
    UP_STRUCTURELIB_ITEM_STRUCTURELIB_CONSTRUCTABLETRIGGER(Mods.StructureLib.ID, "item.structurelib.constructableTrigger", 0),

    // TaintedMagic：GT-Not-Leisure 固定外部物品引用
    // 法杖核心:时间
    UP_TAINTEDMAGIC_ITEMFOCUSTIME(Mods.TaintedMagic.ID, "ItemFocusTime", 0),

    // Thaumcraft：GT-Not-Leisure 固定外部物品引用
    // 法杖核心:元始
    UP_THAUMCRAFT_FOCUSPRIMAL(Mods.Thaumcraft.ID, "FocusPrimal", 0),
    // 法杖核心:守护
    UP_THAUMCRAFT_FOCUSWARDING(Mods.Thaumcraft.ID, "FocusWarding", 0),
    // 凡人护身符
    UP_THAUMCRAFT_ITEMBAUBLEBLANKS(Mods.Thaumcraft.ID, "ItemBaubleBlanks", 0),
    // 凡人指环（元数据 1）
    UP_THAUMCRAFT_ITEMBAUBLEBLANKS_M1(Mods.Thaumcraft.ID, "ItemBaubleBlanks", 1),
    // 元始珍珠（元数据 3）
    UP_THAUMCRAFT_ITEMELDRITCHOBJECT_M3(Mods.Thaumcraft.ID, "ItemEldritchObject", 3),
    // 白色油脂蜡烛
    UP_THAUMCRAFT_BLOCKCANDLE(Mods.Thaumcraft.ID, "blockCandle", 0),
    // 风之魔晶
    UP_THAUMCRAFT_BLOCKCRYSTAL(Mods.Thaumcraft.ID, "blockCrystal", 0),
    // 火之魔晶（元数据 1）
    UP_THAUMCRAFT_BLOCKCRYSTAL_M1(Mods.Thaumcraft.ID, "blockCrystal", 1),
    // 水之魔晶（元数据 2）
    UP_THAUMCRAFT_BLOCKCRYSTAL_M2(Mods.Thaumcraft.ID, "blockCrystal", 2),
    // 复相魔晶（元数据 6）
    UP_THAUMCRAFT_BLOCKCRYSTAL_M6(Mods.Thaumcraft.ID, "blockCrystal", 6),
    // 符文矩阵（元数据 2）
    UP_THAUMCRAFT_BLOCKSTONEDEVICE_M2(Mods.Thaumcraft.ID, "blockStoneDevice", 2),
    // 奥术工作台（元数据 15）
    UP_THAUMCRAFT_BLOCKTABLE_M15(Mods.Thaumcraft.ID, "blockTable", 15),

    // ThaumicBases：GT-Not-Leisure 固定外部物品引用
    // 镶金黑曜石
    UP_THAUMICBASES_ELDRITCHARK(Mods.ThaumicBases.ID, "eldritchArk", 0),
    // 彩虹仙人掌
    UP_THAUMICBASES_RAINBOWCACTUS(Mods.ThaumicBases.ID, "rainbowCactus", 0),
    // 奥术左轮枪
    UP_THAUMICBASES_REVOLVER(Mods.ThaumicBases.ID, "revolver", 0),

    // ThaumicEnergistics：GT-Not-Leisure 固定外部物品引用
    // 魔导源质存储元件（元数据 4）
    UP_THAUMICENERGISTICS_STORAGE_ESSENTIA_M4(Mods.ThaumicEnergistics.ID, "storage.essentia", 4),
    // 奥术装配室
    UP_THAUMICENERGISTICS_THAUMICENERGISTICS_BLOCK_ARCANE_ASSEMBLER(Mods.ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 0),

    // ThaumicExploration：GT-Not-Leisure 固定外部物品引用
    // 炼狱之壶
    UP_THAUMICEXPLORATION_EVERBURNURN(Mods.ThaumicExploration.ID, "everburnUrn", 0),

    // ThaumicTinkerer：GT-Not-Leisure 固定外部物品引用
    // 觉醒灵宝镐
    UP_THAUMICTINKERER_ICHORPICKGEM(Mods.ThaumicTinkerer.ID, "ichorPickGem", 0),

    // TinkerConstruct：GT-Not-Leisure 固定外部物品引用
    // 钴矿石（元数据 1）
    UP_TINKERCONSTRUCT_SEAREDBRICK_M1(Mods.TinkerConstruct.ID, "SearedBrick", 1),
    // 阿迪特矿石（元数据 2）
    UP_TINKERCONSTRUCT_SEAREDBRICK_M2(Mods.TinkerConstruct.ID, "SearedBrick", 2),
    // 合成站
    UP_TINKERCONSTRUCT_CRAFTINGSTATION(Mods.TinkerConstruct.ID, "CraftingStation", 0),
    // 史莱姆水晶（元数据 1）
    UP_TINKERCONSTRUCT_MATERIALS_M1(Mods.TinkerConstruct.ID, "materials", 1),
    // 蓝色史莱姆水晶（元数据 17）
    UP_TINKERCONSTRUCT_MATERIALS_M17(Mods.TinkerConstruct.ID, "materials", 17),
    // 凝固史莱姆块
    UP_TINKERCONSTRUCT_SLIME_GEL(Mods.TinkerConstruct.ID, "slime.gel", 0),
    // 弹跳板
    UP_TINKERCONSTRUCT_SLIME_PAD(Mods.TinkerConstruct.ID, "slime.pad", 0),

    // TwilightForest：GT-Not-Leisure 固定外部物品引用
    // 雪人首领毛皮
    UP_TWILIGHTFOREST_ITEM_ALPHAFUR(Mods.TwilightForest.ID, "item.alphaFur", 0),
    // 极地毛皮
    UP_TWILIGHTFOREST_ITEM_ARCTICFUR(Mods.TwilightForest.ID, "item.arcticFur", 0),
    // 砷铅铁
    UP_TWILIGHTFOREST_ITEM_CARMINITE(Mods.TwilightForest.ID, "item.carminite", 0),
    // 保管符咒 III
    UP_TWILIGHTFOREST_ITEM_CHARMOFKEEPING3(Mods.TwilightForest.ID, "item.charmOfKeeping3", 0),
    // 生命符咒 II
    UP_TWILIGHTFOREST_ITEM_CHARMOFLIFE2(Mods.TwilightForest.ID, "item.charmOfLife2", 0),
    // 粉碎号角
    UP_TWILIGHTFOREST_ITEM_CRUMBLEHORN(Mods.TwilightForest.ID, "item.crumbleHorn", 0),
    // 炽热的血液
    UP_TWILIGHTFOREST_ITEM_FIERYBLOOD(Mods.TwilightForest.ID, "item.fieryBlood", 0),
    // 炽热的泪
    UP_TWILIGHTFOREST_ITEM_FIERYTEARS(Mods.TwilightForest.ID, "item.fieryTears", 0),
    // 巨人的镐
    UP_TWILIGHTFOREST_ITEM_GIANTPICK(Mods.TwilightForest.ID, "item.giantPick", 0),
    // 巨人的剑
    UP_TWILIGHTFOREST_ITEM_GIANTSWORD(Mods.TwilightForest.ID, "item.giantSword", 0),
    // 九头蛇肉排
    UP_TWILIGHTFOREST_ITEM_HYDRACHOP(Mods.TwilightForest.ID, "item.hydraChop", 0),
    // 寒冰炸弹
    UP_TWILIGHTFOREST_ITEM_ICEBOMB(Mods.TwilightForest.ID, "item.iceBomb", 0),
    // 铁木锭
    UP_TWILIGHTFOREST_ITEM_IRONWOODINGOT(Mods.TwilightForest.ID, "item.ironwoodIngot", 0),
    // 骑士金属锭
    UP_TWILIGHTFOREST_ITEM_KNIGHTMETAL(Mods.TwilightForest.ID, "item.knightMetal", 0),
    // 灰烬烧灯
    UP_TWILIGHTFOREST_ITEM_LAMPOFCINDERS(Mods.TwilightForest.ID, "item.lampOfCinders", 0),
    // 魔豆
    UP_TWILIGHTFOREST_ITEM_MAGICBEANS(Mods.TwilightForest.ID, "item.magicBeans", 0),
    // 魔法地图核心
    UP_TWILIGHTFOREST_ITEM_MAGICMAPFOCUS(Mods.TwilightForest.ID, "item.magicMapFocus", 0),
    // 迷宫地图核心
    UP_TWILIGHTFOREST_ITEM_MAZEMAPFOCUS(Mods.TwilightForest.ID, "item.mazeMapFocus", 0),
    // 迷宫破坏者
    UP_TWILIGHTFOREST_ITEM_MAZEBREAKERPICK(Mods.TwilightForest.ID, "item.mazebreakerPick", 0),
    // 牛头人肉排
    UP_TWILIGHTFOREST_ITEM_MEEFSTEAK(Mods.TwilightForest.ID, "item.meefSteak", 0),
    // 牛头人沙拉酱肉
    UP_TWILIGHTFOREST_ITEM_MEEFSTROGANOFF(Mods.TwilightForest.ID, "item.meefStroganoff", 0),
    // 娜迦鳞片
    UP_TWILIGHTFOREST_ITEM_NAGASCALE(Mods.TwilightForest.ID, "item.nagaScale", 0),
    // 幻影头盔
    UP_TWILIGHTFOREST_ITEM_PHANTOMHELM(Mods.TwilightForest.ID, "item.phantomHelm", 0),
    // 幻影胸甲
    UP_TWILIGHTFOREST_ITEM_PHANTOMPLATE(Mods.TwilightForest.ID, "item.phantomPlate", 0),
    // 吸血权杖
    UP_TWILIGHTFOREST_ITEM_SCEPTERLIFEDRAIN(Mods.TwilightForest.ID, "item.scepterLifeDrain", 0),
    // 黄昏权杖
    UP_TWILIGHTFOREST_ITEM_SCEPTERTWILIGHT(Mods.TwilightForest.ID, "item.scepterTwilight", 0),
    // 僵尸权杖
    UP_TWILIGHTFOREST_ITEM_SCEPTERZOMBIE(Mods.TwilightForest.ID, "item.scepterZombie", 0),
    // 钢叶
    UP_TWILIGHTFOREST_ITEM_STEELEAFINGOT(Mods.TwilightForest.ID, "item.steeleafIngot", 0),
    // 三发弓
    UP_TWILIGHTFOREST_ITEM_TRIPLEBOW(Mods.TwilightForest.ID, "item.tripleBow", 0),
    // 九头蛇战利品
    UP_TWILIGHTFOREST_ITEM_TROPHY(Mods.TwilightForest.ID, "item.trophy", 0),
    // 娜迦战利品（元数据 1）
    UP_TWILIGHTFOREST_ITEM_TROPHY_M1(Mods.TwilightForest.ID, "item.trophy", 1),
    // 巫妖战利品（元数据 2）
    UP_TWILIGHTFOREST_ITEM_TROPHY_M2(Mods.TwilightForest.ID, "item.trophy", 2),
    // 暮色恶魂战利品（元数据 3）
    UP_TWILIGHTFOREST_ITEM_TROPHY_M3(Mods.TwilightForest.ID, "item.trophy", 3),
    // 冰雪女王战利品（元数据 4）
    UP_TWILIGHTFOREST_ITEM_TROPHY_M4(Mods.TwilightForest.ID, "item.trophy", 4),
    // 米诺陶战利品（元数据 5）
    UP_TWILIGHTFOREST_ITEM_TROPHY_M5(Mods.TwilightForest.ID, "item.trophy", 5),
    // 幻影骑士战利品（元数据 6）
    UP_TWILIGHTFOREST_ITEM_TROPHY_M6(Mods.TwilightForest.ID, "item.trophy", 6),
    // 雪人首领战利品（元数据 7）
    UP_TWILIGHTFOREST_ITEM_TROPHY_M7(Mods.TwilightForest.ID, "item.trophy", 7),
    // 谜题羊战利品（元数据 8）
    UP_TWILIGHTFOREST_ITEM_TROPHY_M8(Mods.TwilightForest.ID, "item.trophy", 8),
    // 极光柱
    UP_TWILIGHTFOREST_TILE_AURORAPILLAR(Mods.TwilightForest.ID, "tile.AuroraPillar", 0),
    // 蓬松的云
    UP_TWILIGHTFOREST_TILE_FLUFFYCLOUD(Mods.TwilightForest.ID, "tile.FluffyCloud", 0),
    // 巨型圆石
    UP_TWILIGHTFOREST_TILE_GIANTCOBBLE(Mods.TwilightForest.ID, "tile.GiantCobble", 0),
    // 巨型原木
    UP_TWILIGHTFOREST_TILE_GIANTLOG(Mods.TwilightForest.ID, "tile.GiantLog", 0),
    // 巨型黑曜石
    UP_TWILIGHTFOREST_TILE_GIANTOBSIDIAN(Mods.TwilightForest.ID, "tile.GiantObsidian", 0),
    // 巨型暮色森林蘑菇
    UP_TWILIGHTFOREST_TILE_HUGEGLOOMBLOCK(Mods.TwilightForest.ID, "tile.HugeGloomBlock", 0),
    // 巨型荷叶
    UP_TWILIGHTFOREST_TILE_HUGELILYPAD(Mods.TwilightForest.ID, "tile.HugeLilyPad", 0),
    // 巨大的茎
    UP_TWILIGHTFOREST_TILE_HUGESTALK(Mods.TwilightForest.ID, "tile.HugeStalk", 0),
    // 极光方块
    UP_TWILIGHTFOREST_TILE_TFAURORABRICK(Mods.TwilightForest.ID, "tile.TFAuroraBrick", 0),
    // 暮色橡树原木
    UP_TWILIGHTFOREST_TILE_TFLOG(Mods.TwilightForest.ID, "tile.TFLog", 0),
    // 时光树的时钟
    UP_TWILIGHTFOREST_TILE_TFMAGICLOGSPECIAL(Mods.TwilightForest.ID, "tile.TFMagicLogSpecial", 0),
    // 时光树树苗（元数据 5）
    UP_TWILIGHTFOREST_TILE_TFSAPLING_M5(Mods.TwilightForest.ID, "tile.TFSapling", 5),
    // 螺旋纹石砖
    UP_TWILIGHTFOREST_TILE_TFSPIRALBRICKS(Mods.TwilightForest.ID, "tile.TFSpiralBricks", 0),
    // 重现方块
    UP_TWILIGHTFOREST_TILE_TFTOWERDEVICE(Mods.TwilightForest.ID, "tile.TFTowerDevice", 0),
    // 消失方块（元数据 2）
    UP_TWILIGHTFOREST_TILE_TFTOWERDEVICE_M2(Mods.TwilightForest.ID, "tile.TFTowerDevice", 2),
    // 飘渺的云
    UP_TWILIGHTFOREST_TILE_WISPYCLOUD(Mods.TwilightForest.ID, "tile.WispyCloud", 0),

    // UniversalSingularities：GT-Not-Leisure 固定外部物品引用
    // 导电铁奇点
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_ENDERIO_SINGULARITY(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 0),
    // 磁钢奇点（元数据 1）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_ENDERIO_SINGULARITY_M1(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 1),
    // 充能合金奇点（元数据 2）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_ENDERIO_SINGULARITY_M2(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 2),
    // 玄钢奇点（元数据 3）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_ENDERIO_SINGULARITY_M3(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 3),
    // 脉动铁奇点（元数据 4）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_ENDERIO_SINGULARITY_M4(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 4),
    // 红石合金奇点（元数据 5）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_ENDERIO_SINGULARITY_M5(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 5),
    // 魂金奇点（元数据 6）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_ENDERIO_SINGULARITY_M6(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 6),
    // 脉冲合金奇点（元数据 7）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_ENDERIO_SINGULARITY_M7(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 7),
    // 不稳定金属奇点
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_EXTRAUTILITIES_SINGULARITY(Mods.UniversalSingularities.ID, "universal.extraUtilities.singularity", 0),
    // 铝奇点
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY(Mods.UniversalSingularities.ID, "universal.general.singularity", 0),
    // 黄铜奇点（元数据 1）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M1(Mods.UniversalSingularities.ID, "universal.general.singularity", 1),
    // 蓝宝石奇点（元数据 10）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M10(Mods.UniversalSingularities.ID, "universal.general.singularity", 10),
    // 钢奇点（元数据 11）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M11(Mods.UniversalSingularities.ID, "universal.general.singularity", 11),
    // 钛奇点（元数据 12）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M12(Mods.UniversalSingularities.ID, "universal.general.singularity", 12),
    // 钨奇点（元数据 13）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M13(Mods.UniversalSingularities.ID, "universal.general.singularity", 13),
    // 铀奇点（元数据 14）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M14(Mods.UniversalSingularities.ID, "universal.general.singularity", 14),
    // 锌奇点（元数据 15）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M15(Mods.UniversalSingularities.ID, "universal.general.singularity", 15),
    // 磷酸三钙奇点（元数据 16）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M16(Mods.UniversalSingularities.ID, "universal.general.singularity", 16),
    // 钯奇点（元数据 17）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M17(Mods.UniversalSingularities.ID, "universal.general.singularity", 17),
    // 大马士革钢奇点（元数据 18）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M18(Mods.UniversalSingularities.ID, "universal.general.singularity", 18),
    // 黑钢奇点（元数据 19）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M19(Mods.UniversalSingularities.ID, "universal.general.singularity", 19),
    // 青铜奇点（元数据 2）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M2(Mods.UniversalSingularities.ID, "universal.general.singularity", 2),
    // 流体琥珀金奇点（元数据 20）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M20(Mods.UniversalSingularities.ID, "universal.general.singularity", 20),
    // 水银奇点（元数据 21）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M21(Mods.UniversalSingularities.ID, "universal.general.singularity", 21),
    // 暗影钢奇点（元数据 22）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M22(Mods.UniversalSingularities.ID, "universal.general.singularity", 22),
    // 铱奇点（元数据 23）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M23(Mods.UniversalSingularities.ID, "universal.general.singularity", 23),
    // 下界之星奇点（元数据 24）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M24(Mods.UniversalSingularities.ID, "universal.general.singularity", 24),
    // 铂奇点（元数据 25）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M25(Mods.UniversalSingularities.ID, "universal.general.singularity", 25),
    // 超能硅岩奇点（元数据 26）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M26(Mods.UniversalSingularities.ID, "universal.general.singularity", 26),
    // 钚奇点（元数据 27）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M27(Mods.UniversalSingularities.ID, "universal.general.singularity", 27),
    // 陨铁奇点（元数据 28）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M28(Mods.UniversalSingularities.ID, "universal.general.singularity", 28),
    // 戴斯奇点（元数据 29）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M29(Mods.UniversalSingularities.ID, "universal.general.singularity", 29),
    // 木炭奇点（元数据 3）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M3(Mods.UniversalSingularities.ID, "universal.general.singularity", 3),
    // 铕奇点（元数据 30）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M30(Mods.UniversalSingularities.ID, "universal.general.singularity", 30),
    // 脉石奇点（元数据 31）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M31(Mods.UniversalSingularities.ID, "universal.general.singularity", 31),
    // 琥珀金奇点（元数据 4）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M4(Mods.UniversalSingularities.ID, "universal.general.singularity", 4),
    // 殷钢奇点（元数据 5）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M5(Mods.UniversalSingularities.ID, "universal.general.singularity", 5),
    // 镁奇点（元数据 6）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M6(Mods.UniversalSingularities.ID, "universal.general.singularity", 6),
    // 锇奇点（元数据 7）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M7(Mods.UniversalSingularities.ID, "universal.general.singularity", 7),
    // 橄榄石奇点（元数据 8）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M8(Mods.UniversalSingularities.ID, "universal.general.singularity", 8),
    // 红宝石奇点（元数据 9）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_GENERAL_SINGULARITY_M9(Mods.UniversalSingularities.ID, "universal.general.singularity", 9),
    // 蓝石奇点
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_PROJECTRED_SINGULARITY(Mods.UniversalSingularities.ID, "universal.projectRed.singularity", 0),
    // 耐酸铝奇点（元数据 1）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_TINKERSCONSTRUCT_SINGULARITY_M1(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 1),
    // 阿迪特奇点（元数据 2）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_TINKERSCONSTRUCT_SINGULARITY_M2(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 2),
    // 钴奇点（元数据 3）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_TINKERSCONSTRUCT_SINGULARITY_M3(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 3),
    // 末影奇点（元数据 4）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_TINKERSCONSTRUCT_SINGULARITY_M4(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 4),
    // 玛玉灵奇点（元数据 6）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_TINKERSCONSTRUCT_SINGULARITY_M6(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 6),
    // 煤炭奇点
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_VANILLA_SINGULARITY(Mods.UniversalSingularities.ID, "universal.vanilla.singularity", 0),
    // 绿宝石奇点（元数据 1）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_VANILLA_SINGULARITY_M1(Mods.UniversalSingularities.ID, "universal.vanilla.singularity", 1),
    // 钻石奇点（元数据 2）
    UP_UNIVERSALSINGULARITIES_UNIVERSAL_VANILLA_SINGULARITY_M2(Mods.UniversalSingularities.ID, "universal.vanilla.singularity", 2),

    // Witchery：GT-Not-Leisure 固定外部物品引用
    // 无限之蛋
    UP_WITCHERY_INFINITYEGG(Mods.Witchery.ID, "infinityegg", 0),
    // 木灰（元数据 18）
    UP_WITCHERY_INGREDIENT_M18(Mods.Witchery.ID, "ingredient", 18),
    // 水心之酿（元数据 96）
    UP_WITCHERY_INGREDIENT_M96(Mods.Witchery.ID, "ingredient", 96),

    // WitchingGadgets：GT-Not-Leisure 固定外部物品引用
    // 奥术计算器（元数据 7）
    UP_WITCHINGGADGETS_ITEM_WG_MATERIAL_M7(Mods.WitchingGadgets.ID, "item.WG_Material", 7);

    private final String modId;
    private final String registryName;
    private final int metadata;
    private final boolean forgeRegistry;
    private final boolean ic2NamedItem;

    Itemlist(String modId, String registryName) {
        this(modId, registryName, 0);
    }

    Itemlist(String modId, String registryName, int metadata) {
        this(modId, registryName, metadata, false, false);
    }

    Itemlist(String modId, String registryName, boolean forgeRegistry) {
        this(modId, registryName, 0, forgeRegistry, false);
    }

    Itemlist(String modId, String registryName, boolean forgeRegistry, boolean ic2NamedItem) {
        this(modId, registryName, 0, forgeRegistry, ic2NamedItem);
    }

    Itemlist(String modId, String registryName, int metadata, boolean forgeRegistry, boolean ic2NamedItem) {
        this.modId = modId;
        this.registryName = registryName;
        this.metadata = metadata;
        this.forgeRegistry = forgeRegistry;
        this.ic2NamedItem = ic2NamedItem;
    }

    /**
     * Resolves the item after registration, preserving the lookup API used by the original call site.
     *
     * @param amount requested stack size
     * @return a new stack, or {@code null} when an optional item is absent
     */
    public ItemStack get(long amount) {
        if (ic2NamedItem) {
            ItemStack stack = ic2.api.item.IC2Items.getItem(registryName);
            if (stack == null) return null;
            ItemStack copy = stack.copy();
            copy.stackSize = (int) amount;
            return copy;
        }
        if (forgeRegistry) return GameRegistry.findItemStack(modId, registryName, (int) amount);
        return GTModHandler.getModItem(modId, registryName, amount, metadata);
    }
}
