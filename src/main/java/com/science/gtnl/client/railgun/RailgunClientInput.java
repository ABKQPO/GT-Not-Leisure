package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunBeamTogglePacket;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class RailgunClientInput {

    private static final net.minecraft.client.settings.KeyBinding OPEN = new net.minecraft.client.settings.KeyBinding(
        "key.gtnl.railgun_config",
        org.lwjgl.input.Keyboard.KEY_NONE,
        "key.categories.gtnl");

    public RailgunClientInput() {
        cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(OPEN);
    }

    private boolean firing;
    private ItemStack held;
    private int slot = -1;

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        var mc = Minecraft.getMinecraft();
        var player = mc.thePlayer;
        if (player == null || mc.theWorld == null) {
            firing = false;
            held = null;
            RailgunBeamRenderer.setLocalRequested(false);
            return;
        }
        ItemStack stack = player.getHeldItem();
        if (OPEN.isPressed() && stack != null
            && stack.getItem() instanceof ElectromagneticRailgun
            && mc.currentScreen == null)
            ScienceNotLeisure.network.sendToServer(new com.science.gtnl.common.packet.OpenRailgunGuiPacket());
        boolean pressed = stack != null && stack.getItem() instanceof ElectromagneticRailgun
            && mc.currentScreen == null
            && mc.inGameHasFocus
            && !player.isDead
            && mc.gameSettings.keyBindAttack.getIsKeyPressed()
            && !player.isUsingItem();
        if (firing && (slot != player.inventory.currentItem || stack == null
            || held == null
            || held.getItem() != stack.getItem())) pressed = false;
        if (pressed != firing) {
            firing = pressed;
            RailgunBeamRenderer.setLocalRequested(firing);
            ScienceNotLeisure.network.sendToServer(new RailgunBeamTogglePacket(firing));
        }
        held = stack;
        slot = player.inventory.currentItem;
    }
}
