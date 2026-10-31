package com.science.gtnl.compat.nei;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.gui.GuiDirePatternEncoder;
import com.science.gtnl.common.packet.DirePatternHandler;

import appeng.container.slot.SlotFakeCraftingMatrix;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;

public class DirePatternOverlayHandler implements IOverlayHandler {

    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean maxTransfer) {
        if (firstGui instanceof GuiDirePatternEncoder g) {
            var p = getInputInv(firstGui, recipe.getIngredientStacks(recipeIndex));
            ScienceNotLeisure.network.sendToServer(new DirePatternHandler((byte) 2, p));
        }
    }

    public NBTTagCompound getInputInv(GuiContainer gui, List<PositionedStack> ingredientStacks) {
        var recipe = new NBTTagCompound();
        for (var stack : ingredientStacks) {
            if (stack == null || stack.item == null) continue;
            final int col = (stack.relx - 3) / 18;
            final int row = (stack.rely - 3) / 18;
            for (final Slot slot : gui.inventorySlots.inventorySlots) {
                if (slot instanceof SlotFakeCraftingMatrix) {
                    if (slot.getSlotIndex() == col + row * 9) {
                        var n = new NBTTagCompound();
                        stack.item.writeToNBT(n);
                        recipe.setTag(Integer.toString(slot.getSlotIndex()), n);
                        break;
                    }
                }
            }
        }
        return recipe;
    }

}
