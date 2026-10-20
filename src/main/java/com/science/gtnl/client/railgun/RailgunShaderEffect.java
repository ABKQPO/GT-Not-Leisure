package com.science.gtnl.client.railgun;

import net.minecraft.util.ResourceLocation;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.shader.ShaderEffect;

public class RailgunShaderEffect extends ShaderEffect {

    private static final String SHADER_ROOT = "shaders/railgun/";
    private static final ResourceLocation SHARED_VERTEX = new ResourceLocation(
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        SHADER_ROOT + "effect.vert.glsl");

    public static final RailgunShaderEffect BEAM = new RailgunShaderEffect("beam");
    public static final RailgunShaderEffect ORB = new RailgunShaderEffect("orb");
    public static final RailgunShaderEffect CHARGE = new RailgunShaderEffect("charge");

    private RailgunShaderEffect(String name) {
        super(
            SHARED_VERTEX,
            new ResourceLocation(ScienceNotLeisure.RESOURCE_ROOT_ID, SHADER_ROOT + name + ".frag.glsl"));
    }
}
