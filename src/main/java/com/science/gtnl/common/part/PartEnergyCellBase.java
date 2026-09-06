package com.science.gtnl.common.part;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.events.MENetworkPowerStorage;
import appeng.api.networking.events.MENetworkPowerStorage.PowerEventType;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AECableType;
import appeng.me.GridAccessException;
import appeng.parts.AEBasePart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public abstract class PartEnergyCellBase extends AEBasePart implements IAEPowerStorage {

    protected static final String STORED_POWER_TAG = "internalCurrentPower";
    public static final double ENERGY_CELL_CAPACITY = 200000.0;
    public static final double DENSE_ENERGY_CELL_CAPACITY = ENERGY_CELL_CAPACITY * 8.0;
    public static final double SUPER_DENSE_ENERGY_CELL_CAPACITY = DENSE_ENERGY_CELL_CAPACITY * 8.0;
    protected double storedPower;

    public PartEnergyCellBase(ItemStack stack) {
        super(stack);
        storedPower = getStoredPower(stack);
        getProxy().setIdlePowerUsage(0);
    }

    protected abstract double getBaseCapacity();

    protected abstract Block getEnergyCellBlock();

    public double getMaximumPower() {
        return getBaseCapacity() * PowerMultiplier.CONFIG.multiplier;
    }

    public double getStoredPower(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(tag.getDouble(STORED_POWER_TAG), getMaximumPower()));
    }

    public int getChargeLevel() {
        return getChargeLevel(storedPower, getMaximumPower());
    }

    public static int getChargeLevel(double storedPower, double maximumPower) {
        return Math.max(0, Math.min(7, (int) (8.0 * storedPower / maximumPower)));
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection direction) {
        return AECableType.GLASS;
    }

    @Override
    public int cableConnectionRenderTo() {
        return 16;
    }

    @Override
    public void getBoxes(IPartCollisionHelper collisionHelper) {
        collisionHelper.addBox(4, 4, 12, 12, 12, 14);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(IPartRenderHelper renderHelper, RenderBlocks renderer) {
        renderHelper.setTexture(getTexture());
        renderHelper.setBounds(4, 4, 12, 12, 12, 14);
        renderHelper.renderInventoryBox(renderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(int x, int y, int z, IPartRenderHelper renderHelper, RenderBlocks renderer) {
        renderHelper.setTexture(getTexture());
        renderHelper.setBounds(4, 4, 12, 12, 12, 14);
        renderHelper.renderBlock(x, y, z, renderer);
    }

    @Override
    public double injectAEPower(double amount, Actionable mode) {
        double acceptedPower = Math.min(amount, getMaximumPower() - storedPower);
        if (mode == Actionable.MODULATE && acceptedPower > 0.0) {
            boolean wasEmpty = storedPower < 0.01;
            storedPower += acceptedPower;
            if (wasEmpty && storedPower >= 0.01) {
                postStorageEvent(PowerEventType.PROVIDE_POWER);
            }
            persistStoredPower();
        }
        return amount - acceptedPower;
    }

    @Override
    public double getAEMaxPower() {
        return getMaximumPower();
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
    public ItemStack getItemStack(PartItemStack type) {
        syncStoredPowerToStack();
        return super.getItemStack(type);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        storedPower = Math.max(0.0, Math.min(data.getDouble(STORED_POWER_TAG), getMaximumPower()));
        syncStoredPowerToStack();
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

    protected IIcon getTexture() {
        return getEnergyCellBlock().getIcon(0, getChargeLevel());
    }

    protected double extractStoredPower(double amount, Actionable mode) {
        double extractedPower = Math.min(amount, storedPower);
        if (mode == Actionable.MODULATE && extractedPower > 0.0) {
            boolean wasFull = storedPower >= getMaximumPower() - 0.001;
            storedPower -= extractedPower;
            if (wasFull && extractedPower >= 0.001) {
                postStorageEvent(PowerEventType.REQUEST_POWER);
            }
            persistStoredPower();
        }
        return extractedPower;
    }

    protected void persistStoredPower() {
        syncStoredPowerToStack();
        if (getHost() != null) {
            getHost().markForUpdate();
            saveChanges();
        }
    }

    protected void syncStoredPowerToStack() {
        NBTTagCompound tag = is.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            is.setTagCompound(tag);
        }
        tag.setDouble(STORED_POWER_TAG, storedPower);
    }

    protected void postStorageEvent(PowerEventType type) {
        try {
            getProxy().getGrid()
                .postEvent(new MENetworkPowerStorage(this, type));
        } catch (GridAccessException ignored) {}
    }
}
