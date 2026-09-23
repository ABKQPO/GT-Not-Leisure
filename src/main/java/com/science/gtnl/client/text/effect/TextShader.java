package com.science.gtnl.client.text.effect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.science.gtnl.ScienceNotLeisure;

/** Lazy GLSL resources; all resource text is decoded explicitly as UTF-8. */
public class TextShader implements AutoCloseable {

    private static final Pattern INCLUDE = Pattern.compile("(?m)^[\\t ]*#include[\\t ]+\"([^\"]+)\"[\\t ]*\\r?$");
    private final ResourceLocation fragment;
    private final Map<String, Integer> uniforms = new HashMap<>();
    private int program;

    public TextShader(ResourceLocation fragment) {
        this.fragment = fragment;
    }

    public void use() {
        if (program == 0) compile();
        GL20.glUseProgram(program);
    }

    public int uniform(String name) {
        return uniforms.computeIfAbsent(name, key -> GL20.glGetUniformLocation(program, key));
    }

    private void compile() {
        int vertex = 0;
        int pixel = 0;
        int linked = 0;
        try {
            vertex = compileStage(
                GL20.GL_VERTEX_SHADER,
                source(
                    new ResourceLocation(ScienceNotLeisure.RESOURCE_ROOT_ID, "shaders/text/text.vert.glsl"),
                    new HashSet<>()));
            pixel = compileStage(GL20.GL_FRAGMENT_SHADER, source(fragment, new HashSet<>()));
            linked = GL20.glCreateProgram();
            GL20.glAttachShader(linked, vertex);
            GL20.glAttachShader(linked, pixel);
            GL20.glLinkProgram(linked);
            if (GL20.glGetProgrami(linked, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                throw new IllegalStateException(GL20.glGetProgramInfoLog(linked, 8192));
            }
            program = linked;
            linked = 0;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read text shader " + fragment, exception);
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
            String message = GL20.glGetShaderInfoLog(shader, 8192);
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
            while (matcher.find()) {
                String include = matcher.group(1);
                String directory = path.getResourcePath()
                    .substring(
                        0,
                        path.getResourcePath()
                            .lastIndexOf('/') + 1);
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
