package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.railgun.RailgunVec;

public class RailgunRenderHelper {

    private RailgunRenderHelper() {}

    public static void render(Runnable draw) {
        batch(null, draw);
    }

    public static void renderGlow(RailgunGlowSprites.Sprite sprite, Runnable draw) {
        batch(sprite, draw);
    }

    public static void vertex(RailgunVec vector) {
        Tessellator.instance.addVertex(vector.x(), vector.y(), vector.z());
    }

    public static void ribbon(RailgunVec from, RailgunVec to, double width, float red, float green, float blue,
        float alpha) {
        RailgunVec side = facingSide(from, to, width);
        Tessellator.instance.setColorRGBA_F(red, green, blue, alpha);
        vertex(from.subtract(side));
        vertex(from.add(side));
        vertex(to.add(side));
        vertex(to.subtract(side));
    }

    public static void glow(RailgunVec center, double radius, float radians) {
        RailgunVec direction = camera().subtract(center)
            .normalize();
        RailgunVec reference = Math.abs(direction.y()) > 0.95 ? new RailgunVec(1, 0, 0) : new RailgunVec(0, 1, 0);
        RailgunVec right = direction.cross(reference)
            .normalize();
        RailgunVec up = direction.cross(right)
            .normalize();
        float cosine = (float) Math.cos(radians), sine = (float) Math.sin(radians);
        RailgunVec sideways = right.scale(cosine)
            .add(up.scale(sine))
            .scale(radius);
        RailgunVec upward = up.scale(cosine)
            .subtract(right.scale(sine))
            .scale(radius);
        uvVertex(
            center.subtract(sideways)
                .subtract(upward),
            0,
            0);
        uvVertex(
            center.add(sideways)
                .subtract(upward),
            1,
            0);
        uvVertex(
            center.add(sideways)
                .add(upward),
            1,
            1);
        uvVertex(
            center.subtract(sideways)
                .add(upward),
            0,
            1);
    }

    public static void glow(RailgunVec center, double radius) {
        glow(center, radius, 0F);
    }

    public static void glowRibbon(RailgunVec from, RailgunVec to, double fromWidth, double toWidth) {
        uvRibbon(from, to, fromWidth, toWidth, 0, 1);
    }

    public static void uvRibbon(RailgunVec from, RailgunVec to, double fromWidth, double toWidth, double startV,
        double endV) {
        RailgunVec axis = to.subtract(from)
            .cross(
                camera().subtract(
                    from.add(to)
                        .scale(0.5)))
            .normalize();
        if (axis.length() < 1.0E-6) return;
        RailgunVec fromSide = axis.scale(fromWidth), toSide = axis.scale(toWidth);
        uvVertex(from.subtract(fromSide), 0, startV);
        uvVertex(from.add(fromSide), 1, startV);
        uvVertex(to.add(toSide), 1, endV);
        uvVertex(to.subtract(toSide), 0, endV);
    }

    public static void uvPlane(RailgunVec center, double half) {
        uvVertex(center.add(-half, 0, -half), 0, 0);
        uvVertex(center.add(half, 0, -half), 1, 0);
        uvVertex(center.add(half, 0, half), 1, 1);
        uvVertex(center.add(-half, 0, half), 0, 1);
    }

    private static void uvVertex(RailgunVec vector, double u, double v) {
        Tessellator.instance.addVertexWithUV(vector.x(), vector.y(), vector.z(), u, v);
    }

    private static RailgunVec facingSide(RailgunVec from, RailgunVec to, double width) {
        RailgunVec side = to.subtract(from)
            .cross(
                camera().subtract(
                    from.add(to)
                        .scale(0.5)))
            .normalize()
            .scale(width);
        return side.length() < 1.0E-6 ? new RailgunVec(width, 0, 0) : side;
    }

    private static RailgunVec camera() {
        var entity = Minecraft.getMinecraft().renderViewEntity;
        return RailgunVec.pos(entity)
            .add(0, entity.getEyeHeight() - entity.yOffset, 0);
    }

    private static void batch(RailgunGlowSprites.Sprite sprite, Runnable draw) {
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
            if (sprite == null) GL11.glDisable(GL11.GL_TEXTURE_2D);
            else {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                RailgunGlowSprites.bind(sprite);
            }
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glDepthMask(false);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            Tessellator.instance.startDrawingQuads();
            draw.run();
            Tessellator.instance.draw();
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
