package com.science.gtnl.common.render.tile;

import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_INDICATOR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.HighPerformanceComputationArray;
import com.science.gtnl.utils.enums.HPCAModifier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HighPerformanceComputationArrayRenderer {

    public static void renderTileEntity(HighPerformanceComputationArray machine, double x, double y, double z,
        float partialTicks) {
        if (!machine.mMachine) return;

        GL11.glPushMatrix();

        float rotAngle = machine.getRotAngle();
        float rotAxisX = machine.getRotAxisX();
        float rotAxisY = machine.getRotAxisY();
        float rotAxisZ = machine.getRotAxisZ();
        float offsetX = machine.getOffsetX();
        float offsetY = machine.getOffsetY();
        float offsetZ = machine.getOffsetZ();
        int rotation = machine.getRotation()
            .getIndex();
        float angle = (rotation * 90f) % 360f;

        GL11.glTranslated(x, y, z);
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);

        if (rotAxisZ != 0) {
            GL11.glRotatef(-rotAxisZ * (rotation * 90f - 90f), 0f, 1f, 0f);
        } else if (rotAxisY != 0) {
            GL11.glRotatef(rotAxisY * rotAngle, 0f, 1f, 0f);
            if (offsetX != 0 || offsetZ != 0) {
                GL11.glRotatef(angle, 1f, 0f, 0f);
            }
        }

        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        int machineLens = machine.totalLens;
        int[][] colorTable = machine.generateTwoModifierIndexGroups(machine.randomUUID, machineLens);
        if (colorTable.length < 2) {
            GL11.glPopMatrix();
            return;
        }

        int[] widthColors = colorTable[0];
        int[] heightColors = colorTable[1];

        if (widthColors.length < 1 || heightColors.length < 1) {
            GL11.glPopMatrix();
            return;
        }

        float baseOffset = 1.0f / 4096f;

        Tessellator tessellator = Tessellator.instance;
        HPCAModifier[] modifiers = HPCAModifier.values();

        for (int h = 0; h < Math.min(heightColors.length, 3); h++) {
            int index = heightColors[h];
            if (index < 0 || index >= modifiers.length) continue;

            IIcon[] icons = new IIcon[] { OVERLAY_FRONT_INDICATOR.getIcon(), modifiers[index].overlay.getIcon() };
            for (IIcon icon : icons) {
                if (icon == null) continue;

                GL11.glPushMatrix();

                if (rotAxisY != 0) {
                    GL11.glTranslatef(1f, h * Math.abs(rotAxisY) - 1f, 0f);
                } else if (rotAxisZ != 0) {
                    GL11.glTranslatef(h * rotAxisZ - rotAxisZ, -rotAxisZ, 0f);
                }

                GL11.glTranslatef(0f, 0f, -(h + 4) * baseOffset);

                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addVertexWithUV(0, 1, 0, icon.getMinU(), icon.getMaxV());
                tessellator.addVertexWithUV(1, 1, 0, icon.getMaxU(), icon.getMaxV());
                tessellator.addVertexWithUV(1, 0, 0, icon.getMaxU(), icon.getMinV());
                tessellator.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
                tessellator.draw();

                GL11.glPopMatrix();
            }
        }

        for (int w = 0; w < widthColors.length; w++) {
            int index = widthColors[w];
            if (index < 0 || index >= modifiers.length) continue;

            IIcon[] icons = new IIcon[] { OVERLAY_FRONT_INDICATOR.getIcon(), modifiers[index].overlay.getIcon() };
            for (IIcon icon : icons) {
                if (icon == null) continue;

                GL11.glPushMatrix();

                if (rotAxisZ != 0) {
                    GL11.glTranslatef(2 * rotAxisZ, w * -rotAxisZ - 2 * rotAxisZ, 0f);
                } else if (rotAxisY != 0) {
                    GL11.glTranslatef(w * Math.abs(rotAxisY) + 2f, 2f, 0f);
                }

                GL11.glTranslatef(0f, 0f, -(w + 1 + widthColors.length) * baseOffset);

                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addVertexWithUV(0, 1, 0, icon.getMinU(), icon.getMaxV());
                tessellator.addVertexWithUV(1, 1, 0, icon.getMaxU(), icon.getMaxV());
                tessellator.addVertexWithUV(1, 0, 0, icon.getMaxU(), icon.getMinV());
                tessellator.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
                tessellator.draw();

                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
    }

}
