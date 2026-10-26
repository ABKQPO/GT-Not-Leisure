package com.science.gtnl.mixins.early.superCreeper;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.supercreeper.SuperCreeperHooks;
import com.science.gtnl.config.MainConfig;

@Mixin(EntitySpider.class)
public abstract class MixinEntitySpider extends EntityMob {

    public MixinEntitySpider(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate_creeperRidingEffects(CallbackInfo ci) {
        if (this.riddenByEntity instanceof EntityCreeper) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .setBaseValue(0.8D * MainConfig.super_creeper.spiderMoveSpeed);
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .setBaseValue(0.8D);
        }
    }

    @Inject(method = "isBesideClimbableBlock", at = @At("HEAD"), cancellable = true)
    public void isBesideClimbableBlock_increasedRange(CallbackInfoReturnable<Boolean> cir) {
        if (this.riddenByEntity instanceof EntityCreeper && MainConfig.super_creeper.enableCreeperFindSpider) {
            cir.setReturnValue(SuperCreeperHooks.solidBlockAround((EntitySpider) (Object) this));
        }
    }
}
