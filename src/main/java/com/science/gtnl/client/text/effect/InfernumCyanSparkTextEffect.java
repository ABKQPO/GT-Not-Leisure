package com.science.gtnl.client.text.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.text.TextRenderContext;

public class InfernumCyanSparkTextEffect extends ShaderTextEffect {

    private static final int MAX_SHADER_PARTICLES = 32;
    private static final int SPAWN_CHANCE = 6;
    private static final double TICK_SECONDS = 1.0 / 60.0;
    private static final float PARTICLE_PADDING = 32.0f;
    private static final float HALF_PI = (float) (Math.PI * 0.5);
    private static final ResourceLocation BASE_GLOW = new ResourceLocation(
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        "textures/effects/infernum_base_rarity_glow.png");
    private static final ResourceLocation GLEAM = new ResourceLocation(
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        "textures/effects/infernum_gleam.png");

    private final Random random = new Random();
    private final List<CyanSparkle> sparkles = new ArrayList<>();
    private double lastSimulationSeconds = Double.NaN;
    private double simulationAccumulator;

    public InfernumCyanSparkTextEffect(ResourceLocation fragment, int... colors) {
        super(fragment, 1, true, colors);
    }

    @Override
    public float padding(float width, float height) {
        float glowHalfWidth = width * 0.115f * 6.0f;
        float glowHalfHeight = 66.0f * 0.6f * 0.5f;
        float glowCenterY = height / 3.0f;
        float glowPadding = Math.max(glowHalfHeight - glowCenterY, glowHalfHeight + glowCenterY - height);
        return Math.max(height, Math.max(PARTICLE_PADDING, Math.max(glowPadding, glowHalfWidth - width * 0.5f)));
    }

    @Override
    protected void configureUniforms(TextShader shader, TextRenderContext context) {
        GL20.glUniform1i(shader.uniform("baseRarityGlow"), 1);
        GL20.glUniform1i(shader.uniform("gleam"), 2);

        if (!context.shadow()) updateSparkles(
            context.seconds(),
            context.mask()
                .width(),
            context.mask()
                .height());
        int count = Math.min(MAX_SHADER_PARTICLES, sparkles.size());
        GL20.glUniform1i(shader.uniform("particleCount"), count);
        for (int i = 0; i < count; i++) {
            CyanSparkle sparkle = sparkles.get(i);
            GL20.glUniform2f(shader.uniform("particlePosition[" + i + "]"), sparkle.x, sparkle.y);
            GL20.glUniform1f(shader.uniform("particleScale[" + i + "]"), sparkle.scale);
            GL20.glUniform1f(shader.uniform("particleRotation[" + i + "]"), sparkle.rotation);
            GL20.glUniform1f(shader.uniform("particleColorAmount[" + i + "]"), sparkle.colorAmount);
        }
    }

    @Override
    protected void configureTextures(TextShader shader, TextRenderContext context) {
        shader.bindTexture(1, BASE_GLOW);
        prepareTexture();
        shader.bindTexture(2, GLEAM);
        prepareTexture();
    }

    private static void prepareTexture() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    }

    @Override
    public void close() {
        sparkles.clear();
        lastSimulationSeconds = Double.NaN;
        simulationAccumulator = 0;
        super.close();
    }

    private void updateSparkles(double seconds, float textWidth, float textHeight) {
        if (!Double.isFinite(lastSimulationSeconds)) {
            lastSimulationSeconds = seconds;
            return;
        }
        double elapsed = seconds - lastSimulationSeconds;
        lastSimulationSeconds = seconds;
        if (elapsed <= 0) return;
        if (elapsed > 0.25) {
            simulationAccumulator = 0;
            return;
        }
        simulationAccumulator += elapsed;
        while (simulationAccumulator >= TICK_SECONDS) {
            updateSparkleTick(textWidth, textHeight);
            simulationAccumulator -= TICK_SECONDS;
        }
    }

    private void updateSparkleTick(float textWidth, float textHeight) {
        if (random.nextInt(SPAWN_CHANCE) == 0) {
            for (int i = 0; i < 2; i++) sparkles.add(new CyanSparkle(textWidth, textHeight, random));
        }

        for (CyanSparkle sparkle : sparkles) sparkle.update();
        sparkles.removeIf(sparkle -> sparkle.time >= sparkle.lifetime);
    }

    private static final class CyanSparkle {

        private final int lifetime;
        private final float maxScale;
        private final float velocityX;
        private final float velocityY;
        private final float rotation;
        private final float colorAmount;
        private int time;
        private float x;
        private float y;
        private float scale;

        private CyanSparkle(float textWidth, float textHeight, Random random) {
            lifetime = 32 + random.nextInt(13);
            maxScale = (0.8f + random.nextFloat() * 0.3f) * 0.4f;
            x = -(int) (textWidth * 0.5f) + random.nextFloat() * (int) textWidth;
            y = -(int) (textHeight * 0.3f) + random.nextFloat() * (int) (textHeight * 0.35f);

            float length = (float) Math.sqrt(x * x + y * y);
            float directionX = length == 0 ? 0 : x / length;
            float directionY = length == 0 ? 1 : y / length;
            float angle = (random.nextFloat() - 0.5f) * 0.3f;
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            float rotatedX = directionX * cos - directionY * sin;
            float rotatedY = directionX * sin + directionY * cos;
            float speed = 0.1f + random.nextFloat() * 0.2f;
            velocityX = rotatedX * speed;
            velocityY = rotatedY * speed - 0.1f;
            rotation = (float) Math.atan2(velocityY, velocityX) + HALF_PI;

            colorAmount = ultrasmoothStep(random.nextFloat());
        }

        private void update() {
            x += velocityX;
            y += velocityY;
            time++;
            if (time <= 20) scale = maxScale * time / 20f;
            if (lifetime - time <= 20) scale = maxScale * (lifetime - time) / 20f;
        }

        private static float ultrasmoothStep(float value) {
            float first = smoothStep(value);
            return smoothStep(first);
        }

        private static float smoothStep(float value) {
            value = Math.max(0, Math.min(1, value));
            return value * value * (3 - 2 * value);
        }
    }
}
