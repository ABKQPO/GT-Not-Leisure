package com.science.gtnl.client;

import static com.science.gtnl.ScienceNotLeisure.network;
import static com.science.gtnl.common.packet.client.SoundHandler.PLAYING_SOUNDS;
import static com.science.gtnl.common.render.tile.BallRenderer.visualStateMap;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.reavaritia.client.render.CustomEntityRenderer;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaJar;
import com.science.gtnl.common.item.items.NullPointerException;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.packet.OpenVeinMiningPickaxeGuiPacket;
import com.science.gtnl.common.render.item.ItemNullPointerExceptionRender;
import com.science.gtnl.config.QuantumCraftingClientConfig;
import com.science.gtnl.loader.ItemLoader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.INetworkUpdatableItem;
import gregtech.api.net.GTPacketUpdateItem;
import gregtech.client.ElectricJukeboxSound;
import gregtech.crossmod.backhand.Backhand;
import xonin.backhand.api.core.BackhandUtils;

@EventBusSubscriber(side = Side.CLIENT)
public class GTNLClientEvents {

    public static String HALO_NOISE_ICON_TEXTURE = ScienceNotLeisure.MODID + ":halonoise";
    public static IIcon haloNoiseIcon;

    public static String CHEAT_WRENCH_ICON_TEXTURE = "nei:cheat_speical";
    public static IIcon cheatWrenchIcon;

    private GTNLClientEvents() {}

    public static void registerAllIcons(IIconRegister ir) {
        haloNoiseIcon = ir.registerIcon(HALO_NOISE_ICON_TEXTURE);
        cheatWrenchIcon = ir.registerIcon(CHEAT_WRENCH_ICON_TEXTURE);
    }

    @SubscribeEvent
    public static void onClientConnectedToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent aEvent) {
        PLAYING_SOUNDS.clear();
        QuantumCraftingClientConfig.scheduleSync();
    }

    @SubscribeEvent
    public static void onMouseEvent(MouseEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null) return;

        if (handlePreviousMultiEssentiaJarAspect(minecraft, player, event)) return;

        ItemStack held = player.getCurrentEquippedItem();
        ItemStack offHand = Backhand.getOffhandItem(player);
        boolean mainHandPickaxe = held != null && held.getItem() == ItemLoader.veinMiningPickaxe;
        boolean offHandPickaxe = offHand != null && offHand.getItem() == ItemLoader.veinMiningPickaxe;
        if (!mainHandPickaxe && !offHandPickaxe) return;

        if (minecraft.currentScreen == null && event.button == 2 && event.buttonstate) {
            network.sendToServer(new OpenVeinMiningPickaxeGuiPacket(offHandPickaxe));
            event.setCanceled(true);
        }
    }

    private static boolean handlePreviousMultiEssentiaJarAspect(Minecraft minecraft, EntityPlayer player,
        MouseEvent event) {
        if (event.button != 0 || !event.buttonstate
            || !player.isSneaking()
            || minecraft.currentScreen != null
            || minecraft.theWorld == null) {
            return false;
        }

        ItemStack mainHand = player.getCurrentEquippedItem();
        ItemStack offHand = Backhand.getOffhandItem(player);
        boolean mainHandJar = isMultiEssentiaJar(mainHand);
        boolean offHandJar = isMultiEssentiaJar(offHand)
            && (mainHand == null || !(mainHand.getItem() instanceof INetworkUpdatableItem));
        if (!mainHandJar && !offHandJar) return false;

        // GT's existing GTPacketUpdateItem asks the server to cycle back to the previous aspect.
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte(ItemBlockMultiEssentiaJar.CYCLE_PREVIOUS_ASPECT_PACKET_KEY, (byte) 1);
        Runnable cyclePrevious = () -> {
            GTValues.NW.sendToServer(new GTPacketUpdateItem(tag));
            player.swingItem();
        };

        if (mainHandJar) {
            cyclePrevious.run();
        } else {
            BackhandUtils.useOffhandItem(player, cyclePrevious);
        }
        event.setCanceled(true);
        return true;
    }

    private static boolean isMultiEssentiaJar(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemBlockMultiEssentiaJar;
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            registerAllIcons(event.map);
        }
    }

    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.item;
        if (stack != null && stack.getItem() instanceof NullPointerException) {
            ItemNullPointerExceptionRender.renderItem(IItemRenderer.ItemRenderType.ENTITY, event.item, true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.ClientTickEvent aEvent) {
        if (aEvent.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.thePlayer;

            if (player != null) {
                QuantumCraftingClientConfig.syncIfScheduled();
            }

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

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        visualStateMap.clear();
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) {
            EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
            if (renderer instanceof CustomEntityRenderer customEntityRenderer) {
                customEntityRenderer.resetShader();
            }
        }
        TimeStopPocketWatch.setTimeStopped(false);
    }
}
