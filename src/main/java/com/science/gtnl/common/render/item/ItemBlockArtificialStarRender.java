package com.science.gtnl.common.render.item;

import static tectech.rendering.EOH.EOHRenderingUtils.renderEOHStar;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import com.science.gtnl.loader.BlockLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GTMod;
import tectech.rendering.EOH.EOHRenderingUtils;

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
        renderEOHStar(
            EOHRenderingUtils.IDENTITY,
            type,
            GTMod.clientProxy()
                .getAnimationRenderTicks(),
            0.82D);
    }
}
