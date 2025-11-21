package com.science.gtnl.mixins.late.RandomComplement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.utils.RCAEBaseContainer;

import appeng.core.sync.GuiBridge;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.Optional;

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
        if (player.openContainer instanceof RCAEBaseContainer rca) {
            if (rca.rc$isSpecial()) {
                var item = rca.rc$isBauble() ? gtnl$getGuiObjectBaubles(player, rca.rc$getInventorySlot())
                    : player.inventory.getStackInSlot(rca.rc$getInventorySlot());
                if (item != null) {
                    return this.getGuiObject(
                        item,
                        player,
                        w,
                        rca.rc$getInventorySlot(),
                        rca.rc$isBauble() ? 1 : 0,
                        Integer.MIN_VALUE);
                }
            }
        } else if (z == Integer.MIN_VALUE) {
            var item = y == 1 ? gtnl$getGuiObjectBaubles(player, x) : player.inventory.getStackInSlot(x);
            if (item != null) {
                return this.getGuiObject(item, player, w, x, y, z);
            }
        }

        return this.getGuiObject(it, player, w, x, y, z);
    }

    @Redirect(
        method = "getClientGuiElement",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/core/sync/GuiBridge;getGuiObject(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)Ljava/lang/Object;"))
    public Object getGuiObjectC(GuiBridge instance, ItemStack it, EntityPlayer player, World w, int x, int y, int z) {
        if (player.openContainer instanceof RCAEBaseContainer rca) {
            if (rca.rc$isSpecial()) {
                var item = rca.rc$isBauble() ? gtnl$getGuiObjectBaubles(player, rca.rc$getInventorySlot())
                    : player.inventory.getStackInSlot(rca.rc$getInventorySlot());
                if (item != null) {
                    return this.getGuiObject(
                        item,
                        player,
                        w,
                        rca.rc$getInventorySlot(),
                        rca.rc$isBauble() ? 1 : 0,
                        Integer.MIN_VALUE);
                }
            }
        } else if (z == Integer.MIN_VALUE) {
            var item = y == 1 ? gtnl$getGuiObjectBaubles(player, x) : player.inventory.getStackInSlot(x);
            if (item != null) {
                return this.getGuiObject(item, player, w, x, y, z);
            }
        }

        return this.getGuiObject(it, player, w, x, y, z);
    }

    @Unique
    @Optional.Method(modid = "Baubles")
    public ItemStack gtnl$getGuiObjectBaubles(EntityPlayer player, int x) {
        return BaublesApi.getBaubles(player)
            .getStackInSlot(x);
    }

}
