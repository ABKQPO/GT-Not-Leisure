package com.science.gtnl.loader;

import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderLeashKnot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.asm.GTNLEarlyCoreMod;
import com.science.gtnl.common.block.blocks.item.ItemBlockEternalGregTechWorkshopRender;
import com.science.gtnl.common.block.blocks.item.ItemBlockNanoPhagocytosisPlantRender;
import com.science.gtnl.common.block.blocks.tile.TileEntityArtificialStar;
import com.science.gtnl.common.block.blocks.tile.TileEntityEnderElevator;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;
import com.science.gtnl.common.block.blocks.tile.TileEntityLaserBeacon;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaTube;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
import com.science.gtnl.common.block.blocks.tile.TileEntityWaterCandle;
import com.science.gtnl.common.entity.EntityPlayerLeashKnot;
import com.science.gtnl.common.entity.EntitySaddleSlime;
import com.science.gtnl.common.entity.EntitySteamRocket;
import com.science.gtnl.common.render.entity.NullPointerExceptionRender;
import com.science.gtnl.common.render.entity.SaddleSlimeRender;
import com.science.gtnl.common.render.entity.SteamRocketRender;
import com.science.gtnl.common.render.item.AprilFoolMachineRender;
import com.science.gtnl.common.render.item.ItemBlockArtificialStarRender;
import com.science.gtnl.common.render.item.ItemNullPointerExceptionRender;
import com.science.gtnl.common.render.item.ItemPlayerDollRenderer;
import com.science.gtnl.common.render.item.ItemSteamRocketRenderer;
import com.science.gtnl.common.render.item.ItemTwilightSwordRender;
import com.science.gtnl.common.render.item.PigmeeFumoHeadRenderer;
import com.science.gtnl.common.render.item.PigmeeFumoItemRenderer;
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoModel;
import com.science.gtnl.common.render.tile.EnderElevatorRenderer;
import com.science.gtnl.common.render.tile.EternalGregTechWorkshopRenderer;
import com.science.gtnl.common.render.tile.LaserBeconRenderer;
import com.science.gtnl.common.render.tile.MultiEssentiaJarRenderer;
import com.science.gtnl.common.render.tile.MultiEssentiaTubeRenderer;
import com.science.gtnl.common.render.tile.NanoPhagocytosisPlantRenderer;
import com.science.gtnl.common.render.tile.PigmeeFumoRenderer;
import com.science.gtnl.common.render.tile.PlayerDollRenderer;
import com.science.gtnl.common.render.tile.RealArtificialStarRenderer;
import com.science.gtnl.common.render.tile.WaterCandleRenderer;

import Forge.NullPointerException;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.common.render.RenderInit;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.model.ModelRocketTier1;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import thaumcraft.client.renderers.tile.TileTubeBufferRenderer;

public class RenderLoader implements IContentLoader {

    public static int WATER_CANDLE_RENDER_ID;
    public static int ENDER_ELEVATOR_RENDER_ID;
    public static int MULTI_ESSENTIA_TUBE_RENDER_ID;

    @Override
    public String name() {
        return "render";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        ModelRegistry.registerModid(ScienceNotLeisure.MODID);
        MinecraftForge.EVENT_BUS.register(PigmeeFumoModel.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(PigmeeFumoModel.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new PigmeeFumoHeadRenderer());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientInit() {
        WATER_CANDLE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
        ENDER_ELEVATOR_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
        MULTI_ESSENTIA_TUBE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

        EnderElevatorRenderer enderElevatorRenderer = new EnderElevatorRenderer();
        RenderingRegistry.registerBlockHandler(enderElevatorRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnderElevator.class, enderElevatorRenderer);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.enderElevatorBlock), enderElevatorRenderer);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.enderElevatorSlab), enderElevatorRenderer);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.enderElevatorCarpet), enderElevatorRenderer);

        RenderingRegistry.registerBlockHandler(new WaterCandleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaterCandle.class, new WaterCandleRenderer());

        RenderingRegistry.registerBlockHandler(new MultiEssentiaTubeRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserBeacon.class, new LaserBeconRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMultiEssentiaJar.class, new MultiEssentiaJarRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMultiEssentiaTube.class, new TileTubeBufferRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerDoll.class, new PlayerDollRenderer());
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.playerDoll), new ItemPlayerDollRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPigmeeFumo.class, new PigmeeFumoRenderer());
        // GTNHLib draws and transforms the model; the wrapper only binds the block atlas it expects already bound.
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.pigmeeFumo), new PigmeeFumoItemRenderer());

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
        RenderInit.onAtlasStitched(NanoPhagocytosisPlantRenderer::reload);
        RenderInit.onAtlasStitched(EternalGregTechWorkshopRenderer::reload);

        if (GTNLEarlyCoreMod.enableAprilFool) {
            MinecraftForgeClient
                .registerItemRenderer(Item.getItemFromBlock(GregTechAPI.sBlockMachines), new AprilFoolMachineRender());
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
    }
}
