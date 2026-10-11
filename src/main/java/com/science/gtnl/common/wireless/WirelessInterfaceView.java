package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessClusterManager.LinkView;
import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

import appeng.helpers.IInterfaceHost;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;

/** Shared only within one GUI refresh, so changing inventories and output directions cannot leave stale names. */
final class WirelessInterfaceView {

    private final Map<IInterfaceHost, List<MTEHatch>> targets = new IdentityHashMap<>();
    private final Map<LinkView, List<Name>> names = new IdentityHashMap<>();

    record Name(String rawName, String suffix, ItemStack icon) {

        String text() {
            return InterfaceNameLocalization.localizeName(rawName, suffix, icon);
        }
    }

    /** Null means an output cannot be inspected safely, so machine ownership is unknown. */
    List<MTEHatch> hatches(IInterfaceHost host) {
        if (!targets.containsKey(host)) targets.put(host, readHatches(host));
        return targets.get(host);
    }

    private static List<MTEHatch> readHatches(IInterfaceHost host) {
        var tile = host.getTileEntity();
        if (tile == null || tile.isInvalid() || tile.getWorldObj() == null) return null;
        var world = tile.getWorldObj();
        List<MTEHatch> hatches = new ArrayList<>();
        for (ForgeDirection side : host.getTargets()) {
            if (side == ForgeDirection.UNKNOWN) continue;
            int x = tile.xCoord + side.offsetX, y = tile.yCoord + side.offsetY, z = tile.zCoord + side.offsetZ;
            if (!world.blockExists(x, y, z)) return null;
            var target = world.getTileEntity(x, y, z);
            if (target != null && target.isInvalid()) return null;
            if (target instanceof IGregTechTileEntity base && base.getMetaTileEntity() instanceof MTEHatch hatch)
                hatches.add(hatch);
        }
        return hatches;
    }

    List<Name> names(LinkView link) {
        if (!link.name()
            .isEmpty()) return List.of();
        return names.computeIfAbsent(link, this::readNames);
    }

    private List<Name> readNames(LinkView link) {
        Set<IInterfaceHost> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        var names = new TreeMap<String, Name>();
        for (var node : link.members()) {
            if (!(node.getMachine() instanceof IInterfaceHost host) || !seen.add(host)) continue;
            List<MTEHatch> hatches = hatches(host);
            if (hatches == null || hatches.isEmpty() || !canReadIcons(host)) continue;
            String rawName = host.getRawName();
            if (rawName == null || rawName.isEmpty()) continue;
            IChatComponent suffix = host.getNameSuffix();
            String serialized = suffix == null ? "" : IChatComponent.Serializer.func_150696_a(suffix);
            names.putIfAbsent(rawName + '\0' + serialized, new Name(rawName, serialized, host.getDisplayRep()));
        }
        return List.copyOf(names.values());
    }

    private static boolean canReadIcons(IInterfaceHost host) {
        var tile = host.getTileEntity();
        for (ForgeDirection side : host.getTargets()) {
            int x = tile.xCoord + side.offsetX * 2, z = tile.zCoord + side.offsetZ * 2;
            // AE's fallback icon ray trace extends one block beyond the already checked neighbor.
            if (((x >> 4) != ((x - side.offsetX) >> 4) || (z >> 4) != ((z - side.offsetZ) >> 4)) && !tile.getWorldObj()
                .blockExists(x, tile.yCoord + side.offsetY * 2, z)) return false;
        }
        return true;
    }
}
