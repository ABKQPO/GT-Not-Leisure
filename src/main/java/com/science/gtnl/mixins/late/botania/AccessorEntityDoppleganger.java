package com.science.gtnl.mixins.late.botania;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import vazkii.botania.common.entity.EntityDoppleganger;

@Mixin(value = EntityDoppleganger.class, remap = false)
public interface AccessorEntityDoppleganger {

    @Accessor("playersWhoAttacked")
    List<String> getPlayersWhoAttacked();
}
