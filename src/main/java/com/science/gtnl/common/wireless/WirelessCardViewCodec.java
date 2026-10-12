package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.DimensionManager;

import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

import galacticgreg.api.enums.DimensionDef;
import gtneioreplugin.util.DimensionHelper;

public class WirelessCardViewCodec {

    private WirelessCardViewCodec() {}

    static void writeStatistics(NBTTagCompound data, WirelessNetworkStatistics statistics) {
        data.setInteger("devices", statistics.devices());
        data.setInteger("online", statistics.online());
        data.setInteger("missing", statistics.missing());
        data.setBoolean("stable", statistics.stable());
    }

    static void writeIcon(NBTTagCompound data, String key, ItemStack icon) {
        if (icon == null || icon.getItem() == null) return;
        // Display-only identity, never ship inventories or other machine NBT as an icon.
        ItemStack clean = new ItemStack(icon.getItem(), 1, icon.getItemDamage());
        data.setTag(key, clean.writeToNBT(new NBTTagCompound()));
    }

    static void writeDimensionName(NBTTagCompound dimensions, int dimension) {
        String id = Integer.toString(dimension);
        if (dimensions.hasKey(id)) return;
        var world = DimensionManager.getWorld(dimension);
        dimensions.setString(id, world == null ? "" : Objects.toString(DimensionDef.getDimensionName(world), ""));
    }

    static void localizeDimensions(NBTTagCompound data) {
        NBTTagCompound dimensions = data.getCompoundTag("dimensions");
        for (Object key : dimensions.func_150296_c()) {
            String id = (String) key;
            String name = dimensions.getString(id);
            if (!name.isEmpty()) dimensions.setString(id, DimensionHelper.getDimLocalizedName(name));
        }
        localizePosition(data, "source", dimensions);
        localizePosition(data, "groupPosition", dimensions);
        NBTTagList rows = data.getTagList("rows", 10);
        for (int i = 0; i < rows.tagCount(); i++) localizePosition(rows.getCompoundTagAt(i), "position", dimensions);
    }

    static void localizePosition(NBTTagCompound data, String key, NBTTagCompound dimensions) {
        String position = data.getString(key);
        int end = position.indexOf(' ');
        String name = position.startsWith("D")
            ? dimensions.getString(position.substring(1, end < 0 ? position.length() : end))
            : "";
        // Keep the numeric form for coordinate copying and client-side filtering.
        data.setString(key + "Label", name.isEmpty() ? position : name + " " + position);
    }

    static void writeInterfaceNames(NBTTagCompound data, List<WirelessInterfaceView.Name> names) {
        if (names.isEmpty()) return;
        NBTTagList entries = new NBTTagList();
        for (var name : names.subList(0, Math.min(4, names.size()))) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("rawName", name.rawName());
            entry.setString("suffix", name.suffix());
            writeIcon(entry, "icon", name.icon());
            entries.appendTag(entry);
        }
        data.setTag("interfaceNames", entries);
        data.setInteger("interfaceNameCount", names.size());
        writeIcon(
            data,
            "icon",
            names.get(0)
                .icon());
    }

    static void localizeInterfaceNames(NBTTagCompound row) {
        NBTTagList entries = row.getTagList("interfaceNames", 10);
        if (entries.tagCount() == 0) return;
        List<String> names = new ArrayList<>();
        for (int i = 0; i < entries.tagCount(); i++) {
            NBTTagCompound entry = entries.getCompoundTagAt(i);
            names.add(
                InterfaceNameLocalization.localizeName(
                    entry.getString("rawName"),
                    entry.getString("suffix"),
                    ItemStack.loadItemStackFromNBT(entry.getCompoundTag("icon"))));
        }
        int count = row.getInteger("interfaceNameCount");
        String targetName = String.join(" / ", names) + (count > entries.tagCount() ? " … (" + count + ")" : "");
        row.setString("targetName", targetName);
        if (row.getString("name")
            .isEmpty()) row.setString("name", targetName);
    }
}
