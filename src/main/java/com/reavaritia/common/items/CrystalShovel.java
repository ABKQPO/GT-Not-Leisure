package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.Collection;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalShovel extends ItemSpade {

    public CrystalShovel() {
        super(ToolHelper.CRYSTAL);
        this.setUnlocalizedName("CrystalShovel");
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "CrystalShovel");
        this.setMaxDamage(8888);
        MinecraftForge.EVENT_BUS.register(this);
        ReAvaItemList.CrystalShovel.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_CrystalShovel_00"));
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer player) {
            ItemStack heldItem = player.getHeldItem();
            if (heldItem != null && heldItem.getItem() instanceof CrystalShovel) {
                clearDebuffs(player);
                applyPositiveEffects(player);
            }
        }
    }

    private void clearDebuffs(EntityPlayer player) {
        Collection<PotionEffect> activeEffects = player.getActivePotionEffects();
        for (PotionEffect effect : activeEffects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion != null && potion.isBadEffect()) {
                player.removePotionEffect(effect.getPotionID());
            }
        }
    }

    private void applyPositiveEffects(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10, 1, true));
        player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 10, 1, true));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            clearDebuffs(player);
            applyPositiveEffects(player);
            world.playSoundAtEntity(player, "random.orb", 1.0F, 1.0F);
        }
        return stack;
    }
}
