package com.science.gtnl.client.render;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IBaubleRender;

@SideOnly(Side.CLIENT)
public final class PhysicsCapeRenderer {

    private static final ResourceLocation texture = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "textures/models/armor/physics_cape.png");

    private static ModelBiped model;

    private PhysicsCapeRenderer() {}

    public static void render(RenderPlayerEvent event, IBaubleRender.RenderType type) {
        if (type == IBaubleRender.RenderType.BODY) {
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            IBaubleRender.Helper.rotateIfSneaking(event.entityPlayer);
            boolean armor = event.entityPlayer.getCurrentArmor(2) != null;
            GL11.glTranslatef(0F, armor ? -0.07F : -0.01F, 0F);

            float s = 0.1F;
            GL11.glScalef(s, s, s);
            if (model == null) model = new ModelBiped();

            model.bipedBody.render(1F);
        }
    }
}
