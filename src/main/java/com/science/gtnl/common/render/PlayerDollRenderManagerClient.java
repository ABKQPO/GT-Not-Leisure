package com.science.gtnl.common.render;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerDollRenderManagerClient {

    public static final Map<String, ResourceLocation> TEXTURE_SKIN_CACHE = new ConcurrentHashMap<>();
    public static final Map<String, ResourceLocation> TEXTURE_CAPE_CACHE = new ConcurrentHashMap<>();
    public static final Map<String, BufferedImage> TEXTURE_PENDING = new ConcurrentHashMap<>();
    public static final Map<String, Boolean> SKIN_MODEL = new ConcurrentHashMap<>();
    public static final Map<String, Integer> UPLOADED_TEXTURE_ID = new ConcurrentHashMap<>();

    public static final ResourceLocation DEFAULT_SKIN = new ResourceLocation(RESOURCE_ROOT_ID + ":model/skin.png");
    public static final ResourceLocation DEFAULT_CAPE = new ResourceLocation(RESOURCE_ROOT_ID + ":model/cape.png");

    public static final IModelCustom MODEL = AdvancedModelLoader
        .loadModel(new ResourceLocation(RESOURCE_ROOT_ID + ":model/PlayerDoll.obj"));

    public static final File SKIN_DIR = new File("config/GTNotLeisure/skin");
    public static final File CAPE_DIR = new File("config/GTNotLeisure/cape");
    public static final File CUSTOM_SKIN_DIR = new File("config/GTNotLeisure/custom_skin");
    public static final File CUSTOM_CAPE_DIR = new File("config/GTNotLeisure/custom_cape");

    static {
        SKIN_DIR.mkdirs();
        CAPE_DIR.mkdirs();
        CUSTOM_SKIN_DIR.mkdirs();
        CUSTOM_CAPE_DIR.mkdirs();
        SKIN_MODEL.put("model/skin.png", false);
    }

    public static void renderModel(ResourceLocation skin, ResourceLocation cape, byte mode) {
        if (skin == null) skin = DEFAULT_SKIN;
        if (cape == null) cape = DEFAULT_CAPE;
        bindTexture(skin);
        boolean steve = getOrPutSkinModel(
            skin.getResourcePath()
                .replace("texture_", "")
                .replace("skin_", ""));

        MODEL.renderPart("Body");
        MODEL.renderPart("Body_Layer");
        MODEL.renderPart("Head");
        MODEL.renderPart("Head_Layer");
        MODEL.renderPart("Left_Leg");
        MODEL.renderPart("Left_Leg_Layer");
        MODEL.renderPart("Right_Leg");
        MODEL.renderPart("Right_Leg_Layer");

        if (steve) {
            MODEL.renderPart("Left_Arm");
            MODEL.renderPart("Left_Arm_Layer");
            MODEL.renderPart("Right_Arm");
            MODEL.renderPart("Right_Arm_Layer");
        } else {
            MODEL.renderPart("Left_Arm_Alex");
            MODEL.renderPart("Left_Arm_Alex_Layer");
            MODEL.renderPart("Right_Arm_Alex");
            MODEL.renderPart("Right_Arm_Alex_Layer");
        }

        bindTexture(cape == null ? DEFAULT_CAPE : cape);

        if (mode == 2) {
            MODEL.renderPart("Elytra_Right");
            MODEL.renderPart("Elytra_Left");
        } else if (mode == 1) {
            MODEL.renderPart("Cape");
        }
    }

    public static ResourceLocation loadProfileTexture(String uuid, TextureType type) {
        if (uuid == null || PlayerDollRenderManager.BLACKLISTED_UUIDS.contains(uuid))
            return type == TextureType.SKIN ? DEFAULT_SKIN : null;

        Map<String, ResourceLocation> cache = type == TextureType.SKIN ? TEXTURE_SKIN_CACHE : TEXTURE_CAPE_CACHE;

        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        File local = new File(type == TextureType.SKIN ? SKIN_DIR : CAPE_DIR, uuid + ".png");
        if (local.exists()) {
            ResourceLocation tex = getLocalTextureFromFile(local, type);
            if (tex != null) {
                cache.put(uuid, tex);
                return tex;
            }
        }

        String url = fetchTextureUrl(uuid, type);
        if (url == null) {
            cache.put(uuid, type == TextureType.SKIN ? DEFAULT_SKIN : DEFAULT_CAPE);
            return null;
        }

        return downloadAndCacheTexture(url, uuid, type, false);
    }

    public static ResourceLocation downloadAndCacheCustomSkin(String url) {
        return downloadAndCacheTexture(url, Integer.toHexString(url.hashCode()), TextureType.SKIN, true);
    }

    public static ResourceLocation downloadAndCacheCustomCape(String url) {
        return downloadAndCacheTexture(url, Integer.toHexString(url.hashCode()), TextureType.CAPE, true);
    }

    private static ResourceLocation downloadAndCacheTexture(String url, String key, TextureType type, boolean custom) {

        if (url == null) return type == TextureType.SKIN ? DEFAULT_SKIN : null;

        Map<String, ResourceLocation> cache = type == TextureType.SKIN ? TEXTURE_SKIN_CACHE : TEXTURE_CAPE_CACHE;

        ResourceLocation cached = cache.get(key);
        if (cached != null) return cached;

        File dir = type == TextureType.SKIN ? (custom ? CUSTOM_SKIN_DIR : SKIN_DIR)
            : (custom ? CUSTOM_CAPE_DIR : CAPE_DIR);

        File target = new File(dir, key + ".png");

        if (target.exists()) return getLocalTextureFromFile(target, type);

        return PlayerDollRenderManager.AsyncDownloader.getTexture(key, () -> {
            try (InputStream in = new URL(url).openStream(); FileOutputStream out = new FileOutputStream(target)) {

                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) out.write(buf, 0, len);

                if (!isValidImage(target)) {
                    target.delete();
                    return type == TextureType.SKIN ? DEFAULT_SKIN : null;
                }

                ResourceLocation tex = getLocalTextureFromFile(target, type);
                if (tex != null) cache.put(key, tex);
                return tex;

            } catch (IOException e) {
                target.delete();
                return type == TextureType.SKIN ? DEFAULT_SKIN : null;
            }
        });
    }

    public static String fetchTextureUrl(String uuid, TextureType type) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid).openConnection();

            conn.setConnectTimeout(3000);
            if (conn.getResponseCode() != 200) return null;

            String json = readAll(conn.getInputStream());

            JsonObject profile = new JsonParser().parse(json)
                .getAsJsonObject();

            String value = profile.getAsJsonArray("properties")
                .get(0)
                .getAsJsonObject()
                .get("value")
                .getAsString();

            JsonObject textures = new JsonParser().parse(
                new String(
                    Base64.getDecoder()
                        .decode(value)))
                .getAsJsonObject()
                .getAsJsonObject("textures");

            if (type == TextureType.SKIN && textures.has("SKIN")) return textures.getAsJsonObject("SKIN")
                .get("url")
                .getAsString();

            if (type == TextureType.CAPE && textures.has("CAPE")) return textures.getAsJsonObject("CAPE")
                .get("url")
                .getAsString();

        } catch (Exception ignored) {}

        return null;
    }

    public static String readAll(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String s;
            while ((s = br.readLine()) != null) sb.append(s);
        }
        return sb.toString();
    }

    public static ResourceLocation getLocalTextureFromFile(File file, TextureType type) {
        if (!file.exists()) return null;

        String name = file.getName()
            .replace(".png", "");
        Map<String, ResourceLocation> cache = type == TextureType.SKIN ? TEXTURE_SKIN_CACHE : TEXTURE_CAPE_CACHE;

        ResourceLocation cached = cache.get(name);
        if (cached != null) return cached;

        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) return null;

            img = processSkinFormat(img);

            String key = (type == TextureType.SKIN ? "skin_" : "cape_") + name;
            TEXTURE_PENDING.put(key, img);

            ResourceLocation rl = new ResourceLocation("custom", "texture_" + key);
            cache.put(name, rl);
            return rl;

        } catch (IOException e) {
            return null;
        }
    }

    public static void bindTexture(ResourceLocation tex) {
        if (tex == null) tex = DEFAULT_SKIN;

        if (!"custom".equals(tex.getResourceDomain())) {
            Minecraft.getMinecraft().renderEngine.bindTexture(tex);
            return;
        }

        String key = tex.getResourcePath()
            .replace("texture_", "");

        Integer id = UPLOADED_TEXTURE_ID.get(key);
        if (id == null) {
            BufferedImage img = TEXTURE_PENDING.remove(key);
            if (img == null) return;

            id = TextureUtil.uploadTextureImage(GL11.glGenTextures(), img);
            UPLOADED_TEXTURE_ID.put(key, id);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    public static BufferedImage processSkinFormat(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 如果已经是 64x64 且不需要转换，仍需处理不透明度（类似原版 ImageBufferDownload）
        boolean isLegacy = (height == 32 && width == 64);

        BufferedImage result = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = result.getGraphics();
        graphics.drawImage(image, 0, 0, null);

        if (isLegacy) {
            // 填充下半部分透明
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 32, 64, 32);

            // 镜像右侧肢体到左侧肢体 (Minecraft 1.8+ 规范)
            graphics.drawImage(result, 24, 48, 20, 52, 4, 16, 8, 20, null);
            graphics.drawImage(result, 28, 48, 24, 52, 8, 16, 12, 20, null);
            graphics.drawImage(result, 20, 52, 16, 64, 8, 20, 12, 32, null);
            graphics.drawImage(result, 24, 52, 20, 64, 4, 20, 8, 32, null);
            graphics.drawImage(result, 28, 52, 24, 64, 0, 20, 4, 32, null);
            graphics.drawImage(result, 32, 52, 28, 64, 12, 20, 16, 32, null);
            graphics.drawImage(result, 40, 48, 36, 52, 44, 16, 48, 20, null);
            graphics.drawImage(result, 44, 48, 40, 52, 48, 16, 52, 20, null);
            graphics.drawImage(result, 36, 52, 32, 64, 48, 20, 52, 32, null);
            graphics.drawImage(result, 40, 52, 36, 64, 44, 20, 48, 32, null);
            graphics.drawImage(result, 44, 52, 40, 64, 40, 20, 44, 32, null);
            graphics.drawImage(result, 48, 52, 44, 64, 52, 20, 56, 32, null);
        }
        graphics.dispose();

        // 处理像素 alpha 通道（防止部分皮肤软件导出的全黑背景问题）
        int[] imageData = ((DataBufferInt) result.getRaster()
            .getDataBuffer()).getData();
        setAreaOpaque(imageData, 64, 0, 0, 32, 16); // 头部

        if (isLegacy) {
            setAreaTransparent(imageData, 64, 32, 0, 64, 32); // 修正头盔层透明度
        }

        setAreaOpaque(imageData, 64, 0, 16, 64, 32); // 躯干和右肢
        setAreaOpaque(imageData, 64, 16, 48, 48, 64); // 左肢

        return result;
    }

    public static void setAreaTransparent(int[] data, int w, int x, int y, int x2, int y2) {
        if (!hasTransparency(data, w, x, y, x2, y2))
            for (int i = x; i < x2; i++) for (int j = y; j < y2; j++) data[i + j * w] &= 0xFFFFFF;
    }

    public static void setAreaOpaque(int[] data, int w, int x, int y, int x2, int y2) {
        for (int i = x; i < x2; i++) for (int j = y; j < y2; j++) data[i + j * w] |= 0xFF000000;
    }

    public static boolean hasTransparency(int[] data, int w, int x, int y, int x2, int y2) {
        for (int i = x; i < x2; i++) for (int j = y; j < y2; j++) if ((data[i + j * w] >>> 24) < 128) return true;
        return false;
    }

    public static boolean getOrPutSkinModel(String skinFileName) {
        if (skinFileName == null || skinFileName.isEmpty()) return true;

        if (SKIN_MODEL.containsKey(skinFileName)) {
            return SKIN_MODEL.get(skinFileName);
        }

        String key = skinFileName + ".png";

        File file = new File(SKIN_DIR, key);
        if (!file.exists()) {
            file = new File(CUSTOM_SKIN_DIR, key);
        }

        if (!file.exists()) {
            SKIN_MODEL.put(skinFileName, true);
            return true;
        }

        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                SKIN_MODEL.put(skinFileName, true);
                return true;
            }

            int pixel = image.getRGB(54, 20);
            boolean isSteve = (pixel >> 24) != 0x00;
            SKIN_MODEL.put(skinFileName, isSteve);
            return isSteve;
        } catch (IOException e) {
            e.printStackTrace();
            SKIN_MODEL.put(skinFileName, true);
            return true;
        }
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 16) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_\\-]+$");
    }

    public static boolean isValidImage(File file) {
        try (InputStream in = new FileInputStream(file)) {
            byte[] header = new byte[8];
            if (in.read(header) != 8) {
                return false;
            }

            boolean isPng = header[0] == (byte) 0x89 && header[1] == (byte) 0x50
                && header[2] == (byte) 0x4E
                && header[3] == (byte) 0x47
                && header[4] == (byte) 0x0D
                && header[5] == (byte) 0x0A
                && header[6] == (byte) 0x1A
                && header[7] == (byte) 0x0A;

            boolean isJpg = header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF;

            if (!isPng && !isJpg) {
                return false;
            }

            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                return false;
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width > 1024 || height > 1024) {
                return false;
            }

            int pixel = image.getRGB(54, 20);
            boolean isTransparent = (pixel >> 24) == 0x00;
            SKIN_MODEL.put(file.getName(), !isTransparent);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public enum TextureType {
        SKIN,
        CAPE
    }
}
