package com.science.gtnl.client.celestweave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveFlight;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveModuleSetting;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnl.config.ItemConfig;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
public class CelestweaveClientInput {

    static final KeyBinding DASH = new KeyBinding("key.gtnl.celestweave_dash", Keyboard.KEY_V, "key.categories.gtnl");
    static final KeyBinding BACKPACK = new KeyBinding(
        "key.gtnl.celestweave_backpack",
        Keyboard.KEY_NONE,
        "key.categories.gtnl");
    private static final float DEFAULT_FLY_SPEED = 0.05F;
    private static final int REMOTE_MAX_FLY_LEVEL = 8;

    private static boolean flySpeedSet;

    public static void register() {
        ClientRegistry.registerKeyBinding(DASH);
        ClientRegistry.registerKeyBinding(BACKPACK);
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null) {
            flySpeedSet = false;
            return;
        }
        if (minecraft.currentScreen != null || !minecraft.inGameHasFocus) return;
        boolean dash = false, backpack = false;
        while (DASH.isPressed()) dash = true;
        while (BACKPACK.isPressed()) backpack = true;
        if (dash && CelestweaveEnergy
            .activeAndAffords(player, CelestweaveArmor.BOOTS, CelestweaveModule.DASH, ItemConfig.celestweave.dashEu))
            ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.DASH));
        if (backpack) ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.BACKPACK));
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !(event.player instanceof EntityPlayerSP player)
            || player != Minecraft.getMinecraft().thePlayer) return;
        var config = ItemConfig.celestweave;
        ItemStack legs = CelestweaveData.piece(player, CelestweaveArmor.LEGS);
        if (CelestweaveFlight.canFly(player, legs)) {
            int speed = CelestweaveModuleSetting.FLIGHT_SPEED.get(legs);
            if (!Minecraft.getMinecraft()
                .isSingleplayer()) speed = Math.min(speed, REMOTE_MAX_FLY_LEVEL);
            player.capabilities.setFlySpeed(DEFAULT_FLY_SPEED * speed);
            flySpeedSet = true;
            if (player.capabilities.isFlying) {
                int vertical = CelestweaveModuleSetting.FLIGHT_VSPEED.get(legs);
                if (vertical > 1) {
                    double push = 0.05 * (vertical - 1);
                    if (player.movementInput.jump) player.motionY += push;
                    if (player.movementInput.sneak) player.motionY -= push;
                }
                if (CelestweaveModuleSetting.FLIGHT_INERTIA.on(legs) && player.movementInput.moveForward == 0
                    && player.movementInput.moveStrafe == 0) {
                    player.motionX *= 0.5;
                    player.motionZ *= 0.5;
                }
            }
        } else if (flySpeedSet) {
            player.capabilities.setFlySpeed(DEFAULT_FLY_SPEED);
            flySpeedSet = false;
        }
        ItemStack boots = CelestweaveData.piece(player, CelestweaveArmor.BOOTS);
        if (boots == null || !CelestweaveData.active(boots, CelestweaveModule.WALK_ASSIST)
            || !CelestweaveEnergy.affords(player, CelestweaveArmor.BOOTS, boots, config.walkEuPerTick)
            || !CelestweaveModuleSetting.WALK_OMNI.on(boots)
            || !player.onGround
            || player.isSneaking()
            || player.isSprinting()
            || player.capabilities.isFlying) return;
        float forward = player.movementInput.moveForward, strafe = player.movementInput.moveStrafe;
        if ((forward != 0 || strafe != 0) && (forward <= 0 || strafe != 0)) player.moveFlying(strafe, forward, 0.03F);
    }
}
