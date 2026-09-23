package com.science.gtnl.common.machine.multiblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.gui.modularui.NuclearReactorGui;
import com.science.gtnl.common.machine.hatch.NuclearFluidHatch;
import com.science.gtnl.common.machine.hatch.NuclearItemBus;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.enums.GTNLStructureChannels;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.INEIPreviewModifier;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.ItemRadioactiveCell;
import gregtech.api.items.ItemRadioactiveCellIC;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.PositionedStructureError;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import ic2.api.item.ICustomDamageItem;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class NuclearReactor extends MultiMachineBase<NuclearReactor>
    implements ISurvivalConstructable, INEIPreviewModifier {

    public static final double HEAT_PER_EU = 0.1;
    public static final int STEAM_PER_HU = 160;
    public static final int WATER_PER_HU = 1;
    public static final double PLATE_MELT_HEAT = 240_000;
    public static final int PLATE_MELT_AMOUNT = 1008;
    public static final double EXCITATION_ORTHOGONAL = 1.5;
    public static final double EXCITATION_DIAGONAL = 1.2;
    public static final double EXCITATION_THROUGH_FLUID = 1.1;
    public static final double DURABILITY_PER_SECOND_RUNNING = 4.0;
    public static final double DURABILITY_PER_SECOND_IDLE = 1.0;
    public static final double REFLECTOR_DURABILITY_FACTOR = 0.125;

    public static final double STARTUP_RAMP_MINUTES = 60.0;

    public static final double STARTUP_EFFICIENCY_START = 0.60;

    public static final double STARTUP_EFFICIENCY_MAX = 1.10;
    public static final int STRUCTURE_HEIGHT = 5;
    public static final int MAX_CORE = 7;
    public static final int MAX_TIER = 3;
    private static final int[] TIER_CORE = { 3, 5, 7 };
    private static final int[] TIER_FOOTPRINT = { 5, 7, 9 };
    private static final int[] TIER_CUT = { 0, 1, 2 };
    private static final int[][] ORTHOGONAL_DIRECTIONS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    private static final String CELL_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzDEFGHIJKLMNOPQRSTUVWYZ";

    private static List<String> coreCellBlocks() {
        return Arrays.asList(
            "gtnl.hatch.nuclear_item_bus.name",
            "gtnl.hatch.nuclear_fluid_hatch.name",
            Casings.HastelloyNSealantBlock.toStack(1)
                .getUnlocalizedName() + ".name");
    }

    private final IMetaTileEntity[][] mCoreCells = new IMetaTileEntity[MAX_CORE][MAX_CORE];
    private final int[][] mCoreCellPos = new int[MAX_CORE * MAX_CORE][];
    private int mReactorTier = 0;
    private double[][] mHeatBuffer;
    private double[][] mPlateHeat;

    private long mStartupTicks;
    private double[] mDurabilityBuffer;
    private long mSteamOutputPerSecond = 0;
    private long mSteamAccumulator = 0;

    public NuclearReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public NuclearReactor(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NuclearReactor(this.mName);
    }

    public int getReactorTier() {
        return mReactorTier;
    }

    private static int clampTier(int tier) {
        return Math.max(1, Math.min(MAX_TIER, tier));
    }

    public int getEffectiveTier() {
        return clampTier(mReactorTier);
    }

    public static int getCoreSizeForTier(int tier) {
        return TIER_CORE[clampTier(tier) - 1];
    }

    public static int getCornerCutForTier(int tier) {
        return TIER_CUT[clampTier(tier) - 1];
    }

    public int getCoreSize() {
        return getCoreSizeForTier(getEffectiveTier());
    }

    public int getCornerCut() {
        return getCornerCutForTier(getEffectiveTier());
    }

    public int getReactorTierForGui() {
        return mReactorTier;
    }

    public void setReactorTierFromGui(int tier) {}

    public int getStructureFootprint() {
        return TIER_FOOTPRINT[getEffectiveTier() - 1];
    }

    public int getStructureHeight() {
        return STRUCTURE_HEIGHT;
    }

    public int getHorizontalOffset() {
        return getStructureFootprint() / 2;
    }

    public int getVerticalOffset() {
        return getStructureHeight() / 2;
    }

    public int getDepthOffset() {
        return 0;
    }

    public String getLocalNameKey() {
        return switch (mReactorTier) {
            case 2 -> "gtnl.machine.nuclear_reactor_t2.name";
            case 3 -> "gtnl.machine.nuclear_reactor_t3.name";
            default -> "gtnl.machine.nuclear_reactor.name";
        };
    }

    public String getTooltipKey() {
        return switch (mReactorTier) {
            case 2 -> "gtnl.machine.nuclear_reactor_t2.tooltip.0";
            case 3 -> "gtnl.machine.nuclear_reactor_t3.tooltip.0";
            default -> "gtnl.machine.nuclear_reactor.tooltip.0";
        };
    }

    public static boolean isValidCoreCellForTier(int tier, int row, int col) {
        int n = getCoreSizeForTier(tier);
        if (row < 0 || row >= n || col < 0 || col >= n) return false;
        int cut = getCornerCutForTier(tier);
        if (cut <= 0) return true;
        int last = n - 1;
        if (row + col < cut) return false;
        if (last - row + col < cut) return false;
        if (row + last - col < cut) return false;
        if (last - row + last - col < cut) return false;
        return true;
    }

    public boolean isValidCoreCell(int row, int col) {
        return isValidCoreCellForTier(getEffectiveTier(), row, col);
    }

    public static int getCoreCellCountForTier(int tier) {
        int count = 0;
        int n = getCoreSizeForTier(tier);
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (isValidCoreCellForTier(tier, r, c)) count++;
            }
        }
        return count;
    }

    public int getCoreCellCount() {
        return getCoreCellCountForTier(getEffectiveTier());
    }

    public int getCoreRow(int slot) {
        int index = 0;
        for (int r = 0; r < getCoreSize(); r++) {
            for (int c = 0; c < getCoreSize(); c++) {
                if (!isValidCoreCell(r, c)) continue;
                if (index == slot) return r;
                index++;
            }
        }
        return -1;
    }

    public int getCoreCol(int slot) {
        int index = 0;
        for (int r = 0; r < getCoreSize(); r++) {
            for (int c = 0; c < getCoreSize(); c++) {
                if (!isValidCoreCell(r, c)) continue;
                if (index == slot) return c;
                index++;
            }
        }
        return -1;
    }

    public static int getCoreSlotIndexForTier(int tier, int row, int col) {
        if (!isValidCoreCellForTier(tier, row, col)) return -1;
        int index = 0;
        int n = getCoreSizeForTier(tier);
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (!isValidCoreCellForTier(tier, r, c)) continue;
                if (r == row && c == col) return index;
                index++;
            }
        }
        return -1;
    }

    public int getCoreSlotIndex(int row, int col) {
        return getCoreSlotIndexForTier(getEffectiveTier(), row, col);
    }

    protected char cellChar(int row, int col) {
        return CELL_CHARS.charAt(row * MAX_CORE + col);
    }

    public IMetaTileEntity getCoreCell(int slot) {
        int row = getCoreRow(slot);
        int col = getCoreCol(slot);
        if (row < 0 || col < 0) return null;
        return mCoreCells[row][col];
    }

    public IMetaTileEntity getCoreCellAt(int row, int col) {
        if (row < 0 || row >= MAX_CORE || col < 0 || col >= MAX_CORE) return null;
        return mCoreCells[row][col];
    }

    @Override
    public int getCasingTextureID() {
        return Casings.HastelloyNSealantBlock.textureId;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        for (int r = 0; r < MAX_CORE; r++) {
            for (int c = 0; c < MAX_CORE; c++) {
                mCoreCells[r][c] = null;
                mCoreCellPos[r * MAX_CORE + c] = null;
            }
        }
    }

    private static String pieceName(int tier) {
        return tier <= 1 ? "main" : "tier" + tier;
    }

    private static int footprintOf(int tier) {
        return TIER_FOOTPRINT[Math.max(0, Math.min(MAX_TIER, tier) - 1)];
    }

    protected String[][] buildShape(int tier) {
        int core = TIER_CORE[tier - 1];
        int w = TIER_FOOTPRINT[tier - 1];
        int h = STRUCTURE_HEIGHT;
        int interior = w - 2;
        int centerCol = w / 2;
        int centerRow = h / 2;
        int coreOffset = (interior - core) / 2;
        int cut = TIER_CUT[tier - 1];
        int pipeRadius = (core - 2) / 2;
        String[] coreRows = new String[core];
        for (int r = 0; r < core; r++) {
            StringBuilder sb = new StringBuilder(w);
            sb.append('X');
            for (int i = 0; i < interior; i++) {
                int c = i - coreOffset;
                boolean isCore = c >= 0 && c < core && isCoreCellFor(core, cut, r, c);
                sb.append(isCore ? cellChar(r, c) : 'X');
            }
            sb.append('X');
            coreRows[r] = sb.toString();
        }

        StringBuilder emptyTop = new StringBuilder(w);
        emptyTop.append('X');
        for (int i = 0; i < interior; i++) emptyTop.append('X');
        emptyTop.append('X');
        String fixedTopRow = emptyTop.toString();
        String[][] shape = new String[w][h];
        for (int d = 0; d < w; d++) {
            boolean front = d == 0;
            boolean back = d == w - 1;
            for (int row = 0; row < h; row++) {
                String line;
                if (front || back) {
                    StringBuilder sb = new StringBuilder(w);
                    sb.append(' ');
                    for (int i = 0; i < interior; i++) sb.append(row == 0 ? 'X' : 'B');
                    sb.append(' ');
                    line = sb.toString();
                    if (front && row == centerRow) {
                        line = line.substring(0, centerCol) + '~' + line.substring(centerCol + 1);
                    }
                } else if (row == 0) {
                    int r = (d - 1) - coreOffset;
                    line = (r >= 0 && r < core) ? coreRows[r] : fixedTopRow;
                } else if (row == h - 1) {
                    StringBuilder sb = new StringBuilder(w);
                    for (int i = 0; i < w; i++) sb.append('B');
                    line = sb.toString();
                } else {
                    StringBuilder sb = new StringBuilder(w);
                    sb.append('B');
                    for (int i = 1; i < w - 1; i++) {
                        boolean pipe = Math.abs(d - centerCol) <= pipeRadius && Math.abs(i - centerCol) <= pipeRadius;
                        sb.append(pipe ? 'A' : 'C');
                    }
                    sb.append('B');
                    line = sb.toString();
                }
                shape[d][row] = line;
            }
        }
        return shape;
    }

    private static boolean isCoreCellFor(int core, int cut, int row, int col) {
        int last = core - 1;
        if (cut > 0
            && (row + col < cut || last - row + col < cut || row + last - col < cut || last - row + last - col < cut)) {
            return false;
        }
        return true;
    }

    @Override
    public IStructureDefinition<NuclearReactor> getStructureDefinition() {
        StructureDefinition.Builder<NuclearReactor> builder = StructureDefinition.<NuclearReactor>builder()

            .addElement('X', Casings.HastelloyNSealantBlock.asElement())

            .addElement('A', replaceableCasing(Casings.InsulatedFluidPipeCasing.asElement()))
            .addElement('B', replaceableCasing(Casings.HastelloyNSealantBlock.asElement()))
            .addElement('C', replaceableCasing(Casings.HastelloyXStructuralBlock.asElement()));

        for (int r = 0; r < MAX_CORE; r++) {
            for (int c = 0; c < MAX_CORE; c++) {
                builder.addElement(cellChar(r, c), coreCellWithHint(r, c));
            }
        }

        for (int tier = 1; tier <= MAX_TIER; tier++) {
            builder.addShape(pieceName(tier), buildShape(tier));
        }
        return builder.build();
    }

    protected IStructureElement<NuclearReactor> replaceableCasing(IStructureElement<NuclearReactor> casing) {
        return StructureUtility.ofChain(
            casing,
            GTStructureUtility.buildHatchAdder(NuclearReactor.class)
                .atLeast(HatchElement.OutputHatch)
                .casingIndex(getCasingTextureID())
                .hint(1)
                .build());
    }

    protected IStructureElement<NuclearReactor> coreCellWithHint(int row, int col) {
        return StructureUtility.ofChain(coreCell(row, col), coreCellHintElement());
    }

    private IStructureElement<NuclearReactor> coreCellHintElement() {
        return GTStructureUtility.buildHatchAdder(NuclearReactor.class)
            .hatchClasses(NuclearItemBus.class, NuclearFluidHatch.class)
            .casingIndex(Casings.HastelloyNSealantBlock.textureId)
            .hint(2)
            .hint(() -> StatCollector.translateToLocal("gtnl.structure.nuclear_reactor.core_cell"))
            .adder((t, te, casingIndex) -> t.registerCoreCellByCoords(te, casingIndex))
            .build();
    }

    public boolean registerCoreCellByCoords(IGregTechTileEntity aTileEntity, Short aCasingIndex) {
        if (aTileEntity == null) return false;
        for (int r = 0; r < MAX_CORE; r++) {
            for (int c = 0; c < MAX_CORE; c++) {
                int[] pos = mCoreCellPos[r * MAX_CORE + c];
                if (pos != null && pos[0] == aTileEntity.getXCoord()
                    && pos[1] == aTileEntity.getYCoord()
                    && pos[2] == aTileEntity.getZCoord()) {
                    return addCoreCellToMachineList(r, c, aTileEntity, aCasingIndex);
                }
            }
        }
        return false;
    }

    protected IStructureElement<NuclearReactor> coreCell(int row, int col) {
        return new IStructureElement<NuclearReactor>() {

            @Override
            public boolean check(NuclearReactor t, World world, int x, int y, int z) {
                if (isCoreCasing(world, x, y, z)) return true;
                return t.tryRegisterCoreCell(row, col, world, x, y, z);
            }

            @Override
            public boolean couldBeValid(NuclearReactor t, World world, int x, int y, int z, ItemStack trigger) {
                return true;
            }

            @Override
            public boolean spawnHint(NuclearReactor t, World world, int x, int y, int z, ItemStack trigger) {
                if (isCoreCasing(world, x, y, z) || isChamberAt(world, x, y, z)) return true;
                StructureLibAPI.hintParticle(
                    world,
                    x,
                    y,
                    z,
                    Casings.HastelloyNSealantBlock.getBlock(),
                    Casings.HastelloyNSealantBlock.getBlockMeta());
                return true;
            }

            @Override
            public List<String> getDescription(NuclearReactor t) {
                return coreCellBlocks();
            }

            @Override
            public BlocksToPlace getBlocksToPlace(NuclearReactor t, World world, int x, int y, int z, ItemStack trigger,
                AutoPlaceEnvironment env) {
                return BlocksToPlace.create(Casings.HastelloyNSealantBlock.toStack(1));
            }

            @Override
            public boolean placeBlock(NuclearReactor t, World world, int x, int y, int z, ItemStack trigger) {
                if (isPreviewWorld(world)) return placeCasing(world, x, y, z);
                return isCoreCasing(world, x, y, z) || isChamberAt(world, x, y, z);
            }

            @Override
            public PlaceResult survivalPlaceBlock(NuclearReactor t, World world, int x, int y, int z, ItemStack trigger,
                AutoPlaceEnvironment env) {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof IGregTechTileEntity gtTile && isChamber(gtTile.getMetaTileEntity())) {
                    return PlaceResult.SKIP;
                }
                if (isCoreCasing(world, x, y, z)) return PlaceResult.SKIP;

                return StructureUtility.survivalPlaceBlock(
                    Casings.HastelloyNSealantBlock.getBlock(),
                    Casings.HastelloyNSealantBlock.getBlockMeta(),
                    world,
                    x,
                    y,
                    z,
                    env.getSource(),
                    env.getActor(),
                    env.getChatter());
            }
        };
    }

    public static boolean isChamber(IMetaTileEntity meta) {
        return meta instanceof NuclearItemBus || meta instanceof NuclearFluidHatch;
    }

    public static boolean isPreviewWorld(World world) {
        if (world == null) return false;
        for (Class<?> c = world.getClass(); c != null; c = c.getSuperclass()) {
            if (c.getName()
                .startsWith("blockrenderer6343.client.world.")) return true;
        }
        return false;
    }

    public static boolean isChamberAt(World world, int x, int y, int z) {
        if (world == null) return false;
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IGregTechTileEntity gtTile && isChamber(gtTile.getMetaTileEntity());
    }

    public static boolean isCoreCasing(World world, int x, int y, int z) {
        if (world == null) return false;
        Block block = Casings.HastelloyNSealantBlock.getBlock();
        return block != null && world.getBlock(x, y, z) == block
            && world.getBlockMetadata(x, y, z) == Casings.HastelloyNSealantBlock.getBlockMeta();
    }

    public static boolean placeCasing(World world, int x, int y, int z) {
        Block block = Casings.HastelloyNSealantBlock.getBlock();
        if (block == null) return false;
        world.setBlock(x, y, z, block, Casings.HastelloyNSealantBlock.getBlockMeta(), 3);
        return true;
    }

    public boolean tryRegisterCoreCell(int row, int col, World world, int x, int y, int z) {
        if (row < 0 || row >= MAX_CORE || col < 0 || col >= MAX_CORE) return false;

        mCoreCellPos[row * MAX_CORE + col] = new int[] { x, y, z };
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof IGregTechTileEntity gtTile)) return false;
        IMetaTileEntity meta = gtTile.getMetaTileEntity();
        if (!(meta instanceof NuclearItemBus) && !(meta instanceof NuclearFluidHatch)) return false;
        if (meta instanceof MTEHatch hatch) {
            hatch.updateTexture(getCasingTextureID());
        }
        mCoreCells[row][col] = meta;
        return true;
    }

    public boolean addCoreCellToMachineList(int row, int col, IGregTechTileEntity aTileEntity, Short aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        if (row < 0 || row >= MAX_CORE || col < 0 || col >= MAX_CORE) return false;
        IMetaTileEntity meta = aTileEntity.getMetaTileEntity();
        if (!(meta instanceof NuclearItemBus) && !(meta instanceof NuclearFluidHatch)) return false;
        if (mCoreCells[row][col] != null) return false;
        if (aBaseCasingIndex != null && meta instanceof MTEHatch hatch) {
            hatch.updateTexture(aBaseCasingIndex);
        }
        mCoreCells[row][col] = meta;
        return true;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        clearHatches();
        mReactorTier = 0;
        List<StructureError> probeErrors = new ArrayList<>();
        for (int tier = MAX_TIER; tier >= 1; tier--) {
            int w = footprintOf(tier);
            clearHatches();
            if (checkPiece(pieceName(tier), w / 2, STRUCTURE_HEIGHT / 2, 0, probeErrors)) {
                mReactorTier = tier;
                break;
            }
            probeErrors.clear();
        }
        if (mReactorTier == 0) {
            mStartupTicks = 0;
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
            return;
        }

        setupParameters();
        checkHatch(errors);
        checkHatchMin(errors, HatchElement.OutputHatch, 1);

        int itemBusCount = 0;
        int fluidHatchCount = 0;
        for (int slot = 0; slot < getCoreCellCount(); slot++) {
            IMetaTileEntity cell = getCoreCell(slot);
            if (cell instanceof NuclearItemBus) itemBusCount++;
            else if (cell instanceof NuclearFluidHatch) fluidHatchCount++;
        }
        if (itemBusCount == 0) addMissingCoreChamberError(errors, "gtnl.hatch.nuclear_item_bus.name");
        if (fluidHatchCount == 0) addMissingCoreChamberError(errors, "gtnl.hatch.nuclear_fluid_hatch.name");
    }

    private void addMissingCoreChamberError(List<StructureError> errors, String langKey) {
        for (int slot = 0; slot < getCoreCellCount(); slot++) {
            int[] pos = mCoreCellPos[slot];
            if (pos == null) continue;
            errors.add(new PositionedStructureError(pos[0], pos[1], pos[2], Collections.singletonList(langKey)));
            return;
        }
        errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    protected int resolvePreviewTier(ItemStack trigger) {
        if (trigger == null) return 1;

        if (GTNLStructureChannels.NUCLEAR_REACTOR_TIER.hasValue(trigger)) {
            int channelTier = GTNLStructureChannels.NUCLEAR_REACTOR_TIER.getValue(trigger);
            return Math.max(1, Math.min(MAX_TIER, channelTier));
        }
        return Math.max(1, Math.min(MAX_TIER, trigger.stackSize));
    }

    @Override
    public void onPreviewConstruct(ItemStack trigger) {
        if (!GTNLStructureChannels.NUCLEAR_REACTOR_TIER.hasValue(trigger)) {
            trigger.stackSize = 1;
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        int tier = resolvePreviewTier(stackSize);
        buildPiece(pieceName(tier), stackSize, hintsOnly, footprintOf(tier) / 2, STRUCTURE_HEIGHT / 2, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;

        int tier = resolvePreviewTier(stackSize);
        return survivalBuildPiece(
            pieceName(tier),
            stackSize,
            footprintOf(tier) / 2,
            STRUCTURE_HEIGHT / 2,
            0,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return null;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        ITexture casing = Textures.BlockIcons.getCasingTextureForId(getCasingTextureID());
        if (side != aFacing) {
            return new ITexture[] { casing };
        }

        String path = "basicmachines/assembler/OVERLAY_FRONT" + (aActive ? "_ACTIVE" : "");
        return new ITexture[] { casing,
            TextureFactory
                .of(TextureFactory.of(Textures.BlockIcons.customOptional(Mods.GregTech.resourceDomain, path))),
            TextureFactory.builder()
                .addIcon(Textures.BlockIcons.customOptional(Mods.GregTech.resourceDomain, path + "_GLOW"))
                .glow()
                .build() };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();

        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.tooltip.quote"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.tooltip.5"))
            .beginStructureBlock(5, STRUCTURE_HEIGHT, 5, true)
            .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.structure.tiers"))
            .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.structure.output_hatch"))
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.casing"))

            .addOtherStructurePart(
                StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.tooltip.4"),
                StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.tooltip.3"),
                2)
            .addSubChannelUsage(GTNLStructureChannels.NUCLEAR_REACTOR_TIER)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean supportsPowerPanel() {
        return false;
    }

    @Override
    public boolean showRecipeTextInGUI() {
        return false;
    }

    @Override
    public int getRepairStatus() {
        return getIdealStatus();
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public int getPollutionPerSecond(ItemStack aStack) {
        return 0;
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        return CheckRecipeResultRegistry.NONE;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (!aBaseMetaTileEntity.isServerSide()) return;
        if (!mMachine) return;
        runReactorTick(aBaseMetaTileEntity.isAllowedToWork(), aTick);
    }

    private static float sNuclearEnergyMultiplier = -1;

    private static float nuclearEnergyMultiplier() {
        if (sNuclearEnergyMultiplier < 0) {
            sNuclearEnergyMultiplier = 25.0f
                * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/nuclear");
        }
        return sNuclearEnergyMultiplier;
    }

    public double getStartupEfficiency() {
        double minutes = mStartupTicks / 1200.0;
        if (minutes >= STARTUP_RAMP_MINUTES) return STARTUP_EFFICIENCY_MAX;
        double span = STARTUP_EFFICIENCY_MAX - STARTUP_EFFICIENCY_START;
        return STARTUP_EFFICIENCY_START + span * Math.sin(minutes * Math.PI / (2.0 * STARTUP_RAMP_MINUTES));
    }

    public long getStartupTicks() {
        return mStartupTicks;
    }

    public double getRodBaseGeneration(ItemStack rod) {
        if (rod == null || !(rod.getItem() instanceof ItemRadioactiveCellIC cell)) return 0;
        int cells = cell.numberOfCells;
        int pulses = 1 + cells / 2;
        return (double) cell.sEnergy * cells * pulses * nuclearEnergyMultiplier();
    }

    public double getRodHeatPerDirection(ItemStack rod) {
        return getRodBaseGeneration(rod) * HEAT_PER_EU;
    }

    public double getRodTotalHeat(ItemStack rod) {
        return getRodHeatPerDirection(rod) * 6.0;
    }

    public double getRodHeat(ItemStack rod, boolean orthogonal) {
        double perDirection = getRodHeatPerDirection(rod);
        return orthogonal ? perDirection : perDirection / 2.0;
    }

    public static double getExcitationMultiplier(int orthogonalRods, int diagonalRods, int throughFluidRods) {
        return Math.pow(EXCITATION_ORTHOGONAL, Math.max(0, orthogonalRods))
            * Math.pow(EXCITATION_DIAGONAL, Math.max(0, diagonalRods))
            * Math.pow(EXCITATION_THROUGH_FLUID, Math.max(0, throughFluidRods));
    }

    private boolean[][] buildFuelRodGrid() {
        int n = getCoreSize();
        boolean[][] hasRod = new boolean[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (!(getCoreCellAt(row, col) instanceof NuclearItemBus bus)) continue;
                hasRod[row][col] = getRodHeatPerDirection(bus.getWorkingFuelStack()) > 0;
            }
        }
        return hasRod;
    }

    private boolean[][] buildReflectorGrid() {
        int n = getCoreSize();
        boolean[][] hasReflector = new boolean[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (!(getCoreCellAt(row, col) instanceof NuclearItemBus bus)) continue;
                hasReflector[row][col] = NuclearItemBus.isNeutronReflector(bus.getWorkingFuelStack());
            }
        }
        return hasReflector;
    }

    private double getExcitationAt(boolean[][] hasRod, boolean[][] hasReflector, int row, int col) {
        int n = getCoreSize();
        int orthogonalRods = 0;
        int diagonalRods = 0;
        int throughFluidRods = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = row + dr;
                int nc = col + dc;
                if (!isValidCoreCell(nr, nc) || nr < 0 || nr >= n || nc < 0 || nc >= n) continue;
                if (dr == 0 || dc == 0) {
                    if (hasRod[nr][nc] || hasReflector[nr][nc]) orthogonalRods++;
                } else if (hasRod[nr][nc]) {
                    diagonalRods++;
                }
            }
        }

        for (int[] dir : ORTHOGONAL_DIRECTIONS) {
            int mr = row + dir[0];
            int mc = col + dir[1];
            int fr = row + 2 * dir[0];
            int fc = col + 2 * dir[1];
            if (!isValidCoreCell(mr, mc) || !isValidCoreCell(fr, fc)) continue;
            if (fr < 0 || fr >= n || fc < 0 || fc >= n) continue;
            if (!(getCoreCellAt(mr, mc) instanceof NuclearFluidHatch)) continue;
            if (hasRod[fr][fc]) throughFluidRods++;
        }
        return getExcitationMultiplier(orthogonalRods, diagonalRods, throughFluidRods);
    }

    private static final int[][] PLATE_TRANSFER_DIRECTIONS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

    private void transferWithHeatPlates(double[][] heat) {
        int n = getCoreSize();
        if (mPlateHeat == null || mPlateHeat.length != n) mPlateHeat = new double[n][n];
        double[][] transfer = new double[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (!(getCoreCellAt(row, col) instanceof NuclearItemBus bus)) continue;
                if (!NuclearItemBus.isHeatPlate(bus.getWorkingFuelStack())) continue;
                double hu = heat[row][col];
                if (hu <= 0) continue;
                int[] pos = mCoreCellPos[row * MAX_CORE + col];
                if (pos == null) continue;

                mPlateHeat[row][col] += hu;
                if (mPlateHeat[row][col] >= PLATE_MELT_HEAT) {
                    mPlateHeat[row][col] = 0;
                    bus.setInventorySlotContents(NuclearItemBus.SLOT_WORK, null);
                    addFluidOutputs(new FluidStack[] { Materials.Invar.getMolten(PLATE_MELT_AMOUNT) });
                    continue;
                }
                for (int[] dir : PLATE_TRANSFER_DIRECTIONS) {
                    int targetRow = -1;
                    int targetCol = -1;
                    for (int r = 0; r < n && targetRow < 0; r++) {
                        for (int c = 0; c < n; c++) {
                            int[] p = mCoreCellPos[r * MAX_CORE + c];
                            if (p != null && p[0] == pos[0] + dir[0] && p[1] == pos[1] && p[2] == pos[2] + dir[1]) {
                                targetRow = r;
                                targetCol = c;
                                break;
                            }
                        }
                    }
                    if (targetRow < 0) continue;
                    transfer[targetRow][targetCol] += hu;
                }
            }
        }
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (transfer[row][col] > 0) heat[row][col] += transfer[row][col];
            }
        }
    }

    public void runReactorTick(boolean running, long aTick) {
        if (aTick % 20 == 0) {
            mSteamOutputPerSecond = mSteamAccumulator;
            mSteamAccumulator = 0;
            tickFuelRods(running);
        }

        if (!running) {
            mStartupTicks = 0;
            return;
        }

        mStartupTicks++;
        double efficiency = getStartupEfficiency();

        int n = getCoreSize();
        double[][] heat = new double[n][n];
        boolean[][] hasRod = buildFuelRodGrid();
        boolean[][] hasReflector = buildReflectorGrid();

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (!hasRod[row][col]) continue;
                if (!(getCoreCellAt(row, col) instanceof NuclearItemBus bus)) continue;
                ItemStack rod = bus.getWorkingFuelStack();
                double excitation = getExcitationAt(hasRod, hasReflector, row, col);

                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;
                        int nr = row + dr;
                        int nc = col + dc;
                        if (!isValidCoreCell(nr, nc)) continue;
                        boolean orthogonal = (dr == 0 || dc == 0);
                        heat[nr][nc] += getRodHeat(rod, orthogonal) * excitation * efficiency;
                    }
                }
            }
        }

        transferWithHeatPlates(heat);

        if (mHeatBuffer == null || mHeatBuffer.length != n) mHeatBuffer = new double[n][n];
        long steamTotal = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                double hu = heat[row][col];
                if (hu <= 0) continue;
                if (!(getCoreCellAt(row, col) instanceof NuclearFluidHatch hatch)) continue;
                FluidStack water = hatch.getInputFluid();
                if (water == null || water.getFluid() != FluidRegistry.WATER) continue;
                mHeatBuffer[row][col] += hu;

                int usableHu = (int) Math.min(mHeatBuffer[row][col], water.amount / (double) WATER_PER_HU);
                if (usableHu <= 0) continue;
                mHeatBuffer[row][col] -= usableHu;
                hatch.consumeInputFluid(usableHu * WATER_PER_HU, true);
                steamTotal += (long) usableHu * STEAM_PER_HU;
            }
        }

        if (steamTotal > 0) {
            addFluidOutputs(new FluidStack[] { Materials.Steam.getGas((int) Math.min(steamTotal, Integer.MAX_VALUE)) });
            mSteamAccumulator += steamTotal;
        }
    }

    public void tickFuelRods(boolean running) {
        double baseConsumption = running ? DURABILITY_PER_SECOND_RUNNING : DURABILITY_PER_SECOND_IDLE;

        for (int slot = 0; slot < getCoreCellCount(); slot++) {
            if (!(getCoreCell(slot) instanceof NuclearItemBus bus)) continue;
            if (bus.getStackInSlot(NuclearItemBus.SLOT_WORK) != null) continue;
            ItemStack input = bus.getStackInSlot(NuclearItemBus.SLOT_INPUT);
            if (input != null && bus.isAcceptableItem(input)) {
                ItemStack one = input.copy();
                one.stackSize = 1;
                bus.setInventorySlotContents(NuclearItemBus.SLOT_WORK, one);
                input.stackSize--;
                if (input.stackSize <= 0) bus.setInventorySlotContents(NuclearItemBus.SLOT_INPUT, null);
            }
        }

        boolean[][] hasRod = buildFuelRodGrid();
        boolean[][] hasReflector = buildReflectorGrid();
        int cellCount = getCoreCellCount();
        if (mDurabilityBuffer == null || mDurabilityBuffer.length != cellCount) {
            mDurabilityBuffer = new double[cellCount];
        }
        for (int slot = 0; slot < cellCount; slot++) {
            if (!(getCoreCell(slot) instanceof NuclearItemBus bus)) continue;
            ItemStack rod = bus.getStackInSlot(NuclearItemBus.SLOT_WORK);
            if (rod == null || !(rod.getItem() instanceof ItemRadioactiveCellIC cell)) continue;

            double excitation = getExcitationAt(hasRod, hasReflector, getCoreRow(slot), getCoreCol(slot));
            mDurabilityBuffer[slot] += baseConsumption * excitation * excitation;
            int consumption = mDurabilityBuffer[slot] > 0 ? (int) Math.ceil(mDurabilityBuffer[slot]) : 0;
            if (consumption <= 0) continue;
            mDurabilityBuffer[slot] -= consumption;

            int current = ItemRadioactiveCell.getDurabilityOfStack(rod);
            int max = cell.getMaxDamageEx();
            if (current + consumption >= max) {
                ItemStack depleted = cell.sDepleted == null ? null : cell.sDepleted.copy();
                bus.setInventorySlotContents(NuclearItemBus.SLOT_WORK, null);
                if (depleted != null) {
                    depleted.stackSize = 1;
                    moveDepletedToOutput(bus, depleted);
                }
            } else {
                cell.damageItemStack(rod, consumption);
            }
        }

        for (int slot = 0; slot < cellCount; slot++) {
            if (!(getCoreCell(slot) instanceof NuclearItemBus bus)) continue;
            ItemStack reflector = bus.getStackInSlot(NuclearItemBus.SLOT_WORK);
            if (!NuclearItemBus.isNeutronReflector(reflector)) continue;

            int maxDamage = getReflectorMaxDamage(reflector);
            if (maxDamage <= 0) continue;

            if (!hasOrthogonalRod(hasRod, getCoreRow(slot), getCoreCol(slot))) continue;

            double excitation = getExcitationAt(hasRod, hasReflector, getCoreRow(slot), getCoreCol(slot));
            mDurabilityBuffer[slot] += baseConsumption * REFLECTOR_DURABILITY_FACTOR * excitation * excitation;
            int consumption = mDurabilityBuffer[slot] > 0 ? (int) Math.ceil(mDurabilityBuffer[slot]) : 0;
            if (consumption <= 0) continue;
            mDurabilityBuffer[slot] -= consumption;

            int damage = getReflectorDamage(reflector) + consumption;
            if (damage >= maxDamage) {
                bus.setInventorySlotContents(NuclearItemBus.SLOT_WORK, null);
            } else {
                setReflectorDamage(reflector, damage);
            }
        }
    }

    private boolean hasOrthogonalRod(boolean[][] hasRod, int row, int col) {
        int n = getCoreSize();
        for (int[] dir : ORTHOGONAL_DIRECTIONS) {
            int r = row + dir[0];
            int c = col + dir[1];
            if (r < 0 || r >= n || c < 0 || c >= n) continue;
            if (!isValidCoreCell(r, c)) continue;
            if (hasRod[r][c]) return true;
        }
        return false;
    }

    private static int getReflectorDamage(ItemStack stack) {
        if (stack.getItem() instanceof ICustomDamageItem custom) return custom.getCustomDamage(stack);
        return stack.getItemDamage();
    }

    private static int getReflectorMaxDamage(ItemStack stack) {
        if (stack.getItem() instanceof ICustomDamageItem custom) return custom.getMaxCustomDamage(stack);
        return stack.getMaxDamage();
    }

    private static void setReflectorDamage(ItemStack stack, int damage) {
        if (stack.getItem() instanceof ICustomDamageItem custom) {
            custom.setCustomDamage(stack, damage);
        } else {
            stack.setItemDamage(damage);
        }
    }

    private void moveDepletedToOutput(NuclearItemBus bus, ItemStack depleted) {
        ItemStack out = bus.getStackInSlot(NuclearItemBus.SLOT_OUTPUT);
        if (out == null) {
            bus.setInventorySlotContents(NuclearItemBus.SLOT_OUTPUT, depleted);
            return;
        }
        if (out.isItemEqual(depleted) && out.stackSize < out.getMaxStackSize()) {
            out.stackSize++;
            return;
        }
        bus.setInventorySlotContents(NuclearItemBus.SLOT_WORK, depleted);
    }

    public int getSteamOutputPerSecondForGui() {
        return (int) Math.min(mSteamOutputPerSecond, Integer.MAX_VALUE);
    }

    public void setSteamOutputPerSecondFromGui(int value) {}

    public String generateSteamRateForGui() {
        return StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.gui.steam_rate") + ": "
            + mSteamOutputPerSecond
            + " L/s";
    }

    public static class CoreCellDisplay {

        public static final int KIND_NONE = 0;
        public static final int KIND_ITEM_BUS = 1;
        public static final int KIND_FLUID_HATCH = 2;
        public final int kind;
        public final ItemStack item;
        public final FluidStack fluid;

        public CoreCellDisplay(int kind, ItemStack item, FluidStack fluid) {
            this.kind = kind;
            this.item = item;
            this.fluid = fluid;
        }

        public boolean isEmpty() {
            return item == null && fluid == null;
        }

        public NBTTagCompound serialize() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("kind", (byte) kind);
            if (item != null) tag.setTag("item", item.writeToNBT(new NBTTagCompound()));
            if (fluid != null) tag.setTag("fluid", fluid.writeToNBT(new NBTTagCompound()));
            return tag;
        }

        public static CoreCellDisplay deserialize(NBTTagCompound tag) {
            int kind = tag.hasKey("kind") ? tag.getByte("kind") : KIND_NONE;
            ItemStack item = tag.hasKey("item") ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")) : null;
            FluidStack fluid = tag.hasKey("fluid") ? FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"))
                : null;
            return new CoreCellDisplay(kind, item, fluid);
        }

        public CoreCellDisplay copy() {
            return new CoreCellDisplay(kind, item == null ? null : item.copy(), fluid == null ? null : fluid.copy());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof CoreCellDisplay other)) return false;
            return kind == other.kind && ItemStack.areItemStacksEqual(item, other.item)
                && FluidStack.areFluidStackTagsEqual(fluid, other.fluid)
                && (fluid == null ? other.fluid == null : other.fluid != null && fluid.amount == other.fluid.amount);
        }

        @Override
        public int hashCode() {
            return ((item == null ? 0 : item.hashCode()) * 31 + (fluid == null ? 0 : fluid.hashCode())) * 31 + kind;
        }
    }

    public List<CoreCellDisplay> getCoreCellDisplaysForGui() {
        List<CoreCellDisplay> displays = new ArrayList<>(getCoreCellCount());
        for (int slot = 0; slot < getCoreCellCount(); slot++) {
            IMetaTileEntity meta = getCoreCell(slot);
            if (meta instanceof NuclearItemBus bus) {
                ItemStack rod = bus.getWorkingFuelStack();
                displays.add(new CoreCellDisplay(CoreCellDisplay.KIND_ITEM_BUS, rod == null ? null : rod.copy(), null));
            } else if (meta instanceof NuclearFluidHatch hatch) {
                FluidStack fluid = hatch.getInputFluid();
                displays.add(
                    new CoreCellDisplay(CoreCellDisplay.KIND_FLUID_HATCH, null, fluid == null ? null : fluid.copy()));
            } else {
                displays.add(new CoreCellDisplay(CoreCellDisplay.KIND_NONE, null, null));
            }
        }
        return displays;
    }

    public void setCoreCellDisplaysFromGui(List<CoreCellDisplay> displays) {}

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new NuclearReactorGui(this);
    }
}
