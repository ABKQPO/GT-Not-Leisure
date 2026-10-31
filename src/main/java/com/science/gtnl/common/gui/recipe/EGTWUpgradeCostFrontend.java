package com.science.gtnl.common.gui.recipe;

import static gregtech.api.util.GTRecipeConstants.FOG_UPGRADE_NAME_SHORT;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EGTWUpgradeCostFrontend extends GTNLRecipeFrontend {

    public EGTWUpgradeCostFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    protected Slot logoSlot() {
        return new Slot(151, 100);
    }

    @Override
    protected List<Slot> itemInputSlots(int itemInputCount) {
        return grid(itemInputCount, 8, 8, 5);
    }

    @Override
    protected List<Slot> itemOutputSlots(int itemOutputCount) {
        return grid(itemOutputCount, 134, 8, 1);
    }

    @Override
    public void drawDurationInfo(RecipeDisplayInfo recipeInfo) {}

    @Override
    public void drawEnergyInfo(RecipeDisplayInfo recipeInfo) {}

    @Override
    public void drawSpecialInfo(RecipeDisplayInfo recipeInfo) {
        String upgradeName = recipeInfo.recipe.getMetadataOrDefault(FOG_UPGRADE_NAME_SHORT, "");
        int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(upgradeName);
        if (width % 2 == 1) width -= 1;
        int xOffset = 18 - width / 2 - 1;
        recipeInfo.drawText(
            EnumChatFormatting.BLUE.toString() + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + upgradeName,
            119 + xOffset,
            0);
    }
}
