package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.WirelessVisualisationPacket;
import com.science.gtnl.common.wireless.WirelessCardVisualisation;

import appeng.api.util.DimensionalCoord;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.NetworkHandler;
import appeng.items.tools.ToolNetworkVisualiser;

/** Keep AE's collector and refresh policy, including GT proxy links, for frequency-card requests. */
@Mixin(value = ToolNetworkVisualiser.class, remap = false)
public abstract class MixinWirelessVisualiserTool {

    @WrapOperation(
        method = "onUpdate",
        remap = true,
        at = @At(
            value = "INVOKE",
            target = "Lappeng/items/tools/ToolNetworkVisualiser;needToUpdate(Lnet/minecraft/entity/player/EntityPlayerMP;Lappeng/api/util/DimensionalCoord;)Z",
            remap = false))
    private boolean gtnl$refreshCadence(EntityPlayerMP player, DimensionalCoord position, Operation<Boolean> original,
        ItemStack stack, World world, Entity entity, int slot, boolean active) {
        return WirelessCardVisualisation.delegated(stack) ? WirelessCardVisualisation.needsUpdate(player, stack)
            : original.call(player, position);
    }

    @Redirect(
        method = "onUpdate",
        remap = true,
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/util/DimensionalCoord;isInWorld(Lnet/minecraft/world/World;)Z",
            remap = false))
    private boolean gtnl$viewerDimension(DimensionalCoord position, World original, ItemStack stack, World world,
        Entity entity, int slot, boolean active) {
        return position.isInWorld(WirelessCardVisualisation.delegated(stack) ? entity.worldObj : original);
    }

    @Redirect(
        method = "onUpdate",
        remap = true,
        at = @At(
            value = "INVOKE",
            target = "Lappeng/core/sync/network/NetworkHandler;sendTo(Lappeng/core/sync/AppEngPacket;Lnet/minecraft/entity/player/EntityPlayerMP;)V",
            remap = false))
    private void gtnl$cardSnapshot(NetworkHandler network, AppEngPacket packet, EntityPlayerMP player, ItemStack stack,
        World world, Entity entity, int slot, boolean active) {
        if (WirelessCardVisualisation.delegated(stack)) {
            ScienceNotLeisure.network
                .sendTo(new WirelessVisualisationPacket(stack.getTagCompound(), player.dimension, packet), player);
        } else network.sendTo(packet, player);
    }
}
