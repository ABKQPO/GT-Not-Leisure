package com.reavaritia;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.reavaritia.client.gui.GuiHandler;
import com.reavaritia.common.BlockLoader;
import com.reavaritia.common.ItemLoader;
import com.reavaritia.common.entity.EntityExtremeAnvil;
import com.reavaritia.common.items.BlazeSword;
import com.reavaritia.common.items.ChronarchsClock;
import com.reavaritia.common.packet.ExtremeAnvilPacket;
import com.reavaritia.utils.SubscribeEventUtils;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(
    modid = ReAvaritia.MODID,
    version = ReAvaritia.VERSION,
    name = ReAvaritia.MODNAME,
    dependencies = "required-before:sciencenotleisure;" + "after:eternalsingularity;",
    acceptedMinecraftVersions = "1.7.10")
public class ReAvaritia {

    @Mod.Instance(ModList.ModIds.RE_AVARITIA)
    public static ReAvaritia instance;

    public static final String MODID = ModList.ModIds.RE_AVARITIA;
    public static final String MODNAME = "ReAvaritia";
    public static final String VERSION = "1.0.0";
    public static final String Arthor = "HFstudio";
    public static final String RESOURCE_ROOT_ID = ModList.ModIds.RE_AVARITIA;
    public static final Logger LOG = LogManager.getLogger(ReAvaritia.MODID);

    public static SimpleNetworkWrapper network;

    @SidedProxy(clientSide = "com.reavaritia.ClientProxy", serverSide = "com.reavaritia.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(ReAvaritia.MODID);
        proxy.preInit(event);

        SubscribeEventUtils eventHandler = new SubscribeEventUtils();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance()
            .bus()
            .register(eventHandler);

        BlockLoader.registryBlocks();
        ItemLoader.registerItems();
        BlockLoader.registryAnotherData();
        BlazeSword.registerEntity();
        ChronarchsClock.registerEntity();
        EntityExtremeAnvil.registerEntity();

        network.registerMessage(ExtremeAnvilPacket.class, ExtremeAnvilPacket.class, 0, Side.SERVER);
    }
}
