package com.science.gtnl.common.render.tile;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.KuangBiaoOneGiantNuclearFusionReactor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KuangBiaoOneGiantNuclearFusionReactorRenderer {

    public static void renderTileEntityAt(KuangBiaoOneGiantNuclearFusionReactor machine, double x, double y, double z,
        float partialTicks) {
        ForgeDirection back = machine.getExtendedFacing()
            .getRelativeBackInWorld();
        ForgeDirection up = machine.getExtendedFacing()
            .getRelativeUpInWorld();
        Rotation rot = machine.getExtendedFacing()
            .getRotation();

        int xOffset = 19 * back.offsetX + 6 * up.offsetX;
        int yOffset = 19 * back.offsetY + 6 * up.offsetY;
        int zOffset = 19 * back.offsetZ + 6 * up.offsetZ;

        GL11.glPushMatrix();

        // 平移到方块中心 + 偏移位置
        GL11.glTranslated(x + 0.5 + xOffset, y + 0.5 + yOffset, z + 0.5 + zOffset);

        // 根据方块朝向旋转坐标系
        applyFacingRotation(back, rot);

        // 动态旋转（环绕自身法线转动）
        float rotationTick = machine.prevRotation
            + MathHelper.wrapAngleTo180_float(machine.rotation - machine.prevRotation) * partialTicks;
        GL11.glRotatef(rotationTick, -0.05f, 1f, 0f);
        renderRing(x, y, z, partialTicks, 7.8f, 1.2f, 128, 32);

        GL11.glRotatef(2 * rotationTick, 0f, -1f, 0f);
        renderRing(x, y, z, partialTicks, 7.8f, 1f, 128, 32);

        renderLightBeams(8, 9.5f, 2f, 0.3f, rotationTick);
        renderLightBeams(6, 6.5f, 1.5f, 0.1f, -rotationTick);

        GL11.glPopMatrix();
    }

    /**
     * 将模型的本地 +Z 方向对齐到方块的背面(back)，
     * 然后再根据 Rotation 调整“上方向”。
     */
    public static void applyFacingRotation(ForgeDirection back, Rotation rot) {
        // Step 1: 对齐 back -> 世界方向
        switch (back) {
            case SOUTH:
                break; // +Z -> +Z
            case NORTH:
                GL11.glRotatef(180f, 0f, 1f, 0f);
                break; // +Z -> -Z
            case EAST:
                GL11.glRotatef(90f, 0f, 1f, 0f);
                break; // +Z -> +X
            case WEST:
                GL11.glRotatef(-90f, 0f, 1f, 0f);
                break; // +Z -> -X
            case UP:
                GL11.glRotatef(-90f, 1f, 0f, 0f);
                break; // +Z -> +Y
            case DOWN:
                GL11.glRotatef(90f, 1f, 0f, 0f);
                break; // +Z -> -Y
            default:
                break;
        }

        // Step 2: 再绕本地 Z 轴应用旋转（用于对齐 up 方向）
        switch (rot) {
            case NORMAL:
                break;
            case CLOCKWISE:
                GL11.glRotatef(90f, 0f, 0f, 1f);
                break;
            case COUNTER_CLOCKWISE:
                GL11.glRotatef(-90f, 0f, 0f, 1f);
                break;
            case UPSIDE_DOWN:
                GL11.glRotatef(180f, 0f, 0f, 1f);
                break;
        }
    }

    /**
     * 在环内部渲染几个短而细的白色光柱
     */
    public static void renderLightBeams(int beamCount, float innerRadius, float beamHeight, float beamThickness,
        float rotationTick) {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(1f, 1f, 1f, 0.8f);

        for (int i = 0; i < beamCount; i++) {
            double angle = 2 * Math.PI * i / beamCount + Math.toRadians(rotationTick * 10);
            double x = innerRadius * Math.cos(angle);
            double z = innerRadius * Math.sin(angle);

            GL11.glPushMatrix();
            GL11.glTranslated(x, 0, z);
            GL11.glScaled(beamThickness, beamHeight, beamThickness);
            drawUnitCube();
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }

    /**
     * 绘制中心在原点，边长为1的单位立方体
     */
    public static void drawUnitCube() {
        GL11.glBegin(GL11.GL_QUADS);

        // 上表面 y = 0.5
        GL11.glVertex3d(-0.5, 0.5, -0.5);
        GL11.glVertex3d(0.5, 0.5, -0.5);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(-0.5, 0.5, 0.5);

        // 下表面 y = -0.5
        GL11.glVertex3d(-0.5, -0.5, -0.5);
        GL11.glVertex3d(0.5, -0.5, -0.5);
        GL11.glVertex3d(0.5, -0.5, 0.5);
        GL11.glVertex3d(-0.5, -0.5, 0.5);

        // 前表面 z = 0.5
        GL11.glVertex3d(-0.5, -0.5, 0.5);
        GL11.glVertex3d(0.5, -0.5, 0.5);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(-0.5, 0.5, 0.5);

        // 后表面 z = -0.5
        GL11.glVertex3d(-0.5, -0.5, -0.5);
        GL11.glVertex3d(0.5, -0.5, -0.5);
        GL11.glVertex3d(0.5, 0.5, -0.5);
        GL11.glVertex3d(-0.5, 0.5, -0.5);

        // 左表面 x = -0.5
        GL11.glVertex3d(-0.5, -0.5, -0.5);
        GL11.glVertex3d(-0.5, -0.5, 0.5);
        GL11.glVertex3d(-0.5, 0.5, 0.5);
        GL11.glVertex3d(-0.5, 0.5, -0.5);

        // 右表面 x = 0.5
        GL11.glVertex3d(0.5, -0.5, -0.5);
        GL11.glVertex3d(0.5, -0.5, 0.5);
        GL11.glVertex3d(0.5, 0.5, 0.5);
        GL11.glVertex3d(0.5, 0.5, -0.5);

        GL11.glEnd();
    }

    public static void renderRing(double x, double y, double z, float partialTicks, float radius, float tubeRadius,
        int segments, int sides) {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        double interpolatedRotation = 0;
        GL11.glRotated(interpolatedRotation, 1, 0, 0);
        GL11.glRotated(interpolatedRotation, 0, 1, 0);
        GL11.glRotated(interpolatedRotation, 0, 0, 1);

        for (int i = 0; i < segments; i++) {
            double theta1 = 2 * Math.PI * i / segments;
            double theta2 = 2 * Math.PI * (i + 1) / segments;

            for (int j = 0; j < sides; j++) {
                double phi1 = 2 * Math.PI * j / sides;
                double phi2 = 2 * Math.PI * (j + 1) / sides;

                double v = radius + tubeRadius * Math.cos(phi1);
                double x00 = v * Math.cos(theta1);
                double y00 = tubeRadius * Math.sin(phi1);
                double z00 = v * Math.sin(theta1);

                double v1 = radius + tubeRadius * Math.cos(phi2);
                double x01 = v1 * Math.cos(theta1);
                double y01 = tubeRadius * Math.sin(phi2);
                double z01 = v1 * Math.sin(theta1);

                double x10 = v * Math.cos(theta2);
                double y10 = tubeRadius * Math.sin(phi1);
                double z10 = v * Math.sin(theta2);

                double x11 = v1 * Math.cos(theta2);
                double y11 = tubeRadius * Math.sin(phi2);
                double z11 = v1 * Math.sin(theta2);

                float hue = i / (float) segments;
                float rCol = (float) Math.sin(hue * Math.PI * 2) * 0.4f + 0.6f;
                float gCol = (float) Math.sin((hue + 0.33f) * Math.PI * 2) * 0.4f + 0.6f;
                float bCol = (float) Math.sin((hue + 0.66f) * Math.PI * 2) * 0.4f + 0.6f;
                GL11.glColor4f(rCol, gCol, bCol, 0.8f);

                GL11.glBegin(GL11.GL_TRIANGLES);
                GL11.glVertex3d(x00, y00, z00);
                GL11.glVertex3d(x10, y10, z10);
                GL11.glVertex3d(x11, y11, z11);

                GL11.glVertex3d(x00, y00, z00);
                GL11.glVertex3d(x11, y11, z11);
                GL11.glVertex3d(x01, y01, z01);
                GL11.glEnd();
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }
}
