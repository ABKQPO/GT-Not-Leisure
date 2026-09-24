package com.science.gtnl.utils.detrav;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.science.gtnl.common.packet.ProspectingPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.GTUtility;

@SideOnly(Side.CLIENT)
public class DetravMapTexture extends AbstractTexture {

    public static final String ALL_ORES = "All";

    public final ProspectingPacket packet;
    public int width = -1;
    public int height = -1;
    public boolean invert;

    private String selected = ALL_ORES;
    private short[] topId;
    private int[] topY;

    public DetravMapTexture(ProspectingPacket packet) {
        this.packet = packet;
    }

    private BufferedImage getImage() {
        int backgroundColor = invert ? Color.GRAY.getRGB() : Color.WHITE.getRGB();
        int blockSize = packet.getSize();
        int chunkSize = packet.size * 2 + 1;

        topId = null;
        topY = null;

        BufferedImage image = new BufferedImage(blockSize, blockSize, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();

        int playerX = packet.posX - (packet.chunkX - packet.size) * 16 - 1;
        int playerZ = packet.posZ - (packet.chunkZ - packet.size) * 16 - 1;

        for (int z = 0; z < blockSize; z++) {
            for (int x = 0; x < blockSize; x++) {
                image.setRGB(x, z, backgroundColor);
            }
        }

        switch (packet.ptype) {
            case ProspectingPacket.MODE_BIG_ORES, ProspectingPacket.MODE_ALL_ORES -> drawOreTexture(image, blockSize);
            case ProspectingPacket.MODE_FLUIDS -> drawFluidTexture(image, chunkSize);
            case ProspectingPacket.MODE_POLLUTION -> drawPollutionTexture(raster, chunkSize);
            default -> {}
        }

        for (int z = 0; z < blockSize; z++) {
            for (int x = 0; x < blockSize; x++) {
                if (x % 16 == 0 || z % 16 == 0) {
                    raster.setSample(x, z, 0, raster.getSample(x, z, 0) / 2);
                    raster.setSample(x, z, 1, raster.getSample(x, z, 1) / 2);
                    raster.setSample(x, z, 2, raster.getSample(x, z, 2) / 2);
                }

                if (x == playerX || z == playerZ) {
                    raster.setSample(x, z, 0, (raster.getSample(x, z, 0) + 255) / 2);
                    raster.setSample(x, z, 1, raster.getSample(x, z, 1) / 2);
                    raster.setSample(x, z, 2, raster.getSample(x, z, 2) / 2);
                }
            }
        }

        return image;
    }

    private void drawOreTexture(BufferedImage image, int blockSize) {
        short[] depth = new short[blockSize * blockSize];
        Arrays.fill(depth, (short) -1);

        topId = new short[blockSize * blockSize];
        Arrays.fill(topId, (short) -1);
        topY = new int[blockSize * blockSize];

        short selectedId = findSelectedObjectId();

        for (var entry : packet.map.long2ShortEntrySet()) {
            if (selectedId != -1 && selectedId != entry.getShortValue()) {
                continue;
            }

            long coordinate = entry.getLongKey();
            int x = CoordinatePacker.unpackX(coordinate);
            int y = CoordinatePacker.unpackY(coordinate);
            int z = CoordinatePacker.unpackZ(coordinate);
            int index = x + z * blockSize;
            if (y < depth[index]) {
                continue;
            }
            depth[index] = (short) y;

            topId[index] = entry.getShortValue();
            topY[index] = y;
            image.setRGB(x, z, packet.getObjectColor(entry.getShortValue()));
        }
    }

    private void drawFluidTexture(BufferedImage image, int chunkSize) {
        int maxAmount = 1;
        for (int chunkZ = 0; chunkZ < chunkSize; chunkZ++) {
            for (int chunkX = 0; chunkX < chunkSize; chunkX++) {
                if (!matchesFilter(chunkX, chunkZ)) {
                    continue;
                }
                maxAmount = Math.max(maxAmount, packet.getAmount(chunkX, chunkZ));
            }
        }

        for (int chunkZ = 0; chunkZ < chunkSize; chunkZ++) {
            for (int chunkX = 0; chunkX < chunkSize; chunkX++) {
                if (!matchesFilter(chunkX, chunkZ)) {
                    continue;
                }
                int amount = packet.getAmount(chunkX, chunkZ);
                if (amount <= 0) {
                    continue;
                }

                int objectId = packet.map.get(CoordinatePacker.pack(chunkX, 0, chunkZ));
                int fill = Math.max(1, Math.round(16F * amount / maxAmount));

                for (int y = 16 - fill; y < 16; y++) {
                    for (int x = 0; x < 16; x++) {
                        image.setRGB(chunkX * 16 + x, chunkZ * 16 + y, packet.getObjectColor((short) objectId));
                    }
                }
            }
        }
    }

    private boolean matchesFilter(int chunkX, int chunkZ) {
        int objectId = packet.map.get(CoordinatePacker.pack(chunkX, 0, chunkZ));
        var object = packet.objects.get((short) objectId);
        if (object == null) {
            return false;
        }
        return ALL_ORES.equals(selected) || selected.equals(object.left());
    }

    private void drawPollutionTexture(WritableRaster raster, int chunkSize) {
        for (int chunkZ = 0; chunkZ < chunkSize; chunkZ++) {
            for (int chunkX = 0; chunkX < chunkSize; chunkX++) {
                int amount = packet.getAmount(chunkX, chunkZ);
                if (amount == 0) {
                    continue;
                }
                float multiplier = amount / 500000f;
                if (!invert) {
                    multiplier = 1f - multiplier;
                }
                multiplier = GTUtility.clamp(multiplier, 0, 1);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int imageX = chunkX * 16 + x;
                        int imageZ = chunkZ * 16 + z;
                        raster.setSample(imageX, imageZ, 0, (int) (raster.getSample(imageX, imageZ, 0) * multiplier));
                        raster.setSample(imageX, imageZ, 1, (int) (raster.getSample(imageX, imageZ, 1) * multiplier));
                        raster.setSample(imageX, imageZ, 2, (int) (raster.getSample(imageX, imageZ, 2) * multiplier));
                    }
                }
            }
        }
    }

    private short findSelectedObjectId() {
        if (ALL_ORES.equals(selected)) {
            return -1;
        }
        for (var entry : packet.objects.short2ObjectEntrySet()) {
            if (selected.equals(
                entry.getValue()
                    .left())) {
                return entry.getShortKey();
            }
        }
        return -1;
    }

    public String getTopOreName(int x, int z) {
        short objectId = getTopOreObjectId(x, z);
        if (objectId < 0) {
            return null;
        }
        var object = packet.objects.get(objectId);
        return object == null ? null : object.left();
    }

    public int getTopOreY(int x, int z) {
        int blockSize = packet.getSize();
        if (topY == null || x < 0 || z < 0 || x >= blockSize || z >= blockSize) {
            return 0;
        }
        return topY[x + z * blockSize];
    }

    public int getTopOreColor(int x, int z) {
        short objectId = getTopOreObjectId(x, z);
        return objectId < 0 ? 0 : packet.getObjectColor(objectId);
    }

    public String getTopOreMaterialName(int x, int z) {
        short objectId = getTopOreObjectId(x, z);
        return objectId < 0 ? "" : packet.getOreMaterialName(objectId);
    }

    private short getTopOreObjectId(int x, int z) {
        int blockSize = packet.getSize();
        if (topId == null || x < 0 || z < 0 || x >= blockSize || z >= blockSize) {
            return -1;
        }
        return topId[x + z * blockSize];
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) {
        deleteGlTexture();
        if (packet != null) {
            int textureId = getGlTextureId();
            if (textureId < 0) {
                return;
            }
            TextureUtil.uploadTextureImageAllocate(textureId, getImage(), false, false);
            width = packet.getSize();
            height = packet.getSize();
        }
    }

    public void loadTexture(IResourceManager resourceManager, boolean inverted) {
        this.invert = inverted;
        loadTexture(resourceManager);
    }

    public void loadTexture(IResourceManager resourceManager, String selection, boolean inverted) {
        this.selected = selection;
        loadTexture(resourceManager, inverted);
    }

    public int glBindTexture() {
        if (glTextureId < 0) {
            return glTextureId;
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getGlTextureId());
        return glTextureId;
    }

    public void draw(int x, int y) {
        draw(x, y, 1F);
    }

    public void draw(float x, float y, float scale) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        float scaledWidth = width * scale;
        float scaledHeight = height * scale;
        tessellator.addVertexWithUV(x, y + scaledHeight, 0, 0F, 1F);
        tessellator.addVertexWithUV(x + scaledWidth, y + scaledHeight, 0, 1F, 1F);
        tessellator.addVertexWithUV(x + scaledWidth, y, 0, 1F, 0F);
        tessellator.addVertexWithUV(x, y, 0, 0F, 0F);
        tessellator.draw();
    }
}
