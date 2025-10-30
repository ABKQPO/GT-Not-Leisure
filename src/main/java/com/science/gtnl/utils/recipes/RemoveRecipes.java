package com.science.gtnl.utils.recipes;

import static com.dreammaster.scripts.IScriptLoader.wildcard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import gregtech.api.enums.MaterialsGTNH;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class RemoveRecipes {

    public static void removeRecipes() {
        bufferMap = new HashMap<>();
        final long timeStart = System.currentTimeMillis();

        RecipeMapBackend autoClaveRecipe = RecipeMaps.autoclaveRecipes.getBackend();
        RecipeMapBackend circuitAssemblerRecipe = RecipeMaps.circuitAssemblerRecipes.getBackend();
        RecipeMapBackend formingPressRecipe = RecipeMaps.formingPressRecipes.getBackend();
        RecipeMapBackend vacuumFurnaceRecipe = GTPPRecipeMaps.vacuumFurnaceRecipes.getBackend();
        RecipeMapBackend blastFurnaceRecipe = RecipeMaps.blastFurnaceRecipes.getBackend();
        RecipeMapBackend alloyBlastSmelterRecipe = GTPPRecipeMaps.alloyBlastSmelterRecipes.getBackend();
        RecipeMapBackend vacuumFreezerRecipe = RecipeMaps.vacuumFreezerRecipes.getBackend();
        RecipeMapBackend cryogenicFreezerRecipe = GTPPRecipeMaps.advancedFreezerRecipes.getBackend();
        RecipeMapBackend chemicalBathRecipe = RecipeMaps.chemicalBathRecipes.getBackend();
        RecipeMapBackend chemicalPlantRecipe = GTPPRecipeMaps.chemicalPlantRecipes.getBackend();
        RecipeMapBackend dissolutionTankRecipe = LanthanidesRecipeMaps.dissolutionTankRecipes.getBackend();
        Map<String, Integer> removedRecipeCounts = new HashMap<>();

        // 合金冶炼炉
        List<GTRecipe> recipesToRemoveFromAlloyBlastSmelter = new ArrayList<>();
        for (GTRecipe recipe : alloyBlastSmelterRecipe.getAllRecipes()) {
            for (ItemStack input : recipe.mInputs) {
                if (input != null) {
                    // 熔融铕
                    if (input.isItemEqual(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1))) {
                        recipesToRemoveFromAlloyBlastSmelter.add(recipe);
                        break;
                    }
                }
            }
        }
        alloyBlastSmelterRecipe.removeRecipes(recipesToRemoveFromAlloyBlastSmelter);

        // 高压釜
        List<GTRecipe> recipesToRemoveFromAutoClave = new ArrayList<>();
        for (GTRecipe recipe : autoClaveRecipe.getAllRecipes()) {
            for (ItemStack output : recipe.mOutputs) {
                if (output != null) {
                    // 活性生物晶圆
                    if (output.isItemEqual(ItemList.Circuit_Wafer_Bioware.get(1))) {
                        recipesToRemoveFromAutoClave.add(recipe);
                        break;
                    }
                    if (output.isItemEqual(ItemList.Netherite_Scrap_Seed.get(1))) {
                        recipesToRemoveFromAutoClave.add(recipe);
                        break;
                    }
                    if (output.isItemEqual(GTOreDictUnificator.get(OrePrefixes.shard, MaterialsGTNH.Prismarine, 1))) {
                        recipesToRemoveFromAutoClave.add(recipe);
                        break;
                    }
                }
            }
        }
        autoClaveRecipe.removeRecipes(recipesToRemoveFromAutoClave);

        // 冲压机床
        List<GTRecipe> recipesToRemoveFromFormingPress = new ArrayList<>();
        for (GTRecipe recipe : formingPressRecipe.getAllRecipes()) {
            for (ItemStack output : recipe.mOutputs) {
                if (output != null) {
                    // 光学CPU密封外壳
                    if (output.isItemEqual(ItemList.Optical_Cpu_Containment_Housing.get(1))) {
                        recipesToRemoveFromFormingPress.add(recipe);
                        break;
                    }
                }
            }
        }
        formingPressRecipe.removeRecipes(recipesToRemoveFromFormingPress);

        // 工业高炉
        List<GTRecipe> recipesToRemoveFromBlastFurnace = new ArrayList<>();
        for (GTRecipe recipe : blastFurnaceRecipe.getAllRecipes()) {
            for (ItemStack output : recipe.mOutputs) {
                if (output != null) {
                    // 铕锭
                    if (output.isItemEqual(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Europium, 1))) {
                        recipesToRemoveFromBlastFurnace.add(recipe);
                        break;
                    }
                }
            }
        }
        blastFurnaceRecipe.removeRecipes(recipesToRemoveFromBlastFurnace);

        // 真空干燥炉
        List<GTRecipe> recipesToRemoveFromVacuumFurnace = new ArrayList<>();
        for (GTRecipe recipe : vacuumFurnaceRecipe.getAllRecipes()) {
            for (ItemStack output : recipe.mOutputs) {
                if (output != null) {
                    // 铂泡沫
                    if (output.isItemEqual(WerkstoffLoader.PTMetallicPowder.get(OrePrefixes.dust, 1))) {
                        recipesToRemoveFromVacuumFurnace.add(recipe);
                        break;
                    }
                    // 独居石泡沫
                    if (output.isItemEqual(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1))) {
                        recipesToRemoveFromVacuumFurnace.add(recipe);
                        break;
                    }
                    // 镍黄铁泡沫
                    if (output.isItemEqual(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Promethium, 1))) {
                        recipesToRemoveFromVacuumFurnace.add(recipe);
                        break;
                    }
                }
            }
            for (FluidStack output : recipe.mFluidOutputs) {
                if (output != null) {
                    if (output.isFluidEqual(Materials.PrismaticGas.getFluid(1))) {
                        recipesToRemoveFromVacuumFurnace.add(recipe);
                        break;
                    }
                }
            }
        }
        vacuumFurnaceRecipe.removeRecipes(recipesToRemoveFromVacuumFurnace);

        // 埃克森美孚化工厂
        List<GTRecipe> recipesToRemoveFromChemicalPlant = new ArrayList<>();
        for (GTRecipe recipe : chemicalPlantRecipe.getAllRecipes()) {
            for (ItemStack output : recipe.mOutputs) {
                if (output != null) {
                    // 海晶石沉淀
                    if (output.isItemEqual(ItemList.Prismarine_Precipitate.get(1))) {
                        recipesToRemoveFromChemicalPlant.add(recipe);
                        break;
                    }
                }
            }
        }
        chemicalPlantRecipe.removeRecipes(recipesToRemoveFromChemicalPlant);

        // 真空冷冻机
        List<GTRecipe> recipesToRemoveFromVacuumFreezer = new ArrayList<>();
        for (GTRecipe recipe : vacuumFreezerRecipe.getAllRecipes()) {
            for (FluidStack output : recipe.mFluidOutputs) {
                if (output != null) {
                    // 半流质下界气体
                    if (output.isFluidEqual(Materials.NetherSemiFluid.getFluid(1))) {
                        recipesToRemoveFromVacuumFreezer.add(recipe);
                        break;
                    }
                    if (output.isFluidEqual(Materials.PrismaticAcid.getFluid(1))) {
                        recipesToRemoveFromVacuumFreezer.add(recipe);
                        break;
                    }
                }
            }
        }
        vacuumFreezerRecipe.removeRecipes(recipesToRemoveFromVacuumFreezer);

        // 凛冰冷冻机
        List<GTRecipe> recipesToRemoveFromCryogenicFreezer = new ArrayList<>();
        for (GTRecipe recipe : cryogenicFreezerRecipe.getAllRecipes()) {
            for (FluidStack output : recipe.mFluidOutputs) {
                if (output != null) {
                    // 半流质下界气体
                    if (output.isFluidEqual(Materials.NetherSemiFluid.getFluid(1))) {
                        recipesToRemoveFromCryogenicFreezer.add(recipe);
                        break;
                    }
                    if (output.isFluidEqual(Materials.PrismaticAcid.getFluid(1))) {
                        recipesToRemoveFromCryogenicFreezer.add(recipe);
                        break;
                    }
                }
            }
        }
        cryogenicFreezerRecipe.removeRecipes(recipesToRemoveFromCryogenicFreezer);

        // 化学浸洗机
        List<GTRecipe> recipesToRemoveFromChemicalBath = new ArrayList<>();
        for (GTRecipe recipe : chemicalBathRecipe.getAllRecipes()) {
            for (ItemStack output : recipe.mOutputs) {
                if (output != null) {
                    // 易碎的下界合金碎片及下界合金碎片种子
                    if (output.isItemEqual(ItemList.Netherite_Scrap_Seed.get(1))) {
                        recipesToRemoveFromChemicalBath.add(recipe);
                        break;
                    }
                }
            }
            for (FluidStack output : recipe.mFluidOutputs) {
                if (output != null) {
                    if (output.isFluidEqual(Materials.PrismarineContaminatedHydrogenPeroxide.getFluid(1))) {
                        recipesToRemoveFromChemicalBath.add(recipe);
                        break;
                    }
                }
            }
        }
        chemicalBathRecipe.removeRecipes(recipesToRemoveFromChemicalBath);

        List<GTRecipe> recipesToRemoveFromDissolutionTank = new ArrayList<>();
        for (GTRecipe recipe :  dissolutionTankRecipe.getAllRecipes()) {
            for (FluidStack output : recipe.mFluidOutputs) {
                if (output != null) {
                    if (output.isFluidEqual(Materials.PrismarineSolution.getFluid(1))) {
                        recipesToRemoveFromDissolutionTank.add(recipe);
                        break;
                    }
                }
            }
        }
        dissolutionTankRecipe.removeRecipes(recipesToRemoveFromDissolutionTank);

        // 电路装配线
        List<GTRecipe> recipesToRemoveFromCircuitAssembler = new ArrayList<>();
        List<ItemStack> targetOutputs = Arrays.asList(
            ItemList.Circuit_Crystalprocessor.get(1), // 晶体处理器
            ItemList.Circuit_Crystalcomputer.get(1), // 晶体处理器集群
            ItemList.Circuit_Ultimatecrystalcomputer.get(1), // 晶体处理器电脑
            ItemList.Circuit_Crystalmainframe.get(1), // 晶体处理器主机
            ItemList.Circuit_Bioprocessor.get(1), // 生物处理器
            ItemList.Circuit_Neuroprocessor.get(1), // 湿件处理器
            ItemList.Circuit_Wetwarecomputer.get(1), // 湿件处理器集群
            ItemList.Circuit_Wetwaresupercomputer.get(1) // 湿件处理器电脑
        );

        for (GTRecipe recipe : circuitAssemblerRecipe.getAllRecipes()) {
            for (ItemStack output : recipe.mOutputs) {
                if (output == null) continue;
                for (ItemStack target : targetOutputs) {
                    if (output.isItemEqual(target)) {
                        recipesToRemoveFromCircuitAssembler.add(recipe);
                        break;
                    }
                }
            }
        }

        circuitAssemblerRecipe.removeRecipes(recipesToRemoveFromCircuitAssembler);

        removeRecipeByOutputDelayed(ItemList.Machine_EV_LightningRod.get(1));
        removeRecipeByOutputDelayed(ItemList.Machine_IV_LightningRod.get(1));

        if (MainConfig.enableDebugMode) {
            removedRecipeCounts.put("Autoclave", recipesToRemoveFromAutoClave.size());
            removedRecipeCounts.put("Circuit Assembler", recipesToRemoveFromCircuitAssembler.size());
            removedRecipeCounts.put("Forming Press", recipesToRemoveFromFormingPress.size());
            removedRecipeCounts.put("Vacuum Furnace", recipesToRemoveFromVacuumFurnace.size());
            removedRecipeCounts.put("Blast Furnace", recipesToRemoveFromBlastFurnace.size());
            removedRecipeCounts.put("Vacuum Freezer", recipesToRemoveFromVacuumFreezer.size());
            removedRecipeCounts.put("Chemical Plant", recipesToRemoveFromChemicalPlant.size());

            StringBuilder logMessage = new StringBuilder("GTNL: Removed recipes from the following recipe pools:");
            for (Map.Entry<String, Integer> entry : removedRecipeCounts.entrySet()) {
                logMessage.append("\n- ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" recipes");
            }
            System.out.println(logMessage);
        }

        flushBuffer();
        bufferMap = null;
        final long timeToLoad = System.currentTimeMillis() - timeStart;
        ScienceNotLeisure.LOG.info("Recipes removal took {} ms.", timeToLoad);
    }

    public static void removeRecipeByOutputDelayed(Object aOutput) {
        if (aOutput == null) return;
        addToBuffer(getItemsHashed(aOutput, false), r -> true);
    }

    public static void removeRecipeShapelessDelayed(Object aOutput, Object... aRecipe) {
        if (aOutput == null) return;
        ArrayList<Object> aRecipeList = new ArrayList<>(Arrays.asList(aRecipe));
        addToBuffer(getItemsHashed(aOutput, false), r -> {
            if (!(r instanceof ShapelessOreRecipe) && !(r instanceof ShapelessRecipes)) return false;
            if (aRecipeList.isEmpty()) return true;
            @SuppressWarnings("unchecked")
            ArrayList<Object> recipe = (ArrayList<Object>) aRecipeList.clone();
            List<?> rInputs = (r instanceof ShapelessOreRecipe ? ((ShapelessOreRecipe) r).getInput()
                : ((ShapelessRecipes) r).recipeItems);
            for (Object rInput : rInputs) {
                HashSet<GTUtility.ItemId> rInputHashed;
                HashSet<GTUtility.ItemId> rInputHashedNoWildcard;
                try {
                    rInputHashed = getItemsHashed(rInput, true);
                    rInputHashedNoWildcard = getItemsHashed(rInput, false);
                } catch (Exception ex) {
                    return false;
                }
                boolean found = false;
                for (Iterator<Object> iterator = recipe.iterator(); iterator.hasNext();) {
                    Object o = iterator.next();
                    for (GTUtility.ItemId id : getItemsHashed(o, false)) {
                        if (rInputHashed.contains(id)) {
                            found = true;
                            iterator.remove();
                            break;
                        }
                    }
                    if (!found) {
                        for (GTUtility.ItemId id : getItemsHashed(o, true)) {
                            if (rInputHashedNoWildcard.contains(id)) {
                                found = true;
                                iterator.remove();
                                break;
                            }
                        }
                    }
                    if (found) break;
                }
                if (!found) return false;
            }
            return recipe.isEmpty();
        });
    }

    public static HashMap<GTUtility.ItemId, List<Function<IRecipe, Boolean>>> bufferMap;

    public static void addToBuffer(HashSet<GTUtility.ItemId> outputs, Function<IRecipe, Boolean> whenToRemove) {
        for (GTUtility.ItemId output : outputs) {
            bufferMap.computeIfAbsent(output, o -> new ArrayList<>())
                .add(whenToRemove);
        }
    }

    public static HashSet<GTUtility.ItemId> getItemsHashed(Object item, boolean includeWildcardVariants) {
        HashSet<GTUtility.ItemId> hashedItems = new HashSet<>();
        if (item instanceof ItemStack) {
            ItemStack iCopy = ((ItemStack) item).copy();
            iCopy.stackTagCompound = null;
            hashedItems.add(GTUtility.ItemId.createNoCopy(iCopy));
            if (includeWildcardVariants) {
                iCopy = iCopy.copy();
                Items.feather.setDamage(iCopy, wildcard);
                hashedItems.add(GTUtility.ItemId.createNoCopy(iCopy));
            }
        } else if (item instanceof String) {
            for (ItemStack stack : OreDictionary.getOres((String) item)) {
                hashedItems.add(GTUtility.ItemId.createNoCopy(stack));
                if (includeWildcardVariants) {
                    stack = stack.copy();
                    Items.feather.setDamage(stack, wildcard);
                    hashedItems.add(GTUtility.ItemId.createNoCopy(stack));
                }
            }
        } else if (item instanceof ArrayList) {
            // noinspection unchecked
            for (ItemStack stack : (ArrayList<ItemStack>) item) {
                ItemStack iCopy = stack.copy();
                iCopy.stackTagCompound = null;
                hashedItems.add(GTUtility.ItemId.createNoCopy(iCopy));
                if (includeWildcardVariants) {
                    iCopy = iCopy.copy();
                    Items.feather.setDamage(iCopy, wildcard);
                    hashedItems.add(GTUtility.ItemId.createNoCopy(iCopy));
                }
            }
        } else throw new IllegalArgumentException("Invalid input " + item.toString());
        return hashedItems;
    }

    public static void flushBuffer() {
        final ArrayList<IRecipe> list = (ArrayList<IRecipe>) CraftingManager.getInstance()
            .getRecipeList();
        int i = list.size();
        list.removeIf(r -> {
            ItemStack rCopy = r.getRecipeOutput();
            if (rCopy == null) {
                return false;
            }
            if (rCopy.getItem() == null) {
                ScienceNotLeisure.LOG.warn("Someone is adding recipes with null items!");
                return true;
            }
            if (rCopy.stackTagCompound != null) {
                rCopy = rCopy.copy();
                rCopy.stackTagCompound = null;
            }
            GTUtility.ItemId key = GTUtility.ItemId.createNoCopy(rCopy);
            rCopy = rCopy.copy();
            Items.feather.setDamage(rCopy, wildcard);
            GTUtility.ItemId keyWildcard = GTUtility.ItemId.createNoCopy(rCopy);
            List<Function<IRecipe, Boolean>> listWhenToRemove = bufferMap.get(key);
            if (listWhenToRemove == null) listWhenToRemove = bufferMap.get(keyWildcard);
            if (listWhenToRemove == null) return false;
            for (Function<IRecipe, Boolean> whenToRemove : listWhenToRemove) {
                if (whenToRemove.apply(r)) return true;
            }
            return false;
        });
        ScienceNotLeisure.LOG.info("Removed {} recipes!", i - list.size());
    }

}
