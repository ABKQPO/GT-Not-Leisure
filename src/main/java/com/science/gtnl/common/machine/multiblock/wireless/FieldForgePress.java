package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsAlloy;
import tectech.thing.casing.BlockGTCasingsTT;

public class FieldForgePress extends WirelessEnergyMultiMachineBase<FieldForgePress> {

    private static final int HORIZONTAL_OFF_SET = 8;
    private static final int VERTICAL_OFF_SET = 23;
    private static final int DEPTH_OFF_SET = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String FFP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/field_forge_press";
    private static final String[][] shape = StructureUtils.readStructureFromFile(FFP_STRUCTURE_FILE_PATH);

    public FieldForgePress(String aName) {
        super(aName);
    }

    public FieldForgePress(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FieldForgePress(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("FieldForgePressRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(17, 25, 27, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_FieldForgePress_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_FieldForgePress_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_FieldForgePress_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_FieldForgePress_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_FieldForgePress_Casing"), 1)
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
    public IStructureDefinition<FieldForgePress> getStructureDefinition() {
        return StructureDefinition.<FieldForgePress>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 12))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 9))
            .addElement('G', StructureUtility.ofBlock(ModBlocks.blockCasings3Misc, 1))
            .addElement(
                'H',
                buildHatchAdder(FieldForgePress.class)
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
            .addElement('I', StructureUtility.ofBlock(ModBlocks.blockCustomMachineCasings, 3))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('K', StructureUtility.ofBlock(BlockLoader.metaCasing, 12))
            .addElement(
                'L',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.INCONEL_792.getFrameBox(1)
                            .getItem())))
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing > 1800;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.hammerRecipes;
    }

}
