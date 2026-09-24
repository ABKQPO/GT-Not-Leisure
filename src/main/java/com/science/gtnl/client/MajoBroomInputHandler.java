package com.science.gtnl.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.MajoBroomActionPacket;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MajoBroomInputHandler {

    public static final KeyBinding PLACE = new KeyBinding(
        "key.gtnl.majo_broom.place",
        Keyboard.KEY_G,
        "key.categories.gtnl");
    public static final KeyBinding PLACE_AND_RIDE = new KeyBinding(
        "key.gtnl.majo_broom.ride",
        Keyboard.KEY_H,
        "key.categories.gtnl");

    public static void register() {
        ClientRegistry.registerKeyBinding(PLACE);
        ClientRegistry.registerKeyBinding(PLACE_AND_RIDE);
        FMLCommonHandler.instance()
            .bus()
            .register(new MajoBroomInputHandler());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || minecraft.currentScreen != null) return;
        if (PLACE.isPressed())
            ScienceNotLeisure.network.sendToServer(new MajoBroomActionPacket(MajoBroomActionPacket.PLACE));
        if (PLACE_AND_RIDE.isPressed())
            ScienceNotLeisure.network.sendToServer(new MajoBroomActionPacket(MajoBroomActionPacket.PLACE_AND_RIDE));
    }

}
