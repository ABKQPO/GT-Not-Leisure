package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.validMTEList;
import static kekztech.common.Blocks.lscLapotronicEnergyUnit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.loader.BlockLoader;

import cofh.api.energy.IEnergyContainerItem;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import tectech.TecTech;
import tectech.thing.casing.TTCasingsContainer;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class EnergyInfuser extends TTMultiblockBase implements IConstructable {

    private final List<ItemStack> mStoredItems = new ArrayList<>();
    private boolean outputAllItems = false;
    private static final int maxRepairedDamagePerOperation = 10000;
    private static final long usedEuPerDurability = 1000;
    private static final int usedUumPerDurability = 1;
    private int tCountCasing;
    private static IStructureDefinition<EnergyInfuser> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String EI_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/energy_infuser";
    public static String[][] shape = StructureUtils.readStructureFromFile(EI_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 2;
    public final int VERTICAL_OFF_SET = 7;
    public final int DEPTH_OFF_SET = 0;

    @Override
    public IStructureDefinition<EnergyInfuser> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<EnergyInfuser>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(BlockLoader.MetaBlockGlass, 2))
                .addElement('B', ofBlock(TTCasingsContainer.sBlockCasingsTT, 0))
                .addElement(
                    'C',
                    buildHatchAdder(EnergyInfuser.class)
                        .atLeast(InputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                        .casingIndex(1028)
                        .dot(1)
                        .buildAndChain(
                            onElementPass(x -> ++x.tCountCasing, ofBlock(TTCasingsContainer.sBlockCasingsTT, 4))))
                .addElement('D', ofBlock(TTCasingsContainer.sBlockCasingsTT, 7))
                .addElement('E', ofFrame(Materials.Osmiridium))
                .addElement('F', ofBlock(lscLapotronicEnergyUnit, 6))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public EnergyInfuser(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eDismantleBoom = true;
    }

    public EnergyInfuser(String aName) {
        super(aName);
        eDismantleBoom = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EnergyInfuser(this.mName);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        return structureCheck_EM(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    @NotNull
    protected CheckRecipeResult checkProcessing_EM() {
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = 1;
        List<ItemStack> toStore = new ArrayList<>();

        for (MTEHatchInputBus inputBus : mInputBusses) {
            if (!inputBus.isValid()) continue;
            for (int i = 0; i < inputBus.getSizeInventory() - 1; i++) {
                ItemStack stack = inputBus.getStackInSlot(i);
                if (stack == null || stack.getItem() == null) continue;
                if (!isItemStackFullyCharged(stack) || !isItemStackFullyRepaired(stack)) {
                    toStore.add(stack.copy());
                    inputBus.decrStackSize(i, stack.stackSize);
                }

            }
        }

        mStoredItems.addAll(toStore);
        saveNBTData(new NBTTagCompound());

        if (!mStoredItems.isEmpty()) {
            return SimpleCheckRecipeResult.ofSuccess("charging");
        }

        if (toStore.isEmpty()) {
            return SimpleCheckRecipeResult.ofFailure("no_chargeable_item");
        }

        return SimpleCheckRecipeResult.ofSuccess("charging");
    }

    @Override
    public void outputAfterRecipe_EM() {
        if (mStoredItems.isEmpty()) {
            afterRecipeCheckFailed();
            return;
        }

        List<ItemStack> remaining = new ArrayList<>();
        long totalEU = getMaxStoredEU();
        long euPerItem = totalEU / mStoredItems.size();

        for (ItemStack stack : mStoredItems) {
            if (stack == null || stack.getItem() == null) continue;

            int stackSize = stack.stackSize;
            ItemStack[] stackArray = new ItemStack[stackSize];
            for (int i = 0; i < stackSize; i++) {
                stackArray[i] = stack.copy();
                stackArray[i].stackSize = 1;
            }

            for (ItemStack individualStack : stackArray) {
                Item item = individualStack.getItem();
                long euRemaining = euPerItem;

                if (item != null && item.isRepairable()) {
                    int currentDamage = item.getDamage(individualStack);
                    if (currentDamage > 0) {
                        int maxRepair = Math.min(currentDamage, maxRepairedDamagePerOperation);
                        long possibleRepair = Math.min(maxRepair, euRemaining / usedEuPerDurability);
                        int uumNeeded = (int) (possibleRepair * usedUumPerDurability);

                        FluidStack availableUUM = getStoredFluids().stream()
                            .filter(
                                fluid -> Materials.UUMatter.getFluid(1)
                                    .isFluidEqual(fluid))
                            .findAny()
                            .orElse(null);

                        if (availableUUM != null
                            && depleteInput(new FluidStack(Materials.UUMatter.mFluid, uumNeeded))) {
                            item.setDamage(individualStack, currentDamage - (int) possibleRepair);
                            euRemaining -= possibleRepair * usedEuPerDurability;
                        }
                    }
                }

                if (item instanceof IElectricItem) {
                    long charged = (long) ElectricItem.manager.charge(
                        individualStack,
                        euRemaining,
                        ((IElectricItem) item).getTier(individualStack),
                        true,
                        false);
                    euRemaining -= charged;
                } else if (TecTech.hasCOFH && item instanceof IEnergyContainerItem) {
                    long rf = Math.min(
                        ((IEnergyContainerItem) item).getMaxEnergyStored(individualStack)
                            - ((IEnergyContainerItem) item).getEnergyStored(individualStack),
                        euRemaining * mEUtoRF / 10L);
                    rf = ((IEnergyContainerItem) item)
                        .receiveEnergy(individualStack, rf > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) rf, false);
                    euRemaining -= rf * 10L / mEUtoRF;
                }

                setEUValue(euPerItem - euRemaining);

                if ((isItemStackFullyCharged(individualStack) && isItemStackFullyRepaired(individualStack))
                    || outputAllItems) {
                    if (addOutput(individualStack)) {
                        continue;
                    }
                }

                remaining.add(individualStack);
            }
        }

        mStoredItems.clear();
        mStoredItems.addAll(remaining);
        saveNBTData(new NBTTagCompound());
        outputAllItems = false;
    }

    private boolean isItemStackFullyCharged(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return true;
        }
        Item item = stack.getItem();

        for (int i = 0; i < stack.stackSize; i++) {
            if (item instanceof IElectricItem) {
                if (ElectricItem.manager.getCharge(stack) < ((IElectricItem) item).getMaxCharge(stack)) {
                    return false;
                }
            } else if (TecTech.hasCOFH && item instanceof IEnergyContainerItem) {
                if (((IEnergyContainerItem) item).getEnergyStored(stack)
                    < ((IEnergyContainerItem) item).getMaxEnergyStored(stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isItemStackFullyRepaired(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return true;
        }
        Item item = stack.getItem();

        for (int i = 0; i < stack.stackSize; i++) {
            if (item.isRepairable() && item.getDamage(stack) > 0) {
                return false;
            }
        }
        return true;
    }

    protected long getMaxStoredEU() {
        long maxStoredEU = 0;

        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            maxStoredEU = Math.max(
                maxStoredEU,
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU());
        }

        for (MTEHatchEnergyMulti tHatch : validMTEList(eEnergyMulti)) {
            maxStoredEU = Math.max(
                maxStoredEU,
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU());
        }

        return maxStoredEU;
    }

    protected void setEUValue(long newEnergyValue) {
        long maxStoredEU = 0;
        MTEHatchEnergy targetEnergyHatch = null;
        MTEHatchEnergyMulti targetEnergyMulti = null;

        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            long storedEU = tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (storedEU > maxStoredEU) {
                maxStoredEU = storedEU;
                targetEnergyHatch = tHatch;
            }
        }

        for (MTEHatchEnergyMulti tHatch : validMTEList(eEnergyMulti)) {
            long storedEU = tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (storedEU > maxStoredEU) {
                maxStoredEU = storedEU;
                targetEnergyMulti = tHatch;
            }
        }

        if (targetEnergyHatch != null) {
            targetEnergyHatch.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(newEnergyValue, false);
        } else if (targetEnergyMulti != null) {
            targetEnergyMulti.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(newEnergyValue, false);
        }
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        outputAllItems = true;
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EnergyInfuser_00" + this.machineMode));
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        NBTTagList storedItemsList = new NBTTagList();
        for (ItemStack stack : mStoredItems) {
            if (stack != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                stack.writeToNBT(itemTag);
                storedItemsList.appendTag(itemTag);
            }
        }
        aNBT.setTag("mStoredItems", storedItemsList);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        NBTTagList storedItemsList = aNBT.getTagList("mStoredItems", 10);
        mStoredItems.clear();
        for (int i = 0; i < storedItemsList.tagCount(); i++) {
            NBTTagCompound itemTag = storedItemsList.getCompoundTagAt(i);
            ItemStack stack = ItemStack.loadItemStackFromNBT(itemTag);
            if (stack != null) {
                mStoredItems.add(stack);
            }
        }
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("EnergyInfuserRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EnergyInfuser_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EnergyInfuser_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EnergyInfuser_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EnergyInfuser_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EnergyInfuser_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(5, 8, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EnergyInfuser_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EnergyInfuser_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EnergyInfuser_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_EnergyInfuser_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_WHOOUM;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM(
            STRUCTURE_PIECE_MAIN,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            stackSize,
            hintsOnly);
    }

    @Override
    public boolean isSafeVoidButtonEnabled() {
        return false;
    }

    @Override
    public long maxEUStore() {
        return 0;
    }

    @Override
    protected void chargeController_EM(IGregTechTileEntity aBaseMetaTileEntity) {}

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }
}
