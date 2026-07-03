package com.science.gtnl.mixins.late.visualProspecting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

@Mixin(value = VeinTypeCaching.class, remap = false)
public interface AccessorVeinTypeCaching {

    @Accessor("veinTypes")
    static Object2ObjectMap<String, VeinType> getVeinTypes() {
        throw new AssertionError();
    }
}
