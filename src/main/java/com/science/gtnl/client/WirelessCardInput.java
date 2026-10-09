package com.science.gtnl.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.OverloadedFrequencyCard;
import com.science.gtnl.common.packet.OpenWirelessCardPacket;
import com.science.gtnl.common.packet.ToggleWirelessCardPacket;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class WirelessCardInput {

    private static final KeyBinding TOGGLE = new KeyBinding(
        "key.gtnl.wireless_card_auto",
        Keyboard.KEY_NONE,
        "key.categories.gtnl");
    private long nextInput;
    private static final KeyBinding OPEN = new KeyBinding(
        "key.gtnl.wireless_card_open",
        Keyboard.KEY_NONE,
        "key.categories.gtnl");

    public static void register() {
        ClientRegistry.registerKeyBinding(TOGGLE);
        ClientRegistry.registerKeyBinding(OPEN);
        WirelessCardInput handler = new WirelessCardInput();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent event) {
        keys();
    }

    @SubscribeEvent
    public void mouseKey(InputEvent.MouseInputEvent event) {
        keys();
    }

    private void keys() {
        if (OPEN.isPressed()) sendOpen();
        if (TOGGLE.isPressed()) send(false);
    }

    private void sendOpen() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!MainConfig.debug.enableWirelessChannelPrototype || mc.currentScreen != null
            || mc.thePlayer == null
            || mc.theWorld == null) return;
        long now = System.nanoTime();
        if (now < nextInput) return;
        nextInput = now + 300_000_000L;
        ScienceNotLeisure.network.sendToServer(new OpenWirelessCardPacket());
    }

    @SubscribeEvent
    public void mouse(MouseEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!MainConfig.debug.enableWirelessChannelPrototype || mc.currentScreen != null
            || mc.thePlayer == null
            || event.dwheel == 0
            || !mc.thePlayer.isSneaking()) return;
        ItemStack held = mc.thePlayer.getCurrentEquippedItem();
        if (held == null || !(held.getItem() instanceof OverloadedFrequencyCard)) return;
        event.setCanceled(true); // Consume wheel input before vanilla changes the selected hotbar slot.
        send(true);
    }

    private void send(boolean heldOnly) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!MainConfig.debug.enableWirelessChannelPrototype || mc.currentScreen != null
            || mc.thePlayer == null
            || mc.theWorld == null) return;
        long now = System.nanoTime();
        if (now < nextInput) return;
        nextInput = now + 300_000_000L;
        ScienceNotLeisure.network
            .sendToServer(new ToggleWirelessCardPacket(heldOnly, mc.thePlayer.inventory.currentItem));
    }
}
