package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofHatchAdder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.TextLocalization;
import com.science.gtnl.Utils.TextUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.fluid.IFluidStore;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.objects.GTRenderedTexture;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings2;
import gregtech.common.blocks.BlockCasings3;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBase;

public class PrimitiveDistillationTower extends MTESteamMultiBase<PrimitiveDistillationTower>
    implements ISurvivalConstructable {

    public static final int CASING_INDEX = ((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0);
    public static final String STRUCTURE_PIECE_BASE = "base";
    public static final String STRUCTURE_PIECE_LAYER = "layer";
    public static final String STRUCTURE_PIECE_LAYER_HINT = "layerHint";
    public static final String STRUCTURE_PIECE_TOP_HINT = "topHint";
    public static final IStructureDefinition<PrimitiveDistillationTower> STRUCTURE_DEFINITION;

    static {
        IHatchElement<PrimitiveDistillationTower> layeredOutputHatch = OutputHatch
            .withCount(PrimitiveDistillationTower::getCurrentLayerOutputHatchCount)
            .withAdder(PrimitiveDistillationTower::addLayerOutputHatch);
        STRUCTURE_DEFINITION = StructureDefinition.<PrimitiveDistillationTower>builder()
            .addShape(STRUCTURE_PIECE_BASE, transpose(new String[][] { { "A~A", "AAA", "AAA" }, }))
            .addShape(STRUCTURE_PIECE_LAYER, transpose(new String[][] { { "BBB", "BCB", "BBB" }, }))
            .addShape(STRUCTURE_PIECE_LAYER_HINT, transpose(new String[][] { { "BBB", "B B", "BBB" }, }))
            .addShape(STRUCTURE_PIECE_TOP_HINT, transpose(new String[][] { { "DDD", "DDD", "DDD" }, }))
            .addElement(
                'A',
                ofChain(
                    buildSteamInput(PrimitiveDistillationTower.class)
                        .casingIndex(((BlockCasings3) GregTechAPI.sBlockCasings3).getTextureIndex(14))
                        .dot(1)
                        .build(),
                    buildHatchAdder(PrimitiveDistillationTower.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            OutputBus,
                            InputHatch,
                            InputBus)
                        .casingIndex(((BlockCasings3) GregTechAPI.sBlockCasings3).getTextureIndex(14))
                        .dot(1)
                        .build(),
                    onElementPass(PrimitiveDistillationTower::onCasingFound, ofBlock(GregTechAPI.sBlockCasings3, 14))))
            .addElement(
                'B',
                ofChain(
                    onElementPass(PrimitiveDistillationTower::onCasingFound, ofBlock(GregTechAPI.sBlockCasings2, 0)),
                    buildHatchAdder(PrimitiveDistillationTower.class).atLeast(layeredOutputHatch)
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .disallowOnly(ForgeDirection.UP, ForgeDirection.DOWN)
                        .build(),
                    ofHatchAdder(PrimitiveDistillationTower::addLayerOutputHatch, CASING_INDEX, 1)))
            .addElement(
                'D',
                ofChain(
                    buildHatchAdder(PrimitiveDistillationTower.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(layeredOutputHatch)
                        .buildAndChain(GregTechAPI.sBlockCasings2, 0)))
            .addElement(
                'C',
                ofChain(
                    onElementPass(
                        t -> t.onTopLayerFound(false),
                        ofHatchAdder(PrimitiveDistillationTower::addOutputToMachineList, CASING_INDEX, 1)),
                    onElementPass(t -> t.onTopLayerFound(true), ofBlock(GregTechAPI.sBlockCasings2, 0)),
                    isAir()))
            .build();
    }

    public final List<List<MTEHatchOutput>> mOutputHatchesByLayer = new ArrayList<>();
    public int mHeight;
    public int mCasing;
    public boolean mTopLayerFound;

    public PrimitiveDistillationTower(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PrimitiveDistillationTower(String aName) {
        super(aName);
    }

    public void updateHatchTexture() {
        for (MTEHatch h : mSteamInputs) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mSteamOutputs) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mSteamInputFluids) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputHatches) h.updateTexture(getCasingTextureID());
    }

    public int getCasingTextureID() {
        return ((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0);
    }

    @Override
    public VoidingMode getVoidingMode() {
        return VoidingMode.VOID_FLUID;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity aBaseMetaTileEntity, final ForgeDirection side,
        final ForgeDirection facing, final int aColorIndex, final boolean aActive, final boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                aActive ? getFrontOverlayActive() : getFrontOverlay() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PrimitiveDistillationTower(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.PrimitiveDistillationTowerRecipeType)
            .addInfo(TextLocalization.Tooltip_PrimitiveDistillationTower_00)
            .addInfo(TextLocalization.Tooltip_PrimitiveDistillationTower_01)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(3, 7, 3, false)
            .addInputBus(TextLocalization.Tooltip_PrimitiveDistillationTower_Casing_01, 1)
            .addOutputBus(TextLocalization.Tooltip_PrimitiveDistillationTower_Casing_01, 1)
            .addInputHatch(TextLocalization.Tooltip_PrimitiveDistillationTower_Casing_01, 1)
            .addOutputHatch(TextLocalization.Tooltip_PrimitiveDistillationTower_Casing_02, 1)
            .toolTipFinisher(TextUtils.SQY);
        return tt;
    }

    @Override
    public GTRenderedTexture getFrontOverlay() {
        return new GTRenderedTexture(Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER);
    }

    @Override
    public GTRenderedTexture getFrontOverlayActive() {
        return new GTRenderedTexture(Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE);
    }

    @Override
    public int getTierRecipes() {
        return 3;
    }

    @Override
    public String getMachineType() {
        return TextLocalization.PrimitiveDistillationTowerRecipeType;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 8;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.distillationTowerRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {

        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (availableVoltage < recipe.mEUt) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                } else return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Override
            @Nonnull
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setEUtDiscount(0.75)
                    .setSpeedBoost(0.8);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    public void onCasingFound() {
        mCasing++;
    }

    public void onTopLayerFound(boolean aIsCasing) {
        mTopLayerFound = true;
        if (aIsCasing) onCasingFound();
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
    public List<? extends IFluidStore> getFluidOutputSlots(FluidStack[] toOutput) {
        return getFluidOutputSlotsByLayer(toOutput, mOutputHatchesByLayer);
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> (d.flag & (ForgeDirection.UP.flag | ForgeDirection.DOWN.flag)) == 0 && r.isNotRotated()
            && !f.isVerticallyFliped();
    }

    @Override
    public boolean isRotationChangeAllowed() {
        return false;
    }

    @Override
    public IStructureDefinition<PrimitiveDistillationTower> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        // reset
        mOutputHatchesByLayer.forEach(List::clear);
        mHeight = 1;
        mTopLayerFound = false;
        mCasing = 0;

        if (!checkPiece(STRUCTURE_PIECE_BASE, 1, 0, 0)) return false;

        while (mHeight < 7) {
            if (!checkPiece(STRUCTURE_PIECE_LAYER, 1, mHeight, 0)) {
                return false;
            }
            if (mOutputHatchesByLayer.size() < mHeight || mOutputHatchesByLayer.get(mHeight - 1)
                .isEmpty()) return false;
            if (mTopLayerFound) {
                break;
            }
            mHeight++;
        }

        return mCasing >= 7 * (mHeight + 1) - 5 && mHeight == 6;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public void addFluidOutputs(FluidStack[] outputFluids) {
        for (int i = 0; i < outputFluids.length && i < mOutputHatchesByLayer.size(); i++) {
            final FluidStack fluidStack = outputFluids[i];
            if (fluidStack == null) continue;
            FluidStack tStack = fluidStack.copy();
            if (!dumpFluid(mOutputHatchesByLayer.get(i), tStack, true))
                dumpFluid(mOutputHatchesByLayer.get(i), tStack, false);
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_BASE, stackSize, hintsOnly, 1, 0, 0);
        int tTotalHeight = 7;
        for (int i = 1; i < tTotalHeight - 1; i++) {
            buildPiece(STRUCTURE_PIECE_LAYER_HINT, stackSize, hintsOnly, 1, i, 0);
        }
        buildPiece(STRUCTURE_PIECE_TOP_HINT, stackSize, hintsOnly, 1, tTotalHeight - 1, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        mHeight = 0;
        int built = survivialBuildPiece(STRUCTURE_PIECE_BASE, stackSize, 1, 0, 0, elementBudget, env, false, true);
        if (built >= 0) return built;
        int tTotalHeight = 7;
        for (int i = 1; i < tTotalHeight - 1; i++) {
            mHeight = i;
            built = survivialBuildPiece(
                STRUCTURE_PIECE_LAYER_HINT,
                stackSize,
                1,
                i,
                0,
                elementBudget,
                env,
                false,
                true);
            if (built >= 0) return built;
        }
        mHeight = tTotalHeight - 1;
        return survivialBuildPiece(
            STRUCTURE_PIECE_TOP_HINT,
            stackSize,
            1,
            tTotalHeight - 1,
            0,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }
}