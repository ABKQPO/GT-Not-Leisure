package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
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
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.Utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

import bartworks.util.BWUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
import gregtech.common.misc.GTStructureChannels;

public class ElectricBlastFurnace extends MultiMachineBase<ElectricBlastFurnace> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String EBF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/electric_blast_furnace";
    public static final int CASING_INDEX = ((BlockCasings1) sBlockCasings1).getTextureIndex(11);
    public static final String[][] shape = StructureUtils.readStructureFromFile(EBF_STRUCTURE_FILE_PATH);
    protected final int HORIZONTAL_OFF_SET = 2;
    protected final int VERTICAL_OFF_SET = 4;
    protected final int DEPTH_OFF_SET = 0;

    public ElectricBlastFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ElectricBlastFurnace(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ElectricBlastFurnace(this.mName);
    }

    @Override
    public IStructureDefinition<ElectricBlastFurnace> getStructureDefinition() {
        return StructureDefinition.<ElectricBlastFurnace>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(ElectricBlastFurnace.class)
                    .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance, Energy)
                    .casingIndex(CASING_INDEX)
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings1, 11))))
            .addElement('B', ofBlock(sBlockCasings2, 0))
            .addElement('C', ofBlock(sBlockCasings3, 10))
            .addElement('D', ofBlock(sBlockCasings4, 1))
            .addElement(
                'E',
                GTStructureChannels.HEATING_COIL
                    .use(activeCoils(ofCoil(ElectricBlastFurnace::setMCoilLevel, ElectricBlastFurnace::getMCoilLevel))))
            .addElement('F', ofFrame(Materials.StainlessSteel))
            .addElement('G', Muffler.newAny(CASING_INDEX, 1))
            .build();
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_05"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(5, 6, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_Casing_00"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_Casing_00"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_Casing_00"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_Casing_00"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_Casing_00"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_Casing_00"))
            .addMufflerHatch(StatCollector.translateToLocal("Tooltip_ElectricBlastFurnace_Casing_01"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return false;
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
        return CASING_INDEX;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Nonnull
            @Override
            protected GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(ElectricBlastFurnace.this.mHeatingCapacity)
                    .setHeatOC(true)
                    .setHeatDiscount(false)
                    .setEUtDiscount(0.9 * Math.pow(0.95, getMCoilLevel().getTier()))
                    .setDurationModifier(1.0 / 1.25 * Math.pow(0.95, getMCoilLevel().getTier()));
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                return recipe.mSpecialValue <= ElectricBlastFurnace.this.mHeatingCapacity
                    ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public int getMaxParallelRecipes() {
        return Math.max(1, GTUtility.getTier(this.getMaxInputVoltage()) * 2 + (getMCoilLevel().getTier() + 1) * 2);
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
        this.mHeatingCapacity = 0;
        mCountCasing = 0;
        mEnergyHatchTier = 0;
        this.setMCoilLevel(HeatingCoilLevel.None);

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        mEnergyHatchTier = checkEnergyHatchTier();

        if (getMCoilLevel() == HeatingCoilLevel.None) return false;

        if (mMaintenanceHatches.size() != 1 || mMufflerHatches.size() != 1) return false;

        this.mHeatingCapacity = (int) this.getMCoilLevel()
            .getHeat() + 100 * (BWUtil.getTier(this.getMaxInputEu()) - 2);
        return mCountCasing >= 30;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.blastFurnaceRecipes;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MEGA_BLAST_FURNACE_LOOP;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && getMaxInputAmps() <= 2;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(useSingleAmp ? 1 : getMaxInputAmps());
        logic.setAmperageOC(!useSingleAmp);
    }

}
