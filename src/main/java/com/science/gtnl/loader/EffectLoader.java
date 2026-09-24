package com.science.gtnl.loader;

import net.minecraft.potion.Potion;

import com.science.gtnl.common.effect.effects.AweEffect;
import com.science.gtnl.common.effect.effects.BattleEffect;
import com.science.gtnl.common.effect.effects.MissBroomBlessingEffect;
import com.science.gtnl.common.effect.effects.PerfectPhysiqueEffect;
import com.science.gtnl.common.effect.effects.PotionGhostlyShape;
import com.science.gtnl.common.effect.effects.ShimmeringEffect;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class EffectLoader {

    public static Potion awe;
    public static Potion perfect_physique;
    public static Potion shimmering;
    public static Potion ghostly_shape;
    public static Potion battle;
    public static Potion miss_broom_blessing;

    public static void registry() {
        awe = new AweEffect(findNextFreePotionId());
        perfect_physique = new PerfectPhysiqueEffect(findNextFreePotionId());
        shimmering = new ShimmeringEffect(findNextFreePotionId());
        ghostly_shape = new PotionGhostlyShape(findNextFreePotionId());
        battle = new BattleEffect(findNextFreePotionId());
        miss_broom_blessing = new MissBroomBlessingEffect(findNextFreePotionId());
    }

    public static int findNextFreePotionId() {
        for (int i = 200; i < Potion.potionTypes.length; i++) {
            if (Potion.potionTypes[i] == null) {
                return i;
            }
        }

        throw new RuntimeException("No free potion ID found.");
    }

}
