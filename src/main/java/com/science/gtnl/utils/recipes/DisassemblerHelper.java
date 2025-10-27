package com.science.gtnl.utils.recipes;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.recipe.gtnl.ShimmerRecipes;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.items.MetaGeneratedTool;
import gregtech.api.objects.GTItemStack;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtnhintergalactic.recipe.IGRecipeMaps;
import ic2.api.item.IC2Items;

public class DisassemblerHelper {

    public static final boolean DEBUG_MODE = "true".equals(System.getenv("DEBUG_DISASSEMBLER"));

    private static final List<GTItemStack> inputBlacklist = new ArrayList<>();

    static {
        inputBlacklist.add(new GTItemStack(ItemList.Casing_Coil_Superconductor.get(1)));
        inputBlacklist.add(new GTItemStack(Materials.Graphene.getDust(1)));
        inputBlacklist.add(new GTItemStack(ItemList.Circuit_Parts_Vacuum_Tube.get(1)));
        inputBlacklist.add(new GTItemStack(ItemList.Schematic.get(1)));

        if (Mods.Railcraft.isModLoaded()) {
            inputBlacklist.add(new GTItemStack(GTModHandler.getModItem(Mods.Railcraft.ID, "track", 1L, 0)));
            inputBlacklist.add(new GTItemStack(GTModHandler.getModItem(Mods.Railcraft.ID, "track", 1L, 736)));
            inputBlacklist.add(new GTItemStack(GTModHandler.getModItem(Mods.Railcraft.ID, "track", 1L, 816)));
        }

        inputBlacklist.add(new GTItemStack(IC2Items.getItem("mixedMetalIngot")));
        inputBlacklist.add(new GTItemStack(GTModHandler.getModItem(Mods.Railcraft.ID, "machine.alpha", 1, 14)));

        // region transformer
        inputBlacklist.add(new GTItemStack(ItemList.Transformer_MV_LV.get(1L)));
        inputBlacklist.add(new GTItemStack(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockElectric", 1L, 3)));
        inputBlacklist.add(new GTItemStack(ItemList.Transformer_HV_MV.get(1L)));
        inputBlacklist.add(new GTItemStack(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockElectric", 1L, 4)));
        inputBlacklist.add(new GTItemStack(ItemList.Transformer_EV_HV.get(1L)));
        inputBlacklist.add(new GTItemStack(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockElectric", 1L, 5)));
        inputBlacklist.add(new GTItemStack(ItemList.Transformer_IV_EV.get(1L)));
        inputBlacklist.add(new GTItemStack(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockElectric", 1L, 6)));
        // endregion

        inputBlacklist
            .add(new GTItemStack(GTModHandler.getModItem(Mods.AppliedEnergistics2.ID, "item.ItemMultiPart", 1L, 36)));
        inputBlacklist
            .add(new GTItemStack(GTModHandler.getModItem(Mods.AppliedEnergistics2.ID, "item.ItemMultiPart", 1L, 536)));

        // Radiation Proof Plate
        inputBlacklist
            .add(new GTItemStack(GTModHandler.getModItem(Mods.GoodGenerator.ID, "radiationProtectionPlate", 1L, 0)));
    }

    public interface GeneratedRecipeInfo<T> {

        T getOriginal();

        int getDebugIndex();

        String getInfo();
    }

    public static class AssemblerReversed implements GeneratedRecipeInfo<List<GTRecipe>> {

        private final List<GTRecipe> original;
        private final int debugIndex;
        private final String reason;

        public AssemblerReversed(List<GTRecipe> original, int debugIndex, String reason) {
            this.original = original;
            this.debugIndex = debugIndex;
            this.reason = reason;
        }

        @Override
        public List<GTRecipe> getOriginal() {
            return original;
        }

        @Override
        public int getDebugIndex() {
            return debugIndex;
        }

        @Override
        public String getInfo() {
            StringBuilder builder = new StringBuilder();
            builder.append("[[ Generated from Assembler recipes ]]\n");
            builder.append("Reason = ")
                .append(reason)
                .append("\n");
            builder.append("Debug Index = ")
                .append(debugIndex)
                .append("\n");
            for (int i = 0; i < original.size(); i++) {
                GTRecipe recipe = original.get(i);
                builder.append("<< Original recipe (")
                    .append(i)
                    .append(") >>\n");
                builder.append("inputs = ")
                    .append(Arrays.toString(recipe.mInputs))
                    .append("\n");
            }
            return builder.toString();
        }
    }

    public static class CraftingTableReversed implements GeneratedRecipeInfo<ReversedRecipeRegistry.GTCraftingRecipe> {

        private final ReversedRecipeRegistry.GTCraftingRecipe original;
        private final int debugIndex;

        public CraftingTableReversed(ReversedRecipeRegistry.GTCraftingRecipe original, int debugIndex) {
            this.original = original;
            this.debugIndex = debugIndex;
        }

        @Override
        public ReversedRecipeRegistry.GTCraftingRecipe getOriginal() {
            return original;
        }

        @Override
        public int getDebugIndex() {
            return debugIndex;
        }

        @Override
        public String getInfo() {
            return "[[ Generated from GregTech Crafting recipes ]]\n" + "Debug Index = "
                + debugIndex
                + "\n"
                + "inputs = "
                + Arrays.toString(original.getInputs())
                + "\n"
                + "stacktrace = "
                + original.getAdderStackTrace()
                    .toString()
                + "\n";
        }
    }

    public static final Map<GTRecipe, List<GeneratedRecipeInfo<?>>> debugRecipeToRecipe = DEBUG_MODE ? new HashMap<>()
        : null;
    public static final Map<Integer, GeneratedRecipeInfo<?>> debugIndexToRecipe = DEBUG_MODE ? new HashMap<>() : null;
    private static final AtomicInteger debugIndexBumper = new AtomicInteger(0);

    public static List<ItemStack> handleRecipeTransformation(ItemStack[] outputs,
        Set<ItemStack[]> outputsInOtherRecipes) {
        ItemStack[] retOutputs = new ItemStack[outputs.length];

        for (int idx = 0; idx < outputs.length; idx++) {
            ItemStack itemInSlotIdx = outputs[idx];
            ItemData itemDataInSlotIdx = GTOreDictUnificator.getItemData(itemInSlotIdx);

            if (itemDataInSlotIdx == null || itemDataInSlotIdx.mMaterial == null
                || itemDataInSlotIdx.mMaterial.mMaterial == null
                || itemDataInSlotIdx.mPrefix == null) {
                retOutputs[idx] = itemInSlotIdx;
                continue;
            }

            Materials thisMaterial = itemDataInSlotIdx.mMaterial.mMaterial;

            if (outputsInOtherRecipes != null) {
                for (ItemStack[] otherOutputs : outputsInOtherRecipes) {
                    if (idx >= otherOutputs.length) continue;

                    ItemData dataAgainst = GTOreDictUnificator.getItemData(otherOutputs[idx]);
                    if (dataAgainst != null && dataAgainst.mMaterial != null
                        && dataAgainst.mMaterial.mMaterial != null
                        && dataAgainst.mPrefix == itemDataInSlotIdx.mPrefix) {

                        // 1. replace cheaper
                        Materials cheaper = replaceCheaperOrNull(thisMaterial, dataAgainst.mMaterial.mMaterial);
                        if (cheaper != null) {
                            retOutputs[idx] = GTOreDictUnificator.get(
                                OrePrefixes.valueOf(itemDataInSlotIdx.mPrefix.name()),
                                cheaper,
                                itemInSlotIdx.stackSize);
                            continue;
                        }

                        // 2. replace "Any" material
                        Materials nonAny = replaceAnyOrNull(thisMaterial);
                        if (nonAny != null) {
                            retOutputs[idx] = GTOreDictUnificator.get(
                                OrePrefixes.valueOf(itemDataInSlotIdx.mPrefix.name()),
                                nonAny,
                                itemInSlotIdx.stackSize);
                        }
                    }
                }
            }

            // 3. unprocessed fallback
            Materials unprocessed = getUnprocessedMaterials(thisMaterial);
            if (unprocessed != null) {
                retOutputs[idx] = GTOreDictUnificator
                    .get(OrePrefixes.valueOf(itemDataInSlotIdx.mPrefix.name()), unprocessed, itemInSlotIdx.stackSize);
            }

            // 4. replace circuit
            if (itemDataInSlotIdx.mPrefix == OrePrefixes.circuit) {
                ItemStack circuit = getCheapestCircuitOrNull(thisMaterial);
                if (circuit != null) {
                    circuit.stackSize = itemInSlotIdx.stackSize;
                    retOutputs[idx] = circuit;
                }
            }
        }

        for (int idx = 0; idx < outputs.length; idx++) {
            ItemStack original = outputs[idx];
            ItemStack current = retOutputs[idx];

            if (current == null) {
                retOutputs[idx] = original;
                current = original;
            }

            if (GTUtility.areStacksEqual(current, original)) {
                current.stackSize = Math.min(current.stackSize, original.stackSize);
            }

            for (Map.Entry<ItemStack, ItemStack> entry : getAlwaysReplace()) {
                if (GTUtility.areStacksEqual(current, entry.getKey(), true)) {
                    retOutputs[idx] = entry.getValue()
                        .copy();
                    break;
                }
            }

            retOutputs[idx] = handleUnification(retOutputs[idx]);
            retOutputs[idx] = handleWildcard(retOutputs[idx]);
            retOutputs[idx] = handleContainerItem(retOutputs[idx]);
        }

        List<ItemStack> filtered = new ArrayList<>();
        for (ItemStack stack : retOutputs) {
            if (stack != null) {
                filtered.add(stack);
            }
        }
        return filtered;
    }

    private static Materials replaceCheaperOrNull(Materials first, Materials second) {
        if (first == second) return null;

        if (first == Materials.Aluminium && second == Materials.Iron) return second;
        if (first == Materials.Steel && second == Materials.Iron) return second;
        if (first == Materials.WroughtIron && second == Materials.Iron) return second;
        if (first == Materials.Aluminium && second == Materials.WroughtIron) return Materials.Iron;
        if (first == Materials.Aluminium && second == Materials.Steel) return second;

        if (first == Materials.Polytetrafluoroethylene && second == Materials.Plastic) return second;
        if (first == Materials.Polybenzimidazole && second == Materials.Plastic) return second;
        if (first == Materials.Polystyrene && second == Materials.Plastic) return second;
        if (first == Materials.Silicone && second == Materials.Plastic) return second;

        if ((first == Materials.NetherQuartz || first == Materials.CertusQuartz) && second == Materials.Quartzite)
            return second;

        if (first == Materials.Plastic && second == Materials.Wood) return second;
        if (first == Materials.Diamond && second == Materials.Glass) return second;

        return null;
    }

    private static Materials replaceAnyOrNull(Materials first) {
        List<Materials> list = first.mOreReRegistrations;

        if (list != null) {
            for (Materials reg : list) {
                if (reg == Materials.AnyIron) return Materials.Iron;
                if (reg == Materials.AnyCopper) return Materials.Copper;
                if (reg == Materials.AnyRubber) return Materials.Rubber;
                if (reg == Materials.AnyBronze) return Materials.Bronze;
                if (reg == Materials.AnySyntheticRubber) return Materials.Rubber;
            }
        }

        return null;
    }

    private static Materials getUnprocessedMaterials(Materials first) {
        if (first == Materials.SteelMagnetic) return Materials.Steel;
        if (first == Materials.IronMagnetic) return Materials.Iron;
        if (first == Materials.NeodymiumMagnetic) return Materials.Neodymium;
        if (first == Materials.SamariumMagnetic) return Materials.Samarium;
        if (first == Materials.AnnealedCopper) return Materials.Copper;
        return null;
    }

    private static ItemStack getCheapestCircuitOrNull(Materials material) {
        if (material == Materials.ULV) return ItemList.NandChip.get(1);
        if (material == Materials.LV) return ItemList.Circuit_Microprocessor.get(1);
        if (material == Materials.MV) return ItemList.Circuit_Good.get(1);
        if (material == Materials.Advanced) return ItemList.Circuit_Advanced.get(1);
        if (material == Materials.EV) return ItemList.Circuit_Data.get(1);
        if (material == Materials.LuV) return ItemList.Circuit_Master.get(1);
        if (material == Materials.Ultimate) return ItemList.Circuit_Quantummainframe.get(1);
        if (material == Materials.SuperconductorUHV) return ItemList.Circuit_Crystalmainframe.get(1);
        if (material == Materials.UHV) return ItemList.Circuit_Wetwaremainframe.get(1);
        if (material == Materials.UEV) return ItemList.Circuit_Biomainframe.get(1);
        return null;
    }

    private static List<Map.Entry<String, ItemStack>> getOreDictReplace() {
        List<Map.Entry<String, ItemStack>> list = new ArrayList<>();
        list.add(new AbstractMap.SimpleEntry<>("plankWood", new ItemStack(Blocks.planks)));
        list.add(new AbstractMap.SimpleEntry<>("stoneCobble", new ItemStack(Blocks.cobblestone)));
        list.add(new AbstractMap.SimpleEntry<>("gemDiamond", new ItemStack(Items.diamond)));
        list.add(new AbstractMap.SimpleEntry<>("logWood", new ItemStack(Blocks.log)));
        list.add(new AbstractMap.SimpleEntry<>("stickWood", new ItemStack(Items.stick)));
        list.add(new AbstractMap.SimpleEntry<>("treeSapling", new ItemStack(Blocks.sapling)));
        return list;
    }

    private static List<Map.Entry<ItemStack, ItemStack>> getAlwaysReplace() {
        List<Map.Entry<ItemStack, ItemStack>> list = new ArrayList<>();
        list.add(
            new AbstractMap.SimpleEntry<>(
                new ItemStack(Blocks.trapped_chest, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(Blocks.chest, 1, OreDictionary.WILDCARD_VALUE)));
        return list;
    }

    private static ItemStack handleUnification(ItemStack stack) {
        if (stack != null) {
            for (int oreId : OreDictionary.getOreIDs(stack)) {
                String oreName = OreDictionary.getOreName(oreId);
                for (Map.Entry<String, ItemStack> entry : getOreDictReplace()) {
                    if (oreName.equals(entry.getKey())) {
                        ItemStack result = entry.getValue()
                            .copy();
                        result.stackSize = stack.stackSize;
                        return result;
                    }
                }
            }
        }
        return GTOreDictUnificator.get(stack);
    }

    private static ItemStack handleWildcard(ItemStack stack) {
        if (stack != null && stack.getItemDamage() == OreDictionary.WILDCARD_VALUE
            && !stack.getItem()
                .isDamageable()) {
            stack.setItemDamage(0);
        }
        return stack;
    }

    private static ItemStack handleContainerItem(ItemStack stack) {
        if (stack != null && stack.getItem()
            .hasContainerItem(stack)) {
            return null;
        }
        return stack;
    }

    private static boolean shouldDisassemble(ItemStack[] mInputsOrOutputs) {
        return mInputsOrOutputs.length == 1 && shouldDisassembleItemStack(mInputsOrOutputs[0]);
    }

    /**
     * Check if the input item is valid for disassembling.
     */
    private static boolean shouldDisassembleItemStack(ItemStack stack) {
        if (stack == null) return false;

        if (stack.getItem() instanceof MetaGeneratedTool) return false;
        if (isCircuit(stack)) return false;
        if (isOre(stack)) return false;
        if (hasUnpackerRecipe(stack)) return false;

        for (GTItemStack blacklisted : inputBlacklist) {
            if (GTUtility.areStacksEqual(blacklisted.toStack(), stack, true)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCircuit(ItemStack stack) {
        ItemData data = GTOreDictUnificator.getAssociation(stack);
        return data != null && data.mPrefix == OrePrefixes.circuit;
    }

    private static boolean hasUnpackerRecipe(ItemStack stack) {
        return RecipeMaps.unpackagerRecipes.findRecipeQuery()
            .items(stack)
            .find() != null;
    }

    private static boolean isOre(ItemStack stack) {
        ItemData data = GTOreDictUnificator.getAssociation(stack);
        return data != null && (data.mPrefix == OrePrefixes.ore || data.mPrefix == OrePrefixes.crushed
            || data.mPrefix == OrePrefixes.crushedCentrifuged
            || data.mPrefix == OrePrefixes.crushedPurified);
    }

    private static GTRecipe getReversedRecipe(GTRecipe recipe) {
        GTRecipe copy = recipe.copy();
        copy.mInputs = recipe.mOutputs;
        copy.mOutputs = recipe.mInputs;
        return copy;
    }

    private static Multimap<GTItemStack, ItemStack> getOutputHardOverride() {
        Multimap<GTItemStack, ItemStack> map = ArrayListMultimap.create();
        map.put(new GTItemStack(new ItemStack(Blocks.torch, 6)), new ItemStack(Items.stick));
        return map;
    }

    public static void loadAssemblerRecipesToDisassembler() {
        Map<GTItemStack, List<GTRecipe>> assemblerRecipes = new HashMap<>();

        for (GTRecipe recipe : RecipeMaps.assemblerRecipes.getAllRecipes()) {
            if (recipe.mOutputs == null || recipe.mInputs == null || !shouldDisassemble(recipe.mOutputs)) continue;

            GTItemStack key = new GTItemStack(recipe.mOutputs[0]);
            assemblerRecipes.computeIfAbsent(key, k -> new ArrayList<>())
                .add(recipe);
        }

        int totalCount = assemblerRecipes.size();
        ScienceNotLeisure.LOG.info("Loading reversed assembler recipes, total: {}", totalCount);

        for (Map.Entry<GTItemStack, List<GTRecipe>> entry : assemblerRecipes.entrySet()) {
            List<GTRecipe> recipes = entry.getValue();

            try {
                List<GTRecipe> reversedRecipes = new ArrayList<>();
                for (GTRecipe recipe : recipes) {
                    reversedRecipes.add(getReversedRecipe(recipe));
                }

                if (reversedRecipes.isEmpty()) continue;

                GTRecipe reversedFirst = reversedRecipes.remove(0);
                ItemStack revInput = reversedFirst.mInputs[0];

                for (Map.Entry<GTItemStack, ItemStack> hardOverride : getOutputHardOverride().entries()) {
                    if (hardOverride.getKey()
                        .isStackEqual(revInput)) {

                        List<ItemStack> fluidStacks = new ArrayList<>();
                        if (reversedFirst.mFluidInputs != null) {
                            for (FluidStack fluid : reversedFirst.mFluidInputs) {
                                fluidStacks.add(ItemUtils.getFluidPacket(fluid, fluid.amount));
                            }
                        }
                        fluidStacks.add(hardOverride.getValue());
                        ItemStack[] stackArray = fluidStacks.toArray(new ItemStack[0]);

                        GTRecipeBuilder builder = GTValues.RA.stdBuilder()
                            .itemInputs(revInput)
                            .itemOutputs(stackArray)
                            .duration(100)
                            .eut(0)
                            .fake()
                            .setNEIDesc("Generated from Assembler Recipe (Hard-overriden)");
                        applyDebugAssembler(builder, recipes, true);
                        builder.addTo(RecipePool.ShimmerRecipes);
                        ShimmerRecipes.registerConversion(revInput, Collections.singletonList(hardOverride.getValue()));
                    }
                }

                List<ItemStack> transformedOutputs = handleRecipeTransformation(
                    reversedFirst.mOutputs,
                    reversedRecipes.stream()
                        .map(r -> r.mOutputs)
                        .collect(Collectors.toSet())).stream()
                            .filter(s -> GTUtility.isStackValid(s) && s.stackSize > 0)
                            .collect(Collectors.toList());

                List<ItemStack> fluidStacks = new ArrayList<>();
                if (reversedFirst.mFluidInputs != null) {
                    for (FluidStack fluid : reversedFirst.mFluidInputs) {
                        fluidStacks.add(ItemUtils.getFluidPacket(fluid, fluid.amount));
                    }
                }
                fluidStacks.addAll(transformedOutputs);
                ItemStack[] stacksArray = fluidStacks.toArray(new ItemStack[0]);

                GTRecipeBuilder builder = GTValues.RA.stdBuilder()
                    .itemInputs(revInput)
                    .itemOutputs(stacksArray)
                    .duration(100)
                    .eut(0)
                    .fake()
                    .setNEIDesc("Generated from Assembler Recipe");
                applyDebugAssembler(builder, recipes, false);
                builder.addTo(RecipePool.ShimmerRecipes);
                ShimmerRecipes
                    .registerConversion(revInput, Arrays.asList(transformedOutputs.toArray(new ItemStack[0])));

            } catch (Exception e) {
                ScienceNotLeisure.LOG.warn("Failed to process assembler -> disassembler recipe.");
                GTRecipe first = recipes.get(0);
                ScienceNotLeisure.LOG.warn("mInputs: {}", Arrays.toString(first.mInputs));
                ScienceNotLeisure.LOG.warn("mOutputs: {}", Arrays.toString(first.mOutputs));
                ScienceNotLeisure.LOG.warn("Exception: ", e);

                if (e instanceof ArrayIndexOutOfBoundsException) throw e;
            }
        }

        for (GTRecipe recipe : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (recipe == null || recipe.mOutputs == null) continue;

            ItemStack input = recipe.mOutputs[0];
            if (ShimmerRecipes.isInConversions(input)) continue;

            List<ItemStack> outputs = new ArrayList<>();
            for (ItemStack stack : recipe.mInputs) {
                if (stack != null) {
                    outputs.add(stack);
                }
            }
            if (recipe.mFluidInputs != null) {
                for (FluidStack fluid : recipe.mFluidInputs) {
                    outputs.add(ItemUtils.getFluidPacket(fluid, fluid.amount));
                }
            }
            if (outputs.isEmpty()) continue;

            GTValues.RA.stdBuilder()
                .itemInputs(input)
                .itemOutputs(outputs.toArray(new ItemStack[0]))
                .duration(100)
                .eut(0)
                .fake()
                .setNEIDesc("Generated from Assembly Line")
                .addTo(RecipePool.ShimmerRecipes);
            ShimmerRecipes.registerConversion(input, outputs);
        }

        for (GTRecipe recipe : IGRecipeMaps.spaceAssemblerRecipes.getAllRecipes()) {
            if (recipe == null || recipe.mOutputs == null) continue;

            ItemStack input = recipe.mOutputs[0];
            if (ShimmerRecipes.isInConversions(input)) continue;

            List<ItemStack> outputs = new ArrayList<>(new ArrayList<>(Arrays.asList(recipe.mInputs)));
            if (recipe.mFluidInputs != null) {
                for (FluidStack fluid : recipe.mFluidInputs) {
                    outputs.add(ItemUtils.getFluidPacket(fluid, fluid.amount));
                }
            }

            GTValues.RA.stdBuilder()
                .itemInputs(input)
                .itemOutputs(outputs.toArray(new ItemStack[0]))
                .duration(100)
                .eut(0)
                .fake()
                .setNEIDesc("Generated from Space Assembler")
                .addTo(RecipePool.ShimmerRecipes);
            ShimmerRecipes.registerConversion(input, outputs);
        }
    }

    public static void handleGTCraftingRecipe(ReversedRecipeRegistry.GTCraftingRecipe recipe) {
        GTRecipe revRecipe = recipe.toReversedSafe();
        if (revRecipe == null) return;

        if (revRecipe.mOutputs == null || revRecipe.mInputs == null || !shouldDisassemble(revRecipe.mInputs)) return;

        try {
            List<ItemStack> outputs = handleRecipeTransformation(revRecipe.mOutputs, null);
            GTRecipeBuilder builder = GTValues.RA.stdBuilder()
                .itemInputs(revRecipe.mInputs)
                .itemOutputs(outputs.toArray(new ItemStack[0]))
                .duration(100)
                .eut(0)
                .fake()
                .setNEIDesc("Generated from GT Crafting Recipe");
            applyDebugCrafting(builder, recipe);
            builder.addTo(RecipePool.ShimmerRecipes);
            ItemStack input = revRecipe.mInputs.length > 0 ? revRecipe.mInputs[0] : null;
            if (input != null) {
                ShimmerRecipes.registerConversion(input, Arrays.asList(outputs.toArray(new ItemStack[0])));
            }
        } catch (Exception e) {
            ScienceNotLeisure.LOG.warn("Failed to process reversed crafting table recipe to disassembler recipe.");
            ScienceNotLeisure.LOG.warn("mInputs: {}", Arrays.toString(revRecipe.mInputs));
            ScienceNotLeisure.LOG.warn("mOutputs: {}", Arrays.toString(revRecipe.mOutputs));
            ScienceNotLeisure.LOG.warn("Exception", e);
        }
    }

    private static GTRecipeBuilder applyDebugAssembler(GTRecipeBuilder builder, List<GTRecipe> original,
        boolean isHardOverride) {
        if (debugRecipeToRecipe == null) return builder;

        Optional<GTRecipe> generated = builder.build();
        if (!generated.isPresent()) return builder;

        GTRecipe result = generated.get();
        int index = debugIndexBumper.getAndIncrement();

        AssemblerReversed info = new AssemblerReversed(original, index, isHardOverride ? "Hard-override" : "Generated");

        debugRecipeToRecipe.computeIfAbsent(result, k -> new ArrayList<>())
            .add(info);
        debugIndexToRecipe.put(index, info);

        builder.setNEIDesc("Debug Index: " + index);
        return builder;
    }

    private static void applyDebugCrafting(GTRecipeBuilder builder, ReversedRecipeRegistry.GTCraftingRecipe original) {
        if (debugRecipeToRecipe == null) return;

        Optional<GTRecipe> generated = builder.build();
        if (!generated.isPresent()) return;

        GTRecipe result = generated.get();
        int index = debugIndexBumper.getAndIncrement();

        CraftingTableReversed info = new CraftingTableReversed(original, index);

        debugRecipeToRecipe.computeIfAbsent(result, k -> new ArrayList<>())
            .add(info);
        debugIndexToRecipe.put(index, info);

        builder.setNEIDesc("Debug Index: " + index);
    }

}
