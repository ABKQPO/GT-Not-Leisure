package com.science.gtnl.api.mixinHelper;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.AENetworkProxy;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;

public interface IOutputME {

    default List<IAEItemStack> getItemCache() {
        return List.of();
    }

    default List<IAEFluidStack> getFluidCache() {
        return List.of();
    }

    default long getLastOutputTick() {
        return 0;
    }

    default void setLastOutputTick(long value) {}

    default long getLastInputTick() {
        return 0;
    }

    default void setLastInputTick(long value) {}

    default long getTickCounter() {
        return 0;
    }

    default void setTickCounter(long value) {}

    default boolean isAdditionalConnection() {
        return false;
    }

    default void setAdditionalConnection(boolean value) {}

    default EntityPlayer getLastClickedPlayer() {
        return null;
    }

    default void setLastClickedPlayer(EntityPlayer player) {}

    default List<ItemStack> getLockedItems() {
        return null;
    }

    default void setLockedItems(List<ItemStack> items) {}

    default List<String> getLockedFluids() {
        return null;
    }

    default void setLockedFluids(List<String> items) {}

    default AENetworkProxy getGridProxy() {
        return null;
    }

    default void setGridProxy(AENetworkProxy proxy) {}

    default void gtnl$updateValidGridProxySides() {}

    default void gtnl$checkFluidLock() {}

    default void gtnl$flushCachedStack() {}

    default MTEHatchOutputMEBase<?, ?, ?> getOutputProvider() {
        return null;
    }
}
