package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.common.block.blocks.BlockShimmerFluid;
import com.science.gtnl.common.item.ItemEntityHooks;
import com.science.gtnl.common.item.items.PortableItem;

@Mixin(value = EntityItem.class, remap = true)
public abstract class MixinEntityItem extends Entity {

    @Shadow
    public abstract ItemStack getEntityItem();

    public MixinEntityItem(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"))
    private void applyFireImmunity(CallbackInfo ci) {
        if (ticksExisted != 1 || getEntityItem() == null) return;
        if (PortableItem.isFireproof(getEntityItem())) {
            this.isImmuneToFire = true;
            this.fireResistance = Integer.MAX_VALUE;
        } else if (isImmuneToFire) {
            this.isImmuneToFire = false;
            this.fireResistance = 1;
        }
    }

    @Redirect(
        method = "onUpdate",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;moveEntity(DDD)V"))
    private void floatLava(EntityItem instance, double mx, double my, double mz) {
        ItemEntityHooks.moveFloatingOnLava(instance, mx, my, mz);
    }

    @WrapOperation(
        method = "onUpdate",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block noFizzBounce(World instance, int x, int y, int z, Operation<Block> original) {
        return isImmuneToFire ? Blocks.air : original.call(instance, x, y, z);
    }

    @Override
    public boolean isBurning() {
        return !isImmuneToFire && super.isBurning();
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "HEAD"), cancellable = true)
    public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (isImmuneToFire && source.isFireDamage()
            || source.isExplosion() && PortableItem.isExplosionProof(getEntityItem())) cir.setReturnValue(false);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void onUpdateShimmerLift(CallbackInfo ci) {
        BlockShimmerFluid.liftConvertedItem((EntityItem) (Object) this);
    }
}
