package com.science.gtnl.client.railgun.shader;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.shader.GlslProgram;

public class RailgunShaderEffect {

    public static final RailgunShaderEffect BEAM = new RailgunShaderEffect("beam");
    public static final RailgunShaderEffect ORB = new RailgunShaderEffect("orb");
    public static final RailgunShaderEffect CHARGE = new RailgunShaderEffect("charge");

    private static final String SHADER_ROOT = "shaders/railgun/";
    private static final ResourceLocation SHARED_VERTEX = new ResourceLocation(
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        SHADER_ROOT + "effect.vert.glsl");

    private final String name;
    private GlslProgram program;
    private boolean unavailable;
    private int previousProgram;

    public RailgunShaderEffect(String name) {
        this.name = name;
    }

    public boolean begin() {
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
            GlslProgram created = new GlslProgram(
                SHARED_VERTEX,
                new ResourceLocation(ScienceNotLeisure.RESOURCE_ROOT_ID, SHADER_ROOT + name + ".frag.glsl"));
            created.ensureCompiled();
            program = created;
            return created;
        } catch (RuntimeException | LinkageError failure) {
            unavailable = true;
            ScienceNotLeisure.LOG
                .error("Railgun shader " + name + " is unavailable, falling back to fixed function", failure);
            return null;
        }
    }
}
