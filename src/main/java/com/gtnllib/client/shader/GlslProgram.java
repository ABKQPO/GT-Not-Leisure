package com.gtnllib.client.shader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class GlslProgram implements AutoCloseable {

    private static final Pattern INCLUDE = Pattern.compile("(?m)^[\\t ]*#include[\\t ]+\"([^\"]+)\"[\\t ]*\\r?$");
    private static final int LOG_LENGTH = 8192;

    private final ResourceLocation vertexSource;
    private final ResourceLocation fragmentSource;
    private final Object2IntOpenHashMap<String> uniforms = new Object2IntOpenHashMap<>();
    private int program;

    public GlslProgram(ResourceLocation vertexSource, ResourceLocation fragmentSource) {
        this.vertexSource = vertexSource;
        this.fragmentSource = fragmentSource;
    }

    public void ensureCompiled() {
        if (program == 0) compile();
    }

    public void use() {
        ensureCompiled();
        GL20.glUseProgram(program);
    }

    public int uniform(String name) {
        if (program == 0) throw new IllegalStateException("GLSL program is not compiled yet");
        return uniforms.computeIfAbsent(name, (String key) -> GL20.glGetUniformLocation(program, key));
    }

    public void uniform1f(String name, float value) {
        GL20.glUniform1f(uniform(name), value);
    }

    public void uniform2f(String name, float x, float y) {
        GL20.glUniform2f(uniform(name), x, y);
    }

    public void bindTexture(int unit, ResourceLocation texture) {
        if (unit < 0) throw new IllegalArgumentException("Texture unit must be nonnegative");
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + unit);
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(texture);
    }

    private void compile() {
        int vertex = 0;
        int pixel = 0;
        int linked = 0;
        try {
            vertex = compileStage(GL20.GL_VERTEX_SHADER, source(vertexSource, new ObjectOpenHashSet<>()));
            pixel = compileStage(GL20.GL_FRAGMENT_SHADER, source(fragmentSource, new ObjectOpenHashSet<>()));
            linked = GL20.glCreateProgram();
            GL20.glAttachShader(linked, vertex);
            GL20.glAttachShader(linked, pixel);
            GL20.glLinkProgram(linked);
            if (GL20.glGetProgrami(linked, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
                throw new IllegalStateException(GL20.glGetProgramInfoLog(linked, LOG_LENGTH));
            program = linked;
            linked = 0;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read GLSL program " + fragmentSource, exception);
        } finally {
            if (vertex != 0) GL20.glDeleteShader(vertex);
            if (pixel != 0) GL20.glDeleteShader(pixel);
            if (linked != 0) GL20.glDeleteProgram(linked);
        }
    }

    private static int compileStage(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String message = GL20.glGetShaderInfoLog(shader, LOG_LENGTH);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException(message);
        }
        return shader;
    }

    private static String read(ResourceLocation path) throws IOException {
        try (InputStream stream = Minecraft.getMinecraft()
            .getResourceManager()
            .getResource(path)
            .getInputStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String source(ResourceLocation path, Set<ResourceLocation> active) throws IOException {
        if (!active.add(path)) throw new IOException("Cyclic shader include: " + path);
        try {
            Matcher matcher = INCLUDE.matcher(read(path));
            StringBuffer result = new StringBuffer();
            String directory = path.getResourcePath()
                .substring(
                    0,
                    path.getResourcePath()
                        .lastIndexOf('/') + 1);
            while (matcher.find()) {
                String include = matcher.group(1);
                ResourceLocation child = include.indexOf(':') >= 0 ? new ResourceLocation(include)
                    : new ResourceLocation(path.getResourceDomain(), directory + include);
                matcher.appendReplacement(result, Matcher.quoteReplacement(source(child, active)));
            }
            matcher.appendTail(result);
            return result.toString();
        } finally {
            active.remove(path);
        }
    }

    @Override
    public void close() {
        if (program != 0) GL20.glDeleteProgram(program);
        program = 0;
        uniforms.clear();
    }
}
