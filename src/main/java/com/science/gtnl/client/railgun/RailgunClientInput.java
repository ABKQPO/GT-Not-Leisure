package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunBeamTogglePacket;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
public class RailgunClientInput {

    private static final int BEAM_RESEND_TICKS = 10;

    private static boolean firing;
    private static ItemStack held;
    private static int slot = -1;
    private static long lastSent;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        var minecraft = Minecraft.getMinecraft();
        var player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null) {
            firing = false;
            held = null;
            RailgunBeamRenderer.setLocalRequested(false);
            return;
        }
        ItemStack stack = player.getHeldItem();
        boolean pressed = ElectromagneticRailgun.is(stack) && minecraft.currentScreen == null
            && minecraft.inGameHasFocus
            && !player.isDead
            && minecraft.gameSettings.keyBindAttack.getIsKeyPressed()
            && !player.isUsingItem();
        if (firing && (slot != player.inventory.currentItem || stack == null
            || held == null
            || held.getItem() != stack.getItem())) pressed = false;
        long now = minecraft.theWorld.getTotalWorldTime();
        if (pressed != firing) {
            firing = pressed;
            RailgunBeamRenderer.setLocalRequested(firing);
            ScienceNotLeisure.network.sendToServer(new RailgunBeamTogglePacket(firing));
            lastSent = now;
        } else if (firing && !RailgunBeamRenderer.active(player.getEntityId()) && now - lastSent >= BEAM_RESEND_TICKS) {
            ScienceNotLeisure.network.sendToServer(new RailgunBeamTogglePacket(true));
            lastSent = now;
        }
        held = stack;
        slot = player.inventory.currentItem;
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.player != Minecraft.getMinecraft().thePlayer) return;
        EntityPlayer player = event.player;
        ItemStack using = player.getItemInUse();
        ItemStack current = player.getCurrentEquippedItem();
        if (using == null || current == null || using == current) return;
        if (!ElectromagneticRailgun.is(using) || current.getItem() != using.getItem()) return;
        int remaining = player.getItemInUseCount();
        if (remaining > 0) player.setItemInUse(current, remaining);
    }
}
