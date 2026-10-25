package com.science.gtnl.utils.structure;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.google.gson.GsonBuilder;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IMultiblockTooltipRules;
import com.science.gtnl.api.IMultiblockTooltipRules.TieredPart;
import com.science.gtnl.api.IMultiblockTooltipRules.TooltipStats;
import com.science.gtnl.utils.structure.InfoSummary.BonusStat;
import com.science.gtnl.utils.structure.InfoSummary.TierBonus;
import com.science.gtnl.utils.structure.InfoSummary.TierThreshold;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTETooltipMultiBlockBase;
import gregtech.api.structure.IStructureChannels;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class MultiblockTooltipAudit {

    private static final String GTNL_PACKAGE = "com.science.gtnl.";
    private static final String KEY_PREFIX = "gtnl.tooltip_export.";
    private static final String REPORT_FILE = "report.txt";
    private static final String DATA_FILE = "tooltips.json";
    private static final int LOW_FIELD_VALUE = 1, HIGH_FIELD_VALUE = 4;
    private static final int SLOWEST_SHOWN = 10;
    private static final long NANOS_PER_MICRO = 1000;
    private static final double MICROS_PER_MILLI = 1000;
    private static final Pattern TIER_OR_MODE = Pattern.compile("(?i).*(tier|mode).*");
    // Why: these fields are the probes' own knobs, or the probes and line groups already show what they change.
    private static final List<String> SHOWN_FIELDS = Arrays
        .asList("mGlassTier", "mCasingTier", "tooltipVoltageTier", "tooltipParallelTier", "tierMachine", "machineMode");
    private static final List<String> PARALLEL_CONTROLLER_FIELDS = Collections.singletonList("mParallelTier");
    private static final List<String> WIRELESS_FIELDS = Arrays.asList("wirelessUpgrade", "wirelessMode");
    private static final List<TieredPart> PROBED_PARTS = Arrays
        .asList(TieredPart.VOLTAGE, TieredPart.COIL, TieredPart.GLASS, TieredPart.CASING);

    public record MachineReport(int id, String className, String name, long micros, List<String> problems,
        List<String> notes, List<String> autoInfo, List<String> autoStructure, List<String> currentInfo,
        List<String> currentStructure) {}

    public record ExportResult(int machines, int suspicious, File report) {}

    private enum StatFamily {

        PARALLEL("parallel", TooltipStats::parallels),
        SPEED("speed", TooltipStats::speed),
        ENERGY("energy", TooltipStats::energyUsage),
        PERFECT_OC("perfect_oc", stats -> stats.perfectOverclock() ? 1 : 0);

        private final String key;
        private final ToDoubleFunction<TooltipStats> stat;

        StatFamily(String key, ToDoubleFunction<TooltipStats> stat) {
            this.key = key;
            this.stat = stat;
        }

        private String displayName() {
            return text("stat." + key);
        }

        private boolean shows(BonusStat bonusStat) {
            return switch (this) {
                case PARALLEL -> bonusStat == BonusStat.PARALLEL;
                case SPEED -> bonusStat == BonusStat.SPEED || bonusStat == BonusStat.DURATION;
                case ENERGY -> bonusStat == BonusStat.ENERGY;
                case PERFECT_OC -> false;
            };
        }
    }

    private MultiblockTooltipAudit() {}

    public static ExportResult export(File directory) throws IOException {
        List<MachineReport> reports = auditAll();
        Files.createDirectories(directory.toPath());
        File report = new File(directory, REPORT_FILE);
        Files.write(report.toPath(), reportLines(reports), StandardCharsets.UTF_8);
        try (Writer writer = Files.newBufferedWriter(new File(directory, DATA_FILE).toPath(), StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(reports, writer);
        }
        return new ExportResult(reports.size(), suspiciousCount(reports), report);
    }

    public static List<MachineReport> auditAll() {
        List<MachineReport> reports = new ArrayList<>();
        IMetaTileEntity[] machines = GregTechAPI.METATILEENTITIES;
        for (int id = 0; id < machines.length; id++) {
            if (machines[id] instanceof MTEMultiBlockBase machine && machine.getClass()
                .getName()
                .startsWith(GTNL_PACKAGE)) reports.add(audit(id, machine));
        }
        return reports;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static MachineReport audit(int id, MTEMultiBlockBase machine) {
        List<String> problems = new ArrayList<>();
        List<String> notes = new ArrayList<>();
        List<String> autoInfo = Collections.emptyList();
        List<String> autoStructure = Collections.emptyList();
        long start = System.nanoTime();
        if (machine instanceof MTEEnhancedMultiBlockBase enhanced) {
            try {
                MultiblockTooltipBuilder auto = new GTNLMultiblockTooltipBuilder().addAutoInfo(enhanced)
                    .addAutoStructure(enhanced)
                    .toolTipFinisher();
                autoInfo = Arrays.asList(auto.getInformation());
                autoStructure = Arrays.asList(auto.getStructureInformation());
            } catch (RuntimeException e) {
                ScienceNotLeisure.LOG.warn("Cannot build the automatic tooltip of {}", machine.getLocalName(), e);
                problems.add(text("problem.generation_failed", e));
            }
        } else {
            problems.add(text("problem.unsupported"));
        }
        long micros = (System.nanoTime() - start) / NANOS_PER_MICRO;
        if (machine instanceof MTEEnhancedMultiBlockBase enhanced) check(enhanced, problems, notes);
        List<String> currentInfo = Collections.emptyList();
        List<String> currentStructure = Collections.emptyList();
        try {
            if (machine instanceof MTETooltipMultiBlockBase tooltipMachine) {
                currentInfo = Arrays.asList(tooltipMachine.getPrimaryDescription());
                currentStructure = Arrays.asList(tooltipMachine.getSecondaryDescription());
            } else {
                currentInfo = Arrays.asList(machine.getDescription());
            }
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Cannot build the current tooltip of {}", machine.getLocalName(), e);
            problems.add(text("problem.current_failed", e));
        }
        return new MachineReport(
            id,
            machine.getClass()
                .getName(),
            machine.getLocalName(),
            micros,
            problems,
            notes,
            autoInfo,
            autoStructure,
            currentInfo,
            currentStructure);
    }

    private static <T extends MTEEnhancedMultiBlockBase<T>> void check(T machine, List<String> problems,
        List<String> notes) {
        String keyPrefix = MultiblockTooltipReader.keyPrefixOf(machine);
        if (keyPrefix == null) problems.add(text("problem.no_key_prefix"));
        StructureSummary structure = MultiblockTooltipReader.readStructure(machine);
        if (structure == null) problems.add(text("problem.no_structure"));
        String checkFailure = MultiblockTooltipReader.withPrototype(
            machine,
            prototype -> StructureTooltipHooks.recordChecks(prototype)
                .failure());
        if (checkFailure != null) problems.add(text("problem.check_failed", checkFailure));
        InfoSummary info = MultiblockTooltipReader.readInfo(machine, keyPrefix, structure);
        if (info == null) {
            problems.add(text("problem.no_info"));
            return;
        }
        if (info.lines()
            .isEmpty()) notes.add(text("note.no_own_lines"));
        if (!(machine instanceof IMultiblockTooltipRules)) {
            problems.add(text("problem.no_rules"));
            return;
        }
        if (info.stats() == null) {
            notes.add(text("note.no_stats"));
            return;
        }
        if (info.heat() != null && !info.heat()
            .usesRecipeHeat()) problems.add(text("problem.heat_without_recipe_heat"));
        problems.addAll(MultiblockTooltipReader.withPrototype(machine, prototype -> probe(prototype, info, structure)));
    }

    private static <T extends MTEEnhancedMultiBlockBase<T>> List<String> probe(T machine, InfoSummary info,
        StructureSummary structure) {
        IMultiblockTooltipRules rules = (IMultiblockTooltipRules) machine;
        List<String> problems = new ArrayList<>();
        List<IStructureChannels> channels = structure == null ? Collections.emptyList() : structure.channels();
        checkPart(problems, rules, info, TieredPart.VOLTAGE, true);
        checkPart(problems, rules, info, TieredPart.COIL, channels.contains(GTStructureChannels.HEATING_COIL));
        checkPart(problems, rules, info, TieredPart.GLASS, channels.contains(GTStructureChannels.BOROGLASS));
        checkPart(problems, rules, info, TieredPart.CASING, true);
        checkFields(problems, machine, rules, shownFields(rules, structure));
        checkModes(problems, machine, rules);
        return problems;
    }

    private static void checkModes(List<String> problems, MTEEnhancedMultiBlockBase<?> machine,
        IMultiblockTooltipRules rules) {
        List<Integer> modes = MultiblockTooltipReader.machineModes(machine);
        if (modes.size() <= 1) return;
        int original = machine.machineMode;
        try {
            List<TooltipStats> stats = new ArrayList<>();
            for (int mode : modes) {
                machine.machineMode = mode;
                stats.add(rules.getTooltipStats());
            }
            for (TooltipStats other : stats) {
                if (!changedFamilies(stats.get(0), other).isEmpty()) {
                    problems.add(text("problem.mode_changes", modes.size()));
                    return;
                }
            }
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG
                .debug("Cannot probe the modes of {} for the tooltip export", machine.getLocalName(), e);
        } finally {
            machine.machineMode = original;
        }
    }

    private static void checkPart(List<String> problems, IMultiblockTooltipRules rules, InfoSummary info,
        TieredPart part, boolean inStructure) {
        int firstTier = MultiblockTooltipReader.firstProbeTier(part);
        TooltipStats[] probes = MultiblockTooltipReader.probeTiers(rules, part, part.maxTier + 1 - firstTier);
        if (probes == null) return;
        String partName = StatCollector.translateToLocal(part.langKey);
        for (StatFamily family : StatFamily.values()) {
            TierBonus bonus = bonusOf(info, part, family);
            double[] values = bonus != null && bonus.stat() == BonusStat.DURATION
                ? MultiblockTooltipReader.durationFactors(probes, info.stats())
                : MultiblockTooltipReader.values(probes, family.stat);
            if (!MultiblockTooltipReader.allFinite(values)) continue;
            if (family == StatFamily.PERFECT_OC && shownFrom(values, info, part, firstTier)) continue;
            if (bonus == null) {
                if (!isFlat(values)) problems.add(
                    text(
                        "problem.part_not_shown",
                        partName,
                        family.displayName(),
                        text(inStructure ? "reason.irregular" : "reason.no_channel")));
                continue;
            }
            int broken = firstBrokenIndex(values, bonus);
            if (broken >= 0) {
                problems.add(text("problem.part_irregular", partName, family.displayName(), firstTier + broken));
            }
        }
    }

    private static boolean shownFrom(double[] values, InfoSummary info, TieredPart part, int firstTier) {
        for (TierThreshold threshold : info.perfectOverclockFrom()) {
            if (threshold.part() != part) continue;
            for (int index = 0; index < values.length; index++) {
                boolean perfect = values[index] > 0;
                if (perfect != firstTier + index >= threshold.tier()) return false;
            }
            return true;
        }
        return false;
    }

    private static TierBonus bonusOf(InfoSummary info, TieredPart part, StatFamily family) {
        for (TierBonus bonus : info.tierBonuses()) {
            if (bonus.part() == part && bonus.perTier() && family.shows(bonus.stat())) return bonus;
        }
        return null;
    }

    private static int firstBrokenIndex(double[] values, TierBonus bonus) {
        for (int index = 1; index < values.length; index++) {
            double expected = bonus.multiplicative() ? values[0] * Math.pow(bonus.value(), index)
                : values[0] + bonus.value() * index;
            if (!MultiblockTooltipReader.isNearly(values[index], expected)) return index;
        }
        return -1;
    }

    private static Set<String> shownFields(IMultiblockTooltipRules rules, StructureSummary structure) {
        Set<String> shown = new HashSet<>(SHOWN_FIELDS);
        List<String> hatchKeys = structure == null ? Collections.emptyList() : structure.hatchKeys();
        for (String hatchKey : hatchKeys) {
            if (!rules.getTooltipHatchLines(hatchKey)
                .isEmpty()) shown.addAll(PARALLEL_CONTROLLER_FIELDS);
        }
        if (!rules.getTooltipUpgradeLines()
            .isEmpty()) shown.addAll(WIRELESS_FIELDS);
        return shown;
    }

    private static void checkFields(List<String> problems, Object machine, IMultiblockTooltipRules rules,
        Set<String> shown) {
        List<Supplier<TooltipStats>> contexts = probeContexts(rules);
        for (Field field : stateFields(machine.getClass())) {
            Set<StatFamily> changed = fieldEffects(field, machine, contexts);
            // Why: the groups and probes that explain these fields say nothing about perfect overclocking.
            if (shown.contains(field.getName())) changed.retainAll(EnumSet.of(StatFamily.PERFECT_OC));
            if (!changed.isEmpty()) {
                problems.add(text("problem.field_not_shown", field.getName(), displayNames(changed)));
            }
        }
    }

    // Why: a field can matter only once a part is built, such as casings that add parallels on top of glass.
    private static List<Supplier<TooltipStats>> probeContexts(IMultiblockTooltipRules rules) {
        List<Supplier<TooltipStats>> contexts = new ArrayList<>();
        contexts.add(rules::getTooltipStats);
        for (TieredPart part : PROBED_PARTS) {
            int tier = MultiblockTooltipReader.firstProbeTier(part);
            contexts.add(() -> rules.getTooltipStatsAtTier(part, tier));
        }
        return contexts;
    }

    private static List<Field> stateFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && current.getName()
            .startsWith(GTNL_PACKAGE); current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (!isMachineState(field)) continue;
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }

    private static boolean isMachineState(Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) return false;
        Class<?> type = field.getType();
        if (type == boolean.class) return true;
        boolean whole = type == int.class || type == short.class || type == byte.class;
        return whole && TIER_OR_MODE.matcher(field.getName())
            .matches();
    }

    private static Set<StatFamily> fieldEffects(Field field, Object machine, List<Supplier<TooltipStats>> contexts) {
        Set<StatFamily> changed = EnumSet.noneOf(StatFamily.class);
        try {
            Object original = field.get(machine);
            try {
                for (Supplier<TooltipStats> context : contexts) {
                    setProbeValue(field, machine, false);
                    TooltipStats low = context.get();
                    setProbeValue(field, machine, true);
                    changed.addAll(changedFamilies(low, context.get()));
                }
            } finally {
                field.set(machine, original);
            }
        } catch (IllegalAccessException | RuntimeException e) {
            ScienceNotLeisure.LOG.debug(
                "Cannot probe field {} of {} for the tooltip export",
                field.getName(),
                machine.getClass()
                    .getSimpleName(),
                e);
        }
        return changed;
    }

    private static void setProbeValue(Field field, Object machine, boolean high) throws IllegalAccessException {
        Class<?> type = field.getType();
        int value = high ? HIGH_FIELD_VALUE : LOW_FIELD_VALUE;
        if (type == boolean.class) field.setBoolean(machine, high);
        else if (type == int.class) field.setInt(machine, value);
        else if (type == short.class) field.setShort(machine, (short) value);
        else field.setByte(machine, (byte) value);
    }

    private static Set<StatFamily> changedFamilies(TooltipStats first, TooltipStats second) {
        Set<StatFamily> changed = EnumSet.noneOf(StatFamily.class);
        if (first == null || second == null) return changed;
        for (StatFamily family : StatFamily.values()) {
            double before = family.stat.applyAsDouble(first);
            if (!MultiblockTooltipReader.isNearly(before, family.stat.applyAsDouble(second))) changed.add(family);
        }
        return changed;
    }

    private static String displayNames(Set<StatFamily> families) {
        List<String> names = new ArrayList<>();
        for (StatFamily family : families) names.add(family.displayName());
        return GTNLMultiblockTooltipBuilder.joinPhrases(names);
    }

    private static boolean isFlat(double[] values) {
        for (double value : values) if (!MultiblockTooltipReader.isNearly(value, values[0])) return false;
        return true;
    }

    private static int suspiciousCount(List<MachineReport> reports) {
        int count = 0;
        for (MachineReport report : reports) if (!report.problems()
            .isEmpty()) count++;
        return count;
    }

    private static List<String> reportLines(List<MachineReport> reports) {
        List<String> lines = new ArrayList<>();
        long totalMicros = 0;
        for (MachineReport report : reports) totalMicros += report.micros();
        lines.add(text("title"));
        lines.add(text("summary", reports.size(), suspiciousCount(reports), millis(totalMicros)));
        lines.add(text("slowest"));
        List<MachineReport> slowest = new ArrayList<>(reports);
        slowest.sort(
            Comparator.comparingLong(MachineReport::micros)
                .reversed());
        for (MachineReport report : slowest.subList(0, Math.min(SLOWEST_SHOWN, slowest.size()))) {
            lines.add("  " + text("timed", report.name(), millis(report.micros())));
        }

        lines.add("");
        lines.add(text("section.problems"));
        for (MachineReport report : reports) {
            if (report.problems()
                .isEmpty()) continue;
            lines.add(heading(report));
            for (String problem : report.problems()) lines.add("  - " + problem);
        }

        lines.add("");
        lines.add(text("section.notes"));
        for (MachineReport report : reports) {
            if (!report.notes()
                .isEmpty()) lines.add(heading(report) + ": " + String.join(" / ", report.notes()));
        }

        lines.add("");
        lines.add(text("section.machines"));
        for (MachineReport report : reports) {
            lines.add("");
            lines.add(text("machine", report.name(), report.className(), report.id(), millis(report.micros())));
            for (String problem : report.problems()) lines.add("  ! " + problem);
            for (String note : report.notes()) lines.add("  * " + note);
            addBlock(lines, "block.auto_info", report.autoInfo());
            addBlock(lines, "block.auto_structure", report.autoStructure());
            addBlock(lines, "block.current_info", report.currentInfo());
            addBlock(lines, "block.current_structure", report.currentStructure());
        }
        return lines;
    }

    private static String heading(MachineReport report) {
        return text("heading", report.name(), report.className());
    }

    private static void addBlock(List<String> lines, String key, List<String> block) {
        lines.add(text(key));
        for (String line : block) lines.add("    " + EnumChatFormatting.getTextWithoutFormattingCodes(line));
    }

    private static String millis(long micros) {
        return String.format(Locale.ROOT, "%.1f", micros / MICROS_PER_MILLI);
    }

    private static String text(String key, Object... args) {
        return StatCollector.translateToLocalFormatted(KEY_PREFIX + key, args);
    }
}
