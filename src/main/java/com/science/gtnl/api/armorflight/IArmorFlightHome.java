package com.science.gtnl.api.armorflight;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IArmorFlightHome {

    /**
     * Receives an armor piece that flew back to this tile (purged off the wearer, or a delivery that could not
     * reach its owner). Called on the server only.
     *
     * @param owner the wearer the piece belonged to, or null when the owner is offline
     * @param piece the body slot the piece is for
     * @param stack the stack to store; the home may keep the instance
     * @return what did not fit, or null when everything was stored
     */
    ItemStack storeArmorFlightPiece(EntityPlayer owner, ArmorFlightPiece piece, ItemStack stack);
}
