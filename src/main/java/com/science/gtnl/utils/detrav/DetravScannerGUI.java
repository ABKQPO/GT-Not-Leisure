package com.science.gtnl.utils.detrav;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static com.science.gtnl.ScienceNotLeisure.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.science.gtnl.common.packet.ProspectingPacket;
import com.science.gtnl.common.packet.TeleportRequestPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.GTUtility;

@SideOnly(Side.CLIENT)
public class DetravScannerGUI extends GuiScreen {

    public static final int MIN_HEIGHT = 128;
    public static final int MIN_WIDTH = 128;
    public static final ResourceLocation BACKGROUND = new ResourceLocation("gregtech:textures/gui/propick.png");
    private static DetravMapTexture map;

    public OresList oresList;
    public int prevW;
    public int prevH;
    public long lastClickTime;
    public int lastClickX = -1;
    public int lastClickY = -1;

    public static void newMap(DetravMapTexture newMap) {
        if (map != null) {
            map.deleteGlTexture();
        }
        map = newMap;
        map.loadTexture(null);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        long currentTime = System.currentTimeMillis();
        if (button == 0) {
            if (currentTime - lastClickTime < 300 && Math.abs(mouseX - lastClickX) < 5
                && Math.abs(mouseY - lastClickY) < 5) {
                onMapDoubleClick(mouseX, mouseY);
            }
            lastClickTime = currentTime;
            lastClickX = mouseX;
            lastClickY = mouseY;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    public void onMapDoubleClick(int mouseX, int mouseY) {
        if (map == null || map.packet == null) {
            return;
        }

        int currentWidth = Math.max(map.width, MIN_WIDTH);
        int currentHeight = Math.max(map.height, MIN_HEIGHT);
        int guiX = (width - currentWidth - 100) / 2;
        int guiY = (height - currentHeight) / 2;

        int localX = mouseX - guiX;
        int localY = mouseY - guiY;
        if (localX < 0 || localY < 0 || localX >= map.width || localY >= map.height) {
            return;
        }

        int worldX = (map.packet.chunkX - map.packet.size) * 16 + localX;
        int worldZ = (map.packet.chunkZ - map.packet.size) * 16 + localY;

        boolean canTeleport = false;
        String nameToShow = null;

        switch (map.packet.ptype) {
            case ProspectingPacket.MODE_BIG_ORES, ProspectingPacket.MODE_ALL_ORES -> {
                short objectId = getTopmostOreObjectId(localX, localY);
                if (objectId >= 0) {
                    nameToShow = map.packet.getObjectName(objectId);
                    canTeleport = !nameToShow.isEmpty();
                }
            }
            case ProspectingPacket.MODE_FLUIDS -> {
                int chunkX = localX / 16;
                int chunkZ = localY / 16;
                short objectId = map.packet.map.get(CoordinatePacker.pack(chunkX, 0, chunkZ));
                int amount = map.packet.getAmount(chunkX, chunkZ);
                if (objectId >= 0 && amount > 0) {
                    nameToShow = map.packet.getObjectName(objectId);
                    canTeleport = !nameToShow.isEmpty();
                }
            }
            default -> {}
        }

        if (canTeleport) {
            network.sendToServer(new TeleportRequestPacket(worldX, worldZ));
            mc.thePlayer.addChatMessage(
                new ChatComponentTranslation("item.gtnl.detrav_scanner.teleport_to_vein", nameToShow, worldX, worldZ));
            mc.thePlayer.closeScreen();
        }
    }

    private short getTopmostOreObjectId(int localX, int localY) {
        short foundObjectId = -1;
        int topY = Integer.MIN_VALUE;
        for (var entry : map.packet.map.long2ShortEntrySet()) {
            long packed = entry.getLongKey();
            if (CoordinatePacker.unpackX(packed) != localX || CoordinatePacker.unpackZ(packed) != localY) {
                continue;
            }
            int blockY = CoordinatePacker.unpackY(packed);
            if (blockY > topY) {
                topY = blockY;
                foundObjectId = entry.getShortValue();
            }
        }
        return foundObjectId;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        drawDefaultBackground();
        if (map == null) {
            return;
        }

        int currentWidth = Math.max(map.width, MIN_WIDTH);
        int currentHeight = Math.max(map.height, MIN_HEIGHT);
        int guiX = (width - currentWidth - 100) / 2;
        int guiY = (height - currentHeight) / 2;

        if (oresList == null || prevW != width || prevH != height) {
            oresList = new OresList(
                this,
                100,
                currentHeight,
                guiY,
                guiY + currentHeight,
                guiX + currentWidth,
                10,
                map.packet,
                (name, invert) -> {
                    if (map != null) {
                        map.loadTexture(null, name, invert);
                    }
                });
            prevW = width;
            prevH = height;
        }

        drawRect(guiX, guiY, guiX + currentWidth + 100, guiY + currentHeight, 0xFFC6C6C6);
        map.glBindTexture();
        map.draw(guiX, guiY);
        oresList.drawScreen(x, y, f);
        mc.getTextureManager()
            .bindTexture(BACKGROUND);
        GL11.glColor4f(0xFF, 0xFF, 0xFF, 0xFF);

        drawTexturedModalRect(guiX - 5, guiY - 5, 0, 0, 5, 5);
        drawTexturedModalRect(guiX + currentWidth + 100, guiY - 5, 171, 0, 5, 5);
        drawTexturedModalRect(guiX - 5, guiY + currentHeight, 0, 161, 5, 5);
        drawTexturedModalRect(guiX + currentWidth + 100, guiY + currentHeight, 171, 161, 5, 5);

        for (int i = guiX; i < guiX + currentWidth + 100; i += 128) {
            drawTexturedModalRect(i, guiY - 5, 5, 0, Math.min(128, guiX + currentWidth + 100 - i), 5);
            drawTexturedModalRect(i, guiY + currentHeight, 5, 161, Math.min(128, guiX + currentWidth + 100 - i), 5);
        }
        for (int i = guiY; i < guiY + currentHeight; i += 128) {
            drawTexturedModalRect(guiX - 5, i, 0, 5, 5, Math.min(128, guiY + currentHeight - i));
            drawTexturedModalRect(guiX + currentWidth + 100, i, 171, 5, 5, Math.min(128, guiY + currentHeight - i));
        }

        if (map.packet.ptype == ProspectingPacket.MODE_FLUIDS) {
            drawFluidTooltip(x, y, guiX, guiY);
        }
        if (map.packet.ptype == ProspectingPacket.MODE_POLLUTION) {
            drawPollutionTooltip(x, y, guiX, guiY);
        }
    }

    private void drawFluidTooltip(int mouseX, int mouseY, int guiX, int guiY) {
        int chunkX = (mouseX - guiX) / 16;
        int chunkZ = (mouseY - guiY) / 16;
        int chunkSpan = map.packet.size * 2 + 1;
        if (chunkX < 0 || chunkZ < 0 || chunkX >= chunkSpan || chunkZ >= chunkSpan) {
            return;
        }

        List<String> info = new ArrayList<>();
        short objectId = map.packet.map.getOrDefault(CoordinatePacker.pack(chunkX, 0, chunkZ), (short) -1);
        int amount = map.packet.getAmount(chunkX, chunkZ);
        if (objectId >= 0 && amount > 0) {
            info.add(
                StatCollector.translateToLocal("gui.detrav.scanner.tooltip.fluid_name")
                    + map.packet.getObjectName(objectId));
            info.add(
                StatCollector.translateToLocal("gui.detrav.scanner.tooltip.fluid_amount") + formatNumber(amount)
                    + " L");
        } else {
            info.add(StatCollector.translateToLocal("gui.detrav.scanner.tooltip.no_fluid"));
        }
        func_146283_a(info, mouseX, mouseY);
    }

    private void drawPollutionTooltip(int mouseX, int mouseY, int guiX, int guiY) {
        int chunkX = (mouseX - guiX) / 16;
        int chunkZ = (mouseY - guiY) / 16;
        int chunkSpan = map.packet.size * 2 + 1;
        if (chunkX < 0 || chunkZ < 0 || chunkX >= chunkSpan || chunkZ >= chunkSpan) {
            return;
        }

        int amount = map.packet.getAmount(chunkX, chunkZ);
        if (amount <= 0) {
            return;
        }

        List<String> info = new ArrayList<>();
        info.add(
            StatCollector.translateToLocal("gui.detrav.scanner.pollution") + ": "
                + formatNumber(amount)
                + GTUtility.trans("203", " gibbl"));
        func_146283_a(info, mouseX, mouseY);
    }
}
