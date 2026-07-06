package com.science.gtnl.mixins.early.superCreeper;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.config.MainConfig;

@Mixin(EntityCreeper.class)
public abstract class MixinEntityCreeper extends EntityMob {

    public MixinEntityCreeper(World p_i1733_1_) {
        super(p_i1733_1_);
    }

    @Shadow
    public void func_146077_cc() {}

    @Shadow
    public abstract boolean getPowered();

    @Shadow
    public int explosionRadius;

    @Unique
    private int gtnl$delayedExplosionTimer = 0;
    @Unique
    private boolean gtnl$isCreeperExplosionDelayed = false;
    @Unique
    private int gtnl$burningExplosionTimer = 0;
    @Shadow
    public int timeSinceIgnited;
    @Shadow
    public int fuseTime;

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeCustomData(NBTTagCompound tag, CallbackInfo ci) {
        tag.setBoolean("creeperExplosionDelayed", this.gtnl$isCreeperExplosionDelayed);
        tag.setInteger("creeperExplosionDelay", this.gtnl$delayedExplosionTimer);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readCustomData(NBTTagCompound tagCompund, CallbackInfo ci) {
        this.gtnl$isCreeperExplosionDelayed = tagCompund.getBoolean("creeperExplosionDelayed");
        this.gtnl$delayedExplosionTimer = tagCompund.getInteger("creeperExplosionDelay");
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if (source.isExplosion() && source.getSourceOfDamage() instanceof EntityCreeper
            && MainConfig.super_creeper.enableCreeperKilledByCreeperExplosion) {
            this.func_146077_cc();
        }

        if (this.timeSinceIgnited > 0 && this.timeSinceIgnited < this.fuseTime
            && MainConfig.super_creeper.enableCreeperIgnitedDeathExplosion) {
            this.func_146077_cc();
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void onUpdate(CallbackInfo ci) {
        if (gtnl$isCreeperExplosionDelayed && MainConfig.super_creeper.enableCreeperHurtByCreeperExplosion) {
            gtnl$delayedExplosionTimer--;
            if (gtnl$delayedExplosionTimer <= 0) {
                this.func_146077_cc();
                this.setDead();
                gtnl$isCreeperExplosionDelayed = false;
            }
        }

        if (this.isBurning() && MainConfig.super_creeper.enableCreeperBurningExplosion) {
            gtnl$burningExplosionTimer++;
            if (gtnl$burningExplosionTimer >= MainConfig.super_creeper.burningExplosionTimer) {
                this.func_146077_cc();
                this.extinguish();
                gtnl$burningExplosionTimer = 0;
            }
        } else {
            gtnl$burningExplosionTimer = 0;
        }
    }

    @Inject(
        method = "func_146077_cc",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/Explosion;",
            shift = At.Shift.BEFORE))
    private void preCreateExplosion(CallbackInfo ci) {
        if (!this.worldObj.isRemote && MainConfig.super_creeper.allowCreeperExplosionBypassGamerule) {
            if (this.getPowered()) {
                this.worldObj
                    .createExplosion(this, this.posX, this.posY, this.posZ, (float) (this.explosionRadius * 2), true);
            } else {
                this.worldObj
                    .createExplosion(this, this.posX, this.posY, this.posZ, (float) this.explosionRadius, true);
            }
        }
    }
}
