package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gtPlusPlus.core.block.ModBlocks.blockCasings2Misc;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class FishingGround extends GTMMultiMachineBase<FishingGround> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String FG_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/fishing_ground";
    private static final int HORIZONTAL_OFF_SET = 6;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(FG_STRUCTURE_FILE_PATH);

    public FishingGround(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FishingGround(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FishingGround(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDAlgaePondBaseActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDAlgaePondBase)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(18);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.FishingGroundRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("FishingGroundRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FishingGround_00"))
            .addPerfectOCInfo()
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(13, 4, 13, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_FishingGround_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_FishingGround_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_FishingGround_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_FishingGround_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<FishingGround> getStructureDefinition() {
        return StructureDefinition.<FishingGround>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(sBlockCasings2, 13))
            .addElement('B', GTStructureUtility.ofFrame(Materials.StainlessSteel))
            .addElement(
                'C',
                GTStructureUtility.buildHatchAdder(FishingGround.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(blockCasings2Misc, 2))))
            .addElement(
                'D',
                StructureUtility.ofChain(
                    StructureUtility.ofBlockAnyMeta(Blocks.water),
                    StructureUtility.ofBlockAnyMeta(Blocks.flowing_water),
                    StructureUtility.isAir()))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        boolean isFlipped = this.getFlip()
            .isHorizontallyFlipped();
        StructureUtils.setStringBlockXZ(
            aBaseMetaTileEntity,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            shape,
            isFlipped,
            "D",
            Blocks.water);
        setupParameters();
        return mCountCasing >= 25;
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated();
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
