package com.science.gtnl.utils.structure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.IStructureElement.BlocksToPlace;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IMultiblockTooltipRules;
import com.science.gtnl.api.IMultiblockTooltipRules.HeatRules;
import com.science.gtnl.api.IMultiblockTooltipRules.TieredPart;
import com.science.gtnl.api.IMultiblockTooltipRules.TooltipStats;
import com.science.gtnl.utils.enums.GTNLStructureChannels;
import com.science.gtnl.utils.structure.InfoSummary.BonusStat;
import com.science.gtnl.utils.structure.InfoSummary.TierBonus;
import com.science.gtnl.utils.structure.InfoSummary.TierThreshold;
import com.science.gtnl.utils.structure.StructureSummary.BlockLine;
import com.science.gtnl.utils.structure.StructureSummary.ControllerPosition;
import com.science.gtnl.utils.structure.StructureSummary.HatchLine;
import com.science.gtnl.utils.structure.StructureSummary.MaterialTier;
import com.science.gtnl.utils.structure.StructureSummary.Section;
import com.science.gtnl.utils.structure.StructureSummary.Size;
import com.science.gtnl.utils.structure.StructureTooltipHooks.CheckRecord;
import com.science.gtnl.utils.structure.StructureTooltipHooks.HatchRule;
import com.science.gtnl.utils.structure.StructureTooltipHooks.PieceCall;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.structure.IStructureChannels;
import gregtech.api.util.tooltip.MarkdownTooltipLoader;
import gregtech.api.util.tooltip.TooltipHelper;
import gregtech.common.misc.GTStructureChannels;

public class MultiblockTooltipReader {

    private static final int MAX_STRUCTURE_TIERS = 5;
    private static final int MAX_PROJECTOR_STACK = 64;
    // Why: two steps that add the same blocks tell a repeated layer apart from a one-off larger variant.
    private static final int MIN_LAYERED_SIZES = 3;
    private static final int MAX_INFO_LINES = 64;
    private static final Pattern FORMAT_PLACEHOLDER = Pattern.compile("%(\\d+\\$)?[sd]");
    private static final int TIER_PROBES = 2;
    // Why: four tiers tell a steady step or factor apart from curves such as 2^tier, which three tiers cannot.
    private static final int BONUS_PROBE_TIERS = 4;
    private static final double BONUS_TOLERANCE = 1e-6;
    private static final int FIRST_VOLTAGE_PROBE_TIER = 1;
    private static final int LOWEST_PROBE_TIER = 0;
    private static final int FIRST_GLASS_PROBE_TIER = 3;
    // Why: GTM machines start their parallel controller bonus one tier late, at MV.
    public static final int FIRST_PARALLEL_CONTROLLER_PROBE_TIER = 2;
    private static final int MAX_JOINED_TIER_NAMES = 2;
    public static final String NAME_SEPARATOR = " / ";
    private static final String GTNL_KEY_PREFIX = "gtnl.";
    private static final String MACHINE_KEY_PREFIX = "gtnl.machine.";
    private static final String NAME_KEY_SUFFIX = ".name";
    private static final String RECIPE_TYPE_SUFFIX = ".recipe_type";
    private static final Pattern ACRONYM_END = Pattern.compile("([A-Z]+)([A-Z][a-z])");
    private static final Pattern LOWER_TO_UPPER = Pattern.compile("([a-z0-9])([A-Z])");
    private static final Map<String, IStructureChannels> KNOWN_CHANNELS = knownChannels();
    private static final Set<String> NOT_CASING_CHANNELS = new HashSet<>(
        Arrays.asList(
            GTStructureChannels.BOROGLASS.get(),
            GTStructureChannels.HEATING_COIL.get(),
            GTNLStructureChannels.STRUCTURE_RENDER.get()));
    private static final int MAX_MACHINE_MODES = 16;
    // Why: StructureLib only returns bare lang keys, so hatches are recognised by the key families used for hatches.
    private static final String[] HATCH_KEY_PREFIXES = { "GT5U.MBTT.", "gt.blockmachines.", "hatch_type_",
        "gtnl.structure.hatch.", "gtnl.hatch." };
    private static final HatchElement[] HATCH_ORDER = { HatchElement.Energy, HatchElement.ExoticEnergy,
        HatchElement.MultiAmpEnergy, HatchElement.Dynamo, HatchElement.ExoticDynamo, HatchElement.Maintenance,
        HatchElement.Muffler, HatchElement.InputBus, HatchElement.InputHatch, HatchElement.OutputBus,
        HatchElement.OutputHatch };

    private MultiblockTooltipReader() {}

    private static Map<String, IStructureChannels> knownChannels() {
        Map<String, IStructureChannels> channels = new HashMap<>();
        // Why: several GT channels share the "casing" id, and GTNL machines only use it as TIER_CASING.
        channels.put(GTStructureChannels.TIER_CASING.get(), GTStructureChannels.TIER_CASING);
        for (IStructureChannels channel : GTStructureChannels.values()) channels.putIfAbsent(channel.get(), channel);
        for (IStructureChannels channel : GTNLStructureChannels.values()) channels.putIfAbsent(channel.get(), channel);
        return channels;
    }

    public static <T extends MTEEnhancedMultiBlockBase<T>> StructureSummary readStructure(T machine) {
        try {
            return withPrototype(machine, MultiblockTooltipReader::readPrototype);
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the structure of {} for its tooltip", nameOf(machine), e);
            return null;
        }
    }

    // Why: NEI warms tooltips on worker threads, so two threads can dry run the same prototype at once.
    public static <T extends MTEEnhancedMultiBlockBase<T>, R> R withPrototype(T machine, Function<T, R> read) {
        T prototype = prototypeOf(machine);
        synchronized (prototype) {
            return read.apply(prototype);
        }
    }

    // Why: GTNL machines name their lang keys after their name key or, when it is GT's default, after the class.
    public static String keyPrefixOf(MTEEnhancedMultiBlockBase<?> machine) {
        String nameKey = machine.getLocalNameKey();
        if (nameKey.startsWith(GTNL_KEY_PREFIX) && nameKey.endsWith(NAME_KEY_SUFFIX)) {
            return nameKey.substring(0, nameKey.length() - NAME_KEY_SUFFIX.length());
        }
        String fromClass = MACHINE_KEY_PREFIX + snakeCase(
            machine.getClass()
                .getSimpleName());
        boolean known = StatCollector.canTranslate(fromClass + NAME_KEY_SUFFIX)
            || StatCollector.canTranslate(fromClass + RECIPE_TYPE_SUFFIX);
        return known ? fromClass : null;
    }

    private static String snakeCase(String name) {
        String split = LOWER_TO_UPPER.matcher(
            ACRONYM_END.matcher(name)
                .replaceAll("$1_$2"))
            .replaceAll("$1_$2");
        return split.toLowerCase(Locale.ROOT);
    }

    public static <T extends MTEEnhancedMultiBlockBase<T>> InfoSummary readInfo(T machine, String keyPrefix,
        StructureSummary structure) {
        try {
            return withPrototype(machine, controller -> readPrototypeInfo(machine, controller, keyPrefix, structure));
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the information of {} for its tooltip", nameOf(machine), e);
            return null;
        }
    }

    private static <T extends MTEEnhancedMultiBlockBase<T>> InfoSummary readPrototypeInfo(T machine, T controller,
        String keyPrefix, StructureSummary structure) {
        IMultiblockTooltipRules rules = controller instanceof IMultiblockTooltipRules machineRules ? machineRules
            : null;
        TooltipStats stats = null;
        List<TierBonus> bonuses = Collections.emptyList();
        List<TierThreshold> perfectFrom = Collections.emptyList();
        OptionalInt tierSkips = OptionalInt.empty();
        try {
            if (rules != null) stats = rules.getTooltipStats();
            if (stats != null && structure != null) {
                List<TieredPart> parts = probedParts(structure.channels());
                bonuses = tierBonuses(rules, parts, stats);
                perfectFrom = perfectOverclockThresholds(rules, parts, stats, structure.casingTierNames());
            }
            if (stats != null) tierSkips = rules.getTooltipTierSkips();
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the numbers of {} for its tooltip", nameOf(machine), e);
        }
        String machineType = keyPrefix == null ? null : translatedOrNull(keyPrefix + RECIPE_TYPE_SUFFIX);
        if (machineType == null) machineType = recipeMapNames(machine, controller);
        List<String> lines = keyPrefix == null ? new ArrayList<>()
            : ownLines(keyPrefix, tooltipVars(machine, rules, stats));
        List<String> hatchKeys = structure == null ? Collections.emptyList() : structure.hatchKeys();
        List<String> features = rules == null ? new ArrayList<>() : featureLines(machine, rules);
        HeatRules heat = rules == null || stats == null ? null : heatRules(machine, rules);
        List<List<String>> groups = rules == null ? new ArrayList<>() : lineGroups(machine, rules, hatchKeys);
        boolean supportAny = hatchKeys.contains(HatchElement.ExoticEnergy.getDescriptionLangKey());
        boolean supportMultiAmp = !supportAny
            && hatchKeys.contains(HatchElement.MultiAmpEnergy.getDescriptionLangKey());
        return new InfoSummary(
            machineType,
            stats,
            bonuses,
            lines,
            features,
            heat,
            perfectFrom,
            tierSkips,
            pollutionOf(controller),
            groups,
            supportAny,
            supportMultiAmp);
    }

    private static <T extends MTEEnhancedMultiBlockBase<T>> HeatRules heatRules(T machine,
        IMultiblockTooltipRules rules) {
        try {
            return rules.getTooltipHeatRules();
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the heat rules of {} for its tooltip", nameOf(machine), e);
            return null;
        }
    }

    // Why: a machine without a recipe type key is named after the recipe maps of all its modes.
    private static <T extends MTEEnhancedMultiBlockBase<T>> String recipeMapNames(T machine, T controller) {
        Set<String> names = new LinkedHashSet<>();
        int original = controller.machineMode;
        try {
            for (int mode : machineModes(controller)) {
                controller.machineMode = mode;
                for (RecipeMap<?> map : controller.getAvailableRecipeMaps()) {
                    names.add(StatCollector.translateToLocal(map.unlocalizedName));
                }
            }
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the recipe maps of {} for its tooltip", nameOf(machine), e);
        } finally {
            controller.machineMode = original;
        }
        return names.isEmpty() ? null : String.join(NAME_SEPARATOR, names);
    }

    public static List<Integer> machineModes(MTEEnhancedMultiBlockBase<?> controller) {
        if (!controller.supportsMachineModeSwitch()) return Collections.singletonList(controller.machineMode);
        int original = controller.machineMode;
        List<Integer> modes = new ArrayList<>();
        try {
            for (int mode = 0; modes.size() < MAX_MACHINE_MODES
                && !modes.contains(mode); mode = controller.nextMachineMode()) {
                modes.add(mode);
                controller.machineMode = mode;
            }
        } finally {
            controller.machineMode = original;
        }
        return modes;
    }

    private static <T extends MTEEnhancedMultiBlockBase<T>> List<String> featureLines(T machine,
        IMultiblockTooltipRules rules) {
        try {
            return new ArrayList<>(rules.getTooltipFeatureLines());
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the feature lines of {} for its tooltip", nameOf(machine), e);
            return new ArrayList<>();
        }
    }

    private static <T extends MTEEnhancedMultiBlockBase<T>> List<List<String>> lineGroups(T machine,
        IMultiblockTooltipRules rules, List<String> hatchKeys) {
        List<List<String>> groups = new ArrayList<>();
        try {
            List<String> hatchLines = new ArrayList<>();
            for (String hatchKey : hatchKeys) hatchLines.addAll(rules.getTooltipHatchLines(hatchKey));
            if (!hatchLines.isEmpty()) groups.add(hatchLines);
            List<String> upgradeLines = rules.getTooltipUpgradeLines();
            if (!upgradeLines.isEmpty()) groups.add(upgradeLines);
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG
                .warn("Cannot read the hatch and upgrade lines of {} for its tooltip", nameOf(machine), e);
        }
        return groups;
    }

    private static List<String> ownLines(String keyPrefix, Map<String, Object> vars) {
        List<String> lines = markdownLines(keyPrefix, vars);
        if (lines != null) return lines;
        lines = numberedLines(keyPrefix + ".tooltip");
        if (lines.isEmpty()) lines = numberedLines(keyPrefix + ".info");
        if (lines.isEmpty()) addPlainLine(lines, keyPrefix + ".info");
        return lines;
    }

    private static List<String> markdownLines(String keyPrefix, Map<String, Object> vars) {
        String name = keyPrefix.substring(keyPrefix.lastIndexOf('.') + 1)
            .replace('_', '-');
        try {
            return new ArrayList<>(
                MarkdownTooltipLoader.STANDARD
                    .loadStandardPath(new ResourceLocation(ScienceNotLeisure.RESOURCE_ROOT_ID, name), vars));
        } catch (RuntimeException e) {
            // Why: GT throws with an IOException cause when no language has the file, as for lang-file descriptions.
            if (!(e.getCause() instanceof IOException)) {
                ScienceNotLeisure.LOG.warn("Cannot parse the markdown tooltip {}", name, e);
            }
            return null;
        }
    }

    private static <T extends MTEEnhancedMultiBlockBase<T>> Map<String, Object> tooltipVars(T machine,
        IMultiblockTooltipRules rules, TooltipStats stats) {
        Map<String, Object> vars = new HashMap<>();
        if (stats != null) {
            vars.put("parallels", String.valueOf(stats.parallels()));
            vars.put("speed", TooltipHelper.percentageFormat.format(stats.speed()));
            vars.put("energy_usage", TooltipHelper.percentageFormat.format(stats.energyUsage()));
        }
        try {
            if (rules != null) vars.putAll(rules.getTooltipVars());
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the tooltip values of {}", nameOf(machine), e);
        }
        return vars;
    }

    private static List<String> numberedLines(String prefix) {
        List<String> lines = new ArrayList<>();
        for (int index = 0; index < MAX_INFO_LINES; index++) addPlainLine(lines, prefix + "." + index);
        return lines;
    }

    private static void addPlainLine(List<String> lines, String key) {
        if (!StatCollector.canTranslate(key)) return;
        String line = StatCollector.translateToLocal(key);
        // Why: a line with a format placeholder needs values from code, so the machine adds it itself.
        if (!FORMAT_PLACEHOLDER.matcher(line)
            .find()) lines.add(line);
    }

    private static int pollutionOf(MTEEnhancedMultiBlockBase<?> machine) {
        try {
            return machine.getPollutionPerSecond(null);
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the pollution of {} for its tooltip", nameOf(machine), e);
            return 0;
        }
    }

    // Why: a machine without processing logic runs no recipes, so recipe numbers would mislead.
    public static boolean runsRecipes(MTEEnhancedMultiBlockBase<?> machine, Supplier<ProcessingLogic> logic) {
        try {
            return logic.get() != null;
        } catch (RuntimeException e) {
            // Why: only a machine that builds a processing logic can fail to build one on the worldless prototype.
            ScienceNotLeisure.LOG.debug("Cannot build the processing logic of {} for its tooltip", nameOf(machine), e);
            return true;
        }
    }

    private static List<TieredPart> probedParts(List<IStructureChannels> channels) {
        List<TieredPart> parts = new ArrayList<>();
        parts.add(TieredPart.VOLTAGE);
        if (channels.contains(GTStructureChannels.HEATING_COIL)) parts.add(TieredPart.COIL);
        if (channels.contains(GTStructureChannels.BOROGLASS)) parts.add(TieredPart.GLASS);
        parts.add(TieredPart.CASING);
        return parts;
    }

    private static List<TierBonus> tierBonuses(IMultiblockTooltipRules rules, List<TieredPart> parts,
        TooltipStats base) {
        List<TierBonus> bonuses = new ArrayList<>();
        for (TieredPart part : parts) addTierBonuses(bonuses, rules, part);
        addSwitchBonuses(bonuses, rules, TieredPart.HIGH_PRESSURE, base);
        return bonuses;
    }

    private static List<TierThreshold> perfectOverclockThresholds(IMultiblockTooltipRules rules, List<TieredPart> parts,
        TooltipStats base, List<String> casingTierNames) {
        if (base.perfectOverclock()) return Collections.emptyList();
        List<TierThreshold> thresholds = new ArrayList<>();
        for (TieredPart part : parts) {
            int firstTier = firstProbeTier(part);
            TooltipStats[] probes = probeTiers(rules, part, part.maxTier + 1 - firstTier);
            if (probes == null) continue;
            int from = -1;
            for (int index = probes.length - 1; index >= 0 && probes[index].perfectOverclock(); index--) from = index;
            if (from < 0) continue;
            int tier = firstTier + from;
            thresholds.add(new TierThreshold(part, tier, tierName(part, tier, casingTierNames)));
        }
        return thresholds;
    }

    private static String tierName(TieredPart part, int tier, List<String> casingTierNames) {
        return switch (part) {
            case VOLTAGE, GLASS, PARALLEL_CONTROLLER -> TooltipHelper.voltageTierText(tier, false);
            case COIL -> HeatingCoilLevel.getDisplayNameFromHeat(
                (int) HeatingCoilLevel.getFromTier((byte) tier)
                    .getHeat(),
                true) + EnumChatFormatting.GRAY;
            case CASING -> tier < casingTierNames.size()
                ? EnumChatFormatting.WHITE + casingTierNames.get(tier) + EnumChatFormatting.GRAY
                : null;
            case HIGH_PRESSURE -> null;
        };
    }

    private static void addSwitchBonuses(List<TierBonus> bonuses, IMultiblockTooltipRules rules, TieredPart part,
        TooltipStats base) {
        TooltipStats switched = rules.getTooltipStatsAtTier(part, 1);
        if (switched == null) return;
        addSwitchBonus(bonuses, part, BonusStat.PARALLEL, switched.parallels() - base.parallels());
        addSwitchBonus(bonuses, part, BonusStat.SPEED, switched.speed() - base.speed());
        addSwitchBonus(bonuses, part, BonusStat.ENERGY, switched.energyUsage() - base.energyUsage());
    }

    private static void addSwitchBonus(List<TierBonus> bonuses, TieredPart part, BonusStat stat, double change) {
        if (Double.isFinite(change) && !isNearly(change, 0))
            bonuses.add(new TierBonus(part, stat, false, change, false));
    }

    public static List<TierBonus> tierBonusesOf(IMultiblockTooltipRules rules, TieredPart part) {
        List<TierBonus> bonuses = new ArrayList<>();
        addTierBonuses(bonuses, rules, part);
        return bonuses;
    }

    private static void addTierBonuses(List<TierBonus> bonuses, IMultiblockTooltipRules rules, TieredPart part) {
        TooltipStats[] probes = probeTiers(rules, part, BONUS_PROBE_TIERS);
        if (probes == null) return;
        addBonus(bonuses, part, BonusStat.PARALLEL, values(probes, TooltipStats::parallels), true);
        Double speedStep = steadyStep(values(probes, TooltipStats::speed), false);
        // Why: only a fixed speed gain per tier reads as speed, like GT's autoclave; other formulas cut recipe time.
        if (speedStep != null) bonuses.add(new TierBonus(part, BonusStat.SPEED, false, speedStep, true));
        else addBonus(bonuses, part, BonusStat.DURATION, durations(probes), false);
        addBonus(bonuses, part, BonusStat.ENERGY, values(probes, TooltipStats::energyUsage), false);
    }

    // Why: probing tiers a built machine can have keeps formulas that clamp lower tiers from hiding the bonus.
    public static int firstProbeTier(TieredPart part) {
        return switch (part) {
            case VOLTAGE -> FIRST_VOLTAGE_PROBE_TIER;
            case COIL, CASING, HIGH_PRESSURE -> LOWEST_PROBE_TIER;
            case GLASS -> FIRST_GLASS_PROBE_TIER;
            case PARALLEL_CONTROLLER -> FIRST_PARALLEL_CONTROLLER_PROBE_TIER;
        };
    }

    public static TooltipStats[] probeTiers(IMultiblockTooltipRules rules, TieredPart part, int count) {
        TooltipStats[] probes = new TooltipStats[count];
        int firstTier = firstProbeTier(part);
        for (int index = 0; index < count; index++) {
            probes[index] = rules.getTooltipStatsAtTier(part, firstTier + index);
            if (probes[index] == null) return null;
        }
        return probes;
    }

    public static double[] values(TooltipStats[] probes, ToDoubleFunction<TooltipStats> stat) {
        double[] values = new double[probes.length];
        for (int tier = 0; tier < probes.length; tier++) values[tier] = stat.applyAsDouble(probes[tier]);
        return values;
    }

    // Why: recipe time steps are shown as the code writes them, such as 2% for a duration modifier lowered by 0.02.
    public static double[] durations(TooltipStats[] probes) {
        return values(probes, stats -> 1 / stats.speed());
    }

    private static void addBonus(List<TierBonus> bonuses, TieredPart part, BonusStat stat, double[] values,
        boolean whole) {
        Double step = steadyStep(values, whole);
        if (step != null) {
            bonuses.add(new TierBonus(part, stat, false, step, true));
            return;
        }
        Double factor = steadyFactor(values, whole);
        if (factor != null) bonuses.add(new TierBonus(part, stat, true, factor, true));
    }

    private static Double steadyStep(double[] values, boolean whole) {
        if (!allFinite(values)) return null;
        double step = values[1] - values[0];
        if (isNearly(step, 0) || whole && !isWhole(step)) return null;
        for (int tier = 2; tier < values.length; tier++) {
            if (!isNearly(values[tier] - values[tier - 1], step)) return null;
        }
        return step;
    }

    private static Double steadyFactor(double[] values, boolean whole) {
        if (!allFinite(values)) return null;
        for (double value : values) if (isNearly(value, 0)) return null;
        double factor = values[1] / values[0];
        if (isNearly(factor, 1) || whole && !isWhole(factor)) return null;
        for (int tier = 2; tier < values.length; tier++) {
            if (!isNearly(values[tier] / values[tier - 1], factor)) return null;
        }
        return factor;
    }

    public static boolean allFinite(double[] values) {
        for (double value : values) if (!Double.isFinite(value)) return false;
        return true;
    }

    private static boolean isWhole(double value) {
        return isNearly(value, Math.rint(value));
    }

    public static boolean isNearly(double a, double b) {
        return Math.abs(a - b) <= BONUS_TOLERANCE * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    private static String translatedOrNull(String key) {
        return key != null && StatCollector.canTranslate(key) ? StatCollector.translateToLocal(key) : null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends MTEEnhancedMultiBlockBase<T>> T prototypeOf(T machine) {
        IGregTechTileEntity base = machine.getBaseMetaTileEntity();
        if (base == null) return machine;
        // Why: the dry runs below must never touch a machine that is built in a world.
        IMetaTileEntity prototype = GregTechAPI.METATILEENTITIES[base.getMetaTileID()];
        return machine.getClass()
            .isInstance(prototype) ? (T) prototype : machine;
    }

    private static <T extends MTEEnhancedMultiBlockBase<T>> StructureSummary readPrototype(T controller) {
        IStructureDefinition<T> definition = controller.getStructureDefinition();
        Set<String> channelNames = new LinkedHashSet<>();
        List<List<PieceCall>> sizes = recordSizes(controller, channelNames);
        if (sizes.isEmpty()) {
            ScienceNotLeisure.LOG.warn("Cannot find the structure pieces of {} for its tooltip", nameOf(controller));
            return null;
        }
        IMultiblockTooltipRules rules = controller instanceof IMultiblockTooltipRules machineRules ? machineRules
            : null;
        int materialTiers = rules == null ? 0 : rules.getStructureTooltipMaterialTiers();
        CheckRecord checks = StructureTooltipHooks.recordChecks(controller);

        // Why: StructureLib shares one air and one non-air element, which '-', '+' and custom air characters all use.
        IStructureElement<T> air = StructureUtility.isAir();
        IStructureElement<T> anyBlock = StructureUtility.notAir();
        Predicate<IStructureElement<T>> solid = element -> element != air && element != anyBlock;
        List<Cell<T>> layer = layerCells(definition, sizes, solid);
        boolean layered = layer != null;
        List<List<PieceCall>> tiers = layered ? sizes.subList(0, 1) : structureTiers(sizes);
        List<PieceCall> largest = sizes.get(layered || tiers.size() > 1 ? sizes.size() - 1 : 0);
        List<List<Cell<T>>> tierCells = new ArrayList<>();
        for (List<PieceCall> tier : tiers) tierCells.add(readCells(definition, tier, solid));
        if (layered) tierCells.add(layer);
        List<List<Cell<T>>> largestCells = Collections.singletonList(readCells(definition, largest, solid));
        List<Cell<T>> airCells = readCells(definition, largest, element -> element == air);
        Map<String, HatchGroup> hatches = new LinkedHashMap<>();
        List<List<BlockGroup>> tierGroups = new ArrayList<>();
        for (List<Cell<T>> cells : tierCells) {
            tierGroups.add(readGroups(controller, cells, materialTiers, rules, hatches));
        }
        Map<IStructureElement<T>, Set<String>> elementChannels = new LinkedHashMap<>();
        for (List<Cell<T>> cells : tierCells) for (Cell<T> cell : cells) {
            elementChannels.computeIfAbsent(cell.element(), element -> new LinkedHashSet<>());
        }
        for (Map.Entry<IStructureElement<T>, Set<String>> entry : elementChannels.entrySet()) {
            StructureTooltipHooks.recordChannels(controller, entry.getKey(), entry.getValue());
            channelNames.addAll(entry.getValue());
        }
        Set<IStructureChannels> channels = new LinkedHashSet<>();
        for (String channelName : channelNames) {
            IStructureChannels channel = KNOWN_CHANNELS.get(channelName);
            if (channel != null) channels.add(channel);
        }
        applyCasingMinimum(tierGroups.get(0), checks.casingMinimums());

        List<Section> sections = new ArrayList<>();
        for (List<BlockGroup> groups : tierGroups) sections.add(toSection(groups, materialTiers, rules));
        IStructureChannels materialChannel = rules == null ? null : rules.getStructureTooltipMaterialTierChannel();
        Set<String> hatchKeys = new LinkedHashSet<>();
        for (HatchGroup hatch : hatches.values()) hatchKeys.addAll(hatch.keys);

        StructureSummary summary = new StructureSummary(
            size(tierCells.subList(0, 1)),
            size(largestCells),
            isHollow(largestCells, airCells),
            controllerPosition(tierCells.get(0)),
            toHatchLines(hatches, checks.hatchRules()),
            new ArrayList<>(hatchKeys),
            sections,
            new ArrayList<>(channels),
            casingTierNames(controller, elementChannels),
            layered);
        if (summary.hasMaterialTiers() && materialChannel != null
            && !summary.channels()
                .contains(materialChannel)) {
            summary.channels()
                .add(materialChannel);
        }
        return summary;
    }

    private static List<List<PieceCall>> recordSizes(MTEEnhancedMultiBlockBase<?> controller,
        Set<String> channelNames) {
        List<List<PieceCall>> sizes = new ArrayList<>();
        List<PieceCall> previous = Collections.emptyList();
        for (int stackSize = 1; stackSize <= MAX_PROJECTOR_STACK; stackSize++) {
            List<PieceCall> calls = StructureTooltipHooks.recordConstruct(controller, stackSize, channelNames);
            if (calls.isEmpty() || new HashSet<>(previous).containsAll(calls)) break;
            sizes.add(calls);
            previous = calls;
        }
        return sizes;
    }

    private static List<List<PieceCall>> structureTiers(List<List<PieceCall>> sizes) {
        if (sizes.size() > MAX_STRUCTURE_TIERS) return sizes.subList(0, 1);
        List<List<PieceCall>> tiers = new ArrayList<>();
        List<PieceCall> previous = Collections.emptyList();
        for (List<PieceCall> calls : sizes) {
            List<PieceCall> added = new ArrayList<>(calls);
            added.removeAll(previous);
            tiers.add(added);
            previous = calls;
        }
        return tiers;
    }

    // Why: a taller tower moves its top piece up instead of adding to it, so each size is compared as a whole.
    private static <T> List<Cell<T>> layerCells(IStructureDefinition<T> definition, List<List<PieceCall>> sizes,
        Predicate<IStructureElement<T>> kept) {
        if (sizes.size() < MIN_LAYERED_SIZES || isAdditive(sizes)) return null;
        Map<IStructureElement<T>, Integer> layer = null;
        Map<IStructureElement<T>, Integer> previous = null;
        for (List<PieceCall> calls : sizes) {
            Map<IStructureElement<T>, Integer> counts = elementCounts(readCells(definition, calls, kept));
            if (previous != null) {
                Map<IStructureElement<T>, Integer> added = addedCounts(counts, previous);
                if (added == null || added.isEmpty() || layer != null && !layer.equals(added)) return null;
                layer = added;
            }
            previous = counts;
        }
        List<Cell<T>> cells = new ArrayList<>();
        for (Map.Entry<IStructureElement<T>, Integer> entry : layer.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) cells.add(new Cell<>(entry.getKey(), 0, 0, 0));
        }
        return cells;
    }

    private static boolean isAdditive(List<List<PieceCall>> sizes) {
        for (int i = 1; i < sizes.size(); i++) {
            if (!new HashSet<>(sizes.get(i)).containsAll(sizes.get(i - 1))) return false;
        }
        return true;
    }

    private static <T> Map<IStructureElement<T>, Integer> elementCounts(List<Cell<T>> cells) {
        Map<Position, IStructureElement<T>> placed = new HashMap<>();
        for (Cell<T> cell : cells) placed.put(new Position(cell.a(), cell.b(), cell.c()), cell.element());
        Map<IStructureElement<T>, Integer> counts = new HashMap<>();
        for (IStructureElement<T> element : placed.values()) counts.merge(element, 1, Integer::sum);
        return counts;
    }

    private static <T> Map<IStructureElement<T>, Integer> addedCounts(Map<IStructureElement<T>, Integer> counts,
        Map<IStructureElement<T>, Integer> previous) {
        Map<IStructureElement<T>, Integer> added = new LinkedHashMap<>();
        Set<IStructureElement<T>> elements = new LinkedHashSet<>(previous.keySet());
        elements.addAll(counts.keySet());
        for (IStructureElement<T> element : elements) {
            int change = counts.getOrDefault(element, 0) - previous.getOrDefault(element, 0);
            if (change < 0) return null;
            if (change > 0) added.put(element, change);
        }
        return added;
    }

    private static <T> List<Cell<T>> readCells(IStructureDefinition<T> definition, List<PieceCall> calls,
        Predicate<IStructureElement<T>> kept) {
        List<Cell<T>> cells = new ArrayList<>();
        for (PieceCall call : calls) {
            int a = 0, b = 0, c = 0;
            for (IStructureElement<T> element : definition.getStructureFor(call.piece())) {
                if (element.isNavigating()) {
                    a = (element.resetA() ? 0 : a) + element.getStepA();
                    b = (element.resetB() ? 0 : b) + element.getStepB();
                    c = (element.resetC() ? 0 : c) + element.getStepC();
                    continue;
                }
                if (kept.test(element)) {
                    cells.add(
                        new Cell<>(
                            element,
                            a - call.horizontalOffset(),
                            b - call.verticalOffset(),
                            c - call.depthOffset()));
                }
                a++;
            }
        }
        return cells;
    }

    private static <T> Size size(List<List<Cell<T>>> tierCells) {
        int[] min = { 0, 0, 0 };
        int[] max = { 0, 0, 0 };
        for (List<Cell<T>> cells : tierCells) {
            for (Cell<T> cell : cells) {
                int[] position = { cell.a(), cell.b(), cell.c() };
                for (int axis = 0; axis < 3; axis++) {
                    min[axis] = Math.min(min[axis], position[axis]);
                    max[axis] = Math.max(max[axis], position[axis]);
                }
            }
        }
        return new Size(max[0] - min[0] + 1, max[1] - min[1] + 1, max[2] - min[2] + 1);
    }

    // Why: GT marks a structure hollow when it needs air inside, so air walled in on both horizontal axes counts.
    private static <T> boolean isHollow(List<List<Cell<T>>> tierCells, List<Cell<T>> airCells) {
        Map<Line, int[]> spansAlongA = new HashMap<>();
        Map<Line, int[]> spansAlongC = new HashMap<>();
        for (List<Cell<T>> cells : tierCells) {
            for (Cell<T> cell : cells) {
                widen(spansAlongA, new Line(cell.b(), cell.c()), cell.a());
                widen(spansAlongC, new Line(cell.a(), cell.b()), cell.c());
            }
        }
        for (Cell<T> air : airCells) {
            if (isInside(spansAlongA.get(new Line(air.b(), air.c())), air.a())
                && isInside(spansAlongC.get(new Line(air.a(), air.b())), air.c())) return true;
        }
        return false;
    }

    private static void widen(Map<Line, int[]> spans, Line line, int value) {
        int[] span = spans.computeIfAbsent(line, key -> new int[] { value, value });
        span[0] = Math.min(span[0], value);
        span[1] = Math.max(span[1], value);
    }

    private static boolean isInside(int[] span, int value) {
        return span != null && span[0] < value && value < span[1];
    }

    private static <T> ControllerPosition controllerPosition(List<Cell<T>> cells) {
        int minA = 0, maxA = 0, maxB = 0;
        boolean front = true;
        for (Cell<T> cell : cells) {
            minA = Math.min(minA, cell.a());
            maxA = Math.max(maxA, cell.a());
            maxB = Math.max(maxB, cell.b());
            if (cell.a() == 0 && cell.b() == 0 && cell.c() < 0) front = false;
        }
        return new ControllerPosition(maxB + 1, front && -minA == maxA);
    }

    private static <T> List<BlockGroup> readGroups(T controller, List<Cell<T>> cells, int materialTiers,
        IMultiblockTooltipRules rules, Map<String, HatchGroup> hatches) {
        Map<IStructureElement<T>, Integer> counts = new LinkedHashMap<>();
        for (Cell<T> cell : cells) counts.merge(cell.element(), 1, Integer::sum);
        List<Map.Entry<IStructureElement<T>, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(
            Map.Entry.<IStructureElement<T>, Integer>comparingByValue()
                .reversed());

        Map<List<String>, BlockGroup> blocks = new LinkedHashMap<>();
        for (Map.Entry<IStructureElement<T>, Integer> entry : entries) {
            int count = entry.getValue();
            List<String> hatchKeys = new ArrayList<>();
            List<String> blockKeys = new ArrayList<>();
            for (String key : describe(controller, entry.getKey())) {
                if (isHatchKey(key)) hatchKeys.add(key);
                else blockKeys.add(key);
            }

            List<String> blockNames = Collections.emptyList();
            if (!blockKeys.isEmpty()) {
                List<String> tierNames = tierNames(controller, entry.getKey(), Math.max(materialTiers, TIER_PROBES));
                blockNames = blockNames(controller, entry.getKey(), blockKeys, tierNames);
                boolean byMaterialTier = materialTiers > 0 && tierNames != null && blockKeys.size() > 1;
                List<String> names = blockNames;
                BlockGroup group = blocks.computeIfAbsent(
                    byMaterialTier ? tierNames : names,
                    key -> new BlockGroup(names, tierNames, byMaterialTier));
                if (hatchKeys.isEmpty()) group.fixed += count;
                else group.replaceable += count;
            }

            Set<String> countedLines = new HashSet<>();
            for (String hatchKey : hatchKeys) {
                String lineKey = rules == null ? hatchKey : rules.getStructureTooltipHatchLine(hatchKey);
                HatchGroup hatch = hatches.computeIfAbsent(lineKey, key -> new HatchGroup());
                hatch.keys.add(hatchKey);
                hatch.names.add(StatCollector.translateToLocal(lineKey));
                hatch.order = Math.min(hatch.order, hatchOrder(hatchKey));
                if (!blockNames.isEmpty()) {
                    hatch.replacesBlocks = true;
                    hatch.locations.add(
                        StatCollector.translateToLocalFormatted(
                            "gtnl.structure.tooltip.hatch.replaces",
                            String.join(NAME_SEPARATOR, blockNames)));
                } else if (countedLines.add(lineKey)) {
                    hatch.dedicated += count;
                    hatch.locations.add(StatCollector.translateToLocal("gtnl.structure.tooltip.hatch.dedicated"));
                }
            }
        }
        return new ArrayList<>(blocks.values());
    }

    private static <T> List<String> describe(T controller, IStructureElement<T> element) {
        try {
            List<String> keys = element.getDescription(controller);
            if (keys != null && !keys.isEmpty()) return keys;
            // Why: ofFrame(Materials) has no description but reports its frame stack without touching the world.
            List<String> placed = placedNames(controller, element, 1);
            return placed == null ? Collections.emptyList() : placed;
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG
                .warn("Cannot describe a structure element of {} for its tooltip", nameOf(controller), e);
            return Collections.emptyList();
        }
    }

    private static <T> List<String> blockNames(T controller, IStructureElement<T> element, List<String> keys,
        List<String> tierNames) {
        List<String> names = new ArrayList<>();
        for (String key : keys) {
            // Why: some items, such as GT frame boxes, build their names in code and have no lang entry for them.
            if (!StatCollector.canTranslate(key)) return placedNamesOr(controller, element, tierNames, keys);
            names.add(StatCollector.translateToLocal(key));
        }
        return names;
    }

    private static <T> List<String> placedNamesOr(T controller, IStructureElement<T> element, List<String> tierNames,
        List<String> fallback) {
        if (tierNames != null) return tierNames;
        try {
            List<String> placed = placedNames(controller, element, 1);
            if (placed != null && !placed.isEmpty()) return placed;
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.debug("Cannot name a structure element of {} for its tooltip", nameOf(controller), e);
        }
        return fallback;
    }

    private static <T> List<String> tierNames(T controller, IStructureElement<T> element, int tiers) {
        List<String> names = placedTierNames(controller, element, tiers);
        return names != null && names.size() == tiers ? names : null;
    }

    private static <T> List<String> placedTierNames(T controller, IStructureElement<T> element, int tiers) {
        List<String> names = new ArrayList<>();
        try {
            for (int tier = 1; tier <= tiers; tier++) {
                List<String> placed = placedNames(controller, element, tier);
                if (placed == null || placed.isEmpty()) break;
                names.add(String.join(NAME_SEPARATOR, placed));
            }
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.debug("Cannot probe the block tiers of {} for its tooltip", nameOf(controller), e);
            return null;
        }
        // Why: a tiered element places a different block for each projector tier; a choice of blocks places them all.
        return new HashSet<>(names).size() > 1 ? names : null;
    }

    // Why: casing tiers are named after the blocks of the one tiered element whose sub-channel is not glass or coils.
    private static <T> List<String> casingTierNames(T controller,
        Map<IStructureElement<T>, Set<String>> elementChannels) {
        List<String> found = Collections.emptyList();
        for (Map.Entry<IStructureElement<T>, Set<String>> entry : elementChannels.entrySet()) {
            if (!hasCasingChannel(entry.getValue())) continue;
            List<String> names = placedTierNames(controller, entry.getKey(), TieredPart.CASING.maxTier + 1);
            if (names == null) continue;
            if (!found.isEmpty()) return Collections.emptyList();
            found = names;
        }
        return found;
    }

    private static boolean hasCasingChannel(Set<String> channels) {
        for (String channel : channels) {
            if (!NOT_CASING_CHANNELS.contains(channel)) return true;
        }
        return false;
    }

    private static <T> List<String> placedNames(T controller, IStructureElement<T> element, int tier) {
        BlocksToPlace placement = element
            .getBlocksToPlace(controller, null, 0, 0, 0, new ItemStack(Items.stick, tier), null);
        if (placement == null || placement.getStacks() == null) return null;
        List<String> names = new ArrayList<>();
        for (ItemStack stack : placement.getStacks()) names.add(stack.getDisplayName());
        return names;
    }

    private static void applyCasingMinimum(List<BlockGroup> groups, List<Integer> minimums) {
        if (minimums.size() != 1) return;
        BlockGroup target = null;
        for (BlockGroup group : groups) {
            if (group.replaceable > 0 && (target == null || group.replaceable > target.replaceable)) target = group;
        }
        if (target != null) target.minimum = Math.min(minimums.get(0), target.replaceable);
    }

    private static Section toSection(List<BlockGroup> groups, int materialTiers, IMultiblockTooltipRules rules) {
        List<BlockGroup> sorted = new ArrayList<>(groups);
        sorted.sort(
            Comparator.comparingInt(BlockGroup::total)
                .reversed());
        List<BlockLine> blocks = new ArrayList<>();
        List<BlockGroup> byTier = new ArrayList<>();
        for (BlockGroup group : sorted) {
            if (group.byMaterialTier) byTier.add(group);
            else blocks.add(new BlockLine(group.countText(), group.displayName(), group.tierNames != null));
        }

        List<MaterialTier> tiers = new ArrayList<>();
        if (!byTier.isEmpty()) {
            for (int tier = 1; tier <= materialTiers; tier++) {
                List<BlockLine> lines = new ArrayList<>();
                for (BlockGroup group : byTier) {
                    lines.add(new BlockLine(group.countText(), group.tierNames.get(tier - 1), false));
                }
                tiers.add(new MaterialTier(materialTierHeader(tier, rules), lines));
            }
        }
        return new Section(blocks, tiers);
    }

    private static String materialTierHeader(int tier, IMultiblockTooltipRules rules) {
        String header = rules == null ? null : rules.getStructureTooltipMaterialTierHeader(tier);
        return header != null ? header : StatCollector.translateToLocalFormatted("gtnl.structure.tooltip.tier", tier);
    }

    private static List<HatchLine> toHatchLines(Map<String, HatchGroup> hatches, List<HatchRule> hatchRules) {
        mergeLinkedLines(hatches, hatchRules);
        List<HatchGroup> groups = new ArrayList<>(hatches.values());
        groups.sort(Comparator.comparingInt(group -> group.order));
        List<HatchLine> lines = new ArrayList<>();
        for (HatchGroup hatch : groups) {
            lines.add(
                new HatchLine(
                    hatchCount(hatch, hatchRules),
                    new ArrayList<>(hatch.names),
                    hatchLocations(hatch, hatchRules)));
        }
        return lines;
    }

    private static List<String> hatchLocations(HatchGroup hatch, List<HatchRule> hatchRules) {
        for (HatchRule rule : hatchRules) {
            if (rule.perLayer() > 0 && !Collections.disjoint(hatch.keys, rule.hatchKeys())) {
                return Collections.singletonList(
                    StatCollector.translateToLocalFormatted("gtnl.structure.tooltip.hatch.per_layer", rule.perLayer()));
            }
        }
        return new ArrayList<>(hatch.locations);
    }

    private static void mergeLinkedLines(Map<String, HatchGroup> hatches, List<HatchRule> hatchRules) {
        for (HatchRule rule : hatchRules) {
            List<String> linked = new ArrayList<>();
            for (Map.Entry<String, HatchGroup> entry : hatches.entrySet()) {
                if (!Collections.disjoint(entry.getValue().keys, rule.hatchKeys())) linked.add(entry.getKey());
            }
            for (int i = 1; i < linked.size(); i++) {
                hatches.get(linked.get(0))
                    .absorb(hatches.remove(linked.get(i)));
            }
        }
    }

    private static String hatchCount(HatchGroup hatch, List<HatchRule> hatchRules) {
        if (!hatch.replacesBlocks) return String.valueOf(hatch.dedicated);
        int min = 0, max = StructureTooltipHooks.UNLIMITED;
        for (HatchRule rule : hatchRules) {
            if (Collections.disjoint(hatch.keys, rule.hatchKeys())) continue;
            min = Math.max(min, rule.min());
            max = Math.min(max, rule.max());
        }
        if (min == max) return String.valueOf(min);
        if (max == StructureTooltipHooks.UNLIMITED) return min + "+";
        return min + "-" + max;
    }

    private static boolean isHatchKey(String key) {
        for (String prefix : HATCH_KEY_PREFIXES) {
            if (key.startsWith(prefix)) return true;
        }
        return StructureTooltipHooks.isHatchBlockKey(key);
    }

    private static int hatchOrder(String hatchKey) {
        for (int i = 0; i < HATCH_ORDER.length; i++) {
            if (HATCH_ORDER[i].getDescriptionLangKey()
                .equals(hatchKey)) return i;
        }
        return -1;
    }

    private static String nameOf(Object controller) {
        return controller.getClass()
            .getSimpleName();
    }

    private record Cell<T> (IStructureElement<T> element, int a, int b, int c) {}

    private record Position(int a, int b, int c) {}

    private record Line(int first, int second) {}

    private static class BlockGroup {

        private final List<String> names;
        private final List<String> tierNames;
        private final boolean byMaterialTier;
        private int fixed;
        private int replaceable;
        private int minimum;

        private BlockGroup(List<String> names, List<String> tierNames, boolean byMaterialTier) {
            this.names = names;
            this.tierNames = tierNames;
            this.byMaterialTier = byMaterialTier;
        }

        private int total() {
            return fixed + replaceable;
        }

        private String countText() {
            if (replaceable == 0 || minimum == replaceable) return String.valueOf(total());
            return (fixed + minimum) + "-" + total();
        }

        private String displayName() {
            if (tierNames == null || names.size() <= MAX_JOINED_TIER_NAMES) return String.join(NAME_SEPARATOR, names);
            return names.get(0) + " ~ " + names.get(names.size() - 1);
        }
    }

    private static class HatchGroup {

        private final Set<String> keys = new LinkedHashSet<>();
        private final Set<String> names = new LinkedHashSet<>();
        private final Set<String> locations = new LinkedHashSet<>();
        private int dedicated;
        private boolean replacesBlocks;
        private int order = Integer.MAX_VALUE;

        private void absorb(HatchGroup other) {
            keys.addAll(other.keys);
            names.addAll(other.names);
            locations.addAll(other.locations);
            dedicated += other.dedicated;
            replacesBlocks |= other.replacesBlocks;
            order = Math.min(order, other.order);
        }
    }
}
