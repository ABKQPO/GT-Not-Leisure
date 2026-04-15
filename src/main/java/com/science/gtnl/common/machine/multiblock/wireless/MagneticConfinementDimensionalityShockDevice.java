package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsAlloy;
import tectech.thing.casing.BlockGTCasingsTT;

public class MagneticConfinementDimensionalityShockDevice
    extends WirelessEnergyMultiMachineBase<MagneticConfinementDimensionalityShockDevice> {

    private static final int HORIZONTAL_OFF_SET = 11;
    private static final int VERTICAL_OFF_SET = 21;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MCDSD_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/magnetic_confinement_dimensionality_shock_device";
    private static final String[][] shape = StructureUtils.readStructureFromFile(MCDSD_STRUCTURE_FILE_PATH);

    public MagneticConfinementDimensionalityShockDevice(String aName) {
        super(aName);
    }

    public MagneticConfinementDimensionalityShockDevice(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MagneticConfinementDimensionalityShockDevice(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MagneticConfinementDimensionalityShockDeviceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addTecTechHatchInfo()
            .beginStructureBlock(23, 23, 32, true)
            .addInputBus(
                StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_Casing"),
                1)
            .addOutputBus(
                StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_Casing"),
                1)
            .addInputHatch(
                StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_Casing"),
                1)
            .addOutputHatch(
                StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_Casing"),
                1)
            .addEnergyHatch(
                StatCollector.translateToLocal("Tooltip_MagneticConfinementDimensionalityShockDevice_Casing"),
                1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset + 4;
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
    public IStructureDefinition<MagneticConfinementDimensionalityShockDevice> getStructureDefinition() {
        return StructureDefinition.<MagneticConfinementDimensionalityShockDevice>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement('C', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 7))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 9))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockMetal9, 11))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 12))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 13))
            .addElement(
                'J',
                buildHatchAdder(MagneticConfinementDimensionalityShockDevice.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 4))))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockMetal8, 10))
            .addElement('L', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 9))
            .addElement(
                'M',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_X.getFrameBox(1)
                            .getItem())))
            .addElement('N', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 6))
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
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        wirelessUpgrade = true;
        super.onFirstTick(aBaseMetaTileEntity);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing > 500;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        if (wirelessMode) {
            logic.setAvailableVoltage(Long.MAX_VALUE);
            logic.setAvailableAmperage(1);
            logic.setAmperageOC(true);
            logic.enablePerfectOverclock();
        } else {
            logic.setAvailableVoltage(getMaxInputEu());
            logic.setAvailableAmperage(1);
            logic.setAmperageOC(true);
        }
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.transcendentPlasmaMixerRecipes;
    }
}
