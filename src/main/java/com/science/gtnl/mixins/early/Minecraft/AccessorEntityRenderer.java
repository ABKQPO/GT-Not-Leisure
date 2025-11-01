package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface AccessorEntityRenderer {

    // Instance fields
    @Accessor("theShaderGroup")
    ShaderGroup getTheShaderGroup();

    @Accessor("theShaderGroup")
    void setTheShaderGroup(ShaderGroup shaderGroup);

    @Accessor("shaderIndex")
    int getShaderIndex();

    @Accessor("shaderIndex")
    void setShaderIndex(int value);

    @Accessor("resourceManager")
    IResourceManager getResourceManager();

    @Accessor("mc")
    Minecraft getMinecraft();

    @Accessor("mc")
    void setMinecraft(Minecraft value);

    // Static final fields (must be public or public in target class, or Mixin must be able to force access)
    @Accessor("shaderCount")
    static int getShaderCount() {
        throw new UnsupportedOperationException();
    }

    @Accessor("logger")
    static Logger getLogger() {
        throw new UnsupportedOperationException();
    }

    @Accessor("shaderResourceLocations")
    static ResourceLocation[] getShaderResourceLocations() {
        throw new UnsupportedOperationException();
    }
}
