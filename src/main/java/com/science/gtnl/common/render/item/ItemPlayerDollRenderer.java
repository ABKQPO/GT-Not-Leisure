package com.science.gtnl.common.render.item;

import static com.science.gtnl.common.render.PlayerDollRenderManager.BLACKLISTED_UUIDS;
import static com.science.gtnl.common.render.PlayerDollRenderManager.UUID_CACHE;
import static com.science.gtnl.common.render.PlayerDollRenderManager.fetchUUID;
import static com.science.gtnl.common.render.PlayerDollRenderManager.offlineMode;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.DEFAULT_SKIN;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.TextureType;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.downloadAndCacheCustomCape;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.downloadAndCacheCustomSkin;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.isValidUsername;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.loadProfileTexture;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.renderModel;

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
            case EQUIPPED -> {
                GL11.glScalef(1f, 1f, 1f);
                GL11.glTranslatef(0.6f, 0f, 0.6f);
                GL11.glRotatef(0, 0f, 1f, 0f);
            }
            case EQUIPPED_FIRST_PERSON -> {
                GL11.glScalef(1.5f, 1.5f, 1.5f);
                GL11.glTranslatef(0.3f, -0.1f, 0.3f);
                GL11.glRotatef(-90, 0f, 1f, 0f);
            }
            case ENTITY -> {
                GL11.glScalef(1f, 1f, 1f);
                GL11.glTranslatef(0f, -0.5f, 0f);
                GL11.glRotatef(90, 0f, 1f, 0f);
            }
            case INVENTORY -> {
                GL11.glScalef(1f, 1f, 1f);
                GL11.glTranslatef(0f, -0.6f, 0f);
                GL11.glRotatef(90, 0f, 1f, 0f);
            }
        }

        ResourceLocation skinTexture = DEFAULT_SKIN;
        ResourceLocation capeTexture = null;
        byte renderMode = 0;

        NBTTagCompound nbt = item.hasTagCompound() ? item.getTagCompound() : new NBTTagCompound();

        if (nbt.hasKey("RenderCapeMode", 1)) {
            renderMode = nbt.getByte("RenderCapeMode");
        }

        if (nbt.hasKey("SkinHttp", 8)) {
            String skinHttp = nbt.getString("SkinHttp");
            if (!StringUtils.isNullOrEmpty(skinHttp)) {
                skinTexture = downloadAndCacheCustomSkin(skinHttp);
                if (skinTexture == null) skinTexture = DEFAULT_SKIN;

                if (nbt.hasKey("CapeHttp", 8)) {
                    String capeHttp = nbt.getString("CapeHttp");
                    if (!StringUtils.isNullOrEmpty(capeHttp)) {
                        capeTexture = downloadAndCacheCustomCape(capeHttp);
                    }
                }

                renderModel(skinTexture, capeTexture, renderMode);
                GL11.glPopMatrix();
                return;
            }
        }

        if (MainConfig.item.player_doll.enableCustomSkin && !offlineMode) {
            String ownerUUID = null;
            String playerName;

            if (nbt.hasKey("SkullOwner", 8)) {
                playerName = nbt.getString("SkullOwner");
                if (StringUtils.isNullOrEmpty(playerName) || !isValidUsername(playerName)) {
                    playerName = minecraft.thePlayer.getCommandSenderName();
                }

                String cachedUUID = UUID_CACHE.get(playerName.toLowerCase());

                if (nbt.hasKey("OwnerUUID", 8)) {
                    ownerUUID = nbt.getString("OwnerUUID");
                    if (cachedUUID == null || !cachedUUID.equals(ownerUUID)) {
                        String freshUUID = fetchUUID(playerName);
                        if (freshUUID != null) ownerUUID = freshUUID;
                    }
                } else {
                    if (cachedUUID != null) {
                        ownerUUID = cachedUUID;
                    } else {
                        ownerUUID = fetchUUID(playerName);
                    }
                }

                if (ownerUUID != null) nbt.setString("OwnerUUID", ownerUUID);

            } else if (nbt.hasKey("OwnerUUID", 8)) {
                ownerUUID = nbt.getString("OwnerUUID");
            }

            if (ownerUUID != null && !BLACKLISTED_UUIDS.contains(ownerUUID)) {
                skinTexture = loadProfileTexture(ownerUUID, TextureType.SKIN);

                boolean hasCustomCape = false;
                if (nbt.hasKey("CapeHttp", 8)) {
                    String capeHttp = nbt.getString("CapeHttp");
                    if (!StringUtils.isNullOrEmpty(capeHttp)) {
                        capeTexture = downloadAndCacheCustomCape(capeHttp);
                        hasCustomCape = true;
                    }
                }

                if (!hasCustomCape) {
                    capeTexture = loadProfileTexture(ownerUUID, TextureType.CAPE);
                }
            }
        }

        renderModel(skinTexture, capeTexture, renderMode);
        GL11.glPopMatrix();
    }
}
