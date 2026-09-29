// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.block.blocks.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.BaubleItem;

import baubles.api.BaubleType;
import baubles.api.expanded.IBaubleExpanded;

public class ItemBlockPigmeeFumo extends ItemBlock implements IBaubleExpanded {

    public ItemBlockPigmeeFumo(Block block) {
        super(block);
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        return armorType == 0;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleItem.UNIVERSAL_TYPE;
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return BaubleItem.UNIVERSAL_BAUBLE_TYPE;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {}

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {}

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {}

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }
}
