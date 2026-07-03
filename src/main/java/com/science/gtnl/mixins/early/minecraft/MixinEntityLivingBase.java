package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import com.science.gtnl.utils.event.LivingSneakEvent;

@Mixin(value = EntityLivingBase.class, remap = true)
public abstract class MixinEntityLivingBase extends Entity {

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Override
    public void setSneaking(boolean sneaking) {
        super.setSneaking(sneaking);
        LivingSneakEvent.onLivingSneak((EntityLivingBase) (Object) this, sneaking);
    }
}
