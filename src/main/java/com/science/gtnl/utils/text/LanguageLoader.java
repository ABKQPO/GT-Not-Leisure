package com.science.gtnl.utils.text;

import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.Utils;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.FMLCommonHandler;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;

public class LanguageLoader {

    public static void registry() {
        if (!FMLCommonHandler.instance()
            .getCurrentLanguage()
            .equals("zh_CN")) return;

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
        String mPipeName = unlocalizedName.replace(" ", "_");

        addStringLocalization("Material." + mName, localizedName);
        addStringLocalization("bw.werkstoff." + aWerkstoff.getmID() + ".name", localizedName);

        if (aWerkstoff.hasItemType(OrePrefixes.cellMolten)) {
            addStringLocalization("fluid.molten." + unlocalizedName, "熔融" + localizedName);
        }
        if (aWerkstoff.hasItemType(OrePrefixes.cell)) {
            addStringLocalization("fluid." + unlocalizedName, localizedName);
        }

        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt01, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".01.name", "1x%material导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt02, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".02.name", "2x%material导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt04, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".04.name", "4x%material导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt08, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".08.name", "8x%material导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt12, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".12.name", "12x%material导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt16, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.wire." + unlocalizedName + ".16.name", "16x%material导线");
        }

        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt01, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".01.name", "1x%material线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt02, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".02.name", "2x%material线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt04, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".04.name", "4x%material线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt08, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".08.name", "8x%material线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt12, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".12.name", "12x%material线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt16, aWerkstoff, 1) != null) {
            addStringLocalization("gt.blockmachines.cable." + unlocalizedName + ".16.name", "16x%material线缆");
        }

        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeTiny, aWerkstoff, 1) != null) {
            if (isItemPipe) {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + ".name", "%material物品管道");
            } else {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + ".name", "%material流体管道");
            }
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeTiny, aWerkstoff, 1) != null) {
            if (isItemPipe) {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_tiny.name", "%material物品管道");
            } else {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_tiny.name", "%material流体管道");
            }
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeSmall, aWerkstoff, 1) != null) {
            if (isItemPipe) {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_small.name", "%material物品管道");
            } else {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_small.name", "%material流体管道");
            }
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeMedium, aWerkstoff, 1) != null) {
            if (isItemPipe) {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_medium.name", "%material物品管道");
            } else {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_medium.name", "%material流体管道");
            }
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeLarge, aWerkstoff, 1) != null) {
            if (isItemPipe) {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_large.name", "%material物品管道");
            } else {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_large.name", "%material流体管道");
            }
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeHuge, aWerkstoff, 1) != null) {
            if (isItemPipe) {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_huge.name", "%material物品管道");
            } else {
                addStringLocalization("gt.blockmachines.gt_pipe_" + mPipeName + "_huge.name", "%material流体管道");
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

    public static String addStringLocalization(String trimmedKey, String text) {
        return Utils.storeTranslation(trimmedKey, text);
    }
}
