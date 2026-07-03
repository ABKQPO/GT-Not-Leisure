package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.RCAEBaseContainer;
import com.science.gtnl.utils.RCWirelessTerminalGuiObject;

import appeng.container.AEBaseContainer;

@Mixin(value = AEBaseContainer.class, remap = false)
public abstract class MixinAEBaseContainer extends Container implements RCAEBaseContainer {

    @Unique
    private int gtnl$slot;

    @Unique
    private boolean gtnl$isBauble;

    @Unique
    private boolean gtnl$isSpecial;

    @Unique
    private Container gtnl$oldContainer;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Ljava/lang/Object;)V", at = @At("TAIL"))
    public void onInit(InventoryPlayer ip, Object anchor, CallbackInfo ci) {
        if (anchor instanceof RCWirelessTerminalGuiObject w) {
            gtnl$slot = w.getInventorySlot();
            gtnl$isBauble = w.isBauble();
            gtnl$isSpecial = w.isSpecial();
        }
    }

    @Unique
    @Override
    public int rc$getInventorySlot() {
        return gtnl$slot;
    }

    @Unique
    @Override
    public boolean rc$isBauble() {
        return gtnl$isBauble;
    }

    @Unique
    @Override
    public boolean rc$isSpecial() {
        return gtnl$isSpecial;
    }

    @Unique
    @Override
    public void rc$setOldContainer(Container old) {
        gtnl$oldContainer = old;
    }

    @Unique
    @Override
    public Container rc$getOldContainer() {
        return gtnl$oldContainer;
    }
}
