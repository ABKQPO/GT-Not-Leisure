package com.science.gtnl.mixins.early.Stick;

import static codechicken.nei.recipe.GuiUsageRecipe.*;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.item.items.Stick;

import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.IUsageHandler;

@Mixin(value = GuiUsageRecipe.class, remap = false)
public abstract class MixinGuiUsageRecipe extends GuiRecipe<IUsageHandler> {

    public MixinGuiUsageRecipe(GuiScreen prevgui) {
        super(prevgui);
    }

    @Inject(
        method = "openRecipeGui",
        at = @At(
            value = "INVOKE",
            target = "Lcodechicken/nei/recipe/GuiUsageRecipe;getUsageHandlers(Ljava/lang/String;[Ljava/lang/Object;)Ljava/util/ArrayList;",
            shift = At.Shift.BEFORE))
    private static void afterNormalizeItemStack(String inputId, Object[] ingredients,
        CallbackInfoReturnable<Boolean> cir) {
        for (int i = 0; i < ingredients.length; i++) {
            Object ingredient = ingredients[i];
            if (ingredient instanceof ItemStack itemStack && itemStack.getItem() instanceof Stick) {
                ItemStack fakeItem = Stick.getDisguisedStack(itemStack);
                if (fakeItem != null) {
                    ingredients[i] = fakeItem;
                }
            }
        }
    }
}
