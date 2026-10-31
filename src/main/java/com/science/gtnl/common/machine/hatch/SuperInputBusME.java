package com.science.gtnl.common.machine.hatch;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.api.mixinHelper.ICommonMetaTileEntityInventory;
import com.science.gtnl.api.mixinHelper.IMetaTileEntityInventoryHandler;
import com.science.gtnl.common.gui.modularui.hatch.SuperInputBusMEGui;
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
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class SuperInputBusME extends MTEHatchInputBusME implements IConfigurationCircuitSupport,
    IRecipeProcessingAwareHatch, IPowerChannelState, ISmartInputHatch, IDataCopyable {

    public static int SIDE_SLOT_COUNT = 100;
    public static int ALL_SLOT_COUNT = SIDE_SLOT_COUNT * 2 + 1 + 9 * 9;
    public static final int CONFIG_WINDOW_ID = 10;
    public static final int MANUAL_SLOT_WINDOW = 11;

    public ItemStack[] shadowInventory = new ItemStack[SIDE_SLOT_COUNT];
    public int[] storedStackSizes = new int[SIDE_SLOT_COUNT];
    {
        Arrays.fill(storedStackSizes, Integer.MAX_VALUE);
    }
    public int[] savedStackSizes = new int[SIDE_SLOT_COUNT];
    private IStackWatcher watcher;

    public SuperInputBusME(int aID, boolean autoPullAvailable, String aName, String aNameRegional) {
        super(aID, autoPullAvailable, aName, aNameRegional);
        ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[ALL_SLOT_COUNT]);
        ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

            @Override
            public void onContentsChanged(int slot) {
                SuperInputBusME.this.onContentsChanged(slot);
            }
        });
        disableSort = true;
    }

    public SuperInputBusME(String aName, boolean autoPullAvailable, int aTier, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, autoPullAvailable, aTier, aDescription, aTextures);
        ((ICommonMetaTileEntityInventory) this).setMInventory(new ItemStack[ALL_SLOT_COUNT]);
        ((IMetaTileEntityInventoryHandler) this).setInventoryHandler(new ItemStackHandler(mInventory) {

            @Override
            public void onContentsChanged(int slot) {
                SuperInputBusME.this.onContentsChanged(slot);
            }
        });
        disableSort = true;
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return autoPullAvailable ? "gtnl.hatch.super_input_bus_me.advanced.name" : "gtnl.hatch.super_input_bus_me.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public String[] getDescription() {
        List<String> strings = new ObjectArrayList<>(8);
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.tooltip.0"));
        strings.add(
            StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.tooltip.1") + " "
                + GTValues.TIER_COLORS[autoPullAvailable ? 7 : 4]
                + GTValues.VN[autoPullAvailable ? 7 : 4]);
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.tooltip.2"));
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.tooltip.3"));

        if (autoPullAvailable) {
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.auto_pull.tooltip.0"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.auto_pull.tooltip.1"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.auto_pull.tooltip.2"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.auto_pull.tooltip.3"));
        }

        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.tooltip.4"));
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.tooltip.5"));
        return strings.toArray(new String[0]);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperInputBusME(mName, autoPullAvailable, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        int[] sizes = new int[SIDE_SLOT_COUNT];
        for (int i = 0; i < SIDE_SLOT_COUNT; ++i) {
            sizes[i] = mInventory[i + SIDE_SLOT_COUNT] == null ? 0 : mInventory[i + SIDE_SLOT_COUNT].stackSize;
        }
        aNBT.setIntArray("sizes", sizes);

        NBTTagList stackSizeList = new NBTTagList();
        for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
            int size = storedStackSizes[i];
            if (size == Integer.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setInteger("size", size);
            stackSizeList.appendTag(tag);
        }

        aNBT.setTag("storedStackSizes", stackSizeList);
    }

    @Override
    public void setAutoPullItemList(boolean pullItemList) {
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
        configureWatchers();
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("sizes")) {
            int[] sizes = aNBT.getIntArray("sizes");
            if (sizes.length == SIDE_SLOT_COUNT) {
                for (int i = 0; i < SIDE_SLOT_COUNT; ++i) {
                    if (sizes[i] != 0 && mInventory[i] != null) {
                        ItemStack s = mInventory[i].copy();
                        s.stackSize = sizes[i];
                        mInventory[i + SIDE_SLOT_COUNT] = s;
                    }
                }
            }
        }

        if (aNBT.hasKey("storedStackSizes")) {
            NBTTagList list = aNBT.getTagList("storedStackSizes", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slot = tag.getInteger("slot");
                int size = tag.getInteger("size");
                if (slot < SIDE_SLOT_COUNT) {
                    storedStackSizes[slot] = size;
                }
            }
        }
    }

    @Override
    public void updateSlots() {
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
    public void addWatcher(IHatchWatcher watcher) {
        watchers.add(watcher);
    }

    @Override
    public void removeWatcher(IHatchWatcher watcher) {
        watchers.remove(watcher);
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound nbt) {
        if (nbt == null || !COPIED_DATA_IDENTIFIER.equals(nbt.getString("type"))) return false;
        ItemStack circuit = GTUtility.loadItem(nbt, "circuit");
        if (GTUtility.isStackInvalid(circuit)) circuit = null;

        if (autoPullAvailable) {
            setAutoPullItemList(nbt.getBoolean("autoPull"));
            minAutoPullStackSize = nbt.getInteger("minStackSize");
            // Data sticks created before refreshTime was implemented should not cause stocking buses to
            // spam divide by zero errors
            if (nbt.hasKey("refreshTime")) {
                autoPullRefreshTime = nbt.getInteger("refreshTime");
            }
        }

        NBTTagList list = nbt.getTagList("storedStackSizes", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("slot");
            int size = tag.getInteger("size");

            if (slot < SIDE_SLOT_COUNT) {
                storedStackSizes[slot] = size;
            }
        }

        additionalConnection = nbt.getBoolean("additionalConnection");
        if (!autoPullItemList) {
            NBTTagList stockingItems = nbt.getTagList("itemsToStock", 10);
            for (int i = 0; i < stockingItems.tagCount(); i++) {
                this.mInventory[i] = GTUtility.loadItem(stockingItems.getCompoundTagAt(i));
            }
        }
        setInventorySlotContents(getCircuitSlot(), circuit);
        updateValidGridProxySides();
        byte color = nbt.getByte("color");
        this.getBaseMetaTileEntity()
            .setColorization(color);

        return true;
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound nbt = super.getCopiedData(player);

        NBTTagList stackSizeList = new NBTTagList();
        for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
            int size = storedStackSizes[i];
            if (size == Integer.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setInteger("size", size);
            stackSizeList.appendTag(tag);
        }
        nbt.setTag("storedStackSizes", stackSizeList);

        NBTTagList stockingItems = new NBTTagList();
        if (!autoPullItemList) {
            for (int index = 0; index < SIDE_SLOT_COUNT; index++) {
                stockingItems.appendTag(GTUtility.saveItem(mInventory[index]));
            }
            nbt.setTag("itemsToStock", stockingItems);
        }
        return nbt;
    }

    @Override
    public int getCircuitSlot() {
        return SIDE_SLOT_COUNT * 2;
    }

    @Override
    public int getManualSlot() {
        return getManualSlotStartForGui();
    }

    @Override
    public int getCircuitSlotX() {
        return 188;
    }

    @Override
    public int getSizeInventory() {
        return ALL_SLOT_COUNT;
    }

    @Override
    public boolean setStackToZeroInsteadOfNull(int aIndex) {
        if (processingRecipe) {
            return true;
        }
        return aIndex < 201 || aIndex >= ALL_SLOT_COUNT;
    }

    @Override
    public ItemStack getStackInSlot(int aIndex) {
        if (aIndex < 0 || aIndex >= mInventory.length) return null;

        if (aIndex == getCircuitSlot() || (aIndex >= getManualSlot() && aIndex < ALL_SLOT_COUNT)) {
            return mInventory[aIndex];
        }

        if (!processingRecipe) return null;

        // Display slots
        if (aIndex >= SIDE_SLOT_COUNT && aIndex < SIDE_SLOT_COUNT * 2) return null;

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
                request.setStackSize(storedStackSizes[aIndex]);

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
        for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
            mInventory[i] = null;
        }
    }

    @Override
    public void refreshItemList() {
        if (!isActive()) {
            return;
        }
        boolean inputChanged = false;
        AENetworkProxy proxy = getProxy();
        try {
            IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                .getItemInventory();
            Iterator<IAEItemStack> iterator = sg.getStorageList()
                .iterator();
            int index = 0;
            while (iterator.hasNext() && index < SIDE_SLOT_COUNT) {
                IAEItemStack currItem = iterator.next();
                if (currItem.getStackSize() >= minAutoPullStackSize) {
                    ItemStack itemstack = GTUtility.copyAmount(
                        storedStackSizes[index] == Integer.MAX_VALUE ? 1 : storedStackSizes[index],
                        currItem.getItemStack());
                    inputChanged |= !areItemStacksEqual(mInventory[index], itemstack);
                    this.mInventory[index] = itemstack;
                    index++;
                }
            }
            for (int i = index; i < SIDE_SLOT_COUNT; i++) {
                inputChanged |= mInventory[i] != null;
                mInventory[i] = null;
            }

        } catch (final GridAccessException ignored) {}
        if (inputChanged) {
            scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
        }
    }

    @Override
    public void updateAllInformationSlots() {
        for (int index = 0; index < SIDE_SLOT_COUNT; index++) {
            updateInformationSlot(index);
        }
    }

    @Override
    public CheckRecipeResult endRecipeProcessing(MTEMultiBlockBase controller) {
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

    /**
     * Update the right side of the GUI, which shows the amounts of items set on the left side
     */
    @Override
    public void updateInformationSlot(int index) {
        updateInformationSlot(index, mInventory[index]);
    }

    public ItemStack updateInformationSlot(int aIndex, ItemStack aStack) {
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
                    request.setStackSize(storedStackSizes[aIndex]);
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
        if (index < 0 || index >= shadowInventory.length) {
            return null;
        }
        return shadowInventory[index];
    }

    public int getShadowInventorySize() {
        return shadowInventory.length;
    }

    @Override
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(
                    (IGridProxyable) getBaseMetaTileEntity(),
                    "proxy",
                    autoPullAvailable ? GTNLItemList.AdvancedSuperInputBusME.get(1)
                        : GTNLItemList.SuperInputBusME.get(1),
                    true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                updateValidGridProxySides();
                if (getBaseMetaTileEntity().getWorld() != null) gridProxy.setOwner(
                    getBaseMetaTileEntity().getWorld()
                        .getPlayerEntityByName(getBaseMetaTileEntity().getOwnerName()));
            }
        }
        return this.gridProxy;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return aIndex >= 201 && aIndex < ALL_SLOT_COUNT;
    }

    @Override
    public int getGUIWidth() {
        return 392;
    }

    @Override
    public int getGUIHeight() {
        return 179;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        refreshGuiStateOnOpen();
        return new SuperInputBusMEGui(this).build(data, syncManager, uiSettings);
    }

    public int getFilterSlotCountForGui() {
        return SIDE_SLOT_COUNT;
    }

    public int getStockSlotOffsetForGui() {
        return SIDE_SLOT_COUNT;
    }

    public int getManualSlotStartForGui() {
        return SIDE_SLOT_COUNT * 2 + 1;
    }

    public ItemStack updateInformationSlotForGui(int index, ItemStack stack) {
        ItemStack informationStack = updateInformationSlot(index, stack);
        configureWatchers();
        return informationStack;
    }

    public boolean containsFilterStackForGui(ItemStack stack) {
        for (int i = 0; i < SIDE_SLOT_COUNT; ++i) {
            if (GTUtility.areStacksEqual(mInventory[i], stack, false)) return true;
        }
        return false;
    }

    public int getStoredStackSizeForGui(int slot) {
        if (slot < 0 || slot >= storedStackSizes.length) {
            return Integer.MAX_VALUE;
        }
        return storedStackSizes[slot];
    }

    public void setStoredStackSizeForGui(int slot, int stackSize) {
        if (slot < 0 || slot >= storedStackSizes.length) {
            return;
        }
        storedStackSizes[slot] = Math.max(1, stackSize);
        updateInformationSlot(slot);
        configureWatchers();
    }

    protected void refreshGuiStateOnOpen() {
        if (!getBaseMetaTileEntity().isServerSide()) {
            return;
        }
        if (autoPullItemList) {
            refreshItemList();
        }
        updateAllInformationSlots();
    }

    protected void clearInformationSlots() {
        for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
            mInventory[i + SIDE_SLOT_COUNT] = null;
        }
    }

    @Override
    public void updateWatcher(IStackWatcher newWatcher) {
        watcher = newWatcher;
        configureWatchers();
    }

    @Override
    public void onStackChange(IItemList stacks, IAEStack fullStack, IAEStack diffStack, BaseActionSource source,
        StorageChannel channel) {
        if (diffStack.getStackSize() > 0) {
            scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
        }
    }

    private void configureWatchers() {
        if (watcher != null) {
            watcher.clear();
            if (MachineStats.machines.useStackWatcher && !autoPullItemList) {
                for (int i = 0; i < SIDE_SLOT_COUNT; i++) {
                    ItemStack stack = mInventory[i];
                    if (stack != null) watcher.add(AEItemStack.create(stack));
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
        for (IHatchWatcher hatchWatcher : watchers) {
            hatchWatcher.scheduleRecipeCheck(reason);
        }
    }

    protected ItemStack createDisplayStack(IAEItemStack stack, int slot) {
        ItemStack displayStack = stack == null ? null : stack.getItemStack();
        if (displayStack == null) {
            return null;
        }
        long requestedSize = storedStackSizes[slot];
        long availableSize = stack.getStackSize();
        displayStack.stackSize = (int) Math.min(Integer.MAX_VALUE, Math.min(availableSize, requestedSize));
        return displayStack.stackSize > 0 ? displayStack : null;
    }
}
