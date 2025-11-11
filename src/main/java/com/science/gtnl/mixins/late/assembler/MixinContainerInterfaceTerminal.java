package com.science.gtnl.mixins.late.assembler;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;

import appeng.container.implementations.ContainerInterfaceTerminal;
import appeng.items.misc.ItemEncodedPattern;

@Mixin(value = ContainerInterfaceTerminal.class, remap = false)
public class MixinContainerInterfaceTerminal {

    @Shadow
    @Final
    private Map<Long, AccessorInvTracker> trackedById;

    @WrapOperation(
        method = "doAction",
        at = @At(value = "CONSTANT", args = "classValue=appeng/items/misc/ItemEncodedPattern"))
    public boolean doAction(Object object, Operation<Boolean> original, @Local(name = "handStack") ItemStack handStack,
        @Local(name = "player") EntityPlayerMP player, @Local(name = "id") long id) {
        if (original.call(object)) {
            var inv = this.trackedById.get(id);
            if (inv.getPatterns() instanceof AssemblerMatrix.CombinationPatternsIInventory) {
                var i = (ItemEncodedPattern) object;
                var d = i.getPatternForItem(handStack, player.worldObj);
                return d.isCraftable();
            }
            return true;
        }
        return false;
    }
}
