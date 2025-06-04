package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.basicMachine.ManaTank;

import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.storage.MTEDigitalTankBase;

@SuppressWarnings("UnusedMixin")
@Mixin(value = MTEDigitalTankBase.class, remap = false)
public abstract class MTEDigitalTankBase_Mixin {

    @Inject(method = "onScrewdriverRightClick", at = @At("HEAD"), cancellable = true)
    private void onScrewdriverRightClickInject(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool, CallbackInfo ci) {
        Object self = this;

        if (self instanceof ManaTank tank) {
            tank.isLiquidizerMode = !tank.isLiquidizerMode;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal("Mode_ManaTank_0" + (tank.isLiquidizerMode ? 0 : 1)));
            ci.cancel();
        }
    }
}
