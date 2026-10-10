package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.compat.BaublesCompat;
import com.science.gtnl.common.item.items.OverloadedFrequencyCard;
import com.science.gtnl.common.wireless.WirelessCardSelection.Candidate;

import appeng.api.AEApi;
import appeng.api.config.Settings;
import appeng.api.util.DimensionalCoord;
import appeng.items.tools.ToolNetworkVisualiser;
import appeng.items.tools.ToolNetworkVisualiser.VisualisationModes;
import appeng.tile.networking.TileController;

/** Adapts a bound card to AE's existing collector; it never creates or removes network links. */
public final class WirelessCardVisualisation {

    private static final String MODE = "GTNLWirelessVisualisationMode";
    public static final String DELEGATE = "GTNLWirelessVisualiserDelegate";
    public static final int MAX_PACKET_BYTES = 2_000_000;
    private static final Map<EntityPlayerMP, Refresh> LAST_UPDATES = new WeakHashMap<>();

    record Refresh(WirelessCardBinding binding, int dimension, long tick) {

        boolean due(WirelessCardBinding current, int viewerDimension, long now) {
            return !binding.equals(current) || dimension != viewerDimension || now < tick || now - tick >= 100;
        }
    }

    private WirelessCardVisualisation() {}

    public static int mode(NBTTagCompound tag) {
        int value = tag == null ? 0 : tag.getInteger(MODE);
        return validMode(value) ? value : 0;
    }

    public static boolean validMode(int value) {
        return value >= 0 && value <= VisualisationModes.values().length;
    }

    public static int cycle(NBTTagCompound tag) {
        int next = (mode(tag) + 1) % (VisualisationModes.values().length + 1);
        tag.setInteger(MODE, next);
        return next;
    }

    public static String modeKey(int mode) {
        return mode <= 0 || !validMode(mode) ? "gtnl.wireless.gui.visualisation_off"
            : "gtnl.wireless.gui.visualisation." + VisualisationModes.values()[mode - 1].name()
                .toLowerCase(Locale.ROOT);
    }

    public static void cycle(ItemStack card, EntityPlayer player) {
        if (card.getTagCompound() == null) card.setTagCompound(new NBTTagCompound());
        if (cycle(card.getTagCompound()) == 1) LAST_UPDATES.remove(player);
        WirelessCardInventory.sync(player, card);
    }

    /** Separate card cadence from the native tool; enabling must not inherit an old cooldown. */
    public static boolean needsUpdate(EntityPlayerMP player, ItemStack tool) {
        var binding = WirelessCardBinding.read(tool.getTagCompound());
        if (binding == null) return false;
        long now = player.worldObj.getTotalWorldTime();
        Refresh previous = LAST_UPDATES.get(player);
        if (previous != null && !previous.due(binding, player.dimension, now)) return false;
        LAST_UPDATES.put(player, new Refresh(binding, player.dimension, now));
        return true;
    }

    public static ItemStack tool(WirelessCardBinding binding, int dimension, int mode) {
        if (mode <= 0 || !validMode(mode)) return null;
        ItemStack tool = AEApi.instance()
            .definitions()
            .items()
            .toolNetworkVisualiser()
            .maybeStack(1)
            .orNull();
        if (tool == null) return null;
        NBTTagCompound tag = new NBTTagCompound();
        binding.write(tag);
        tag.setBoolean(DELEGATE, true);
        var source = binding.source();
        // Single-link modes are anchored to the source block. Do not select an unrelated block at the
        // same coordinates when viewing another dimension; full-network modes still show local nodes.
        new DimensionalCoord(source.x(), dimension == source.dimension() ? source.y() : -1, source.z(), dimension)
            .writeToNBT(tag);
        tool.setTagCompound(tag);
        ToolNetworkVisualiser.getConfigManager(tool)
            .putSetting(Settings.NETWORK_VISUALISER, VisualisationModes.values()[mode - 1]);
        return tool;
    }

    public static boolean delegated(ItemStack stack) {
        return stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .getBoolean(DELEGATE);
    }

    /** Share selection between worn ticks, held-item ticks and the client renderer. */
    public static ItemStack activeCard(EntityPlayer player) {
        if (player == null || player.isDead) return null;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ToolNetworkVisualiser) return null;
        var candidates = new ArrayList<Candidate<ItemStack>>();
        for (ItemStack stack : WirelessCardInventory
            .collect(new ItemStack[] { held }, BaublesCompat.getBaubles(player))) {
            if (!(stack.getItem() instanceof OverloadedFrequencyCard)) continue;
            var binding = WirelessCardBinding.read(stack.getTagCompound());
            candidates.add(
                new Candidate<>(
                    stack,
                    stack == held,
                    binding != null && binding.belongsTo(player.getUniqueID()),
                    binding != null,
                    mode(stack.getTagCompound()) != 0));
        }
        return WirelessCardSelection.select(candidates, true)
            .card();
    }

    public static void update(ItemStack card, EntityPlayerMP player, int slot) {
        if (activeCard(player) != card) return;
        int mode = mode(card.getTagCompound());
        if (mode == 0) return;
        WirelessCardBinding binding = WirelessCardBinding.read(card.getTagCompound());
        if (binding == null || !binding.belongsTo(player.getUniqueID())) return;
        var source = binding.source()
            .node();
        if (!WirelessChannelPrototype.canBuild(source, player)) return;
        // Address.node() only visits already loaded chunks. Never let the native collector load its target.
        var tile = binding.source()
            .tile();
        if (tile == null || tile.getClass() != TileController.class || tile.isInvalid()) return;
        ItemStack tool = tool(
            binding,
            binding.source()
                .dimension(),
            mode);
        if (tool != null) tool.getItem()
            .onUpdate(tool, tile.getWorldObj(), player, slot, true);
    }
}
