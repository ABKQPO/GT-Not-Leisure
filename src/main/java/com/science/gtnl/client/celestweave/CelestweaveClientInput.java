package com.science.gtnl.client.celestweave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.celestweave.CelestweaveBehaviors;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveFlight;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveServerEvents;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CelestweaveClientInput {

    static final KeyBinding DASH = new KeyBinding("key.gtnl.celestweave_dash", Keyboard.KEY_V, "key.categories.gtnl");
    static final KeyBinding RADIAL = new KeyBinding(
        "key.gtnl.celestweave_radial",
        Keyboard.KEY_G,
        "key.categories.gtnl");
    static final KeyBinding BACKPACK = new KeyBinding(
        "key.gtnl.celestweave_backpack",
        Keyboard.KEY_NONE,
        "key.categories.gtnl");
    private static final float DEFAULT_FLY_SPEED = 0.05F;
    private static final int REMOTE_MAX_FLY_LEVEL = 8;

    private boolean flySpeedSet;

    private CelestweaveClientInput() {}

    public static void register() {
        ClientRegistry.registerKeyBinding(DASH);
        ClientRegistry.registerKeyBinding(RADIAL);
        ClientRegistry.registerKeyBinding(BACKPACK);
        FMLCommonHandler.instance()
            .bus()
            .register(new CelestweaveClientInput());
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null) {
            flySpeedSet = false;
            return;
        }
        if (minecraft.currentScreen != null || !minecraft.inGameHasFocus) return;
        boolean dash = false, backpack = false, radial = false;
        while (DASH.isPressed()) dash = true;
        while (BACKPACK.isPressed()) backpack = true;
        while (RADIAL.isPressed()) radial = true;
        if (dash && CelestweaveServerEvents
            .activeAndAffords(player, 3, CelestweaveModule.DASH, MainConfig.item.celestweave.dashEu))
            ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.DASH));
        if (backpack) ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.BACKPACK));
        if (radial && CelestweaveServerEvents.wearsAny(player))
            minecraft.displayGuiScreen(new CelestweaveRadialScreen(RADIAL));
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !(event.player instanceof EntityPlayerSP player)
            || player != Minecraft.getMinecraft().thePlayer) return;
        var config = MainConfig.item.celestweave;
        ItemStack legs = CelestweaveServerEvents.piece(player, 2);
        if (CelestweaveFlight.powered(player, legs)) {
            int speed = CelestweaveFlight.level(legs, CelestweaveBehaviors.SPEED);
            if (!Minecraft.getMinecraft()
                .isSingleplayer()) speed = Math.min(speed, REMOTE_MAX_FLY_LEVEL);
            player.capabilities.setFlySpeed(DEFAULT_FLY_SPEED * speed);
            flySpeedSet = true;
            if (player.capabilities.isFlying) {
                int vertical = CelestweaveFlight.level(legs, CelestweaveBehaviors.VSPEED);
                if (vertical > 1) {
                    double push = 0.05 * (vertical - 1);
                    if (player.movementInput.jump) player.motionY += push;
                    if (player.movementInput.sneak) player.motionY -= push;
                }
                if (CelestweaveData.cfgBool(legs, CelestweaveModule.FLIGHT, CelestweaveBehaviors.INERTIA, false)
                    && player.movementInput.moveForward == 0
                    && player.movementInput.moveStrafe == 0) {
                    player.motionX *= 0.5;
                    player.motionZ *= 0.5;
                }
            }
        } else if (flySpeedSet) {
            player.capabilities.setFlySpeed(DEFAULT_FLY_SPEED);
            flySpeedSet = false;
        }
        ItemStack boots = CelestweaveServerEvents.piece(player, 3);
        if (boots == null || !CelestweaveData.active(boots, CelestweaveModule.WALK_ASSIST)
            || !CelestweaveEnergy.affords(player, 3, boots, config.walkEuPerTick)
            || !CelestweaveData.cfgBool(boots, CelestweaveModule.WALK_ASSIST, CelestweaveBehaviors.OMNI, true)
            || !player.onGround
            || player.isSneaking()
            || player.isSprinting()
            || player.capabilities.isFlying) return;
        float forward = player.movementInput.moveForward, strafe = player.movementInput.moveStrafe;
        if ((forward != 0 || strafe != 0) && (forward <= 0 || strafe != 0)) player.moveFlying(strafe, forward, 0.03F);
    }
}
