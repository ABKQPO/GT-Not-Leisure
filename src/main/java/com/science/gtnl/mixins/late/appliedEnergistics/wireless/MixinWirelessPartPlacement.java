package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.wireless.WirelessAutoConnect;

import appeng.api.parts.IPart;
import appeng.parts.CableBusContainer;

@Mixin(value = CableBusContainer.class, remap = false)
public abstract class MixinWirelessPartPlacement {

    @Shadow
    public abstract TileEntity getTile();

    @Shadow
    public abstract IPart getPart(ForgeDirection side);

    @Inject(method = "addPart", at = @At("RETURN"))
    private void gtnl$placedPart(ItemStack stack, ForgeDirection side, EntityPlayer player,
        CallbackInfoReturnable<ForgeDirection> cir) {
        ForgeDirection added = cir.getReturnValue();
        if (player != null && !player.worldObj.isRemote && added != null) {
            WirelessAutoConnect.placedPart(player, getTile(), added, getPart(added));
        }
    }
}
