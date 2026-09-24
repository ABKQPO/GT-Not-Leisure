package com.science.gtnl.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.common.packet.MajoBroomActionPacket;
import com.science.gtnl.common.packet.MajoBroomVerticalPacket;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MajoBroomInputHandler {

    private static final KeyBinding PLACE = new KeyBinding("key.gtnl.majo_broom.place", Keyboard.KEY_G,
        "key.categories.gtnl");
    private static final KeyBinding PLACE_AND_RIDE = new KeyBinding("key.gtnl.majo_broom.ride", Keyboard.KEY_H,
        "key.categories.gtnl");
    private int lastBroomId = -1;
    private byte lastVerticalInput;
    private int ticksSinceSend;

    private MajoBroomInputHandler() {}

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
        if (PLACE.isPressed()) ScienceNotLeisure.network.sendToServer(new MajoBroomActionPacket(MajoBroomActionPacket.PLACE));
        if (PLACE_AND_RIDE.isPressed())
            ScienceNotLeisure.network.sendToServer(new MajoBroomActionPacket(MajoBroomActionPacket.PLACE_AND_RIDE));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || !(minecraft.thePlayer.ridingEntity instanceof EntityMajoBroom broom)) {
            lastBroomId = -1;
            lastVerticalInput = 0;
            ticksSinceSend = 0;
            return;
        }

        byte verticalInput = 0;
        if (minecraft.currentScreen == null) {
            boolean ascend = minecraft.gameSettings.keyBindJump.getIsKeyPressed();
            boolean descend = minecraft.gameSettings.keyBindSprint.getIsKeyPressed();
            if (ascend != descend) verticalInput = (byte) (ascend ? 1 : -1);
        }
        broom.setVerticalInput(verticalInput);

        int broomId = broom.getEntityId();
        if (broomId != lastBroomId || verticalInput != lastVerticalInput || ++ticksSinceSend >= 20) {
            ScienceNotLeisure.network.sendToServer(new MajoBroomVerticalPacket(broomId, verticalInput));
            lastBroomId = broomId;
            lastVerticalInput = verticalInput;
            ticksSinceSend = 0;
        }
    }
}
