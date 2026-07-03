package com.science.gtnl.loader;

import com.science.gtnl.utils.enums.GTNLItemList;

import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import lombok.Getter;

public class NutrientLoader {

    public static void registry() {
        for (Nutrient nutrient : NutrientList.get()) {
            switch (FoodCategory.fromName(nutrient.name)) {
                case DAIRY, FRUIT -> {

                }
                case GRAIN -> nutrient.foodItems.add(GTNLItemList.SuspiciousStew.get(1));
                case PROTEIN -> nutrient.foodItems.add(GTNLItemList.KFCFamily.get(1));
                case VEGETABLE -> nutrient.foodItems.add(GTNLItemList.SuspiciousStew.get(1));
                default -> throw new IllegalStateException("Unexpected value: " + nutrient.name);
            }
        }
    }

    @Getter
    public enum FoodCategory {

        DAIRY("dairy"),
        FRUIT("fruit"),
        GRAIN("grain"),
        PROTEIN("protein"),
        VEGETABLE("vegetable");

        private final String name;

        FoodCategory(String name) {
            this.name = name;
        }

        public static FoodCategory fromName(String name) {
            for (FoodCategory category : values()) {
                if (category.name.equals(name)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("Unknown FoodCategory name: " + name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
