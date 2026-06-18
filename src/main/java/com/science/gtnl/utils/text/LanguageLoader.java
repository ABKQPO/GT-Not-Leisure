package com.science.gtnl.utils.text;

import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.Utils;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.FMLCommonHandler;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTLanguageManager;

public class LanguageLoader {

    private static boolean fluidPipePrefixLocalized;
    private static boolean itemPipePrefixLocalized;

    public static void registry() {
        String currentLanguage = FMLCommonHandler.instance()
            .getCurrentLanguage();
        if (currentLanguage == null || currentLanguage.isEmpty()) {
            currentLanguage = GTLanguageManager.LanguageCode;
        }
        if (!"zh_CN".equals(currentLanguage)) return;

        // Bartwork material
        addWerkstoffLocalization(GTNLMaterials.Hexanitrohexaazaisowurtzitane, "六硝基六氮杂异伍兹烷", false);
        addWerkstoffLocalization(GTNLMaterials.CrudeHexanitrohexaazaisowurtzitane, "粗制六硝基六氮杂异伍兹烷", false);
        addWerkstoffLocalization(GTNLMaterials.SilicaGel, "硅胶", false);
        addWerkstoffLocalization(GTNLMaterials.Ethylenediamine, "乙二胺", false);
        addWerkstoffLocalization(GTNLMaterials.Ethanolamine, "乙醇胺", false);
        addWerkstoffLocalization(GTNLMaterials.SilicaGelBase, "硅胶基质", false);
        addWerkstoffLocalization(GTNLMaterials.FluoroboricAcide, "氟硼酸", false);
        addWerkstoffLocalization(GTNLMaterials.Tetraacetyldinitrohexaazaisowurtzitane, "四乙酰二硝基六氮杂异戊二烯", false);
        addWerkstoffLocalization(GTNLMaterials.NitroniumTetrafluoroborate, "四氟硝铵", false);
        addWerkstoffLocalization(GTNLMaterials.NitronsoniumTetrafluoroborate, "四氟硼酸亚硝铵", false);
        addWerkstoffLocalization(GTNLMaterials.BoronFluoride, "氟化硼", false);
        addWerkstoffLocalization(GTNLMaterials.SodiumTetrafluoroborate, "四氟硼酸钠", false);
        addWerkstoffLocalization(GTNLMaterials.BoronTrioxide, "氧化硼", false);
        addWerkstoffLocalization(GTNLMaterials.Dibenzyltetraacetylhexaazaisowurtzitane, "二基基四乙酰六氮杂异纤锌烷", false);
        addWerkstoffLocalization(GTNLMaterials.Benzaldehyde, "苯甲醇", false);
        addWerkstoffLocalization(GTNLMaterials.HydrobromicAcid, "氢溴酸", false);
        addWerkstoffLocalization(GTNLMaterials.SuccinimidylAcetate, "琥珀酰亚胺醋酸酯", false);
        addWerkstoffLocalization(GTNLMaterials.Hexabenzylhexaazaisowurtzitane, "六苄基六氮杂异伍兹烷", false);
        addWerkstoffLocalization(GTNLMaterials.NHydroxysuccinimide, "羟基丁二酰亚胺", false);
        addWerkstoffLocalization(GTNLMaterials.SuccinicAnhydride, "丁二酸酐", false);
        addWerkstoffLocalization(GTNLMaterials.HydroxylamineHydrochloride, "盐酸羟胺", false);
        addWerkstoffLocalization(GTNLMaterials.BariumChloride, "氯化钡", false);
        addWerkstoffLocalization(GTNLMaterials.HydroxylammoniumSulfate, "羟胺硫酸盐", false);
        addWerkstoffLocalization(GTNLMaterials.AcrylonitrileButadieneStyrene, "ABS塑料", false);
        addWerkstoffLocalization(GTNLMaterials.PotassiumHydroxylaminedisulfonate, "羟胺二磺酸钾", false);
        addWerkstoffLocalization(GTNLMaterials.PotassiumSulfate, "硫酸钾", false);
        addWerkstoffLocalization(GTNLMaterials.PotassiumBisulfite, "亚硫酸氢钾", false);
        addWerkstoffLocalization(GTNLMaterials.NitrousAcid, "亚硝酸", false);
        addWerkstoffLocalization(GTNLMaterials.SodiumNitrite, "亚硝酸钠", false);
        addWerkstoffLocalization(GTNLMaterials.CoAcAbCatalyst, "Co/AC-AB催化剂粉", false);
        addWerkstoffLocalization(GTNLMaterials.SodiumNitrateSolution, "硝酸钠溶液", false);
        addWerkstoffLocalization(GTNLMaterials.Benzylamine, "苄胺", false);
        addWerkstoffLocalization(GTNLMaterials.Glyoxal, "乙二醛", false);
        addWerkstoffLocalization(GTNLMaterials.Acetonitrile, "乙腈", false);
        addWerkstoffLocalization(GTNLMaterials.AmmoniumChloride, "氯化铵", false);
        addWerkstoffLocalization(GTNLMaterials.Hexamethylenetetramine, "环六亚甲基四胺", false);
        addWerkstoffLocalization(GTNLMaterials.BenzylChloride, "氯化苄", false);
        addWerkstoffLocalization(GTNLMaterials.SuccinicAcid, "琥珀酸", false);
        addWerkstoffLocalization(GTNLMaterials.MaleicAnhydride, "顺丁烯二酸酐", false);
        addWerkstoffLocalization(GTNLMaterials.SuperMutatedLivingSolder, "超突变活性焊料", false);
        addWerkstoffLocalization(GTNLMaterials.Polyimide, "聚酰亚胺", false);
        addWerkstoffLocalization(GTNLMaterials.PloyamicAcid, "聚酰胺酸（PAA）", false);
        addWerkstoffLocalization(GTNLMaterials.Oxydianiline, "二氨基二苯醚", false);
        addWerkstoffLocalization(GTNLMaterials.PyromelliticDianhydride, "均苯二甲酸酐", false);
        addWerkstoffLocalization(GTNLMaterials.Durene, "杜烯", false);
        addWerkstoffLocalization(GTNLMaterials.Germaniumtungstennitride, "锗-钨氮化物", false);
        addWerkstoffLocalization(GTNLMaterials.Polyetheretherketone, "聚醚醚酮", false);
        addWerkstoffLocalization(GTNLMaterials.FluidMana, "液态魔力", false);
        addWerkstoffLocalization(GTNLMaterials.ExcitedNaquadahFuel, "激发的混合硅岩基燃料", false);
        addWerkstoffLocalization(GTNLMaterials.RareEarthHydroxides, "稀土氢氧化物", false);
        addWerkstoffLocalization(GTNLMaterials.RareEarthChlorides, "稀土氯化物", false);
        addWerkstoffLocalization(GTNLMaterials.RareEarthOxide, "稀土氧化物", false);
        addWerkstoffLocalization(GTNLMaterials.RareEarthMetal, "稀土金属", false);
        addWerkstoffLocalization(GTNLMaterials.BarnardaCSappy, "巴纳德C树汁", false);
        addWerkstoffLocalization(GTNLMaterials.NeutralisedRedMud, "中和赤泥", false);
        addWerkstoffLocalization(GTNLMaterials.FerricReeChloride, "含稀土氯化铁", false);
        addWerkstoffLocalization(GTNLMaterials.LaNdOxidesSolution, "镧-钕氧化物", false);
        addWerkstoffLocalization(GTNLMaterials.SmGdOxidesSolution, "钐-钆氧化物", false);
        addWerkstoffLocalization(GTNLMaterials.TbHoOxidesSolution, "铽-钬氧化物", false);
        addWerkstoffLocalization(GTNLMaterials.ErLuOxidesSolution, "饵-镥氧化物", false);
        addWerkstoffLocalization(GTNLMaterials.PraseodymiumOxide, "氧化镨", false);
        addWerkstoffLocalization(GTNLMaterials.ScandiumOxide, "氧化钪", false);
        addWerkstoffLocalization(GTNLMaterials.GadoliniumOxide, "氧化钆", false);
        addWerkstoffLocalization(GTNLMaterials.TerbiumOxide, "氧化铽", false);
        addWerkstoffLocalization(GTNLMaterials.DysprosiumOxide, "氧化镝", false);
        addWerkstoffLocalization(GTNLMaterials.HolmiumOxide, "氧化钬", false);
        addWerkstoffLocalization(GTNLMaterials.ErbiumOxide, "氧化铒", false);
        addWerkstoffLocalization(GTNLMaterials.ThuliumOxide, "氧化铥", false);
        addWerkstoffLocalization(GTNLMaterials.YtterbiumOxide, "氧化镱", false);
        addWerkstoffLocalization(GTNLMaterials.LutetiumOxide, "氧化镥", false);
        addWerkstoffLocalization(GTNLMaterials.MolybdenumDisilicide, "二硅化钼", false);
        addWerkstoffLocalization(GTNLMaterials.HSLASteel, "HSLA钢", false);
        addWerkstoffLocalization(GTNLMaterials.Actinium, "锕", false);
        addWerkstoffLocalization(GTNLMaterials.Rutherfordium, "𬬻", false);
        addWerkstoffLocalization(GTNLMaterials.Dubnium, "𬭊", false);
        addWerkstoffLocalization(GTNLMaterials.Seaborgium, "𬭳", false);
        addWerkstoffLocalization(GTNLMaterials.Technetium, "锝", false);
        addWerkstoffLocalization(GTNLMaterials.Bohrium, "𬭛", false);
        addWerkstoffLocalization(GTNLMaterials.Hassium, "𬭶", false);
        addWerkstoffLocalization(GTNLMaterials.Meitnerium, "鿏", false);
        addWerkstoffLocalization(GTNLMaterials.Darmstadtium, "\uD86D\uDFFC", false);
        addWerkstoffLocalization(GTNLMaterials.Roentgenium, "𬬭", false);
        addWerkstoffLocalization(GTNLMaterials.Copernicium, "鿔", false);
        addWerkstoffLocalization(GTNLMaterials.Moscovium, "镆", false);
        addWerkstoffLocalization(GTNLMaterials.Livermorium, "𫟷", false);
        addWerkstoffLocalization(GTNLMaterials.Astatine, "砹", false);
        addWerkstoffLocalization(GTNLMaterials.Tennessine, "鿬", false);
        addWerkstoffLocalization(GTNLMaterials.Francium, "钫", false);
        addWerkstoffLocalization(GTNLMaterials.Berkelium, "锫", false);
        addWerkstoffLocalization(GTNLMaterials.Einsteinium, "锿", false);
        addWerkstoffLocalization(GTNLMaterials.Mendelevium, "钔", false);
        addWerkstoffLocalization(GTNLMaterials.Nobelium, "锘", false);
        addWerkstoffLocalization(GTNLMaterials.Lawrencium, "铹", false);
        addWerkstoffLocalization(GTNLMaterials.Nihonium, "鿭", false);
        addWerkstoffLocalization(GTNLMaterials.ZnFeAlCl, "锌-铁-铝-氯混合", false);
        addWerkstoffLocalization(GTNLMaterials.BenzenediazoniumTetrafluoroborate, "四氟硼酸重氮苯", false);
        addWerkstoffLocalization(GTNLMaterials.FluoroBenzene, "氟苯", false);
        addWerkstoffLocalization(GTNLMaterials.AntimonyTrifluoride, "三氟化锑", false);
        addWerkstoffLocalization(GTNLMaterials.Fluorotoluene, "氟甲苯", false);
        addWerkstoffLocalization(GTNLMaterials.Resorcinol, "间苯二酚", false);
        addWerkstoffLocalization(GTNLMaterials.Hydroquinone, "对苯二酚", false);
        addWerkstoffLocalization(GTNLMaterials.Difluorobenzophenone, "二氟二甲苯酮", false);
        addWerkstoffLocalization(GTNLMaterials.FluorineCrackedNaquadah, "加氟裂化硅岩", false);
        addWerkstoffLocalization(GTNLMaterials.EnrichedNaquadahWaste, "富集硅岩废液", false);
        addWerkstoffLocalization(GTNLMaterials.RadonCrackedEnrichedNaquadah, "加氡裂化富集硅岩", false);
        addWerkstoffLocalization(GTNLMaterials.NaquadriaWaste, "超能硅岩废液", false);
        addWerkstoffLocalization(GTNLMaterials.SmallBaka, "硝苯氮", false);
        addWerkstoffLocalization(GTNLMaterials.LargeBaka, "𫟼苯氮", false);
        addWerkstoffLocalization(GTNLMaterials.CompressedSteam, "压缩蒸汽", false);
        addWerkstoffLocalization(GTNLMaterials.Stronze, "青钢", false);
        addWerkstoffLocalization(GTNLMaterials.Breel, "青铁", false);
        addWerkstoffLocalization(GTNLMaterials.PitchblendeSlag, "沥青铀矿渣", false);
        addWerkstoffLocalization(GTNLMaterials.UraniumSlag, "铀矿渣", false);
        addWerkstoffLocalization(GTNLMaterials.UraniumChlorideSlag, "氯化铀矿渣", false);
        addWerkstoffLocalization(GTNLMaterials.RadiumChloride, "氯化镭", false);
        addWerkstoffLocalization(GTNLMaterials.GravelSluice, "沙砾泥浆", false);
        addWerkstoffLocalization(GTNLMaterials.SandSluice, "沙子泥浆", false);
        addWerkstoffLocalization(GTNLMaterials.ObsidianSluice, "黑曜石泥浆", false);
        addWerkstoffLocalization(GTNLMaterials.GemSluice, "宝石泥浆", false);
        addWerkstoffLocalization(GTNLMaterials.TwilightSluice, "暮色泥浆", false);
        addWerkstoffLocalization(GTNLMaterials.EnderAir, "末地空气", false);
        addWerkstoffLocalization(GTNLMaterials.LiquidEnderAir, "液态末地空气", false);
        addWerkstoffLocalization(GTNLMaterials.MixturePineoil, "松油混合物", false);
        addWerkstoffLocalization(GTNLMaterials.ToxicMercurySludge, "剧毒水银污泥", false);
        addWerkstoffLocalization(GTNLMaterials.PostProcessBeWaste, "后处理铍废液", false);
        addWerkstoffLocalization(GTNLMaterials.QuantumInfusion, "量子灌输液", false);
        addWerkstoffLocalization(GTNLMaterials.Periodicium, "錭錤錶", false);
        addWerkstoffLocalization(GTNLMaterials.Stargate, "星门", false);
        addWerkstoffLocalization(GTNLMaterials.GlowThorium, "荧光钍燃料", false);
        addWerkstoffLocalization(GTNLMaterials.UraniumFuel, "混合铀燃料", false);
        addWerkstoffLocalization(GTNLMaterials.UraniumWaste, "铀废料", false);
        addWerkstoffLocalization(GTNLMaterials.AmmoniumBisulfate, "硫酸氢铵", false);
        addWerkstoffLocalization(GTNLMaterials.AmmoniumPersulfate, "过硫酸铵", false);

        addGTMaterialLocalization(Materials.BlueAlloy, "蓝色合金", true);

        writePlaceholderStrings();
    }

    public static void writePlaceholderStrings() {
        addStringLocalization("bw.itemtype.plateSuperdense", "超致密%material板");
        addStringLocalization("bw.itemtype.nanite", "%material纳米蜂群");
    }

    public static void addWerkstoffLocalization(Werkstoff aWerkstoff, String localizedName, boolean isItemPipe) {
        String unlocalizedName = aWerkstoff.getDefaultName()
            .toLowerCase();
        String mName = unlocalizedName.replace(" ", "");

        addStringLocalization("Material." + mName, localizedName);
        addStringLocalization("bw.werkstoff." + aWerkstoff.getmID() + ".name", localizedName);

        if (aWerkstoff.hasItemType(OrePrefixes.cellMolten)) {
            addStringLocalization("fluid.molten." + unlocalizedName, "熔融" + localizedName);
        }
        if (aWerkstoff.hasItemType(OrePrefixes.cell)) {
            addStringLocalization("fluid." + unlocalizedName, localizedName);
        }

        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt01, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".01.name", "1x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt02, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".02.name", "2x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt04, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".04.name", "4x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt08, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".08.name", "8x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt12, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".12.name", "12x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt16, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".16.name", "16x%s导线");
        }

        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt01, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".01.name", "1x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt02, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".02.name", "2x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt04, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".04.name", "4x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt08, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".08.name", "8x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt12, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".12.name", "12x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt16, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".16.name", "16x%s线缆");
        }

        if (hasAnyPipePrefix(aWerkstoff)) {
            if (isItemPipe) {
                registerItemPipePrefixLocalization();
            } else {
                registerFluidPipePrefixLocalization();
            }
        }
    }

    public static void addGTMaterialLocalization(Materials aMaterial, String localizedName, boolean isItemPipe) {
        String mName = aMaterial.mName.toLowerCase();
        int mID = aMaterial.mMetaItemSubID;

        addStringLocalization("gt.blockframes." + mID + ".name", "%s框架");
        addStringLocalization("gt.blockores.1" + mID + ".name", "%s矿石");
        addStringLocalization("gt.blockores.2" + mID + ".name", "%s矿石");
        addStringLocalization("gt.blockores.3" + mID + ".name", "%s矿石");
        addStringLocalization("gt.blockores.4" + mID + ".name", "%s矿石");
        addStringLocalization("gt.blockores.5" + mID + ".name", "%s矿石");
        addStringLocalization("gt.blockores.6" + mID + ".name", "%s矿石");
        addStringLocalization("gt.blockores.16" + mID + ".name", "贫瘠%s矿石");
        addStringLocalization("gt.blockores.17" + mID + ".name", "贫瘠%s矿石");
        addStringLocalization("gt.blockores.18" + mID + ".name", "贫瘠%s矿石");
        addStringLocalization("gt.blockores.19" + mID + ".name", "贫瘠%s矿石");
        addStringLocalization("gt.blockores.20" + mID + ".name", "贫瘠%s矿石");
        addStringLocalization("gt.blockores.21" + mID + ".name", "贫瘠%s矿石");
        addStringLocalization("gt.blockores.22" + mID + ".name", "贫瘠%s矿石");

        addStringLocalization("gt.metaitem.01." + mID + ".name", "小撮%s粉");
        addStringLocalization("gt.metaitem.01.1" + mID + ".name", "小堆%s粉");
        addStringLocalization("gt.metaitem.01.2" + mID + ".name", "%s粉");
        addStringLocalization("gt.metaitem.01.9" + mID + ".name", "%s粒");

        addStringLocalization("gt.metaitem.01.11" + mID + ".name", "%s锭");
        addStringLocalization("gt.metaitem.01.12" + mID + ".name", "热%s锭");
        addStringLocalization("gt.metaitem.01.13" + mID + ".name", "双重%s锭");
        addStringLocalization("gt.metaitem.01.14" + mID + ".name", "三重%s锭");
        addStringLocalization("gt.metaitem.01.15" + mID + ".name", "四重%s锭");
        addStringLocalization("gt.metaitem.01.16" + mID + ".name", "五重%s锭");
        addStringLocalization("gt.metaitem.01.17" + mID + ".name", "%s板");
        addStringLocalization("gt.metaitem.01.18" + mID + ".name", "双重%s板");
        addStringLocalization("gt.metaitem.01.19" + mID + ".name", "三重%s板");
        addStringLocalization("gt.metaitem.01.20" + mID + ".name", "四重%s板");
        addStringLocalization("gt.metaitem.01.21" + mID + ".name", "五重%s板");
        addStringLocalization("gt.metaitem.01.22" + mID + ".name", "致密%s板");
        addStringLocalization("gt.metaitem.01.23" + mID + ".name", "%s杆");
        addStringLocalization("gt.metaitem.01.24" + mID + ".name", "%s弹簧");
        addStringLocalization("gt.metaitem.01.25" + mID + ".name", "%s滚珠");
        addStringLocalization("gt.metaitem.01.26" + mID + ".name", "%s螺栓");
        addStringLocalization("gt.metaitem.01.27" + mID + ".name", "%s螺丝");
        addStringLocalization("gt.metaitem.01.28" + mID + ".name", "%s环");
        addStringLocalization("gt.metaitem.01.29" + mID + ".name", "%s箔");
        addStringLocalization("gt.metaitem.01.31" + mID + ".name", "%s等离子单元");

        addStringLocalization("gt.metaitem.02." + mID + ".name", "%s剑刃");
        addStringLocalization("gt.metaitem.02.1" + mID + ".name", "%s镐头");
        addStringLocalization("gt.metaitem.02.2" + mID + ".name", "%s铲头");
        addStringLocalization("gt.metaitem.02.3" + mID + ".name", "%s斧头");
        addStringLocalization("gt.metaitem.02.4" + mID + ".name", "%s锄头");
        addStringLocalization("gt.metaitem.02.5" + mID + ".name", "%s锤头");
        addStringLocalization("gt.metaitem.02.6" + mID + ".name", "%s锉刀刃");
        addStringLocalization("gt.metaitem.02.7" + mID + ".name", "%s锯刃");
        addStringLocalization("gt.metaitem.02.8" + mID + ".name", "%s钻头");
        addStringLocalization("gt.metaitem.02.9" + mID + ".name", "%s链锯刃");
        addStringLocalization("gt.metaitem.02.10" + mID + ".name", "%s扳手顶");
        addStringLocalization("gt.metaitem.02.11" + mID + ".name", "%s万用铲头");
        addStringLocalization("gt.metaitem.02.12" + mID + ".name", "%s镰刀刃");
        addStringLocalization("gt.metaitem.02.13" + mID + ".name", "%s犁头");
        addStringLocalization("gt.metaitem.02.15" + mID + ".name", "%s圆锯锯刃");
        addStringLocalization("gt.metaitem.02.16" + mID + ".name", "%s涡轮扇叶");
        addStringLocalization("gt.metaitem.02.18" + mID + ".name", "%s外壳");
        addStringLocalization("gt.metaitem.02.19" + mID + ".name", "细%s导线");
        addStringLocalization("gt.metaitem.02.20" + mID + ".name", "小型%s齿轮");
        addStringLocalization("gt.metaitem.02.21" + mID + ".name", "%s转子");
        addStringLocalization("gt.metaitem.02.22" + mID + ".name", "长%s杆");
        addStringLocalization("gt.metaitem.02.23" + mID + ".name", "小型%s弹簧");
        addStringLocalization("gt.metaitem.02.24" + mID + ".name", "%s弹簧");
        addStringLocalization("gt.metaitem.02.31" + mID + ".name", "%s齿轮");

        addStringLocalization("gt.metaitem.03.6" + mID + ".name", "超致密%s板");

        addStringLocalization("gt.metaitem.99." + mID + ".name", "熔融%s单元");

        addStringLocalization("Material." + mName, localizedName);
        addStringLocalization("fluid.molten." + mName, "熔融" + localizedName);
        addStringLocalization("fluid.plasma." + mName, localizedName + "等离子体");
        addStringLocalization("gt.blockmachines.gt_frame_" + mName + ".name", "%s框架");
        addStringLocalization("gt.blockmachines.wire." + mName + ".01.name", "1x%s导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".02.name", "2x%s导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".04.name", "4x%s导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".08.name", "8x%s导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".12.name", "12x%s导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".16.name", "16x%s导线");
        addStringLocalization("gt.blockmachines.cable." + mName + ".01.name", "1x%s线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".02.name", "2x%s线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".04.name", "4x%s线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".08.name", "8x%s线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".12.name", "12x%s线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".16.name", "16x%s线缆");

        if (isItemPipe) {
            registerItemPipePrefixLocalization();
        } else {
            registerFluidPipePrefixLocalization();
        }
    }

    private static boolean hasAnyPipePrefix(Werkstoff aWerkstoff) {
        return WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeTiny, aWerkstoff, 1) != null
            || WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeSmall, aWerkstoff, 1) != null
            || WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeMedium, aWerkstoff, 1) != null
            || WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeLarge, aWerkstoff, 1) != null
            || WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeHuge, aWerkstoff, 1) != null;
    }

    private static void registerFluidPipePrefixLocalization() {
        if (fluidPipePrefixLocalized) {
            return;
        }
        addStringLocalization("gt.oreprefix.tiny_material_fluid_pipe", "微型%s流体管道");
        addStringLocalization("gt.oreprefix.small_material_fluid_pipe", "小型%s流体管道");
        addStringLocalization("gt.oreprefix.material_fluid_pipe", "%s流体管道");
        addStringLocalization("gt.oreprefix.large_material_fluid_pipe", "大型%s流体管道");
        addStringLocalization("gt.oreprefix.huge_material_fluid_pipe", "巨型%s流体管道");
        addStringLocalization("gt.oreprefix.quadruple_material_fluid_pipe", "四联%s流体管道");
        addStringLocalization("gt.oreprefix.nonuple_material_fluid_pipe", "九联%s流体管道");
        fluidPipePrefixLocalized = true;
    }

    private static void registerItemPipePrefixLocalization() {
        if (itemPipePrefixLocalized) {
            return;
        }
        addStringLocalization("gt.oreprefix.tiny_material_item_pipe", "微型%s物品管道");
        addStringLocalization("gt.oreprefix.small_material_item_pipe", "小型%s物品管道");
        addStringLocalization("gt.oreprefix.material_item_pipe", "%s物品管道");
        addStringLocalization("gt.oreprefix.large_material_item_pipe", "大型%s物品管道");
        addStringLocalization("gt.oreprefix.huge_material_item_pipe", "巨型%s物品管道");
        addStringLocalization("gt.oreprefix.tiny_restrictive_material_item_pipe", "微型限流%s物品管道");
        addStringLocalization("gt.oreprefix.small_restrictive_material_item_pipe", "小型限流%s物品管道");
        addStringLocalization("gt.oreprefix.restrictive_material_item_pipe", "限流%s物品管道");
        addStringLocalization("gt.oreprefix.large_restrictive_material_item_pipe", "大型限流%s物品管道");
        addStringLocalization("gt.oreprefix.huge_restrictive_material_item_pipe", "巨型限流%s物品管道");
        itemPipePrefixLocalized = true;
    }

    public static String addStringLocalization(String trimmedKey, String text) {
        return Utils.storeTranslation(trimmedKey, text);
    }
}
