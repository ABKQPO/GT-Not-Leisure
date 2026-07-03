package com.science.gtnl.mixins.late.draconicEvolution;

import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.brandon3055.draconicevolution.common.items.armor.CustomArmorHandler;

@Mixin(value = CustomArmorHandler.class, remap = false)
public interface AccessorCustomArmorHandler {

    @Accessor("ADMIN_KILL")
    static DamageSource getAdminKill() {
        throw new AssertionError();
    }
}
