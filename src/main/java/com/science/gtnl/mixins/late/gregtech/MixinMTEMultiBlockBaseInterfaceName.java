package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.api.mixinHelper.IInterfaceNameController;
import com.science.gtnl.utils.appliedEnergistics.InterfaceNameControllerContext;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.structure.error.StructureError;

@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MixinMTEMultiBlockBaseInterfaceName implements IInterfaceNameController {

    @Unique
    private long gtnl$interfaceNameStructureVersion;

    @Override
    public long getInterfaceNameStructureVersion() {
        return gtnl$interfaceNameStructureVersion;
    }

    @Inject(method = "clearHatches", at = @At("HEAD"))
    private void gtnl$invalidateInterfaceNameLinks(CallbackInfo callbackInfo) {
        gtnl$interfaceNameStructureVersion++;
    }

    @WrapOperation(
        method = { "checkStructure", "checkMachine_TT" },
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/metatileentity/implementations/MTEMultiBlockBase;checkMachine(Lgregtech/api/interfaces/tileentity/IGregTechTileEntity;Lnet/minecraft/item/ItemStack;Ljava/util/List;)V"))
    private void gtnl$trackControllerDuringStructureCheck(MTEMultiBlockBase controller, IGregTechTileEntity base,
        ItemStack stack, List<StructureError> errors, Operation<Void> original) {
        MTEMultiBlockBase previous = InterfaceNameControllerContext.enter(controller);
        try {
            original.call(controller, base, stack, errors);
        } finally {
            InterfaceNameControllerContext.restore(previous);
        }
    }

}
