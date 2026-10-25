package com.science.gtnl.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.structure.IStructureChannels;

public interface IMultiblockTooltipRules {

    /** Tier skip count of a machine that runs no recipe above the tier of its energy input. */
    int NO_TIER_SKIP = 0;
    /** Tier skip count of a machine that runs recipes of any tier. */
    int UNLIMITED_TIER_SKIPS = Integer.MAX_VALUE;

    /**
     * The numbers shown at the top of the automatic tooltip, read from the machine in its base state: no parallel
     * controller, no upgrades, no energy hatch, heating coils and glass at tier 0 and, for tiered machines, the first
     * tier. All three lines are always shown, colored the GT way.
     *
     * @param parallels        parallel count
     * @param speed            speed multiplier compared to a single block machine, such as 1.67 for 167%
     * @param energyUsage      EU or steam usage multiplier, such as 0.8 for 80%
     * @param steam            whether the machine runs on steam, which shows steam usage instead of EU usage
     * @param perfectOverclock whether overclocking keeps full efficiency in this state
     */
    record TooltipStats(int parallels, double speed, double energyUsage, boolean steam, boolean perfectOverclock) {}

    /**
     * Something whose tier changes a machine's numbers, shown as a "per tier" line under the matching number.
     * {@code maxTier} is the highest tier a built machine can have.
     */
    enum TieredPart {

        VOLTAGE("gtnl.structure.tooltip.part.voltage", VoltageIndex.MAX),
        COIL("gtnl.structure.tooltip.part.coil", HeatingCoilLevel.getMaxTier()),
        // Why: GT's GlassTier keeps its top tier private; it is UXV.
        GLASS("gtnl.structure.tooltip.part.glass", VoltageIndex.UXV),
        // Why: component assembly line casings have the widest casing range, LV at 0 to UXV at 12.
        CASING("gtnl.structure.tooltip.part.casing", 12),
        HIGH_PRESSURE("gtnl.structure.tooltip.part.high_pressure", 1);

        public final String langKey;
        public final int maxTier;

        TieredPart(String langKey, int maxTier) {
            this.langKey = langKey;
            this.maxTier = maxTier;
        }
    }

    /**
     * Returns the numbers shown at the top of the automatic tooltip. The perfect overclock and tier skip lines are
     * shown only together with the numbers.
     *
     * @return the numbers, or null to show none, such as for a machine that runs no recipes
     */
    default TooltipStats getTooltipStats() {
        return null;
    }

    /**
     * Returns the numbers with one tiered part set to a given tier and everything else in the base state of
     * {@link #getTooltipStats()}. The tooltip compares a few tiers to show the bonus per tier: the input voltage for
     * every machine, heating coils and glass when the structure contains them. {@link TieredPart#HIGH_PRESSURE} is a
     * switch instead: tier 1 is the high pressure build, and its difference from the base is shown once.
     *
     * @param part the tiered part
     * @param tier its tier, counted the way the machine's own formulas count it, such as 1 for an LV input
     * @return the numbers, or null when the machine has no bonus from this part
     */
    default TooltipStats getTooltipStatsAtTier(TieredPart part, int tier) {
        return null;
    }

    /**
     * Returns the values the machine's markdown tooltip shows with {@code {var:name}}, already formatted for display.
     * Every machine with numbers also gets {@code parallels}, {@code speed} and {@code energy_usage}.
     *
     * @return the values by name
     */
    default Map<String, Object> getTooltipVars() {
        return Collections.emptyMap();
    }

    /**
     * Returns localized feature lines built from the machine's own values, such as which glass tier lifts the energy
     * hatch limit, shown after the machine's own lines.
     *
     * @return the lines, in display order
     */
    default List<String> getTooltipFeatureLines() {
        return Collections.emptyList();
    }

    /**
     * Returns how many voltage tiers a recipe may be above the machine's energy input, shown with GT's tier skip line
     * after the feature lines.
     *
     * @return the tier count, {@link #NO_TIER_SKIP} or {@link #UNLIMITED_TIER_SKIPS}, or empty to show no line
     */
    default OptionalInt getTooltipTierSkips() {
        return OptionalInt.empty();
    }

    /**
     * How the voltage raises the machine heat, and how heat above a recipe's required heat changes the recipe, as the
     * machine's overclock calculator applies it.
     *
     * @param overclock          whether each heat overclock step above the recipe heat grants a perfect overclock
     * @param discount           whether each heat discount step above the recipe heat lowers the EU usage
     * @param discountFactor     EU usage multiplier per discount step, such as 0.95 for 5% less
     * @param usesRecipeHeat     whether the calculator compares the machine heat with the recipe's required heat
     * @param heatPerVoltageTier heat in kelvin that each voltage tier past {@code voltageHeatFrom} adds, or 0 when the
     *                           voltage does not change the heat
     * @param voltageHeatFrom    the voltage tier that adds no heat; each tier below it takes the same heat away
     */
    record HeatRules(boolean overclock, boolean discount, double discountFactor, boolean usesRecipeHeat,
        int heatPerVoltageTier, int voltageHeatFrom) {}

    /**
     * Returns how heat changes this machine's recipes, shown after the feature lines.
     *
     * @return the rules, or null when heat changes nothing
     */
    default HeatRules getTooltipHeatRules() {
        return null;
    }

    /**
     * Returns the localized lines shown only when the structure accepts a hatch type, such as what a parallel
     * controller hatch does. All such lines form one group under a separator near the end of the tooltip.
     *
     * @param hatchLangKey description lang key of a hatch type the structure accepts
     * @return the lines, in display order
     */
    default List<String> getTooltipHatchLines(String hatchLangKey) {
        return Collections.emptyList();
    }

    /**
     * Returns the localized lines about the machine's upgrades and modes, such as the wireless upgrade, shown as one
     * group under a separator after the hatch group.
     *
     * @return the lines, in display order
     */
    default List<String> getTooltipUpgradeLines() {
        return Collections.emptyList();
    }

    /**
     * Puts hatch types that count as one kind of hatch on one line under one name, such as every kind of steam input
     * shown as a steam hatch. The line's count still comes from the structure check.
     *
     * @param hatchLangKey description lang key of a hatch type the structure accepts
     * @return the lang key that names the line; returning {@code hatchLangKey} keeps the hatch type on a line of its
     *         own under its own name
     */
    default String getStructureTooltipHatchLine(String hatchLangKey) {
        return hatchLangKey;
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
