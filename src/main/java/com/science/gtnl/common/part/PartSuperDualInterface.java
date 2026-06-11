package com.science.gtnl.common.part;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.jetbrains.annotations.NotNull;

import com.glodblock.github.inventory.AEFluidInventory;
import com.glodblock.github.inventory.IAEFluidTank;
import com.glodblock.github.util.DualityFluidInterface;
import com.science.gtnl.CommonProxy;
import com.science.gtnl.api.ICustomGui;
import com.science.gtnl.api.mixinHelper.IDualityInterface;
import com.science.gtnl.common.me.dual.DualInterfaceHostSupport;
import com.science.gtnl.common.me.dual.IDualInterfaceHost;
import com.science.gtnl.common.me.dual.SuperDualInterfaceSlots;
import com.science.gtnl.mixins.late.AppliedEnergistics.AccessorPartInterface;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStackType;
import appeng.parts.automation.StackUpgradeInventory;
import appeng.parts.misc.PartInterface;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.WrapperInvSlot;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class PartSuperDualInterface extends PartInterface implements ICustomGui, IDualInterfaceHost {

    public int configSlots = 27;
    public int storageSlots = 27;
    public int patternSlots = 108;
    public int upgradeSlots = 4;

    private final DualInterfaceHostSupport dualHostSupport;

    public PartSuperDualInterface(ItemStack is) {
        super(is);
        var duality = (IDualityInterface) ((AccessorPartInterface) this).getDuality();
        duality.setConfigSlots(configSlots);
        duality.setStorageSlots(storageSlots);
        duality.setPatternSlots(patternSlots);
        duality.setUpgradeSlots(upgradeSlots);
        duality.setConfig(new AppEngInternalAEInventory(this, configSlots));
        duality.setStorage(new AppEngInternalInventory(this, storageSlots));
        duality.setPatterns(new AppEngInternalInventory(this, patternSlots));
        duality.setSlotInv(new WrapperInvSlot(duality.getStorage()));
        duality.setUpgrades(
            new StackUpgradeInventory(
                duality.getGridProxy()
                    .getMachineRepresentation(),
                this,
                upgradeSlots));
        duality.setRequireWork(new IAEItemStack[storageSlots]);
        duality.setHasFuzzyConfig(new boolean[configSlots]);
        var fluidConfig = new AppEngInternalAEInventory(this, SuperDualInterfaceSlots.FLUID_SLOT_COUNT);
        dualHostSupport = new DualInterfaceHostSupport(fluidConfig, this);
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        getDualityFluid().onChannelStateChange(c);
        super.stateChange(c);
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        getDualityFluid().onPowerStateChange(c);
        super.stateChange(c);
    }

    @Override
    public void gridChanged() {
        super.gridChanged();
        getDualityFluid().gridChanged();
    }

    @Override
    public boolean onPartActivate(EntityPlayer player, Vec3 pos) {
        if (player.isSneaking()) {
            return false;
        }
        CommonProxy.openGui(player, GuiType.SuperDualInterfaceGUI, getSide(), getHost().getTile());
        return true;
    }

    @Override
    public int rows() {
        return 12;
    }

    @Override
    public ItemStack getOriginGuiIcon() {
        return GTNLItemList.PartSuperDualInterface.get(1);
    }

    @Override
    public DualityFluidInterface getDualityFluid() {
        return dualHostSupport.getDualityFluid();
    }

    @Override
    public AEFluidInventory getInternalFluid() {
        return dualHostSupport.getInternalFluid();
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        return getDualityFluid().getFluidInventory();
    }

    @Override
    @Nullable
    public IMEMonitor<?> getMEMonitor(@NotNull IAEStackType<?> type) {
        if (type == ITEM_STACK_TYPE) {
            return getItemInventory();
        }
        if (type == FLUID_STACK_TYPE) {
            return getFluidInventory();
        }
        return getDualityFluid().getMEMonitor(type);
    }

    @Override
    public AppEngInternalAEInventory getConfig() {
        return dualHostSupport.getConfig();
    }

    @Override
    public void setConfig(int slot, IAEFluidStack stack) {
        dualHostSupport.setConfig(slot, stack);
    }

    @Override
    public void setFluidInv(int slot, IAEFluidStack stack) {
        dualHostSupport.setFluidInv(slot, stack);
    }

    @Override
    public void onFluidInventoryChanged(IAEFluidTank tank, int slot) {
        saveChanges();
        getTileEntity().markDirty();
        dualHostSupport.onFluidInventoryChanged(tank, slot);
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        for (int i = 0; i < getConfig().getSizeInventory(); i++) {
            ByteBufUtils.writeItemStack(data, getConfig().getStackInSlot(i));
        }
        getInternalFluid().writeToBuf(data);
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        super.readFromStream(data);
        boolean changed = false;
        for (int i = 0; i < getConfig().getSizeInventory(); i++) {
            ItemStack stack = ByteBufUtils.readItemStack(data);
            if (!ItemStack.areItemStacksEqual(stack, getConfig().getStackInSlot(i))) {
                getConfig().setInventorySlotContents(i, stack);
                changed = true;
            }
        }
        getDualityFluid().loadConfigFromPacket(getConfig());
        changed |= getInternalFluid().readFromBuf(data);
        return changed;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        getConfig().readFromNBT(data, "ConfigInv");
        getDualityFluid().loadConfigFromPacket(getConfig());
        getInternalFluid().readFromNBT(data, "FluidInv");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        getConfig().writeToNBT(data, "ConfigInv");
        getInternalFluid().writeToNBT(data, "FluidInv");
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        TickingRequest item = super.getTickingRequest(node);
        TickingRequest fluid = getDualityFluid().getTickingRequest(node);
        return new TickingRequest(
            Math.min(item.minTickRate, fluid.minTickRate),
            Math.max(item.maxTickRate, fluid.maxTickRate),
            item.isSleeping && fluid.isSleeping,
            true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        TickRateModulation item = super.tickingRequest(node, ticksSinceLastCall);
        TickRateModulation fluid = getDualityFluid().tickingRequest(node, ticksSinceLastCall);
        return item.ordinal() >= fluid.ordinal() ? item : fluid;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return dualHostSupport.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return dualHostSupport.drain(from, resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return dualHostSupport.drain(from, maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return dualHostSupport.canFill(from, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return dualHostSupport.canDrain(from, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return dualHostSupport.getTankInfo(from);
    }
}
