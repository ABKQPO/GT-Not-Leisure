package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;
import java.util.Map;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.common.entity.EntityBlazeFireball;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.ToolHelper;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlazeSword extends ItemSword {

    public BlazeSword() {
        super(ToolHelper.BLAZE);
        this.setUnlocalizedName("BlazeSword");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "BlazeSword");
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setMaxDamage(7777);
        ReAvaItemList.BlazeSword.set(new ItemStack(this, 1));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(Enchantment.fireAspect.effectId, 10);
        if (!world.isRemote) {
            EntityBlazeFireball fireball = new EntityBlazeFireball(world, player);
            world.spawnEntityInWorld(fireball);
        }
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        playerIn.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 40, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_BlazeSword_00"));
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        stack.addEnchantment(Enchantment.fireAspect, 10);
    }

    public static void registerEntity() {
        EntityRegistry
            .registerModEntity(EntityBlazeFireball.class, "BlazeFireBall", 0, ModList.ReAvaritia.ID, 64, 1, true);
    }
}
