package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.ofHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.Utils.item.TextUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

import bartworks.API.BorosilicateGlass;
import bartworks.API.SideReference;
import bartworks.API.recipe.BartWorksRecipeMaps;
import bartworks.common.items.ItemLabParts;
import bartworks.common.loaders.FluidLoader;
import bartworks.common.net.PacketBioVatRenderer;
import bartworks.common.tileentities.tiered.GT_MetaTileEntity_RadioHatch;
import bartworks.util.*;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.*;

public class Incubator extends MultiMachineBase<Incubator> implements ISurvivalConstructable {

    public static final HashMap<Coords, Integer> staticColorMap = new HashMap<>();

    private static final byte TIMERDIVIDER = 20;

    private final HashSet<EntityPlayerMP> playerMPHashSet = new HashSet<>();
    private final ArrayList<GT_MetaTileEntity_RadioHatch> mRadHatches = new ArrayList<>();
    private int height = 1;
    private Fluid mFluid = FluidRegistry.LAVA;
    private BioCulture mCulture;
    private ItemStack mStack;
    private boolean needsVisualUpdate = true;
    private static final int CASING_INDEX = 210;
    private byte glassTier;
    private int mSievert;
    private int mNeededSievert;
    private int mCasing = 0;
    private int mExpectedMultiplier = 0;
    private int mTimes = 0;
    private boolean isVisibleFluid = false;
    public static IStructureDefinition<Incubator> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static String[][] shape;
    public static final String INCUBATOR_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/incubator";
    public final int horizontalOffSet = 2;
    public final int verticalOffSet = 4;
    public final int depthOffSet = 0;

    public Incubator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        shape = StructureUtils.readStructureFromFile(INCUBATOR_STRUCTURE_FILE_PATH);
    }

    public Incubator(String aName) {
        super(aName);
        shape = StructureUtils.readStructureFromFile(INCUBATOR_STRUCTURE_FILE_PATH);
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && f.isNotFlipped();
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    public float getSpeedBonus() {
        return 1;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.IncubatorRecipeType)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_00)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_01)
            .addInfo(TextLocalization.Tooltip_Incubator_00)
            .addInfo(TextLocalization.Tooltip_Incubator_01)
            .addInfo(TextLocalization.Tooltip_Incubator_02)
            .beginStructureBlock(5, 5, 5, false)
            .addMaintenanceHatch(TextLocalization.Tooltip_Incubator_Casing, 1)
            .addOtherStructurePart(
                TextLocalization.Tooltip_Incubator_Radio_Hatch,
                TextLocalization.Tooltip_Incubator_Casing,
                1)
            .addInputBus(TextLocalization.Tooltip_Incubator_Casing, 1)
            .addOutputBus(TextLocalization.Tooltip_Incubator_Casing, 1)
            .addInputHatch(TextLocalization.Tooltip_Incubator_Casing, 1)
            .addOutputHatch(TextLocalization.Tooltip_Incubator_Casing, 1)
            .addEnergyHatch(TextLocalization.Tooltip_Incubator_Casing, 1)
            .toolTipFinisher(TextUtils.SNL + TextUtils.SQY + " §rX " + TextUtils.SRP);
        return tt;
    }

    @Override
    public IStructureDefinition<Incubator> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<Incubator>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    withChannel(
                        "glass",
                        BorosilicateGlass.ofBoroGlass(
                            (byte) 0,
                            (byte) 1,
                            Byte.MAX_VALUE,
                            (te, t) -> te.glassTier = t,
                            te -> te.glassTier)))
                .addElement('B', ofBlock(sBlockCasings3, 11))
                .addElement(
                    'C',
                    ofChain(
                        ofHatchAdder(Incubator::addMaintenanceToMachineList, CASING_INDEX, 1),
                        ofHatchAdder(Incubator::addOutputToMachineList, CASING_INDEX, 1),
                        ofHatchAdder(Incubator::addInputToMachineList, CASING_INDEX, 1),
                        ofHatchAdder(Incubator::addRadiationInputToMachineList, CASING_INDEX, 1),
                        ofHatchAdder(Incubator::addEnergyInputToMachineList, CASING_INDEX, 1),
                        onElementPass(e -> e.mCasing++, ofBlock(sBlockReinforced, 2))))
                .addElement('D', ofBlockAnyMeta(Blocks.sponge))
                .addElement('E', ofChain(isAir(), ofBlockAnyMeta(FluidLoader.bioFluidBlock)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public static int[] specialValueUnpack(int aSpecialValue) {
        int[] ret = new int[4];
        ret[0] = aSpecialValue & 0xF; // = glass tier
        ret[1] = aSpecialValue >>> 4 & 0b11; // = special value
        ret[2] = aSpecialValue >>> 6 & 0b1; // boolean exact svt | 1 = true | 0 = false
        ret[3] = aSpecialValue >>> 7 & Integer.MAX_VALUE; // = sievert
        return ret;
    }

    private int getInputCapacity() {
        return this.mInputHatches.stream()
            .mapToInt(MTEHatchInput::getCapacity)
            .sum();
    }

    @Override
    public int getCapacity() {
        int ret = 0;
        ret += this.getInputCapacity();
        return ret;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return super.fill(resource, doFill);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return BartWorksRecipeMaps.bacterialVatRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (!BWUtil.areStacksEqualOrNull((ItemStack) recipe.mSpecialItems, Incubator.this.getControllerSlot()))
                    return CheckRecipeResultRegistry.NO_RECIPE;
                int[] conditions = Incubator.specialValueUnpack(recipe.mSpecialValue);
                Incubator.this.mNeededSievert = conditions[3];

                if (Incubator.this.glassTier < conditions[0]) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(conditions[0]);
                }

                if (conditions[2] == 0) {
                    if (Incubator.this.mSievert < Incubator.this.mNeededSievert) {
                        return ResultWrongSievert.insufficientSievert(Incubator.this.mNeededSievert);
                    }
                } else if (Incubator.this.mSievert != conditions[3]) {
                    return ResultWrongSievert.wrongSievert(conditions[3]);
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setEUtDiscount(0.8)
                    .setSpeedBoost(0.6);
            }

            @NotNull
            @Override
            protected ParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(recipeWithMultiplier(recipe, inputFluids));
            }
        };
    }

    @Override
    public int getMaxParallelRecipes() {
        return 4;
    }

    protected GTRecipe recipeWithMultiplier(GTRecipe recipe, FluidStack[] fluidInputs) {
        GTRecipe tRecipe = recipe.copy();
        int multiplier = 1001;
        mExpectedMultiplier = multiplier;
        // Calculate max multiplier limited by input fluids
        long fluidAmount = 0;
        for (FluidStack fluid : fluidInputs) {
            if (recipe.mFluidInputs[0].isFluidEqual(fluid)) {
                fluidAmount += fluid.amount;
            }
        }
        multiplier = (int) Math.min(multiplier, fluidAmount / recipe.mFluidInputs[0].amount);
        // In case multiplier is 0
        multiplier = Math.max(multiplier, 1);
        mTimes = multiplier;
        tRecipe.mFluidInputs[0].amount *= multiplier;
        tRecipe.mFluidOutputs[0].amount *= multiplier;
        return tRecipe;
    }

    @Override
    protected void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setSpecialSlotItem(this.getControllerSlot());
    }

    private boolean addRadiationInputToMachineList(IGregTechTileEntity aTileEntity, int CasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (!(aMetaTileEntity instanceof GT_MetaTileEntity_RadioHatch)) {
            return false;
        } else {
            ((GT_MetaTileEntity_RadioHatch) aMetaTileEntity).updateTexture(CasingIndex);
            return this.mRadHatches.add((GT_MetaTileEntity_RadioHatch) aMetaTileEntity);
        }
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack itemStack) {
        this.mRadHatches.clear();
        this.glassTier = 0;
        this.mCasing = 0;

        if (!this.checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet)) return false;

        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (glassTier < VoltageIndex.UHV & mEnergyHatch.mTier > glassTier) {
                return false;
            }
        }

        return this.mCasing >= 19 && this.mRadHatches.size() <= 1
            && this.mOutputHatches.size() == 1
            && this.mMaintenanceHatches.size() == 1
            && !this.mInputHatches.isEmpty()
            && !this.mEnergyHatches.isEmpty();
    }

    private int reCalculateFluidAmmount() {
        return this.getStoredFluids()
            .stream()
            .mapToInt(fluidStack -> fluidStack.amount)
            .sum();
    }

    private int reCalculateHeight() {
        return this.reCalculateFluidAmmount() > this.getCapacity() / 4 - 1
            ? this.reCalculateFluidAmmount() >= this.getCapacity() / 2 ? 3 : 2
            : 1;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (this.height != this.reCalculateHeight()) this.needsVisualUpdate = true;
        this.doAllVisualThings();
        if (this.getBaseMetaTileEntity()
            .isServerSide() && this.mRadHatches.size() == 1) {
            this.mSievert = this.mRadHatches.get(0)
                .getSievert();
            if (this.getBaseMetaTileEntity()
                .isActive() && this.mNeededSievert > this.mSievert) this.mOutputFluids = null;
        }
        if (aBaseMetaTileEntity.isServerSide() && this.mMaxProgresstime <= 0) {
            this.mTimes = 0;
            this.mMaxProgresstime = 0;
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("mFluidHeight", this.height);
        if (this.mCulture != null && !this.mCulture.getName()
            .isEmpty()) aNBT.setString("mCulture", this.mCulture.getName());
        else if ((this.mCulture == null || this.mCulture.getName()
            .isEmpty()) && !aNBT.getString("mCulture")
                .isEmpty()) {
                    aNBT.removeTag("mCulture");
                }
        if (this.mFluid != null) aNBT.setString("mFluid", this.mFluid.getName());
        aNBT.setInteger("mSievert", this.mSievert);
        aNBT.setInteger("mNeededSievert", this.mNeededSievert);
        aNBT.setBoolean("isVisibleFluid", this.isVisibleFluid);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        this.height = aNBT.getInteger("mFluidHeight");
        this.mCulture = BioCulture.getBioCulture(aNBT.getString("mCulture"));
        if (!aNBT.getString("mFluid")
            .isEmpty()) this.mFluid = FluidRegistry.getFluid(aNBT.getString("mFluid"));
        this.mSievert = aNBT.getInteger("mSievert");
        this.mNeededSievert = aNBT.getInteger("mNeededSievert");
        super.loadNBTData(aNBT);
        this.isVisibleFluid = aNBT.getBoolean("isVisibleFluid");
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new Incubator(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX) };
    }

    @Override
    public void construct(ItemStack itemStack, boolean b) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, itemStack, b, horizontalOffSet, verticalOffSet, depthOffSet);
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

    @Override
    public String[] getInfoData() {
        final String[] baseInfoData = super.getInfoData();
        final String[] infoData = new String[baseInfoData.length + 2];
        System.arraycopy(baseInfoData, 0, infoData, 0, baseInfoData.length);
        // See https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/11923
        // here we must check the machine is well-formed as otherwise getExpectedMultiplier might error out!
        infoData[infoData.length - 2] = StatCollector.translateToLocal("BW.infoData.BioVat.expectedProduction") + ": "
            + EnumChatFormatting.GREEN
            + (this.mMachine ? (this.mMaxProgresstime <= 0 ? 1001 : this.mExpectedMultiplier) * 100 : -1)
            + EnumChatFormatting.RESET
            + " %";
        infoData[infoData.length - 1] = StatCollector.translateToLocal("BW.infoData.BioVat.production") + ": "
            + EnumChatFormatting.GREEN
            + (this.mMaxProgresstime <= 0 ? 0 : this.mTimes) * 100
            + EnumChatFormatting.RESET
            + " %";
        return infoData;
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ) {
        if (aPlayer.isSneaking()) {
            batchMode = !batchMode;
            if (batchMode) {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("misc.BatchModeTextOn"));
            } else {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("misc.BatchModeTextOff"));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return true;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return true;
    }

    @Override
    public void checkMaintenance() {
        if (!shouldCheckMaintenance()) return;

        if (getRepairStatus() != getIdealStatus()) {
            for (MTEHatchMaintenance tHatch : validMTEList(mMaintenanceHatches)) {
                if (tHatch.mAuto) tHatch.autoMaintainance();
                if (tHatch.mWrench) mWrench = true;
                if (tHatch.mScrewdriver) mScrewdriver = true;
                if (tHatch.mSoftHammer) mSoftHammer = true;
                if (tHatch.mHardHammer) mHardHammer = true;
                if (tHatch.mSolderingTool) mSolderingTool = true;
                if (tHatch.mCrowbar) mCrowbar = true;

                tHatch.mWrench = false;
                tHatch.mScrewdriver = false;
                tHatch.mSoftHammer = false;
                tHatch.mHardHammer = false;
                tHatch.mSolderingTool = false;
                tHatch.mCrowbar = false;
            }
        }
    }

    private void sendAllRequiredRendererPackets() {
        int height = this.reCalculateHeight();
        if (this.mFluid != null && height > 1 && this.reCalculateFluidAmmount() > 0) {
            for (int x = -1; x < 2; x++) for (int y = 2; y < height + 1; y++) // Y轴提高1格
                for (int z = -1; z < 2; z++) this.sendPackagesOrRenewRenderer(x, y, z, this.mCulture);
        }
    }

    private void sendPackagesOrRenewRenderer(int x, int y, int z, BioCulture lCulture) {
        int xDir = this.getXDir();
        int zDir = this.getZDir();

        Incubator.staticColorMap.remove(
            new Coords(
                xDir + x
                    + this.getBaseMetaTileEntity()
                        .getXCoord(),
                y + 1
                    + this.getBaseMetaTileEntity()
                        .getYCoord(), // Y轴提高1格
                zDir + z
                    + this.getBaseMetaTileEntity()
                        .getZCoord(),
                this.getBaseMetaTileEntity()
                    .getWorld().provider.dimensionId));

        Incubator.staticColorMap.put(
            new Coords(
                xDir + x
                    + this.getBaseMetaTileEntity()
                        .getXCoord(),
                y + 1
                    + this.getBaseMetaTileEntity()
                        .getYCoord(), // Y轴提高1格
                zDir + z
                    + this.getBaseMetaTileEntity()
                        .getZCoord(),
                this.getBaseMetaTileEntity()
                    .getWorld().provider.dimensionId),
            lCulture == null ? BioCulture.NULLCULTURE.getColorRGB() : lCulture.getColorRGB());

        if (SideReference.Side.Server) {
            GTValues.NW.sendPacketToAllPlayersInRange(
                this.getBaseMetaTileEntity()
                    .getWorld(),
                new PacketBioVatRenderer(
                    new Coords(
                        xDir + x
                            + this.getBaseMetaTileEntity()
                                .getXCoord(),
                        y + 1
                            + this.getBaseMetaTileEntity()
                                .getYCoord(), // Y轴提高1格
                        zDir + z
                            + this.getBaseMetaTileEntity()
                                .getZCoord(),
                        this.getBaseMetaTileEntity()
                            .getWorld().provider.dimensionId),
                    lCulture == null ? BioCulture.NULLCULTURE.getColorRGB() : lCulture.getColorRGB(),
                    true),
                this.getBaseMetaTileEntity()
                    .getXCoord(),
                this.getBaseMetaTileEntity()
                    .getZCoord());

            GTValues.NW.sendPacketToAllPlayersInRange(
                this.getBaseMetaTileEntity()
                    .getWorld(),
                new PacketBioVatRenderer(
                    new Coords(
                        xDir + x
                            + this.getBaseMetaTileEntity()
                                .getXCoord(),
                        y + 1
                            + this.getBaseMetaTileEntity()
                                .getYCoord(), // Y轴提高1格
                        zDir + z
                            + this.getBaseMetaTileEntity()
                                .getZCoord(),
                        this.getBaseMetaTileEntity()
                            .getWorld().provider.dimensionId),
                    lCulture == null ? BioCulture.NULLCULTURE.getColorRGB() : lCulture.getColorRGB(),
                    false),
                this.getBaseMetaTileEntity()
                    .getXCoord(),
                this.getBaseMetaTileEntity()
                    .getZCoord());
        }
        this.needsVisualUpdate = true;
    }

    private void check_Chunk() {
        World aWorld = this.getBaseMetaTileEntity()
            .getWorld();
        if (!aWorld.isRemote) {
            for (Object tObject : aWorld.playerEntities) {
                if (!(tObject instanceof EntityPlayerMP tPlayer)) {
                    break;
                }
                Chunk tChunk = aWorld.getChunkFromBlockCoords(
                    this.getBaseMetaTileEntity()
                        .getXCoord(),
                    this.getBaseMetaTileEntity()
                        .getZCoord());
                if (tPlayer.getServerForPlayer()
                    .getPlayerManager()
                    .isPlayerWatchingChunk(tPlayer, tChunk.xPosition, tChunk.zPosition)) {
                    if (!this.playerMPHashSet.contains(tPlayer)) {
                        this.playerMPHashSet.add(tPlayer);
                        this.sendAllRequiredRendererPackets();
                    }
                } else {
                    this.playerMPHashSet.remove(tPlayer);
                }
            }
        }
    }

    private void placeFluid() {
        this.isVisibleFluid = true;
        int xDir = this.getXDir();
        int zDir = this.getZDir();
        this.height = this.reCalculateHeight();
        if (this.mFluid != null && this.height > 1 && this.reCalculateFluidAmmount() > 0) {
            for (int x = -1; x < 2; x++) {
                for (int y = 1; y < this.height + 1; y++) { // Y轴提高1格
                    for (int z = -1; z < 2; z++) {
                        if (this.getBaseMetaTileEntity()
                            .getWorld()
                            .getBlock(
                                xDir + x
                                    + this.getBaseMetaTileEntity()
                                        .getXCoord(),
                                y + this.getBaseMetaTileEntity()
                                    .getYCoord(),
                                zDir + z
                                    + this.getBaseMetaTileEntity()
                                        .getZCoord())
                            .equals(Blocks.air)) {
                            this.getBaseMetaTileEntity()
                                .getWorld()
                                .setBlock(
                                    xDir + x
                                        + this.getBaseMetaTileEntity()
                                            .getXCoord(),
                                    y + this.getBaseMetaTileEntity()
                                        .getYCoord(),
                                    zDir + z
                                        + this.getBaseMetaTileEntity()
                                            .getZCoord(),
                                    FluidLoader.bioFluidBlock);
                        }
                    }
                }
            }
        }
    }

    private void removeFluid(int xDir, int zDir) {
        this.isVisibleFluid = false;

        for (int x = -1; x < 2; x++) {
            for (int y = 2; y < 4; y++) { // Y轴提高1格
                for (int z = -1; z < 2; z++) {
                    if (this.getBaseMetaTileEntity()
                        .getWorld()
                        .getBlock(
                            xDir + x
                                + this.getBaseMetaTileEntity()
                                    .getXCoord(),
                            y + this.getBaseMetaTileEntity()
                                .getYCoord(),
                            zDir + z
                                + this.getBaseMetaTileEntity()
                                    .getZCoord())
                        .equals(FluidLoader.bioFluidBlock)) {
                        this.getBaseMetaTileEntity()
                            .getWorld()
                            .setBlockToAir(
                                xDir + x
                                    + this.getBaseMetaTileEntity()
                                        .getXCoord(),
                                y + this.getBaseMetaTileEntity()
                                    .getYCoord(),
                                zDir + z
                                    + this.getBaseMetaTileEntity()
                                        .getZCoord());
                    }
                    Incubator.staticColorMap.remove(
                        new Coords(
                            xDir + x
                                + this.getBaseMetaTileEntity()
                                    .getXCoord(),
                            y + this.getBaseMetaTileEntity()
                                .getYCoord(),
                            zDir + z
                                + this.getBaseMetaTileEntity()
                                    .getZCoord()),
                        this.getBaseMetaTileEntity()
                            .getWorld().provider.dimensionId);
                }
            }
        }
    }

    @Override
    public void onRemoval() {
        if (this.isVisibleFluid) {
            int xDir = this.getXDir();
            int zDir = this.getZDir();
            this.removeFluid(xDir, zDir);
            this.sendRenderPackets(xDir, zDir);
        } else if (this.getBaseMetaTileEntity()
            .getWorld()
            .getWorldTime() % 20 == 7) {
                this.sendRenderPackets();
            }

        super.onRemoval();
    }

    private void sendRenderPackets() {
        int xDir = this.getXDir();
        int zDir = this.getZDir();
        this.sendRenderPackets(xDir, zDir);
    }

    private void sendRenderPackets(int xDir, int zDir) {
        if (SideReference.Side.Server) {
            for (int x = -1; x < 2; x++) {
                for (int y = 2; y < 4; y++) { // Y轴提高1格
                    for (int z = -1; z < 2; z++) {
                        GTValues.NW.sendPacketToAllPlayersInRange(
                            this.getBaseMetaTileEntity()
                                .getWorld(),
                            new PacketBioVatRenderer(
                                new Coords(
                                    xDir + x
                                        + this.getBaseMetaTileEntity()
                                            .getXCoord(),
                                    y + this.getBaseMetaTileEntity()
                                        .getYCoord(),
                                    zDir + z
                                        + this.getBaseMetaTileEntity()
                                            .getZCoord(),
                                    this.getBaseMetaTileEntity()
                                        .getWorld().provider.dimensionId),
                                this.mCulture == null ? BioCulture.NULLCULTURE.getColorRGB()
                                    : this.mCulture.getColorRGB(),
                                true),
                            this.getBaseMetaTileEntity()
                                .getXCoord(),
                            this.getBaseMetaTileEntity()
                                .getZCoord());
                    }
                }
            }
        }
    }

    private int getXDir() {
        return this.getBaseMetaTileEntity()
            .getBackFacing().offsetX * 2;
    }

    private int getZDir() {
        return this.getBaseMetaTileEntity()
            .getBackFacing().offsetZ * 2;
    }

    public void doAllVisualThings() {
        if (this.getBaseMetaTileEntity()
            .isServerSide()) {
            if (this.mMachine) {
                ItemStack aStack = this.mInventory[1];
                BioCulture lCulture = null;
                int xDir = this.getXDir();
                int zDir = this.getZDir();

                if (this.getBaseMetaTileEntity()
                    .getTimer() % 200 == 0) {
                    this.check_Chunk();
                }

                if (this.needsVisualUpdate && this.getBaseMetaTileEntity()
                    .getTimer() % Incubator.TIMERDIVIDER == 0) {
                    for (int x = -1; x < 2; x++) {
                        for (int y = 2; y < 4; y++) { // Y轴提高1格
                            for (int z = -1; z < 2; z++) {
                                this.getBaseMetaTileEntity()
                                    .getWorld()
                                    .setBlockToAir(
                                        xDir + x
                                            + this.getBaseMetaTileEntity()
                                                .getXCoord(),
                                        y + this.getBaseMetaTileEntity()
                                            .getYCoord(),
                                        zDir + z
                                            + this.getBaseMetaTileEntity()
                                                .getZCoord());
                            }
                        }
                    }
                }

                this.height = this.reCalculateHeight();
                if (this.mFluid != null && this.height > 1 && this.reCalculateFluidAmmount() > 0) {
                    if (!BWUtil.areStacksEqualOrNull(aStack, this.mStack)
                        || this.needsVisualUpdate && this.getBaseMetaTileEntity()
                            .getTimer() % Incubator.TIMERDIVIDER == 1) {
                        for (int x = -1; x < 2; x++) {
                            for (int y = 2; y < this.height + 1; y++) { // Y轴提高1格
                                for (int z = -1; z < 2; z++) {
                                    if (aStack == null
                                        || aStack.getItem() instanceof ItemLabParts && aStack.getItemDamage() == 0) {
                                        if (this.mCulture == null || aStack == null
                                            || aStack.getTagCompound() == null
                                            || this.mCulture.getID() != aStack.getTagCompound()
                                                .getInteger("ID")) {
                                            lCulture = aStack == null || aStack.getTagCompound() == null ? null
                                                : BioCulture.getBioCulture(
                                                    aStack.getTagCompound()
                                                        .getString("Name"));
                                            this.sendPackagesOrRenewRenderer(x, y, z, lCulture);
                                        }
                                    }
                                }
                            }
                        }
                        this.mStack = aStack;
                        this.mCulture = lCulture;
                    }
                    if (this.needsVisualUpdate && this.getBaseMetaTileEntity()
                        .getTimer() % Incubator.TIMERDIVIDER == 1) {
                        if (this.getBaseMetaTileEntity()
                            .isClientSide()) {
                            new Throwable().printStackTrace();
                        }
                        this.placeFluid();
                        this.needsVisualUpdate = false;
                    }
                }
            } else {
                this.onRemoval();
            }
        }
    }

}
