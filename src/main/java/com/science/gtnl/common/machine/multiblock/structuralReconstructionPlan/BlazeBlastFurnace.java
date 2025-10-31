package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.GTValues.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import bartworks.util.BWUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class BlazeBlastFurnace extends MultiMachineBase<BlazeBlastFurnace> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String BBF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/blaze_blast_furnace";
    public static final String[][] shape = StructureUtils.readStructureFromFile(BBF_STRUCTURE_FILE_PATH);
    protected final int HORIZONTAL_OFF_SET = 3;
    protected final int VERTICAL_OFF_SET = 3;
    protected final int DEPTH_OFF_SET = 1;
    public int mMultiTier = 1;

    public BlazeBlastFurnace(final int aID, final String aName, final String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public BlazeBlastFurnace(final String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new BlazeBlastFurnace(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("BlazeBlastFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .beginStructureBlock(7, 6, 7, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_00"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_00"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_00"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_00"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_01"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_00"), 1)
            .addMufflerHatch(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_02"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_00"), 1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("FluidBlazeInputHatch"),
                StatCollector.translateToLocal("Tooltip_BlazeBlastFurnace_Casing_00"),
                1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<BlazeBlastFurnace> getStructureDefinition() {
        return StructureDefinition.<BlazeBlastFurnace>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings2, 15))
            .addElement(
                'B',
                GTStructureChannels.HEATING_COIL
                    .use(activeCoils(ofCoil(BlazeBlastFurnace::setMCoilLevel, BlazeBlastFurnace::getMCoilLevel))))
            .addElement('C', ofBlock(ModBlocks.blockCasings3Misc, 11))
            .addElement('D', ofBlock(ModBlocks.blockCasingsMisc, 14))
            .addElement(
                'E',
                ofChain(
                    buildHatchAdder(BlazeBlastFurnace.class)
                        .atLeast(
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch,
                            Energy.or(ExoticEnergy),
                            Maintenance,
                            ParallelCon)
                        .dot(1)
                        .casingIndex(getCasingTextureID())
                        .build(),
                    onElementPass(x -> ++x.mCountCasing, ofBlock(ModBlocks.blockCasingsMisc, 15)),
                    buildHatchAdder(BlazeBlastFurnace.class).adder(BlazeBlastFurnace::addFluidBlazeInputHatch)
                        .hatchId(21503)
                        .shouldReject(x -> !x.mFluidBlazeInputHatch.isEmpty())
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .build()))
            .addElement('F', Muffler.newAny(TAE.getIndexFromPage(2, 11), 1))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
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

    public int getMultiTier(ItemStack inventory) {
        if (inventory == null) return 1;
        if (inventory.isItemEqual(GTNLItemList.BlazeCube.get(1))) return 4;
        return 1;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 50;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mMultiTier = 1;
        mFluidBlazeInputHatch.clear();
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        this.mMultiTier = getMultiTier(getControllerSlot());
        this.mHeatingCapacity = (int) getMCoilLevel().getHeat() + 100 * (BWUtil.getTier(getMaxInputVoltage()) - 2);
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && !mFluidBlazeInputHatch.isEmpty()
            && getMCoilLevel() != HeatingCoilLevel.None
            && checkEnergyHatch();
    }

    @Override
    public void updateSlots() {
        for (CustomFluidHatch tHatch : validMTEList(mFluidBlazeInputHatch)) tHatch.updateSlots();
        super.updateSlots();
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(15);
    }

    @Override
    public void updateHatchTexture() {
        super.updateHatchTexture();
        for (MTEHatch h : mMufflerHatches) h.updateTexture(TAE.getIndexFromPage(2, 11));
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAAdvancedEBFActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAAdvancedEBF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.blastFurnaceRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Nonnull
            @Override
            protected GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(getMachineHeat())
                    .setHeatOC(getHeatOC())
                    .setHeatDiscount(getHeatDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                return recipe.mSpecialValue <= mHeatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public int getMachineHeat() {
        return mHeatingCapacity;
    }

    @Override
    public boolean getHeatDiscount() {
        return true;
    }

    @Override
    public boolean getHeatOC() {
        return true;
    }

    @Override
    public double getDurationModifier() {
        return 1.0 / 2.5;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 64 * mMultiTier;
    }

    @Override
    public int getMaxTierSkip() {
        return 0;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (this.mStartUpCheck < 0) {
            startRecipeProcessing();
            if (this.mMaxProgresstime > 0 && aTick % 20 == 0) {
                int baseAmount = (int) (10 * Math.pow(GTUtility.getTier(-lEUt), 2)) * mMultiTier;
                if (!this.depleteInputFromRestrictedHatches(this.mFluidBlazeInputHatch, baseAmount)) {
                    this.causeMaintenanceIssue();
                    this.stopMachine(
                        ShutDownReasonRegistry
                            .outOfFluid(Objects.requireNonNull(FluidUtils.getFluidStack("molten.blaze", baseAmount))));
                }
            }
            endRecipeProcessing();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_ADV_FREEZER_LOOP;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 4;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(!useSingleAmp);
    }

    @Override
    public String[] getInfoData() {
        long storedEnergy = 0;
        long maxEnergy = 0;
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            storedEnergy += tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            maxEnergy += tHatch.getBaseMetaTileEntity()
                .getEUCapacity();
        }

        return new String[] {
            StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(mProgresstime / 20)
                + EnumChatFormatting.RESET
                + " s / "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(mMaxProgresstime / 20)
                + EnumChatFormatting.RESET
                + " s",
            StatCollector.translateToLocal("GT5U.multiblock.energy") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(storedEnergy)
                + EnumChatFormatting.RESET
                + " EU / "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(maxEnergy)
                + EnumChatFormatting.RESET
                + " EU",
            StatCollector.translateToLocal("GT5U.multiblock.usage") + ": "
                + EnumChatFormatting.RED
                + GTUtility.formatNumbers(-lEUt)
                + EnumChatFormatting.RESET
                + " EU/t",
            StatCollector.translateToLocal("GT5U.multiblock.mei") + ": "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(getMaxInputVoltage())
                + EnumChatFormatting.RESET
                + " EU/t(*2A) "
                + StatCollector.translateToLocal("GT5U.machines.tier")
                + ": "
                + EnumChatFormatting.YELLOW
                + VN[GTUtility.getTier(getMaxInputVoltage())]
                + EnumChatFormatting.RESET,
            StatCollector.translateToLocal("GT5U.multiblock.problems") + ": "
                + EnumChatFormatting.RED
                + (getIdealStatus() - getRepairStatus())
                + EnumChatFormatting.RESET
                + " "
                + StatCollector.translateToLocal("GT5U.multiblock.efficiency")
                + ": "
                + EnumChatFormatting.YELLOW
                + mEfficiency / 100.0F
                + EnumChatFormatting.RESET
                + " %",
            StatCollector.translateToLocal("GT5U.EBF.heat") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(mHeatingCapacity)
                + EnumChatFormatting.RESET
                + " K",
            StatCollector.translateToLocal("GT5U.multiblock.pollution") + ": "
                + EnumChatFormatting.GREEN
                + getAveragePollutionPercentage()
                + EnumChatFormatting.RESET
                + " %" };
    }
}
