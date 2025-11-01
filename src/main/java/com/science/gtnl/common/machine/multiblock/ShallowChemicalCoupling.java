package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class ShallowChemicalCoupling extends GTMMultiMachineBase<ShallowChemicalCoupling>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SCC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/shallow_chemical_coupling";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SCC_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 9;
    private static final int DEPTH_OFF_SET = 0;

    public ShallowChemicalCoupling(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ShallowChemicalCoupling(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ShallowChemicalCoupling(this.mName);
    }

    @Override
    public IStructureDefinition<ShallowChemicalCoupling> getStructureDefinition() {
        return StructureDefinition.<ShallowChemicalCoupling>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(ShallowChemicalCoupling.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(BlockLoader.metaCasing, 19))))
            .addElement('B', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement(
                'C',
                GTStructureChannels.HEATING_COIL.use(
                    activeCoils(
                        ofCoil(ShallowChemicalCoupling::setMCoilLevel, ShallowChemicalCoupling::getMCoilLevel))))
            .addElement('D', ofBlock(sBlockCasings8, 1))
            .addElement('E', ofFrame(Materials.NaquadahAlloy))
            .build();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ShallowChemicalCouplingRecipes"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(7, 11, 7, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_Casing_00"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_Casing_00"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_Casing_00"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_Casing_00"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_Casing_00"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ShallowChemicalCoupling_Casing_00"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return GTUtility.getTextureId((byte) 116, (byte) 19);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Override
            public @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                return recipe.mSpecialValue <= mHeatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }

            @Nonnull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(getMachineHeat())
                    .setHeatOC(getHeatOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public boolean getHeatOC() {
        return true;
    }

    @Override
    public int getMachineHeat() {
        return mHeatingCapacity;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.85, getMCoilLevel().getTier());
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.85, getMCoilLevel().getTier());
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
        if (mMachine) return -1;
        this.setMCoilLevel(HeatingCoilLevel.None);
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
    public boolean checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 30;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        this.mHeatingCapacity = (int) this.getMCoilLevel()
            .getHeat();
    }

    @Override
    public boolean checkHatch() {
        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (mGlassTier < VoltageIndex.UHV & mEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        for (MTEHatch mExoticEnergyHatch : this.mExoticEnergyHatches) {
            if (mGlassTier < VoltageIndex.UHV && mExoticEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.ShallowChemicalCouplingRecipes;
    }

}
