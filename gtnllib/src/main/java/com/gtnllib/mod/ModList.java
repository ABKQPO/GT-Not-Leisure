package com.gtnllib.mod;

import java.util.List;
import java.util.Locale;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.data.IMod;
import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

import cpw.mods.fml.common.Loader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

@Getter
public enum ModList implements IMod, ITargetMod {

    Angelica("angelica", "Angelica", "com.gtnewhorizons.angelica.loading.AngelicaTweaker", false),
    TakoTech("TakoTech", "Tako Tech"),
    EyeOfHarmonyBuffer("eyeofharmonybuffer", "Eye Of Harmony Buffer"),
    ProgrammableHatches("programmablehatches", "Programmable Hatches"),
    TwistSpaceTechnology("TwistSpaceTechnology", "Twist Space Technology"),
    BoxPlusPlus("boxplusplus", "Box Plus Plus"),
    NHUtilities("NHUtilities", "NH-Utilities", "com.xir.NHUtilities.main.NHUtilitiesCore"),
    AE2Thing("ae2thing", "AE2 Things"),
    QzMiner("qz_miner", "Qz Miner", false),
    OTHTechnology("123Technology", "123Technology"),
    Baubles("Baubles", "Baubles", false),
    Overpowered("Overpowered", "Overpowered"),
    ThinkTech("thinktech", "Think Tech"),
    VMTweak("vmtweak", "Void Miner Tweak"),
    Sudoku("sudoku", "Sudoku", false),
    GiveCount("givecount", "Give Count", false),
    ChromaticTooltips("chromatictooltips", "Chromatic Tooltips", false),
    ChromaticTooltipsCompat("chromatictooltipscompat", "Chromatic Tooltips Compat", false),
    AppliedEnergistics("appliedenergistics2", "Applied Energistics 2", "appeng.transformer.AppEngCore", false),
    WirelessCraftingTerminal("ae2wct", "Wireless Crafting Terminal", false),
    ThaumicEnergistics("thaumicenergistics", "Thaumic Energistics", false),

    NewHorizonsCoreMod("dreamcraft", "GT New Horizons Core Mod", "com.dreammaster.coremod.DreamCoreMod", false),
    GalaxySpace("GalaxySpace", "Galaxy Space", false),
    BetterQuestingAPI("bqapi", "Better Questing API", false),
    EnhancedLootBags("enhancedlootbags", "Enhanced Loot Bags", false),
    NotEnoughItems("NotEnoughItems", "Not Enough Items", false),
    InventoryBogoSorter("bogosorter", "Inventory Bogo Sorter", false),
    MouseTweaks("MouseTweaks", "Mouse Tweaks", false),
    CraftingTweaks("craftingtweaks", "Gregory Tweaks For Crafting", false),
    NotEnoughEnergistics("neenergistics", "Not Enough Energistics", false),
    NEICustomDiagrams("neicustomdiagram", "NEI Custom Diagrams", false),
    AvaritiaAddons("avaritiaddons", "Avaritia Addons", false),
    EtFuturumRequiem("etfuturum", "Et Futurum Requiem", false),
    ForgeMultipart("McMultipart", "Forge Multipart", false),
    MatterManipulator("matter-manipulator", "Matter Manipulator", false);

    public static final ModList[] VALUES = values();

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

    public static List<ModList> loadedListedMods() {
        List<ModList> loaded = new ObjectArrayList<>();
        for (ModList mod : VALUES) {
            if (mod.showInModList && mod.isModLoaded()) loaded.add(mod);
        }
        return loaded;
    }

    public static boolean allListedModsLoaded() {
        for (ModList mod : VALUES) {
            if (mod.showInModList && !mod.isModLoaded()) return false;
        }
        return true;
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
