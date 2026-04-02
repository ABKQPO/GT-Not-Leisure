package com.science.gtnl.common.item.items.bauble;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RoyalGel extends BaubleItem {

    public RoyalGel() {
        this.setUnlocalizedName("RoyalGel");
        this.setMaxStackSize(1);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "RoyalGel");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, getUnlocalizedName());
        GTNLItemList.RoyalGel.set(new ItemStack(this, 1));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return ItemUtils.UNIVERSAL_TYPE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.getIconString());
    }

    @SubscribeEvent
    public void onPlayerAttacked(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayerMP player)) return;
        if (!(event.source.getSourceOfDamage() instanceof EntitySlime)) return;

        if (hasRoyalGelEquipped(player)) {
            event.setCanceled(true);
        }
    }

    private boolean hasRoyalGelEquipped(EntityPlayerMP player) {
        if (BaublesApi.getBaubles(player) == null) return false;

        for (int i = 0; i < BaublesApi.getBaubles(player)
            .getSizeInventory(); i++) {
            ItemStack stack = BaublesApi.getBaubles(player)
                .getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof RoyalGel) {
                return true;
            }
        }
        return false;
    }
}
