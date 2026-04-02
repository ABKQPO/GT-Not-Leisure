package com.science.gtnl.common.effect;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EffectBase extends Potion {

    private static final ResourceLocation resource = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "textures/gui/potions.png");

    public EffectBase(int id, String name, boolean badEffect, int color, int iconIndex) {
        super(id, badEffect, color);
        setPotionName("gtnl.potion." + name);
        int x = ((iconIndex - 1) % 13 + 1);
        int y = ((iconIndex - 1) / 13) * 144;
        setIconIndex(x, y);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);

        return super.getStatusIconIndex();
    }

    public boolean hasEffect(EntityLivingBase entity) {
        return hasEffect(entity, this);
    }

    public boolean hasEffect(EntityLivingBase entity, Potion potion) {
        return entity.getActivePotionEffect(potion) != null;
    }

}
