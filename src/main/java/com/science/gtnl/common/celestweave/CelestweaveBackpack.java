package com.science.gtnl.common.celestweave;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.common.equipment.EquipmentMessages;
import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.item.items.PortableChestType;
import com.science.gtnl.common.item.items.PortableItem;
import com.science.gtnl.container.portableWorkbench.ContainerPortableChest;
import com.science.gtnl.container.portableWorkbench.ContainerPortableCompressedChest;
import com.science.gtnl.container.portableWorkbench.ContainerPortableEnderChest;
import com.science.gtnl.container.portableWorkbench.ContainerPortableInfinityChest;
import com.science.gtnl.container.portableWorkbench.PortableStorageHost;
import com.science.gtnl.utils.enums.GuiType;

public final class CelestweaveBackpack {

    private static final String[] CONTENT_KEYS = { "Items", "Contents" };
    private static final Set<PortableItem.PortableType> STORAGE = EnumSet.of(
        PortableItem.PortableType.ENDERCHEST,
        PortableItem.PortableType.COMPRESSEDCHEST,
        PortableItem.PortableType.INFINITYCHEST,
        PortableItem.PortableType.COPPER,
        PortableItem.PortableType.IRON,
        PortableItem.PortableType.SILVER,
        PortableItem.PortableType.STEEL,
        PortableItem.PortableType.GOLD,
        PortableItem.PortableType.DIAMOND,
        PortableItem.PortableType.CRYSTAL,
        PortableItem.PortableType.OBSIDIAN,
        PortableItem.PortableType.NETHERITE,
        PortableItem.PortableType.DARKSTEEL);

    private CelestweaveBackpack() {}

    public static boolean accepts(ItemStack stack) {
        return stack != null && stack.getItem() instanceof PortableItem
            && STORAGE.contains(PortableItem.getPortableType(stack));
    }

    public static boolean isBackpackArmor(ItemStack stack) {
        return stack != null && stack.getItem() instanceof CelestweaveArmor armor
            && armor.armorType == CelestweaveArmor.CHEST
            && CelestweaveData.installed(stack)
                .contains(CelestweaveModule.BACKPACK);
    }

    public static boolean blockedInStorage(ItemStack stack) {
        return isBackpackArmor(stack) || CelestweaveModule.of(stack) == CelestweaveModule.BACKPACK;
    }

    private static String portableId(ItemStack chest) {
        return chest != null && chest.hasTagCompound() ? chest.getTagCompound()
            .getString("PortableID") : "";
    }

    public static ItemStack store(ItemStack module) {
        if (CelestweaveModule.of(module) != CelestweaveModule.BACKPACK) return module;
        ItemStack chest = CelestweaveData.heldOf(module);
        if (chest == null || !accepts(chest)) return module;
        String id = PortableItem.ensurePortableID(chest);
        NBTTagCompound contents = new NBTTagCompound();
        NBTTagCompound tag = chest.getTagCompound();
        for (String key : CONTENT_KEYS) if (tag.hasKey(key)) {
            contents.setTag(key, tag.getTag(key));
            tag.removeTag(key);
        }
        CelestweaveBackpackData data = CelestweaveBackpackData.get();
        if (data == null) return module;
        if (!contents.hasNoTags() || data.read(id) == null) data.write(id, contents);
        return CelestweaveData.withHeld(module.copy(), chest);
    }

    public static ItemStack strip(ItemStack module) {
        if (CelestweaveModule.of(module) != CelestweaveModule.BACKPACK) return module;
        ItemStack chest = CelestweaveData.heldOf(module);
        if (chest == null || !chest.hasTagCompound()) return module;
        NBTTagCompound tag = chest.getTagCompound();
        boolean changed = false;
        for (String key : CONTENT_KEYS) if (tag.hasKey(key)) {
            tag.removeTag(key);
            changed = true;
        }
        return changed ? CelestweaveData.withHeld(module.copy(), chest) : module;
    }

    public static ItemStack bare(ItemStack chest) {
        if (chest == null || !chest.hasTagCompound()) return chest;
        ItemStack copy = null;
        for (String key : CONTENT_KEYS) if (chest.getTagCompound()
            .hasKey(key)) {
                if (copy == null) copy = chest.copy();
                copy.getTagCompound()
                    .removeTag(key);
            }
        return copy != null ? copy : chest;
    }

    public static String heldId(ItemStack module) {
        return CelestweaveModule.of(module) == CelestweaveModule.BACKPACK ? portableId(CelestweaveData.heldOf(module))
            : "";
    }

    public static void discard(String id) {
        CelestweaveBackpackData data = id.isEmpty() ? null : CelestweaveBackpackData.get();
        if (data != null) data.remove(id);
    }

    public static ItemStack restore(ItemStack module) {
        if (CelestweaveModule.of(module) != CelestweaveModule.BACKPACK) return module;
        ItemStack chest = CelestweaveData.heldOf(module);
        String id = portableId(chest);
        CelestweaveBackpackData data = id.isEmpty() ? null : CelestweaveBackpackData.get();
        NBTTagCompound contents = data != null ? data.read(id) : null;
        if (contents == null) return module;
        return CelestweaveData.withHeld(module.copy(), withContents(chest, contents));
    }

    private static ItemStack withContents(ItemStack chest, NBTTagCompound contents) {
        ItemStack copy = chest.copy();
        NBTTagCompound tag = copy.getTagCompound();
        for (String key : CONTENT_KEYS) if (contents.hasKey(key)) tag.setTag(
            key,
            contents.getTag(key)
                .copy());
        return copy;
    }

    private static ItemStack wornChest(EntityPlayer player) {
        ItemStack chest = CelestweaveData.piece(player, CelestweaveArmor.CHEST);
        return isBackpackArmor(chest) ? chest : null;
    }

    public static ItemStack wornBackpack(EntityPlayer player) {
        ItemStack chest = wornChest(player);
        ItemStack held = chest == null ? null : CelestweaveData.held(chest, CelestweaveModule.BACKPACK);
        return accepts(held) ? held : null;
    }

    private static ItemStack ensureId(EntityPlayer player) {
        ItemStack chest = wornChest(player);
        if (chest == null) return null;
        ItemStack[] modules = CelestweaveData.moduleStacks(chest);
        for (int i = 0; i < modules.length; i++) {
            if (CelestweaveModule.of(modules[i]) != CelestweaveModule.BACKPACK) continue;
            ItemStack held = CelestweaveData.heldOf(modules[i]);
            if (!accepts(held)) return null;
            if (!portableId(held).isEmpty()) return held;
            modules[i] = store(modules[i]);
            CelestweaveData.writeModuleStacks(chest, modules);
            return CelestweaveData.heldOf(modules[i]);
        }
        return null;
    }

    public static void open(EntityPlayerMP player) {
        ItemStack held = ensureId(player);
        if (held == null) {
            EquipmentMessages.hotbar(player, "item.gtnl.celestweave.message.no_backpack");
            return;
        }
        if (player.openContainer != player.inventoryContainer) player.closeScreen();
        CommonProxy.openGui(player, GuiType.CelestweaveBackpackGUI, null, player.worldObj, 0, 0, 0);
    }

    public static Object serverContainer(EntityPlayer player) {
        ItemStack held = wornBackpack(player);
        String id = portableId(held);
        if (held == null || id.isEmpty()) return null;
        PortableItem.PortableType type = PortableItem.getPortableType(held);
        CelestweaveBackpackData data = CelestweaveBackpackData.get();
        NBTTagCompound contents = data != null ? data.read(id) : null;
        ItemStack virtual = contents != null ? withContents(held, contents) : held.copy();
        PortableStorageHost host = new Host(id, type, held.getItem(), player.dimension);
        return container(player, type, virtual, host);
    }

    public static Object container(EntityPlayer player, PortableItem.PortableType type, ItemStack stack,
        PortableStorageHost host) {
        return switch (type) {
            case ENDERCHEST -> new ContainerPortableEnderChest(player.inventory, player.getInventoryEnderChest(), host);
            case COMPRESSEDCHEST -> new ContainerPortableCompressedChest(stack, player.inventory, host);
            case INFINITYCHEST -> new ContainerPortableInfinityChest(stack, player.inventory, host);
            default -> new ContainerPortableChest(player.inventory, stack, chestType(type), host);
        };
    }

    public static PortableChestType chestType(PortableItem.PortableType type) {
        return PortableChestType.valueOf(type.name());
    }

    public static PortableStorageHost clientHost() {
        return new PortableStorageHost() {

            @Override
            public boolean valid(EntityPlayer player) {
                return true;
            }

            @Override
            public void save(EntityPlayer player, IInventory inventory) {}

            @Override
            public boolean fromBackpack() {
                return true;
            }
        };
    }

    private record Host(String id, PortableItem.PortableType type, Item item, int dimension)
        implements PortableStorageHost {

        @Override
        public boolean valid(EntityPlayer player) {
            ItemStack held = wornBackpack(player);
            return held != null && player.dimension == dimension
                && id.equals(portableId(held))
                && PortableItem.getPortableType(held) == type;
        }

        @Override
        public void save(EntityPlayer player, IInventory inventory) {
            if (player.worldObj.isRemote || type == PortableItem.PortableType.ENDERCHEST) return;
            CelestweaveBackpackData data = CelestweaveBackpackData.get();
            if (data == null) return;
            ItemStack virtual = new ItemStack(item, 1, type.getMeta());
            type.saveInventory(virtual, inventory);
            NBTTagCompound contents = new NBTTagCompound();
            for (String key : CONTENT_KEYS) if (virtual.getTagCompound()
                .hasKey(key))
                contents.setTag(
                    key,
                    virtual.getTagCompound()
                        .getTag(key));
            data.write(id, contents);
        }

        @Override
        public boolean fromBackpack() {
            return true;
        }
    }
}
