package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.LOG;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.science.gtnl.common.block.blocks.tile.TileEntityLaserBeacon;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.render.tile.MeteorMinerRenderer;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;

import bartworks.system.material.BWTileEntityMetaGeneratedOre;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.ISBRWorldContext;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.blocks.TileEntityOres;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.render.IMTERenderer;
import gtPlusPlus.core.block.ModBlocks;
import lombok.Getter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class MeteorMiner extends MultiMachineBase<MeteorMiner> implements ISurvivalConstructable, IMTERenderer {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_TIER2 = "tier2";
    private static final String MMO_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/meteor_miner_one";
    private static final String MMT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/meteor_miner_two";
    private static final String[][] shape_t1 = StructureUtils.readStructureFromFile(MMO_STRUCTURE_FILE_PATH);
    private static final String[][] shape_t2 = StructureUtils.readStructureFromFile(MMT_STRUCTURE_FILE_PATH);

    public TileEntityLaserBeacon renderer;
    public int xStart, yStart, zStart;
    public int fortuneTier = 0;
    public boolean isStartInitialized = false;
    public boolean hasFinished = true;
    public boolean isWaiting = false;
    public boolean isResetting = false;
    public boolean enableRender = true;
    public final Collection<ItemStack> itemDrop = new ArrayList<>();
    public byte tierMachine = 0;

    public final Deque<BlockPos> scanQueue = new ArrayDeque<>();
    public final Deque<List<BlockPos>> rowQueue = new ArrayDeque<>();

    public static final int SCAN_WIDTH = 100;
    public static final int SCAN_HEIGHT = 150;
    public static final int SCAN_DEPTH = 100;
    public static final int MAX_BLOCKS_PER_CYCLE = MainConfig.meteorMinerMaxBlockPerCycle;
    public static final int MAX_ROWS_PER_CYCLE = MainConfig.meteorMinerMaxRowPerCycle;

    @Getter
    public float renderAngle = 0f;

    public MeteorMiner(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MeteorMiner(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MeteorMiner(this.mName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean renderInWorld(ISBRWorldContext ctx) {
        if (MainConfig.enableAprilFool) {
            return true;
        } else {
            return super.renderInWorld(ctx);
        }
    }

    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        if (MainConfig.enableAprilFool) {
            MeteorMinerRenderer.renderTileEntity(this, x, y, z, timeSinceLastTick);
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public IStructureDefinition<MeteorMiner> getStructureDefinition() {
        return StructureDefinition.<MeteorMiner>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape_t1))
            .addShape(STRUCTURE_PIECE_TIER2, transpose(shape_t2))
            .addElement('A', chainAllGlasses())
            .addElement('B', ofBlock(GregTechAPI.sBlockCasings1, 15))
            .addElement('C', ofBlock(GregTechAPI.sBlockCasings5, 5))
            .addElement('D', ofFrame(Materials.StainlessSteel))
            .addElement('E', ofBlock(ModBlocks.blockSpecialMultiCasings, 6))
            .addElement('F', ofBlock(ModBlocks.blockSpecialMultiCasings, 8))
            .addElement('G', ofBlock(BlockLoader.laserBeacon, 0))
            .addElement(
                'H',
                buildHatchAdder(MeteorMiner.class).atLeast(Maintenance, OutputBus, Energy)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(ofBlock(ModBlocks.blockSpecialMultiCasings, 6)))
            .addElement(
                'I',
                buildHatchAdder(MeteorMiner.class).hatchClass(MTEHatchInputBus.class)
                    .shouldReject(t -> !t.mInputBusses.isEmpty())
                    .adder(MeteorMiner::addInjector)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(ofBlock(ModBlocks.blockSpecialMultiCasings, 6)))
            .addElement(
                'J',
                buildHatchAdder(MeteorMiner.class).atLeast(Maintenance, OutputBus, Energy.or(ExoticEnergy))
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(ofBlock(GregTechAPI.sBlockCasings8, 2)))
            .addElement('K', ofBlock(GregTechAPI.sBlockCasings4, 7))
            .addElement('L', ofBlock(GregTechAPI.sBlockCasings8, 2))
            .addElement('M', ofBlock(GregTechAPI.sBlockCasings8, 3))
            .addElement('N', ofBlock(GregTechAPI.sBlockCasings9, 11))
            .addElement('O', ofFrame(Materials.Neutronium))
            .addElement('P', ofFrame(Materials.BlackPlutonium))
            .build();
    }

    public boolean addInjector(IGregTechTileEntity aBaseMetaTileEntity, int aBaseCasingIndex) {
        IMetaTileEntity aMetaTileEntity = aBaseMetaTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (!(aMetaTileEntity instanceof MTEHatchInputBus bus)) return false;
        if (bus.getTierForStructure() > 0) return false;
        bus.updateTexture(aBaseCasingIndex);
        return mInputBusses.add(bus);
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> (d.flag & (ForgeDirection.UP.flag | ForgeDirection.DOWN.flag)) == 0 && r.isNotRotated()
            && !f.isVerticallyFliped();
    }

    @Override
    public void onDisableWorking() {
        if (renderer != null) renderer.setShouldRender(false);
        super.onDisableWorking();
    }

    @Override
    public void onBlockDestroyed() {
        if (renderer != null) renderer.setShouldRender(false);
        super.onBlockDestroyed();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        if (stackSize.stackSize < 2) {
            buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 9, 13, 7);
        } else buildPiece(STRUCTURE_PIECE_TIER2, stackSize, hintsOnly, 9, 15, 3);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return stackSize.stackSize < 2
            ? survivalBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 9, 13, 7, elementBudget, env, false, true)
            : survivalBuildPiece(STRUCTURE_PIECE_TIER2, stackSize, 9, 15, 3, elementBudget, env, false, true);
    }

    @Override
    public int getCasingTextureID() {
        return tierMachine > 1 ? GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings8, 2)
            : TAE.getIndexFromPage(0, 6);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        ITexture[] rTexture;
        if (side == aFacing) {
            if (aActive) {
                rTexture = new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_METEOR_MINER_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_METEOR_MINER_ACTIVE_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            } else {
                rTexture = new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_METEOR_MINER)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_METEOR_MINER_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
        }
        return rTexture;
    }

    @Override
    public void onValueUpdate(byte aValue) {
        if (tierMachine != aValue) {
            tierMachine = (byte) (aValue & 0x0F);
        }
    }

    @Override
    public byte getUpdateData() {
        if (tierMachine <= 0) return 0;
        return tierMachine;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MeteorMinerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_11"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_12"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_13"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_14"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_15"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_16"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_17"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_07"))
            .addController(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_01_01"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_01_02"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_01_02"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_01_02"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_01_03"), 2)
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_MeteorMiner_13"))
            .addController(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_02_01"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_02_02"), 3)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_02_02"), 3)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_MeteorMiner_Casing_02_02"), 3)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        enableRender = !enableRender;
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("MeteorMiner_Render_" + (enableRender ? "Enabled" : "Disabled")));
        if (renderer != null) renderer.setShouldRender(enableRender);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (checkPiece(STRUCTURE_PIECE_MAIN, 9, 13, 7) && checkHatch()) {
            tierMachine = 1;
        } else if (checkPiece(STRUCTURE_PIECE_TIER2, 9, 15, 3) && checkHatch()) {
            tierMachine = 2;
        }
        if (mInputBusses.isEmpty() && this.tierMachine == 1 || !findLaserRenderer(getBaseMetaTileEntity().getWorld()))
            return false;
        setupParameters();
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
        return this.tierMachine > 0;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        this.tierMachine = 0;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && !mEnergyHatches.isEmpty();
    }

    public boolean findLaserRenderer(World w) {
        this.setStartCoords();
        if (w.getTileEntity(
            xStart,
            getBaseMetaTileEntity().getYCoord() + (this.tierMachine == 1 ? 10 : 15),
            zStart) instanceof TileEntityLaserBeacon laser) {
            renderer = laser;
            renderer.setRotationFields(getDirection(), getRotation(), getFlip());
            return true;
        }
        return false;
    }

    public int getTierMachine(ItemStack inventory) {
        if (inventory == null) return 0;
        return inventory.isItemEqual(GTNLItemList.MeteorMinerSchematic2.get(1)) ? 2
            : inventory.isItemEqual(GTNLItemList.MeteorMinerSchematic1.get(1)) ? 1 : 0;
    }

    public boolean isSpecificItem(ItemStack stack, String modId, String itemName) {
        ItemStack specificItem = getModItem(modId, itemName, 1, 0);
        return stack.getItem() == specificItem.getItem() && stack.getItemDamage() == specificItem.getItemDamage();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("xStart", xStart);
        aNBT.setInteger("yStart", yStart);
        aNBT.setInteger("zStart", zStart);
        aNBT.setBoolean("isStartInitialized", isStartInitialized);
        aNBT.setBoolean("hasFinished", hasFinished);
        aNBT.setBoolean("isWaiting", isWaiting);
        aNBT.setBoolean("enableRender", enableRender);
        aNBT.setInteger("tierMachine", tierMachine);
        aNBT.setInteger("fortuneTier", fortuneTier);
        aNBT.setDouble("renderAngle", renderAngle);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        xStart = aNBT.getInteger("xStart");
        yStart = aNBT.getInteger("yStart");
        zStart = aNBT.getInteger("zStart");
        isStartInitialized = aNBT.getBoolean("isStartInitialized");
        hasFinished = aNBT.getBoolean("hasFinished");
        isWaiting = aNBT.getBoolean("isWaiting");
        enableRender = aNBT.getBoolean("enableRender");
        tierMachine = aNBT.getByte("tierMachine");
        fortuneTier = aNBT.getInteger("fortuneTier");
        renderAngle = (float) aNBT.getDouble("renderAngle");
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        renderAngle += 1f;
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    public void reset() {
        this.isResetting = false;
        this.hasFinished = true;
        this.isWaiting = false;
        scanQueue.clear();
        rowQueue.clear();
        this.initializeDrillPos();
    }

    public void startReset() {
        this.isResetting = true;
        stopMachine(ShutDownReasonRegistry.NONE);
        checkMachine(this.getBaseMetaTileEntity(), mInventory[1]);
        enableWorking();
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (this.tierMachine != this.getTierMachine(mInventory[1])) {
            stopMachine(ShutDownReasonRegistry.NONE);
            return SimpleCheckRecipeResult.ofFailure("missing_schematic");
        }

        if (renderer != null) {
            renderer.setColors(1, 0, 0);
        }

        if (isResetting) {
            this.reset();
            return SimpleCheckRecipeResult.ofSuccess("meteor_reset");
        }

        setElectricityStats();
        if (!isEnergyEnough()) {
            return CheckRecipeResultRegistry.insufficientPower(lEUt);
        }

        if (hasFinished) {
            if (!isWaiting) {
                isWaiting = true;
                if (renderer != null) renderer.setShouldRender(false);
            }
            setElectricityStats();
            boolean centerReady = checkIsBlock();
            if (centerReady) {
                isWaiting = false;
                isStartInitialized = false;
                hasFinished = false;
            }
            return SimpleCheckRecipeResult.ofSuccess("meteor_waiting");
        }

        if (!isStartInitialized) {
            setStartCoords();
            if (tierMachine == 1) {
                prepareScanQueue();
            } else {
                prepareRowQueue();
            }
        }

        if (!hasFinished) {
            setFortuneTier();
            if (tierMachine == 1) {
                int done = 0;
                while (done < MAX_BLOCKS_PER_CYCLE && !scanQueue.isEmpty()) {
                    BlockPos pos = scanQueue.pollFirst();
                    mineAt(pos.x, pos.y, pos.z);
                    done++;
                }
            } else {
                int rows = 0;
                while (rows < MAX_ROWS_PER_CYCLE && !rowQueue.isEmpty()) {
                    List<BlockPos> row = rowQueue.pollFirst();
                    for (BlockPos pos : row) {
                        mineAt(pos.x, pos.y, pos.z);
                    }
                    rows++;
                }
            }

            mOutputItems = itemDrop.toArray(new ItemStack[0]);
            itemDrop.clear();

            boolean queueEmpty = (tierMachine == 1 ? scanQueue.isEmpty() : rowQueue.isEmpty());
            if (queueEmpty) {
                hasFinished = true;
                if (renderer != null) renderer.setShouldRender(false);
                checkMachine(this.getBaseMetaTileEntity(), mInventory[1]);
            } else {
                if (renderer != null) {
                    renderer.setShouldRender(true);
                    renderer.setRange(150);
                }
            }
        }

        return SimpleCheckRecipeResult.ofSuccess("meteor_mining");
    }

    public void setFortuneTier() {
        this.fortuneTier = 0;

        if (this.tierMachine == 2) {
            this.fortuneTier = 5;
            return;
        }

        if (!mInputBusses.isEmpty()) {
            Optional<ItemStack> input = Optional.ofNullable(
                mInputBusses.get(0)
                    .getInventoryHandler()
                    .getStackInSlot(0));
            input.ifPresent(stack -> this.fortuneTier = getFortuneTierForItem(stack));
        }
    }

    public int getFortuneTierForItem(ItemStack stack) {
        if (isSpecificItem(stack, Botania.ID, "terraPick")) {
            return 4;
        } else if (isSpecificItem(stack, BloodMagic.ID, "boundPickaxe")) {
            return 3;
        } else if (isSpecificItem(stack, Thaumcraft.ID, "ItemPickaxeElemental")) {
            return 2;
        } else {
            return 0;
        }
    }

    public void prepareScanQueue() {
        scanQueue.clear();
        World w = getBaseMetaTileEntity().getWorld();
        int x0 = xStart - SCAN_WIDTH / 2;
        int y0 = yStart;
        int z0 = zStart - SCAN_DEPTH / 2;
        for (int dy = 0; dy < SCAN_HEIGHT; dy++) {
            for (int dx = 0; dx < SCAN_WIDTH; dx++) {
                for (int dz = 0; dz < SCAN_DEPTH; dz++) {
                    int x = x0 + dx, y = y0 + dy, z = z0 + dz;
                    if (!w.isAirBlock(x, y, z)) {
                        scanQueue.addLast(new BlockPos(x, y, z));
                    }
                }
            }
        }
        isStartInitialized = true;
        hasFinished = false;
    }

    public void prepareRowQueue() {
        rowQueue.clear();
        World w = getBaseMetaTileEntity().getWorld();
        int x0 = xStart - SCAN_WIDTH / 2;
        int y0 = yStart;
        int z0 = zStart - SCAN_DEPTH / 2;
        for (int dy = 0; dy < SCAN_HEIGHT; dy++) {
            for (int dx = 0; dx < SCAN_WIDTH; dx++) {
                List<BlockPos> row = new ArrayList<>(SCAN_DEPTH);
                for (int dz = 0; dz < SCAN_DEPTH; dz++) {
                    int x = x0 + dx, y = y0 + dy, z = z0 + dz;
                    if (!w.isAirBlock(x, y, z)) {
                        row.add(new BlockPos(x, y, z));
                    }
                }
                if (!row.isEmpty()) {
                    rowQueue.addLast(row);
                }
            }
        }
        isStartInitialized = true;
        hasFinished = false;
    }

    public void mineAt(int x, int y, int z) {
        World w = getBaseMetaTileEntity().getWorld();
        if (w.isAirBlock(x, y, z)) return;

        Block target = w.getBlock(x, y, z);
        int meta = w.getBlockMetadata(x, y, z);
        if (target.getBlockHardness(w, x, y, z) < 0) return;

        Collection<ItemStack> drops = target.getDrops(w, x, y, z, meta, 0);

        if (GTUtility.isOre(target, meta)) {
            try {
                TileEntity te = w.getTileEntity(x, y, z);
                if (te instanceof TileEntityOres || te instanceof BWTileEntityMetaGeneratedOre) {
                    itemDrop.addAll(getOutputByDrops(drops));
                }
            } catch (Exception e) {
                LOG.error("GTNL Meteor Miner: GT Ore Error [{},{},{}]", x, y, z, e);
            }
        } else {
            itemDrop.addAll(drops);
        }
        w.setBlockToAir(x, y, z);
        w.removeTileEntity(x, y, z);
    }

    public Collection<ItemStack> getOutputByDrops(Collection<ItemStack> oreBlockDrops) {
        long voltage = getMaxInputVoltage();
        Collection<ItemStack> outputItems = new HashSet<>();
        oreBlockDrops.forEach(currentItem -> {
            if (!doUseMaceratorRecipe(currentItem)) {
                outputItems.add(multiplyStackSize(currentItem));
                return;
            }
            GTRecipe tRecipe = RecipeMaps.maceratorRecipes.findRecipeQuery()
                .items(currentItem)
                .voltage(voltage)
                .find();
            if (tRecipe == null) {
                outputItems.add(currentItem);
                return;
            }
            for (int i = 0; i < tRecipe.mOutputs.length; i++) {
                ItemStack recipeOutput = tRecipe.mOutputs[i].copy();
                if (getBaseMetaTileEntity().getRandomNumber(10000) < tRecipe.getOutputChance(i))
                    multiplyStackSize(recipeOutput);
                outputItems.add(recipeOutput);
            }
        });
        return outputItems;
    }

    public ItemStack multiplyStackSize(ItemStack itemStack) {
        itemStack.stackSize *= getBaseMetaTileEntity().getRandomNumber(this.fortuneTier + 1) + 1;
        return itemStack;
    }

    public boolean doUseMaceratorRecipe(ItemStack currentItem) {
        ItemData itemData = GTOreDictUnificator.getItemData(currentItem);
        return itemData == null || itemData.mPrefix != OrePrefixes.crushed && itemData.mPrefix != OrePrefixes.dustImpure
            && itemData.mPrefix != OrePrefixes.dust
            && itemData.mPrefix != OrePrefixes.gem
            && itemData.mPrefix != OrePrefixes.gemChipped
            && itemData.mPrefix != OrePrefixes.gemExquisite
            && itemData.mPrefix != OrePrefixes.gemFlawed
            && itemData.mPrefix != OrePrefixes.gemFlawless
            && itemData.mMaterial.mMaterial != Materials.Oilsands;
    }

    public void setStartCoords() {
        ForgeDirection facing = getBaseMetaTileEntity().getBackFacing();
        if (facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH) {
            xStart = getBaseMetaTileEntity().getXCoord();
            zStart = (this.tierMachine == 1 ? 2 : 6) * getExtendedFacing().getRelativeBackInWorld().offsetZ
                + getBaseMetaTileEntity().getZCoord();
        } else {
            xStart = (this.tierMachine == 1 ? 2 : 6) * getExtendedFacing().getRelativeBackInWorld().offsetX
                + getBaseMetaTileEntity().getXCoord();
            zStart = getBaseMetaTileEntity().getZCoord();
        }
        yStart = (this.tierMachine == 1 ? 14 : 16) + getBaseMetaTileEntity().getYCoord();
    }

    public void initializeDrillPos() {
        this.isStartInitialized = true;
        this.hasFinished = false;
    }

    public boolean checkIsBlock() {
        World world = getBaseMetaTileEntity().getWorld();
        for (int y = yStart + 1; y <= 255; y++) {
            if (!world.isAirBlock(xStart, y, zStart)) {
                return true;
            }
        }
        return false;
    }

    public void setElectricityStats() {
        this.mOutputItems = new ItemStack[0];

        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;

        GTNL_OverclockCalculator calculator = new GTNL_OverclockCalculator().setEUt(getAverageInputVoltage())
            .setAmperage(getMaxInputAmps())
            .setRecipeEUt(128)
            .setDuration(12 * 20)
            .setAmperageOC(mEnergyHatches.size() != 1)
            .setExtraDurationModifier(mConfigSpeedBoost);
        calculator.calculate();
        this.mMaxProgresstime = (isWaiting) ? 200 : calculator.getDuration();
        this.lEUt = (int) (calculator.getConsumption() / ((isWaiting) ? 8 : 1));
    }

    public boolean isEnergyEnough() {
        long requiredEnergy = 512 + getMaxInputVoltage() * 4;
        for (MTEHatchEnergy energyHatch : mEnergyHatches) {
            requiredEnergy -= energyHatch.getEUVar();
            if (requiredEnergy <= 0) return true;
        }
        return false;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        builder.widget(
            new ButtonWidget().setOnClick((clickData, widget) -> this.startReset())
                .setPlayClickSound(true)
                .setBackground(
                    () -> new IDrawable[] { GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_CYCLIC })
                .setPos(new Pos2d(174, 112))
                .setSize(16, 16));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("fortuneTier", this.fortuneTier);
        tag.setInteger("tierMachine", this.tierMachine);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            StatCollector.translateToLocal("Info_MeteorMiner_00") + EnumChatFormatting.WHITE
                + tag.getInteger("tierMachine")
                + EnumChatFormatting.RESET);
        currentTip.add(
            StatCollector.translateToLocal("Info_MeteorMiner_01") + EnumChatFormatting.WHITE
                + tag.getInteger("fortuneTier")
                + EnumChatFormatting.RESET);
    }
}
