package com.science.gtnl.common.wireless;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

import com.science.gtnl.common.wireless.WirelessMachineGroups.Row;
import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

/** Share display identities for every page; localize with the same client code used to render the rows. */
final class WirelessSearchNames {

    private final WirelessSearchIndex<Key> index = new WirelessSearchIndex<>();

    String name(ItemStack icon) {
        // Retain the original server-language keywords as well as the label visible to this player.
        return (icon == null ? "" : icon.getDisplayName()) + " " + index.name(Key.of(icon));
    }

    String name(WirelessInterfaceView.Name name) {
        return InterfaceNameLocalization.localizeName(name.rawName(), name.suffix(), name.icon()) + " "
            + index.name(Key.of(name));
    }

    void accept(Reply reply) {
        index.accept(reply.revision(), reply.start(), reply.names());
    }

    void update(List<Row> rows, WirelessInterfaceView interfaces, long session, Consumer<NBTTagCompound> send) {
        var candidates = new LinkedHashSet<Key>();
        for (Row row : rows) {
            candidates.add(Key.of(row.icon()));
            for (var link : row.links()) {
                candidates.add(
                    Key.of(
                        link.node()
                            .getGridBlock()
                            .getMachineRepresentation()));
                for (var name : interfaces.names(link)) candidates.add(Key.of(name));
            }
        }
        if (!index.update(candidates)) return;
        List<Key> keys = index.keys();
        for (int start : index.pendingBatches()) {
            NBTTagCompound data = new NBTTagCompound();
            data.setLong("session", session);
            data.setLong("revision", index.revision());
            data.setInteger("start", start);
            NBTTagList entries = new NBTTagList();
            for (int i = start; i < Math.min(keys.size(), start + WirelessSearchIndex.BATCH_SIZE); i++)
                entries.appendTag(
                    keys.get(i)
                        .write());
            data.setTag("names", entries);
            send.accept(data);
        }
    }

    record Key(Item item, int damage, String rawName, String suffix) {

        static Key of(ItemStack icon) {
            return of(icon, "", "");
        }

        static Key of(WirelessInterfaceView.Name name) {
            return of(name.icon(), name.rawName(), name.suffix());
        }

        private static Key of(ItemStack icon, String rawName, String suffix) {
            return new Key(
                icon == null ? null : icon.getItem(),
                icon == null ? 0 : icon.getItemDamage(),
                rawName,
                suffix);
        }

        NBTTagCompound write() {
            NBTTagCompound data = new NBTTagCompound();
            if (item != null) data.setTag("icon", new ItemStack(item, 1, damage).writeToNBT(new NBTTagCompound()));
            data.setString("rawName", rawName);
            data.setString("suffix", suffix);
            return data;
        }
    }

    record Reply(long session, long revision, int start, List<String> names) {

        static Reply read(PacketBuffer buf) throws IOException {
            long session = buf.readLong(), revision = buf.readLong();
            int start = buf.readInt(), count = buf.readUnsignedByte();
            if (count == 0 || count > WirelessSearchIndex.BATCH_SIZE)
                throw new IllegalArgumentException("Invalid batch");
            List<String> names = new ArrayList<>(count);
            for (int i = 0; i < count; i++) names.add(buf.readStringFromBuffer(WirelessSearchIndex.MAX_NAME_LENGTH));
            return new Reply(session, revision, start, names);
        }

        void write(PacketBuffer buf) throws IOException {
            buf.writeLong(session);
            buf.writeLong(revision);
            buf.writeInt(start);
            buf.writeByte(names.size());
            for (String name : names) buf.writeStringToBuffer(name);
        }
    }

    static Reply localize(NBTTagCompound data) {
        NBTTagList entries = data.getTagList("names", 10);
        List<String> names = new ArrayList<>();
        for (int i = 0; i < entries.tagCount(); i++) {
            NBTTagCompound entry = entries.getCompoundTagAt(i);
            ItemStack icon = ItemStack.loadItemStackFromNBT(entry.getCompoundTag("icon"));
            String rawName = entry.getString("rawName");
            String text = rawName.isEmpty() ? icon == null ? "" : icon.getDisplayName()
                : InterfaceNameLocalization.localizeName(rawName, entry.getString("suffix"), icon);
            names.add(text.substring(0, Math.min(text.length(), WirelessSearchIndex.MAX_NAME_LENGTH)));
        }
        return new Reply(data.getLong("session"), data.getLong("revision"), data.getInteger("start"), names);
    }
}
