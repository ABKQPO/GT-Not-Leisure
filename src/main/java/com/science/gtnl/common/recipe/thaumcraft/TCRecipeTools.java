package com.science.gtnl.common.recipe.thaumcraft;

import static thaumcraft.common.config.ConfigItems.itemEldritchObject;
import static thaumcraft.common.config.ConfigItems.itemJarNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.utils.AspectTooltipUtils;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import fox.spiteful.avaritia.items.LudicrousItems;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import lombok.Getter;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

public class TCRecipeTools {

    public static ArrayList<ShapelessArcaneCraftingRecipe> ShaplessAR = new ArrayList<>();
    public static ArrayList<ShapedArcaneCraftingRecipe> ShapedAR = new ArrayList<>();
    public static ArrayList<InfusionCraftingRecipe> ICR = new ArrayList<>();

    public TCRecipeTools() {}

    public static final ItemStack IC2_MACHINE = ModsItemlist.IC2IronFurnace.get(1);
    public static final ItemStack BLAST_FURNACE_TEMPLATE = GTModHandler
        .getModItem(Mods.EtFuturumRequiem.ID, "blast_furnace", 1);
    public static final Set<ItemId> UNCONSUMED_ITEMS = new HashSet<>();
    public static final Set<ItemId> PRIMORDIAL_PEARL_RETURN_EXCLUSIONS = new HashSet<>();

    // Recipe duration is derived from the total aspect amount.
    private static final int ARCANE_DURATION_BASE_TICKS = 20;

    // Duration coefficients distinguish arcane crafting from infusion.
    public static final int ARCANE_DURATION_TICKS_PER_ASPECT = 8;
    public static final int INFUSION_DURATION_TICKS_PER_ASPECT = 16;

    private static Set<Item> skipItems;

    static {
        if (Mods.Avaritia.isModLoaded()) addAvaritia();

        if (Mods.BloodMagic.isModLoaded()) {
            UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.BloodMagicWeakBloodOrb.get(1)));
            UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.BloodMagicApprenticeBloodOrb.get(1)));
            UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.BloodMagicMagicianBloodOrb.get(1)));
            UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.BloodMagicMasterBloodOrb.get(1)));
            UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.BloodMagicArchmageBloodOrb.get(1)));
            UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.BloodMagicTranscendentBloodOrb.get(1)));
            UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.BloodMagicCreativeFiller.get(1)));
        }

        if (Mods.ForbiddenMagic.isModLoaded()) {
            UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.ForbiddenMagicEldritchOrb.get(1)));
        }

        UNCONSUMED_ITEMS.add(ItemId.create(ModsItemlist.ThaumcraftFocusWarding.get(1)));
    }

    @Optional.Method(modid = "Avaritia")
    public static void addAvaritia() {
        ItemId bigPearl = ItemId.create(new ItemStack(LudicrousItems.bigPearl));
        UNCONSUMED_ITEMS.add(bigPearl);
        PRIMORDIAL_PEARL_RETURN_EXCLUSIONS.add(bigPearl);
        UNCONSUMED_ITEMS.add(ItemId.create(new ItemStack(LudicrousItems.armok_orb)));
    }

    // Identify Thaumcraft items that should be omitted from recipe pools.
    // Multiple recipe pools share the lazily initialized set.
    public static boolean shouldSkipThaumcraftItem(Item item) {
        if (skipItems == null) {
            skipItems = new HashSet<>();
            skipItems.add(itemJarNode);

            if (Mods.ThaumicBases.isModLoaded()) {
                Item revolver = GameRegistry.findItem(Mods.ThaumicBases.ID, "revolver");

                if (null != revolver) {
                    skipItems.add(revolver);
                }
            }

            if (Mods.Gadomancy.isModLoaded()) {
                Item itemEtherealFamiliar = GameRegistry.findItem(Mods.Gadomancy.ID, "ItemEtherealFamiliar");

                if (null != itemEtherealFamiliar) {
                    skipItems.add(itemEtherealFamiliar);
                }
            }
        }

        return skipItems.contains(item);
    }

    // Normalize Thaumcraft inputs and replace IC2 machines with blast furnace templates.
    public static ItemStack[] checkInputSpecial(ItemStack... itemStacks) {
        final int len = itemStacks.length;
        ItemStack[] copy = new ItemStack[len];

        for (int idx = 0; idx < len; idx++) {
            ItemStack i = itemStacks[idx];

            if (i == null) {
                copy[idx] = null;
                continue;
            }

            ItemStack out = i.copy();

            if (out.getItem() == IC2_MACHINE.getItem() && BLAST_FURNACE_TEMPLATE != null) {
                ItemStack bf = BLAST_FURNACE_TEMPLATE.copy();
                bf.stackSize = out.stackSize;
                out = bf;
            }

            if (UNCONSUMED_ITEMS.contains(ItemId.create(out))) {
                out.stackSize = 0;
            }

            copy[idx] = out;
        }

        return copy;
    }

    // Convert Thaumcraft components into GregTech recipe inputs.
    // Circuit boards expand by tier so any board from the same tier is accepted.
    // Other components retain single-item semantics, and zero quantity means non-consuming.
    public static Object toGTInput(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return stack;

        ItemData data = GTOreDictUnificator.getAssociation(stack);
        if (data == null || data.mPrefix != OrePrefixes.circuit
            || data.mMaterial == null
            || data.mMaterial.mMaterial == null) {
            return stack;
        }

        // ItemData.toString() is the ore dictionary name registered for the tier.
        // Fall back to a single item when the ore dictionary entry is empty.
        Object oreName = OrePrefixes.circuit.get(data.mMaterial.mMaterial);
        if (oreName == null || GTOreDictUnificator.getOres(oreName)
            .isEmpty()) {
            return stack;
        }

        return new Object[] { oreName, stack.stackSize };
    }

    public static Object[] toGTInputs(ItemStack... stacks) {
        Object[] converted = new Object[stacks.length];

        for (int i = 0; i < stacks.length; i++) {
            converted[i] = toGTInput(stacks[i]);
        }

        return converted;
    }

    // Build the ItemStack array displayed in the NEI aspect slots.
    public static ItemStack[] createAspectDisplayStacks(AspectList aspectList) {
        return createAspectDisplayStacksFromCopy(copyAspectList(aspectList));
    }

    private static ItemStack[] createAspectDisplayStacksFromCopy(AspectList aspectList) {
        if (aspectList.size() == 0) {
            return new ItemStack[0];
        }

        Aspect[] aspects = aspectList.getAspectsSortedAmount();
        ItemStack[] stacks = new ItemStack[aspects.length];

        for (int i = 0; i < aspects.length; i++) {
            stacks[i] = AspectTooltipUtils.createAspectStack(aspects[i], aspectList.getAmount(aspects[i]));
        }

        return stacks;
    }

    public static ItemStack[] appendPrimordialPearlReturns(ItemStack[] outputs, ProcessingLogic logic) {
        if (!(logic instanceof GTNLProcessingLogic gtnlLogic)) return outputs;
        return appendPrimordialPearlReturns(outputs, gtnlLogic.getAppliedRecipe(), gtnlLogic.getCurrentParallels());
    }

    public static ItemStack[] appendPrimordialPearlReturns(ItemStack[] outputs, GTRecipe recipe, int parallels) {
        if (outputs == null || recipe == null || recipe.mOutputs == null || parallels <= 0) return outputs;

        ItemId primordialPearl = ItemId.create(new ItemStack(itemEldritchObject, 1, 3));
        long pearlsPerCraft = 0;
        for (ItemStack output : recipe.mOutputs) {
            if (output == null || PRIMORDIAL_PEARL_RETURN_EXCLUSIONS.contains(ItemId.create(output))) continue;

            InfusionRecipe infusionRecipe = ThaumcraftApi.getInfusionRecipe(output);
            if (infusionRecipe == null || infusionRecipe.getComponents() == null) continue;

            for (ItemStack component : infusionRecipe.getComponents()) {
                if (component != null && primordialPearl.equals(ItemId.create(component))) pearlsPerCraft++;
            }
        }

        long pearlCount = pearlsPerCraft * parallels;
        if (pearlCount <= 0) return outputs;

        List<ItemStack> withReturns = new ArrayList<>(Arrays.asList(outputs));
        while (pearlCount > 0) {
            int stackSize = (int) Math.min(64, pearlCount);
            withReturns.add(new ItemStack(itemEldritchObject, stackSize, 3));
            pearlCount -= stackSize;
        }
        return withReturns.toArray(new ItemStack[0]);
    }

    // Register a Thaumcraft recipe with aspect and research metadata.
    // The convenience overload derives duration from the aspect total at LV voltage.
    public static void addArcaneRecipe(IRecipeMap map, ItemStack[] inputs, ItemStack[] output, AspectList aspects,
        String research, RecipeMetadataKey<AspectList> aspectKey, RecipeMetadataKey<String> researchKey) {

        AspectList snapshot = copyAspectList(aspects);
        addArcaneRecipeWithSnapshot(
            map,
            inputs,
            output,
            snapshot,
            research,
            aspectKey,
            researchKey,
            computeAspectDurationFromCopy(snapshot, ARCANE_DURATION_TICKS_PER_ASPECT),
            TierEU.RECIPE_LV);
    }

    // Derive processing duration in ticks from the aspect total.
    public static int computeAspectDuration(AspectList aspects, int ticksPerAspect) {
        if (aspects == null) {
            return ARCANE_DURATION_BASE_TICKS;
        }

        int totalAmount = 0;
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null) {
                totalAmount += aspects.getAmount(aspect);
            }
        }
        return ARCANE_DURATION_BASE_TICKS + totalAmount * ticksPerAspect;
    }

    // Allow callers to provide duration in ticks and voltage in EU/t.
    public static void addArcaneRecipe(IRecipeMap map, ItemStack[] inputs, ItemStack[] output, AspectList aspects,
        String research, RecipeMetadataKey<AspectList> aspectKey, RecipeMetadataKey<String> researchKey, int duration,
        long eut) {

        addArcaneRecipeWithSnapshot(
            map,
            inputs,
            output,
            copyAspectList(aspects),
            research,
            aspectKey,
            researchKey,
            duration,
            eut);
    }

    public static void addArcaneRecipeWithCircuit(IRecipeMap map, ItemStack[] inputs, ItemStack[] output,
        AspectList aspects, String research, RecipeMetadataKey<AspectList> aspectKey,
        RecipeMetadataKey<String> researchKey, int duration, long eut, int circuit) {

        addArcaneRecipeWithSnapshotAndCircuit(
            map,
            inputs,
            output,
            copyAspectList(aspects),
            research,
            aspectKey,
            researchKey,
            duration,
            eut,
            circuit);
    }

    public static void addArcaneRecipeWithCircuit(IRecipeMap map, ItemStack[] inputs, ItemStack[] output,
        AspectList aspects, String research, RecipeMetadataKey<AspectList> aspectKey,
        RecipeMetadataKey<String> researchKey, int circuit) {

        AspectList snapshot = copyAspectList(aspects);
        addArcaneRecipeWithCircuit(
            map,
            inputs,
            output,
            snapshot,
            research,
            aspectKey,
            researchKey,
            computeAspectDurationFromCopy(snapshot, ARCANE_DURATION_TICKS_PER_ASPECT),
            TierEU.RECIPE_LV,
            circuit);
    }

    private static void addArcaneRecipeWithSnapshot(IRecipeMap map, ItemStack[] inputs, ItemStack[] output,
        AspectList snapshot, String research, RecipeMetadataKey<AspectList> aspectKey,
        RecipeMetadataKey<String> researchKey, int duration, long eut) {

        RecipeBuilder.builder()
            .ignoreCollision()
            .clearInvalid()
            .itemInputsUnified(inputs)
            .itemOutputs(output)
            .special(createAspectDisplayStacksFromCopy(snapshot))
            .metadata(aspectKey, snapshot)
            .metadata(researchKey, research)
            .duration(duration)
            .eut(eut)
            .addTo(map);
    }

    private static void addArcaneRecipeWithSnapshotAndCircuit(IRecipeMap map, ItemStack[] inputs, ItemStack[] output,
        AspectList snapshot, String research, RecipeMetadataKey<AspectList> aspectKey,
        RecipeMetadataKey<String> researchKey, int duration, long eut, int circuit) {

        RecipeBuilder.builder()
            .ignoreCollision()
            .clearInvalid()
            .itemInputsUnified(inputs)
            .circuit(circuit)
            .itemOutputs(output)
            .special(createAspectDisplayStacksFromCopy(snapshot))
            .metadata(aspectKey, snapshot)
            .metadata(researchKey, research)
            .duration(duration)
            .eut(eut)
            .addTo(map);
    }

    // Register the same recipe form with ore dictionary alternatives from toGTInput.
    public static void addArcaneRecipe(IRecipeMap map, Object[] inputs, ItemStack[] output, AspectList aspects,
        String research, RecipeMetadataKey<AspectList> aspectKey, RecipeMetadataKey<String> researchKey, int duration,
        long eut) {

        AspectList snapshot = copyAspectList(aspects);

        RecipeBuilder.builder()
            .ignoreCollision()
            .clearInvalid()
            .itemInputs(inputs)
            .itemOutputs(output)
            .special(createAspectDisplayStacksFromCopy(snapshot))
            .metadata(aspectKey, snapshot)
            .metadata(researchKey, research)
            .duration(duration)
            .eut(eut)
            .addTo(map);
    }

    public static void addArcaneRecipeWithCircuit(IRecipeMap map, Object[] inputs, ItemStack[] output,
        AspectList aspects, String research, RecipeMetadataKey<AspectList> aspectKey,
        RecipeMetadataKey<String> researchKey, int duration, long eut, int circuit) {

        AspectList snapshot = copyAspectList(aspects);
        RecipeBuilder.builder()
            .ignoreCollision()
            .clearInvalid()
            .itemInputs(inputs)
            .circuit(circuit)
            .itemOutputs(output)
            .special(createAspectDisplayStacksFromCopy(snapshot))
            .metadata(aspectKey, snapshot)
            .metadata(researchKey, research)
            .duration(duration)
            .eut(eut)
            .addTo(map);
    }

    public static void addArcaneRecipeWithCircuit(IRecipeMap map, Object[] inputs, ItemStack[] output,
        AspectList aspects, String research, RecipeMetadataKey<AspectList> aspectKey,
        RecipeMetadataKey<String> researchKey, int circuit) {

        AspectList snapshot = copyAspectList(aspects);
        addArcaneRecipeWithCircuit(
            map,
            inputs,
            output,
            snapshot,
            research,
            aspectKey,
            researchKey,
            computeAspectDurationFromCopy(snapshot, ARCANE_DURATION_TICKS_PER_ASPECT),
            TierEU.RECIPE_LV,
            circuit);
    }

    public static void getShapedArcaneCraftingRecipe() {
        ShapedAR.clear();
        List<Object> craftingRecipes = ThaumcraftApi.getCraftingRecipes();
        for (Object r : craftingRecipes) {
            if (!(r instanceof ShapedArcaneRecipe recipe)) {
                continue;
            }

            if (recipe.getRecipeOutput() instanceof ItemStack && recipe.getRecipeOutput()
                .getItem() != null) {
                ShapedArcaneCraftingRecipe y = new ShapedArcaneCraftingRecipe(
                    recipe.getInput(),
                    recipe.getRecipeOutput(),
                    recipe.getAspects(),
                    recipe.getResearch());
                ShapedAR.add(y);
            }
        }
    }

    public static void getShapelessArcaneCraftingRecipe() {
        ShaplessAR.clear();
        List<Object> craftingRecipes = ThaumcraftApi.getCraftingRecipes();
        for (Object r : craftingRecipes) {
            if (!(r instanceof ShapelessArcaneRecipe recipe)) {
                continue;
            }

            if (recipe.getRecipeOutput() instanceof ItemStack && recipe.getRecipeOutput()
                .getItem() != null) {
                ShapelessArcaneCraftingRecipe y = new ShapelessArcaneCraftingRecipe(
                    recipe.getInput(),
                    recipe.getRecipeOutput(),
                    recipe.getAspects(),
                    recipe.getResearch());
                ShaplessAR.add(y);
            }
        }
    }

    public static void getInfusionCraftingRecipe() {
        ICR.clear();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (!(r instanceof InfusionRecipe recipe)) {
                continue;
            }

            if ((recipe.getRecipeOutput() instanceof ItemStack
                && ((ItemStack) recipe.getRecipeOutput()).getItem() != null
                && recipe.getRecipeInput() != null)) {
                InfusionCraftingRecipe y = new InfusionCraftingRecipe(
                    recipe.getRecipeInput(),
                    recipe.getRecipeOutput(),
                    recipe.getComponents(),
                    recipe.getAspects(),
                    recipe.getResearch());
                ICR.add(y);
            }
        }
    }

    public static AspectList copyAspectList(AspectList source) {
        if (source == null) {
            return new AspectList();
        }

        AspectList copy = new AspectList();
        if (source.size() == 0) {
            return copy;
        }

        Aspect[] aspects = source.getAspects();
        for (Aspect aspect : aspects) {
            if (aspect != null) {
                copy.add(aspect, source.getAmount(aspect));
            }
        }
        return copy;
    }

    public static int computeAspectDurationFromCopy(AspectList aspects, int ticksPerAspect) {
        return ARCANE_DURATION_BASE_TICKS + aspects.visSize() * ticksPerAspect;
    }

    public static class ShapedArcaneCraftingRecipe {

        @Getter
        private final Object[] InputItems;
        private final ItemStack OutputItem;
        private final AspectList InputAspects;
        private final String Research;

        public ShapedArcaneCraftingRecipe(Object[] InputItems, ItemStack OutputItem, AspectList inputAspects,
            String research) {
            this.InputItems = InputItems;
            this.OutputItem = OutputItem;
            this.InputAspects = copyAspectList(inputAspects);
            this.Research = research == null ? "" : research;
        }

        public ItemStack getOutput() {
            return OutputItem;
        }

        public AspectList getInputAspects() {
            return InputAspects;
        }

        public String getResearch() {
            return Research;
        }
    }

    public static class ShapelessArcaneCraftingRecipe {

        @Getter
        private final ArrayList<?> InputItems;
        private final ItemStack OutputItem;
        private final AspectList InputAspects;
        private final String Research;

        public ShapelessArcaneCraftingRecipe(ArrayList<?> InputItems, ItemStack OutputItem, AspectList inputAspects,
            String research) {
            this.InputItems = InputItems;
            this.OutputItem = OutputItem;
            this.InputAspects = copyAspectList(inputAspects);
            this.Research = research == null ? "" : research;
        }

        public ItemStack getOutput() {
            return OutputItem;
        }

        public AspectList getInputAspects() {
            return InputAspects;
        }

        public String getResearch() {
            return Research;
        }
    }

    public static class InfusionCraftingRecipe {

        private final ItemStack InputItem;
        private final ItemStack OutputItem;
        private final ItemStack[] Components;
        private final AspectList InputAspects;
        private final String Research;

        public InfusionCraftingRecipe(ItemStack inputItem, Object outputItem, ItemStack[] components,
            AspectList inputAspects, String research) {

            this.InputItem = inputItem;
            this.OutputItem = (ItemStack) outputItem;
            this.Components = components;
            this.InputAspects = copyAspectList(inputAspects);
            this.Research = research == null ? "" : research;
        }

        public ItemStack[] getInputItem() {
            ItemStack[] input = new ItemStack[Components.length + 1];
            input[0] = InputItem;

            for (int index = 0; index < Components.length; index++) {
                input[index + 1] = Components[index];
            }

            return input;
        }

        public ItemStack getOutput() {
            return OutputItem;
        }

        public ItemStack[] getComponents() {
            return Components;
        }

        public AspectList getInputAspects() {
            return InputAspects;
        }

        public String getResearch() {
            return Research;
        }
    }
}
