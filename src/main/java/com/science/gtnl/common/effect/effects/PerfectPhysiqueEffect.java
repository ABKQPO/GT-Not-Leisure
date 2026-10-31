package com.science.gtnl.common.effect.effects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.science.gtnl.common.effect.EffectBase;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.utils.ColorUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(phase = Phase.PRE)
public class PerfectPhysiqueEffect extends EffectBase {

    public PerfectPhysiqueEffect(int id) {
        super(id, "perfect_physique", false, ColorUtils.rgb(ColorUtils.effectPerfectPhysique), 2);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer player) {
            PotionEffect effect = player.getActivePotionEffect(EffectLoader.perfect_physique);

            if (effect != null) {
                if (player.getHealth() > 0.0F && player.getHealth() < 15.0F) {
                    player.setHealth(15.0F);
                }
                player.getFoodStats()
                    .addStats(20, 20.0F);
            }
        }
    }
}
