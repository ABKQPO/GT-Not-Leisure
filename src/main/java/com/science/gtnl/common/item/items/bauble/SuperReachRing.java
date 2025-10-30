package com.science.gtnl.common.item.items.bauble;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.utils.enums.GTNLItemList;

import baubles.api.BaubleType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vazkii.botania.common.Botania;

public class SuperReachRing extends BaubleItem {

    public SuperReachRing() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("SuperReachRing");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "SuperReachRing");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GTNLItemList.SuperReachRing.set(new ItemStack(this, 1));
    }

    @Override
    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
        Botania.proxy.setExtraReach(player, 100F);
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        super.onUnequipped(itemstack, player);
        Botania.proxy.setExtraReach(player, -100F);
    }

    @Override
    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.RING;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.getIconString());
    }

}
