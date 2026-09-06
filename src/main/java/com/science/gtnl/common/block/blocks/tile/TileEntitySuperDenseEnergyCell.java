package com.science.gtnl.common.block.blocks.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.events.MENetworkPowerStorage;
import appeng.api.networking.events.MENetworkPowerStorage.PowerEventType;
import appeng.api.util.AECableType;
import appeng.me.GridAccessException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import appeng.util.SettingsFrom;

public class TileEntitySuperDenseEnergyCell extends AENetworkTile implements IAEPowerStorage {

    protected static final double INTERNAL_MAX_POWER = 200000.0 * 8.0 * 8.0;

    protected double internalCurrentPower;
    protected byte currentMetadata = -1;
    protected byte currentLightLevel = -1;

    public TileEntitySuperDenseEnergyCell() {
        getProxy().setIdlePowerUsage(0);
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection direction) {
        return AECableType.COVERED;
    }

    @Override
    public void onReady() {
        super.onReady();
        currentMetadata = (byte) worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        currentLightLevel = -1;
        updateChargeLevel();
        updateLightLevel();
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt(NBTTagCompound data) {
        if (!worldObj.isRemote) {
            data.setDouble("internalCurrentPower", internalCurrentPower);
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt(NBTTagCompound data) {
        internalCurrentPower = Math.max(0.0, Math.min(data.getDouble("internalCurrentPower"), getInternalMaxPower()));
    }

    @Override
    public boolean canBeRotated() {
        return false;
    }

    @Override
    public void uploadSettings(SettingsFrom from, NBTTagCompound compound) {
        if (from == SettingsFrom.DISMANTLE_ITEM) {
            internalCurrentPower = Math
                .max(0.0, Math.min(compound.getDouble("internalCurrentPower"), getInternalMaxPower()));
        }
    }

    @Override
    public NBTTagCompound downloadSettings(SettingsFrom from) {
        if (from != SettingsFrom.DISMANTLE_ITEM) {
            return null;
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("internalCurrentPower", internalCurrentPower);
        tag.setDouble("internalMaxPower", getInternalMaxPower());
        return tag;
    }

    @Override
    public void getDrops(World world, int x, int y, int z, List<ItemStack> drops) {
        Block block = world.getBlock(x, y, z);
        Item item = Item.getItemFromBlock(block);
        if (item == null) {
            return;
        }

        ItemStack stack = new ItemStack(item);
        stack.setTagCompound(downloadSettings(SettingsFrom.DISMANTLE_ITEM));
        drops.add(stack);
    }

    @Override
    public double injectAEPower(double amount, Actionable mode) {
        if (mode == Actionable.SIMULATE) {
            double simulatedPower = internalCurrentPower + amount;
            return Math.max(0.0, simulatedPower - getInternalMaxPower());
        }

        if (internalCurrentPower < 0.01 && amount > 0.01) {
            getProxy().getNode()
                .getGrid()
                .postEvent(new MENetworkPowerStorage(this, PowerEventType.PROVIDE_POWER));
        }

        internalCurrentPower += amount;
        if (internalCurrentPower > getInternalMaxPower()) {
            double rejectedPower = internalCurrentPower - getInternalMaxPower();
            internalCurrentPower = getInternalMaxPower();
            updateVisualState();
            return rejectedPower;
        }

        updateVisualState();
        return 0.0;
    }

    @Override
    public double getAEMaxPower() {
        return getInternalMaxPower();
    }

    @Override
    public double getAECurrentPower() {
        return internalCurrentPower;
    }

    public int getLightLevel() {
        return (int) (15.0 * internalCurrentPower / getInternalMaxPower());
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
        return multiplier.divide(extractAEPower(multiplier.multiply(amount), mode));
    }

    protected double extractAEPower(double amount, Actionable mode) {
        if (mode == Actionable.SIMULATE) {
            return Math.min(internalCurrentPower, amount);
        }

        boolean wasFull = internalCurrentPower >= getInternalMaxPower() - 0.001;
        if (wasFull && amount > 0.001) {
            try {
                getProxy().getGrid()
                    .postEvent(new MENetworkPowerStorage(this, PowerEventType.REQUEST_POWER));
            } catch (GridAccessException ignored) {}
        }

        double extractedPower = Math.min(internalCurrentPower, amount);
        internalCurrentPower -= extractedPower;
        updateVisualState();
        return extractedPower;
    }

    protected double getInternalMaxPower() {
        return INTERNAL_MAX_POWER * PowerMultiplier.CONFIG.multiplier;
    }

    protected void updateVisualState() {
        updateChargeLevel();
        updateLightLevel();
    }

    protected void updateChargeLevel() {
        if (notLoaded()) {
            return;
        }

        byte chargeLevel = (byte) (8.0 * internalCurrentPower / getInternalMaxPower());
        chargeLevel = (byte) Math.max(0, Math.min(7, chargeLevel));
        if (currentMetadata != chargeLevel) {
            currentMetadata = chargeLevel;
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, currentMetadata, 2);
        }
    }

    protected void updateLightLevel() {
        if (notLoaded()) {
            return;
        }

        byte lightLevel = (byte) getLightLevel();
        if (currentLightLevel != lightLevel) {
            currentLightLevel = lightLevel;
            worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
        }
    }
}
