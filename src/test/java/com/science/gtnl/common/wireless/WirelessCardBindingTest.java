package com.science.gtnl.common.wireless;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;

/** Exercises the actual saved NBT format; does not initialize Minecraft registries or a world. */
public final class WirelessCardBindingTest {

    public static void main(String[] args) throws Exception {
        UUID owner = UUID.fromString("01111111-2222-4333-8444-555555555555");
        WirelessCardBinding binding = new WirelessCardBinding(
            new Address(-1, -120, 65, 300, ForgeDirection.UNKNOWN),
            owner,
            "测试玩家");
        NBTTagCompound itemTag = new NBTTagCompound();
        var refresh = new WirelessCardVisualisation.Refresh(binding, 0, 200);
        check(!refresh.due(binding, 0, 299), "Continuous viewing retains its periodic scan limit");
        check(refresh.due(binding, 0, 300), "A snapshot refreshes after 100 ticks");
        check(refresh.due(binding, -1, 201), "Changing viewer dimension immediately requests local nodes");
        check(refresh.due(binding, 0, 1), "A world clock reset cannot stall refreshing");
        check(
            refresh.due(new WirelessCardBinding(new Address(0, 1, 2, 3, ForgeDirection.UNKNOWN), owner, "new"), 0, 201),
            "Changing source bypasses the previous network's refresh limit");
        itemTag.setString("unrelated", "keep");
        check(!WirelessCardBinding.automatic(null), "New cards default to manual mode");
        check(!WirelessCardBinding.automatic(itemTag), "Old cards without an auto tag default to manual mode");
        WirelessCardBinding.automatic(itemTag, true);
        binding.write(itemTag);
        check(WirelessCardVisualisation.mode(itemTag) == 0, "Old cards default to no visualisation");
        for (int i = 1; i <= 8; i++) {
            check(WirelessCardVisualisation.cycle(itemTag) == i, "Cycle every native AE display mode");
            check(
                binding.equals(WirelessCardBinding.read(itemTag)),
                "Changing display mode preserves frequency ownership");
            check(WirelessCardBinding.automatic(itemTag), "Changing display mode preserves automatic connection");
        }
        check(WirelessCardVisualisation.cycle(itemTag) == 0, "Display cycle returns to Off");
        for (int invalidMode : new int[] { -1, 9, Integer.MAX_VALUE }) {
            itemTag.setInteger("GTNLWirelessVisualisationMode", invalidMode);
            check(WirelessCardVisualisation.mode(itemTag) == 0, "Malformed mode is safely disabled");
        }
        WirelessCardVisualisation.cycle(itemTag);
        check(WirelessCardBinding.automatic(itemTag), "Binding preserves the selected automatic mode");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompressedStreamTools.writeCompressed(itemTag, bytes);
        NBTTagCompound restored = CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes.toByteArray()));
        check(
            binding.equals(WirelessCardBinding.read(restored)),
            "Owner and cross-dimension coordinates survive saved NBT");
        check(binding.belongsTo(owner), "Bound player can use the card");
        check(
            !binding.belongsTo(UUID.fromString("61111111-2222-4333-8444-555555555555")),
            "Another UUID cannot use the card");
        WirelessCardBinding.clear(restored);
        check(WirelessCardBinding.read(restored) == null, "Clearing removes only the binding");
        check(WirelessCardVisualisation.mode(restored) == 1, "Display preference survives saving and clearing binding");
        check(WirelessCardBinding.automatic(restored), "Automatic mode survives saving and clearing a binding");
        binding.write(restored);
        check(WirelessCardBinding.automatic(restored), "Rebinding preserves automatic mode");
        WirelessCardBinding.automatic(restored, false);
        check(
            binding.equals(WirelessCardBinding.read(restored)),
            "Switching auto mode never changes frequency or owner");
        check("keep".equals(restored.getString("unrelated")), "Clearing preserves other item data");
        check(WirelessCardBinding.read(null) == null, "A new card has no binding");
        for (String field : new String[] { "version", "dimension", "x", "y", "z", "owner" }) {
            NBTTagCompound damaged = (NBTTagCompound) itemTag.copy();
            damaged.getCompoundTag("GTNLWirelessCard")
                .removeTag(field);
            check(
                WirelessCardBinding.read(damaged) == null,
                "Missing " + field + " must not resolve to default coordinates or owner");
        }
        NBTTagCompound invalid = (NBTTagCompound) itemTag.copy();
        invalid.getCompoundTag("GTNLWirelessCard")
            .setString("owner", "not-a-uuid");
        check(WirelessCardBinding.read(invalid) == null, "Malformed owner is rejected");
        invalid = (NBTTagCompound) itemTag.copy();
        invalid.getCompoundTag("GTNLWirelessCard")
            .setInteger("version", 2);
        check(
            WirelessCardBinding.read(invalid) == null,
            "Unknown data versions are not interpreted as prototype bindings");
        System.out.println("WirelessCardBindingTest: saved data and ownership checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
