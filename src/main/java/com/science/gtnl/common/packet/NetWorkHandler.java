package com.science.gtnl.common.packet;

import static com.science.gtnl.ScienceNotLeisure.network;

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
        network.registerMessage(KeyBindingHandler.class, KeyBindingHandler.class, i++, Side.SERVER);
        network.registerMessage(WirelessPickBlock.class, WirelessPickBlock.class, i++, Side.SERVER);
        network.registerMessage(ContainerRollBACK.class, ContainerRollBACK.class, i++, Side.SERVER);
        network.registerMessage(SudoPacket.Handler.class, SudoPacket.class, i++, Side.CLIENT);
        network.registerMessage(NBTUpdatePacket.Handler.class, NBTUpdatePacket.class, i++, Side.SERVER);
        network.registerMessage(
            PortableInfinityChestSyncPacket.Handler.class,
            PortableInfinityChestSyncPacket.class,
            i++,
            Side.SERVER);
        network.registerMessage(
            PortableInfinityChestSyncPacket.Handler.class,
            PortableInfinityChestSyncPacket.class,
            i++,
            Side.CLIENT);
        network.registerMessage(StatusMessage.class, StatusMessage.class, i++, Side.CLIENT);
        network
            .registerMessage(SyncCircuitNanitesPacket.Handler.class, SyncCircuitNanitesPacket.class, i++, Side.CLIENT);
        network.registerMessage(SyncRecipePacket.Handler.class, SyncRecipePacket.class, i++, Side.CLIENT);
        network.registerMessage(SyncLinkedUnitsPacket.Handler.class, SyncLinkedUnitsPacket.class, i++, Side.CLIENT);
    }
}
