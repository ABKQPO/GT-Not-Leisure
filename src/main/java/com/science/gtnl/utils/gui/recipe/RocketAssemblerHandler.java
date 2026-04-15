package com.science.gtnl.utils.gui.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.nei.NEIGTNLConfig;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import gregtech.api.recipe.RecipeCategory;
import gregtech.api.util.GTRecipe;
import gregtech.nei.GTNEIDefaultHandler;

public class RocketAssemblerHandler extends GTNEIDefaultHandler {

    public RocketAssemblerHandler(RecipeCategory recipeCategory) {
        super(recipeCategory);
        if (NEIGTNLConfig.isAdded) return;
        FMLInterModComms.sendRuntimeMessage(
            ScienceNotLeisure.instance,
            "NEIPlugins",
            "register-crafting-handler",
            "gregtech@" + this.getRecipeName() + "@" + this.getOverlayIdentifier());
        GuiCraftingRecipe.craftinghandlers.add(this);
        GuiUsageRecipe.usagehandlers.add(this);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new RocketAssemblerHandler(this.recipeCategory);
    }

    @Override
    public void drawBackground(int aRecipeIndex) {
        super.drawBackground(aRecipeIndex);
        if (arecipes == null || arecipes.isEmpty()) return;
        GTRecipe recipe = ((CachedDefaultRecipe) this.arecipes.get(aRecipeIndex)).mRecipe;
        if (recipe.mSpecialValue <= 0) return;
        ItemStack[] inputs = recipe.mInputs;
        ItemStack output = recipe.mOutputs[0];
        if (inputs == null || output == null) return;

        int[][] selectedInputs = null;
        int[] selectedOutput = null;

        switch (recipe.mSpecialValue) {
            case 1 -> {
                selectedInputs = RocketAssemblerFrontend.rocketT1Inputs;
                selectedOutput = RocketAssemblerFrontend.rocketT1Output;
            }
            case 2 -> {
                selectedInputs = RocketAssemblerFrontend.rocketT2Inputs;
                selectedOutput = RocketAssemblerFrontend.rocketT2Output;
            }
            case 3 -> {
                selectedInputs = RocketAssemblerFrontend.rocketT3Inputs;
                selectedOutput = RocketAssemblerFrontend.rocketT3Output;
            }
            case 4 -> {
                selectedInputs = RocketAssemblerFrontend.rocketT4Inputs;
                selectedOutput = RocketAssemblerFrontend.rocketT4Output;
            }
            case 5 -> {
                selectedInputs = RocketAssemblerFrontend.rocketT5Inputs;
                selectedOutput = RocketAssemblerFrontend.rocketT5Output;
            }
            case 6 -> {
                selectedInputs = RocketAssemblerFrontend.rocketT6Inputs;
                selectedOutput = RocketAssemblerFrontend.rocketT6Output;
            }
            case 7 -> {
                selectedInputs = RocketAssemblerFrontend.rocketT7Inputs;
                selectedOutput = RocketAssemblerFrontend.rocketT7Output;
            }
            case 8 -> {
                selectedInputs = RocketAssemblerFrontend.rocketT8Inputs;
                selectedOutput = RocketAssemblerFrontend.rocketT8Output;
            }
            default -> {}
        }

        if (selectedInputs == null) {
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
