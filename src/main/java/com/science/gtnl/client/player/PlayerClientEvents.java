package com.science.gtnl.client.player;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.title.TitleAPI;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.mixins.early.minecraft.AccessorGuiChat;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
public class PlayerClientEvents {

    public static final Random RANDOM = new Random();
    public static boolean HAS_HANDLED_DEATH_MESSAGE = false;
    public static float LAST_HEALTH = 20.0f;

    private PlayerClientEvents() {}

    @SubscribeEvent
    public static void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer != null) {
            BaubleItem.removePlayer(minecraft.thePlayer.getUniqueID());
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END && !MainConfig.other.enableDeathIncompleteMessage) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        float currentHealth = mc.thePlayer.getHealth();

        if (LAST_HEALTH > 0 && currentHealth <= 0
            && !HAS_HANDLED_DEATH_MESSAGE
            && mc.currentScreen instanceof GuiChat guiChat) {
            String chat = ((AccessorGuiChat) guiChat).getInputField()
                .getText();
            mc.thePlayer.sendChatMessage(chat + (chat.startsWith("/") ? "" : "-"));
            mc.thePlayer.closeScreen();
            HAS_HANDLED_DEATH_MESSAGE = true;
        }

        if (currentHealth > 0) {
            HAS_HANDLED_DEATH_MESSAGE = false;
        }

        LAST_HEALTH = currentHealth;
    }

    @SubscribeEvent
    public static void onIngameMenuGuiOpen(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (player != null && !player.capabilities.isCreativeMode) {
            PotionEffect effect = player.getActivePotionEffect(EffectLoader.awe);

            if (effect != null && event.gui instanceof GuiIngameMenu) {
                TitleAPI.setTimes(10, 100, 20);
                TitleAPI.setTitle(new ChatComponentTranslation("gtnl.effect.awe.cancel.0"));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onInventoryGuiOpen(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (player != null && !player.capabilities.isCreativeMode) {
            PotionEffect effect = player.getActivePotionEffect(EffectLoader.awe);

            if (effect != null && event.gui instanceof GuiInventory) {
                String[] messages = { "gtnl.effect.awe.cancel.1", "gtnl.effect.awe.cancel.2" };
                String message = messages[RANDOM.nextInt(messages.length)];
                TitleAPI.setTimes(10, 100, 20);
                TitleAPI.setTitle(new ChatComponentTranslation(message));

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderLivingEvent.Pre event) {
        if (!(event.entity instanceof EntityPlayer player)) return;

        if (player.isPotionActive(EffectLoader.shimmering) || player.isPotionActive(EffectLoader.ghostly_shape)) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderLivingEvent.Post event) {
        if (!(event.entity instanceof EntityPlayer player)) return;

        if (player.isPotionActive(EffectLoader.shimmering) || player.isPotionActive(EffectLoader.ghostly_shape)) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
        }
    }
}
