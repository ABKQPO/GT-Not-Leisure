package com.science.gtnl.compat.tinkersconstruct;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Mods;
import tconstruct.client.tabs.AbstractTab;
import tconstruct.client.tabs.TabRegistry;

// Callable without Tinkers' Construct installed: its classes are only touched inside the optional methods and Tab.
@SideOnly(Side.CLIENT)
public class CelestweaveBackpackTabs {

    private static final ItemStack FALLBACK = new ItemStack(Blocks.chest);

    private CelestweaveBackpackTabs() {}

    public static void register() {
        if (Mods.TinkerConstruct.isModLoaded()) registerTab();
    }

    public static void add(int left, int top, List<?> buttons) {
        if (Mods.TinkerConstruct.isModLoaded()) addTabs(left, top, buttons);
    }

    @Optional.Method(modid = "TConstruct")
    private static void registerTab() {
        TabRegistry.registerTab(new Tab());
    }

    @Optional.Method(modid = "TConstruct")
    @SuppressWarnings("unchecked")
    private static void addTabs(int left, int top, List<?> buttons) {
        TabRegistry.updateTabValues(left, top, Tab.class);
        TabRegistry.addTabsToList((List<GuiButton>) buttons);
    }

    public static class Tab extends AbstractTab {

        public Tab() {
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
