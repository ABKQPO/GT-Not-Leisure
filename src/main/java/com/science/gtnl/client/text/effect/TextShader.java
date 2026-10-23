package com.science.gtnl.client.text.effect;

import net.minecraft.util.ResourceLocation;

import com.gtnllib.client.shader.GlslProgram;
import com.science.gtnl.ScienceNotLeisure;

public class TextShader extends GlslProgram {

    public TextShader(ResourceLocation fragment) {
        super(new ResourceLocation(ScienceNotLeisure.RESOURCE_ROOT_ID, "shaders/text/text.vert.glsl"), fragment);
    }
}
