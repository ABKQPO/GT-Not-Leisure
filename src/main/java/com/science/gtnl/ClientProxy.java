package com.science.gtnl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderLeashKnot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.client.GTNLInputHandler;
import com.science.gtnl.client.GTNLTooltipManager;
import com.science.gtnl.common.block.blocks.item.ItemBlockEternalGregTechWorkshopRender;
import com.science.gtnl.common.block.blocks.item.ItemBlockNanoPhagocytosisPlantRender;
import com.science.gtnl.common.block.blocks.tile.TileEntityArtificialStar;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;
import com.science.gtnl.common.block.blocks.tile.TileEntityLaserBeacon;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
import com.science.gtnl.common.block.blocks.tile.TileEntityWaterCandle;
import com.science.gtnl.common.entity.EntityPlayerLeashKnot;
import com.science.gtnl.common.entity.EntitySaddleSlime;
import com.science.gtnl.common.entity.EntitySteamRocket;
import com.science.gtnl.common.render.entity.NullPointerExceptionRender;
import com.science.gtnl.common.render.entity.SaddleSlimeRender;
import com.science.gtnl.common.render.entity.SteamRocketRender;
import com.science.gtnl.common.render.item.ItemBlockArtificialStarRender;
import com.science.gtnl.common.render.item.ItemMeteorMinerMachineRender;
import com.science.gtnl.common.render.item.ItemNullPointerExceptionRender;
import com.science.gtnl.common.render.item.ItemPlayerDollRenderer;
import com.science.gtnl.common.render.item.ItemSteamRocketRenderer;
import com.science.gtnl.common.render.item.ItemTwilightSwordRender;
import com.science.gtnl.common.render.tile.EternalGregTechWorkshopRenderer;
import com.science.gtnl.common.render.tile.LaserBeconRenderer;
import com.science.gtnl.common.render.tile.NanoPhagocytosisPlantRenderer;
import com.science.gtnl.common.render.tile.PlayerDollRenderer;
import com.science.gtnl.common.render.tile.RealArtificialStarRenderer;
import com.science.gtnl.common.render.tile.WaterCandleRenderer;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.SubscribeEventClientUtils;
import com.science.gtnl.utils.detrav.DetravScannerGUI;
import com.science.gtnl.utils.enums.GuiType;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortableAdvancedWorkbench;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortableAnvil;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortableBasicWorkbench;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortableChest;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortableEnchanting;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortableEnderChest;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortableFurnace;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortablePortableCompressedChest;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortablePortableInfinityChest;

import Forge.NullPointerException;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import fox.spiteful.avaritia.render.FancyHaloRenderer;
import gregtech.api.GregTechAPI;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.model.ModelRocketTier1;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        RenderingRegistry.registerBlockHandler(new WaterCandleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaterCandle.class, new WaterCandleRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserBeacon.class, new LaserBeconRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerDoll.class, new PlayerDollRenderer());
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.playerDoll), new ItemPlayerDollRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArtificialStar.class, new RealArtificialStarRenderer());
        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(BlockLoader.blockArtificialStarRender),
            new ItemBlockArtificialStarRender());

        MinecraftForgeClient.registerItemRenderer(ItemLoader.twilightSword, new ItemTwilightSwordRender());

        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(BlockLoader.nanoPhagocytosisPlantRender),
            new ItemBlockNanoPhagocytosisPlantRender(BlockLoader.nanoPhagocytosisPlantRender));
        ClientRegistry
            .bindTileEntitySpecialRenderer(TileEntityNanoPhagocytosisPlant.class, new NanoPhagocytosisPlantRenderer());

        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(BlockLoader.eternalGregTechWorkshopRender),
            new ItemBlockEternalGregTechWorkshopRender(BlockLoader.eternalGregTechWorkshopRender));
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileEntityEternalGregTechWorkshop.class,
            new EternalGregTechWorkshopRenderer());

        MinecraftForgeClient.registerItemRenderer(ItemLoader.testItem, new FancyHaloRenderer());
        MinecraftForgeClient.registerItemRenderer(ItemLoader.metaItem, new FancyHaloRenderer());

        if (MainConfig.enableAprilFool) {
            MinecraftForgeClient.registerItemRenderer(
                Item.getItemFromBlock(GregTechAPI.sBlockMachines),
                new ItemMeteorMinerMachineRender());
        }

        MinecraftForgeClient.registerItemRenderer(
            ItemLoader.steamRocket,
            new ItemSteamRocketRenderer(
                new EntitySteamRocket(ClientProxyCore.mc.theWorld),
                new ModelRocketTier1(),
                new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/rocketT1.png")));
        RenderingRegistry.registerEntityRenderingHandler(
            EntitySteamRocket.class,
            new SteamRocketRender(new ModelRocketTier1(), GalacticraftCore.ASSET_PREFIX, "rocketT1"));

        RenderingRegistry.registerEntityRenderingHandler(
            EntitySaddleSlime.class,
            new SaddleSlimeRender(new ModelSlime(16), new ModelSlime(0), 0.25f));

        RenderingRegistry.registerEntityRenderingHandler(EntityPlayerLeashKnot.class, new RenderLeashKnot());

        RenderingRegistry.registerEntityRenderingHandler(NullPointerException.class, new NullPointerExceptionRender());
        MinecraftForgeClient
            .registerItemRenderer(ItemLoader.nullPointerException, new ItemNullPointerExceptionRender());

        MinecraftForge.EVENT_BUS.register(ItemLoader.stick);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void completeInit(FMLLoadCompleteEvent event) {
        super.completeInit(event);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(new SubscribeEventClientUtils());
        MinecraftForge.EVENT_BUS.register(GTNLInputHandler.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(new SubscribeEventClientUtils());
        GuiContainerManager.addTooltipHandler(new GTNLTooltipManager());

    }

    @Override
    public void openProspectorGUI() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        player.openGui(
            ScienceNotLeisure.instance,
            GuiType.DetravScannerGUI.getID(),
            player.worldObj,
            (int) player.posX,
            (int) player.posY,
            (int) player.posZ);
    }

    @Override
    public EntityPlayer getEntityPlayerFromContext(MessageContext ctx) {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return switch (GuiType.getGuiType(ID)) {
            case DetravScannerGUI -> new DetravScannerGUI();
            case PortableBasicWorkBenchGUI -> new GuiPortableBasicWorkbench(player.inventory, world);
            case PortableAdvancedWorkBenchGUI -> new GuiPortableAdvancedWorkbench(
                new ContainerPortableAdvancedWorkbench(player.inventory, player.worldObj, player.getHeldItem()));
            case PortableFurnaceGUI -> new GuiPortableFurnace(player.inventory);
            case PortableAnvilGUI -> new GuiPortableAnvil(player.inventory, world);
            case PortableEnderChestGUI -> new GuiPortableEnderChest(player.inventory, player.getInventoryEnderChest());
            case PortableEnchantingGUI -> new GuiPortableEnchanting(player.inventory, world);
            case PortableCompressedChestGUI -> new GuiPortablePortableCompressedChest(
                player.getHeldItem(),
                player.inventory);
            case PortableInfinityChestGUI -> new GuiPortablePortableInfinityChest(
                player.getHeldItem(),
                player.inventory);
            case PortableCopperChestGUI -> new GuiPortableChest.Copper(player.inventory, player.getHeldItem());
            case PortableIronChestGUI -> new GuiPortableChest.Iron(player.inventory, player.getHeldItem());
            case PortableSilverChestGUI -> new GuiPortableChest.Silver(player.inventory, player.getHeldItem());
            case PortableSteelChestGUI -> new GuiPortableChest.Steel(player.inventory, player.getHeldItem());
            case PortableGoldenChestGUI -> new GuiPortableChest.Gold(player.inventory, player.getHeldItem());
            case PortableDiamondChestGUI -> new GuiPortableChest.Diamond(player.inventory, player.getHeldItem());
            case PortableCrystalChestGUI -> new GuiPortableChest.Crystal(player.inventory, player.getHeldItem());
            case PortableObsidianChestGUI -> new GuiPortableChest.Obsidian(player.inventory, player.getHeldItem());
            case PortableNetheriteChestGUI -> new GuiPortableChest.Netherite(player.inventory, player.getHeldItem());
            case PortableDarkSteelChestGUI -> new GuiPortableChest.DarkSteel(player.inventory, player.getHeldItem());
        };
    }
}
