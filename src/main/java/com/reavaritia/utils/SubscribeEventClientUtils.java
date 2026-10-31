package com.reavaritia.utils;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.reavaritia.common.items.InfinitySword;
import com.science.gtnl.config.ReAvaritiaConfig;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SubscribeEventClientUtils {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (ReAvaritiaConfig.infinity_sword.enableSpecialRender) {
            EntityPlayer player = event.entityPlayer;
            ItemStack heldItem = player.getHeldItem();

            if (heldItem != null && heldItem.getItem() instanceof InfinitySword) {
                float currentTime = (player.worldObj.getTotalWorldTime() + event.partialRenderTick) / 20.0F;

                float rotationSpeed = 1800.0F;

                float rotationAngle = (currentTime * rotationSpeed) % 360;

                GL11.glPushMatrix();
                GL11.glRotatef(rotationAngle, 0.0F, 1.0F, 0.0F);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (ReAvaritiaConfig.infinity_sword.enableSpecialRender) {
            EntityPlayer player = event.entityPlayer;
            ItemStack heldItem = player.getHeldItem();

            if (heldItem != null && heldItem.getItem() instanceof InfinitySword) {
                GL11.glPopMatrix();

                ModelBiped playerModel = event.renderer.modelBipedMain;
                playerModel.bipedHead.rotateAngleX = 0;
            }
        }
    }
}
