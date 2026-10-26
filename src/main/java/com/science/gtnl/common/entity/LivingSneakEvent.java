package com.science.gtnl.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import lombok.Getter;

@Getter
public class LivingSneakEvent extends LivingEvent {

    public static void onLivingSneak(EntityLivingBase entity, boolean sneaking) {
        MinecraftForge.EVENT_BUS.post(new LivingSneakEvent(entity, sneaking));
    }

    private final boolean sneaking;

    public LivingSneakEvent(EntityLivingBase entity, boolean sneaking) {
        super(entity);
        this.sneaking = sneaking;
    }

}
