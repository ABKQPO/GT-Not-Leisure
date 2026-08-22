package com.science.gtnl.common.recipe.thaumcraft;

import static thaumcraft.common.config.ConfigItems.itemJarNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.utils.AspectTooltipUtils;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import fox.spiteful.avaritia.items.LudicrousItems;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.GTModHandler;
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

    public static final ItemStack IC2_MACHINE = GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockMachine", 1, 1);
    public static final ItemStack BLAST_FURNACE_TEMPLATE = GTModHandler
        .getModItem(Mods.EtFuturumRequiem.ID, "blast_furnace", 1);
    public static final Set<ItemId> UNCONSUMED_ITEMS = new HashSet<>();
    public static final Set<ItemId> PRIMORDIAL_PEARL_RETURN_EXCLUSIONS = new HashSet<>();

    // 配方时长随源质总量自动生成：基础时长 + 每点源质 × 总量。
    private static final int ARCANE_DURATION_BASE_TICKS = 20;

    // 每点源质的时长系数：奥术合成较快，注魔较慢，可按池调。
    public static final int ARCANE_DURATION_TICKS_PER_ASPECT = 8;
    public static final int INFUSION_DURATION_TICKS_PER_ASPECT = 16;

    private static Set<Item> skipItems;

    static {
        if (Mods.Avaritia.isModLoaded()) addAvaritia();

        if (Mods.BloodMagic.isModLoaded()) {
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "weakBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "apprenticeBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "magicianBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "masterBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "archmageBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "transcendentBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "creativeFiller", 1)));
        }

        if (Mods.ForbiddenMagic.isModLoaded()) {
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.ForbiddenMagic.ID, "EldritchOrb", 1)));
        }

        UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.Thaumcraft.ID, "FocusWarding", 1)));
    }

    @Optional.Method(modid = "Avaritia")
    public static void addAvaritia() {
        ItemId bigPearl = ItemId.create(new ItemStack(LudicrousItems.bigPearl));
        UNCONSUMED_ITEMS.add(bigPearl);
        PRIMORDIAL_PEARL_RETURN_EXCLUSIONS.add(bigPearl);
        UNCONSUMED_ITEMS.add(ItemId.create(new ItemStack(LudicrousItems.armok_orb)));
    }

    // 判断某个 TC 物品是否应从配方池中跳过（罐节点、以及特定联动模组物品）。
    // 供多个配方池共用同一份懒加载集合。
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

    // 归一化 TC 配方输入：IC2 机器替换为高炉模板、不可消耗物品清零。
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

    // 将源质列表构建为 NEI special 槽的展示 ItemStack 数组。
    public static ItemStack[] createAspectDisplayStacks(AspectList aspectList) {
        if (aspectList == null || aspectList.size() == 0) {
            return new ItemStack[0];
        }

        Aspect[] aspects = aspectList.getAspectsSortedAmount();
        ItemStack[] stacks = new ItemStack[aspects.length];

        for (int i = 0; i < aspects.length; i++) {
            stacks[i] = AspectTooltipUtils.createAspectStack(aspects[i], aspectList.getAmount(aspects[i]));
        }

        return stacks;
    }

    // 统一注册一张携带源质/研究元数据的 TC 配方（奥术合成与注魔通用）。
    // 便捷重载：时长按源质总量自动生成（奥术系数），电压 LV。
    public static void addArcaneRecipe(IRecipeMap map, ItemStack[] inputs, ItemStack[] output, AspectList aspects,
        String research, RecipeMetadataKey<AspectList> aspectKey, RecipeMetadataKey<String> researchKey) {

        addArcaneRecipe(
            map,
            inputs,
            output,
            aspects,
            research,
            aspectKey,
            researchKey,
            computeAspectDuration(aspects, ARCANE_DURATION_TICKS_PER_ASPECT),
            TierEU.RECIPE_LV);
    }

    // 根据源质总量与每点源质系数生成处理时长（tick）：基础时长 + 每点源质 × 总量。
    public static int computeAspectDuration(AspectList aspects, int ticksPerAspect) {
        int totalAmount = aspects == null ? 0 : aspects.visSize();
        return ARCANE_DURATION_BASE_TICKS + totalAmount * ticksPerAspect;
    }

    // 可自定义时长（tick）与电压（EU/t）。
    public static void addArcaneRecipe(IRecipeMap map, ItemStack[] inputs, ItemStack[] output, AspectList aspects,
        String research, RecipeMetadataKey<AspectList> aspectKey, RecipeMetadataKey<String> researchKey, int duration,
        long eut) {

        RecipeBuilder.builder()
            .ignoreCollision()
            .clearInvalid()
            .itemInputsUnified(inputs)
            .itemOutputs(output)
            .special(createAspectDisplayStacks(aspects))
            .metadata(aspectKey, aspects.copy())
            .metadata(researchKey, research)
            .duration(duration)
            .eut(eut)
            .addTo(map);
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
            this.InputAspects = inputAspects == null ? new AspectList() : inputAspects;
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
            this.InputAspects = inputAspects == null ? new AspectList() : inputAspects;
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
            this.InputAspects = inputAspects == null ? new AspectList() : inputAspects;
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
