package com.science.gtnl.common.machine.multiblock.steam;

import java.util.ArrayList;
import java.util.List;

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
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.IOutputHatch;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.fluid.IFluidStore;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class PrimitiveDistillationTower extends SteamMultiMachineBase<PrimitiveDistillationTower>
    implements ISurvivalConstructable {

    public static final String STRUCTURE_PIECE_BASE = "base";
    public static final String STRUCTURE_PIECE_LAYER = "layer";
    public static final String STRUCTURE_PIECE_LAYER_HINT = "layerHint";
    public static final String STRUCTURE_PIECE_TOP_HINT = "topHint";

    public final List<List<MTEHatchOutput>> mOutputHatchesByLayer = new ArrayList<>();
    public int mHeight;
    public boolean mTopLayerFound;

    public PrimitiveDistillationTower(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PrimitiveDistillationTower(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.primitive_distillation_tower.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PrimitiveDistillationTower(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.primitive_distillation_tower.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.primitive_distillation_tower.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.primitive_distillation_tower.tooltip.1"))
            .beginStructureBlock(3, 7, 3, false)
            .addInputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.primitive_distillation_tower.tooltip.casing.0"),
                1)
            .addOutputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.primitive_distillation_tower.tooltip.casing.0"),
                1)
            .addInputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.primitive_distillation_tower.tooltip.casing.0"),
                1)
            .addOutputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.primitive_distillation_tower.tooltip.casing.1"),
                1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated();
    }

    @Override
    public int getTierRecipes() {
        return 3;
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
    public double getEUtDiscount() {
        return super.getEUtDiscount() * 0.75;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / 0.8;
    }

    @Override
    public IStructureDefinition<PrimitiveDistillationTower> getStructureDefinition() {
        IHatchElement<PrimitiveDistillationTower> layeredOutputHatch = HatchElement.OutputHatch
            .withCount(PrimitiveDistillationTower::getCurrentLayerOutputHatchCount)
            .withAdder(PrimitiveDistillationTower::addLayerOutputHatch);
        return StructureDefinition.<PrimitiveDistillationTower>builder()
            .addShape(STRUCTURE_PIECE_BASE, StructureUtility.transpose(new String[][] { { "A~A", "AAA", "AAA" }, }))
            .addShape(STRUCTURE_PIECE_LAYER, StructureUtility.transpose(new String[][] { { "BBB", "BCB", "BBB" }, }))
            .addShape(
                STRUCTURE_PIECE_LAYER_HINT,
                StructureUtility.transpose(new String[][] { { "BBB", "B B", "BBB" }, }))
            .addShape(STRUCTURE_PIECE_TOP_HINT, StructureUtility.transpose(new String[][] { { "DDD", "DDD", "DDD" }, }))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(PrimitiveDistillationTower.class)
                        .casingIndex(Casings.SteelFireboxCasing.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamBigInput(PrimitiveDistillationTower.class)
                        .casingIndex(Casings.SteelFireboxCasing.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamInput(PrimitiveDistillationTower.class)
                        .casingIndex(Casings.SteelFireboxCasing.getTextureId())
                        .hint(1)
                        .build(),
                    GTStructureUtility.buildHatchAdder(PrimitiveDistillationTower.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.OutputBus,
                            HatchElement.InputHatch,
                            HatchElement.InputBus,
                            HatchElement.Maintenance)
                        .casingIndex(Casings.SteelFireboxCasing.getTextureId())
                        .hint(1)
                        .build(),
                    StructureUtility.onElementPass(
                        PrimitiveDistillationTower::onCasingFound,
                        Casings.SteelFireboxCasing.asElement())))
            .addElement(
                'B',
                StructureUtility.ofChain(
                    StructureUtility.onElementPass(
                        PrimitiveDistillationTower::onCasingFound,
                        Casings.SolidSteelMachineCasing.asElement()),
                    GTStructureUtility.buildHatchAdder(PrimitiveDistillationTower.class)
                        .atLeast(layeredOutputHatch)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .disallowOnly(ForgeDirection.UP, ForgeDirection.DOWN)
                        .build(),
                    GTStructureUtility
                        .ofHatchAdder(PrimitiveDistillationTower::addLayerOutputHatch, getCasingTextureID(), 1)))
            .addElement(
                'C',
                StructureUtility.ofChain(
                    StructureUtility.onElementPass(
                        t -> t.onTopLayerFound(false),
                        GTStructureUtility
                            .ofHatchAdder(PrimitiveDistillationTower::addOutputToMachineList, getCasingTextureID(), 1)),
                    StructureUtility
                        .onElementPass(t -> t.onTopLayerFound(true), Casings.SolidSteelMachineCasing.asElement()),
                    StructureUtility.isAir()))
            .addElement('D', Casings.SolidSteelMachineCasing.asElement())
            .addElement(
                'D',
                GTStructureUtility.buildHatchAdder(PrimitiveDistillationTower.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(HatchElement.OutputHatch)
                    .buildAndChain(GregTechAPI.sBlockCasings2, 0))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_BASE, 1, 0, 0, errors)) return;
        while (mHeight < 7) {
            if (!checkPiece(STRUCTURE_PIECE_LAYER, 1, mHeight, 0, errors)) return;
            if (mOutputHatchesByLayer.size() < mHeight || mOutputHatchesByLayer.get(mHeight - 1)
                .isEmpty()) errors.add(GTNLStructureErrors.missingDistillationLayerOutputHatch());
            if (mTopLayerFound) {
                break;
            }
            // not top
            mHeight++;
        }
        updateHatchTexture();
        if (mHeight < 6) {
            errors.add(StructureErrorRegistry.TOO_SHORT_HEIGHT);
            return;
        }
        if (!mTopLayerFound) {
            errors.add(StructureErrors.of("GT5U.gui.text.structure_error.missing_top"));
            return;
        }
        checkCasingMin(errors, mCountCasing, 7 * (mHeight + 1) - 5);
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
        int built = survivalBuildPiece(STRUCTURE_PIECE_BASE, stackSize, 1, 0, 0, elementBudget, env, false, true);
        if (built >= 0) return built;
        int tTotalHeight = 7;
        for (int i = 1; i < tTotalHeight - 1; i++) {
            mHeight = i;
            built = survivalBuildPiece(STRUCTURE_PIECE_LAYER_HINT, stackSize, 1, i, 0, elementBudget, env, false, true);
            if (built >= 0) return built;
        }
        mHeight = tTotalHeight - 1;
        return survivalBuildPiece(
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

    @Override
    public int getCasingTextureID() {
        return Casings.SolidSteelMachineCasing.getTextureId();
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
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.primitive_distillation_tower.recipe_type");
    }

    public List<? extends IFluidStore> getFluidOutputSlots(FluidStack[] toOutput) {
        List<IFluidStore> ret = new ArrayList<>();
        for (List<MTEHatchOutput> layer : mOutputHatchesByLayer) {
            for (MTEHatchOutput hatch : layer) {
                if (hatch.outputsLiquids() && hatch instanceof IFluidStore fs) {
                    ret.add(fs);
                }
            }
        }
        return ret;
    }

    public boolean addFluidOutputs(@NotNull FluidStack[] outputFluids) {
        List<IOutputHatch> allHatches = new ArrayList<>();
        for (List<MTEHatchOutput> layer : mOutputHatchesByLayer) {
            for (MTEHatchOutput hatch : layer) {
                if (hatch instanceof IOutputHatch oh && hatch.outputsLiquids()) {
                    allHatches.add(oh);
                }
            }
        }
        return addFluidOutputs(outputFluids, allHatches);
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mOutputHatchesByLayer.forEach(List::clear);
        mHeight = 1;
        mTopLayerFound = false;
    }

    public void onCasingFound() {
        mCountCasing++;
    }

    public void onTopLayerFound(boolean isCasing) {
        mTopLayerFound = true;
        if (isCasing) {
            onCasingFound();
        }
    }

    public int getCurrentLayerOutputHatchCount() {
        return mOutputHatchesByLayer.size() < mHeight || mHeight <= 0 ? 0
            : mOutputHatchesByLayer.get(mHeight - 1)
                .size();
    }

    public boolean addLayerOutputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null || aTileEntity.isDead()
            || !(aTileEntity.getMetaTileEntity() instanceof MTEHatchOutput outputHatch)) return false;
        while (mOutputHatchesByLayer.size() < mHeight) {
            mOutputHatchesByLayer.add(new ArrayList<>());
        }
        outputHatch.updateTexture(aBaseCasingIndex);
        return mOutputHatchesByLayer.get(mHeight - 1)
            .add(outputHatch);
    }
}
