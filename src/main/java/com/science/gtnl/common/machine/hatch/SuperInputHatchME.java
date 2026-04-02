package com.science.gtnl.common.machine.hatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Interactable;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.core.localization.WailaText;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.item.AEFluidStack;
import gregtech.api.enums.GTValues;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.MTEHatchInputME;

public class SuperInputHatchME extends MTEHatchInputME {

    public static int SLOT_COUNT = 100;

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

    public boolean additionalConnection = false;

    public boolean autoPullAvailable;
    public int autoPullRefreshTime = 100;
    public boolean justHadNewFluids = false;
    public boolean expediteRecipeCheck = false;

    public static final FluidStack[] EMPTY_FLUID_STACK = new FluidStack[0];

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
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperInputHatchME(mName, autoPullAvailable, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        List<String> strings = new ArrayList<>(8);
        strings.add(StatCollector.translateToLocal("Tooltip_SuperInputHatchME_00"));
        strings.add(
            StatCollector.translateToLocal("Tooltip_SuperInputHatchME_01")
                + GTValues.TIER_COLORS[autoPullAvailable ? 10 : 9]
                + GTValues.VN[autoPullAvailable ? 10 : 9]);
        strings.add(StatCollector.translateToLocal("Tooltip_SuperInputHatchME_02"));
        strings.add(StatCollector.translateToLocal("Tooltip_SuperInputHatchME_03"));

        if (autoPullAvailable) {
            strings.add(StatCollector.translateToLocal("Tooltip_AdvancedSuperInputHatchME_00"));
            strings.add(StatCollector.translateToLocal("Tooltip_AdvancedSuperInputHatchME_01"));
            strings.add(StatCollector.translateToLocal("Tooltip_AdvancedSuperInputHatchME_02"));
            strings.add(StatCollector.translateToLocal("Tooltip_AdvancedSuperInputHatchME_03"));
        }

        strings.add(StatCollector.translateToLocal("Tooltip_SuperInputHatchME_04"));
        strings.add(StatCollector.translateToLocal("Tooltip_SuperInputHatchME_05"));
        return strings.toArray(new String[0]);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTimer % autoPullRefreshTime == 0 && autoPullFluidList) {
                refreshFluidList();
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
            return;
        }

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
                    if (expediteRecipeCheck) {
                        FluidStack previous = storedFluids[index];
                        if (fluidStack != null && previous != null) {
                            justHadNewFluids = !fluidStack.isFluidEqual(previous);
                        }
                    }
                    storedFluids[index] = fluidStack;
                    index++;
                }
            }

            for (int i = index; i < SLOT_COUNT; i++) {
                storedFluids[i] = null;
            }
        } catch (final GridAccessException ignored) {}
    }

    @Override
    public void setSavedFluid(int i, FluidStack stack) {
        shadowStoredFluids[i] = stack;
        savedStackSizes[i] = stack == null ? 0 : stack.amount;
    }

    @Override
    public FluidStack[] getStoredFluids() {
        if (!processingRecipe) {
            return EMPTY_FLUID_STACK;
        }

        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) {
            return EMPTY_FLUID_STACK;
        }

        updateAllInformationSlots();

        for (int i = 0; i < SLOT_COUNT; i++) {
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
    public boolean justUpdated() {
        if (expediteRecipeCheck && isAllowedToWork()) {
            boolean ret = justHadNewFluids;
            justHadNewFluids = false;
            return ret;
        }
        return false;
    }

    @Override
    public FluidStack drain(ForgeDirection side, FluidStack aFluid, boolean doDrain) {
        // this is an ME input hatch. allowing draining via logistics would be very wrong (and against
        // canTankBeEmptied()) but we do need to support draining from controller, which uses the UNKNOWN direction.
        if (side != ForgeDirection.UNKNOWN) return null;
        FluidStack stored = getMatchingFluidStack(aFluid);
        if (stored == null) return null;
        FluidStack drained = GTUtility.copyAmount(Math.min(stored.amount, aFluid.amount), stored);
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
    }

    @Override
    public boolean doFastRecipeCheck() {
        return expediteRecipeCheck;
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
            // We want to track if any FluidStack is modified to notify any connected controllers to make a recipe check
            // early
            if (expediteRecipeCheck) {
                FluidStack previous = storedInformationFluids[index];
                if (resultFluid != null) {
                    justHadNewFluids = !resultFluid.isFluidEqual(previous);
                }
            }
            storedInformationFluids[index] = resultFluid;
        } catch (final GridAccessException ignored) {}
    }

    public BaseActionSource getRequestSource() {
        if (requestSource == null) requestSource = new MachineSource((IActionHost) getBaseMetaTileEntity());
        return requestSource;
    }

    @Override
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
    @Override
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
    @Override
    public FluidStack getFirstShadowFluidStack() {
        return getFirstShadowFluidStack(false);
    }

    /**
     * Gets the first non-null shadow fluid stack.
     *
     * @param hasToMatchGhost Whether the first fluid stack returned has to match the first non-null ghost stack
     * @return The first shadow fluid stack, or null if this doesn't exist.
     */
    @Override
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

    @Override
    public int getShadowStoredFluidsSize() {
        return shadowStoredFluids.length;
    }

    @Override
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
        aNBT.setBoolean("expediteRecipeCheck", expediteRecipeCheck);
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
        expediteRecipeCheck = aNBT.getBoolean("expediteRecipeCheck");
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
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return COPIED_DATA_IDENTIFIER;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound aNBT) {
        if (aNBT == null || !COPIED_DATA_IDENTIFIER.equals(aNBT.getString("type"))) return false;

        if (autoPullAvailable) {
            setAutoPullFluidList(aNBT.getBoolean("autoPull"));
            minAutoPullAmount = aNBT.getInteger("minAmount");
            autoPullRefreshTime = aNBT.getInteger("refreshTime");
            expediteRecipeCheck = aNBT.getBoolean("expediteRecipeCheck");
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
        aNBT.setBoolean("expediteRecipeCheck", expediteRecipeCheck);
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

    @Override
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
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        if (autoPullAvailable) {
            buildContext.addSyncedWindow(CONFIG_WINDOW_ID, this::createStackSizeConfigurationWindow);
        }

        for (int i = 15; i < SLOT_COUNT + 15; i++) {
            int slotID = i;
            buildContext.addSyncedWindow(i, (player) -> createStroedStackSizeWindow(player, slotID - 15));
        }

        final Scrollable scrollable = new Scrollable().setVerticalScroll();
        SlotGroup leftFluidSlotGroup = SlotGroup.ofFluidTanks(
            IntStream.range(0, SLOT_COUNT)
                .mapToObj(index -> createTankForFluidStack(storedFluids, index, 1))
                .collect(Collectors.toList()),
            10)
            .phantom(true)
            .widgetCreator((slotIndex, h) -> (FluidSlotWidget) new FluidSlotWidget(h) {

                @Override
                public void tryClickPhantom(ClickData clickData, ItemStack cursorStack) {
                    IGregTechTileEntity gtTE = getBaseMetaTileEntity();
                    if (gtTE.isServerSide() && clickData.mouseButton == 2) {
                        getContext().openSyncedWindow(slotIndex + 15);
                        return;
                    }

                    if (clickData.mouseButton != 0 || autoPullFluidList) return;

                    FluidStack heldFluid = getFluidForPhantomItem(cursorStack);
                    if (cursorStack == null) {
                        storedFluids[slotIndex] = null;
                    } else {
                        if (containsSuchStack(heldFluid)) return;
                        storedFluids[slotIndex] = heldFluid;
                    }
                    if (gtTE.isServerSide()) {
                        updateInformationSlot(slotIndex);
                        detectAndSendChanges(false);
                    }
                }

                @Override
                public void tryScrollPhantom(int direction) {}

                @Override
                public IDrawable[] getBackground() {
                    IDrawable slot;
                    if (autoPullFluidList) {
                        slot = GTUITextures.SLOT_DARK_GRAY;
                    } else {
                        slot = ModularUITextures.FLUID_SLOT;
                    }
                    return new IDrawable[] { slot, GTUITextures.OVERLAY_SLOT_ARROW_ME };
                }

                @Override
                public void buildTooltip(List<Text> tooltip) {
                    FluidStack fluid = getContent();
                    if (fluid != null) {
                        addFluidNameInfo(tooltip, fluid);

                        if (!autoPullFluidList) {
                            tooltip.add(Text.localised("modularui.phantom.single.clear"));
                        }
                    } else {
                        tooltip.add(
                            Text.localised("modularui.fluid.empty")
                                .format(EnumChatFormatting.WHITE));
                    }

                    if (autoPullFluidList) {
                        tooltip.add(Text.localised("GT5U.machines.stocking_bus.cannot_set_slot"));
                    }
                }
            }.setUpdateTooltipEveryTick(true))
            .build();
        scrollable.widget(leftFluidSlotGroup);

        SlotGroup rightFluidSlotGroup = SlotGroup.ofFluidTanks(
            IntStream.range(0, SLOT_COUNT)
                .mapToObj(index -> createTankForFluidStack(storedInformationFluids, index, Integer.MAX_VALUE))
                .collect(Collectors.toList()),
            10)
            .phantom(true)
            .widgetCreator((slotIndex, h) -> (FluidSlotWidget) new FluidSlotWidget(h) {

                @Override
                public void tryClickPhantom(ClickData clickData, ItemStack cursorStack) {}

                @Override
                public void tryScrollPhantom(int direction) {}

                @Override
                public void buildTooltip(List<Text> tooltip) {
                    FluidStack fluid = getContent();
                    if (fluid != null) {
                        addFluidNameInfo(tooltip, fluid);
                        tooltip.add(Text.localised("modularui.fluid.phantom.amount", fluid.amount));
                        addAdditionalFluidInfo(tooltip, fluid);
                        if (!Interactable.hasShiftDown()) {
                            tooltip.add(Text.EMPTY);
                            tooltip.add(Text.localised("modularui.tooltip.shift"));
                        }
                    } else {
                        tooltip.add(
                            Text.localised("modularui.fluid.empty")
                                .format(EnumChatFormatting.WHITE));
                    }
                }
            }.setUpdateTooltipEveryTick(true))
            .background(GTUITextures.SLOT_DARK_GRAY)
            .controlsAmount(true)
            .build();
        scrollable.widget(rightFluidSlotGroup.setPos(198, 0));

        builder.widget(
            scrollable.setSize(18 * 21 + 4, 72)
                .setPos(7, 9));

        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_ARROW_DOUBLE)
                .setPos(190, 30)
                .setSize(12, 12));

        if (autoPullAvailable) {
            builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (clickData.mouseButton == 0) {
                    setAutoPullFluidList(!autoPullFluidList);
                } else if (clickData.mouseButton == 1 && !widget.isClient()) {
                    widget.getContext()
                        .openSyncedWindow(CONFIG_WINDOW_ID);
                }
            })
                .setPlayClickSound(true)
                .setBackground(() -> {
                    if (autoPullFluidList) {
                        return new IDrawable[] { GTUITextures.BUTTON_STANDARD_PRESSED,
                            GTUITextures.OVERLAY_BUTTON_AUTOPULL_ME };
                    } else {
                        return new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                            GTUITextures.OVERLAY_BUTTON_AUTOPULL_ME_DISABLED };
                    }
                })
                .addTooltips(
                    Arrays.asList(
                        StatCollector.translateToLocal("GT5U.machines.stocking_hatch.auto_pull.tooltip.1"),
                        StatCollector.translateToLocal("GT5U.machines.stocking_hatch.auto_pull.tooltip.2")))
                .setSize(16, 16)
                .setPos(188, 10))
                .widget(new FakeSyncWidget.BooleanSyncer(() -> autoPullFluidList, this::setAutoPullFluidList));
        }

        builder.widget(TextWidget.dynamicText(() -> {
            boolean isActive = isActive();
            boolean isPowered = isPowered();
            boolean isBooting = isBooting();

            String state = WailaText.getPowerState(isActive, isPowered, isBooting);

            if (isActive && isPowered) {
                return Text.localised("{0}{1}§f", EnumChatFormatting.GREEN, state);
            } else {
                return new Text(EnumChatFormatting.DARK_RED + state);
            }
        })
            .setTextAlignment(Alignment.Center)
            .setSize(130, 9)
            .setPos(131, 84));
        addGregTechLogo(builder);
    }

    public FluidStackTank createTankForFluidStack(FluidStack[] fluidStacks, int slotIndex, int capacity) {
        return new FluidStackTank(() -> fluidStacks[slotIndex], (stack) -> {
            if (getBaseMetaTileEntity().isServerSide()) {
                return;
            }

            fluidStacks[slotIndex] = stack;
        }, capacity);
    }

    @Override
    public ModularWindow createStackSizeConfigurationWindow(final EntityPlayer player) {
        final int WIDTH = 78;
        final int HEIGHT = 115;
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
            TextWidget.localised("GT5U.machines.stocking_hatch.min_amount")
                .setPos(3, 2)
                .setSize(74, 14))
            .widget(
                new NumericWidget().setSetter(val -> minAutoPullAmount = (int) val)
                    .setGetter(() -> minAutoPullAmount)
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
        return builder.build();
    }

    public ModularWindow createStroedStackSizeWindow(EntityPlayer player, int slotID) {
        final int WIDTH = 110;
        final int HEIGHT = 66;
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
            TextWidget.localised("Info_SuperInputHatchME_00")
                .setPos(3, 6)
                .setSize(106, 14))
            .widget(
                new TextWidget(StatCollector.translateToLocal("Info_SuperInputHatchME_01") + slotID).setPos(3, 20)
                    .setSize(106, 14))
            .widget(
                new NumericWidget().setSetter(val -> storedStackSizes[slotID] = (int) val)
                    .setGetter(() -> storedStackSizes[slotID])
                    .setBounds(1, Integer.MAX_VALUE)
                    .setScrollValues(1, 1000, 10000)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(106, 18)
                    .setPos(3, 36)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .attachSyncer(
                        new FakeSyncWidget.IntegerSyncer(
                            () -> storedStackSizes[slotID],
                            i -> storedStackSizes[slotID] = i),
                        builder));
        return builder.build();
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(367, 81));
    }
}
