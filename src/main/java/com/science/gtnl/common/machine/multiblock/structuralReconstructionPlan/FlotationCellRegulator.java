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
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class FlotationCellRegulator extends GTMMultiMachineBase<FlotationCellRegulator>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String FCR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/flotation_cell_regulator";
    private static final int HORIZONTAL_OFF_SET = 6;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 1;
    private static final String[][] shape = StructureUtils.readStructureFromFile(FCR_STRUCTURE_FILE_PATH);

    public FlotationCellRegulator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FlotationCellRegulator(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.flotation_cell_regulator.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FlotationCellRegulator(this.mName);
    }

    @Override
    public IStructureDefinition<FlotationCellRegulator> getStructureDefinition() {
        return StructureDefinition.<FlotationCellRegulator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', Casings.TungstensteelPipeCasing.asElement())
            .addElement('C', Casings.HastelloyNSealantBlock.asElement())
            .addElement(
                'D',
                GTStructureUtility.buildHatchAdder(FlotationCellRegulator.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.InputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputBus,
                        HatchElement.OutputHatch,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, Casings.HastelloyXStructuralBlock.asElement())))
            .addElement('E', Casings.InconelReinforcedCasing.asElement())
            .addElement('F', Casings.FlotationCellCasings.asElement())
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 25);
    }

    @Override
    protected int getGlassEnergyTierLimit() {
        return VoltageIndex.UHV;
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
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.CellRegulatorRecipes;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public double getEUtDiscount() {
        return 1 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1 - (Math.max(0, mParallelTier - 1) / 50.0);
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(18);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDFrothFlotationCellActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDFrothFlotationCell)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.flotation_cell_regulator.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.flotation_cell_regulator.tooltip.0"))
            .addPerfectOCInfo()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.3"))
            .addSupportMultiAmp()
            .beginStructureBlock(9, 5, 7, true)
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.flotation_cell_regulator.tooltip.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.flotation_cell_regulator.tooltip.casing"))
            .addEnergyHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.flotation_cell_regulator.tooltip.casing"))
            .addMaintenanceHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.flotation_cell_regulator.tooltip.casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }
}
