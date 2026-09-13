package com.science.gtnl.mixins.early.minecraft;

import java.util.Map;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the map Minecraft keeps from tile entity class to the name it was registered with.
 * <p>
 * The map is a static field of the target, so the accessor is declared as an instance method: Mixin generates the field
 * read as a static read for it, which keeps the usual "cast the tile entity to the accessor interface" call pattern.
 */
@Mixin(value = TileEntity.class, remap = true)
public interface AccessorTileEntity {

    @Accessor("classToNameMap")
    Map<Class<?>, String> getClassToNameMap();
}
