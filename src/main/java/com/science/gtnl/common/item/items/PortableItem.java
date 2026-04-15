package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.science.gtnl.CommonProxy;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.client.gui.portableWorkbench.GuiPortableChest;
import com.science.gtnl.utils.InventoryInfinityChest;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.Actionable;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.tile.misc.TileInterface;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.IDualInputHatchWithPattern;
import lombok.val;

public class PortableItem extends Item {

    public PortableItem() {
        super();
        this.setUnlocalizedName("PortableItem");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "PortableItem");
        this.setMaxStackSize(1);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setHasSubtypes(true);
        GameRegistry.registerItem(this, getUnlocalizedName());
        for (PortableType type : PortableType.values()) {
            GTNLItemList.valueOf(type.getUnlocalizedName())
                .set(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote && player.isSneaking()) {
            return this.tryMoveItems(world, x, y, z, player.getHeldItem(), player);
        } else {
            return false;
        }
    }

    public boolean tryMoveItems(World world, int x, int y, int z, ItemStack stack, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IInventory inventory) {
            boolean patternOnly = te instanceof IGregTechTileEntity gtTE
                && gtTE.getMetaTileEntity() instanceof IDualInputHatchWithPattern;
            val type = getPortableType(stack);
            val bagIInv = type.getInventory(stack);
            if (bagIInv == null) return false;
            val bagInv = new InvWrapper(bagIInv);
            if (tryMoveItemsToAE(te, bagInv, player)) {
                type.saveInventory(stack, bagIInv);
                return true;
            }
            val inv = new InvWrapper(inventory);

            for (int slot = 0; slot < bagInv.getSlots(); slot++) {
                var item = bagInv.getStackInSlot(slot);
                if (item == null) continue;
                if (patternOnly && !(item.getItem() instanceof ICraftingPatternItem)) continue;
                item = item.copy();
                for (int iSlot = 0; iSlot < inv.getSlots(); iSlot++) {
                    item = inv.insertItem(iSlot, item, false);
                    if (item == null) break;
                }
                if (item == null || bagInv.getStackInSlot(slot).stackSize != item.stackSize) {
                    bagInv.setStackInSlot(slot, item);
                }
            }
            type.saveInventory(stack, bagIInv);
            return true;
        }
        return false;
    }

    public boolean tryMoveItemsToAE(TileEntity te, InvWrapper bagInv, EntityPlayer player) {
        if (!(te instanceof TileInterface ae)) return false;
        val node = ae.getActionableNode();
        if (node == null) return false;
        val grid = node.getGrid();
        if (grid == null) return false;
        IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
        if (storageGrid == null) return false;
        val s = storageGrid.getItemInventory();
        val source = new PlayerSource(player, ae);
        for (int slot = 0; slot < bagInv.getSlots(); slot++) {
            val item = bagInv.getStackInSlot(slot);
            if (item == null) continue;
            IAEItemStack aeItem = AEItemStack.create(item);
            aeItem = s.injectItems(aeItem, Actionable.MODULATE, source);
            if (aeItem == null) {
                bagInv.setStackInSlot(slot, null);
            } else if (bagInv.getStackInSlot(slot).stackSize != aeItem.getStackSize()) {
                bagInv.getStackInSlot(slot).stackSize = (int) aeItem.getStackSize();
            }
        }
        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item.PortableItem." + itemStack.getItemDamage();
    }

    @Override
    public String getUnlocalizedName() {
        return "PortableItem";
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (PortableType type : PortableType.values()) {
            type.icon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + type.getUnlocalizedName());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        PortableType type = PortableType.byMeta(meta);
        return type != null ? type.icon : null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && !player.isSneaking()) {
            PortableType type = getPortableType(stack);
            if (type != null && type.getGuiID() >= 0) {
                CommonProxy.openGui(player, type.getGuiType(), null, world, 0, 0, 0);
            }
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity player, int slot, boolean isSelected) {
        if (world.isRemote) return;
        if (stack.getItemDamage() != PortableType.FURNACE.getMeta()) return;

        IInventory inv = getFurnaceInventory(stack);

        int burnTime = stack.hasTagCompound() ? stack.getTagCompound()
            .getInteger("BurnTime") : 0;
        int cookTime = stack.hasTagCompound() ? stack.getTagCompound()
            .getInteger("CookTime") : 0;
        int currentItemBurnTime = stack.hasTagCompound() ? stack.getTagCompound()
            .getInteger("CurrentItemBurnTime") : 0;

        ItemStack input = inv.getStackInSlot(0);
        ItemStack fuel = inv.getStackInSlot(1);
        ItemStack output = inv.getStackInSlot(2);

        if (burnTime > 0) {
            burnTime--;
        }

        boolean canSmelt = false;
        ItemStack result = null;
        if (input != null) {
            result = FurnaceRecipes.smelting()
                .getSmeltingResult(input);
            canSmelt = result != null && (output == null
                || (output.isItemEqual(result) && output.stackSize + result.stackSize <= output.getMaxStackSize()));
        }

        if (burnTime == 0) {
            currentItemBurnTime = 0;
            if (canSmelt && fuel != null && TileEntityFurnace.isItemFuel(fuel)) {
                currentItemBurnTime = burnTime = TileEntityFurnace.getItemBurnTime(fuel);
                if (fuel.stackSize == 1 && fuel.getItem()
                    .hasContainerItem(fuel)) {
                    inv.setInventorySlotContents(1, null);
                } else {
                    fuel.stackSize--;
                    inv.setInventorySlotContents(1, fuel);
                    if (fuel.stackSize <= 0) inv.setInventorySlotContents(1, null);
                }
            }
        }

        if (input == null || !canSmelt) {
            cookTime = 0;
        }

        if (burnTime > 0 && canSmelt) {
            cookTime++;
            if (cookTime >= 200) {
                if (output == null) {
                    inv.setInventorySlotContents(2, result.copy());
                } else {
                    output.stackSize += result.stackSize;
                    inv.setInventorySlotContents(2, output);
                }
                input.stackSize--;
                inv.setInventorySlotContents(0, input);
                if (input.stackSize <= 0) inv.setInventorySlotContents(0, null);
                cookTime = 0;
            }
        } else {
            cookTime = Math.max(0, cookTime - 1);
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound tag = stack.getTagCompound();
        tag.setInteger("CookTime", cookTime);
        tag.setInteger("BurnTime", burnTime);
        tag.setInteger("CurrentItemBurnTime", currentItemBurnTime);
        stack.setTagCompound(tag);
        saveFurnaceInventory.save(stack, inv);
    }

    public static PortableType getPortableType(@NotNull ItemStack stack) {
        return PortableType.byMeta(stack.getItemDamage());
    }

    public static String ensurePortableID(ItemStack stack) {
        if (stack == null) return null;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey("PortableID")) {
            tag.setString(
                "PortableID",
                UUID.randomUUID()
                    .toString());
        }
        return tag.getString("PortableID");
    }

    public static boolean matchesPortableID(ItemStack held, String targetID) {
        if (held == null || !(held.getItem() instanceof PortableItem)) return false;
        if (!held.hasTagCompound()) return false;
        NBTTagCompound tag = held.getTagCompound();
        return targetID != null && targetID.equals(tag.getString("PortableID"));
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (PortableType type : PortableType.values()) {
            list.add(new ItemStack(item, 1, type.ordinal()));
        }
    }

    @SubscribeEvent
    public void onItemEntityDamage(LivingHurtEvent event) {
        if (!(event.entity instanceof EntityItem entityItem)) return;
        ItemStack stack = entityItem.getEntityItem();
        if (stack == null) return;

        if (!(stack.getItem() instanceof PortableItem)) return;

        int meta = stack.getItemDamage();

        boolean isProtected = meta == PortableType.OBSIDIAN.getMeta() || meta == PortableType.NETHERITE.getMeta()
            || meta == PortableType.DARKSTEEL.getMeta();

        if (!isProtected) return;

        DamageSource source = event.source;
        if (source.isFireDamage() || source.isExplosion()) {
            event.setCanceled(true);
        }
    }

    @FunctionalInterface
    public interface PortableInventory {

        PortableInventory NULL = s -> null;

        IInventory get(ItemStack stack);
    }

    @FunctionalInterface
    public interface PortableInventorySave {

        void save(@NotNull ItemStack stack, @NotNull IInventory inv);
    }

    public static IInventory getInventory(ItemStack stack, int size) {
        InventoryBasic inv = new InventoryBasic("PortableAdvancedWorkbench", false, size);

        if (stack.hasTagCompound()) {
            NBTTagList list = stack.getTagCompound()
                .getTagList("Items", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound itemTag = list.getCompoundTagAt(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < inv.getSizeInventory()) {
                    inv.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
                }
            }
        }

        return inv;
    }

    public static InventoryInfinityChest getInfinityInventory(@NotNull ItemStack stack) {
        InventoryInfinityChest inv = new InventoryInfinityChest(
            getPortableType(stack) == PortableType.INFINITYCHEST ? Integer.MAX_VALUE : 64);
        if (!stack.hasTagCompound()) return inv;
        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey("Contents")) return inv;

        NBTTagList list = compound.getTagList("Contents", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound slotTag = list.getCompoundTagAt(i);
            int slot = slotTag.getShort("Slot");
            if (slot >= 0 && slot < inv.getSizeInventory()) {
                ItemStack slotStack = ItemStack.loadItemStackFromNBT(slotTag);
                if (slotStack != null) {
                    slotStack.stackSize = slotTag.getInteger("intCount");
                    if (slotTag.hasKey("tag")) {
                        slotStack.setTagCompound(slotTag.getCompoundTag("tag"));
                    }
                    inv.setInventorySlotContents(slot, slotStack);
                }
            }
        }
        return inv;
    }

    public static IInventory getFurnaceInventory(ItemStack stack) {
        InventoryBasic inv = new InventoryBasic("PortableFurnace", false, 3);

        if (stack.hasTagCompound()) {
            NBTTagList list = stack.getTagCompound()
                .getTagList("Items", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound itemTag = list.getCompoundTagAt(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < inv.getSizeInventory()) {
                    inv.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
                }
            }
        }

        return inv;
    }

    public static final PortableInventorySave saveInventory = (stack, inv) -> {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (s != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                s.writeToNBT(tag);
                list.appendTag(tag);
            }
        }

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setTag("Items", list);
    };

    public static final PortableInventorySave saveInfinityInventory = (stack, inv) -> {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slotStack = inv.getStackInSlot(i);
            if (slotStack != null) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setShort("Slot", (short) i);
                slotStack.writeToNBT(slotTag);
                slotTag.setInteger("intCount", slotStack.stackSize);
                if (slotStack.hasTagCompound()) {
                    slotTag.setTag("tag", slotStack.getTagCompound());
                }
                list.appendTag(slotTag);
            }
        }

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setTag("Contents", list);
    };

    public static final PortableInventorySave saveFurnaceInventory = (stack, inv) -> {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (s != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                s.writeToNBT(tag);
                list.appendTag(tag);
            }
        }

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setTag("Items", list);
    };

    public enum PortableType {

        BASIC("BasicWorkBench", GuiType.PortableBasicWorkBenchGUI),
        ADVANCED("AdvancedWorkBench", GuiType.PortableAdvancedWorkBenchGUI,
            stack -> PortableItem.getInventory(stack, 9)),
        FURNACE("Furnace", GuiType.PortableFurnaceGUI, PortableItem::getFurnaceInventory, saveFurnaceInventory),
        ANVIL("Anvil", GuiType.PortableAnvilGUI),
        ENDERCHEST("EnderChest", GuiType.PortableEnderChestGUI),
        ENCHANTING("EnchantingTable", GuiType.PortableEnchantingGUI),
        COMPRESSEDCHEST("CompressedChest", GuiType.PortableCompressedChestGUI, PortableItem::getInfinityInventory,
            saveInfinityInventory),
        INFINITYCHEST("InfinityChest", GuiType.PortableInfinityChestGUI, PortableItem::getInfinityInventory,
            saveInfinityInventory),
        COPPER("CopperChest", GuiType.PortableCopperChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.COPPER.getCapacity())),
        IRON("IronChest", GuiType.PortableIronChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.IRON.getCapacity())),
        SILVER("SilverChest", GuiType.PortableSilverChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.SILVER.getCapacity())),
        STEEL("SteelChest", GuiType.PortableSteelChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.STEEL.getCapacity())),
        GOLD("GoldenChest", GuiType.PortableGoldenChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.GOLD.getCapacity())),
        DIAMOND("DiamondChest", GuiType.PortableDiamondChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.DIAMOND.getCapacity())),
        CRYSTAL("CrystalChest", GuiType.PortableCrystalChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.CRYSTAL.getCapacity())),
        OBSIDIAN("ObsidianChest", GuiType.PortableObsidianChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.OBSIDIAN.getCapacity())),
        NETHERITE("NetheriteChest", GuiType.PortableNetheriteChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.NETHERITE.getCapacity())),
        DARKSTEEL("DarkSteelChest", GuiType.PortableDarkSteelChestGUI,
            stack -> PortableItem.getInventory(stack, GuiPortableChest.GUI.DARKSTEEL.getCapacity()));

        private final String baseName;
        public final GuiType gui;
        public IIcon icon;
        private final PortableInventory inventory;
        private final PortableInventorySave inventorySave;

        PortableType(String baseName, GuiType gui) {
            this.baseName = baseName;
            this.gui = gui;
            this.inventory = PortableInventory.NULL;
            this.inventorySave = null;
        }

        PortableType(String baseName, GuiType gui, @NotNull PortableInventory p) {
            this.baseName = baseName;
            this.gui = gui;
            this.inventory = p;
            this.inventorySave = saveInventory;
        }

        PortableType(String baseName, GuiType gui, @NotNull PortableInventory p, @NotNull PortableInventorySave s) {
            this.baseName = baseName;
            this.gui = gui;
            this.inventory = p;
            this.inventorySave = s;
        }

        public IInventory getInventory(ItemStack stack) {
            return inventory.get(stack);
        }

        public void saveInventory(ItemStack stack, IInventory inventory) {
            if (inventorySave != null) {
                inventorySave.save(stack, inventory);
            }
        }

        public int getMeta() {
            return ordinal();
        }

        public String getUnlocalizedName() {
            return "Portable" + baseName;
        }

        public static PortableType byMeta(int meta) {
            return (meta >= 0 && meta < values().length) ? values()[meta] : null;
        }

        public GuiType getGuiType() {
            return gui;
        }

        public int getGuiID() {
            return gui.getID();
        }
    }
}
