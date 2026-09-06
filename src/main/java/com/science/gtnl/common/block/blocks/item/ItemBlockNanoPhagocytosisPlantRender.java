package com.science.gtnl.common.block.blocks.item;

import static tectech.rendering.EOH.EOHRenderingUtils.renderGORGEStar;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import gregtech.GTMod;
import tectech.rendering.EOH.EOHRenderingUtils;

public class ItemBlockNanoPhagocytosisPlantRender extends ItemBlock implements IItemRenderer {

    public ItemBlockNanoPhagocytosisPlantRender(Block block) {
        super(block);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        enableOpaqueColorInversion();
        renderGORGEStar(
            EOHRenderingUtils.IDENTITY,
            type,
            GTMod.clientProxy()
                .getAnimationRenderTicks(),
            0.82);
        disableOpaqueColorInversion();

        enablePseudoTransparentColorInversion();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void enablePseudoTransparentColorInversion() {
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_INVERTED);
    }

    private static void enableOpaqueColorInversion() {
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_COPY_INVERTED);
    }

    private static void disableOpaqueColorInversion() {
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
    }
}
