package com.science.gtnl.common.effect.effects;

import com.science.gtnl.common.effect.EffectBase;

public class ElectromagneticParalysisEffect extends EffectBase {

    public ElectromagneticParalysisEffect(int id) {
        super(id, "electromagnetic_paralysis", true, 0x40CFFF, 1);
        func_111184_a(
            net.minecraft.entity.SharedMonsterAttributes.movementSpeed,
            "65548f18-30ab-4bfe-9c9e-abba2f08883b",
            -0.85,
            2);
    }
}
