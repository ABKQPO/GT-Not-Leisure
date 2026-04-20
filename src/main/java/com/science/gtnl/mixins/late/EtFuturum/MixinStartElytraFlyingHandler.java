package com.science.gtnl.mixins.late.EtFuturum;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.reavaritia.common.items.InfinityElytra;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;
import ganymedes01.etfuturum.network.StartElytraFlyingHandler;
import ganymedes01.etfuturum.network.StartElytraFlyingMessage;

@Mixin(value = StartElytraFlyingHandler.class, remap = false)
public class MixinStartElytraFlyingHandler {

    @Inject(
        method = "onMessage(Lganymedes01/etfuturum/network/StartElytraFlyingMessage;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
        at = @At("HEAD"),
        cancellable = true)
    private void onHandleInfinityElytra(StartElytraFlyingMessage message, MessageContext ctx,
        CallbackInfoReturnable<IMessage> cir) {
        EntityPlayer player = ((NetHandlerPlayServer) ctx.netHandler).playerEntity;
        if (!player.onGround && !((IElytraPlayer) player).etfu$isElytraFlying() && !player.isInWater()) {
            ItemStack itemstack = ItemArmorElytra.getElytra(player);
            if (itemstack != null && itemstack.getItem() instanceof InfinityElytra) {
                ((IElytraPlayer) player).etfu$setElytraFlying(true);
                cir.setReturnValue(null);
            }
        }
    }
}
