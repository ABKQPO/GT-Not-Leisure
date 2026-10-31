package com.science.gtnl.mixins.early.forge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.block.BlockEntityTagPlacement;

@Mixin(value = ForgeHooks.class, remap = false)
public class MixinForgeHook {

    @Inject(method = "onPlaceItemIntoWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preOnPlaceItemIntoWorldRewrite(ItemStack itemstack, EntityPlayer player, World world, int x,
        int y, int z, int side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        Boolean placed = BlockEntityTagPlacement
            .placeItemIntoWorld(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);
        if (placed != null) cir.setReturnValue(placed);
    }
}
