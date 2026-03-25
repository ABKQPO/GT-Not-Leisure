package com.science.gtnl.common.part;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.api.ICustomGui;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.parts.automation.PartFormationPlane;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;

public class PartActiveFormationPlane extends PartFormationPlane implements IGridTickable, ICustomGui {

    public final BaseActionSource source;
    public int nextSlot = 0;

    public PartActiveFormationPlane(ItemStack is) {
        super(is);
        this.getConfigManager()
            .registerSetting(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
        this.source = new MachineSource(this);
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 20, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        if (!this.getProxy()
            .isActive()) {
            return TickRateModulation.IDLE;
        }

        return this.doActiveFormation();
    }

    @Override
    public void readFromNBT(final NBTTagCompound extra) {
        super.readFromNBT(extra);
        this.nextSlot = extra.getInteger("nextSlot");
    }

    @Override
    public void writeToNBT(final NBTTagCompound extra) {
        super.writeToNBT(extra);
        extra.setInteger("nextSlot", this.nextSlot);
    }

    public TickRateModulation doActiveFormation() {
        try {
            IStorageGrid storage = this.getProxy()
                .getStorage();
            IEnergyGrid energy = this.getProxy()
                .getEnergy();

            List<IAEItemStack> filters = this.getFilterList();

            if (filters.isEmpty()) {
                return TickRateModulation.IDLE;
            }

            final SchedulingMode mode = (SchedulingMode) this.getConfigManager()
                .getSetting(Settings.SCHEDULING_MODE);
            int filterSize = filters.size();

            int offset = 0;
            if (mode == SchedulingMode.RANDOM) {
                offset = Platform.getRandom()
                    .nextInt(filterSize);
            } else if (mode == SchedulingMode.ROUNDROBIN) {
                offset = this.nextSlot % filterSize;
            }

            for (int i = 0; i < filterSize; i++) {
                int currentIndex = (i + offset) % filterSize;
                IAEItemStack filterStack = filters.get(currentIndex);

                IAEItemStack request = filterStack.copy();
                request.setStackSize(1);

                IAEItemStack extracted = storage.getItemInventory()
                    .extractItems(request, Actionable.SIMULATE, this.source);

                if (extracted != null) {
                    double powerReq = 1.0;
                    if (energy.extractAEPower(powerReq, Actionable.SIMULATE, PowerMultiplier.CONFIG) > powerReq - 0.1) {

                        IAEItemStack leftover = super.injectItems(extracted.copy(), Actionable.MODULATE, this.source);

                        if (leftover == null) {
                            energy.extractAEPower(powerReq, Actionable.MODULATE, PowerMultiplier.CONFIG);
                            storage.getItemInventory()
                                .extractItems(request, Actionable.MODULATE, this.source);

                            if (mode == SchedulingMode.ROUNDROBIN) {
                                this.nextSlot = (currentIndex + 1) % filterSize;
                            }

                            return TickRateModulation.FASTER;
                        }
                    }
                }
            }

        } catch (GridAccessException e) {
            // :P
        }

        return TickRateModulation.IDLE;
    }

    public List<IAEItemStack> getFilterList() {
        List<IAEItemStack> list = new ArrayList<>();
        AppEngInternalAEInventory config = (AppEngInternalAEInventory) this.getInventoryByName("config");
        for (int i = 0; i < config.getSizeInventory(); i++) {
            ItemStack is = config.getStackInSlot(i);
            if (is != null) {
                list.add(AEItemStack.create(is));
            }
        }
        return list;
    }

    @Override
    public boolean onPartActivate(final EntityPlayer p, final Vec3 pos) {
        if (p.isSneaking()) {
            return false;
        }
        CommonProxy.openGui(
            p,
            GuiType.ActiveFormationPlaneGUI,
            this.getSide(),
            this.getHost()
                .getTile());
        return true;
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack input, Actionable type, BaseActionSource src) {
        return input;
    }

    @Override
    public ItemStack getOriginGuiIcon() {
        return GTNLItemList.PartActiveFormationPlane.get(1);
    }
}
