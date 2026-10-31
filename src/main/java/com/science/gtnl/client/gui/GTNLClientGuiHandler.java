package com.science.gtnl.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.client.celestweave.CelestweaveBackpackClient;
import com.science.gtnl.client.gui.detrav.DetravScannerGUI;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableAdvancedWorkbench;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableAnvil;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableBasicWorkbench;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableCellWorkbench;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableEnchanting;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableEnderChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableFurnace;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortablePortableCompressedChest;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortablePortableInfinityChest;
import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperDualInterface;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperInterface;
import com.science.gtnl.common.gui.container.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.common.gui.portableWorkbench.PortableCellWorkbenchGuiFactory;
import com.science.gtnl.common.item.cellworkbench.PortableCellWorkbenchHost;
import com.science.gtnl.common.part.PartActiveFormationPlane;
import com.science.gtnl.common.part.PartMECellDock;
import com.science.gtnl.common.part.PartSuperDualInterface;
import com.science.gtnl.common.part.PartSuperInterface;
import com.science.gtnl.common.part.PartUniversalExportBus;
import com.science.gtnl.common.part.PartUniversalImportBus;
import com.science.gtnl.common.part.PartUniversalStorageBus;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.client.gui.implementations.GuiCellRestriction;
import appeng.client.gui.implementations.GuiOreFilter;
import appeng.container.AEBaseContainer;
import appeng.helpers.IOreFilterable;
import appeng.helpers.IPriorityHost;
import appeng.parts.automation.PartSharedItemBus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GTNLClientGuiHandler {

    public static Object clientElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
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
            case PortableCellWorkbenchGUI -> createPortableCellWorkbenchGui(player, x);
            case PortableCellWorkbenchOreFilterGUI -> createPortableCellWorkbenchOreFilterGui(player, x);
            case PortableCellWorkbenchRestrictionGUI -> createPortableCellWorkbenchRestrictionGui(player, x);
            case CelestweaveBackpackGUI -> CelestweaveBackpackClient.gui(player);
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
            case SuperDualInterfaceGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof TileEntitySuperDualInterface si) {
                    yield new GuiSuperDualInterface(player.inventory, si);
                } else if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartSuperDualInterface si) {
                        yield new GuiSuperDualInterface(player.inventory, si);
                    }
                }
                yield null;
            }
            case SuperDualInterfaceFluidGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof TileEntitySuperDualInterface si) {
                    yield new GuiSuperDualInterfaceFluid(player.inventory, si);
                } else if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartSuperDualInterface si) {
                        yield new GuiSuperDualInterfaceFluid(player.inventory, si);
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
            case UniversalStorageBusGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof IPartHost host && host.getPart(side) instanceof PartUniversalStorageBus bus) {
                    yield new GuiUniversalStorageBus(player.inventory, bus);
                }
                yield null;
            }
            case UniversalBusGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof IPartHost host && host.getPart(side) instanceof PartSharedItemBus<?>bus
                    && (bus instanceof PartUniversalImportBus || bus instanceof PartUniversalExportBus)) {
                    yield new GuiUniversalBusIO(player.inventory, bus);
                }
                yield null;
            }
            case UniversalOreFilterGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof IPartHost host && host.getPart(side) instanceof IOreFilterable filterable
                    && (filterable instanceof PartUniversalStorageBus || filterable instanceof PartUniversalImportBus
                        || filterable instanceof PartUniversalExportBus)) {
                    yield new GuiOreFilter(player.inventory, filterable);
                }
                yield null;
            }
            case MECellDockGUI -> {
                var t = world.getTileEntity(x, y, z);
                if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartMECellDock dock) {
                        yield new GuiMECellDock(player.inventory, dock);
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
                } else if (t instanceof TileEntitySuperDualInterface si) {
                    priorityHost = si;
                    type = GuiType.SuperDualInterfaceGUI;
                } else if (t instanceof IPartHost host) {
                    IPart part = host.getPart(side);
                    if (part instanceof PartSuperInterface si) {
                        priorityHost = si;
                        type = GuiType.SuperInterfaceGUI;
                    } else if (part instanceof PartSuperDualInterface si) {
                        priorityHost = si;
                        type = GuiType.SuperDualInterfaceGUI;
                    } else if (part instanceof PartActiveFormationPlane si) {
                        priorityHost = si;
                        type = GuiType.ActiveFormationPlaneGUI;
                    } else if (part instanceof PartMECellDock dock) {
                        priorityHost = dock;
                        type = GuiType.MECellDockGUI;
                    } else if (part instanceof PartUniversalStorageBus bus) {
                        priorityHost = bus;
                        type = GuiType.UniversalStorageBusGUI;
                    }
                }

                if (priorityHost != null) {
                    yield new GuiCustomPriority(player.inventory, priorityHost, type);
                }
                yield null;
            }
        };
    }

    private static Object createPortableCellWorkbenchGui(EntityPlayer player, int inventorySlot) {
        PortableCellWorkbenchHost host = createPortableCellWorkbenchHost(player, inventorySlot);
        return host == null ? null : new GuiPortableCellWorkbench(player.inventory, host);
    }

    private static Object createPortableCellWorkbenchOreFilterGui(EntityPlayer player, int inventorySlot) {
        PortableCellWorkbenchHost host = createPortableCellWorkbenchHost(player, inventorySlot);
        if (host == null) return null;

        GuiOreFilter gui = new GuiOreFilter(player.inventory, host);
        PortableCellWorkbenchGuiFactory.configureSubGui((AEBaseContainer) gui.inventorySlots, host);
        return gui;
    }

    private static Object createPortableCellWorkbenchRestrictionGui(EntityPlayer player, int inventorySlot) {
        PortableCellWorkbenchHost host = createPortableCellWorkbenchHost(player, inventorySlot);
        if (host == null) return null;

        GuiCellRestriction gui = new GuiCellRestriction(player.inventory, host);
        PortableCellWorkbenchGuiFactory.configureSubGui((AEBaseContainer) gui.inventorySlots, host);
        return gui;
    }

    private static PortableCellWorkbenchHost createPortableCellWorkbenchHost(EntityPlayer player, int inventorySlot) {
        if (inventorySlot < 0 || inventorySlot >= player.inventory.getSizeInventory()) return null;
        if (player.inventory.getStackInSlot(inventorySlot) == null || player.inventory.getStackInSlot(inventorySlot)
            .getItem() != ItemLoader.portableCellWorkbenchItem) return null;
        return new PortableCellWorkbenchHost(player.inventory, ItemLoader.portableCellWorkbenchItem, inventorySlot);
    }
}
