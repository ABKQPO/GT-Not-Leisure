package com.science.gtnl.client.text.effect;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL20;

import com.science.gtnl.client.text.TextRenderContext;

/** Keeps rare source-tick flashes independent of GPU float hashes and the number of draw calls. */
public class BurnishedAuricTextEffect extends ShaderTextEffect {

    private final TickFlashTimeline flashes = new TickFlashTimeline(0.005, 13, 216000, 0x4155524943L);

    public BurnishedAuricTextEffect(ResourceLocation fragment, int... colors) {
        super(fragment, 1, true, colors);
    }

    @Override
    protected void configureUniforms(TextShader shader, TextRenderContext context) {
        double seconds = context.seconds() * context.style()
            .speed() % 3600;
        GL20.glUniform1i(shader.uniform("flashActive"), flashes.active((int) (seconds * 60)) ? 1 : 0);
    }
}
