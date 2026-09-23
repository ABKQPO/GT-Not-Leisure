package com.science.gtnl.utils.detrav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.packet.ProspectingPacket;

import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

@SideOnly(Side.CLIENT)
public class OresList extends GuiScrollingList {

    private final Object2IntOpenHashMap<String> colors = new Object2IntOpenHashMap<>();
    private final List<String> allKeys = new ArrayList<>();
    private final List<String> keys = new ArrayList<>();
    private final GuiScreen parent;
    private final BiConsumer<String, Boolean> onSelected;
    private boolean invert;
    private String selectedName;

    public OresList(GuiScreen parent, int width, int height, int top, int bottom, int left, int entryHeight,
        ProspectingPacket packet, BiConsumer<String, Boolean> onSelected) {
        super(parent.mc, width, height, top, bottom, left, entryHeight);
        this.parent = parent;
        this.onSelected = onSelected;

        allKeys.addAll(
            packet.objects.short2ObjectEntrySet()
                .stream()
                .map(
                    entry -> entry.getValue()
                        .left())
                .toList());
        Collections.sort(allKeys);

        if (packet.ptype == ProspectingPacket.MODE_POLLUTION) {
            allKeys.clear();
            allKeys.add(StatCollector.translateToLocal("gui.detrav.scanner.pollution"));
        } else if (allKeys.size() > 1) {
            allKeys.add(0, DetravMapTexture.ALL_ORES);
        }

        keys.addAll(allKeys);
        selectedName = keys.isEmpty() ? null : keys.get(0);

        for (var entry : packet.objects.short2ObjectEntrySet()) {
            colors.put(
                entry.getValue()
                    .left(),
                entry.getValue()
                    .rightInt());
        }
    }

    public void setFilter(String query) {
        keys.clear();
        if (query == null || query.trim()
            .isEmpty()) {
            keys.addAll(allKeys);
            return;
        }
        String lowerCaseQuery = query.toLowerCase();
        for (String key : allKeys) {
            if (key.toLowerCase()
                .contains(lowerCaseQuery)) {
                keys.add(key);
            }
        }
    }

    @Override
    protected int getSize() {
        return keys.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        if (index < 0 || index >= keys.size()) {
            return;
        }
        selectedName = keys.get(index);
        if (doubleClick) {
            invert = !invert;
        }
        if (onSelected != null) {
            onSelected.accept(selectedName, invert);
        }
    }

    @Override
    protected boolean isSelected(int index) {
        return selectedName != null && index >= 0 && index < keys.size() && selectedName.equals(keys.get(index));
    }

    @Override
    protected int getContentHeight() {
        // Floor at the viewport height so short (filtered) lists stay top-aligned instead of centring.
        return Math.max(super.getContentHeight(), bottom - top - 4);
    }

    @Override
    protected void drawBackground() {}

    @Override
    protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
        String name = keys.get(slotIdx);

        int textLeft = left + 3;
        int textColor = 0xFFE0E0E0;
        if (colors.containsKey(name)) {
            int color = 0xFF000000 | colors.getInt(name);
            Gui.drawRect(left + 3, slotTop, left + 11, slotTop + 8, color);
            textLeft = left + 14;
            textColor = readable(color);
        }

        parent.drawString(
            parent.mc.fontRenderer,
            parent.mc.fontRenderer.trimStringToWidth(name, listWidth - (textLeft - left) - 6),
            textLeft,
            slotTop - 1,
            textColor);
    }

    private static int readable(int argb) {
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        int luma = (red * 299 + green * 587 + blue * 114) / 1000;
        if (luma < 120) {
            float lift = (120 - luma) / 120f;
            red += (int) ((255 - red) * lift);
            green += (int) ((255 - green) * lift);
            blue += (int) ((255 - blue) * lift);
        }
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }
}
