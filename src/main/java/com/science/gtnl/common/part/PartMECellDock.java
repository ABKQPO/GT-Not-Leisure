package com.science.gtnl.common.part;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.api.ICustomGui;
import com.science.gtnl.common.item.items.ItemPartMECellDock;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.AEApi;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.networking.GridFlags;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.BusSupport;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.MEMonitorHandler;
import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.client.render.BusRenderHelper;
import appeng.client.texture.ExtraBlockTextures;
import appeng.helpers.IPriorityHost;
import appeng.me.GridAccessException;
import appeng.me.storage.MEInventoryHandler;
import appeng.parts.PartBasicState;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.Getter;

public class PartMECellDock extends PartBasicState
    implements IAEAppEngInventory, IChestOrDrive, IPriorityHost, ICustomGui, ISidedInventory {

    private static final String CELL_NBT_KEY = "cell";
    private static final String PRIORITY_NBT_KEY = "priority";
    private static final String ROTATION_NBT_KEY = "rotation";
    private static final int CELL_SLOT = 0;
    private static final int[] CELL_SLOTS = { CELL_SLOT };

    @Getter
    private final AppEngInternalInventory cellInventory = new AppEngInternalInventory(this, 1, 1);
    private final Reference2ObjectOpenHashMap<IAEStackType<?>, List<IMEInventoryHandler>> cellHandlers = new Reference2ObjectOpenHashMap<>();
    @SuppressWarnings("rawtypes")
    private final Reference2ObjectOpenHashMap<IAEStackType<?>, MEMonitorHandler> cellMonitors = new Reference2ObjectOpenHashMap<>();
    private final BaseActionSource actionSource = new MachineSource(this);

    private ICellHandler cellHandler;
    private IMEInventoryHandler<?> cellInventoryHandler;
    private int priority;
    private int clientCellStatus;
    private int clientCellType;
    private int rotation;
    private int clientRotation;
    private boolean cached;

    public PartMECellDock(ItemStack stack) {
        super(stack);
        cellInventory.setMaxStackSize(1);
        getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        getProxy().setIdlePowerUsage(1.0);
    }

    @Override
    public boolean canBePlacedOn(BusSupport support) {
        return support == BusSupport.CABLE;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        cellInventory.readFromNBT(data, CELL_NBT_KEY);
        priority = data.getInteger(PRIORITY_NBT_KEY);
        rotation = data.getByte(ROTATION_NBT_KEY) & 3;
        cached = false;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        cellInventory.writeToNBT(data, CELL_NBT_KEY);
        data.setInteger(PRIORITY_NBT_KEY, priority);
        data.setByte(ROTATION_NBT_KEY, (byte) rotation);
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeByte(getCellStatus(CELL_SLOT));
        data.writeByte(getCellType(CELL_SLOT));
        data.writeByte(rotation);
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        boolean changed = super.readFromStream(data);
        int previousStatus = clientCellStatus;
        int previousType = clientCellType;
        int previousRotation = clientRotation;
        clientCellStatus = data.readUnsignedByte();
        clientCellType = data.readUnsignedByte();
        clientRotation = data.readUnsignedByte() & 3;
        return changed || previousStatus != clientCellStatus
            || previousType != clientCellType
            || previousRotation != clientRotation;
    }

    @Override
    public void saveChanges() {
        getHost().markForSave();
        getHost().markForUpdate();
    }

    @Override
    public void saveChanges(IMEInventory cellInventory) {
        saveChanges();
    }

    @Override
    public void onChangeInventory(IInventory inventory, int slot, InvOperation operation, ItemStack removedStack,
        ItemStack newStack) {
        if (operation == InvOperation.markDirty) {
            saveChanges();
            return;
        }

        cached = false;
        updateState();
        postCellArrayUpdate(removedStack, newStack);
        getHost().notifyNeighbors();
        saveChanges();
    }

    @Override
    public int getCellCount() {
        return 1;
    }

    @Override
    public int getCellStatus(int slot) {
        if (slot != CELL_SLOT) {
            return 0;
        }
        if (Platform.isClient()) {
            return clientCellStatus;
        }
        updateState();
        return cellHandler == null || cellInventoryHandler == null ? 0
            : cellHandler.getStatusForCell(getCell(), cellInventoryHandler);
    }

    @Override
    public int getCellType(int slot) {
        if (slot != CELL_SLOT) {
            return 0;
        }
        if (Platform.isClient()) {
            return clientCellType;
        }
        updateState();
        if (cellInventoryHandler == null || cellInventoryHandler.getStackType() == ITEM_STACK_TYPE) {
            return 0;
        }
        return cellInventoryHandler.getStackType() == FLUID_STACK_TYPE ? 1 : 2;
    }

    @Override
    public boolean isPowered() {
        return Platform.isClient() ? super.isPowered() : getProxy().isActive();
    }

    @Override
    public boolean canBeRotated() {
        return true;
    }

    @Override
    public ForgeDirection getForward() {
        return getSide() == null ? ForgeDirection.SOUTH : getSide();
    }

    @Override
    public ForgeDirection getUp() {
        return getUpForRotation(getForward(), getRenderRotation());
    }

    @Override
    public void setOrientation(ForgeDirection forward, ForgeDirection up) {
        if (forward != getForward()) {
            return;
        }

        for (int candidate = 0; candidate < 4; candidate++) {
            if (getUpForRotation(forward, candidate) == up) {
                rotation = candidate;
                getHost().markForSave();
                getHost().markForUpdate();
                return;
            }
        }
    }

    @Override
    public boolean toggleItemStorageCellLocking() {
        return false;
    }

    @Override
    public int applyStickyToItemStorageCells(ItemStack cards) {
        return 0;
    }

    @Override
    @Nonnull
    @SuppressWarnings("rawtypes")
    public List<IMEInventoryHandler> getCellArray(IAEStackType<?> type) {
        if (!getProxy().isActive()) {
            return Collections.emptyList();
        }
        updateState();
        return cellHandlers.getOrDefault(type, Collections.emptyList());
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int newPriority) {
        if (priority == newPriority) {
            return;
        }
        priority = newPriority;
        cached = false;
        updateState();
        notifyCellArrayChanged();
        saveChanges();
    }

    @Override
    public ItemStack getOriginGuiIcon() {
        return GTNLItemList.MECellDock.get(1);
    }

    @Override
    public boolean onPartActivate(EntityPlayer player, Vec3 pos) {
        ItemStack heldStack = player.getHeldItem();
        if (Platform.isWrench(player, heldStack, (int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord)) {
            if (Platform.isServer()) {
                rotate();
            }
            player.swingItem();
            return true;
        }

        if (!player.isSneaking()) {
            if (Platform.isServer()) {
                CommonProxy.openGui(player, GuiType.MECellDockGUI, getSide(), getTile());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onPartShiftActivate(EntityPlayer player, Vec3 pos) {
        return false;
    }

    @Override
    public void getDrops(List<ItemStack> drops, boolean wrenched) {
        ItemStack cell = getCell();
        if (cell != null) {
            drops.add(cell.copy());
        }
    }

    @Override
    public void getBoxes(IPartCollisionHelper helper) {
        helper.addBox(3, 3, 12, 13, 13, 16);
        helper.addBox(5, 5, 11, 11, 11, 12);
    }

    @Override
    public int getSizeInventory() {
        return cellInventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return cellInventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return cellInventory.decrStackSize(slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return cellInventory.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        cellInventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName() {
        return cellInventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return cellInventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return cellInventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return cellInventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        cellInventory.openInventory();
    }

    @Override
    public void closeInventory() {
        cellInventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == CELL_SLOT && stack != null
            && AEApi.instance()
                .registries()
                .cell()
                .isCellHandled(stack);
    }

    @Override
    public void markDirty() {
        cellInventory.markDirty();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return CELL_SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return getStackInSlot(slot) == null && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == CELL_SLOT && getStackInSlot(slot) != null;
    }

    @Override
    public int cableConnectionRenderTo() {
        return 5;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(int x, int y, int z, IPartRenderHelper helper, RenderBlocks renderer) {
        try {
            int status = getCellStatus(CELL_SLOT);
            int type = getCellType(CELL_SLOT);

            helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));
            helper.setTexture(
                ItemPartMECellDock.bodyDown,
                ItemPartMECellDock.bodyUp,
                ItemPartMECellDock.bodyNorth,
                ItemPartMECellDock.bodySouth,
                ItemPartMECellDock.bodyWest,
                ItemPartMECellDock.bodyEast);
            helper.setBounds(3, 3, 12, 13, 13, 16);
            helper.renderBlock(x, y, z, renderer);

            helper.setTexture(
                ItemPartMECellDock.baseDown,
                ItemPartMECellDock.baseUp,
                ItemPartMECellDock.baseNorth,
                ItemPartMECellDock.baseSouth,
                ItemPartMECellDock.baseWest,
                ItemPartMECellDock.baseEast);
            helper.setBounds(5, 5, 10.99f, 11, 11, 12);
            helper.renderBlock(x, y, z, renderer);

            renderInternals(x, y, z, helper, renderer);
            renderSlot(x, y, z, helper, renderer, status, type);
            renderStatusLight(x, y, z, helper, renderer, status);
        } finally {
            helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));
            restoreSpin(helper);
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderInternals(int x, int y, int z, IPartRenderHelper helper, RenderBlocks renderer) {
        helper.setTexture(ItemPartMECellDock.internalsVertical);
        helper.setBounds(7, 5, 11, 7, 11, 12);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.EAST));
        helper.renderBlock(x, y, z, renderer);
        helper.setBounds(9, 5, 11, 9, 11, 12);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.WEST));
        helper.renderBlock(x, y, z, renderer);

        helper.setTexture(ItemPartMECellDock.internalsHorizontal);
        helper.setBounds(5, 7, 11, 11, 7, 12);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.UP));
        helper.renderBlock(x, y, z, renderer);
        helper.setBounds(5, 9, 11, 11, 9, 12);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.DOWN));
        helper.renderBlock(x, y, z, renderer);

        helper.setTexture(
            ItemPartMECellDock.internalsCenterFace,
            ItemPartMECellDock.internalsCenterFace,
            ItemPartMECellDock.internalsCenterSide,
            ItemPartMECellDock.internalsCenterSide,
            ItemPartMECellDock.internalsCenterSide,
            ItemPartMECellDock.internalsCenterSide);
        helper.setBounds(6.01f, 6.01f, 11, 9.99f, 9.99f, 12);
        helper.setFacesToRender(
            worldFaces(helper, ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.UP, ForgeDirection.DOWN));
        helper.renderBlock(x, y, z, renderer);
    }

    @SideOnly(Side.CLIENT)
    private void renderSlot(int x, int y, int z, IPartRenderHelper helper, RenderBlocks renderer, int status,
        int type) {
        helper.setTexture(status == 0 ? ItemPartMECellDock.slotUp : createCellIcon(type));
        helper.setBounds(4.99f, 10.01f, 12.99f, 11.01f, 10.01f, 15.01f);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.UP));
        helper.renderBlock(x, y, z, renderer);

        helper.setTexture(ItemPartMECellDock.slotSouth);
        helper.setBounds(4.99f, 10.01f, 12.99f, 11.01f, 13.01f, 12.99f);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.SOUTH));
        helper.renderBlock(x, y, z, renderer);

        helper.setTexture(ItemPartMECellDock.slotNorth);
        helper.setBounds(4.99f, 10.01f, 15.01f, 11.01f, 13.01f, 15.01f);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.NORTH));
        helper.renderBlock(x, y, z, renderer);

        helper.setTexture(ItemPartMECellDock.slotEast);
        helper.setBounds(4.99f, 10.01f, 12.99f, 4.99f, 13.01f, 15.01f);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.EAST));
        helper.renderBlock(x, y, z, renderer);

        helper.setTexture(ItemPartMECellDock.slotWest);
        helper.setBounds(11.01f, 10.01f, 12.99f, 11.01f, 13.01f, 15.01f);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.WEST));
        helper.renderBlock(x, y, z, renderer);
    }

    @SideOnly(Side.CLIENT)
    private void renderStatusLight(int x, int y, int z, IPartRenderHelper helper, RenderBlocks renderer, int status) {
        if (status == 0) {
            return;
        }

        helper.normalRendering();

        boolean powered = isPowered();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(powered ? (15 << 20 | 15 << 4) : 0);
        tessellator.setColorOpaque_I(powered ? getStatusColor(status) : 0x000000);

        helper.setBounds(11, 5, 16.02f, 12, 6, 16.02f);
        helper.setFacesToRender(worldFaces(helper, ForgeDirection.SOUTH));
        helper.renderFace(x, y, z, ExtraBlockTextures.White.getIcon(), ForgeDirection.SOUTH, renderer);

        tessellator.setColorOpaque_I(0xFFFFFF);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(IPartRenderHelper helper, RenderBlocks renderer) {
        helper.setTexture(
            ItemPartMECellDock.bodyDown,
            ItemPartMECellDock.bodyUp,
            ItemPartMECellDock.bodyNorth,
            ItemPartMECellDock.bodySouth,
            ItemPartMECellDock.bodyWest,
            ItemPartMECellDock.bodyEast);
        helper.setBounds(3, 3, 12, 13, 13, 16);
        helper.renderInventoryBox(renderer);

        helper.setTexture(
            ItemPartMECellDock.baseDown,
            ItemPartMECellDock.baseUp,
            ItemPartMECellDock.baseNorth,
            ItemPartMECellDock.baseSouth,
            ItemPartMECellDock.baseWest,
            ItemPartMECellDock.baseEast);
        helper.setBounds(5, 5, 10.99f, 11, 11, 12);
        helper.renderInventoryBox(renderer);

        helper.setBounds(7, 5, 11, 7, 11, 12);
        helper.renderInventoryFace(ItemPartMECellDock.internalsVertical, ForgeDirection.EAST, renderer);
        helper.setBounds(9, 5, 11, 9, 11, 12);
        helper.renderInventoryFace(ItemPartMECellDock.internalsVertical, ForgeDirection.WEST, renderer);

        helper.setBounds(5, 7, 11, 11, 7, 12);
        helper.renderInventoryFace(ItemPartMECellDock.internalsHorizontal, ForgeDirection.UP, renderer);
        helper.setBounds(5, 9, 11, 11, 9, 12);
        helper.renderInventoryFace(ItemPartMECellDock.internalsHorizontal, ForgeDirection.DOWN, renderer);

        helper.setBounds(6.01f, 6.01f, 11, 9.99f, 9.99f, 12);
        helper.renderInventoryFace(ItemPartMECellDock.internalsCenterSide, ForgeDirection.EAST, renderer);
        helper.renderInventoryFace(ItemPartMECellDock.internalsCenterSide, ForgeDirection.WEST, renderer);
        helper.renderInventoryFace(ItemPartMECellDock.internalsCenterFace, ForgeDirection.UP, renderer);
        helper.renderInventoryFace(ItemPartMECellDock.internalsCenterFace, ForgeDirection.DOWN, renderer);

        helper.setBounds(4.99f, 10.01f, 12.99f, 11.01f, 10.01f, 15.01f);
        helper.renderInventoryFace(ItemPartMECellDock.slotUp, ForgeDirection.UP, renderer);
        helper.setBounds(4.99f, 10.01f, 12.99f, 11.01f, 13.01f, 12.99f);
        helper.renderInventoryFace(ItemPartMECellDock.slotSouth, ForgeDirection.SOUTH, renderer);
        helper.setBounds(4.99f, 10.01f, 15.01f, 11.01f, 13.01f, 15.01f);
        helper.renderInventoryFace(ItemPartMECellDock.slotNorth, ForgeDirection.NORTH, renderer);
        helper.setBounds(4.99f, 10.01f, 12.99f, 4.99f, 13.01f, 15.01f);
        helper.renderInventoryFace(ItemPartMECellDock.slotEast, ForgeDirection.EAST, renderer);
        helper.setBounds(11.01f, 10.01f, 12.99f, 11.01f, 13.01f, 15.01f);
        helper.renderInventoryFace(ItemPartMECellDock.slotWest, ForgeDirection.WEST, renderer);
    }

    @MENetworkEventSubscribe
    public void onChannelStateChanged(MENetworkChannelsChanged event) {
        if (Platform.isServer()) {
            notifyCellArrayChanged();
            getHost().markForUpdate();
        }
    }

    @MENetworkEventSubscribe
    public void onPowerStateChanged(MENetworkPowerStatusChange event) {
        if (Platform.isServer()) {
            notifyCellArrayChanged();
            getHost().markForUpdate();
        }
    }

    private void updateState() {
        if (cached) {
            return;
        }

        cellHandlers.clear();
        cellMonitors.clear();
        cellHandler = null;
        cellInventoryHandler = null;
        double powerUsage = 1.0;
        ItemStack cell = getCell();

        if (cell != null) {
            cellHandler = AEApi.instance()
                .registries()
                .cell()
                .getHandler(cell);
            if (cellHandler != null) {
                for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
                    IMEInventoryHandler<?> handler = cellHandler.getCellInventory(cell, this, type);
                    if (handler == null) {
                        continue;
                    }

                    cellInventoryHandler = handler;
                    powerUsage += cellHandler.cellIdleDrain(cell, handler);
                    addCellHandler(type, handler);
                    break;
                }
            }
        }

        getProxy().setIdlePowerUsage(powerUsage);
        cached = true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addCellHandler(IAEStackType<?> type, IMEInventoryHandler<?> handler) {
        MEInventoryHandler wrappedHandler = new MEInventoryHandler(handler, type);
        wrappedHandler.setPriority(priority);

        MEMonitorHandler monitor = new MEMonitorHandler(wrappedHandler);
        monitor.addListener(new CellNetNotifier(type), monitor);
        cellMonitors.put(type, monitor);
        cellHandlers.put(type, Collections.singletonList(monitor));
    }

    private void notifyCellArrayChanged() {
        try {
            getProxy().getGrid()
                .postEvent(new MENetworkCellArrayUpdate());
        } catch (GridAccessException ignored) {}
    }

    private void postCellArrayUpdate(ItemStack removedStack, ItemStack newStack) {
        try {
            getProxy().getGrid()
                .postEvent(new MENetworkCellArrayUpdate());
            IStorageGrid storageGrid = getProxy().getStorage();
            Platform.postChanges(storageGrid, removedStack, newStack, actionSource);
        } catch (GridAccessException ignored) {}
    }

    private ItemStack getCell() {
        return cellInventory.getStackInSlot(CELL_SLOT);
    }

    private void rotate() {
        rotation = (rotation + 1) & 3;
        getHost().markForSave();
        getHost().markForUpdate();
    }

    @SideOnly(Side.CLIENT)
    private IIcon createCellIcon(int type) {
        float v = 1 + type * 4f;
        return new ItemPartMECellDock.MappedIcon(
            ExtraBlockTextures.MEStorageCellTextures.getIcon(),
            4.99f,
            11.01f,
            1,
            6,
            12.99f,
            15.01f,
            v,
            v + 2);
    }

    private int getStatusColor(int status) {
        return switch (status) {
            case 1 -> 0x00FF00;
            case 2 -> 0x00AAFF;
            case 3 -> 0xFFAA00;
            case 4 -> 0xFF0000;
            default -> 0xFFFFFF;
        };
    }

    private int getRenderRotation() {
        return Platform.isClient() ? clientRotation : rotation;
    }

    @SideOnly(Side.CLIENT)
    private void applySpin(IPartRenderHelper helper) {
        if (!(helper instanceof BusRenderHelper busHelper)) {
            return;
        }

        ForgeDirection ax = busHelper.getWorldX();
        ForgeDirection ay = busHelper.getWorldY();
        for (int i = getRenderRotation() & 3; i > 0; i--) {
            ForgeDirection nextX = ay.getOpposite();
            ay = ax;
            ax = nextX;
        }
        busHelper.setOrientation(ax, ay, busHelper.getWorldZ());
    }

    @SideOnly(Side.CLIENT)
    private void restoreSpin(IPartRenderHelper helper) {
        if (helper instanceof BusRenderHelper busHelper) {
            busHelper.setOrientation(busHelper.getWorldX(), busHelper.getWorldY(), busHelper.getWorldZ());
        }
    }

    @SideOnly(Side.CLIENT)
    private ForgeDirection localToWorld(IPartRenderHelper helper, ForgeDirection localFace) {
        return switch (localFace) {
            case DOWN -> helper.getWorldY()
                .getOpposite();
            case UP -> helper.getWorldY();
            case NORTH -> helper.getWorldZ()
                .getOpposite();
            case SOUTH -> helper.getWorldZ();
            case EAST -> helper.getWorldX();
            case WEST -> helper.getWorldX()
                .getOpposite();
            default -> ForgeDirection.UNKNOWN;
        };
    }

    @SideOnly(Side.CLIENT)
    private EnumSet<ForgeDirection> worldFaces(IPartRenderHelper helper, ForgeDirection... localFaces) {
        EnumSet<ForgeDirection> faces = EnumSet.noneOf(ForgeDirection.class);
        for (ForgeDirection localFace : localFaces) {
            faces.add(localToWorld(helper, localFace));
        }
        return faces;
    }

    private ForgeDirection getUpForRotation(ForgeDirection forward, int currentRotation) {
        ForgeDirection ax;
        ForgeDirection ay;
        switch (forward) {
            case DOWN -> {
                ax = ForgeDirection.EAST;
                ay = ForgeDirection.NORTH;
            }
            case UP -> {
                ax = ForgeDirection.EAST;
                ay = ForgeDirection.SOUTH;
            }
            case EAST -> {
                ax = ForgeDirection.SOUTH;
                ay = ForgeDirection.UP;
            }
            case WEST -> {
                ax = ForgeDirection.NORTH;
                ay = ForgeDirection.UP;
            }
            case NORTH -> {
                ax = ForgeDirection.WEST;
                ay = ForgeDirection.UP;
            }
            default -> {
                ax = ForgeDirection.EAST;
                ay = ForgeDirection.UP;
            }
        }

        for (int i = currentRotation & 3; i > 0; i--) {
            ForgeDirection nextX = ay.getOpposite();
            ay = ax;
            ax = nextX;
        }
        return ay;
    }

    private class CellNetNotifier implements IMEMonitorHandlerReceiver<IAEStack<?>> {

        private final IAEStackType<?> type;

        private CellNetNotifier(IAEStackType<?> type) {
            this.type = type;
        }

        @Override
        public boolean isValid(Object verificationToken) {
            return cellMonitors.get(type) == verificationToken;
        }

        @Override
        public void postChange(IBaseMonitor<IAEStack<?>> monitor, Iterable<IAEStack<?>> change,
            BaseActionSource source) {
            try {
                if (getProxy().isActive()) {
                    getProxy().getStorage()
                        .postAlterationOfStoredItems(type, change, actionSource);
                }
            } catch (GridAccessException ignored) {}
            getHost().markForUpdate();
        }

        @Override
        public void onListUpdate() {
            getHost().markForUpdate();
        }
    }
}
