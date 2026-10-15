package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static net.minecraft.util.EnumChatFormatting.AQUA;
import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.YELLOW;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.item.ItemStackNBT;
import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.client.topology.NetworkTopologyGui;
import com.science.gtnl.common.topology.TopologyBinding;
import com.science.gtnl.common.topology.TopologyContainer;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NetworkTopologyTool extends Item implements IGuiHolder<PlayerInventoryGuiData> {

    private static final String LANG = "item.gtnl.network_topology_tool.";

    public NetworkTopologyTool() {
        setUnlocalizedName("gtnl.network_topology_tool");
        setTextureName(RESOURCE_ROOT_ID + ":network_topology_tool");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        setMaxStackSize(1);
        GameRegistry.registerItem(this, "network_topology_tool");
        GTNLItemList.NetworkTopologyTool.set(new ItemStack(this));
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        int index = data.getSlotIndex();
        int location = index == data.getPlayer().inventory.currentItem ? 0 : index + 1;
        TopologyContainer container = new TopologyContainer(data.getPlayer(), location);
        settings.customContainer(() -> container);
        settings.canInteractWith(container::canInteractWith);
        syncManager.syncValue("topology", container.sync);
        // Every synchronised value is registered above on both sides; the rendering widgets are client only.
        return syncManager.isClient() ? createPanel(container) : new ModularPanel("network_topology");
    }

    @SideOnly(Side.CLIENT)
    private ModularPanel createPanel(TopologyContainer container) {
        return new NetworkTopologyGui(container);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel panel) {
        return ((NetworkTopologyGui) panel).screen();
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // Returning true on the client would suppress vanilla's block-use packet before the server ever sees it.
        if (world.isRemote) return false;
        TopologyBinding binding = TopologyBinding.read(stack.getTagCompound());
        if (!ownerAllowed(binding, player)) return true;
        GridCoord target = selectTarget(world, x, y, z, side, hitX, hitY, hitZ);
        IGridNode node = target == null ? null : target.node();
        if (node == null || node.getGrid() == null) {
            message(player, "invalid_target");
            return true;
        }
        if (!canView(node.getGrid(), player)) {
            message(player, "denied");
            return true;
        }
        new TopologyBinding(target, player.getUniqueID(), player.getCommandSenderName()).write(ItemStackNBT.get(stack));
        syncHeldTool(player);
        message(player, "bound", target.dimension(), target.x(), target.y(), target.z());
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // The block packet has been sent by now; consume the client click so the air-use fallback does not fire.
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) return stack;
        // Defensive guard: a block interaction must never clear the binding through the air-use fallback.
        MovingObjectPosition hit = getMovingObjectPositionFromPlayer(world, player, false);
        if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) return stack;
        TopologyBinding binding = TopologyBinding.read(stack.getTagCompound());
        if (!ownerAllowed(binding, player)) return stack;
        if (player.isSneaking()) {
            TopologyBinding.clear(stack.getTagCompound());
            syncHeldTool(player);
            message(player, "cleared");
            return stack;
        }
        if (binding == null) {
            message(player, "unbound");
            return stack;
        }
        GuiFactories.playerInventory()
            .openFromMainHand(player);
        return stack;
    }

    private static GridCoord selectTarget(World world, int x, int y, int z, int side, float hitX, float hitY,
        float hitZ) {
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IPartHost host) {
            SelectedPart selected = host.selectPart(Vec3.createVectorHelper(hitX, hitY, hitZ));
            if (selected.part == null) return null; // A facade is not a grid node.
            direction = selected.side;
        }
        return new GridCoord(world.provider.dimensionId, x, y, z, direction);
    }

    private static boolean canView(IGrid grid, EntityPlayer player) {
        ISecurityGrid security = grid.getCache(ISecurityGrid.class);
        return security == null || security.hasPermission(player, SecurityPermissions.BUILD);
    }

    private static boolean ownerAllowed(TopologyBinding binding, EntityPlayer player) {
        if (binding == null || binding.belongsTo(player.getUniqueID())) return true;
        message(player, "wrong_owner", binding.ownerName());
        return false;
    }

    private static void syncHeldTool(EntityPlayer player) {
        player.inventory.markDirty();
        player.inventoryContainer.detectAndSendChanges();
        if (player.openContainer != player.inventoryContainer) player.openContainer.detectAndSendChanges();
    }

    private static void message(EntityPlayer player, String key, Object... args) {
        player.addChatMessage(new ChatComponentTranslation(LANG + key, args));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return TopologyBinding.read(stack.getTagCompound()) != null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        TopologyBinding binding = TopologyBinding.read(stack.getTagCompound());
        if (binding == null) {
            tooltip.add(YELLOW + StatCollector.translateToLocal(LANG + "source_unbound"));
        } else {
            GridCoord source = binding.source();
            tooltip.add(
                AQUA + StatCollector
                    .translateToLocalFormatted(LANG + "bound", source.dimension(), source.x(), source.y(), source.z()));
            tooltip.add(GRAY + StatCollector.translateToLocalFormatted(LANG + "owner", binding.ownerName()));
        }
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(GRAY + StatCollector.translateToLocal(LANG + "tooltip.0"));
            tooltip.add(GRAY + StatCollector.translateToLocal(LANG + "tooltip.1"));
            tooltip.add(GRAY + StatCollector.translateToLocal(LANG + "tooltip.2"));
            tooltip.add(GRAY + StatCollector.translateToLocal(LANG + "tooltip.3"));
        } else tooltip.add(YELLOW + StatCollector.translateToLocal(LANG + "more_hint"));
    }
}
