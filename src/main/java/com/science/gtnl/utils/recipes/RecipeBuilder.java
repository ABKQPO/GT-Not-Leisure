package com.science.gtnl.utils.recipes;

import static gregtech.api.util.GTRecipeMapUtil.SPECIAL_VALUE_ALIASES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.mixins.early.gregtech.AccessorGTRecipe;
import com.science.gtnl.mixins.early.gregtech.AccessorGTRecipeBuilder;
import com.science.gtnl.mixins.early.gregtech.AccessorGTRecipeWithAlt;

import com.LazyFlesh.variablehorizons.variants.VariantNames;
import gregtech.GTLoggers;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.objects.OreDictItemStack;
import gregtech.api.objects.SubstituteFluidStack;
import gregtech.api.recipe.RecipeCategory;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.recipe.metadata.RecipeMetadataStorage;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.extensions.ArrayExt;

public class RecipeBuilder {

    // debug mode expose problems. panic mode help you check nothing is wrong-ish without you actively monitoring
    public static final boolean DEBUG_MODE_NULL;
    // Any stable release should be tested at least once with this: -Dgt.recipebuilder.panic.null=true
    public static boolean PANIC_MODE_NULL;
    public static final boolean DEBUG_MODE_INVALID;
    public static final boolean DEBUG_MODE_FULL_ENERGY;
    // Any stable release should be tested at least once with this: -Dgt.recipebuilder.panic.invalid=true
    public static final boolean PANIC_MODE_INVALID;
    public static final boolean DEBUG_MODE_COLLISION;

    // Any stable release should be tested at least once with this: -Dgt.recipebuilder.panic.collision=true
    public static final boolean PANIC_MODE_COLLISION;

    // This should only be enabled in non stable instances only with -Dgt.recipebuilder.recipe_collision_check=true
    public static final boolean ENABLE_COLLISION_CHECK;

    static {
        boolean debugAll;
        if (System.getProperties()
            .containsKey("gt.recipebuilder.debug")) {
            debugAll = Boolean.getBoolean("gt.recipebuilder.debug");
        } else {
            // turn on debug by default in dev mode
            debugAll = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        }
        DEBUG_MODE_NULL = debugAll || Boolean.getBoolean("gt.recipebuilder.debug.null");
        DEBUG_MODE_INVALID = debugAll || Boolean.getBoolean("gt.recipebuilder.debug.invalid");
        DEBUG_MODE_COLLISION = debugAll || Boolean.getBoolean("gt.recipebuilder.debug.collision");
        DEBUG_MODE_FULL_ENERGY = debugAll || Boolean.getBoolean("gt.recipebuilder.debug.fullenergy");

        final boolean panicAll = Boolean.getBoolean("gt.recipebuilder.panic");
        PANIC_MODE_NULL = panicAll || Boolean.getBoolean("gt.recipebuilder.panic.null");
        PANIC_MODE_INVALID = panicAll || Boolean.getBoolean("gt.recipebuilder.panic.invalid");
        PANIC_MODE_COLLISION = panicAll || Boolean.getBoolean("gt.recipebuilder.panic.collision");
        ENABLE_COLLISION_CHECK = Boolean.getBoolean("gt.recipebuilder.recipe_collision_check");
    }

    public static RecipeBuilder builder() {
        return new RecipeBuilder();
    }

    public ItemStack[] inputsBasic = GTValues.emptyItemStackArray;
    public Object[] inputsOreDict;
    public ItemStack[] outputs = GTValues.emptyItemStackArray;
    public ItemStack[][] alts;
    public int[] altOreIds;
    public FluidStack[] fluidInputs = GTValues.emptyFluidStackArray;
    public FluidStack[][] altFluidInputs;
    public FluidStack[] fluidOutputs = GTValues.emptyFluidStackArray;
    public int[] inputChances, fluidInputChances, outputChances, fluidOutputChances;
    public Object special;
    public int duration = -1;
    public int eut = -1;
    public int specialValue;
    public boolean enabled = true;
    public boolean hidden = false;
    public boolean fakeRecipe = false;
    public boolean mCanBeBuffered = true;
    public boolean mNeedsEmptyOutput = false;
    public boolean nbtSensitive = false;
    public String[] neiDesc;
    public RecipeCategory recipeCategory;
    @Nullable
    public IRecipeMetadataStorage metadataStorage;
    public boolean checkForCollision = true;
    @Nullable
    public ItemStack pendingIntegratedCircuit;
    /**
     * If recipe addition should be skipped.
     */
    public boolean skip = false;
    public boolean valid = true;
    private boolean preserveNullItemInputs;
    private int inputItemCount;
    private int outputItemCount;
    private int inputFluidCount;
    private int outputFluidCount;

    public RecipeBuilder() {}

    /**
     * circuit method helper. Adds appropriate integrated circuit into item inputs.
     */
    private ItemStack[] applyPendingCircuit(ItemStack[] baseInputs) {
        if (pendingIntegratedCircuit == null) {
            return baseInputs;
        }
        // Append pending circuit & clear it
        ItemStack[] newInputs = new ItemStack[baseInputs.length + 1];
        System.arraycopy(baseInputs, 0, newInputs, 0, baseInputs.length);
        newInputs[baseInputs.length] = pendingIntegratedCircuit;
        pendingIntegratedCircuit = null;
        return newInputs;
    }

    /**
     * Specialized item input method for adding integrated circuits into a recipe.
     */
    public RecipeBuilder circuit(int integratedCircuitNumber) {
        ItemStack circuit = GTUtility.getIntegratedCircuit(integratedCircuitNumber);

        if (inputsBasic != null && inputsBasic.length > 0) {
            // Input condition
            pendingIntegratedCircuit = circuit;
            inputsBasic = applyPendingCircuit(inputsBasic);
        } else {
            // No inputs condition
            inputsBasic = new ItemStack[] { circuit };
            pendingIntegratedCircuit = circuit;
        }

        return this;
    }

    public RecipeBuilder itemInputsUnified(ItemStack... inputs) {
        if (skip) return this;
        if (debugNull() && containsNull(inputs)) handleNullRecipeComponents("itemInputUnified");
        inputsBasic = applyPendingCircuit(ArrayExt.removeTrailingNulls(inputs));
        preserveNullItemInputs = false;
        inputItemCount = countValidItemStacks(inputsBasic);
        inputsOreDict = null;
        alts = null;
        altOreIds = null;
        return this;
    }

    public RecipeBuilder itemInputsUnified(ItemStack[] inputs, int[] chances) {
        if (skip) return this;
        this.inputChances = chances;
        return itemInputsUnified(inputs);
    }

    public RecipeBuilder itemInputs(List<ItemStack> inputItems) {
        return itemInputs(inputItems.toArray(new ItemStack[0]));
    }

    public RecipeBuilder itemInputs(ItemStack... inputItems) {
        if (skip) return this;
        if (inputItems != null && inputItems.length > 0) {
            this.inputsBasic = applyPendingCircuit(inputItems);
            inputItemCount = countValidItemStacks(this.inputsBasic);
        } else {
            this.inputsBasic = GTValues.emptyItemStackArray;
            inputItemCount = 0;
        }
        preserveNullItemInputs = false;
        inputsOreDict = null;
        alts = null;
        altOreIds = null;
        return this;
    }

    public RecipeBuilder itemInputs(ItemStack[] inputs, int[] chances) {
        if (skip) return this;
        this.inputChances = chances;
        return itemInputs(inputs);
    }

    public RecipeBuilder itemInputsUnsafe(ItemStack... inputItems) {
        if (skip) return this;
        if (debugNull() && containsNull(inputItems)) handleNullRecipeComponents("itemInputsUnsafe");
        this.inputsBasic = applyPendingCircuit(fix(inputItems, true));
        this.inputItemCount = countValidItemStacks(this.inputsBasic);
        this.preserveNullItemInputs = false;
        this.inputsOreDict = null;
        this.alts = null;
        this.altOreIds = null;
        return this;
    }

    public RecipeBuilder itemInputsUnsafe(ItemStack[] inputs, int[] chances) {
        if (skip) return this;
        this.inputChances = chances;
        return itemInputsUnsafe(inputs);
    }

    public RecipeBuilder itemInputs(Object... inputs) {
        if (skip) return this;

        this.inputsOreDict = inputs;
        int len = inputs.length;
        this.alts = new ItemStack[len][];
        this.altOreIds = new int[len];
        Arrays.fill(altOreIds, -1);

        for (int i = 0; i < len; i++) {
            Object in = inputs[i];
            ItemStack[] result;

            if (in instanceof ItemStack stack) {
                result = new ItemStack[] { stack };

            } else if (in instanceof ItemStack[]arr) {
                result = arr.clone();

            } else if (in instanceof OreDictItemStack oreDictItemStack) {
                altOreIds[i] = OreDictionary.getOreID(oreDictItemStack.mOreName);
                ArrayList<ItemStack> ores = GTOreDictUnificator.getOres(oreDictItemStack.mOreName);
                if (ores.isEmpty()) {
                    GTLoggers.GT_FML_LOGGER.error(
                        "Warning: OreDict entry \"" + oreDictItemStack.mOreName
                            + "\" is empty; recipe will be skipped.");
                    result = GTValues.emptyItemStackArray;
                } else {
                    ArrayList<ItemStack> list = new ArrayList<>(ores.size());
                    for (ItemStack ore : ores) {
                        ItemStack copy = GTUtility.copyAmount(oreDictItemStack.mAmount, ore);
                        if (GTUtility.isStackValid(copy)) {
                            list.add(copy);
                        }
                    }
                    result = list.toArray(new ItemStack[0]);
                }

            } else if (in instanceof Object[]arr && arr.length == 2) {
                altOreIds[i] = OreDictionary.getOreID(arr[0].toString());
                ArrayList<ItemStack> ores = GTOreDictUnificator.getOres(arr[0]);
                if (ores.isEmpty()) {
                    GTLoggers.GT_FML_LOGGER
                        .error("Warning: OreDict entry \"{}\" is empty; recipe will be skipped.", arr[0]);
                    result = GTValues.emptyItemStackArray;
                } else {
                    int size = ((Number) arr[1]).intValue();
                    ArrayList<ItemStack> list = new ArrayList<>(ores.size());
                    for (ItemStack ore : ores) {
                        ItemStack copy = GTUtility.copyAmount(size, ore);
                        if (GTUtility.isStackValid(copy)) list.add(copy);
                    }
                    result = list.toArray(new ItemStack[0]);
                }

            } else if (in == null) {
                handleNullRecipeComponents("recipe oredict input");
                result = GTValues.emptyItemStackArray;

            } else {
                throw new IllegalArgumentException("index " + i + ", unexpected type: " + in.getClass());
            }

            alts[i] = result;
        }

        int l = alts.length;
        ArrayList<ItemStack> basics = new ArrayList<>(l);
        for (ItemStack[] s : alts) {
            basics.add(s.length > 0 ? s[0] : null);
        }

        this.inputsBasic = applyPendingCircuit(
            basics.isEmpty() ? GTValues.emptyItemStackArray : basics.toArray(new ItemStack[0]));
        preserveNullItemInputs = false;
        inputItemCount = countValidItemStacks(this.inputsBasic);

        return this;
    }

    public RecipeBuilder itemInputs(Object[] inputs, int[] chances) {
        if (skip) return this;
        this.inputChances = chances;
        return itemInputs(inputs);
    }

    public RecipeBuilder itemInputsAllowNulls(ItemStack... inputs) {
        if (skip) return this;
        inputsBasic = fix(inputs, false);
        preserveNullItemInputs = true;
        inputItemCount = countValidItemStacks(inputsBasic);
        inputsOreDict = null;
        alts = null;
        altOreIds = null;
        return this;
    }

    public RecipeBuilder itemOutputs(ItemStack... outputItems) {
        if (skip) return this;
        this.outputs = filterValidItemStacks(outputItems);
        outputItemCount = this.outputs.length;
        return this;
    }

    public RecipeBuilder itemOutputs(ItemStack[] outputs, int[] chances) {
        if (skip) return this;
        this.outputChances = chances;
        return itemOutputs(outputs);
    }

    public RecipeBuilder fluidInputs(FluidStack... inputFluids) {
        if (skip) return this;
        if (debugNull() && containsNull(inputFluids)) handleNullRecipeComponents("fluidInputs");
        this.fluidInputs = inputFluids == null ? GTValues.emptyFluidStackArray : ArrayExt.removeNullFluids(inputFluids);
        inputFluidCount = this.fluidInputs.length;
        this.altFluidInputs = null;
        return this;
    }

    public RecipeBuilder fluidInputs(Object... fluids) {
        if (skip) return this;
        if (containsNull(fluids) || fluids.length == 0) {
            this.fluidInputs = GTValues.emptyFluidStackArray;
            this.altFluidInputs = null;
            this.inputFluidCount = 0;
            if (debugNull()) handleNullRecipeComponents("SubstituteFluidStack");
            return this;
        }

        List<FluidStack> mainFluidList = new ArrayList<>(fluids.length);
        List<FluidStack[]> altFluidList = new ArrayList<>(fluids.length);
        for (Object fluid : fluids) {
            if (fluid instanceof SubstituteFluidStack substituteFluidStack) {
                FluidStack[] alternatives = substituteFluidStack.fluidStacks.toArray(new FluidStack[0]);
                mainFluidList.add(alternatives.length > 0 ? alternatives[0] : null);
                altFluidList.add(alternatives);
            } else if (fluid instanceof FluidStack fluidStack) {
                mainFluidList.add(fluidStack);
                altFluidList.add(new FluidStack[] { fluidStack });
            } else {
                throw new IllegalArgumentException("Unexpected type: " + fluid.getClass());
            }
        }
        this.fluidInputs = mainFluidList.toArray(new FluidStack[0]);
        this.altFluidInputs = altFluidList.toArray(new FluidStack[0][]);
        inputFluidCount = this.fluidInputs.length;
        return this;
    }

    public RecipeBuilder fluidInputs(FluidStack[] inputs, int[] chances) {
        if (skip) return this;
        this.fluidInputChances = chances;
        return fluidInputs(inputs);
    }

    public RecipeBuilder fluidOutputs(FluidStack... outputFluids) {
        if (skip) return this;
        this.fluidOutputs = outputFluids == null ? GTValues.emptyFluidStackArray
            : ArrayExt.removeNullFluids(outputFluids);
        outputFluidCount = this.fluidOutputs.length;
        return this;
    }

    public RecipeBuilder fluidOutputs(FluidStack[] outputs, int[] chances) {
        if (skip) return this;
        this.fluidOutputChances = chances;
        return fluidOutputs(outputs);
    }

    public RecipeBuilder inputChances(int... outputChance) {
        this.inputChances = outputChance;
        return this;
    }

    public RecipeBuilder inputFluidChances(int... outputChance) {
        this.fluidInputChances = outputChance;
        return this;
    }

    public RecipeBuilder outputChances(int... outputChance) {
        this.outputChances = outputChance;
        return this;
    }

    public RecipeBuilder outputFluidChances(int... outputChance) {
        this.fluidOutputChances = outputChance;
        return this;
    }

    public RecipeBuilder special(Object special) {
        this.special = special;
        return this;
    }

    public RecipeBuilder eut(int eut) {
        this.eut = eut;
        return this;
    }

    public RecipeBuilder eut(long eut) {
        this.eut = (int) eut;
        return this;
    }

    public RecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public RecipeBuilder duration(long duration) {
        this.duration = (int) duration;
        return this;
    }

    public RecipeBuilder specialValue(int specialValue) {
        this.specialValue = specialValue;
        return this;
    }

    public RecipeBuilder disabled() {
        this.enabled = false;
        return this;
    }

    public RecipeBuilder setNEIDesc(String... neiDesc) {
        this.neiDesc = neiDesc;
        return this;
    }

    public RecipeBuilder recipeCategory(RecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
        return this;
    }

    public RecipeBuilder hidden() {
        this.hidden = true;
        return this;
    }

    public RecipeBuilder fake() {
        this.fakeRecipe = true;
        return this;
    }

    public RecipeBuilder noBuffer() {
        this.mCanBeBuffered = false;
        return this;
    }

    public RecipeBuilder needsEmptyOutput() {
        this.mNeedsEmptyOutput = true;
        return this;
    }

    public RecipeBuilder nbtSensitive() {
        this.nbtSensitive = true;
        return this;
    }

    public RecipeBuilder ignoreCollision() {
        this.checkForCollision = false;
        return this;
    }

    public RecipeBuilder clearInvalid() {
        valid = true;
        return this;
    }

    public RecipeBuilder invalidate() {
        valid = false;
        return this;
    }

    public RecipeBuilder requireMods(Mods... mods) {
        for (final Mods mod : mods) {
            if (!mod.isModLoaded()) {
                skip = true;
                return this;
            }
        }
        skip = false;
        return this;
    }

    public RecipeBuilder requiresCleanRoom() {
        return metadata(GTRecipeConstants.CLEANROOM, true);
    }

    public RecipeBuilder requiresLowGravity() {
        return metadata(GTRecipeConstants.LOW_GRAVITY, true);
    }

    public <T> RecipeBuilder metadata(RecipeMetadataKey<T> key, T value) {
        if (skip) return this;
        if (metadataStorage == null) {
            metadataStorage = new RecipeMetadataStorage();
        }
        metadataStorage.store(key, value);
        return this;
    }

    public <T> T getMetadata(RecipeMetadataKey<T> key) {
        return metadataStorage == null ? null : metadataStorage.getMetadata(key);
    }

    public RecipeBuilder modifiedByVariant(String... variantNames) {
        if (!Mods.VariableHorizons.isModLoaded()) {
            return this;
        }
        for (String variantName : variantNames) {
            if (VariantNames.activeContains(variantName)) {
                skip = true;
                return this;
            }
        }
        return this;
    }

    public RecipeBuilder copy() {
        RecipeBuilder copy = copyState();
        copy.metadataStorage = metadataStorage == null ? null : metadataStorage.copy();
        return copy;
    }

    public RecipeBuilder copyNoMetadata() {
        RecipeBuilder copy = copyState();
        copy.metadataStorage = null;
        return copy;
    }

    private RecipeBuilder copyState() {
        RecipeBuilder copy = new RecipeBuilder();
        copy.inputsBasic = copyArray(inputsBasic);
        copy.inputsOreDict = copyArray(inputsOreDict);
        copy.outputs = copyArray(outputs);
        copy.alts = copyArray(alts);
        copy.altFluidInputs = copyArray(altFluidInputs);
        copy.altOreIds = altOreIds == null ? null : altOreIds.clone();
        copy.fluidInputs = copyArray(fluidInputs);
        copy.fluidOutputs = copyArray(fluidOutputs);
        copy.inputChances = inputChances == null ? null : inputChances.clone();
        copy.outputChances = outputChances == null ? null : outputChances.clone();
        copy.fluidInputChances = fluidInputChances == null ? null : fluidInputChances.clone();
        copy.fluidOutputChances = fluidOutputChances == null ? null : fluidOutputChances.clone();
        copy.special = special;
        copy.duration = duration;
        copy.eut = eut;
        copy.specialValue = specialValue;
        copy.enabled = enabled;
        copy.hidden = hidden;
        copy.fakeRecipe = fakeRecipe;
        copy.mCanBeBuffered = mCanBeBuffered;
        copy.mNeedsEmptyOutput = mNeedsEmptyOutput;
        copy.nbtSensitive = nbtSensitive;
        copy.neiDesc = neiDesc == null ? null : neiDesc.clone();
        copy.recipeCategory = recipeCategory;
        copy.checkForCollision = checkForCollision;
        copy.skip = skip;
        copy.valid = valid;
        copy.pendingIntegratedCircuit = pendingIntegratedCircuit;
        copy.preserveNullItemInputs = preserveNullItemInputs;
        copy.inputItemCount = inputItemCount;
        copy.outputItemCount = outputItemCount;
        copy.inputFluidCount = inputFluidCount;
        copy.outputFluidCount = outputFluidCount;
        return copy;
    }

    public ItemStack getItemInputBasic(int index) {
        return inputsBasic[index];
    }

    public Object getItemInputOreDict(int index) {
        return inputsOreDict[index];
    }

    public ItemStack getItemOutput(int index) {
        return outputs[index];
    }

    public FluidStack getFluidInput(int index) {
        return fluidInputs[index];
    }

    public FluidStack getFluidOutput(int index) {
        return fluidOutputs[index];
    }

    public ItemStack[] getItemInputsBasic() {
        return inputsBasic;
    }

    public Object[] getItemInputsOreDict() {
        return inputsOreDict;
    }

    public Object[] getFluidInputsInterchangeable() {
        if (altFluidInputs == null) {
            return fluidInputs;
        }
        Object[] result = new Object[altFluidInputs.length];
        for (int i = 0; i < altFluidInputs.length; i++) {
            FluidStack[] alternatives = altFluidInputs[i];
            result[i] = alternatives == null || alternatives.length <= 1 ? alternatives == null ? null : alternatives[0]
                : new SubstituteFluidStack(alternatives);
        }
        return result;
    }

    public ItemStack[] getItemOutputs() {
        return outputs;
    }

    public FluidStack[] getFluidInputs() {
        return fluidInputs;
    }

    public FluidStack[] getFluidOutputs() {
        return fluidOutputs;
    }

    public int getDuration() {
        return duration;
    }

    public int[] getInputChances() {
        return inputChances;
    }

    public int[] getOutputChances() {
        return outputChances;
    }

    public int[] getFluidInputChances() {
        return fluidInputChances;
    }

    public int[] getFluidOutputChances() {
        return fluidOutputChances;
    }

    public int getEUt() {
        return eut;
    }

    public RecipeCategory getRecipeCategory() {
        return recipeCategory;
    }

    public boolean isCheckForCollision() {
        return checkForCollision;
    }

    public boolean isValid() {
        return valid;
    }

    public RecipeBuilder forceOreDictInput() {
        if (inputsOreDict != null || inputsBasic == null) {
            return this;
        }
        return itemInputs((Object[]) inputsBasic);
    }

    public RecipeBuilder validateNoInput() {
        if (!skip && countValidItemStacks(inputsBasic) > 0) invalidate();
        return this;
    }

    public RecipeBuilder validateNoInputFluid() {
        if (!skip && countValidFluids(fluidInputs) > 0) invalidate();
        return this;
    }

    public RecipeBuilder validateNoOutput() {
        if (!skip && countValidItemStacks(outputs) > 0) invalidate();
        return this;
    }

    public RecipeBuilder validateNoOutputFluid() {
        if (!skip && countValidFluids(fluidOutputs) > 0) invalidate();
        return this;
    }

    public RecipeBuilder validateInputCount(int min, int max) {
        if (!skip && !withinRange(countValidItemStacks(inputsBasic), min, max)) invalidate();
        return this;
    }

    public RecipeBuilder validateInputFluidCount(int min, int max) {
        if (!skip && !withinRange(countValidFluids(fluidInputs), min, max)) invalidate();
        return this;
    }

    public RecipeBuilder validateOutputCount(int min, int max) {
        if (!skip && !withinRange(countValidItemStacks(outputs), min, max)) invalidate();
        return this;
    }

    public RecipeBuilder validateOutputFluidCount(int min, int max) {
        if (!skip && !withinRange(countValidFluids(fluidOutputs), min, max)) invalidate();
        return this;
    }

    public RecipeBuilder validateAnyInput() {
        if (!skip && countValidItemStacks(inputsBasic) == 0 && countValidFluids(fluidInputs) == 0) invalidate();
        return this;
    }

    public RecipeBuilder validateAnyOutput() {
        if (!skip && countValidItemStacks(outputs) == 0 && countValidFluids(fluidOutputs) == 0) invalidate();
        return this;
    }

    public RecipeBuilder addTo(IRecipeMap recipeMap) {
        if (skip) {
            return this;
        }

        if (recipeMap == null) {
            ScienceNotLeisure.LOG.error("RecipeMap is null!");
            return this;
        }

        normalizeChanceArrays();
        if (shouldUseDirectRecipePath()) {
            addDirectRecipe(recipeMap);
            return this;
        }

        GTRecipeBuilder builder = GTValues.RA.stdBuilder();
        if (inputsOreDict != null) {
            builder = builder.itemInputs(inputsOreDict);
        } else if (inputsBasic != null) {
            builder = builder.itemInputs(inputsBasic);
        }

        if (outputs != null) {
            builder = builder.itemOutputs(outputs);
        }

        if (special != null) {
            builder = builder.special(special);
        }

        if (inputChances != null) {
            builder = builder.inputChances(inputChances);
        }

        if (fluidInputChances != null) {
            builder = builder.fluidInputChances(fluidInputChances);
        }

        if (outputChances != null) {
            builder = builder.outputChances(outputChances);
        }

        if (fluidOutputChances != null) {
            builder = builder.fluidOutputChances(fluidOutputChances);
        }

        if (altFluidInputs != null) {
            Object[] fluids = new Object[altFluidInputs.length];
            for (int i = 0; i < altFluidInputs.length; i++) {
                FluidStack[] alternatives = altFluidInputs[i];
                fluids[i] = alternatives.length == 1 ? alternatives[0] : new SubstituteFluidStack(alternatives);
            }
            builder = builder.fluidInputs(fluids);
        } else if (fluidInputs != null) {
            builder = builder.fluidInputs(fluidInputs);
        }

        if (fluidOutputs != null) {
            builder = builder.fluidOutputs(fluidOutputs);
        }

        builder = builder.duration(duration)
            .eut(eut)
            .specialValue(specialValue);

        if (neiDesc != null) {
            builder = builder.setNEIDesc(neiDesc);
        }

        builder = valid ? builder.clearInvalid() : builder.invalidate();

        if (!checkForCollision) builder = builder.ignoreCollision();
        if (hidden) builder = builder.hidden();
        if (fakeRecipe) builder = builder.fake();
        if (nbtSensitive) builder = builder.nbtSensitive();

        if (metadataStorage != null) {
            ((AccessorGTRecipeBuilder) builder).setMetadataStorage(metadataStorage);
        }

        recipeMap.doAdd(builder);
        return this;
    }

    public RecipeBuilder addTo(RecipeMap<?> recipeMap) {
        if (skip) {
            return this;
        }
        return addTo((IRecipeMap) recipeMap);
    }

    public RecipeBuilder reset() {
        metadataStorage = null;
        alts = null;
        altFluidInputs = null;
        inputChances = null;
        outputChances = null;
        fluidInputChances = null;
        fluidOutputChances = null;
        duration = -1;
        enabled = true;
        eut = -1;
        fakeRecipe = false;
        fluidInputs = null;
        fluidOutputs = null;
        hidden = false;
        inputsBasic = null;
        inputsOreDict = null;
        mCanBeBuffered = true;
        mNeedsEmptyOutput = false;
        nbtSensitive = false;
        neiDesc = null;
        recipeCategory = null;
        outputs = null;
        special = null;
        specialValue = 0;
        skip = false;
        valid = true;
        pendingIntegratedCircuit = null;
        preserveNullItemInputs = false;
        inputItemCount = 0;
        outputItemCount = 0;
        inputFluidCount = 0;
        outputFluidCount = 0;
        return this;
    }

    public Optional<GTRecipe> build() {
        return buildRecipe(true);
    }

    public Optional<GTRecipe.GTRecipe_WithAlt> buildWithAlt() {
        if (skip) {
            return Optional.empty();
        }
        if (inputsOreDict == null && altFluidInputs == null) {
            throw new UnsupportedOperationException();
        }
        if (!valid) {
            handleInvalidRecipe();
            return Optional.empty();
        }
        normalizeChanceArrays();
        preBuildChecks();
        return Optional.of(
            decorate(
                AccessorGTRecipeWithAlt.create(
                    inputsBasic,
                    outputs,
                    fluidInputs,
                    fluidOutputs,
                    inputChances,
                    outputChances,
                    fluidInputChances,
                    fluidOutputChances,
                    special,
                    duration,
                    eut,
                    specialValue,
                    enabled,
                    hidden,
                    fakeRecipe,
                    mCanBeBuffered,
                    mNeedsEmptyOutput,
                    nbtSensitive,
                    neiDesc,
                    metadataStorage,
                    recipeCategory,
                    alts,
                    altOreIds,
                    altFluidInputs)));
    }

    private Optional<GTRecipe> buildRecipe(boolean normalizeChances) {
        if (skip) {
            return Optional.empty();
        }
        if (!valid) {
            handleInvalidRecipe();
            return Optional.empty();
        }
        if (normalizeChances) {
            normalizeChanceArrays();
        }
        preBuildChecks();
        if (inputsOreDict != null || altFluidInputs != null) {
            return Optional.of(
                decorate(
                    AccessorGTRecipeWithAlt.create(
                        inputsBasic,
                        outputs,
                        fluidInputs,
                        fluidOutputs,
                        inputChances,
                        outputChances,
                        fluidInputChances,
                        fluidOutputChances,
                        special,
                        duration,
                        eut,
                        specialValue,
                        enabled,
                        hidden,
                        fakeRecipe,
                        mCanBeBuffered,
                        mNeedsEmptyOutput,
                        nbtSensitive,
                        neiDesc,
                        metadataStorage,
                        recipeCategory,
                        alts,
                        altOreIds,
                        altFluidInputs)));
        }
        return Optional.of(
            decorate(
                AccessorGTRecipe.create(
                    inputsBasic,
                    outputs,
                    fluidInputs,
                    fluidOutputs,
                    inputChances,
                    fluidInputChances,
                    outputChances,
                    fluidOutputChances,
                    eut,
                    specialValue,
                    enabled,
                    hidden,
                    fakeRecipe,
                    mCanBeBuffered,
                    mNeedsEmptyOutput,
                    nbtSensitive,
                    neiDesc,
                    metadataStorage,
                    recipeCategory,
                    special,
                    duration)));
    }

    public void preBuildChecks() {
        if (duration == -1) throw new IllegalStateException("no duration");
        if (eut == -1) throw new IllegalStateException("no eut");
    }

    public <T extends GTRecipe> T decorate(T r) {
        r.mHidden = hidden;
        r.mCanBeBuffered = mCanBeBuffered;
        r.mNeedsEmptyOutput = mNeedsEmptyOutput;
        r.isNBTSensitive = nbtSensitive;
        r.mFakeRecipe = fakeRecipe;
        r.mEnabled = enabled;
        if (neiDesc != null) r.setNeiDesc(neiDesc);
        applyDefaultSpecialValues(r);
        return r;
    }

    @Contract("_, !null -> !null")
    @Nullable
    public <T> T getMetadataOrDefault(RecipeMetadataKey<T> key, T defaultValue) {
        if (metadataStorage == null) {
            return defaultValue;
        }
        return key.cast(metadataStorage.getMetadataOrDefault(key, defaultValue));
    }

    public void applyDefaultSpecialValues(GTRecipe recipe) {
        if (recipe.mSpecialValue != 0) return;

        int specialValue = 0;
        if (getMetadataOrDefault(GTRecipeConstants.LOW_GRAVITY, false)) specialValue -= 100;
        if (getMetadataOrDefault(GTRecipeConstants.CLEANROOM, false)) specialValue -= 200;
        for (RecipeMetadataKey<? extends Number> ident : SPECIAL_VALUE_ALIASES) {
            Number metadata = getMetadataOrDefault(ident, null);
            if (metadata != null) {
                specialValue = metadata.intValue();
                break;
            }
        }
        recipe.mSpecialValue = specialValue;
    }

    private void normalizeChanceArrays() {
        inputChances = ArrayExt.fixChancesArray(inputChances, inputItemCount);
        fluidInputChances = ArrayExt.fixChancesArray(fluidInputChances, inputFluidCount);
        outputChances = ArrayExt.fixChancesArray(outputChances, outputItemCount);
        fluidOutputChances = ArrayExt.fixChancesArray(fluidOutputChances, outputFluidCount);
    }

    private boolean shouldUseDirectRecipePath() {
        return preserveNullItemInputs;
    }

    private void addDirectRecipe(IRecipeMap recipeMap) {
        Optional<GTRecipe> recipe = buildRecipe(false);
        if (recipe.isEmpty()) {
            return;
        }
        if (recipeMap instanceof RecipeMap<?>actualRecipeMap) {
            actualRecipeMap.addRecipe(recipe.get(), checkForCollision, fakeRecipe, hidden);
            return;
        }
        throw new IllegalStateException("Null-slot recipes require a concrete RecipeMap instance.");
    }

    private ItemStack[] filterValidItemStacks(ItemStack[] stacks) {
        if (stacks == null || stacks.length == 0) {
            return GTValues.emptyItemStackArray;
        }
        List<ItemStack> validStacks = new ArrayList<>(stacks.length);
        for (ItemStack stack : stacks) {
            if (GTUtility.isStackValid(stack)) {
                validStacks.add(stack);
            }
        }
        return validStacks.isEmpty() ? GTValues.emptyItemStackArray : validStacks.toArray(new ItemStack[0]);
    }

    private static <T> T[] copyArray(T[] values) {
        return values == null ? null : values.clone();
    }

    private int countValidItemStacks(ItemStack[] stacks) {
        if (stacks == null || stacks.length == 0) {
            return 0;
        }
        int count = 0;
        for (ItemStack stack : stacks) {
            if (GTUtility.isStackValid(stack)) {
                count++;
            }
        }
        return count;
    }

    private int countValidFluids(FluidStack[] stacks) {
        if (stacks == null || stacks.length == 0) {
            return 0;
        }
        int count = 0;
        for (FluidStack stack : stacks) {
            if (stack != null && stack.getFluid() != null && stack.amount > 0) {
                count++;
            }
        }
        return count;
    }

    private static boolean withinRange(int value, int min, int max) {
        return (min < 0 || value >= min) && (max < 0 || value <= max);
    }

    public static void handleInvalidRecipe() {
        if (!DEBUG_MODE_INVALID && !PANIC_MODE_INVALID) {
            return;
        }
        ScienceNotLeisure.LOG.error("invalid recipe");

        ScienceNotLeisure.LOG.error("Stacktrace:", new IllegalArgumentException());

        if (PANIC_MODE_INVALID) {
            throw new IllegalArgumentException("invalid recipe");
        }
    }

    public static ItemStack[] fix(ItemStack[] inputs, boolean aUnsafe) {
        return GTOreDictUnificator
            .setStackArray(true, aUnsafe, ArrayExt.withoutTrailingNulls(inputs, ItemStack[]::new));
    }

    public static boolean debugNull() {
        return DEBUG_MODE_NULL || PANIC_MODE_NULL;
    }

    public static boolean containsNull(Object[] arr) {
        if (arr == null) return true;
        for (final Object o : arr) {
            if (o == null) return true;
        }
        return false;
    }

    public static void handleNullRecipeComponents(String componentType) {
        // place a breakpoint here to catch all these issues
        GTLoggers.GT_FML_LOGGER.error("null detected in {}", componentType);
        new NullPointerException().printStackTrace();
        if (PANIC_MODE_NULL) {
            throw new IllegalArgumentException("null in argument");
        }
    }
}
