package com.science.gtnl.compat.ae2;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class InterfaceTextureCompat {

    public static IIconRegister withColoredFallback(IIconRegister registry, String baseTexture) {
        String coloredPrefix = baseTexture.replace(":", ":interface/") + "_";
        return name -> registry
            .registerIcon(name.startsWith(coloredPrefix) && !hasBlockTexture(name) ? baseTexture : name);
    }

    private static boolean hasBlockTexture(String name) {
        ResourceLocation icon = new ResourceLocation(name);
        ResourceLocation texture = new ResourceLocation(
            icon.getResourceDomain(),
            "textures/blocks/" + icon.getResourcePath() + ".png");
        try (var stream = Minecraft.getMinecraft()
            .getResourceManager()
            .getResource(texture)
            .getInputStream()) {
            return true;
        } catch (IOException missing) {
            return false;
        }
    }
}
