package com.gtnllib.client.shader;

import java.util.List;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.gtnllib.GTNLLib;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ShaderEffect {

    private static final List<ShaderEffect> ALL = new ObjectArrayList<>();

    private final ResourceLocation vertex;
    private final ResourceLocation fragment;
    private GlslProgram program;
    private boolean unavailable;
    private int previousProgram;

    public ShaderEffect(ResourceLocation vertex, ResourceLocation fragment) {
        this.vertex = vertex;
        this.fragment = fragment;
        synchronized (ALL) {
            ALL.add(this);
        }
    }

    public static void reloadAll() {
        synchronized (ALL) {
            for (ShaderEffect effect : ALL) effect.reset();
        }
    }

    private void reset() {
        if (program != null) program.close();
        program = null;
        unavailable = false;
    }

    public boolean begin() {
        if (!OpenGlHelper.shadersSupported) return false;
        int previous = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        GlslProgram active = program();
        if (active == null) return false;
        previousProgram = previous;
        active.use();
        return true;
    }

    public void end() {
        GL20.glUseProgram(previousProgram);
        previousProgram = 0;
    }

    public void uniform1f(String uniform, float value) {
        if (program != null) program.uniform1f(uniform, value);
    }

    public void uniform2f(String uniform, float x, float y) {
        if (program != null) program.uniform2f(uniform, x, y);
    }

    private GlslProgram program() {
        if (unavailable) return null;
        if (program != null) return program;
        try {
            GlslProgram created = new GlslProgram(vertex, fragment);
            created.ensureCompiled();
            program = created;
            return created;
        } catch (RuntimeException | LinkageError failure) {
            unavailable = true;
            GTNLLib.LOG.error("Shader " + fragment + " is unavailable, falling back to fixed function", failure);
            return null;
        }
    }

    public static class Reloader {

        @SubscribeEvent
        public void reload(TextureStitchEvent.Post event) {
            if (event.map.getTextureType() == 0) reloadAll();
        }
    }
}
