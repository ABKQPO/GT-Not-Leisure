package com.science.gtnl.utils.structure;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IMultiblockTooltipRules.TooltipStats;
import com.science.gtnl.utils.structure.InfoSummary.BonusStat;
import com.science.gtnl.utils.structure.InfoSummary.TierBonus;
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

    private static final String NAME_SEPARATOR = " / ";
    // Why: GT's stat methods add a finished line, so these lines are rebuilt from GT's keys to append the bonuses.
    private static final String PARALLEL_KEY = "GT5U.MBTT.Parallel.Base";
    private static final String SPEED_KEY = "GT5U.MBTT.Speed.Base";
    private static final String EU_KEY = "GT5U.MBTT.EuDiscount.Base";
    private static final String STEAM_KEY = "GT5U.MBTT.SteamDiscount.Base";
    private static final String WITH_BONUS_KEY = "gtnl.structure.tooltip.stat_with_bonus";
    private static final String BONUS_KEY = "gtnl.structure.tooltip.per_tier";
    private static final String DURATION_BONUS_KEY = "gtnl.structure.tooltip.per_tier.duration";
    private static final String INCREASE_KEY = "gtnl.structure.tooltip.change.increase";
    private static final String DECREASE_KEY = "gtnl.structure.tooltip.change.decrease";
    private static final String COMPOUND_KEY = "gtnl.structure.tooltip.change.compound";

    private StructureSummary structure;
    private boolean structureRead;

    @Override
    public GTNLMultiblockTooltipBuilder addMachineType(String machine) {
        super.addMachineType(machine);
        return this;
    }

    @Override
    public GTNLMultiblockTooltipBuilder addInfo(String info) {
        super.addInfo(info);
        return this;
    }

    @Override
    public GTNLMultiblockTooltipBuilder addSeparator() {
        super.addSeparator();
        return this;
    }

    @Override
    public GTNLMultiblockTooltipBuilder addSeparator(EnumChatFormatting color, int length) {
        super.addSeparator(color, length);
        return this;
    }

    @Override
    public GTNLMultiblockTooltipBuilder addSupportAny() {
        super.addSupportAny();
        return this;
    }

    @Override
    public GTNLMultiblockTooltipBuilder addSupportMultiAmp() {
        super.addSupportMultiAmp();
        return this;
    }

    @Override
    public GTNLMultiblockTooltipBuilder addPerfectOCInfo() {
        super.addPerfectOCInfo();
        return this;
    }

    public <T extends MTEEnhancedMultiBlockBase<T>> GTNLMultiblockTooltipBuilder addAutoInfo(T machine) {
        return addAutoInfo(machine, MultiblockTooltipReader.keyPrefixOf(machine));
    }

    public <T extends MTEEnhancedMultiBlockBase<T>> GTNLMultiblockTooltipBuilder addAutoInfo(T machine,
        String keyPrefix) {
        InfoSummary info = MultiblockTooltipReader.readInfo(machine, keyPrefix, structureOf(machine));
        if (info == null) return this;
        if (info.machineType() != null) addMachineType(info.machineType());
        TooltipStats stats = info.stats();
        if (stats != null) addStatLines(stats, info.tierBonuses());
        for (String line : info.lines()) addInfo(line);
        if (info.supportAny()) addSupportAny();
        else if (info.supportMultiAmp()) addSupportMultiAmp();
        if (info.materialTierNotice() != null) addInfo(info.materialTierNotice());
        return this;
    }

    public <T extends MTEEnhancedMultiBlockBase<T>> GTNLMultiblockTooltipBuilder addAutoStructure(T machine) {
        StructureSummary summary = structureOf(machine);
        if (summary == null) return this;
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
        return this;
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
