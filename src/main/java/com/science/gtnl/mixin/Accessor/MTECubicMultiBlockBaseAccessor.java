package com.science.gtnl.mixin.Accessor;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.metatileentity.implementations.MTECubicMultiBlockBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import scala.collection.immutable.List;

@Mixin(value = MTECubicMultiBlockBase.class,remap = false)
public interface MTECubicMultiBlockBaseAccessor<T extends MTECubicMultiBlockBase<T>> {

    @Invoker("onCorrectCasingAdded")
    void invokeOnCorrectCasingAdded();

    @Invoker("getAllowedHatches")
    List<IHatchElement<? super MTECubicMultiBlockBase<?>>> invokeGetAllowedHatches();

    @Invoker("getHatchTextureIndex")
    int invokeGetHatchTextureIndex();

}
