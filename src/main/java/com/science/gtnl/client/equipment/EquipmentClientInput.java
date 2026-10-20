package com.science.gtnl.client.equipment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.IEquipmentItem;
import com.science.gtnl.common.packet.OpenEquipmentGuiPacket;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EquipmentClientInput {

    public static final KeyBinding EQUIPMENT_SETTINGS = new KeyBinding(
        "key.gtnl.equipment_settings",
        Keyboard.KEY_G,
        "key.categories.gtnl");

    private static final List<BooleanSupplier> SETTINGS_HANDLERS = new ArrayList<>();

    private EquipmentClientInput() {}

    public static void register() {
        ClientRegistry.registerKeyBinding(EQUIPMENT_SETTINGS);
        FMLCommonHandler.instance()
            .bus()
            .register(new EquipmentClientInput());
    }

    public static void addSettingsHandler(BooleanSupplier handler) {
        SETTINGS_HANDLERS.add(handler);
    }

    public static boolean holdsEquipment(EntityPlayerSP player) {
        ItemStack held = player == null ? null : player.getHeldItem();
        return held != null && held.getItem() instanceof IEquipmentItem;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null
            || minecraft.currentScreen != null
            || !minecraft.inGameHasFocus) return;
        boolean pressed = false;
        while (EQUIPMENT_SETTINGS.isPressed()) pressed = true;
        if (!pressed) return;
        for (BooleanSupplier handler : SETTINGS_HANDLERS) if (handler.getAsBoolean()) return;
        if (holdsEquipment(player)) ScienceNotLeisure.network.sendToServer(new OpenEquipmentGuiPacket());
    }
}
