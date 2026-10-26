package com.science.gtnl.utils.machine;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.storage.data.IAEItemStack;
import gregtech.api.enums.OutputBusType;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.metatileentity.BaseMetaTileEntity;

public class MachineUtils {

    private MachineUtils() {}

    public static boolean equlsItemStackAndAEItemStack(IAEItemStack AEstack, ItemStack stack) {
        if (AEstack == null) {
            return stack == null;
        } else {
            if (stack == null) return false;
            boolean b = AEstack.getItem() == stack.getItem() && AEstack.getItemDamage() == stack.getItemDamage();
            if (b) {
                if (AEstack.getTagCompound() != null) {
                    return AEstack.getTagCompound()
                        .equals(stack.getTagCompound());
                } else return stack.getTagCompound() == null;
            }
            return false;
        }
    }

    public static boolean isMEOutputBus(IOutputBus outputBus) {
        OutputBusType type = outputBus.getBusType();
        return type == OutputBusType.MECacheFiltered || type == OutputBusType.MEFiltered
            || type == OutputBusType.MECacheUnfiltered
            || type == OutputBusType.MEUnfiltered;
    }

    public static UUID getOwner(Object te) {
        if (te instanceof BaseMetaTileEntity igte) {
            return igte.getOwnerUuid();
        } else {
            return null;
        }
    }

    public static ItemStack[] flat(ItemStackG[] mStoredItemInternal2) {
        if (mStoredItemInternal2 == null || mStoredItemInternal2.length == 0) {
            return new ItemStack[0];
        }
        int size = 0;
        for (ItemStackG itemStackG : mStoredItemInternal2) {
            if (itemStackG != null) {
                size += itemStackG.flat().length;
            }
        }
        if (size == 0) {
            return new ItemStack[0];
        }
        ItemStack[] flattened = new ItemStack[size];
        int index = 0;
        for (ItemStackG itemStackG : mStoredItemInternal2) {
            if (itemStackG == null) {
                continue;
            }
            ItemStack[] stacks = itemStackG.flat();
            System.arraycopy(stacks, 0, flattened, index, stacks.length);
            index += stacks.length;
        }
        return flattened;
    }

    public static FluidStack[] flat(FluidTankG[] mStoredItemInternal2) {
        if (mStoredItemInternal2 == null || mStoredItemInternal2.length == 0) {
            return new FluidStack[0];
        }
        int size = 0;
        for (FluidTankG fluidTankG : mStoredItemInternal2) {
            if (fluidTankG != null) {
                size += fluidTankG.flat().length;
            }
        }
        if (size == 0) {
            return new FluidStack[0];
        }
        FluidStack[] flattened = new FluidStack[size];
        int index = 0;
        for (FluidTankG fluidTankG : mStoredItemInternal2) {
            if (fluidTankG == null) {
                continue;
            }
            FluidStack[] stacks = fluidTankG.flat();
            System.arraycopy(stacks, 0, flattened, index, stacks.length);
            index += stacks.length;
        }
        return flattened;
    }

    public interface VargsFunction<T, R> {

        R apply(T... t);
    }

    public static VargsFunction<ItemStack[], ItemStack[]> filterStack = (s) -> {
        if (s == null || s.length == 0) {
            return new ItemStack[0];
        }
        int size = 0;
        for (ItemStack[] stacks : s) {
            if (stacks == null) {
                continue;
            }
            for (ItemStack stack : stacks) {
                if (stack != null) {
                    size++;
                }
            }
        }
        if (size == 0) {
            return new ItemStack[0];
        }
        ItemStack[] filtered = new ItemStack[size];
        int index = 0;
        for (ItemStack[] stacks : s) {
            if (stacks == null) {
                continue;
            }
            for (ItemStack stack : stacks) {
                if (stack != null) {
                    filtered[index++] = stack;
                }
            }
        }
        return filtered;
    };
}
