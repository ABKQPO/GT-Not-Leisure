package com.science.gtnl.common.gui.modularui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.gtnewhorizon.gtnhlib.client.renderer.shader.ShaderProgram;
import com.science.gtnl.ScienceNotLeisure;

public class StellarIrisGalaxyRenderer {

    private static ShaderProgram shaderProgram;
    private static int timeUniform = -1;
    private static int screenSizeUniform = -1;
    private static int intensityUniform = -1;
    private static boolean initializationAttempted;

    public static boolean render(int width, int height, float intensity, int ticks) {
        if (!initialize() || width <= 0 || height <= 0) {
            return false;
        }
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_TEXTURE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        shaderProgram.use();
        GL20.glUniform1f(timeUniform, ticks / 20.0F);
        GL20.glUniform2f(screenSizeUniform, width, height);
        GL20.glUniform1f(intensityUniform, intensity);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, height, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(width, height, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(width, 0.0D, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        ShaderProgram.clear();
        GL11.glPopAttrib();
        return true;
    }

    private static boolean initialize() {
        if (initializationAttempted) {
            return shaderProgram != null && shaderProgram.getProgram() != 0;
        }
        initializationAttempted = true;
        if (!OpenGlHelper.shadersSupported) {
            return false;
        }
        try {
            shaderProgram = new ShaderProgram(
                ScienceNotLeisure.MODID,
                "shaders/stellar_iris_galaxy.vert.glsl",
                "shaders/stellar_iris_galaxy.frag.glsl");
            if (shaderProgram.getProgram() == 0) {
                return false;
            }
            timeUniform = shaderProgram.getUniformLocation("u_Time");
            screenSizeUniform = shaderProgram.getUniformLocation("u_ScreenSize");
            intensityUniform = shaderProgram.getUniformLocation("u_Intensity");
            ShaderProgram.clear();
            return true;
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("Unable to initialize the Stellar Iris galaxy shader", exception);
            shaderProgram = null;
            return false;
        }
    }
}
