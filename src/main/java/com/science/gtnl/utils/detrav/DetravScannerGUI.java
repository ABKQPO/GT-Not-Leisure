package com.science.gtnl.utils.detrav;

import static com.science.gtnl.ScienceNotLeisure.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.packet.TeleportRequestPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;

@SideOnly(Side.CLIENT)
public class DetravScannerGUI extends GuiScreen {

    public static DetravMapTexture map = null;
    public OresList oresList = null;

    public final static int minHeight = 128;
    public final static int minWidth = 128;
    public int prevW;
    public int prevH;

    public static final ResourceLocation back = new ResourceLocation("gregtech:textures/gui/propick.png");

    public DetravScannerGUI() {

    }

    public static void newMap(DetravMapTexture aMap) {
        if (map != null) {
            map.deleteGlTexture();
        }
        map = aMap;
        map.loadTexture(null);
    }

    public long lastClickTime = 0;
    public int lastClickX = -1, lastClickY = -1;

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
        if (map == null || map.packet == null) return;

        int currentWidth = Math.max(map.width, minWidth);
        int currentHeight = Math.max(map.height, minHeight);
        int aX = (this.width - currentWidth - 100) / 2;
        int aY = (this.height - currentHeight) / 2;

        int tX = mouseX - aX;
        int tY = mouseY - aY;

        if (tX < 0 || tY < 0 || tX >= map.width || tY >= map.height) return;

        int baseX = (map.packet.chunkX() - map.packet.size()) * 16;
        int baseZ = (map.packet.chunkZ() - map.packet.size()) * 16;

        int worldX = baseX + tX;
        int worldZ = baseZ + tY;

        int ptype = map.packet.ptype();
        Byte2ShortMap[][] grid = map.packet.map();
        if (grid == null || tX >= grid.length || tY >= grid[0].length) return;
        Byte2ShortMap cell = grid[tX][tY];

        boolean canTeleport = false;
        String nameToShow = null;

        if (cell != null) {
            if (ptype == 0 || ptype == 1) {
                for (short meta : cell.values()) {
                    if (meta != 0 && map.packet.metaMap()
                        .containsKey(meta)) {
                        nameToShow = map.packet.metaMap()
                            .get(meta);
                        canTeleport = true;
                        break;
                    }
                }
            } else if (ptype == 2) {
                short fluidId = cell.getOrDefault((byte) 1, (short) 0);
                short fluidAmount = cell.getOrDefault((byte) 2, (short) 0);
                if (fluidId != 0 && fluidAmount > 0) {
                    nameToShow = map.packet.metaMap()
                        .getOrDefault(fluidId, "Unknown Fluid");
                    canTeleport = true;
                }
            }
        }

        if (canTeleport) {
            network.sendToServer(new TeleportRequestPacket(worldX, worldZ));
            if (nameToShow != null) {
                mc.thePlayer
                    .addChatMessage(new ChatComponentTranslation("Info_DetravScanner_TP", nameToShow, worldX, worldZ));
            }
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        this.drawDefaultBackground();
        if (map == null) return;
        int currentWidth = Math.max(map.width, minWidth);
        int currentHeight = Math.max(map.height, minHeight);
        int aX = (this.width - currentWidth - 100) / 2;
        int aY = (this.height - currentHeight) / 2;

        if (oresList == null || (prevW != width || prevH != height)) {
            oresList = new OresList(
                this,
                100,
                currentHeight,
                aY,
                aY + currentHeight,
                aX + currentWidth,
                10,
                map.packet.ores(),
                ((name, invert) -> { if (map != null) map.loadTexture(null, name, invert); }));
            prevW = width;
            prevH = height;
        }

        // gtnl$draw back for ores
        drawRect(aX, aY, aX + currentWidth + 100, aY + currentHeight, 0xFFC6C6C6);
        map.glBindTexture();
        map.draw(aX, aY);
        oresList.drawScreen(x, y, f);
        mc.getTextureManager()
            .bindTexture(back);
        GL11.glColor4f(0xFF, 0xFF, 0xFF, 0xFF);

        // gtnl$draw corners
        drawTexturedModalRect(aX - 5, aY - 5, 0, 0, 5, 5);// leftTop
        drawTexturedModalRect(aX + currentWidth + 100, aY - 5, 171, 0, 5, 5);// RightTop
        drawTexturedModalRect(aX - 5, aY + currentHeight, 0, 161, 5, 5);// leftDown
        drawTexturedModalRect(aX + currentWidth + 100, aY + currentHeight, 171, 161, 5, 5);// RightDown

        // gtnl$draw edges
        for (int i = aX; i < aX + currentWidth + 100; i += 128)
            drawTexturedModalRect(i, aY - 5, 5, 0, Math.min(128, aX + currentWidth + 100 - i), 5); // top
        for (int i = aX; i < aX + currentWidth + 100; i += 128)
            drawTexturedModalRect(i, aY + currentHeight, 5, 161, Math.min(128, aX + currentWidth + 100 - i), 5); // down
        for (int i = aY; i < aY + currentHeight; i += 128)
            drawTexturedModalRect(aX - 5, i, 0, 5, 5, Math.min(128, aY + currentHeight - i)); // left
        for (int i = aY; i < aY + currentHeight; i += 128)
            drawTexturedModalRect(aX + currentWidth + 100, i, 171, 5, 5, Math.min(128, aY + currentHeight - i)); // right

        if (map.packet.ptype() == 2) {
            Byte2ShortMap[][] fluidInfo = map.packet.map();
            int tX = x - aX;
            int tY = y - aY;
            if (tX >= 0 && tY >= 0 && tX < fluidInfo.length && tY < fluidInfo[0].length) {
                List<String> info = new ArrayList<>();
                if (fluidInfo[tX][tY] != null) {
                    short fluidId = fluidInfo[tX][tY].get((byte) 1);
                    short fluidAmount = fluidInfo[tX][tY].get((byte) 2);
                    if (fluidId != 0 && fluidAmount > 0) {
                        info.add(
                            StatCollector.translateToLocal("gui.detrav.scanner.tooltip.fluid_name")
                                + map.packet.metaMap()
                                    .get(fluidId));
                        info.add(
                            StatCollector.translateToLocal("gui.detrav.scanner.tooltip.fluid_amount")
                                + GTUtility.formatNumbers(fluidAmount)
                                + " L");
                    } else info.add(StatCollector.translateToLocal("gui.detrav.scanner.tooltip.no_fluid"));
                } else {
                    info.add(StatCollector.translateToLocal("gui.detrav.scanner.tooltip.no_fluid"));
                }
                func_146283_a(info, x, y);
            }
        }
    }

}
