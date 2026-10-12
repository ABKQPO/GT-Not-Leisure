package com.science.gtnl.common.wireless;

import static com.gtnewhorizon.gtnhlib.capability.Capabilities.getCapability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.mixinHelper.IDualityInterface;

import appeng.api.AEApi;
import appeng.api.crafting.ICraftingIconProvider;
import appeng.api.interfaces.IInterfaceNameProvider;
import appeng.api.networking.security.MachineSource;
import appeng.api.parts.IStorageBus;
import appeng.api.util.IInterfaceViewable;
import appeng.helpers.IInterfaceHost;
import appeng.parts.AEBasePart;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;

public class WirelessInterfaceView {

    private final Map<IInterfaceHost, List<MTEHatch>> targets = new IdentityHashMap<>();
    private final Map<WirelessLinkView, List<Name>> names = new IdentityHashMap<>();

    record Name(String rawName, String suffix, ItemStack icon) {

        Name(String rawName, IChatComponent suffix, ItemStack icon) {
            this(rawName, suffix == null ? "" : IChatComponent.Serializer.func_150696_a(suffix), icon);
        }

    }

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
            // AE checks all horizontal neighbors when resolving double chests.
            if (target instanceof TileEntityChest && !world.checkChunksExist(x - 1, y, z - 1, x + 1, y, z + 1))
                return null;
            if (target instanceof IGregTechTileEntity base && base.getMetaTileEntity() instanceof MTEHatch hatch)
                hatches.add(hatch);
        }
        return hatches;
    }

    List<Name> names(WirelessLinkView link) {
        return names.computeIfAbsent(link, this::readNames);
    }

    private List<Name> readNames(WirelessLinkView link) {
        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        var names = new TreeMap<String, Name>();
        for (var node : link.members()) {
            Object machine = node.getMachine();
            if (!(machine instanceof IInterfaceViewable) && machine instanceof IGregTechTileEntity base)
                machine = base.getMetaTileEntity();
            if (!seen.add(machine)) continue;
            Name name;
            if (machine instanceof IInterfaceHost host) {
                name = ((IDualityInterface) host.getInterfaceDuality())
                    .gtnl$withoutCustomName(() -> readInterfaceName(host));
            } else if (machine instanceof IInterfaceViewable host) name = readInterfaceName(host);
            else if (machine instanceof IStorageBus bus && machine instanceof AEBasePart part)
                name = readStorageName(bus, part);
            else continue;
            if (name != null) names.putIfAbsent(name.rawName() + '\0' + name.suffix(), name);
        }
        return List.copyOf(names.values());
    }

    private Name readInterfaceName(IInterfaceViewable host) {
        var tile = host.getTileEntity();
        if (tile == null || tile.isInvalid() || tile.getWorldObj() == null) return null;
        if (host instanceof IInterfaceHost iface && (hatches(iface) == null || !canReadIcons(iface))) return null;
        ItemStack icon = host.getDisplayRep();
        // An interface without a target reports "Nothing"; retain its normal device label instead.
        if (host instanceof IInterfaceHost && (icon == null || icon.getItem() == null)) return null;
        if (icon == null) icon = host.getSelfRep();
        String rawName = host.getRawName();
        return rawName == null || rawName.isEmpty() ? null : new Name(rawName, host.getNameSuffix(), icon);
    }

    private static Name readStorageName(IStorageBus bus, AEBasePart part) {
        var host = part.getHost();
        var tile = host == null ? null : host.getTile();
        ForgeDirection side = part.getSide();
        if (tile == null || tile.isInvalid()
            || tile.getWorldObj() == null
            || side == null
            || side == ForgeDirection.UNKNOWN) return null;
        var world = tile.getWorldObj();
        int x = tile.xCoord + side.offsetX, y = tile.yCoord + side.offsetY, z = tile.zCoord + side.offsetZ;
        if (!world.blockExists(x, y, z)) return null;
        var target = world.getTileEntity(x, y, z);
        if (target == null || target.isInvalid()) return null;
        if (target instanceof TileEntityChest && !world.checkChunksExist(x - 1, y, z - 1, x + 1, y, z + 1)) return null;
        // Select AE's adapter without constructing an inventory or resetting the bus's live storage handler.
        if (AEApi.instance()
            .registries()
            .externalStorage()
            .getHandler(target, side.getOpposite(), bus.getStackType(), new MachineSource(part)) == null) return null;
        var iconProvider = getCapability(target, ICraftingIconProvider.class);
        ItemStack icon = iconProvider == null ? null : iconProvider.getMachineCraftingIcon();
        if (icon == null || icon.getItem() == null) {
            var block = world.getBlock(x, y, z);
            icon = new ItemStack(block, 1, block.getDamageValue(world, x, y, z));
        }
        if (icon.getItem() == null) return null;
        var nameProvider = getCapability(target, IInterfaceNameProvider.class);
        return new Name(
            icon.getUnlocalizedName(),
            nameProvider == null ? null : nameProvider.getInterfaceNameSuffix(),
            icon);
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
