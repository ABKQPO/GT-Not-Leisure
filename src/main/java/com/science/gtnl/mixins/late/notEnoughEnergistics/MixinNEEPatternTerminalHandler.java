package com.science.gtnl.mixins.late.notEnoughEnergistics;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.github.vfyjxf.nee.nei.NEEPatternTerminalHandler;

import gregtech.api.util.GTOreDictUnificator;

@Mixin(value = NEEPatternTerminalHandler.class, remap = false)
public abstract class MixinNEEPatternTerminalHandler {

    @Redirect(
        method = "packProcessRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;",
            remap = true,
            ordinal = 1))
    public ItemStack gtnl$replaceOutputStack(ItemStack instance) {
        int[] oreIDs = OreDictionary.getOreIDs(instance);

        for (int oreId : oreIDs) {
            String oreName = OreDictionary.getOreName(oreId);
            if (oreName == null) continue;
            if (!oreName.startsWith("ingotHot")) continue;

            String cooledOreName = "ingot" + oreName.substring("ingotHot".length());
            List<ItemStack> cooledStacks = OreDictionary.getOres(cooledOreName);
            if (cooledStacks == null || cooledStacks.isEmpty()) continue;

            ItemStack replacement = cooledStacks.get(0);
            if (replacement == null) continue;

            ItemStack result = GTOreDictUnificator.setStack(replacement.copy());
            result.stackSize = instance.stackSize;

            if (instance.stackTagCompound != null) {
                result.stackTagCompound = (NBTTagCompound) instance.stackTagCompound.copy();
            }

            return result;
        }

        return instance.copy();
    }
}
