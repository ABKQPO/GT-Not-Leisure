package com.science.gtnl.client.text;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

/** Owns the GL state changed by a temporary font target or effect pass. */
public class TextRenderState implements AutoCloseable {

    private static final Deque<FloatBuffer> MATRIX_BUFFERS = new ArrayDeque<>();
    private final FloatBuffer projection = saveMatrix(GL11.GL_PROJECTION_MATRIX);
    private final FloatBuffer modelView = saveMatrix(GL11.GL_MODELVIEW_MATRIX);
    private final boolean shaders = GLContext.getCapabilities().OpenGL20;
    private final boolean framebuffers = OpenGlHelper.isFramebufferEnabled();
    private final int program = shaders ? GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) : 0;
    private final int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
    private final int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
    private final int drawFramebuffer = framebuffers ? GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING) : 0;
    private final boolean separateTargets = GLContext.getCapabilities().OpenGL30
        || GLContext.getCapabilities().GL_ARB_framebuffer_object;
    private final int readFramebuffer = separateTargets ? GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING)
        : drawFramebuffer;
    private final float lightX = OpenGlHelper.lastBrightnessX;
    private final float lightY = OpenGlHelper.lastBrightnessY;

    public TextRenderState() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private static FloatBuffer saveMatrix(int parameter) {
        FloatBuffer buffer = MATRIX_BUFFERS.pollFirst();
        if (buffer == null) buffer = BufferUtils.createFloatBuffer(16);
        buffer.clear();
        GL11.glGetFloat(parameter, buffer);
        return buffer;
    }

    @Override
    public void close() {
        if (framebuffers && separateTargets) {
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFramebuffer);
        } else if (framebuffers) {
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, drawFramebuffer);
        }
        if (shaders) GL20.glUseProgram(program);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadMatrix(modelView);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadMatrix(projection);
        GL11.glPopAttrib();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
        OpenGlHelper.setActiveTexture(activeTexture);
        GL11.glMatrixMode(matrixMode);
        MATRIX_BUFFERS.addFirst(modelView);
        MATRIX_BUFFERS.addFirst(projection);
    }
}
