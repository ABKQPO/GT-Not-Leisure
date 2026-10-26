package com.science.gtnl.common.machine.hatch;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.google.common.collect.ImmutableSet;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IRecipeProcessingAwareDualHatch;
import com.science.gtnl.common.gui.modularui.hatch.SuperDualInputHatchMEGui;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.machine.FluidTankG;
import com.science.gtnl.utils.machine.ItemStackG;
import com.science.gtnl.utils.machine.MachineUtils;

import appeng.api.config.Actionable;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.storage.IItemDisplayRegistry.ItemRenderHook;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.objects.GTDualInputPattern;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.config.MachineStats;
import gregtech.common.tileentities.machines.IDualInputHatchWithPattern;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gregtech.common.tileentities.machines.IHatchWatcher;
import gregtech.common.tileentities.machines.ISmartInputHatch;
import gregtech.common.tileentities.machines.RecipeCheckReason;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class SuperDualInputHatchME extends MTEHatchInputBus
    implements IDualInputHatchWithPattern, IRecipeProcessingAwareDualHatch, IDataCopyable, ISmartInputHatch,
    IPowerChannelState, IGridProxyable, IStackWatcherHost {

    public static int SLOT_COUNT = 100;
    public static final String COPIED_DATA_IDENTIFIER = "superStockingDualHatch";

    public static MachineUtils.VargsFunction<Object[], FluidStack[]> asFluidStack = (s) -> Arrays.stream(s)
        .flatMap(Arrays::stream)
        .map(f -> {
            if (f instanceof FluidTank tank) {
                return tank.getFluid();
            } else if (f instanceof FluidStack stack) {
                return stack;
            } else if (f == null) {
                /* ignore */return null;
            } else {
                throw new RuntimeException("only FluidStack or FluidTank are accepted");
            }
        })
        .filter(a -> a != null && a.amount > 0)
        .toArray(FluidStack[]::new);

    public boolean allowAuto;
    public boolean autoPullItemList;
    public boolean recipe;
    public boolean off;
    public boolean additionalConnection;

    public long minAutoPullItemAmount = 1;
    public long minAutoPullFluidAmount = 1;
    public int autoPullRefreshTime = 100;
    public int intmaxs = 3;

    public MachineSource requestSource;
    public AENetworkProxy gridProxy;

    public ItemStack[] i_mark = new ItemStack[SLOT_COUNT];
    public ItemStackG[] i_shadow = new ItemStackG[SLOT_COUNT];
    public ItemStack[] i_display = new ItemStack[SLOT_COUNT];
    public long[] i_saved = new long[SLOT_COUNT];
    public long[] i_stored = new long[SLOT_COUNT];

    public FluidStack[] f_mark = new FluidStack[SLOT_COUNT];
    public FluidTankG[] f_shadow = new FluidTankG[SLOT_COUNT];
    public FluidStack[] f_display = new FluidStack[SLOT_COUNT];
    public long[] f_saved = new long[SLOT_COUNT];
    public long[] f_stored = new long[SLOT_COUNT];

    public long[] i_client = new long[SLOT_COUNT];
    public long[] f_client = new long[SLOT_COUNT];
    private IStackWatcher watcher;
    private boolean wasReady = true;

    public DecimalFormat df2 = new DecimalFormat("#,###.00");
    public DecimalFormat df = new DecimalFormat("#,###");

    public ItemStackHandler inventoryHandlerMark = new ItemStackHandler(i_mark);
    public ItemStackHandler inventoryHandlerDisplay = new ItemStackHandler(i_display);
    @Getter
    private final Mui2ArrayItemHandler mui2FilterItemHandler = new Mui2ArrayItemHandler(i_mark);
    @Getter
    private final Mui2ArrayItemHandler mui2InformationItemHandler = new Mui2ArrayItemHandler(i_display);

    {
        Arrays.fill(i_stored, Long.MAX_VALUE);
        Arrays.fill(f_stored, Long.MAX_VALUE);

        for (int i = 0; i < SLOT_COUNT; i++) {
            f_shadow[i] = new FluidTankG();
        }
    }

    public SuperDualInputHatchME(int id, String name, String nameRegional, int tier, boolean autoPullAvailable) {
        super(id, name, nameRegional, tier, 1, new String[] { "" });
        allowAuto = autoPullAvailable;
    }

    public SuperDualInputHatchME(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures,
        boolean autoPullAvailable) {
        super(aName, aTier, 1, aDescription, aTextures);
        allowAuto = autoPullAvailable;
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return allowAuto ? "gtnl.hatch.super_dual_input_me.advanced.name" : "gtnl.hatch.super_dual_input_me.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public String[] getDescription() {
        List<String> strings = new ObjectArrayList<>(8);
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.tooltip.0"));
        strings.add(
            StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.tooltip.1") + " "
                + GTValues.TIER_COLORS[mTier]
                + GTValues.VN[mTier]);
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.tooltip.2"));
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.tooltip.3"));

        if (allowAuto) {
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.auto_pull.tooltip.0"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.auto_pull.tooltip.1"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.auto_pull.tooltip.2"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.auto_pull.tooltip.3"));
        }

        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.tooltip.4"));
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.tooltip.5"));
        return strings.toArray(new String[0]);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperDualInputHatchME(mName, mTier, mDescriptionArray, mTextures, allowAuto);
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

    public ItemStack updateInformationSlot(int aIndex, ItemStack aStack) {

        if (aStack == null) {
            inventoryHandlerDisplay.setStackInSlot(aIndex, null);
        } else {
            AENetworkProxy proxy = getProxy();
            if (!proxy.isActive()) {
                inventoryHandlerDisplay.setStackInSlot(aIndex, null);
                return null;
            }

            if (!isAllowedToWork()) {
                this.i_shadow[aIndex] = null;
                this.i_saved[aIndex] = 0;
                this.i_client[aIndex] = 0;
                super.setInventorySlotContents(aIndex + SLOT_COUNT, null);
                return null;
            }

            try {
                IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                    .getItemInventory();
                IAEItemStack request = AEItemStack.create(i_mark[aIndex]);
                request.setStackSize(i_stored[aIndex]);
                IAEItemStack result = sg.extractItems(request, Actionable.SIMULATE, getRequestSource());
                ItemStack s = (result != null) ? result.getItemStack() : null;
                if (result != null) {
                    ItemStackG g = ItemStackG.fromAE(result, intmaxs);
                    result.setStackSize(g.stackSize());
                }
                i_client[aIndex] = result == null ? 0 : result.getStackSize();
                inventoryHandlerDisplay.setStackInSlot(aIndex, s);
                return s;
            } catch (GridAccessException ignored) {}
        }

        return null;
    }

    public void updateInformationSlotF(int aIndex) {

        FluidStack fluidStack = f_mark[aIndex];
        if (fluidStack == null) {
            f_display[aIndex] = null;
            return;
        }

        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) {
            f_display[aIndex] = null;
            return;
        }

        if (!isAllowedToWork()) {
            this.f_shadow[aIndex] = null;
            this.f_saved[aIndex] = 0;
            this.f_client[aIndex] = 0;
            return;
        }

        try {
            IMEMonitor<IAEFluidStack> sg = proxy.getStorage()
                .getFluidInventory();
            IAEFluidStack request = AEFluidStack.create(fluidStack);
            request.setStackSize(f_stored[aIndex]);
            IAEFluidStack result = sg.extractItems(request, Actionable.SIMULATE, getRequestSource());
            if (result != null) {
                FluidTankG g = new FluidTankG();
                g.fromAE(result, intmaxs);
                result.setStackSize(g.getFluidAmount());
            }
            FluidStack resultFluid = (result != null) ? result.getFluidStack() : null;
            f_client[aIndex] = result == null ? 0 : result.getStackSize();
            f_display[aIndex] = resultFluid;
        } catch (GridAccessException ignored) {}

    }

    public FluidStackTank createTankForFluidStack(FluidStack[] fluidStacks, int slotIndex, int capacity) {

        class IndexFluidStackTank extends FluidStackTank implements Supplier<Integer> {

            public IndexFluidStackTank(Supplier<FluidStack> getter, Consumer<FluidStack> setter, int capacity) {
                super(getter, setter, capacity);

            }

            @Override
            public Integer get() {

                return slotIndex;
            }
        }

        return new IndexFluidStackTank(() -> fluidStacks[slotIndex], (stack) -> {
            if (getBaseMetaTileEntity().isServerSide()) {
                return;
            }

            fluidStacks[slotIndex] = stack;
        }, capacity);

    }

    public List<IFluidTank> createFluidTankList(FluidStack[] fluidStacks, int capacity) {
        ObjectArrayList<IFluidTank> fluidTanks = new ObjectArrayList<>(SLOT_COUNT);
        for (int slotIndex = 0; slotIndex < SLOT_COUNT; slotIndex++) {
            fluidTanks.add(createTankForFluidStack(fluidStacks, slotIndex, capacity));
        }
        return fluidTanks;
    }

    public boolean isAllowedToWork() {
        IGregTechTileEntity igte = getBaseMetaTileEntity();

        return igte != null && igte.isAllowedToWork();
    }

    @Override
    public int getCircuitSlot() {
        return 0;
    }

    @Override
    public int getCircuitSlotX() {
        return 8;
    }

    @Override
    public int getCircuitSlotY() {
        return 82;
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
        return new SuperDualInputHatchMEGui(this).build(data, syncManager, uiSettings);
    }

    public void setAutoPullItemList(boolean pullItemList) {

        autoPullItemList = pullItemList;
        if (!autoPullItemList) {
            for (int i = 0; i < SLOT_COUNT; i++) {
                i_mark[i] = null;
                f_mark[i] = null;
            }
        } else {
            refreshItemList();
            refreshItemListF();
        }
        updateAllInformationSlots();
        configureWatchers();
    }

    public int getDualSlotCountForGui() {
        return SLOT_COUNT;
    }

    public boolean isAutoPullItemListForGui() {
        return autoPullItemList;
    }

    public long getMinAutoPullItemAmountForGui() {
        return minAutoPullItemAmount;
    }

    public void setMinAutoPullItemAmountForGui(long amount) {
        minAutoPullItemAmount = Math.max(1L, amount);
        scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
    }

    public long getMinAutoPullFluidAmountForGui() {
        return minAutoPullFluidAmount;
    }

    public void setMinAutoPullFluidAmountForGui(long amount) {
        minAutoPullFluidAmount = Math.max(1L, amount);
        scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
    }

    public int getAutoPullRefreshTimeForGui() {
        return autoPullRefreshTime;
    }

    public void setAutoPullRefreshTimeForGui(int refreshTime) {
        autoPullRefreshTime = Math.max(1, refreshTime);
    }

    public int getIntMaxScaleForGui() {
        return intmaxs;
    }

    public void setIntMaxScaleForGui(int value) {
        intmaxs = Math.max(1, Math.min(10_000, value));
        updateAllInformationSlots();
    }

    public ItemStack getFilterItemForGui(int slot) {
        if (slot < 0 || slot >= i_mark.length) {
            return null;
        }
        return i_mark[slot];
    }

    public void setFilterItemForGui(int slot, ItemStack stack) {
        if (slot < 0 || slot >= i_mark.length) {
            return;
        }
        i_mark[slot] = stack;
        inventoryHandlerMark.setStackInSlot(slot, stack);
        updateInformationSlot(slot, stack);
        configureWatchers();
    }

    public ItemStack getInformationItemForGui(int slot) {
        if (slot < 0 || slot >= i_display.length) {
            return null;
        }
        return i_display[slot];
    }

    public long getInformationItemAmountForGui(int slot) {
        if (slot < 0 || slot >= i_client.length) {
            return 0L;
        }
        return i_client[slot];
    }

    public boolean containsFilterItemForGui(ItemStack stack) {
        for (int i = 0; i < SLOT_COUNT; ++i) {
            if (GTUtility.areStacksEqual(i_mark[i], stack, false)) return true;
        }
        return false;
    }

    public long getStoredItemStackSizeForGui(int slot) {
        if (slot < 0 || slot >= i_stored.length) {
            return Long.MAX_VALUE;
        }
        return i_stored[slot];
    }

    public void setStoredItemStackSizeForGui(int slot, long stackSize) {
        if (slot < 0 || slot >= i_stored.length) {
            return;
        }
        i_stored[slot] = Math.max(1L, stackSize);
        updateInformationSlot(slot, i_mark[slot]);
        configureWatchers();
    }

    public FluidStack getFilterFluidForGui(int slot) {
        if (slot < 0 || slot >= f_mark.length) {
            return null;
        }
        return f_mark[slot];
    }

    public void setFilterFluidForGui(int slot, FluidStack fluid) {
        if (slot < 0 || slot >= f_mark.length) {
            return;
        }
        f_mark[slot] = fluid;
        updateInformationSlotF(slot);
        configureWatchers();
    }

    public FluidStack getInformationFluidForGui(int slot) {
        if (slot < 0 || slot >= f_display.length) {
            return null;
        }
        return f_display[slot];
    }

    public long getInformationFluidAmountForGui(int slot) {
        if (slot < 0 || slot >= f_client.length) {
            return 0L;
        }
        return f_client[slot];
    }

    public boolean containsFilterFluidForGui(FluidStack fluid) {
        for (int i = 0; i < SLOT_COUNT; ++i) {
            if (GTUtility.areFluidsEqual(f_mark[i], fluid, false)) {
                return true;
            }
        }
        return false;
    }

    public long getStoredFluidStackSizeForGui(int slot) {
        if (slot < 0 || slot >= f_stored.length) {
            return Long.MAX_VALUE;
        }
        return f_stored[slot];
    }

    public void setStoredFluidStackSizeForGui(int slot, long stackSize) {
        if (slot < 0 || slot >= f_stored.length) {
            return;
        }
        f_stored[slot] = Math.max(1L, stackSize);
        updateInformationSlotF(slot);
        configureWatchers();
    }

    public void refreshItemList() {
        if (!isActive()) {
            clearItemDisplayData();
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
            while (iterator.hasNext() && index < SLOT_COUNT) {
                IAEItemStack currItem = iterator.next();
                if (currItem.getStackSize() >= minAutoPullItemAmount) {
                    ItemStack itemstack = GTUtility.copyAmount(
                        i_stored[index] == Long.MAX_VALUE ? 1 : (int) Math.min(Integer.MAX_VALUE, i_stored[index]),
                        currItem.getItemStack());
                    inputChanged |= !areItemStacksEqual(i_mark[index], itemstack);
                    this.i_mark[index] = itemstack;
                    index++;
                }
            }
            for (int i = index; i < SLOT_COUNT; i++) {
                inputChanged |= i_mark[i] != null;
                i_mark[i] = null;
                i_display[i] = null;
                i_client[i] = 0;
            }

        } catch (GridAccessException ignored) {}
        if (inputChanged) {
            configureWatchers();
        }
    }

    public void refreshItemListF() {
        if (!isActive()) {
            clearFluidDisplayData();
            return;
        }
        boolean inputChanged = false;
        AENetworkProxy proxy = getProxy();
        try {
            IMEMonitor<IAEFluidStack> sg = proxy.getStorage()
                .getFluidInventory();
            Iterator<IAEFluidStack> iterator = sg.getStorageList()
                .iterator();
            int index = 0;
            while (iterator.hasNext() && index < SLOT_COUNT) {
                IAEFluidStack currItem = iterator.next();
                if (currItem.getStackSize() >= minAutoPullFluidAmount) {
                    FluidStack fluidstack = GTUtility.copyAmount(
                        f_stored[index] == Long.MAX_VALUE ? 1 : (int) Math.min(Integer.MAX_VALUE, f_stored[index]),
                        currItem.getFluidStack());
                    inputChanged |= !areFluidStacksEqual(f_mark[index], fluidstack);
                    this.f_mark[index] = fluidstack;
                    index++;
                }
            }

            for (int i = index; i < SLOT_COUNT; i++) {
                inputChanged |= f_mark[i] != null;
                f_mark[i] = null;
                f_display[i] = null;
                f_client[i] = 0;
            }
        } catch (GridAccessException ignored) {}
        if (inputChanged) {
            configureWatchers();
        }
    }

    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (getBaseMetaTileEntity().isServerSide()) {
            boolean ready = isAllowedToWork() && isActive();
            if (ready && !wasReady) scheduleRecipeCheck(RecipeCheckReason.IMMEDIATE);
            wasReady = ready;
            if (aTimer % autoPullRefreshTime == 0 && autoPullItemList) {
                refreshItemList();
                refreshItemListF();
                updateAllInformationSlots();
            }
            if (aTimer % 20 == 0) {
                getBaseMetaTileEntity().setActive(isActive());
            }
        }

        super.onPostTick(aBaseMetaTileEntity, aTimer);
    }

    public BaseActionSource getRequestSource() {
        if (requestSource == null) requestSource = new MachineSource((IActionHost) getBaseMetaTileEntity());
        return requestSource;
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
            if (MachineStats.machines.useStackWatcher) {
                for (ItemStack stack : i_mark) {
                    if (stack != null) watcher.add(AEItemStack.create(stack));
                }
                for (FluidStack fluid : f_mark) {
                    if (fluid != null) watcher.add(AEFluidStack.create(fluid));
                }
            }
        }
        scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
    }

    private boolean areItemStacksEqual(ItemStack first, ItemStack second) {
        return first == second || first != null && second != null
            && first.stackSize == second.stackSize
            && GTUtility.areStacksEqual(first, second);
    }

    private boolean areFluidStacksEqual(FluidStack first, FluidStack second) {
        return first == second || first != null && second != null
            && first.amount == second.amount
            && GTUtility.areFluidsEqual(first, second);
    }

    private void scheduleRecipeCheck(RecipeCheckReason reason) {
        for (IHatchWatcher hatchWatcher : watchers) {
            hatchWatcher.scheduleRecipeCheck(reason);
        }
    }

    @Override
    public void startRecipeProcessing() {
        recipe = true;
        for (int i = 0; i < SLOT_COUNT; i++) {
            i_shadow[i] = null;
            i_saved[i] = 0;
            if (i_mark[i] != null) {

                try {
                    IAEItemStack possible = null;
                    if (i_mark[i] != null) possible = getProxy().getStorage()
                        .getItemInventory()
                        .extractItems(
                            AEItemStack.create(i_mark[i])
                                .setStackSize(i_stored[i]),
                            Actionable.SIMULATE,
                            getRequestSource());
                    i_shadow[i] = possible == null ? null : ItemStackG.fromAE(possible, intmaxs);
                    if (i_shadow[i] != null) i_saved[i] = i_shadow[i].stackSize();

                } catch (GridAccessException ignored) {}
            }
        }
        for (int i = 0; i < SLOT_COUNT; i++) {
            f_shadow[i].setFluid(null);
            f_saved[i] = 0;
            if (f_mark[i] != null) {

                try {
                    IAEFluidStack possible = null;
                    if (f_mark[i] != null) {
                        possible = getProxy().getStorage()
                            .getFluidInventory()
                            .extractItems(
                                AEFluidStack.create(f_mark[i])
                                    .setStackSize(f_stored[i]),
                                Actionable.SIMULATE,
                                getRequestSource());
                    }
                    if (possible != null) {
                        f_shadow[i].fromAE(possible, intmaxs);
                    }
                    if (f_shadow[i] != null) f_saved[i] = f_shadow[i].getFluidAmount();

                } catch (GridAccessException ignored) {}
            }
        }
    }

    @Override
    public CheckRecipeResult endRecipeProcessing(MTEMultiBlockBase controller) {
        recipe = false;
        for (int i = 0; i < SLOT_COUNT; i++) {
            long current = i_shadow[i] == null ? 0 : i_shadow[i].stackSize();
            long original = i_saved[i];
            if (current > original) {
                throw new AssertionError("?");
            }
            if (current < 0) {
                throw new AssertionError("??");
            }
            if (current < original) {
                long delta = original - current;
                if (i_mark[i] == null) {
                    ScienceNotLeisure.LOG.fatal("marked item missing!");
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                }
                IAEItemStack toextract = AEItemStack.create(i_mark[i])
                    .setStackSize(delta);
                try {
                    IAEItemStack get = getProxy().getStorage()
                        .getItemInventory()
                        .extractItems(toextract, Actionable.MODULATE, getRequestSource());
                    if (get == null || get.getStackSize() != get.getStackSize()) {
                        IGregTechTileEntity te = controller.getBaseMetaTileEntity();
                        ScienceNotLeisure.LOG.fatal(
                            "Super Dual Input Hatch ME cannot extract! at {}, {}, {}",
                            te.getXCoord(),
                            te.getYCoord(),
                            te.getZCoord());
                        controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                        return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                    }
                } catch (GridAccessException e) {
                    e.printStackTrace();
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                }

            }

        }

        for (int i = 0; i < SLOT_COUNT; i++) {
            long current = f_shadow[i] == null ? 0 : f_shadow[i].getFluidAmount();
            long original = f_saved[i];
            if (current > original) {
                throw new AssertionError("?");
            }
            if (current < 0) {
                throw new AssertionError("??");
            }
            if (current < original) {
                long delta = original - current;
                if (f_mark[i] == null) {
                    ScienceNotLeisure.LOG.fatal("marked fluid missing!");
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                }
                IAEFluidStack toextract = AEFluidStack.create(f_mark[i])
                    .setStackSize(delta);
                try {
                    IAEFluidStack get = getProxy().getStorage()
                        .getFluidInventory()
                        .extractItems(toextract, Actionable.MODULATE, getRequestSource());
                    if (get == null || get.getStackSize() != get.getStackSize()) {
                        ScienceNotLeisure.LOG.fatal("cannot extract!");
                        controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                        return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                    }
                } catch (GridAccessException e) {
                    e.printStackTrace();
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                }

            }

        }

        updateAllInformationSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public void updateAllInformationSlots() {
        for (int index = 0; index < SLOT_COUNT; index++) {
            updateInformationSlot(index, i_mark[index]);
        }

        for (int index = 0; index < SLOT_COUNT; index++) {
            updateInformationSlotF(index/* , f_mark[index] */);
        }

    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        getProxy().onReady();
    }

    @Override
    public Iterator<? extends IDualInputInventoryWithPattern> inventories() {
        if (off || !recipe || isEmpty()) {
            return Collections.emptyIterator();
        }

        Iterator<? extends IDualInputInventoryWithPattern> itr = getItr();
        return itr != null ? itr : Collections.emptyIterator();
    }

    public boolean isEmpty() {
        for (ItemStackG i : i_shadow) {
            if (i != null && i.stackSize() > 0) return false;
        }
        for (FluidTankG i : f_shadow) {
            if (i != null && i.getFluidAmount() > 0) return false;
        }
        return true;
    }

    public interface NonCachedDualInputInventory extends IDualInputInventoryWithPattern {

        @Override
        default boolean shouldBeCached() {

            return false;
        }
    }

    public Iterator<? extends IDualInputInventoryWithPattern> getItr() {

        IDualInputInventoryWithPattern inventory = new NonCachedDualInputInventory() {

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public ItemStack[] getItemInputs() {
                return MachineUtils.filterStack
                    .apply(MachineUtils.flat(i_shadow), new ItemStack[] { getStackInSlot(0) });
            }

            @Override
            public FluidStack[] getFluidInputs() {
                return asFluidStack.apply(MachineUtils.flat(f_shadow));
            }

            @Override
            public GTDualInputPattern getPatternInputs() {
                return new GTDualInputPattern() {

                    {
                        inputItems = getItemInputs();
                    }
                    {
                        inputFluid = getFluidInputs();
                    }
                };
            }
        };

        return ImmutableSet.of(inventory)
            .iterator();
    }

    @Override
    public Optional<IDualInputInventory> getFirstNonEmptyInventory() {
        if (off) return Optional.empty();
        if (!recipe) {
            return Optional.empty();// huh...
        }
        Iterator<? extends IDualInputInventory> x = inventories();

        return x.hasNext() ? Optional.of(x.next()) : Optional.empty();
    }

    @Override
    public boolean supportsFluids() {
        return true;
    }

    @Override
    public ItemStack[] getSharedItems() {
        return new ItemStack[0];
    }

    public void setProcessingLogic(ProcessingLogic pl) {}

    @Override
    public AENetworkProxy getProxy() {

        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(
                    this,
                    "proxy",
                    allowAuto ? GTNLItemList.AdvancedSuperDualInputHatchME.get(1)
                        : GTNLItemList.SuperDualInputHatchME.get(1),
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
    public boolean isPowered() {
        return getProxy().isPowered();
    }

    @Override
    public boolean isActive() {
        return getProxy().isActive();
    }

    public void updateValidGridProxySides() {

        if (additionalConnection) {
            getProxy().setValidSides(EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN)));
        } else {
            getProxy().setValidSides(EnumSet.of(getBaseMetaTileEntity().getFrontFacing()));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setBoolean("additionalConnection", additionalConnection);
        aNBT.setBoolean("allowAuto", allowAuto);
        getProxy().writeToNBT(aNBT);
        super.saveNBTData(aNBT);

        NBTTagList storedList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            long size = f_stored[i];
            if (size == Long.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setLong("size", size);
            storedList.appendTag(tag);
        }
        aNBT.setTag("f_stored", storedList);

        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            FluidStack fluidStack = f_mark[i];
            if (fluidStack == null) {
                nbtTagList.appendTag(new NBTTagCompound());
                continue;
            }
            NBTTagCompound fluidTag = fluidStack.writeToNBT(new NBTTagCompound());
            if (f_mark[i] != null) fluidTag.setInteger("informationAmount", f_mark[i].amount);
            nbtTagList.appendTag(fluidTag);
        }
        aNBT.setTag("storedFluids", nbtTagList);

        storedList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            long size = i_stored[i];
            if (size == Long.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setLong("size", size);
            storedList.appendTag(tag);
        }
        aNBT.setTag("i_stored", storedList);

        nbtTagList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack fluidStack = i_mark[i];
            if (fluidStack == null) {
                nbtTagList.appendTag(new NBTTagCompound());
                continue;
            }
            NBTTagCompound fluidTag = fluidStack.writeToNBT(new NBTTagCompound());
            if (i_mark[i] != null) fluidTag.setInteger("informationAmount", i_mark[i].stackSize);
            nbtTagList.appendTag(fluidTag);
        }
        aNBT.setTag("storedItems", nbtTagList);

        int[] sizesf = new int[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; ++i) sizesf[i] = f_display[i] == null ? 0 : f_display[i].amount;
        aNBT.setIntArray("sizesF", sizesf);
        int[] sizes = new int[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; ++i) sizes[i] = i_display[i] == null ? 0 : i_display[i].stackSize;
        aNBT.setIntArray("sizes", sizes);

        ByteBuffer b = ByteBuffer.allocate(Long.SIZE / Byte.SIZE * SLOT_COUNT);
        for (long l : i_client) {
            b.putLong(l);
        }
        aNBT.setByteArray("clientDisplayValue", b.array());

        b = ByteBuffer.allocate(Long.SIZE / Byte.SIZE * SLOT_COUNT);
        for (long l : f_client) {
            b.putLong(l);
        }
        aNBT.setByteArray("clientDisplayValueF", b.array());

        aNBT.setLong("itemMinAmount", minAutoPullItemAmount);
        aNBT.setLong("fluidMinAmount", minAutoPullFluidAmount);
        aNBT.setBoolean("autoPull", autoPullItemList);

        aNBT.setInteger("intmaxs", intmaxs);
        aNBT.setInteger("interval", autoPullRefreshTime);

        getProxy().writeToNBT(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        additionalConnection = aNBT.getBoolean("additionalConnection");
        allowAuto = aNBT.getBoolean("allowAuto");
        minAutoPullItemAmount = aNBT.getLong("itemMinAmount");
        minAutoPullFluidAmount = aNBT.getLong("fluidMinAmount");
        getProxy().readFromNBT(aNBT);
        super.loadNBTData(aNBT);

        if (aNBT.hasKey("f_stored")) {
            NBTTagList list = aNBT.getTagList("f_stored", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slot = tag.getInteger("slot");
                long size = tag.getLong("size");
                if (slot < SLOT_COUNT) {
                    f_stored[slot] = size;
                }
            }
        }

        if (aNBT.hasKey("storedFluids")) {
            NBTTagList nbtTagList = aNBT.getTagList("storedFluids", 10);
            int c = Math.min(nbtTagList.tagCount(), SLOT_COUNT);
            for (int i = 0; i < c; i++) {
                NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
                FluidStack fluidStack = GTUtility.loadFluid(nbtTagCompound);
                f_mark[i] = fluidStack;

                if (nbtTagCompound.hasKey("informationAmount")) {
                    int informationAmount = nbtTagCompound.getInteger("informationAmount");
                    f_mark[i] = GTUtility.copyAmount(informationAmount, fluidStack);
                }
            }
        }

        if (aNBT.hasKey("i_stored")) {
            NBTTagList list = aNBT.getTagList("i_stored", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slot = tag.getInteger("slot");
                long size = tag.getLong("size");
                if (slot < SLOT_COUNT) {
                    i_stored[slot] = size;
                }
            }
        }

        if (aNBT.hasKey("storedItems")) {
            NBTTagList nbtTagList = aNBT.getTagList("storedItems", 10);
            int c = Math.min(nbtTagList.tagCount(), SLOT_COUNT);
            for (int i = 0; i < c; i++) {
                NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
                ItemStack fluidStack = GTUtility.loadItem(nbtTagCompound);
                i_mark[i] = fluidStack;

                if (nbtTagCompound.hasKey("informationAmount")) {
                    int informationAmount = nbtTagCompound.getInteger("informationAmount");
                    i_mark[i] = GTUtility.copyAmount(informationAmount, fluidStack);
                }
            }
        }

        if (aNBT.hasKey("sizesF")) {
            int[] size = aNBT.getIntArray("sizesF");
            for (int i = 0; i < SLOT_COUNT; i++) {
                if (f_mark[i] != null) {
                    f_display[i] = f_mark[i].copy();
                    f_display[i].amount = size[i];

                }

            }
        }

        if (aNBT.hasKey("sizes")) {
            int[] size = aNBT.getIntArray("sizes");
            for (int i = 0; i < SLOT_COUNT && i < size.length; i++) {
                if (i_mark[i] != null) {
                    i_display[i] = i_mark[i].copy();
                    i_display[i].stackSize = size[i];
                }

            }
        }
        if (aNBT.hasKey("clientDisplayValue")) {
            ByteBuffer b = ByteBuffer.allocate(8 * SLOT_COUNT);
            b.put(aNBT.getByteArray("clientDisplayValue"));
            b.flip();
            LongBuffer l = b.asLongBuffer();
            for (int i = 0; i < SLOT_COUNT; i++) i_client[i] = l.get();

            b = ByteBuffer.allocate(8 * SLOT_COUNT);
            b.put(aNBT.getByteArray("clientDisplayValueF"));
            b.flip();
            l = b.asLongBuffer();
            for (int i = 0; i < SLOT_COUNT; i++) f_client[i] = l.get();
        }

        autoPullRefreshTime = aNBT.getInteger("interval");
        intmaxs = aNBT.getInteger("intmaxs");
        autoPullItemList = aNBT.getBoolean("autoPull");
    }

    protected void refreshGuiStateOnOpen() {
        if (!getBaseMetaTileEntity().isServerSide()) {
            return;
        }
        if (autoPullItemList) {
            refreshItemList();
            refreshItemListF();
        }
        updateAllInformationSlots();
    }

    protected void clearItemDisplayData() {
        Arrays.fill(i_display, null);
        Arrays.fill(i_client, 0L);
    }

    protected void clearFluidDisplayData() {
        Arrays.fill(f_display, null);
        Arrays.fill(f_client, 0L);
    }

    public long[] getInformationItemAmountsForGui() {
        return i_client.clone();
    }

    public void setInformationItemAmountsForGui(long[] amounts) {
        if (amounts == null) {
            Arrays.fill(i_client, 0L);
            return;
        }
        System.arraycopy(amounts, 0, i_client, 0, Math.min(i_client.length, amounts.length));
        if (amounts.length < i_client.length) {
            Arrays.fill(i_client, amounts.length, i_client.length, 0L);
        }
    }

    public long[] getInformationFluidAmountsForGui() {
        return f_client.clone();
    }

    public void setInformationFluidAmountsForGui(long[] amounts) {
        if (amounts == null) {
            Arrays.fill(f_client, 0L);
            return;
        }
        System.arraycopy(amounts, 0, f_client, 0, Math.min(f_client.length, amounts.length));
        if (amounts.length < f_client.length) {
            Arrays.fill(f_client, amounts.length, f_client.length, 0L);
        }
    }

    public static class Mui2ArrayItemHandler implements IItemHandlerModifiable {

        private final ItemStack[] stacks;

        public Mui2ArrayItemHandler(ItemStack[] stacks) {
            this.stacks = stacks;
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            validateSlot(slot);
            stacks[slot] = stack;
        }

        @Override
        public int getSlots() {
            return stacks.length;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            validateSlot(slot);
            return stacks[slot];
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack == null) {
                return null;
            }
            validateSlot(slot);
            ItemStack existing = stacks[slot];
            if (existing != null && !GTUtility.areStacksEqual(existing, stack, true)) {
                return stack;
            }
            int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
            int existingAmount = existing == null ? 0 : existing.stackSize;
            int insertAmount = Math.min(stack.stackSize, limit - existingAmount);
            if (insertAmount <= 0) {
                return stack;
            }
            if (!simulate) {
                ItemStack changed = existing == null ? stack.copy() : existing.copy();
                changed.stackSize = existingAmount + insertAmount;
                stacks[slot] = changed;
            }
            if (insertAmount == stack.stackSize) {
                return null;
            }
            ItemStack remainder = stack.copy();
            remainder.stackSize -= insertAmount;
            return remainder;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            validateSlot(slot);
            if (amount <= 0 || stacks[slot] == null) {
                return null;
            }
            ItemStack existing = stacks[slot];
            int extractAmount = Math.min(amount, existing.stackSize);
            ItemStack extracted = existing.copy();
            extracted.stackSize = extractAmount;
            if (!simulate) {
                if (extractAmount >= existing.stackSize) {
                    stacks[slot] = null;
                } else {
                    existing.stackSize -= extractAmount;
                }
            }
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            validateSlot(slot);
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            validateSlot(slot);
            return true;
        }

        private void validateSlot(int slot) {
            if (slot < 0 || slot >= stacks.length) {
                throw new RuntimeException("Slot " + slot + " not in valid range");
            }
        }
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {

        return getProxy().getNode();
    }

    @Override
    public void securityBreak() {

    }

    @Override
    public DimensionalCoord getLocation() {

        return new DimensionalCoord((TileEntity) this.getBaseMetaTileEntity());
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return getTexturesInactive(aBaseTexture);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_BUFFER) };
    }

    public static class HookHolder {

        public static ItemRenderHook SKIP_ITEM_STACK_SIZE_HOOK = new ItemRenderHook() {

            @Override
            public boolean renderOverlay(FontRenderer fr, TextureManager tm, ItemStack is, int x, int y) {
                return true;
            }

            @Override
            public boolean showStackSize(ItemStack is) {
                return false;
            }
        };
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack is) {
        additionalConnection = !additionalConnection;
        updateValidGridProxySides();
        aPlayer.addChatComponentMessage(
            new ChatComponentTranslation("GT5U.hatch.additionalConnection." + additionalConnection));
        return true;
    }

    @Override
    public void onEnableWorking() {}

    @Override
    public void onColorChangeServer(byte aColor) {
        updateAE2ProxyColor();
    }

    public void updateAE2ProxyColor() {
        AENetworkProxy proxy = getProxy();
        byte color = this.getColor();
        if (color == -1) {
            proxy.setColor(AEColor.Transparent);
        } else {
            proxy.setColor(AEColor.values()[Dyes.transformDyeIndex(color)]);
        }
        if (proxy.getNode() != null) {
            proxy.getNode()
                .updateState();
        }
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aPlayer instanceof EntityPlayerMP) {
            ItemStack dataStick = aPlayer.inventory.getCurrentItem();
            if (ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) {
                dataStick.stackTagCompound = this.getCopiedData(aPlayer);
                dataStick.setStackDisplayName("Super Dual Input Hatch ME Link Configuration");
                aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.saved"));
            }
        }
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer, ForgeDirection side,
        float aX, float aY, float aZ) {
        if (!(aPlayer instanceof EntityPlayerMP)) {
            this.openGui(aPlayer);
            return this.onRightclick(aBaseMetaTileEntity, aPlayer);
        } else {
            ItemStack dataStick = aPlayer.inventory.getCurrentItem();
            if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) {
                this.openGui(aPlayer);
                return this.onRightclick(aBaseMetaTileEntity, aPlayer);
            } else if (!this.pasteCopiedData(aPlayer, dataStick.stackTagCompound)) {
                return false;
            } else {
                aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.loaded"));
                return true;
            }
        }
    }

    @Override
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return COPIED_DATA_IDENTIFIER;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound aNBT) {
        if (aNBT == null || !COPIED_DATA_IDENTIFIER.equals(aNBT.getString("type"))) return false;
        ItemStack circuit = GTUtility.loadItem(aNBT, "circuit");
        if (GTUtility.isStackInvalid(circuit)) circuit = null;

        NBTTagList list = aNBT.getTagList("f_stored", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("slot");
            long size = tag.getLong("size");
            if (slot < SLOT_COUNT) {
                f_stored[slot] = size;
            }
        }
        list = aNBT.getTagList("i_stored", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("slot");
            long size = tag.getLong("size");
            if (slot < SLOT_COUNT) {
                i_stored[slot] = size;
            }
        }

        if (allowAuto) {
            setAutoPullItemList(aNBT.getBoolean("autoPull"));
            minAutoPullItemAmount = aNBT.getLong("itemMinStackSize");
            minAutoPullFluidAmount = aNBT.getLong("fluidMinStackSize");
            // Data sticks created before refreshTime was implemented should not cause stocking buses to
            // spam divide by zero errors
            if (aNBT.hasKey("refreshTime")) {
                autoPullRefreshTime = aNBT.getInteger("refreshTime");
            }
        }

        additionalConnection = aNBT.getBoolean("additionalConnection");
        if (!autoPullItemList) {
            NBTTagList stockingItems = aNBT.getTagList("itemsToStock", 10);
            for (int i = 0; i < stockingItems.tagCount(); i++) {
                i_mark[i] = GTUtility.loadItem(stockingItems.getCompoundTagAt(i));
            }

            NBTTagList stockingFluids = aNBT.getTagList("fluidsToStock", 10);
            for (int i = 0; i < stockingFluids.tagCount(); i++) {
                f_mark[i] = GTUtility.loadFluid(stockingFluids.getCompoundTagAt(i));
            }
        }
        setInventorySlotContents(getCircuitSlot(), circuit);
        updateValidGridProxySides();
        byte color = aNBT.getByte("color");
        this.getBaseMetaTileEntity()
            .setColorization(color);

        return true;
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("type", COPIED_DATA_IDENTIFIER);
        aNBT.setBoolean("autoPull", autoPullItemList);
        aNBT.setLong("itemMinStackSize", minAutoPullItemAmount);
        aNBT.setLong("fluidMinStackSize", minAutoPullFluidAmount);
        aNBT.setBoolean("additionalConnection", additionalConnection);
        aNBT.setInteger("refreshTime", autoPullRefreshTime);
        aNBT.setByte("color", this.getColor());

        NBTTagList storedList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            long size = f_stored[i];
            if (size == Long.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setLong("size", size);
            storedList.appendTag(tag);
        }
        aNBT.setTag("f_stored", storedList);

        storedList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            long size = i_stored[i];
            if (size == Long.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setLong("size", size);
            storedList.appendTag(tag);
        }
        aNBT.setTag("i_stored", storedList);

        NBTTagList stockingItems = new NBTTagList();
        NBTTagList stockingFluids = new NBTTagList();

        if (!autoPullItemList) {
            for (int index = 0; index < SLOT_COUNT; index++) {
                FluidStack fluidStack = f_mark[index];
                if (fluidStack == null) {
                    continue;
                }
                stockingFluids.appendTag(fluidStack.writeToNBT(new NBTTagCompound()));
            }
            aNBT.setTag("fluidsToStock", stockingFluids);

            for (int index = 0; index < SLOT_COUNT; index++) {
                stockingItems.appendTag(GTUtility.saveItem(i_mark[index]));
            }
            aNBT.setTag("itemsToStock", stockingItems);
        }
        return aNBT;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        if (!allowAuto) {
            super.getWailaBody(itemStack, currenttip, accessor, config);
            return;
        }

        NBTTagCompound tag = accessor.getNBTData();
        boolean autopull = tag.getBoolean("autoPull");
        long itemMinSize = tag.getLong("itemMinAmount");
        long fluidMinSize = tag.getLong("fluidMinAmount");
        currenttip.add(
            StatCollector.translateToLocal("GT5U.waila.stocking_bus.auto_pull." + (autopull ? "enabled" : "disabled")));
        if (autopull) {
            currenttip.add(
                StatCollector.translateToLocalFormatted(
                    "gtnl.hatch.super_dual_input_me.auto_pull.item_min_amount",
                    NumberFormatUtil.formatNumber(itemMinSize)));
            currenttip.add(
                StatCollector.translateToLocalFormatted(
                    "gtnl.hatch.super_dual_input_me.auto_pull.fluid_min_amount",
                    NumberFormatUtil.formatNumber(fluidMinSize)));
        }
        super.getWailaBody(itemStack, currenttip, accessor, config);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        if (!allowAuto) {
            super.getWailaNBTData(player, tile, tag, world, x, y, z);
            return;
        }

        tag.setBoolean("autoPull", autoPullItemList);
        tag.setLong("itemMinAmount", minAutoPullItemAmount);
        tag.setLong("fluidMinAmount", minAutoPullFluidAmount);
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }

    @Override
    public void trunOffME() {
        off = true;
    }

    @Override
    public void trunONME() {
        off = false;
    }

    @Override
    public void onFacingChange() {
        updateValidGridProxySides();
    }

}
