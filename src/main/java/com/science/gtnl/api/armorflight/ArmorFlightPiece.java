package com.science.gtnl.api.armorflight;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public enum ArmorFlightPiece {

    FEET(3, 0.10),
    LEGS(2, 0.45),
    CHEST(1, 0.70),
    HEAD(0, 0.90);

    public static final ArmorFlightPiece[] VALUES = values();
    public static final ArmorFlightPiece[] SEQUENCE = { FEET, LEGS, CHEST, HEAD };

    public final int armorType;
    public final double heightFraction;

    ArmorFlightPiece(int armorType, double heightFraction) {
        this.armorType = armorType;
        this.heightFraction = heightFraction;
    }

    public int equipmentSlot() {
        return 4 - armorType;
    }

    public int inventoryIndex() {
        return 3 - armorType;
    }

    public ItemStack worn(EntityPlayer player) {
        return player.inventory.armorInventory[inventoryIndex()];
    }

    public void setWorn(EntityPlayer player, ItemStack stack) {
        player.inventory.armorInventory[inventoryIndex()] = stack;
    }

    public boolean accepts(ItemStack stack, EntityPlayer player) {
        return stack != null && stack.getItem() != null
            && stack.getItem()
                .isValidArmor(stack, armorType, player);
    }

    public static ArmorFlightPiece byArmorType(int armorType) {
        for (ArmorFlightPiece p : VALUES) if (p.armorType == armorType) return p;
        return null;
    }

    public static ArmorFlightPiece byOrdinal(int ordinal) {
        return VALUES[Math.floorMod(ordinal, VALUES.length)];
    }
}
