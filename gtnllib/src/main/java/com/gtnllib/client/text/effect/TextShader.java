package com.gtnllib.client.text.effect;

import net.minecraft.util.ResourceLocation;

import com.gtnllib.GTNLLib;
import com.gtnllib.client.shader.GlslProgram;

public class TextShader extends GlslProgram {

    public TextShader(ResourceLocation fragment) {
        super(new ResourceLocation(GTNLLib.MODID, "shaders/text/text.vert.glsl"), fragment);
    }
}
