package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.google.common.math.LongMath;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.block.blocks.BlockEternalGregTechWorkshopRender;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;
import com.science.gtnl.common.gui.modularui.multiblock.EternalGregTechWorkshopGui;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.EGTWUpgradeStorage;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.EternalGregTechWorkshopUpgrade;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.MilestoneFormatter;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.MilestoneIcon;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;

import bartworks.common.loaders.ItemRegistry;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.INEIPreviewModifier;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.GTStructureChannels;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Setter;
import tectech.thing.casing.TTCasingsContainer;

@IMetaTileEntity.SkipGenerateDescription
public class EternalGregTechWorkshop extends MultiMachineBase<EternalGregTechWorkshop> implements INEIPreviewModifier {

    private static final int DEFAULT_PREVIEW_HEIGHT = 4;
    private static final int MAX_PREVIEW_HEIGHT = 64;
    private static final String OBSERVER_WORLD_CLASS_NAME = "blockrenderer6343.client.world.ObserverWorld";

    // 75 x 19 x 75
    private static final String STRUCTURE_PIECE_MAIN_TOP = "main_top";
    private static final int HORIZONTAL_OFF_SET_TOP = 37;
    private static final int VERTICAL_OFF_SET_TOP = 46;
    private static final int DEPTH_OFF_SET_TOP = 11;

    // 53 x 22 x 53
    private static final String STRUCTURE_PIECE_MAIN_UP = "main_up";
    private static final int HORIZONTAL_OFF_SET_UP = 26;
    private static final int VERTICAL_OFF_SET_UP = 27;
    private static final int DEPTH_OFF_SET_UP = 0;

    // 53 x 11 x 53
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 26;
    private static final int VERTICAL_OFF_SET = 5;
    private static final int DEPTH_OFF_SET = 1;

    // 53 x 22 x 53
    private static final String STRUCTURE_PIECE_MAIN_DOWN = "main_down";
    private static final int HORIZONTAL_OFF_SET_DOWN = 26;
    private static final int VERTICAL_OFF_SET_DOWN = -6;
    private static final int DEPTH_OFF_SET_DOWN = 0;

    // 75 x 22 x 75
    private static final String STRUCTURE_PIECE_MAIN_BOTTOM = "main_bottom";
    private static final int HORIZONTAL_OFF_SET_BOTTOM = 37;
    private static final int VERTICAL_OFF_SET_BOTTOM = -28;
    private static final int DEPTH_OFF_SET_BOTTOM = 11;

    // 63 x 7 x 63
    private static final String STRUCTURE_PIECE_MAIN_EXTRA = "main_extra";
    private static final String STRUCTURE_PIECE_MAIN_EXTRA_AIR = "main_extra_air";
    private static final int HORIZONTAL_OFF_SET_EXTRA = 31;
    private static final int VERTICAL_OFF_SET_EXTRA_UP = 14;
    private static final int VERTICAL_OFF_SET_EXTRA_DOWN = -8;
    private static final int DEPTH_OFF_SET_EXTRA = 5;

    private static final String EGTWT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/top";
    private static final String EGTWU_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/up";
    private static final String EGTWC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/center";
    private static final String EGTWD_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/down";
    private static final String EGTWB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/bottom";
    private static final String EGTWE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/extra";
    private static final String[][] shapeTop = StructureUtils.readStructureFromFile(EGTWT_STRUCTURE_FILE_PATH);
    private static final String[][] shapeUp = StructureUtils.readStructureFromFile(EGTWU_STRUCTURE_FILE_PATH);
    private static final String[][] shape = StructureUtils.readStructureFromFile(EGTWC_STRUCTURE_FILE_PATH);
    private static final String[][] shapeDown = StructureUtils.readStructureFromFile(EGTWD_STRUCTURE_FILE_PATH);
    private static final String[][] shapeBottom = StructureUtils.readStructureFromFile(EGTWB_STRUCTURE_FILE_PATH);
    public static final String[][] shapeExtra = StructureUtils.readStructureFromFile(EGTWE_STRUCTURE_FILE_PATH);
    private static final String[][] shapeExtraAir = StructureUtils.replaceLetters(shapeExtra, "a");
    private static final IStructureDefinition<EternalGregTechWorkshop> STRUCTURE_DEFINITION = StructureDefinition
        .<EternalGregTechWorkshop>builder()
        .addShape(STRUCTURE_PIECE_MAIN_TOP, StructureUtility.transpose(shapeTop))
        .addShape(STRUCTURE_PIECE_MAIN_UP, StructureUtility.transpose(shapeUp))
        .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
        .addShape(STRUCTURE_PIECE_MAIN_DOWN, StructureUtility.transpose(shapeDown))
        .addShape(STRUCTURE_PIECE_MAIN_BOTTOM, StructureUtility.transpose(shapeBottom))
        .addShape(STRUCTURE_PIECE_MAIN_EXTRA, StructureUtility.transpose(shapeExtra))
        .addShape(STRUCTURE_PIECE_MAIN_EXTRA_AIR, StructureUtility.transpose(shapeExtraAir))
        .addElement('A', Casings.SingularityReinforcedStellarShieldingCasing.asElement())
        .addElement('B', StructureUtility.ofBlock(Loaders.componentAssemblylineCasing, 12))
        .addElement('C', Casings.DimensionalInjectionCasing.asElement())
        .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSEMotor, 4))
        .addElement('E', Casings.ExtremeDensitySpaceBendingCasing.asElement())
        .addElement('F', Casings.NaquadriaReinforcedWaterPlantCasing.asElement())
        .addElement('G', Casings.CelestialMatterGuidanceCasing.asElement())
        .addElement('H', Casings.DimensionalBridge.asElement())
        .addElement('I', Casings.SpaceTimeBendingCore.asElement())
        .addElement('J', Casings.ParticleBeamGuidancePipeCasing.asElement())
        .addElement('K', GTStructureUtility.ofFrame(Materials.NaquadahAlloy))
        .addElement('L', Casings.OmniPurposeInfinityFusedGlass.asElement())
        .addElement('M', StructureUtility.ofBlock(ItemRegistry.bw_realglas, 6))
        .addElement(
            'N',
            GTStructureUtility.buildHatchAdder(EternalGregTechWorkshop.class)
                .atLeast(
                    HatchElement.Maintenance,
                    HatchElement.InputBus,
                    HatchElement.OutputBus,
                    HatchElement.InputHatch,
                    HatchElement.OutputHatch)
                .casingIndex(961)
                .hint(1)
                .buildAndChain(
                    StructureUtility
                        .onElementPass(x -> ++x.mCountCasing, Casings.CelestialMatterGuidanceCasing.asElement())))
        .addElement('O', Casings.QuarkExclusionCasing.asElement())
        .addElement('P', Casings.ReinforcedTemporalStructureCasing.asElement())
        .addElement('Q', Casings.CentralGravitonFlowModulator.asElement())
        .addElement('R', Casings.StellarEnergySiphonCasing.asElement())
        .addElement('S', Casings.HarmonicPhononTransmissionConduit.asElement())
        .addElement('T', StructureUtility.ofBlock(Loaders.gravityStabilizationCasing, 0))
        .addElement('U', Casings.GallifreyanSpacetimeCompressionFieldGenerator.asElement())
        .addElement('V', Casings.GallifreyanTimeDilationFieldGenerator.asElement())
        .addElement('W', Casings.ReinforcedSpatialStructureCasing.asElement())
        .addElement('X', Casings.GallifreyanStabilisationFieldGenerator.asElement())
        .addElement(
            'Y',
            HatchElementBuilder.<EternalGregTechWorkshop>builder()
                .atLeast(EternalGregTechWorkshop.moduleElement.Module)
                .casingIndex(960)
                .hint(1)
                .hint(1)
                .buildAndChain(TTCasingsContainer.GodforgeCasings, 0))
        .addElement(
            'Z',
            StructureUtility.ofChain(
                Casings.DimensionalBridge.asElement(),
                StructureUtility.ofBlock(BlockLoader.eternalGregTechWorkshopRender, 0)))
        .addElement('a', StructureUtility.isAir())
        .build();

    public int mHeatingCapacity = 0;
    public int mMachineTier = 0;
    public int mModuleTier = 1;
    public long mMaxUseEUt = 0;
    public double mEUtDiscount = 1;
    public double mSpeedBoost = 1;
    public UUID ownerUUID;
    public boolean mExtraModule;
    @Setter
    public boolean enableExtraModule;
    public boolean secretUpgrade;
    public boolean enableRender = true;
    public boolean isRenderActive;

    public static final MilestoneFormatter DEFAULT_FORMATTING_MODE = MilestoneFormatter.COMMA;
    public static final BigInteger DEFAULT_TOTAL_POWER = BigInteger.ZERO;
    public static final int DEFAULT_FUEL_CONSUMPTION_FACTOR = 1;

    public static final long POWER_MILESTONE_CONSTANT = LongMath.pow(10, 15);
    public static final long RECIPE_MILESTONE_CONSTANT = LongMath.pow(10, 7);
    public static final long FUEL_MILESTONE_CONSTANT = 10_000;
    public static final double POWER_LOG_CONSTANT = Math.log(9);
    public static final double RECIPE_LOG_CONSTANT = Math.log(4);
    public static final double FUEL_LOG_CONSTANT = Math.log(3);

    public ItemStack[] storedUpgradeWindowItems = new ItemStack[16];
    public ItemStackHandler inputSlotHandler = new ItemStackHandler(16);

    public BigInteger totalPowerConsumed = DEFAULT_TOTAL_POWER;
    public long totalRecipesProcessed;
    public long totalFuelConsumed;
    public float totalExtensionsBuilt;

    public long fuelConsumption;

    public int fuelConsumptionFactor = DEFAULT_FUEL_CONSUMPTION_FACTOR;
    public int selectedFuelType;

    public int gravitonShardsAvailable = 0;
    public int gravitonShardsSpent;
    public boolean gravitonShardEjection;

    public float powerMilestonePercentage;
    public float recipeMilestonePercentage;
    public float fuelMilestonePercentage;
    public float structureMilestonePercentage;
    public int currentMilestoneID = 0;
    public final int[] milestoneProgress = new int[] { 0, 0, 0, 0 };

    public MilestoneFormatter formattingMode = DEFAULT_FORMATTING_MODE;

    public EGTWUpgradeStorage upgrades = new EGTWUpgradeStorage();
    public EternalGregTechWorkshopUpgrade currentUpgradeWindow;

    public ObjectArrayList<EternalGregTechWorkshopModule> moduleHatches = new ObjectArrayList<>();
    public long ticker = 0;
    public final ArrayList<FluidStack> validFuelList = new ArrayList<>() {

        {
            add(Materials.DTR.getFluid(1));
            add(Materials.RawStarMatter.getFluid(1));
            add(Materials.MHDCSM.getMolten(1));
        }
    };

    public EternalGregTechWorkshop(String aName) {
        super(aName);
    }

    public EternalGregTechWorkshop(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EternalGregTechWorkshop(this.mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.EternalGregTechWorkshopUpgradeRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.eternal_greg_tech_workshop.recipe_type"))
            .beginStructureBlock(75, 96, 75, true)
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.eternal_gregtech_workshop.casing"), 1)
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.eternal_gregtech_workshop.casing"), 1)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.eternal_gregtech_workshop.casing"), 1)
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.eternal_gregtech_workshop.casing"), 1)
            .addSubChannelUsage(GTStructureChannels.STRUCTURE_HEIGHT)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID() + 1),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_GOD_FORGE_CONTROLLER)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID() + 1),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_GOD_FORGE_CONTROLLER)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID() + 1) };
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getCasingTextureID() {
        return 960;
    }

    @Override
    public IStructureDefinition<EternalGregTechWorkshop> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mCountCasing = 0;
        moduleHatches.clear();
        int checkTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) {
            if (isRenderActive) destroyRenderer();
            mMachineTier = 0;
            mExtraModule = false;
            return;
        }

        while (checkTier < Integer.MAX_VALUE - 1) {
            if (checkPiece(
                STRUCTURE_PIECE_MAIN_UP,
                HORIZONTAL_OFF_SET_UP,
                VERTICAL_OFF_SET_UP + checkTier * 22,
                DEPTH_OFF_SET_UP,
                null)
                && checkPiece(
                    STRUCTURE_PIECE_MAIN_DOWN,
                    HORIZONTAL_OFF_SET_DOWN,
                    VERTICAL_OFF_SET_DOWN - checkTier * 22,
                    DEPTH_OFF_SET_DOWN,
                    null)) {
                checkTier++;
            } else break;
        }

        if (!checkPiece(
            STRUCTURE_PIECE_MAIN_TOP,
            HORIZONTAL_OFF_SET_TOP,
            VERTICAL_OFF_SET_TOP + (checkTier - 1) * 22,
            DEPTH_OFF_SET_TOP,
            errors)) {
            if (isRenderActive) destroyRenderer();
            mMachineTier = 0;
            mExtraModule = false;
            return;
        }

        if (!checkPiece(
            STRUCTURE_PIECE_MAIN_BOTTOM,
            HORIZONTAL_OFF_SET_BOTTOM,
            VERTICAL_OFF_SET_BOTTOM - (checkTier - 1) * 22,
            DEPTH_OFF_SET_BOTTOM,
            errors)) {
            if (isRenderActive) destroyRenderer();
            mMachineTier = 0;
            mExtraModule = false;
            return;
        }

        if (enableExtraModule && checkTier > 0) {
            for (int i = 0; i < checkTier; i++) {
                if (isRenderActive) {
                    if (!checkPiece(
                        STRUCTURE_PIECE_MAIN_EXTRA_AIR,
                        HORIZONTAL_OFF_SET_EXTRA,
                        VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                        DEPTH_OFF_SET_EXTRA,
                        null)) {
                        destroyRenderer();
                        mExtraModule = false;
                        break;
                    }
                    if (!checkPiece(
                        STRUCTURE_PIECE_MAIN_EXTRA_AIR,
                        HORIZONTAL_OFF_SET_EXTRA,
                        VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                        DEPTH_OFF_SET_EXTRA,
                        null)) {
                        mExtraModule = false;
                        break;
                    }
                } else {
                    if (!checkPiece(
                        STRUCTURE_PIECE_MAIN_EXTRA,
                        HORIZONTAL_OFF_SET_EXTRA,
                        VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                        DEPTH_OFF_SET_EXTRA,
                        null)) {
                        mExtraModule = false;
                        break;
                    }
                    if (!checkPiece(
                        STRUCTURE_PIECE_MAIN_EXTRA,
                        HORIZONTAL_OFF_SET_EXTRA,
                        VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                        DEPTH_OFF_SET_EXTRA,
                        null)) {
                        mExtraModule = false;
                        break;
                    }
                }
                mExtraModule = true;
            }
        }
        errors.clear();
        mMachineTier = checkTier;

        if (enableExtraModule && !isRenderActive && enableRender && mTotalRunTime > 0) {
            createRenderer();
        } else if (!enableExtraModule && isRenderActive) {
            destroyRenderer();
            mExtraModule = false;
        }
        checkCasingMin(errors, mCountCasing, 2);
    }

    @Override
    public void onPreviewConstruct(@NotNull ItemStack trigger) {
        buildPreviewStructure(trigger, false);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPreviewStructure(stackSize, hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        int count = resolvePreviewHeight(stackSize);
        int built;

        built = this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        for (int i = 0; i < count; i++) {
            built = this.survivalBuildPiece(
                STRUCTURE_PIECE_MAIN_UP,
                stackSize,
                HORIZONTAL_OFF_SET_UP,
                VERTICAL_OFF_SET_UP + i * 22,
                DEPTH_OFF_SET_UP,
                elementBudget,
                env,
                false,
                true);

            if (built >= 0) return built;

            built = this.survivalBuildPiece(
                STRUCTURE_PIECE_MAIN_DOWN,
                stackSize,
                HORIZONTAL_OFF_SET_DOWN,
                VERTICAL_OFF_SET_DOWN - i * 22,
                DEPTH_OFF_SET_DOWN,
                elementBudget,
                env,
                false,
                true);

            if (built >= 0) return built;

            if (count > 1) {
                built = this.survivalBuildPiece(
                    STRUCTURE_PIECE_MAIN_EXTRA,
                    stackSize,
                    HORIZONTAL_OFF_SET_EXTRA,
                    VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                    DEPTH_OFF_SET_EXTRA,
                    elementBudget,
                    env,
                    false,
                    true);

                if (built >= 0) return built;

                built = this.survivalBuildPiece(
                    STRUCTURE_PIECE_MAIN_EXTRA,
                    stackSize,
                    HORIZONTAL_OFF_SET_EXTRA,
                    VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                    DEPTH_OFF_SET_EXTRA,
                    elementBudget,
                    env,
                    false,
                    true);

                if (built >= 0) return built;
            }
        }

        built = this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_TOP,
            stackSize,
            HORIZONTAL_OFF_SET_TOP,
            VERTICAL_OFF_SET_TOP + (count - 1) * 22,
            DEPTH_OFF_SET_TOP,
            elementBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        built = this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_BOTTOM,
            stackSize,
            HORIZONTAL_OFF_SET_BOTTOM,
            VERTICAL_OFF_SET_BOTTOM - (count - 1) * 22,
            DEPTH_OFF_SET_BOTTOM,
            elementBudget,
            env,
            false,
            true);

        return built;
    }

    private void buildPreviewStructure(ItemStack trigger, boolean hintsOnly) {
        int count = resolvePreviewHeight(trigger);

        buildPiece(STRUCTURE_PIECE_MAIN, trigger, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);

        for (int i = 0; i < count; i++) {
            buildPiece(
                STRUCTURE_PIECE_MAIN_UP,
                trigger,
                hintsOnly,
                HORIZONTAL_OFF_SET_UP,
                VERTICAL_OFF_SET_UP + i * 22,
                DEPTH_OFF_SET_UP);

            buildPiece(
                STRUCTURE_PIECE_MAIN_DOWN,
                trigger,
                hintsOnly,
                HORIZONTAL_OFF_SET_DOWN,
                VERTICAL_OFF_SET_DOWN - i * 22,
                DEPTH_OFF_SET_DOWN);

            if (count > 1) {
                buildPiece(
                    STRUCTURE_PIECE_MAIN_EXTRA,
                    trigger,
                    hintsOnly,
                    HORIZONTAL_OFF_SET_EXTRA,
                    VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                    DEPTH_OFF_SET_EXTRA);

                buildPiece(
                    STRUCTURE_PIECE_MAIN_EXTRA,
                    trigger,
                    hintsOnly,
                    HORIZONTAL_OFF_SET_EXTRA,
                    VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                    DEPTH_OFF_SET_EXTRA);
            }
        }

        buildPiece(
            STRUCTURE_PIECE_MAIN_TOP,
            trigger,
            hintsOnly,
            HORIZONTAL_OFF_SET_TOP,
            VERTICAL_OFF_SET_TOP + (count - 1) * 22,
            DEPTH_OFF_SET_TOP);

        buildPiece(
            STRUCTURE_PIECE_MAIN_BOTTOM,
            trigger,
            hintsOnly,
            HORIZONTAL_OFF_SET_BOTTOM,
            VERTICAL_OFF_SET_BOTTOM - (count - 1) * 22,
            DEPTH_OFF_SET_BOTTOM);
    }

    private int resolvePreviewHeight(ItemStack trigger) {
        if (trigger == null) {
            return DEFAULT_PREVIEW_HEIGHT;
        }
        if (GTStructureChannels.STRUCTURE_HEIGHT.hasValue(trigger)) {
            return GTStructureChannels.STRUCTURE_HEIGHT
                .getValueClamped(trigger, DEFAULT_PREVIEW_HEIGHT, MAX_PREVIEW_HEIGHT);
        }
        if (isBlockRendererObserverWorld()) {
            return DEFAULT_PREVIEW_HEIGHT;
        }
        return GTStructureChannels.STRUCTURE_HEIGHT
            .getValueClamped(trigger, DEFAULT_PREVIEW_HEIGHT, MAX_PREVIEW_HEIGHT);
    }

    private boolean isBlockRendererObserverWorld() {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        World world = baseMetaTileEntity == null ? null : baseMetaTileEntity.getWorld();
        return world != null && OBSERVER_WORLD_CLASS_NAME.equals(
            world.getClass()
                .getName());
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && getBaseMetaTileEntity().isAllowedToWork()) {
            ticker++;
            if (ticker % (5 * SECONDS) == 0) {
                startRecipeProcessing();

                if (!moduleHatches.isEmpty() && moduleHatches.size() <= mMachineTier * 4) {
                    for (EternalGregTechWorkshopModule module : moduleHatches) {
                        if (allowModuleConnection(module, this)) {
                            module.connect();
                            module.setEUtDiscount(mEUtDiscount);
                            module.setDurationModifier(mSpeedBoost);
                            module.setMaxParallel(getTrueParallel());
                            module.setMaxUseEUt(mMaxUseEUt);
                            module.setHeat(mHeatingCapacity);
                            EternalGregTechWorkshopModule.queryMilestoneStats(module, this);
                        } else {
                            module.disconnect();
                            module.setEUtDiscount(1.0D);
                            module.setDurationModifier(1.0D);
                            module.setMaxParallel(0);
                            module.setMaxUseEUt(0);
                        }
                    }
                } else if (moduleHatches.size() > mMachineTier * 4) {
                    for (EternalGregTechWorkshopModule module : moduleHatches) {
                        module.disconnect();
                    }
                }

                drainFuel();
                determineCompositionMilestoneLevel();
                determineMilestoneProgress();
                endRecipeProcessing();
                if (mEfficiency < 0) mEfficiency = 0;
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            enableRender = !enableRender;
            GTUtility.sendChatTrans(aPlayer, "gtnl.chat.render." + (enableRender ? "enabled" : "disabled"));
            if (!enableRender && isRenderActive) destroyRenderer();
        }
        return true;
    }

    public void drainFuel() {
        fuelConsumption = (long) Math.max(calculateFuelConsumption(this) * 5, 1);

        FluidStack fuelToDrain = new FluidStack(validFuelList.get(selectedFuelType), (int) fuelConsumption);
        for (MTEHatchInput hatch : GTUtility.filterValidMTEs(mInputHatches)) {
            FluidStack drained = hatch.drain(ForgeDirection.UNKNOWN, fuelToDrain, true);
            if (drained == null) {
                continue;
            }

            fuelToDrain.amount -= drained.amount;

            if (fuelToDrain.amount == 0) {
                totalFuelConsumed += fuelConsumptionFactor;
                return;
            }
        }
    }

    public static double calculateFuelConsumption(EternalGregTechWorkshop egtw) {
        double upgradeFactor = 1;
        if (egtw.isUpgradeActive(EternalGregTechWorkshopUpgrade.STEM)) {
            upgradeFactor = 0.8;
        }
        if (egtw.selectedFuelType == 0) {
            return egtw.fuelConsumptionFactor * 300 * Math.pow(1.15, egtw.fuelConsumptionFactor) * upgradeFactor;
        }
        if (egtw.selectedFuelType == 1) {
            return egtw.fuelConsumptionFactor * 2 * Math.pow(1.08, egtw.fuelConsumptionFactor) * upgradeFactor;
        } else return egtw.fuelConsumptionFactor / 25f * upgradeFactor;
    }

    @Override
    public void onRemoval() {
        if (moduleHatches != null && !moduleHatches.isEmpty()) {
            for (EternalGregTechWorkshopModule module : moduleHatches) {
                module.disconnect();
            }
        }
        super.onRemoval();
    }

    @Override
    public boolean isFlipChangeAllowed() {
        if (mMachine || isRenderActive) return false;
        return super.isFlipChangeAllowed();
    }

    @Override
    public boolean isRotationChangeAllowed() {
        if (mMachine || isRenderActive) return false;
        return super.isRotationChangeAllowed();
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        if (isRenderActive) {
            destroyRenderer();
        }
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        if (getBaseMetaTileEntity().isAllowedToWork()) {
            if (addEUToGlobalEnergyMap(ownerUUID, -moduleHatches.size() * Integer.MAX_VALUE)) {
                setEUtDiscount(Math.pow(0.95D, mModuleTier));
                setDurationModifier(Math.pow(0.9D, mModuleTier));
                setMaxUseEUt((1L << Math.min(mModuleTier, 28)) * (Integer.MAX_VALUE * 10L));

                mEfficiencyIncrease = 10000;
                mMaxProgresstime = 128;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }

        mEUtDiscount = 1;
        mSpeedBoost = 1;
        mMaxUseEUt = 0;
        mEfficiencyIncrease = 0;
        mMaxProgresstime = 0;
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public static boolean allowModuleConnection(EternalGregTechWorkshopModule module, EternalGregTechWorkshop center) {

        if (module instanceof EternalGregTechWorkshopModule) {
            return true;
        }

        return false;
    }

    public boolean addModuleToMachineList(IGregTechTileEntity tileEntity, int baseCasingIndex) {
        if (tileEntity == null) {
            return false;
        }
        IMetaTileEntity metaTileEntity = tileEntity.getMetaTileEntity();
        if (metaTileEntity == null) {
            return false;
        }
        if (metaTileEntity instanceof EternalGregTechWorkshopModule module) {
            return moduleHatches.add(module);
        }
        return false;
    }

    public int getFuelType() {
        return selectedFuelType;
    }

    public void setFuelType(int fuelType) {
        selectedFuelType = fuelType;
    }

    public int getFuelFactor() {
        return fuelConsumptionFactor;
    }

    public void setFuelFactorFromGui(int fuelFactor) {
        fuelConsumptionFactor = MathHelper.clamp_int(fuelFactor, 1, calculateMaxFuelFactor(this));
    }

    public long getFuelConsumptionForGui() {
        return fuelConsumption;
    }

    public FluidStack getFuelStackForGui(int fuelType) {
        return validFuelList.get(MathHelper.clamp_int(fuelType, 0, validFuelList.size() - 1));
    }

    public int getMachineTierForGui() {
        return mMachineTier;
    }

    public boolean isExtraModuleEnabledForGui() {
        return enableExtraModule;
    }

    public void setExtraModuleEnabledFromGui(boolean enabled) {
        enableExtraModule = enabled;
        checkStructure(true, getBaseMetaTileEntity());
    }

    public boolean isExtraModuleBuiltForGui() {
        return mExtraModule;
    }

    public String getMachineStateTextForGui() {
        if (mProgresstime > 0) {
            return StatCollector.translateToLocal("gtnl.machine.eternal_greg_tech_workshop.running");
        }
        if (mMachine) {
            return StatCollector.translateToLocal("gtnl.machine.eternal_greg_tech_workshop.standby");
        }
        return StatCollector.translateToLocal("gtnl.machine.eternal_greg_tech_workshop.incomplete");
    }

    public int getCurrentMilestoneIdForGui() {
        return MathHelper.clamp_int(currentMilestoneID, 0, MilestoneIcon.VALUES.length - 1);
    }

    public void setCurrentMilestoneIdFromGui(int milestoneId) {
        currentMilestoneID = MathHelper.clamp_int(milestoneId, 0, MilestoneIcon.VALUES.length - 1);
    }

    public MilestoneFormatter getFormattingModeForGui() {
        return formattingMode;
    }

    public void setFormattingModeFromGui(MilestoneFormatter formatter) {
        formattingMode = formatter == null ? DEFAULT_FORMATTING_MODE : formatter;
    }

    public BigInteger getTotalPowerConsumedForGui() {
        return totalPowerConsumed;
    }

    public long getTotalRecipesProcessedForGui() {
        return totalRecipesProcessed;
    }

    public long getTotalFuelConsumedForGui() {
        return totalFuelConsumed;
    }

    public int getMilestoneProgressLevelForGui(int milestoneId) {
        return milestoneProgress[MathHelper.clamp_int(milestoneId, 0, milestoneProgress.length - 1)];
    }

    public float getMilestonePercentageForGui(int milestoneId) {
        return switch (MathHelper.clamp_int(milestoneId, 0, MilestoneIcon.VALUES.length - 1)) {
            case 1 -> recipeMilestonePercentage;
            case 2 -> fuelMilestonePercentage;
            case 3 -> structureMilestonePercentage;
            default -> powerMilestonePercentage;
        };
    }

    public int getGravitonShardsAvailableForGui() {
        return gravitonShardsAvailable;
    }

    public void setGravitonShardsAvailableFromGui(int shards) {
        gravitonShardsAvailable = MathHelper.clamp_int(shards, 0, 112);
    }

    public boolean isGravitonShardEjectionForGui() {
        return gravitonShardEjection;
    }

    public void setGravitonShardEjectionFromGui(boolean enabled) {
        gravitonShardEjection = enabled && upgrades.isUpgradeActive(EternalGregTechWorkshopUpgrade.END);
    }

    public boolean isSecretUpgradeForGui() {
        return secretUpgrade;
    }

    public void setSecretUpgradeFromGui(boolean enabled) {
        secretUpgrade = enabled;
    }

    public EternalGregTechWorkshopUpgrade getCurrentUpgradeForGui() {
        return currentUpgradeWindow == null ? EternalGregTechWorkshopUpgrade.START : currentUpgradeWindow;
    }

    public void setCurrentUpgradeFromGui(EternalGregTechWorkshopUpgrade upgrade) {
        currentUpgradeWindow = upgrade == null ? EternalGregTechWorkshopUpgrade.START : upgrade;
    }

    public List<EGTWUpgradeStorage.UpgradeData> getUpgradeDataForGui() {
        ObjectArrayList<EGTWUpgradeStorage.UpgradeData> data = new ObjectArrayList<>(
            EternalGregTechWorkshopUpgrade.VALUES.length);
        for (EternalGregTechWorkshopUpgrade upgrade : EternalGregTechWorkshopUpgrade.VALUES) {
            data.add(upgrades.getData(upgrade));
        }
        return data;
    }

    public void setUpgradeDataFromGui(List<EGTWUpgradeStorage.UpgradeData> data) {
        for (int i = 0; i < data.size() && i < EternalGregTechWorkshopUpgrade.VALUES.length; i++) {
            upgrades.unlockedUpgrades.put(EternalGregTechWorkshopUpgrade.VALUES[i], data.get(i));
        }
    }

    public boolean isUpgradeActiveForGui(EternalGregTechWorkshopUpgrade upgrade) {
        return isUpgradeActive(upgrade);
    }

    public boolean isUpgradeCostPaidForGui(EternalGregTechWorkshopUpgrade upgrade) {
        return upgrades.isCostPaid(upgrade);
    }

    public void completeUpgradeFromGui(EternalGregTechWorkshopUpgrade upgrade) {
        completeUpgrade(upgrade);
    }

    public void respecUpgradeFromGui(EternalGregTechWorkshopUpgrade upgrade) {
        respecUpgrade(upgrade);
    }

    public void payUpgradeCostFromGui(EternalGregTechWorkshopUpgrade upgrade) {
        upgrades.payCost(upgrade, inputSlotHandler);
    }

    public void resetUpgradesFromGui() {
        upgrades.resetAll();
    }

    public void unlockAllUpgradesFromGui() {
        upgrades.unlockAll();
    }

    public ItemStackHandler getUpgradeInputHandlerForGui() {
        restoreStoredUpgradeWindowItemsForGui();
        return inputSlotHandler;
    }

    private void restoreStoredUpgradeWindowItemsForGui() {
        for (int i = 0; i < storedUpgradeWindowItems.length; i++) {
            if (storedUpgradeWindowItems[i] == null) continue;
            inputSlotHandler.insertItem(i, storedUpgradeWindowItems[i], false);
            storedUpgradeWindowItems[i] = null;
        }
    }

    public double getEUtDiscount() {
        return mEUtDiscount;
    }

    public void setEUtDiscount(double eutDiscount) {
        mEUtDiscount = eutDiscount;
    }

    public double getSpeedBoost() {
        return mSpeedBoost;
    }

    public void setDurationModifier(double speedBoost) {
        mSpeedBoost = speedBoost;
    }

    public int getHeatingCapacity() {
        return mHeatingCapacity;
    }

    public void setHeatingCapacity(int heatingCapacity) {
        mHeatingCapacity = heatingCapacity;
    }

    public long getMaxUseEUt() {
        return mMaxUseEUt;
    }

    public void setMaxUseEUt(long maxUseEUt) {
        mMaxUseEUt = maxUseEUt;
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean getDefaultInputSeparationMode() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean isRecipeLockingEnabled() {
        return false;
    }

    @Override
    public boolean isInputSeparationEnabled() {
        return false;
    }

    public void determineCompositionMilestoneLevel() {
        int[] uniqueModuleCount = new int[5];
        for (EternalGregTechWorkshopModule module : moduleHatches) {
            if (module instanceof EGTWFusionModule) {
                uniqueModuleCount[2] = 1;
                break;
            }
        }
        totalExtensionsBuilt = Arrays.stream(uniqueModuleCount)
            .sum();
        milestoneProgress[3] = (int) Math.floor(totalExtensionsBuilt);
    }

    public void determineMilestoneProgress() {
        if (milestoneProgress[0] < 7) {
            powerMilestonePercentage = (float) Math.max(
                (Math.log((totalPowerConsumed.divide(BigInteger.valueOf(POWER_MILESTONE_CONSTANT))).longValue())
                    / POWER_LOG_CONSTANT + 1),
                0) / 7;
            milestoneProgress[0] = (int) Math.floor(powerMilestonePercentage * 7);
        }

        if (milestoneProgress[1] < 7) {
            recipeMilestonePercentage = (float) Math
                .max((Math.log(totalRecipesProcessed * 1f / RECIPE_MILESTONE_CONSTANT) / RECIPE_LOG_CONSTANT + 1), 0)
                / 7;
            milestoneProgress[1] = (int) Math.floor(recipeMilestonePercentage * 7);
        }
        if (milestoneProgress[2] < 7) {
            fuelMilestonePercentage = (float) Math
                .max((Math.log(totalFuelConsumed * 1f / FUEL_MILESTONE_CONSTANT) / FUEL_LOG_CONSTANT + 1), 0) / 7;
            milestoneProgress[2] = (int) Math.floor(fuelMilestonePercentage * 7);
        }

        if (milestoneProgress[3] <= 7) {
            structureMilestonePercentage = totalExtensionsBuilt / 7f;
        }
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new EternalGregTechWorkshopGui(this);
    }

    public boolean isUpgradeActive(EternalGregTechWorkshopUpgrade upgrade) {
        return upgrades.isUpgradeActive(upgrade);
    }

    public static int calculateMaxFuelFactor(EternalGregTechWorkshop egtw) {
        int fuelCap = 5;
        if (egtw.isUpgradeActive(EternalGregTechWorkshopUpgrade.TSE)) {
            fuelCap = Integer.MAX_VALUE;
        } else {
            if (egtw.isUpgradeActive(EternalGregTechWorkshopUpgrade.GEM)) {
                fuelCap += egtw.getTotalActiveUpgrades();
            }
            if (egtw.isUpgradeActive(EternalGregTechWorkshopUpgrade.CFCE)) {
                fuelCap *= (int) 1.2;
            }
        }
        return Math.max(fuelCap, 1);
    }

    public int getTotalActiveUpgrades() {
        return upgrades.getTotalActiveUpgrades();
    }

    public void completeUpgrade(EternalGregTechWorkshopUpgrade upgrade) {
        if (isUpgradeActive(upgrade)) return;
        if (!upgrades.checkPrerequisites(upgrade)) return;
        if (!upgrades.checkCost(upgrade, gravitonShardsAvailable)) return;

        upgrades.unlockUpgrade(upgrade);
        gravitonShardsAvailable -= upgrade.getShardCost();
        gravitonShardsSpent += upgrade.getShardCost();
    }

    public void respecUpgrade(EternalGregTechWorkshopUpgrade upgrade) {
        if (!isUpgradeActive(upgrade)) return;
        if (!upgrades.checkDependents(upgrade)) return;

        upgrades.respecUpgrade(upgrade);
        gravitonShardsAvailable += upgrade.getShardCost();
        gravitonShardsSpent -= upgrade.getShardCost();

        if (upgrade == EternalGregTechWorkshopUpgrade.END) {
            gravitonShardEjection = false;
        }
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        saveGeneralNBT(aNBT, false);
        super.saveNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        NBTTagCompound upgradeWindowStorageNBTTag = new NBTTagCompound();
        int storageIndex = 0;
        for (ItemStack itemStack : inputSlotHandler.getStacks()) {
            if (itemStack != null) {
                upgradeWindowStorageNBTTag
                    .setInteger(storageIndex + "stacksizeOfStoredUpgradeItems", itemStack.stackSize);
                aNBT.setTag(storageIndex + "storedUpgradeItem", itemStack.writeToNBT(new NBTTagCompound()));
            }
            storageIndex++;
        }
        aNBT.setTag("upgradeWindowStorage", upgradeWindowStorageNBTTag);

        saveGeneralNBT(aNBT, true);
        aNBT.setInteger("mMachineTier", mMachineTier);
        aNBT.setBoolean("enableExtraModule", enableExtraModule);
        aNBT.setBoolean("mExtraModule", mExtraModule);
        aNBT.setBoolean("isRenderActive", isRenderActive);
        aNBT.setBoolean("enableRender", enableRender);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mMachineTier = aNBT.getInteger("mMachineTier");
        mModuleTier = aNBT.getInteger("mModuleTier");
        enableExtraModule = aNBT.getBoolean("enableExtraModule");
        mExtraModule = aNBT.getBoolean("mExtraModule");
        gravitonShardsSpent = aNBT.getInteger("gravitonShardsSpent");
        isRenderActive = aNBT.getBoolean("isRenderActive");
        if (aNBT.hasKey("enableRender")) enableRender = aNBT.getBoolean("enableRender");

        if (aNBT.hasKey("totalPowerConsumed")) {
            totalPowerConsumed = new BigInteger(aNBT.getByteArray("totalPowerConsumed"));
        }
        if (aNBT.hasKey("formattingMode")) {
            int index = MathHelper.clamp_int(aNBT.getInteger("formattingMode"), 0, MilestoneFormatter.VALUES.length);
            formattingMode = MilestoneFormatter.VALUES[index];
        }

        NBTTagCompound tempItemTag = aNBT.getCompoundTag("upgradeWindowStorage");
        for (int index = 0; index < 16; index++) {
            int stackSize = tempItemTag.getInteger(index + "stacksizeOfStoredUpgradeItems");
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag(index + "storedUpgradeItem"));
            if (itemStack != null) {
                storedUpgradeWindowItems[index] = itemStack.splitStack(stackSize);
            }
        }

        upgrades.rebuildFromNBT(aNBT);
    }

    public void saveGeneralNBT(NBTTagCompound NBT, boolean force) {
        if (force || mModuleTier != 0) NBT.setInteger("mModuleTier", mModuleTier);
        if (force || gravitonShardsSpent != 0) NBT.setInteger("gravitonShardsSpent", gravitonShardsSpent);
        if (force || gravitonShardsAvailable != 0) NBT.setInteger("gravitonShardsAvailable", gravitonShardsAvailable);
        if (force || secretUpgrade) NBT.setBoolean("secretUpgrade", secretUpgrade);

        if (force || gravitonShardsAvailable != 0) NBT.setInteger("gravitonShardsAvailable", gravitonShardsAvailable);
        if (force || gravitonShardsSpent != 0) NBT.setInteger("gravitonShardsSpent", gravitonShardsSpent);
        if (force || totalRecipesProcessed != 0) NBT.setLong("totalRecipesProcessed", totalRecipesProcessed);
        if (force || totalFuelConsumed != 0) NBT.setLong("totalFuelConsumed", totalFuelConsumed);
        if (force || gravitonShardEjection) NBT.setBoolean("gravitonShardEjection", gravitonShardEjection);
        if (force || secretUpgrade) NBT.setBoolean("secretUpgrade", secretUpgrade);

        if (force || !DEFAULT_TOTAL_POWER.equals(totalPowerConsumed)) {
            NBT.setByteArray("totalPowerConsumed", totalPowerConsumed.toByteArray());
        }
        if (force || formattingMode != DEFAULT_FORMATTING_MODE) {
            NBT.setInteger("formattingMode", formattingMode.ordinal());
        }
        upgrades.serializeToNBT(NBT, force);
    }

    public void destroyExtraModule() {
        for (int i = 0; i < mMachineTier; i++) {
            buildPiece(
                STRUCTURE_PIECE_MAIN_EXTRA_AIR,
                null,
                false,
                HORIZONTAL_OFF_SET_EXTRA,
                VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                DEPTH_OFF_SET_EXTRA);
            buildPiece(
                STRUCTURE_PIECE_MAIN_EXTRA_AIR,
                null,
                false,
                HORIZONTAL_OFF_SET_EXTRA,
                VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                DEPTH_OFF_SET_EXTRA);
            mExtraModule = true;
        }

    }

    public void buildExtraModule() {
        for (int i = 0; i < mMachineTier; i++) {
            buildPiece(
                STRUCTURE_PIECE_MAIN_EXTRA,
                null,
                false,
                HORIZONTAL_OFF_SET_EXTRA,
                VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                DEPTH_OFF_SET_EXTRA);
            buildPiece(
                STRUCTURE_PIECE_MAIN_EXTRA,
                null,
                false,
                HORIZONTAL_OFF_SET_EXTRA,
                VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                DEPTH_OFF_SET_EXTRA);
            mExtraModule = true;
        }
    }

    public void destroyRenderer() {
        ChunkCoordinates renderPos = getRenderPos();
        World world = this.getBaseMetaTileEntity()
            .getWorld();

        if (!(world
            .getBlock(renderPos.posX, renderPos.posY, renderPos.posZ) instanceof BlockEternalGregTechWorkshopRender))
            return;

        world.setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, Blocks.air);
        world.setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, GregTechAPI.sBlockCasings1, 14, 2);

        buildExtraModule();

        isRenderActive = false;
        disableWorking();
    }

    public void createRenderer() {
        ChunkCoordinates renderPos = getRenderPos();

        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, Blocks.air);
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, BlockLoader.eternalGregTechWorkshopRender);
        TileEntityEternalGregTechWorkshop rendererTileEntity = (TileEntityEternalGregTechWorkshop) this
            .getBaseMetaTileEntity()
            .getWorld()
            .getTileEntity(renderPos.posX, renderPos.posY, renderPos.posZ);

        destroyExtraModule();

        rendererTileEntity.setRenderRotation(getRotation(), getDirection());
        updateRenderer();

        isRenderActive = true;
        enableWorking();
    }

    public ChunkCoordinates getRenderPos() {
        IGregTechTileEntity gregTechTileEntity = this.getBaseMetaTileEntity();
        int x = gregTechTileEntity.getXCoord();
        int y = gregTechTileEntity.getYCoord();
        int z = gregTechTileEntity.getZCoord();
        double xOffset = 26 * getExtendedFacing().getRelativeBackInWorld().offsetX;
        double yOffset = 26 * getExtendedFacing().getRelativeBackInWorld().offsetY;
        double zOffset = 26 * getExtendedFacing().getRelativeBackInWorld().offsetZ;
        return new ChunkCoordinates((int) (x + xOffset), (int) (y + yOffset), (int) (z + zOffset));
    }

    public TileEntityEternalGregTechWorkshop getRenderer() {
        ChunkCoordinates renderPos = getRenderPos();
        TileEntity tile = this.getBaseMetaTileEntity()
            .getWorld()
            .getTileEntity(renderPos.posX, renderPos.posY, renderPos.posZ);

        if (tile instanceof TileEntityEternalGregTechWorkshop egtwTile) {
            return egtwTile;
        }
        return null;
    }

    public void updateRenderer() {
        TileEntityEternalGregTechWorkshop tile = getRenderer();
        if (tile == null) return;
        tile.setRenderCount(mMachineTier);
        tile.updateToClient();
    }

    public void addTotalPowerConsumed(BigInteger amount) {
        totalPowerConsumed = totalPowerConsumed.add(amount);
    }

    public void addTotalRecipesProcessed(long amount) {
        totalRecipesProcessed += amount;
    }

    public enum moduleElement implements IHatchElement<EternalGregTechWorkshop> {

        Module(EternalGregTechWorkshop::addModuleToMachineList, EternalGregTechWorkshopModule.class) {

            @Override
            public long count(EternalGregTechWorkshop tileEntity) {
                return tileEntity.moduleHatches.size();
            }
        };

        public final List<Class<? extends IMetaTileEntity>> mteClasses;
        public final IGTHatchAdder<EternalGregTechWorkshop> adder;

        @SafeVarargs
        moduleElement(IGTHatchAdder<EternalGregTechWorkshop> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super EternalGregTechWorkshop> adder() {
            return adder;
        }
    }
}
