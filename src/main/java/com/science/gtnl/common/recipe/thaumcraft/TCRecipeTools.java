package com.science.gtnl.common.recipe.thaumcraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

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

        public int getAspectAmount(Aspect aspect) {
            return aspect == null ? 0 : InputAspects.getAmount(aspect);
        }

        public int getAspectAmount() {
            int total = 0;

            for (Aspect aspect : InputAspects.getAspects()) {
                if (aspect != null) {
                    total += InputAspects.getAmount(aspect);
                }
            }

            return total;
        }
    }
}
