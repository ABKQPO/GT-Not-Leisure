package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.metatileentity.BaseTileEntity.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.ChangeableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.LargeInventoryCrafting;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.core.localization.WailaText;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IMEConnectable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.Getter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class AssemblerMatrix extends MultiMachineBase<AssemblerMatrix>
    implements IInterfaceHost, IGridProxyable, IAEAppEngInventory, IMEConnectable {

    public static int eachPatternCasingCapacity = 72;
    public static int eachCraftingCasingParallel = 2048;
    public static int eachSingularityCraftingCasingParallel = Integer.MAX_VALUE;
    public static final int MODE_INPUT = 0;
    public static final int MODE_OUTPUT = 1;
    public static final int MODE_OPERATING = 2;

    public int mCountPatternCasing = -1;
    public int mCountCrafterCasing = -1;
    public int mCountSingularityCrafterCasing = -1;
    public int mCountSpeedCasing = -1;
    public int mMaxSlots = 0;
    public long usedParallel = 0;
    public long mMaxParallelLong = 0;

    private AENetworkProxy gridProxy;
    private DualityInterface di;
    private final MachineSource source = new MachineSource(this);
    private final Map<ItemStack, ICraftingPatternDetails> patterns = new Reference2ObjectOpenHashMap<>();
    @Getter
    private final Set<IAEItemStack> possibleOutputs = new ObjectOpenHashSet<>();
    @Getter
    private final CombinationPatternsIInventory inventory = new CombinationPatternsIInventory();

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

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        this.getInterfaceDuality()
            .notifyNeighbors();
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        this.getInterfaceDuality()
            .notifyNeighbors();
    }

    public boolean isPowered() {
        return getProxy() != null && getProxy().isPowered();
    }

    public boolean isActive() {
        return getProxy() != null && getProxy().isActive();
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        boolean isActive = tag.getBoolean("isAEActive");
        boolean isPowered = tag.getBoolean("isAEPowered");
        currenttip.add(WailaText.getPowerState(isActive, isPowered, false));
        if (tag.getLong("maxParallelLong") > 1) {
            currenttip.add(
                StatCollector.translateToLocal("GT5U.multiblock.parallelism") + " (Long): "
                    + EnumChatFormatting.WHITE
                    + tag.getLong("maxParallelLong"));
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        boolean isActive = isActive();
        boolean isPowered = isPowered();
        tag.setBoolean("isAEActive", isActive);
        tag.setBoolean("isAEPowered", isPowered);
        tag.setLong("maxParallelLong", mMaxParallelLong);
    }

    /**
     * 向合成网络提供样板
     */
    @Override
    public void provideCrafting(ICraftingProviderHelper craftingTracker) {
        if (mMachine && this.getProxy()
            .isActive() && !patterns.isEmpty()) {
            for (var value : patterns.values()) {
                craftingTracker.addCraftingOption(this, value);
            }
        }
    }

    /**
     * 样板背包更新回调，用于刷新样板
     */
    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation operation, ItemStack removedStack,
        ItemStack newStack) {
        boolean work = false;
        if (removedStack != null) {
            var i = patterns.remove(removedStack);
            if (i != null) {
                possibleOutputs.remove(i.getCondensedOutputs()[0]);
            }
            work = true;
        }
        if (newStack != null) {
            if (newStack.getItem() instanceof ICraftingPatternItem ic) {
                var p = ic.getPatternForItem(
                    newStack,
                    this.getBaseMetaTileEntity()
                        .getWorld());
                if (p.isCraftable()) {
                    patterns.put(newStack, p);
                    possibleOutputs.add(p.getCondensedOutputs()[0]);
                    work = true;
                }
            }
        }
        if (work) {
            try {
                this.getProxy()
                    .getGrid()
                    .postEvent(
                        new MENetworkCraftingPatternChange(
                            this,
                            this.getProxy()
                                .getNode()));
            } catch (GridAccessException ignored) {

            }
        }
    }

    private final Queue<IAEItemStack> outputs = new ArrayDeque<>();
    private final Queue<IAEItemStack> inputs = new ArrayDeque<>();
    public IAEItemStack[] cachedOutputItems = null;

    @Override
    public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        final var out = patternDetails.getCondensedOutputs()[0];
        final var p = ((LargeInventoryCrafting) table).getAssemblerSize();
        for (int i = 0; i < table.getSizeInventory(); i++) {
            final var stack = table.getStackInSlot(i);
            if (stack != null) {
                final var c = getContainerItem(stack);
                if (c != null) {
                    inputs.add(
                        AEItemStack.create(c)
                            .setStackSize(p));
                }
                stack.stackSize = 1;
            }
        }
        outputs.add(
            out.copy()
                .setStackSize(out.getStackSize() * p));
        return true;
    }

    // 检查物品是否在输入消耗后有返回物
    private ItemStack getContainerItem(ItemStack stack) {
        final var i = stack.getItem();
        if (i == null) return null;
        if (!i.hasContainerItem(stack)) return null;
        final ItemStack ci = i.getContainerItem(stack.copy());
        if (ci != null && ci.isItemStackDamageable() && ci.getItemDamage() > ci.getMaxDamage()) {
            return null;
        }

        return ci;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (checkStructure(true)) {
            this.mStartUpCheck = -1;
            this.mUpdate = 200;
        }
        getProxy().onReady();
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        final ChangeableWidget recipeOutputItemsWidget = new ChangeableWidget(this::generateCurrentRecipeInfoWidget);

        // Display current recipe
        screenElements.widget(
            new FakeSyncWidget.ListSyncer<>(
                () -> cachedOutputItems != null ? ObjectArrayList.wrap(cachedOutputItems)
                    : ObjectLists.emptyList(),
                val -> {
                    cachedOutputItems = val.toArray(new IAEItemStack[0]);
                    recipeOutputItemsWidget.notifyChangeNoSync();
                },
                AssemblerMatrix::writeAEItemStack,
                AssemblerMatrix::loadAEItemStack));
        screenElements.widget(recipeOutputItemsWidget);
    }

    private static IAEItemStack loadAEItemStack(PacketBuffer buffer) {
        try {
            return AEItemStack.loadItemStackFromPacket(buffer);
        } catch (IOException e) {
            return null;
        }
    }

    private static void writeAEItemStack(PacketBuffer buffer, @NotNull IAEItemStack stack) {
        try {
            stack.writeToPacket(buffer);
        } catch (IOException ignored) {

        }
    }

    @Override
    public Widget generateCurrentRecipeInfoWidget() {
        final DynamicPositionedColumn processingDetails = new DynamicPositionedColumn();

        if (cachedOutputItems != null) {
            final Reference2LongMap<IAEItemStack> nameToAmount = new Reference2LongOpenHashMap<>();

            for (IAEItemStack item : cachedOutputItems) {
                if (item == null || item.getStackSize() <= 0) continue;
                nameToAmount.merge(item, item.getStackSize(), Long::sum);
            }

            final List<Reference2LongMap.Entry<IAEItemStack>> sortedMap = nameToAmount.reference2LongEntrySet()
                .stream()
                .sorted(
                    ((Comparator<Reference2LongMap.Entry<IAEItemStack>> & Serializable) (c1, c2) -> Long
                        .compare(c1.getLongValue(), c2.getLongValue())).reversed())
                .collect(Collectors.toCollection(ObjectArrayList::new));

            for (Reference2LongMap.Entry<IAEItemStack> entry : sortedMap) {
                long itemCount = entry.getLongValue();
                String itemName = entry.getKey()
                    .getItemStack()
                    .getDisplayName();
                String itemAmountString = EnumChatFormatting.WHITE + " x "
                    + EnumChatFormatting.GOLD
                    + formatShortenedLong(itemCount)
                    + EnumChatFormatting.WHITE
                    + appendRate(false, itemCount, true);
                String lineText = EnumChatFormatting.AQUA + truncateText(itemName, 40 - itemAmountString.length())
                    + itemAmountString;
                String lineTooltip = EnumChatFormatting.AQUA + itemName + "\n" + appendRate(false, itemCount, false);

                processingDetails.widget(
                    new MultiChildWidget().addChild(
                        new ItemDrawable(
                            entry.getKey()
                                .getItemStack()
                                .copy()).asWidget()
                                    .setSize(8, 8)
                                    .setPos(0, 0))
                        .addChild(
                            new TextWidget(lineText).setTextAlignment(Alignment.CenterLeft)
                                .addTooltip(lineTooltip)
                                .setPos(10, 1)));
            }
        }
        return processingDetails;
    }

    /**
     * 是否可被发配
     */
    @Override
    public boolean isBusy() {
        return !mMachine && machineMode == MODE_OPERATING;
    }

    @Override
    public int getMaxParallelRecipes() {
        mMaxParallelLong = (long) eachCraftingCasingParallel * mCountCrafterCasing
            + (long) eachSingularityCraftingCasingParallel * mCountSingularityCrafterCasing;
        mMaxSlots = eachPatternCasingCapacity * mCountPatternCasing;

        return (int) mMaxParallelLong;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        super.onScrewdriverRightClick(side, aPlayer, aX, aY, aZ, aTool);
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatToPlayer(aPlayer, "Can't change mode when running !");
            return;
        }
        this.machineMode = (this.machineMode + 1) % 3;
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("AssemblerMatrix_Mode_" + this.machineMode));
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public ButtonWidget createModeSwitchButton(IWidgetBuilder<?> builder) {
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            if (mMaxProgresstime > 0) return;
            onMachineModeSwitchClick();
            setMachineMode(nextMachineMode());
        })
            .setPlayClickSound(mMaxProgresstime <= 0)
            .setBackground(() -> {
                if (mMaxProgresstime > 0) {
                    return new IDrawable[] { GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_RECIPE_LOCKED };
                } else {
                    return new IDrawable[] { GTUITextures.BUTTON_STANDARD, getMachineModeIcon(getMachineMode()) };
                }
            })
            .attachSyncer(new FakeSyncWidget.IntegerSyncer(this::getMachineMode, this::setMachineMode), builder)
            .dynamicTooltip(
                () -> ImmutableList.of(
                    StatCollector.translateToLocal(
                        mMaxProgresstime > 0 ? "GT5U.gui.button.forbidden" : "GT5U.gui.button.mode_switch")))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(getMachineModeSwitchButtonPos())
            .setSize(16, 16);

        button.attachSyncer(
            new FakeSyncWidget.IntegerSyncer(() -> mMaxProgresstime, newInt -> mMaxProgresstime = newInt),
            builder,
            (widget, val) -> widget.notifyTooltipChange());
        return (ButtonWidget) button;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
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
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public void setMachineMode(int index) {
        super.setMachineMode(index);
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == MODE_INPUT) return MODE_OUTPUT;
        else if (machineMode == MODE_OUTPUT) return MODE_OPERATING;
        else return MODE_INPUT;
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_UNPACKAGER);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("AssemblerMatrix_Mode_" + machineMode);
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        saveInvData(aNBT, true);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mCountCrafterCasing", mCountCrafterCasing);
        aNBT.setInteger("mCountSingularityCrafterCasing", mCountSingularityCrafterCasing);
        aNBT.setInteger("mCountPatternCasing", mCountPatternCasing);
        aNBT.setInteger("mCountSpeedCasing", mCountSpeedCasing);
        aNBT.setLong("mMaxParallelLong", mMaxParallelLong);
        saveInvData(aNBT, false);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveInvData(NBTTagCompound aNBT, boolean external) {
        NBTTagCompound storeRoot = new NBTTagCompound();

        String uuid = Utils.ensureUUID(aNBT);

        // cachedOutputItems
        NBTTagList cachedList = new NBTTagList();
        if (cachedOutputItems != null) {
            for (IAEItemStack item : cachedOutputItems) {
                if (item != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    item.writeToNBT(tag);
                    cachedList.appendTag(tag);
                }
            }
        }
        storeRoot.setTag("CACHED_OUTPUT_ITEMS", cachedList);

        // outputs
        NBTTagList outputList = new NBTTagList();
        if (outputs != null && !outputs.isEmpty()) {
            for (IAEItemStack stack : outputs) {
                if (stack != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    stack.writeToNBT(tag);
                    outputList.appendTag(tag);
                }
            }
        }
        storeRoot.setTag("OUTPUT_ITEMS", outputList);

        // inputs
        NBTTagList inputList = new NBTTagList();
        if (inputs != null && !inputs.isEmpty()) {
            for (IAEItemStack stack : inputs) {
                if (stack != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    stack.writeToNBT(tag);
                    inputList.appendTag(tag);
                }
            }
        }
        storeRoot.setTag("INPUT_ITEMS", inputList);

        // inventory
        NBTTagCompound invTag = new NBTTagCompound();
        inventory.saveNBTData(invTag);
        storeRoot.setTag("INVENTORY", invTag);

        if (external) {
            File worldDir = DimensionManager.getCurrentSaveRootDirectory();
            File dataDir = new File(worldDir, "data");
            if (!dataDir.exists()) dataDir.mkdirs();

            File storeFile = new File(dataDir, "AssemblerMatrix_" + uuid + ".dat");
            try {
                CompressedStreamTools.safeWrite(storeRoot, storeFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            aNBT.setTag("CrafterInv", storeRoot);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mCountSpeedCasing = aNBT.getInteger("mCountSpeedCasing");
        mCountSingularityCrafterCasing = aNBT.getInteger("mCountSingularityCrafterCasing");
        mCountCrafterCasing = aNBT.getInteger("mCountCrafterCasing");
        mCountPatternCasing = aNBT.getInteger("mCountPatternCasing");
        usedParallel = aNBT.getLong("usedParallel");
        mMaxParallelLong = aNBT.getLong("mMaxParallelLong");

        NBTTagCompound storeRoot = null;

        if (aNBT.hasKey("storeUUID")) {
            String uuid = aNBT.getString("storeUUID");
            try {
                File worldDir = DimensionManager.getCurrentSaveRootDirectory();
                File dataDir = new File(worldDir, "data");
                File storeFile = new File(dataDir, "AssemblerMatrix_" + uuid + ".dat");

                if (storeFile.exists()) {
                    storeRoot = CompressedStreamTools.read(storeFile);
                    if (!storeFile.delete()) {
                        System.err.println("Warning: Failed to delete CrafterInv file " + storeFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (storeRoot == null && aNBT.hasKey("CrafterInv")) {
            storeRoot = aNBT.getCompoundTag("CrafterInv");
        }

        if (storeRoot != null) {
            // cachedOutputItems
            NBTTagList cachedList = storeRoot.getTagList("CACHED_OUTPUT_ITEMS", 10);
            if (cachedList != null && cachedList.tagCount() > 0) {
                cachedOutputItems = new IAEItemStack[cachedList.tagCount()];
                for (int i = 0; i < cachedList.tagCount(); i++) {
                    cachedOutputItems[i] = AEItemStack.loadItemStackFromNBT(cachedList.getCompoundTagAt(i));
                }
            }

            // outputs
            NBTTagList outputList = storeRoot.getTagList("OUTPUT_ITEMS", 10);
            if (outputList != null && outputList.tagCount() > 0) {
                for (int i = 0; i < outputList.tagCount(); i++) {
                    IAEItemStack aeStack = AEItemStack.loadItemStackFromNBT(outputList.getCompoundTagAt(i));
                    if (aeStack != null) outputs.add(aeStack);
                }
            }

            // inputs
            NBTTagList inputList = storeRoot.getTagList("INPUT_ITEMS", 10);
            if (inputList != null && inputList.tagCount() > 0) {
                for (int i = 0; i < inputList.tagCount(); i++) {
                    IAEItemStack aeStack = AEItemStack.loadItemStackFromNBT(inputList.getCompoundTagAt(i));
                    if (aeStack != null) inputs.add(aeStack);
                }
            }

            // inventory
            if (storeRoot.hasKey("INVENTORY")) {
                inventory.loadNBTData(storeRoot.getCompoundTag("INVENTORY"));
            }
        }

        updateAE2ProxyColor();
        updateValidGridProxySides();
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
        final var old = mMaxSlots;
        setupParameters();
        if (mMaxSlots != old) upPatterns();
        getProxy().setValidSides(allDirection);
        return true;
    }

    public void upPatterns() {
        patterns.clear();
        possibleOutputs.clear();

        for (var newStack : this.inventory.getAllItemsCopy()) {
            if (newStack.getItem() instanceof ICraftingPatternItem ic) {
                var p = ic.getPatternForItem(
                    newStack,
                    AssemblerMatrix.this.getBaseMetaTileEntity()
                        .getWorld());
                if (p.isCraftable()) {
                    patterns.put(newStack, p);
                    possibleOutputs.add(p.getCondensedOutputs()[0]);
                }
            }
        }
        try {
            AssemblerMatrix.this.getProxy()
                .getGrid()
                .postEvent(
                    new MENetworkCraftingPatternChange(
                        AssemblerMatrix.this,
                        AssemblerMatrix.this.getProxy()
                            .getNode()));
        } catch (GridAccessException ignored) {

        }
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        mMaxParallelLong = (long) eachCraftingCasingParallel * mCountCrafterCasing
            + (long) eachSingularityCraftingCasingParallel * mCountSingularityCrafterCasing;
        mMaxSlots = eachPatternCasingCapacity * mCountPatternCasing;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mCountPatternCasing = 0;
        mCountCrafterCasing = 0;
        mCountSingularityCrafterCasing = 0;
        mCountSpeedCasing = 0;
        mMaxParallelLong = 0;
        mMaxSlots = 0;
        patterns.clear();
        possibleOutputs.clear();
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch()
            && mCountCasing + mCountPatternCasing
                + mCountCrafterCasing
                + mCountSingularityCrafterCasing
                + mCountSpeedCasing == 343
            && mCountSpeedCasing <= 5;
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
                    onElementPass(t -> t.mCountCasing++, ofBlock(BlockLoader.metaCasing02, 5)),
                    onElementPass(t -> t.mCountPatternCasing++, ofBlock(BlockLoader.metaCasing02, 6)),
                    onElementPass(t -> t.mCountCrafterCasing++, ofBlock(BlockLoader.metaCasing02, 7)),
                    onElementPass(t -> t.mCountSingularityCrafterCasing++, ofBlock(BlockLoader.metaCasing02, 8)),
                    onElementPass(t -> t.mCountSpeedCasing++, ofBlock(BlockLoader.metaCasing02, 9))))
            .build();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_ME_HATCH)
                    .extFacing()
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
        if (machineMode < 2) {
            if (machineMode == MODE_INPUT && inventory.size() < mMaxSlots) {
                List<ItemStack> inputs = getStoredInputs();
                boolean updated = false;

                for (ItemStack input : inputs) {
                    if (!(input.getItem() instanceof ICraftingPatternItem i)) continue;
                    int slot = inventory.getFirstEmptySlot();
                    if (slot == -1) continue;
                    ItemStack pattern = input.copy();
                    pattern.stackSize = 1;
                    inventory.setInventorySlotContents(slot, pattern);
                    var p = i.getPatternForItem(
                        input,
                        this.getBaseMetaTileEntity()
                            .getWorld());
                    if (!p.isCraftable()) continue;
                    patterns.put(pattern, p);
                    possibleOutputs.add(p.getCondensedOutputs()[0]);

                    input.stackSize--;
                    updated = true;
                    if (inventory.size() >= mMaxSlots) break;
                }
                if (updated) {
                    updateSlots();
                    try {
                        this.getProxy()
                            .getGrid()
                            .postEvent(
                                new MENetworkCraftingPatternChange(
                                    this,
                                    this.getProxy()
                                        .getNode()));
                    } catch (GridAccessException ignored) {}
                }
                updateSlots();
            } else if (machineMode == MODE_OUTPUT && !inventory.isEmpty()) { // output mode
                tryOutputInventory(inventory);
            } else {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
            mMaxProgresstime = 10;
            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            lEUt = 0;
            return CheckRecipeResultRegistry.SUCCESSFUL;
        } else if (isActive() && machineMode == MODE_OPERATING) {
            if (mMaxSlots > 0 && !inventory.isEmpty() && !outputs.isEmpty()) {
                long parallel = mMaxParallelLong;
                parallel = Math.min(parallel, getMaxInputEu() / 2);
                int maximum = outputs.size();
                usedParallel = 0L;

                if (!inputs.isEmpty()) {
                    var grid = getProxy().getNode()
                        .getGrid();
                    IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
                    IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                    var storage = storageGrid.getItemInventory();
                    final var s = inputs.size();
                    for (int i = 0; i < s; i++) {
                        var in = inputs.poll();
                        if (in == null) continue;
                        var leftover = Platform.poweredInsert(energyGrid, storage, in, source);
                        if (leftover != null) inputs.add(leftover);
                    }
                }

                List<IAEItemStack> preparedOutputs = new ObjectArrayList<>(maximum);

                IAEItemStack stack;
                while (parallel > 0 && (stack = outputs.poll()) != null) {
                    long stackSize = stack.getStackSize();
                    if (stackSize <= parallel) {
                        parallel -= stackSize;
                        usedParallel += stackSize;

                        preparedOutputs.add(stack);
                    } else {
                        long remain = stackSize - parallel;
                        usedParallel += parallel;
                        stack.decStackSize(parallel);
                        preparedOutputs.add(
                            stack.copy()
                                .setStackSize(parallel));

                        if (remain > 0) {
                            var remainStack = stack.copy();
                            remainStack.setStackSize(remain);
                            outputs.add(remainStack);
                        }

                        parallel = 0;
                    }

                    if (outputs.isEmpty() || --maximum == 0) break;
                }

                if (!preparedOutputs.isEmpty()) {
                    this.cachedOutputItems = preparedOutputs.toArray(new IAEItemStack[preparedOutputs.size()]);
                    this.lEUt = -2 * Math.max(1, usedParallel);
                    this.mEfficiency = 10000;
                    this.mEfficiencyIncrease = 10000;
                    this.mMaxProgresstime = Math.max(1, 40 >> mCountSpeedCasing);
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }
            }
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public void outputAfterRecipe() {
        super.outputAfterRecipe();
        if (cachedOutputItems == null || cachedOutputItems.length == 0 || usedParallel == 0) return;

        try {
            var grid = getProxy().getNode()
                .getGrid();
            IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
            IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
            var storage = storageGrid.getItemInventory();

            long remainingParallel = usedParallel;

            for (IAEItemStack stack : cachedOutputItems) {
                if (remainingParallel <= 0) break;

                long toInsert = Math.min(remainingParallel, stack.getStackSize());
                if (toInsert <= 0) continue;

                IAEItemStack insertStack = stack;
                if (stack.getStackSize() != toInsert) {
                    insertStack = stack.copy();
                    insertStack.setStackSize(toInsert);
                }

                var leftover = Platform.poweredInsert(energyGrid, storage, insertStack, source);

                remainingParallel -= toInsert;

                if (leftover != null && leftover.getStackSize() != 0) {
                    outputs.add(leftover);
                }
            }
        } finally {
            cachedOutputItems = new IAEItemStack[0];
            usedParallel = 0;
        }
    }

    @Override
    public void stopMachine(@NotNull ShutDownReason reason) {
        super.stopMachine(reason);
        cachedOutputItems = new IAEItemStack[0];
        usedParallel = 0;
    }

    @Override
    public String[] getInfoData() {
        List<String> info = new ObjectArrayList<>(super.getInfoData());
        info.add(
            StatCollector.translateToLocal("kubatech.infodata.running_mode") + " "
                + EnumChatFormatting.GOLD
                + (machineMode == 0 ? StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.input")
                    : (machineMode == 1 ? StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.output")
                        : StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.operating.normal"))));
        info.add(
            StatCollector.translateToLocalFormatted(
                "Info_AssemblerMatrix_00",
                "" + EnumChatFormatting.GOLD + inventory.size() + EnumChatFormatting.RESET,
                (inventory.size() > mMaxSlots ? EnumChatFormatting.DARK_RED.toString()
                    : EnumChatFormatting.GOLD.toString()) + mMaxSlots + EnumChatFormatting.RESET));
        return info.toArray(new String[0]);
    }

    public static final EnumSet<ForgeDirection> allDirection = EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN));
    public static final EnumSet<ForgeDirection> emptyDirection = EnumSet.noneOf(ForgeDirection.class);

    @Override
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            var bmte = getBaseMetaTileEntity();
            if (bmte instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(this, "proxy", GTNLItemList.AssemblerMatrix.get(1), true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                updateValidGridProxySides();
                if (bmte.getWorld() != null) {
                    gridProxy.setOwner(
                        bmte.getWorld()
                            .getPlayerEntityByName(bmte.getOwnerName()));
                }
            }
        }
        return gridProxy;
    }

    @Override
    public void onFacingChange() {
        super.onFacingChange();
        updateValidGridProxySides();
    }

    public void updateValidGridProxySides() {
        if (mMachine) {
            getProxy().setValidSides(allDirection);
        } else {
            getProxy().setValidSides(emptyDirection);
        }
    }

    @Override
    public DualityInterface getInterfaceDuality() {
        if (di == null) {
            di = new DualityInterface(this.getProxy(), this);
        }
        return di;
    }

    @Override
    public EnumSet<ForgeDirection> getTargets() {
        return emptyDirection;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(
            getBaseMetaTileEntity().getWorld(),
            getBaseMetaTileEntity().getXCoord(),
            getBaseMetaTileEntity().getYCoord(),
            getBaseMetaTileEntity().getZCoord());
    }

    @Override
    public TileEntity getTileEntity() {
        return (TileEntity) getBaseMetaTileEntity();
    }

    @Override
    public void saveChanges() {
        this.getInterfaceDuality()
            .saveChanges();
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
        return inventory;
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
        if (name.equals("patterns")) {
            return this.inventory;
        }
        return this.getInterfaceDuality()
            .getInventoryByName(name);
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        return this.getProxy()
            .getNode();
    }

    @Override
    public void onColorChangeServer(byte aColor) {
        updateAE2ProxyColor();
    }

    public void updateAE2ProxyColor() {
        AENetworkProxy proxy = getProxy();
        byte color = this.getColor();
        if (color == -1) {
            proxy.setColor(AEColor.Transparent);
        } else {
            proxy.setColor(AEColor.values()[Dyes.transformDyeIndex(color)]);
        }
        if (proxy.getNode() != null) {
            proxy.getNode()
                .updateState();
        }
    }

    @Override
    public void securityBreak() {}

    @Override
    public ItemStack getCrafterIcon() {
        return GTNLItemList.AssemblerMatrix.get(1);
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.getInterfaceDuality()
            .getRequestedJobs();
    }

    @Override
    public IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack items, Actionable mode) {
        return this.getInterfaceDuality()
            .injectCraftedItems(link, items, mode);
    }

    @Override
    public void jobStateChange(ICraftingLink link) {
        this.getInterfaceDuality()
            .jobStateChange(link);
    }

    @Override
    public IGridNode getActionableNode() {
        AENetworkProxy gp = getProxy();
        return gp != null ? gp.getNode() : null;
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection forgeDirection) {
        return AECableType.SMART;
    }

    @Override
    public boolean connectsToAllSides() {
        return true;
    }

    @Override
    public void setConnectsToAllSides(boolean connects) {}

    @Override
    public IConfigManager getConfigManager() {
        return this.getInterfaceDuality()
            .getConfigManager();
    }

    @Override
    public String getName() {
        return getCrafterIcon().getDisplayName();
    }

    @Override
    public ItemStack getDisplayRep() {
        return getSelfRep();
    }

    public void tryOutputInventory(IInventory inventory) {
        int emptySlots = 0;
        boolean ignoreEmptiness = false;

        for (MTEHatchOutputBus i : mOutputBusses) {
            if (i instanceof MTEHatchOutputBusME) {
                ignoreEmptiness = true;
                break;
            }
            for (int j = 0; j < i.getSizeInventory(); j++) {
                if (i.isValidSlot(j) && i.getStackInSlot(j) == null) {
                    emptySlots++;
                }
            }
        }

        if (emptySlots == 0 && !ignoreEmptiness) return;

        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;

            if (!ignoreEmptiness && emptySlots < 1) break;

            addOutput(stack);

            emptySlots--;

            inventory.setInventorySlotContents(slot, null);
        }

        try {
            this.getProxy()
                .getGrid()
                .postEvent(
                    new MENetworkCraftingPatternChange(
                        this,
                        this.getProxy()
                            .getNode()));
        } catch (GridAccessException ignored) {}
    }

    public class CombinationPatternsIInventory implements IInventory, Iterable<ItemStack> {

        private AppEngInternalInventory[] combinationInventory = new AppEngInternalInventory[0];

        private AppEngInternalInventory getInventory(int ordinal) {
            if (ordinal >= combinationInventory.length) {
                combinationInventory = Arrays.copyOf(combinationInventory, ordinal + 1);
            }
            var i = combinationInventory[ordinal];
            if (i == null) {
                combinationInventory[ordinal] = i = new AppEngInternalInventory(
                    AssemblerMatrix.this,
                    eachPatternCasingCapacity,
                    1);
            }
            return i;
        }

        @Override
        public int getSizeInventory() {
            return AssemblerMatrix.this.mMaxSlots;
        }

        @Override
        public ItemStack getStackInSlot(int slotIn) {
            size = -1;
            return packItem(
                getInventory(slotIn / eachPatternCasingCapacity).getStackInSlot(slotIn % eachPatternCasingCapacity));
        }

        @Override
        public ItemStack decrStackSize(int slot, int count) {
            size = -1;
            return packItem(
                getInventory(slot / eachPatternCasingCapacity).decrStackSize(slot % eachPatternCasingCapacity, count));
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            size = -1;
            return packItem(
                getInventory(slot / eachPatternCasingCapacity)
                    .getStackInSlotOnClosing(slot % eachPatternCasingCapacity));
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            size = -1;
            getInventory(slot / eachPatternCasingCapacity)
                .setInventorySlotContents(slot % eachPatternCasingCapacity, stack);
        }

        @Override
        public String getInventoryName() {
            return "patterns";
        }

        @Override
        public boolean hasCustomInventoryName() {
            return false;
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return true;
        }

        @Override
        public void openInventory() {

        }

        @Override
        public void closeInventory() {

        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            size = -1;
            return getInventory(slot / eachPatternCasingCapacity)
                .isItemValidForSlot(slot % eachPatternCasingCapacity, stack);
        }

        public void saveNBTData(NBTTagCompound aNBT) {
            if (getBaseMetaTileEntity().isServerSide()) {
                var n = new NBTTagCompound();
                for (var i = 0; i < combinationInventory.length; i++) {
                    var inv = combinationInventory[i];
                    if (inv != null) {
                        inv.writeToNBT(n, Integer.toString(i));
                    }
                }
                aNBT.setTag("patterns", n);
            }
        }

        public void loadNBTData(NBTTagCompound aNBT) {
            var n = aNBT.getCompoundTag("patterns");
            for (var o : n.func_150296_c()) {
                getInventory(Integer.parseInt(o)).readFromNBT(n.getCompoundTag(o));
            }
            AssemblerMatrix.this.upPatterns();
        }

        private int size = -1;

        public int size() {
            if (size < 0) {
                size = 0;
                for (ItemStack inv : this) {
                    ++size;
                }
            }
            return size;
        }

        public boolean isEmpty() {
            return size() == 0;
        }

        private ItemStack packItem(ItemStack stack) {
            if (stack == null) return null;
            if (stack.stackSize <= 0) return null;
            return stack;
        }

        public List<ItemStack> getAllItemsCopy() {
            List<ItemStack> result = new ObjectArrayList<>();
            for (ItemStack stack : this) {
                result.add(stack);
            }
            return result;
        }

        public int getFirstEmptySlot() {
            for (int slot = 0; slot < getSizeInventory(); slot++) {
                if (getStackInSlot(slot) == null) {
                    return slot;
                }
            }
            return -1;
        }

        public boolean insertPattern(ItemStack stack) {
            var slot = getFirstEmptySlot();
            if (slot < 0) return false;
            this.setInventorySlotContents(slot, stack);
            return true;
        }

        @Override
        public @NotNull NoNullInvIteratot iterator() {
            return new NoNullInvIteratot();
        }

        public class NoNullInvIteratot implements Iterator<ItemStack> {

            private int invOrdinal = 0;
            private int slotOrdinal = -1;
            private int nowInv = -1;
            private int nowSlot = -1;
            private boolean nowAvailable = false;

            @Override
            public boolean hasNext() {
                upAvailable();
                return nowAvailable;
            }

            @Override
            public ItemStack next() {
                if (hasNext()) {
                    nowAvailable = false;
                    return CombinationPatternsIInventory.this.combinationInventory[nowInv = invOrdinal]
                        .getStackInSlot(nowSlot = slotOrdinal);
                }
                nowInv = -1;
                nowSlot = -1;
                return null;
            }

            @Override
            public void remove() {
                if (nowInv < 0) return;
                CombinationPatternsIInventory.this.combinationInventory[nowInv].setInventorySlotContents(nowSlot, null);
                nowInv = -1;
                nowSlot = -1;
            }

            private void upAvailable() {
                if (!nowAvailable) {
                    while (mMaxSlots >= (invOrdinal * eachPatternCasingCapacity + slotOrdinal + 1)) {
                        if (invOrdinal >= combinationInventory.length) {
                            slotOrdinal = eachPatternCasingCapacity;
                            break;
                        }
                        var inv = CombinationPatternsIInventory.this.combinationInventory[invOrdinal];
                        if (inv == null) {
                            ++invOrdinal;
                            continue;
                        }
                        while (++slotOrdinal < inv.getSizeInventory()) {
                            var stack = inv.getStackInSlot(slotOrdinal);
                            if (stack != null) {
                                nowAvailable = true;
                                return;
                            }
                        }
                        slotOrdinal = -1;
                        ++invOrdinal;
                    }
                    nowInv = -1;
                    nowSlot = -1;
                }
            }
        }
    }
}
