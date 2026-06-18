package com.science.gtnl.utils.recipes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.dreammaster.item.NHItemList;
import com.github.bsideup.jabel.Desugar;
import com.glodblock.github.common.item.ItemFluidPacket;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.gtnl.ShimmerRecipes;
import com.science.gtnl.config.MainConfig;

import appeng.api.AEApi;
import appeng.api.util.AEColor;
import cpw.mods.fml.common.Optional.Method;
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
import gregtech.api.util.GTUtility;
import gtnhintergalactic.recipe.IGRecipeMaps;
import ic2.api.item.IC2Items;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import tectech.thing.CustomItemList;

public class DisassemblerHelper {

    public static final boolean DEBUG_MODE = "true".equals(System.getenv("DEBUG_DISASSEMBLER"));

    public static final ObjectArrayList<GTItemStack> inputBlacklist = new ObjectArrayList<>();

    static {
        inputBlacklist.add(new GTItemStack(ItemList.Casing_Coil_Superconductor.get(1)));
        inputBlacklist.add(new GTItemStack(Materials.Graphene.getDust(1)));
        inputBlacklist.add(new GTItemStack(ItemList.Circuit_Parts_Vacuum_Tube.get(1)));
        inputBlacklist.add(new GTItemStack(ItemList.Schematic.get(1)));
        inputBlacklist.add(new GTItemStack(ItemList.ZPM.get(1)));
        inputBlacklist.add(new GTItemStack(CustomItemList.hatch_CreativeMaintenance.get(1)));

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

        var aeParts = AEApi.instance()
            .definitions()
            .parts();

        inputBlacklist.add(
            new GTItemStack(
                aeParts.craftingTerminal()
                    .maybeStack(1)
                    .orNull()));
        inputBlacklist.add(
            new GTItemStack(
                aeParts.cableDense()
                    .stack(AEColor.Transparent, 1)));

        // Radiation Proof Plate
        inputBlacklist
            .add(new GTItemStack(GTModHandler.getModItem(Mods.GoodGenerator.ID, "radiationProtectionPlate", 1L, 0)));
    }

    public interface GeneratedRecipeInfo<T> {

        T original();

        int debugIndex();

        String getInfo();
    }

    @Desugar
    public record AssemblerReversed(ObjectList<GTRecipe> original, int debugIndex, String reason)
        implements GeneratedRecipeInfo<ObjectList<GTRecipe>> {

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

    @Desugar
    public record CraftingTableReversed(ReversedRecipeRegistry.GTCraftingRecipe original, int debugIndex)
        implements GeneratedRecipeInfo<ReversedRecipeRegistry.GTCraftingRecipe> {

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

    public static final Object2ObjectOpenHashMap<GTRecipe, ObjectList<GeneratedRecipeInfo<?>>> debugRecipeToRecipe = DEBUG_MODE
        ? new Object2ObjectOpenHashMap<>()
        : null;
    public static final Int2ObjectOpenHashMap<GeneratedRecipeInfo<?>> debugIndexToRecipe = DEBUG_MODE
        ? new Int2ObjectOpenHashMap<>()
        : null;
    public static final AtomicInteger debugIndexBumper = new AtomicInteger(0);

    public static ObjectList<ItemStack> handleRecipeTransformation(ItemStack[] outputs,
        ObjectOpenHashSet<ItemStack[]> outputsInOtherRecipes) {
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
                                OrePrefixes.getPrefix(itemDataInSlotIdx.mPrefix.getName()),
                                cheaper,
                                itemInSlotIdx.stackSize);
                            continue;
                        }

                        // 2. replace "Any" material
                        Materials nonAny = replaceAnyOrNull(thisMaterial);
                        if (nonAny != null) {
                            retOutputs[idx] = GTOreDictUnificator.get(
                                OrePrefixes.getPrefix(itemDataInSlotIdx.mPrefix.getName()),
                                nonAny,
                                itemInSlotIdx.stackSize);
                        }
                    }
                }
            }

            // 3. unprocessed fallback
            Materials unprocessed = getUnprocessedMaterials(thisMaterial);
            if (unprocessed != null) {
                retOutputs[idx] = GTOreDictUnificator.get(
                    OrePrefixes.getPrefix(itemDataInSlotIdx.mPrefix.getName()),
                    unprocessed,
                    itemInSlotIdx.stackSize);
            }

            // 4. replace circuit
            if (itemDataInSlotIdx.mPrefix == OrePrefixes.circuit) {
                ItemStack circuit = Mods.NewHorizonsCoreMod.isModLoaded() ? getCheapestCircuitOrNull(thisMaterial)
                    : null;
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

            for (Object2ObjectMap.Entry<ItemStack, ItemStack> entry : getAlwaysReplace().object2ObjectEntrySet()) {
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

        return Arrays.stream(retOutputs)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    public static Materials replaceCheaperOrNull(Materials first, Materials second) {
        if (first == second) return null;

        if (first == Materials.Aluminium && second == Materials.Iron) return second;
        if (first == Materials.Steel && second == Materials.Iron) return second;
        if (first == Materials.CastIron && second == Materials.Iron) return second;
        if (first == Materials.Aluminium && second == Materials.CastIron) return Materials.Iron;
        if (first == Materials.Aluminium && second == Materials.Steel) return second;

        if (first == Materials.Polytetrafluoroethylene && second == Materials.Polyethylene) return second;
        if (first == Materials.Polybenzimidazole && second == Materials.Polyethylene) return second;
        if (first == Materials.Polystyrene && second == Materials.Polyethylene) return second;
        if (first == Materials.RubberSilicone && second == Materials.Polyethylene) return second;

        if ((first == Materials.NetherQuartz || first == Materials.CertusQuartz) && second == Materials.Quartzite)
            return second;

        if (first == Materials.Polyethylene && second == Materials.Wood) return second;
        if (first == Materials.Diamond && second == Materials.Glass) return second;

        return null;
    }

    public static Materials replaceAnyOrNull(Materials first) {
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

    public static Materials getUnprocessedMaterials(Materials first) {
        if (first == Materials.SteelMagnetic) return Materials.Steel;
        if (first == Materials.IronMagnetic) return Materials.Iron;
        if (first == Materials.NeodymiumMagnetic) return Materials.Neodymium;
        if (first == Materials.SamariumMagnetic) return Materials.Samarium;
        if (first == Materials.AnnealedCopper) return Materials.Copper;
        return null;
    }

    @Method(modid = "dreamcraft")
    public static ItemStack getCheapestCircuitOrNull(Materials material) {
        if (material == Materials.ULV) return NHItemList.CircuitULV.get(1);
        if (material == Materials.LV) return NHItemList.CircuitLV.get(1);
        if (material == Materials.MV) return NHItemList.CircuitMV.get(1);
        if (material == Materials.HV) return NHItemList.CircuitHV.get(1);
        if (material == Materials.EV) return NHItemList.CircuitEV.get(1);
        if (material == Materials.IV) return NHItemList.CircuitIV.get(1);
        if (material == Materials.LuV) return NHItemList.CircuitLuV.get(1);
        if (material == Materials.ZPM) return NHItemList.CircuitZPM.get(1);
        if (material == Materials.UV) return NHItemList.CircuitUV.get(1);
        if (material == Materials.UHV) return NHItemList.CircuitUHV.get(1);
        if (material == Materials.UEV) return NHItemList.CircuitUEV.get(1);
        if (material == Materials.UIV) return NHItemList.CircuitUIV.get(1);
        if (material == Materials.UMV) return NHItemList.CircuitUMV.get(1);
        if (material == Materials.UXV) return NHItemList.CircuitUXV.get(1);
        if (material == Materials.MAX) return NHItemList.CircuitMAX.get(1);
        return null;
    }

    public static Object2ObjectMap<String, ItemStack> getOreDictReplace() {
        Object2ObjectMap<String, ItemStack> map = new Object2ObjectArrayMap<>();
        map.put("plankWood", new ItemStack(Blocks.planks));
        map.put("stoneCobble", new ItemStack(Blocks.cobblestone));
        map.put("gemDiamond", new ItemStack(Items.diamond));
        map.put("logWood", new ItemStack(Blocks.log));
        map.put("stickWood", new ItemStack(Items.stick));
        map.put("treeSapling", new ItemStack(Blocks.sapling));
        return map;
    }

    public static Object2ObjectMap<ItemStack, ItemStack> getAlwaysReplace() {
        Object2ObjectMap<ItemStack, ItemStack> map = new Object2ObjectLinkedOpenHashMap<>();
        map.put(
            new ItemStack(Blocks.trapped_chest, 1, OreDictionary.WILDCARD_VALUE),
            new ItemStack(Blocks.chest, 1, OreDictionary.WILDCARD_VALUE));
        return map;
    }

    public static ItemStack handleUnification(ItemStack stack) {
        if (stack != null) {
            for (int oreId : OreDictionary.getOreIDs(stack)) {
                String oreName = OreDictionary.getOreName(oreId);
                Object2ObjectMap<String, ItemStack> oreDictReplace = getOreDictReplace();
                if (oreDictReplace.containsKey(oreName)) {
                    ItemStack result = oreDictReplace.get(oreName)
                        .copy();
                    result.stackSize = stack.stackSize;
                    return result;
                }
            }
        }
        return GTOreDictUnificator.get(stack);
    }

    public static ItemStack handleWildcard(ItemStack stack) {
        if (stack != null && stack.getItemDamage() == OreDictionary.WILDCARD_VALUE
            && !stack.getItem()
                .isDamageable()) {
            stack.setItemDamage(0);
        }
        return stack;
    }

    public static ItemStack handleContainerItem(ItemStack stack) {
        if (stack != null && stack.getItem()
            .hasContainerItem(stack)) {
            return null;
        }
        return stack;
    }

    public static boolean shouldDisassemble(ItemStack[] mInputsOrOutputs) {
        return mInputsOrOutputs.length == 1 && shouldDisassembleItemStack(mInputsOrOutputs[0]);
    }

    /**
     * Check if the input item is valid for disassembling.
     */
    public static boolean shouldDisassembleItemStack(ItemStack stack) {
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

    public static boolean isCircuit(ItemStack stack) {
        ItemData data = GTOreDictUnificator.getAssociation(stack);
        return data != null && data.mPrefix == OrePrefixes.circuit;
    }

    public static boolean hasUnpackerRecipe(ItemStack stack) {
        return RecipeMaps.unpackagerRecipes.findRecipeQuery()
            .items(stack)
            .find() != null;
    }

    public static boolean isOre(ItemStack stack) {
        ItemData data = GTOreDictUnificator.getAssociation(stack);
        return data != null && (data.mPrefix == OrePrefixes.ore || data.mPrefix == OrePrefixes.crushed
            || data.mPrefix == OrePrefixes.crushedCentrifuged
            || data.mPrefix == OrePrefixes.crushedPurified);
    }

    public static GTRecipe getReversedRecipe(GTRecipe recipe) {
        GTRecipe copy = recipe.copy();
        copy.mInputs = recipe.mOutputs;
        copy.mOutputs = recipe.mInputs;
        return copy;
    }

    public static void loadHardOverrideRecipes() {
        for (GTRecipe recipe : GTNLRecipeMaps.HardOverrideRecipes.getAllRecipes()) {
            if (recipe == null || recipe.mOutputs == null) continue;

            ItemStack input = recipe.mOutputs[0];

            ObjectArrayList<ItemStack> outputs = new ObjectArrayList<>(recipe.mInputs);
            if (recipe.mFluidInputs != null) {
                for (FluidStack fluid : recipe.mFluidInputs) {
                    addFluidPacketOutput(outputs, fluid, "Hard-Override Recipe");
                }
            }

            outputs.removeIf(stack -> stack == null || stack.stackSize <= 0);
            if (outputs.isEmpty()) continue;

            if (shouldBuildShimmerFakeRecipe()) {
                RecipeBuilder builder = RecipeBuilder.builder()
                    .itemInputs(input)
                    .itemOutputs(outputs.toArray(new ItemStack[0]))
                    .duration(100)
                    .eut(0)
                    .fake()
                    .setNEIDesc("Generated from Hard-Override Recipe");
                addShimmerFakeRecipe(builder);
            }
            ShimmerRecipes.registerConversion(input, outputs);
        }
        GTNLRecipeMaps.HardOverrideRecipes.getBackend()
            .clearRecipes();
    }

    public static void loadAssemblerRecipesToDisassembler() {
        Object2ObjectMap<GTItemStack, ObjectArrayList<GTRecipe>> assemblerRecipes = new Object2ObjectArrayMap<>();

        for (GTRecipe recipe : RecipeMaps.assemblerRecipes.getAllRecipes()) {
            if (recipe.mOutputs == null || recipe.mInputs == null || !shouldDisassemble(recipe.mOutputs)) continue;
            ItemStack input = recipe.mOutputs[0];
            if (ShimmerRecipes.isInConversions(input)) continue;

            GTItemStack key = new GTItemStack(input);
            assemblerRecipes.computeIfAbsent(key, k -> new ObjectArrayList<>())
                .add(recipe);
        }

        int totalCount = assemblerRecipes.size();
        ScienceNotLeisure.LOG.info("Loading reversed assembler recipes, total: {}", totalCount);

        for (Object2ObjectMap.Entry<GTItemStack, ObjectArrayList<GTRecipe>> entry : assemblerRecipes
            .object2ObjectEntrySet()) {
            ObjectArrayList<GTRecipe> recipes = entry.getValue();

            try {
                ObjectArrayList<GTRecipe> reversedRecipes = new ObjectArrayList<>();
                for (GTRecipe recipe : recipes) {
                    reversedRecipes.add(getReversedRecipe(recipe));
                }

                if (reversedRecipes.isEmpty()) continue;

                GTRecipe reversedFirst = reversedRecipes.get(0);
                ItemStack revInput = reversedFirst.mInputs[0];

                ObjectArrayList<ItemStack> transformedOutputs = handleRecipeTransformation(
                    reversedFirst.mOutputs,
                    reversedRecipes.stream()
                        .map(r -> r.mOutputs)
                        .collect(Collectors.toCollection(ObjectOpenHashSet::new))).stream()
                            .filter(s -> GTUtility.isStackValid(s) && s.stackSize > 0)
                            .collect(Collectors.toCollection(ObjectArrayList::new));

                ObjectArrayList<ItemStack> outputs = new ObjectArrayList<>();
                if (reversedFirst.mFluidInputs != null) {
                    for (FluidStack fluid : reversedFirst.mFluidInputs) {
                        addFluidPacketOutput(outputs, fluid, "Assembler Recipe");
                    }
                }
                outputs.addAll(transformedOutputs);
                if (outputs.isEmpty()) continue;

                if (shouldBuildShimmerFakeRecipe()) {
                    RecipeBuilder builder = RecipeBuilder.builder()
                        .itemInputs(revInput)
                        .itemOutputs(outputs.toArray(new ItemStack[0]))
                        .duration(100)
                        .eut(0)
                        .fake()
                        .setNEIDesc("Generated from Assembler Recipe");
                    applyDebugAssembler(builder, recipes, false);
                    addShimmerFakeRecipe(builder);
                }
                ShimmerRecipes.registerConversion(revInput, outputs);

            } catch (Exception e) {
                ScienceNotLeisure.LOG.warn("Failed to process assembler -> disassembler recipe.");
                GTRecipe first = recipes.isEmpty() ? null : recipes.get(0);
                if (first != null) {
                    ScienceNotLeisure.LOG.warn("mInputs: {}", Arrays.toString(first.mInputs));
                    ScienceNotLeisure.LOG.warn("mOutputs: {}", Arrays.toString(first.mOutputs));
                }
                ScienceNotLeisure.LOG.warn("Exception: ", e);

                if (e instanceof ArrayIndexOutOfBoundsException) throw e;
            }
        }

        for (GTRecipe recipe : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (recipe == null || recipe.mOutputs == null) continue;

            ItemStack input = recipe.mOutputs[0];
            if (ShimmerRecipes.isInConversions(input)) continue;

            ObjectArrayList<ItemStack> outputs = new ObjectArrayList<>();
            for (ItemStack stack : recipe.mInputs) {
                if (stack != null) {
                    outputs.add(stack);
                }
            }
            if (recipe.mFluidInputs != null) {
                for (FluidStack fluid : recipe.mFluidInputs) {
                    addFluidPacketOutput(outputs, fluid, "Assembly Line");
                }
            }
            if (outputs.isEmpty()) continue;

            if (shouldBuildShimmerFakeRecipe()) {
                RecipeBuilder builder = RecipeBuilder.builder()
                    .itemInputs(input)
                    .itemOutputs(outputs.toArray(new ItemStack[0]))
                    .duration(100)
                    .eut(0)
                    .fake()
                    .setNEIDesc("Generated from Assembly Line");
                addShimmerFakeRecipe(builder);
            }
            ShimmerRecipes.registerConversion(input, outputs);
        }

        for (GTRecipe recipe : IGRecipeMaps.spaceAssemblerRecipes.getAllRecipes()) {
            if (recipe == null || recipe.mOutputs == null) continue;

            ItemStack input = recipe.mOutputs[0];
            if (ShimmerRecipes.isInConversions(input)) continue;

            ObjectArrayList<ItemStack> outputs = new ObjectArrayList<>(recipe.mInputs);
            if (recipe.mFluidInputs != null) {
                for (FluidStack fluid : recipe.mFluidInputs) {
                    addFluidPacketOutput(outputs, fluid, "Space Assembler");
                }
            }
            outputs.removeIf(stack -> stack == null || stack.stackSize <= 0);
            if (outputs.isEmpty()) continue;

            if (shouldBuildShimmerFakeRecipe()) {
                RecipeBuilder builder = RecipeBuilder.builder()
                    .itemInputs(input)
                    .itemOutputs(outputs.toArray(new ItemStack[0]))
                    .duration(100)
                    .eut(0)
                    .fake()
                    .setNEIDesc("Generated from Space Assembler");
                addShimmerFakeRecipe(builder);
            }
            ShimmerRecipes.registerConversion(input, outputs);
        }
    }

    public static void handleGTCraftingRecipe(ReversedRecipeRegistry.GTCraftingRecipe recipe) {
        GTRecipe revRecipe = recipe.toReversedSafe();
        if (revRecipe == null) return;

        ItemStack[] inputs = revRecipe.mInputs;
        if (inputs == null || inputs.length == 0) return;

        if (!shouldDisassemble(inputs)) return;

        ItemStack input = inputs[0];
        if (ShimmerRecipes.isInConversions(input)) return;

        try {
            ObjectList<ItemStack> outputs = handleRecipeTransformation(revRecipe.mOutputs, null);
            if (shouldBuildShimmerFakeRecipe()) {
                RecipeBuilder builder = RecipeBuilder.builder()
                    .itemInputs(input)
                    .itemOutputs(outputs.toArray(new ItemStack[0]))
                    .duration(100)
                    .eut(0)
                    .fake()
                    .setNEIDesc("Generated from GT Crafting Recipe");
                applyDebugCrafting(builder, recipe);
                addShimmerFakeRecipe(builder);
            }
            if (input != null) {
                ShimmerRecipes.registerConversion(input, outputs);
            }
        } catch (Exception e) {
            ScienceNotLeisure.LOG.warn("Failed to process reversed crafting table recipe to disassembler recipe.");
            ScienceNotLeisure.LOG.warn("mInputs: {}", Arrays.toString(revRecipe.mInputs));
            ScienceNotLeisure.LOG.warn("mOutputs: {}", Arrays.toString(revRecipe.mOutputs));
            ScienceNotLeisure.LOG.warn("Exception", e);
        }
    }

    public static RecipeBuilder applyDebugAssembler(RecipeBuilder builder, ObjectList<GTRecipe> original,
        boolean isHardOverride) {
        if (debugRecipeToRecipe == null) return builder;

        Optional<GTRecipe> generated = builder.build();
        if (!generated.isPresent()) return builder;

        GTRecipe result = generated.get();
        int index = debugIndexBumper.getAndIncrement();

        AssemblerReversed info = new AssemblerReversed(original, index, isHardOverride ? "Hard-override" : "Generated");

        debugRecipeToRecipe.computeIfAbsent(result, k -> new ObjectArrayList<>())
            .add(info);
        debugIndexToRecipe.put(index, info);

        builder.setNEIDesc("Debug Index: " + index);
        return builder;
    }

    private static void addShimmerFakeRecipe(RecipeBuilder builder) {
        if (MainConfig.recipe.enableShimmerFakeRecipeInNEI) {
            builder.addTo(GTNLRecipeMaps.ShimmerRecipes);
        }
    }

    private static boolean shouldBuildShimmerFakeRecipe() {
        return MainConfig.recipe.enableShimmerFakeRecipeInNEI || debugRecipeToRecipe != null;
    }

    private static void addFluidPacketOutput(ObjectArrayList<ItemStack> outputs, FluidStack fluid, String source) {
        ItemStack packet = createFluidPacket(fluid, source);
        if (GTUtility.isStackValid(packet) && packet.stackSize > 0) {
            outputs.add(packet);
        }
    }

    private static ItemStack createFluidPacket(FluidStack fluid, String source) {
        if (fluid == null) {
            ScienceNotLeisure.LOG.warn("Skipping null fluid while generating Shimmer recipe from {}.", source);
            return null;
        }
        if (fluid.amount <= 0) {
            ScienceNotLeisure.LOG.warn(
                "Skipping fluid packet with non-positive amount while generating Shimmer recipe from {}: {}",
                source,
                describeFluidStack(fluid));
            return null;
        }
        if (fluid.getFluid() == null) {
            ScienceNotLeisure.LOG.warn(
                "Skipping fluid packet with missing fluid type while generating Shimmer recipe from {}: {}",
                source,
                describeFluidStack(fluid));
            return null;
        }
        String fluidName = fluid.getFluid()
            .getName();
        if (fluidName == null || fluidName.isEmpty()) {
            ScienceNotLeisure.LOG.warn(
                "Skipping fluid packet with empty fluid name while generating Shimmer recipe from {}: {}",
                source,
                describeFluidStack(fluid));
            return null;
        }
        try {
            return ItemFluidPacket.newStack(fluid);
        } catch (IllegalArgumentException e) {
            ScienceNotLeisure.LOG.warn(
                "Skipping invalid fluid packet while generating Shimmer recipe from {}: {}",
                source,
                describeFluidStack(fluid),
                e);
            return null;
        }
    }

    private static String describeFluidStack(FluidStack fluid) {
        if (fluid == null) {
            return "null";
        }
        String fluidName = fluid.getFluid() == null ? "null"
            : fluid.getFluid()
                .getName();
        return "FluidStack{name=" + fluidName + ", amount=" + fluid.amount + "}";
    }

    public static void applyDebugCrafting(RecipeBuilder builder, ReversedRecipeRegistry.GTCraftingRecipe original) {
        if (debugRecipeToRecipe == null) return;

        Optional<GTRecipe> generated = builder.build();
        if (!generated.isPresent()) return;

        GTRecipe result = generated.get();
        int index = debugIndexBumper.getAndIncrement();

        CraftingTableReversed info = new CraftingTableReversed(original, index);

        debugRecipeToRecipe.computeIfAbsent(result, k -> new ObjectArrayList<>())
            .add(info);
        debugIndexToRecipe.put(index, info);

        builder.setNEIDesc("Debug Index: " + index);
    }

}
