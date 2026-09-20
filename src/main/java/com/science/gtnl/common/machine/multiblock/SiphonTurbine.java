package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ExoticDynamo;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.gui.modularui.SiphonTurbineGui;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class SiphonTurbine extends MultiMachineBase<SiphonTurbine> {

    public static final int CYCLE_TICKS = 128;
    public static final int ABSORB_PERCENT = 70;
    public static final long TIER1_LIMIT = 10_000_000L;
    public static final long TIER2_LIMIT = 200_000_000L;
    public static final long TIER3_LIMIT = 2_000_000_000L;
    public static final int TIER1_RATE = 4;
    public static final int TIER2_RATE = 2;
    public static final int TIER3_RATE = 1;
    public static final int TIER4_RATE = 0;
    public static final int OVERFLOW_MULTIPLIER = 2;
    public static final int PUSH_INTERVAL_TICKS = 1;
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String ST_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/siphon_turbine";
    private static final String[][] shape = StructureUtils.readStructureFromFile(ST_STRUCTURE_FILE_PATH);
    public UUID ownerUUID;
    public long euCache;
    public long tickOutput;
    public final long[] outputWindow = new long[CYCLE_TICKS];
    public int outputWindowIndex;
    public int pushCountdown = PUSH_INTERVAL_TICKS;
    public int roundMultiplier = 1;
    public boolean doubleNextRound;

    public SiphonTurbine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SiphonTurbine(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.siphon_turbine.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SiphonTurbine(this.mName);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (aBaseMetaTileEntity.isServerSide()) {
            this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        }
    }

    @Override
    public int getCasingTextureID() {
        return Casings.HeatProofMachineCasing.textureId;
    }

    @Override
    public IStructureDefinition<SiphonTurbine> getStructureDefinition() {
        return StructureDefinition.<SiphonTurbine>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))

            .addElement(
                'A',
                StructureUtility.ofChain(
                    Casings.HeatProofMachineCasing.asElement(),
                    GTStructureUtility.buildHatchAdder(SiphonTurbine.class)
                        .atLeast(HatchElement.Maintenance, HatchElement.Dynamo.or(ExoticDynamo))
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build()))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        if (mDynamoHatches.isEmpty() && mExoticDynamoHatches.isEmpty()) {
            errors.add(StructureErrors.missingHatch(HatchElement.Dynamo));
        }
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
        ITexture casing = Textures.BlockIcons.getCasingTextureForId(getCasingTextureID());
        if (side != aFacing) {
            return new ITexture[] { casing };
        }
        return new ITexture[] { casing, TextureFactory.builder()
            .addIcon(
                aActive ? Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE
                    : Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER)
            .extFacing()
            .build(),
            TextureFactory.builder()
                .addIcon(
                    aActive ? Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE_GLOW
                        : Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_GLOW)
                .extFacing()
                .glow()
                .build() };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return null;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (mDynamoHatches.isEmpty() && mExoticDynamoHatches.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        mMaxProgresstime = CYCLE_TICKS;
        mEUt = 0;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mOutputItems = null;
        mOutputFluids = null;
        return CheckRecipeResultRegistry.GENERATING;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (!aBaseMetaTileEntity.isServerSide()) return;

        if (--pushCountdown <= 0) {
            pushCountdown = PUSH_INTERVAL_TICKS;
            runTurbinePushCycle();
        }

        outputWindow[outputWindowIndex] = tickOutput;
        outputWindowIndex = (outputWindowIndex + 1) % CYCLE_TICKS;
        tickOutput = 0L;
    }

    @Override
    public void finishRecipeProgress(ItemStack controllerSlot) {
        super.finishRecipeProgress(controllerSlot);
        if (getBaseMetaTileEntity() == null || !getBaseMetaTileEntity().isServerSide()) return;
        absorbSteamFromWirelessNetwork();
    }

    public void absorbSteamFromWirelessNetwork() {
        if (ownerUUID == null) {
            IGregTechTileEntity base = getBaseMetaTileEntity();
            if (base == null) return;
            ownerUUID = base.getOwnerUuid();
        }
        if (ownerUUID == null) return;

        euCache = 0L;
        roundMultiplier = doubleNextRound ? OVERFLOW_MULTIPLIER : 1;
        doubleNextRound = false;
        BigInteger network = SteamWirelessNetworkManager.getUserSteam(ownerUUID);
        if (network.signum() <= 0) {
            markDirty();
            return;
        }

        BigInteger absorbed = network.multiply(BigInteger.valueOf(ABSORB_PERCENT))
            .divide(BigInteger.valueOf(100));
        if (absorbed.signum() <= 0) return;
        if (!SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, absorbed.negate())) return;

        addEuToCache(euForAbsorbedSteam(clampToLong(absorbed)) * roundMultiplier);

        if (absorbed.compareTo(BigInteger.valueOf(TIER3_LIMIT)) > 0) {
            doubleNextRound = true;
        }
        markDirty();
    }

    public static long euForAbsorbedSteam(long amount) {
        if (amount <= 0) return 0L;
        long remaining = amount;
        long eu = 0L;
        long tier1 = Math.min(remaining, TIER1_LIMIT);
        eu += tier1 * TIER1_RATE;
        remaining -= tier1;

        long tier2 = Math.min(remaining, TIER2_LIMIT - TIER1_LIMIT);
        eu += tier2 * TIER2_RATE;
        remaining -= tier2;

        long tier3 = Math.min(remaining, TIER3_LIMIT - TIER2_LIMIT);
        eu += tier3 * TIER3_RATE;
        return eu;
    }

    private void addEuToCache(long eu) {
        if (eu <= 0) return;
        euCache = euCache > Long.MAX_VALUE - eu ? Long.MAX_VALUE : euCache + eu;
    }

    private void runTurbinePushCycle() {
        if (euCache <= 0) return;
        long space = getTotalDynamoSpace();
        if (space <= 0) return;

        long give = Math.min(euCache, space);
        long injected = injectEuIntoDynamosDirect(mDynamoHatches, give);
        injected += injectEuIntoDynamosDirect(mExoticDynamoHatches, give - injected);
        if (injected <= 0) return;

        euCache -= injected;
        tickOutput += injected;
        markDirty();
    }

    public long getTotalDynamoSpace() {
        long space = getDynamoSpace(mDynamoHatches);
        for (MTEHatch dynamo : mExoticDynamoHatches) {
            space = saturatingAdd(space, getDynamoSpace(dynamo));
        }
        return space;
    }

    private static long getDynamoSpace(Collection<? extends MTEHatch> dynamos) {
        long space = 0L;
        for (MTEHatch dynamo : dynamos) {
            space = saturatingAdd(space, getDynamoSpace(dynamo));
        }
        return space;
    }

    private static long getDynamoSpace(MTEHatch dynamo) {
        if (dynamo == null || !dynamo.isValid()) return 0L;
        IGregTechTileEntity base = dynamo.getBaseMetaTileEntity();
        if (base == null) return 0L;

        long capacity = base.getEUCapacity();
        long stored = base.getStoredEU();
        if (capacity <= 0 || stored >= capacity) return 0L;
        long space = capacity - stored;
        return Math.max(0L, space);
    }

    private static long saturatingAdd(long a, long b) {
        if (a <= 0L) return Math.max(0L, b);
        if (b <= 0L) return a;
        return a > Long.MAX_VALUE - b ? Long.MAX_VALUE : a + b;
    }

    private long injectEuIntoDynamosDirect(Collection<? extends MTEHatch> dynamos, long amount) {
        long remaining = amount;
        long injected = 0L;
        for (MTEHatch dynamo : dynamos) {
            if (remaining <= 0) return injected;

            long space = getDynamoSpace(dynamo);
            long accepted = Math.min(remaining, space);
            if (accepted <= 0) continue;

            IGregTechTileEntity base = dynamo.getBaseMetaTileEntity();
            if (base == null) continue;
            base.increaseStoredEnergyUnits(accepted, false);
            injected += accepted;
            remaining -= accepted;
        }
        return injected;
    }

    private static long clampToLong(BigInteger value) {
        return value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0 ? Long.MAX_VALUE : value.longValue();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("euCache", euCache);
        aNBT.setInteger("pushCountdown", pushCountdown);
        aNBT.setInteger("roundMultiplier", roundMultiplier);
        aNBT.setBoolean("doubleNextRound", doubleNextRound);
        if (ownerUUID != null) aNBT.setString("OwnerUUID", ownerUUID.toString());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        euCache = aNBT.getLong("euCache");
        pushCountdown = Math.max(1, aNBT.getInteger("pushCountdown"));
        roundMultiplier = Math.max(1, aNBT.getInteger("roundMultiplier"));
        doubleNextRound = aNBT.getBoolean("doubleNextRound");
        if (aNBT.hasKey("OwnerUUID")) {
            try {
                ownerUUID = UUID.fromString(aNBT.getString("OwnerUUID"));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public String getEuCacheForGui() {
        return formatAmount(BigInteger.valueOf(euCache));
    }

    public String getOutputLastCycleForGui() {
        return formatAmount(BigInteger.valueOf(getOutputInLastCycle()));
    }

    public long getOutputInLastCycle() {
        long sum = 0L;
        for (long value : outputWindow) sum += value;
        return sum;
    }

    public void setInfoFromGui(String ignored) {}

    private static String formatAmount(BigInteger value) {
        if (value == null) return "0";
        if (value.abs()
            .compareTo(BigInteger.valueOf(1_000_000L)) >= 0) {
            return GTUtility.scientificFormat(value);
        }
        return NumberFormatUtil.formatNumber(value);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SiphonTurbineGui(this);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        currenttip.add(
            StatCollector.translateToLocalFormatted(
                "gtnl.machine.siphon_turbine.gui.eu_cache",
                formatAmount(BigInteger.valueOf(tag.getLong("euCache")))));
        currenttip.add(
            StatCollector.translateToLocalFormatted(
                "gtnl.machine.siphon_turbine.gui.output",
                formatAmount(BigInteger.valueOf(tag.getLong("outputLastCycle")))));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setLong("euCache", euCache);
        tag.setLong("outputLastCycle", getOutputInLastCycle());
    }

    @Override
    public int getPollutionPerSecond(ItemStack aStack) {
        return 0;
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.siphon_turbine.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.siphon_turbine.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.siphon_turbine.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.siphon_turbine.tooltip.2"))
            .beginStructureBlock(3, 3, 3, false)
            .addDynamoHatch("1+", StatCollector.translateToLocal("gtnl.machine.siphon_turbine.casing"))
            .addMaintenanceHatch("0-1", StatCollector.translateToLocal("gtnl.machine.siphon_turbine.casing"))
            .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.siphon_turbine.structure"))
            .toolTipFinisher();
        return tt;
    }
}
