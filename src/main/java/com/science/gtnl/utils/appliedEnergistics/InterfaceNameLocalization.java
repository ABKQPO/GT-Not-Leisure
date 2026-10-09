package com.science.gtnl.utils.appliedEnergistics;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class InterfaceNameLocalization {

    public static void writeName(NBTTagCompound tag, String prefix, String rawName, IChatComponent suffix,
        ItemStack displayRepresentation) {
        tag.removeTag(prefix + "RawName");
        tag.removeTag(prefix + "Suffix");
        tag.removeTag(prefix + "Display");
        tag.setString(prefix + "RawName", rawName == null ? "" : rawName);
        if (suffix != null) tag.setString(prefix + "Suffix", IChatComponent.Serializer.func_150696_a(suffix));
        if (displayRepresentation != null) {
            tag.setTag(prefix + "Display", displayRepresentation.writeToNBT(new NBTTagCompound()));
        }
    }

    public static String localizeName(NBTTagCompound tag, String prefix) {
        if (!tag.hasKey(prefix + "RawName")) return "";
        ItemStack display = tag.hasKey(prefix + "Display")
            ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag(prefix + "Display"))
            : null;
        return localizeName(tag.getString(prefix + "RawName"), tag.getString(prefix + "Suffix"), display);
    }

    public static String localizeName(String rawName, String serializedSuffix, ItemStack displayRepresentation) {
        if (rawName == null || rawName.isEmpty()) return "";
        String name = localizeRawName(rawName, displayRepresentation);
        if (serializedSuffix == null || serializedSuffix.isEmpty()) return name;
        return name + resolveSuffix(serializedSuffix);
    }

    public static String localizeRawName(String rawName, ItemStack displayRepresentation) {
        if (rawName == null || rawName.isEmpty()) return "";
        if (StatCollector.canTranslate(rawName)) return StatCollector.translateToLocal(rawName);

        String fallbackKey = rawName + ".name";
        if (StatCollector.canTranslate(fallbackKey)) return StatCollector.translateToLocal(fallbackKey);
        if (displayRepresentation != null) return displayRepresentation.getDisplayName();
        return StatCollector.translateToFallback(rawName);
    }

    public static String resolveSuffix(String serializedSuffix) {
        if (serializedSuffix == null || serializedSuffix.isEmpty()) return "";
        try {
            IChatComponent component = IChatComponent.Serializer.func_150699_a(serializedSuffix);
            return component == null ? serializedSuffix : component.getUnformattedText();
        } catch (Exception ignored) {
            return serializedSuffix;
        }
    }
}
