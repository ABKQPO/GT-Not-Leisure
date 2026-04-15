package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

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
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class AdvancedRareEarthCentrifugal extends GTMMultiMachineBase<AdvancedRareEarthCentrifugal>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String AREC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/advanced_rare_earth_centrifugal";
    private static final int HORIZONTAL_OFF_SET = 11;
    private static final int VERTICAL_OFF_SET = 11;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(AREC_STRUCTURE_FILE_PATH);

    public AdvancedRareEarthCentrifugal(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public AdvancedRareEarthCentrifugal(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new AdvancedRareEarthCentrifugal(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialThermalCentrifugeActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialThermalCentrifuge)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 7);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.RareEarthCentrifugalRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("AdvancedRareEarthCentrifugalRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(23, 13, 24, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_AdvancedRareEarthCentrifugal_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_AdvancedRareEarthCentrifugal_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_AdvancedRareEarthCentrifugal_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_AdvancedRareEarthCentrifugal_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_AdvancedRareEarthCentrifugal_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_AdvancedRareEarthCentrifugal_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<AdvancedRareEarthCentrifugal> getStructureDefinition() {
        return StructureDefinition.<AdvancedRareEarthCentrifugal>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(Loaders.compactFusionCoil, 1))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 0))
            .addElement(
                'C',
                GTStructureUtility.buildHatchAdder(AdvancedRareEarthCentrifugal.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('E', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement('F', GTStructureUtility.ofFrame(Materials.TungstenSteel))
            .addElement('G', StructureUtility.ofBlock(ModBlocks.blockSpecialMultiCasings, 11))
            .addElement('H', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('I', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 20;
    }

    @Override
    public boolean checkEnergyHatch() {
        return true;
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
}
