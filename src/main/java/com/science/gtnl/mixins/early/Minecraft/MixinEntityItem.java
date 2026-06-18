package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
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
        if (ticksExisted == 1) {
            if (getEntityItem() != null) {
                int meta = getEntityItem().getItemDamage();
                if (getEntityItem().getItem() instanceof PortableItem
                    && (meta == PortableItem.PortableType.OBSIDIAN.getMeta()
                        || meta == PortableItem.PortableType.NETHERITE.getMeta()
                        || meta == PortableItem.PortableType.DARKSTEEL.getMeta())) {
                    this.isImmuneToFire = true;
                    this.fireResistance = Integer.MAX_VALUE;
                } else if (isImmuneToFire) {
                    this.isImmuneToFire = false;
                    this.fireResistance = 1;
                }
            }
        }
    }

    @Redirect(
        method = "onUpdate",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;moveEntity(DDD)V"))
    private void floatLava(EntityItem instance, double mx, double my, double mz) {
        double buoyancy = 0;
        if (isImmuneToFire && this.worldObj
            .getBlock(
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posY),
                MathHelper.floor_double(this.posZ))
            .getMaterial() == Material.lava) {
            motionY += 0.04D;
            my += 0.04D;
            buoyancy = 0.3D;
            mx *= 0.5D;
            mz *= 0.5D;
        }
        moveEntity(mx, my + buoyancy, mz);
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
        if (isImmuneToFire && source.isFireDamage()) {
            cir.setReturnValue(false);
        }
        if (getEntityItem() != null) {
            int meta = getEntityItem().getItemDamage();
            if (source.isExplosion() && getEntityItem().getItem() instanceof PortableItem
                && (meta == PortableItem.PortableType.OBSIDIAN.getMeta()
                    || meta == PortableItem.PortableType.NETHERITE.getMeta()
                    || meta == PortableItem.PortableType.DARKSTEEL.getMeta())) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void onUpdateShimmerLift(CallbackInfo ci) {
        EntityItem item = (EntityItem) (Object) this;
        NBTTagCompound tag = item.getEntityData();

        if (!tag.getBoolean("ShimmerConverted")) return;

        World world = item.worldObj;
        int x = MathHelper.floor_double(item.posX);
        int y = MathHelper.floor_double(item.posY);
        int z = MathHelper.floor_double(item.posZ);

        boolean isInShimmer = false;

        if (world.blockExists(x, y, z)) {
            Block blockAtY = world.getBlock(x, y, z);
            if (blockAtY instanceof BlockShimmerFluid) {
                isInShimmer = true;
            }
        }

        if (!isInShimmer && world.blockExists(x, y - 1, z)) {
            Block blockBelow = world.getBlock(x, y - 1, z);
            if (blockBelow instanceof BlockShimmerFluid) {
                isInShimmer = true;
                y = y - 1;
            }
        }

        if (!isInShimmer) return;

        double fluidTopY = y + 1.0;
        double targetY = fluidTopY + 0.95;
        double dy = targetY - item.posY;

        if (dy > 0.05) {
            item.motionY = Math.min(0.07, dy * 0.15);
            item.motionX = 0;
            item.motionZ = 0;
            item.velocityChanged = true;
            item.fallDistance = 0f;
            item.onGround = false;
        } else {
            item.motionY = 0;
            item.motionX = 0;
            item.motionZ = 0;
            item.velocityChanged = true;
        }
    }
}
