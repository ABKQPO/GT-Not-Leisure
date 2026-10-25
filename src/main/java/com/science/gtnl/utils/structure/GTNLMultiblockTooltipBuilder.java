package com.science.gtnl.utils.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.regex.Pattern;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IMultiblockTooltipRules;
import com.science.gtnl.api.IMultiblockTooltipRules.HeatRules;
import com.science.gtnl.api.IMultiblockTooltipRules.TooltipStats;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.structure.InfoSummary.BonusStat;
import com.science.gtnl.utils.structure.InfoSummary.TierBonus;
import com.science.gtnl.utils.structure.InfoSummary.TierThreshold;
import com.science.gtnl.utils.structure.StructureSummary.BlockLine;
import com.science.gtnl.utils.structure.StructureSummary.ControllerPosition;
import com.science.gtnl.utils.structure.StructureSummary.HatchLine;
import com.science.gtnl.utils.structure.StructureSummary.MaterialTier;
import com.science.gtnl.utils.structure.StructureSummary.Section;
import com.science.gtnl.utils.structure.StructureSummary.Size;

import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.structure.IStructureChannels;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;

public class GTNLMultiblockTooltipBuilder extends MultiblockTooltipBuilder {

    private static final String NAME_SEPARATOR = MultiblockTooltipReader.NAME_SEPARATOR;
    // Why: GT's stat methods add a finished line, so these lines are rebuilt from GT's keys to append the bonuses.
    private static final String PARALLEL_KEY = "GT5U.MBTT.Parallel.Base";
    private static final String SPEED_KEY = "GT5U.MBTT.Speed.Base";
    private static final String EU_KEY = "GT5U.MBTT.EuDiscount.Base";
    private static final String STEAM_KEY = "GT5U.MBTT.SteamDiscount.Base";
    private static final String WITH_BONUS_KEY = "gtnl.structure.tooltip.stat_with_bonus";
    private static final String BONUS_KEY = "gtnl.structure.tooltip.per_tier";
    private static final String INCREASE_KEY = "gtnl.structure.tooltip.change.increase";
    private static final String DECREASE_KEY = "gtnl.structure.tooltip.change.decrease";
    private static final String DURATION_BONUS_KEY = "gtnl.structure.tooltip.per_tier.duration";
    private static final String COMPOUND_KEY = "gtnl.structure.tooltip.change.compound";
    private static final String SWITCH_BONUS_KEY = "gtnl.structure.tooltip.switch_bonus";
    private static final String LIST_JOIN_KEY = "gtnl.structure.tooltip.join.list";
    private static final String LAST_JOIN_KEY = "gtnl.structure.tooltip.join.last";
    // Why: GT 5.09.54.205 has no hatch or mechanic tooltip colors yet; these are the ones newer GT versions use.
    public static final EnumChatFormatting HATCH_COLOR = EnumChatFormatting.GREEN;
    public static final EnumChatFormatting MECHANIC_COLOR = EnumChatFormatting.LIGHT_PURPLE;

    private static final String HEAT_DISCOUNT_KEY = "gtnl.structure.tooltip.heat_discount";
    private static final String HEAT_OVERCLOCK_KEY = "gtnl.structure.tooltip.heat_overclock";
    private static final String PERFECT_OC_FROM_KEY = "gtnl.structure.tooltip.perfect_oc_from";
    private static final String TIER_FROM_KEY = "gtnl.structure.tooltip.tier_from.";
    private static final String CASING_LEVEL_FROM_KEY = TIER_FROM_KEY + "casing_level";
    private static final String HEAT_UNIT = "K";
    // Why: Minecraft color codes reset the style, so italics are reapplied after every color change.
    private static final Pattern STYLE_RESET = Pattern.compile("(\u00a7[0-9a-fA-FrR])");

    private final List<List<String>> pendingGroups = new ArrayList<>();
    private StructureSummary structure;
    private boolean structureRead;

    public <T extends MTEEnhancedMultiBlockBase<T>> GTNLMultiblockTooltipBuilder addAutoTooltip(T machine) {
        return addAutoTooltip(machine, MultiblockTooltipReader.keyPrefixOf(machine));
    }

    public <T extends MTEEnhancedMultiBlockBase<T>> GTNLMultiblockTooltipBuilder addAutoTooltip(T machine,
        String keyPrefix) {
        addAutoInfo(machine, keyPrefix);
        addAutoStructure(machine);
        return this;
    }

    private <T extends MTEEnhancedMultiBlockBase<T>> void addAutoInfo(T machine, String keyPrefix) {
        InfoSummary info = MultiblockTooltipReader.readInfo(machine, keyPrefix, structureOf(machine));
        if (info == null) return;
        if (info.machineType() != null) addMachineType(info.machineType());
        TooltipStats stats = info.stats();
        if (stats != null) addStatLines(stats, info.tierBonuses());
        for (String line : info.lines()) addInfo(ownLine(line));
        for (String line : info.features()) addInfo(line);
        if (info.heat() != null) addHeatLines(info.heat());
        if (stats != null) addRecipeRuleLines(stats, info.perfectOverclockFrom(), info.tierSkips());
        addPollutionAmount(info.pollution());
        if (info.supportAny()) addSupportAny();
        else if (info.supportMultiAmp()) addSupportMultiAmp();
        pendingGroups.addAll(info.groups());
    }

    @Override
    public MultiblockTooltipBuilder toolTipFinisher(EnumChatFormatting separatorColor, int separatorLength) {
        for (List<String> group : pendingGroups) {
            addSeparator();
            for (String line : group) addInfo(line);
        }
        pendingGroups.clear();
        return super.toolTipFinisher(separatorColor, separatorLength);
    }

    public static String hatchName(String langKey) {
        return TooltipHelper.coloredText(StatCollector.translateToLocal(langKey), HATCH_COLOR);
    }

    public static String mechanicName(String langKey) {
        return TooltipHelper.coloredText(StatCollector.translateToLocal(langKey), MECHANIC_COLOR);
    }

    public static String joinPhrases(List<String> phrases) {
        if (phrases.isEmpty()) return "";
        String joined = phrases.get(0);
        for (int i = 1; i < phrases.size() - 1; i++) {
            joined = StatCollector.translateToLocalFormatted(LIST_JOIN_KEY, joined, phrases.get(i));
        }
        if (phrases.size() == 1) return joined;
        return StatCollector.translateToLocalFormatted(LAST_JOIN_KEY, joined, phrases.get(phrases.size() - 1));
    }

    private <T extends MTEEnhancedMultiBlockBase<T>> void addAutoStructure(T machine) {
        StructureSummary summary = structureOf(machine);
        if (summary == null) return;
        boolean structureTiers = summary.sections()
            .size() > 1;

        addSize(summary.firstTier(), summary.allTiers(), structureTiers, summary.hollow());
        addControllerLine(summary.controller());
        for (HatchLine line : summary.hatchLines()) addHatchLine(line);
        for (int tier = 0; tier < summary.sections()
            .size(); tier++) {
            if (structureTiers) {
                addStructureInfo("");
                addStructureInfo(structureTierHeader(tier + 1));
            }
            addSection(
                summary.sections()
                    .get(tier),
                !structureTiers);
        }
        addChannelLines(summary, structureTiers);
    }

    private <T extends MTEEnhancedMultiBlockBase<T>> StructureSummary structureOf(T machine) {
        if (!structureRead) {
            structure = MultiblockTooltipReader.readStructure(machine);
            structureRead = true;
        }
        return structure;
    }

    private void addStatLines(TooltipStats stats, List<TierBonus> bonuses) {
        addStatLine(statText(PARALLEL_KEY, TooltipHelper.parallelText(stats.parallels())), bonuses, BonusStat.PARALLEL);
        addStatLine(
            statText(SPEED_KEY, TooltipHelper.speedText((float) stats.speed())),
            bonuses,
            BonusStat.SPEED,
            BonusStat.DURATION);
        addStatLine(energyText(stats), bonuses, BonusStat.ENERGY);
    }

    private void addHeatLines(HeatRules heat) {
        if (!heat.usesRecipeHeat()) return;
        if (heat.discount()) {
            addInfo(
                StatCollector.translateToLocalFormatted(
                    HEAT_DISCOUNT_KEY,
                    GTNLOverclockCalculator.HEAT_DISCOUNT_THRESHOLD + HEAT_UNIT,
                    TooltipHelper.percentageFormat.format(1 - heat.discountFactor())));
        }
        if (heat.overclock()) {
            addInfo(
                StatCollector.translateToLocalFormatted(
                    HEAT_OVERCLOCK_KEY,
                    GTNLOverclockCalculator.HEAT_OVERCLOCK_THRESHOLD + HEAT_UNIT));
        }
    }

    private void addRecipeRuleLines(TooltipStats stats, List<TierThreshold> perfectFrom, OptionalInt tierSkips) {
        if (stats.perfectOverclock()) addPerfectOCInfo();
        for (TierThreshold threshold : perfectFrom) {
            addInfo(StatCollector.translateToLocalFormatted(PERFECT_OC_FROM_KEY, thresholdText(threshold)));
        }
        if (!tierSkips.isPresent()) return;
        int skips = tierSkips.getAsInt();
        if (skips == IMultiblockTooltipRules.UNLIMITED_TIER_SKIPS) addUnlimitedTierSkips();
        else if (skips == IMultiblockTooltipRules.NO_TIER_SKIP) addNoTierSkips();
        else addMaxTierSkips(skips);
    }

    private static String ownLine(String line) {
        String italic = EnumChatFormatting.ITALIC.toString();
        return italic + STYLE_RESET.matcher(line)
            .replaceAll("$1" + italic);
    }

    private static String thresholdText(TierThreshold threshold) {
        if (threshold.tierName() == null) {
            return StatCollector.translateToLocalFormatted(
                CASING_LEVEL_FROM_KEY,
                EnumChatFormatting.WHITE + String.valueOf(threshold.tier()) + EnumChatFormatting.GRAY);
        }
        String partKey = TIER_FROM_KEY + threshold.part()
            .name()
            .toLowerCase(Locale.ROOT);
        return StatCollector.translateToLocalFormatted(partKey, threshold.tierName());
    }

    private static String energyText(TooltipStats stats) {
        float usage = (float) stats.energyUsage();
        if (stats.steam())
            return statText(STEAM_KEY, TooltipHelper.effText(TooltipHelper.percentageFormat.format(usage)));
        return statText(EU_KEY, TooltipHelper.effText(usage));
    }

    private static String statText(String key, String value) {
        return String.format(StatCollector.translateToLocal(key), value);
    }

    private void addStatLine(String line, List<TierBonus> bonuses, BonusStat... stats) {
        List<BonusStat> shown = Arrays.asList(stats);
        for (TierBonus bonus : bonuses) {
            if (shown.contains(bonus.stat())) {
                line = StatCollector.translateToLocalFormatted(WITH_BONUS_KEY, line, bonusText(bonus));
            }
        }
        addInfo(line);
    }

    private static String bonusText(TierBonus bonus) {
        String part = TooltipHelper.tierText(StatCollector.translateToLocal(bonus.part().langKey));
        if (!bonus.perTier()) return StatCollector.translateToLocalFormatted(SWITCH_BONUS_KEY, part, changeText(bonus));
        String key = bonus.stat() == BonusStat.DURATION ? DURATION_BONUS_KEY : BONUS_KEY;
        String text = StatCollector.translateToLocalFormatted(key, part, changeText(bonus));
        return bonus.multiplicative() ? StatCollector.translateToLocalFormatted(COMPOUND_KEY, text) : text;
    }

    private static String changeText(TierBonus bonus) {
        double change = bonus.multiplicative() ? bonus.value() - 1 : bonus.value();
        String amount = bonus.stat() == BonusStat.PARALLEL && !bonus.multiplicative()
            ? Long.toString(Math.round(Math.abs(change)))
            : TooltipHelper.percentageFormat.format(Math.abs(change));
        String text = StatCollector.translateToLocalFormatted(change < 0 ? DECREASE_KEY : INCREASE_KEY, amount);
        return TooltipHelper.coloredText(text, colorOf(bonus.stat()));
    }

    private static EnumChatFormatting colorOf(BonusStat stat) {
        return switch (stat) {
            case PARALLEL -> TooltipHelper.PARALLEL_COLOR;
            case SPEED, DURATION -> TooltipHelper.SPEED_COLOR;
            case ENERGY -> TooltipHelper.EFF_COLOR;
        };
    }

    private void addSize(Size first, Size all, boolean variable, boolean hollow) {
        if (!variable) {
            beginStructureBlock(first.width(), first.height(), first.length(), hollow);
            return;
        }
        beginVariableStructureBlock(
            first.width(),
            all.width(),
            first.height(),
            all.height(),
            first.length(),
            all.length(),
            hollow);
    }

    private void addControllerLine(ControllerPosition controller) {
        String key = controller.frontCenter() ? "gtnl.structure.tooltip.controller.front_center"
            : "gtnl.structure.tooltip.controller.layer";
        addController(StatCollector.translateToLocalFormatted(key, controller.layer()));
    }

    private void addHatchLine(HatchLine line) {
        String count = line.count() == null ? "" : EnumChatFormatting.YELLOW + line.count() + " ";
        addStructureInfo(
            count + EnumChatFormatting.WHITE
                + String.join(NAME_SEPARATOR, line.names())
                + ": "
                + EnumChatFormatting.GRAY
                + String.join(NAME_SEPARATOR, line.locations()));
    }

    private void addSection(Section section, boolean leadingBlank) {
        if (leadingBlank && !section.blocks()
            .isEmpty()) addStructureInfo("");
        for (BlockLine block : section.blocks()) addCasing(block.count(), block.name(), block.tiered());
        for (MaterialTier tier : section.materialTiers()) {
            addStructureInfo("");
            addStructureInfo(tier.header());
            for (BlockLine block : tier.blocks()) addCasing(block.count(), block.name(), block.tiered());
        }
    }

    private void addChannelLines(StructureSummary summary, boolean structureTiers) {
        if (!structureTiers && summary.channels()
            .isEmpty()) return;
        addStructureInfo("");
        if (structureTiers) {
            addMasterChannel(StatCollector.translateToLocal("gtnl.structure.tooltip.channel.structure_tier"));
        }
        for (IStructureChannels channel : summary.channels()) addSubChannelUsage(channel);
    }

    private static String structureTierHeader(int tier) {
        String key = tier == 1 ? "gtnl.structure.tooltip.tier" : "gtnl.structure.tooltip.tier.extra";
        return StatCollector.translateToLocalFormatted(key, tier);
    }
}
