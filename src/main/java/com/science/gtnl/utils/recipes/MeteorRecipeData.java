package com.science.gtnl.utils.recipes;

import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;

import WayofTime.alchemicalWizardry.common.summoning.meteor.Meteor;
import WayofTime.alchemicalWizardry.common.summoning.meteor.MeteorComponent;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class MeteorRecipeData {

    public final ItemStack input;
    public final ObjectArrayList<ItemStack> outputs = new ObjectArrayList<>();
    public final FloatArrayList chances = new FloatArrayList();
    public final IntArrayList expectedAmounts = new IntArrayList();
    public final int cost;
    public final int radius;

    public MeteorRecipeData(Meteor meteor) {
        this.input = meteor.focusItem != null ? meteor.focusItem.copy() : null;
        this.cost = meteor.cost;
        this.radius = meteor.radius;

        float fillerRatio = meteor.fillerChance / 100.0f;
        float componentRatio = 1.0f - fillerRatio;

        ObjectArrayList<MeteorComponent> componentsCopy = copyComponents(meteor.ores);
        ObjectArrayList<MeteorComponent> fillersCopy = meteor.fillerChance > 0 ? copyComponents(meteor.filler)
            : new ObjectArrayList<>();

        float totalComponentWeight = calculateTotalWeight(componentsCopy);
        componentsCopy.sort(Comparator.comparingInt(c -> -c.getWeight()));

        processComponents(componentsCopy, componentRatio, totalComponentWeight);

        if (meteor.fillerChance > 0) {
            float totalFillerWeight = calculateTotalWeight(fillersCopy);
            fillersCopy.sort(Comparator.comparingInt(c -> -c.getWeight()));
            processComponents(fillersCopy, fillerRatio, totalFillerWeight);
        }
    }

    public ObjectArrayList<MeteorComponent> copyComponents(List<MeteorComponent> originals) {
        ObjectArrayList<MeteorComponent> copies = new ObjectArrayList<>();
        for (MeteorComponent comp : originals) {
            copies.add(
                new MeteorComponent(
                    comp.getBlock()
                        .copy(),
                    comp.getWeight()));
        }
        return copies;
    }

    public float calculateTotalWeight(ObjectArrayList<MeteorComponent> components) {
        return (float) components.stream()
            .mapToInt(MeteorComponent::getWeight)
            .sum();
    }

    public void processComponents(ObjectArrayList<MeteorComponent> components, float ratio, float totalWeight) {
        for (MeteorComponent component : components) {
            float chance = component.getWeight() / totalWeight * ratio;
            ItemStack outputStack = component.getBlock()
                .copy();
            outputStack.stackSize = getEstimatedAmount(chance, this.radius);
            outputs.add(outputStack);
            chances.add(chance);
            expectedAmounts.add(outputStack.stackSize);
        }
    }

    public int getEstimatedAmount(float chance, int radius) {
        return (int) Math.ceil(4f / 3 * Math.PI * Math.pow(radius + 0.5, 3) * chance);
    }

    public int getTotalExpectedAmount() {
        return expectedAmounts.intStream()
            .sum();
    }
}
