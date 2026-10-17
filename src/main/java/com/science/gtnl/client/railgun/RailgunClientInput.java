package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.packet.OpenRailgunGuiPacket;
import com.science.gtnl.common.packet.RailgunBeamTogglePacket;
import com.science.gtnl.common.railgun.RailgunDefaults;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RailgunClientInput {

    private static final KeyBinding OPEN = new KeyBinding(
        "key.gtnl.railgun_config",
        Keyboard.KEY_NONE,
        "key.categories.gtnl");

    public RailgunClientInput() {
        ClientRegistry.registerKeyBinding(OPEN);
    }

    private boolean firing;
    private ItemStack held;
    private int slot = -1;
    private long lastSent;

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
            && mc.currentScreen == null) ScienceNotLeisure.network.sendToServer(new OpenRailgunGuiPacket());
        boolean pressed = stack != null && stack.getItem() instanceof ElectromagneticRailgun
            && mc.currentScreen == null
            && mc.inGameHasFocus
            && !player.isDead
            && mc.gameSettings.keyBindAttack.getIsKeyPressed()
            && !player.isUsingItem();
        if (firing && (slot != player.inventory.currentItem || stack == null
            || held == null
            || held.getItem() != stack.getItem())) pressed = false;
        long now = mc.theWorld.getTotalWorldTime();
        if (pressed != firing) {
            firing = pressed;
            RailgunBeamRenderer.setLocalRequested(firing);
            ScienceNotLeisure.network.sendToServer(new RailgunBeamTogglePacket(firing));
            lastSent = now;
        } else if (firing && !RailgunBeamRenderer.active(player.getEntityId())
            && now - lastSent >= RailgunDefaults.BEAM_RESEND_TICKS) {
                ScienceNotLeisure.network.sendToServer(new RailgunBeamTogglePacket(true));
                lastSent = now;
            }
        held = stack;
        slot = player.inventory.currentItem;
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.player != Minecraft.getMinecraft().thePlayer) return;
        EntityPlayer player = event.player;
        ItemStack using = player.getItemInUse();
        ItemStack current = player.getCurrentEquippedItem();
        if (using == null || current == null || using == current) return;
        if (!(using.getItem() instanceof ElectromagneticRailgun) || current.getItem() != using.getItem()) return;
        int remaining = player.getItemInUseCount();
        if (remaining > 0) player.setItemInUse(current, remaining);
    }
}
