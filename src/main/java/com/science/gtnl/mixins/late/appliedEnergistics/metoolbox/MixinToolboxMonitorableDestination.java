package com.science.gtnl.mixins.late.appliedEnergistics.metoolbox;

import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.common.metoolbox.ToolboxMonitorableAdapter;

import appeng.container.implementations.ContainerMEMonitorable;
import appeng.util.InventoryAdaptor;

@Mixin(value = ContainerMEMonitorable.class, remap = false)
public abstract class MixinToolboxMonitorableDestination {

    @WrapOperation(
        method = "doMonitorableAction",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/util/InventoryAdaptor;getAdaptor(Ljava/lang/Object;Lnet/minecraftforge/common/util/ForgeDirection;)Lappeng/util/InventoryAdaptor;"))
    private InventoryAdaptor gtnl$useSessionDestination(Object target, ForgeDirection side,
        Operation<InventoryAdaptor> original) {
        if ((Object) this instanceof ToolboxMonitorableAdapter adapter && adapter.inventoryDestination() != null) {
            return adapter.inventoryDestination();
        }
        return original.call(target, side);
    }
}
