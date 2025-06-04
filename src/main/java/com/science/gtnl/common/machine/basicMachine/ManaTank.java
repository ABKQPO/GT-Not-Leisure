package com.science.gtnl.common.machine.basicMachine;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTUtility.formatNumbers;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.common.materials.MaterialPool;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.widget.FluidLockWidget;
import gregtech.common.tileentities.storage.MTEDigitalTankBase;
import vazkii.botania.api.subtile.ManaHelper;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.block.tile.TileSpecialFlower;
import vazkii.botania.common.block.tile.mana.TilePool;

public class ManaTank extends MTEDigitalTankBase {

    public boolean isLiquidizerMode = true;
    public static final int MANA_POOL_RADIUS = 5;
    public static final int MANA_FLOWER_RADIUS = 6;
    public static FluidStack fluidMana;

    public ManaTank(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 10);
    }

    public ManaTank(String aName, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, 10, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ManaTank(mName, mDescriptionArray, mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public String[] getInfoData() {

        if (mFluid == null) {
            return new String[] { EnumChatFormatting.BLUE + "Mana Tank" + EnumChatFormatting.RESET, "Stored Fluid:",
                EnumChatFormatting.GOLD + "No Fluid" + EnumChatFormatting.RESET,
                EnumChatFormatting.GREEN + "0 L"
                    + EnumChatFormatting.RESET
                    + " "
                    + EnumChatFormatting.YELLOW
                    + GTUtility.formatNumbers(getCapacity())
                    + " L"
                    + EnumChatFormatting.RESET };
        }
        return new String[] { EnumChatFormatting.BLUE + "Mana Tank" + EnumChatFormatting.RESET, "Stored Fluid:",
            EnumChatFormatting.GOLD + mFluid.getLocalizedName() + EnumChatFormatting.RESET,
            EnumChatFormatting.GREEN + GTUtility.formatNumbers(mFluid.amount)
                + " L"
                + EnumChatFormatting.RESET
                + " "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(getCapacity())
                + " L"
                + EnumChatFormatting.RESET };
    }

    @Override
    public void addAdditionalTooltipInformation(ItemStack stack, List<String> tooltip) {
        tooltip.add(StatCollector.translateToLocal("Tooltip_ManaTank_00"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_ManaTank_01"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_ManaTank_02"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_ManaTank_03"));
        if (stack.hasTagCompound()
            && (stack.stackTagCompound.hasKey("mFluid") || stack.stackTagCompound.hasKey("lockedFluidName"))) {
            final FluidStack tContents = FluidStack
                .loadFluidStackFromNBT(stack.stackTagCompound.getCompoundTag("mFluid"));
            if (tContents != null && tContents.amount > 0) {
                tooltip.add(
                    GTLanguageManager.addStringLocalization("TileEntity_TANK_INFO", "Contains Fluid: ")
                        + EnumChatFormatting.YELLOW
                        + tContents.getLocalizedName()
                        + EnumChatFormatting.GRAY);
                tooltip.add(
                    GTLanguageManager.addStringLocalization("TileEntity_TANK_AMOUNT", "Fluid Amount: ")
                        + EnumChatFormatting.GREEN
                        + formatNumbers(tContents.amount)
                        + " L"
                        + EnumChatFormatting.GRAY);
            } else if (stack.stackTagCompound.hasKey("lockedFluidName")) {
                String fluidName = stack.stackTagCompound.getString("lockedFluidName");
                Fluid fluid = FluidRegistry.getFluid(fluidName);
                if (fluid == null) return;
                tooltip.add(
                    StatCollector.translateToLocalFormatted(
                        "GT5U.item.tank.locked_to",
                        EnumChatFormatting.YELLOW + fluid.getLocalizedName()));
            }
        }
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        if (mFluid != null && mFluid.amount >= 0) {
            aNBT.setTag("mFluid", mFluid.writeToNBT(new NBTTagCompound()));
        }
        if (mOutputFluid) aNBT.setBoolean("mOutputFluid", true);
        if (mVoidFluidPart) aNBT.setBoolean("mVoidOverflow", true);
        if (mVoidFluidFull) aNBT.setBoolean("mVoidFluidFull", true);
        aNBT.setBoolean("mLockFluid", true);
        aNBT.setString("lockedFluidName", lockedFluidName);
        if (this.mAllowInputFromOutputSide) aNBT.setBoolean("mAllowInputFromOutputSide", true);

        super.setItemNBT(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("mOutputFluid", this.mOutputFluid);
        aNBT.setBoolean("mVoidOverflow", this.mVoidFluidPart);
        aNBT.setBoolean("mVoidFluidFull", this.mVoidFluidFull);
        aNBT.setBoolean("mLockFluid", mLockFluid);
        aNBT.setString("lockedFluidName", lockedFluidName);
        aNBT.setBoolean("isLiquidizerMode", isLiquidizerMode);
        aNBT.setBoolean("mAllowInputFromOutputSide", this.mAllowInputFromOutputSide);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mOutputFluid = aNBT.getBoolean("mOutputFluid");
        mVoidFluidPart = aNBT.getBoolean("mVoidOverflow");
        mVoidFluidFull = aNBT.getBoolean("mVoidFluidFull");
        mLockFluid = aNBT.getBoolean("mLockFluid");
        isLiquidizerMode = aNBT.getBoolean("isLiquidizerMode");
        setLockedFluidName(aNBT.getString("lockedFluidName"));
        mAllowInputFromOutputSide = aNBT.getBoolean("mAllowInputFromOutputSide");
    }

    @Override
    public String getLockedFluidName() {
        return this.lockedFluidName = "fluidmana";
    }

    @Override
    public void lockFluid(boolean lock) {
        this.mLockFluid = true;
        this.lockedFluidName = "fluidmana";
    }

    @Override
    public boolean isFluidLocked() {
        return true;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (fluidMana == null) fluidMana = MaterialPool.FluidMana.getFluidOrGas(0);
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (isFluidChangingAllowed() && getFillableStack() != null && getFillableStack().amount <= 0)
                setFillableStack(null);

            if (mVoidFluidFull && getFillableStack() != null) {
                mVoidFluidPart = false;
                mLockFluid = true;
                setFillableStack(null);
            }

            if (doesEmptyContainers()) {
                FluidStack tFluid = GTUtility.getFluidForFilledItem(mInventory[getInputSlot()], true);
                if (tFluid != null && isFluidInputAllowed(tFluid)) {
                    if (getFillableStack() == null) {
                        if (isFluidInputAllowed(tFluid)) {
                            if ((tFluid.amount <= getRealCapacity()) || mVoidFluidPart) {
                                tFluid = tFluid.copy();
                                if (aBaseMetaTileEntity.addStackToSlot(
                                    getOutputSlot(),
                                    GTUtility.getContainerForFilledItem(mInventory[getInputSlot()], true),
                                    1)) {
                                    setFillableStack(tFluid);
                                    this.onEmptyingContainerWhenEmpty();
                                    aBaseMetaTileEntity.decrStackSize(getInputSlot(), 1);
                                }
                            }
                        }
                    } else {
                        if (tFluid.isFluidEqual(getFillableStack())) {
                            if ((((long) tFluid.amount + getFillableStack().amount) <= (long) getRealCapacity())
                                || mVoidFluidPart
                                || mVoidFluidFull) {
                                if (aBaseMetaTileEntity.addStackToSlot(
                                    getOutputSlot(),
                                    GTUtility.getContainerForFilledItem(mInventory[getInputSlot()], true),
                                    1)) {
                                    getFillableStack().amount += Math
                                        .min(tFluid.amount, getRealCapacity() - getFillableStack().amount);
                                    aBaseMetaTileEntity.decrStackSize(getInputSlot(), 1);
                                }
                            }
                        }
                    }
                }
            }

            if (doesFillContainers()) {
                ItemStack tOutput = GTUtility
                    .fillFluidContainer(getDrainableStack(), mInventory[getInputSlot()], false, true);
                if (tOutput != null && aBaseMetaTileEntity.addStackToSlot(getOutputSlot(), tOutput, 1)) {
                    FluidStack tFluid = GTUtility.getFluidForFilledItem(tOutput, true);
                    aBaseMetaTileEntity.decrStackSize(getInputSlot(), 1);
                    if (tFluid != null) getDrainableStack().amount -= tFluid.amount;
                    if (getDrainableStack().amount <= 0 && isFluidChangingAllowed()) setDrainableStack(null);
                }
            }
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isClientSide()) return;

        if (aBaseMetaTileEntity.isAllowedToWork() && aTick % 128 == 0) {
            List<TilePool> pools = findManaPoolsInRange(aBaseMetaTileEntity);
            List<SubTileGenerating> flowers = findGeneratingFlowersInRange(aBaseMetaTileEntity);

            if (!flowers.isEmpty()) {
                processFlowerManaTransfer(flowers);
            }

            if (!pools.isEmpty()) {
                if (isLiquidizerMode) {
                    for (TilePool pool : pools) {
                        if (isDropMetaValid(pool)) {
                            fillChamberToMax();
                            return;
                        }
                    }
                    transferManaToChamber(pools);
                } else {
                    transferFluidToPools(pools);
                }
            }
        }
    }

    private boolean isDropMetaValid(TilePool pool) {
        int meta = pool.getBlockMetadata();
        return meta == 1;
    }

    private void fillChamberToMax() {
        int targetMana = getCapacity();
        int currentMana = getFluidAmount();
        if (currentMana < targetMana) {
            int manaToAdd = targetMana - currentMana;
            FluidStack fluidStack = createFluidStack(manaToAdd);
            fill(fluidStack, true);
        }
    }

    private void transferManaToChamber(List<TilePool> pools) {
        int fluidAmount = getFluidAmount();
        int capacity = getCapacity();

        for (TilePool pool : pools) {
            if (isDropMetaValid(pool)) continue;

            int currentMana = pool.getCurrentMana();
            if (currentMana <= 0) continue;

            int manaToTransfer = Math.min(currentMana, capacity - fluidAmount);
            if (manaToTransfer <= 0) continue;

            FluidStack fluidStack = createFluidStack(manaToTransfer);
            int filledAmount = fill(fluidStack, true);
            if (filledAmount > 0) {
                pool.recieveMana(-filledAmount);
            }
        }
    }

    private void transferFluidToPools(List<TilePool> pools) {
        int fluidAmount = getFluidAmount();

        for (TilePool pool : pools) {
            if (isDropMetaValid(pool)) continue;

            int availableSpace = pool.getAvailableSpaceForMana();

            if (availableSpace <= 0) continue;

            int fluidToTransfer = Math.min(fluidAmount, availableSpace);
            if (fluidToTransfer <= 0) continue;

            FluidStack drainedStack = drain(fluidToTransfer, true);
            if (drainedStack != null && drainedStack.amount > 0) {
                pool.recieveMana(drainedStack.amount);
            }
        }
    }

    private void processFlowerManaTransfer(List<SubTileGenerating> flowers) {
        int currentAmount = getFluidAmount();
        int capacity = getCapacity();

        for (SubTileGenerating flower : flowers) {
            int flowerMana = ManaHelper.getMana(flower);
            if (flowerMana <= 0) continue;

            int transferAmount = Math.min(flowerMana, capacity - currentAmount);
            if (transferAmount <= 0) continue;

            ManaHelper.setMana(flower, flowerMana - transferAmount);

            FluidStack fluidStack = createFluidStack(transferAmount);
            int filledAmount = fill(fluidStack, true);

            if (filledAmount > 0) {
                currentAmount += filledAmount;
            }

            TileEntity supertile = ManaHelper.getSupertile(flower);
            if (supertile != null) {
                flower.sync();
            }
        }
    }

    private static FluidStack createFluidStack(int amount) {
        var c = fluidMana.copy();
        c.amount = amount;
        return c;
    }

    private List<TilePool> findManaPoolsInRange(IGregTechTileEntity aBaseMetaTileEntity) {
        World world = aBaseMetaTileEntity.getWorld();
        int x = aBaseMetaTileEntity.getXCoord();
        int y = aBaseMetaTileEntity.getYCoord();
        int z = aBaseMetaTileEntity.getZCoord();

        List<TilePool> pools = new ArrayList<>();

        for (int dx = -MANA_POOL_RADIUS; dx <= MANA_POOL_RADIUS; dx++) {
            for (int dy = -MANA_POOL_RADIUS; dy <= MANA_POOL_RADIUS; dy++) {
                for (int dz = -MANA_POOL_RADIUS; dz <= MANA_POOL_RADIUS; dz++) {
                    TileEntity te = world.getTileEntity(x + dx, y + dy, z + dz);
                    if (te instanceof TilePool pool) {
                        pools.add(pool);
                    }
                }
            }
        }
        return pools;
    }

    private List<SubTileGenerating> findGeneratingFlowersInRange(IGregTechTileEntity aBaseMetaTileEntity) {
        World world = aBaseMetaTileEntity.getWorld();
        int x = aBaseMetaTileEntity.getXCoord();
        int y = aBaseMetaTileEntity.getYCoord();
        int z = aBaseMetaTileEntity.getZCoord();

        List<SubTileGenerating> flowers = new ArrayList<>();

        for (int dx = -MANA_FLOWER_RADIUS; dx <= MANA_FLOWER_RADIUS; dx++) {
            for (int dy = -MANA_FLOWER_RADIUS; dy <= MANA_FLOWER_RADIUS; dy++) {
                for (int dz = -MANA_FLOWER_RADIUS; dz <= MANA_FLOWER_RADIUS; dz++) {
                    TileEntity te = world.getTileEntity(x + dx, y + dy, z + dz);
                    if (te instanceof TileSpecialFlower flowerTile) {
                        SubTileEntity subTile = flowerTile.getSubTile();
                        if (subTile instanceof SubTileGenerating) {
                            flowers.add((SubTileGenerating) subTile);
                        }
                    }
                }
            }
        }
        return flowers;
    }

    @Override
    public void setLockedFluidName(String lockedFluidName) {
        lockedFluidName = "fluidmana";
        this.lockedFluidName = lockedFluidName;
        Fluid fluid = FluidRegistry.getFluid(lockedFluidName);
        if (fluid != null) {
            setFillableStack(new FluidStack(fluid, getFluidAmount()));
            mLockFluid = true;
        }
    }

    @Override
    public int getRealCapacity() {
        return commonSizeCompute(10);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        fluidTank.setAllowOverflow(allowOverflow());
        fluidTank.setPreventDraining(mLockFluid);

        FluidSlotWidget fluidSlotWidget = new FluidSlotWidget(fluidTank);
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(7, 16)
                .setSize(71, 45))
            .widget(
                new SlotWidget(inventoryHandler, getInputSlot())
                    .setBackground(getGUITextureSet().getItemSlot(), GTUITextures.OVERLAY_SLOT_IN)
                    .setPos(79, 16))
            .widget(
                new SlotWidget(inventoryHandler, getOutputSlot()).setAccess(true, false)
                    .setBackground(getGUITextureSet().getItemSlot(), GTUITextures.OVERLAY_SLOT_OUT)
                    .setPos(79, 43))
            .widget(
                fluidSlotWidget.setOnClickContainer(widget -> onEmptyingContainerWhenEmpty())
                    .setBackground(GTUITextures.TRANSPARENT)
                    .setPos(58, 41))
            .widget(
                new TextWidget(StatCollector.translateToLocal("GT5U.machines.digitaltank.fluid.amount"))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setPos(10, 20))
            .widget(
                new TextWidget().setStringSupplier(() -> numberFormat.format(mFluid != null ? mFluid.amount : 0))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setPos(10, 30))
            .widget(
                new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                    .setPos(98, 16)
                    .setSize(71, 45))
            .widget(new FluidLockWidget(this).setPos(149, 41))
            .widget(
                new TextWidget(StatCollector.translateToLocal("GT5U.machines.digitaltank.lockfluid.label"))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setPos(101, 20))
            .widget(TextWidget.dynamicString(() -> {
                FluidStack fluidStack = FluidRegistry.getFluidStack(lockedFluidName, 1);
                return fluidStack != null ? fluidStack.getLocalizedName()
                    : StatCollector.translateToLocal("GT5U.machines.digitaltank.lockfluid.empty");
            })
                .setDefaultColor(COLOR_TEXT_WHITE.get())
                .setTextAlignment(Alignment.CenterLeft)
                .setMaxWidth(65)
                .setPos(101, 30))
            .widget(new CycleButtonWidget().setToggle(() -> mOutputFluid, val -> {
                mOutputFluid = val;
                if (!mOutputFluid) {
                    GTUtility.sendChatToPlayer(
                        buildContext.getPlayer(),
                        GTUtility.trans("262", "Fluid Auto Output Disabled"));
                } else {
                    GTUtility.sendChatToPlayer(
                        buildContext.getPlayer(),
                        GTUtility.trans("263", "Fluid Auto Output Enabled"));
                }
            })
                .setVariableBackground(GTUITextures.BUTTON_STANDARD_TOGGLE)
                .setStaticTexture(GTUITextures.OVERLAY_BUTTON_AUTOOUTPUT_FLUID)
                .setGTTooltip(() -> mTooltipCache.getData("GT5U.machines.digitaltank.autooutput.tooltip"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(7, 63)
                .setSize(18, 18))
            .widget(new CycleButtonWidget().setToggle(() -> mLockFluid, val -> {
                lockFluid(true);
                fluidTank.setPreventDraining(mLockFluid);

                String inBrackets;
                if (mLockFluid) {
                    if (mFluid == null) {
                        setLockedFluidName(null);
                        inBrackets = GTUtility
                            .trans("264", "currently none, will be locked to the next that is put in");
                    } else {
                        setLockedFluidName(
                            getDrainableStack().getFluid()
                                .getName());
                        inBrackets = getDrainableStack().getLocalizedName();
                    }
                    GTUtility.sendChatToPlayer(
                        buildContext.getPlayer(),
                        String.format("%s (%s)", GTUtility.trans("265", "1 specific Fluid"), inBrackets));
                } else {
                    fluidTank.drain(0, true);
                    GTUtility
                        .sendChatToPlayer(buildContext.getPlayer(), GTUtility.trans("266", "Lock Fluid Mode Disabled"));
                }
                fluidSlotWidget.notifyTooltipChange();
            })
                .setVariableBackground(GTUITextures.BUTTON_STANDARD_TOGGLE)
                .setStaticTexture(GTUITextures.OVERLAY_BUTTON_LOCK)
                .setGTTooltip(() -> mTooltipCache.getData("GT5U.machines.digitaltank.lockfluid.tooltip"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(25, 63)
                .setSize(18, 18))
            .widget(new CycleButtonWidget().setToggle(() -> mAllowInputFromOutputSide, val -> {
                mAllowInputFromOutputSide = val;
                if (!mAllowInputFromOutputSide) {
                    GTUtility.sendChatToPlayer(
                        buildContext.getPlayer(),
                        translateToLocal("gt.interact.desc.input_from_output_off"));
                } else {
                    GTUtility.sendChatToPlayer(
                        buildContext.getPlayer(),
                        translateToLocal("gt.interact.desc.input_from_output_on"));
                }
            })
                .setVariableBackground(GTUITextures.BUTTON_STANDARD_TOGGLE)
                .setStaticTexture(GTUITextures.OVERLAY_BUTTON_INPUT_FROM_OUTPUT_SIDE)
                .setGTTooltip(() -> mTooltipCache.getData("GT5U.machines.digitaltank.inputfromoutput.tooltip"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(43, 63)
                .setSize(18, 18))
            .widget(new CycleButtonWidget().setToggle(() -> mVoidFluidPart, val -> {
                mVoidFluidPart = val;
                fluidTank.setAllowOverflow(allowOverflow());
                if (!mVoidFluidPart) {
                    GTUtility.sendChatToPlayer(
                        buildContext.getPlayer(),
                        GTUtility.trans("267", "Overflow Voiding Mode Disabled"));
                } else {
                    GTUtility.sendChatToPlayer(
                        buildContext.getPlayer(),
                        GTUtility.trans("268", "Overflow Voiding Mode Enabled"));
                }
            })
                .setVariableBackground(GTUITextures.BUTTON_STANDARD_TOGGLE)
                .setStaticTexture(GTUITextures.OVERLAY_BUTTON_TANK_VOID_EXCESS)
                .setGTTooltip(() -> mTooltipCache.getData("GT5U.machines.digitaltank.voidoverflow.tooltip"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(98, 63)
                .setSize(18, 18))
            .widget(new CycleButtonWidget().setToggle(() -> mVoidFluidFull, val -> {
                mVoidFluidFull = val;
                fluidTank.setAllowOverflow(allowOverflow());
                if (!mVoidFluidFull) {
                    GTUtility
                        .sendChatToPlayer(buildContext.getPlayer(), GTUtility.trans("269", "Void Full Mode Disabled"));
                } else {
                    GTUtility
                        .sendChatToPlayer(buildContext.getPlayer(), GTUtility.trans("270", "Void Full Mode Enabled"));
                }
            })
                .setVariableBackground(GTUITextures.BUTTON_STANDARD_TOGGLE)
                .setStaticTexture(GTUITextures.OVERLAY_BUTTON_TANK_VOID_ALL)
                .setGTTooltip(() -> mTooltipCache.getData("GT5U.machines.digitaltank.voidfull.tooltip"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(116, 63)
                .setSize(18, 18));
    }
}
