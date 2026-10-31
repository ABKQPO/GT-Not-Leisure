package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.api.mixinHelper.RCAEBaseContainer;
import com.science.gtnl.compat.baubles.BaublesInventory;

import appeng.core.sync.GuiBridge;

@Mixin(value = GuiBridge.class, remap = false)
public abstract class MixinGuiBridge {

    @Shadow
    protected abstract Object getGuiObject(ItemStack it, EntityPlayer player, World w, int x, int y, int z);

    @Redirect(
        method = "getServerGuiElement",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/core/sync/GuiBridge;getGuiObject(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)Ljava/lang/Object;"))
    public Object getGuiObjectS(GuiBridge instance, ItemStack it, EntityPlayer player, World w, int x, int y, int z) {
        return gtnl$guiObject(it, player, w, x, y, z);
    }

    @Redirect(
        method = "getClientGuiElement",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/core/sync/GuiBridge;getGuiObject(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)Ljava/lang/Object;"))
    public Object getGuiObjectC(GuiBridge instance, ItemStack it, EntityPlayer player, World w, int x, int y, int z) {
        return gtnl$guiObject(it, player, w, x, y, z);
    }

    // A wireless terminal opened from a slot: y is 1 for a Baubles slot, and z is Integer.MIN_VALUE.
    @Unique
    private Object gtnl$guiObject(ItemStack it, EntityPlayer player, World w, int x, int y, int z) {
        boolean rcContainer = player.openContainer instanceof RCAEBaseContainer;
        if (player.openContainer instanceof RCAEBaseContainer rca && rca.rc$isSpecial()) {
            int slot = rca.rc$getInventorySlot(), inBaubles = rca.rc$isBauble() ? 1 : 0;
            ItemStack item = gtnl$terminal(player, slot, inBaubles);
            if (item != null) return this.getGuiObject(item, player, w, slot, inBaubles, Integer.MIN_VALUE);
        } else if (!rcContainer && z == Integer.MIN_VALUE) {
            ItemStack item = gtnl$terminal(player, x, y);
            if (item != null) return this.getGuiObject(item, player, w, x, y, z);
        }
        return this.getGuiObject(it, player, w, x, y, z);
    }

    @Unique
    private static ItemStack gtnl$terminal(EntityPlayer player, int slot, int inBaubles) {
        if (inBaubles != 1) return player.inventory.getStackInSlot(slot);
        IInventory baubles = BaublesInventory.of(player);
        return baubles == null ? null : baubles.getStackInSlot(slot);
    }
}
