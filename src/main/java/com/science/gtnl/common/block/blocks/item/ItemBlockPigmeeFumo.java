// Pigmee Fumo port from AE2 Lightning Tech Reborn.
// Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
// License: LGPL-3.0. Model author: TedXenon.
// Original model credit: "Made with Blockbench, made by TedXenon".
// Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.block.blocks.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.science.gtnl.utils.item.ItemUtils;

import baubles.api.BaubleType;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.IBaubleExpanded;

// Standard placeable form of BlockPigmeeFumo, additionally allowed into the helmet slot and into any
// Baubles slot.
// This is deliberately not an ItemArmor: it grants no armour points and no damage reduction. The
// head-worn geometry is drawn by the client-only player render event handler, so only the slot admission
// check is needed here.
public class ItemBlockPigmeeFumo extends ItemBlock implements IBaubleExpanded {

    // "universal" is admitted by every Baubles slot, including the ones other mods register, so the doll
    // is not limited to the ring, amulet and belt slots. Baubles-Expanded supplies the string.
    private static final String[] BAUBLE_TYPES = { BaubleExpandedSlots.universalType };

    // block: the unique BlockPigmeeFumo instance passed by GameRegistry; the item is not registered twice
    public ItemBlockPigmeeFumo(Block block) {
        super(block);
    }

    // stack: the stack being validated
    // armorType: Forge armour index where 0 is the helmet; not the inventory slot index
    // entity: the equipping entity
    // Returns true only for armorType == 0; provides no armour protection
    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        return armorType == 0;
    }

    // The legacy single-type accessor. ItemUtils resolves UNIVERSAL reflectively and only falls back to
    // RING if the installed Baubles lacks that constant.
    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return ItemUtils.UNIVERSAL_TYPE;
    }

    // The expanded accessor, which is what a Baubles-Expanded slot actually consults.
    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return BAUBLE_TYPES;
    }

    // The doll has no per-tick behaviour; Baubles requires the method but there is nothing to do.
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
