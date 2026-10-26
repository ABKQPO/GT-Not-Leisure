package com.science.gtnl.compat.thaumcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.common.block.blocks.BlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.items.wands.ItemWandCasting;

@EventBusSubscriber
public class EssentiaJarWandEvents {

    private static long suppressWandAirUseTick = -1;

    private EssentiaJarWandEvents() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMultiEssentiaJarWandClear(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj == null || !player.isSneaking()) {
            return;
        }

        ItemStack heldStack = player.getHeldItem();
        if (heldStack == null || !(heldStack.getItem() instanceof ItemWandCasting)) {
            return;
        }

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            if (player.worldObj.isRemote && suppressWandAirUseTick == player.worldObj.getTotalWorldTime()) {
                suppressWandAirUseTick = -1;
                event.setCanceled(true);
            }
            return;
        }

        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        TileEntity tile = player.worldObj.getTileEntity(event.x, event.y, event.z);

        if (!(tile instanceof TileEntityMultiEssentiaJar jar)) {
            return;
        }

        if (!player.canPlayerEdit(event.x, event.y, event.z, event.face, heldStack)) {
            return;
        }

        if (player.worldObj.isRemote) {
            suppressWandAirUseTick = player.worldObj.getTotalWorldTime();
            return;
        }

        event.setCanceled(true);

        int clearedAmount = jar.clearAllEssentia();

        if (clearedAmount <= 0) {
            player.addChatMessage(new ChatComponentTranslation("gtnl.chat.multi_essentia_jar.already_empty"));
            return;
        }

        BlockMultiEssentiaJar.playEssentiaSlosh(player.worldObj, event.x, event.y, event.z);

        player.swingItem();

        player.addChatMessage(new ChatComponentTranslation("gtnl.chat.multi_essentia_jar.cleared", clearedAmount));
    }
}
