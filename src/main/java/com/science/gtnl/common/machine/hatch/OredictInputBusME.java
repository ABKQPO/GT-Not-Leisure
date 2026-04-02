package com.science.gtnl.common.machine.hatch;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.TextFieldWidget;
import com.science.gtnl.mixins.early.Gregtech.AccessorCommonMetaTileEntity;
import com.science.gtnl.mixins.early.Gregtech.AccessorMetaTileEntity;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.localization.WailaText;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.item.AEItemStack;
import appeng.util.prioitylist.OreFilteredList;
import gregtech.api.enums.GTValues;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.gui.modularui.widget.AESlotWidget;
import gregtech.common.tileentities.machines.IRecipeProcessingAwareHatch;
import gregtech.common.tileentities.machines.ISmartInputHatch;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;

public class OredictInputBusME extends MTEHatchInputBusME implements IRecipeProcessingAwareHatch, IAddGregtechLogo,
    IAddUIWidgets, IPowerChannelState, ISmartInputHatch, IDataCopyable {

    public static int SIDE_SLOT_COUNT = 100;
    public static int ALL_SLOT_COUNT = SIDE_SLOT_COUNT * 2 + 1 + 9 * 9;
    public ItemStack[] shadowInventory;
    public int[] savedStackSizes;

    public static final int CONFIG_WINDOW_ID = 10;
    public static final int MANUAL_SLOT_WINDOW = 11;

    @Nullable
    public String oreDict;
    public boolean isSuper;

    public OredictInputBusME(int aID, String aName, String aNameRegional, boolean isSuper) {
        super(aID, true, aName, aNameRegional);
        this.isSuper = isSuper;
        if (isSuper) {
            shadowInventory = new ItemStack[SIDE_SLOT_COUNT];
            savedStackSizes = new int[SIDE_SLOT_COUNT];
            ((AccessorCommonMetaTileEntity) this).setInventory(new ItemStack[ALL_SLOT_COUNT]);
            ((AccessorMetaTileEntity) this).setInventoryHandler(new ItemStackHandler(mInventory) {

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
            ((AccessorCommonMetaTileEntity) this).setInventory(new ItemStack[ALL_SLOT_COUNT]);
            ((AccessorMetaTileEntity) this).setInventoryHandler(new ItemStackHandler(mInventory) {

                @Override
                public void onContentsChanged(int slot) {
                    OredictInputBusME.this.onContentsChanged(slot);
                }
            });
            disableSort = true;
        }
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OredictInputBusME(mName, autoPullAvailable, mTier, mDescriptionArray, mTextures, isSuper);
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_OredictInputBusME_00"),
            StatCollector.translateToLocal("Tooltip_OredictInputBusME_01") + GTValues.TIER_COLORS[isSuper ? 8 : 6],
            StatCollector.translateToLocal("Tooltip_OredictInputBusME_02"),
            StatCollector.translateToLocalFormatted("Tooltip_OredictInputBusME_03", isSuper ? 100 : 16),
            StatCollector.translateToLocal("Tooltip_OredictInputBusME_04"),
            StatCollector.translateToLocal("Tooltip_OredictInputBusME_05"),
            StatCollector.translateToLocalFormatted("Tooltip_OredictInputBusME_06", isSuper ? 100 : 16),
            StatCollector.translateToLocal("Tooltip_OredictInputBusME_07"),
            StatCollector.translateToLocal("Tooltip_OredictInputBusME_08"),
            StatCollector.translateToLocal("Tooltip_OredictInputBusME_09") };
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
        if (hasFilter()) {
            for (int i = 0; i < SLOT_COUNT; i++) {
                mInventory[i] = null;
            }
        } else {
            refreshItemList();
        }
        updateAllInformationSlots();
    }

    @Override
    public void refreshItemList() {
        if (!isActive()) return;
        AENetworkProxy proxy = getProxy();
        try {
            Predicate<IAEItemStack> oreFilterList = createFilter();

            IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                .getItemInventory();
            Iterator<IAEItemStack> iterator = sg.getStorageList()
                .iterator();

            int index = 0;
            while (iterator.hasNext() && index < (isSuper ? SIDE_SLOT_COUNT : SLOT_COUNT)) {
                IAEItemStack currItem = iterator.next();
                if (currItem == null || oreFilterList == null || !oreFilterList.test(currItem)) continue;

                if (currItem.getStackSize() >= minAutoPullStackSize) {
                    ItemStack itemstack = GTUtility.copyAmount(1, currItem.getItemStack());
                    if (expediteRecipeCheck) {
                        ItemStack previous = this.mInventory[index];
                        if (itemstack != null) {
                            justHadNewItems = !ItemStack.areItemStacksEqual(itemstack, previous);
                        }
                    }
                    this.mInventory[index] = itemstack;
                    index++;
                }
            }
            for (int i = index; i < (isSuper ? SIDE_SLOT_COUNT : SLOT_COUNT); i++) {
                mInventory[i] = null;
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
            updateInformationSlot(index, mInventory[index]);
        }
    }

    /**
     * Update the right side of the GUI, which shows the amounts of items set on the left side
     */
    @Override
    public ItemStack updateInformationSlot(int aIndex, ItemStack aStack) {
        if (!isSuper) return super.updateInformationSlot(aIndex, aStack);
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
                    // We want to track changes in any ItemStack to notify any connected controllers to make a recipe
                    // check early
                    if (expediteRecipeCheck) {
                        ItemStack previous = getStackInSlot(aIndex + SIDE_SLOT_COUNT);
                        if (s != null) {
                            justHadNewItems = !ItemStack.areItemStacksEqual(s, previous);
                        }
                    }
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
    @Override
    public ItemStack getShadowItemStack(int index) {
        if (!isSuper) return super.getShadowItemStack(index);
        if (index < 0 || index >= shadowInventory.length) {
            return null;
        }
        return shadowInventory[index];
    }

    @Override
    public int getShadowInventorySize() {
        if (!isSuper) return super.getShadowInventorySize();
        return shadowInventory.length;
    }

    @Override
    public AENetworkProxy getProxy() {
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
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound nbt) {
        oreDict = nbt.getString("oreDict");
        return super.pasteCopiedData(player, nbt);
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound nbt = super.getCopiedData(player);
        nbt.setString("oreDict", oreDict);

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
    public int getGUIWidth() {
        return isSuper ? 392 : super.getGUIWidth();
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
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        if (!isSuper) {
            super.addUIWidgets(builder, buildContext);
            return;
        }

        buildContext.addSyncedWindow(MANUAL_SLOT_WINDOW, this::createSlotManualWindow);
        final SlotWidget[] aeSlotWidgets = new SlotWidget[100];

        if (autoPullAvailable) {
            buildContext.addSyncedWindow(CONFIG_WINDOW_ID, this::createStackSizeConfigurationWindow);
        }

        final Scrollable scrollable = new Scrollable().setVerticalScroll();
        SlotGroup leftSlotGroup = SlotGroup.ofItemHandler(inventoryHandler, 10)
            .startFromSlot(0)
            .endAtSlot(99)
            .phantom(true)
            .slotCreator(index -> new BaseSlot(inventoryHandler, index, true) {

                @Override
                public boolean isEnabled() {
                    return !autoPullItemList && super.isEnabled();
                }
            })
            .widgetCreator(slot -> (SlotWidget) new SlotWidget(slot) {

                @Override
                public void phantomClick(ClickData clickData, ItemStack cursorStack) {
                    if (clickData.mouseButton != 0 || !getMcSlot().isEnabled()) return;
                    final int aSlotIndex = getMcSlot().getSlotIndex();
                    if (cursorStack == null) {
                        getMcSlot().putStack(null);
                    } else {
                        if (containsSuchStack(cursorStack)) return;
                        getMcSlot().putStack(GTUtility.copyAmount(1, cursorStack));
                    }
                    if (getBaseMetaTileEntity().isServerSide()) {
                        final ItemStack newInfo = updateInformationSlot(aSlotIndex, cursorStack);
                        aeSlotWidgets[getMcSlot().getSlotIndex()].getMcSlot()
                            .putStack(newInfo);
                    }
                }

                @Override
                public IDrawable[] getBackground() {
                    IDrawable slot;
                    if (autoPullItemList) {
                        slot = GTUITextures.SLOT_DARK_GRAY;
                    } else {
                        slot = ModularUITextures.ITEM_SLOT;
                    }
                    return new IDrawable[] { slot, GTUITextures.OVERLAY_SLOT_ARROW_ME };
                }

                @Override
                public List<String> getExtraTooltip() {
                    if (autoPullItemList) {
                        return Collections.singletonList(
                            StatCollector.translateToLocal("GT5U.machines.stocking_bus.cannot_set_slot"));
                    } else {
                        return Collections
                            .singletonList(StatCollector.translateToLocal("modularui.phantom.single.clear"));
                    }
                }

                public boolean containsSuchStack(ItemStack tStack) {
                    for (int i = 0; i < 100; ++i) {
                        if (GTUtility.areStacksEqual(mInventory[i], tStack, false)) return true;
                    }
                    return false;
                }
            }.dynamicTooltip(() -> {
                if (autoPullItemList) {
                    return Collections
                        .singletonList(StatCollector.translateToLocal("GT5U.machines.stocking_bus.cannot_set_slot"));
                } else {
                    return Collections.emptyList();
                }
            })
                .setUpdateTooltipEveryTick(true))
            .build();
        scrollable.widget(leftSlotGroup);

        SlotGroup rightSlotGroup = SlotGroup.ofItemHandler(inventoryHandler, 10)
            .startFromSlot(100)
            .endAtSlot(199)
            .phantom(true)
            .background(GTUITextures.SLOT_DARK_GRAY)
            .widgetCreator(
                slot -> aeSlotWidgets[slot.getSlotIndex() - 100] = new AESlotWidget(slot).disableInteraction())
            .build();
        scrollable.widget(rightSlotGroup.setPos(198, 0));

        builder.widget(
            scrollable.setSize(18 * 21 + 4, 72)
                .setPos(7, 9))
            .widget(
                new DrawableWidget().setDrawable(GTUITextures.PICTURE_ARROW_DOUBLE)
                    .setPos(190, 30)
                    .setSize(12, 12));

        if (autoPullAvailable) {
            builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (clickData.mouseButton == 0) {
                    setAutoPullItemList(!autoPullItemList);
                } else if (clickData.mouseButton == 1 && !widget.isClient()) {
                    widget.getContext()
                        .openSyncedWindow(CONFIG_WINDOW_ID);
                }
            })
                .setBackground(() -> {
                    if (autoPullItemList) {
                        return new IDrawable[] { GTUITextures.BUTTON_STANDARD_PRESSED,
                            GTUITextures.OVERLAY_BUTTON_AUTOPULL_ME };
                    } else {
                        return new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                            GTUITextures.OVERLAY_BUTTON_AUTOPULL_ME_DISABLED };
                    }
                })
                .setSize(16, 16)
                .setPos(188, 10))
                .widget(new FakeSyncWidget.BooleanSyncer(() -> autoPullItemList, this::setAutoPullItemList));
        }

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (clickData.mouseButton == 0) {
                widget.getContext()
                    .openSyncedWindow(MANUAL_SLOT_WINDOW);
            }
        })
            .setPlayClickSound(true)
            .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE)
            .setSize(16, 16)
            .setPos(188, 46));

        builder.widget(TextWidget.dynamicText(() -> {
            boolean isActive = isActive();
            boolean isPowered = isPowered();
            boolean isBooting = isBooting();

            String state = WailaText.getPowerState(isActive, isPowered, isBooting);

            if (isActive && isPowered) {
                return new Text(MessageFormat.format("{0}{1}§f", EnumChatFormatting.GREEN, state));
            } else {
                return new Text(EnumChatFormatting.DARK_RED + state);
            }
        })
            .setTextAlignment(Alignment.Center)
            .setSize(130, 9)
            .setPos(131, 84));
        addGregTechLogo(builder);
    }

    public ModularWindow createSlotManualWindow(final EntityPlayer player) {
        final int WIDTH = 176;
        final int HEIGHT = 86;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))));

        final Scrollable scrollable = new Scrollable().setVerticalScroll();
        SlotGroup slotGroup = SlotGroup.ofItemHandler(inventoryHandler, 9)
            .startFromSlot(201)
            .endAtSlot(ALL_SLOT_COUNT - 1)
            .phantom(false)
            .background(getGUITextureSet().getItemSlot())
            .build();
        scrollable.widget(slotGroup);

        builder.widget(
            scrollable.setSize(18 * 9 + 4, 18 * 4)
                .setPos(7, 7));
        return builder.build();
    }

    @Override
    public ModularWindow createStackSizeConfigurationWindow(EntityPlayer player) {
        final int WIDTH = 78;
        final int HEIGHT = 169;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)));
        builder.widget(
            TextWidget.localised("GT5U.machines.stocking_bus.min_stack_size")
                .setPos(3, 2)
                .setSize(74, 14))
            .widget(
                new NumericWidget().setSetter(val -> minAutoPullStackSize = (int) val)
                    .setGetter(() -> minAutoPullStackSize)
                    .setBounds(1, Integer.MAX_VALUE)
                    .setScrollValues(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(70, 18)
                    .setPos(3, 18)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));
        builder.widget(
            TextWidget.localised("GT5U.machines.stocking_bus.refresh_time")
                .setPos(3, 42)
                .setSize(74, 14))
            .widget(
                new NumericWidget().setSetter(val -> autoPullRefreshTime = (int) val)
                    .setGetter(() -> autoPullRefreshTime)
                    .setBounds(1, Integer.MAX_VALUE)
                    .setScrollValues(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(70, 18)
                    .setPos(3, 58)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));
        builder.widget(
            TextWidget.localised("GT5U.machines.stocking_bus.force_check")
                .setPos(3, 88)
                .setSize(50, 14))
            .widget(
                new CycleButtonWidget().setToggle(() -> expediteRecipeCheck, this::setRecipeCheck)
                    .setTextureGetter(
                        state -> expediteRecipeCheck ? GTUITextures.OVERLAY_BUTTON_CHECKMARK
                            : GTUITextures.OVERLAY_BUTTON_CROSS)
                    .setBackground(GTUITextures.BUTTON_STANDARD)
                    .setPos(53, 87)
                    .setSize(16, 16)
                    .addTooltip(StatCollector.translateToLocal("GT5U.machines.stocking_bus.hatch_warning")));
        builder.widget(
            TextWidget.localised("Info_OredictInputBusME_Oredict")
                .setPos(3, 120)
                .setSize(50, 14))
            .widget(
                new TextFieldWidget().setSetter((value) -> oreDict = value)
                    .setGetter(() -> hasFilter() ? oreDict : "")
                    .setTextAlignment(Alignment.Center)
                    .setScrollBar()
                    .setTextColor(Color.WHITE.normal)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .setPos(3, 136)
                    .setSize(70, 18)
                    .attachSyncer(new FakeSyncWidget.StringSyncer(this::getOreDict, this::setOreDict), builder));
        return builder.build();
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        if (!isSuper) return;
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(367, 81));
    }

    @Override
    public String[] getInfoData() {
        String busStatusKey = getProxy() != null && getProxy().isActive()
            ? StatCollector.translateToLocal("Info_OredictInputBusME_Online")
            : StatCollector.translateToLocalFormatted("Info_OredictInputBusME_Offline", getAEDiagnostics());
        String oreDictStatusKey = hasFilter()
            ? StatCollector.translateToLocalFormatted("Info_OredictInputBusME_Oredict_Set", oreDict)
            : StatCollector.translateToLocal("Info_OredictInputBusME_Oredict_Unset");
        return new String[] { busStatusKey,
            StatCollector.translateToLocal("Info_OredictInputBusME_Oredict") + oreDictStatusKey };
    }

}
