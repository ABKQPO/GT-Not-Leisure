package com.science.gtnl.utils.text;

import static gregtech.api.util.GTLanguageManager.addStringLocalization;

import com.science.gtnl.common.material.MaterialPool;

import bartworks.system.material.Werkstoff;
import cpw.mods.fml.common.FMLCommonHandler;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;

public class LanguageLoader {

    public static void registry() {
        if (!FMLCommonHandler.instance()
            .getCurrentLanguage()
            .equals("zh_CN")) return;

        // Bartwork material
        addWerkstoffLocalization(MaterialPool.Hexanitrohexaazaisowurtzitane, "六硝基六氮杂异伍兹烷", false);
        addWerkstoffLocalization(MaterialPool.CrudeHexanitrohexaazaisowurtzitane, "粗制六硝基六氮杂异伍兹烷", false);
        addWerkstoffLocalization(MaterialPool.SilicaGel, "硅胶", false);
        addWerkstoffLocalization(MaterialPool.Ethylenediamine, "乙二胺", false);
        addWerkstoffLocalization(MaterialPool.Ethanolamine, "乙醇胺", false);
        addWerkstoffLocalization(MaterialPool.SilicaGelBase, "硅胶基质", false);
        addWerkstoffLocalization(MaterialPool.FluoroboricAcide, "氟硼酸", false);
        addWerkstoffLocalization(MaterialPool.Tetraacetyldinitrohexaazaisowurtzitane, "四乙酰二硝基六氮杂异戊二烯", false);
        addWerkstoffLocalization(MaterialPool.NitroniumTetrafluoroborate, "四氟硝铵", false);
        addWerkstoffLocalization(MaterialPool.NitronsoniumTetrafluoroborate, "四氟硼酸亚硝铵", false);
        addWerkstoffLocalization(MaterialPool.BoronFluoride, "氟化硼", false);
        addWerkstoffLocalization(MaterialPool.SodiumTetrafluoroborate, "四氟硼酸钠", false);
        addWerkstoffLocalization(MaterialPool.BoronTrioxide, "氧化硼", false);
        addWerkstoffLocalization(MaterialPool.Dibenzyltetraacetylhexaazaisowurtzitane, "二基基四乙酰六氮杂异纤锌烷", false);
        addWerkstoffLocalization(MaterialPool.Benzaldehyde, "苯甲醇", false);
        addWerkstoffLocalization(MaterialPool.HydrobromicAcid, "氢溴酸", false);
        addWerkstoffLocalization(MaterialPool.SuccinimidylAcetate, "琥珀酰亚胺醋酸酯", false);
        addWerkstoffLocalization(MaterialPool.Hexabenzylhexaazaisowurtzitane, "六苄基六氮杂异伍兹烷", false);
        addWerkstoffLocalization(MaterialPool.NHydroxysuccinimide, "羟基丁二酰亚胺", false);
        addWerkstoffLocalization(MaterialPool.SuccinicAnhydride, "丁二酸酐", false);
        addWerkstoffLocalization(MaterialPool.HydroxylamineHydrochloride, "盐酸羟胺", false);
        addWerkstoffLocalization(MaterialPool.BariumChloride, "氯化钡", false);
        addWerkstoffLocalization(MaterialPool.HydroxylammoniumSulfate, "羟胺硫酸盐", false);
        addWerkstoffLocalization(MaterialPool.AcrylonitrileButadieneStyrene, "ABS塑料", false);
        addWerkstoffLocalization(MaterialPool.PotassiumHydroxylaminedisulfonate, "羟胺二磺酸钾", false);
        addWerkstoffLocalization(MaterialPool.PotassiumSulfate, "硫酸钾", false);
        addWerkstoffLocalization(MaterialPool.PotassiumBisulfite, "亚硫酸氢钾", false);
        addWerkstoffLocalization(MaterialPool.NitrousAcid, "亚硝酸", false);
        addWerkstoffLocalization(MaterialPool.SodiumNitrite, "亚硝酸钠", false);
        addWerkstoffLocalization(MaterialPool.CoAcAbCatalyst, "Co/AC-AB催化剂粉", false);
        addWerkstoffLocalization(MaterialPool.SodiumNitrateSolution, "硝酸钠溶液", false);
        addWerkstoffLocalization(MaterialPool.Benzylamine, "苄胺", false);
        addWerkstoffLocalization(MaterialPool.Glyoxal, "乙二醛", false);
        addWerkstoffLocalization(MaterialPool.Acetonitrile, "乙腈", false);
        addWerkstoffLocalization(MaterialPool.AmmoniumChloride, "氯化铵", false);
        addWerkstoffLocalization(MaterialPool.Hexamethylenetetramine, "环六亚甲基四胺", false);
        addWerkstoffLocalization(MaterialPool.BenzylChloride, "氯化苄", false);
        addWerkstoffLocalization(MaterialPool.SuccinicAcid, "琥珀酸", false);
        addWerkstoffLocalization(MaterialPool.MaleicAnhydride, "顺丁烯二酸酐", false);
        addWerkstoffLocalization(MaterialPool.SuperMutatedLivingSolder, "超突变活性焊料", false);
        addWerkstoffLocalization(MaterialPool.Polyimide, "聚酰亚胺", false);
        addWerkstoffLocalization(MaterialPool.PloyamicAcid, "聚酰胺酸（PAA）", false);
        addWerkstoffLocalization(MaterialPool.Oxydianiline, "二氨基二苯醚", false);
        addWerkstoffLocalization(MaterialPool.PyromelliticDianhydride, "均苯二甲酸酐", false);
        addWerkstoffLocalization(MaterialPool.Durene, "杜烯", false);
        addWerkstoffLocalization(MaterialPool.Germaniumtungstennitride, "锗-钨氮化物", false);
        addWerkstoffLocalization(MaterialPool.Polyetheretherketone, "聚醚醚酮", false);
        addWerkstoffLocalization(MaterialPool.FluidMana, "液态魔力", false);
        addWerkstoffLocalization(MaterialPool.ExcitedNaquadahFuel, "激发的混合硅岩基燃料", false);
        addWerkstoffLocalization(MaterialPool.RareEarthHydroxides, "稀土氢氧化物", false);
        addWerkstoffLocalization(MaterialPool.RareEarthChlorides, "稀土氯化物", false);
        addWerkstoffLocalization(MaterialPool.RareEarthOxide, "稀土氧化物", false);
        addWerkstoffLocalization(MaterialPool.RareEarthMetal, "稀土金属", false);
        addWerkstoffLocalization(MaterialPool.BarnardaCSappy, "巴纳德C树汁", false);
        addWerkstoffLocalization(MaterialPool.NeutralisedRedMud, "中和赤泥", false);
        addWerkstoffLocalization(MaterialPool.FerricReeChloride, "含稀土氯化铁", false);
        addWerkstoffLocalization(MaterialPool.LaNdOxidesSolution, "镧-钕氧化物", false);
        addWerkstoffLocalization(MaterialPool.SmGdOxidesSolution, "钐-钆氧化物", false);
        addWerkstoffLocalization(MaterialPool.TbHoOxidesSolution, "铽-钬氧化物", false);
        addWerkstoffLocalization(MaterialPool.ErLuOxidesSolution, "饵-镥氧化物", false);
        addWerkstoffLocalization(MaterialPool.PraseodymiumOxide, "氧化镨", false);
        addWerkstoffLocalization(MaterialPool.ScandiumOxide, "氧化钪", false);
        addWerkstoffLocalization(MaterialPool.GadoliniumOxide, "氧化钆", false);
        addWerkstoffLocalization(MaterialPool.TerbiumOxide, "氧化铽", false);
        addWerkstoffLocalization(MaterialPool.DysprosiumOxide, "氧化镝", false);
        addWerkstoffLocalization(MaterialPool.HolmiumOxide, "氧化钬", false);
        addWerkstoffLocalization(MaterialPool.ErbiumOxide, "氧化铒", false);
        addWerkstoffLocalization(MaterialPool.ThuliumOxide, "氧化铥", false);
        addWerkstoffLocalization(MaterialPool.YtterbiumOxide, "氧化镱", false);
        addWerkstoffLocalization(MaterialPool.LutetiumOxide, "氧化镥", false);
        addWerkstoffLocalization(MaterialPool.MolybdenumDisilicide, "二硅化钼", false);
        addWerkstoffLocalization(MaterialPool.HSLASteel, "HSLA钢", false);
        addWerkstoffLocalization(MaterialPool.Actinium, "锕", false);
        addWerkstoffLocalization(MaterialPool.Rutherfordium, "𬬻", false);
        addWerkstoffLocalization(MaterialPool.Dubnium, "𬭊", false);
        addWerkstoffLocalization(MaterialPool.Seaborgium, "𬭳", false);
        addWerkstoffLocalization(MaterialPool.Technetium, "锝", false);
        addWerkstoffLocalization(MaterialPool.Bohrium, "𬭛", false);
        addWerkstoffLocalization(MaterialPool.Hassium, "𬭶", false);
        addWerkstoffLocalization(MaterialPool.Meitnerium, "鿏", false);
        addWerkstoffLocalization(MaterialPool.Darmstadtium, "\uD86D\uDFFC", false);
        addWerkstoffLocalization(MaterialPool.Roentgenium, "𬬭", false);
        addWerkstoffLocalization(MaterialPool.Copernicium, "鿔", false);
        addWerkstoffLocalization(MaterialPool.Moscovium, "镆", false);
        addWerkstoffLocalization(MaterialPool.Livermorium, "𫟷", false);
        addWerkstoffLocalization(MaterialPool.Astatine, "砹", false);
        addWerkstoffLocalization(MaterialPool.Tennessine, "鿬", false);
        addWerkstoffLocalization(MaterialPool.Francium, "钫", false);
        addWerkstoffLocalization(MaterialPool.Berkelium, "锫", false);
        addWerkstoffLocalization(MaterialPool.Einsteinium, "锿", false);
        addWerkstoffLocalization(MaterialPool.Mendelevium, "钔", false);
        addWerkstoffLocalization(MaterialPool.Nobelium, "锘", false);
        addWerkstoffLocalization(MaterialPool.Lawrencium, "铹", false);
        addWerkstoffLocalization(MaterialPool.Nihonium, "鿭", false);
        addWerkstoffLocalization(MaterialPool.ZnFeAlCl, "锌-铁-铝-氯混合", false);
        addWerkstoffLocalization(MaterialPool.BenzenediazoniumTetrafluoroborate, "四氟硼酸重氮苯", false);
        addWerkstoffLocalization(MaterialPool.FluoroBenzene, "氟苯", false);
        addWerkstoffLocalization(MaterialPool.AntimonyTrifluoride, "三氟化锑", false);
        addWerkstoffLocalization(MaterialPool.Fluorotoluene, "氟甲苯", false);
        addWerkstoffLocalization(MaterialPool.Resorcinol, "间苯二酚", false);
        addWerkstoffLocalization(MaterialPool.Hydroquinone, "对苯二酚", false);
        addWerkstoffLocalization(MaterialPool.Difluorobenzophenone, "二氟二甲苯酮", false);
        addWerkstoffLocalization(MaterialPool.FluorineCrackedNaquadah, "加氟裂化硅岩", false);
        addWerkstoffLocalization(MaterialPool.EnrichedNaquadahWaste, "富集硅岩废液", false);
        addWerkstoffLocalization(MaterialPool.RadonCrackedEnrichedNaquadah, "加氡裂化富集硅岩", false);
        addWerkstoffLocalization(MaterialPool.NaquadriaWaste, "超能硅岩废液", false);
        addWerkstoffLocalization(MaterialPool.SmallBaka, "硝苯氮", false);
        addWerkstoffLocalization(MaterialPool.LargeBaka, "\uD86D\uDFFC苯氮", false);
        addWerkstoffLocalization(MaterialPool.CompressedSteam, "压缩蒸汽", false);
        addWerkstoffLocalization(MaterialPool.Stronze, "青钢", false);
        addWerkstoffLocalization(MaterialPool.Breel, "青铁", false);
        addWerkstoffLocalization(MaterialPool.PitchblendeSlag, "沥青铀矿渣", false);
        addWerkstoffLocalization(MaterialPool.UraniumSlag, "铀矿渣", false);
        addWerkstoffLocalization(MaterialPool.UraniumChlorideSlag, "氯化铀矿渣", false);
        addWerkstoffLocalization(MaterialPool.RadiumChloride, "氯化镭", false);
        addWerkstoffLocalization(MaterialPool.GravelSluice, "沙砾泥浆", false);
        addWerkstoffLocalization(MaterialPool.SandSluice, "沙子泥浆", false);
        addWerkstoffLocalization(MaterialPool.ObsidianSluice, "黑曜石泥浆", false);
        addWerkstoffLocalization(MaterialPool.GemSluice, "宝石泥浆", false);
        addWerkstoffLocalization(MaterialPool.EnderAir, "末地空气", false);
        addWerkstoffLocalization(MaterialPool.LiquidEnderAir, "液态末地空气", false);
        addWerkstoffLocalization(MaterialPool.MixturePineoil, "松油混合物", false);
        addWerkstoffLocalization(MaterialPool.ToxicMercurySludge, "剧毒水银污泥", false);
        addWerkstoffLocalization(MaterialPool.PostProcessBeWaste, "后处理铍废液", false);
        addWerkstoffLocalization(MaterialPool.Periodicium, "錭錤錶", false);
        addWerkstoffLocalization(MaterialPool.Stargate, "星门", false);

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
        if (aWerkstoff.hasItemType(OrePrefixes.wire)) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".01.name", "1x%material导线");
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".02.name", "2x%material导线");
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".04.name", "4x%material导线");
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".08.name", "8x%material导线");
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".12.name", "12x%material导线");
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".16.name", "16x%material导线");
        }
        if (aWerkstoff.hasItemType(OrePrefixes.cable)) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".01.name", "1x%material线缆");
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".02.name", "2x%material线缆");
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".04.name", "4x%material线缆");
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".08.name", "8x%material线缆");
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".12.name", "12x%material线缆");
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".16.name", "16x%material线缆");
        }
        if (aWerkstoff.hasItemType(OrePrefixes.pipe)) {
            if (isItemPipe) {
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + ".name", "%material物品管道");
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + "_huge.name", "巨型%material物品管道");
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + "_large.name", "大型%material物品管道");
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + "_small.name", "小型%material物品管道");
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + "_tiny.name", "微型%material物品管道");
            } else {
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + ".name", "%material流体管道");
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + "_huge.name", "巨型%material流体管道");
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + "_large.name", "大型%material流体管道");
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + "_small.name", "小型%material流体管道");
                addStringLocalization("gt.blockmachines.gt_pipe_" + unlocalizedName + "_tiny.name", "微型%material流体管道");
            }
        }
    }

    public static void addGTMaterialLocalization(Materials aMaterial, String localizedName, boolean isItemPipe) {
        String mName = aMaterial.mName.toLowerCase();
        int mID = aMaterial.mMetaItemSubID;

        addStringLocalization("gt.blockframes." + mID + ".name", "%material框架");
        addStringLocalization("gt.blockores.1" + mID + ".name", "%material矿石");
        addStringLocalization("gt.blockores.2" + mID + ".name", "%material矿石");
        addStringLocalization("gt.blockores.3" + mID + ".name", "%material矿石");
        addStringLocalization("gt.blockores.4" + mID + ".name", "%material矿石");
        addStringLocalization("gt.blockores.5" + mID + ".name", "%material矿石");
        addStringLocalization("gt.blockores.6" + mID + ".name", "%material矿石");
        addStringLocalization("gt.blockores.16" + mID + ".name", "贫瘠%material矿石");
        addStringLocalization("gt.blockores.17" + mID + ".name", "贫瘠%material矿石");
        addStringLocalization("gt.blockores.18" + mID + ".name", "贫瘠%material矿石");
        addStringLocalization("gt.blockores.19" + mID + ".name", "贫瘠%material矿石");
        addStringLocalization("gt.blockores.20" + mID + ".name", "贫瘠%material矿石");
        addStringLocalization("gt.blockores.21" + mID + ".name", "贫瘠%material矿石");
        addStringLocalization("gt.blockores.22" + mID + ".name", "贫瘠%material矿石");

        addStringLocalization("gt.metaitem.01." + mID + ".name", "小撮%material粉");
        addStringLocalization("gt.metaitem.01.1" + mID + ".name", "小堆%material粉");
        addStringLocalization("gt.metaitem.01.2" + mID + ".name", "%material粉");
        addStringLocalization("gt.metaitem.01.9" + mID + ".name", "%material粒");

        addStringLocalization("gt.metaitem.01.11" + mID + ".name", "%material锭");
        addStringLocalization("gt.metaitem.01.12" + mID + ".name", "热%material锭");
        addStringLocalization("gt.metaitem.01.13" + mID + ".name", "双重%material锭");
        addStringLocalization("gt.metaitem.01.14" + mID + ".name", "三重%material锭");
        addStringLocalization("gt.metaitem.01.15" + mID + ".name", "四重%material锭");
        addStringLocalization("gt.metaitem.01.16" + mID + ".name", "五重%material锭");
        addStringLocalization("gt.metaitem.01.17" + mID + ".name", "%material板");
        addStringLocalization("gt.metaitem.01.18" + mID + ".name", "双重%material板");
        addStringLocalization("gt.metaitem.01.19" + mID + ".name", "三重%material板");
        addStringLocalization("gt.metaitem.01.20" + mID + ".name", "四重%material板");
        addStringLocalization("gt.metaitem.01.21" + mID + ".name", "五重%material板");
        addStringLocalization("gt.metaitem.01.22" + mID + ".name", "致密%material板");
        addStringLocalization("gt.metaitem.01.23" + mID + ".name", "%material杆");
        addStringLocalization("gt.metaitem.01.24" + mID + ".name", "%material弹簧");
        addStringLocalization("gt.metaitem.01.25" + mID + ".name", "%material滚珠");
        addStringLocalization("gt.metaitem.01.26" + mID + ".name", "%material螺栓");
        addStringLocalization("gt.metaitem.01.27" + mID + ".name", "%material螺丝");
        addStringLocalization("gt.metaitem.01.28" + mID + ".name", "%material环");
        addStringLocalization("gt.metaitem.01.29" + mID + ".name", "%material箔");
        addStringLocalization("gt.metaitem.01.31" + mID + ".name", "%material等离子单元");

        addStringLocalization("gt.metaitem.02." + mID + ".name", "%material剑刃");
        addStringLocalization("gt.metaitem.02.1" + mID + ".name", "%material镐头");
        addStringLocalization("gt.metaitem.02.2" + mID + ".name", "%material铲头");
        addStringLocalization("gt.metaitem.02.3" + mID + ".name", "%material斧头");
        addStringLocalization("gt.metaitem.02.4" + mID + ".name", "%material锄头");
        addStringLocalization("gt.metaitem.02.5" + mID + ".name", "%material锤头");
        addStringLocalization("gt.metaitem.02.6" + mID + ".name", "%material锉刀刃");
        addStringLocalization("gt.metaitem.02.7" + mID + ".name", "%material锯刃");
        addStringLocalization("gt.metaitem.02.8" + mID + ".name", "%material钻头");
        addStringLocalization("gt.metaitem.02.9" + mID + ".name", "%material链锯刃");
        addStringLocalization("gt.metaitem.02.10" + mID + ".name", "%material扳手顶");
        addStringLocalization("gt.metaitem.02.11" + mID + ".name", "%material万用铲头");
        addStringLocalization("gt.metaitem.02.12" + mID + ".name", "%material镰刀刃");
        addStringLocalization("gt.metaitem.02.13" + mID + ".name", "%material犁头");
        addStringLocalization("gt.metaitem.02.15" + mID + ".name", "%material圆锯锯刃");
        addStringLocalization("gt.metaitem.02.16" + mID + ".name", "%material涡轮扇叶");
        addStringLocalization("gt.metaitem.02.18" + mID + ".name", "%material外壳");
        addStringLocalization("gt.metaitem.02.19" + mID + ".name", "细%material导线");
        addStringLocalization("gt.metaitem.02.20" + mID + ".name", "小型%material齿轮");
        addStringLocalization("gt.metaitem.02.21" + mID + ".name", "%material转子");
        addStringLocalization("gt.metaitem.02.22" + mID + ".name", "长%material杆");
        addStringLocalization("gt.metaitem.02.23" + mID + ".name", "小型%material弹簧");
        addStringLocalization("gt.metaitem.02.24" + mID + ".name", "%material弹簧");
        addStringLocalization("gt.metaitem.02.31" + mID + ".name", "%material齿轮");

        addStringLocalization("gt.metaitem.03.6" + mID + ".name", "超致密%material板");

        addStringLocalization("gt.metaitem.99." + mID + ".name", "熔融%material单元");

        addStringLocalization("Material." + mName, localizedName);
        addStringLocalization("fluid.molten." + mName, "熔融" + localizedName);
        addStringLocalization("fluid.plasma." + mName, localizedName + "等离子体");
        addStringLocalization("gt.blockmachines.gt_frame_" + mName + ".name", "%material框架");
        addStringLocalization("gt.blockmachines.wire." + mName + ".01.name", "1x%material导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".02.name", "2x%material导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".04.name", "4x%material导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".08.name", "8x%material导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".12.name", "12x%material导线");
        addStringLocalization("gt.blockmachines.wire." + mName + ".16.name", "16x%material导线");
        addStringLocalization("gt.blockmachines.cable." + mName + ".01.name", "1x%material线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".02.name", "2x%material线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".04.name", "4x%material线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".08.name", "8x%material线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".12.name", "12x%material线缆");
        addStringLocalization("gt.blockmachines.cable." + mName + ".16.name", "16x%material线缆");

        if (isItemPipe) {
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + ".name", "%material物品管道");
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + "_huge.name", "巨型%material物品管道");
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + "_large.name", "大型%material物品管道");
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + "_small.name", "小型%material物品管道");
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + "_tiny.name", "微型%material物品管道");
        } else {
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + ".name", "%material流体管道");
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + "_huge.name", "巨型%material流体管道");
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + "_large.name", "大型%material流体管道");
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + "_small.name", "小型%material流体管道");
            addStringLocalization("gt.blockmachines.gt_pipe_" + mName + "_tiny.name", "微型%material流体管道");
        }
    }
}
