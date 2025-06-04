package com.science.gtnl.common.render.tile;

import static com.science.gtnl.common.render.PlayerDollRenderManager.*;
import static com.science.gtnl.common.render.PlayerDollRenderManagerClient.*;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerDollRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        if (!(tileEntity instanceof TileEntityPlayerDoll)) return;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);

        int orientation = tileEntity.getBlockMetadata();
        switch (orientation) {
            case 4:
                GL11.glRotatef(90, 0, 1, 0);
                break;
            case 5:
                GL11.glRotatef(-90, 0, 1, 0);
                break;
            case 3:
                GL11.glRotatef(180, 0, 1, 0);
                break;
            default:
                break;
        }
        GL11.glRotatef(180, 0, 1, 0);

        ResourceLocation skinTexture = DEFAULT_SKIN;
        ResourceLocation capeTexture = DEFAULT_CAPE;

        NBTTagCompound nbt = new NBTTagCompound();
        tileEntity.writeToNBT(nbt);

        if (nbt.hasKey("SkinHttp", 8)) {
            String skinHttp = nbt.getString("SkinHttp");
            if (!StringUtils.isNullOrEmpty(skinHttp)) {
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

            if (nbt.hasKey("SkullOwner", 8)) {
                playerName = nbt.getString("SkullOwner");
                if (playerName == null || playerName.isEmpty()) {
                    playerName = Minecraft.getMinecraft().thePlayer.getCommandSenderName();
                }
                if (!isValidUsername(playerName)) {
                    playerName = Minecraft.getMinecraft().thePlayer.getCommandSenderName();
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

            if (nbt.hasKey("CapeHttp", 8)) {
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
        if (nbt.hasKey("enableElytra")) {
            enableElytra = nbt.getBoolean("enableElytra");
        }

        if (skinTexture == null) {
            skinTexture = DEFAULT_SKIN;
        }

        renderModel(skinTexture, capeTexture, enableElytra);
        GL11.glPopMatrix();
    }
}
