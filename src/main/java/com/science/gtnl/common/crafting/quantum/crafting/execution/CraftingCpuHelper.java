/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package com.science.gtnl.common.crafting.quantum.crafting.execution;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import net.minecraft.world.World;

import com.gtnllib.utils.LongMath;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.crafting.inv.ICraftingInventory;

import appeng.api.config.Actionable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/**
 * Helper functions used by the CPU.
 */
public class CraftingCpuHelper {

    /**
     * Get all potential input templates that count as "1" ingredient according to the given inputs for a pattern slot,
     * and which are available.
     */
    public static Iterable<InputTemplate> getValidItemTemplates(ICraftingInventory inv, IPatternDetails.IInput input,
        World level) {
        return () -> new Iterator<>() {

            private final GenericStack[] possibleInputs = input.possibleInputs();
            private final Set<FuzzyInputKey> checkedInputs = new ObjectOpenHashSet<>();
            private int possibleInputIndex;
            private Iterator<AEKey> fuzzyIterator;
            private long fuzzyAmount;
            private InputTemplate next;

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }

                next = findNext();
                return next != null;
            }

            @Override
            public InputTemplate next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                var result = next;
                next = null;
                return result;
            }

            private InputTemplate findNext() {
                while (true) {
                    while (fuzzyIterator != null && fuzzyIterator.hasNext()) {
                        var fuzz = fuzzyIterator.next();
                        if (input.isValid(fuzz, level)) {
                            return new InputTemplate(fuzz, fuzzyAmount);
                        }
                    }

                    fuzzyIterator = null;
                    if (!advancePossibleInput()) {
                        return null;
                    }
                }
            }

            private boolean advancePossibleInput() {
                while (possibleInputIndex < possibleInputs.length) {
                    var stack = possibleInputs[possibleInputIndex++];
                    if (!checkedInputs.add(new FuzzyInputKey(stack.what(), stack.amount()))) {
                        continue;
                    }

                    fuzzyAmount = stack.amount();
                    fuzzyIterator = inv.findFuzzyTemplates(stack.what())
                        .iterator();
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Extract a whole number of templates, and return how many were extracted.
     */
    public static long extractTemplates(ICraftingInventory inv, InputTemplate template, long multiplier) {
        long maxTotal = LongMath.saturatedMultiply(template.amount(), multiplier);
        // Extract as much as possible.
        var extracted = inv.extract(template.key(), maxTotal, Actionable.SIMULATE);
        if (extracted == 0) return 0;
        // Adjust to have a whole number of templates.
        multiplier = extracted / template.amount();
        maxTotal = LongMath.saturatedMultiply(template.amount(), multiplier);
        if (maxTotal == 0) return 0;
        extracted = inv.extract(template.key(), maxTotal, Actionable.MODULATE);
        if (extracted == 0 || extracted != maxTotal) {
            throw new IllegalStateException("Failed to correctly extract whole number. Invalid simulation!");
        }
        return multiplier;
    }

    private record FuzzyInputKey(Object primaryKey, long amount) {

        private FuzzyInputKey(AEKey key, long amount) {
            this(key.getPrimaryKey(), amount);
        }
    }
}
