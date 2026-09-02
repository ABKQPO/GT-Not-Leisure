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
            : cellHandler.getStatusForCell(getCell(), cellInventoryHandler.getInternal());
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
    @SideOnly(Side.CLIENT)
    public void renderStatic(int x, int y, int z, IPartRenderHelper helper, RenderBlocks renderer) {
        helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));
        setRotatedTextures(
            helper,
            ItemPartMECellDock.bodyDownIcon,
            ItemPartMECellDock.bodyUpIcon,
            ItemPartMECellDock.bodyNorthIcon,
            ItemPartMECellDock.bodySouthIcon,
            ItemPartMECellDock.bodyWestIcon,
            ItemPartMECellDock.bodyEastIcon);
        helper.setBounds(3, 3, 12, 13, 13, 15.99f);
        helper.renderBlock(x, y, z, renderer);
        setRotatedTextures(
            helper,
            ItemPartMECellDock.slotDownIcon,
            ItemPartMECellDock.slotUpIcon,
            ItemPartMECellDock.slotNorthIcon,
            ItemPartMECellDock.slotSouthIcon,
            ItemPartMECellDock.slotWestIcon,
            ItemPartMECellDock.slotEastIcon);
        setSlotBounds(helper);
        helper.setFacesToRender(
            getRotatedWorldFaces(
                helper,
                ForgeDirection.UP,
                ForgeDirection.NORTH,
                ForgeDirection.SOUTH,
                ForgeDirection.EAST,
                ForgeDirection.WEST));
        helper.renderBlock(x, y, z, renderer);
        helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));

        setBaseTextures(helper);
        setRotatedBounds(helper, 5, 5, 11, 11, 11, 12);
        helper.renderBlock(x, y, z, renderer);
        setInternalVerticalTextures(helper);
        setRotatedBounds(helper, 7, 5, 11.99f, 9, 11, 12.01f);
        helper.setFacesToRender(getRotatedWorldFaces(helper, ForgeDirection.EAST, ForgeDirection.WEST));
        helper.renderBlock(x, y, z, renderer);
        setInternalHorizontalTextures(helper);
        setRotatedBounds(helper, 5, 7, 11.995f, 11, 9, 12.015f);
        helper.setFacesToRender(getRotatedWorldFaces(helper, ForgeDirection.UP, ForgeDirection.DOWN));
        helper.renderBlock(x, y, z, renderer);
        setInternalCenterTextures(helper);
        setRotatedBounds(helper, 6, 6, 12.0f, 10, 10, 12.02f);
        helper.setFacesToRender(
            getRotatedWorldFaces(
                helper,
                ForgeDirection.EAST,
                ForgeDirection.WEST,
                ForgeDirection.UP,
                ForgeDirection.DOWN));
        helper.renderBlock(x, y, z, renderer);
        helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));

        renderCellDisplay(x, y, z, helper, renderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(IPartRenderHelper helper, RenderBlocks renderer) {
        helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));
        setRotatedTextures(
            helper,
            ItemPartMECellDock.bodyDownIcon,
            ItemPartMECellDock.bodyUpIcon,
            ItemPartMECellDock.bodyNorthIcon,
            ItemPartMECellDock.bodySouthIcon,
            ItemPartMECellDock.bodyWestIcon,
            ItemPartMECellDock.bodyEastIcon);
        helper.setBounds(3, 3, 12, 13, 13, 15.99f);
        helper.renderInventoryBox(renderer);
        setRotatedTextures(
            helper,
            ItemPartMECellDock.slotDownIcon,
            ItemPartMECellDock.slotUpIcon,
            ItemPartMECellDock.slotNorthIcon,
            ItemPartMECellDock.slotSouthIcon,
            ItemPartMECellDock.slotWestIcon,
            ItemPartMECellDock.slotEastIcon);
        setSlotBounds(helper);
        helper.setFacesToRender(
            EnumSet.of(
                ForgeDirection.UP,
                ForgeDirection.NORTH,
                ForgeDirection.SOUTH,
                ForgeDirection.EAST,
                ForgeDirection.WEST));
        helper.renderInventoryBox(renderer);
        helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));

        setBaseTextures(helper);
        helper.setBounds(5, 5, 11, 11, 11, 12);
        helper.renderInventoryBox(renderer);
        setInternalVerticalTextures(helper);
        helper.setBounds(7, 5, 11.99f, 9, 11, 12.01f);
        helper.setFacesToRender(EnumSet.of(ForgeDirection.EAST, ForgeDirection.WEST));
        helper.renderInventoryBox(renderer);
        setInternalHorizontalTextures(helper);
        helper.setBounds(5, 7, 11.995f, 11, 9, 12.015f);
        helper.setFacesToRender(EnumSet.of(ForgeDirection.UP, ForgeDirection.DOWN));
        helper.renderInventoryBox(renderer);
        setInternalCenterTextures(helper);
        helper.setBounds(6, 6, 12.0f, 10, 10, 12.02f);
        helper.setFacesToRender(
            EnumSet.of(ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.UP, ForgeDirection.DOWN));
        helper.renderInventoryBox(renderer);
        helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));

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

    @SideOnly(Side.CLIENT)
    private void setBaseTextures(IPartRenderHelper helper) {
        setRotatedTextures(
            helper,
            ItemPartMECellDock.baseDownIcon,
            ItemPartMECellDock.baseUpIcon,
            ItemPartMECellDock.baseNorthIcon,
            ItemPartMECellDock.baseSouthIcon,
            ItemPartMECellDock.baseWestIcon,
            ItemPartMECellDock.baseEastIcon);
    }

    @SideOnly(Side.CLIENT)
    private void setInternalVerticalTextures(IPartRenderHelper helper) {
        setRotatedTextures(
            helper,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.internalVerticalIcon,
            ItemPartMECellDock.internalVerticalEastIcon);
    }

    @SideOnly(Side.CLIENT)
    private void setInternalHorizontalTextures(IPartRenderHelper helper) {
        setRotatedTextures(
            helper,
            ItemPartMECellDock.internalHorizontalDownIcon,
            ItemPartMECellDock.internalHorizontalIcon,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.sideIcon);
    }

    @SideOnly(Side.CLIENT)
    private void setInternalCenterTextures(IPartRenderHelper helper) {
        setRotatedTextures(
            helper,
            ItemPartMECellDock.internalCenterDownIcon,
            ItemPartMECellDock.internalCenterFaceIcon,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.sideIcon,
            ItemPartMECellDock.internalCenterSideIcon,
            ItemPartMECellDock.internalCenterEastIcon);
    }

    @SideOnly(Side.CLIENT)
    private void setRotatedTextures(IPartRenderHelper helper, IIcon down, IIcon up, IIcon north, IIcon south,
        IIcon west, IIcon east) {
        int planeRotation = getPlaneRotation(helper);
        helper.setTexture(
            getTextureForFace(
                unrotateLocalFace(ForgeDirection.DOWN, planeRotation),
                down,
                up,
                north,
                south,
                west,
                east),
            getTextureForFace(unrotateLocalFace(ForgeDirection.UP, planeRotation), down, up, north, south, west, east),
            getTextureForFace(
                unrotateLocalFace(ForgeDirection.NORTH, planeRotation),
                down,
                up,
                north,
                south,
                west,
                east),
            getTextureForFace(
                unrotateLocalFace(ForgeDirection.SOUTH, planeRotation),
                down,
                up,
                north,
                south,
                west,
                east),
            getTextureForFace(
                unrotateLocalFace(ForgeDirection.WEST, planeRotation),
                down,
                up,
                north,
                south,
                west,
                east),
            getTextureForFace(
                unrotateLocalFace(ForgeDirection.EAST, planeRotation),
                down,
                up,
                north,
                south,
                west,
                east));
    }

    @SideOnly(Side.CLIENT)
    private IIcon getTextureForFace(ForgeDirection face, IIcon down, IIcon up, IIcon north, IIcon south, IIcon west,
        IIcon east) {
        return switch (face) {
            case DOWN -> down;
            case UP -> up;
            case NORTH -> north;
            case SOUTH -> south;
            case WEST -> west;
            case EAST -> east;
            default -> down;
        };
    }

    private ForgeDirection unrotateLocalFace(ForgeDirection localFace, int planeRotation) {
        return switch (planeRotation) {
            case 1 -> switch (localFace) {
                    case UP -> ForgeDirection.WEST;
                    case EAST -> ForgeDirection.UP;
                    case DOWN -> ForgeDirection.EAST;
                    case WEST -> ForgeDirection.DOWN;
                    default -> localFace;
                };
            case 2 -> switch (localFace) {
                    case UP -> ForgeDirection.DOWN;
                    case EAST -> ForgeDirection.WEST;
                    case DOWN -> ForgeDirection.UP;
                    case WEST -> ForgeDirection.EAST;
                    default -> localFace;
                };
            case 3 -> switch (localFace) {
                    case UP -> ForgeDirection.EAST;
                    case EAST -> ForgeDirection.DOWN;
                    case DOWN -> ForgeDirection.WEST;
                    case WEST -> ForgeDirection.UP;
                    default -> localFace;
                };
            default -> localFace;
        };
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
    private void renderCellDisplay(int x, int y, int z, IPartRenderHelper helper, RenderBlocks renderer) {
        int status = getCellStatus(CELL_SLOT);
        int type = getCellType(CELL_SLOT);
        int minV = status == 0 ? 13 : 1 + type * 4;
        int maxV = status == 0 ? 15 : 3 + type * 4;
        IIcon cellIcon = new AtlasRegionIcon(ExtraBlockTextures.MEStorageCellTextures.getIcon(), 1, minV, 6, maxV);

        Tessellator.instance.setColorOpaque_I(0xFFFFFF);
        helper.setTexture(cellIcon);
        setCellBounds(helper);
        helper.setFacesToRender(EnumSet.of(getRotatedWorldFace(helper, ForgeDirection.SOUTH)));
        helper.renderBlock(x, y, z, renderer);
        helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));

        if (status != 0) {
            Tessellator.instance.setColorOpaque_I(isPowered() ? getStatusColor(status) : 0x000000);
            helper.setTexture(ExtraBlockTextures.White.getIcon());
            setStatusBounds(helper);
            helper.setFacesToRender(EnumSet.of(getRotatedWorldFace(helper, ForgeDirection.SOUTH)));
            helper.renderBlock(x, y, z, renderer);
            helper.setFacesToRender(EnumSet.allOf(ForgeDirection.class));
        }
        Tessellator.instance.setColorOpaque_I(0xFFFFFF);
    }

    @SideOnly(Side.CLIENT)
    private static class AtlasRegionIcon implements IIcon {

        private final IIcon source;
        private final float minU;
        private final float minV;
        private final float maxU;
        private final float maxV;

        private AtlasRegionIcon(IIcon source, float minU, float minV, float maxU, float maxV) {
            this.source = source;
            this.minU = minU;
            this.minV = minV;
            this.maxU = maxU;
            this.maxV = maxV;
        }

        @Override
        public int getIconWidth() {
            return source.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            return source.getIconHeight();
        }

        @Override
        public float getMinU() {
            return getInterpolatedU(0);
        }

        @Override
        public float getMaxU() {
            return getInterpolatedU(16);
        }

        @Override
        public float getInterpolatedU(double value) {
            return source.getInterpolatedU(minU + (maxU - minU) * value / 16);
        }

        @Override
        public float getMinV() {
            return getInterpolatedV(0);
        }

        @Override
        public float getMaxV() {
            return getInterpolatedV(16);
        }

        @Override
        public float getInterpolatedV(double value) {
            return source.getInterpolatedV(minV + (maxV - minV) * value / 16);
        }

        @Override
        public String getIconName() {
            return source.getIconName();
        }
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

    private void setSlotBounds(IPartRenderHelper helper) {
        setRotatedBounds(helper, 4.99f, 10.01f, 12.99f, 11.01f, 13.01f, 15.01f);
    }

    private void setCellBounds(IPartRenderHelper helper) {
        setRotatedBounds(helper, 5, 10, 16.01f, 11, 12, 16.03f);
    }

    private void setStatusBounds(IPartRenderHelper helper) {
        setRotatedBounds(helper, 9, 10, 16.03f, 10, 11, 16.05f);
    }

    private void setRotatedBounds(IPartRenderHelper helper, float minX, float minY, float minZ, float maxX, float maxY,
        float maxZ) {
        switch (getPlaneRotation(helper)) {
            case 1 -> helper.setBounds(minY, 16 - maxX, minZ, maxY, 16 - minX, maxZ);
            case 2 -> helper.setBounds(16 - maxX, 16 - maxY, minZ, 16 - minX, 16 - minY, maxZ);
            case 3 -> helper.setBounds(16 - maxY, minX, minZ, 16 - minY, maxX, maxZ);
            default -> helper.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    private EnumSet<ForgeDirection> getRotatedWorldFaces(IPartRenderHelper helper, ForgeDirection... localFaces) {
        EnumSet<ForgeDirection> worldFaces = EnumSet.noneOf(ForgeDirection.class);
        for (ForgeDirection localFace : localFaces) {
            worldFaces.add(getRotatedWorldFace(helper, localFace));
        }
        return worldFaces;
    }

    private ForgeDirection getRotatedWorldFace(IPartRenderHelper helper, ForgeDirection localFace) {
        return getBaseWorldFace(helper, rotateLocalFace(helper, localFace));
    }

    private ForgeDirection getBaseWorldFace(IPartRenderHelper helper, ForgeDirection localFace) {
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

    private ForgeDirection rotateLocalFace(IPartRenderHelper helper, ForgeDirection localFace) {
        return switch (getPlaneRotation(helper)) {
            case 1 -> switch (localFace) {
                    case UP -> ForgeDirection.EAST;
                    case EAST -> ForgeDirection.DOWN;
                    case DOWN -> ForgeDirection.WEST;
                    case WEST -> ForgeDirection.UP;
                    default -> localFace;
                };
            case 2 -> switch (localFace) {
                    case UP -> ForgeDirection.DOWN;
                    case EAST -> ForgeDirection.WEST;
                    case DOWN -> ForgeDirection.UP;
                    case WEST -> ForgeDirection.EAST;
                    default -> localFace;
                };
            case 3 -> switch (localFace) {
                    case UP -> ForgeDirection.WEST;
                    case EAST -> ForgeDirection.UP;
                    case DOWN -> ForgeDirection.EAST;
                    case WEST -> ForgeDirection.DOWN;
                    default -> localFace;
                };
            default -> localFace;
        };
    }

    private int getPlaneRotation(IPartRenderHelper helper) {
        ForgeDirection currentUp = getUp();
        ForgeDirection baseUp = helper.getWorldY();
        if (currentUp == baseUp) {
            return 0;
        }
        if (currentUp == helper.getWorldX()) {
            return 1;
        }
        if (currentUp == baseUp.getOpposite()) {
            return 2;
        }
        return 3;
    }

    private ForgeDirection getUpForRotation(ForgeDirection forward, int currentRotation) {
        return switch (forward) {
            case DOWN -> switch (currentRotation & 3) {
                    case 0 -> ForgeDirection.NORTH;
                    case 1 -> ForgeDirection.WEST;
                    case 2 -> ForgeDirection.SOUTH;
                    default -> ForgeDirection.EAST;
                };
            case UP -> switch (currentRotation & 3) {
                    case 0 -> ForgeDirection.NORTH;
                    case 1 -> ForgeDirection.EAST;
                    case 2 -> ForgeDirection.SOUTH;
                    default -> ForgeDirection.WEST;
                };
            case NORTH -> switch (currentRotation & 3) {
                    case 0 -> ForgeDirection.UP;
                    case 1 -> ForgeDirection.WEST;
                    case 2 -> ForgeDirection.DOWN;
                    default -> ForgeDirection.EAST;
                };
            case SOUTH -> switch (currentRotation & 3) {
                    case 0 -> ForgeDirection.UP;
                    case 1 -> ForgeDirection.EAST;
                    case 2 -> ForgeDirection.DOWN;
                    default -> ForgeDirection.WEST;
                };
            case WEST -> switch (currentRotation & 3) {
                    case 0 -> ForgeDirection.UP;
                    case 1 -> ForgeDirection.SOUTH;
                    case 2 -> ForgeDirection.DOWN;
                    default -> ForgeDirection.NORTH;
                };
            case EAST -> switch (currentRotation & 3) {
                    case 0 -> ForgeDirection.UP;
                    case 1 -> ForgeDirection.NORTH;
                    case 2 -> ForgeDirection.DOWN;
                    default -> ForgeDirection.SOUTH;
                };
            default -> ForgeDirection.UP;
        };
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
