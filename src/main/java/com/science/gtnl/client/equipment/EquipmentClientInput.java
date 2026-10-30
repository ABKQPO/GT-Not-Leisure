package com.science.gtnl.client.equipment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.science.gtnl.client.portalgun.PortalGunWheelScreen;
import com.science.gtnl.common.item.items.PortalGun;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
public class EquipmentClientInput {

    public static final KeyBinding EQUIPMENT_SETTINGS = new KeyBinding(
        "key.gtnl.equipment_settings",
        Keyboard.KEY_G,
        "key.categories.gtnl");

    public static void register() {
        ClientRegistry.registerKeyBinding(EQUIPMENT_SETTINGS);
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null
            || minecraft.currentScreen != null
            || !minecraft.inGameHasFocus) return;
        boolean pressed = false;
        while (EQUIPMENT_SETTINGS.isPressed()) pressed = true;
        if (!pressed) return;
        if (PortalGun.held(player) != null) {
            PortalGunWheelScreen.open(EQUIPMENT_SETTINGS);
            return;
        }
        int[] slots = EquipmentWheelScreen.equipmentSlots(player);
        if (slots.length > 0) new EquipmentWheelScreen(EQUIPMENT_SETTINGS, slots).open();
    }
}
