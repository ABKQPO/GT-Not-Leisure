package com.science.gtnl.common.packet;

import static com.science.gtnl.ScienceNotLeisure.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;
import gregtech.api.enums.Mods;

public class NetWorkHandler {

    public static void registerAllMessage() {
        int i = 0;
        registerMessage(SoundPacket.class, i++, Side.CLIENT);
        registerMessage(TitlePacket.class, i++, Side.CLIENT);
        registerMessage(TickratePacket.class, i++, Side.CLIENT);
        registerMessage(ProspectingPacket.class, i++, Side.CLIENT);
        registerMessage(TileEntityNBTPacket.class, i++, Side.CLIENT);
        registerMessage(SyncHPCAVariablesPacket.class, i++, Side.CLIENT);
        registerMessage(ContainerRollBACK.class, i++, Side.CLIENT);
        registerMessage(GetTileEntityNBTRequestPacket.class, i++, Side.SERVER);
        registerMessage(TeleportRequestPacket.class, i++, Side.SERVER);
        registerMessage(KeyBindingHandler.class, i++, Side.SERVER);
        registerMessage(WirelessPickBlock.class, i++, Side.SERVER);
        registerMessage(ContainerRollBACK.class, i++, Side.SERVER);
        registerMessage(SudoPacket.class, i++, Side.CLIENT);
        registerMessage(NBTUpdatePacket.class, i++, Side.SERVER);
        registerMessage(PortableInfinityChestSyncPacket.class, i++, Side.SERVER);
        registerMessage(PktPatternTermUploadPattern.class, i++, Side.SERVER);
        registerMessage(DirePatternHandler.class, i++, Side.SERVER);
        registerMessage(MEChiselSyncParallel.class, i++, Side.SERVER);
        registerMessage(MEChiselSyncParallel.class, i++, Side.CLIENT);
        registerMessage(PortableInfinityChestSyncPacket.class, i++, Side.CLIENT);
        registerMessage(StatusMessage.class, i++, Side.CLIENT);
        registerMessage(SyncCircuitNanitesPacket.class, i++, Side.CLIENT);
        registerMessage(SyncRecipePacket.class, i++, Side.CLIENT);
        registerMessage(PlaceItemInHotbarPacket.class, i++, Side.CLIENT);
        registerMessage(RequestGameProfilePacket.class, i++, Side.SERVER);
        registerMessage(SwitchToCustomGuiPacket.class, i++, Side.SERVER);
        if (Mods.EtFuturumRequiem.isModLoaded()) registerMessage(ElytraBoostPacket.class, i++, Side.SERVER);
    }

    private static <T extends IMessageHandler<T, IMessage> & IMessage> void registerMessage(Class<T> c, int i,
        Side side) {
        network.registerMessage(c, c, i, side);
    }
}
