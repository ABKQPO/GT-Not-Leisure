package com.science.gtnl.mixins.late.overpowered;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.compat.overpowered.RecipeOutputChance;

import gregtech.crossmod.waila.GregtechTEWailaDataProvider;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = GregtechTEWailaDataProvider.class, remap = false)
public class MixinGregtechWailaDataProvider {

    @Inject(
        method = "getNBTData(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/world/World;III)Lnet/minecraft/nbt/NBTTagCompound;",
        at = @At("RETURN"),
        cancellable = true)
    private void injectGetNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (!RecipeOutputChance.enabled()) return;
        RecipeOutputChance.Bonus bonus = RecipeOutputChance.bonusOf(tile);
        if (bonus != null) {
            RecipeOutputChance.writeWaila(tag, bonus);
            cir.setReturnValue(tag);
        }
    }

    @Inject(method = "getWailaBody", at = @At("RETURN"), cancellable = true)
    private void injectGetWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config, CallbackInfoReturnable<List<String>> cir) {
        if (!RecipeOutputChance.enabled()) return;
        RecipeOutputChance.Bonus bonus = RecipeOutputChance.readWaila(accessor.getNBTData());
        if (bonus != null) {
            currenttip.add(RecipeOutputChance.describe(bonus));
            cir.setReturnValue(currenttip);
        }
    }

}
