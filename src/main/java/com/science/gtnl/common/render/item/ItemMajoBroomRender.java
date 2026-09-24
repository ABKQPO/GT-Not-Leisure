package com.science.gtnl.common.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.render.entity.MajoBroomRender;
import com.science.gtnl.common.render.model.MajoBroomModel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemMajoBroomRender implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        switch (type) {
            case INVENTORY -> {
                GL11.glTranslatef(0.5F, 0.55F, 0);
                GL11.glRotatef(35, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glScalef(0.42F, 0.42F, 0.42F);
            }
            case EQUIPPED_FIRST_PERSON -> {
                GL11.glTranslatef(0.5F, 0.0F, 0.3F);
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glScalef(0.45F, 0.45F, 0.45F);
            }
            case EQUIPPED -> {
                GL11.glTranslatef(0.3F, 0.1F, 0.1F);
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glScalef(0.4F, 0.4F, 0.4F);
            }
            case ENTITY -> {
                GL11.glRotatef(35, 0, 1, 0);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
            }
            default -> {}
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(MajoBroomRender.TEXTURE);
        MajoBroomModel.INSTANCE.render();
        GL11.glPopMatrix();
    }
}
