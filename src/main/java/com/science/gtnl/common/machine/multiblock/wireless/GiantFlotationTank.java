package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsBA0;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsAlloy;

public class GiantFlotationTank extends WirelessEnergyMultiMachineBase<GiantFlotationTank> {

    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 5;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String GFT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/giant_flotation_tank";
    private static final String[][] shape = StructureUtils.readStructureFromFile(GFT_STRUCTURE_FILE_PATH);

    public GiantFlotationTank(String aName) {
        super(aName);
    }

    public GiantFlotationTank(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GiantFlotationTank(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("GiantFlotationTankRecipeType"))
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
            .beginStructureBlock(27, 8, 74, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_GiantFlotationTank_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_GiantFlotationTank_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_GiantFlotationTank_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_GiantFlotationTank_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_GiantFlotationTank_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(18);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_SMELTER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_SMELTER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_SMELTER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_SMELTER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<GiantFlotationTank> getStructureDefinition() {
        return StructureDefinition.<GiantFlotationTank>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 5))
            .addElement('B', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('C', StructureUtility.ofBlock(sBlockCasingsBA0, 6))
            .addElement(
                'D',
                buildHatchAdder(GiantFlotationTank.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 2))))
            .addElement('E', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 11))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings11, 5))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 7))
            .addElement('H', StructureUtility.ofBlock(BlockLoader.metaCasing, 12))
            .addElement('I', StructureUtility.ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings11, 2))
            .addElement(
                'K',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.MARAGING300.getFrameBox(1)
                            .getItem())))
            .addElement('L', StructureUtility.ofBlock(ModBlocks.blockSpecialMultiCasings, 9))
            .addElement(
                'M',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.AQUATIC_STEEL.getFrameBox(1)
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mCountCasing > 100;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.CellRegulatorRecipes;
    }
}
