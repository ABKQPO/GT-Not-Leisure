package com.science.gtnl.common.machine.hatch;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.api.mixinHelper.ICommonMetaTileEntityInventory;
import com.science.gtnl.api.mixinHelper.IMetaTileEntityInventoryHandler;
import com.science.gtnl.common.gui.modularui.hatch.OredictInputBusMEGui;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.item.AEItemStack;
import appeng.util.prioitylist.OreFilteredList;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.config.MachineStats;
import gregtech.common.tileentities.machines.IHatchWatcher;
import gregtech.common.tileentities.machines.IRecipeProcessingAwareHatch;
import gregtech.common.tileentities.machines.ISmartInputHatch;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.RecipeCheckReason;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class OredictInputBusME extends MTEHatchInputBusME
    implements IRecipeProcessingAwareHatch, IPowerChannelState, ISmartInputHatch, IDataCopyable {

    public static int SIDE_SLOT_COUNT = 100;
    public static int ALL_SLOT_COUNT = SIDE_SLOT_COUNT * 2 + 1 + 9 * 9;
    public ItemStack[] shadowInventory;
    public int[] savedStackSizes;

    public static final int CONFIG_WINDOW_ID = 10;
    public static final int MANUAL_SLOT_WINDOW = 11;

    @Nullable
    public String oreDict;
    public boolean isSuper;
    private IStackWatcher superWatcher;

    public OredictInputBusME(int aID, String aName, String aNameRegional, boolean isSuper) {
        super(aID, true, aName, aNameRegional);
        this.isSuper = isSuper;
        if (isSuper) {
            shadowInventory = new ItemStack[SIDE_SLOT_COUNT];
            savedStackSizes = new int[SIDE_SLOT_COUNT];
            ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[ALL_SLOT_COUNT]);
            ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

                @Override
                public void onContentsChanged(int slot) {
                    OredictInputBusME.this.onContentsChanged(slot);
                }
            });
            disableSort = true;
        }
    }

    public OredictInputBusME(String aName, boolean autoPullAvailable, int aTier, String[] aDescription,
        ITexture[][][] aTextures, boolean isSuper) {
        super(aName, autoPullAvailable, aTier, aDescription, aTextures);
        this.isSuper = isSuper;
        if (isSuper) {
            shadowInventory = new ItemStack[SIDE_SLOT_COUNT];
            savedStackSizes = new int[SIDE_SLOT_COUNT];
            ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[ALL_SLOT_COUNT]);
            ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

                @Override
                public void onContentsChanged(int slot) {
                    OredictInputBusME.this.onContentsChanged(slot);
                }
            });
            disableSort = true;
        }
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return isSuper ? "gtnl.hatch.oredict_input_bus_me.advanced.name" : "gtnl.hatch.oredict_input_bus_me.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OredictInputBusME(mName, autoPullAvailable, mTier, mDescriptionArray, mTextures, isSuper);
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.tooltip.0"),
            StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.tooltip.1") + " "
                + GTValues.TIER_COLORS[isSuper ? 8 : 6],
            StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.tooltip.2"),
            StatCollector.translateToLocalFormatted("gtnl.hatch.oredict_input_bus_me.tooltip.3", isSuper ? 100 : 16),
            StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.tooltip.4"),
            StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.tooltip.5"),
            StatCollector.translateToLocalFormatted("gtnl.hatch.oredict_input_bus_me.tooltip.6", isSuper ? 100 : 16),
            StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.tooltip.7"),
            StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.tooltip.8"),
            StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.tooltip.9") };
    }

    @Nullable
    public String getOreDict() {
        return oreDict;
    }

    public boolean hasFilter() {
        return oreDict != null && !oreDict.isEmpty();
    }

    public void setOreDict(@Nullable String oreDict) {
        this.oreDict = oreDict;
        if (autoPullItemList) {
            refreshItemList();
        } else if (isSuper) {
            int filterSlotCount = Math.min(getConfiguredFilterSlotCount(), mInventory.length);
            for (int i = 0; i < filterSlotCount; i++) {
                mInventory[i] = null;
            }
        } else {
            clearSlotConfigs();
        }
        updateAllInformationSlots();
        configureSuperWatcher();
    }

    public String getOreDictForGui() {
        return hasFilter() ? oreDict : "";
    }

    @Override
    public void refreshItemList() {
        if (!isActive()) {
            if (isSuper) {
                clearSuperInformationSlots();
            }
            return;
        }
        AENetworkProxy proxy = getProxy();
        try {
            Predicate<IAEItemStack> oreFilterList = createFilter();

            IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                .getItemInventory();
            Iterator<IAEItemStack> iterator = sg.getStorageList()
                .iterator();

            int index = 0;
            boolean inputChanged = false;
            int slotLimit = isSuper ? SIDE_SLOT_COUNT : SLOT_COUNT;
            while (iterator.hasNext() && index < slotLimit) {
                IAEItemStack currItem = iterator.next();
                if (currItem == null || oreFilterList == null || !oreFilterList.test(currItem)) continue;

                if (currItem.getStackSize() >= minAutoPullStackSize) {
                    ItemStack itemstack = GTUtility.copyAmount(1, currItem.getItemStack());
                    if (isSuper) {
                        inputChanged |= !areItemStacksEqual(mInventory[index], itemstack);
                        this.mInventory[index] = itemstack;
                        this.mInventory[index + SIDE_SLOT_COUNT] = copyInformationStack(currItem);
                    } else {
                        setSlotConfig(index, itemstack);
                        Slot newSlot = slots[index];
                        if (newSlot != null) {
                            newSlot.extracted = currItem.getItemStack();
                            newSlot.extractedAmount = newSlot.extracted == null ? 0 : newSlot.extracted.stackSize;
                        }
                    }
                    index++;
                }
            }
            if (isSuper) {
                for (int i = index; i < slotLimit; i++) {
                    inputChanged |= mInventory[i] != null;
                    mInventory[i] = null;
                    mInventory[i + SIDE_SLOT_COUNT] = null;
                }
            } else {
                Arrays.fill(slots, index, slotLimit, null);
            }

            if (isSuper && inputChanged) {
                scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
            }

        } catch (final GridAccessException ignored) {}
    }

    public Predicate<IAEItemStack> createFilter() {
        if (!hasFilter()) return null;
        return OreFilteredList.makeFilter(oreDict);
    }

    @Override
    public void updateAllInformationSlots() {
        if (!isSuper) {
            super.updateAllInformationSlots();
            return;
        }
        for (int index = 0; index < SIDE_SLOT_COUNT; index++) {
            try {
                updateInformationSlot(index);
            } catch (GridAccessException ignored) {}
        }
    }

    @Override
    public void updateInformationSlot(int index) throws GridAccessException {
        if (!isSuper) {
            super.updateInformationSlot(index);
            return;
        }
        updateInformationSlot(index, mInventory[index]);
    }

    /**
     * Update the right side of the GUI, which shows the amounts of items set on the left side.
     */
    public ItemStack updateInformationSlot(int aIndex, ItemStack aStack) {
        if (!isSuper) {
            try {
                super.updateInformationSlot(aIndex);
                return aIndex >= 0 && aIndex < SLOT_COUNT && slots[aIndex] != null ? slots[aIndex].extracted : null;
            } catch (GridAccessException ignored) {
                return null;
            }
        }
        if (aIndex >= 0 && aIndex < SIDE_SLOT_COUNT) {
            if (aStack == null) {
                super.setInventorySlotContents(aIndex + SIDE_SLOT_COUNT, null);
            } else {
                AENetworkProxy proxy = getProxy();
                if (!proxy.isActive()) {
                    super.setInventorySlotContents(aIndex + SIDE_SLOT_COUNT, null);
                    return null;
                }

                if (!isAllowedToWork()) {
                    this.shadowInventory[aIndex] = null;
                    this.savedStackSizes[aIndex] = 0;
                    super.setInventorySlotContents(aIndex + SIDE_SLOT_COUNT, null);
                    return null;
                }

                try {
                    IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                        .getItemInventory();
                    IAEItemStack request = AEItemStack.create(mInventory[aIndex]);
                    request.setStackSize(Integer.MAX_VALUE);
                    IAEItemStack result = sg.extractItems(request, Actionable.SIMULATE, getRequestSource());
                    ItemStack s = (result != null) ? result.getItemStack() : null;
                    setInventorySlotContents(aIndex + SIDE_SLOT_COUNT, s);
                    return s;
                } catch (final GridAccessException ignored) {}
            }
        }
        return null;
    }

    /**
     * Used to avoid slot update.
     */
    public ItemStack getShadowItemStack(int index) {
        if (!isSuper) return index >= 0 && index < SLOT_COUNT && slots[index] != null ? slots[index].extracted : null;
        if (index < 0 || index >= shadowInventory.length) {
            return null;
        }
        return shadowInventory[index];
    }

    public int getShadowInventorySize() {
        if (!isSuper) return SLOT_COUNT;
        return shadowInventory.length;
    }

    @Override
    public @NotNull AENetworkProxy getProxy() {
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable gridProxyable) {
                gridProxy = new AENetworkProxy(
                    gridProxyable,
                    "proxy",
                    isSuper ? GTNLItemList.SuperOredictInputBusME.get(1) : GTNLItemList.OredictInputBusME.get(1),
                    true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                updateValidGridProxySides();

                var bmte = getBaseMetaTileEntity();
                if (bmte.getWorld() != null) {
                    gridProxy.setOwner(
                        bmte.getWorld()
                            .getPlayerEntityByName(bmte.getOwnerName()));
                }
            }
        }

        return gridProxy;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        saveFilter(aNBT);
        if (isSuper) {
            int[] sizes = new int[100];
            for (int i = 0; i < 100; ++i) sizes[i] = mInventory[i + 100] == null ? 0 : mInventory[i + 100].stackSize;
            aNBT.setIntArray("sizes", sizes);
        }
    }

    public void saveFilter(NBTTagCompound aNBT) {
        if (hasFilter()) {
            aNBT.setString("oreDict", oreDict);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        loadFilter(aNBT);
        if (isSuper && aNBT.hasKey("sizes")) {
            int[] sizes = aNBT.getIntArray("sizes");
            if (sizes.length == 100) {
                for (int i = 0; i < 100; ++i) {
                    if (sizes[i] != 0 && mInventory[i] != null) {
                        ItemStack s = mInventory[i].copy();
                        s.stackSize = sizes[i];
                        mInventory[i + 100] = s;
                    }
                }
            }
        }
    }

    public void loadFilter(NBTTagCompound aNBT) {
        if (aNBT.hasKey("oreDict")) oreDict = aNBT.getString("oreDict");
    }

    @Override
    public void setAutoPullItemList(boolean pullItemList) {
        if (!isSuper) {
            super.setAutoPullItemList(pullItemList);
            return;
        }
        if (!autoPullAvailable) {
            return;
        }

        autoPullItemList = pullItemList;
        if (!autoPullItemList) {
            for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
                mInventory[i] = null;
            }
        } else {
            refreshItemList();
        }
        updateAllInformationSlots();
        configureSuperWatcher();
    }

    @Override
    public void updateSlots() {
        if (!isSuper) {
            super.updateSlots();
            return;
        }
        for (int i = 201; i < ALL_SLOT_COUNT; i++) {
            if (mInventory[i] != null && mInventory[i].stackSize <= 0) {
                mInventory[i] = null;
            }
        }
    }

    @Override
    public boolean needsPeriodicChecks() {
        return !MachineStats.machines.useStackWatcher;
    }

    @Override
    public void updateWatcher(IStackWatcher newWatcher) {
        if (!isSuper) {
            super.updateWatcher(newWatcher);
            return;
        }
        superWatcher = newWatcher;
        configureSuperWatcher();
    }

    @Override
    public void onStackChange(IItemList stacks, IAEStack fullStack, IAEStack diffStack, BaseActionSource source,
        StorageChannel channel) {
        if (!isSuper) {
            super.onStackChange(stacks, fullStack, diffStack, source, channel);
            return;
        }
        if (diffStack.getStackSize() > 0) {
            scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
        }
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound nbt) {
        oreDict = nbt.getString("oreDict");
        return super.pasteCopiedData(player, nbt);
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound nbt = super.getCopiedData(player);
        if (hasFilter()) {
            nbt.setString("oreDict", oreDict);
        }

        NBTTagList stockingItems = new NBTTagList();

        if (isSuper && !autoPullItemList) {
            for (int index = 0; index < SIDE_SLOT_COUNT; index++) {
                stockingItems.appendTag(GTUtility.saveItem(mInventory[index]));
            }
            nbt.setTag("itemsToStock", stockingItems);
        }
        return nbt;
    }

    @Override
    public int getCircuitSlot() {
        return isSuper ? SIDE_SLOT_COUNT * 2 : super.getCircuitSlot();
    }

    @Override
    public int getCircuitSlotX() {
        return isSuper ? 188 : super.getCircuitSlotX();
    }

    @Override
    public boolean setStackToZeroInsteadOfNull(int aIndex) {
        if (processingRecipe) {
            return true;
        }
        return isSuper ? (aIndex < 201 || aIndex >= ALL_SLOT_COUNT) : aIndex != getManualSlot();
    }

    @Override
    public ItemStack getStackInSlot(int aIndex) {
        if (!isSuper || !processingRecipe) return super.getStackInSlot(aIndex);

        if (aIndex < 0 || aIndex > mInventory.length) return null;

        // Display slots
        if (aIndex >= SIDE_SLOT_COUNT && aIndex < SIDE_SLOT_COUNT * 2) return null;

        if (aIndex == getCircuitSlot() || (aIndex >= 201 && aIndex < ALL_SLOT_COUNT)) return mInventory[aIndex];

        if (mInventory[aIndex] != null) {

            AENetworkProxy proxy = getProxy();
            if (proxy == null || !proxy.isActive()) {
                return null;
            }

            if (!isAllowedToWork()) {
                this.shadowInventory[aIndex] = null;
                this.savedStackSizes[aIndex] = 0;
                super.setInventorySlotContents(aIndex + SIDE_SLOT_COUNT, null);
                return null;
            }

            try {
                IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                    .getItemInventory();

                IAEItemStack request = AEItemStack.create(mInventory[aIndex]);
                request.setStackSize(Integer.MAX_VALUE);

                IAEItemStack result = sg.extractItems(request, Actionable.SIMULATE, getRequestSource());

                if (result != null) {
                    this.shadowInventory[aIndex] = result.getItemStack();
                    this.savedStackSizes[aIndex] = this.shadowInventory[aIndex].stackSize;
                    this.setInventorySlotContents(aIndex + SIDE_SLOT_COUNT, this.shadowInventory[aIndex]);
                    return this.shadowInventory[aIndex];
                } else {
                    // Request failed
                    this.setInventorySlotContents(aIndex + SIDE_SLOT_COUNT, null);
                    return null;
                }
            } catch (final GridAccessException ignored) {}
            return null;
        } else {
            // AE available but no items requested
            this.setInventorySlotContents(aIndex + SIDE_SLOT_COUNT, null);
        }
        return mInventory[aIndex];
    }

    @Override
    public void onExplosion() {
        if (!isSuper) {
            super.onExplosion();
            return;
        }
        for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
            mInventory[i] = null;
        }
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return isSuper ? aIndex >= 201 && aIndex < ALL_SLOT_COUNT : super.isValidSlot(aIndex);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        refreshSuperGuiStateOnOpen();
        return new OredictInputBusMEGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    public int getGUIWidth() {
        return isSuper ? 392 : super.getGUIWidth();
    }

    public int getFilterSlotCountForGui() {
        return getConfiguredFilterSlotCount();
    }

    public int getStockSlotOffsetForGui() {
        return getConfiguredFilterSlotCount();
    }

    public int getManualSlotStartForGui() {
        return isSuper ? SIDE_SLOT_COUNT * 2 + 1 : getManualSlot();
    }

    public ItemStack updateInformationSlotForGui(int index, ItemStack stack) {
        ItemStack informationStack = updateInformationSlot(index, stack);
        configureSuperWatcher();
        return informationStack;
    }

    protected int getConfiguredFilterSlotCount() {
        return isSuper ? SIDE_SLOT_COUNT : SLOT_COUNT;
    }

    public Slot[] getSlotsForGui() {
        return slots;
    }

    public boolean containsFilterStackForGui(ItemStack stack) {
        if (isSuper) {
            for (int i = 0; i < getFilterSlotCountForGui(); ++i) {
                if (GTUtility.areStacksEqual(mInventory[i], stack, false)) return true;
            }
            return false;
        }
        for (Slot slot : slots) {
            if (slot != null && GTUtility.areStacksEqual(slot.config, stack, false)) return true;
        }
        return false;
    }

    protected void refreshSuperGuiStateOnOpen() {
        if (!isSuper || !getBaseMetaTileEntity().isServerSide()) {
            return;
        }
        if (autoPullItemList) {
            refreshItemList();
            return;
        }
        updateAllInformationSlots();
    }

    protected void clearSuperInformationSlots() {
        for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
            mInventory[i + SIDE_SLOT_COUNT] = null;
        }
    }

    private void configureSuperWatcher() {
        if (!isSuper) {
            return;
        }
        if (superWatcher != null) {
            superWatcher.clear();
            if (MachineStats.machines.useStackWatcher && !autoPullItemList) {
                for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
                    ItemStack stack = mInventory[i];
                    if (stack != null) {
                        superWatcher.add(AEItemStack.create(stack));
                    }
                }
            }
        }
        scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
    }

    private boolean areItemStacksEqual(ItemStack first, ItemStack second) {
        return first == second || first != null && second != null
            && first.stackSize == second.stackSize
            && GTUtility.areStacksEqual(first, second, true);
    }

    private void scheduleRecipeCheck(RecipeCheckReason reason) {
        for (IHatchWatcher watcher : watchers) {
            watcher.scheduleRecipeCheck(reason);
        }
    }

    protected ItemStack copyInformationStack(IAEItemStack stack) {
        return stack == null ? null : stack.getItemStack();
    }

    @Override
    public CheckRecipeResult endRecipeProcessing(MTEMultiBlockBase controller) {
        if (!isSuper) return super.endRecipeProcessing(controller);
        CheckRecipeResult checkRecipeResult = CheckRecipeResultRegistry.SUCCESSFUL;
        for (int i = 0; i < SIDE_SLOT_COUNT; ++i) {
            if (savedStackSizes[i] != 0) {
                ItemStack oldStack = shadowInventory[i];
                if (oldStack == null || oldStack.stackSize < savedStackSizes[i]) {
                    AENetworkProxy proxy = getProxy();
                    try {
                        IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                            .getItemInventory();
                        IAEItemStack request = AEItemStack.create(mInventory[i]);
                        int toExtract = savedStackSizes[i] - (oldStack == null ? 0 : oldStack.stackSize);
                        request.setStackSize(toExtract);
                        IAEItemStack result = sg.extractItems(request, Actionable.MODULATE, getRequestSource());
                        proxy.getEnergy()
                            .extractAEPower(request.getStackSize(), Actionable.MODULATE, PowerMultiplier.CONFIG);
                        setInventorySlotContents(i + SIDE_SLOT_COUNT, oldStack);
                        if (result == null || result.getStackSize() != toExtract) {
                            controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                            checkRecipeResult = SimpleCheckRecipeResult
                                .ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                        }
                    } catch (final GridAccessException ignored) {
                        controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                        checkRecipeResult = SimpleCheckRecipeResult
                            .ofFailurePersistOnShutdown("stocking_hatch_fail_extraction");
                    }
                }
                savedStackSizes[i] = 0;
                shadowInventory[i] = null;
                if (mInventory[i + SIDE_SLOT_COUNT] != null && mInventory[i + SIDE_SLOT_COUNT].stackSize <= 0) {
                    mInventory[i + SIDE_SLOT_COUNT] = null;
                }
            }
        }
        processingRecipe = false;
        return checkRecipeResult;
    }

    @Override
    public String[] getInfoData() {
        String busStatusKey = getProxy() != null && getProxy().isActive()
            ? "gtnl.hatch.oredict_input_bus_me.info.online"
            : IGregTechDeviceInformation.encode("gtnl.hatch.oredict_input_bus_me.info.offline", getAEDiagnostics());
        return new String[] { busStatusKey,
            hasFilter()
                ? IGregTechDeviceInformation.encode(
                    "gtnl.hatch.oredict_input_bus_me.info.ore_dict.format",
                    EnumChatFormatting.GREEN + oreDict + EnumChatFormatting.RESET)
                : IGregTechDeviceInformation.encode(
                    "gtnl.hatch.oredict_input_bus_me.info.ore_dict.format",
                    IGregTechDeviceInformation.translatable("gtnl.hatch.oredict_input_bus_me.info.ore_dict.unset")) };
    }

}
