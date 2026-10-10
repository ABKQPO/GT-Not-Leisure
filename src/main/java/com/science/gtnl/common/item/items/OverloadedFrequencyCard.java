package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.wireless.AutomaticWirelessEntrances;
import com.science.gtnl.common.wireless.WirelessCardBinding;
import com.science.gtnl.common.wireless.WirelessCardContainer;
import com.science.gtnl.common.wireless.WirelessCardInventory;
import com.science.gtnl.common.wireless.WirelessCardVisualisation;
import com.science.gtnl.common.wireless.WirelessChannelPrototype;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Entrance;
import com.science.gtnl.common.wireless.WirelessClusterManager;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.pathing.ControllerState;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.tile.networking.TileController;
import baubles.api.BaubleType;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A card toggles its source's entire physical cluster, including a suspended conflicting claim. */
public final class OverloadedFrequencyCard extends Item implements IBaubleExpanded, IGuiHolder<PlayerInventoryGuiData> {

    private static final String LANG = "item.gtnl.overloaded_frequency_card.";

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        int index = data.getSlotIndex();
        int location = data.getInventoryType() == InventoryTypes.BAUBLES ? -index - 1
            : index == data.getPlayer().inventory.currentItem ? 0 : index + 1;
        var container = new WirelessCardContainer(data.getPlayer(), location);
        settings.customContainer(() -> container);
        settings.canInteractWith(container::canInteractWith);
        syncManager.syncValue("wireless", container.sync);
        // All synchronization is explicitly registered above on both sides. Rendering widgets are client-only.
        return syncManager.isClient() ? createPanel(container) : new ModularPanel("wireless_card").size(320, 232);
    }

    @SideOnly(Side.CLIENT)
    private ModularPanel createPanel(WirelessCardContainer container) {
        return new com.science.gtnl.client.WirelessCardGui(container);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel panel) {
        return ((com.science.gtnl.client.WirelessCardGui) panel).screen();
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        // Expanded uses getBaubleTypes; keep the legacy API compatible with bundled older enums.
        return BaubleType.RING;
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return new String[] { BaubleExpandedSlots.universalType };
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (player instanceof EntityPlayerMP serverPlayer && !player.worldObj.isRemote)
            WirelessCardVisualisation.update(stack, serverPlayer, 0);
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {}

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {}

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase wearer) {
        if (!(wearer instanceof EntityPlayer player)) return false;
        WirelessCardBinding binding = WirelessCardBinding.read(stack.getTagCompound());
        return binding == null || binding.belongsTo(player.getUniqueID());
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    public OverloadedFrequencyCard() {
        setUnlocalizedName("gtnl.overloaded_frequency_card");
        setTextureName(RESOURCE_ROOT_ID + ":wireless_frequency_card");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        setMaxStackSize(1);
        GameRegistry.registerItem(this, "overloaded_frequency_card");
        GTNLItemList.OverloadedFrequencyCard.set(new ItemStack(this));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean active) {
        if (!world.isRemote && active && entity instanceof EntityPlayerMP player)
            WirelessCardVisualisation.update(stack, player, slot);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // Returning true on the client here would prevent vanilla's block-use packet from being sent.
        if (world.isRemote) return false;
        WirelessCardBinding binding = WirelessCardBinding.read(stack.getTagCompound());
        if (!ownerAllowed(binding, player)) return true;
        if (!player.canPlayerEdit(x, y, z, side, stack)) {
            message(player, "denied");
            return true;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (player.isSneaking()) {
            if (WirelessChannelPrototype.isSupportedController(tile)) {
                bind(stack, player, new Address(world.provider.dimensionId, x, y, z, ForgeDirection.UNKNOWN));
            } else {
                message(player, "bind_hint");
            }
            return true;
        }
        if (binding == null) {
            message(player, "unbound");
            return true;
        }
        if (tile instanceof TileController) {
            showStatus(binding, player);
            return true;
        }
        Address target = selectTarget(world, x, y, z, side, hitX, hitY, hitZ);
        IGridNode targetNode = target == null ? null : target.node();
        if (targetNode == null) {
            message(player, "invalid_target");
            return true;
        }
        if (!canBuild(targetNode, player)) return true;
        try {
            WirelessClusterManager.refreshTarget(targetNode);
            if (WirelessClusterManager.isLinked(binding.source(), targetNode)) {
                // A suspended binding can be removed even while its source chunk is unavailable.
                IGridNode source = binding.source()
                    .node();
                if (source != null && !canBuild(source, player)) return true;
                WirelessClusterManager.disconnectCluster(binding.source(), targetNode);
                message(player, "cluster_disconnected");
            } else {
                if (sourceNode(binding, player) == null) return true;
                WirelessClusterManager.connectCluster(binding.source(), target);
                message(player, "cluster_connected");
            }
        } catch (IllegalArgumentException | IllegalStateException | FailedConnection failure) {
            message(player, "failed", failure.getMessage());
        }
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // The block packet has now been sent. Consume the client click to suppress the air-use fallback.
        return true;
    }

    private static Address selectTarget(World world, int x, int y, int z, int side, float hitX, float hitY,
        float hitZ) {
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IPartHost host) {
            SelectedPart selected = host.selectPart(Vec3.createVectorHelper(hitX, hitY, hitZ));
            if (selected.part == null) return null; // A facade is not a grid node.
            direction = selected.side;
        }
        return new Address(world.provider.dimensionId, x, y, z, direction);
    }

    private static void bind(ItemStack stack, EntityPlayer player, Address source) {
        IGridNode node = source.node();
        if (node == null) {
            message(player, "unavailable");
            return;
        }
        if (!canBuild(node, player)) return;
        IPathingGrid path = node.getGrid()
            .getCache(IPathingGrid.class);
        if (path.isNetworkBooting() || path.getControllerState() != ControllerState.CONTROLLER_ONLINE) {
            message(player, "unstable");
            return;
        }
        source = WirelessChannelPrototype.controllerSource(source);
        if (stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
        new WirelessCardBinding(source, player.getUniqueID(), player.getCommandSenderName())
            .write(stack.getTagCompound());
        WirelessCardInventory.sync(player, stack);
        message(player, "bound", source.dimension(), source.x(), source.y(), source.z());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) return stack;
        // Defensive guard: block interactions must never clear a binding via vanilla's air-use fallback.
        MovingObjectPosition hit = getMovingObjectPositionFromPlayer(world, player, false);
        if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) return stack;
        WirelessCardBinding binding = WirelessCardBinding.read(stack.getTagCompound());
        if (!ownerAllowed(binding, player)) return stack;
        if (player.isSneaking()) {
            WirelessCardBinding.clear(stack.getTagCompound());
            WirelessCardInventory.sync(player, stack);
            message(player, "cleared");
        } else {
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        }
        return stack;
    }

    private static boolean ownerAllowed(WirelessCardBinding binding, EntityPlayer player) {
        if (binding == null || binding.belongsTo(player.getUniqueID())) return true;
        message(player, "wrong_owner", binding.ownerName());
        return false;
    }

    private static boolean canBuild(IGridNode node, EntityPlayer player) {
        if (WirelessChannelPrototype.canBuild(node, player)) return true;
        message(player, "denied");
        return false;
    }

    private static IGridNode sourceNode(WirelessCardBinding binding, EntityPlayer player) {
        TileEntity tile = binding.source()
            .tile();
        IGridNode node = WirelessChannelPrototype.isSupportedController(tile) ? binding.source()
            .node() : null;
        if (node == null) {
            message(player, "unavailable");
            return null;
        }
        return canBuild(node, player) ? node : null;
    }

    private static void showStatus(WirelessCardBinding binding, EntityPlayer player) {
        WirelessClusterManager.refresh();
        var summary = WirelessClusterManager.summary(binding.source());
        message(
            player,
            "cluster_status",
            summary.clusters(),
            summary.active(),
            summary.conflicted(),
            summary.waiting());
        if (summary.scanLimited()) message(player, "scan_limit");
        message(
            player,
            "planner_status",
            new ChatComponentTranslation(
                LANG + "planner."
                    + AutomaticWirelessEntrances.status(binding.source())
                        .name()
                        .toLowerCase(Locale.ROOT)));
        IGridNode node = sourceNode(binding, player);
        if (node == null) return;
        var grid = node.getGrid();
        IPathingGrid path = grid.getCache(IPathingGrid.class);
        int entrances = 0;
        for (Entrance entrance : WirelessChannelPrototype.entrances()) {
            if (entrance.isLive() && entrance.sourceNode()
                .getGrid() == grid) entrances++;
        }
        message(player, "status", WirelessChannelPrototype.capacity(grid), entrances);
        if (!WirelessChannelPrototype.channelsEnabled()) {
            message(player, "channels_disabled");
        } else if (!AutomaticWirelessEntrances.isSettled(path)) {
            message(player, "unstable");
        } else {
            var allocation = WirelessChannelPrototype.allocation(grid);
            if (entrances > 0 && allocation != null) {
                message(player, "allocation", allocation.used(), allocation.capacity());
            } else {
                message(player, "no_allocation");
            }
        }
    }

    private static void message(EntityPlayer player, String key, Object... args) {
        player.addChatMessage(new ChatComponentTranslation(LANG + key, args));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return WirelessCardBinding.read(stack.getTagCompound()) != null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(
            StatCollector.translateToLocal(
                LANG + (WirelessCardBinding.automatic(stack.getTagCompound()) ? "auto_on" : "auto_off")));
        WirelessCardBinding binding = WirelessCardBinding.read(stack.getTagCompound());
        if (binding == null) {
            tooltip.add(StatCollector.translateToLocal(LANG + "unbound"));
        } else {
            Address source = binding.source();
            tooltip.add(
                StatCollector
                    .translateToLocalFormatted(LANG + "bound", source.dimension(), source.x(), source.y(), source.z()));
            tooltip.add(StatCollector.translateToLocalFormatted(LANG + "owner", binding.ownerName()));
        }
        tooltip.add(StatCollector.translateToLocal(LANG + "manage_hint"));
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(
                StatCollector.translateToLocalFormatted(
                    "gtnl.wireless.gui.visualisation",
                    StatCollector.translateToLocal(
                        WirelessCardVisualisation.modeKey(WirelessCardVisualisation.mode(stack.getTagCompound())))));
            tooltip.add(StatCollector.translateToLocal("gtnl.wireless.gui.visualisation_hint"));
            for (int i = 0; i < 3; i++) tooltip.add(StatCollector.translateToLocal(LANG + "tooltip." + i));
            tooltip.add(StatCollector.translateToLocal(LANG + "auto_hint"));
            tooltip.add(StatCollector.translateToLocal(LANG + "bauble_hint"));
        } else tooltip.add(StatCollector.translateToLocal(LANG + "more_hint"));
    }
}
