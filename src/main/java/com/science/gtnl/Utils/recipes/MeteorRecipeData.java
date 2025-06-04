package com.science.gtnl.Utils.recipes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;

import WayofTime.alchemicalWizardry.common.summoning.meteor.Meteor;
import WayofTime.alchemicalWizardry.common.summoning.meteor.MeteorComponent;

public class MeteorRecipeData {

    public final ItemStack input;
    public final List<ItemStack> outputs = new ArrayList<>();
    public final List<Float> chances = new ArrayList<>();
    public final List<Integer> expectedAmounts = new ArrayList<>();
    public final int cost;
    public final int radius;

    public MeteorRecipeData(Meteor meteor) {
        this.input = meteor.focusItem != null ? meteor.focusItem.copy() : null;
        this.cost = meteor.cost;
        this.radius = meteor.radius;

        float fillerRatio = meteor.fillerChance / 100.0f;
        float componentRatio = 1.0f - fillerRatio;

        List<MeteorComponent> componentsCopy = copyComponents(meteor.ores);
        List<MeteorComponent> fillersCopy = meteor.fillerChance > 0 ? copyComponents(meteor.filler) : new ArrayList<>();

        float totalComponentWeight = calculateTotalWeight(componentsCopy);
        componentsCopy.sort(Comparator.comparingInt(c -> -c.getWeight()));

        processComponents(componentsCopy, componentRatio, totalComponentWeight);

        if (meteor.fillerChance > 0) {
            float totalFillerWeight = calculateTotalWeight(fillersCopy);
            fillersCopy.sort(Comparator.comparingInt(c -> -c.getWeight()));
            processComponents(fillersCopy, fillerRatio, totalFillerWeight);
        }
    }

    private List<MeteorComponent> copyComponents(List<MeteorComponent> originals) {
        List<MeteorComponent> copies = new ArrayList<>();
        for (MeteorComponent comp : originals) {
            copies.add(
                new MeteorComponent(
                    comp.getBlock()
                        .copy(),
                    comp.getWeight()));
        }
        return copies;
    }

    private float calculateTotalWeight(List<MeteorComponent> components) {
        return (float) components.stream()
            .mapToInt(MeteorComponent::getWeight)
            .sum();
    }

    private void processComponents(List<MeteorComponent> components, float ratio, float totalWeight) {
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

    private int getEstimatedAmount(float chance, int radius) {
        return (int) Math.ceil(4f / 3 * Math.PI * Math.pow(radius + 0.5, 3) * chance);
    }

    public int getTotalExpectedAmount() {
        return expectedAmounts.stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
}
