package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static com.science.gtnl.utils.Utils.*;
import static com.science.gtnl.utils.Utils.mergeArray;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.*;
import static gregtech.api.enums.GTValues.VN;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.*;
import static gregtech.common.misc.WirelessNetworkManager.*;
import static gtPlusPlus.core.block.ModBlocks.*;
import static gtnhlanth.common.register.LanthItemList.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.api.IWirelessEnergy;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.render.tile.KuangBiaoOneGiantNuclearFusionReactorRenderer;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
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
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.render.IMTERenderer;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class KuangBiaoOneGiantNuclearFusionReactor
    extends GTMMultiMachineBase<KuangBiaoOneGiantNuclearFusionReactor> implements ISurvivalConstructable, IMTERenderer {

    public GTRecipe mLastRecipe;
    public long mEUStore;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String KBFR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/kuang_biao_giant_nuclear_fusion_reactor";
    private static final int HORIZONTAL_OFF_SET = 19;
    private static final int VERTICAL_OFF_SET = 14;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(KBFR_STRUCTURE_FILE_PATH);

    public float rotation = 0;
    public float prevRotation = 0;
    public static float ROTATION_SPEED = 1.2f;
    public boolean enableRender = true;

    public KuangBiaoOneGiantNuclearFusionReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public KuangBiaoOneGiantNuclearFusionReactor(String aName) {
        super(aName);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 1500;
    }

    @Override
    public boolean checkEnergyHatch() {
        return true;
    }

    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        if (mMaxProgresstime <= 0 || !enableRender) return;
        KuangBiaoOneGiantNuclearFusionReactorRenderer.renderTileEntityAt(this, x, y, z, timeSinceLastTick);
    }

    @Override
    public void onValueUpdate(byte aValue) {
        mMachine = (aValue & 0x01) != 0;
        enableRender = (aValue & 0x02) != 0;
    }

    @Override
    public byte getUpdateData() {
        byte data = 0;
        if (mMachine) data |= 0x01;
        if (enableRender) data |= 0x02;
        return data;
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
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("enableRender", enableRender);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        enableRender = aNBT.getBoolean("enableRender");
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("KuangBiaoOneGiantNuclearFusionReactorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_00"))
            .addInfo(
                StatCollector.translateToLocalFormatted(
                    "Tooltip_KuangBiaoOneGiantNuclearFusionReactor_01",
                    (int) ((getMachineDurationModifier() - 1) * 100)))
            .addInfo(
                StatCollector.translateToLocalFormatted(
                    "Tooltip_KuangBiaoOneGiantNuclearFusionReactor_02",
                    (int) (getMachineEUtDiscount() * 100)))
            .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_03"))
            .addInfo(
                StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_04")
                    + GTUtility.formatNumbers(maxEUStore())
                    + " EU")
            .addInfo(
                StatCollector.translateToLocalFormatted(
                    "Tooltip_KuangBiaoOneGiantNuclearFusionReactor_05",
                    TIER_COLORS[getRecipeMaxTier()] + VN[getRecipeMaxTier()]))
            .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(39, 17, 39, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_KuangBiaoTwoGiantNuclearFusionReactor_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_KuangBiaoTwoGiantNuclearFusionReactor_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_KuangBiaoTwoGiantNuclearFusionReactor_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_KuangBiaoTwoGiantNuclearFusionReactor_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.fusionRecipes;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -2;
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.enableRender = !enableRender;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal(
                    "KuangBiaoOneGiantNuclearFusionReactor_Render_" + (this.enableRender ? "Enabled" : "Disabled")));
        }
        return true;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        prevRotation = rotation;
        rotation = (rotation + ROTATION_SPEED) % 360f;
        return super.onRunningTick(aStack);
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
                checkStructure(true);
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
                            this.mLastRecipe = null;
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
                                    if (this.mEUStore < this.mLastRecipe.mSpecialValue + this.lEUt) {
                                        stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                                    }
                                    aBaseMetaTileEntity.decreaseStoredEnergyUnits(this.lEUt, true);
                                }
                            }
                            if (mMaxProgresstime <= 0) mEfficiency = 10000;
                        }
                    }
                } else if (aBaseMetaTileEntity.isAllowedToWork()) {
                    this.mLastRecipe = null;
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
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setPerfectOC(getPerfectOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                long powerToStart = recipe.getMetadataOrDefault(GTRecipeConstants.FUSION_THRESHOLD, 0L);
                if (!mRunningOnLoad) {
                    if (powerToStart > mEUStore) {
                        return CheckRecipeResultRegistry.insufficientStartupPower(BigInteger.valueOf(powerToStart));
                    }
                    if (recipe.mEUt > GTValues.V[getRecipeMaxTier() + 1]) {
                        return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                    }
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                CheckRecipeResult result = super.process();
                if (mRunningOnLoad) mRunningOnLoad = false;
                if (result.wasSuccessful()) {
                    KuangBiaoOneGiantNuclearFusionReactor.this.mLastRecipe = lastRecipe;
                } else {
                    KuangBiaoOneGiantNuclearFusionReactor.this.mLastRecipe = null;
                }
                return result;
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return getMachineEUtDiscount() - (mParallelTier / 12.5);
    }

    @Override
    public double getDurationModifier() {
        return Math.max(0.000001, 1.0 / getMachineDurationModifier() - (Math.max(0, mParallelTier - 1) / 50.0));
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_FUSION_LOOP;
    }

    @Override
    public String[] getInfoData() {
        double plasmaOut = 0;
        if (mMaxProgresstime > 0) plasmaOut = (double) mOutputFluids[0].amount / mMaxProgresstime;

        return new String[] {
            StatCollector.translateToLocal("scanner.info.UX.0") + ": "
                + EnumChatFormatting.LIGHT_PURPLE
                + GTUtility.formatNumbers(getTrueParallel())
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

    @Override
    public IStructureDefinition<KuangBiaoOneGiantNuclearFusionReactor> getStructureDefinition() {
        return StructureDefinition.<KuangBiaoOneGiantNuclearFusionReactor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlockAnyMeta(ELECTRODE_CASING))
            .addElement('B', ofBlock(sBlockCasings8, 10))
            .addElement(
                'C',
                buildHatchAdder(KuangBiaoOneGiantNuclearFusionReactor.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(Maintenance, InputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy), ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(getCasing(), getCasingMeta()))))
            .addElement('D', ofBlock(getConcrete(), getConcreteMeta()))
            .addElement('E', ofFrame(Materials.Tungsten))
            .addElement('F', ofFrame(getFrame()))
            .addElement('G', ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('H', ofBlock(blockCasingsMisc, 5))
            .addElement('I', ofBlock(Loaders.compactFusionCoil, getCoilMeta()))
            .addElement('J', ofBlock(blockCasingsMisc, 15))
            .addElement('K', ofBlock(sBlockCasings10, 3))
            .build();
    }

    @Override
    public int getCasingTextureID() {
        return GTUtility.getCasingTextureIndex(getCasing(), getCasingMeta());
    }

    public abstract double getMachineEUtDiscount();

    public double getMachineDurationModifier() {
        return 2.0;
    }

    public abstract Block getCasing();

    public abstract int getCasingMeta();

    public Block getConcrete() {
        return sBlockCasings9;
    }

    public int getConcreteMeta() {
        return 3;
    }

    public abstract int getCoilMeta();

    public abstract Materials getFrame();

    public abstract int getRecipeMaxTier();

    public static class LuVTier extends KuangBiaoOneGiantNuclearFusionReactor {

        public LuVTier(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LuVTier(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new LuVTier(this.mName);
        }

        @Override
        public long maxEUStore() {
            return 160000000L;
        }

        @Override
        public double getMachineEUtDiscount() {
            return 8;
        }

        @Override
        public Block getCasing() {
            return sBlockCasings1;
        }

        @Override
        public int getCasingMeta() {
            return 6;
        }

        @Override
        public int getCoilMeta() {
            return 0;
        }

        @Override
        public Materials getFrame() {
            return Materials.NaquadahAlloy;
        }

        @Override
        public int getRecipeMaxTier() {
            return 6;
        }
    }

    public static class ZPMTier extends KuangBiaoOneGiantNuclearFusionReactor {

        public ZPMTier(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public ZPMTier(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new ZPMTier(this.mName);
        }

        @Override
        public long maxEUStore() {
            return 320000000L;
        }

        @Override
        public double getMachineEUtDiscount() {
            return 6;
        }

        @Override
        public Block getCasing() {
            return sBlockCasings4;
        }

        @Override
        public int getCasingMeta() {
            return 6;
        }

        @Override
        public int getCoilMeta() {
            return 1;
        }

        @Override
        public Materials getFrame() {
            return Materials.Duranium;
        }

        @Override
        public int getRecipeMaxTier() {
            return 7;
        }
    }

    public static class UVTier extends KuangBiaoOneGiantNuclearFusionReactor {

        public UVTier(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public UVTier(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new UVTier(this.mName);
        }

        @Override
        public long maxEUStore() {
            return 640000000L;
        }

        @Override
        public double getMachineEUtDiscount() {
            return 4;
        }

        @Override
        public Block getCasing() {
            return sBlockCasings4;
        }

        @Override
        public int getCasingMeta() {
            return 8;
        }

        @Override
        public int getCoilMeta() {
            return 2;
        }

        @Override
        public Materials getFrame() {
            return Materials.Neutronium;
        }

        @Override
        public int getRecipeMaxTier() {
            return 8;
        }
    }

    public static class UHVTier extends KuangBiaoOneGiantNuclearFusionReactor {

        public UHVTier(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public UHVTier(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new UHVTier(this.mName);
        }

        @Override
        public int getCasingTextureID() {
            return TAE.GTPP_INDEX(44);
        }

        @Override
        public long maxEUStore() {
            return 5120000000L;
        }

        @Override
        public double getMachineEUtDiscount() {
            return 2;
        }

        @Override
        public Block getCasing() {
            return blockCasings3Misc;
        }

        @Override
        public int getCasingMeta() {
            return 12;
        }

        @Override
        public Block getConcrete() {
            return sBlockCasingsDyson;
        }

        @Override
        public int getConcreteMeta() {
            return 9;
        }

        @Override
        public int getCoilMeta() {
            return 3;
        }

        @Override
        public Materials getFrame() {
            return Materials.InfinityCatalyst;
        }

        @Override
        public int getRecipeMaxTier() {
            return 9;
        }
    }

    public static class UEVTier extends KuangBiaoOneGiantNuclearFusionReactor implements IWirelessEnergy {

        public int totalOverclockedDuration = 0;
        public int maxParallelStored = -1;

        public static final String ZERO_STRING = "0";

        public UUID ownerUUID;
        public boolean isRecipeProcessing = false;
        @Getter
        public boolean wirelessMode = false;
        @Getter
        @Setter
        public boolean wirelessUpgrade = false;
        public BigInteger costingEU = BigInteger.ZERO;
        public String costingEUText = ZERO_STRING;
        public int cycleNum = 100_000;
        public int cycleNow = 0;

        public UEVTier(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public UEVTier(String aName) {
            super(aName);
        }

        @Override
        public void setWirelessMode(boolean mode) {
            if (wirelessUpgrade) {
                wirelessMode = mode;
            } else {
                wirelessMode = false;
            }
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new UEVTier(this.mName);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("KuangBiaoOneGiantNuclearFusionReactorRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_00"))
                .addInfo(
                    StatCollector.translateToLocalFormatted(
                        "Tooltip_KuangBiaoOneGiantNuclearFusionReactor_01",
                        (int) ((getMachineDurationModifier() - 1) * 100)))
                .addInfo(
                    StatCollector.translateToLocalFormatted(
                        "Tooltip_KuangBiaoOneGiantNuclearFusionReactor_02",
                        (int) (getMachineEUtDiscount() * 100)))
                .addInfo(StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_03"))
                .addInfo(
                    StatCollector.translateToLocal("Tooltip_KuangBiaoOneGiantNuclearFusionReactor_04") + maxEUStore()
                        + " EU")
                .addInfo(
                    StatCollector.translateToLocalFormatted(
                        "Tooltip_KuangBiaoOneGiantNuclearFusionReactor_05",
                        TIER_COLORS[getRecipeMaxTier()] + VN[getRecipeMaxTier()]))
                .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
                .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
                .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
                .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
                .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
                .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
                .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
                .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
                .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(39, 17, 39, true)
                .addInputBus(StatCollector.translateToLocal("Tooltip_KuangBiaoTwoGiantNuclearFusionReactor_Casing"))
                .addOutputBus(StatCollector.translateToLocal("Tooltip_KuangBiaoTwoGiantNuclearFusionReactor_Casing"))
                .addEnergyHatch(StatCollector.translateToLocal("Tooltip_KuangBiaoTwoGiantNuclearFusionReactor_Casing"))
                .addMaintenanceHatch(
                    StatCollector.translateToLocal("Tooltip_KuangBiaoTwoGiantNuclearFusionReactor_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        public void setItemNBT(NBTTagCompound aNBT) {
            super.setItemNBT(aNBT);
            aNBT.setBoolean("wirelessUpgrade", wirelessUpgrade);
        }

        @Override
        public void saveNBTData(NBTTagCompound aNBT) {
            super.saveNBTData(aNBT);
            aNBT.setBoolean("wirelessUpgrade", wirelessUpgrade);
            aNBT.setBoolean("wirelessMode", wirelessMode);
        }

        @Override
        public void loadNBTData(NBTTagCompound aNBT) {
            super.loadNBTData(aNBT);
            wirelessUpgrade = aNBT.getBoolean("wirelessUpgrade");
            wirelessMode = aNBT.getBoolean("wirelessMode");
        }

        @Override
        public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
            super.onFirstTick(aBaseMetaTileEntity);
            this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        }

        @Override
        public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
            super.getWailaBody(itemStack, currentTip, accessor, config);
            final NBTTagCompound tag = accessor.getNBTData();
            if (tag.getBoolean("wirelessUpgrade")) {
                currentTip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("Waila_WirelessUpgrade"));
            }
            if (tag.getBoolean("wirelessMode")) {
                currentTip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
                currentTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                        + EnumChatFormatting.RESET
                        + ": "
                        + EnumChatFormatting.GOLD
                        + tag.getString("costingEUText")
                        + EnumChatFormatting.RESET
                        + " EU");
            }
        }

        @Override
        public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x,
            int y, int z) {
            super.getWailaNBTData(player, tile, tag, world, x, y, z);
            final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
            if (tileEntity != null) {
                tag.setBoolean("wirelessUpgrade", wirelessUpgrade);
                tag.setBoolean("wirelessMode", wirelessMode);
                if (wirelessMode) tag.setString("costingEUText", costingEUText);
            }
        }

        @Override
        public String[] getInfoData() {
            List<String> ret = new ArrayList<>(Arrays.asList(super.getInfoData()));
            if (wirelessMode) {
                ret.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
                ret.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                        + EnumChatFormatting.RESET
                        + ": "
                        + EnumChatFormatting.GOLD
                        + costingEUText
                        + EnumChatFormatting.RESET
                        + " EU");
            }
            return ret.toArray(new String[0]);
        }

        @Override
        public void startRecipeProcessing() {
            isRecipeProcessing = true;
            super.startRecipeProcessing();
        }

        @Override
        public void endRecipeProcessing() {
            super.endRecipeProcessing();
            isRecipeProcessing = false;
        }

        @Override
        public ProcessingLogic createProcessingLogic() {
            return new GTNL_ProcessingLogic() {

                @NotNull
                @Override
                public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                    GTNL_OverclockCalculator logic = super.createOverclockCalculator(recipe)
                        .setExtraDurationModifier(mConfigSpeedBoost)
                        .setAmperageOC(true)
                        .setDurationDecreasePerOC(4)
                        .setEUtIncreasePerOC(4)
                        .setAmperage(availableAmperage)
                        .setRecipeEUt(recipe.mEUt)
                        .setEUt(availableVoltage)
                        .setEUtDiscount(0.4 - (mParallelTier / 50.0))
                        .setDurationModifier(1.0 / 10.0 * Math.pow(0.75, mParallelTier));

                    if (wirelessMode) {
                        logic.setEUt(V[Math.min(mParallelTier + 1, 14)]);
                        logic.setAmperage((8L << (2 * mParallelTier)) - 2L);
                        logic.setAmperageOC(false);
                    }
                    return logic;
                }

                @NotNull
                @Override
                public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                    long powerToStart = recipe.getMetadataOrDefault(GTRecipeConstants.FUSION_THRESHOLD, 0L);
                    if (!mRunningOnLoad) {
                        if (!wirelessMode && powerToStart > mEUStore) {
                            return CheckRecipeResultRegistry.insufficientStartupPower(BigInteger.valueOf(powerToStart));
                        }
                        if (recipe.mEUt > GTValues.V[getRecipeMaxTier() + 1]) {
                            return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                        }
                    }
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }

                @NotNull
                @Override
                public CheckRecipeResult process() {
                    CheckRecipeResult result = super.process();
                    if (mRunningOnLoad) mRunningOnLoad = false;
                    if (result.wasSuccessful()) {
                        KuangBiaoOneGiantNuclearFusionReactor.UEVTier.this.mLastRecipe = lastRecipe;
                    } else {
                        KuangBiaoOneGiantNuclearFusionReactor.UEVTier.this.mLastRecipe = null;
                    }
                    return result;
                }

            }.setMaxParallelSupplier(this::getTrueParallel);
        }

        @Override
        public int getMaxParallelRecipes() {
            if (maxParallelStored >= 0) {
                return maxParallelStored;
            }

            if (mParallelControllerHatches.size() == 1) {
                ParallelControllerHatch module = mParallelControllerHatches.get(0);
                mParallelTier = module.mTier;
                return 16 << (2 * (module.mTier - 2));
            } else if (mParallelTier <= 1) {
                return 8;
            } else {
                return 16 << (2 * (mParallelTier - 2));
            }
        }

        @Override
        public int getCasingTextureID() {
            return TAE.GTPP_INDEX(52);
        }

        @Override
        public long maxEUStore() {
            return 20480000000L;
        }

        @Override
        public double getMachineEUtDiscount() {
            return 0.4;
        }

        @Override
        public double getMachineDurationModifier() {
            return 10.0;
        }

        @Override
        public Block getCasing() {
            return blockCasings6Misc;
        }

        @Override
        public int getCasingMeta() {
            return 0;
        }

        @Override
        public Block getConcrete() {
            return sBlockCasingsDyson;
        }

        @Override
        public int getConcreteMeta() {
            return 9;
        }

        @Override
        public int getCoilMeta() {
            return 4;
        }

        @Override
        public Materials getFrame() {
            return Materials.Infinity;
        }

        @Override
        public int getRecipeMaxTier() {
            return 13;
        }

        @Override
        public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
            wirelessMode = false;
            boolean result = super.checkMachine(aBaseMetaTileEntity, aStack);
            setWirelessMode(mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty());
            return result;
        }

        @Nonnull
        @Override
        public CheckRecipeResult checkProcessing() {
            maxParallelStored = -1;
            mParallelTier = 0;
            ItemStack controllerItem = getControllerSlot();
            int parallelTierItem = getParallelTier(controllerItem);
            mParallelTier = Math.max(mParallelTier, parallelTierItem);
            costingEU = BigInteger.ZERO;
            costingEUText = ZERO_STRING;
            totalOverclockedDuration = 0;
            cycleNow = 0;
            maxParallelStored = getTrueParallel();
            if (!wirelessMode) return super.checkProcessing();

            boolean succeeded = false;
            CheckRecipeResult finalResult = CheckRecipeResultRegistry.SUCCESSFUL;
            for (cycleNow = 0; cycleNow < cycleNum; cycleNow++) {
                CheckRecipeResult r = wirelessModeProcessOnce();

                if (!r.wasSuccessful()) {
                    finalResult = r;
                    break;
                }
                succeeded = true;
                if (maxParallelStored <= 0) {
                    finalResult = r;
                    break;
                }
            }

            if (!succeeded) return finalResult;
            updateSlots();
            if (totalOverclockedDuration > 0) {
                totalOverclockedDuration = (int) Math
                    .max(1, totalOverclockedDuration * Math.pow(0.75, mParallelTier - 4) / (cycleNow + 1));
            } else {
                totalOverclockedDuration = 1;
            }
            costingEUText = GTUtility.formatNumbers(costingEU);

            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = totalOverclockedDuration;

            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        public CheckRecipeResult wirelessModeProcessOnce() {
            if (!isRecipeProcessing) startRecipeProcessing();
            setupProcessingLogic(processingLogic);

            CheckRecipeResult result = doCheckRecipe();
            if (!result.wasSuccessful()) {
                return result;
            }

            BigInteger costEU = BigInteger.valueOf(processingLogic.getCalculatedEut())
                .multiply(BigInteger.valueOf(processingLogic.getDuration()));

            if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(NEGATIVE_ONE))) {
                return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
            }

            costingEU = costingEU.add(costEU);

            mOutputItems = mergeArray(mOutputItems, processingLogic.getOutputItems());
            mOutputFluids = mergeArray(mOutputFluids, processingLogic.getOutputFluids());
            totalOverclockedDuration += processingLogic.getDuration();
            maxParallelStored = maxParallelStored - processingLogic.getCurrentParallels();

            endRecipeProcessing();
            return result;
        }
    }
}
