package com.science.gtnl.common.packet;

import static com.science.gtnl.ScienceNotLeisure.network;

import com.gtnllib.network.ClientboundPacket;
import com.gtnllib.network.PayloadHandler;
import com.gtnllib.network.ServerboundPacket;

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
        registerServerbound(OpenVeinMiningPickaxeGuiPacket.class);
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
        registerServerbound(OpenPortableCellWorkbenchSubGuiPacket.class);
        registerClientbound(SuperDualInterfaceFluidSyncPacket.class);
        if (Mods.EtFuturumRequiem.isModLoaded()) registerServerbound(ElytraBoostPacket.class);
        registerServerbound(ToggleWirelessCardPacket.class);
        registerServerbound(OpenWirelessCardPacket.class);
        registerClientbound(WirelessHighlightPacket.class);
        registerClientbound(WirelessVisualisationPacket.class);
        registerServerbound(MiniTerminalRequestPacket.class);
        registerClientbound(MiniTerminalUpdatePacket.class);
        registerServerbound(YogTerminalActionPacket.class);
        registerServerbound(CreativeScreenPacket.class);
        registerClientbound(YogTerminalSyncPacket.class);
        registerClientbound(YogTerminalStatePacket.class);
        registerServerbound(UniversalStorageBusActionPacket.class);
        registerServerbound(QuantumCraftingModePacket.class);
        registerClientbound(QuantumCraftingAvailabilityPacket.class);
        registerClientbound(QuantumCraftConfirmPacket.class);
        registerServerbound(RecursivePatternQueryPacket.class);
        registerClientbound(RecursivePatternResultPacket.class);
        registerClientbound(RailgunFireFxPacket.class);
        registerClientbound(RailgunRecoilFxPacket.class);
        registerServerbound(RailgunBeamTogglePacket.class);
        registerServerbound(OpenEquipmentGuiPacket.class);
        registerServerbound(CelestweaveKeyPacket.class);
        registerClientbound(RailgunBeamUpdatePacket.class);
        registerClientbound(RailgunBeamChainFxPacket.class);
        registerClientbound(RailgunChargeCapPacket.class);
        registerClientbound(CelestweaveFxPacket.class);
        registerClientbound(CelestweaveFlightPacket.class);
        registerServerbound(PortalGunSelectPacket.class);
        registerServerbound(PortalGunFavoritePacket.class);
        registerClientbound(CelestweaveFireballPacket.class);
        registerClientbound(CelestweaveConcealPacket.class);
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
