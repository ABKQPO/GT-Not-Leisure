package com.science.gtnl.utils.event;

import static com.science.gtnl.ScienceNotLeisure.network;
import static com.science.gtnl.common.packet.client.SoundHandler.PLAYING_SOUNDS;
import static com.science.gtnl.common.render.tile.BallRenderer.visualStateMap;

import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.reavaritia.client.render.CustomEntityRenderer;
import com.science.gtnl.common.item.items.NullPointerException;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.packet.NBTUpdatePacket;
import com.science.gtnl.common.packet.client.TitleDisplayHandler;
import com.science.gtnl.common.render.item.ItemNullPointerExceptionRender;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.mixins.early.Minecraft.AccessorGuiChat;
import com.science.gtnl.utils.ClientUtils;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.client.ElectricJukeboxSound;

public class SubscribeEventClientUtils {

    public static final Random random = new Random();
    public static String haloNoiseIconTexture = ModList.ScienceNotLeisure.resourceDomain + ":halonoise";
    public static IIcon haloNoiseIcon;

    public static String cheatWrenchIconTexture = "nei:cheat_speical";
    public static IIcon cheatWrenchIcon;

    public static boolean hasHandledDeathMessage = false;
    public static float lastHealth = 20.0f;

    public static void registerAllIcons(net.minecraft.client.renderer.texture.IIconRegister ir) {
        haloNoiseIcon = ir.registerIcon(haloNoiseIconTexture);
        cheatWrenchIcon = ir.registerIcon(cheatWrenchIconTexture);
    }

    // Player
    @SubscribeEvent
    public void onClientConnectedToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent aEvent) {
        PLAYING_SOUNDS.clear();
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        ItemStack held = player.getCurrentEquippedItem();
        if (held == null) return;

        if (held.getItem() != ItemLoader.veinMiningPickaxe) return;

        NBTTagCompound nbt = held.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            held.setTagCompound(nbt);
        }

        boolean rightClickHeld = Mouse.isButtonDown(1);

        if (player.isSneaking() && !rightClickHeld) {
            if (event.dwheel == 0) return;
            int oldRange = nbt.hasKey("range") ? nbt.getInteger("range") : 0;
            int newRange = oldRange;

            if (event.dwheel > 0) {
                newRange++;
            } else {
                newRange--;
            }

            if (newRange < -1) newRange = -1;
            if (newRange > MainConfig.item.vein_miner_pickaxe.maxRange)
                newRange = MainConfig.item.vein_miner_pickaxe.maxRange;

            if (newRange != oldRange) {
                nbt.setInteger("range", newRange);
                ClientUtils.showSubtitle("Tooltip_VeinMiningPickaxe_00", newRange);
                network.sendToServer(new NBTUpdatePacket(player.inventory.currentItem, held));
                event.setCanceled(true);
            }
        }

        if (!player.isSneaking() && rightClickHeld) {
            if (event.dwheel == 0) return;
            int oldAmount = nbt.hasKey("amount") ? nbt.getInteger("amount") : 0;
            int newAmount = oldAmount;

            if (event.dwheel > 0) {
                newAmount += 10000;
            } else {
                newAmount -= 10000;
            }

            if (newAmount < 0) newAmount = 0;
            if (newAmount > MainConfig.item.vein_miner_pickaxe.maxRange)
                newAmount = MainConfig.item.vein_miner_pickaxe.maxRange;

            if (newAmount != oldAmount) {
                nbt.setInteger("amount", newAmount);
                ClientUtils.showSubtitle("Tooltip_VeinMiningPickaxe_01", newAmount);
                network.sendToServer(new NBTUpdatePacket(player.inventory.currentItem, held));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END && !MainConfig.other.enableDeathIncompleteMessage) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        float currentHealth = mc.thePlayer.getHealth();

        if (lastHealth > 0 && currentHealth <= 0
            && !hasHandledDeathMessage
            && mc.currentScreen instanceof GuiChat guiChat) {
            String chat = ((AccessorGuiChat) guiChat).getInputField()
                .getText();
            mc.thePlayer.sendChatMessage(chat + (chat.startsWith("/") ? "" : "-"));
            mc.thePlayer.closeScreen();
            hasHandledDeathMessage = true;
        }

        if (currentHealth > 0) {
            hasHandledDeathMessage = false;
        }

        lastHealth = currentHealth;
    }

    @SubscribeEvent
    public void onIngameMenuGuiOpen(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (player != null && !player.capabilities.isCreativeMode) {
            PotionEffect effect = player.getActivePotionEffect(EffectLoader.awe);

            if (effect != null && event.gui instanceof GuiIngameMenu) {
                TitleDisplayHandler
                    .displayTitle(StatCollector.translateToLocal("Awe_Cancel_01"), 100, 0xFFFFFF, 3, 10, 20);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onInventoryGuiOpen(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (player != null && !player.capabilities.isCreativeMode) {
            PotionEffect effect = player.getActivePotionEffect(EffectLoader.awe);

            if (effect != null && event.gui instanceof GuiInventory) {
                String[] messages = { "Awe_Cancel_02_01", "Awe_Cancel_02_02" };
                String message = messages[random.nextInt(messages.length)];
                TitleDisplayHandler.displayTitle(StatCollector.translateToLocal(message), 100, 0xFFFFFF, 3, 10, 20);

                event.setCanceled(true);
            }
        }
    }

    // Render
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            registerAllIcons(event.map);
        }
    }

    @SubscribeEvent
    public void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.item;
        if (stack != null && stack.getItem() instanceof NullPointerException) {
            ItemNullPointerExceptionRender.renderItem(IItemRenderer.ItemRenderType.ENTITY, event.item, true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderLivingEvent.Pre event) {
        if (!(event.entity instanceof EntityPlayer player)) return;

        if (player.isPotionActive(EffectLoader.shimmering) || player.isPotionActive(EffectLoader.ghostly_shape)) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderLivingEvent.Post event) {
        if (!(event.entity instanceof EntityPlayer player)) return;

        if (player.isPotionActive(EffectLoader.shimmering) || player.isPotionActive(EffectLoader.ghostly_shape)) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
        }
    }

    // Sound
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTickEvent(TickEvent.ClientTickEvent aEvent) {
        if (aEvent.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.thePlayer;

            if (player != null && mc.theWorld != null && !PLAYING_SOUNDS.isEmpty()) {
                double playerX = player.posX;
                double playerY = player.posY;
                double playerZ = player.posZ;

                for (Map.Entry<String, ElectricJukeboxSound> entry : PLAYING_SOUNDS.entrySet()) {
                    ElectricJukeboxSound sound = entry.getValue();
                    sound.xPosition = (float) playerX;
                    sound.yPosition = (float) playerY;
                    sound.zPosition = (float) playerZ;
                }
            }
        }
    }

    // World
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        visualStateMap.clear();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) {
            EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
            if (renderer instanceof CustomEntityRenderer customEntityRenderer) {
                customEntityRenderer.resetShader();
            }
        }
        TimeStopPocketWatch.setTimeStopped(false);
    }
}
