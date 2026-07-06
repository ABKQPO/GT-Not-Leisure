package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.RCWirelessTerminalGuiObject;

import appeng.api.features.IWirelessTermHandler;
import appeng.helpers.WirelessTerminalGuiObject;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(value = WirelessTerminalGuiObject.class, remap = false)
public abstract class MixinWirelessTerminalGuiObject implements RCWirelessTerminalGuiObject {

    @Final
    @Shadow
    private int inventorySlot;

    @Unique
    private boolean gtnl$isBauble;

    @Unique
    private boolean gtnl$isSpecial;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(IWirelessTermHandler wh, ItemStack is, EntityPlayer ep, World w, int x, int y, int z,
        CallbackInfo ci) {
        if (z == Integer.MIN_VALUE) {
            gtnl$isSpecial = true;
            gtnl$isBauble = y == 1;
        } else {
            gtnl$isSpecial = false;
        }
    }

    @Shadow
    @Override
    public abstract int getInventorySlot();

    @Unique
    @Override
    public boolean isBauble() {
        return gtnl$isBauble;
    }

    @Unique
    @Override
    public boolean isSpecial() {
        return gtnl$isSpecial;
    }
}
