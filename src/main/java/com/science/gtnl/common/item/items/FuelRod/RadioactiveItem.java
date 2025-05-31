package com.science.gtnl.common.item.items.FuelRod;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;

import ic2.core.IC2Potion;

// Code From NH-Utilities
public class RadioactiveItem extends Item {

    private final int mRadio;

    public RadioactiveItem(String name, int mRadio) {
        super();
        this.mRadio = mRadio;
        this.setUnlocalizedName(name);
        this.setTextureName(name);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int slot, boolean isHeld) {
        super.onUpdate(stack, worldIn, entityIn, slot, isHeld);
        EntityLivingBase tPlayer = (EntityPlayer) entityIn;
        tPlayer.addPotionEffect(new PotionEffect(IC2Potion.radiation.id, mRadio, 4));

    }

    @Override
    public Item setTextureName(String textureName) {
        this.iconString = RESOURCE_ROOT_ID + ":" + "" + textureName;
        return this;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName();
    }

}
