package com.science.gtnl.utils.enums;

import java.util.Locale;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.data.IMod;
import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

import cpw.mods.fml.common.Loader;
import lombok.Getter;

@Getter
public enum ModList implements IMod, ITargetMod {

    ScienceNotLeisure(ModIds.SCIENCE_NOT_LEISURE, Names.SCIENCE_NOT_LEISURE, false),
    TakoTech(ModIds.TAKO_TECH, Names.TAKO_TECH),
    EyeOfHarmonyBuffer(ModIds.EYE_OF_HARMONY_BUFFER, Names.EYE_OF_HARMONY_BUFFER),
    ProgrammableHatches(ModIds.PROGRAMMABLE_HATCHES, Names.PROGRAMMABLE_HATCHES),
    TwistSpaceTechnology(ModIds.TWIST_SPACE_TECHNOLOGY, Names.TWIST_SPACE_TECHNOLOGY),
    BoxPlusPlus(ModIds.BOX_PLUS_PLUS, Names.BOX_PLUS_PLUS),
    NHUtilities(ModIds.NH_UTILITIES, Names.NH_UTILITIES, "com.xir.NHUtilities.main.NHUtilitiesCore"),
    AE2Thing(ModIds.AE2_THING, Names.AE2_THING),
    QzMiner(ModIds.QZ_MINER, Names.QZ_MINER, false),
    OTHTechnology(ModIds.OTH_TECHNOLOGY, Names.OTH_TECHNOLOGY),
    Baubles(ModIds.BAUBLES, Names.BAUBLES, false),
    Overpowered(ModIds.OVER_POWERED, Names.OVER_POWERED),
    ThinkTech(ModIds.THINK_TECH, Names.THINK_TECH),
    VMTweak(ModIds.VMT_TWEAK, Names.VMT_TWEAK),
    ReAvaritia(ModIds.RE_AVARITIA, Names.RE_AVARITIA, false),
    Sudoku(ModIds.SUDOKU, Names.SUDOKU, false),
    GiveCount(ModIds.GIVE_COUNT, Names.GIVECOUNT, false),
    ChromaticTooltips(ModIds.CHROMATIC_TOOLTIPS, Names.CHROMATIC_TOOLTIPS, false),
    ChromaticTooltipsCompat(ModIds.CHROMATIC_TOOLTIPS_COMPAT, Names.CHROMATIC_TOOLTIPS_COMPAT, false),

    NewHorizonsCoreMod(ModIds.NEW_HORIZONS_CORE_MOD, Names.NEW_HORIZONS_CORE_MOD,
        "com.dreammaster.coremod.DreamCoreMod", false),
    GalaxySpace(ModIds.GALAXY_SPACE, Names.GALAXY_SPACE, false),
    BetterQuestingAPI(ModIds.BETTER_QUESTING_API, Names.BETTER_QUESTING_API, false),
    EnhancedLootBags(ModIds.ENHANCED_LOOT_BAGS, Names.ENHANCED_LOOT_BAGS, false),
    NotEnoughItems(ModIds.NOT_ENOUGH_ITEMS, Names.NOT_ENOUGH_ITEMS, false),
    NotEnoughEnergistics(ModIds.NOT_ENOUGH_ENERGISTICS, Names.NOT_ENOUGH_ENERGISTICS, false),
    NEICustomDiagrams(ModIds.N_E_I_CUSTOM_DIAGRAMS, Names.N_E_I_CUSTOM_DIAGRAMS, false),
    AvaritiaAddons(ModIds.AVARITIA_ADDONS, Names.AVARITIA_ADDONS, false),
    EtFuturumRequiem(ModIds.ET_FUTURUM_REQUIEM, Names.ET_FUTURUM_REQUIEM, false),
    ForgeMultipart(ModIds.FORGE_MULTIPART, Names.FORGE_MULTIPART, false);

    public static final ModList[] VALUES = values();

    public static class ModIds {

        public static final String SCIENCE_NOT_LEISURE = "sciencenotleisure";
        public static final String EYE_OF_HARMONY_BUFFER = "eyeofharmonybuffer";
        public static final String PROGRAMMABLE_HATCHES = "programmablehatches";
        public static final String TWIST_SPACE_TECHNOLOGY = "TwistSpaceTechnology";
        public static final String BOX_PLUS_PLUS = "boxplusplus";
        public static final String NH_UTILITIES = "NHUtilities";
        public static final String AE2_THING = "ae2thing";
        public static final String QZ_MINER = "qz_miner";
        public static final String OTH_TECHNOLOGY = "123Technology";
        public static final String BAUBLES = "Baubles";
        public static final String OVER_POWERED = "Overpowered";
        public static final String THINK_TECH = "thinktech";
        public static final String VMT_TWEAK = "vmtweak";
        public static final String GIVE_COUNT = "givecount";
        public static final String RE_AVARITIA = "reavaritia";
        public static final String SUDOKU = "sudoku";
        public static final String TAKO_TECH = "TakoTech";
        public static final String CHROMATIC_TOOLTIPS = "chromatictooltips";
        public static final String CHROMATIC_TOOLTIPS_COMPAT = "chromatictooltipscompat";

        public static final String NEW_HORIZONS_CORE_MOD = "dreamcraft";
        public static final String GALAXY_SPACE = "GalaxySpace";
        public static final String BETTER_QUESTING_API = "bqapi";
        public static final String ENHANCED_LOOT_BAGS = "enhancedlootbags";
        public static final String NOT_ENOUGH_ITEMS = "NotEnoughItems";
        public static final String NOT_ENOUGH_ENERGISTICS = "neenergistics";
        public static final String N_E_I_CUSTOM_DIAGRAMS = "neicustomdiagram";
        public static final String AVARITIA_ADDONS = "avaritiaddons";
        public static final String ET_FUTURUM_REQUIEM = "etfuturum";
        public static final String FORGE_MULTIPART = "McMultipart";
    }

    public static class Names {

        public static final String SCIENCE_NOT_LEISURE = "Science Not Leisure";
        public static final String EYE_OF_HARMONY_BUFFER = "Eye Of Harmony Buffer";
        public static final String PROGRAMMABLE_HATCHES = "Programmable Hatches";
        public static final String TWIST_SPACE_TECHNOLOGY = "Twist Space Technology";
        public static final String BOX_PLUS_PLUS = "Box Plus Plus";
        public static final String NH_UTILITIES = "NH-Utilities";
        public static final String AE2_THING = "AE2 Things";
        public static final String QZ_MINER = "Qz Miner";
        public static final String OTH_TECHNOLOGY = "123Technology";
        public static final String BAUBLES = "Baubles";
        public static final String OVER_POWERED = "Overpowered";
        public static final String THINK_TECH = "Think Tech";
        public static final String VMT_TWEAK = "Void Miner Tweak";
        public static final String GIVECOUNT = "Give Count";
        public static final String RE_AVARITIA = "Re Avaritia";
        public static final String SUDOKU = "Sudoku";
        public static final String TAKO_TECH = "Tako Tech";

        public static final String CHROMATIC_TOOLTIPS = "Chromatic Tooltips";
        public static final String CHROMATIC_TOOLTIPS_COMPAT = "Chromatic Tooltips Compat";

        public static final String NEW_HORIZONS_CORE_MOD = "GT New Horizons Core Mod";
        public static final String GALAXY_SPACE = "Galaxy Space";
        public static final String BETTER_QUESTING_API = "Better Questing API";
        public static final String ENHANCED_LOOT_BAGS = "Enhanced Loot Bags";
        public static final String NOT_ENOUGH_ITEMS = "Not Enough Items";
        public static final String NOT_ENOUGH_ENERGISTICS = "Not Enough Energistics";
        public static final String N_E_I_CUSTOM_DIAGRAMS = "NEI Custom Diagrams";
        public static final String AVARITIA_ADDONS = "Avaritia Addons";
        public static final String ET_FUTURUM_REQUIEM = "Et Futurum Requiem";
        public static final String FORGE_MULTIPART = "Forge Multipart";
    }

    public final String ID;
    public final String resourceDomain;
    public final String displayName;
    public final boolean showInModList;
    private final TargetModBuilder targetBuilder;
    private Boolean modLoaded;

    ModList(String ID, String displayName) {
        this(ID, displayName, null, true);
    }

    ModList(String ID, String displayName, boolean showInModList) {
        this(ID, displayName, null, showInModList);
    }

    ModList(String ID, String displayName, String coreModClass) {
        this(ID, displayName, coreModClass, true);
    }

    ModList(String ID, String displayName, String coreModClass, boolean showInModList) {
        this.ID = ID;
        this.resourceDomain = ID.toLowerCase(Locale.ENGLISH);
        this.displayName = displayName;
        this.showInModList = showInModList;
        this.targetBuilder = new TargetModBuilder().setModId(ID)
            .setCoreModClass(coreModClass);
    }

    @NotNull
    @Override
    public TargetModBuilder getBuilder() {
        return targetBuilder;
    }

    @Override
    public boolean isModLoaded() {
        if (this.modLoaded == null) {
            this.modLoaded = Loader.isModLoaded(ID);
        }
        return this.modLoaded;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getResourceLocation() {
        return resourceDomain;
    }

    public String getResourcePath(String path) {
        return this.getResourceLocation(path)
            .toString();
    }

    public String getResourcePath(String... path) {
        return this.getResourceLocation(path)
            .toString();
    }

    public ResourceLocation getResourceLocation(String path) {
        return new ResourceLocation(this.resourceDomain, path);
    }

    public ResourceLocation getResourceLocation(String... path) {
        return new ResourceLocation(this.resourceDomain, String.join("/", path));
    }
}
