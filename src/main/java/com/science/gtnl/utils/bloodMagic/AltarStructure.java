/*
 * Original class by https://github.com/Superfrogman98, copied with permission Original PR:
 * https://github.com/GTNewHorizons/StructureCompat/pull/6 Link to message giving permission (GT: New Horizons Discord
 * server): https://discord.com/channels/181078474394566657/603348502637969419/1367345532376453130
 */

package com.science.gtnl.utils.bloodMagic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import com.github.bsideup.jabel.Desugar;
import com.google.common.base.Joiner;
import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import WayofTime.alchemicalWizardry.ModBlocks;
import WayofTime.alchemicalWizardry.api.BlockStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class AltarStructure {

    public static final String STRUCTURE_ALTAR = "tier1";
    public static final String STRUCTURE_TIER_2 = "tier2";
    public static final String STRUCTURE_TIER_3 = "tier3";
    public static final String STRUCTURE_TIER_4 = "tier4";
    public static final String STRUCTURE_TIER_5 = "tier5";
    public static final String STRUCTURE_TIER_6 = "tier6";
    @SuppressWarnings("rawtypes")
    public static final AnyBlock ANY_BLOCK = new AnyBlock();
    public static StructureDefinition.Builder<TileEntityCommandBlock> altarBuilder;

    public static final int[][] TIER_OFFSET = { { 0, 0, 0 }, { 1, -1, 1 }, { 3, 1, 3 }, { 5, 2, 5 }, { 8, -3, 8 },
        { 11, 3, 11 } };

    @SuppressWarnings("unchecked")
    public static void registerAltarStructureInfo() {
        altarBuilder = IStructureDefinition.builder();
        String T6_S = "                       ";
        String T6_SR = "r                     r";
        // region Structure
        // spotless:off
        altarBuilder
                .addShape(STRUCTURE_ALTAR, new String[][] {{"a"}})
                .addShape(STRUCTURE_TIER_2, StructureUtility.transpose(new String[][] {{"rrr","r r","rrr"}}))
                .addShape(STRUCTURE_TIER_3, StructureUtility.transpose(new String[][] {{"g     g","       ","       ","       ","       ","       ","g     g"},
                        {"t     t","       ","       ","       ","       ","       ","t     t"},
                        {"t     t","       ","       ","       ","       ","       ","t     t"},
                        {" rrrrr ","r     r","r     r","r     r","r     r","r     r"," rrrrr "}}))
                .addShape(STRUCTURE_TIER_4, StructureUtility.transpose(new String[][]
                        {{"b         b","           ","           ","           ","           ","           ","           ","           ","           ","           ","b         b"},
                                {"v         v","           ","           ","           ","           ","           ","           ","           ","           ","           ","v         v"},
                                {"v         v","           ","           ","           ","           ","           ","           ","           ","           ","           ","v         v"},
                                {"v         v","           ","           ","           ","           ","           ","           ","           ","           ","           ","v         v"},
                                {"v         v","           ","           ","           ","           ","           ","           ","           ","           ","           ","v         v"},
                                {"  rrrrrrr  ","           ","r         r","r         r","r         r","r         r","r         r","r         r","r         r","           ","  rrrrrrr  "}}))
                .addShape(STRUCTURE_TIER_5, StructureUtility.transpose(new String[][] {{"e               e","                 ","                 ","                 ","                 ","                 ","                 ","                 ","                 ","                 ","                 ","                 ","                 ","                 ","                 ","                 ","e               e"},
                        {"  rrrrrrrrrrrrr  ","                 ","r               r","r               r","r               r","r               r","r               r","r               r","r               r","r               r","r               r","r               r","r               r","r               r","r               r","                 ","  rrrrrrrrrrrrr  "}}))
                .addShape(STRUCTURE_TIER_6, StructureUtility.transpose(new String[][]
                        {
                                {"c                     c",T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,"c                     c"},
                                {"i                     i",T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,"i                     i"},
                                {"i                     i",T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,"i                     i"},
                                {"i                     i",T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,"i                     i"},
                                {"i                     i",T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,"i                     i"},
                                {"i                     i",T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,"i                     i"},
                                {"i                     i",T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,"i                     i"},
                                {"i                     i",T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,T6_S,"i                     i"},
                                {"  rrrrrrrrrrrrrrrrrrr  ",T6_S,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_SR,T6_S, "  rrrrrrrrrrrrrrrrrrr  "},
                        }))
                .addElement('a',StructureUtility.ofBlock(Blocks.command_block,0))
                .addElement('r',StructureUtility.ofChain(StructureUtility.ofBlockAnyMeta(ModBlocks.bloodRune),StructureUtility.ofBlockAnyMeta(ModBlocks.speedRune),StructureUtility.ofBlockAnyMeta(ModBlocks.runeOfSelfSacrifice),StructureUtility.ofBlockAnyMeta(ModBlocks.runeOfSacrifice),StructureUtility.ofBlockAnyMeta(ModBlocks.efficiencyRune)));

        char[] keys = {'t', 'g', 'v', 'b', 'e', 'i', 'c'};
        List<BlockStack>[] values = new List[] {
                AlchemicalWizardry.thirdTierPillars,
                AlchemicalWizardry.thirdTierCaps,
                AlchemicalWizardry.fourthTierPillars,
                AlchemicalWizardry.fourthTierCaps,
                AlchemicalWizardry.fifthTierBeacons,
                AlchemicalWizardry.sixthTierPillars,
                AlchemicalWizardry.sixthTierCaps
        };

        for (int i = 0; i < keys.length; i++) {
            if (values[i].isEmpty()) {
                altarBuilder.addElement(keys[i], ANY_BLOCK);
                continue;
            }

            altarBuilder.addElement(keys[i],
                    StructureUtility.ofChain(
                            values[i].stream()
                                    .map(s -> s.getMeta() == OreDictionary.WILDCARD_VALUE
                                            ? StructureUtility.ofBlockAnyMeta(s.getBlock())
                                            : StructureUtility.ofBlock(s.getBlock(), s.getMeta()))
                                    .toArray(IStructureElement[]::new)
                    )
            );
        }

        IMultiblockInfoContainer.registerTileClass(TileEntityCommandBlock.class,
            new AltarStructure.AltarMultiblockInfoContainer(altarBuilder.build()));
        // spotless:on
    }

    @Desugar
    public record AltarMultiblockInfoContainer(IStructureDefinition<TileEntityCommandBlock> structureAltar)
        implements IMultiblockInfoContainer<TileEntityCommandBlock> {

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TileEntityCommandBlock ctx,
            ExtendedFacing aSide) {
            int tier = triggerStack.stackSize;
            if (tier > 6) {
                tier = 6;
            }
            for (int i = 1; i <= tier; i++) {
                this.structureAltar.buildOrHints(
                    ctx,
                    triggerStack,
                    "tier" + i,
                    ctx.getWorldObj(),
                    ExtendedFacing.DEFAULT,
                    ctx.xCoord,
                    ctx.yCoord,
                    ctx.zCoord,
                    TIER_OFFSET[i - 1][0],
                    TIER_OFFSET[i - 1][1],
                    TIER_OFFSET[i - 1][2],
                    hintsOnly);
            }
        }

        @Override
        public int survivalConstruct(ItemStack triggerStack, int elementBudge, ISurvivalBuildEnvironment env,
            TileEntityCommandBlock altar, ExtendedFacing aSide) {
            int built = 0;
            int tier = triggerStack.stackSize;
            if (tier > 6) {
                tier = 6;
            }

            for (int i = 1; i <= tier; i++) {
                built += this.structureAltar.survivalBuild(
                    altar,
                    triggerStack,
                    "tier" + i,
                    altar.getWorldObj(),
                    ExtendedFacing.DEFAULT,
                    altar.xCoord,
                    altar.yCoord,
                    altar.zCoord,
                    TIER_OFFSET[i - 1][0],
                    TIER_OFFSET[i - 1][1],
                    TIER_OFFSET[i - 1][2],
                    elementBudge,
                    env,
                    false);
            }
            return built;
        }

        @SuppressWarnings("unchecked")
        @Override
        public String[] getDescription(ItemStack stackSize) {
            ArrayList<String> out = new ArrayList<>();
            out.add(StatCollector.translateToLocal("structurelib.bloodmagic.altar.1"));
            out.add(StatCollector.translateToLocal("structurelib.bloodmagic.altar.2"));

            IntArrayList pillarTiers = new IntArrayList(new int[] { 3, 4, 6 });
            List<BlockStack>[] pillarLists = new List[] { AlchemicalWizardry.thirdTierPillars,
                AlchemicalWizardry.fourthTierPillars, AlchemicalWizardry.sixthTierPillars };

            IntArrayList pillars = new IntArrayList();

            for (int i = 0; i < pillarTiers.size(); i++) {
                if (pillarLists[i].isEmpty()) {
                    pillars.add(pillarTiers.getInt(i));
                }
            }

            if (!pillars.isEmpty()) {
                out.add(
                    StatCollector.translateToLocalFormatted(
                        "structurelib.bloodmagic.altar.3",
                        Joiner.on(", ")
                            .join(pillars)));
            }

            IntArrayList capTiers = new IntArrayList(new int[] { 3, 4, 6 });
            List<BlockStack>[] capLists = new List[] { AlchemicalWizardry.thirdTierCaps,
                AlchemicalWizardry.fourthTierCaps, AlchemicalWizardry.sixthTierCaps };

            IntArrayList caps = new IntArrayList();
            for (int i = 0; i < capTiers.size(); i++) {
                if (capLists[i].isEmpty()) {
                    caps.add(capTiers.getInt(i));
                }
            }

            if (!caps.isEmpty()) {
                out.add(
                    StatCollector.translateToLocalFormatted(
                        "structurelib.bloodmagic.altar.4",
                        Joiner.on(", ")
                            .join(caps)));
            }

            if (AlchemicalWizardry.fifthTierBeacons.isEmpty()) {
                out.add(StatCollector.translateToLocal("structurelib.bloodmagic.altar.5"));
            }

            return out.toArray(new String[0]);
        }
    }
}
