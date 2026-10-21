package com.science.gtnl.common.part;

import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.common.part.universal.UniversalBusHelper;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.InsertionMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.me.GridAccessException;
import appeng.parts.automation.PartBaseExportBus;
import appeng.tile.inventory.IAEStackInventory;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;

public class PartUniversalExportBus extends PartBaseExportBus<IAEItemStack> {

    public static final String NBT_NEXT_SLOT = "universalNextSlot";

    public int nextSlot = 0;

    public PartUniversalExportBus(ItemStack is) {
        super(is);
    }

    @Override
    public void readFromNBT(final NBTTagCompound extra) {
        super.readFromNBT(extra);
        this.nextSlot = extra.getInteger(NBT_NEXT_SLOT);
    }

    @Override
    public void writeToNBT(final NBTTagCompound extra) {
        super.writeToNBT(extra);
        extra.setInteger(NBT_NEXT_SLOT, this.nextSlot);
    }

    @Override
    public Object getTarget() {
        final TileEntity self = this.getHost()
            .getTile();
        return this.getTileEntity(
            self,
            self.xCoord + this.getSide().offsetX,
            self.yCoord + this.getSide().offsetY,
            self.zCoord + this.getSide().offsetZ);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public TickRateModulation doBusWork() {
        if (!this.getProxy()
            .isActive() || !this.canDoBusWork()) {
            return TickRateModulation.IDLE;
        }
        if (!(this.getTarget() instanceof TileEntity target)) {
            return TickRateModulation.SLEEP;
        }

        boolean worked = false;
        try {
            final IStorageGrid storage = this.getProxy()
                .getStorage();
            final IEnergyGrid energy = this.getProxy()
                .getEnergy();
            final ForgeDirection targetSide = this.getSide()
                .getOpposite();
            final long operations = UniversalBusHelper.calculateOperations(this);
            final IAEStackInventory config = this.getAEInventoryByName(StorageName.CONFIG);
            final SchedulingMode schedulingMode = (SchedulingMode) this.getConfigManager()
                .getSetting(Settings.SCHEDULING_MODE);
            final int slots = this.availableSlots();

            final Map<IAEStackType<?>, Long> budgets = new HashMap<>();
            final Map<IAEStackType<?>, IMEInventory> externals = new HashMap<>();
            InventoryAdaptor itemAdaptor = null;
            int lastWorkedSlot = -1;

            for (int x = 0; x < slots; x++) {
                final int slot = this.getStartingSlot(schedulingMode, x, slots);
                final IAEStack<?> stack = config.getAEStackInSlot(slot);
                if (stack == null) continue;

                final IAEStackType<?> type = stack.getStackType();
                final long budget = budgets.computeIfAbsent(type, t -> operations * t.getAmountPerUnit());
                if (budget <= 0) continue;

                final IMEMonitor<?> network = storage.getMEMonitor(type);
                if (network == null) continue;

                final long moved;
                if (type == ITEM_STACK_TYPE) {
                    if (itemAdaptor == null) {
                        itemAdaptor = InventoryAdaptor.getAdaptor(
                            target,
                            targetSide,
                            InventoryAdaptor.ALLOW_ITEMS | InventoryAdaptor.FOR_INSERTS);
                        if (itemAdaptor == null) continue;
                    }
                    moved = this.exportItems(
                        itemAdaptor,
                        (IAEItemStack) stack,
                        (IMEMonitor<IAEItemStack>) network,
                        energy,
                        budget);
                } else {
                    final IMEInventory external = externals.computeIfAbsent(
                        type,
                        t -> UniversalBusHelper.getTransferInventory(target, targetSide, t, this.mySrc));
                    if (external == null) continue;
                    moved = UniversalBusHelper.exportFrom(energy, network, external, stack, budget, this.mySrc);
                }

                if (moved > 0) {
                    budgets.put(type, budget - moved);
                    worked = true;
                    lastWorkedSlot = x;
                }
            }

            if (schedulingMode == SchedulingMode.ROUNDROBIN && lastWorkedSlot >= 0 && slots > 0) {
                this.nextSlot = (this.nextSlot + lastWorkedSlot + 1) % slots;
            }
        } catch (final GridAccessException ignored) {}

        return worked ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
    }

    private long exportItems(InventoryAdaptor adaptor, IAEItemStack config, IMEMonitor<IAEItemStack> network,
        IEnergySource energy, long budget) {
        final Collection<IAEItemStack> sources;
        if (this.getInstalledUpgrades(Upgrades.FUZZY) > 0) {
            sources = network.getStorageList()
                .findFuzzy(
                    config,
                    (FuzzyMode) this.getConfigManager()
                        .getSetting(Settings.FUZZY_MODE));
        } else {
            sources = Collections.singletonList(config);
        }

        long moved = 0;
        for (IAEItemStack source : sources) {
            final long remaining = budget - moved;
            if (remaining <= 0) break;

            final IAEItemStack toInsert = source.copy();
            toInsert.setStackSize(remaining);
            final IAEStack<?> notFit = adaptor.simulateAddStack(toInsert, InsertionMode.DEFAULT);
            final long canFit = remaining - (notFit == null ? 0 : notFit.getStackSize());
            if (canFit <= 0) continue;

            final IAEItemStack request = source.copy();
            request.setStackSize(canFit);
            final IAEItemStack extracted = Platform.poweredExtraction(energy, network, request, this.mySrc);
            if (extracted == null || extracted.getStackSize() <= 0) continue;

            final IAEStack<?> failed = adaptor.addStack(extracted, InsertionMode.DEFAULT);
            long placed = extracted.getStackSize();
            if (failed != null && failed.getStackSize() > 0) {
                placed -= failed.getStackSize();
                final IAEItemStack back = extracted.copy();
                back.setStackSize(failed.getStackSize());
                network.injectItems(back, Actionable.MODULATE, this.mySrc);
            }
            moved += placed;
        }
        return moved;
    }

    private int getStartingSlot(final SchedulingMode schedulingMode, final int x, final int slots) {
        if (schedulingMode == SchedulingMode.RANDOM) {
            return Platform.getRandom()
                .nextInt(slots);
        }
        if (schedulingMode == SchedulingMode.ROUNDROBIN) {
            return (this.nextSlot + x) % slots;
        }
        return x;
    }

    @Override
    public void doFuzzy(IAEItemStack aes, FuzzyMode fzMode, IEnergyGrid energy, IMEMonitor<IAEItemStack> gridInv) {}

    @Override
    public void doOreDict(IEnergyGrid energy, IMEMonitor<IAEItemStack> gridInv) {}

    @Override
    public int calculateAmountToSend() {
        return (int) Math.min(Integer.MAX_VALUE, UniversalBusHelper.calculateOperations(this));
    }

    @Override
    public IMEMonitor<IAEItemStack> getMonitor() {
        try {
            return this.getProxy()
                .getStorage()
                .getItemInventory();
        } catch (final GridAccessException e) {
            return null;
        }
    }

    @Override
    public IAEStackType<IAEItemStack> getStackType() {
        return ITEM_STACK_TYPE;
    }

    @Override
    public void saveAEStackInv() {
        this.saveChanges();
    }

    @Override
    public boolean supportFuzzy() {
        return true;
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final Vec3 pos) {
        if (player.isSneaking()) return false;
        if (Platform.isClient()) return true;
        CommonProxy.openGui(
            player,
            GuiType.UniversalBusGUI,
            this.getSide(),
            this.getHost()
                .getTile());
        return true;
    }
}
