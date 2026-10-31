package com.science.gtnl.mixins.late.overpowered;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.compat.overpowered.RecipeOutputChance;

import gregtech.api.items.MetaBaseItem;
import gregtech.common.items.behaviors.BehaviourScanner;

@Mixin(value = BehaviourScanner.class, remap = false)
public class MixinBehaviourScanner {

    @Inject(
        method = "onItemUseFirst(Lgregtech/api/items/MetaBaseItem;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIILnet/minecraftforge/common/util/ForgeDirection;FFF)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/NBTTagCompound;setInteger(Ljava/lang/String;I)V",
            shift = At.Shift.BEFORE))
    private void GTNotLeisure$addScanInfo(MetaBaseItem aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld,
        int aX, int aY, int aZ, ForgeDirection side, float hitX, float hitY, float hitZ,
        CallbackInfoReturnable<Boolean> cir, @Local List<String> list) {
        if (!RecipeOutputChance.enabled()) return;
        RecipeOutputChance.Bonus bonus = RecipeOutputChance.bonusOf(aWorld.getTileEntity(aX, aY, aZ));
        if (bonus != null) list.add(RecipeOutputChance.describe(bonus));
    }
}
