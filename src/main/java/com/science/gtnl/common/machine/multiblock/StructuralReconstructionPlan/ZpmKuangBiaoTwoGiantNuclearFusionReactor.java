package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.validMTEList;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;
import static gtnhlanth.common.register.LanthItemList.ELECTRODE_CASING;

import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
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
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;

public class ZpmKuangBiaoTwoGiantNuclearFusionReactor
    extends GTMMultiMachineBase<ZpmKuangBiaoTwoGiantNuclearFusionReactor> implements ISurvivalConstructable {

    public GTRecipe lastRecipe;
    public long mEUStore;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<ZpmKuangBiaoTwoGiantNuclearFusionReactor> STRUCTURE_DEFINITION = null;
    public static final String KBFR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/kuang_biao_giant_nuclear_fusion_reactor";
    public static final int CASING_INDEX = GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings4, 6);
    public final int HORIZONTAL_OFF_SET = 19;
    public final int VERTICAL_OFF_SET = 14;
    public final int DEPTH_OFF_SET = 0;
    public static String[][] shape = StructureUtils.readStructureFromFile(KBFR_STRUCTURE_FILE_PATH);

    public ZpmKuangBiaoTwoGiantNuclearFusionReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ZpmKuangBiaoTwoGiantNuclearFusionReactor(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ZpmKuangBiaoTwoGiantNuclearFusionReactor(this.mName);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        mParallelTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) {
            return false;
        }

        energyHatchTier = checkEnergyHatchTier();
        if (!checkHatch()) {
            return false;
        }

        mParallelTier = getParallelTier(aStack);
        return tCountCasing >= 1500;
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
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("KuangBiaoOneGiantNuclearFusionReactorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_04") + "320M EU")
            .addInfo(StatCollector.translateToLocal("Tooltip_ZpmKuangBiaoTwoGiantNuclearFusionReactor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(39, 17, 39, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_ZpmKuangBiaoTwoGiantNuclearFusionReactor_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ZpmKuangBiaoTwoGiantNuclearFusionReactor_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ZpmKuangBiaoTwoGiantNuclearFusionReactor_Casing"))
            .addMaintenanceHatch(
                StatCollector.translateToLocal("Tooltip_ZpmKuangBiaoTwoGiantNuclearFusionReactor_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.fusionRecipes;
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -2;
    }

    @Override
    public long maxEUStore() {
        return 320000000L;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            mTotalRunTime++;
            if (mEfficiency < 0) mEfficiency = 0;
            if (mUpdated) {
                if (mUpdate <= 0) mUpdate = 50;
                mUpdated = false;
            }
            if (--mUpdate == 0 || --mStartUpCheck == 0) {
                checkStructure(true, aBaseMetaTileEntity);
                updateHatchTexture();
            }

            if (mStartUpCheck < 0) {
                if (mMachine) {
                    this.mEUStore = aBaseMetaTileEntity.getStoredEU();
                    long maxEnergy = maxEUStore();

                    if (!this.mEnergyHatches.isEmpty()) {
                        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
                            if (mEUStore >= maxEnergy) break;

                            long availableEnergy = tHatch.getBaseMetaTileEntity()
                                .getStoredEU();
                            long remainingCapacity = maxEnergy - mEUStore;

                            long energyToMove = Math.min(availableEnergy, remainingCapacity);

                            if (tHatch.getBaseMetaTileEntity()
                                .decreaseStoredEnergyUnits(energyToMove, false)) {
                                aBaseMetaTileEntity.increaseStoredEnergyUnits(energyToMove, true);
                                mEUStore += energyToMove;
                            }
                        }
                    }

                    if (!this.mExoticEnergyHatches.isEmpty()) {
                        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
                            if (mEUStore >= maxEnergy) break;

                            long availableEnergy = tHatch.getBaseMetaTileEntity()
                                .getStoredEU();
                            long remainingCapacity = maxEnergy - mEUStore;

                            long energyToMove = Math.min(availableEnergy, remainingCapacity);

                            if (tHatch.getBaseMetaTileEntity()
                                .decreaseStoredEnergyUnits(energyToMove, false)) {
                                aBaseMetaTileEntity.increaseStoredEnergyUnits(energyToMove, true);
                                mEUStore += energyToMove;
                            }
                        }
                    }

                    if (this.mEUStore <= 0 && mMaxProgresstime > 0) {
                        stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                    }
                    if (mMaxProgresstime > 0) {
                        this.getBaseMetaTileEntity()
                            .decreaseStoredEnergyUnits(-lEUt, true);
                        if (mMaxProgresstime > 0 && ++mProgresstime >= mMaxProgresstime) {
                            if (mOutputItems != null)
                                for (ItemStack tStack : mOutputItems) if (tStack != null) addOutput(tStack);
                            if (mOutputFluids != null)
                                for (FluidStack tStack : mOutputFluids) if (tStack != null) addOutput(tStack);
                            mEfficiency = 10000;
                            mOutputItems = null;
                            mOutputFluids = null;
                            mProgresstime = 0;
                            mMaxProgresstime = 0;
                            mEfficiencyIncrease = 0;
                            mLastWorkingTick = mTotalRunTime;
                            this.mEUStore = aBaseMetaTileEntity.getStoredEU();
                            this.lastRecipe = null;
                            if (aBaseMetaTileEntity.isAllowedToWork()) {
                                checkRecipe();
                            }
                        }
                    } else {
                        if (aTick % 100 == 0 || aBaseMetaTileEntity.hasWorkJustBeenEnabled()
                            || aBaseMetaTileEntity.hasInventoryBeenModified()) {
                            if (aBaseMetaTileEntity.isAllowedToWork()) {
                                this.mEUStore = aBaseMetaTileEntity.getStoredEU();
                                if (checkRecipe()) {
                                    markDirty();
                                    if (this.mEUStore < this.lastRecipe.mSpecialValue + this.lEUt) {
                                        stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                                    }
                                    aBaseMetaTileEntity.decreaseStoredEnergyUnits(this.lEUt, true);
                                }
                            }
                            if (mMaxProgresstime <= 0) mEfficiency = 10000;
                        }
                    }
                } else if (aBaseMetaTileEntity.isAllowedToWork()) {
                    this.lastRecipe = null;
                    stopMachine(ShutDownReasonRegistry.STRUCTURE_INCOMPLETE);
                }
            }
            aBaseMetaTileEntity.setActive(mMaxProgresstime > 0);
        } else {
            doActivitySound(getActivitySoundLoop());
        }
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setAmperageOC(true)
                    .setDurationDecreasePerOC(4)
                    .setEUtIncreasePerOC(4)
                    .setAmperage(availableAmperage)
                    .setRecipeEUt(recipe.mEUt)
                    .setEUt(availableVoltage)
                    .setEUtDiscount(4 - (mParallelTier / 12.5))
                    .setDurationModifier(1.0 / 2.0 - (mParallelTier / 200.0));
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                ZpmKuangBiaoTwoGiantNuclearFusionReactor.this.lastRecipe = null;
                if (!mRunningOnLoad) {
                    if (recipe.mSpecialValue > mEUStore) {
                        return CheckRecipeResultRegistry.insufficientStartupPower(recipe.mSpecialValue);
                    }
                    if (recipe.mEUt > GTValues.V[8]) {
                        return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                    }
                }
                ZpmKuangBiaoTwoGiantNuclearFusionReactor.this.lastRecipe = recipe;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public IStructureDefinition<ZpmKuangBiaoTwoGiantNuclearFusionReactor> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<ZpmKuangBiaoTwoGiantNuclearFusionReactor>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlockAnyMeta(ELECTRODE_CASING))
                .addElement('B', ofBlock(sBlockCasings8, 10))
                .addElement(
                    'C',
                    buildHatchAdder(ZpmKuangBiaoTwoGiantNuclearFusionReactor.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings4, 6))))
                .addElement('D', ofBlock(sBlockCasings9, 3))
                .addElement('E', ofFrame(Materials.Tungsten))
                .addElement('F', ofFrame(Materials.Duranium))
                .addElement('G', ofBlock(BlockLoader.MetaBlockGlass, 2))
                .addElement('H', ofBlock(blockCasingsMisc, 5))
                .addElement('I', ofBlock(Loaders.compactFusionCoil, 1))
                .addElement('J', ofBlock(blockCasingsMisc, 15))
                .addElement('K', ofBlock(sBlockCasings10, 3))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_FUSION_LOOP;
    }

    @Override
    public String[] getInfoData() {
        double plasmaOut = 0;
        if (mMaxProgresstime > 0) plasmaOut = (double) mOutputFluids[0].amount / mMaxProgresstime;

        return new String[] {
            EnumChatFormatting.BLUE + "Fusion Reactor MK "
                + EnumChatFormatting.RESET
                + EnumChatFormatting.RED
                + "II"
                + EnumChatFormatting.RESET,
            StatCollector.translateToLocal("scanner.info.UX.0") + ": "
                + EnumChatFormatting.LIGHT_PURPLE
                + GTUtility.formatNumbers(getMaxParallelRecipes())
                + EnumChatFormatting.RESET,
            StatCollector.translateToLocal("GT5U.fusion.req") + ": "
                + EnumChatFormatting.RED
                + GTUtility.formatNumbers(-lEUt)
                + EnumChatFormatting.RESET
                + "EU/t",
            StatCollector.translateToLocal("GT5U.fusion.plasma") + ": "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(plasmaOut)
                + EnumChatFormatting.RESET
                + "L/t" };
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public Set<VoidingMode> getAllowedVoidingModes() {
        return VoidingMode.FLUID_ONLY_MODES;
    }
}
