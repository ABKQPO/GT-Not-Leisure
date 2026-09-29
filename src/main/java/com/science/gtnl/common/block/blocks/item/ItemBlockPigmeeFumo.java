// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
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

// Placeable BlockPigmeeFumo, wearable in the helmet slot and any Baubles slot; deliberately not an ItemArmor.
public class ItemBlockPigmeeFumo extends ItemBlock implements IBaubleExpanded {

    // Baubles-Expanded's "universal" type is admitted by every slot, including ones other mods register.
    private static final String[] BAUBLE_TYPES = { BaubleExpandedSlots.universalType };

    public ItemBlockPigmeeFumo(Block block) {
        super(block);
    }

    // Provides no armour protection.
    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        return armorType == 0;
    }

    // Legacy accessor: ItemUtils resolves UNIVERSAL reflectively, falling back to RING when that constant is missing.
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
