package com.science.gtnl.common.packet;

import static com.science.gtnl.ScienceNotLeisure.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;

public class NetWorkHandler {

    public static void registerAllMessage() {
        int i = 0;
        network.registerMessage(SoundPacket.Handler.class, SoundPacket.class, i++, Side.CLIENT);
        network.registerMessage(TitlePacket.Handler.class, TitlePacket.class, i++, Side.CLIENT);
        network.registerMessage(TickratePacket.Handler.class, TickratePacket.class, i++, Side.CLIENT);
        network.registerMessage(SyncConfigPacket.Handler.class, SyncConfigPacket.class, i++, Side.CLIENT);
        network.registerMessage(ProspectingPacket.Handler.class, ProspectingPacket.class, i++, Side.CLIENT);
        network.registerMessage(TileEntityNBTPacket.Handler.class, TileEntityNBTPacket.class, i++, Side.CLIENT);
        network.registerMessage(SyncHPCAVariablesPacket.Handler.class, SyncHPCAVariablesPacket.class, i++, Side.CLIENT);
        network.registerMessage(ContainerRollBACK.class, ContainerRollBACK.class, i++, Side.CLIENT);
        network.registerMessage(
            GetTileEntityNBTRequestPacket.Handler.class,
            GetTileEntityNBTRequestPacket.class,
            i++,
            Side.SERVER);
        network.registerMessage(TeleportRequestPacket.Handler.class, TeleportRequestPacket.class, i++, Side.SERVER);
        registerMessage(KeyBindingHandler.class, i++, Side.SERVER);
        registerMessage(WirelessPickBlock.class, i++, Side.SERVER);
        registerMessage(ContainerRollBACK.class, i++, Side.SERVER);
        network.registerMessage(SudoPacket.Handler.class, SudoPacket.class, i++, Side.CLIENT);
        network.registerMessage(NBTUpdatePacket.Handler.class, NBTUpdatePacket.class, i++, Side.SERVER);
        network.registerMessage(
            PortableInfinityChestSyncPacket.Handler.class,
            PortableInfinityChestSyncPacket.class,
            i++,
            Side.SERVER);
        registerMessage(PktPatternTermUploadPattern.class, i++, Side.SERVER);
        registerMessage(EncodeDirePattern.class, i++, Side.SERVER);
        network.registerMessage(
            PortableInfinityChestSyncPacket.Handler.class,
            PortableInfinityChestSyncPacket.class,
            i++,
            Side.CLIENT);
        registerMessage(StatusMessage.class, i++, Side.CLIENT);
        network
            .registerMessage(SyncCircuitNanitesPacket.Handler.class, SyncCircuitNanitesPacket.class, i++, Side.CLIENT);
        network.registerMessage(SyncRecipePacket.Handler.class, SyncRecipePacket.class, i++, Side.CLIENT);
    }

    private static <T extends IMessageHandler<T, IMessage> & IMessage> void registerMessage(Class<T> c, int i,
        Side side) {
        network.registerMessage(c, c, i, side);
    }
}
