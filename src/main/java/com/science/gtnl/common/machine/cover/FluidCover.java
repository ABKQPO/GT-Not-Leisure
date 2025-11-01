package com.science.gtnl.common.machine.cover;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import gregtech.api.covers.CoverContext;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IMachineProgress;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.CommonMetaTileEntity;
import gregtech.common.covers.CoverLegacyData;

public class FluidCover extends CoverLegacyData {

    public Fluid fluid;
    public String description;

    public FluidCover(CoverContext context, FluidStack fluid, String descriptionKey) {
        super(context);
        this.fluid = fluid.getFluid();
        this.description = descriptionKey;
    }

    public FluidCover(CoverContext context, Fluid fluid, String descriptionKey) {
        super(context);
        this.fluid = fluid;
        this.description = descriptionKey;
    }

    @Override
    public boolean isRedstoneSensitive(long aTimer) {
        return false;
    }

    @Override
    public void doCoverThings(byte aInputRedstone, long aTimer) {
        if (aTimer % getTickRate() == 0) {
            ICoverable coverable = coveredTile.get();
            if (coverable instanceof IMachineProgress machineProgress) {
                if (machineProgress.isAllowedToWork()) {
                    tryAddFluid(machineProgress);
                }
            }
        }
    }

    public void tryAddFluid(IMachineProgress tileEntity) {
        if (tileEntity instanceof BaseMetaTileEntity baseTile
            && baseTile.getMetaTileEntity() instanceof CommonMetaTileEntity commonMetaTile) {
            if (this.fluid == null) return;
            FluidStack fluid = commonMetaTile.getFluid();
            if (fluid != null && fluid.getFluid() != this.fluid) return;
            int capacity = commonMetaTile.getCapacity();
            int fluidAmount = fluid != null ? commonMetaTile.getFluidAmount() : 0;
            int current = Math.max(0, capacity - fluidAmount);
            commonMetaTile.fill(new FluidStack(this.fluid, current), true);

        }
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocal(this.description);
    }

    @Override
    public boolean alwaysLookConnected() {
        return true;
    }

    @Override
    public int getMinimumTickRate() {
        return 5;
    }

    @Override
    public void readDataFromNbt(NBTBase nbt) {
        if (nbt instanceof NBTTagCompound compound) {
            this.coverData = compound.getInteger("CoverData");
            this.description = compound.getString("Description");

            if (compound.hasKey("Fluid")) {
                String fluidName = compound.getString("Fluid");
                this.fluid = FluidRegistry.getFluid(fluidName);
            } else {
                this.fluid = null;
            }

        } else if (nbt instanceof NBTTagInt tagInt) {
            this.coverData = tagInt.func_150287_d();
        }
    }

    @Override
    public @NotNull NBTBase saveDataToNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("CoverData", this.coverData);
        compound.setString("Description", this.description);

        if (this.fluid != null) {
            compound.setString("Fluid", this.fluid.getName());
        }

        return compound;
    }

}
