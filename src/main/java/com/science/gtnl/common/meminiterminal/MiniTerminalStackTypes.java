package com.science.gtnl.common.meminiterminal;

import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import net.minecraft.item.ItemStack;

import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;

public class MiniTerminalStackTypes {

    private static final int MAX_FILTERS = 32;

    public static IAEStackType<?> containerType(ItemStack stack) {
        if (stack == null) return null;
        for (IAEStackType<?> type : AEStackTypeRegistry.getSortedTypes()) {
            if (type != ITEM_STACK_TYPE && type.isContainerItemForType(stack)) return type;
        }
        return null;
    }

    public static IAEStack<?> containerContents(ItemStack stack) {
        IAEStackType<?> type = containerType(stack);
        return type == null ? null : type.getStackFromContainerItem(stack);
    }

    public static IAEStack<?> convertFromItem(ItemStack stack) {
        if (stack == null) return null;
        for (IAEStackType<?> type : AEStackTypeRegistry.getSortedTypes()) {
            if (type == ITEM_STACK_TYPE) continue;
            IAEStack<?> converted = type.convertStackFromItem(stack);
            if (converted != null) return converted;
        }
        return null;
    }

    public static void writeFilters(ByteBuf buf, Reference2BooleanMap<IAEStackType<?>> filters) {
        int count = 0;
        for (IAEStackType<?> type : filters.keySet()) if (type != ITEM_STACK_TYPE) count++;
        buf.writeByte(Math.min(count, MAX_FILTERS));
        int written = 0;
        for (Reference2BooleanMap.Entry<IAEStackType<?>> entry : filters.reference2BooleanEntrySet()) {
            if (entry.getKey() == ITEM_STACK_TYPE || written++ >= MAX_FILTERS) continue;
            buf.writeByte(AEStackTypeRegistry.getNetworkId(entry.getKey()));
            buf.writeBoolean(entry.getBooleanValue());
        }
    }

    public static Reference2BooleanMap<IAEStackType<?>> readFilters(ByteBuf buf) {
        int count = buf.readUnsignedByte();
        if (count > MAX_FILTERS) throw new IllegalArgumentException("Mini terminal type filters");
        Reference2BooleanMap<IAEStackType<?>> filters = new Reference2BooleanOpenHashMap<>();
        for (int index = 0; index < count; index++) {
            IAEStackType<?> type = AEStackTypeRegistry.getTypeFromNetworkId(buf.readByte());
            int value = buf.readUnsignedByte();
            if (type == null || type == ITEM_STACK_TYPE || value > 1)
                throw new IllegalArgumentException("Mini terminal type filter");
            filters.put(type, value == 1);
        }
        return filters;
    }
}
