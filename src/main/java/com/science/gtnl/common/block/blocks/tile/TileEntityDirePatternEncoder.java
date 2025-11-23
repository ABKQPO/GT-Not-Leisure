package com.science.gtnl.common.block.blocks.tile;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import appeng.tile.AEBaseTile;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import lombok.Getter;

public class TileEntityDirePatternEncoder extends AEBaseTile implements IAEAppEngInventory {

    // 0 = input,1 = output
    @Getter
    private final AppEngInternalInventory pattern = new AppEngInternalInventory(this, 2, 64);
    // 0 - 80 = input,81 = output
    @Getter
    private final AppEngInternalInventory recipe = new AppEngInternalInventory(this, 82, 1);

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readCustomFromNBT(NBTTagCompound aNBT) {
        pattern.readFromNBT(aNBT, "pattern");
        recipe.readFromNBT(aNBT, "recipe");
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeCustomToNBT(NBTTagCompound aNBT) {
        pattern.writeToNBT(aNBT, "pattern");
        recipe.writeToNBT(aNBT, "recipe");
    }

    @Override
    public boolean dropItems() {
        return true;
    }

    @Override
    public void getDrops(final World w, final int x, final int y, final int z, final List<ItemStack> drops) {
        for (int l = 0; l < pattern.getSizeInventory(); l++) {
            final ItemStack is = pattern.getStackInSlot(l);
            if (is != null) {
                drops.add(is);
            }
        }
    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removedStack,
        ItemStack newStack) {

    }
}
