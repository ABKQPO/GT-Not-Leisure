package com.science.gtnl.common.machine.multiblock;

import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtnhlanth.common.register.LanthItemList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.TTCasingsContainer;

public class RealArtificialStar extends MultiMachineBase<RealArtificialStar> {

    private static final int HORIZONTAL_OFF_SET = 62;
    private static final int VERTICAL_OFF_SET = 88;
    private static final int DEPTH_OFF_SET = 15;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String RAS_STRUCTURE_FILE_PATH = ScienceNotLeisure.RESOURCE_ROOT_ID + ":"
        + "multiblock/real_artificial_star";
    private static final String[][] shape = StructureUtils.readStructureFromFile(RAS_STRUCTURE_FILE_PATH);
    public static long MaxOfDepletedExcitedNaquadahFuelRod = MainConfig.machine.artificial_star.euEveryDepletedExcitedNaquadahFuelRod;
    public static long MaxOfEnhancementCore = MainConfig.machine.artificial_star.euEveryEnhancementCore;
    public static long MaxOfAntimatter = 3;
    public static long MaxOfAntimatterFuelRod = 1024;
    public static long MaxOfStrangeAnnihilationFuelRod = 32768;
    public String ownerName;
    public UUID ownerUUID;
    public long storageEU = 0;
    public int tierDimensionField = -1;
    public int tierTimeField = -1;
    public int tierStabilisationField = -1;
    public double outputMultiplier = 1;
    public int recoveryChance = 0;
    public byte rewardContinuous = 0;
    public BigInteger currentOutputEU = BigInteger.ZERO;
    public final DecimalFormat decimalFormat = new DecimalFormat("#.0");
    public boolean isRendering = false;
    public static boolean configEnableDefaultRender = MainConfig.machine.artificial_star.enableRenderDefaultArtificialStar;
    public boolean enableRender = configEnableDefaultRender;

    public static final ItemStack TST_PROTO = GTModHandler
        .getModItem(ModList.TwistSpaceTechnology.ID, "MetaItem01", 1, 17);
    private static final ItemStack DEPLETED_ROD = GTNLItemList.DepletedExcitedNaquadahFuelRod.get(1);
    private static final ItemStack ENHANCEMENT_CORE = GTNLItemList.EnhancementCore.get(1);
    private static final ItemStack TST_ANTIMATTER = ModList.TwistSpaceTechnology.isModLoaded()
        ? GTModHandler.getModItem(ModList.TwistSpaceTechnology.ID, "MetaItem01", 1, 14)
        : null;
    private static final ItemStack TST_FUEL_ROD = ModList.TwistSpaceTechnology.isModLoaded()
        ? GTModHandler.getModItem(ModList.TwistSpaceTechnology.ID, "MetaItem01", 1, 16)
        : null;
    private static final ItemStack TST_STRANGE_ROD = ModList.TwistSpaceTechnology.isModLoaded()
        ? GTModHandler.getModItem(ModList.TwistSpaceTechnology.ID, "MetaItem01", 1, 29)
        : null;

    public RealArtificialStar(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public RealArtificialStar(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new RealArtificialStar(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return 13;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("isActive")) {
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Info_RealArtificialStar_00")
                    + EnumChatFormatting.GOLD
                    + tag.getString("currentOutputEU")
                    + EnumChatFormatting.RED
                    + " * "
                    + decimalFormat.format(tag.getDouble("outputMultiplier"))
                    + EnumChatFormatting.GREEN
                    + " * 2147483647"
                    + EnumChatFormatting.RESET
                    + " EU / "
                    + MainConfig.machine.artificial_star.secondsOfArtificialStarProgressCycleTime
                    + " s");
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            if (tileEntity.isActive()) {
                tag.setString("currentOutputEU", currentOutputEU.toString());
                tag.setDouble("outputMultiplier", (outputMultiplier * (rewardContinuous + 100) / 100));
            }
        }
    }

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 6];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = EnumChatFormatting.GOLD + StatCollector.translateToLocal("Info_RealArtificialStar_01")
            + EnumChatFormatting.RESET
            + ": "
            + EnumChatFormatting.GREEN
            + (rewardContinuous + 100)
            + "%";
        ret[origin.length + 1] = EnumChatFormatting.GOLD + StatCollector.translateToLocal("Info_RealArtificialStar_02")
            + EnumChatFormatting.RESET
            + ": "
            + EnumChatFormatting.GREEN
            + outputMultiplier;
        ret[origin.length + 2] = EnumChatFormatting.GOLD + StatCollector.translateToLocal("Info_RealArtificialStar_03")
            + EnumChatFormatting.RESET
            + ": "
            + EnumChatFormatting.YELLOW
            + tierDimensionField;
        ret[origin.length + 3] = EnumChatFormatting.GOLD + StatCollector.translateToLocal(
            "Info_RealArtificialStar_04") + EnumChatFormatting.RESET + ": " + EnumChatFormatting.YELLOW + tierTimeField;
        ret[origin.length + 4] = EnumChatFormatting.GOLD + StatCollector.translateToLocal("Info_RealArtificialStar_05")
            + EnumChatFormatting.RESET
            + ": "
            + EnumChatFormatting.YELLOW
            + tierStabilisationField;
        ret[origin.length + 5] = EnumChatFormatting.GOLD + StatCollector.translateToLocal("Info_RealArtificialStar_06")
            + EnumChatFormatting.RESET
            + ": "
            + EnumChatFormatting.AQUA
            + recoveryChance
            + EnumChatFormatting.RESET
            + "/"
            + EnumChatFormatting.AQUA
            + "1000";
        return ret;
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.enableRender = !enableRender;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal("Info_Render_" + (this.enableRender ? "Enabled" : "Disabled")));
        }
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.RealArtificialStarRecipes;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        // iterate input bus slot
        // consume fuel and generate EU
        boolean flag = false;
        long recoveryAmount = 0;
        long recoveryAmountTST = 0;

        currentOutputEU = BigInteger.ZERO;

        for (ItemStack items : getStoredInputs()) {
            if (items == null || items.stackSize <= 0) continue;

            long size = items.stackSize;
            boolean matched = false;

            if (GTUtility.areStacksEqual(items, DEPLETED_ROD, true)) {
                currentOutputEU = currentOutputEU.add(
                    BigInteger.valueOf(MaxOfDepletedExcitedNaquadahFuelRod)
                        .multiply(BigInteger.valueOf(size)));
                matched = true;
            } else if (GTUtility.areStacksEqual(items, ENHANCEMENT_CORE, true)) {
                currentOutputEU = currentOutputEU.add(
                    BigInteger.valueOf(MaxOfEnhancementCore)
                        .multiply(BigInteger.valueOf(size)));
                matched = true;
            } else if (ModList.TwistSpaceTechnology.isModLoaded()) {
                if (GTUtility.areStacksEqual(items, TST_ANTIMATTER, true)) {
                    currentOutputEU = currentOutputEU.add(
                        BigInteger.valueOf(MaxOfAntimatter)
                            .multiply(BigInteger.valueOf(size)));
                    matched = true;
                } else if (GTUtility.areStacksEqual(items, TST_FUEL_ROD, true)) {
                    currentOutputEU = currentOutputEU.add(
                        BigInteger.valueOf(MaxOfAntimatterFuelRod)
                            .multiply(BigInteger.valueOf(size)));
                    recoveryAmountTST += size;
                    matched = true;
                } else if (GTUtility.areStacksEqual(items, TST_STRANGE_ROD, true)) {
                    currentOutputEU = currentOutputEU.add(
                        BigInteger.valueOf(MaxOfStrangeAnnihilationFuelRod)
                            .multiply(BigInteger.valueOf(size)));
                    recoveryAmountTST += size;
                    matched = true;
                }
            }

            if (matched) {
                flag = true;
                items.stackSize = 0;
            }
        }

        // flush input slots
        updateSlots();

        // if no antimatter or fuel rod input
        if (!flag) {
            // set 0 to multiplier of rewarding continuous operation
            rewardContinuous = 0;
            // stop render
            if (isRendering) {
                destroyRenderBlock();
                isRendering = false;
            }
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        // add EU to the wireless EU net
        BigDecimal eu = new BigDecimal(currentOutputEU).multiply(BigDecimal.valueOf(outputMultiplier))
            .multiply(BigDecimal.valueOf((rewardContinuous + 100d) / 100d))
            .multiply(new BigDecimal(Utils.INTEGER_MAX_VALUE));

        BigInteger result = eu.toBigInteger();
        if (!addEUToGlobalEnergyMap(ownerUUID, result)) {
            return CheckRecipeResultRegistry.INTERNAL_ERROR;
        }

        // set progress time with cfg
        mMaxProgresstime = (int) (20 * MainConfig.machine.artificial_star.secondsOfArtificialStarProgressCycleTime);
        // chance to recover FrameMaterial
        if (recoveryChance == 1000) {
            if (recoveryAmount > 0) {
                // mOutputItems = getRecovers(recoveryAmount);
            }
            if (recoveryAmountTST > 0 && ModList.TwistSpaceTechnology.isModLoaded()) {
                mOutputItems = getRecoversTST(recoveryAmountTST);
            }
        } else if (XSTR.XSTR_INSTANCE.nextInt(1000) < recoveryChance) {
            if (recoveryAmount > 0) {
                // mOutputItems = getRecovers(recoveryAmount);
            }
            if (recoveryAmountTST > 0 && ModList.TwistSpaceTechnology.isModLoaded()) {
                mOutputItems = getRecoversTST(recoveryAmountTST);
            }
        }

        // increase multiplier of rewarding continuous operation
        if (rewardContinuous < 50) rewardContinuous++;

        // start render
        if (enableRender && !isRendering) {
            createRenderBlock();
            isRendering = true;
        }
        return CheckRecipeResultRegistry.GENERATING;
    }

    // public ItemStack[] getRecovers(long amount) {
    // List<ItemStack> list = new ArrayList<>();
    //
    // if (amount <= Integer.MAX_VALUE) {
    // list.add(StellarConstructionFrameMaterial.get((int) amount));
    // } else {
    // int stack = (int) (amount / Integer.MAX_VALUE);
    // int remainder = (int) (amount % Integer.MAX_VALUE);
    // ItemStack t = StellarConstructionFrameMaterial.get(Integer.MAX_VALUE);
    //
    // int i = 0;
    // while (i < stack) {
    // list.add(t.copy());
    // i++;
    // }
    //
    // if (remainder > 0) {
    // list.add(GTUtility.copyAmountUnsafe(remainder, t));
    // }
    // }
    //
    // return list.toArray(new ItemStack[0]);
    // }

    public ItemStack[] getRecoversTST(long amount) {
        if (amount <= 0 || !ModList.TwistSpaceTechnology.isModLoaded()) return new ItemStack[0];

        List<ItemStack> list = new ArrayList<>();

        long fullStacks = amount / Integer.MAX_VALUE;
        int remainder = (int) (amount % Integer.MAX_VALUE);

        for (long i = 0; i < fullStacks; i++) {
            list.add(GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, TST_PROTO));
        }

        if (remainder > 0) {
            list.add(GTUtility.copyAmountUnsafe(remainder, TST_PROTO));
        }

        return list.toArray(new ItemStack[0]);
    }

    // Artificial Star Output multiplier
    public void calculateOutputMultiplier() {
        // tTime^0.25 * tDim^0.25 * 1.588186^(tStabilisation-2)
        // (100^0.25)*(1.588186^(10-2))) = 128.000
        // 1.588186^(-1) = 0.629
        this.outputMultiplier = Math.pow(1d * tierTimeField * tierDimensionField, 0.25d)
            * Math.pow(1.588186d, tierStabilisationField - 2);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (aBaseMetaTileEntity.isServerSide()) {
            this.ownerName = aBaseMetaTileEntity.getOwnerName();
            this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (isRendering && mMaxProgresstime == 0 && rewardContinuous == 0) {
            isRendering = false;
            destroyRenderBlock();
        }
        if (rewardContinuous != 0 && mMaxProgresstime == 0) rewardContinuous = 0;
    }

    @Override
    public void onDisableWorking() {
        if (isRendering) {
            destroyRenderBlock();
        }
        super.onDisableWorking();
    }

    @Override
    public void onRemoval() {
        if (isRendering) {
            destroyRenderBlock();
        }
        super.onRemoval();
    }

    @Override
    public void onBlockDestroyed() {
        if (isRendering) {
            destroyRenderBlock();
        }
        super.onBlockDestroyed();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("storageEU", storageEU);
        aNBT.setInteger("tierDimensionField", tierDimensionField);
        aNBT.setInteger("tierTimeField", tierTimeField);
        aNBT.setInteger("tierStabilisationField", tierStabilisationField);
        aNBT.setDouble("outputMultiplier", outputMultiplier);
        aNBT.setByte("rewardContinuous", rewardContinuous);
        aNBT.setString("currentOutputEU", currentOutputEU.toString());
        aNBT.setBoolean("isRendering", isRendering);
        aNBT.setBoolean("enableRender", enableRender);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        storageEU = aNBT.getLong("storageEU");
        tierDimensionField = aNBT.getInteger("tierDimensionField");
        tierTimeField = aNBT.getInteger("tierTimeField");
        tierStabilisationField = aNBT.getInteger("tierStabilisationField");
        outputMultiplier = aNBT.getDouble("outputMultiplier");
        rewardContinuous = aNBT.getByte("rewardContinuous");
        currentOutputEU = new BigInteger(aNBT.getString("currentOutputEU"));
        isRendering = aNBT.getBoolean("isRendering");
        if (aNBT.hasKey("enableRender")) enableRender = aNBT.getBoolean("enableRender");
    }

    @Override
    public boolean supportsCraftingMEBuffer() {
        return false;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        calculateOutputMultiplier();
        recoveryChance = tierDimensionField * tierTimeField * tierStabilisationField;
        return true;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && tierDimensionField > 0
            && tierTimeField > 0
            && tierStabilisationField > 0
            && mInputBusses.size() == 1;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mInputBusses.clear();
        tierDimensionField = -1;
        tierTimeField = -1;
        tierStabilisationField = -1;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
    public IStructureDefinition<RealArtificialStar> getStructureDefinition() {
        return StructureDefinition.<RealArtificialStar>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.EOH_DILATION.use(
                    StructureUtility.ofBlocksTiered(
                        RealArtificialStar::getTierTimeFieldBlockFromBlock,
                        ImmutableList.of(
                            Pair.of(TTCasingsContainer.sBlockCasingsTT, 14),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 0),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 1),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 2),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 3),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 4),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 5),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 6),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 7),
                            Pair.of(TTCasingsContainer.TimeAccelerationFieldGenerator, 8)),
                        -1,
                        (t, m) -> t.tierTimeField = m,
                        t -> t.tierTimeField)))
            .addElement('B', StructureUtility.ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('C', StructureUtility.ofBlock(Loaders.compactFusionCoil, 4))
            .addElement(
                'D',
                buildHatchAdder(RealArtificialStar.class)
                    .atLeast(HatchElement.Maintenance, HatchElement.InputBus, HatchElement.OutputBus)
                    .adder(RealArtificialStar::addInputBusOrOutputBusToMachineList)
                    .dot(1)
                    .casingIndex(13)
                    .buildAndChain(GregTechAPI.sBlockCasings1, 13))
            .addElement(
                'E',
                GTStructureChannels.EOH_COMPRESSION.use(
                    StructureUtility.ofBlocksTiered(
                        RealArtificialStar::getTierDimensionFieldBlockFromBlock,
                        ImmutableList.of(
                            Pair.of(GregTechAPI.sBlockCasings1, 14),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 0),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 1),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 2),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 3),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 4),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 5),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 6),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 7),
                            Pair.of(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 8)),
                        -1,
                        (t, m) -> t.tierDimensionField = m,
                        t -> t.tierDimensionField)))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 11))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement(
                'H',
                GTStructureChannels.EOH_STABILISATION.use(
                    StructureUtility.ofBlocksTiered(
                        RealArtificialStar::getTierStabilisationFieldBlockFromBlock,
                        ImmutableList.of(
                            Pair.of(TTCasingsContainer.sBlockCasingsTT, 9),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 0),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 1),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 2),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 3),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 4),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 5),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 6),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 7),
                            Pair.of(TTCasingsContainer.StabilisationFieldGenerators, 8)),
                        -1,
                        (t, m) -> t.tierStabilisationField = m,
                        t -> t.tierStabilisationField)))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 0))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 5))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 8))
            .addElement('L', StructureUtility.ofBlock(BlockQuantumGlass.INSTANCE, 0))
            .build();
    }

    @Nullable
    public static Integer getTierDimensionFieldBlockFromBlock(Block block, int meta) {
        if (block == null) return null;
        if (block == GregTechAPI.sBlockCasings1 && 14 == meta) return 1;
        if (block == TTCasingsContainer.SpacetimeCompressionFieldGenerators) return meta + 2;
        return null;
    }

    @Nullable
    public static Integer getTierTimeFieldBlockFromBlock(Block block, int meta) {
        if (block == null) return null;
        if (block == TTCasingsContainer.sBlockCasingsTT && 14 == meta) return 1;
        if (block == TTCasingsContainer.TimeAccelerationFieldGenerator) return meta + 2;
        return null;
    }

    @Nullable
    public static Integer getTierStabilisationFieldBlockFromBlock(Block block, int meta) {
        if (block == null) return null;
        if (block == TTCasingsContainer.sBlockCasingsTT && 9 == meta) return 1;
        if (block == TTCasingsContainer.StabilisationFieldGenerators) return meta + 2;
        return null;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("Tooltip_RealArtificialStar_MachineType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_09"))
            .beginStructureBlock(121, 112, 109, false)
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_02_01"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_02_02"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_02_03"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_02_04"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_02_05"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_02_06"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_01"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_02"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_03"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_04"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_05"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_06"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_07"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_08"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_09"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_10"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_11"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_12"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_13"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStarInfo_14"))
            .addSubChannelUsage(GTStructureChannels.EOH_COMPRESSION)
            .addSubChannelUsage(GTStructureChannels.EOH_DILATION)
            .addSubChannelUsage(GTStructureChannels.EOH_STABILISATION)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) {
                return new ITexture[] { Textures.BlockIcons.casingTexturePages[0][12], TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            }
            return new ITexture[] { Textures.BlockIcons.casingTexturePages[0][12], TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.casingTexturePages[0][12] };
    }

    public void createRenderBlock() {
        int x = getBaseMetaTileEntity().getXCoord();
        int y = getBaseMetaTileEntity().getYCoord();
        int z = getBaseMetaTileEntity().getZCoord();

        double xOffset = 40 * getExtendedFacing().getRelativeBackInWorld().offsetX
            + 36 * getExtendedFacing().getRelativeUpInWorld().offsetX;
        double zOffset = 40 * getExtendedFacing().getRelativeBackInWorld().offsetZ
            + 36 * getExtendedFacing().getRelativeUpInWorld().offsetZ;
        double yOffset = 40 * getExtendedFacing().getRelativeBackInWorld().offsetY
            + 36 * getExtendedFacing().getRelativeUpInWorld().offsetY;

        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock((int) (x + xOffset), (int) (y + yOffset), (int) (z + zOffset), Blocks.air);
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock((int) (x + xOffset), (int) (y + yOffset), (int) (z + zOffset), BlockLoader.artificialStarRender);
    }

    public void destroyRenderBlock() {
        int x = getBaseMetaTileEntity().getXCoord();
        int y = getBaseMetaTileEntity().getYCoord();
        int z = getBaseMetaTileEntity().getZCoord();

        double xOffset = 40 * getExtendedFacing().getRelativeBackInWorld().offsetX
            + 36 * getExtendedFacing().getRelativeUpInWorld().offsetX;
        double zOffset = 40 * getExtendedFacing().getRelativeBackInWorld().offsetZ
            + 36 * getExtendedFacing().getRelativeUpInWorld().offsetZ;
        double yOffset = 40 * getExtendedFacing().getRelativeBackInWorld().offsetY
            + 36 * getExtendedFacing().getRelativeUpInWorld().offsetY;

        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock((int) (x + xOffset), (int) (y + yOffset), (int) (z + zOffset), Blocks.air);
    }

    public boolean addInputBusOrOutputBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return addInputBusToMachineList(aTileEntity, aBaseCasingIndex)
            || addOutputBusToMachineList(aTileEntity, aBaseCasingIndex);
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
}
