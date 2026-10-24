package com.science.gtnl.common.equipment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ic2.api.item.IElectricItem;

public interface IEquipmentElectricItem extends IElectricItem {

    /**
     * The tier of the energy module installed in the stack, or 0 when none is installed.
     */
    int energyTier(ItemStack stack);

    /**
     * How much EU the stack can store with its current modules.
     */
    double capacity(ItemStack stack);

    /**
     * A new stack of this item without modules, charged to its capacity.
     */
    default ItemStack charged() {
        ItemStack stack = new ItemStack((Item) this);
        EquipmentEnergy.write(stack, capacity(stack), capacity(stack));
        return stack;
    }

    @Override
    default boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    default Item getChargedItem(ItemStack stack) {
        return (Item) this;
    }

    @Override
    default Item getEmptyItem(ItemStack stack) {
        return (Item) this;
    }

    @Override
    default double getMaxCharge(ItemStack stack) {
        return capacity(stack);
    }

    @Override
    default int getTier(ItemStack stack) {
        return EquipmentEnergy.voltageTier(energyTier(stack));
    }

    @Override
    default double getTransferLimit(ItemStack stack) {
        return EquipmentEnergy.transferLimit(energyTier(stack));
    }
}
