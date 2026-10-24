package com.science.gtnl.common.effect.effects;

import net.minecraft.entity.SharedMonsterAttributes;

import com.science.gtnl.common.effect.EffectBase;

public class ElectromagneticParalysisEffect extends EffectBase {

    private static final double SLOWDOWN = -0.85;
    private static final int MULTIPLY_TOTAL = 2;

    public ElectromagneticParalysisEffect(int id) {
        super(id, "electromagnetic_paralysis", true, 0x40CFFF, 1);
        func_111184_a(
            SharedMonsterAttributes.movementSpeed,
            "65548f18-30ab-4bfe-9c9e-abba2f08883b",
            SLOWDOWN,
            MULTIPLY_TOTAL);
    }
}
