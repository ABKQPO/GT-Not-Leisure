package com.science.gtnl.mixins.late.appliedEnergistics;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerUpgradeable;

@Mixin(value = ContainerUpgradeable.class, remap = false)
public abstract class MixinContainerUpgradeable extends AEBaseContainer {

    public MixinContainerUpgradeable(InventoryPlayer ip, Object anchor) {
        super(ip, anchor);
    }

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/implementations/guiobjects/IGuiItem;getGuiObject(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;III)Lappeng/api/implementations/guiobjects/IGuiItemObject;"),
        index = 1)
    private World gtnl$usePlayerWorldForItemGui(World world) {
        if (world != null) {
            return world;
        }

        InventoryPlayer playerInventory = this.getPlayerInv();
        return playerInventory == null || playerInventory.player == null ? null : playerInventory.player.worldObj;
    }
}
