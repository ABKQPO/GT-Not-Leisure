package com.science.gtnl.common.render.item;

import static com.science.gtnl.common.render.PlayerDollRenderManager.*;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.*;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemPlayerDollRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft minecraft = Minecraft.getMinecraft();
        GL11.glPushMatrix();

        switch (type) {
            case EQUIPPED:
                GL11.glScalef(1f, 1f, 1f);
                GL11.glTranslatef(0.6f, 0f, 0.6f);
                GL11.glRotatef(0, 0f, 1f, 0f);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glScalef(1.5f, 1.5f, 1.5f);
                GL11.glTranslatef(0.3f, -0.1f, 0.3f);
                GL11.glRotatef(-90, 0f, 1f, 0f);
                break;
            case ENTITY:
                GL11.glScalef(1f, 1f, 1f);
                GL11.glTranslatef(0f, -0.5f, 0f);
                GL11.glRotatef(90, 0f, 1f, 0f);
                break;
            case INVENTORY:
                GL11.glScalef(1f, 1f, 1f);
                GL11.glTranslatef(0f, -0.6f, 0f);
                GL11.glRotatef(90, 0f, 1f, 0f);
                break;
            default:
                break;
        }

        ResourceLocation skinTexture = DEFAULT_SKIN;
        ResourceLocation capeTexture = DEFAULT_CAPE;

        if (item.hasTagCompound()) {
            NBTTagCompound nbt = item.getTagCompound();
            if (nbt.hasKey("SkinHttp", 8)) {
                String skinHttp = nbt.getString("SkinHttp");
                boolean enableElytra = false;
                if (nbt.hasKey("enableElytra")) {
                    enableElytra = nbt.getBoolean("enableElytra");
                }
                if (nbt.hasKey("CapeHttp", 8)) {
                    String capeHttp = nbt.getString("CapeHttp");
                    if (!StringUtils.isNullOrEmpty(capeHttp)) {
                        capeTexture = downloadAndCacheCustomCape(capeHttp);
                    }
                    if (capeTexture == null) {
                        capeTexture = DEFAULT_CAPE;
                    }
                }

                if (!StringUtils.isNullOrEmpty(skinHttp)) {
                    skinTexture = downloadAndCacheCustomSkin(skinHttp);
                    if (skinTexture == null) {
                        skinTexture = DEFAULT_SKIN;
                    }
                    renderModel(skinTexture, capeTexture, enableElytra);
                    GL11.glPopMatrix();
                    return;
                }
            }
        }

        if (MainConfig.enableCustomPlayerDoll && !offlineMode) {
            String ownerUUID = null;
            String playerName;
            NBTTagCompound nbt = item.getTagCompound();

            if (nbt != null) {
                if (nbt.hasKey("SkullOwner", 8)) {
                    playerName = nbt.getString("SkullOwner");
                    if (playerName == null || playerName.isEmpty()) {
                        playerName = minecraft.thePlayer.getCommandSenderName();
                    }
                    if (!isValidUsername(playerName)) {
                        playerName = minecraft.thePlayer.getCommandSenderName();
                    }

                    String cachedUUID = UUID_CACHE.get(playerName.toLowerCase());
                    if (nbt.hasKey("OwnerUUID", 8)) {
                        ownerUUID = nbt.getString("OwnerUUID");

                        if (cachedUUID != null && cachedUUID.equals(ownerUUID)) {
                            ownerUUID = cachedUUID;
                            nbt.setString("OwnerUUID", ownerUUID);
                        } else {
                            String freshUUID = fetchUUID(playerName);
                            if (freshUUID != null) {
                                ownerUUID = freshUUID;
                                nbt.setString("OwnerUUID", ownerUUID);
                            }
                        }
                    } else {
                        if (cachedUUID != null) {
                            ownerUUID = cachedUUID;
                            nbt.setString("OwnerUUID", ownerUUID);
                        } else {
                            ownerUUID = fetchUUID(playerName);
                            if (ownerUUID != null) {
                                nbt.setString("OwnerUUID", ownerUUID);
                            }
                        }
                    }
                } else if (nbt.hasKey("OwnerUUID", 8)) {
                    ownerUUID = nbt.getString("OwnerUUID");
                }
            }

            if (ownerUUID != null && !BLACKLISTED_UUIDS.contains(ownerUUID)) {
                File cachedFile = new File(SKIN_DIR, ownerUUID + ".png");

                if (cachedFile.exists()) {
                    skinTexture = getLocalTextureFromFile(cachedFile);
                } else {
                    String skinUrl = fetchSkinUrl(ownerUUID);
                    if (skinUrl != null) {
                        skinTexture = downloadAndCacheSkinFromUrl(skinUrl, ownerUUID);
                    }
                }
            }

            if (nbt != null && nbt.hasKey("CapeHttp", 8)) {
                String capeHttp = nbt.getString("CapeHttp");
                if (!StringUtils.isNullOrEmpty(capeHttp)) {
                    capeTexture = downloadAndCacheCustomCape(capeHttp);
                }
                if (capeTexture == null) {
                    capeTexture = DEFAULT_CAPE;
                }
            }
        }

        boolean enableElytra = false;
        NBTTagCompound nbt = item.getTagCompound();
        if (nbt != null && nbt.hasKey("enableElytra")) {
            enableElytra = nbt.getBoolean("enableElytra");
        }

        renderModel(skinTexture, capeTexture, enableElytra);
        GL11.glPopMatrix();
    }
}
