package com.science.gtnl.common.machine.hatch;

import java.util.Arrays;
import java.util.Objects;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSource;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.api.IRecipeProcessingAwareDualHatch;
import com.science.gtnl.api.mixinHelper.ISkipStackSizeCheck;
import com.science.gtnl.common.gui.modularui.HumongousDualInputHatchGui;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import gregtech.GTMod;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.gui.modularui.util.ProxiedItemHandlerModifiable;
import gregtech.common.inventory.AEInventory;

public class HumongousDualInputHatch extends DualInputHatch
    implements ISkipStackSizeCheck, IRecipeProcessingAwareDualHatch {

    private static final String ITEM_INVENTORY_NBT_KEY = "itemInventory";
    private static final String LEGACY_INVENTORY_NBT_KEY = "Inventory";

    private AEInventory itemInventory;
    private IItemHandlerModifiable humongousItemHandler;
    private int processing;
    private ItemStack[] originalStacks;
    private ItemStack[] containedStacks;

    public HumongousDualInputHatch(int id, String name, String nameRegional, int aTier) {
        super(id, name, nameRegional, aTier);
        initializeHumongousStorage(aTier);
    }

    public HumongousDualInputHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        initializeHumongousStorage(aTier);
    }

    private void initializeHumongousStorage(int tier) {
        this.mStoredFluid = new FluidStack[tier];
        this.fluidTanks = new FluidStackTank[tier];
        this.mCapacityPer = Integer.MAX_VALUE;
        mDescriptionArray[2] = StatCollector.translateToLocal("Tooltip_DualInputHatch_02_00")
            + NumberFormatUtil.formatNumber(tier)
            + StatCollector.translateToLocal("Tooltip_DualInputHatch_02_01")
            + NumberFormatUtil.formatNumber(mCapacityPer)
            + "L";

        for (int i = 0; i < tier; i++) {
            final int index = i;
            this.fluidTanks[i] = new FluidStackTank(
                () -> mStoredFluid[index],
                fluid -> mStoredFluid[index] = fluid,
                mCapacityPer);
        }

        this.itemInventory = new HumongousItemInventory(getItemStorageSlotCount());
        this.humongousItemHandler = createItemHandler();
        this.inventory = new HumongousInventory(mStoredFluid);
        this.disableSort = true;
    }

    private IItemHandlerModifiable createItemHandler() {
        return new ProxiedItemHandlerModifiable(itemInventory) {

            @Override
            public int getSlots() {
                return itemInventory.getSlots() + 1;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                if (slot == getCircuitSlot()) return mInventory[getCircuitSlot()];
                return super.getStackInSlot(slot);
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                if (slot == getCircuitSlot()) {
                    mInventory[getCircuitSlot()] = GTUtility.copyAmount(0, stack);
                    return;
                }
                super.setStackInSlot(slot, stack);
            }
        };
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HumongousDualInputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int getInventoryStackLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getStackSizeLimit(int slot, ItemStack stack) {
        if (!isItemStorageSlot(slot)) return super.getStackSizeLimit(slot, stack);
        return GTUtility.longToInt(itemInventory.getAESlotLimit(slot, AEItemStack.create(stack)));
    }

    @Override
    public int getSizeInventory() {
        return processing > 0 ? getItemStorageSlotCount() + 1 : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        if (slotIndex == getCircuitSlot()) return mInventory[getCircuitSlot()];
        if (processing > 0) return getArrayStack(containedStacks, slotIndex);
        IAEItemStack stack = itemInventory.getAEStackInSlot(slotIndex);
        return toItemStack(stack);
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack stack) {
        if (slotIndex == getCircuitSlot()) {
            mInventory[getCircuitSlot()] = GTUtility.copyAmount(0, stack);
            markDirty();
            return;
        }

        if (!isItemStorageSlot(slotIndex)) return;
        itemInventory.setStackInSlot(slotIndex, stack == null ? null : AEItemStack.create(stack));
        markDirty();
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        if (!isItemStorageSlot(index)) return super.decrStackSize(index, amount);
        if (processing > 0) return decrementContainedStack(index, amount);
        return itemInventory.extractItem(index, amount, false, true);
    }

    private ItemStack decrementContainedStack(int index, int amount) {
        ItemStack stack = getArrayStack(containedStacks, index);
        if (stack == null || amount <= 0) return null;

        int removed = Math.min(amount, stack.stackSize);
        ItemStack result = GTUtility.copyAmountUnsafe(removed, stack);
        stack.stackSize -= removed;
        if (stack.stackSize <= 0) containedStacks[index] = null;
        return result;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return isItemStorageSlot(aIndex);
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        if (!isItemStorageSlot(aIndex)) return false;
        if (mRecipeMap != null && !disableFilter && !mRecipeMap.containsInput(aStack)) return false;

        IAEItemStack existing = itemInventory.getAEStackInSlot(aIndex);
        if (existing != null) {
            if (existing.getStackSize() >= itemInventory.getAESlotLimit(aIndex, existing)) return false;
        }

        if (!disableLimited) {
            int containingSlot = itemInventory.indexOf(aStack);
            if (containingSlot != -1) return containingSlot == aIndex;
        }

        return existing == null || existing.isSameType(aStack);
    }

    @Override
    public IItemHandlerModifiable getInventoryHandler() {
        return humongousItemHandler;
    }

    @Override
    public void startRecipeProcessing() {
        if (processing == 0) {
            originalStacks = createRecipeSnapshot();
            containedStacks = copySnapshot(originalStacks);
        }
        processing++;
    }

    @Override
    public CheckRecipeResult endRecipeProcessing(MTEMultiBlockBase controller) {
        processing--;
        if (processing > 0) return CheckRecipeResultRegistry.SUCCESSFUL;

        if (processing < 0) {
            processing = 0;
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        for (int slotIndex = 0; slotIndex < getItemStorageSlotCount(); slotIndex++) {
            ItemStack original = getArrayStack(originalStacks, slotIndex);
            ItemStack contained = getArrayStack(containedStacks, slotIndex);
            if (original == null) continue;

            int delta = original.stackSize - (contained == null ? 0 : contained.stackSize);
            if (delta == 0) continue;

            if (delta < 0) {
                GTMod.GT_FML_LOGGER.error(
                    "Humongous dual input hatch has more recipe items than it started with; cancelling recipe (slot={}, original={}, contained={}, delta={})",
                    slotIndex,
                    original,
                    contained,
                    delta);
                controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                clearRecipeSnapshots();
                return CheckRecipeResultRegistry.CRASH;
            }

            IAEItemStack stored = itemInventory.getAEStackInSlot(slotIndex);
            if (stored == null || delta > stored.getStackSize()) {
                GTMod.GT_FML_LOGGER.error(
                    "Humongous dual input hatch consumed more items than available; cancelling recipe (slot={}, original={}, contained={}, delta={})",
                    slotIndex,
                    original,
                    contained,
                    delta);
                controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                clearRecipeSnapshots();
                return CheckRecipeResultRegistry.CRASH;
            }

            stored.decStackSize(delta);
            itemInventory.setStackInSlot(slotIndex, stored.getStackSize() == 0 ? null : stored);
        }

        clearRecipeSnapshots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public void trunOffME() {}

    @Override
    public void trunONME() {}

    private void clearRecipeSnapshots() {
        originalStacks = null;
        containedStacks = null;
    }

    private ItemStack[] createRecipeSnapshot() {
        ItemStack[] snapshot = new ItemStack[getItemStorageSlotCount()];
        for (int i = 0; i < snapshot.length; i++) {
            snapshot[i] = toItemStack(itemInventory.getAEStackInSlot(i));
        }
        return snapshot;
    }

    private ItemStack[] copySnapshot(ItemStack[] source) {
        if (source == null) return null;
        ItemStack[] copy = new ItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = GTUtility.copy(source[i]);
        }
        return copy;
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        if (!itemInventory.getStorageList()
            .isEmpty()) {
            aNBT.setTag(ITEM_INVENTORY_NBT_KEY, itemInventory.writeToNBT(new NBTTagCompound()));
        }
        writeFluidTanksToNBT(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setTag(ITEM_INVENTORY_NBT_KEY, itemInventory.writeToNBT(new NBTTagCompound()));
    }

    private void writeFluidTanksToNBT(NBTTagCompound tag) {
        if (mStoredFluid == null) return;

        for (int i = 0; i < mStoredFluid.length; i++) {
            FluidStack fluid = mStoredFluid[i];
            if (fluid != null) {
                tag.setTag("mFluid" + i, fluid.writeToNBT(new NBTTagCompound()));
            }
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        if (aNBT.hasKey(ITEM_INVENTORY_NBT_KEY)) {
            itemInventory.readFromNBT(aNBT.getCompoundTag(ITEM_INVENTORY_NBT_KEY));
        } else {
            loadLegacyItemInventory(aNBT);
            migrateLoadedStacksFromMachineInventory();
        }
    }

    private void loadLegacyItemInventory(NBTTagCompound tag) {
        if (!tag.hasKey(LEGACY_INVENTORY_NBT_KEY)) return;

        NBTTagList itemList = tag.getTagList(LEGACY_INVENTORY_NBT_KEY, 10);
        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
            int slot = itemTag.getInteger("IntSlot");
            ItemStack stack = readItemStackFromNBT(itemTag);
            if (slot == getCircuitSlot()) {
                mInventory[getCircuitSlot()] = stack;
            } else if (isItemStorageSlot(slot) && stack != null) {
                itemInventory.setStackInSlot(slot, AEItemStack.create(stack));
                mInventory[slot] = null;
            }
        }
    }

    private void migrateLoadedStacksFromMachineInventory() {
        for (int i = 0; i < getItemStorageSlotCount(); i++) {
            ItemStack stack = mInventory[i];
            if (stack == null) continue;
            itemInventory.setStackInSlot(i, AEItemStack.create(stack));
            mInventory[i] = null;
        }
    }

    public static void writeItemStackToNBT(NBTTagCompound tag, ItemStack stack, int slot) {
        if (stack == null) return;
        tag.setInteger("id", Item.getIdFromItem(stack.getItem()));
        tag.setInteger("Damage", stack.getItemDamage());
        tag.setInteger("Count", stack.stackSize);
        tag.setInteger("IntSlot", slot);

        if (stack.stackTagCompound != null) {
            tag.setTag("tag", stack.stackTagCompound);
        }
    }

    public static ItemStack readItemStackFromNBT(NBTTagCompound tag) {
        if (!tag.hasKey("id") || !tag.hasKey("Count") || !tag.hasKey("Damage")) return null;

        int id = tag.getInteger("id");
        int meta = tag.getInteger("Damage");
        int count = tag.getInteger("Count");

        if (count < 0) return null;

        ItemStack stack = new ItemStack(Item.getItemById(id), count, meta);

        if (tag.hasKey("tag")) {
            stack.stackTagCompound = tag.getCompoundTag("tag");
        }
        return stack;
    }

    @Override
    public void onBlockDestroyed() {
        Arrays.fill(mInventory, null);
        clearRecipeSnapshots();
        super.onBlockDestroyed();
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new HumongousDualInputHatchGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after HumongousDualInputHatch mui2 parity is verified.
        final int itemColumns = Math.max(1, mTier);
        final int itemRows = Math.max(1, mTier);

        final int totalWidth = 9 * itemColumns + 36;
        final int totalHeight = 5 * itemRows + 81;
        final int centerX = (176 - totalWidth) / 2;
        final int centerY = (166 - totalHeight) / 2;

        for (int row = 0; row < itemRows; row++) {
            for (int col = 0; col < itemColumns; col++) {
                int slotIndex = row * itemColumns + col;
                if (slotIndex < itemSlotAmount - 1) {
                    builder.widget(
                        SlotWidget.phantom(inventoryHandler, slotIndex)
                            .disableInteraction()
                            .setBackground(ModularUITextures.ITEM_SLOT)
                            .setPos(centerX + col * 18 + 5, centerY + row * 18));
                }
            }
        }

        for (int i = 0; i < mTier; i++) {
            builder.widget(
                new FluidSlotWidget(fluidTanks[i]).setBackground(ModularUITextures.FLUID_SLOT)
                    .setPos(centerX + 18 * itemColumns + 5, centerY + i * 18));
        }

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (clickData.mouseButton == 0 && !widget.isClient()) {
                refundAll();
            }
        })
            .setPlayClickSound(true)
            .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_EXPORT)
            .addTooltips(ImmutableList.of(StatCollector.translateToLocal("Button_Tooltip_HumongousDualInputHatch_00")))
            .setSize(16, 16)
            .setPos(170 + 4 * (mTier - 1) + mTier / 2, 102 + 14 * (mTier - 1)));

        addGregTechLogo(builder);
    }

    @Override
    @Deprecated
    public void addGregTechLogo(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 fallback after HumongousDualInputHatch mui2 parity is verified.
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(169 + 4 * (mTier - 1) + mTier / 2, 120 + 14 * (mTier - 1)));
    }

    public void refundAll() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null) return;

        ForgeDirection front = base.getFrontFacing();
        TileEntity targetTile = base.getTileEntityAtSide(front);
        refundItems(base, front, targetTile);
        refundFluids(base, front, targetTile);
        updateSlots();
        base.markDirty();
    }

    private void refundItems(IGregTechTileEntity base, ForgeDirection front, TileEntity targetTile) {
        for (int slot = 0; slot < itemInventory.getSlots(); slot++) {
            while (true) {
                IAEItemStack stored = itemInventory.getAEStackInSlot(slot);
                if (stored == null || stored.getStackSize() <= 0) break;

                int chunkSize = GTUtility.longToInt(stored.getStackSize());
                ItemStack chunk = toItemStack(stored, chunkSize);
                int inserted = insertIntoTargetInventory(targetTile, front, chunk);
                if (inserted > 0) {
                    itemInventory.extractAEItem(slot, inserted, false, true);
                }

                int remaining = chunkSize - inserted;
                if (remaining <= 0) continue;

                if (!spawnRefundRemainder(base, front, slot, stored, remaining)) break;
            }
        }
    }

    private int insertIntoTargetInventory(TileEntity targetTile, ForgeDirection front, ItemStack stack) {
        if (targetTile instanceof ISidedInventory sidedInventory) {
            return insertIntoSidedInventory(sidedInventory, front, stack);
        }
        if (targetTile instanceof IInventory inv) {
            return insertIntoInventory(inv, stack);
        }
        return 0;
    }

    private int insertIntoSidedInventory(ISidedInventory inventory, ForgeDirection front, ItemStack stack) {
        int moved = 0;
        ItemStack remaining = stack.copy();
        int side = front.getOpposite()
            .ordinal();

        for (int slot : inventory.getAccessibleSlotsFromSide(side)) {
            if (remaining.stackSize <= 0) break;
            if (!inventory.canInsertItem(slot, remaining, side)) continue;
            moved += insertIntoSlot(inventory, slot, remaining);
        }

        return moved;
    }

    private int insertIntoInventory(IInventory inventory, ItemStack stack) {
        int moved = 0;
        ItemStack remaining = stack.copy();

        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (remaining.stackSize <= 0) break;
            moved += insertIntoSlot(inventory, slot, remaining);
        }

        return moved;
    }

    private int insertIntoSlot(IInventory inventory, int slot, ItemStack remaining) {
        ItemStack slotStack = inventory.getStackInSlot(slot);
        int maxStack = slotStack != null ? Math.min(slotStack.getMaxStackSize(), inventory.getInventoryStackLimit())
            : inventory.getInventoryStackLimit();

        if (slotStack == null) {
            int toMove = Math.min(maxStack, remaining.stackSize);
            ItemStack copy = remaining.copy();
            copy.stackSize = toMove;
            inventory.setInventorySlotContents(slot, copy);
            remaining.stackSize -= toMove;
            return toMove;
        }

        if (!GTUtility.areStacksEqual(slotStack, remaining, true)) return 0;

        int space = maxStack - slotStack.stackSize;
        if (space <= 0) return 0;

        int toMove = Math.min(space, remaining.stackSize);
        slotStack.stackSize += toMove;
        remaining.stackSize -= toMove;
        return toMove;
    }

    private boolean spawnRefundRemainder(IGregTechTileEntity base, ForgeDirection front, int slot, IAEItemStack stored,
        int amount) {
        int xBlock = base.getXCoord() + front.offsetX;
        int yBlock = base.getYCoord() + front.offsetY;
        int zBlock = base.getZCoord() + front.offsetZ;

        if (!base.getWorld()
            .isAirBlock(xBlock, yBlock, zBlock)) {
            return false;
        }

        ItemStack refund = toItemStack(stored, amount);
        double x = xBlock + 0.5;
        double y = yBlock + 0.5;
        double z = zBlock + 0.5;
        base.getWorld()
            .spawnEntityInWorld(new EntityItem(base.getWorld(), x, y, z, refund));
        itemInventory.extractAEItem(slot, amount, false, true);
        return true;
    }

    private void refundFluids(IGregTechTileEntity base, ForgeDirection front, TileEntity targetTile) {
        if (!(targetTile instanceof IFluidHandler fluidHandler)) return;

        for (int i = 0; i < mStoredFluid.length; i++) {
            FluidStack fluid = mStoredFluid[i];
            if (fluid != null && fluid.amount > 0) {
                int filled = fluidHandler.fill(front.getOpposite(), fluid.copy(), true);
                if (filled > 0) {
                    fluid.amount -= filled;
                    if (fluid.amount <= 0) mStoredFluid[i] = null;
                }
            }
        }
        base.markDirty();
    }

    @Override
    public void updateSlots() {
        super.updateSlots();
        for (int i = 0; i < itemInventory.getSlots(); i++) {
            IAEItemStack stack = itemInventory.getAEStackInSlot(i);
            if (stack != null && stack.getStackSize() <= 0) itemInventory.setStackInSlot(i, (IAEItemStack) null);
        }
    }

    public AEInventory getAEInventory() {
        return itemInventory;
    }

    public int getItemStorageSlotCount() {
        return itemSlotAmount - 1;
    }

    @Override
    protected ItemSource getItemSource(ForgeDirection side) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        return base != null && side == base.getFrontFacing() ? itemInventory.getItemIO() : null;
    }

    @Override
    protected ItemSink getItemSink(ForgeDirection side) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        return base != null && side == base.getFrontFacing() ? itemInventory.getItemIO() : null;
    }

    private boolean isItemStorageSlot(int slot) {
        return slot >= 0 && slot < getItemStorageSlotCount();
    }

    private ItemStack getArrayStack(ItemStack[] array, int index) {
        if (array == null || index < 0 || index >= array.length) return null;
        return array[index];
    }

    private ItemStack toItemStack(IAEItemStack stack) {
        if (stack == null) return null;
        return toItemStack(stack, GTUtility.longToInt(stack.getStackSize()));
    }

    private ItemStack toItemStack(IAEItemStack stack, int amount) {
        if (stack == null || amount <= 0) return null;
        return GTUtility.copyAmountUnsafe(amount, stack.getItemStack());
    }

    private ItemStack[] getVisibleItemInputs() {
        ItemStack[] storageInputs = processing > 0 ? containedStacks : createRecipeSnapshot();
        ItemStack circuit = mInventory[getCircuitSlot()];
        if (circuit == null) {
            return Arrays.stream(storageInputs)
                .filter(Objects::nonNull)
                .toArray(ItemStack[]::new);
        }

        ItemStack[] nonNullInputs = Arrays.stream(storageInputs)
            .filter(Objects::nonNull)
            .toArray(ItemStack[]::new);
        ItemStack[] inputs = Arrays.copyOf(nonNullInputs, nonNullInputs.length + 1);
        inputs[inputs.length - 1] = circuit;
        return inputs;
    }

    private boolean isItemStorageEmpty() {
        for (IAEItemStack stack : itemInventory.inventory) {
            if (stack != null && stack.getStackSize() > 0) return false;
        }
        return true;
    }

    private class HumongousInventory extends Inventory {

        public HumongousInventory(FluidStack[] fluid) {
            super(new ItemStack[0], fluid);
        }

        @Override
        public ItemStack[] getItemInputs() {
            if (isEmpty()) return new ItemStack[0];
            return getVisibleItemInputs();
        }

        @Override
        public boolean isEmpty() {
            if (!isItemStorageEmpty()) return false;
            if (mInventory[getCircuitSlot()] != null) return false;
            return super.isEmpty();
        }
    }

    private class HumongousItemInventory extends AEInventory {

        public HumongousItemInventory(int slotCount) {
            super(slotCount);
        }

        @Override
        public long getAESlotLimit(int slot, IAEItemStack stack) {
            return Long.MAX_VALUE;
        }

        @Override
        protected boolean allowPutStack(int slotIndex, IAEItemStack toInsert) {
            return HumongousDualInputHatch.this.allowPutStack(
                getBaseMetaTileEntity(),
                slotIndex,
                ForgeDirection.UNKNOWN,
                toInsert == null ? null : toInsert.getItemStack());
        }

        @Override
        protected boolean allowPullStack(int slotIndex) {
            return true;
        }

        @Override
        protected AEInventory copyImpl() {
            return new HumongousItemInventory(slotCount);
        }

        @Override
        public BaseActionSource getActionSource() {
            return new MachineSource((BaseMetaTileEntity) getBaseMetaTileEntity());
        }
    }
}
