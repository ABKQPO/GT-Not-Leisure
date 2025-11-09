package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.*;
import static forestry.api.apiculture.BeeManager.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static kubatech.api.gui.KubaTechUITextures.*;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.Platform;
import com.google.common.collect.ImmutableSet;
import com.science.gtnl.utils.enums.GTNLItemList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.MainAxisAlignment;
import com.gtnewhorizons.modularui.api.screen.ITileWithModularUI;
import com.gtnewhorizons.modularui.api.screen.ModularUIContext;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.builder.UIBuilder;
import com.gtnewhorizons.modularui.common.builder.UIInfo;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularGui;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularUIContainer;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.Column;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedRow;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamApiaryModule;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import forestry.api.apiculture.EnumBeeType;
import forestry.plugins.PluginApiculture;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import kubatech.api.DynamicInventory;

public class AssemblerMatrix extends MultiMachineBase<AssemblerMatrix> implements IInterfaceHost , IGridProxyable {

    public int mMaxSlots = 0;
    public List<SteamApiaryModule.BeeSimulator> mStorage = new ObjectArrayList<>();

    public int mCountPatternCasing = -1;
    public int mCountCrafterCasing = -1;

    private AENetworkProxy gridProxy;
    protected final MachineSource source = new MachineSource(this);
    private DualityInterface di;
    private final Map<ItemStack,ICraftingPatternDetails> patterns = new Reference2ObjectOpenHashMap<>();

    public static final int CONFIGURATION_WINDOW_ID = 10;
    public static final int MODE_PRIMARY_INPUT = 0;
    public static final int MODE_PRIMARY_OUTPUT = 1;
    public static final int MODE_PRIMARY_OPERATING = 2;
    public int mPrimaryMode = MODE_PRIMARY_INPUT;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String AM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/assembler_matrix";
    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(AM_STRUCTURE_FILE_PATH);

    public AssemblerMatrix(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public AssemblerMatrix(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new AssemblerMatrix(this.mName);
    }

    /**
     * 向合成网络提供样板
     * @param craftingTracker crafting helper
     */
    @Override
    public void provideCrafting(ICraftingProviderHelper craftingTracker) {
        if (mMachine && this.getProxy().isActive() && !patterns.isEmpty()) {
            for (var value : patterns.values()) {
                craftingTracker.addCraftingOption(this, value);
            }
        }
    }

    //TODO:保存和读取NBT
    private final Queue<IAEItemStack> outputs = new ArrayDeque<>();
    private int workTime = 0;
    private static final int workPeriod = 20;

    @Override
    public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        var out = patternDetails.getCondensedOutputs()[0];
        var parallel = 0;
        for (int i = 0; i < table.getSizeInventory(); i++) {
            var stack = table.getStackInSlot(i);
            if (stack != null) {
                if (outputs.isEmpty()) workTime = 0;
                outputs.add(out.copy().setStackSize(out.getStackSize() * stack.stackSize));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (!aBaseMetaTileEntity.isServerSide()) return;
        if (!outputs.isEmpty()) {
            if (++workTime == workPeriod) {
                workTime = 0;
                long parallel = getMaxParallelRecipes();
                int maximum = outputs.size();
                do {
                    var stack = outputs.poll();

                    var grid = getProxy().getNode().getGrid();
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                    var storage = storageGrid.getItemInventory();
                    if (stack.getStackSize() <= parallel) {
                        parallel -= stack.getStackSize();
                        var newItem = Platform.poweredInsert(energyGrid, storage, stack, source);
                        if (newItem != null && newItem.getStackSize() != 0) {
                            outputs.add(newItem);
                        }
                    } else {
                        stack.decStackSize(parallel);
                        var newItem = Platform.poweredInsert(energyGrid, storage, stack.copy().setStackSize(parallel), source);
                        if (newItem != null && newItem.getStackSize() != 0) {
                            outputs.add(newItem);
                        }
                    }
                    if (outputs.isEmpty() || --maximum == 0) return;
                } while (parallel > 0);
            }
        }
    }

    /**
     * 是否可被发配
     */
    @Override
    public boolean isBusy() {
        return !mMachine;
    }

    @Override
    public int getMaxParallelRecipes() {
        mMaxParallel = 512 * mCountCrafterCasing;
        mMaxSlots = 72 * mCountPatternCasing;
        return mMaxParallel;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        super.onScrewdriverRightClick(side, aPlayer, aX, aY, aZ, aTool);
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatToPlayer(aPlayer, "Can't change mode when running !");
            return;
        }
        mPrimaryMode++;
        if (mPrimaryMode == 3) mPrimaryMode = 0;
        switch (mPrimaryMode) {
            case 0:
                GTUtility.sendChatToPlayer(aPlayer, "Changed primary mode to: Input mode");
                break;
            case 1:
                GTUtility.sendChatToPlayer(aPlayer, "Changed primary mode to: Output mode");
                break;
            case 2:
                GTUtility.sendChatToPlayer(aPlayer, "Changed primary mode to: Operating mode");
                break;
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        if (getBaseMetaTileEntity().isServerSide())
            tryOutputAll(mStorage, s -> Collections.singletonList(s.queenStack));
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mMaxSlots", mMaxSlots);
        aNBT.setInteger("mPrimaryMode", mPrimaryMode);
        aNBT.setInteger("mStorageSize", mStorage.size());
        for (int i = 0; i < mStorage.size(); i++) aNBT.setTag(
            "mStorage." + i,
            mStorage.get(i)
                .toNBTTagCompound());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mMaxSlots = aNBT.getInteger("mMaxSlots");
        mPrimaryMode = aNBT.getInteger("mPrimaryMode");
        for (int i = 0, isize = aNBT.getInteger("mStorageSize"); i < isize; i++)
            mStorage.add(new SteamApiaryModule.BeeSimulator(aNBT.getCompoundTag("mStorage." + i)));
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("AssemblerMatrixRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AssemblerMatrix_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AssemblerMatrix_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AssemblerMatrix_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AssemblerMatrix_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AssemblerMatrix_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 9, 9, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            getProxy().setValidSides(emptyDirection);
            return false;
        }
        setupParameters();
        getProxy().setValidSides(allDirection);
        return true;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        mMaxParallel = 512 * mCountCrafterCasing;
        mMaxSlots = 72 * mCountPatternCasing;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mCountCrafterCasing = 0;
        mCountPatternCasing = 0;
        mMaxParallel = 0;
        mMaxSlots = 0;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && mCountCrafterCasing + mCountPatternCasing + mCountCasing == 343;
    }

    @Override
    public boolean checkEnergyHatch() {
        return true;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
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
    public IStructureDefinition<AssemblerMatrix> getStructureDefinition() {
        return StructureDefinition.<AssemblerMatrix>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(AssemblerMatrix.class).atLeast(Maintenance, InputBus, OutputBus)
                    .adder(AssemblerMatrix::addToMachineList)
                    .dot(1)
                    .casingIndex(getCasingTextureID())
                    .buildAndChain(BlockLoader.metaCasing02, 4))
            .addElement('B', ofChain(chainAllGlasses(), ofBlock(BlockLoader.metaCasing02, 5)))
            .addElement(
                'C',
                ofChain(
                    onElementPass(t -> t.mCountPatternCasing++, ofBlock(BlockLoader.metaCasing02, 6)),
                    onElementPass(t -> t.mCountCrafterCasing++, ofBlock(BlockLoader.metaCasing02, 7)),
                    onElementPass(t -> t.mCountCasing++, ofBlock(BlockLoader.metaCasing02, 5))))
            .build();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return GTUtility.getTextureId((byte) 116, (byte) 36);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (mPrimaryMode < 2) { // input and output mode
            if (mPrimaryMode == MODE_PRIMARY_INPUT && mStorage.size() < mMaxSlots) {
                World w = getBaseMetaTileEntity().getWorld();
                List<ItemStack> inputs = getStoredInputs();
                for (ItemStack input : inputs) {
                    if (beeRoot.getType(input) == EnumBeeType.QUEEN) {
                        SteamApiaryModule.BeeSimulator bs = new SteamApiaryModule.BeeSimulator(input, w, 6);
                        if (bs.isValid) {
                            mStorage.add(bs);
                        }
                    }
                    if (mStorage.size() >= mMaxSlots) break;
                }
                updateSlots();
            } else if (mPrimaryMode == MODE_PRIMARY_OUTPUT && !mStorage.isEmpty()) { // output mode
                tryOutputAll(mStorage, s -> Collections.singletonList(s.queenStack));
            } else {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
            mMaxProgresstime = 10;
            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            lEUt = 0;
            return CheckRecipeResultRegistry.SUCCESSFUL;
        } else if (mPrimaryMode == MODE_PRIMARY_OPERATING) {
            if (mMaxSlots > 0 && !mStorage.isEmpty()) {

                if (mStorage.size() > mMaxSlots) return SimpleCheckRecipeResult.ofFailure("MegaApiary_slotoverflow");

                int maxConsume = mStorage.size() * 40;
                int toConsume = maxConsume;
                List<ItemStack> inputs = getStoredInputs();

                for (ItemStack input : inputs) {
                    if (!input.isItemEqual(PluginApiculture.items.royalJelly.getItemStack(1))) continue;
                    int consumed = Math.min(input.stackSize, toConsume);
                    toConsume -= consumed;
                    input.stackSize -= consumed;
                    if (toConsume == 0) break;
                }
                double boosted = 1d;
                if (toConsume != maxConsume) {
                    boosted += (((double) maxConsume - (double) toConsume) / (double) maxConsume) * 2d;
                    this.updateSlots();
                }

                List<ItemStack> stacks = new ObjectArrayList<>();
                for (int i = 0, mStorageSize = Math.min(mStorage.size(), mMaxSlots); i < mStorageSize; i++) {
                    SteamApiaryModule.BeeSimulator beeSimulator = mStorage.get(i);
                    // stacks.addAll(beeSimulator.getDrops(this, 64_00d * boosted));
                }

                this.lEUt = -GTValues.V[4] * mMaxSlots;
                this.mEfficiency = 10000;
                this.mEfficiencyIncrease = 10000;
                this.mMaxProgresstime = 600;
                this.mOutputItems = stacks.toArray(new ItemStack[0]);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public String[] getInfoData() {
        List<String> info = new ObjectArrayList<>(super.getInfoData());
        info.add(
            StatCollector.translateToLocal("kubatech.infodata.running_mode") + " "
                + EnumChatFormatting.GOLD
                + (mPrimaryMode == 0 ? StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.input")
                    : (mPrimaryMode == 1 ? StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.output")
                        : StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.operating.normal"))));
        info.add(
            StatCollector.translateToLocalFormatted(
                "kubatech.infodata.mia.running_mode.bee_storage",
                "" + EnumChatFormatting.GOLD + mStorage.size() + EnumChatFormatting.RESET,
                (mStorage.size() > mMaxSlots ? EnumChatFormatting.DARK_RED.toString()
                    : EnumChatFormatting.GOLD.toString()) + mMaxSlots + EnumChatFormatting.RESET));
        HashMap<String, Integer> infos = new HashMap<>();
        for (int i = 0; i < mStorage.size(); i++) {
            StringBuilder builder = new StringBuilder();
            if (i > mMaxSlots) builder.append(EnumChatFormatting.DARK_RED);
            builder.append(EnumChatFormatting.GOLD);
            SteamApiaryModule.BeeSimulator beeSimulator = mStorage.get(i);
            builder.append(beeSimulator.queenStack.getDisplayName());
            infos.merge(builder.toString(), 1, Integer::sum);
        }
        infos.forEach((key, value) -> info.add("x" + value + ": " + key));

        return info.toArray(new String[0]);
    }

    public static UIInfo<?, ?> patternUI = createMetaTileEntityUI();

    public static UIInfo<?, ?> createMetaTileEntityUI() {
        return UIBuilder.of()
            .container((player, world, x, y, z) -> {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof BaseMetaTileEntity baseMetaTileEntity) {
                    IMetaTileEntity mte = baseMetaTileEntity.getMetaTileEntity();
                    if (!(mte instanceof SteamApiaryModule steamApiaryModule)) return null;
                    final UIBuildContext buildContext = new UIBuildContext(player);
                    final ModularWindow window = ((ITileWithModularUI) te).createWindow(buildContext);
                    return new MUIContainer(
                        new ModularUIContext(buildContext, te::markDirty),
                        window,
                        steamApiaryModule);
                }
                return null;
            })
            .gui((player, world, x, y, z) -> {
                if (!world.isRemote) return null;
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof BaseMetaTileEntity baseMetaTileEntity) {
                    IMetaTileEntity mte = baseMetaTileEntity.getMetaTileEntity();
                    if (!(mte instanceof SteamApiaryModule steamApiaryModule)) return null;
                    final UIBuildContext buildContext = new UIBuildContext(player);
                    final ModularWindow window = ((ITileWithModularUI) te).createWindow(buildContext);
                    return new ModularGui(
                        new MUIContainer(new ModularUIContext(buildContext, null), window, steamApiaryModule));
                }
                return null;
            })
            .build();
    }

    public void addConfigurationWidgets(DynamicPositionedRow configurationElements, UIBuildContext buildContext) {
        buildContext.addSyncedWindow(CONFIGURATION_WINDOW_ID, this::createConfigurationWindow);
        configurationElements.setSynced(false);
        configurationElements.widget(
            new ButtonWidget().setOnClick(
                (clickData, widget) -> {
                    if (!widget.isClient()) widget.getContext()
                        .openSyncedWindow(CONFIGURATION_WINDOW_ID);
                })
                .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_CYCLIC)
                .addTooltip(StatCollector.translateToLocal("kubatech.gui.text.configuration"))
                .setSize(16, 16));
    }

    private static final EnumSet<ForgeDirection> allDirection = EnumSet.allOf(ForgeDirection.class);
    private static final EnumSet<ForgeDirection> emptyDirection = EnumSet.noneOf(ForgeDirection.class);

    //TODO:目前无论成型与否都无法链接频道
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(
                    (IGridProxyable) getBaseMetaTileEntity(),
                    "proxy",
                    GTNLItemList.AssemblerMatrix.get(1),
                    true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                if (getBaseMetaTileEntity().getWorld() != null) gridProxy.setOwner(
                    getBaseMetaTileEntity().getWorld()
                                           .getPlayerEntityByName(getBaseMetaTileEntity().getOwnerName()));
            }
        }

        return gridProxy;
    }

    @Override
    public DualityInterface getInterfaceDuality() {
        if (di == null) {
            di = new DualityInterface(this.getProxy(),this);
        }
        return di;
    }

    @Override
    public EnumSet<ForgeDirection> getTargets() {
        return emptyDirection;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(getBaseMetaTileEntity().getWorld(),getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getYCoord(), getBaseMetaTileEntity().getZCoord());
    }

    @Override
    public TileEntity getTileEntity() {
        return getBaseMetaTileEntity().getTileEntity(getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getYCoord(), getBaseMetaTileEntity().getZCoord());
    }

    @Override
    public void saveChanges() {
        this.getInterfaceDuality().saveChanges();
    }

    /**
     * @return 是否能在接口终端显示
     */
    @Override
    public boolean shouldDisplay() {
        return true;
    }

    @Override
    public boolean allowsPatternOptimization() {
        return false;
    }

    @Override
    public ItemStack getSelfRep() {
        return GTNLItemList.AssemblerMatrix.get(1);
    }

    @Override
    public int rows() {
        return mMaxSlots / 9;
    }

    @Override
    public int rowSize() {
        return 9;
    }

    /**
     * @return 用于接口终端显示与操作样板
     */
    @Override
    public IInventory getPatterns() {
        return getInterfaceDuality().getPatterns();
    }

    @Override
    public int getInstalledUpgrades(Upgrades u) {
        return mMaxSlots / 9 - 1;
    }

    @Override
    public TileEntity getTile() {
        return getTileEntity();
    }

    @Override
    public IInventory getInventoryByName(String name) {
        return this.getInterfaceDuality().getInventoryByName(name);
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        return this.getProxy().getNode();
    }

    @Override
    public void securityBreak() {

    }

    @Override
    public ItemStack getCrafterIcon() {
        return GTNLItemList.AssemblerMatrix.get(1);
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.getInterfaceDuality().getRequestedJobs();
    }

    @Override
    public IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack items, Actionable mode) {
        return this.getInterfaceDuality().injectCraftedItems(link, items, mode);
    }

    @Override
    public void jobStateChange(ICraftingLink link) {
        this.getInterfaceDuality().jobStateChange(link);
    }

    @Override
    public IGridNode getActionableNode() {
        return this.getProxy().getNode();
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.getInterfaceDuality().getConfigManager();
    }

    public static class MUIContainer extends ModularUIContainer {

        final WeakReference<SteamApiaryModule> parent;

        public MUIContainer(ModularUIContext context, ModularWindow mainWindow, SteamApiaryModule mte) {
            super(context, mainWindow);
            parent = new WeakReference<>(mte);
        }

        @Override
        public ItemStack transferStackInSlot(EntityPlayer aPlayer, int aSlotIndex) {
            if (!(aPlayer instanceof EntityPlayerMP)) return super.transferStackInSlot(aPlayer, aSlotIndex);
            final Slot s = getSlot(aSlotIndex);
            if (s == null) return super.transferStackInSlot(aPlayer, aSlotIndex);
            if (aSlotIndex >= 36) return super.transferStackInSlot(aPlayer, aSlotIndex);
            final ItemStack aStack = s.getStack();
            if (aStack == null) return super.transferStackInSlot(aPlayer, aSlotIndex);
            SteamApiaryModule mte = parent.get();
            if (mte == null) return super.transferStackInSlot(aPlayer, aSlotIndex);
            if (mte.mStorage.size() >= mte.mMaxSlots) return super.transferStackInSlot(aPlayer, aSlotIndex);
            if (beeRoot.getType(aStack) == EnumBeeType.QUEEN) {
                if (mte.mMaxProgresstime > 0) {
                    GTUtility.sendChatToPlayer(aPlayer, EnumChatFormatting.RED + "Can't insert while running !");
                    return super.transferStackInSlot(aPlayer, aSlotIndex);
                }
                World w = mte.getBaseMetaTileEntity()
                    .getWorld();
                SteamApiaryModule.BeeSimulator bs = new SteamApiaryModule.BeeSimulator(
                    aStack,
                    w,
                    6 + mte.recipeOcCount);
                if (bs.isValid) {
                    mte.mStorage.add(bs);
                    s.putStack(null);
                    detectAndSendChanges();
                    return null;
                }
            }
            return super.transferStackInSlot(aPlayer, aSlotIndex);
        }
    }

    public static final int INVENTORY_WIDTH = 128;
    public static final int INVENTORY_HEIGHT = 60;
    public static final int INVENTORY_X = 10;
    public static final int INVENTORY_Y = 16;
    public static final int INVENTORY_BORDER_WIDTH = 3;

    public DynamicInventory<SteamApiaryModule.BeeSimulator> dynamicInventory = new DynamicInventory<>(
        INVENTORY_WIDTH,
        INVENTORY_HEIGHT,
        () -> mMaxSlots,
        mStorage,
        s -> s.queenStack){

    }.allowInventoryInjection(input -> {
            World w = getBaseMetaTileEntity().getWorld();
            if (input.getItem() instanceof ICraftingPatternItem i) {
                SteamApiaryModule.BeeSimulator bs = new SteamApiaryModule.BeeSimulator(input, w, 6);
                if (bs.isValid) {
                    mStorage.add(bs);
                    patterns.put(input,i.getPatternForItem(input,this.getBaseMetaTileEntity().getWorld()));
                    try {
                        this.getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this,this.getProxy().getNode()));
                    } catch (GridAccessException ignored) {

                    }
                    return input;
                }
            }
            return null;
        })
            .allowInventoryExtraction(stack -> {
                var out = mStorage.remove(stack);
                if (out != null) {
                    patterns.remove(out.queenStack);
                    try {
                        this.getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this,this.getProxy().getNode()));
                    } catch (GridAccessException ignored) {

                    }
                }
                return out;
            })
            .allowInventoryReplace((i, stack) -> {
                if (stack.stackSize != 1) return null;
                World w = getBaseMetaTileEntity().getWorld();
                if (stack.getItem() instanceof ICraftingPatternItem ic) {
                    SteamApiaryModule.BeeSimulator bs = new SteamApiaryModule.BeeSimulator(stack, w, 6);
                    if (bs.isValid) {
                        SteamApiaryModule.BeeSimulator removed = mStorage.remove(i);
                        patterns.remove(removed.queenStack);
                        mStorage.add(bs);
                        patterns.put(stack, ic.getPatternForItem(stack, this.getBaseMetaTileEntity().getWorld()));
                        try {
                            this.getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this,this.getProxy().getNode()));
                        } catch (GridAccessException ignored) {

                        }
                        return removed.queenStack;
                    }
                }
                return null;
            })
            .setEnabled(() -> this.mMaxProgresstime == 0);

    public boolean isInInventory = true;

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        isInInventory = !getBaseMetaTileEntity().isActive();
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(4, 4)
                .setSize(190, 85)
                .setEnabled(w -> !isInInventory));

        final int backgroundPadding = INVENTORY_BORDER_WIDTH * 2;
        builder.widget(
            new DrawableWidget().setDrawable(APIARY_INVENTORY_BACKGROUND)
                .setPos(INVENTORY_X - INVENTORY_BORDER_WIDTH, INVENTORY_Y - INVENTORY_BORDER_WIDTH)
                .setSize(INVENTORY_WIDTH + backgroundPadding, INVENTORY_HEIGHT + backgroundPadding)
                .setEnabled(w -> isInInventory));

        builder.widget(
            dynamicInventory.asWidget(builder, buildContext)
                .setPos(INVENTORY_X, INVENTORY_Y)
                .setEnabled(w -> isInInventory));

        builder.widget(
            new CycleButtonWidget().setToggle(() -> isInInventory, i -> isInInventory = i)
                .setTextureGetter(
                    i -> i == 0 ? new Text(StatCollector.translateToLocal("kubatech.gui.text.inventory"))
                        : new Text(StatCollector.translateToLocal("kubatech.gui.text.status")))
                .setBackground(GTUITextures.BUTTON_STANDARD)
                .setPos(140, 91)
                .setSize(55, 16));

        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        drawTexts(screenElements, null);
        builder.widget(
            new Scrollable().setVerticalScroll()
                .widget(screenElements)
                .setPos(10, 7)
                .setSize(182, 79)
                .setEnabled(w -> !isInInventory));

        builder.widget(createPowerSwitchButton(builder))
            .widget(createVoidExcessButton(builder))
            .widget(createInputSeparationButton(builder))
            .widget(createBatchModeButton(builder))
            .widget(createLockToSingleRecipeButton(builder))
            .widget(createStructureUpdateButton(builder));

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        patternUI.open(
            aPlayer,
            aBaseMetaTileEntity.getWorld(),
            aBaseMetaTileEntity.getXCoord(),
            aBaseMetaTileEntity.getYCoord(),
            aBaseMetaTileEntity.getZCoord());
        return true;
    }

    public ModularWindow createConfigurationWindow(final EntityPlayer player) {
        ModularWindow.Builder builder = ModularWindow.builder(200, 100);
        builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);

        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.OVERLAY_BUTTON_CYCLIC)
                .setPos(5, 5)
                .setSize(16, 16))
            .widget(new TextWidget(StatCollector.translateToLocal("kubatech.gui.text.configuration")).setPos(25, 9))
            .widget(
                ButtonWidget.closeWindowButton(true)
                    .setPos(185, 3));

        // 主模式选择：输入、输出、运行
        builder.widget(
            new Column().widget(
                new CycleButtonWidget().setLength(3)
                    .setGetter(() -> mPrimaryMode)
                    .setSetter(val -> {
                        if (this.mMaxProgresstime > 0) {
                            GTUtility.sendChatToPlayer(player, "Can't change mode when running !");
                            return;
                        }
                        mPrimaryMode = val;
                        if (!(player instanceof EntityPlayerMP)) return;
                        switch (mPrimaryMode) {
                            case 0:
                                GTUtility.sendChatToPlayer(player, "Changed primary mode to: Input mode");
                                break;
                            case 1:
                                GTUtility.sendChatToPlayer(player, "Changed primary mode to: Output mode");
                                break;
                            case 2:
                                GTUtility.sendChatToPlayer(player, "Changed primary mode to: Operating mode");
                                break;
                        }
                    })
                    .addTooltip(
                        0,
                        new Text(StatCollector.translateToLocal("kubatech.gui.text.input")).color(Color.YELLOW.dark(3)))
                    .addTooltip(
                        1,
                        new Text(StatCollector.translateToLocal("kubatech.gui.text.output"))
                            .color(Color.YELLOW.dark(3)))
                    .addTooltip(
                        2,
                        new Text(StatCollector.translateToLocal("kubatech.gui.text.operating"))
                            .color(Color.GREEN.dark(3)))
                    .setTextureGetter(
                        i -> i == 0
                            ? new Text(StatCollector.translateToLocal("kubatech.gui.text.input"))
                                .color(Color.YELLOW.dark(3))
                            : i == 1
                                ? new Text(StatCollector.translateToLocal("kubatech.gui.text.output"))
                                    .color(Color.YELLOW.dark(3))
                                : new Text(StatCollector.translateToLocal("kubatech.gui.text.operating"))
                                    .color(Color.GREEN.dark(3)))
                    .setBackground(
                        ModularUITextures.VANILLA_BACKGROUND,
                        GTUITextures.OVERLAY_BUTTON_CYCLIC.withFixedSize(18, 18))
                    .setSize(70, 18)
                    .addTooltip(StatCollector.translateToLocal("kubatech.gui.text.mia.primary_mode")))
                .setEnabled(widget -> !getBaseMetaTileEntity().isActive())
                .setPos(10, 30));

        builder.widget(
            new Column().widget(
                new TextWidget(StatCollector.translateToLocal("kubatech.gui.text.mia.primary_mode")).setSize(100, 18))
                .setEnabled(widget -> !getBaseMetaTileEntity().isActive())
                .setPos(100, 30));

        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.OVERLAY_BUTTON_CROSS)
                .setSize(18, 18)
                .setPos(10, 30)
                .addTooltip(
                    new Text(StatCollector.translateToLocal("GT5U.gui.text.cannot_change_when_running"))
                        .color(Color.RED.dark(3)))
                .setEnabled(widget -> getBaseMetaTileEntity().isActive()));

        return builder.build();
    }

    public <Y> void tryOutputAll(@Nullable List<Y> list, @Nullable Function<Y, List<ItemStack>> mappingFunction) {
        if (list == null || list.isEmpty() || mappingFunction == null) return;
        int emptySlots = 0;
        boolean ignoreEmptiness = false;
        for (MTEHatchOutputBus i : mOutputBusses) {
            if (i instanceof MTEHatchOutputBusME) {
                ignoreEmptiness = true;
                break;
            }
            for (int j = 0; j < i.getSizeInventory(); j++)
                if (i.isValidSlot(j)) if (i.getStackInSlot(j) == null) emptySlots++;
        }
        if (emptySlots == 0 && !ignoreEmptiness) return;
        while (!list.isEmpty()) {
            List<ItemStack> toOutputNow = mappingFunction.apply(list.get(0));
            if (toOutputNow == null) {
                list.remove(0);
                continue;
            }
            if (!ignoreEmptiness && emptySlots < toOutputNow.size()) break;
            emptySlots -= toOutputNow.size();
            list.remove(0);
            for (ItemStack stack : toOutputNow) {
                addOutput(stack);
            }
        }
    }
}
