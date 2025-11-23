package com.science.gtnl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.common.packet.NetWorkHandler;
import com.science.gtnl.common.recipe.gtnl.ExtremeExtremeEntityCrusherRecipes;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.MaterialLoader;
import com.science.gtnl.utils.SubscribeEventUtils;
import com.science.gtnl.utils.enums.GuiType;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.gui.ContainerDirePatternEncoder;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableAnvil;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableBasicWorkbench;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableChest;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableCompressedChest;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableEnchanting;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableEnderChest;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableFurnace;
import com.science.gtnl.utils.gui.portableWorkbench.ContainerPortableInfinityChest;
import com.science.gtnl.utils.gui.portableWorkbench.GuiPortableChest;
import com.science.gtnl.utils.machine.VMTweakHelper;
import com.science.gtnl.utils.recipes.CraftingUnitHandler;

import appeng.api.AEApi;
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
        if (Mods.MobsInfo.isModLoaded()) {
            MinecraftForge.EVENT_BUS.register(new ExtremeExtremeEntityCrusherRecipes());
        }
        FMLCommonHandler.instance()
            .bus()
            .register(new SubscribeEventUtils());

        NetWorkHandler.registerAllMessage();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        if (!ModList.VMTweak.isModLoaded() && MainConfig.enableVoidMinerTweak) {
            MinecraftForge.EVENT_BUS.register(new VMTweakHelper());
            FMLCommonHandler.instance()
                .bus()
                .register(new VMTweakHelper());
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

        // AltarStructure.registerAltarStructureInfo();
    }

    public void completeInit(FMLLoadCompleteEvent event) {
        MaterialLoader.loadCompleteInit();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return switch (GuiType.getGuiType(ID)) {
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
        };
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void openProspectorGUI() {
        // just Client code
    }

    public EntityPlayer getEntityPlayerFromContext(final MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }
}
