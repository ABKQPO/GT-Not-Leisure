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
        itemTag.setString("unrelated", "keep");
        check(!WirelessCardBinding.automatic(null), "New cards default to manual mode");
        check(!WirelessCardBinding.automatic(itemTag), "Old cards without an auto tag default to manual mode");
        WirelessCardBinding.automatic(itemTag, true);
        binding.write(itemTag);
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
