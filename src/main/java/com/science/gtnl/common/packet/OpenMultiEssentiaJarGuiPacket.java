package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.block.blocks.BlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import cpw.mods.fml.common.network.ByteBufUtils;
import gregtech.crossmod.backhand.Backhand;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.aspects.Aspect;

public class OpenMultiEssentiaJarGuiPacket extends ServerboundPacket {

    private static final byte OPEN_HELD_GUI = 0;
    private static final byte OPEN_BLOCK_GUI = 1;
    private static final byte PREVIOUS_BLOCK_ASPECT = 2;
    private static final byte SELECT_BLOCK_ASPECT = 3;
    private static final byte PREVIOUS_HELD_ASPECT = 4;
    private static final double MAX_INTERACTION_DISTANCE_SQUARED = 64.0D;

    private byte action = OPEN_HELD_GUI;
    private int x;
    private int y;
    private int z;
    private String aspectTag = "";

    public OpenMultiEssentiaJarGuiPacket() {}

    private OpenMultiEssentiaJarGuiPacket(byte action, int x, int y, int z, String aspectTag) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.aspectTag = aspectTag == null ? "" : aspectTag;
    }

    public static OpenMultiEssentiaJarGuiPacket openBlockGui(int x, int y, int z) {
        return new OpenMultiEssentiaJarGuiPacket(OPEN_BLOCK_GUI, x, y, z, "");
    }

    public static OpenMultiEssentiaJarGuiPacket previousBlockAspect(int x, int y, int z) {
        return new OpenMultiEssentiaJarGuiPacket(PREVIOUS_BLOCK_ASPECT, x, y, z, "");
    }

    public static OpenMultiEssentiaJarGuiPacket selectBlockAspect(int x, int y, int z, String aspectTag) {
        return new OpenMultiEssentiaJarGuiPacket(SELECT_BLOCK_ASPECT, x, y, z, aspectTag);
    }

    public static OpenMultiEssentiaJarGuiPacket previousHeldAspect() {
        return new OpenMultiEssentiaJarGuiPacket(PREVIOUS_HELD_ASPECT, 0, 0, 0, "");
    }

    @Override
    protected void read(ByteBuf buf) {
        action = buf.readByte();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        aspectTag = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeByte(action);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeUTF8String(buf, aspectTag);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> handleOnServerThread(player));
    }

    private void handleOnServerThread(EntityPlayerMP player) {
        if (action == OPEN_HELD_GUI) {
            openHeldGui(player);
            return;
        }
        if (action == PREVIOUS_HELD_ASPECT) {
            handlePreviousHeldAspect(player);
            return;
        }
        if (action < OPEN_BLOCK_GUI || action > SELECT_BLOCK_ASPECT) return;

        World world = player.worldObj;
        if (!world.blockExists(x, y, z)
            || player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) > MAX_INTERACTION_DISTANCE_SQUARED) {
            return;
        }

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMultiEssentiaJar jar)) return;

        switch (action) {
            case OPEN_BLOCK_GUI -> {
                if (jar.getStoredTypeCount() <= 0) {
                    player.addChatMessage(
                        new ChatComponentTranslation(
                            "Info_MultiEssentiaJar_Empty",
                            TileEntityMultiEssentiaJar.MAX_CAPACITY));
                    return;
                }
                GuiFactories.tileEntity()
                    .open(player, x, y, z);
            }
            case PREVIOUS_BLOCK_ASPECT -> BlockMultiEssentiaJar
                .sendActiveAspectStatus(player, jar, jar.cyclePreviousActiveAspect());
            case SELECT_BLOCK_ASPECT -> jar.setActiveAspect(Aspect.getAspect(aspectTag));
            default -> {}
        }
    }

    private static void openHeldGui(EntityPlayerMP player) {
        ItemStack mainHand = player.inventory.getCurrentItem();
        if (isSelectableJar(mainHand)) {
            GuiFactories.playerInventory()
                .openFromMainHand(player);
            return;
        }

        ItemStack offHand = Backhand.getOffhandItem(player);
        if (isSelectableJar(offHand)) {
            GuiFactories.playerInventory()
                .openFromPlayerInventory(player, Backhand.getOffhandSlot(player));
        }
    }

    private static void handlePreviousHeldAspect(EntityPlayerMP player) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (!isMultiEssentiaJar(stack)) {
            stack = Backhand.getOffhandItem(player);
        }
        if (!isMultiEssentiaJar(stack)) return;

        Aspect activeAspect = TileEntityMultiEssentiaJar.cyclePreviousActiveAspect(stack);
        ItemBlockMultiEssentiaJar.sendActiveAspectStatus(player, stack, activeAspect);
        if (activeAspect != null) player.inventoryContainer.detectAndSendChanges();
    }

    private static boolean isSelectableJar(ItemStack stack) {
        return isMultiEssentiaJar(stack) && TileEntityMultiEssentiaJar.getStoredAspects(stack)
            .visSize() > 0;
    }

    private static boolean isMultiEssentiaJar(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemBlockMultiEssentiaJar;
    }

}
