package com.science.gtnl.common.packet;

import static com.science.gtnl.ScienceNotLeisure.network;

import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.packet.base.PayloadHandler;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;
import gregtech.api.enums.Mods;

public class NetWorkHandler {

    private static int nextPacketId;
    private static boolean initialized;

    public static synchronized void register() {
        if (initialized) return;
        initialized = true;
        nextPacketId = 0;
        registerClientbound(SoundPacket.class);
        registerClientbound(TickratePacket.class);
        registerClientbound(ProspectingPacket.class);
        registerClientbound(TileEntityNBTPacket.class);
        registerClientbound(SyncHPCAVariablesPacket.class);
        registerClientbound(RestorePreviousGuiPacket.class);
        registerServerbound(GetTileEntityNBTRequestPacket.class);
        registerServerbound(TeleportRequestPacket.class);
        registerServerbound(KeyBindingHandler.class);
        registerServerbound(WirelessPickBlock.class);
        registerClientbound(SudoPacket.class);
        registerServerbound(NBTUpdatePacket.class);
        registerServerbound(PktPatternTermUploadPattern.class);
        registerServerbound(DirePatternHandler.class);
        registerServerbound(MEChiselSyncParallel.class);
        registerClientbound(MEChiselSyncParallel.Clientbound.class);
        registerClientbound(PortableInfinityChestSyncPacket.class);
        registerClientbound(StatusMessage.class);
        registerClientbound(SyncCircuitNanitesPacket.class);
        registerClientbound(DraconicArmorProjectionSyncPacket.class);
        registerClientbound(DraconicArmorProjectionHitEffectPacket.class);
        registerClientbound(PlaceItemInHotbarPacket.class);
        registerServerbound(RequestGameProfilePacket.class);
        registerServerbound(SwitchToCustomGuiPacket.class);
        registerServerbound(SwitchSuperDualInterfaceGuiPacket.class);
        registerClientbound(SuperDualInterfaceFluidSyncPacket.class);
        if (Mods.EtFuturumRequiem.isModLoaded()) registerServerbound(ElytraBoostPacket.class);
    }

    public static <T extends ClientboundPacket> void registerClientbound(Class<T> packet) {
        registerClientbound(PayloadHandler.Client.class, packet);
    }

    public static <T extends ServerboundPacket> void registerServerbound(Class<T> packet) {
        registerServerbound(PayloadHandler.Server.class, packet);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T extends ClientboundPacket> void registerClientbound(Class<? extends IMessageHandler> handler,
        Class<T> packet) {
        network.registerMessage((Class) handler, packet, nextPacketId++, Side.CLIENT);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T extends ServerboundPacket> void registerServerbound(Class<? extends IMessageHandler> handler,
        Class<T> packet) {
        network.registerMessage((Class) handler, packet, nextPacketId++, Side.SERVER);
    }
}
