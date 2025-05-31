package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.fluid.IFluidStore;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.tileentities.machines.MTEHatchOutputME;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public class LargeDistillery extends GTMMultiMachineBase<LargeDistillery> implements ISurvivalConstructable {

    private static final int MACHINEMODE_TOWER = 0;
    private static final int MACHINEMODE_DISTILLERY = 1;
    protected static final int CASING_INDEX = TAE.GTPP_INDEX(11);
    public final int HORIZONTAL_OFF_SET = 2;
    public final int VERTICAL_OFF_SET = 0;
    public final int DEPTH_OFF_SET = 0;
    protected static final String STRUCTURE_PIECE_BASE = "base";
    protected static final String STRUCTURE_PIECE_LAYER = "layer";
    protected static final String STRUCTURE_PIECE_LAYER_HINT = "layerHint";
    protected static final String STRUCTURE_PIECE_TOP_HINT = "topHint";
    protected static final String STRUCTURE_PIECE_TOP = "top";
    public static final String LDB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_distillery/base";
    public static final String LDL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_distillery/layer";
    public static final String LDLH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/large_distillery/layer_hint";
    public static final String LDTH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/large_distillery/top_hint";
    public static final String LDT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_distillery/top";
    public static String[][] shape_base = StructureUtils.readStructureFromFile(LDB_STRUCTURE_FILE_PATH);
    public static String[][] shape_layer = StructureUtils.readStructureFromFile(LDL_STRUCTURE_FILE_PATH);
    public static String[][] shape_layer_hint = StructureUtils.readStructureFromFile(LDLH_STRUCTURE_FILE_PATH);
    public static String[][] shape_top_hint = StructureUtils.readStructureFromFile(LDTH_STRUCTURE_FILE_PATH);
    public static String[][] shape_top = StructureUtils.readStructureFromFile(LDT_STRUCTURE_FILE_PATH);

    protected final List<List<MTEHatchOutput>> mOutputHatchesByLayer = new ArrayList<>();
    protected int mHeight;

    public LargeDistillery(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeDistillery(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeDistillery(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeDistilleryRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeDistillery_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeDistillery_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(5, 15, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeDistillery_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeDistillery_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeDistillery_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeDistillery_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeDistillery_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeDistillery_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { BlockIcons.getCasingTextureForId(CASING_INDEX), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { BlockIcons.getCasingTextureForId(CASING_INDEX), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { BlockIcons.getCasingTextureForId(CASING_INDEX) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_TOWER) ? RecipeMaps.distillationTowerRecipes : RecipeMaps.distilleryRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.distillationTowerRecipes, RecipeMaps.distilleryRecipes);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_WASHPLANT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mode", machineMode);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        machineMode = aNBT.getInteger("mode");
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (byte) ((this.machineMode + 1) % 2);
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("LargeDistillery_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("LargeDistillery_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    protected void onCasingFound() {
        tCountCasing++;
    }

    protected int getCurrentLayerOutputHatchCount() {
        return mOutputHatchesByLayer.size() < mHeight || mHeight <= 0 ? 0
            : mOutputHatchesByLayer.get(mHeight - 1)
                .size();
    }

    protected boolean addLayerOutputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null || aTileEntity.isDead()
            || !(aTileEntity.getMetaTileEntity() instanceof MTEHatchOutput tHatch)) return false;
        while (mOutputHatchesByLayer.size() < mHeight) mOutputHatchesByLayer.add(new ArrayList<>());
        tHatch.updateTexture(aBaseCasingIndex);
        return mOutputHatchesByLayer.get(mHeight - 1)
            .add(tHatch);
    }

    @Override
    public List<? extends IFluidStore> getFluidOutputSlots(FluidStack[] toOutput) {
        return getFluidOutputSlotsByLayer(toOutput, mOutputHatchesByLayer);
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public boolean isRotationChangeAllowed() {
        return false;
    }

    @Override
    public IStructureDefinition<LargeDistillery> getStructureDefinition() {
        IHatchElement<LargeDistillery> layeredOutputHatch = OutputHatch
            .withCount(LargeDistillery::getCurrentLayerOutputHatchCount)
            .withAdder(LargeDistillery::addLayerOutputHatch);
        return StructureDefinition.<LargeDistillery>builder()
            .addShape(STRUCTURE_PIECE_BASE, transpose(shape_base))
            .addShape(STRUCTURE_PIECE_LAYER, transpose(shape_layer))
            .addShape(STRUCTURE_PIECE_LAYER_HINT, transpose(shape_layer_hint))
            .addShape(STRUCTURE_PIECE_TOP_HINT, transpose(shape_top_hint))
            .addShape(STRUCTURE_PIECE_TOP, transpose(shape_top))
            .addElement(
                'A',
                ofChain(
                    buildHatchAdder(LargeDistillery.class)
                        .atLeast(Energy.or(ExoticEnergy), OutputBus, InputHatch, InputBus, Maintenance)
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .build(),
                    onElementPass(LargeDistillery::onCasingFound, ofBlock(blockCasingsMisc, 11))))
            .addElement(
                'B',
                ofChain(
                    buildHatchAdder(LargeDistillery.class).atLeast(layeredOutputHatch)
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .disallowOnly(ForgeDirection.UP, ForgeDirection.DOWN)
                        .build(),
                    ofHatchAdder(LargeDistillery::addEnergyInputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(LargeDistillery::addLayerOutputHatch, CASING_INDEX, 1),
                    ofHatchAdder(LargeDistillery::addMaintenanceToMachineList, CASING_INDEX, 1),
                    onElementPass(LargeDistillery::onCasingFound, ofBlock(blockCasingsMisc, 11))))
            .addElement('C', ofBlock(sBlockCasings2, 13))
            .addElement(
                'D',
                ofChain(
                    ofHatchAdder(LargeDistillery::addOutputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(LargeDistillery::addMaintenanceToMachineList, CASING_INDEX, 1),
                    ofBlock(blockCasingsMisc, 11),
                    isAir()))
            .addElement(
                'E',
                buildHatchAdder(LargeDistillery.class).atLeast(layeredOutputHatch)
                    .casingIndex(CASING_INDEX)
                    .dot(1)
                    .disallowOnly(ForgeDirection.UP)
                    .buildAndChain(blockCasingsMisc, 11))
            .addElement('F', ofBlock(blockCasingsMisc, 11))
            .addElement('G', Muffler.newAny(CASING_INDEX, 1))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mOutputHatchesByLayer.forEach(List::clear);
        mHeight = 1;
        tCountCasing = 0;

        if (!checkPiece(STRUCTURE_PIECE_BASE, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;

        while (mHeight <= 12) {
            if (!checkPiece(STRUCTURE_PIECE_LAYER, HORIZONTAL_OFF_SET, mHeight, DEPTH_OFF_SET)) {
                return false;
            }
            if (mOutputHatchesByLayer.size() < mHeight || mOutputHatchesByLayer.get(mHeight - 1)
                .isEmpty()) return false;
            if (checkPiece(STRUCTURE_PIECE_TOP, HORIZONTAL_OFF_SET, mHeight + 1, DEPTH_OFF_SET)) {
                break;
            }
            mHeight++;
        }

        if (!checkPiece(STRUCTURE_PIECE_TOP_HINT, HORIZONTAL_OFF_SET, mHeight, DEPTH_OFF_SET)) return false;

        energyHatchTier = checkEnergyHatchTier();
        if (MainConfig.enableMachineAmpLimit) {
            for (MTEHatch hatch : getExoticEnergyHatches()) {
                if (hatch instanceof MTEHatchEnergyTunnel) {
                    return false;
                }
            }
            if (getMaxInputAmps() > 64) return false;
        }

        return tCountCasing >= 5 * (mHeight + 1) - 5 && mHeight + 1 >= 3
            && mMaintenanceHatches.size() == 1
            && mMufflerHatches.size() == 1;
    }

    @Override
    protected void addFluidOutputs(FluidStack[] outputFluids) {
        for (int i = 0; i < outputFluids.length && i < mOutputHatchesByLayer.size(); i++) {
            final FluidStack fluidStack = outputFluids[i];
            if (fluidStack == null) continue;
            FluidStack tStack = fluidStack.copy();
            if (!dumpFluid(mOutputHatchesByLayer.get(i), tStack, true))
                dumpFluid(mOutputHatchesByLayer.get(i), tStack, false);
        }
    }

    @Override
    public boolean canDumpFluidToME() {
        return this.mOutputHatchesByLayer.stream()
            .allMatch(
                tLayerOutputHatches -> tLayerOutputHatches.stream()
                    .anyMatch(tHatch -> (tHatch instanceof MTEHatchOutputME tMEHatch) && (tMEHatch.canAcceptFluid())));
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_BASE, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
        int tTotalHeight = Math.min(13, stackSize.stackSize + 2);
        for (int i = 1; i < tTotalHeight - 1; i++) {
            buildPiece(STRUCTURE_PIECE_LAYER_HINT, stackSize, hintsOnly, HORIZONTAL_OFF_SET, i, DEPTH_OFF_SET);
        }
        buildPiece(STRUCTURE_PIECE_TOP_HINT, stackSize, hintsOnly, HORIZONTAL_OFF_SET, tTotalHeight - 1, DEPTH_OFF_SET);
        buildPiece(STRUCTURE_PIECE_TOP, stackSize, hintsOnly, HORIZONTAL_OFF_SET, tTotalHeight, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        mHeight = 0;
        int built = survivalBuildPiece(
            STRUCTURE_PIECE_BASE,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
        if (built >= 0) return built;
        int tTotalHeight = Math.min(13, stackSize.stackSize + 2);
        for (int i = 1; i < tTotalHeight - 1; i++) {
            mHeight = i;
            built = survivalBuildPiece(
                STRUCTURE_PIECE_LAYER_HINT,
                stackSize,
                HORIZONTAL_OFF_SET,
                i,
                DEPTH_OFF_SET,
                elementBudget,
                env,
                false,
                true);
            if (built >= 0) return built;
        }
        mHeight = tTotalHeight - 1;
        built = survivalBuildPiece(
            STRUCTURE_PIECE_TOP_HINT,
            stackSize,
            HORIZONTAL_OFF_SET,
            tTotalHeight - 1,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
        if (built >= 0) return built;
        mHeight = tTotalHeight;
        return survivalBuildPiece(
            STRUCTURE_PIECE_TOP,
            stackSize,
            HORIZONTAL_OFF_SET,
            tTotalHeight,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setAmperageOC(true)
                    .setDurationDecreasePerOC(2)
                    .setEUtIncreasePerOC(4)
                    .setAmperage(availableAmperage)
                    .setRecipeEUt(recipe.mEUt)
                    .setEUt(availableVoltage)
                    .setEUtDiscount(0.5 - (mParallelTier / 50.0))
                    .setDurationModifier(Math.max(0.05, 1.0 / 4.0 - (mParallelTier / 200.0)));
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        batchMode = !batchMode;
        if (batchMode) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("misc.BatchModeTextOn"));
        } else {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("misc.BatchModeTextOff"));
        }
        return true;
    }
}
