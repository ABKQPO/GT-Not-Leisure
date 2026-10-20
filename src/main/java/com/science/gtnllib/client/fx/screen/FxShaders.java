package com.science.gtnllib.client.fx.screen;

import net.minecraft.util.ResourceLocation;

import com.science.gtnllib.GTNLLib;
import com.science.gtnllib.client.shader.ShaderEffect;

public class FxShaders {

    private static final String ROOT = "shaders/fx/";
    private static final ResourceLocation VERTEX = new ResourceLocation(GTNLLib.MODID, ROOT + "shape.vert.glsl");

    static final ShaderEffect DISC = shader("disc");
    static final ShaderEffect RING = shader("ring");
    static final ShaderEffect SECTOR = shader("sector");
    static final ShaderEffect SEGMENT = shader("segment");
    static final ShaderEffect GLOW = shader("glow");
    static final ShaderEffect BLUR = shader("blur");
    static final ShaderEffect FROSTED = shader("frosted");

    private FxShaders() {}

    private static ShaderEffect shader(String name) {
        return new ShaderEffect(VERTEX, new ResourceLocation(GTNLLib.MODID, ROOT + name + ".frag.glsl"));
    }
}
