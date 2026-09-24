package com.science.gtnl.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.common.packet.MajoBroomActionPacket;
import com.science.gtnl.common.packet.MajoBroomControlPacket;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
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
    public int lastBroomId = -1;
    public byte lastForwardInput;
    public byte lastStrafeInput;
    public byte lastVerticalInput;
    public int ticksSinceSend;

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

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || !(minecraft.thePlayer.ridingEntity instanceof EntityMajoBroom broom)) {
            lastBroomId = -1;
            lastForwardInput = 0;
            lastStrafeInput = 0;
            lastVerticalInput = 0;
            ticksSinceSend = 0;
            return;
        }

        byte forwardInput = 0;
        byte strafeInput = 0;
        byte verticalInput = 0;
        if (minecraft.currentScreen == null) {
            forwardInput = quantizeInput(minecraft.thePlayer.moveForward);
            strafeInput = quantizeInput(minecraft.thePlayer.moveStrafing);
            if (forwardInput > 0) {
                if (minecraft.thePlayer.rotationPitch < -15.0F) verticalInput = 1;
                else if (minecraft.thePlayer.rotationPitch > 15.0F) verticalInput = -1;
            }
        }
        broom.setRiderInput(forwardInput, strafeInput, verticalInput);

        int broomId = broom.getEntityId();
        if (broomId != lastBroomId || forwardInput != lastForwardInput
            || strafeInput != lastStrafeInput
            || verticalInput != lastVerticalInput
            || ++ticksSinceSend >= 10) {
            ScienceNotLeisure.network
                .sendToServer(new MajoBroomControlPacket(broomId, forwardInput, strafeInput, verticalInput));
            lastBroomId = broomId;
            lastForwardInput = forwardInput;
            lastStrafeInput = strafeInput;
            lastVerticalInput = verticalInput;
            ticksSinceSend = 0;
        }
    }

    public static byte quantizeInput(float input) {
        return (byte) Math.round(Math.max(-1.0F, Math.min(1.0F, input)) * 127.0F);
    }
}
