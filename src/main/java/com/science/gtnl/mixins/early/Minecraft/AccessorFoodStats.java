package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.util.FoodStats;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FoodStats.class, remap = true)
public interface AccessorFoodStats {

    @Accessor("foodLevel")
    void setFoodlevel(int foodlevel);

    @Accessor("foodSaturationLevel")
    void setFoodSaturationLevel(float foodSaturationLevel);

    @Accessor("foodExhaustionLevel")
    void setFoodExhaustionLevel(float foodExhaustionLevel);

    @Accessor("foodExhaustionLevel")
    float getFoodExhaustionLevel();

    @Accessor("foodTimer")
    void setFoodTimer(int foodTimer);
}
