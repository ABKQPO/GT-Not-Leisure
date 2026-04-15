package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gtPlusPlus.core.block.ModBlocks.blockCasings2Misc;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class LargeAssembler extends GTMMultiMachineBase<LargeAssembler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String LA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_assembler";
    private static final int HORIZONTAL_OFF_SET = 6;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LA_STRUCTURE_FILE_PATH);

    public LargeAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeAssembler(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeAssembler(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.getIndexFromPage(0, 10);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblerRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeAssembler_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(9, 3, 3, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeAssembler_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeAssembler_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeAssembler_Casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeAssembler> getStructureDefinition() {
        return StructureDefinition.<LargeAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(LargeAssembler.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(blockCasings2Misc, 12))))
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
    public boolean checkHatch() {
        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (mGlassTier < VoltageIndex.UHV && mEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        for (MTEHatch mExoticEnergyHatch : this.mExoticEnergyHatches) {
            if (mGlassTier < VoltageIndex.UHV && mExoticEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        return super.checkHatch();
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
    public double getEUtDiscount() {
        return super.getEUtDiscount();
    }

    @Override
    public double getDurationModifier() {
        return 1 / 3.0 - (Math.max(0, mParallelTier - 1) / 50.0);
    }
}
