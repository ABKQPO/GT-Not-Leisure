package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.gui.modularui.GTNLMultiBlockBaseGui;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.utils.structure.GTNLStructureErrors;
import com.science.gtnl.utils.structure.StructureTooltipHooks;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class LargeDistillery extends GTMMultiMachineBase<LargeDistillery> implements ISurvivalConstructable {

    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 0;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_BASE = "base";
    private static final String STRUCTURE_PIECE_LAYER = "layer";
    private static final String STRUCTURE_PIECE_LAYER_HINT = "layerHint";
    private static final String STRUCTURE_PIECE_TOP_HINT = "topHint";
    private static final String STRUCTURE_PIECE_TOP = "top";
    private static final String LDB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_distillery/base";
    private static final String LDL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_distillery/layer";
    private static final String LDLH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/large_distillery/layer_hint";
    private static final String LDTH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/large_distillery/top_hint";
    private static final String LDT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_distillery/top";
    private static final String[][] shape_base = StructureUtils.readStructureFromFile(LDB_STRUCTURE_FILE_PATH);
    private static final String[][] shape_layer = StructureUtils.readStructureFromFile(LDL_STRUCTURE_FILE_PATH);
    private static final String[][] shape_layer_hint = StructureUtils.readStructureFromFile(LDLH_STRUCTURE_FILE_PATH);
    private static final String[][] shape_top_hint = StructureUtils.readStructureFromFile(LDTH_STRUCTURE_FILE_PATH);
    private static final String[][] shape_top = StructureUtils.readStructureFromFile(LDT_STRUCTURE_FILE_PATH);
    private static final int MIN_LAYERS = 2;
    private static final int MIN_CASINGS_PER_LAYER = 5;
    public static final int MACHINEMODE_TOWER = 0;
    public static final int MACHINEMODE_DISTILLERY = 1;

    public final List<List<MTEHatchOutput>> mOutputHatchesByLayer = new ArrayList<>();
    public int mHeight;

    public LargeDistillery(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeDistillery(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.large_distillery.name";
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public boolean isRotationChangeAllowed() {
        return false;
    }

    @Override
    public IStructureDefinition<LargeDistillery> getStructureDefinition() {
        IHatchElement<LargeDistillery> layeredOutputHatch = HatchElement.OutputHatch
            .withCount(LargeDistillery::getCurrentLayerOutputHatchCount)
            .withAdder(LargeDistillery::addLayerOutputHatch);
        return StructureDefinition.<LargeDistillery>builder()
            .addShape(STRUCTURE_PIECE_BASE, StructureUtility.transpose(shape_base))
            .addShape(STRUCTURE_PIECE_LAYER, StructureUtility.transpose(shape_layer))
            .addShape(STRUCTURE_PIECE_LAYER_HINT, StructureUtility.transpose(shape_layer_hint))
            .addShape(STRUCTURE_PIECE_TOP_HINT, StructureUtility.transpose(shape_top_hint))
            .addShape(STRUCTURE_PIECE_TOP, StructureUtility.transpose(shape_top))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(LargeDistillery.class)
                        .atLeast(
                            HatchElement.Maintenance,
                            HatchElement.Energy.or(HatchElement.ExoticEnergy),
                            HatchElement.OutputBus,
                            HatchElement.InputHatch,
                            HatchElement.InputBus,
                            HatchElement.Maintenance,
                            ParallelCon)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility
                        .onElementPass(LargeDistillery::onCasingFound, Casings.MultitankExteriorCasing.asElement())))
            .addElement(
                'B',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(LargeDistillery.class)
                        .atLeast(layeredOutputHatch)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .disallowOnly(ForgeDirection.UP, ForgeDirection.DOWN)
                        .build(),
                    GTStructureUtility
                        .ofHatchAdder(LargeDistillery::addEnergyInputToMachineList, getCasingTextureID(), 1),
                    GTStructureUtility.ofHatchAdder(LargeDistillery::addLayerOutputHatch, getCasingTextureID(), 1),
                    GTStructureUtility
                        .ofHatchAdder(LargeDistillery::addMaintenanceToMachineList, getCasingTextureID(), 1),
                    StructureUtility
                        .onElementPass(LargeDistillery::onCasingFound, Casings.MultitankExteriorCasing.asElement())))
            .addElement('C', Casings.SteelPipeCasing.asElement())
            .addElement(
                'D',
                StructureUtility.ofChain(
                    GTStructureUtility.ofHatchAdder(LargeDistillery::addOutputToMachineList, getCasingTextureID(), 1),
                    GTStructureUtility
                        .ofHatchAdder(LargeDistillery::addMaintenanceToMachineList, getCasingTextureID(), 1),
                    Casings.MultitankExteriorCasing.asElement(),
                    StructureUtility.isAir()))
            .addElement(
                'E',
                GTStructureUtility.buildHatchAdder(LargeDistillery.class)
                    .atLeast(layeredOutputHatch)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .disallowOnly(ForgeDirection.UP)
                    .buildAndChain(blockCasingsMisc, 11))
            .addElement('F', Casings.MultitankExteriorCasing.asElement())
            .addElement('G', HatchElement.Muffler.newAny(getCasingTextureID(), 1))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_BASE, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;

        while (mHeight <= 12) {
            if (!checkPiece(STRUCTURE_PIECE_LAYER, HORIZONTAL_OFF_SET, mHeight, DEPTH_OFF_SET, errors)) {
                return;
            }
            if (!StructureTooltipHooks.onCheckLayerHatches(this, MIN_LAYERS, 1, HatchElement.OutputHatch)
                && (mOutputHatchesByLayer.size() < mHeight || mOutputHatchesByLayer.get(mHeight - 1)
                    .isEmpty()))
                errors.add(GTNLStructureErrors.missingDistillationLayerOutputHatch());
            if (checkPiece(STRUCTURE_PIECE_TOP, HORIZONTAL_OFF_SET, mHeight + 1, DEPTH_OFF_SET, null)) {
                break;
            }
            mHeight++;
        }

        if (!checkPiece(STRUCTURE_PIECE_TOP_HINT, HORIZONTAL_OFF_SET, mHeight, DEPTH_OFF_SET, errors)) return;
        if (mHeight < MIN_LAYERS) {
            errors.add(StructureErrorRegistry.TOO_SHORT_HEIGHT);
            return;
        }

        setupParameters();
        checkHatch(errors);
        checkHatchExact(errors, HatchElement.Muffler, 1);
        checkCasingMin(errors, mCountCasing, MIN_CASINGS_PER_LAYER * mHeight);
    }

    @Override
    public List<String> getStructureTooltipCasingRequirements(int minimum) {
        return List.of(
            StatCollector.translateToLocalFormatted(
                "gtnl.machine.large_distillery.tooltip.casing_total",
                minimum,
                MIN_CASINGS_PER_LAYER),
            StatCollector.translateToLocal("gtnl.machine.large_distillery.tooltip.casing_counted"));
    }

    @Override
    public void runStructureTooltipCheck(Runnable check) {
        int height = mHeight;
        try {
            mHeight = MIN_LAYERS;
            check.run();
        } finally {
            mHeight = height;
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mOutputHatchesByLayer.forEach(List::clear);
        mHeight = 1;
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
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_TOWER) ? RecipeMaps.distillationTowerRecipes : RecipeMaps.distilleryRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.distillationTowerRecipes, RecipeMaps.distilleryRecipes);
    }

    @Override
    public boolean addFluidOutputs(FluidStack[] outputFluids) {
        boolean succeed = true;
        for (int i = 0; i < outputFluids.length && i < mOutputHatchesByLayer.size(); i++) {
            final FluidStack fluidStack = outputFluids[i];
            if (fluidStack == null) continue;
            FluidStack stack = fluidStack.copy();
            addOutputPartial(stack, mOutputHatchesByLayer.get(i));
            if (stack.amount > 0) succeed = false;
        }
        return succeed;
    }

    @Override
    public boolean canDumpFluidToME(List<GTUtility.FluidId> outputs) {
        return canDumpFluidToMEByLayer(outputs, mOutputHatchesByLayer);
    }

    @Override
    public double getEUtDiscount() {
        return 0.5 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return Math.max(0.05, 1.0 / 5.0 - (Math.max(0, mParallelTier - 1) / 50.0));
    }

    @Override
    public SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { BlockIcons.getCasingTextureForId(getCasingTextureID()), TextureFactory.builder()
                .addIcon(BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(11);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTNLMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_WASHPLANT,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    @Override
    @Deprecated
    public void setMachineModeIcons() {
        // TODO: Remove this mui1 fallback after the Large Distillery GUI no longer supports mui1 startup paths.
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_WASHPLANT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 2;
        GTUtility.sendChatTrans(aPlayer, getMachineModeKey());
    }

    @Override
    public String getMachineModeKey() {
        return "gtnl.machine.large_distillery.mode." + machineMode;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        batchMode = !batchMode;
        if (batchMode) {
            GTUtility.sendChatTrans(aPlayer, "misc.BatchModeTextOn");
        } else {
            GTUtility.sendChatTrans(aPlayer, "misc.BatchModeTextOff");
        }
        return true;
    }

    public void onCasingFound() {
        mCountCasing++;
    }

    public int getCurrentLayerOutputHatchCount() {
        return mOutputHatchesByLayer.size() < mHeight || mHeight <= 0 ? 0
            : mOutputHatchesByLayer.get(mHeight - 1)
                .size();
    }

    public boolean addLayerOutputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null || aTileEntity.isDead()
            || !(aTileEntity.getMetaTileEntity() instanceof MTEHatchOutput tHatch)) return false;
        while (mOutputHatchesByLayer.size() < mHeight) mOutputHatchesByLayer.add(new ArrayList<>());
        tHatch.updateTexture(aBaseCasingIndex);
        return mOutputHatchesByLayer.get(mHeight - 1)
            .add(tHatch);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeDistillery(this.mName);
    }
}
