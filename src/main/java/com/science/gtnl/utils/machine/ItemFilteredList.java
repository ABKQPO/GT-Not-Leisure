package com.science.gtnl.utils.machine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.AELog;
import appeng.util.prioitylist.IPartitionList;

public class ItemFilteredList implements IPartitionList<IAEItemStack> {

    public final Predicate<IAEItemStack> filterPredicate;

    public ItemFilteredList(String filter) {
        this.filterPredicate = makeFilter(filter.trim());
    }

    @Override
    public boolean isListed(IAEItemStack input) {
        return filterPredicate != null && filterPredicate.test(input);
    }

    @Override
    public boolean isEmpty() {
        return this.filterPredicate == null;
    }

    @Override
    public Iterable<IAEItemStack> getItems() {
        return null;
    }

    public static Predicate<IAEItemStack> makeFilter(String filter) {
        try {
            Predicate<ItemStack> matcher = makeMatcher(filter);
            if (matcher == null) return null;
            return new ItemListMatcher(matcher);
        } catch (Exception e) {
            AELog.debug(e);
            return null;
        }
    }

    public static Predicate<ItemStack> makeMatcher(String filter) {
        if (filter.isEmpty()) return null;

        // modid:itemname@meta
        String modid = null;
        String name = null;
        Integer meta = null;

        try {
            // 解析 meta
            String[] metaSplit = filter.split("@", 2);
            String beforeMeta = metaSplit[0];
            if (metaSplit.length > 1) {
                try {
                    meta = Integer.parseInt(metaSplit[1]);
                } catch (NumberFormatException ignored) {
                    meta = null;
                }
            }

            // 解析 modid 和 registry name
            String[] parts = beforeMeta.split(":", 2);
            if (parts.length == 2) {
                modid = parts[0].trim();
                name = parts[1].trim();
            } else {
                name = beforeMeta.trim();
            }

            final String fModid = modid;
            final String fName = name;
            final Integer fMeta = meta;

            return (stack) -> {
                if (ItemUtil.isStackInvalid(stack)) return false;

                String rl = Item.itemRegistry.getNameForObject(stack.getItem());
                if (rl == null || rl.isEmpty()) return false;

                String modIDPart;
                String namePart;
                int colonIndex = rl.indexOf(':');
                if (colonIndex >= 0) {
                    modIDPart = rl.substring(0, colonIndex);
                    namePart = rl.substring(colonIndex + 1);
                } else {
                    modIDPart = "minecraft";
                    namePart = rl;
                }

                if (fModid != null && !fModid.isEmpty() && !matchesRegexOrWildcard(modIDPart, fModid)) return false;
                if (fName != null && !fName.isEmpty() && !matchesRegexOrWildcard(namePart, fName)) return false;
                return fMeta == null || stack.getItemDamage() == fMeta;
            };

        } catch (Exception e) {
            AELog.debug("Failed to parse item filter: " + filter, e);
            return null;
        }
    }

    public static boolean matchesRegexOrWildcard(String value, String pattern) {
        if (pattern.equals("*")) return true;

        boolean isRegex = pattern.matches(".*[\\^\\$\\.\\*\\+\\?\\{\\}\\[\\]\\|\\(\\)].*");

        if (isRegex) {
            Pattern p = Pattern.compile(pattern);
            return p.matcher(value)
                .matches();
        } else if (pattern.contains("*")) {
            String regex = pattern.replace("*", ".*");
            return Pattern.matches(regex, value);
        } else {
            return value.equalsIgnoreCase(pattern);
        }
    }

    public static class ItemListMatcher implements Predicate<IAEItemStack> {

        final Map<ItemRef, Boolean> cache = new ConcurrentHashMap<>();
        final Predicate<ItemStack> matcher;

        public ItemListMatcher(Predicate<ItemStack> matcher) {
            this.matcher = matcher;
        }

        @Override
        public boolean test(IAEItemStack t) {
            if (t == null) return false;
            return cache.compute(new ItemRef(t), (k, v) -> (v != null) ? v : matcher.test(t.getItemStack()));
        }
    }

    public static class ItemRef {

        public final Item item;
        public final int meta;
        public final int hash;

        ItemRef(IAEItemStack stack) {
            this.item = stack.getItem();
            this.meta = stack.getItemDamage();
            this.hash = item.hashCode() ^ meta;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof ItemRef other)) return false;
            return other.item == this.item && other.meta == this.meta;
        }
    }
}
