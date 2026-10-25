package com.science.gtnl.api;

import java.util.Collections;
import java.util.List;

import gregtech.api.structure.IStructureChannels;

public interface IMultiblockTooltipRules {

    /**
     * The numbers shown at the top of the automatic tooltip, read from the machine in its base state: no parallel
     * controller, no upgrades, no energy hatch, heating coils and glass at tier 0 and, for tiered machines, the first
     * tier. All three lines are always shown, colored the GT way.
     *
     * @param parallels   parallel count
     * @param speed       speed multiplier compared to a single block machine, such as 1.67 for 167%
     * @param energyUsage EU or steam usage multiplier, such as 0.8 for 80%
     * @param steam       whether the machine runs on steam, which shows steam usage instead of EU usage
     */
    record TooltipStats(int parallels, double speed, double energyUsage, boolean steam) {}

    /**
     * Something whose tier changes a machine's numbers, shown as a "per tier" line under the matching number.
     */
    enum TieredPart {

        VOLTAGE("gtnl.structure.tooltip.part.voltage"),
        COIL("gtnl.structure.tooltip.part.coil"),
        GLASS("gtnl.structure.tooltip.part.glass");

        public final String langKey;

        TieredPart(String langKey) {
            this.langKey = langKey;
        }
    }

    /**
     * Returns the numbers shown at the top of the automatic tooltip.
     *
     * @return the numbers, or null to show none
     */
    default TooltipStats getTooltipStats() {
        return null;
    }

    /**
     * Returns the numbers with one tiered part set to a given tier and everything else in the base state of
     * {@link #getTooltipStats()}. The tooltip compares a few tiers to show the bonus per tier: the input voltage for
     * every machine, heating coils and glass when the structure contains them.
     *
     * @param part the tiered part
     * @param tier its tier, counted the way the machine's own formulas count it, such as 1 for an LV input
     * @return the numbers, or null when the machine has no bonus from this part
     */
    default TooltipStats getTooltipStatsAtTier(TieredPart part, int tier) {
        return null;
    }

    /**
     * Returns the lang keys of the feature lines every machine of this kind shares, shown after the machine's own
     * lines.
     *
     * @return the lang keys, in display order
     */
    default List<String> getTooltipFeatureKeys() {
        return Collections.emptyList();
    }

    /**
     * Returns the lang keys of the lines shown only when the structure accepts a hatch type, such as what a parallel
     * controller hatch does. They are shown between the machine's own lines and the feature lines.
     *
     * @param hatchLangKey description lang key of a hatch type the structure accepts
     * @return the lang keys, in display order
     */
    default List<String> getTooltipHatchFeatureKeys(String hatchLangKey) {
        return Collections.emptyList();
    }

    /**
     * Returns the lang key of the line that explains the material tiers, shown when the structure lists its blocks
     * by material tier.
     *
     * @return the lang key, or null to show none
     */
    default String getTooltipMaterialTierNotice() {
        return null;
    }

    /**
     * Puts hatch types that satisfy one structure requirement on one line, for requirements the structure check
     * enforces with its own code, such as "any one kind of steam input".
     *
     * @param hatchLangKey description lang key of a hatch type the structure accepts
     * @return the line key; returning {@code hatchLangKey} keeps the hatch type on a line of its own
     */
    default String getStructureTooltipHatchLine(String hatchLangKey) {
        return hatchLangKey;
    }

    /**
     * Returns the count of a hatch line whose requirement the structure check enforces with its own code instead of
     * the GT and GTNL hatch count checks, in the GT style such as "1", "1+" or "0-1".
     *
     * @param hatchLine the line key returned by {@link #getStructureTooltipHatchLine(String)}
     * @return the count text, or null to read the count from the hatch count checks
     */
    default String getStructureTooltipHatchCount(String hatchLine) {
        return null;
    }

    /**
     * Returns how many material tiers the structure blocks come in, such as bronze and steel. The blocks are then
     * listed once per tier.
     *
     * @return the number of material tiers, or 0 when the blocks have none
     */
    default int getStructureTooltipMaterialTiers() {
        return 0;
    }

    /**
     * Returns the localized header above the blocks of one material tier.
     *
     * @param tier material tier, starting at 1
     * @return the header, or null to use the default tier header
     */
    default String getStructureTooltipMaterialTierHeader(int tier) {
        return null;
    }

    /**
     * Returns the projector channel that picks the material tier.
     *
     * @return the channel, or null to list none
     */
    default IStructureChannels getStructureTooltipMaterialTierChannel() {
        return null;
    }
}
