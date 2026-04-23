package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static kekztech.common.Blocks.lscLapotronicEnergyUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.brandon3055.draconicevolution.common.blocks.itemblocks.DraconiumItemBlock;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.internal.network.NetworkUtils;
import com.gtnewhorizons.modularui.common.widget.ChangeableWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.item.ItemUtils;

import cofh.api.energy.IEnergyContainerItem;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.WirelessNetworkManager;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import tectech.thing.casing.TTCasingsContainer;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class EnergyInfuser extends TTMultiblockBase implements IConstructable, ISurvivalConstructable {

    public List<ItemStack> mStoredItems = new ArrayList<>();
    public boolean outputAllItems = false;
    public static final int maxRepairedDamagePerOperation = 10000;
    public static final long usedEuPerDurability = 1000;
    public static final int usedUumPerDurability = 1;
    public int mCountCasing;
    public UUID ownerUUID;
    public boolean wirelessMode;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EI_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/energy_infuser";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EI_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 7;
    private static final int DEPTH_OFF_SET = 0;

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
    public IStructureDefinition<EnergyInfuser> getStructure_EM() {
        return StructureDefinition.<EnergyInfuser>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('B', ofBlock(TTCasingsContainer.sBlockCasingsTT, 0))
            .addElement(
                'C',
                buildHatchAdder(EnergyInfuser.class)
                    .atLeast(InputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                    .casingIndex(1028)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(x -> ++x.mCountCasing, ofBlock(TTCasingsContainer.sBlockCasingsTT, 4))))
            .addElement('D', ofBlock(TTCasingsContainer.sBlockCasingsTT, 7))
            .addElement('E', ofFrame(Materials.Osmiridium))
            .addElement('F', ofBlock(lscLapotronicEnergyUnit, 6))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        wirelessMode = false;
        if (!structureCheck_EM(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        wirelessMode = mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty() && eEnergyMulti.isEmpty();
        return true;
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick_EM(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing_EM() {
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = 1;
        List<ItemStack> toStore = new ArrayList<>();

        for (ItemStack stack : getAllStoredInputs()) {
            if (ItemUtil.isStackInvalid(stack)) continue;
            if (!isItemStackFullyCharged(stack) || !isItemStackFullyRepaired(stack)) {
                toStore.add(stack.copy());
                stack.stackSize = 0;
            }
        }

        mStoredItems.addAll(toStore);

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
            if (ItemUtil.isStackInvalid(stack)) continue;

            int stackSize = stack.stackSize;
            ItemStack[] stackArray = new ItemStack[stackSize];
            for (int i = 0; i < stackSize; i++) {
                stackArray[i] = stack.copy();
                stackArray[i].stackSize = 1;
            }

            for (ItemStack individualStack : stackArray) {
                Item item = individualStack.getItem();

                if (item != null && item.isRepairable()) {
                    int currentDamage = item.getDamage(individualStack);
                    if (currentDamage > 0) {
                        int maxRepair = Math.min(currentDamage, maxRepairedDamagePerOperation);
                        long possibleRepair = Math.min(maxRepair, euPerItem / usedEuPerDurability);
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
                            decreaseEUValue(possibleRepair * usedEuPerDurability);
                        }
                    }
                }

                if (item instanceof IElectricItem electricItem) {
                    double missingItemCharge = electricItem.getMaxCharge(individualStack)
                        - ElectricItem.manager.getCharge(individualStack);
                    double charge = Math.min(missingItemCharge, euPerItem);
                    long charged = (long) Math.ceil(
                        ElectricItem.manager
                            .charge(individualStack, charge, electricItem.getTier(individualStack), true, false));
                    decreaseEUValue(charged);
                } else if (Mods.COFHCore.isModLoaded() && item instanceof IEnergyContainerItem energyContainerItem) {
                    long rf = Math.min(
                        energyContainerItem.getMaxEnergyStored(individualStack)
                            - energyContainerItem.getEnergyStored(individualStack),
                        euPerItem * GregTechAPI.mEUtoRF / 10L);
                    int rfToCharge = (int) rf;
                    rf = energyContainerItem.receiveEnergy(individualStack, rfToCharge, false);
                    decreaseEUValue(rf * 10L / GregTechAPI.mEUtoRF);
                }

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
        if (ItemUtil.isStackInvalid(stack)) {
            return true;
        }
        Item item = stack.getItem();

        for (int i = 0; i < stack.stackSize; i++) {
            if (item instanceof IElectricItem electricItem) {
                if (ElectricItem.manager.getCharge(stack) < electricItem.getMaxCharge(stack)) {
                    return false;
                }
            } else if (Mods.COFHCore.isModLoaded() && item instanceof IEnergyContainerItem energyContainerItem) {
                if (item instanceof DraconiumItemBlock && stack.getItemDamage() == 2) return true;
                if (energyContainerItem.getEnergyStored(stack) < energyContainerItem.getMaxEnergyStored(stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isItemStackFullyRepaired(ItemStack stack) {
        if (ItemUtil.isStackInvalid(stack)) {
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

    public long getMaxStoredEU() {
        if (wirelessMode) {
            return Utils.toLongSafe(WirelessNetworkManager.getUserEU(ownerUUID));
        }

        long maxStoredEU = 0;

        for (MTEHatchEnergy tHatch : GTUtility.validMTEList(mEnergyHatches)) {
            maxStoredEU = Math.max(
                maxStoredEU,
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU());
        }

        for (MTEHatchEnergyMulti tHatch : GTUtility.validMTEList(eEnergyMulti)) {
            maxStoredEU = Math.max(
                maxStoredEU,
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU());
        }

        return maxStoredEU;
    }

    public void decreaseEUValue(long energyToRemove) {
        if (wirelessMode) {
            WirelessNetworkManager.addEUToGlobalEnergyMap(ownerUUID, -energyToRemove);
            return;
        }

        long maxStoredEU = 0;
        MTEHatchEnergy targetEnergyHatch = null;
        MTEHatchEnergyMulti targetEnergyMulti = null;

        for (MTEHatchEnergy tHatch : GTUtility.validMTEList(mEnergyHatches)) {
            long storedEU = tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (storedEU > maxStoredEU) {
                maxStoredEU = storedEU;
                targetEnergyHatch = tHatch;
            }
        }

        for (MTEHatchEnergyMulti tHatch : GTUtility.validMTEList(eEnergyMulti)) {
            long storedEU = tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (storedEU > maxStoredEU) {
                maxStoredEU = storedEU;
                targetEnergyMulti = tHatch;
            }
        }

        if (targetEnergyHatch != null) {
            targetEnergyHatch.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(energyToRemove, false);
        } else if (targetEnergyMulti != null) {
            targetEnergyMulti.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(energyToRemove, false);
        }
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        outputAllItems = true;
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EnergyInfuser_00"));
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);

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
            .addInfo(StatCollector.translateToLocal("Tooltip_EnergyInfuser_05"))
            .addTecTechHatchInfo()
            .beginStructureBlock(5, 8, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EnergyInfuser_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EnergyInfuser_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EnergyInfuser_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_EnergyInfuser_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean showRecipeTextInGUI() {
        return false;
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        screenElements.widget(
            TextWidget.dynamicString(this::generateCurrentProgress)
                .setSynced(false)
                .setTextAlignment(new Alignment(-1, -1))
                .setSize(180, 12)
                .setEnabled(widget -> (mStoredItems != null && !mStoredItems.isEmpty()) || (mMaxProgresstime > 0)));
        final ChangeableWidget recipeOutputItemsWidget = new ChangeableWidget(this::generateCurrentRecipeInfoWidget);
        // Display current recipe
        screenElements.widget(
            new FakeSyncWidget.ListSyncer<>(
                () -> mStoredItems != null ? mStoredItems : Collections.emptyList(),
                val -> {
                    mStoredItems = val;
                    recipeOutputItemsWidget.notifyChangeNoSync();
                },
                NetworkUtils::writeItemStack,
                NetworkUtils::readItemStack))
            .widget(new FakeSyncWidget.IntegerSyncer(() -> mProgresstime, val -> mProgresstime = val))
            .widget(new FakeSyncWidget.IntegerSyncer(() -> mMaxProgresstime, val -> {
                mMaxProgresstime = val;
                recipeOutputItemsWidget.notifyChangeNoSync();
            }));
        screenElements.widget(recipeOutputItemsWidget);
    }

    @Override
    public Widget generateCurrentRecipeInfoWidget() {
        final DynamicPositionedColumn processingDetails = new DynamicPositionedColumn();

        if (mStoredItems != null) {
            final Map<ItemStack, Long> nameToAmount = new HashMap<>();

            for (ItemStack item : mStoredItems) {
                if (item == null || item.stackSize <= 0) continue;
                nameToAmount.merge(item, (long) item.stackSize, Long::sum);
            }

            final List<Map.Entry<ItemStack, Long>> sortedMap = nameToAmount.entrySet()
                .stream()
                .sorted(
                    Map.Entry.<ItemStack, Long>comparingByValue()
                        .reversed())
                .collect(Collectors.toList());

            for (Map.Entry<ItemStack, Long> entry : sortedMap) {
                Long itemCount = entry.getValue();
                String itemName = entry.getKey()
                    .getDisplayName();
                String itemAmountString = EnumChatFormatting.WHITE + " x "
                    + EnumChatFormatting.GOLD
                    + GTUtility.formatShortenedLong(itemCount)
                    + EnumChatFormatting.WHITE
                    + appendRate(false, itemCount, true);
                String lineText = EnumChatFormatting.AQUA
                    + GTUtility.truncateText(itemName, 40 - itemAmountString.length())
                    + itemAmountString;
                String lineTooltip = EnumChatFormatting.AQUA + itemName + "\n" + appendRate(false, itemCount, false);

                processingDetails.widget(
                    new MultiChildWidget().addChild(
                        new ItemDrawable(
                            entry.getKey()
                                .copy()).asWidget()
                                    .setSize(8, 8)
                                    .setPos(0, 0))
                        .addChild(
                            new TextWidget(lineText).setTextAlignment(Alignment.CenterLeft)
                                .addTooltip(lineTooltip)
                                .setPos(10, 1)));
            }
        }
        return processingDetails;
    }

    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_WHOOUM;
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
    public void chargeController_EM(IGregTechTileEntity aBaseMetaTileEntity) {}

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
