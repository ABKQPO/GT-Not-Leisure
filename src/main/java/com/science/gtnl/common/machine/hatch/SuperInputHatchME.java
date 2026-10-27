package com.science.gtnl.common.machine.hatch;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.fluid.FluidStackTank;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.modularui.hatch.SuperInputHatchMEGui;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.item.AEFluidStack;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
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
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gregtech.common.tileentities.machines.RecipeCheckReason;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class SuperInputHatchME extends MTEHatchInputME implements IConfigurationCircuitSupport {

    public static int SLOT_COUNT = 100;
    public static final FluidStack[] EMPTY_FLUID_STACK = new FluidStack[0];
    public static final int CONFIG_WINDOW_ID = 10;

    public FluidStack[] storedFluids = new FluidStack[SLOT_COUNT];
    public FluidStack[] storedInformationFluids = new FluidStack[SLOT_COUNT];
    public int[] storedStackSizes = new int[SLOT_COUNT];
    {
        Arrays.fill(storedStackSizes, Integer.MAX_VALUE);
    }

    // these two fields should ALWAYS be mutated simultaneously
    // in most cases, you should call setSavedFluid() instead of trying to write to the array directly
    // a desync of these two fields can lead to catastrophe
    public FluidStack[] shadowStoredFluids = new FluidStack[SLOT_COUNT];
    public int[] savedStackSizes = new int[SLOT_COUNT];
    private IStackWatcher watcher;

    public boolean additionalConnection = false;

    public boolean autoPullAvailable;
    public int autoPullRefreshTime = 100;

    public SuperInputHatchME(int aID, boolean autoPullAvailable, String aName, String aNameRegional) {
        super(aID, autoPullAvailable, aName, aNameRegional);
        this.autoPullAvailable = autoPullAvailable;
    }

    public SuperInputHatchME(String aName, boolean autoPullAvailable, int aTier, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, autoPullAvailable, aTier, aDescription, aTextures);
        this.autoPullAvailable = autoPullAvailable;
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return autoPullAvailable ? "gtnl.hatch.super_input_hatch_me.advanced.name"
            : "gtnl.hatch.super_input_hatch_me.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperInputHatchME(mName, autoPullAvailable, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        List<String> strings = new ObjectArrayList<>(8);
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.tooltip.0"));
        strings.add(
            StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.tooltip.1") + " "
                + GTValues.TIER_COLORS[autoPullAvailable ? 10 : 9]
                + GTValues.VN[autoPullAvailable ? 10 : 9]);
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.tooltip.2"));
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.tooltip.3"));

        if (autoPullAvailable) {
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.auto_pull.tooltip.0"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.auto_pull.tooltip.1"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.auto_pull.tooltip.2"));
            strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.auto_pull.tooltip.3"));
        }

        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.tooltip.4"));
        strings.add(StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.tooltip.5"));
        return strings.toArray(new String[0]);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTimer % autoPullRefreshTime == 0 && autoPullFluidList) {
                refreshFluidList();
                updateAllInformationSlots();
            }
            if (aTimer % 20 == 0) {
                aBaseMetaTileEntity.setActive(isActive());
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTimer);
    }

    public void refreshFluidList() {
        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) {
            boolean inputChanged = containsStoredFluid();
            Arrays.fill(storedFluids, null);
            clearInformationFluids();
            if (inputChanged) {
                scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
            }
            return;
        }

        boolean inputChanged = false;
        try {
            IMEMonitor<IAEFluidStack> sg = proxy.getStorage()
                .getFluidInventory();
            Iterator<IAEFluidStack> iterator = sg.getStorageList()
                .iterator();

            int index = 0;
            while (iterator.hasNext() && index < SLOT_COUNT) {
                IAEFluidStack currItem = iterator.next();
                if (currItem.getStackSize() >= minAutoPullAmount) {
                    FluidStack fluidStack = GTUtility.copyAmount(
                        storedStackSizes[index] == Integer.MAX_VALUE ? 1 : storedStackSizes[index],
                        currItem.getFluidStack());
                    inputChanged |= !areFluidStacksEqual(storedFluids[index], fluidStack);
                    storedFluids[index] = fluidStack;
                    index++;
                }
            }

            for (int i = index; i < SLOT_COUNT; i++) {
                inputChanged |= storedFluids[i] != null;
                storedFluids[i] = null;
                storedInformationFluids[i] = null;
            }
        } catch (final GridAccessException ignored) {}
        if (inputChanged) {
            scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
        }
    }

    public void setSavedFluid(int i, FluidStack stack) {
        shadowStoredFluids[i] = stack;
        savedStackSizes[i] = stack == null ? 0 : stack.amount;
    }

    @Override
    public FluidStack[] getStoredFluids() {
        if (!isAllowedToWork()) {
            return EMPTY_FLUID_STACK;
        }

        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) {
            return EMPTY_FLUID_STACK;
        }

        if (!processingRecipe) {
            List<FluidStack> fluids = new ObjectArrayList<>(SLOT_COUNT);
            for (FluidStack fluidStack : storedFluids) {
                if (fluidStack != null) {
                    fluids.add(GTUtility.copyAmount(1, fluidStack));
                }
            }
            return fluids.toArray(EMPTY_FLUID_STACK);
        }

        updateAllInformationSlots();

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (savedStackSizes[i] != 0 || shadowStoredFluids[i] != null) {
                continue;
            }

            if (storedFluids[i] == null) {
                setSavedFluid(i, null);
                continue;
            }

            FluidStack fluidStackWithAmount = storedInformationFluids[i];

            setSavedFluid(i, fluidStackWithAmount);
        }

        return shadowStoredFluids;
    }

    @Override
    public FluidStack drain(ForgeDirection side, FluidStack aFluid, boolean doDrain) {
        return drain(side, aFluid, aFluid == null ? 0 : aFluid.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection side, FluidStack aFluid, int amount, boolean doDrain) {
        // this is an ME input hatch. allowing draining via logistics would be very wrong (and against
        // canTankBeEmptied()) but we do need to support draining from controller, which uses the UNKNOWN direction.
        if (side != ForgeDirection.UNKNOWN) return null;
        if (aFluid == null || amount <= 0) return null;
        FluidStack stored = getMatchingFluidStack(aFluid);
        if (stored == null) return null;
        FluidStack drained = GTUtility.copyAmount(Math.min(stored.amount, amount), stored);
        if (doDrain) {
            stored.amount -= drained.amount;
        }
        return drained;
    }

    @Override
    public void startRecipeProcessing() {
        processingRecipe = true;
        updateAllInformationSlots();
    }

    @Override
    public CheckRecipeResult endRecipeProcessing(MTEMultiBlockBase controller) {
        CheckRecipeResult checkRecipeResult = CheckRecipeResultRegistry.SUCCESSFUL;
        AENetworkProxy proxy = getProxy();

        for (int i = 0; i < SLOT_COUNT; ++i) {
            FluidStack oldStack = shadowStoredFluids[i];
            int toExtract = savedStackSizes[i] - (oldStack != null ? oldStack.amount : 0);
            if (toExtract <= 0) continue;

            try {
                IMEMonitor<IAEFluidStack> sg = proxy.getStorage()
                    .getFluidInventory();

                IAEFluidStack request = AEFluidStack.create(storedFluids[i]);
                request.setStackSize(toExtract);
                IAEFluidStack extractionResult = sg.extractItems(request, Actionable.MODULATE, getRequestSource());
                proxy.getEnergy()
                    .extractAEPower(toExtract, Actionable.MODULATE, PowerMultiplier.CONFIG);

                if (extractionResult == null || extractionResult.getStackSize() != toExtract) {
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    checkRecipeResult = SimpleCheckRecipeResult
                        .ofFailurePersistOnShutdown("stocking_hatch_fail_extraction");
                }
            } catch (GridAccessException ignored) {
                controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                checkRecipeResult = SimpleCheckRecipeResult
                    .ofFailurePersistOnShutdown("stocking_hatch_fail_extraction");
            }
            setSavedFluid(i, null);
            if (storedInformationFluids[i] != null && storedInformationFluids[i].amount <= 0) {
                storedInformationFluids[i] = null;
            }
        }

        processingRecipe = false;
        return checkRecipeResult;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        getProxy().onReady();
    }

    public void updateValidGridProxySides() {
        if (additionalConnection) {
            getProxy().setValidSides(EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN)));
        } else {
            getProxy().setValidSides(EnumSet.of(getBaseMetaTileEntity().getFrontFacing()));
        }
    }

    @Override
    public void onFacingChange() {
        updateValidGridProxySides();
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
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        additionalConnection = !additionalConnection;
        updateValidGridProxySides();
        aPlayer.addChatComponentMessage(
            new ChatComponentTranslation("GT5U.hatch.additionalConnection." + additionalConnection));
        return true;
    }

    @Override
    public boolean connectsToAllSides() {
        return additionalConnection;
    }

    @Override
    public void setConnectsToAllSides(boolean connects) {
        additionalConnection = connects;
        updateValidGridProxySides();
    }

    @Override
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(
                    (IGridProxyable) getBaseMetaTileEntity(),
                    "proxy",
                    autoPullAvailable ? GTNLItemList.AdvancedSuperInputHatchME.get(1)
                        : GTNLItemList.SuperInputHatchME.get(1),
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
        return getProxy() != null && getProxy().isPowered();
    }

    @Override
    public boolean isActive() {
        return getProxy() != null && getProxy().isActive();
    }

    public void setAutoPullFluidList(boolean pullFluidList) {
        if (!autoPullAvailable) {
            return;
        }

        autoPullFluidList = pullFluidList;
        if (!autoPullFluidList) {
            Arrays.fill(storedFluids, null);
        } else {
            refreshFluidList();
        }
        updateAllInformationSlots();
        configureWatchers();
    }

    public void updateAllInformationSlots() {
        for (int index = 0; index < SLOT_COUNT; index++) {
            updateInformationSlot(index);
        }
    }

    @Override
    public void updateInformationSlot(int index) {
        if (index < 0 || index >= SLOT_COUNT) {
            return;
        }

        FluidStack fluidStack = storedFluids[index];
        if (fluidStack == null) {
            storedInformationFluids[index] = null;
            return;
        }

        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) {
            storedInformationFluids[index] = null;
            return;
        }

        if (!isAllowedToWork()) {
            storedInformationFluids[index] = null;
            return;
        }

        try {
            IMEMonitor<IAEFluidStack> sg = proxy.getStorage()
                .getFluidInventory();
            IAEFluidStack request = AEFluidStack.create(fluidStack);
            request.setStackSize(storedStackSizes[index]);
            IAEFluidStack result = sg.extractItems(request, Actionable.SIMULATE, getRequestSource());
            FluidStack resultFluid = (result != null) ? result.getFluidStack() : null;
            storedInformationFluids[index] = resultFluid;
        } catch (final GridAccessException ignored) {}
    }

    public BaseActionSource getRequestSource() {
        if (requestSource == null) requestSource = new MachineSource((IActionHost) getBaseMetaTileEntity());
        return requestSource;
    }

    public FluidStack getMatchingFluidStack(FluidStack fluidStack) {
        if (fluidStack == null) return null;

        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) {
            return null;
        }

        for (int i = 0; i < storedFluids.length; i++) {
            if (storedFluids[i] == null) {
                continue;
            }

            if (GTUtility.areFluidsEqual(fluidStack, storedFluids[i], false)) {
                if (processingRecipe && shadowStoredFluids[i] != null) {
                    return shadowStoredFluids[i].amount > 0 ? shadowStoredFluids[i] : null;
                }

                updateInformationSlot(i);
                if (storedInformationFluids[i] != null) {
                    setSavedFluid(i, storedInformationFluids[i]);
                    return shadowStoredFluids[i];
                }

                setSavedFluid(i, null);
                return null;
            }
        }
        return null;
    }

    /**
     * Used to avoid slot update.
     */
    public FluidStack getShadowFluidStack(int index) {
        if (index < 0 || index >= storedFluids.length) {
            return null;
        }

        return shadowStoredFluids[index];
    }

    /**
     * Gets the first non-null shadow fluid stack.
     *
     * @return The first shadow fluid stack, or null if this doesn't exist.
     */
    public FluidStack getFirstShadowFluidStack() {
        return getFirstShadowFluidStack(false);
    }

    /**
     * Gets the first non-null shadow fluid stack.
     *
     * @param hasToMatchGhost Whether the first fluid stack returned has to match the first non-null ghost stack
     * @return The first shadow fluid stack, or null if this doesn't exist.
     */
    public FluidStack getFirstShadowFluidStack(boolean hasToMatchGhost) {
        FluidStack fluidStack;
        FluidStack lockedSlot = null;
        if (hasToMatchGhost) {
            byte slotToCheck = 0;
            do {
                lockedSlot = storedFluids[slotToCheck];
                slotToCheck++;
            } while (lockedSlot == null && slotToCheck < storedFluids.length);
            if (lockedSlot == null) return null;
        }
        byte slotToCheck = 0;
        do {
            fluidStack = getShadowFluidStack(slotToCheck);
            slotToCheck++;
        } while ((fluidStack == null || !(hasToMatchGhost && lockedSlot.getFluid() == fluidStack.getFluid()))
            && slotToCheck < getShadowStoredFluidsSize());
        return fluidStack;
    }

    public int getShadowStoredFluidsSize() {
        return shadowStoredFluids.length;
    }

    public int getFluidSlot(FluidStack fluidStack) {
        if (fluidStack == null) return -1;

        for (int i = 0; i < storedFluids.length; i++) {
            if (storedFluids[i] == null) {
                continue;
            }

            if (GTUtility.areFluidsEqual(fluidStack, storedFluids[i], false)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        NBTTagList stackSizeList = new NBTTagList();
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            int size = storedStackSizes[i];
            if (size != Integer.MAX_VALUE) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("slot", i);
                tag.setInteger("size", size);
                stackSizeList.appendTag(tag);
            }

            FluidStack fluidStack = storedFluids[i];
            if (fluidStack == null) {
                continue;
            }
            NBTTagCompound fluidTag = fluidStack.writeToNBT(new NBTTagCompound());
            if (storedInformationFluids[i] != null)
                fluidTag.setInteger("informationAmount", storedInformationFluids[i].amount);
            nbtTagList.appendTag(fluidTag);
        }

        aNBT.setTag("storedStackSizes", stackSizeList);
        aNBT.setTag("storedFluids", nbtTagList);
        aNBT.setInteger("refreshTime", autoPullRefreshTime);
        aNBT.setBoolean("additionalConnection", additionalConnection);
        getProxy().writeToNBT(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        if (aNBT.hasKey("storedStackSizes")) {
            NBTTagList list = aNBT.getTagList("storedStackSizes", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slot = tag.getInteger("slot");
                int size = tag.getInteger("size");
                if (slot < SLOT_COUNT) {
                    storedStackSizes[slot] = size;
                }
            }
        }

        if (aNBT.hasKey("storedFluids")) {
            NBTTagList nbtTagList = aNBT.getTagList("storedFluids", 10);
            int c = Math.min(nbtTagList.tagCount(), SLOT_COUNT);
            for (int i = 0; i < c; i++) {
                NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
                FluidStack fluidStack = GTUtility.loadFluid(nbtTagCompound);
                storedFluids[i] = fluidStack;

                if (nbtTagCompound.hasKey("informationAmount")) {
                    int informationAmount = nbtTagCompound.getInteger("informationAmount");
                    storedInformationFluids[i] = GTUtility.copyAmount(informationAmount, fluidStack);
                }
            }
        }

        additionalConnection = aNBT.getBoolean("additionalConnection");
        if (aNBT.hasKey("refreshTime")) {
            autoPullRefreshTime = aNBT.getInteger("refreshTime");
        }
        getProxy().readFromNBT(aNBT);
        updateAE2ProxyColor();
        updateValidGridProxySides();
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (!autoPullAvailable) {
            return;
        }

        setAutoPullFluidList(!autoPullFluidList);
        aPlayer.addChatMessage(
            new ChatComponentTranslation(
                "GT5U.machines.stocking_hatch.auto_pull_toggle." + (autoPullFluidList ? "enabled" : "disabled")));
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound aNBT) {
        if (aNBT == null || !COPIED_DATA_IDENTIFIER.equals(aNBT.getString("type"))) return false;

        if (autoPullAvailable) {
            setAutoPullFluidList(aNBT.getBoolean("autoPull"));
            minAutoPullAmount = aNBT.getInteger("minAmount");
            autoPullRefreshTime = aNBT.getInteger("refreshTime");
        }
        additionalConnection = aNBT.getBoolean("additionalConnection");

        NBTTagList list = aNBT.getTagList("storedStackSizes", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("slot");
            int size = tag.getInteger("size");

            if (slot < SLOT_COUNT) {
                storedStackSizes[slot] = size;
            }
        }

        if (!autoPullFluidList) {
            NBTTagList stockingFluids = aNBT.getTagList("fluidsToStock", 10);
            for (int i = 0; i < stockingFluids.tagCount(); i++) {
                storedFluids[i] = GTUtility.loadFluid(stockingFluids.getCompoundTagAt(i));
            }
        }
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
        aNBT.setBoolean("autoPull", autoPullFluidList);
        aNBT.setInteger("minAmount", minAutoPullAmount);
        aNBT.setBoolean("additionalConnection", additionalConnection);
        aNBT.setInteger("refreshTime", autoPullRefreshTime);
        aNBT.setByte("color", this.getColor());

        NBTTagList stackSizeList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            int size = storedStackSizes[i];
            if (size == Integer.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setInteger("size", size);
            stackSizeList.appendTag(tag);
        }
        aNBT.setTag("storedStackSizes", stackSizeList);

        NBTTagList stockingFluids = new NBTTagList();
        if (!autoPullFluidList) {
            for (int index = 0; index < SLOT_COUNT; index++) {
                FluidStack fluidStack = storedFluids[index];
                if (fluidStack == null) {
                    continue;
                }
                stockingFluids.appendTag(fluidStack.writeToNBT(new NBTTagCompound()));
            }
            aNBT.setTag("fluidsToStock", stockingFluids);
        }
        return aNBT;
    }

    public boolean containsSuchStack(FluidStack tStack) {
        for (int i = 0; i < 100; ++i) {
            if (GTUtility.areFluidsEqual(storedFluids[i], tStack, false)) {
                return true;
            }
        }
        return false;
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
    public int getCircuitSlot() {
        return 0;
    }

    @Override
    public boolean allowSelectCircuit() {
        return true;
    }

    @Override
    public int getCircuitSlotX() {
        return 188;
    }

    @Override
    public int getCircuitSlotY() {
        return 64;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        refreshGuiStateOnOpen();
        return new SuperInputHatchMEGui(this).build(data, syncManager, uiSettings);
    }

    public int getFluidSlotCountForGui() {
        return SLOT_COUNT;
    }

    public boolean isAutoPullFluidListForGui() {
        return autoPullFluidList;
    }

    public int getMinAutoPullAmountForGui() {
        return minAutoPullAmount;
    }

    public void setMinAutoPullAmountForGui(int amount) {
        minAutoPullAmount = Math.max(1, amount);
    }

    public int getAutoPullRefreshTimeForGui() {
        return autoPullRefreshTime;
    }

    public void setAutoPullRefreshTimeForGui(int refreshTime) {
        autoPullRefreshTime = Math.max(1, refreshTime);
    }

    public FluidStack getFilterFluidForGui(int slot) {
        if (slot < 0 || slot >= storedFluids.length) {
            return null;
        }
        return storedFluids[slot];
    }

    public void setFilterFluidForGui(int slot, FluidStack fluid) {
        if (slot < 0 || slot >= storedFluids.length) {
            return;
        }
        storedFluids[slot] = fluid;
        updateInformationSlot(slot);
        configureWatchers();
    }

    public FluidStack getInformationFluidForGui(int slot) {
        if (slot < 0 || slot >= storedInformationFluids.length) {
            return null;
        }
        return storedInformationFluids[slot];
    }

    public boolean containsFluidForGui(FluidStack fluid) {
        return containsSuchStack(fluid);
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
        if (autoPullFluidList) {
            refreshFluidList();
        }
        updateAllInformationSlots();
    }

    protected void clearInformationFluids() {
        Arrays.fill(storedInformationFluids, null);
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
            if (MachineStats.machines.useStackWatcher && !autoPullFluidList) {
                for (FluidStack fluid : storedFluids) {
                    if (fluid != null) watcher.add(AEFluidStack.create(fluid));
                }
            }
        }
        scheduleRecipeCheck(RecipeCheckReason.THROTTLED);
    }

    private boolean containsStoredFluid() {
        for (FluidStack fluid : storedFluids) {
            if (fluid != null) {
                return true;
            }
        }
        return false;
    }

    private boolean areFluidStacksEqual(FluidStack first, FluidStack second) {
        return first == second || first != null && second != null
            && first.amount == second.amount
            && GTUtility.areFluidsEqual(first, second, true);
    }

    private void scheduleRecipeCheck(RecipeCheckReason reason) {
        for (IHatchWatcher hatchWatcher : watchers) {
            hatchWatcher.scheduleRecipeCheck(reason);
        }
    }

    public List<IFluidTank> createFluidTankList(FluidStack[] fluidStacks, int capacity) {
        ObjectArrayList<IFluidTank> fluidTanks = new ObjectArrayList<>(SLOT_COUNT);
        for (int slotIndex = 0; slotIndex < SLOT_COUNT; slotIndex++) {
            fluidTanks.add(createTankForFluidStack(fluidStacks, slotIndex, capacity));
        }
        return fluidTanks;
    }

    public FluidStackTank createTankForFluidStack(FluidStack[] fluidStacks, int slotIndex, int capacity) {
        return new FluidStackTank(() -> fluidStacks[slotIndex], (stack) -> {
            if (getBaseMetaTileEntity().isServerSide()) {
                return;
            }

            fluidStacks[slotIndex] = stack;
        }, capacity);
    }
}
