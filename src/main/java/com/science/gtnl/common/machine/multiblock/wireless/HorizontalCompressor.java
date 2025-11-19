package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR_GLOW;
import static gregtech.api.util.GTStructureUtility.*;
import static tectech.thing.casing.TTCasingsContainer.*;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

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
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
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
import gregtech.api.recipe.metadata.CompressionTierKey;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsAlloy;
import lombok.Getter;

public class HorizontalCompressor extends WirelessEnergyMultiMachineBase<HorizontalCompressor> {

    public static final int MANUAL_INSERTION_WINDOW_ID = 15;

    public static final ItemStack[] REQUIRED_ITEMS = new ItemStack[] {
        GTUtility.copyAmountUnsafe(3670, ItemList.Extreme_Density_Casing.get(1)),
        GTUtility.copyAmountUnsafe(988, ItemList.Background_Radiation_Casing.get(1)), ItemList.Hawking_Glass.get(64),
        GTUtility.copyAmountUnsafe(144, GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.NaquadahAlloy, 1)),
        ItemList.Hatch_BlackHoleUtility.get(1), ItemList.Machine_Multi_BlackHoleCompressor.get(1),
        ItemList.Black_Hole_Stabilizer.get(1) };

    public ItemStack[] storedUpgradeWindowItems = new ItemStack[16];
    public ItemStackHandler inputSlotHandler = new ItemStackHandler(16);
    public int[] paidCost = new int[REQUIRED_ITEMS.length];

    @Getter
    public boolean hasConsumed = false;

    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String HC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/horizontal_compressor";
    private static final String[][] shape = StructureUtils.readStructureFromFile(HC_STRUCTURE_FILE_PATH);

    public HorizontalCompressor(String aName) {
        super(aName);
    }

    public HorizontalCompressor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HorizontalCompressor(this.mName);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
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
            .addTooltip(StatCollector.translateToLocal("Info_HorizontalCompressor_00"))
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("HorizontalCompressorRecipeType"))
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
            .addInfo(StatCollector.translateToLocal("Tooltip_HorizontalCompressor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(11, 10, 30, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_HorizontalCompressor_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_HorizontalCompressor_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_HorizontalCompressor_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_HorizontalCompressor_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_HorizontalCompressor_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_HorizontalCompressor_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings4, 0);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_COMPRESSOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_COMPRESSOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_COMPRESSOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_COMPRESSOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<HorizontalCompressor> getStructureDefinition() {
        return StructureDefinition.<HorizontalCompressor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasingsTT, 0))
            .addElement('B', ofBlock(sBlockCasings10, 3))
            .addElement('C', ofBlock(ModBlocks.blockCasingsMisc, 11))
            .addElement('D', ofBlock(sBlockCasings8, 7))
            .addElement('E', ofBlock(sBlockCasings9, 9))
            .addElement('F', ofBlock(sBlockCasings6, 8))
            .addElement('G', ofBlock(sBlockCasings2, 0))
            .addElement('H', ofBlock(sBlockCasings4, 10))
            .addElement(
                'I',
                buildHatchAdder(HorizontalCompressor.class)
                    .atLeast(
                        Maintenance,
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings4, 0))))
            .addElement('J', ofBlock(ModBlocks.blockCasingsMisc, 0))
            .addElement('K', ofBlock(sBlockMetal5, 2))
            .addElement(
                'L',
                ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_N.getFrameBox(1)
                            .getItem())))
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
        return mCountCasing > 200;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.getMetadataOrDefault(CompressionTierKey.INSTANCE, 0) == 2 && !hasConsumed) {
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
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.compressorRecipes;
    }

}
