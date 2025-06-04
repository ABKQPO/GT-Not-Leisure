package com.science.gtnl.common.render;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.render.PlayerDollRenderManager.*;

import java.awt.image.BufferedImage;
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
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlayerDollRenderManagerClient {

    public static boolean isSteveModel = false;
    public static Map<String, ResourceLocation> textureCache = new HashMap<>();
    private static final Map<String, BufferedImage> pendingTextures = new HashMap<>();
    private static final Map<String, Integer> uploadedTextureIds = new HashMap<>();
    public static ResourceLocation DEFAULT_SKIN = new ResourceLocation(RESOURCE_ROOT_ID + ":" + "model/skin.png");
    public static ResourceLocation DEFAULT_CAPE = new ResourceLocation(RESOURCE_ROOT_ID + ":" + "model/cape.png");
    public static ResourceLocation MODEL_STEVE = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "model/PlayerDollSteve.obj");
    public static ResourceLocation MODEL_ALEX = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "model/PlayerDollAlex.obj");
    public static ResourceLocation MODEL_STEVE_ELYTRA = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "model/PlayerDollSteve.obj");
    public static ResourceLocation MODEL_ALEX_ELYTRA = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "model/PlayerDollAlexElytra.obj");
    public static IModelCustom modelSteve = AdvancedModelLoader.loadModel(MODEL_STEVE);
    public static IModelCustom modelAlex = AdvancedModelLoader.loadModel(MODEL_ALEX);
    public static IModelCustom modelSteveElytra = AdvancedModelLoader.loadModel(MODEL_STEVE_ELYTRA);
    public static IModelCustom modelAlexElytra = AdvancedModelLoader.loadModel(MODEL_ALEX_ELYTRA);

    public static File SKIN_DIR = new File("config/GTNotLeisure/skin");
    public static File CUSTOM_SKIN_DIR = new File("config/GTNotLeisure/custom_skin");
    public static File CUSTOM_CAPE_DIR = new File("config/GTNotLeisure/custom_cape");

    static {
        if (!SKIN_DIR.exists()) SKIN_DIR.mkdirs();
        if (!CUSTOM_SKIN_DIR.exists()) CUSTOM_SKIN_DIR.mkdirs();
        if (!CUSTOM_CAPE_DIR.exists()) CUSTOM_CAPE_DIR.mkdirs();
    }

    public static void renderModel(ResourceLocation skinTexture, ResourceLocation capeTexture, boolean enableElytra) {
        bindTexture(skinTexture);
        if (isSteveModel) {
            if (enableElytra) {
                modelSteveElytra.renderPart("Body");
                modelSteveElytra.renderPart("Body_Layer");
                modelSteveElytra.renderPart("Hat");
                modelSteveElytra.renderPart("Hat_Layer");
                modelSteveElytra.renderPart("Head");
                modelSteveElytra.renderPart("Left_Arm");
                modelSteveElytra.renderPart("Left_Arm_Layer");
                modelSteveElytra.renderPart("Left_Leg");
                modelSteveElytra.renderPart("Left_Leg_Layer");
                modelSteveElytra.renderPart("Right_Arm");
                modelSteveElytra.renderPart("Right_Arm_Layer");
                modelSteveElytra.renderPart("Right_Leg");
                modelSteveElytra.renderPart("Right_Leg_Layer");
                bindTexture(capeTexture);
                modelSteveElytra.renderPart("ElytraRight");
                modelSteveElytra.renderPart("ElytraLeft");
            } else {
                modelSteve.renderPart("Body");
                modelSteve.renderPart("Body_Layer");
                modelSteve.renderPart("Hat");
                modelSteve.renderPart("Hat_Layer");
                modelSteve.renderPart("Head");
                modelSteve.renderPart("Left_Arm");
                modelSteve.renderPart("Left_Arm_Layer");
                modelSteve.renderPart("Left_Leg");
                modelSteve.renderPart("Left_Leg_Layer");
                modelSteve.renderPart("Right_Arm");
                modelSteve.renderPart("Right_Arm_Layer");
                modelSteve.renderPart("Right_Leg");
                modelSteve.renderPart("Right_Leg_Layer");
                bindTexture(capeTexture);
                modelSteve.renderPart("cape");
            }
        } else {
            if (enableElytra) {
                modelAlexElytra.renderPart("Body");
                modelAlexElytra.renderPart("Body_Layer");
                modelAlexElytra.renderPart("Hat");
                modelAlexElytra.renderPart("Hat_Layer");
                modelAlexElytra.renderPart("Head");
                modelAlexElytra.renderPart("Left_Arm");
                modelAlexElytra.renderPart("Left_Arm_Layer");
                modelAlexElytra.renderPart("Left_Leg");
                modelAlexElytra.renderPart("Left_Leg_Layer");
                modelAlexElytra.renderPart("Right_Arm");
                modelAlexElytra.renderPart("Right_Arm_Layer");
                modelAlexElytra.renderPart("Right_Leg");
                modelAlexElytra.renderPart("Right_Leg_Layer");
                bindTexture(capeTexture);
                modelAlexElytra.renderPart("ElytraRight");
                modelAlexElytra.renderPart("ElytraLeft");
            } else {
                modelAlex.renderPart("Body");
                modelAlex.renderPart("Body_Layer");
                modelAlex.renderPart("Hat");
                modelAlex.renderPart("Hat_Layer");
                modelAlex.renderPart("Head");
                modelAlex.renderPart("Left_Arm");
                modelAlex.renderPart("Left_Arm_Layer");
                modelAlex.renderPart("Left_Leg");
                modelAlex.renderPart("Left_Leg_Layer");
                modelAlex.renderPart("Right_Arm");
                modelAlex.renderPart("Right_Arm_Layer");
                modelAlex.renderPart("Right_Leg");
                modelAlex.renderPart("Right_Leg_Layer");
                bindTexture(capeTexture);
                modelAlex.renderPart("cape");
            }
        }
    }

    public static String fetchSkinUrl(String uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);

            if (connection.getResponseCode() != 200) {
                BLACKLISTED_UUIDS.add(uuid);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String json = response.toString();
            if (json.contains("\"errorMessage\"")) {
                BLACKLISTED_UUIDS.add(uuid);
                return null;
            }

            JsonObject profileJson = new JsonParser().parse(json)
                .getAsJsonObject();
            String value = profileJson.getAsJsonArray("properties")
                .get(0)
                .getAsJsonObject()
                .get("value")
                .getAsString();
            String decoded = new String(
                Base64.getDecoder()
                    .decode(value));
            JsonObject texturesJson = new JsonParser().parse(decoded)
                .getAsJsonObject();
            return texturesJson.getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            BLACKLISTED_UUIDS.add(uuid);
            return null;
        }
    }

    public static ResourceLocation downloadAndCacheSkinFromUrl(String url, String uuid) {

        if (BLACKLISTED_SKIN_URLS.contains(url)) {
            return DEFAULT_SKIN;
        }
        File targetFile = new File(SKIN_DIR, uuid + ".png");

        if (targetFile.exists()) {
            return getLocalTextureFromFile(targetFile);
        }

        ResourceLocation result = AsyncDownloader.getTexture(uuid, () -> {
            if (BLACKLISTED_SKIN_URLS.contains(url)) return DEFAULT_SKIN;

            try (InputStream in = new URL(url).openStream(); FileOutputStream out = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                if (isValidImage(targetFile)) {
                    ResourceLocation texture = getLocalTextureFromFile(targetFile);
                    if (texture != null) {
                        textureCache.put(uuid, texture);
                    }
                    return texture;
                }
            } catch (IOException e) {
                targetFile.delete();
            }
            BLACKLISTED_SKIN_URLS.add(url);
            return DEFAULT_SKIN;
        });

        return result != null ? result : DEFAULT_SKIN;
    }

    public static ResourceLocation downloadAndCacheCustomSkin(String skinHttp) {

        if (BLACKLISTED_SKIN_URLS.contains(skinHttp)) {
            return null;
        }

        String fileName = Integer.toHexString(skinHttp.hashCode()) + ".png";
        File targetFile = new File(CUSTOM_SKIN_DIR, fileName);

        ResourceLocation cached = textureCache.get(Integer.toHexString(skinHttp.hashCode()));
        if (cached != null) return cached;

        if (targetFile.exists()) {
            return getLocalTextureFromFile(targetFile);
        }

        ResourceLocation result = AsyncDownloader.getTexture(Integer.toHexString(skinHttp.hashCode()), () -> {
            try (InputStream in = new URL(skinHttp).openStream();
                FileOutputStream out = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                if (isValidImage(targetFile)) {
                    ResourceLocation texture = getLocalTextureFromFile(targetFile);
                    if (texture != null) {
                        textureCache.put(Integer.toHexString(skinHttp.hashCode()), texture);
                    }
                    return texture;
                }
            } catch (IOException e) {
                targetFile.delete();
            }
            BLACKLISTED_SKIN_URLS.add(skinHttp);
            return null;
        });

        return result != null ? result : DEFAULT_SKIN;
    }

    public static ResourceLocation downloadAndCacheCustomCape(String capeHttp) {
        String fileName = Integer.toHexString(capeHttp.hashCode()) + ".png";
        File targetFile = new File(CUSTOM_CAPE_DIR, fileName);

        if (BLACKLISTED_CAPE_URLS.contains(capeHttp)) {
            return null;
        }

        ResourceLocation cached = textureCache.get(Integer.toHexString(capeHttp.hashCode()));
        if (cached != null) return cached;

        if (targetFile.exists()) {
            return getLocalTextureFromFile(targetFile);
        }

        ResourceLocation result = AsyncDownloader.getTexture(Integer.toHexString(capeHttp.hashCode()), () -> {
            try (InputStream in = new URL(capeHttp).openStream();
                FileOutputStream out = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                if (isValidImage(targetFile)) {
                    ResourceLocation texture = getLocalTextureFromFile(targetFile);
                    if (texture != null) {
                        textureCache.put(Integer.toHexString(capeHttp.hashCode()), texture);
                    }
                    return texture;
                }
            } catch (IOException e) {
                targetFile.delete();
            }
            BLACKLISTED_CAPE_URLS.add(capeHttp);
            return null;
        });

        return result != null ? result : DEFAULT_CAPE;
    }

    public static ResourceLocation getLocalTextureFromFile(File file) {
        if (file == null || !file.exists()) return null;

        String fileName = file.getName();

        if (textureCache.containsKey(fileName)) {
            return textureCache.get(fileName);
        }

        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                pendingTextures.put(fileName, image);

                ResourceLocation textureLocation = new ResourceLocation("custom", "texture_" + fileName);
                textureCache.put(fileName, textureLocation);

                return textureLocation;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
            isSteveModel = !isTransparent;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void bindTexture(ResourceLocation texture) {
        if (texture == null) {
            texture = DEFAULT_SKIN;
        }

        if (texture.getResourceDomain()
            .equals("custom")) {
            String key = texture.getResourcePath()
                .replace("texture_", "");

            if (!uploadedTextureIds.containsKey(key)) {
                BufferedImage image = pendingTextures.remove(key);
                if (image != null) {
                    int textureId = TextureUtil.uploadTextureImage(GL11.glGenTextures(), image);
                    uploadedTextureIds.put(key, textureId);
                } else {
                    return;
                }
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, uploadedTextureIds.get(key));
        } else {
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        }
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 16) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_\\-]+$");
    }

}
