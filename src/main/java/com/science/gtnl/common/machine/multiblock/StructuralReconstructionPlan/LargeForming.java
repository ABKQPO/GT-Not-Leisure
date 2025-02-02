package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings3;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.core.block.ModBlocks.blockCasings3Misc;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialPlatePress;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialPlatePressActive;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.Utils.item.TextUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;

public class LargeForming extends GTMMultiMachineBase<LargeForming> implements ISurvivalConstructable {

    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<LargeForming> STRUCTURE_DEFINITION = null;
    public static final String LF_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/large_forming";
    public static final int CASING_INDEX = TAE.GTPP_INDEX(33);
    public final int horizontalOffSet = 3;
    public final int verticalOffSet = 2;
    public final int depthOffSet = 0;
    public static String[][] shape;

    public LargeForming(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        shape = StructureUtils.readStructureFromFile(LF_STRUCTURE_FILE_PATH);
    }

    public LargeForming(String aName) {
        super(aName);
        shape = StructureUtils.readStructureFromFile(LF_STRUCTURE_FILE_PATH);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeForming(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(oMCDIndustrialPlatePressActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(oMCDIndustrialPlatePress)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.formingPressRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.LargeFormingRecipeType)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_00)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_01)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_02)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_03)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(7, 3, 3, true)
            .addInputBus(TextLocalization.Tooltip_LargeForming_Casing)
            .addOutputBus(TextLocalization.Tooltip_LargeForming_Casing)
            .addEnergyHatch(TextLocalization.Tooltip_LargeForming_Casing)
            .addMaintenanceHatch(TextLocalization.Tooltip_LargeForming_Casing)
            .toolTipFinisher(TextUtils.SNL + TextUtils.SQY + " §rX " + TextUtils.SRP);
        return tt;
    }

    @Override
    public IStructureDefinition<LargeForming> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeForming>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlockAnyMeta(GameRegistry.findBlock(IndustrialCraft2.ID, "blockAlloyGlass")))
                .addElement('B', ofBlock(sBlockCasings2, 5))
                .addElement(
                    'C',
                    buildHatchAdder(LargeForming.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputBus, OutputBus, Maintenance, Energy)
                        .buildAndChain(onElementPass(x -> ++x.mCasing, ofBlock(blockCasings3Misc, 1))))
                .addElement('D', ofBlock(sBlockCasings3, 10))
                .addElement('E', ofFrame(Materials.StainlessSteel))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCasing = 0;
        ParallelTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet) && checkHatch()) {
            return false;
        }

        ParallelTier = getParallelTier(aStack);
        return mCasing >= 10;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);
    }
}
