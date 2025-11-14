package com.science.gtnl.mixins.late.AppliedEnergistics;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.inventory.IInventory;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.ISkipStackSizeCheck;

import appeng.util.inv.AdaptorIInventory;
import appeng.util.inv.WrapperMCISidedInventory;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

@Mixin(value = AdaptorIInventory.class, remap = false)
public class MixinAdaptorIInventory {

    @Final
    @Shadow
    private IInventory i;
    @Final
    @Shadow
    private boolean wrapperEnabled;
    @Shadow
    private boolean skipStackSizeCheck;

    @Unique
    private static Function<Object, Object> get;

    private static Function<Object, Object> init() {
        if (get == null) try {
            Field f = WrapperMCISidedInventory.class.getDeclaredField("side");
            f.setAccessible(true);
            get = s -> {
                try {
                    return f.get(s);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return get;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"), require = 1)
    public void constructor(IInventory s, CallbackInfo a) {

        if (wrapperEnabled) {
            if (i instanceof WrapperMCISidedInventory) {
                WrapperMCISidedInventory wrap = (WrapperMCISidedInventory) i;
                Object wrapped = init().apply(wrap);
                skipStackSizeCheck = skipStackSizeCheck || check(wrapped);
                return;
            }

        }

        skipStackSizeCheck = skipStackSizeCheck || check(s);
    }

    private static boolean check(Object s) {
        if (s != null && s instanceof IGregTechTileEntity) {
            return Optional.ofNullable(((IGregTechTileEntity) s).getMetaTileEntity())
                .map(ss -> ss instanceof ISkipStackSizeCheck ? (ISkipStackSizeCheck) ss : null)
                .isPresent();
        }
        return false;
    }

}
