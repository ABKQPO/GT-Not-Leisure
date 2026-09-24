package com.science.gtnl.common.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.science.gtnl.loader.ItemLoader;

public class EntityElainaBroom extends EntityMajoBroom {

    public static final int EFFECT_DURATION_TICKS = 30;
    public static final int EFFECT_REFRESH_THRESHOLD_TICKS = 5;

    public EntityElainaBroom(World world) {
        super(world);
    }

    @Override
    protected boolean hasUnlimitedFlight() {
        return true;
    }

    @Override
    public double getMaxHorizontalSpeed() {
        return super.getMaxHorizontalSpeed() * 2.0D;
    }

    @Override
    public ItemStack getDefaultBroomStack() {
        return new ItemStack(ItemLoader.elainaBroom);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote || !(riddenByEntity instanceof EntityPlayer player)) return;
        refreshEffect(player, Potion.resistance);
        refreshEffect(player, Potion.regeneration);
    }

    public void refreshEffect(EntityPlayer player, Potion potion) {
        PotionEffect current = player.getActivePotionEffect(potion);
        if (current != null && (current.getAmplifier() > 1
            || (current.getAmplifier() == 1 && current.getDuration() > EFFECT_REFRESH_THRESHOLD_TICKS))) return;
        player.addPotionEffect(new PotionEffect(potion.id, EFFECT_DURATION_TICKS, 1));
    }
}
