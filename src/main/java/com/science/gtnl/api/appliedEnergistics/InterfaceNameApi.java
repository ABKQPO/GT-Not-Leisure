package com.science.gtnl.api.appliedEnergistics;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;

import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

import appeng.api.util.IInterfaceViewable;

public class InterfaceNameApi {

    public static String localizeName(NBTTagCompound tag, String prefix) {
        return InterfaceNameLocalization.localizeName(tag, prefix);
    }

    public static String localizeName(String rawName, String serializedSuffix, ItemStack displayRepresentation) {
        return InterfaceNameLocalization.localizeName(rawName, serializedSuffix, displayRepresentation);
    }

    public static String localizeName(IInterfaceViewable viewable) {
        if (viewable == null) return "";
        IChatComponent suffix = viewable.getNameSuffix();
        String serializedSuffix = suffix == null ? "" : IChatComponent.Serializer.func_150696_a(suffix);
        return localizeName(viewable.getRawName(), serializedSuffix, viewable.getDisplayRep());
    }

    public static String localizeRawName(String rawName, ItemStack displayRepresentation) {
        return InterfaceNameLocalization.localizeRawName(rawName, displayRepresentation);
    }

    public static String resolveSuffix(String serializedSuffix) {
        return InterfaceNameLocalization.resolveSuffix(serializedSuffix);
    }
}
