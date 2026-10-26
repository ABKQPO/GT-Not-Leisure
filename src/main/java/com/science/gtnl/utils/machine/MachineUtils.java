package com.science.gtnl.utils.machine;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;

import appeng.api.storage.data.IAEItemStack;
import gregtech.api.enums.OutputBusType;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

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

    // GT's getStoredFluidsForColor / getStoredInputsForColor, except that the same stack seen through several ME
    // hatches or buses counts once, and crafting input buffers count when the machine supports them.
    public static ArrayList<FluidStack> storedFluidsForColor(MTEMultiBlockBase machine, Optional<Byte> color,
        Consumer<MTEHatchInput> setHatchRecipeMap, boolean supportsCraftingMEBuffer) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(machine.mInputHatches)) {
            byte hatchColor = tHatch.getColor();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            setHatchRecipeMap.accept(tHatch);
            if (tHatch instanceof MTEHatchMultiInput multiInputHatch) {
                for (FluidStack tFluid : multiInputHatch.getStoredFluid()) {
                    if (tFluid != null) {
                        rList.add(tFluid);
                    }
                }
            } else if (tHatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                    if (fluidStack != null) {
                        inputsFromME.put(fluidStack.getFluid(), fluidStack);
                    }
                }
            } else {
                FluidStack fillableStack = tHatch.getFillableStack();
                if (fillableStack != null) {
                    rList.add(fillableStack);
                }
            }
        }

        if (supportsCraftingMEBuffer) {
            for (IDualInputHatch dualInputHatch : machine.mDualInputHatches) {
                for (FluidStack fluidStack : dualInputHatch.getAllFluids()) {
                    if (fluidStack != null) {
                        rList.add(fluidStack);
                    }
                }
            }
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    public static ArrayList<ItemStack> storedInputsForColor(MTEMultiBlockBase machine, Optional<Byte> color,
        boolean supportsCraftingMEBuffer) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        Map<ItemId, ItemStack> inputsFromME = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInputBus tHatch : GTUtility.validMTEList(machine.mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) {
                continue;
            }
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = machine.getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        inputsFromME.put(ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        if (supportsCraftingMEBuffer) {
            for (IDualInputHatch dualInputHatch : machine.mDualInputHatches) {
                for (ItemStack itemStack : dualInputHatch.getAllItems()) {
                    if (itemStack != null) {
                        rList.add(itemStack);
                    }
                }
            }
        }

        ItemStack stackInSlot1 = machine.getStackInSlot(1);
        if (GTUtility.isAnyIntegratedCircuit(stackInSlot1)) rList.add(stackInSlot1);
        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    // Data stick copy of a GT ME output hatch or bus: where it is, so a GTNL proxy can link to it.
    public static NBTTagCompound addMasterPosition(IGregTechTileEntity tileEntity, NBTTagCompound tag) {
        if (tag == null) tag = new NBTTagCompound();
        NBTTagCompound masterNBT = new NBTTagCompound();
        masterNBT.setInteger("masterX", tileEntity.getXCoord());
        masterNBT.setInteger("masterY", tileEntity.getYCoord());
        masterNBT.setInteger("masterZ", tileEntity.getZCoord());
        masterNBT.setInteger("masterDim", tileEntity.getWorld().provider.dimensionId);
        tag.setTag("master", masterNBT);
        return tag;
    }
}
