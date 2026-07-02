package com.science.gtnl.common.machine.hatch;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_ITEMVAULTPORTHATCH;

import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.api.IStackVault;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IMEConnectable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;

public class VaultPortHatch extends MTEHatch
    implements ICellContainer, IGridProxyable, IActionHost, IPowerChannelState, IMEConnectable {

    public IStackVault controller;
    public AENetworkProxy gridProxy = null;
    public BaseActionSource machineSource = new MachineSource(this);
    private final Map<IAEStackType<?>, List<IMEInventoryHandler>> handlers = new IdentityHashMap<>();

    public VaultPortHatch(int aID, String aName, String aNameRegional) {
        super(
            aID,
            aName,
            aNameRegional,
            3,
            0,
            new String[] { StatCollector.translateToLocal("Tooltip_VaultPortHatch_00") });
    }

    public VaultPortHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new VaultPortHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN),
            TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ITEMVAULTPORTHATCH)
                .extFacing()
                .build() };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN),
            TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ITEMVAULTPORTHATCH)
                .extFacing()
                .build() };
    }

    @Override
    public void onFirstTick(IGregTechTileEntity baseMetaTileEntity) {
        super.onFirstTick(baseMetaTileEntity);
        getProxy().onReady();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && controller != null && !controller.isValid()) {
            unbind();
        }
    }

    @Override
    public void onFacingChange() {
        super.onFacingChange();
        updateValidGridProxySides();
    }

    public void bind(IStackVault controller) {
        if (this.controller != null && this.controller == controller) return;
        unbind();
        this.controller = controller;
        for (IAEStackType<?> type : this.controller.getSupportedStackTypes()) {
            postStoredStacks(type, 1);
        }
    }

    private <T extends IAEStack<T>> void postStoredStacks(IAEStackType<T> type, long multiplier) {
        IItemList<T> stacks = this.controller.getStoredStacks(type);
        for (T stack : stacks) {
            if (stack != null) {
                postUpdate(stack, stack.getStackSize() * multiplier);
            }
        }
    }

    public void unbind() {
        if (this.controller == null) return;
        for (IAEStackType<?> type : this.controller.getSupportedStackTypes()) {
            postStoredStacks(type, -1);
        }
        this.controller = null;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        if (this.gridProxy != null) {
            NBTTagCompound proxyTag = new NBTTagCompound();
            this.gridProxy.writeToNBT(proxyTag);
            aNBT.setTag("gridProxy", proxyTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        NBTTagCompound proxyTag = aNBT.getCompoundTag("gridProxy");
        this.getProxy()
            .readFromNBT(proxyTag);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public IGridNode getActionableNode() {
        AENetworkProxy gp = getProxy();
        return gp != null ? gp.getNode() : null;
    }

    @Override
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(this, "proxy", GTNLItemList.VaultPortHatch.get(1), true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                var bmte = getBaseMetaTileEntity();
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

    public void updateValidGridProxySides() {
        getProxy().setValidSides(EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN)));
    }

    @Override
    public boolean connectsToAllSides() {
        return true;
    }

    @Override
    public void setConnectsToAllSides(boolean connects) {
        updateValidGridProxySides();
    }

    @Override
    public IGridNode getGridNode(ForgeDirection forgeDirection) {
        AENetworkProxy gp = getProxy();
        return gp != null ? gp.getNode() : null;
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection forgeDirection) {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak() {}

    @Override
    @SuppressWarnings("rawtypes")
    public List<IMEInventoryHandler> getCellArray(StorageChannel channel) {
        if (channel == StorageChannel.ITEMS) {
            return getCellArray(ITEM_STACK_TYPE);
        } else if (channel == StorageChannel.FLUIDS) {
            return getCellArray(FLUID_STACK_TYPE);
        }
        return Collections.emptyList();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull List<IMEInventoryHandler> getCellArray(IAEStackType<?> type) {
        if (controller == null || type == null || !controller.supportsStackType(type)) {
            return Collections.emptyList();
        }
        List<IMEInventoryHandler> cached = handlers.get(type);
        if (cached != null) {
            return cached;
        }
        List<IMEInventoryHandler> created = Collections.singletonList(createHandler(type));
        handlers.put(type, created);
        return created;
    }

    private <T extends IAEStack<T>> IMEInventoryHandler<T> createHandler(IAEStackType<T> type) {
        return new StackMEInventory<>(type);
    }

    @Override
    public void saveChanges(IMEInventory cellInventory) {
        // This is handled by the host itself.
    }

    @Override
    public boolean isPowered() {
        return getProxy() != null && getProxy().isPowered();
    }

    @Override
    public boolean isActive() {
        return getProxy() != null && getProxy().isActive();
    }

    @Override
    public DimensionalCoord getLocation() {
        IGregTechTileEntity gtm = this.getBaseMetaTileEntity();
        return new DimensionalCoord(gtm.getWorld(), gtm.getXCoord(), gtm.getYCoord(), gtm.getZCoord());
    }

    public void postUpdateItem(ItemStack itemStack, long amt) {
        if (itemStack == null) return;
        postUpdate(AEItemStack.create(itemStack), amt);
    }

    public void postUpdateFluid(FluidStack fluid, long amt) {
        if (fluid == null) return;
        postUpdate(AEFluidStack.create(fluid), amt);
    }

    public void postUpdate(IAEStack<?> stack, long amt) {
        if (stack == null || amt == 0) return;
        IAEStack<?> delta = stack.copy()
            .setStackSize(amt);
        try {
            getProxy().getStorage()
                .postAlterationOfStoredItems(
                    delta.getStackType(),
                    Collections.singletonList(delta),
                    this.machineSource);
        } catch (GridAccessException e) {
            // Ignore grid access failures during passive cache updates.
        }
    }

    @MENetworkEventSubscribe
    public void powerRender(final MENetworkPowerStatusChange c) {
        try {
            AENetworkProxy proxy = getProxy();
            if (proxy != null && proxy.isActive()) {
                proxy.getGrid()
                    .postEvent(new MENetworkCellArrayUpdate());
            }
        } catch (GridAccessException e) {
            // Ignore grid refresh failures when the network becomes unavailable.
        }
    }

    @MENetworkEventSubscribe
    public void channelRender(final MENetworkChannelsChanged c) {
        try {
            AENetworkProxy proxy = getProxy();
            if (proxy != null && proxy.isActive()) {
                proxy.getGrid()
                    .postEvent(new MENetworkCellArrayUpdate());
            }
        } catch (GridAccessException e) {
            // Ignore grid refresh failures when the network becomes unavailable.
        }
    }

    public class StackMEInventory<T extends IAEStack<T>> implements IMEInventoryHandler<T> {

        private final IAEStackType<T> stackType;

        public StackMEInventory(IAEStackType<T> stackType) {
            this.stackType = stackType;
        }

        @Override
        public T injectItems(T input, Actionable mode, BaseActionSource src) {
            if (input == null) return null;
            if (controller == null || getBaseMetaTileEntity() == null || !controller.supportsStackType(stackType))
                return input;
            if (mode != Actionable.SIMULATE) getBaseMetaTileEntity().markDirty();
            long amount = controller.injectStack(input, mode != Actionable.SIMULATE);
            if (amount == 0) return input;
            if (amount >= input.getStackSize()) return null;
            return input.copy()
                .setStackSize(input.getStackSize() - amount);
        }

        @Override
        public T extractItems(T request, Actionable mode, BaseActionSource src) {
            if (request == null) return null;
            if (controller == null || getBaseMetaTileEntity() == null || !controller.supportsStackType(stackType))
                return null;
            if (mode != Actionable.SIMULATE) getBaseMetaTileEntity().markDirty();
            long amount = controller.extractStack(request, mode != Actionable.SIMULATE);
            if (amount == 0) return null;
            return request.copy()
                .setStackSize(amount);
        }

        @Override
        public StorageChannel getChannel() {
            if (stackType == ITEM_STACK_TYPE) return StorageChannel.ITEMS;
            if (stackType == FLUID_STACK_TYPE) return StorageChannel.FLUIDS;
            return null;
        }

        @Override
        public IAEStackType<?> getStackType() {
            return stackType;
        }

        @Override
        public AccessRestriction getAccess() {
            return AccessRestriction.READ_WRITE;
        }

        @Override
        public boolean isPrioritized(T input) {
            return true;
        }

        @Override
        public boolean canAccept(T input) {
            if (controller == null || input == null || !controller.supportsStackType(stackType)) return false;
            return controller.getStoredStack(input) != null
                || controller.stackTypesCount(stackType) < controller.maxStackTypes(stackType);
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public boolean validForPass(int i) {
            return true;
        }

        @Override
        public IItemList<T> getAvailableItems(IItemList<T> out, int iteration) {
            if (controller != null && controller.supportsStackType(stackType)) {
                IItemList<T> storedStacks = controller.getStoredStacks(stackType);
                for (T stack : storedStacks) {
                    if (stack != null) {
                        out.add(stack.copy());
                    }
                }
            }
            return out;
        }
    }
}
