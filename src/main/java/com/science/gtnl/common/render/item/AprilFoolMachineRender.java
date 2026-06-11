package com.science.gtnl.common.render.item;

import static com.science.gtnl.utils.enums.GTNLMachineID.METEOR_MINER;
import static tectech.rendering.EOH.EOHRenderingUtils.renderGORGEStar;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GTMod;
import gregtech.api.GregTechAPI;

@SideOnly(Side.CLIENT)
public class AprilFoolMachineRender implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item.getItem() != Item.getItemFromBlock(GregTechAPI.sBlockMachines)) return false;
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
        if (item.getItem() != Item.getItemFromBlock(GregTechAPI.sBlockMachines)) return;
        if (item.getItemDamage() != METEOR_MINER.ID) return;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        renderGORGEStar(
            type,
            GTMod.clientProxy()
                .getAnimationRenderTicks(),
            0.74);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
