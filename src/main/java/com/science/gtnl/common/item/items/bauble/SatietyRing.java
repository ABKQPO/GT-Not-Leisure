package com.science.gtnl.common.item.items.bauble;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.utils.enums.GTNLItemList;

import baubles.api.BaubleType;

public class SatietyRing extends BaubleItem {

    public SatietyRing() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("SatietyRing");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "SatietyRing");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GTNLItemList.SatietyRing.set(new ItemStack(this, 1));
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        super.onWornTick(itemstack, player);
        player.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, 200, 255));
        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 1200, 255));

        if (player instanceof EntityPlayer entityPlayer) {
            entityPlayer.getFoodStats()
                .addStats(20, 20.0F);
        }
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        super.onUnequipped(itemstack, player);
        player.removePotionEffect(Potion.field_76443_y.id);
    }
}
