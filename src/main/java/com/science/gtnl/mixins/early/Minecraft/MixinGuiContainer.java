package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.api.mixinHelper.IInfinityChestGui;

@Mixin(value = GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {

    @ModifyArg(
        method = "drawItemStack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"),
        index = 5)
    public String modifyAltText(String original, @Local(name = "stack") ItemStack stack) {
        if (!(this instanceof IInfinityChestGui)) return original;
        if (original != null && !original.isEmpty()) return original;
        if (stack.stackSize == 0) {
            return EnumChatFormatting.YELLOW + "0";
        }

        return humanReadableValue(stack.stackSize);
    }

    @Unique
    private static String humanReadableValue(final int value) {
        if (value > 0 && value < 1000) return Integer.toString(value);
        else if (value >= 1000 && value < 1000000) return value / 1000 + "K";
        else if (value >= 1000000 && value <= 1000000000) return value / 1000000 + "M";
        else if (value >= 1000000000) return value / 1000000000 + "G";
        else return null;
    }

}
