package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.sBlockCasings4;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsTieredGTPP;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class LargeRockCrusher extends GTMMultiMachineBase<LargeRockCrusher> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String LRC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_rock_crusher";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LRC_STRUCTURE_FILE_PATH);

    public static FluidStack water = Materials.Water.getFluid(1);
    public static FluidStack lava = Materials.Lava.getFluid(1);

    public LargeRockCrusher(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeRockCrusher(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeRockCrusher(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAIndustrialRockBreakerActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAIndustrialRockBreaker)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings4, 14);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.IndustrialRockCrusherRecipes;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        boolean hasWater = false;
        boolean hasLava = false;

        for (FluidStack fluid : getStoredFluids()) {
            if (GTUtility.areFluidsEqual(fluid, water)) {
                hasWater = true;
            }
            if (GTUtility.areFluidsEqual(fluid, lava)) {
                hasLava = true;
            }
            if (hasWater && hasLava) break;
        }

        if (!hasWater || !hasLava) return CheckRecipeResultRegistry.NO_RECIPE;

        return super.checkProcessing();
    }

    @Override
    public double getEUtDiscount() {
        return 1;
    }

    @Override
    public double getDurationModifier() {
        return 1;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeRockCrusherRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeRockCrusher_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(5, 4, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeRockCrusher_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeRockCrusher_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeRockCrusher_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeRockCrusher_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeRockCrusher_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeRockCrusher> getStructureDefinition() {
        return StructureDefinition.<LargeRockCrusher>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.MARAGING350.getFrameBox(1)
                            .getItem())))
            .addElement(
                'B',
                buildHatchAdder(LargeRockCrusher.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasings4, 14))))
            .addElement('C', StructureUtility.ofBlock(blockCasingsTieredGTPP, 2))
            .addElement('D', StructureUtility.ofBlock(BlockLoader.metaBlockColumn, 2))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 30;
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
