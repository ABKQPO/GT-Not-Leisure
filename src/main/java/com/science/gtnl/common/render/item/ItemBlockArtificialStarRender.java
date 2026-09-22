package com.science.gtnl.common.render.item;

import static tectech.rendering.EOH.EOHRenderingUtils.renderEOHStar;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.joml.Matrix4f;

import com.science.gtnl.loader.BlockLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemBlockArtificialStarRender implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item.getItem() != Item.getItemFromBlock(BlockLoader.artificialStarRender)) {
            return false;
        }
        return switch (type) {
            case ENTITY, EQUIPPED, EQUIPPED_FIRST_PERSON, INVENTORY -> true;
            default -> false;
        };
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (item.getItem() != Item.getItemFromBlock(BlockLoader.artificialStarRender)) return;
        Matrix4f modelMatrix = new Matrix4f().translation(0.5F, 0.5F, 0.5F)
            .scale(0.25F);
        double renderTime = Minecraft.getMinecraft().theWorld == null ? 0.0D
            : Minecraft.getMinecraft().theWorld.getTotalWorldTime();
        renderEOHStar(modelMatrix, type, renderTime, 1.0D);
    }
}
