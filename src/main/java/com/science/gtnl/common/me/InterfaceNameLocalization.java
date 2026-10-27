package com.science.gtnl.common.me;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

import com.science.gtnl.api.mixinHelper.IInterfaceNameHatch;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.api.interfaces.IInterfaceNameProvider;
import appeng.helpers.ICustomNameObject;
import gregtech.api.interfaces.INonConsumedItemDisplay;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.crossmod.ae2.ChatComponentGhostCircuitSuffix;
import gregtech.crossmod.ae2.ChatComponentNonConsumedItemsSuffix;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class InterfaceNameLocalization {

    public static void writeMachineName(NBTTagCompound tag, String prefix, MTEHatch hatch) {
        ItemStack craftingIcon = hatch.getMachineCraftingIcon();
        if (craftingIcon == null) craftingIcon = hatch.getStackForm(1);
        IChatComponent suffix = null;
        if (hatch.getBaseMetaTileEntity() instanceof IInterfaceNameProvider provider) {
            suffix = provider.getInterfaceNameSuffix();
        }
        if (craftingIcon != null) writeName(tag, prefix, craftingIcon.getUnlocalizedName(), suffix, craftingIcon);
    }

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
        return localizeName(rawName, serializedSuffix, displayRepresentation, null);
    }

    public static String localizeName(String rawName, String serializedSuffix, ItemStack displayRepresentation,
        EnumChatFormatting circuitColor) {
        if (rawName == null || rawName.isEmpty()) return "";
        String name = localizeRawName(rawName, displayRepresentation);
        if (serializedSuffix == null || serializedSuffix.isEmpty()) return name;
        return name + resolveSuffix(serializedSuffix, circuitColor);
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
        return resolveSuffix(serializedSuffix, null);
    }

    private static String resolveSuffix(String serializedSuffix, EnumChatFormatting circuitColor) {
        if (serializedSuffix == null || serializedSuffix.isEmpty()) return "";
        try {
            IChatComponent component = IChatComponent.Serializer.func_150699_a(serializedSuffix);
            if (component == null) return serializedSuffix;
            if (circuitColor == null) return component.getUnformattedText();
            StringBuilder suffix = new StringBuilder();
            for (Object entry : component) {
                IChatComponent part = (IChatComponent) entry;
                String text = part.getUnformattedTextForChat();
                if (part instanceof ChatComponentGhostCircuitSuffix && !text.isEmpty()) {
                    suffix.append(circuitColor)
                        .append(text)
                        .append(EnumChatFormatting.RESET);
                } else suffix.append(text);
            }
            return suffix.toString();
        } catch (Exception ignored) {
            return serializedSuffix;
        }
    }

    public static IChatComponent machineSuffix(MetaTileEntity metaTileEntity, IChatComponent nameSuffix) {
        if (metaTileEntity instanceof ICustomNameObject customNameObject && customNameObject.hasCustomName()) {
            return nameSuffix;
        }

        RecipeMap<?> recipeMap = metaTileEntity instanceof IInterfaceNameHatch hatch ? hatch.getInterfaceNameRecipeMap()
            : null;
        if (recipeMap != null) {
            IChatComponent recipeMapSuffix = new ChatComponentText(" - ")
                .appendSibling(new ChatComponentTranslation(recipeMap.unlocalizedName));
            nameSuffix = nameSuffix == null ? recipeMapSuffix : nameSuffix.appendSibling(recipeMapSuffix);
        }

        List<ItemStack> extraItems = extraItems(metaTileEntity, recipeMap);
        if (extraItems.isEmpty()) return nameSuffix;

        IChatComponent extraItemSuffix = new ChatComponentNonConsumedItemsSuffix(extraItems);
        return nameSuffix == null ? extraItemSuffix : nameSuffix.appendSibling(extraItemSuffix);
    }

    private static List<ItemStack> extraItems(MetaTileEntity metaTileEntity, RecipeMap<?> recipeMap) {
        if (!(metaTileEntity instanceof MTEHatch)
            || metaTileEntity instanceof IInterfaceNameHatch hatch && hatch.handlesOwnInterfaceName()) {
            return Collections.emptyList();
        }

        List<ItemStack> extraItems = new ObjectArrayList<>();
        for (int slot = 0; slot < metaTileEntity.getSizeInventory(); slot++) {
            ItemStack stack = metaTileEntity.getStackInSlot(slot);
            if (ItemUtils.isExtraItem(stack) && !INonConsumedItemDisplay.isDisplayableItem(recipeMap, stack)) {
                extraItems.add(stack.copy());
            }
        }
        return extraItems;
    }
}
