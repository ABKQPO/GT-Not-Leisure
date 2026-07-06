package com.science.gtnl.mixins.early.superCreeper;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            int blockX = MathHelper.floor_double(this.posX);
            int blockY = MathHelper.floor_double(this.boundingBox.minY);
            int blockZ = MathHelper.floor_double(this.posZ);

            for (int xOffset = -2; xOffset <= 2; ++xOffset) {
                for (int zOffset = -2; zOffset <= 2; ++zOffset) {
                    if (xOffset == 0 && zOffset == 0) continue;
                    Block block = this.worldObj.getBlock(blockX + xOffset, blockY, blockZ + zOffset);

                    if (block != null && block.getMaterial()
                        .isSolid()
                        && block.getMaterial()
                            .isOpaque()) {
                        cir.setReturnValue(true);
                        cir.cancel();
                        return;
                    }
                }
            }
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
