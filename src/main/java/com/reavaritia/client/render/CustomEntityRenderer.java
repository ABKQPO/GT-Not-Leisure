package com.reavaritia.client.render;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonSyntaxException;
import com.science.gtnl.mixins.early.minecraft.AccessorEntityRenderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomEntityRenderer extends EntityRenderer {

    public CustomEntityRenderer(Minecraft mc) {
        super(mc, mc.getResourceManager());
    }

    public CustomEntityRenderer(Minecraft mc, IResourceManager resourceManager) {
        super(mc, resourceManager);
    }

    public void activateDesaturateShader(int index) {
        if (OpenGlHelper.shadersSupported) {
            AccessorEntityRenderer accessor = (AccessorEntityRenderer) this;

            ShaderGroup shaderGroup = accessor.getTheShaderGroup();
            if (shaderGroup != null) {
                shaderGroup.deleteShaderGroup();
            }

            accessor.setShaderIndex(index);

            ResourceLocation[] shaderResources = AccessorEntityRenderer.getShaderResourceLocations();
            Logger logger = AccessorEntityRenderer.getLogger();

            try {
                logger.info("Selecting effect {}", shaderResources[index]);
                shaderGroup = new ShaderGroup(
                    accessor.getMinecraft()
                        .getTextureManager(),
                    accessor.getResourceManager(),
                    accessor.getMinecraft()
                        .getFramebuffer(),
                    shaderResources[index]);
                shaderGroup.createBindFramebuffers(
                    accessor.getMinecraft().displayWidth,
                    accessor.getMinecraft().displayHeight);

                accessor.setTheShaderGroup(shaderGroup);
            } catch (IOException | JsonSyntaxException e) {
                logger.warn("Failed to load shader: {}", shaderResources[index], e);
                accessor.setShaderIndex(AccessorEntityRenderer.getShaderCount());
            }
        }
    }

    public void resetShader() {
        AccessorEntityRenderer accessor = (AccessorEntityRenderer) this;

        ShaderGroup shaderGroup = accessor.getTheShaderGroup();
        if (shaderGroup != null) {
            shaderGroup.deleteShaderGroup();
        }

        accessor.setShaderIndex(AccessorEntityRenderer.getShaderCount());

        try {
            AccessorEntityRenderer.getLogger()
                .info("Resetting shader to default");
            accessor.setTheShaderGroup(null);
        } catch (Exception e) {
            AccessorEntityRenderer.getLogger()
                .warn("Failed to reset shader", e);
        }
    }
}
