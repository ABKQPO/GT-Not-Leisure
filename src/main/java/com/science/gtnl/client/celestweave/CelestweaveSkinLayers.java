package com.science.gtnl.client.celestweave;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;

import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveSkinLayers {

    private static final List<ModelRenderer> HIDDEN = new ArrayList<>();

    private CelestweaveSkinLayers() {}

    public static void hide(ModelBiped model, EntityLivingBase entity) {
        restore();
        if (CelestweaveData.piece(entity, CelestweaveArmor.HEAD) != null) {
            hidePart(model.bipedHeadwear);
            hideChildren(model.bipedHead);
        }
        if (CelestweaveData.piece(entity, CelestweaveArmor.CHEST) != null) {
            hideChildren(model.bipedBody);
            hideChildren(model.bipedRightArm);
            hideChildren(model.bipedLeftArm);
        }
        if (CelestweaveData.piece(entity, CelestweaveArmor.LEGS) != null
            || CelestweaveData.piece(entity, CelestweaveArmor.BOOTS) != null) {
            hideChildren(model.bipedRightLeg);
            hideChildren(model.bipedLeftLeg);
        }
    }

    public static void hideArm(ModelRenderer arm, EntityLivingBase entity) {
        restore();
        if (CelestweaveData.piece(entity, CelestweaveArmor.CHEST) != null) hideChildren(arm);
    }

    public static void restore() {
        for (ModelRenderer layer : HIDDEN) layer.showModel = true;
        HIDDEN.clear();
    }

    private static void hideChildren(ModelRenderer part) {
        if (part == null || part.childModels == null) return;
        for (Object child : part.childModels) if (child instanceof ModelRenderer layer) hidePart(layer);
    }

    private static void hidePart(ModelRenderer part) {
        if (part == null || !part.showModel) return;
        part.showModel = false;
        HIDDEN.add(part);
    }
}
