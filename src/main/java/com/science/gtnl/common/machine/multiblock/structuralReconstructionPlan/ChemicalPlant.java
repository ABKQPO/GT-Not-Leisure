package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class ChemicalPlant extends GTMMultiMachineBase<ChemicalPlant> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String CP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/chemical_plant";
    private static final int HORIZONTAL_OFF_SET = 0;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(CP_STRUCTURE_FILE_PATH);

    public ChemicalPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ChemicalPlant(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.chemical_plant.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ChemicalPlant(this.mName);
    }

    @Override
    public IStructureDefinition<ChemicalPlant> getStructureDefinition() {
        return StructureDefinition.<ChemicalPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(ChemicalPlant::setMCoilLevel, ChemicalPlant::getMCoilLevel))))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(ChemicalPlant.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, Casings.ChemicallyInertMachineCasing.asElement())))
            .addElement('C', Casings.PTFEPipeCasing.asElement())
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 50);
    }

    @Override
    protected boolean requiresCoilStructureCheck() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.multiblockChemicalReactorRecipes;
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 * Math.pow(0.95, getMCoilLevel().getTier());
    }

    @Override
    public double getDurationModifier() {
        return 1 / 1.67 * Math.pow(0.95, getMCoilLevel().getTier());
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        resetParallelTier();
        ParallelControllerHatch module = getSingleParallelControllerHatch();
        if (module != null) {
            mParallelTier = module.mTier;
            return Math.min(module.getParallel(), 1024);
        }

        int maxRecipes;
        if (mParallelTier <= 2) {
            maxRecipes = 8;
        } else {
            maxRecipes = 1 << (2 * (mParallelTier - 2));
        }
        return Math.min(maxRecipes, 1024);
    }

    @Override
    public int getCasingTextureID() {
        return Casings.ChemicallyInertMachineCasing.getTextureId();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.chemical_plant.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.chemical_plant.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.chemical_plant.tooltip.1"))
            .addPerfectOCInfo()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.3"))
            .addSupportMultiAmp()
            .beginStructureBlock(5, 5, 5, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.chemical_plant.tooltip.casing"))
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.chemical_plant.tooltip.casing"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.chemical_plant.tooltip.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.chemical_plant.tooltip.casing"))
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.chemical_plant.tooltip.casing"))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.chemical_plant.tooltip.casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }
}
