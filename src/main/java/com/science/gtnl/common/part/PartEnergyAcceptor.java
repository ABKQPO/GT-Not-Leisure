package com.science.gtnl.common.part;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkPowerStorage;
import appeng.api.networking.events.MENetworkPowerStorage.PowerEventType;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.util.AECableType;
import appeng.me.GridAccessException;
import appeng.parts.AEBasePart;
import appeng.parts.p2p.IPartGT5Power;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IEnergySink;
import io.netty.buffer.ByteBuf;

public class PartEnergyAcceptor extends AEBasePart
    implements IAEPowerStorage, IEnergySink, IEnergyReceiver, IPartGT5Power {

    protected static final String STORED_POWER_TAG = "internalCurrentPower";
    public static final double MAXIMUM_POWER = 8000.0;
    protected double storedPower;

    public PartEnergyAcceptor(ItemStack stack) {
        super(stack);
        getProxy().setIdlePowerUsage(0.0);
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection direction) {
        return AECableType.COVERED;
    }

    @Override
    public int cableConnectionRenderTo() {
        return 1;
    }

    @Override
    public void getBoxes(IPartCollisionHelper collisionHelper) {
        collisionHelper.addBox(5, 5, 14, 11, 11, 15);
        collisionHelper.addBox(0, 0, 15, 16, 16, 16);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(IPartRenderHelper renderHelper, RenderBlocks renderer) {
        renderHelper.setTexture(getTexture());
        renderHelper.setBounds(0, 0, 15, 16, 16, 16);
        renderHelper.renderInventoryBox(renderer);
        renderHelper.setBounds(5, 5, 14, 11, 11, 15);
        renderHelper.renderInventoryBox(renderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(int x, int y, int z, IPartRenderHelper renderHelper, RenderBlocks renderer) {
        renderHelper.setTexture(getTexture());
        renderHelper.setBounds(0, 0, 15, 16, 16, 16);
        renderHelper.renderBlock(x, y, z, renderer);
        renderHelper.setBounds(5, 5, 14, 11, 11, 15);
        renderHelper.renderBlock(x, y, z, renderer);
    }

    @Override
    public double injectAEPower(double amount, Actionable mode) {
        if (amount < 0.000001) {
            return 0.0;
        }

        if (mode == Actionable.SIMULATE) {
            double simulatedPower = storedPower + amount;
            return simulatedPower > getAEMaxPower() ? simulatedPower - getAEMaxPower() : 0.0;
        }

        if (storedPower < 0.01 && amount > 0.01) {
            postStorageEvent(PowerEventType.PROVIDE_POWER);
        }
        storedPower += amount;
        if (storedPower > getAEMaxPower()) {
            amount = storedPower - getAEMaxPower();
            storedPower = getAEMaxPower();
            persistStoredPower();
            return amount;
        }

        if (amount > 0.0) {
            persistStoredPower();
        }
        return 0.0;
    }

    @Override
    public double getAEMaxPower() {
        return MAXIMUM_POWER * PowerMultiplier.CONFIG.multiplier;
    }

    @Override
    public double getAECurrentPower() {
        return storedPower;
    }

    @Override
    public boolean isAEPublicPowerStorage() {
        return true;
    }

    @Override
    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public double extractAEPower(double amount, Actionable mode, PowerMultiplier multiplier) {
        return multiplier.divide(extractStoredPower(multiplier.multiply(amount), mode));
    }

    @Override
    public long injectEnergyUnits(long voltage, long amperage) {
        if (voltage <= 0L || amperage <= 0L) {
            return 0L;
        }

        double energy = PowerUnits.EU.convertTo(PowerUnits.AE, voltage * amperage);
        double overflow = funnelPowerIntoStorage(energy, Actionable.SIMULATE);
        if (overflow >= energy) {
            return 0L;
        }

        long usedAmperage = amperage - (long) Math.ceil(PowerUnits.AE.convertTo(PowerUnits.EU, overflow) / voltage);
        if (usedAmperage > 0L) {
            energy = PowerUnits.EU.convertTo(PowerUnits.AE, voltage * usedAmperage);
            funnelPowerIntoStorage(energy, Actionable.MODULATE);
            return usedAmperage;
        }
        return 0L;
    }

    @Override
    public boolean inputEnergy() {
        return true;
    }

    @Override
    public boolean outputsEnergy() {
        return false;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return isPowerSide(direction);
    }

    @Override
    public double getDemandedEnergy() {
        return getExternalPowerDemand(PowerUnits.EU, Double.MAX_VALUE);
    }

    @Override
    public int getSinkTier() {
        return Integer.MAX_VALUE;
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (!isPowerSide(directionFrom)) {
            return amount;
        }

        double overflow = PowerUnits.EU.convertTo(PowerUnits.AE, injectExternalPower(PowerUnits.EU, amount));
        if (overflow > 0.0) {
            storedPower += overflow;
            persistStoredPower();
        }
        return 0.0;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (!isPowerSide(from) || maxReceive <= 0) {
            return 0;
        }

        int demand = (int) Math.floor(getExternalPowerDemand(PowerUnits.RF, maxReceive));
        int received = Math.min(maxReceive, demand);
        if (!simulate) {
            injectExternalPower(PowerUnits.RF, received);
        }
        return received;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        if (!isPowerSide(from)) {
            return 0;
        }
        return (int) Math.floor(PowerUnits.AE.convertTo(PowerUnits.RF, getAECurrentPower()));
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (!isPowerSide(from)) {
            return 0;
        }
        return (int) Math.floor(PowerUnits.AE.convertTo(PowerUnits.RF, getAEMaxPower()));
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return isPowerSide(from);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        storedPower = Math.max(0.0, data.getDouble(STORED_POWER_TAG));
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setDouble(STORED_POWER_TAG, storedPower);
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeDouble(storedPower);
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        boolean changed = super.readFromStream(data);
        double previousPower = storedPower;
        storedPower = data.readDouble();
        return changed || previousPower != storedPower;
    }

    protected double getExternalPowerDemand(PowerUnits externalUnit, double maximumRequired) {
        return PowerUnits.AE.convertTo(
            externalUnit,
            Math.max(0.0, getFunnelPowerDemand(externalUnit.convertTo(PowerUnits.AE, maximumRequired))));
    }

    protected double getFunnelPowerDemand(double maximumRequired) {
        try {
            IEnergyGrid grid = getProxy().getEnergy();
            return grid.getEnergyDemand(maximumRequired);
        } catch (GridAccessException ignored) {
            return getAEMaxPower() - storedPower;
        }
    }

    protected double injectExternalPower(PowerUnits input, double amount) {
        return PowerUnits.AE
            .convertTo(input, funnelPowerIntoStorage(input.convertTo(PowerUnits.AE, amount), Actionable.MODULATE));
    }

    protected double funnelPowerIntoStorage(double power, Actionable mode) {
        try {
            IEnergyGrid grid = getProxy().getEnergy();
            double leftover = grid.injectPower(power, mode);
            return mode == Actionable.SIMULATE ? leftover : 0.0;
        } catch (GridAccessException ignored) {
            return injectAEPower(power, mode);
        }
    }

    protected IIcon getTexture() {
        Block energyAcceptor = AEApi.instance()
            .definitions()
            .blocks()
            .energyAcceptor()
            .maybeBlock()
            .orNull();
        return energyAcceptor.getIcon(0, 0);
    }

    protected boolean isPowerSide(ForgeDirection direction) {
        return direction != ForgeDirection.UNKNOWN && direction == getSide();
    }

    protected double extractStoredPower(double amount, Actionable mode) {
        double extractedPower = Math.min(amount, storedPower);
        if (mode == Actionable.MODULATE && extractedPower > 0.0) {
            boolean wasFull = storedPower >= getAEMaxPower() - 0.001;
            storedPower -= extractedPower;
            if (wasFull && extractedPower >= 0.001) {
                postStorageEvent(PowerEventType.REQUEST_POWER);
            }
            persistStoredPower();
        }
        return extractedPower;
    }

    protected void persistStoredPower() {
        if (getHost() != null) {
            getHost().markForUpdate();
            saveChanges();
        }
    }

    protected void postStorageEvent(PowerEventType type) {
        try {
            getProxy().getGrid()
                .postEvent(new MENetworkPowerStorage(this, type));
        } catch (GridAccessException ignored) {}
    }
}
