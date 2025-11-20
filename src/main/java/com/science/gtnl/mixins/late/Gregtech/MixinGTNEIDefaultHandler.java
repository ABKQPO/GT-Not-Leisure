package com.science.gtnl.mixins.late.Gregtech;

import static com.science.gtnl.utils.gui.recipe.RocketAssemblerFrontend.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.gui.recipe.RocketAssemblerFrontend;

import codechicken.nei.recipe.TemplateRecipeHandler;
import gregtech.api.util.GTRecipe;
import gregtech.nei.GTNEIDefaultHandler;

@Mixin(value = GTNEIDefaultHandler.class, remap = false)
public abstract class MixinGTNEIDefaultHandler extends TemplateRecipeHandler {

    @Unique
    private GTRecipe lastRecipe;

    /**
     * @see RocketAssemblerFrontend
     */
    @Inject(method = "drawBackground", at = @At("TAIL"), cancellable = true)
    private void drawBackground(int aRecipeIndex, CallbackInfo ci) {
        if (arecipes == null || arecipes.isEmpty()) return;
        GTNEIDefaultHandler.CachedDefaultRecipe cachedRecipe = ((GTNEIDefaultHandler.CachedDefaultRecipe) this.arecipes
            .get(aRecipeIndex));
        if (cachedRecipe == null) return;
        GTRecipe recipe = cachedRecipe.mRecipe;
        if (recipe.getRecipeCategory().recipeMap != RecipePool.RocketAssemblerRecipes) return;
        if (lastRecipe == recipe || recipe.mSpecialValue > 0) {
            lastRecipe = recipe;

            ItemStack[] inputs = recipe.mInputs;
            ItemStack output = recipe.mOutputs[0];
            if (inputs == null || output == null) return;

            int[][] selectedInputs = null;
            int[] selectedOutput = null;

            switch (recipe.mSpecialValue) {
                case 1 -> {
                    selectedInputs = rocketT1Inputs;
                    selectedOutput = rocketT1Output;
                }
                case 2 -> {
                    selectedInputs = rocketT2Inputs;
                    selectedOutput = rocketT2Output;
                }
                case 3 -> {
                    selectedInputs = rocketT3Inputs;
                    selectedOutput = rocketT3Output;
                }
                case 4 -> {
                    selectedInputs = rocketT4Inputs;
                    selectedOutput = rocketT4Output;
                }
                case 5 -> {
                    selectedInputs = rocketT5Inputs;
                    selectedOutput = rocketT5Output;
                }
                case 6 -> {
                    selectedInputs = rocketT6Inputs;
                    selectedOutput = rocketT6Output;
                }
                case 7 -> {
                    selectedInputs = rocketT7Inputs;
                    selectedOutput = rocketT7Output;
                }
                case 8 -> {
                    selectedInputs = rocketT8Inputs;
                    selectedOutput = rocketT8Output;
                }
                default -> {}
            }

            if (selectedInputs == null || selectedOutput == null) {
                return;
            }

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(ModularUITextures.ITEM_SLOT.location);

            for (int i = 0; i < inputs.length; i++) {
                Gui.func_146110_a(selectedInputs[i][0] - 1, selectedInputs[i][1] - 1, 0, 0, 18, 18, 18, 18);
            }
            Gui.func_146110_a(selectedOutput[0] - 1, selectedOutput[1] - 1, 0, 0, 18, 18, 18, 18);

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
}
