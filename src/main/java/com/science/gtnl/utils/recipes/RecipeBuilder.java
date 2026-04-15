package com.science.gtnl.utils.recipes;

import static gregtech.api.util.GTRecipeMapUtil.SPECIAL_VALUE_ALIASES;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.mixins.early.Gregtech.AccessorGTRecipe;
import com.science.gtnl.mixins.early.Gregtech.AccessorGTRecipeBuilder;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeCategory;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.recipe.metadata.RecipeMetadataStorage;
import gregtech.api.util.GTLog;
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

    public ItemStack[] inputItems = GTValues.emptyItemStackArray;
    public Object[] inputsOreDict;
    public ItemStack[] outputItems = GTValues.emptyItemStackArray;
    public ItemStack[][] alts;
    public FluidStack[] inputFluids = GTValues.emptyFluidStackArray;
    public FluidStack[] outputFluids = GTValues.emptyFluidStackArray;
    public int[] outputChance;
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
    /**
     * If recipe addition should be skipped.
     */
    public boolean skip = false;
    public boolean valid = true;

    public RecipeBuilder() {}

    public RecipeBuilder itemInputsUnified(ItemStack... inputs) {
        if (skip) return this;
        if (debugNull() && containsNull(inputs)) handleNullRecipeComponents("itemInputUnified");
        inputItems = ArrayExt.removeTrailingNulls(inputs);
        inputsOreDict = null;
        alts = null;
        return this;
    }

    public RecipeBuilder itemInputs(List<ItemStack> inputItems) {
        return itemInputs(inputItems.toArray(new ItemStack[0]));
    }

    public RecipeBuilder itemInputs(ItemStack... inputItems) {
        if (inputItems != null && inputItems.length > 0) {
            this.inputItems = inputItems;
        }
        return this;
    }

    public RecipeBuilder itemInputs(Object... inputs) {
        if (skip) return this;

        this.inputsOreDict = inputs;
        int len = inputs.length;
        this.alts = new ItemStack[len][];

        for (int i = 0; i < len; i++) {
            Object in = inputs[i];
            ItemStack[] result;

            if (in instanceof ItemStack stack) {
                result = new ItemStack[] { stack };

            } else if (in instanceof ItemStack[]arr) {
                result = arr.clone();

            } else if (in instanceof Object[]arr && arr.length == 2) {
                ArrayList<ItemStack> ores = GTOreDictUnificator.getOres(arr[0]);
                if (ores.isEmpty()) {
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

        this.inputItems = basics.isEmpty() ? GTValues.emptyItemStackArray : basics.toArray(new ItemStack[0]);

        return this;
    }

    public RecipeBuilder itemInputsAllowNulls(ItemStack... inputs) {
        if (skip) return this;
        inputItems = fix(inputs, false);
        inputsOreDict = null;
        alts = null;
        return this;
    }

    public RecipeBuilder itemOutputs(ItemStack... outputItems) {
        if (outputItems != null && outputItems.length > 0) {
            this.outputItems = outputItems;
        }
        return this;
    }

    public RecipeBuilder fluidInputs(FluidStack... inputFluids) {
        if (inputFluids != null && inputFluids.length > 0) {
            this.inputFluids = inputFluids;
        }
        return this;
    }

    public RecipeBuilder fluidOutputs(FluidStack... outputFluids) {
        if (outputFluids != null && outputFluids.length > 0) {
            this.outputFluids = outputFluids;
        }
        return this;
    }

    public RecipeBuilder outputChances(int... outputChance) {
        this.outputChance = outputChance;
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

    public RecipeBuilder addTo(IRecipeMap recipeMap) {
        if (skip) {
            return this;
        }

        if (recipeMap == null) {
            ScienceNotLeisure.LOG.error("RecipeMap is null!");
            return this;
        }

        GTRecipeBuilder builder = GTValues.RA.stdBuilder();
        if (inputItems != null) {
            builder = builder.itemInputs(inputItems);
        }

        if (outputItems != null) {
            builder = builder.itemOutputs(outputItems);
        }

        if (special != null) {
            builder = builder.special(special);
        }

        if (outputChance != null) {
            builder = builder.outputChances(outputChance);
        }

        if (inputFluids != null) {
            builder = builder.fluidInputs(inputFluids);
        }

        if (outputFluids != null) {
            builder = builder.fluidOutputs(outputFluids);
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

        this.build()
            .map(this::decorate)
            .ifPresent(recipeMap::add);

        return this;
    }

    public Optional<GTRecipe> build() {
        if (skip) {
            return Optional.empty();
        }
        if (!valid) {
            handleInvalidRecipe();
            return Optional.empty();
        }
        preBuildChecks();
        return Optional.of(
            decorate(
                AccessorGTRecipe.create(
                    inputItems,
                    outputItems,
                    inputFluids,
                    outputFluids,
                    outputChance,
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
                    recipeCategory)));
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
        for (RecipeMetadataKey<Integer> ident : SPECIAL_VALUE_ALIASES) {
            Integer metadata = getMetadataOrDefault(ident, null);
            if (metadata != null) {
                specialValue = metadata;
                break;
            }
        }
        recipe.mSpecialValue = specialValue;
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
        GTLog.err.print("null detected in ");
        GTLog.err.println(componentType);
        new NullPointerException().printStackTrace(GTLog.err);
        if (PANIC_MODE_NULL) {
            throw new IllegalArgumentException("null in argument");
        }
    }
}
