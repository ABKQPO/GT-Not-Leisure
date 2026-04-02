package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalAxe extends ItemAxe {

    public CrystalAxe() {
        super(ToolHelper.CRYSTAL);
        this.setUnlocalizedName("CrystalAxe");
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "CrystalAxe");
        this.setMaxDamage(8888);
        ReAvaItemList.CrystalAxe.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));

        performAreaAttack(attacker, target);

        stack.damageItem(1, attacker);
        return true;
    }

    private void performAreaAttack(EntityLivingBase attacker, EntityLivingBase target) {
        World world = attacker.worldObj;
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(
            target.posX - (double) 10,
            target.posY - (double) 10,
            target.posZ - (double) 10,
            target.posX + (double) 10,
            target.posY + (double) 10,
            target.posZ + (double) 10);

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        for (EntityLivingBase entity : entities) {
            if (entity != attacker && entity != target) {
                entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 10.0F);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advanced) {
        list.add(StatCollector.translateToLocal("Tooltip_CrystalAxe_00"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        player.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        int duration = this.getMaxItemUseDuration(stack) - timeLeft;

        if (duration > 20) {
            playerIn.addPotionEffect(new PotionEffect(Potion.resistance.id, 40, 1));
        }
    }
}
