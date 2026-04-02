package com.science.gtnl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderLeashKnot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.science.gtnl.asm.GTNLEarlyCoreMod;
import com.science.gtnl.client.GTNLInputHandler;
import com.science.gtnl.client.GTNLTooltipManager;
import com.science.gtnl.client.gui.GuiActiveFormationPlane;
import com.science.gtnl.client.gui.GuiCustomPriority;
import com.science.gtnl.client.gui.GuiDirePatternEncoder;
import com.science.gtnl.client.gui.GuiMEChisel;
import com.science.gtnl.client.gui.GuiSuperInterface;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableAdvancedWorkbench;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableAnvil;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableBasicWorkbench;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableEnchanting;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableEnderChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableFurnace;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortablePortableCompressedChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortablePortableInfinityChest;
import com.science.gtnl.common.block.blocks.item.ItemBlockEternalGregTechWorkshopRender;
import com.science.gtnl.common.block.blocks.item.ItemBlockNanoPhagocytosisPlantRender;
import com.science.gtnl.common.block.blocks.tile.TileEntityArtificialStar;
import com.science.gtnl.common.block.blocks.tile.TileEntityBeamFormer;
import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.common.block.blocks.tile.TileEntityEnderElevator;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;
import com.science.gtnl.common.block.blocks.tile.TileEntityLaserBeacon;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperInterface;
import com.science.gtnl.common.block.blocks.tile.TileEntityWaterCandle;
import com.science.gtnl.common.command.CommandSpoce;
import com.science.gtnl.common.entity.EntityParticleBeam;
import com.science.gtnl.common.entity.EntityPlayerLeashKnot;
import com.science.gtnl.common.entity.EntitySaddleSlime;
import com.science.gtnl.common.entity.EntitySteamRocket;
import com.science.gtnl.common.packet.client.TitleDisplayHandler;
import com.science.gtnl.common.part.PartActiveFormationPlane;
import com.science.gtnl.common.part.PartSuperInterface;
import com.science.gtnl.common.render.SpoceRenderHandler;
import com.science.gtnl.common.render.entity.NullPointerExceptionRender;
import com.science.gtnl.common.render.entity.SaddleSlimeRender;
import com.science.gtnl.common.render.entity.SteamRocketRender;
import com.science.gtnl.common.render.item.ItemBlockArtificialStarRender;
import com.science.gtnl.common.render.item.ItemMeteorMinerMachineRender;
import com.science.gtnl.common.render.item.ItemNullPointerExceptionRender;
import com.science.gtnl.common.render.item.ItemPlayerDollRenderer;
import com.science.gtnl.common.render.item.ItemSteamRocketRenderer;
import com.science.gtnl.common.render.item.ItemTwilightSwordRender;
import com.science.gtnl.common.render.tile.EnderElevatorRenderer;
import com.science.gtnl.common.render.tile.EternalGregTechWorkshopRenderer;
import com.science.gtnl.common.render.tile.LaserBeconRenderer;
import com.science.gtnl.common.render.tile.NanoPhagocytosisPlantRenderer;
import com.science.gtnl.common.render.tile.PlayerDollRenderer;
import com.science.gtnl.common.render.tile.RealArtificialStarRenderer;
import com.science.gtnl.common.render.tile.WaterCandleRenderer;
import com.science.gtnl.container.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.detrav.DetravScannerGUI;
import com.science.gtnl.utils.enums.GuiType;
import com.science.gtnl.utils.event.SubscribeEventClientUtils;
import com.science.gtnl.utils.gui.NotificationTickHandler;

import Forge.NullPointerException;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.client.render.ItemRenderer;
import appeng.client.render.TESRWrapper;
import appeng.helpers.IPriorityHost;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.client.FMLClientHandler;
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
import gregtech.api.enums.Mods;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.model.ModelRocketTier1;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

public class ClientProxy extends CommonProxy {

    public static int waterCandleRenderID;
    public static int enderElevatorRenderID;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        ClientCommandHandler.instance.registerCommand(new CommandSpoce());

        waterCandleRenderID = RenderingRegistry.getNextAvailableRenderId();
        enderElevatorRenderID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(new EnderElevatorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnderElevator.class, new EnderElevatorRenderer());

        RenderingRegistry.registerBlockHandler(new WaterCandleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaterCandle.class, new WaterCandleRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserBeacon.class, new LaserBeconRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(
            TileEntityBeamFormer.class,
            new TESRWrapper(BlockLoader.beamFormer.getRenderer()));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockLoader.beamFormer), ItemRenderer.INSTANCE);

        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.direPatternEncoder), ItemRenderer.INSTANCE);

        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockLoader.meChisel), ItemRenderer.INSTANCE);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.superInterface), ItemRenderer.INSTANCE);

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerDoll.class, new PlayerDollRenderer());
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.playerDoll), new ItemPlayerDollRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArtificialStar.class, new RealArtificialStarRenderer());
        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(BlockLoader.artificialStarRender),
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

        if (Mods.Avaritia.isModLoaded()) registerFancyHaloRenderer();

        if (GTNLEarlyCoreMod.enableAprilFool) {
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
        FMLCommonHandler.instance()
            .bus()
            .register(new SubscribeEventClientUtils());
        MinecraftForge.EVENT_BUS.register(GTNLInputHandler.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(GTNLInputHandler.INSTANCE);
        GuiContainerManager.addTooltipHandler(new GTNLTooltipManager());

        MinecraftForge.EVENT_BUS.register(new TitleDisplayHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new TitleDisplayHandler());

        FMLCommonHandler.instance()
            .bus()
            .register(new NotificationTickHandler());

        MinecraftForge.EVENT_BUS.register(new SpoceRenderHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new SpoceRenderHandler());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ForgeDirection side = ForgeDirection.getOrientation(ID & 7);
        int guiID = ID >> 3;
        return switch (GuiType.getGuiType(guiID)) {
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
            case DirePatternEncoderGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof TileEntityDirePatternEncoder d) {
                    yield new GuiDirePatternEncoder(player.inventory, d);
                }
                yield null;
            }
            case PortableDarkSteelChestGUI -> new GuiPortableChest.DarkSteel(player.inventory, player.getHeldItem());
            case MEChiselGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof TileEntityMEChisel d) {
                    yield new GuiMEChisel(player.inventory, d);
                }
                yield null;
            }
            case SuperInterfaceGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof TileEntitySuperInterface si) {
                    yield new GuiSuperInterface(player.inventory, si);
                } else if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartSuperInterface si) {
                        yield new GuiSuperInterface(player.inventory, si);
                    }
                }
                yield null;
            }
            case ActiveFormationPlaneGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartActiveFormationPlane si) {
                        yield new GuiActiveFormationPlane(player.inventory, si);
                    }
                }
                yield null;
            }
            case CustomPriorityGUI -> {
                var t = world.getTileEntity(x, y, z);
                IPriorityHost priorityHost = null;
                GuiType type = null;

                if (t instanceof TileEntitySuperInterface si) {
                    priorityHost = si;
                    type = GuiType.SuperInterfaceGUI;
                } else if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartSuperInterface si) {
                        priorityHost = si;
                        type = GuiType.SuperInterfaceGUI;
                    } else if (part instanceof PartActiveFormationPlane si) {
                        priorityHost = si;
                        type = GuiType.ActiveFormationPlaneGUI;
                    }
                }

                if (priorityHost != null) {
                    yield new GuiCustomPriority(player.inventory, priorityHost, type);
                }
                yield null;
            }
        };
    }

    @Override
    public EntityPlayer getEntityPlayerFromContext(MessageContext ctx) {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public EntityParticleBeam particleBeam(double x, double y, double z, double masterX, double masterY, double masterZ,
        double radius, EntityParticleBeam oldBeam, boolean render) {
        EntityParticleBeam beam = oldBeam;
        boolean inRange = ParticleHandler.isInRange(masterX, masterY, masterZ, 256);
        if (beam == null || beam.isDead) {
            if (inRange) {
                beam = new EntityParticleBeam(x, y, z, masterX, masterY, masterZ, radius);
                FMLClientHandler.instance()
                    .getClient().effectRenderer.addEffect(beam);
            }
        } else if (!inRange) {
            beam.setDead();
            return null;
        } else {
            beam.update();
        }
        return beam;
    }

    public void registerFancyHaloRenderer() {
        MinecraftForgeClient.registerItemRenderer(ItemLoader.testItem, new FancyHaloRenderer());
        MinecraftForgeClient.registerItemRenderer(ItemLoader.metaItem, new FancyHaloRenderer());
    }
}
