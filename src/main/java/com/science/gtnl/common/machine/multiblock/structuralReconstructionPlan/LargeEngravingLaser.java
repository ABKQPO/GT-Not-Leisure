package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

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

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
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
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class LargeEngravingLaser extends GTMMultiMachineBase<LargeEngravingLaser> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String LEL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_engraving_laser";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LEL_STRUCTURE_FILE_PATH);

    public LargeEngravingLaser(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeEngravingLaser(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeEngravingLaser(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings10, 1)),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ENGRAVER_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ENGRAVER_ACTIVE_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            } else {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings10, 1)),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ENGRAVER)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ENGRAVER_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            }
        }
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings10, 1)) };

    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings10, 1);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.laserEngraverRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeEngravingLaserRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEngravingLaser_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEngravingLaser_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEngravingLaser_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(5, 4, 5, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeEngravingLaser_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeEngravingLaser_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeEngravingLaser_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeEngravingLaser_Casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeEngravingLaser> getStructureDefinition() {
        return StructureDefinition.<LargeEngravingLaser>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(LargeEngravingLaser.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 1))))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 15))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 10))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 25;
    }

    @Override
    public boolean checkHatch() {
        mEnergyHatchTier = checkEnergyHatchTier();
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
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return Math.max(0.05, 1.0 / 3.5 - (Math.max(0, mParallelTier - 1) / 50.0));
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
