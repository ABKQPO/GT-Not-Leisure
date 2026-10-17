package com.science.gtnl.client.railgun;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;

import org.lwjgl.opengl.GL11;

public class RailgunGlowSprites {

    public enum Sprite {

        RADIAL(128, 3F),
        STREAK(128, 1.7F);

        private final int size;
        private final float falloff;

        Sprite(int size, float falloff) {
            this.size = size;
            this.falloff = falloff;
        }

        public int size() {
            return size;
        }

        public float falloff() {
            return falloff;
        }

        public boolean circular() {
            return this == RADIAL;
        }
    }

    private static final Map<Sprite, Integer> HANDLES = new EnumMap<>(Sprite.class);

    public static void clear() {
        for (Integer handle : HANDLES.values()) GL11.glDeleteTextures(handle);
        HANDLES.clear();
    }

    public static void bind(Sprite sprite) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, handle(sprite));
    }

    private static int handle(Sprite sprite) {
        Integer cached = HANDLES.get(sprite);
        if (cached != null) return cached;
        int handle = GL11.glGenTextures();
        TextureUtil.uploadTextureImage(handle, build(sprite));
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, handle);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        HANDLES.put(sprite, handle);
        return handle;
    }

    private static BufferedImage build(Sprite sprite) {
        int size = sprite.size();
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        float center = (size - 1) / 2F;
        for (int y = 0; y < size; y++) {
            float offsetY = sprite.circular() ? (y - center) / center : 0F;
            for (int x = 0; x < size; x++) {
                float offsetX = (x - center) / center;
                float distance = Math.min(1F, (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY));
                float alpha = (float) Math.pow(1F - distance, sprite.falloff());
                image.setRGB(x, y, Math.round(alpha * 255F) << 24 | 0xFFFFFF);
            }
        }
        return image;
    }
}
