package com.science.gtnl.mixins.late.gregtech;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.recipes.ReversedRecipeRegistry;

import gregtech.api.util.GTShapedRecipe;
import gregtech.common.blocks.ItemMachines;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Mixin(value = GTShapedRecipe.class, remap = false)
public class MixinGTShapedRecipe {

    @Inject(
        method = "<init>(Lnet/minecraft/item/ItemStack;ZZ[Lnet/minecraft/enchantment/Enchantment;[I[Ljava/lang/Object;)V",
        at = @At("RETURN"))
    private void init(ItemStack aResult, boolean aRemovableByGT, boolean aKeepingNBT, Enchantment[] aEnchantmentsAdded,
        int[] aEnchantmentLevelsAdded, Object[] aRecipe, CallbackInfo ci) {
        if (aResult.getItem() instanceof ItemMachines) {
            ObjectArrayList<Object> filteredRecipe = new ObjectArrayList<>();
            for (Object obj : aRecipe) {
                if (obj instanceof String string && string.startsWith("craftingTool")) {
                    continue;
                }
                filteredRecipe.add(obj);
            }
            ReversedRecipeRegistry.registerShaped(aResult, filteredRecipe.toArray());
        }
    }

}
