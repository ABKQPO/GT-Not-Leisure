package com.science.gtnl.common.item.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.science.gtnl.loader.EffectLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KFCFamily extends ItemFood {

    public KFCFamily() {
        super(20, 20, true);
        this.setAlwaysEdible();
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.kfc_family";
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
        toolTip.add(StatCollector.translateToLocal("item.gtnl.kfc_family.tooltip.0"));
    }
}
