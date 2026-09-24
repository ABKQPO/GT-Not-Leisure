package com.science.gtnl.utils.detrav;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static com.science.gtnl.ScienceNotLeisure.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.science.gtnl.common.packet.ProspectingPacket;
import com.science.gtnl.common.packet.TeleportRequestPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DetravScannerGUI extends GuiScreen {

    private static final int MIN_HEIGHT = 128;
    private static final int MIN_WIDTH = 128;
    private static final int SEARCH_HEIGHT = 14;
    private static final int HINT_HEIGHT = 5;
    private static final int SCROLLBAR_THICKNESS = 4;
    private static final int ORE_SEARCH_RADIUS = 5;
    private static final int DOUBLE_CLICK_MILLIS = 300;
    private static final int DOUBLE_CLICK_SLOP = 5;
    private static final int MAX_LIST_WIDTH = 220;
    private static final float MIN_ZOOM = 0.1F;
    private static final float MAX_ZOOM = 4.0F;
    private static final float ZOOM_LERP = 0.22F;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("gregtech:textures/gui/propick.png");
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("gui.button.press");

    private static DetravMapTexture map;

    private OresList oresList;
    private GuiTextField searchField;

    private int panelX;
    private int panelY;
    private int mapWidth;
    private int mapHeight;
    private int listX;
    private int listWidth;
    private int viewWidth;
    private int viewHeight;
    private int viewportX;
    private int viewportY;
    private float panX;
    private float panY;
    private float targetPanX;
    private float targetPanY;
    private float maxPanX;
    private float maxPanY;
    private float zoom = 1F;
    private float targetZoom = 1F;
    private float zoomAnchorMapX;
    private float zoomAnchorMapZ;
    private int zoomAnchorScreenX;
    private int zoomAnchorScreenY;
    private boolean zoomAnchorActive;
    private boolean panInitialised;
    private int dragMode;
    private int dragLastX;
    private int dragLastY;
    private boolean leftMouseDown;
    private boolean wasRightDown;
    private int frameTextureWidth;
    private int frameTextureHeight;
    private int nearestOreX;
    private int nearestOreZ;
    private String searchText = "";
    private long lastClickTime;
    private int lastClickX = -1;
    private int lastClickY = -1;

    public static void newMap(DetravMapTexture newMap) {
        if (map != null) {
            map.deleteGlTexture();
        }
        map = newMap;
        map.loadTexture(null);
    }

    @Override
    public void initGui() {
        allowUserInput = true;
        Keyboard.enableRepeatEvents(true);
        if (map == null) {
            return;
        }

        // Size the list column to the longest entry, clamped so the map keeps a usable viewport.
        int listNamesWidth = 100;
        for (var entry : map.packet.objects.short2ObjectEntrySet()) {
            listNamesWidth = Math.max(
                listNamesWidth,
                fontRendererObj.getStringWidth(
                    entry.getValue()
                        .left())
                    + 24);
        }
        listWidth = Math.min(listNamesWidth, MAX_LIST_WIDTH);

        // The map keeps its full 1:1 scale inside a viewport; when the scan is larger than the window the viewport
        // scrolls instead of shrinking the map.
        viewWidth = Math.min(map.width, Math.max(MIN_WIDTH, width - listWidth - 12));
        viewHeight = Math.min(map.height, Math.max(MIN_HEIGHT, height - 12));
        maxPanX = maxPanForZoom(zoom, map.width, viewWidth);
        maxPanY = maxPanForZoom(zoom, map.height, viewHeight);

        mapWidth = Math.max(viewWidth, MIN_WIDTH);
        mapHeight = Math.max(viewHeight, MIN_HEIGHT);

        panelX = (width - mapWidth - listWidth) / 2;
        panelY = (height - mapHeight) / 2;
        listX = panelX + mapWidth;

        viewportX = panelX + (mapWidth - viewWidth) / 2;
        viewportY = panelY + (mapHeight - viewHeight) / 2;

        if (!panInitialised) {
            targetPanX = map.packet.posX - (map.packet.chunkX - map.packet.size) * 16 - 1 - viewWidth / 2F;
            targetPanY = map.packet.posZ - (map.packet.chunkZ - map.packet.size) * 16 - 1 - viewHeight / 2F;
            panX = targetPanX;
            panY = targetPanY;
            panInitialised = true;
        }
        targetPanX = clamp(targetPanX, 0, maxPanForZoom(targetZoom, map.width, viewWidth));
        targetPanY = clamp(targetPanY, 0, maxPanForZoom(targetZoom, map.height, viewHeight));
        panX = clamp(panX, 0, maxPanX);
        panY = clamp(panY, 0, maxPanY);

        searchField = new GuiTextField(fontRendererObj, listX + 1, panelY, listWidth - 2, SEARCH_HEIGHT);
        searchField.setMaxStringLength(64);
        searchField.setText(searchText);

        int listTop = panelY + SEARCH_HEIGHT + 1;
        int listBottom = panelY + mapHeight - HINT_HEIGHT;
        oresList = new OresList(
            this,
            listWidth,
            listBottom - listTop,
            listTop,
            listBottom,
            listX,
            10,
            map.packet,
            (name, invert) -> {
                if (map != null) {
                    map.loadTexture(null, name, invert);
                }
            });
        oresList.setFilter(searchText);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        dragMode = 0;
        leftMouseDown = false;
        wasRightDown = false;
    }

    @Override
    public void updateScreen() {
        if (searchField != null) {
            searchField.updateCursorCounter();
        }
    }

    @Override
    public void handleMouseInput() {
        clearReleasedMapDrag();

        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            handleMapWheel(wheel);
        }

        super.handleMouseInput();

        clearReleasedMapDrag();
    }

    public boolean handleMapWheel(int wheel) {
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        return handleMapWheel(wheel, mouseX, mouseY);
    }

    public boolean handleMapWheel(int wheel, int mouseX, int mouseY) {
        if (map == null || wheel == 0) return false;

        if (!inViewport(mouseX, mouseY)) {
            return false;
        }

        float mapX = mapCoordinateX(mouseX, zoom, panX);
        float mapZ = mapCoordinateZ(mouseY, zoom, panY);
        float previousTargetZoom = targetZoom;
        targetZoom = clampZoom(targetZoom + (wheel > 0 ? 0.15F : -0.15F));
        if (targetZoom == previousTargetZoom) {
            return false;
        }
        targetPanX = mapX - (mouseX - mapOriginX(targetZoom)) / targetZoom;
        targetPanY = mapZ - (mouseY - mapOriginY(targetZoom)) / targetZoom;
        zoomAnchorMapX = mapX;
        zoomAnchorMapZ = mapZ;
        zoomAnchorScreenX = mouseX;
        zoomAnchorScreenY = mouseY;
        zoomAnchorActive = true;
        clampTargetPan();
        return true;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (searchField != null) {
            searchField.mouseClicked(mouseX, mouseY, button);
        }
        if (button == 0) {
            leftMouseDown = true;
            beginMapDrag(mouseX, mouseY);
            long now = System.currentTimeMillis();
            if (now - lastClickTime < DOUBLE_CLICK_MILLIS && Math.abs(mouseX - lastClickX) < DOUBLE_CLICK_SLOP
                && Math.abs(mouseY - lastClickY) < DOUBLE_CLICK_SLOP) {
                onMapDoubleClick(mouseX, mouseY);
            }
            lastClickTime = now;
            lastClickX = mouseX;
            lastClickY = mouseY;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        // GuiScreen reports wheel events as mouse movement while a button is held. Do not let a scroll event
        // move the map as a side effect of zooming.
        if (clickedMouseButton == 0 && Mouse.getEventDWheel() == 0) {
            updateMapDrag(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        if (button == 0) {
            leftMouseDown = false;
            dragMode = 0;
        }
    }

    private void onMapDoubleClick(int mouseX, int mouseY) {
        if (map == null || map.packet == null || !inViewport(mouseX, mouseY)) {
            return;
        }

        int localX = mapCoordinateX(mouseX);
        int localY = mapCoordinateZ(mouseY);
        if (localX < 0 || localY < 0 || localX >= map.width || localY >= map.height) {
            return;
        }

        String nameToShow = null;
        switch (map.packet.ptype) {
            case ProspectingPacket.MODE_BIG_ORES, ProspectingPacket.MODE_ALL_ORES -> {
                String name = map.getTopOreName(localX, localY);
                if (name != null && !name.isEmpty()) {
                    nameToShow = name;
                }
            }
            case ProspectingPacket.MODE_FLUIDS -> {
                int chunkX = localX / 16;
                int chunkZ = localY / 16;
                int amount = map.packet.getAmount(chunkX, chunkZ);
                short objectId = map.packet.map.get(CoordinatePacker.pack(chunkX, 0, chunkZ));
                var object = map.packet.objects.get(objectId);
                if (object != null && amount > 0) {
                    nameToShow = object.left();
                }
            }
            default -> {}
        }

        if (nameToShow == null) {
            return;
        }

        int worldX = localX + (map.packet.chunkX - map.packet.size) * 16;
        int worldZ = localY + (map.packet.chunkZ - map.packet.size) * 16;
        network.sendToServer(new TeleportRequestPacket(worldX, worldZ));
        mc.thePlayer.addChatMessage(
            new ChatComponentTranslation("item.gtnl.detrav_scanner.teleport_to_vein", nameToShow, worldX, worldZ));
        mc.thePlayer.closeScreen();
    }

    private void pollMarkerClick(int mouseX, int mouseY) {
        boolean down = Mouse.isButtonDown(1);
        if (down && !wasRightDown) {
            tryToggleMarker(mouseX, mouseY);
        }
        wasRightDown = down;
    }

    private void tryToggleMarker(int mouseX, int mouseY) {
        if (map == null || !inViewport(mouseX, mouseY)) {
            return;
        }
        if (map.packet.ptype != ProspectingPacket.MODE_BIG_ORES
            && map.packet.ptype != ProspectingPacket.MODE_ALL_ORES) {
            return;
        }
        if (!findNearestOre(mouseX, mouseY)) {
            return;
        }

        int worldX = nearestOreX + (map.packet.chunkX - map.packet.size) * 16;
        int worldZ = nearestOreZ + (map.packet.chunkZ - map.packet.size) * 16;

        DetravOreMarkerRenderer.toggleMarker(
            mc.thePlayer.dimension,
            worldX,
            map.getTopOreY(nearestOreX, nearestOreZ),
            worldZ,
            map.getTopOreName(nearestOreX, nearestOreZ),
            map.getTopOreColor(nearestOreX, nearestOreZ),
            map.getTopOreMaterialName(nearestOreX, nearestOreZ));

        mc.getSoundHandler()
            .playSound(PositionedSoundRecord.func_147674_a(CLICK_SOUND, 1.0F));
    }

    private boolean findNearestOre(int mouseX, int mouseY) {
        if (map == null) {
            return false;
        }

        int blockX = mapCoordinateX(mouseX);
        int blockZ = mapCoordinateZ(mouseY);

        int bestDistance = Integer.MAX_VALUE;
        boolean found = false;
        for (int dz = -ORE_SEARCH_RADIUS; dz <= ORE_SEARCH_RADIUS; dz++) {
            for (int dx = -ORE_SEARCH_RADIUS; dx <= ORE_SEARCH_RADIUS; dx++) {
                int columnX = blockX + dx;
                int columnZ = blockZ + dz;
                if (map.getTopOreName(columnX, columnZ) == null) {
                    continue;
                }
                int distance = dx * dx + dz * dz;
                if (distance < bestDistance) {
                    bestDistance = distance;
                    nearestOreX = columnX;
                    nearestOreZ = columnZ;
                    found = true;
                }
            }
        }
        return found;
    }

    private void beginMapDrag(int mouseX, int mouseY) {
        if (map == null) {
            dragMode = 0;
            return;
        }

        boolean overVertical = overVerticalScrollbar(mouseX, mouseY);
        boolean overHorizontal = overHorizontalScrollbar(mouseX, mouseY);
        boolean inMapViewport = inViewport(mouseX, mouseY);
        if (overVertical || overHorizontal || inMapViewport) {
            cancelZoomAnimation();
        }

        if (overVertical) {
            dragMode = 3;
        } else if (overHorizontal) {
            dragMode = 2;
        } else if (inMapViewport && (maxPanX > 0 || maxPanY > 0)) {
            dragMode = 1;
        } else {
            dragMode = 0;
        }
        dragLastX = mouseX;
        dragLastY = mouseY;
    }

    private void updateMapDrag(int mouseX, int mouseY) {
        if (dragMode == 0) {
            return;
        }
        if (!leftMouseDown || !Mouse.isButtonDown(0)) {
            leftMouseDown = false;
            dragMode = 0;
            return;
        }

        if (zoomAnchorActive) {
            cancelZoomAnimation();
        }

        if (dragMode == 1) {
            targetPanX = clamp(targetPanX - (mouseX - dragLastX) / zoom, 0, maxPanX);
            targetPanY = clamp(targetPanY - (mouseY - dragLastY) / zoom, 0, maxPanY);
            panX = targetPanX;
            panY = targetPanY;
            dragLastX = mouseX;
            dragLastY = mouseY;
        } else if (dragMode == 2) {
            float trackWidth = viewWidth - (maxPanY > 0 ? SCROLLBAR_THICKNESS : 0);
            float thumbWidth = Math.max(12, trackWidth * viewWidth / (map.width * zoom));
            float trackRange = Math.max(1F, trackWidth - thumbWidth);
            targetPanX = clamp((mouseX - viewportX - thumbWidth / 2F) / trackRange * maxPanX, 0, maxPanX);
            panX = targetPanX;
        } else {
            float trackHeight = viewHeight - (maxPanX > 0 ? SCROLLBAR_THICKNESS : 0);
            float thumbHeight = Math.max(12, trackHeight * viewHeight / (map.height * zoom));
            float trackRange = Math.max(1F, trackHeight - thumbHeight);
            targetPanY = clamp(
                (mouseY - viewportY - thumbHeight / 2F) / trackRange * maxPanForZoom(zoom, map.height, viewHeight),
                0,
                maxPanY);
            panY = targetPanY;
        }
    }

    private boolean overVerticalScrollbar(int mouseX, int mouseY) {
        return maxPanY > 0 && mouseX >= viewportX + viewWidth - SCROLLBAR_THICKNESS
            && mouseX < viewportX + viewWidth
            && mouseY >= viewportY
            && mouseY < viewportY + viewHeight;
    }

    private boolean overHorizontalScrollbar(int mouseX, int mouseY) {
        return maxPanX > 0 && mouseY >= viewportY + viewHeight - SCROLLBAR_THICKNESS
            && mouseY < viewportY + viewHeight
            && mouseX >= viewportX
            && mouseX < viewportX + viewWidth;
    }

    private boolean inViewport(int mouseX, int mouseY) {
        return mouseX >= viewportX && mouseX < viewportX + viewWidth
            && mouseY >= viewportY
            && mouseY < viewportY + viewHeight;
    }

    private void updateMapTransform() {
        targetZoom = clampZoom(targetZoom);
        zoom += (targetZoom - zoom) * ZOOM_LERP;
        if (Math.abs(targetZoom - zoom) < 0.001F) {
            zoom = targetZoom;
        }
        maxPanX = maxPanForZoom(zoom, map.width, viewWidth);
        maxPanY = maxPanForZoom(zoom, map.height, viewHeight);
        clampTargetPan();

        if (zoomAnchorActive) {
            if (zoom == targetZoom) {
                panX = targetPanX;
                panY = targetPanY;
                zoomAnchorActive = false;
                return;
            }

            panX = clamp(zoomAnchorMapX - (zoomAnchorScreenX - mapOriginX(zoom)) / zoom, 0, maxPanX);
            panY = clamp(zoomAnchorMapZ - (zoomAnchorScreenY - mapOriginY(zoom)) / zoom, 0, maxPanY);
            return;
        }

        panX += (targetPanX - panX) * ZOOM_LERP;
        panY += (targetPanY - panY) * ZOOM_LERP;
        if (Math.abs(targetPanX - panX) < 0.01F) panX = targetPanX;
        if (Math.abs(targetPanY - panY) < 0.01F) panY = targetPanY;
        panX = clamp(panX, 0, maxPanX);
        panY = clamp(panY, 0, maxPanY);
    }

    private void cancelZoomAnimation() {
        if (!zoomAnchorActive && zoom == targetZoom) {
            return;
        }
        zoomAnchorActive = false;
        targetZoom = zoom;
        targetPanX = panX;
        targetPanY = panY;
    }

    private void clearReleasedMapDrag() {
        if (dragMode != 0 && (!leftMouseDown || !Mouse.isButtonDown(0))) {
            leftMouseDown = false;
            dragMode = 0;
        }
    }

    private void clampTargetPan() {
        targetPanX = clamp(targetPanX, 0, maxPanForZoom(targetZoom, map.width, viewWidth));
        targetPanY = clamp(targetPanY, 0, maxPanForZoom(targetZoom, map.height, viewHeight));
    }

    private float clampZoom(float value) {
        return clamp(value, minimumZoom(), MAX_ZOOM);
    }

    private float minimumZoom() {
        if (map == null || map.width <= 0 || map.height <= 0) return MIN_ZOOM;
        return Math.max(MIN_ZOOM, Math.max(viewWidth / (float) map.width, viewHeight / (float) map.height));
    }

    private float mapOriginX(float scale) {
        return viewportX + Math.max(0F, (viewWidth - map.width * scale) / 2F);
    }

    private float mapOriginY(float scale) {
        return viewportY + Math.max(0F, (viewHeight - map.height * scale) / 2F);
    }

    private float mapCoordinateX(int mouseX, float scale, float offset) {
        return (mouseX - mapOriginX(scale)) / scale + offset;
    }

    private float mapCoordinateZ(int mouseY, float scale, float offset) {
        return (mouseY - mapOriginY(scale)) / scale + offset;
    }

    private int mapCoordinateX(int mouseX) {
        return Math.round(mapCoordinateX(mouseX, zoom, panX));
    }

    private int mapCoordinateZ(int mouseY) {
        return Math.round(mapCoordinateZ(mouseY, zoom, panY));
    }

    private int mapScreenX(float mapX) {
        return Math.round(mapOriginX(zoom) + (mapX - panX) * zoom);
    }

    private int mapScreenZ(float mapZ) {
        return Math.round(mapOriginY(zoom) + (mapZ - panY) * zoom);
    }

    private static float maxPanForZoom(float scale, int mapSize, int viewSize) {
        return Math.max(0F, mapSize - viewSize / scale);
    }

    private static int clamp(int value, int minimum, int maximum) {
        return value < minimum ? minimum : Math.min(value, maximum);
    }

    private static float clamp(float value, float minimum, float maximum) {
        return value < minimum ? minimum : Math.min(value, maximum);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (searchField != null && searchField.isFocused() && keyCode != Keyboard.KEY_ESCAPE) {
            if (searchField.textboxKeyTyped(typedChar, keyCode)) {
                searchText = searchField.getText();
                if (oresList != null) {
                    oresList.setFilter(searchText);
                }
            }
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        if (map == null || oresList == null) {
            return;
        }

        if (dragMode != 0 && (!leftMouseDown || !Mouse.isButtonDown(0))) {
            leftMouseDown = false;
            dragMode = 0;
        }
        updateMapTransform();
        pollMarkerClick(mouseX, mouseY);

        drawRect(panelX, panelY, panelX + mapWidth + listWidth, panelY + mapHeight, 0xFFC6C6C6);

        ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int scaleFactor = resolution.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            viewportX * scaleFactor,
            mc.displayHeight - (viewportY + viewHeight) * scaleFactor,
            viewWidth * scaleFactor,
            viewHeight * scaleFactor);

        map.glBindTexture();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        map.draw(mapOriginX(zoom) - panX * zoom, mapOriginY(zoom) - panY * zoom, zoom);
        drawPlayerDirectionMarker();
        drawMarkerOverlays();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        fontRendererObj.drawStringWithShadow("N", viewportX + viewWidth / 2 - 2, viewportY + 1, 0xFFFFFFFF);

        drawMapScrollbars();

        searchField.drawTextBox();
        if (searchField.getText()
            .isEmpty() && !searchField.isFocused()) {
            fontRendererObj.drawString(
                StatCollector.translateToLocal("gui.detrav.scanner.search.hint"),
                listX + 4,
                panelY + 3,
                0xFF808080);
        }

        // Feed an out-of-bounds Y while dragging so the list does not latch and scroll along with the map.
        oresList.drawScreen(mouseX, dragMode != 0 ? -1 : mouseY, partialTicks);
        drawInvertHint();
        drawFrame();
        drawHoverTooltip(mouseX, mouseY);
    }

    private void drawInvertHint() {
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 1F);
        String hint = fontRendererObj.trimStringToWidth(
            EnumChatFormatting.ITALIC + StatCollector.translateToLocal("gui.detrav.scanner.hint.invert"),
            (listWidth - 4) * 2);
        int hintX = (listX + listWidth - 2) * 2 - fontRendererObj.getStringWidth(hint);
        int hintY = (panelY + mapHeight - HINT_HEIGHT) * 2 + (HINT_HEIGHT * 2 - 8) / 2;
        fontRendererObj.drawString(hint, hintX, hintY, 0xFF5E5E5E);
        GL11.glPopMatrix();
    }

    private void drawFrame() {
        mc.getTextureManager()
            .bindTexture(BACKGROUND);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        if (frameTextureWidth <= 0) {
            frameTextureWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            frameTextureHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        }

        int frameWidth = mapWidth + listWidth;
        drawFramePiece(panelX - 5, panelY - 5, 0, 0, 5, 5);
        drawFramePiece(panelX + frameWidth, panelY - 5, 171, 0, 5, 5);
        drawFramePiece(panelX - 5, panelY + mapHeight, 0, 161, 5, 5);
        drawFramePiece(panelX + frameWidth, panelY + mapHeight, 171, 161, 5, 5);

        for (int x = panelX; x < panelX + frameWidth; x += 128) {
            int pieceWidth = Math.min(128, panelX + frameWidth - x);
            drawFramePiece(x, panelY - 5, 5, 0, pieceWidth, 5);
            drawFramePiece(x, panelY + mapHeight, 5, 161, pieceWidth, 5);
        }
        for (int y = panelY; y < panelY + mapHeight; y += 128) {
            int pieceHeight = Math.min(128, panelY + mapHeight - y);
            drawFramePiece(panelX - 5, y, 0, 5, 5, pieceHeight);
            drawFramePiece(panelX + frameWidth, y, 171, 5, 5, pieceHeight);
        }
    }

    private void drawFramePiece(int x, int y, int u, int v, int pieceWidth, int pieceHeight) {
        float textureWidth = 1F / (frameTextureWidth > 0 ? frameTextureWidth : 256);
        float textureHeight = 1F / (frameTextureHeight > 0 ? frameTextureHeight : 256);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + pieceHeight, zLevel, u * textureWidth, (v + pieceHeight) * textureHeight);
        tessellator.addVertexWithUV(
            x + pieceWidth,
            y + pieceHeight,
            zLevel,
            (u + pieceWidth) * textureWidth,
            (v + pieceHeight) * textureHeight);
        tessellator.addVertexWithUV(x + pieceWidth, y, zLevel, (u + pieceWidth) * textureWidth, v * textureHeight);
        tessellator.addVertexWithUV(x, y, zLevel, u * textureWidth, v * textureHeight);
        tessellator.draw();
    }

    private void drawHoverTooltip(int mouseX, int mouseY) {
        if (!inViewport(mouseX, mouseY)) {
            return;
        }

        int blockX = mapCoordinateX(mouseX);
        int blockZ = mapCoordinateZ(mouseY);

        switch (map.packet.ptype) {
            case ProspectingPacket.MODE_BIG_ORES, ProspectingPacket.MODE_ALL_ORES -> {
                if (!findNearestOre(mouseX, mouseY)) {
                    return;
                }
                List<String> lines = new ArrayList<>();
                lines.add(map.getTopOreName(nearestOreX, nearestOreZ));
                lines.add(
                    StatCollector.translateToLocalFormatted(
                        "gui.detrav.scanner.tooltip.ore_pos",
                        nearestOreX + (map.packet.chunkX - map.packet.size) * 16,
                        nearestOreZ + (map.packet.chunkZ - map.packet.size) * 16));
                lines.add(
                    StatCollector.translateToLocalFormatted(
                        "gui.detrav.scanner.tooltip.ore_depth",
                        map.getTopOreY(nearestOreX, nearestOreZ)));
                lines.add(
                    EnumChatFormatting.DARK_GRAY
                        + StatCollector.translateToLocal("gui.detrav.scanner.tooltip.mark_hint"));
                func_146283_a(lines, mouseX, mouseY);
            }
            case ProspectingPacket.MODE_FLUIDS -> {
                if (!inScanArea(blockX, blockZ)) {
                    return;
                }
                int chunkX = blockX / 16;
                int chunkZ = blockZ / 16;
                List<String> lines = new ArrayList<>();
                short objectId = map.packet.map.getOrDefault(CoordinatePacker.pack(chunkX, 0, chunkZ), (short) -1);
                int amount = map.packet.getAmount(chunkX, chunkZ);
                var object = map.packet.objects.get(objectId);

                if (object != null && amount > 0) {
                    lines.add(
                        StatCollector
                            .translateToLocalFormatted("gui.detrav.scanner.tooltip.fluid_name", object.left()));
                    lines.add(
                        StatCollector.translateToLocalFormatted(
                            "gui.detrav.scanner.tooltip.fluid_amount",
                            formatNumber(amount)));
                } else {
                    lines.add(StatCollector.translateToLocal("gui.detrav.scanner.tooltip.no_fluid"));
                }
                func_146283_a(lines, mouseX, mouseY);
            }
            case ProspectingPacket.MODE_POLLUTION -> {
                if (!inScanArea(blockX, blockZ)) {
                    return;
                }
                int amount = map.packet.getAmount(blockX / 16, blockZ / 16);
                if (amount <= 0) {
                    return;
                }
                List<String> lines = new ArrayList<>();
                lines.add(
                    StatCollector.translateToLocal("gui.detrav.scanner.pollution") + ": "
                        + formatNumber(amount)
                        + " "
                        + StatCollector.translateToLocal("gui.detrav.scanner.gibbl"));
                func_146283_a(lines, mouseX, mouseY);
            }
            default -> {}
        }
    }

    private boolean inScanArea(int blockX, int blockZ) {
        int span = map.packet.getSize();
        return blockX >= 0 && blockZ >= 0 && blockX < span && blockZ < span;
    }

    private void drawMapScrollbars() {
        if (maxPanX > 0 && maxPanY > 0) {
            drawRect(
                viewportX + viewWidth - SCROLLBAR_THICKNESS,
                viewportY + viewHeight - SCROLLBAR_THICKNESS,
                viewportX + viewWidth,
                viewportY + viewHeight,
                0x80000000);
        }
        if (maxPanX > 0) {
            int trackWidth = viewWidth - (maxPanY > 0 ? SCROLLBAR_THICKNESS : 0);
            int top = viewportY + viewHeight - SCROLLBAR_THICKNESS;
            drawRect(viewportX, top, viewportX + trackWidth, top + SCROLLBAR_THICKNESS, 0x80000000);
            int thumbWidth = Math.max(12, (int) (trackWidth * viewWidth / (map.width * zoom)));
            int thumbX = viewportX + (int) ((trackWidth - thumbWidth) * panX / maxPanX);
            drawRect(thumbX, top, thumbX + thumbWidth, top + SCROLLBAR_THICKNESS, 0xFFB0B0B0);
        }
        if (maxPanY > 0) {
            int trackHeight = viewHeight - (maxPanX > 0 ? SCROLLBAR_THICKNESS : 0);
            int left = viewportX + viewWidth - SCROLLBAR_THICKNESS;
            drawRect(left, viewportY, left + SCROLLBAR_THICKNESS, viewportY + trackHeight, 0x80000000);
            int thumbHeight = Math.max(12, (int) (trackHeight * viewHeight / (map.height * zoom)));
            int thumbY = viewportY + (int) ((trackHeight - thumbHeight) * panY / maxPanY);
            drawRect(left, thumbY, left + SCROLLBAR_THICKNESS, thumbY + thumbHeight, 0xFFB0B0B0);
        }
    }

    private void drawPlayerDirectionMarker() {
        if (mc.thePlayer == null) {
            return;
        }

        int playerX = map.packet.posX - (map.packet.chunkX - map.packet.size) * 16 - 1;
        int playerZ = map.packet.posZ - (map.packet.chunkZ - map.packet.size) * 16 - 1;
        if (playerX < 0 || playerZ < 0 || playerX >= map.width || playerZ >= map.height) {
            return;
        }

        drawPlayerHeading(mapScreenX(playerX), mapScreenZ(playerZ), mc.thePlayer.rotationYaw);
    }

    private void drawMarkerOverlays() {
        if (map.packet.ptype != ProspectingPacket.MODE_BIG_ORES
            && map.packet.ptype != ProspectingPacket.MODE_ALL_ORES) {
            return;
        }

        int dimensionId = mc.thePlayer.dimension;
        int originX = (map.packet.chunkX - map.packet.size) * 16;
        int originZ = (map.packet.chunkZ - map.packet.size) * 16;

        for (DetravOreMarker marker : DetravOreMarkerRenderer.getMarkers()) {
            if (marker.dimensionId != dimensionId) {
                continue;
            }
            int cellX = marker.x - originX;
            int cellZ = marker.z - originZ;
            if (cellX < 0 || cellZ < 0 || cellX >= map.width || cellZ >= map.height) {
                continue;
            }
            int centreX = mapScreenX(cellX + 0.5F);
            int centreZ = mapScreenZ(cellZ + 0.5F);
            int outerSide = Math.max(2, Math.round(5F * zoom));
            int innerSide = Math.max(1, Math.round(3F * zoom));
            drawCenteredHollowRect(centreX, centreZ, outerSide, 0xFF000000);
            drawCenteredHollowRect(centreX, centreZ, innerSide, 0xFFFFD700);
        }
    }

    private void drawCenteredHollowRect(int centreX, int centreY, int side, int color) {
        drawHollowRect(Math.round(centreX - side / 2F), Math.round(centreY - side / 2F), side, color);
    }

    private void drawHollowRect(int x, int y, int side, int color) {
        drawRect(x, y, x + side, y + 1, color);
        drawRect(x, y + side - 1, x + side, y + side, color);
        drawRect(x, y + 1, x + 1, y + side - 1, color);
        drawRect(x + side - 1, y + 1, x + side, y + side - 1, color);
    }

    private void drawPlayerHeading(int centreX, int centreY, float yaw) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(centreX + 0.5F, centreY + 0.5F, 0F);
        GL11.glRotatef(yaw + 180F, 0F, 0F, 1F);

        Tessellator tessellator = Tessellator.instance;
        // Teardrop perimeter: round bottom, sharp tip at the top.
        float[][] body = { { 6.06F, -1.5F }, { 7F, 2F }, { 6F, 5F }, { 3F, 8F }, { 0F, 9F }, { -3F, 8F }, { -6F, 5F },
            { -7F, 2F }, { -6.06F, -1.5F }, { 0F, -14F } };

        fanShape(tessellator, body, 0F, 2F, 0.9F, 0xEDD8FF);
        fanShape(tessellator, body, 0F, 2F, 0.75F, 0xC071FF);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(1.5F);
        strokeShape(tessellator, body, 0F, 2F, 0.9F, 0xEDD8FF);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private static void fanShape(Tessellator tessellator, float[][] points, float centreX, float centreY, float scale,
        int color) {
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        tessellator.setColorOpaque_I(color);
        tessellator.addVertex(centreX, centreY, 0);
        for (int index = 0; index <= points.length; index++) {
            float[] point = points[index % points.length];
            tessellator.addVertex(centreX + (point[0] - centreX) * scale, centreY + (point[1] - centreY) * scale, 0);
        }
        tessellator.draw();
    }

    private static void strokeShape(Tessellator tessellator, float[][] points, float centreX, float centreY,
        float scale, int color) {
        tessellator.startDrawing(GL11.GL_LINE_LOOP);
        tessellator.setColorOpaque_I(color);
        for (float[] point : points) {
            tessellator.addVertex(centreX + (point[0] - centreX) * scale, centreY + (point[1] - centreY) * scale, 0);
        }
        tessellator.draw();
    }
}
