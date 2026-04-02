package com.science.gtnl.utils.gui.recipe;

import static gregtech.api.util.GTRecipeConstants.FOG_UPGRADE_NAME_SHORT;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EGTWUpgradeCostFrontend extends RecipeMapFrontend {

    public EGTWUpgradeCostFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(new Pos2d(151, 100).add(windowOffset)));
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return UIHelper.getGridPositions(itemInputCount, 8, 8, 5);
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return UIHelper.getGridPositions(itemOutputCount, 134, 8, 1);
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
