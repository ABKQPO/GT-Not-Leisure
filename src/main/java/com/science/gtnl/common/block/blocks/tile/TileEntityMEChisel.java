package com.science.gtnl.common.block.blocks.tile;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cricketcraft.chisel.api.carving.CarvingUtils;
import com.cricketcraft.chisel.api.carving.ICarvingRegistry;
import com.google.common.collect.ImmutableSet;
import com.science.gtnl.utils.ChiselPatternDetails;
import com.science.gtnl.utils.GTNLNBTTagList;

import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

public class TileEntityMEChisel extends AENetworkInvTile implements IInterfaceHost, IGridTickable {

    private static final EnumSet<ForgeDirection> targets = EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN));
    public final DualityInterface duality = new DualityInterface(this.getProxy(), this);
    public final AppEngInternalInventory inv = new AppEngInternalInventory(this, 1, 1);
    public final MachineSource source = new MachineSource(this);
    public final List<ChiselPatternDetails> patterns = new ObjectArrayList<>();
    public final IItemList<IAEItemStack> cache = new ItemList();
    @Getter
    private int parallel = 1;

    public void onReady() {
        super.onReady();
        this.getProxy()
            .setIdlePowerUsage(10);
        this.getProxy()
            .setValidSides(targets);
    }

    @MENetworkEventSubscribe
    public void stateChange(MENetworkChannelsChanged c) {
        this.duality.notifyNeighbors();
    }

    @MENetworkEventSubscribe
    public void stateChange(MENetworkPowerStatusChange c) {
        this.duality.notifyNeighbors();
    }

    @Override
    @NotNull
    public IInventory getInternalInventory() {
        return inv;
    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation invOperation, ItemStack removed,
        ItemStack added) {
        if (!Mods.Chisel.isModLoaded()) return;
        if ((removed == null && added == null) || invOperation != InvOperation.setInventorySlotContents) return;
        patterns.clear();
        ItemStack s;
        if ((s = inv.getStackInSlot(0)) != null) {
            var r = getChiselRegistry();
            if (r != null) {
                var input = AEItemStack.create(s);
                if (!ChiselPatternDetails
                    .addChiselPatterns(input, r.getItemsForChiseling(s), patterns, this.parallel)) {
                    this.inv.setInventorySlotContents(0, null);
                }
            }
        }
        var n = this.getProxy()
            .getNode();
        if (n == null) return;
        n.getGrid()
            .postEvent(new MENetworkCraftingPatternChange(this, n));
    }

    @Optional.Method(modid = "chisel")
    public ICarvingRegistry getChiselRegistry() {
        return CarvingUtils.getChiselRegistry();
    }

    private static final int[] NO_SLOTS = new int[0];

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection whichSide) {
        return NO_SLOTS;
    }

    public void setParallel(int parallel) {
        if (parallel < 1) parallel = 1;
        this.parallel = parallel;
        if (!this.worldObj.isRemote && inv.getStackInSlot(0) != null) {
            for (var pattern : patterns) {
                pattern.setParallel(this.parallel);
            }
            var n = this.getProxy()
                .getNode();
            if (n == null) return;
            n.getGrid()
                .postEvent(new MENetworkCraftingPatternChange(this, n));
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeCustomNBT(NBTTagCompound data) {
        data.setInteger("parallel", this.parallel);
        NBTTagList list = new NBTTagList();
        for (var stack : this.cache) {
            NBTTagCompound nbt = new NBTTagCompound();
            stack.writeToNBT(nbt);
            list.appendTag(nbt);
        }
        data.setTag("cacheItems", list);
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readCustomFromNBT(NBTTagCompound data) {
        this.parallel = Math.max(1, data.getInteger("parallel"));
        for (var pattern : patterns) {
            pattern.setParallel(this.parallel);
        }
        this.cache.resetStatus();
        var list = data.getTagList("cacheItems", Constants.NBT.TAG_COMPOUND);
        for (var nbtBase : ((GTNLNBTTagList) list)) {
            this.cache.addStorage(AEItemStack.loadItemStackFromNBT((NBTTagCompound) nbtBase));
        }
    }

    @Override
    public DualityInterface getInterfaceDuality() {
        return duality;
    }

    @Override
    public EnumSet<ForgeDirection> getTargets() {
        return targets;
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public int getInstalledUpgrades(Upgrades upgrades) {
        return 0;
    }

    @Override
    public IInventory getInventoryByName(String s) {
        return null;
    }

    @Override
    public void provideCrafting(ICraftingProviderHelper providerHelper) {
        if (this.getProxy()
            .isActive()) {
            for (var pattern : patterns) {
                providerHelper.addCraftingOption(this, pattern);
            }
        }
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails details, InventoryCrafting crafting) {
        if (details.getCondensedInputs().length == 0 || details.getCondensedOutputs().length == 0) return false;
        var inputD = details.getCondensedInputs()[0].getItemStack();
        for (var i = 0; i < crafting.getSizeInventory(); i++) {
            var input = crafting.getStackInSlot(i);
            if (input == null) continue;
            if (inputD.isItemEqual(input)) {
                var out = details.getCondensedOutputs()[0].copy()
                    .setStackSize(input.stackSize);
                cache.addStorage(out);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.duality.getRequestedJobs();
    }

    @Override
    public IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack items, Actionable actionable) {
        return this.duality.injectCraftedItems(link, items, actionable);
    }

    @Override
    public void jobStateChange(ICraftingLink iCraftingLink) {
        this.duality.jobStateChange(iCraftingLink);
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.duality.getConfigManager();
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public AECableType getCableConnectionType(@NotNull ForgeDirection dir) {
        return AECableType.SMART;
    }

    @Override
    public @NotNull TickingRequest getTickingRequest(@NotNull IGridNode node) {
        return this.duality.getTickingRequest(node);
    }

    @Override
    public @NotNull TickRateModulation tickingRequest(@NotNull IGridNode node, int i) {
        if (cache.isEmpty()) return TickRateModulation.SLOWER;
        var grid = node.getGrid();
        IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
        IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
        var storage = storageGrid.getItemInventory();
        ItemList o = null;
        for (var stack : cache) {
            if (stack == null || stack.getStackSize() == 0) continue;
            var newItem = Platform.poweredInsert(energyGrid, storage, stack, source);
            if (newItem != null && newItem.getStackSize() != 0) {
                if (o == null) o = new ItemList();
                o.addStorage(newItem);
            }
        }
        cache.resetStatus();
        if (o != null) {
            for (var stack : o) {
                cache.addStorage(stack);
            }
        }
        return TickRateModulation.URGENT;
    }

    @Override
    public boolean shouldDisplay() {
        return false;
    }

    @Override
    public boolean allowsPatternOptimization() {
        return false;
    }

}
