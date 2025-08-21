package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.Utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings8;
import gregtech.common.misc.GTStructureChannels;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public class ChemicalPlant extends GTMMultiMachineBase<ChemicalPlant> implements ISurvivalConstructable {

    public static final int CASING_INDEX = ((BlockCasings8) sBlockCasings8).getTextureIndex(0);
    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String CP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/chemical_plant";
    protected final int HORIZONTAL_OFF_SET = 0;
    protected final int VERTICAL_OFF_SET = 3;
    protected final int DEPTH_OFF_SET = 0;
    public static final String[][] shape = StructureUtils.readStructureFromFile(CP_STRUCTURE_FILE_PATH);

    public ChemicalPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ChemicalPlant(String aName) {
        super(aName);
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ChemicalPlant(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.multiblockChemicalReactorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ChemicalPlantRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ChemicalPlant_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ChemicalPlant_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(5, 5, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<ChemicalPlant> getStructureDefinition() {
        return StructureDefinition.<ChemicalPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.HEATING_COIL
                    .use(activeCoils(ofCoil(ChemicalPlant::setMCoilLevel, ChemicalPlant::getMCoilLevel))))
            .addElement(
                'B',
                buildHatchAdder(ChemicalPlant.class).casingIndex(CASING_INDEX)
                    .dot(1)
                    .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 0))))
            .addElement('C', ofBlock(sBlockCasings8, 1))
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCountCasing = 0;
        mParallelTier = 0;
        this.setMCoilLevel(HeatingCoilLevel.None);

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }

        if (getMCoilLevel() == HeatingCoilLevel.None) return false;

        mEnergyHatchTier = checkEnergyHatchTier();
        if (MainConfig.enableMachineAmpLimit) {
            for (MTEHatch hatch : getExoticEnergyHatches()) {
                if (hatch instanceof MTEHatchEnergyTunnel) {
                    return false;
                }
            }
            if (getRealMaxInputAmps() > 64) return false;
        }

        mParallelTier = getParallelTier(aStack);
        return mCountCasing >= 50;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            protected GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return new GTNL_OverclockCalculator().setRecipeEUt(recipe.mEUt)
                    .setAmperage(availableAmperage)
                    .setEUt(availableVoltage)
                    .setDuration(recipe.mDuration)
                    .setAmperageOC(true)
                    .setDurationDecreasePerOC(4)
                    .setEUtIncreasePerOC(4)
                    .setEUtDiscount(0.8 * Math.pow(0.95, getMCoilLevel().getTier()))
                    .setDurationModifier(1 / 1.67 * Math.pow(0.95, getMCoilLevel().getTier()));
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public int getMaxParallelRecipes() {
        int maxRecipes;
        if (mParallelTier <= 2) {
            maxRecipes = 8;
        } else {
            maxRecipes = (int) Math.pow(4, mParallelTier - 3);
        }
        return Math.min(maxRecipes, 1024);
    }
}
