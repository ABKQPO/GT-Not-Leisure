package com.science.gtnl.common.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
import com.science.gtnl.common.render.PlayerDollRenderManager;
import com.science.gtnl.common.render.PlayerDollRenderManagerClient;
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
            case 4 -> GL11.glRotatef(90, 0, 1, 0);
            case 5 -> GL11.glRotatef(-90, 0, 1, 0);
            case 3 -> GL11.glRotatef(180, 0, 1, 0);
        }

        GL11.glRotatef(180, 0, 1, 0);

        ResourceLocation skinTexture = PlayerDollRenderManagerClient.DEFAULT_SKIN;
        ResourceLocation capeTexture = null;

        NBTTagCompound nbt = new NBTTagCompound();
        tileEntity.writeToNBT(nbt);

        byte renderMode = 0;
        if (nbt.hasKey("RenderCapeMode", 1)) {
            renderMode = nbt.getByte("RenderCapeMode");
        }

        if (nbt.hasKey("SkinHttp", 8)) {
            String skinHttp = nbt.getString("SkinHttp");
            if (!StringUtils.isNullOrEmpty(skinHttp)) {
                skinTexture = PlayerDollRenderManagerClient.downloadAndCacheCustomSkin(skinHttp);
                if (skinTexture == null) skinTexture = PlayerDollRenderManagerClient.DEFAULT_SKIN;

                if (nbt.hasKey("CapeHttp", 8)) {
                    String capeHttp = nbt.getString("CapeHttp");
                    if (!StringUtils.isNullOrEmpty(capeHttp)) {
                        capeTexture = PlayerDollRenderManagerClient.downloadAndCacheCustomCape(capeHttp);
                    }
                }

                PlayerDollRenderManagerClient.renderModel(skinTexture, capeTexture, renderMode);
                GL11.glPopMatrix();
                return;
            }
        }

        if (MainConfig.item.player_doll.enableCustomSkin && !PlayerDollRenderManager.offlineMode) {
            String ownerUUID = null;
            String playerName;

            if (nbt.hasKey("SkullOwner", 8)) {
                playerName = nbt.getString("SkullOwner");

                if (StringUtils.isNullOrEmpty(playerName)
                    || !PlayerDollRenderManagerClient.isValidUsername(playerName)) {
                    playerName = Minecraft.getMinecraft().thePlayer.getCommandSenderName();
                }

                String cachedUUID = PlayerDollRenderManager.UUID_CACHE.get(playerName.toLowerCase());

                if (nbt.hasKey("OwnerUUID", 8)) {
                    ownerUUID = nbt.getString("OwnerUUID");

                    if (cachedUUID != null && cachedUUID.equals(ownerUUID)) {
                        ownerUUID = cachedUUID;
                    } else {
                        String freshUUID = PlayerDollRenderManager.fetchUUID(playerName);
                        if (freshUUID != null) ownerUUID = freshUUID;
                    }

                } else {
                    if (cachedUUID != null) {
                        ownerUUID = cachedUUID;
                    } else {
                        ownerUUID = PlayerDollRenderManager.fetchUUID(playerName);
                    }
                }

                if (ownerUUID != null) {
                    nbt.setString("OwnerUUID", ownerUUID);
                }

            } else if (nbt.hasKey("OwnerUUID", 8)) {
                ownerUUID = nbt.getString("OwnerUUID");
            }

            if (ownerUUID != null && !PlayerDollRenderManager.BLACKLISTED_UUIDS.contains(ownerUUID)) {
                skinTexture = PlayerDollRenderManagerClient
                    .loadProfileTexture(ownerUUID, PlayerDollRenderManagerClient.TextureType.SKIN);

                boolean hasCustomCape = false;
                if (nbt.hasKey("CapeHttp", 8)) {
                    String capeHttp = nbt.getString("CapeHttp");
                    if (!StringUtils.isNullOrEmpty(capeHttp)) {
                        capeTexture = PlayerDollRenderManagerClient.downloadAndCacheCustomCape(capeHttp);
                        hasCustomCape = true;
                    }
                }

                if (!hasCustomCape) {
                    capeTexture = PlayerDollRenderManagerClient
                        .loadProfileTexture(ownerUUID, PlayerDollRenderManagerClient.TextureType.CAPE);
                }
            }
        }

        PlayerDollRenderManagerClient.renderModel(skinTexture, capeTexture, renderMode);
        GL11.glPopMatrix();
    }
}
