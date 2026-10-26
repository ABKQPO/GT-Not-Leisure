package com.science.gtnl.common.machine.basicMachine;

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

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.common.gui.modularui.singleblock.ManaTankGui;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.text.LanguageLoader;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.storage.MTEDigitalTankBase;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import vazkii.botania.api.subtile.ManaHelper;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.block.tile.TileSpecialFlower;
import vazkii.botania.common.block.tile.mana.TilePool;

public class ManaTank extends MTEDigitalTankBase {

    public static final int MANA_POOL_RADIUS = 5;
    public static final int MANA_FLOWER_RADIUS = 6;
    public static final String MANA_FLUID_NAME = "fluidmana";
    public static FluidStack fluidMana;

    public boolean isLiquidizerMode = true;

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
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (fluidMana == null) fluidMana = GTNLMaterials.FluidMana.getFluidOrGas(0);
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

    @Override
    public boolean acceptsFluidLock(Fluid fluid) {
        return fluid != null && MANA_FLUID_NAME.equals(fluid.getName());
    }

    @Override
    public int getRealCapacity() {
        return commonSizeCompute(10);
    }

    @Override
    public String[] getInfoData() {

        if (mFluid == null) {
            return new String[] { "gtnl.infodata.mana_tank.name", "gtnl.infodata.stored_fluid",
                "gtnl.infodata.no_fluid",
                IGregTechDeviceInformation.encode(
                    "gtnl.infodata.fluid_amount",
                    EnumChatFormatting.GREEN + "0" + EnumChatFormatting.RESET,
                    EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(getCapacity())
                        + EnumChatFormatting.RESET) };
        }
        return new String[] { "gtnl.infodata.mana_tank.name", "gtnl.infodata.stored_fluid",
            IGregTechDeviceInformation.encode("gtnl.infodata.fluid_name", mFluid.getLocalizedName()),
            IGregTechDeviceInformation.encode(
                "gtnl.infodata.fluid_amount",
                EnumChatFormatting.GREEN + NumberFormatUtil.formatNumber(mFluid.amount) + EnumChatFormatting.RESET,
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(getCapacity()) + EnumChatFormatting.RESET) };
    }

    @Override
    public void addAdditionalTooltipInformation(ItemStack stack, List<String> tooltip) {
        tooltip.add(StatCollector.translateToLocal("gtnl.machine.mana_tank.tooltip.0"));
        tooltip.add(StatCollector.translateToLocal("gtnl.machine.mana_tank.tooltip.1"));
        tooltip.add(StatCollector.translateToLocal("gtnl.machine.mana_tank.tooltip.2"));
        tooltip.add(StatCollector.translateToLocal("gtnl.machine.mana_tank.tooltip.3"));
        if (stack.hasTagCompound()
            && (stack.stackTagCompound.hasKey("mFluid") || stack.stackTagCompound.hasKey("lockedFluidName"))) {
            final FluidStack tContents = FluidStack
                .loadFluidStackFromNBT(stack.stackTagCompound.getCompoundTag("mFluid"));
            if (tContents != null && tContents.amount > 0) {
                LanguageLoader.addGTEnglishName("TileEntity_TANK_INFO", "Contains Fluid: ");
                LanguageLoader.addGTEnglishName("TileEntity_TANK_AMOUNT", "Fluid Amount: ");
                tooltip.add(
                    StatCollector.translateToLocal("TileEntity_TANK_INFO") + EnumChatFormatting.YELLOW
                        + tContents.getLocalizedName()
                        + EnumChatFormatting.GRAY);
                tooltip.add(
                    StatCollector.translateToLocal("TileEntity_TANK_AMOUNT") + EnumChatFormatting.GREEN
                        + NumberFormatUtil.formatNumber(tContents.amount)
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
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new ManaTankGui(this).build(data, syncManager, uiSettings);
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
        aNBT.setString("lockedFluidName", MANA_FLUID_NAME);
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
        aNBT.setString("lockedFluidName", MANA_FLUID_NAME);
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
        setLockedFluid(FluidRegistry.getFluid(MANA_FLUID_NAME));
        mAllowInputFromOutputSide = aNBT.getBoolean("mAllowInputFromOutputSide");
    }

    @Override
    public void setLockedFluid(Fluid lockedFluid) {
        Fluid manaFluid = FluidRegistry.getFluid(MANA_FLUID_NAME);
        if (manaFluid == null) return;

        this.lockedFluid = manaFluid;
        if (getFluidAmount() == 0) {
            setFillableStack(new FluidStack(manaFluid, 0));
        }
        mLockFluid = true;
    }

    @Override
    public void lockFluid(boolean lock) {
        this.mLockFluid = true;
        setLockedFluid(FluidRegistry.getFluid(MANA_FLUID_NAME));
    }

    @Override
    public boolean isFluidLocked() {
        return true;
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

    private List<TilePool> findManaPoolsInRange(IGregTechTileEntity aBaseMetaTileEntity) {
        World world = aBaseMetaTileEntity.getWorld();
        int x = aBaseMetaTileEntity.getXCoord();
        int y = aBaseMetaTileEntity.getYCoord();
        int z = aBaseMetaTileEntity.getZCoord();

        List<TilePool> pools = new ObjectArrayList<>();

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

        List<SubTileGenerating> flowers = new ObjectArrayList<>();

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

    private boolean isDropMetaValid(TilePool pool) {
        int meta = pool.getBlockMetadata();
        return meta == 1;
    }

    private static FluidStack createFluidStack(int amount) {
        var c = fluidMana.copy();
        c.amount = amount;
        return c;
    }
}
