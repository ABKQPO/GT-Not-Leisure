package com.science.gtnl.common.item.tools;

import java.util.Collection;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.client.CreativeTabsLoader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalShovel extends ItemSpade {

    public static final ToolMaterial CRYSTAL = EnumHelper.addToolMaterial("CRYSTAL", 3, 8888, 100.0F, 9.0F, 22);

    public CrystalShovel() {
        super(CRYSTAL);
        this.setUnlocalizedName("CrystalShovel");
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setCreativeTab(CreativeTabsLoader.GTNotLeisure);
        this.setTextureName("sciencenotleisure:CrystalShovel");
        this.setMaxDamage(8888);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        toolTip.add(TextLocalization.Tooltip_CrystalShovel_00);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
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
