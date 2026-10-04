package com.science.gtnl.utils.enums;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;
import ic2.api.item.IC2Items;

public enum ModsItemlist {

    // 量子桥接卡
    AE2FluidCraftQuantumBridgeCard(Mods.AE2FluidCraft.ID, "quantum_bridge_card", true),
    // 能量卡
    AE2FluidCraftEnergyCard(Mods.AE2FluidCraft.ID, "energy_card", true),
    // ME二合一接口（方块）
    AE2FluidCraftBlockFluidInterface(Mods.AE2FluidCraft.ID, "fluid_interface", 0),
    // 256k-ME流体存储组件
    AE2FluidCraft256kFluidStorageComponent(Mods.AE2FluidCraft.ID, "fluid_part", 4),
    // 4096k-ME流体存储组件
    AE2FluidCraft4096kFluidStorageComponent(Mods.AE2FluidCraft.ID, "fluid_part", 6),
    // 16384k-ME流体存储组件
    AE2FluidCraft16384kFluidStorageComponent(Mods.AE2FluidCraft.ID, "fluid_part", 7),
    // ME人造宇宙流体存储元件
    AE2FluidCraftFluidStorageUniverse(Mods.AE2FluidCraft.ID, "fluid_storage.Universe", 0),
    // ME高级多流体存储外壳
    AE2FluidCraftAdvancedFluidStorageHousing(Mods.AE2FluidCraft.ID, "fluid_storage_housing", 3),
    // ME二合一接口（线缆部件）
    AE2FluidCraftPartFluidInterface(Mods.AE2FluidCraft.ID, "part_fluid_interface", 0),
    // ME流体存储总线
    AE2FluidCraftPartFluidStorageBus(Mods.AE2FluidCraft.ID, "part_fluid_storage_bus", 0),

    // 无限增幅卡
    AE2WCTInfinityBoosterCard(Mods.AE2WCT.ID, "infinityBoosterCard", true),

    // 运算压印模板
    AE2CalculationPress(Mods.AppliedEnergistics2.ID, "item.ItemMultiMaterial", 13),
    // 工程压印模板
    AE2EngineeringPress(Mods.AppliedEnergistics2.ID, "item.ItemMultiMaterial", 14),
    // 逻辑压印模板
    AE2LogicPress(Mods.AppliedEnergistics2.ID, "item.ItemMultiMaterial", 15),
    // 硅压印模板
    AE2SiliconPress(Mods.AppliedEnergistics2.ID, "item.ItemMultiMaterial", 19),
    // ME 接口
    AE2MEInterface(Mods.AppliedEnergistics2.ID, "tile.BlockInterface"),

    // 阿卡西记录
    AvaritiaAkashicRecord(Mods.Avaritia.ID, "Akashic_Record", 0),
    // 寰宇肉丸
    AvaritiaCosmicMeatballs(Mods.Avaritia.ID, "Cosmic_Meatballs", 0),
    // 水晶矩阵
    AvaritiaCrystalMatrix(Mods.Avaritia.ID, "Crystal_Matrix", 0),
    // 终望珍珠
    AvaritiaEndestPearl(Mods.Avaritia.ID, "Endest_Pearl", 0),
    // 无尽胸甲
    AvaritiaInfinityChest(Mods.Avaritia.ID, "Infinity_Chest", 0),
    // 无尽头盔
    AvaritiaInfinityHelm(Mods.Avaritia.ID, "Infinity_Helm", 0),
    // 无尽护腿
    AvaritiaInfinityPants(Mods.Avaritia.ID, "Infinity_Pants", 0),
    // 世界崩解之镐
    AvaritiaInfinityPickaxe(Mods.Avaritia.ID, "Infinity_Pickaxe", 0),
    // 无尽靴子
    AvaritiaInfinityShoes(Mods.Avaritia.ID, "Infinity_Shoes", 0),
    // 寰宇支配之剑
    AvaritiaInfinitySword(Mods.Avaritia.ID, "Infinity_Sword", 0),
    // 阿蒙克气血宝珠
    AvaritiaOrbArmok(Mods.Avaritia.ID, "Orb_Armok", 0),
    // 钻石晶格
    AvaritiaDiamondLattice(Mods.Avaritia.ID, "Resource", 0),
    // 水晶矩阵锭
    AvaritiaCrystalMatrixIngot(Mods.Avaritia.ID, "Resource", 1),
    // 无尽催化剂
    AvaritiaInfinityCatalyst(Mods.Avaritia.ID, "Resource", 5),
    // 无尽之锭
    AvaritiaInfinityIngot(Mods.Avaritia.ID, "Resource", 6),
    // 唱片碎片
    AvaritiaRecordFragment(Mods.Avaritia.ID, "Resource", 7),
    // 恒星燃料
    AvaritiaStellarFuel(Mods.Avaritia.ID, "Resource", 8),
    // 铁奇点
    IronSingularity(Mods.Avaritia.ID, "Singularity", 0),
    // 金奇点
    GoldSingularity(Mods.Avaritia.ID, "Singularity", 1),
    // 末影锭奇点
    EnderiumSingularity(Mods.Avaritia.ID, "Singularity", 10),
    // 黏土奇点
    ClaySingularity(Mods.Avaritia.ID, "Singularity", 11),
    // 青金石奇点
    LapisSingularity(Mods.Avaritia.ID, "Singularity", 2),
    // 红石奇点
    RedstoneSingularity(Mods.Avaritia.ID, "Singularity", 3),
    // 下界石英奇点
    QuartzSingularity(Mods.Avaritia.ID, "Singularity", 4),
    // 铜奇点
    CopperSingularity(Mods.Avaritia.ID, "Singularity", 5),
    // 锡奇点
    TinSingularity(Mods.Avaritia.ID, "Singularity", 6),
    // 铅奇点
    LeadSingularity(Mods.Avaritia.ID, "Singularity", 7),
    // 银奇点
    SilverSingularity(Mods.Avaritia.ID, "Singularity", 8),
    // 镍奇点
    NickelSingularity(Mods.Avaritia.ID, "Singularity", 9),
    // 超级煲
    AvaritiaUltimateStew(Mods.Avaritia.ID, "Ultimate_Stew", 0),
    // 物质团解压器
    AvaritiaClusterOpener(Mods.Avaritia.ID, "cluster_opener", 0),

    // 压缩箱子
    AvaritiaAddonsCompressedChest(Mods.AvaritiaAddons.ID, "CompressedChest", 0),
    // 梦魇工作台
    AvaritiaAddonsExtremeAutoCrafter(Mods.AvaritiaAddons.ID, "ExtremeAutoCrafter", 0),
    // 无尽箱子
    AvaritiaAddonsInfinityChest(Mods.AvaritiaAddons.ID, "InfinityChest", 0),

    // 松树树叶
    BiomesOPlentyPineLeaves(Mods.BiomesOPlenty.ID, "colorizedLeaves2", 1),
    // 松树树苗
    BiomesOPlentyPineSapling(Mods.BiomesOPlenty.ID, "colorizedSaplings", 5),
    // 粉珊瑚
    BiomesOPlentyPinkCoral(Mods.BiomesOPlenty.ID, "coral1", 12),
    // 橙珊瑚
    BiomesOPlentyOrangeCoral(Mods.BiomesOPlenty.ID, "coral1", 13),
    // 蓝珊瑚
    BiomesOPlentyBlueCoral(Mods.BiomesOPlenty.ID, "coral1", 14),
    // 夜光珊瑚
    BiomesOPlentyGlowingCoral(Mods.BiomesOPlenty.ID, "coral1", 15),
    // 大型睡莲
    BiomesOPlentyLilyBop(Mods.BiomesOPlenty.ID, "lilyBop", 0),
    // 中型睡莲
    BiomesOPlentyMediumLilyPad(Mods.BiomesOPlenty.ID, "lilyBop", 1),
    // 小型睡莲
    BiomesOPlentySmallLilyPad(Mods.BiomesOPlenty.ID, "lilyBop", 2),
    // 松树原木
    BiomesOPlentyLogs4(Mods.BiomesOPlenty.ID, "logs4", 0),
    // 松果
    BiomesOPlentyPineCone(Mods.BiomesOPlenty.ID, "misc", 13),

    // 血之TNT
    BloodArsenalBloodTnt(Mods.BloodArsenal.ID, "blood_tnt", 0),
    // 生命注入器
    BloodArsenalLifeInfuser(Mods.BloodArsenal.ID, "life_infuser", 0),
    // 生命能量具现器
    BloodArsenalLpMaterializer(Mods.BloodArsenal.ID, "lp_materializer", 0),

    // 血之祭坛
    BloodMagicAltar(Mods.BloodMagic.ID, "Altar", 0),
    // [觉醒]激活水晶
    BloodMagicAwakenedActivationCrystal(Mods.BloodMagic.ID, "activationCrystal", 1),
    // 学徒气血宝珠
    BloodMagicApprenticeBloodOrb(Mods.BloodMagic.ID, "apprenticeBloodOrb", 0),
    // 贤者气血宝珠
    BloodMagicArchmageBloodOrb(Mods.BloodMagic.ID, "archmageBloodOrb", 0),
    // 炼金术台
    BloodMagicBlockWritingTable(Mods.BloodMagic.ID, "blockWritingTable", 0),
    // 测试宝珠
    BloodMagicCreativeFiller(Mods.BloodMagic.ID, "creativeFiller", 0),
    // 献祭刀
    BloodMagicDaggerOfSacrifice(Mods.BloodMagic.ID, "daggerOfSacrifice", 0),
    // 仪式推测杖
    BloodMagicRitualDiviner(Mods.BloodMagic.ID, "itemRitualDiviner", 2),
    // 法师气血宝珠
    BloodMagicMagicianBloodOrb(Mods.BloodMagic.ID, "magicianBloodOrb", 0),
    // 导师气血宝珠
    BloodMagicMasterBloodOrb(Mods.BloodMagic.ID, "masterBloodOrb", 0),
    // 主仪式石
    BloodMagicMasterStone(Mods.BloodMagic.ID, "masterStone", 0),
    // 卓越气血宝珠
    BloodMagicTranscendentBloodOrb(Mods.BloodMagic.ID, "transcendentBloodOrb", 0),
    // 虚弱气血宝珠
    BloodMagicWeakBloodOrb(Mods.BloodMagic.ID, "weakBloodOrb", 0),

    // 魔力钢锭
    BotaniaManasteelIngot(Mods.Botania.ID, "manaResource", 0),
    // 魔力珍珠
    BotaniaManaPearl(Mods.Botania.ID, "manaResource", 1),
    // 魔力钻石
    BotaniaManaDiamond(Mods.Botania.ID, "manaResource", 2),
    // 炼金催化器
    BotaniaAlchemyCatalyst(Mods.Botania.ID, "alchemyCatalyst", 0),
    // 精灵门核心
    BotaniaAlfheimPortal(Mods.Botania.ID, "alfheimPortal", 0),
    // 艾琳的意志
    BotaniaAncientWill(Mods.Botania.ID, "ancientWill", 0),
    // 达洛克的意志
    BotaniaWillOfDharok(Mods.Botania.ID, "ancientWill", 1),
    // 古赞的意志
    BotaniaWillOfGuthan(Mods.Botania.ID, "ancientWill", 2),
    // 托拉格的意志
    BotaniaWillOfTorag(Mods.Botania.ID, "ancientWill", 3),
    // 威拉克的意志
    BotaniaWillOfVerac(Mods.Botania.ID, "ancientWill", 4),
    // 卡瑞的意志
    BotaniaWillOfKaril(Mods.Botania.ID, "ancientWill", 5),
    // 彩虹桥方块
    BotaniaBifrostPerm(Mods.Botania.ID, "bifrostPerm", 0),
    // 彩虹玻璃板
    BotaniaBifrostPermPane(Mods.Botania.ID, "bifrostPermPane", 0),
    // 黑莲花
    BotaniaBlackLotus(Mods.Botania.ID, "blackLotus", 0),
    // 暗黑莲花
    BotaniaBlackestLotus(Mods.Botania.ID, "blackLotus", 1),
    // 炼造催化器
    BotaniaConjurationCatalyst(Mods.Botania.ID, "conjurationCatalyst", 0),
    // 多媒体火花
    BotaniaCorporeaSpark(Mods.Botania.ID, "corporeaSpark", 0),
    // 主媒体火花
    BotaniaMasterCorporeaSpark(Mods.Botania.ID, "corporeaSpark", 1),
    // 命运骰子
    BotaniaDice(Mods.Botania.ID, "dice", 0),
    // 精灵玻璃
    BotaniaElfGlass(Mods.Botania.ID, "elfGlass", 0),
    // 盖亚守护者的头
    BotaniaGaiaHead(Mods.Botania.ID, "gaiaHead", 0),
    // 魔力透镜：传送
    BotaniaWarpLens(Mods.Botania.ID, "lens", 18),
    // 魔力透镜：反射
    BotaniaBounceLens(Mods.Botania.ID, "lens", 5),
    // 植物魔法辞典
    BotaniaLexicon(Mods.Botania.ID, "lexicon", 0),
    // 磁化指环
    BotaniaMagnetRing(Mods.Botania.ID, "magnetRing", 0),
    // 不稳定信标
    BotaniaManaBeacon(Mods.Botania.ID, "manaBeacon", 0),
    // 盖亚魂锭
    BotaniaGaiaSpiritIngot(Mods.Botania.ID, "manaResource", 14),
    // 瓶装末地空气
    BotaniaEnderAirBottle(Mods.Botania.ID, "manaResource", 15),
    // 泰拉钢锭
    BotaniaTerrasteelIngot(Mods.Botania.ID, "manaResource", 4),
    // 盖亚之魂
    BotaniaGaiaSpirit(Mods.Botania.ID, "manaResource", 5),
    // 源质钢锭
    BotaniaElementiumIngot(Mods.Botania.ID, "manaResource", 7),
    // 精灵尘
    BotaniaPixieDust(Mods.Botania.ID, "manaResource", 8),
    // 龙石
    BotaniaDragonstone(Mods.Botania.ID, "manaResource", 9),
    // 增生之种
    BotaniaOvergrowthSeed(Mods.Botania.ID, "overgrowthSeed", 0),
    // 增生之种
    BotaniaOvergrowthSeedDamage3(Mods.Botania.ID, "overgrowthSeed", 3),
    // 粉色手炮
    BotaniaPinkinator(Mods.Botania.ID, "pinkinator", 0),
    // 力量传递器
    BotaniaPistonRelay(Mods.Botania.ID, "pistonRelay", 0),
    // 永恒魔力池
    BotaniaEverlastingManaPool(Mods.Botania.ID, "pool", 1),
    // 神话魔力池
    BotaniaFabulousManaPool(Mods.Botania.ID, "pool", 3),
    // 魔力泵
    BotaniaPump(Mods.Botania.ID, "pump", 0),
    // 魔法水晶
    BotaniaManaPylon(Mods.Botania.ID, "pylon", 0),
    // 自然水晶
    BotaniaNaturaPylon(Mods.Botania.ID, "pylon", 1),
    // 盖亚水晶
    BotaniaGaiaPylon(Mods.Botania.ID, "pylon", 2),
    // 触物指环
    BotaniaReachRing(Mods.Botania.ID, "reachRing", 0),
    // 破损的唱片
    BotaniaRecordGaia2(Mods.Botania.ID, "recordGaia2", 0),
    // 魔力转换器
    BotaniaRfGenerator(Mods.Botania.ID, "rfGenerator", 0),
    // 水之符文
    BotaniaWaterRune(Mods.Botania.ID, "rune", 0),
    // 火之符文
    BotaniaFireRune(Mods.Botania.ID, "rune", 1),
    // 暴食符文
    BotaniaGluttonyRune(Mods.Botania.ID, "rune", 10),
    // 贪婪符文
    BotaniaGreedRune(Mods.Botania.ID, "rune", 11),
    // 懒惰符文
    BotaniaSlothRune(Mods.Botania.ID, "rune", 12),
    // 暴怒符文
    BotaniaWrathRune(Mods.Botania.ID, "rune", 13),
    // 嫉妒符文
    BotaniaEnvyRune(Mods.Botania.ID, "rune", 14),
    // 傲慢符文
    BotaniaPrideRune(Mods.Botania.ID, "rune", 15),
    // 地之符文
    BotaniaEarthRune(Mods.Botania.ID, "rune", 2),
    // 风之符文
    BotaniaAirRune(Mods.Botania.ID, "rune", 3),
    // 春之符文
    BotaniaSpringRune(Mods.Botania.ID, "rune", 4),
    // 夏之符文
    BotaniaSummerRune(Mods.Botania.ID, "rune", 5),
    // 秋之符文
    BotaniaAutumnRune(Mods.Botania.ID, "rune", 6),
    // 冬之符文
    BotaniaWinterRune(Mods.Botania.ID, "rune", 7),
    // 魔力符文
    BotaniaManaRune(Mods.Botania.ID, "rune", 8),
    // 欲望符文
    BotaniaLustRune(Mods.Botania.ID, "rune", 9),
    // 符文祭坛
    BotaniaRuneAltar(Mods.Botania.ID, "runeAltar", 0),
    // 火花
    BotaniaSpark(Mods.Botania.ID, "spark", 0),
    // 植物魔法花
    BotaniaSpecialFlower(Mods.Botania.ID, "specialFlower", 0),
    // 盖亚魔力发射器
    BotaniaGaiaManaSpreader(Mods.Botania.ID, "spreader", 3),
    // 安山岩
    BotaniaAndesite(Mods.Botania.ID, "stone", 0),
    // 玄武岩
    BotaniaBasalt(Mods.Botania.ID, "stone", 1),
    // 闪长岩
    BotaniaDiorite(Mods.Botania.ID, "stone", 2),
    // 花岗岩
    BotaniaGranite(Mods.Botania.ID, "stone", 3),
    // 魔力钢块
    BotaniaStorage(Mods.Botania.ID, "storage", 0),
    // 泰拉粉碎者
    BotaniaTerraPick(Mods.Botania.ID, "terraPick", 0),
    // 泰拉凝聚板
    BotaniaTerraPlate(Mods.Botania.ID, "terraPlate", 0),

    // 储罐
    BuildCraftFactoryTankBlock(Mods.BuildCraftFactory.ID, "tankBlock", 0),

    // 钻石凿子
    ChiselDiamondChisel(Mods.Chisel.ID, "diamondChisel", 0),

    // 创造模式内存
    ComputronicsOCSpecialParts(Mods.Computronics.ID, "computronics.ocSpecialParts", 0),

    // 觉醒核心
    DraconicEvolutionAwakenedCore(Mods.DraconicEvolution.ID, "awakenedCore", 0),
    // 小的混沌残片
    DraconicEvolutionSmallChaosFragment(Mods.DraconicEvolution.ID, "chaosFragment", 1),
    // 混沌碎片
    DraconicEvolutionChaosShard(Mods.DraconicEvolution.ID, "chaosShard", 0),
    // 混沌核心
    DraconicEvolutionChaoticCore(Mods.DraconicEvolution.ID, "chaoticCore", 0),
    // Dezil的棉花糖
    DraconicEvolutionDezilsMarshmallow(Mods.DraconicEvolution.ID, "dezilsMarshmallow", 0),
    // 龙芯
    DraconicEvolutionDraconicCore(Mods.DraconicEvolution.ID, "draconicCore", 0),
    // 龙块
    DraconicEvolutionDraconium(Mods.DraconicEvolution.ID, "draconium", 0),
    // 充能龙块
    DraconicEvolutionChargedDraconiumBlock(Mods.DraconicEvolution.ID, "draconium", 2),
    // 龙之心
    DraconicEvolutionDragonHeart(Mods.DraconicEvolution.ID, "dragonHeart", 0),
    // 物品错位器
    DraconicEvolutionWyvernItemDislocator(Mods.DraconicEvolution.ID, "magnet", 0),
    // 觉醒物品错位器
    DraconicEvolutionAwakenedItemDislocator(Mods.DraconicEvolution.ID, "magnet", 1),
    // 反应堆核心
    DraconicEvolutionReactorCore(Mods.DraconicEvolution.ID, "reactorCore", 0),
    // 高级错位宝石
    DraconicEvolutionTeleporterMKII(Mods.DraconicEvolution.ID, "teleporterMKII", 0),

    // 混沌注魔八重压缩太阳能
    EMTChaosInfusedOctupleSolarPanel(Mods.ElectroMagicTools.ID, "EMTSolars4", 14),
    // 风注魔八重压缩太阳能
    EMTAirInfusedOctupleSolarPanel(Mods.ElectroMagicTools.ID, "EMTSolars4", 15),
    // 水注魔八重压缩太阳能
    EMTWaterInfusedOctupleSolarPanel(Mods.ElectroMagicTools.ID, "EMTSolars5", 1),
    // 火注魔八重压缩太阳能
    EMTFireInfusedOctupleSolarPanel(Mods.ElectroMagicTools.ID, "EMTSolars5", 2),
    // 能量源质发电机
    ElectroMagicToolsEssentiaGenerators(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 0),
    // 火之源质发电机
    EMTFireEssentiaGenerator(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 1),
    // 灵气源质发电机
    EMTVitalEssentiaGenerator(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 2),
    // 木之源质发电机
    EMTWoodEssentiaGenerator(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 3),
    // 风之源质发电机
    EMTAirEssentiaGenerator(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 4),
    // 贪婪源质发电机
    EMTGreedEssentiaGenerator(Mods.ElectroMagicTools.ID, "EssentiaGenerators", 5),

    // 电容库
    EnderIOBlockCapBank(Mods.EnderIO.ID, "blockCapBank", 0),
    // 玄钢砧
    EnderIOBlockDarkSteelAnvil(Mods.EnderIO.ID, "blockDarkSteelAnvil", 0),
    // 末影人头
    EnderIOBlockEndermanSkull(Mods.EnderIO.ID, "blockEndermanSkull", 0),
    // 禁锢末影人头
    EnderIOTormentedEndermanHead(Mods.EnderIO.ID, "blockEndermanSkull", 2),
    // 种植站
    EnderIOBlockFarmStation(Mods.EnderIO.ID, "blockFarmStation", 0),
    // 红石合金块
    EnderIORedstoneAlloyBlock(Mods.EnderIO.ID, "blockIngotStorage", 3),
    // 玄钢块
    EnderIODarkSteelBlock(Mods.EnderIO.ID, "blockIngotStorage", 6),
    // 电动刷怪笼
    EnderIOBlockPoweredSpawner(Mods.EnderIO.ID, "blockPoweredSpawner", 0),
    // 光伏板
    EnderIOBlockSolarPanel(Mods.EnderIO.ID, "blockSolarPanel", 0),
    // 高级光伏板
    EnderIOAdvancedPhotovoltaicCell(Mods.EnderIO.ID, "blockSolarPanel", 1),
    // 脉冲光伏板
    EnderIOVibrantPhotovoltaicCell(Mods.EnderIO.ID, "blockSolarPanel", 2),
    // 电容
    EnderIOItemBasicCapacitor(Mods.EnderIO.ID, "itemBasicCapacitor", 0),
    // 僵尸电极
    EnderIOItemFrankenSkull(Mods.EnderIO.ID, "itemFrankenSkull", 0),
    // Z-逻辑控制器
    EnderIOZLogicController(Mods.EnderIO.ID, "itemFrankenSkull", 1),
    // 人造僵尸
    EnderIOZombieElectrode(Mods.EnderIO.ID, "itemFrankenSkull", 2),
    // 末影谐振器
    EnderIOEnderResonator(Mods.EnderIO.ID, "itemFrankenSkull", 3),
    // 意识末影谐振器
    EnderIOSentientEnderResonator(Mods.EnderIO.ID, "itemFrankenSkull", 4),
    // 守卫者二极管
    EnderIOGuardianDiode(Mods.EnderIO.ID, "itemFrankenSkull", 6),
    // 预知晶体
    EnderIOPrescientCrystal(Mods.EnderIO.ID, "itemMaterial", 13),
    // 脉冲晶体粉
    EnderIOVibrantCrystalPowder(Mods.EnderIO.ID, "itemMaterial", 14),
    // 末影晶体粉
    EnderIOEnderCrystalPowder(Mods.EnderIO.ID, "itemMaterial", 16),
    // 预知晶体粉
    EnderIOPrescientCrystalPowder(Mods.EnderIO.ID, "itemMaterial", 17),
    // 脉冲晶体
    EnderIOVibrantCrystal(Mods.EnderIO.ID, "itemMaterial", 5),
    // 末影晶体
    EnderIOEnderCrystal(Mods.EnderIO.ID, "itemMaterial", 8),
    // 诱引晶体
    EnderIOAttractorCrystal(Mods.EnderIO.ID, "itemMaterial", 9),
    // 能量导管
    EnderIOItemPowerConduit(Mods.EnderIO.ID, "itemPowerConduit", 0),

    // 末影箱子
    EnderStorageEnderChest(Mods.EnderStorage.ID, "enderChest", 0),
    // 末影蓄水槽（末影储罐）
    EnderStorageEnderTank(Mods.EnderStorage.ID, "enderChest", 1),

    // 闪瞬奇点
    EternalSingularityCombined(Mods.EternalSingularity.ID, "combined_singularity", 0),
    // 圣灵奇点
    HolySingularity(Mods.EternalSingularity.ID, "combined_singularity", 1),
    // 静空奇点
    VoidSingularity(Mods.EternalSingularity.ID, "combined_singularity", 15),
    // 意面奇点
    SpaghettiSingularity(Mods.EternalSingularity.ID, "combined_singularity", 2),
    // 大气奇点
    AtmosphericSingularity(Mods.EternalSingularity.ID, "combined_singularity", 3),
    // 神秘奇点
    MysticSingularity(Mods.EternalSingularity.ID, "combined_singularity", 4),
    // 史诗奇点
    EpicSingularity(Mods.EternalSingularity.ID, "combined_singularity", 5),
    // 星耀奇点
    AstralSingularity(Mods.EternalSingularity.ID, "combined_singularity", 6),
    // 永恒奇点
    EternalSingularity(Mods.EternalSingularity.ID, "eternal_singularity", 0),

    // 黑石
    EtFuturumRequiemBlackstone(Mods.EtFuturumRequiem.ID, "blackstone", 0),
    // 蓝冰
    EtFuturumRequiemBlueIce(Mods.EtFuturumRequiem.ID, "blue_ice", 0),
    // 深板岩圆石
    EtFuturumRequiemCobbledDeepslate(Mods.EtFuturumRequiem.ID, "cobbled_deepslate", 0),
    // 深板岩
    EtFuturumRequiemDeepslate(Mods.EtFuturumRequiem.ID, "deepslate", 0),
    // 鞘翅
    EtFuturumRequiemElytra(Mods.EtFuturumRequiem.ID, "elytra", 0),
    // 岩浆块
    EtFuturumRequiemMagma(Mods.EtFuturumRequiem.ID, "magma", 0),
    // 下界合金斧
    EtFuturumRequiemNetheriteAxe(Mods.EtFuturumRequiem.ID, "netherite_axe", 0),
    // 下界合金靴子
    EtFuturumRequiemNetheriteBoots(Mods.EtFuturumRequiem.ID, "netherite_boots", 0),
    // 下界合金胸甲
    EtFuturumRequiemNetheriteChestplate(Mods.EtFuturumRequiem.ID, "netherite_chestplate", 0),
    // 下界合金头盔
    EtFuturumRequiemNetheriteHelmet(Mods.EtFuturumRequiem.ID, "netherite_helmet", 0),
    // 下界合金锄
    EtFuturumRequiemNetheriteHoe(Mods.EtFuturumRequiem.ID, "netherite_hoe", 0),
    // 下界合金护腿
    EtFuturumRequiemNetheriteLeggings(Mods.EtFuturumRequiem.ID, "netherite_leggings", 0),
    // 下界合金镐
    EtFuturumRequiemNetheritePickaxe(Mods.EtFuturumRequiem.ID, "netherite_pickaxe", 0),
    // 下界合金碎片
    EtFuturumRequiemNetheriteScrap(Mods.EtFuturumRequiem.ID, "netherite_scrap", 0),
    // 下界合金锹
    EtFuturumRequiemNetheriteSpade(Mods.EtFuturumRequiem.ID, "netherite_spade", 0),
    // 下界合金剑
    EtFuturumRequiemNetheriteSword(Mods.EtFuturumRequiem.ID, "netherite_sword", 0),
    // 潜影壳
    EtFuturumRequiemShulkerShell(Mods.EtFuturumRequiem.ID, "shulker_shell", 0),
    // 黏液块
    EtFuturumRequiemSlime(Mods.EtFuturumRequiem.ID, "slime", 0),
    // 灵魂火把
    EtFuturumRequiemSoulTorch(Mods.EtFuturumRequiem.ID, "soul_torch", 0),
    // 海绵
    EtFuturumRequiemSponge(Mods.EtFuturumRequiem.ID, "sponge", 0),
    // 湿海绵
    EtFuturumWetSponge(Mods.EtFuturumRequiem.ID, "sponge", 1),
    // 不死图腾
    EtFuturumRequiemTotemOfUndying(Mods.EtFuturumRequiem.ID, "totem_of_undying", 0),

    // 七重压缩圆石
    ExtraUtilitiesCompressedCobbleSeven(Mods.ExtraUtilities.ID, "cobblestone_compressed", 6),
    // 八重压缩圆石
    ExtraUtilitiesCompressedCobbleEight(Mods.ExtraUtilities.ID, "cobblestone_compressed", 7),
    // 漆黑之门
    ExtraUtilitiesDarkPortal(Mods.ExtraUtilities.ID, "dark_portal", 0),
    // 不稳定金属方块
    ExtraUtilitiesUnstableIngotBlock(Mods.ExtraUtilities.ID, "decorativeBlock1", 5),
    // 荧石玻璃
    ExtraUtilitiesGlowstoneGlass(Mods.ExtraUtilities.ID, "decorativeBlock2", 7),
    // 钻石锥刺
    ExtraUtilitiesSpikeBaseDiamond(Mods.ExtraUtilities.ID, "spike_base_diamond", 0),
    // 垃圾桶（流体）
    ExtraUtilitiesFluidTrashCan(Mods.ExtraUtilities.ID, "trashcan", 1),
    // 不稳定金属锭
    ExtraUtilitiesUnstableingot(Mods.ExtraUtilities.ID, "unstableingot", 0),

    // 邪术气血宝珠
    ForbiddenMagicEldritchOrb(Mods.ForbiddenMagic.ID, "EldritchOrb", 0),

    // 蜂箱组组件
    ForestryAlveary(Mods.Forestry.ID, "alveary", 0),
    // 蜂箱组克隆盒
    ForestryAlvearySwarmer(Mods.Forestry.ID, "alveary", 2),
    // 蜂箱组稳定器
    ForestryAlvearyStabilizer(Mods.Forestry.ID, "alveary", 6),
    // 蜂蜡
    ForestryBeeswax(Mods.Forestry.ID, "beeswax", 0),
    // 农场齿轮箱
    ForestryFarmGearbox(Mods.Forestry.ID, "ffarm", 2),
    // 农场水阀
    ForestryFarmValve(Mods.Forestry.ID, "ffarm", 4),
    // 农场控制盒
    ForestryFarmControl(Mods.Forestry.ID, "ffarm", 5),
    // 树叶
    ForestryLeaves(Mods.Forestry.ID, "leaves", 0),
    // 松树原木
    ForestryPineLog(Mods.Forestry.ID, "logs", 20),
    // 花粉
    ForestryPollen(Mods.Forestry.ID, "pollen", 0),
    // 蜂王浆
    ForestryRoyalJelly(Mods.Forestry.ID, "royalJelly", 0),

    // 天域使魔
    GadomancyItemEtherealFamiliar(Mods.Gadomancy.ID, "ItemEtherealFamiliar", 0),

    // 轻质合金板
    AmunRaLightweightAlloyPlate(Mods.GalacticraftAmunRa.ID, "item.baseItem", 15),
    // 暗物质碎片
    AmunRaDarkMatterFragment(Mods.GalacticraftAmunRa.ID, "item.baseItem", 26),
    // 穿梭机图纸
    AmunRaItemSchematic(Mods.GalacticraftAmunRa.ID, "item.schematic", 0),
    // 暗物质
    AmunRaDarkMatter(Mods.GalacticraftAmunRa.ID, "tile.baseBlockRock", 14),

    // 高级晶圆
    GalacticraftAdvancedWafer(Mods.GalacticraftCore.ID, "item.basicItem", 14),
    // 无限氧气罐
    GalacticraftCoreItemInfiniteOxygen(Mods.GalacticraftCore.ID, "item.infiniteOxygen", 0),
    // 2阶火箭图纸
    GalacticraftTier2RocketSchematic(Mods.GalacticraftCore.ID, "item.schematic", 1),
    // 1阶火箭
    GalacticraftCoreItemSpaceship(Mods.GalacticraftCore.ID, "item.spaceship", 0),
    // NASA工作台
    GalacticraftCoreTileRocketWorkbench(Mods.GalacticraftCore.ID, "tile.rocketWorkbench", 0),

    // 3阶火箭
    GalacticraftMarsItemItemTier3Rocket(Mods.GalacticraftMars.ID, "item.itemTier3Rocket", 0),
    // 3阶火箭图纸
    GalacticraftMarsItemSchematic(Mods.GalacticraftMars.ID, "item.schematic", 0),
    // 2阶火箭
    GalacticraftMarsItemSpaceshipTier2(Mods.GalacticraftMars.ID, "item.spaceshipTier2", 0),

    // 巴纳德C树木原木
    GalaxySpaceBarnardaClog(Mods.GalaxySpace.ID, "barnardaClog", 0),
    // 4阶火箭控制电脑
    GalaxySpaceRocketControlComputerTier4(Mods.GalaxySpace.ID, "item.RocketControlComputer", 4),
    // 7阶火箭控制电脑
    GalaxySpaceRocketControlComputerTier7(Mods.GalaxySpace.ID, "item.RocketControlComputer", 7),
    // 4阶火箭图纸
    GalaxySpaceTier4RocketSchematic(Mods.GalaxySpace.ID, "item.SchematicTier4", 0),
    // 5阶火箭图纸
    GalaxySpaceTier5RocketSchematic(Mods.GalaxySpace.ID, "item.SchematicTier5", 0),
    // 6阶火箭图纸
    GalaxySpaceTier6RocketSchematic(Mods.GalaxySpace.ID, "item.SchematicTier6", 0),
    // 7阶火箭图纸
    GalaxySpaceTier7RocketSchematic(Mods.GalaxySpace.ID, "item.SchematicTier7", 0),
    // 8阶火箭图纸
    GalaxySpaceTier8RocketSchematic(Mods.GalaxySpace.ID, "item.SchematicTier8", 0),
    // 4阶火箭
    GalaxySpaceItemTier4Rocket(Mods.GalaxySpace.ID, "item.Tier4Rocket", 0),
    // 5阶火箭
    GalaxySpaceItemTier5Rocket(Mods.GalaxySpace.ID, "item.Tier5Rocket", 0),
    // 6阶火箭
    GalaxySpaceItemTier6Rocket(Mods.GalaxySpace.ID, "item.Tier6Rocket", 0),
    // 7阶火箭
    GalaxySpaceItemTier7Rocket(Mods.GalaxySpace.ID, "item.Tier7Rocket", 0),
    // 8阶火箭
    GalaxySpaceItemTier8Rocket(Mods.GalaxySpace.ID, "item.Tier8Rocket", 0),
    // 鲸鱼座T星E藻类（形态一）
    GalaxySpaceCetiESeaweedFormI(Mods.GalaxySpace.ID, "tcetiedandelions", 0),
    // 鲸鱼座T星E藻类（形态二）
    GalaxySpaceCetiESeaweedFormII(Mods.GalaxySpace.ID, "tcetiedandelions", 1),
    // 鲸鱼座T星E藻类（形态三）
    GalaxySpaceCetiESeaweedFormIII(Mods.GalaxySpace.ID, "tcetiedandelions", 2),
    // 鲸鱼座T星E藻类（形态四）
    GalaxySpaceCetiESeaweedFormIV(Mods.GalaxySpace.ID, "tcetiedandelions", 3),
    // 鲸鱼座T星E藻类（形态五）
    GalaxySpaceCetiESeaweedFormV(Mods.GalaxySpace.ID, "tcetiedandelions", 4),
    // 鲸鱼座T星E藻类（形态六）
    GalaxySpaceCetiESeaweedFormVI(Mods.GalaxySpace.ID, "tcetiedandelions", 5),

    // 防辐射板
    GoodGeneratorRadiationProtectionPlate(Mods.GoodGenerator.ID, "radiationProtectionPlate"),

    // 喷射引擎
    GraviSuiteJetEngine(Mods.GraviSuite.ID, "itemSimpleItem", 6),

    // 脱水线圈 []
    GTPlusPlusDehydratorCoil(Mods.GTPlusPlus.ID, "itemDehydratorCoil", 3),
    // 能量核心 [UHV]
    GTPlusPlusItemItemBufferCore10(Mods.GTPlusPlus.ID, "item.itemBufferCore10", 0),

    // 末影粉末
    HardcoreEnderExpansionEndPowder(Mods.HardcoreEnderExpansion.ID, "end_powder", 0),
    // 末影粉末矿石
    HardcoreEnderExpansionEndPowderOre(Mods.HardcoreEnderExpansion.ID, "end_powder_ore", 0),

    // 低压变压器
    IC2LVTransformer(Mods.IndustrialCraft2.ID, "blockElectric", 3),
    // 中压变压器
    IC2MVTransformer(Mods.IndustrialCraft2.ID, "blockElectric", 4),
    // 高压变压器
    IC2HVTransformer(Mods.IndustrialCraft2.ID, "blockElectric", 5),
    // 超高压变压器
    IC2EVTransformer(Mods.IndustrialCraft2.ID, "blockElectric", 6),
    // 合金锭
    IC2MixedMetalIngot(Mods.IndustrialCraft2.ID, "mixedMetalIngot", false, true),
    // 核反应堆
    IC2NuclearReactor(Mods.IndustrialCraft2.ID, "blockGenerator", 5),
    // 工业TNT
    IC2BlockITNT(Mods.IndustrialCraft2.ID, "blockITNT", 0),
    // 铁炉
    IC2IronFurnace(Mods.IndustrialCraft2.ID, "blockMachine", 1),
    // 核弹
    IC2BlockNuke(Mods.IndustrialCraft2.ID, "blockNuke", 0),
    // 核反应仓
    IC2ReactorChamber(Mods.IndustrialCraft2.ID, "blockReactorChamber", 0),
    // 任意损伤值的核反应仓
    IC2ReactorChamberAnyDamage(Mods.IndustrialCraft2.ID, "blockReactorChamber", OreDictionary.WILDCARD_VALUE),
    // 橡胶树原木
    IC2BlockRubWood(Mods.IndustrialCraft2.ID, "blockRubWood", 0),
    // 能量水晶
    IC2EnergyCrystal(Mods.IndustrialCraft2.ID, "itemBatCrystal", 26),
    // 兰波顿水晶
    IC2LapotronCrystal(Mods.IndustrialCraft2.ID, "itemBatLamaCrystal", 26),
    // 空单元
    IC2EmptyCell(Mods.IndustrialCraft2.ID, "itemCellEmpty", 13),
    // 粘性树脂
    IC2ItemHarz(Mods.IndustrialCraft2.ID, "itemHarz", 0),
    // 生碳纤维
    IC2ItemPartCarbonFibre(Mods.IndustrialCraft2.ID, "itemPartCarbonFibre", 0),
    // 电路板
    IC2ItemPartCircuit(Mods.IndustrialCraft2.ID, "itemPartCircuit", 0),
    // 高级电路板
    IC2ItemPartCircuitAdv(Mods.IndustrialCraft2.ID, "itemPartCircuitAdv", 0),
    // 加厚中子反射板
    IC2ThickNeutronReflector(Mods.IndustrialCraft2.ID, "reactorReflectorThick", 1),

    // 铁箱子
    IronChestsBlockIronChest(Mods.IronChests.ID, "BlockIronChest", 0),
    // 金箱子
    IronChestsGoldChest(Mods.IronChests.ID, "BlockIronChest", 1),
    // 钻石箱子
    IronChestsDiamondChest(Mods.IronChests.ID, "BlockIronChest", 2),
    // 铜箱子
    IronChestsCopperChest(Mods.IronChests.ID, "BlockIronChest", 3),
    // 钢箱子
    IronChestsSteelChest(Mods.IronChests.ID, "BlockIronChest", 4),
    // 水晶箱子
    IronChestsCrystalChest(Mods.IronChests.ID, "BlockIronChest", 5),
    // 黑曜石箱子
    IronChestsObsidianChest(Mods.IronChests.ID, "BlockIronChest", 6),
    // 下界合金箱子
    IronChestsNetheriteChest(Mods.IronChests.ID, "BlockIronChest", 8),
    // 玄钢箱子
    IronChestsDarkSteelChest(Mods.IronChests.ID, "BlockIronChest", 9),

    // 钻石储罐
    IronTanksDiamondTank(Mods.IronTanks.ID, "diamondTank", 0),

    // 寻矿魔杖
    IWillFindYouIfuBuildingKit(Mods.IWillFindYou.ID, "ifu_buildingKit", 0),

    // 兰波顿机械方块/电容
    KekzTechLapotronicEnergyUnitBlock(Mods.KekzTech.ID, "kekztech_lapotronicenergyunit_block", 0),

    // 穿刺箭
    BattleGearPiercingArrow(Mods.MineAndBladeBattleGear2.ID, "mb.arrow", 3),

    // 黑云
    NaturaBlackCloud(Mods.Natura.ID, "Cloud", 1),
    // 灰云
    NaturaGrayCloud(Mods.Natura.ID, "Cloud", 2),
    // 硫云
    NaturaSulfurCloud(Mods.Natura.ID, "Cloud", 3),

    // 强化玻璃透镜
    NHCoreModReinforcedGlassLens(Mods.NewHorizonsCoreMod.ID, "ReinforcedGlassLense"),
    // 神秘水晶透镜
    NHCoreModMysteriousCrystalLens(Mods.NewHorizonsCoreMod.ID, "MysteriousCrystalLens"),
    // 拉多克斯聚合物透镜
    NHCoreModRadoxPolymerLens(Mods.NewHorizonsCoreMod.ID, "RadoxPolymerLens"),
    // 彩色透镜
    NHCoreModChromaticLens(Mods.NewHorizonsCoreMod.ID, "ChromaticLens"),
    // 琼脂
    NHCoreModAgar(Mods.NewHorizonsCoreMod.ID, "GTNHBioItems", 2),

    // 电梯
    OpenBlocksElevator(Mods.OpenBlocks.ID, "elevator", 0),
    // 海绵
    OpenBlocksSponge(Mods.OpenBlocks.ID, "sponge", 0),

    // T3加速处理器（APU）
    OpenComputersTier3APU(Mods.OpenComputers.ID, "item", 103),
    // 创造模式组件总线
    OpenComputersCreativeComponentBus(Mods.OpenComputers.ID, "item", 114),
    // T4服务器
    OpenComputersTier4Server(Mods.OpenComputers.ID, "item", 69),
    // T3微控制器外壳
    OpenComputersTier3MicrocontrollerCase(Mods.OpenComputers.ID, "item", 90),
    // T3无人机外壳
    OpenComputersTier3DroneCase(Mods.OpenComputers.ID, "item", 91),
    // T3平板电脑外壳
    OpenComputersTier3TabletCase(Mods.OpenComputers.ID, "item", 93),

    // 生鳀鱼
    PamsHarvestCraftAnchovyrawItem(Mods.PamsHarvestCraft.ID, "anchovyrawItem", 0),
    // 生鲈鱼
    PamsHarvestCraftBassrawItem(Mods.PamsHarvestCraft.ID, "bassrawItem", 0),
    // 生鱿鱼
    PamsHarvestCraftCalamarirawItem(Mods.PamsHarvestCraft.ID, "calamarirawItem", 0),
    // 生鲤鱼
    PamsHarvestCraftCarprawItem(Mods.PamsHarvestCraft.ID, "carprawItem", 0),
    // 生鲶鱼
    PamsHarvestCraftCatfishrawItem(Mods.PamsHarvestCraft.ID, "catfishrawItem", 0),
    // 生嘉鱼
    PamsHarvestCraftCharrrawItem(Mods.PamsHarvestCraft.ID, "charrrawItem", 0),
    // 生蛤蜊
    PamsHarvestCraftClamrawItem(Mods.PamsHarvestCraft.ID, "clamrawItem", 0),
    // 生螃蟹
    PamsHarvestCraftCrabrawItem(Mods.PamsHarvestCraft.ID, "crabrawItem", 0),
    // 蔓越莓
    PamsHarvestCraftCranberryItem(Mods.PamsHarvestCraft.ID, "cranberryItem", 0),
    // 生龙虾
    PamsHarvestCraftCrayfishrawItem(Mods.PamsHarvestCraft.ID, "crayfishrawItem", 0),
    // 生鳗鱼
    PamsHarvestCraftEelrawItem(Mods.PamsHarvestCraft.ID, "eelrawItem", 0),
    // 生青蛙
    PamsHarvestCraftFrograwItem(Mods.PamsHarvestCraft.ID, "frograwItem", 0),
    // 绿心鱼
    PamsHarvestCraftGreenheartfishItem(Mods.PamsHarvestCraft.ID, "greenheartfishItem", 0),
    // 生石斑鱼
    PamsHarvestCraftGrouperrawItem(Mods.PamsHarvestCraft.ID, "grouperrawItem", 0),
    // 生青鱼
    PamsHarvestCraftHerringrawItem(Mods.PamsHarvestCraft.ID, "herringrawItem", 0),
    // 香辣鸡翅
    PamsHarvestCraftHotwingsItem(Mods.PamsHarvestCraft.ID, "hotwingsItem", 0),
    // 生海蜇
    PamsHarvestCraftJellyfishrawItem(Mods.PamsHarvestCraft.ID, "jellyfishrawItem", 0),
    // 生泥鱼
    PamsHarvestCraftMudfishrawItem(Mods.PamsHarvestCraft.ID, "mudfishrawItem", 0),
    // 生章鱼
    PamsHarvestCraftOctopusrawItem(Mods.PamsHarvestCraft.ID, "octopusrawItem", 0),
    // 生鲈鱼
    PamsHarvestCraftPerchrawItem(Mods.PamsHarvestCraft.ID, "perchrawItem", 0),
    // 水稻
    PamsHarvestCraftRiceItem(Mods.PamsHarvestCraft.ID, "riceItem", 0),
    // 蜂王浆
    PamsHarvestCraftRoyaljellyItem(Mods.PamsHarvestCraft.ID, "royaljellyItem", 0),
    // 生扇贝
    PamsHarvestCraftScalloprawItem(Mods.PamsHarvestCraft.ID, "scalloprawItem", 0),
    // 海带
    PamsHarvestCraftSeaweedItem(Mods.PamsHarvestCraft.ID, "seaweedItem", 0),
    // 生虾
    PamsHarvestCraftShrimprawItem(Mods.PamsHarvestCraft.ID, "shrimprawItem", 0),
    // 生蜗牛
    PamsHarvestCraftSnailrawItem(Mods.PamsHarvestCraft.ID, "snailrawItem", 0),
    // 生鲷鱼
    PamsHarvestCraftSnapperrawItem(Mods.PamsHarvestCraft.ID, "snapperrawItem", 0),
    // 生罗非鱼
    PamsHarvestCraftTilapiarawItem(Mods.PamsHarvestCraft.ID, "tilapiarawItem", 0),
    // 生鳟鱼
    PamsHarvestCraftTroutrawItem(Mods.PamsHarvestCraft.ID, "troutrawItem", 0),
    // 生金枪鱼
    PamsHarvestCraftTunarawItem(Mods.PamsHarvestCraft.ID, "tunarawItem", 0),
    // 生海龟
    PamsHarvestCraftTurtlerawItem(Mods.PamsHarvestCraft.ID, "turtlerawItem", 0),
    // 生碧古鱼
    PamsHarvestCraftWalleyerawItem(Mods.PamsHarvestCraft.ID, "walleyerawItem", 0),
    // 荸荠
    PamsHarvestCraftWaterchestnutItem(Mods.PamsHarvestCraft.ID, "waterchestnutItem", 0),

    // 创造模式IC芯片
    ProjectRedCreativeICChip(Mods.ProjectRedFabrication.ID, "projectred.fabrication.icchip", 1),

    // 普通轨道
    RailcraftTrack(Mods.Railcraft.ID, "track", 0),
    RailcraftTrackLegacyDamage736(Mods.Railcraft.ID, "track", 736),
    RailcraftTrackLegacyDamage816(Mods.Railcraft.ID, "track", 816),
    // 集水器壁板
    RailcraftWaterTankWall(Mods.Railcraft.ID, "machine.alpha", 14),
    // 高级焦炉砖块
    RailcraftAdvancedCokeOvenBrick(Mods.Railcraft.ID, "machine.alpha", 12),
    // 民科蒸汽引擎
    RailcraftHobbyistSteamEngine(Mods.Railcraft.ID, "machine.beta", 7),

    // 沃土
    RandomThingsFertilizedDirt(Mods.RandomThings.ID, "fertilizedDirt", 0),
    // 灵气
    RandomThingsSpirit(Mods.RandomThings.ID, "ingredient", 3),

    // 吊炸天电容
    SGCraftIc2Capacitor(Mods.SGCraft.ID, "ic2Capacitor", 0),
    // RF星门能量单元
    SGCraftRfPowerUnit(Mods.SGCraft.ID, "rfPowerUnit", 0),
    // 星门导标升级
    SGCraftSgChevronUpgrade(Mods.SGCraft.ID, "sgChevronUpgrade", 0),
    // 星门控制水晶
    SGCraftSgControllerCrystal(Mods.SGCraft.ID, "sgControllerCrystal", 0),
    // 星门核心水晶
    SGCraftSgCoreCrystal(Mods.SGCraft.ID, "sgCoreCrystal", 0),
    // 星门虹膜升级
    SGCraftSgIrisUpgrade(Mods.SGCraft.ID, "sgIrisUpgrade", 0),
    // 星门底座方块
    SGCraftStargateBase(Mods.SGCraft.ID, "stargateBase", 0),
    // 星门控制器
    SGCraftStargateController(Mods.SGCraft.ID, "stargateController", 0),
    // 星门外环段
    SGCraftStargateRing(Mods.SGCraft.ID, "stargateRing", 0),
    // 星门导标方块
    SGCraftStargateChevronBlock(Mods.SGCraft.ID, "stargateRing", 1),

    // 标准车壳
    StevesCartsStandardHull(Mods.StevesCarts2.ID, "CartModule", 38),
    // 无尽引擎
    StevesCartsInfinityEngine(Mods.StevesCarts2.ID, "CartModule", 61),
    // 升级：创造模式
    StevesCartsCreativeUpgrade(Mods.StevesCarts2.ID, "upgrade", 14),

    // 抽屉管理器
    StorageDrawersController(Mods.StorageDrawers.ID, "controller", 0),
    // 抽屉容量升级（II）
    StorageDrawersCapacityUpgradeII(Mods.StorageDrawers.ID, "upgrade", 2),
    // 抽屉容量升级（III）
    StorageDrawersCapacityUpgradeIII(Mods.StorageDrawers.ID, "upgrade", 3),
    // 抽屉容量升级（IV）
    StorageDrawersCapacityUpgradeIV(Mods.StorageDrawers.ID, "upgrade", 4),
    // 抽屉容量升级（V）
    StorageDrawersCapacityUpgradeV(Mods.StorageDrawers.ID, "upgrade", 5),
    // 抽屉容量升级（VI）
    StorageDrawersCapacityUpgradeVI(Mods.StorageDrawers.ID, "upgrade", 6),
    // 抽屉容量升级（VII）
    StorageDrawersCapacityUpgradeVII(Mods.StorageDrawers.ID, "upgrade", 7),
    // 抽屉容量升级（VIII）
    StorageDrawersCapacityUpgradeVIII(Mods.StorageDrawers.ID, "upgrade", 8),
    // 升级模板
    StorageDrawersUpgradeTemplate(Mods.StorageDrawers.ID, "upgradeTemplate", 0),

    // 多方块机器全息投影仪
    StructureLibConstructableTrigger(Mods.StructureLib.ID, "item.structurelib.constructableTrigger", 0),

    // 法杖核心:时间
    TaintedMagicItemFocusTime(Mods.TaintedMagic.ID, "ItemFocusTime", 0),

    // 法杖核心:元始
    ThaumcraftFocusPrimal(Mods.Thaumcraft.ID, "FocusPrimal", 0),
    // 法杖核心:守护
    ThaumcraftFocusWarding(Mods.Thaumcraft.ID, "FocusWarding", 0),
    // 凡人护身符
    ThaumcraftItemBaubleBlanks(Mods.Thaumcraft.ID, "ItemBaubleBlanks", 0),
    // 凡人指环
    ThaumcraftMundaneRing(Mods.Thaumcraft.ID, "ItemBaubleBlanks", 1),
    // 元始珍珠
    ThaumcraftPrimordialPearl(Mods.Thaumcraft.ID, "ItemEldritchObject", 3),
    // 白色油脂蜡烛
    ThaumcraftBlockCandle(Mods.Thaumcraft.ID, "blockCandle", 0),
    // 风之魔晶
    ThaumcraftBlockCrystal(Mods.Thaumcraft.ID, "blockCrystal", 0),
    // 火之魔晶
    ThaumcraftFireCrystal(Mods.Thaumcraft.ID, "blockCrystal", 1),
    // 水之魔晶
    ThaumcraftWaterCrystal(Mods.Thaumcraft.ID, "blockCrystal", 2),
    // 复相魔晶
    ThaumcraftMixedCrystal(Mods.Thaumcraft.ID, "blockCrystal", 6),
    // 符文矩阵
    ThaumcraftRunicMatrix(Mods.Thaumcraft.ID, "blockStoneDevice", 2),
    // 奥术工作台
    ThaumcraftArcaneWorkbench(Mods.Thaumcraft.ID, "blockTable", 15),

    // 镶金黑曜石
    ThaumicBasesEldritchArk(Mods.ThaumicBases.ID, "eldritchArk", 0),
    // 彩虹仙人掌
    ThaumicBasesRainbowCactus(Mods.ThaumicBases.ID, "rainbowCactus", 0),
    // 奥术左轮枪
    ThaumicBasesRevolver(Mods.ThaumicBases.ID, "revolver", 0),

    // 魔导源质存储元件
    ThaumicEnergisticsEssentiaStorageCell(Mods.ThaumicEnergistics.ID, "storage.essentia", 4),
    // 奥术装配室
    ThaumicEnergisticsArcaneAssembler(Mods.ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 0),

    // 炼狱之壶
    ThaumicExplorationEverburnUrn(Mods.ThaumicExploration.ID, "everburnUrn", 0),

    // 觉醒灵宝镐
    ThaumicTinkererIchorPickGem(Mods.ThaumicTinkerer.ID, "ichorPickGem", 0),

    // 钴矿石
    TinkerConstructCobaltOre(Mods.TinkerConstruct.ID, "SearedBrick", 1),
    // 阿迪特矿石
    TinkerConstructArditeOre(Mods.TinkerConstruct.ID, "SearedBrick", 2),
    // 合成站
    TinkerConstructCraftingStation(Mods.TinkerConstruct.ID, "CraftingStation", 0),
    // 史莱姆水晶
    TinkerConstructSlimeCrystal(Mods.TinkerConstruct.ID, "materials", 1),
    // 蓝色史莱姆水晶
    TinkerConstructBlueSlimeCrystal(Mods.TinkerConstruct.ID, "materials", 17),
    // 凝固史莱姆块
    TinkerConstructSlimeGel(Mods.TinkerConstruct.ID, "slime.gel", 0),
    // 弹跳板
    TinkerConstructSlimePad(Mods.TinkerConstruct.ID, "slime.pad", 0),

    // 雪人首领毛皮
    TwilightForestItemAlphaFur(Mods.TwilightForest.ID, "item.alphaFur", 0),
    // 极地毛皮
    TwilightForestItemArcticFur(Mods.TwilightForest.ID, "item.arcticFur", 0),
    // 砷铅铁
    TwilightForestItemCarminite(Mods.TwilightForest.ID, "item.carminite", 0),
    // 保管符咒 III
    TwilightForestItemCharmOfKeeping3(Mods.TwilightForest.ID, "item.charmOfKeeping3", 0),
    // 生命符咒 II
    TwilightForestItemCharmOfLife2(Mods.TwilightForest.ID, "item.charmOfLife2", 0),
    // 粉碎号角
    TwilightForestItemCrumbleHorn(Mods.TwilightForest.ID, "item.crumbleHorn", 0),
    // 炽热的血液
    TwilightForestItemFieryBlood(Mods.TwilightForest.ID, "item.fieryBlood", 0),
    // 炽热的泪
    TwilightForestItemFieryTears(Mods.TwilightForest.ID, "item.fieryTears", 0),
    // 巨人的镐
    TwilightForestItemGiantPick(Mods.TwilightForest.ID, "item.giantPick", 0),
    // 巨人的剑
    TwilightForestItemGiantSword(Mods.TwilightForest.ID, "item.giantSword", 0),
    // 九头蛇肉排
    TwilightForestItemHydraChop(Mods.TwilightForest.ID, "item.hydraChop", 0),
    // 寒冰炸弹
    TwilightForestItemIceBomb(Mods.TwilightForest.ID, "item.iceBomb", 0),
    // 铁木锭
    TwilightForestItemIronwoodIngot(Mods.TwilightForest.ID, "item.ironwoodIngot", 0),
    // 骑士金属锭
    TwilightForestItemKnightMetal(Mods.TwilightForest.ID, "item.knightMetal", 0),
    // 灰烬烧灯
    TwilightForestItemLampOfCinders(Mods.TwilightForest.ID, "item.lampOfCinders", 0),
    // 魔豆
    TwilightForestItemMagicBeans(Mods.TwilightForest.ID, "item.magicBeans", 0),
    // 魔法地图核心
    TwilightForestItemMagicMapFocus(Mods.TwilightForest.ID, "item.magicMapFocus", 0),
    // 迷宫地图核心
    TwilightForestItemMazeMapFocus(Mods.TwilightForest.ID, "item.mazeMapFocus", 0),
    // 迷宫破坏者
    TwilightForestItemMazebreakerPick(Mods.TwilightForest.ID, "item.mazebreakerPick", 0),
    // 牛头人肉排
    TwilightForestItemMeefSteak(Mods.TwilightForest.ID, "item.meefSteak", 0),
    // 牛头人沙拉酱肉
    TwilightForestItemMeefStroganoff(Mods.TwilightForest.ID, "item.meefStroganoff", 0),
    // 娜迦鳞片
    TwilightForestItemNagaScale(Mods.TwilightForest.ID, "item.nagaScale", 0),
    // 幻影头盔
    TwilightForestItemPhantomHelm(Mods.TwilightForest.ID, "item.phantomHelm", 0),
    // 幻影胸甲
    TwilightForestItemPhantomPlate(Mods.TwilightForest.ID, "item.phantomPlate", 0),
    // 吸血权杖
    TwilightForestItemScepterLifeDrain(Mods.TwilightForest.ID, "item.scepterLifeDrain", 0),
    // 黄昏权杖
    TwilightForestItemScepterTwilight(Mods.TwilightForest.ID, "item.scepterTwilight", 0),
    // 僵尸权杖
    TwilightForestItemScepterZombie(Mods.TwilightForest.ID, "item.scepterZombie", 0),
    // 钢叶
    TwilightForestItemSteeleafIngot(Mods.TwilightForest.ID, "item.steeleafIngot", 0),
    // 三发弓
    TwilightForestItemTripleBow(Mods.TwilightForest.ID, "item.tripleBow", 0),
    // 九头蛇战利品
    HydraTrophy(Mods.TwilightForest.ID, "item.trophy", 0),
    // 娜迦战利品
    NagaTrophy(Mods.TwilightForest.ID, "item.trophy", 1),
    // 巫妖战利品
    LichTrophy(Mods.TwilightForest.ID, "item.trophy", 2),
    // 暮色恶魂战利品
    UrGhastTrophy(Mods.TwilightForest.ID, "item.trophy", 3),
    // 冰雪女王战利品
    SnowQueenTrophy(Mods.TwilightForest.ID, "item.trophy", 4),
    // 米诺陶战利品
    MinoshroomTrophy(Mods.TwilightForest.ID, "item.trophy", 5),
    // 幻影骑士战利品
    KnightPhantomTrophy(Mods.TwilightForest.ID, "item.trophy", 6),
    // 雪人首领战利品
    AlphaYetiTrophy(Mods.TwilightForest.ID, "item.trophy", 7),
    // 谜题羊战利品
    QuestingRamTrophy(Mods.TwilightForest.ID, "item.trophy", 8),
    // 极光柱
    TwilightForestTileAuroraPillar(Mods.TwilightForest.ID, "tile.AuroraPillar", 0),
    // 蓬松的云
    TwilightForestTileFluffyCloud(Mods.TwilightForest.ID, "tile.FluffyCloud", 0),
    // 巨型圆石
    TwilightForestTileGiantCobble(Mods.TwilightForest.ID, "tile.GiantCobble", 0),
    // 巨型原木
    TwilightForestTileGiantLog(Mods.TwilightForest.ID, "tile.GiantLog", 0),
    // 巨型黑曜石
    TwilightForestTileGiantObsidian(Mods.TwilightForest.ID, "tile.GiantObsidian", 0),
    // 巨型暮色森林蘑菇
    TwilightForestTileHugeGloomBlock(Mods.TwilightForest.ID, "tile.HugeGloomBlock", 0),
    // 巨型荷叶
    TwilightForestTileHugeLilyPad(Mods.TwilightForest.ID, "tile.HugeLilyPad", 0),
    // 巨大的茎
    TwilightForestTileHugeStalk(Mods.TwilightForest.ID, "tile.HugeStalk", 0),
    // 极光方块
    TwilightForestAuroraBrick(Mods.TwilightForest.ID, "tile.TFAuroraBrick", 0),
    // 暮色橡树原木
    TwilightForestLog(Mods.TwilightForest.ID, "tile.TFLog", 0),
    // 时光树的时钟
    TwilightForestMagicLogSpecial(Mods.TwilightForest.ID, "tile.TFMagicLogSpecial", 0),
    // 时光树树苗
    TwilightForestTreeOfTimeSapling(Mods.TwilightForest.ID, "tile.TFSapling", 5),
    // 螺旋纹石砖
    TwilightForestSpiralBricks(Mods.TwilightForest.ID, "tile.TFSpiralBricks", 0),
    // 重现方块
    TwilightForestTowerDevice(Mods.TwilightForest.ID, "tile.TFTowerDevice", 0),
    // 消失方块
    TwilightForestVanishingBlock(Mods.TwilightForest.ID, "tile.TFTowerDevice", 2),
    // 飘渺的云
    TwilightForestTileWispyCloud(Mods.TwilightForest.ID, "tile.WispyCloud", 0),

    // 反物质
    TwistSpaceAntimatter(ModList.TwistSpaceTechnology.ID, "MetaItem01", 14),
    // 反物质燃料棒
    TwistSpaceAntimatterFuelRod(ModList.TwistSpaceTechnology.ID, "MetaItem01", 16),
    // 星体结构框架材料
    TwistSpaceStellarStructuralFrameMaterial(ModList.TwistSpaceTechnology.ID, "MetaItem01", 17),
    // 奇异湮灭燃料棒
    TwistSpaceStrangeAnnihilationFuelRod(ModList.TwistSpaceTechnology.ID, "MetaItem01", 29),

    // 导电铁奇点
    USConductiveIronSingularity(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 0),
    // 磁钢奇点
    USElectricalSteelSingularity(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 1),
    // 充能合金奇点
    USEnergeticAlloySingularity(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 2),
    // 玄钢奇点
    USDarkSteelSingularity(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 3),
    // 脉动铁奇点
    USPulsatingIronSingularity(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 4),
    // 红石合金奇点
    USRedstoneAlloySingularity(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 5),
    // 魂金奇点
    USSoulariumSingularity(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 6),
    // 脉冲合金奇点
    USVibrantAlloySingularity(Mods.UniversalSingularities.ID, "universal.enderIO.singularity", 7),
    // 不稳定金属奇点
    USUnstableSingularity(Mods.UniversalSingularities.ID, "universal.extraUtilities.singularity", 0),
    // 铝奇点
    USAluminumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 0),
    // 黄铜奇点
    USBrassSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 1),
    // 蓝宝石奇点
    USSapphireSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 10),
    // 钢奇点
    USSteelSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 11),
    // 钛奇点
    USTitaniumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 12),
    // 钨奇点
    USTungstenSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 13),
    // 铀奇点
    USUraniumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 14),
    // 锌奇点
    USZincSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 15),
    // 磷酸三钙奇点
    USTricalciumPhosphateSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 16),
    // 钯奇点
    USPalladiumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 17),
    // 大马士革钢奇点
    USDamascusSteelSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 18),
    // 黑钢奇点
    USBlackSteelSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 19),
    // 青铜奇点
    USBronzeSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 2),
    // 流体琥珀金奇点
    USElectrumFluxSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 20),
    // 水银奇点
    USQuicksilverSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 21),
    // 暗影钢奇点
    USShadowSteelSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 22),
    // 铱奇点
    USIridiumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 23),
    // 下界之星奇点
    USNetherStarSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 24),
    // 铂奇点
    USPlatinumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 25),
    // 超能硅岩奇点
    USNaquadriaSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 26),
    // 钚奇点
    USPlutoniumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 27),
    // 陨铁奇点
    USMeteoricIronSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 28),
    // 戴斯奇点
    USDeshSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 29),
    // 木炭奇点
    USCharcoalSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 3),
    // 铕奇点
    USEuropiumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 30),
    // 脉石奇点
    USGangueSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 31),
    // 琥珀金奇点
    USElectrumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 4),
    // 殷钢奇点
    USInvarSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 5),
    // 镁奇点
    USMagnesiumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 6),
    // 锇奇点
    USOsmiumSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 7),
    // 橄榄石奇点
    USPeridotSingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 8),
    // 红宝石奇点
    USRubySingularity(Mods.UniversalSingularities.ID, "universal.general.singularity", 9),
    // 蓝石奇点
    USElectrotineSingularity(Mods.UniversalSingularities.ID, "universal.projectRed.singularity", 0),
    // 耐酸铝奇点
    USAlumiteSingularity(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 1),
    // 阿迪特奇点
    USArditeSingularity(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 2),
    // 钴奇点
    USCobaltSingularity(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 3),
    // 末影奇点
    USEnderSingularity(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 4),
    // 玛玉灵奇点
    USManyullynSingularity(Mods.UniversalSingularities.ID, "universal.tinkersConstruct.singularity", 6),
    // 煤炭奇点
    USCoalSingularity(Mods.UniversalSingularities.ID, "universal.vanilla.singularity", 0),
    // 绿宝石奇点
    USEmeraldSingularity(Mods.UniversalSingularities.ID, "universal.vanilla.singularity", 1),
    // 钻石奇点
    USDiamondSingularity(Mods.UniversalSingularities.ID, "universal.vanilla.singularity", 2),

    // 无限之蛋
    WitcheryInfinityegg(Mods.Witchery.ID, "infinityegg", 0),
    // 木灰
    WitcheryWoodAsh(Mods.Witchery.ID, "ingredient", 18),
    // 水心之酿
    WitcheryWaterArtichokeBrew(Mods.Witchery.ID, "ingredient", 96),

    // 奥术计算器
    WitchingGadgetsArcaneCalculator(Mods.WitchingGadgets.ID, "item.WG_Material", 7);

    private final String modId;
    private final String registryName;
    private final int metadata;
    private final boolean forgeRegistry;
    private final boolean ic2NamedItem;

    ModsItemlist(String modId, String registryName) {
        this(modId, registryName, 0);
    }

    ModsItemlist(String modId, String registryName, int metadata) {
        this(modId, registryName, metadata, false, false);
    }

    ModsItemlist(String modId, String registryName, boolean forgeRegistry) {
        this(modId, registryName, 0, forgeRegistry, false);
    }

    ModsItemlist(String modId, String registryName, boolean forgeRegistry, boolean ic2NamedItem) {
        this(modId, registryName, 0, forgeRegistry, ic2NamedItem);
    }

    ModsItemlist(String modId, String registryName, int metadata, boolean forgeRegistry, boolean ic2NamedItem) {
        this.modId = modId;
        this.registryName = registryName;
        this.metadata = metadata;
        this.forgeRegistry = forgeRegistry;
        this.ic2NamedItem = ic2NamedItem;
    }

    public ItemStack get(long amount) {
        if (ic2NamedItem) {
            ItemStack stack = IC2Items.getItem(registryName);
            if (stack == null) return null;
            ItemStack copy = stack.copy();
            copy.stackSize = (int) amount;
            return copy;
        }
        if (forgeRegistry) return GameRegistry.findItemStack(modId, registryName, (int) amount);
        return GTModHandler.getModItem(modId, registryName, amount, metadata);
    }
}
