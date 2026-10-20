package com.science.gtnl.client.celestweave;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;

import tconstruct.client.tabs.AbstractTab;
import tconstruct.client.tabs.TabRegistry;

public class CelestweaveBackpackTicCompat {

    private static final ItemStack FALLBACK = new ItemStack(Blocks.chest);

    private CelestweaveBackpackTicCompat() {}

    static void register() {
        TabRegistry.registerTab(new Tab());
    }

    @SuppressWarnings("unchecked")
    static void add(int left, int top, List<?> buttons) {
        TabRegistry.updateTabValues(left, top, Tab.class);
        TabRegistry.addTabsToList((List<GuiButton>) buttons);
    }

    public static class Tab extends AbstractTab {

        Tab() {
            super(0, 0, 0, FALLBACK);
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            ItemStack pack = minecraft.thePlayer != null ? CelestweaveBackpack.wornBackpack(minecraft.thePlayer) : null;
            drawButton(minecraft, pack != null ? pack : FALLBACK, true);
        }

        @Override
        public void onTabClicked() {
            ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.BACKPACK));
        }

        @Override
        public boolean shouldAddToList() {
            var player = Minecraft.getMinecraft().thePlayer;
            return player != null && CelestweaveBackpack.wornBackpack(player) != null;
        }
    }
}
