package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gtPlusPlus.core.block.ModBlocks.blockCasings2Misc;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeRockCrusher;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
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
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class MantleCrusher extends WirelessEnergyMultiMachineBase<MantleCrusher> {

    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 10;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/mantle_crusher";
    private static final String[][] shape = StructureUtils.readStructureFromFile(MC_STRUCTURE_FILE_PATH);

    public MantleCrusher(String aName) {
        super(aName);
    }

    public MantleCrusher(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MantleCrusher(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MantleCrusherRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MantleCrusher_00"))
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
            .beginStructureBlock(15, 12, 15, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_MantleCrusher_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MantleCrusher_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_MantleCrusher_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_MantleCrusher_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MantleCrusher_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10);
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
    public IStructureDefinition<MantleCrusher> getStructureDefinition() {
        return StructureDefinition.<MantleCrusher>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement('B', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 12))
            .addElement('D', StructureUtility.ofBlock(BlockLoader.metaCasing, 16))
            .addElement(
                'E',
                GTStructureUtility.buildHatchAdder(MantleCrusher.class)
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
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 1))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 13))
            .addElement('H', StructureUtility.ofBlock(blockCasings2Misc, 12))
            .addElement('I', GTStructureUtility.ofFrame(Materials.Naquadah))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 9))
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
        return mCountCasing > 200;
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
            if (GTUtility.areFluidsEqual(fluid, LargeRockCrusher.water)) {
                hasWater = true;
            }
            if (GTUtility.areFluidsEqual(fluid, LargeRockCrusher.lava)) {
                hasLava = true;
            }
            if (hasWater && hasLava) break;
        }

        if (!hasWater || !hasLava) return CheckRecipeResultRegistry.NO_RECIPE;

        return super.checkProcessing();
    }

}
