package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KFCFamily extends ItemFood {

    public KFCFamily(int hunger, float saturation, boolean isWolfFood) {
        super(hunger, saturation, isWolfFood);
        this.setUnlocalizedName("KFCFamily");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "KFCFamily");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setAlwaysEdible();
        GTNLItemList.KFCFamily.set(new ItemStack(this, 1));
    }

    @Override
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(EffectLoader.perfect_physique.getId(), 86400 * 20, 1));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_KFCFamily_00"));
    }
}
