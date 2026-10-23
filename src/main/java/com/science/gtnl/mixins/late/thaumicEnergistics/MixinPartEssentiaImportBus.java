package com.science.gtnl.mixins.late.thaumicEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.common.block.blocks.tile.IMultiEssentiaContainer;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumicenergistics.common.integration.tc.EssentiaTileContainerHelper;
import thaumicenergistics.common.parts.PartEssentiaImportBus;
import thaumicenergistics.common.storage.AEEssentiaStack;

@Mixin(value = PartEssentiaImportBus.class, remap = false)
public class MixinPartEssentiaImportBus {

    @WrapOperation(
        method = "importStuff",
        at = @At(
            value = "INVOKE",
            target = "Lthaumicenergistics/common/integration/tc/EssentiaTileContainerHelper;getAspectInContainer(Lthaumcraft/api/aspects/IAspectContainer;)Lthaumcraft/api/aspects/Aspect;"))
    private Aspect gtnl$selectFilteredAspect(EssentiaTileContainerHelper helper, IAspectContainer container,
        Operation<Aspect> original, @Local(argsOnly = true) AEEssentiaStack whatToImport) {
        if (container instanceof IMultiEssentiaContainer && whatToImport != null
            && container.containerContains(whatToImport.getAspect()) > 0) {
            return whatToImport.getAspect();
        }
        return original.call(helper, container);
    }
}
