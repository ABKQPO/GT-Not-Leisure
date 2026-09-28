// Pigmee Fumo port from AE2 Lightning Tech Reborn.
// Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
// License: LGPL-3.0. Model author: TedXenon.
// Original model credit: "Made with Blockbench, made by TedXenon".
// Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.block.blocks.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

// Standard placeable form of BlockPigmeeFumo, additionally
// allowed into the helmet slot.
// This is deliberately not an ItemArmor: it grants no armour points and no damage reduction. The
// head-worn geometry is drawn by the client-only player render event handler, so only the slot admission
// check is needed here.
public class ItemBlockPigmeeFumo extends ItemBlock {

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
}
