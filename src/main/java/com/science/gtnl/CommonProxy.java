package com.science.gtnl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.client.gui.portableWorkbench.GuiPortableChest;
import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperInterface;
import com.science.gtnl.common.entity.EntityParticleBeam;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.common.packet.NetWorkHandler;
import com.science.gtnl.common.part.PartActiveFormationPlane;
import com.science.gtnl.common.part.PartSuperInterface;
import com.science.gtnl.common.recipe.gtnl.ExtremeExtremeEntityCrusherRecipes;
import com.science.gtnl.common.world.GTNLWorldgenloader;
import com.science.gtnl.common.world.VoidWorldHandler;
import com.science.gtnl.common.world.WorldListener;
import com.science.gtnl.container.ContainerActiveFormationPlane;
import com.science.gtnl.container.ContainerCustomPriority;
import com.science.gtnl.container.ContainerDirePatternEncoder;
import com.science.gtnl.container.ContainerMEChisel;
import com.science.gtnl.container.ContainerSuperInterface;
import com.science.gtnl.container.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.container.portableWorkbench.ContainerPortableAnvil;
import com.science.gtnl.container.portableWorkbench.ContainerPortableBasicWorkbench;
import com.science.gtnl.container.portableWorkbench.ContainerPortableChest;
import com.science.gtnl.container.portableWorkbench.ContainerPortableCompressedChest;
import com.science.gtnl.container.portableWorkbench.ContainerPortableEnchanting;
import com.science.gtnl.container.portableWorkbench.ContainerPortableEnderChest;
import com.science.gtnl.container.portableWorkbench.ContainerPortableFurnace;
import com.science.gtnl.container.portableWorkbench.ContainerPortableInfinityChest;
import com.science.gtnl.loader.MaterialLoader;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;
import com.science.gtnl.utils.event.SubscribeEventUtils;
import com.science.gtnl.utils.machine.VMTweakHelper;
import com.science.gtnl.utils.recipes.CraftingUnitHandler;

import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.container.ContainerOpenContext;
import appeng.helpers.IPriorityHost;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gregtech.api.enums.Mods;

public class CommonProxy implements IGuiHandler {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new SubscribeEventUtils());
        FMLCommonHandler.instance()
            .bus()
            .register(new SubscribeEventUtils());
        MinecraftForge.EVENT_BUS.register(new VoidWorldHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new VoidWorldHandler());
        MinecraftForge.TERRAIN_GEN_BUS.register(new GTNLWorldgenloader());

        NetWorkHandler.registerAllMessage();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new VMTweakHelper());
        FMLCommonHandler.instance()
            .bus()
            .register(new VMTweakHelper());

        FMLCommonHandler.instance()
            .bus()
            .register(new WorldListener());
        MinecraftForge.EVENT_BUS.register(new WorldListener());

        if (Mods.MobsInfo.isModLoaded()) {
            MinecraftForge.EVENT_BUS.register(new ExtremeExtremeEntityCrusherRecipes());
        }
        CraftingUnitHandler.register();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        var interfaceTerminal = AEApi.instance()
            .registries()
            .interfaceTerminal();
        interfaceTerminal.register(SuperCraftingInputHatchME.class);
        interfaceTerminal.register(AssemblerMatrix.class);
        interfaceTerminal.register(PartSuperInterface.class);
        interfaceTerminal.register(TileEntitySuperInterface.class);

        Upgrades.ADVANCED_BLOCKING.registerItem(GTNLItemList.PartSuperInterface.get(1), 1);
        Upgrades.CRAFTING.registerItem(GTNLItemList.PartSuperInterface.get(1), 1);
        Upgrades.FAKE_CRAFTING.registerItem(GTNLItemList.PartSuperInterface.get(1), 1);
        Upgrades.FUZZY.registerItem(GTNLItemList.PartSuperInterface.get(1), 1);
        Upgrades.LOCK_CRAFTING.registerItem(GTNLItemList.PartSuperInterface.get(1), 1);

        Upgrades.ADVANCED_BLOCKING.registerItem(GTNLItemList.SuperInterface.get(1), 1);
        Upgrades.CRAFTING.registerItem(GTNLItemList.SuperInterface.get(1), 1);
        Upgrades.FAKE_CRAFTING.registerItem(GTNLItemList.SuperInterface.get(1), 1);
        Upgrades.FUZZY.registerItem(GTNLItemList.SuperInterface.get(1), 1);
        Upgrades.LOCK_CRAFTING.registerItem(GTNLItemList.SuperInterface.get(1), 1);

        Upgrades.CAPACITY.registerItem(GTNLItemList.PartActiveFormationPlane.get(1), 5);
        Upgrades.FUZZY.registerItem(GTNLItemList.PartActiveFormationPlane.get(1), 1);
        Upgrades.INVERTER.registerItem(GTNLItemList.PartActiveFormationPlane.get(1), 1);

        // AltarStructure.registerAltarStructureInfo();
    }

    public void completeInit(FMLLoadCompleteEvent event) {
        MaterialLoader.loadCompleteInit();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ForgeDirection side = ForgeDirection.getOrientation(ID & 7);
        int guiID = ID >> 3;
        return switch (GuiType.getGuiType(guiID)) {
            case DetravScannerGUI -> null;
            case PortableBasicWorkBenchGUI -> new ContainerPortableBasicWorkbench(player, world, player.getHeldItem());
            case PortableAdvancedWorkBenchGUI -> new ContainerPortableAdvancedWorkbench(
                player.inventory,
                player.worldObj,
                player.getHeldItem());
            case PortableFurnaceGUI -> new ContainerPortableFurnace(
                player.inventory,
                player.worldObj,
                player.getHeldItem());
            case PortableAnvilGUI -> new ContainerPortableAnvil(player.inventory, player);
            case PortableEnderChestGUI -> new ContainerPortableEnderChest(
                player.inventory,
                player.getInventoryEnderChest());
            case PortableEnchantingGUI -> new ContainerPortableEnchanting(player.inventory, world);
            case PortableCompressedChestGUI -> new ContainerPortableCompressedChest(
                player.getHeldItem(),
                player.inventory);
            case PortableInfinityChestGUI -> new ContainerPortableInfinityChest(player.getHeldItem(), player.inventory);
            case PortableCopperChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.COPPER);
            case PortableIronChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.IRON);
            case PortableSilverChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.SILVER);
            case PortableSteelChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.STEEL);
            case PortableGoldenChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.GOLD);
            case PortableDiamondChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.DIAMOND);
            case PortableCrystalChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.CRYSTAL);
            case PortableObsidianChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.OBSIDIAN);
            case PortableNetheriteChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.NETHERITE);
            case PortableDarkSteelChestGUI -> new ContainerPortableChest(
                player.inventory,
                player.getHeldItem(),
                GuiPortableChest.GUI.DARKSTEEL);
            case DirePatternEncoderGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof TileEntityDirePatternEncoder d) {
                    yield new ContainerDirePatternEncoder(player.inventory, d);
                }
                yield null;
            }
            case MEChiselGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof TileEntityMEChisel d) {
                    yield new ContainerMEChisel(player.inventory, d);
                }
                yield null;
            }
            case SuperInterfaceGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof TileEntitySuperInterface si) {
                    var container = new ContainerSuperInterface(player.inventory, si);
                    ContainerOpenContext ctx = new ContainerOpenContext(si);
                    ctx.setWorld(world);
                    ctx.setX(x);
                    ctx.setY(y);
                    ctx.setZ(z);
                    ctx.setSide(side);
                    container.setOpenContext(ctx);
                    yield container;

                } else if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartSuperInterface si) {
                        var container = new ContainerSuperInterface(player.inventory, si);
                        ContainerOpenContext ctx = new ContainerOpenContext(si);
                        ctx.setWorld(world);
                        ctx.setX(x);
                        ctx.setY(y);
                        ctx.setZ(z);
                        ctx.setSide(side);
                        container.setOpenContext(ctx);
                        yield container;
                    }
                }
                yield null;
            }
            case ActiveFormationPlaneGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartActiveFormationPlane si) {
                        var container = new ContainerActiveFormationPlane(player.inventory, si);
                        ContainerOpenContext ctx = new ContainerOpenContext(si);
                        ctx.setWorld(world);
                        ctx.setX(x);
                        ctx.setY(y);
                        ctx.setZ(z);
                        ctx.setSide(side);
                        container.setOpenContext(ctx);
                        yield container;
                    }
                }
                yield null;
            }
            case CustomPriorityGUI -> {
                var t = world.getTileEntity(x, y, z);
                IPriorityHost priorityHost = null;
                if (t instanceof TileEntitySuperInterface si) {
                    priorityHost = si;
                } else if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartSuperInterface si) {
                        priorityHost = si;
                    } else if (part instanceof PartActiveFormationPlane si) {
                        priorityHost = si;
                    }
                }
                if (priorityHost != null) {
                    var container = new ContainerCustomPriority(player.inventory, priorityHost);
                    ContainerOpenContext ctx = new ContainerOpenContext(priorityHost);
                    ctx.setWorld(world);
                    ctx.setX(x);
                    ctx.setY(y);
                    ctx.setZ(z);
                    ctx.setSide(side);
                    container.setOpenContext(ctx);
                    yield container;
                }
                yield null;
            }
        };
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public EntityPlayer getEntityPlayerFromContext(final MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    public EntityParticleBeam particleBeam(double x, double y, double z, double masterX, double masterY, double masterZ,
        double radius, EntityParticleBeam oldBeam, boolean render) {
        return null;
    }

    public static void openGui(EntityPlayer player, GuiType type, ForgeDirection side, TileEntity tile) {
        if (tile == null) return;
        openGui(player, type, side, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }

    public static void openGui(EntityPlayer player, GuiType type, ForgeDirection side, World world, int x, int y,
        int z) {
        if (player == null || world == null) return;
        if (side == null) side = ForgeDirection.UNKNOWN;

        int fullID = (type.getID() << 3) | (side.ordinal() & 7);

        player.openGui(ScienceNotLeisure.instance, fullID, world, x, y, z);
    }
}
