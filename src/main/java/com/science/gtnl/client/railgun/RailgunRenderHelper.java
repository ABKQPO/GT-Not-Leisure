package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.railgun.RailgunVec;

public final class RailgunRenderHelper {

    private RailgunRenderHelper() {}

    public static void render(Runnable draw) {
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glDepthMask(false);
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

    public static void ribbon(RailgunVec from, RailgunVec to, double width, float red, float green, float blue,
        float alpha) {
        EntityCamera camera = camera();
        RailgunVec side = to.subtract(from)
            .cross(
                camera.position.subtract(
                    from.add(to)
                        .scale(0.5)))
            .normalize()
            .scale(width);
        if (side.length() < 1.0E-6) side = new RailgunVec(width, 0, 0);
        Tessellator.instance.setColorRGBA_F(red, green, blue, alpha);
        vertex(from.subtract(side));
        vertex(from.add(side));
        vertex(to.add(side));
        vertex(to.subtract(side));
    }

    private static EntityCamera camera() {
        var entity = Minecraft.getMinecraft().renderViewEntity;
        return new EntityCamera(
            RailgunVec.pos(entity)
                .add(0, entity.getEyeHeight() - entity.yOffset, 0));
    }

    private record EntityCamera(RailgunVec position) {}

    public static void vertex(RailgunVec vector) {
        Tessellator.instance.addVertex(vector.x(), vector.y(), vector.z());
    }
}
