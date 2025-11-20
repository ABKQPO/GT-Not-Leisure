package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static goodgenerator.loader.Loaders.compactFusionCoil;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.EternalGregTechWorkshopUI;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import lombok.Getter;

public class EngravingLaserPlant extends WirelessEnergyMultiMachineBase<EngravingLaserPlant> {

    private static final int MACHINEMODE_LASER = 0;
    private static final int MACHINEMODE_PRECISION_LASER = 1;
    private static final int HORIZONTAL_OFF_SET = 10;
    private static final int VERTICAL_OFF_SET = 9;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String ELP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/engraving_laser_plant";
    private static final String[][] shape = StructureUtils.readStructureFromFile(ELP_STRUCTURE_FILE_PATH);
    private static final int MANUAL_INSERTION_WINDOW_ID = 15;

    public static final ItemStack[] REQUIRED_ITEMS = new ItemStack[] {
        GTUtility.copyAmountUnsafe(114514, ItemList.Circuit_Silicon_Wafer7.get(1)) };

    public ItemStack[] storedUpgradeWindowItems = new ItemStack[16];
    public ItemStackHandler inputSlotHandler = new ItemStackHandler(16);
    public int[] paidCost = new int[REQUIRED_ITEMS.length];

    public int mCasingTier;

    @Getter
    public boolean hasConsumed = false;

    public EngravingLaserPlant(String aName) {
        super(aName);
    }

    public EngravingLaserPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EngravingLaserPlant(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("EngravingLaserPlantRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(21, 12, 22, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 7);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<EngravingLaserPlant> getStructureDefinition() {
        return StructureDefinition.<EngravingLaserPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings10, 8))
            .addElement('B', ofBlock(sBlockCasingsTT, 0))
            .addElement('C', ofBlock(sBlockCasings6, 9))
            .addElement(
                'D',
                buildHatchAdder(EngravingLaserPlant.class)
                    .atLeast(
                        Maintenance,
                        InputBus,
                        OutputBus,
                        InputHatch,
                        OutputHatch,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 7))))
            .addElement('E', ofBlock(sBlockCasings8, 12))
            .addElement('F', ofBlock(sBlockCasings9, 1))
            .addElement('G', ofBlock(BlockLoader.metaCasing, 5))
            .addElement('H', ofBlock(BlockLoader.metaCasing, 4))
            .addElement('I', ofBlock(compactFusionCoil, 2))
            .addElement(
                'J',
                ofBlocksTiered(
                    (block, meta) -> block == Loaders.componentAssemblylineCasing ? meta : -1,
                    IntStream.range(0, 13)
                        .mapToObj(i -> Pair.of(Loaders.componentAssemblylineCasing, i))
                        .collect(Collectors.toList()),
                    -2,
                    (t, meta) -> t.mCasingTier = meta,
                    t -> t.mCasingTier))
            .addElement('K', ofBlock(sBlockCasings10, 11))
            .addElement('L', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('M', ofFrame(Materials.Neutronium))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing > 750;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && mCasingTier >= 0;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mCasingTier = -2;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.95, mGlassTier) * Math.pow(0.95, mCasingTier);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.95, mGlassTier) * Math.pow(0.95, mCasingTier);
    }

    public long getMachineVoltageLimit() {
        if (mCasingTier < 0) return 0;
        if (wirelessMode) {
            if (mCasingTier >= 11) {
                return V[Math.min(mParallelTier + 1, 14)];
            } else {
                return V[Math.min(Math.min(mParallelTier + 1, mCasingTier + 3), 14)];
            }
        } else if (mCasingTier >= 11) {
            return V[mEnergyHatchTier];
        } else {
            return V[Math.min(mCasingTier + 3, mEnergyHatchTier)];
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        int base = super.getMaxParallelRecipes();
        if (machineMode == MACHINEMODE_PRECISION_LASER) {
            base >>= 4;
            if (base < 1) base = 1;
        }

        return base;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_LASER) ? RecipeMaps.laserEngraverRecipes : RecipePool.PrecisionLaserEngraver;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.laserEngraverRecipes, RecipePool.PrecisionLaserEngraver);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (byte) ((this.machineMode + 1) % 2);
        GTUtility
            .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("EngravingLaserPlant_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("EngravingLaserPlant_Mode_" + machineMode);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_SLICING);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mGlassTier", mGlassTier);
        aNBT.setInteger("casingTier", mCasingTier);
        NBTTagCompound upgradeWindowStorageNBTTag = new NBTTagCompound();
        int storageIndex = 0;
        for (ItemStack itemStack : inputSlotHandler.getStacks()) {
            if (itemStack != null) {
                upgradeWindowStorageNBTTag
                    .setInteger(storageIndex + "stacksizeOfStoredUpgradeItems", itemStack.stackSize);
                aNBT.setTag(storageIndex + "storedUpgradeItem", itemStack.writeToNBT(new NBTTagCompound()));
            }
            storageIndex++;
        }
        aNBT.setTag("upgradeWindowStorage", upgradeWindowStorageNBTTag);

        NBTTagCompound paidCostTag = new NBTTagCompound();
        for (int i = 0; i < paidCost.length; i++) {
            paidCostTag.setInteger("cost" + i, paidCost[i]);
        }
        aNBT.setTag("paidCosts", paidCostTag);

        aNBT.setBoolean("hasConsumed", hasConsumed);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mGlassTier = aNBT.getInteger("mGlassTier");
        mCasingTier = aNBT.getInteger("casingTier");
        NBTTagCompound tempItemTag = aNBT.getCompoundTag("upgradeWindowStorage");
        for (int index = 0; index < 16; index++) {
            int stackSize = tempItemTag.getInteger(index + "stacksizeOfStoredUpgradeItems");
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag(index + "storedUpgradeItem"));
            if (itemStack != null) {
                storedUpgradeWindowItems[index] = itemStack.splitStack(stackSize);
            }
        }

        NBTTagCompound paidCostTag = aNBT.getCompoundTag("paidCosts");
        for (int i = 0; i < paidCost.length; i++) {
            paidCost[i] = paidCostTag.getInteger("cost" + i);
        }

        hasConsumed = aNBT.getBoolean("hasConsumed");
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);

        buildContext.addSyncedWindow(MANUAL_INSERTION_WINDOW_ID, this::createConsumeWindow);
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::isHasConsumed, val -> hasConsumed = val));

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(MANUAL_INSERTION_WINDOW_ID);
            }
        })
            .setBackground(
                () -> new IDrawable[] {
                    hasConsumed ? GTUITextures.BUTTON_STANDARD_PRESSED : GTUITextures.BUTTON_STANDARD,
                    GTUITextures.OVERLAY_BUTTON_ARROW_GREEN_UP })
            .addTooltip(StatCollector.translateToLocal("Info_EngravingLaserPlant_00"))
            .setTooltipShowUpDelay(5)
            .setPos(174, 110)
            .setSize(16, 16));
    }

    public ModularWindow createConsumeWindow(EntityPlayer player) {
        final int WIDTH = 160;
        final int HEIGHT = 120;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT)))
                .subtract(5, 0)
                .add(0, 4));

        for (int i = 0; i < 16; i++) {
            inputSlotHandler.insertItem(i, storedUpgradeWindowItems[i], false);
            storedUpgradeWindowItems[i] = null;
        }

        builder.widget(
            SlotGroup.ofItemHandler(inputSlotHandler, 4)
                .startFromSlot(0)
                .endAtSlot(15)
                .phantom(false)
                .background(getGUITextureSet().getItemSlot())
                .build()
                .setPos(80, 10));

        for (int i = 0; i < REQUIRED_ITEMS.length; i++) {
            ItemStack stack = REQUIRED_ITEMS[i];
            int stackCost = paidCost[i];
            Widget costWidget = EternalGregTechWorkshopUI.createExtraCostWidget(stack, () -> stackCost);
            costWidget.setPos(5 + (36 * (i % 2)), 6 + (18 * (i / 2)));
            builder.widget(costWidget);
        }

        builder.widget(new MultiChildWidget().addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                if (tryConsumeItems()) {
                    hasConsumed = true;
                }
                EternalGregTechWorkshopUI.reopenWindow(widget, MANUAL_INSERTION_WINDOW_ID);
            }
        })
            .setPlayClickSound(true)
            .setBackground(GTUITextures.BUTTON_STANDARD)
            .setSize(140, 20))
            .addChild(
                new TextWidget(StatCollector.translateToLocal("gt.blockmachines.multimachine.FOG.consumeUpgradeMats"))
                    .setTextAlignment(Alignment.Center)
                    .setScale(0.75f)
                    .setSize(140, 20))
            .setPos(10, 90)
            .setSize(140, 20));

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getWindow()
                    .closeWindow();
            }
        })
            .setBackground(ModularUITextures.VANILLA_BACKGROUND, new Text("x"))
            .setPos(151, 0)
            .setSize(10, 10));

        return builder.build();
    }

    public boolean tryConsumeItems() {
        boolean allFulfilled = true;

        for (int i = 0; i < REQUIRED_ITEMS.length; i++) {
            ItemStack required = REQUIRED_ITEMS[i];
            int requiredAmount = required.stackSize;
            int alreadyPaid = paidCost[i];

            if (alreadyPaid >= requiredAmount) continue;

            int remainingToPay = requiredAmount - alreadyPaid;

            for (int slot = 0; slot < inputSlotHandler.getSlots(); slot++) {
                ItemStack slotStack = inputSlotHandler.getStackInSlot(slot);
                if (slotStack == null) continue;

                if (GTUtility.areStacksEqual(slotStack, required)) {
                    int extract = Math.min(remainingToPay, slotStack.stackSize);
                    inputSlotHandler.extractItem(slot, extract, false);
                    alreadyPaid += extract;
                    remainingToPay -= extract;
                    paidCost[i] = alreadyPaid;
                    if (remainingToPay <= 0) break;
                }
            }

            if (alreadyPaid < requiredAmount) {
                allFulfilled = false;
            }
        }

        return allFulfilled;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (machineMode == MACHINEMODE_PRECISION_LASER && !hasConsumed) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
                if (wirelessMode && recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @Nonnull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 1];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = StatCollector.translateToLocal("scanner.info.CASS.tier")
            + (mCasingTier >= 0 ? GTValues.VN[mCasingTier + 1] : "None!");
        return ret;
    }

}
