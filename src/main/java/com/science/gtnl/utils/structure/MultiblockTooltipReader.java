package com.science.gtnl.utils.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.IStructureElement.BlocksToPlace;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IMultiblockTooltipRules;
import com.science.gtnl.api.IMultiblockTooltipRules.TieredPart;
import com.science.gtnl.api.IMultiblockTooltipRules.TooltipStats;
import com.science.gtnl.utils.structure.InfoSummary.BonusStat;
import com.science.gtnl.utils.structure.InfoSummary.TierBonus;
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
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.structure.IStructureChannels;
import gregtech.api.util.GlassTier;
import gregtech.common.misc.GTStructureChannels;

public class MultiblockTooltipReader {

    private static final int MAX_STRUCTURE_TIERS = 5;
    private static final int MAX_INFO_LINES = 64;
    private static final Pattern FORMAT_PLACEHOLDER = Pattern.compile("%(\\d+\\$)?[sd]");
    private static final int TIER_PROBES = 2;
    // Why: four tiers tell a steady step or factor apart from curves such as 2^tier, which three tiers cannot.
    private static final int BONUS_PROBE_TIERS = 4;
    private static final double BONUS_TOLERANCE = 1e-6;
    private static final int FIRST_VOLTAGE_PROBE_TIER = 1;
    private static final int FIRST_COIL_PROBE_TIER = 0;
    private static final int FIRST_GLASS_PROBE_TIER = 3;
    private static final int MAX_JOINED_TIER_NAMES = 2;
    private static final String NAME_SEPARATOR = " / ";
    private static final String GTNL_KEY_PREFIX = "gtnl.";
    private static final String MACHINE_KEY_PREFIX = "gtnl.machine.";
    private static final String NAME_KEY_SUFFIX = ".name";
    private static final Pattern ACRONYM_END = Pattern.compile("([A-Z]+)([A-Z][a-z])");
    private static final Pattern LOWER_TO_UPPER = Pattern.compile("([a-z0-9])([A-Z])");
    private static final String TIERED_GLASS_KEY = "GT5U.structure.tiered_glass";
    private static final String HEATING_COIL_KEY = "GT5U.structure.heating_coil";
    // Why: StructureLib only returns bare lang keys, so hatches are recognised by the key families used for hatches.
    private static final String[] HATCH_KEY_PREFIXES = { "GT5U.MBTT.", "gt.blockmachines.", "hatch_type_",
        "gtnl.structure.hatch." };
    private static final HatchElement[] HATCH_ORDER = { HatchElement.Energy, HatchElement.ExoticEnergy,
        HatchElement.MultiAmpEnergy, HatchElement.Dynamo, HatchElement.ExoticDynamo, HatchElement.Maintenance,
        HatchElement.Muffler, HatchElement.InputBus, HatchElement.InputHatch, HatchElement.OutputBus,
        HatchElement.OutputHatch };

    private MultiblockTooltipReader() {}

    public static <T extends MTEEnhancedMultiBlockBase<T>> StructureSummary readStructure(T machine) {
        try {
            return readPrototype(prototypeOf(machine));
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the structure of {} for its tooltip", nameOf(machine), e);
            return null;
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
        return StatCollector.canTranslate(fromClass + NAME_KEY_SUFFIX) ? fromClass : null;
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
            T controller = prototypeOf(machine);
            IMultiblockTooltipRules rules = controller instanceof IMultiblockTooltipRules machineRules ? machineRules
                : null;
            List<String> lines = keyPrefix == null ? new ArrayList<>() : numberedLines(keyPrefix + ".tooltip");
            if (lines.isEmpty() && keyPrefix != null) lines = numberedLines(keyPrefix + ".info");
            List<String> hatchKeys = structure == null ? Collections.emptyList() : structure.hatchKeys();
            if (rules != null) {
                for (String hatchKey : hatchKeys) addTranslated(lines, rules.getTooltipHatchFeatureKeys(hatchKey));
                addTranslated(lines, rules.getTooltipFeatureKeys());
            }
            boolean supportAny = hatchKeys.contains(HatchElement.ExoticEnergy.getDescriptionLangKey());
            boolean supportMultiAmp = !supportAny
                && hatchKeys.contains(HatchElement.MultiAmpEnergy.getDescriptionLangKey());
            boolean tiered = structure != null && structure.hasMaterialTiers();
            TooltipStats stats = null;
            List<TierBonus> bonuses = Collections.emptyList();
            try {
                if (rules != null) stats = rules.getTooltipStats();
                if (stats != null && structure != null) bonuses = tierBonuses(rules, structure.channels());
            } catch (RuntimeException e) {
                ScienceNotLeisure.LOG.warn("Cannot read the numbers of {} for its tooltip", nameOf(machine), e);
            }
            return new InfoSummary(
                keyPrefix == null ? null : translatedOrNull(keyPrefix + ".recipe_type"),
                stats,
                bonuses,
                lines,
                supportAny,
                supportMultiAmp,
                tiered && rules != null ? translatedOrNull(rules.getTooltipMaterialTierNotice()) : null);
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot read the information of {} for its tooltip", nameOf(machine), e);
            return null;
        }
    }

    private static List<String> numberedLines(String prefix) {
        List<String> lines = new ArrayList<>();
        for (int index = 0; index < MAX_INFO_LINES; index++) {
            String key = prefix + "." + index;
            if (!StatCollector.canTranslate(key)) continue;
            String line = StatCollector.translateToLocal(key);
            // Why: a line with a format placeholder needs values from code, so the machine adds it itself.
            if (!FORMAT_PLACEHOLDER.matcher(line)
                .find()) lines.add(line);
        }
        return lines;
    }

    private static void addTranslated(List<String> lines, List<String> keys) {
        for (String key : keys) lines.add(StatCollector.translateToLocal(key));
    }

    private static List<TierBonus> tierBonuses(IMultiblockTooltipRules rules, List<IStructureChannels> channels) {
        List<TierBonus> bonuses = new ArrayList<>();
        addTierBonuses(bonuses, rules, TieredPart.VOLTAGE);
        if (channels.contains(GTStructureChannels.HEATING_COIL)) addTierBonuses(bonuses, rules, TieredPart.COIL);
        if (channels.contains(GTStructureChannels.BOROGLASS)) addTierBonuses(bonuses, rules, TieredPart.GLASS);
        return bonuses;
    }

    private static void addTierBonuses(List<TierBonus> bonuses, IMultiblockTooltipRules rules, TieredPart part) {
        TooltipStats[] probes = new TooltipStats[BONUS_PROBE_TIERS];
        int firstTier = firstProbeTier(part);
        for (int index = 0; index < probes.length; index++) {
            probes[index] = rules.getTooltipStatsAtTier(part, firstTier + index);
            if (probes[index] == null) return;
        }
        addBonus(bonuses, part, BonusStat.PARALLEL, values(probes, TooltipStats::parallels), true);
        if (!addBonus(bonuses, part, BonusStat.SPEED, values(probes, TooltipStats::speed), false)) {
            addStepBonus(bonuses, part, BonusStat.DURATION, values(probes, stats -> 1 / stats.speed()), false);
        }
        addBonus(bonuses, part, BonusStat.ENERGY, values(probes, TooltipStats::energyUsage), false);
    }

    // Why: probing tiers a built machine can have keeps formulas that clamp lower tiers from hiding the bonus.
    private static int firstProbeTier(TieredPart part) {
        return switch (part) {
            case VOLTAGE -> FIRST_VOLTAGE_PROBE_TIER;
            case COIL -> FIRST_COIL_PROBE_TIER;
            case GLASS -> FIRST_GLASS_PROBE_TIER;
        };
    }

    private static double[] values(TooltipStats[] probes, ToDoubleFunction<TooltipStats> stat) {
        double[] values = new double[probes.length];
        for (int tier = 0; tier < probes.length; tier++) values[tier] = stat.applyAsDouble(probes[tier]);
        return values;
    }

    private static boolean addBonus(List<TierBonus> bonuses, TieredPart part, BonusStat stat, double[] values,
        boolean whole) {
        return addStepBonus(bonuses, part, stat, values, whole) || addFactorBonus(bonuses, part, stat, values, whole);
    }

    private static boolean addStepBonus(List<TierBonus> bonuses, TieredPart part, BonusStat stat, double[] values,
        boolean whole) {
        if (!allFinite(values)) return false;
        double step = values[1] - values[0];
        if (isNearly(step, 0) || whole && !isWhole(step)) return false;
        for (int tier = 2; tier < values.length; tier++) {
            if (!isNearly(values[tier] - values[tier - 1], step)) return false;
        }
        bonuses.add(new TierBonus(part, stat, false, step));
        return true;
    }

    private static boolean addFactorBonus(List<TierBonus> bonuses, TieredPart part, BonusStat stat, double[] values,
        boolean whole) {
        if (!allFinite(values)) return false;
        for (double value : values) if (isNearly(value, 0)) return false;
        double factor = values[1] / values[0];
        if (isNearly(factor, 1) || whole && !isWhole(factor)) return false;
        for (int tier = 2; tier < values.length; tier++) {
            if (!isNearly(values[tier] / values[tier - 1], factor)) return false;
        }
        bonuses.add(new TierBonus(part, stat, true, factor));
        return true;
    }

    private static boolean allFinite(double[] values) {
        for (double value : values) if (!Double.isFinite(value)) return false;
        return true;
    }

    private static boolean isWhole(double value) {
        return isNearly(value, Math.rint(value));
    }

    private static boolean isNearly(double a, double b) {
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
        List<List<PieceCall>> tiers = recordTiers(controller);
        if (tiers.isEmpty()) {
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
        List<List<Cell<T>>> tierCells = new ArrayList<>();
        List<Cell<T>> airCells = new ArrayList<>();
        for (List<PieceCall> tier : tiers) {
            tierCells.add(readCells(definition, tier, element -> element != air && element != anyBlock));
            airCells.addAll(readCells(definition, tier, element -> element == air));
        }
        Map<String, HatchGroup> hatches = new LinkedHashMap<>();
        Set<IStructureChannels> channels = new LinkedHashSet<>();
        List<List<BlockGroup>> tierGroups = new ArrayList<>();
        for (List<Cell<T>> cells : tierCells) {
            tierGroups.add(readGroups(controller, cells, materialTiers, rules, hatches, channels));
        }
        applyCasingMinimum(tierGroups.get(0), checks.casingMinimums());

        List<Section> sections = new ArrayList<>();
        for (List<BlockGroup> groups : tierGroups) sections.add(toSection(groups, materialTiers, rules));
        IStructureChannels materialChannel = rules == null ? null : rules.getStructureTooltipMaterialTierChannel();
        Set<String> hatchKeys = new LinkedHashSet<>();
        for (HatchGroup hatch : hatches.values()) hatchKeys.addAll(hatch.keys);

        StructureSummary summary = new StructureSummary(
            size(tierCells.subList(0, 1)),
            size(tierCells),
            isHollow(tierCells, airCells),
            controllerPosition(tierCells.get(0)),
            toHatchLines(hatches, checks.hatchRules(), rules),
            new ArrayList<>(hatchKeys),
            sections,
            new ArrayList<>(channels));
        if (summary.hasMaterialTiers() && materialChannel != null) summary.channels()
            .add(materialChannel);
        return summary;
    }

    private static List<List<PieceCall>> recordTiers(MTEEnhancedMultiBlockBase<?> controller) {
        List<List<PieceCall>> tiers = new ArrayList<>();
        List<PieceCall> previous = Collections.emptyList();
        for (int stackSize = 1; stackSize <= MAX_STRUCTURE_TIERS + 1; stackSize++) {
            List<PieceCall> calls = StructureTooltipHooks.recordConstruct(controller, stackSize);
            if (calls.isEmpty() || new HashSet<>(previous).containsAll(calls)) break;
            if (stackSize > MAX_STRUCTURE_TIERS) return tiers.subList(0, 1);
            List<PieceCall> added = new ArrayList<>(calls);
            added.removeAll(previous);
            tiers.add(added);
            previous = calls;
        }
        return tiers;
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
        IMultiblockTooltipRules rules, Map<String, HatchGroup> hatches, Set<IStructureChannels> channels) {
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
                IStructureChannels channel = channelOf(key);
                if (channel != null) channels.add(channel);
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
                hatch.names.add(StatCollector.translateToLocal(hatchKey));
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
        List<String> names = new ArrayList<>();
        try {
            for (int tier = 1; tier <= tiers; tier++) {
                List<String> placed = placedNames(controller, element, tier);
                if (placed == null || placed.isEmpty()) return null;
                names.add(String.join(NAME_SEPARATOR, placed));
            }
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.debug("Cannot probe the block tiers of {} for its tooltip", nameOf(controller), e);
            return null;
        }
        // Why: a tiered element places a different block for each projector tier; a choice of blocks places them all.
        return new HashSet<>(names).size() > 1 ? names : null;
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

    private static List<HatchLine> toHatchLines(Map<String, HatchGroup> hatches, List<HatchRule> hatchRules,
        IMultiblockTooltipRules rules) {
        mergeLinkedLines(hatches, hatchRules);
        List<Map.Entry<String, HatchGroup>> entries = new ArrayList<>(hatches.entrySet());
        entries.sort(Comparator.comparingInt((Map.Entry<String, HatchGroup> entry) -> entry.getValue().order));
        List<HatchLine> lines = new ArrayList<>();
        for (Map.Entry<String, HatchGroup> entry : entries) {
            HatchGroup hatch = entry.getValue();
            lines.add(
                new HatchLine(
                    hatchCount(entry.getKey(), hatch, hatchRules, rules),
                    new ArrayList<>(hatch.names),
                    new ArrayList<>(hatch.locations)));
        }
        return lines;
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

    private static String hatchCount(String lineKey, HatchGroup hatch, List<HatchRule> hatchRules,
        IMultiblockTooltipRules rules) {
        if (!hatch.replacesBlocks) return String.valueOf(hatch.dedicated);
        String custom = rules == null ? null : rules.getStructureTooltipHatchCount(lineKey);
        if (custom != null) return custom;
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

    private static IStructureChannels channelOf(String key) {
        if (TIERED_GLASS_KEY.equals(key) || GlassTier.getTierLangKeys()
            .contains(key)) return GTStructureChannels.BOROGLASS;
        if (HEATING_COIL_KEY.equals(key)) return GTStructureChannels.HEATING_COIL;
        return null;
    }

    private static boolean isHatchKey(String key) {
        for (String prefix : HATCH_KEY_PREFIXES) {
            if (key.startsWith(prefix)) return true;
        }
        return false;
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
