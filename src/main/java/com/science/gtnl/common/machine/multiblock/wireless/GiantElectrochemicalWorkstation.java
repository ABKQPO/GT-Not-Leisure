package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
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
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import gtnhintergalactic.tile.multi.elevator.TileEntitySpaceElevator;
import gtnhlanth.common.register.LanthItemList;

@IMetaTileEntity.SkipGenerateDescription
public class GiantElectrochemicalWorkstation extends WirelessEnergyMultiMachineBase<GiantElectrochemicalWorkstation> {

    private static final int HORIZONTAL_OFF_SET = 22;
    private static final int VERTICAL_OFF_SET = 6;
    private static final int DEPTH_OFF_SET = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String GEW_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/giant_electrochemical_workstation";
    private static final String[][] shape = StructureUtils.readStructureFromFile(GEW_STRUCTURE_FILE_PATH);

    public GiantElectrochemicalWorkstation(String aName) {
        super(aName);
    }

    public GiantElectrochemicalWorkstation(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GiantElectrochemicalWorkstation(this.mName);
    }

    @Override
    public IStructureDefinition<GiantElectrochemicalWorkstation> getStructureDefinition() {
        return StructureDefinition.<GiantElectrochemicalWorkstation>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', Casings.CompressionPipeCasing.asElement())
            .addElement('B', Casings.ElectrolyzerCasing.asElement())
            .addElement('C', Casings.PTFEPipeCasing.asElement())
            .addElement('D', Casings.HermeticCasing9.asElement())
            .addElement('E', Casings.NeutroniumStabilizationCasing.asElement())
            .addElement('F', Casings.AdvancedIridiumPlatedMachineCasing.asElement())
            .addElement(
                'G',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(
                            GiantElectrochemicalWorkstation::setMCoilLevel,
                            GiantElectrochemicalWorkstation::getMCoilLevel))))
            .addElement(
                'H',
                GTStructureUtility.buildHatchAdder(GiantElectrochemicalWorkstation.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            Casings.StabilizedNaquadahWaterPlantCasing.asElement())))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockTintedGlass, 1))
            .addElement('J', GTStructureUtility.ofFrame(Materials.Polytetrafluoroethylene))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSE, 0))
            .addElement(
                'L',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_N.getFrameBox(1)
                            .getItem())))
            .addElement('M', StructureUtility.ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 101);
    }

    @Override
    protected boolean requiresCoilStructureCheck() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.electrolyzerNonCellRecipes;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.85, getMCoilLevel().getTier());
    }

    @Override
    public int getCasingTextureID() {
        return Casings.StabilizedNaquadahWaterPlantCasing.getTextureId();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialElectrolyzerActive)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialElectrolyzerActiveGlow)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialElectrolyzer)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialElectrolyzerGlow)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.giant_electrochemical_workstation.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.giant_electrochemical_workstation.info"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.5"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.6"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.7"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.8"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.9"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.10"))
            .addSupportAny()
            .beginStructureBlock(45, 13, 15, true)
            .addInputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.giant_electrochemical_workstation.casing"),
                1)
            .addOutputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.giant_electrochemical_workstation.casing"),
                1)
            .addInputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.giant_electrochemical_workstation.casing"),
                1)
            .addOutputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.giant_electrochemical_workstation.casing"),
                1)
            .addEnergyHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.giant_electrochemical_workstation.casing"),
                1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

}
