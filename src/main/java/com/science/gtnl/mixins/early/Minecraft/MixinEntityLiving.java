package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.common.entity.EntityPlayerLeashKnot;

@Mixin(value = EntityLiving.class, remap = true)
public abstract class MixinEntityLiving extends EntityLivingBase {

    @Shadow
    private Entity leashedToEntity;

    public MixinEntityLiving(World p_i1594_1_) {
        super(p_i1594_1_);
    }

    @Redirect(
        method = "clearLeashed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLiving;dropItem(Lnet/minecraft/item/Item;I)Lnet/minecraft/entity/item/EntityItem;"))
    private EntityItem cancelLeadDropIfPlayerLeash(EntityLiving self, Item item, int amount) {
        if (this.leashedToEntity instanceof EntityPlayerLeashKnot) {
            return null;
        } else {
            return self.dropItem(item, amount);
        }
    }
}
