package com.science.gtnl.common.recipe.gtnl;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.utils.recipes.DisassemblerHelper;
import com.science.gtnl.utils.recipes.ReversedRecipeRegistry;

import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class ShimmerRecipes {

    public static Object2ObjectOpenHashMap<Item, ObjectList<ConversionEntry>> conversionMap = new Object2ObjectOpenHashMap<>();

    public static void loadRecipes() {
        DisassemblerHelper.loadHardOverrideRecipes();
        DisassemblerHelper.loadAssemblerRecipesToDisassembler();
        ReversedRecipeRegistry.registerAllReversedRecipes();
    }

    public static void registerConversion(ItemStack input, ObjectList<ItemStack> outputs) {
        if (input == null || outputs == null || outputs.isEmpty()) return;

        // Filter non-null outputs
        ObjectArrayList<ItemStack> filteredOutputs = outputs.stream()
            .filter(Objects::nonNull)
            .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);

        if (filteredOutputs.isEmpty()) return;

        ConversionEntry entry = new ConversionEntry(input, filteredOutputs);

        conversionMap.computeIfAbsent(input.getItem(), k -> new ObjectArrayList<>())
            .add(entry);
    }

    @Nullable
    public static ObjectList<ItemStack> getConversionResult(ItemStack input) {
        if (input == null || input.stackSize <= 0) return null;
        ObjectList<ConversionEntry> entries = conversionMap.get(input.getItem());
        if (entries == null) return null;

        for (ConversionEntry entry : entries) {
            if (entry.matches(input, false)) {
                int ratio = input.stackSize / entry.input.stackSize;
                return entry.getScaledOutputs(ratio);
            }
        }

        return null;
    }

    public static boolean isInConversions(ItemStack input) {
        if (input == null) return false;
        ObjectList<ConversionEntry> entries = conversionMap.get(input.getItem());
        if (entries == null) return false;
        for (ConversionEntry entry : entries) {
            if (entry.matches(input)) return true;
        }
        return false;
    }

    @Desugar
    public record ConversionEntry(ItemStack input, ObjectArrayList<ItemStack> outputs) {

        public ConversionEntry(ItemStack input, ObjectArrayList<ItemStack> outputs) {
            this.input = input.copy();
            this.outputs = outputs.stream()
                .map(ItemStack::copy)
                .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);
        }

        public boolean matches(ItemStack stack) {
            return matches(stack, true);
        }

        public boolean matches(ItemStack stack, boolean ignoreNBT) {
            return stack != null && stack.stackSize >= input.stackSize
                && GTUtility.areStacksEqual(stack, input, ignoreNBT);
        }

        public ObjectArrayList<ItemStack> getScaledOutputs(int scale) {
            return outputs.stream()
                .map(out -> {
                    ItemStack scaled = out.copy();
                    scaled.stackSize = scaled.stackSize * scale;
                    return scaled;
                })
                .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);
        }
    }
}
