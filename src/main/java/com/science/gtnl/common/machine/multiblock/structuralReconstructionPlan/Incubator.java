package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTRecipeConstants.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ParallelHelper;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import bartworks.API.SideReference;
import bartworks.API.recipe.BartWorksRecipeMaps;
import bartworks.common.items.ItemLabParts;
import bartworks.common.loaders.FluidLoader;
import bartworks.common.net.PacketBioVatRenderer;
import bartworks.common.tileentities.tiered.MTERadioHatch;
import bartworks.util.BWUtil;
import bartworks.util.BioCulture;
import bartworks.util.Coords;
import bartworks.util.ResultWrongSievert;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.recipe.Sievert;
import gregtech.common.misc.GTStructureChannels;

public class Incubator extends MultiMachineBase<Incubator> implements ISurvivalConstructable {

    public static final HashMap<Coords, Integer> staticColorMap = new HashMap<>();

    public static final byte TIMERDIVIDER = 20;

    public Sievert defaultSievertData = new Sievert(0, false);
    public HashSet<EntityPlayerMP> playerMPHashSet = new HashSet<>();
    public ArrayList<MTERadioHatch> mRadHatches = new ArrayList<>();
    public int height = 1;
    public Fluid mFluid = FluidRegistry.LAVA;
    public BioCulture mCulture;
    public ItemStack mStack;
    public boolean needsVisualUpdate = true;
    public int mSievert;
    public int mNeededSievert;
    public boolean isVisibleFluid = false;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String INCUBATOR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/incubator";
    private static final String[][] shape = StructureUtils.readStructureFromFile(INCUBATOR_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;

    public Incubator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public Incubator(String aName) {
        super(aName);
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && f.isNotFlipped();
    }

    @Override
    public int getCasingTextureID() {
        return 210;
    }

    @Override
    public boolean getPerfectOC() {
        return false;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("IncubatorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Incubator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Incubator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Incubator_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .beginStructureBlock(5, 5, 5, false)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_Incubator_Casing"), 1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("Tooltip_Incubator_Radio_Hatch"),
                StatCollector.translateToLocal("Tooltip_Incubator_Casing"),
                1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_Incubator_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_Incubator_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_Incubator_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_Incubator_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_Incubator_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<Incubator> getStructureDefinition() {
        return StructureDefinition.<Incubator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', ofBlock(sBlockCasings3, 11))
            .addElement(
                'C',
                ofChain(
                    buildHatchAdder(Incubator.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(
                            InputHatch,
                            OutputHatch,
                            InputBus,
                            OutputBus,
                            Maintenance,
                            Energy.or(ExoticEnergy),
                            RadioHatchElement.RadioHatch)
                        .buildAndChain(),
                    onElementPass(e -> e.mCountCasing++, ofBlock(sBlockReinforced, 2))))
            .addElement(
                'D',
                ofBlock(
                    EtFuturumRequiem.isModLoaded() ? GameRegistry.findBlock(EtFuturumRequiem.ID, "sponge")
                        : Blocks.sponge,
                    1))
            .addElement('E', ofChain(isAir(), ofBlockAnyMeta(FluidLoader.bioFluidBlock)))
            .build();
    }

    public int getInputCapacity() {
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
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                Sievert data = recipe.getMetadataOrDefault(GTRecipeConstants.SIEVERT, defaultSievertData);
                int sievert = data.sievert;
                boolean isExact = data.isExact;
                int glass = recipe.getMetadataOrDefault(GLASS, 0);
                if (!BWUtil.areStacksEqualOrNull((ItemStack) recipe.mSpecialItems, getControllerSlot()))
                    return CheckRecipeResultRegistry.NO_RECIPE;
                mNeededSievert = sievert;

                if (mGlassTier < glass) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(glass);
                }

                if (!isExact) {
                    if (mSievert < mNeededSievert) {
                        return ResultWrongSievert.insufficientSievert(mNeededSievert);
                    }
                } else if (mSievert != sievert) {
                    return ResultWrongSievert.wrongSievert(sievert);
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @NotNull
            @Override
            public GTNL_ParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(recipeWithMultiplier(recipe, inputFluids));
            }
        };
    }

    @Override
    public double getEUtDiscount() {
        return 0.8;
    }

    @Override
    public double getDurationModifier() {
        return 1 / 1.67;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 4;
    }

    public GTRecipe recipeWithMultiplier(GTRecipe recipe, FluidStack[] fluidInputs) {
        if (recipe == null || fluidInputs == null) {
            return recipe;
        }

        if (recipe.mFluidInputs == null || recipe.mFluidInputs.length == 0
            || recipe.mFluidOutputs == null
            || recipe.mFluidOutputs.length == 0) {
            return recipe;
        }

        if (recipe.mFluidInputs[0] == null || recipe.mFluidOutputs[0] == null) {
            return recipe;
        }

        for (FluidStack fluid : fluidInputs) {
            if (fluid == null) {
                return recipe;
            }
        }

        GTRecipe tRecipe = recipe.copy();
        int multiplier;

        long fluidAmount = 0;
        for (FluidStack fluid : fluidInputs) {
            if (recipe.mFluidInputs[0].isFluidEqual(fluid)) {
                fluidAmount += fluid.amount;
            }
        }

        multiplier = (int) fluidAmount / (recipe.mFluidInputs[0].amount * 1001);
        multiplier = Math.max(Math.min(multiplier, getTrueParallel()), 1);
        multiplier *= getExpectedMultiplier(recipe.getFluidOutput(0));

        tRecipe.mFluidInputs[0].amount *= multiplier;
        tRecipe.mFluidOutputs[0].amount *= multiplier;

        return tRecipe;
    }

    public int getExpectedMultiplier(@Nullable FluidStack recipeFluidOutput) {
        FluidStack storedFluidOutputs = mOutputHatches.get(0)
            .getFluid();
        if (storedFluidOutputs == null) return 1001;
        if (storedFluidOutputs.isFluidEqual(recipeFluidOutput)) {
            return 1001;
        }
        return 1;
    }

    @Override
    public void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setSpecialSlotItem(this.getControllerSlot());
    }

    public boolean addRadiationInputToMachineList(IGregTechTileEntity aTileEntity, int CasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (!(aMetaTileEntity instanceof MTERadioHatch radioHatch)) {
            return false;
        } else {
            radioHatch.updateTexture(CasingIndex);
            return this.mRadHatches.add(radioHatch);
        }
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack itemStack) {
        if (!this.checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
            || !checkHatch()) return false;
        setupParameters();
        return this.mCountCasing >= 19;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        this.mRadHatches.clear();
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
        return super.checkHatch() && checkEnergyHatch()
            && this.mRadHatches.size() <= 1
            && this.mOutputHatches.size() == 1
            && this.mMaintenanceHatches.size() == 1
            && !this.mInputHatches.isEmpty()
            && (!this.mEnergyHatches.isEmpty() || !this.mExoticEnergyHatches.isEmpty());
    }

    public int reCalculateFluidAmmount() {
        return this.getStoredFluids()
            .stream()
            .mapToInt(fluidStack -> fluidStack.amount)
            .sum();
    }

    public int reCalculateHeight() {
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
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public void construct(ItemStack itemStack, boolean b) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, itemStack, b, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
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
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
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

    public void sendAllRequiredRendererPackets() {
        int height = this.reCalculateHeight();
        if (this.mFluid != null && height > 1 && this.reCalculateFluidAmmount() > 0) {
            for (int x = -1; x < 2; x++) for (int y = 2; y < height + 1; y++) // Y轴提高1格
                for (int z = -1; z < 2; z++) this.sendPackagesOrRenewRenderer(x, y, z, this.mCulture);
        }
    }

    public void sendPackagesOrRenewRenderer(int x, int y, int z, BioCulture lCulture) {
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

    public void check_Chunk() {
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

    public void placeFluid() {
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

    public void removeFluid(int xDir, int zDir) {
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
            .getTotalWorldTime() % 20 == 7) {
                this.sendRenderPackets();
            }

        super.onRemoval();
    }

    public void sendRenderPackets() {
        int xDir = this.getXDir();
        int zDir = this.getZDir();
        this.sendRenderPackets(xDir, zDir);
    }

    public void sendRenderPackets(int xDir, int zDir) {
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

    public int getXDir() {
        return this.getBaseMetaTileEntity()
            .getBackFacing().offsetX * 2;
    }

    public int getZDir() {
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

    public enum RadioHatchElement implements IHatchElement<Incubator> {

        RadioHatch(Incubator::addRadiationInputToMachineList, MTERadioHatch.class) {

            @Override
            public long count(Incubator bioVat) {
                return bioVat.mRadHatches.size();
            }
        };

        public final List<Class<? extends IMetaTileEntity>> mteClasses;
        public final IGTHatchAdder<Incubator> adder;

        @SafeVarargs
        RadioHatchElement(IGTHatchAdder<Incubator> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public IGTHatchAdder<? super Incubator> adder() {
            return adder;
        }
    }

}
