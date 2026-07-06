package com.science.gtnl.mixins.late.twistSpaceTechnology;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;

import com.Nxer.TwistSpaceTechnology.common.machine.multiMachineClasses.GTCM_MultiMachineBase;
import com.Nxer.TwistSpaceTechnology.system.OreProcess.machines.TST_OreProcessingFactory;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Mixin(value = TST_OreProcessingFactory.class, remap = false)
public abstract class MixinTST_OreProcessingFactory extends GTCM_MultiMachineBase<TST_OreProcessingFactory> {

    public MixinTST_OreProcessingFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            byte hatchColor = tHatch.getColor();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            setHatchRecipeMap(tHatch);
            if (tHatch instanceof MTEHatchMultiInput multiInputHatch) {
                for (FluidStack tFluid : multiInputHatch.getStoredFluid()) {
                    if (tFluid != null) {
                        rList.add(tFluid);
                    }
                }
            } else if (tHatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                    if (fluidStack != null) {
                        // Prevent the same fluid from different ME hatches from being recognized
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

        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
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

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        Map<ItemId, ItemStack> inputsFromME = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInputBus tHatch : GTUtility.validMTEList(mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) {
                continue;
            }
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        // Prevent the same item from different ME buses from being recognized
                        inputsFromME.put(ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                for (ItemStack itemStack : dualInputHatch.getAllItems()) {
                    if (itemStack != null) {
                        rList.add(itemStack);
                    }
                }
            }
        }

        ItemStack stackInSlot1 = getStackInSlot(1);
        if (GTUtility.isAnyIntegratedCircuit(stackInSlot1)) rList.add(stackInSlot1);
        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;

    }
}
