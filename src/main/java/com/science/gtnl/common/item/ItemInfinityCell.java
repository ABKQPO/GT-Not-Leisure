package com.science.gtnl.common.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.loader.ItemLoader.infinityCell;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.bsideup.jabel.Desugar;
import com.glodblock.github.api.FluidCraftAPI;
import com.glodblock.github.inventory.InventoryHandler;
import com.glodblock.github.inventory.gui.GuiType;
import com.glodblock.github.util.BlockPos;
import com.glodblock.github.util.NameConst;
import com.science.gtnl.client.GTNLCreativeTabs;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import appeng.core.sync.GuiBridge;
import appeng.items.storage.ItemCreativeStorageCell;
import appeng.util.Platform;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEFluidStackType;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class ItemInfinityCell extends ItemCreativeStorageCell {

    public static final long STORAGE_SIZE = Long.MAX_VALUE / 2;
    public static final String CUSTOM_TEXTURE_TAG = "CustomTexture";
    public static final String DEFAULT_ICON_KEY = "default";
    public static final String DEFAULT_TEXTURE_NAME = "InfinityCell";
    public static final String TOOLTIP_CELL_CONTENTS = NameConst.TT_KEY + "cell_contents";
    public static final String TOOLTIP_CELL_EMPTY = NameConst.TT_KEY + "cell_empty";
    public static final String TOOLTIP_INFINITY_FLUID_STORAGE = NameConst.TT_KEY + "infinity_fluid";
    public static final Map<String, IIcon> ICON_MAP = new Object2ObjectOpenHashMap<>();
    public static final Set<String> REGISTERED_TEXTURES = new ObjectOpenHashSet<>();
    public static final List<ItemStack> REGISTERED_CELLS = new ObjectArrayList<>();

    public ItemInfinityCell() {
        this.setTextureName(RESOURCE_ROOT_ID + ":" + DEFAULT_TEXTURE_NAME);
        this.setHasSubtypes(true);
        this.setUnlocalizedName("InfinityCell");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, getUnlocalizedName());
        AEApi.instance()
            .registries()
            .cell()
            .addCellHandler(new InfinityCellHandler());
    }

    @Override
    public void getCheckedSubItems(final Item sameItem, final CreativeTabs creativeTab, List<ItemStack> itemStacks) {
        itemStacks.addAll(REGISTERED_CELLS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        ICON_MAP.clear();
        for (String tex : REGISTERED_TEXTURES) {
            ICON_MAP.put(tex, register.registerIcon(RESOURCE_ROOT_ID + ":" + tex));
        }

        ICON_MAP.put(DEFAULT_ICON_KEY, register.registerIcon(RESOURCE_ROOT_ID + ":" + DEFAULT_TEXTURE_NAME));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (stack.hasTagCompound()) {
            String texName = stack.getTagCompound()
                .getString(CUSTOM_TEXTURE_TAG);
            IIcon icon = ICON_MAP.get(texName);
            if (icon != null) {
                return icon;
            }
        }
        return ICON_MAP.get(DEFAULT_ICON_KEY);
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        return getIcon(stack, 0);
    }

    public static ItemStack getSubItem(StorageChannel s, String unlocalizedName, String textureName,
        List<SubItem> subItemsList) {
        return getSubItem(s, unlocalizedName, textureName, subItemsList.toArray(new SubItem[subItemsList.size()]));
    }

    public static ItemStack getSubItem(StorageChannel s, SubItem... subItems) {
        return getSubItem(s, null, null, subItems);
    }

    public static ItemStack getSubItem(StorageChannel s, String unlocalizedName, String textureName,
        SubItem... subItems) {
        return getSubItemInternal(s, unlocalizedName, textureName, subItems);
    }

    public static ItemStack getSubItemInternal(StorageChannel s, String unlocalizedName, String textureName,
        SubItem... subItems) {
        var cell = new ItemStack(infinityCell);
        var tag = new NBTTagCompound();
        var list = new NBTTagList();

        if (textureName != null) {
            tag.setString(CUSTOM_TEXTURE_TAG, textureName);
            REGISTERED_TEXTURES.add(textureName);
        }

        if (s == StorageChannel.ITEMS) {
            tag.setString("t", "i");
            for (SubItem subItem : subItems) {
                var rtag = new NBTTagCompound();
                rtag.setString("id", subItem.id);
                rtag.setShort("Damage", subItem.Damage);
                if (subItem.nbt != null) {
                    rtag.setTag("tag", subItem.nbt);
                }
                list.appendTag(rtag);
            }
        } else {
            tag.setString("t", "f");
            for (SubItem subItem : subItems) {
                var rtag = new NBTTagCompound();
                rtag.setString("id", subItem.id);
                list.appendTag(rtag);
            }
        }
        tag.setTag("infinityList", list);

        if (unlocalizedName != null) {
            tag.setString("key", unlocalizedName);
        }

        cell.setTagCompound(tag);

        REGISTERED_CELLS.add(cell);

        return cell;
    }

    @Desugar
    public record SubItem(String id, short Damage, @Nullable NBTTagCompound nbt) {

        public static SubItem getInstance(FluidStack stack) {
            return getInstance(stack.getFluid());
        }

        public static SubItem getInstance(Fluid fluid) {
            return getInstance(fluid.getName());
        }

        public static SubItem getInstance(String fluidName) {
            return new SubItem(fluidName, (short) 0, null);
        }

        public static SubItem getInstance(String id, short Damege) {
            return new SubItem(id, Damege, null);
        }

        public static SubItem getInstance(String id, short Damege, NBTTagCompound nbt) {
            return new SubItem(id, Damege, nbt);
        }

        public static SubItem getInstance(ItemStack stack) {
            return getInstance(stack.getItem(), (short) stack.getItemDamage(), stack.getTagCompound());
        }

        public static SubItem getInstance(Item item, short Damege, NBTTagCompound nbt) {
            return new SubItem(Item.itemRegistry.getNameForObject(item), Damege, nbt);
        }

        public static SubItem getInstance(Item item, short Damege) {
            return getInstance(item, Damege, null);
        }

        public static SubItem getInstance(Item item) {
            return getInstance(item, (short) 0, null);
        }

        public static SubItem getInstance(Block block, short Damege, NBTTagCompound nbt) {
            return new SubItem(Block.blockRegistry.getNameForObject(block), Damege, nbt);
        }

        public static SubItem getInstance(Block block, short Damege) {
            return getInstance(block, Damege, null);
        }

        public static SubItem getInstance(Block block) {
            return getInstance(block, (short) 0, null);
        }

    }

    public List<? extends IAEStack<?>> getRecord(@NotNull ItemStack stack, @NotNull StorageChannel s) {
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound()
                .hasKey("infinityList")) {
                return getInfinityStack(
                    stack.getTagCompound()
                        .getTagList("infinityList", 10),
                    s);
            }
        }
        return Collections.emptyList();
    }

    private List<? extends IAEStack<?>> getInfinityStack(NBTTagList list, @NotNull StorageChannel s) {
        return switch (s) {
            case ITEMS -> {
                List<IAEItemStack> out = new ObjectArrayList<>();
                for (int i = 0; i < list.tagCount(); i++) {
                    var tag = list.getCompoundTagAt(i);
                    var item = (Item) Item.itemRegistry.getObject(tag.getString("id"));
                    if (item == null) {
                        yield Collections.emptyList();
                    }
                    final ItemStack itemstack = new ItemStack(item, 1, tag.getShort("Damage"));

                    if (tag.hasKey("tag", 10)) {
                        itemstack.stackTagCompound = tag.getCompoundTag("tag");
                    }
                    out.add(AEItemStack.create(itemstack));
                }
                yield out;
            }
            case FLUIDS -> {
                List<IAEFluidStack> out = new ObjectArrayList<>();
                for (int i = 0; i < list.tagCount(); i++) {
                    var tag = list.getCompoundTagAt(i);
                    String fluidName = tag.getString("id");
                    if (fluidName == null) {
                        yield Collections.emptyList();
                    }
                    var fluid = FluidRegistry.getFluid(fluidName);
                    if (fluid == null) {
                        yield Collections.emptyList();
                    }
                    FluidStack stack = new FluidStack(fluid, 1);
                    out.add(AEFluidStack.create(stack));
                }
                yield out;
            }
        };
    }

    public StorageChannel getChannel(ItemStack stack) {
        if (stack.getItem() instanceof ItemInfinityCell && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();

            String type = tag.getString("t");
            if (type.equals("f")) {
                return StorageChannel.FLUIDS;
            }

            if (type.equals("i")) {
                return StorageChannel.ITEMS;
            }
        }
        return null;
    }

    @Override
    public boolean isEditable(final ItemStack is) {
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        var c = getChannel(stack);
        if (c != null && stack.hasTagCompound()) {
            var key = stack.getTagCompound()
                .getString("key");
            if (!key.isEmpty()) {
                return StatCollector
                    .translateToLocalFormatted("item.InfinityCell.name", StatCollector.translateToLocal(key));
            }
            var r = getRecord(stack, c);
            if (!r.isEmpty()) {
                return StatCollector.translateToLocalFormatted(
                    "item.InfinityCell.name",
                    c == StorageChannel.ITEMS ? ((IAEItemStack) r.get(0)).getItemStack()
                        .getDisplayName()
                        : ((IAEFluidStack) r.get(0)).getFluidStack()
                            .getLocalizedName());
            }
            return StatCollector.translateToLocal("item.InfinityCell.unknown");
        }
        return StatCollector.translateToLocal("item.InfinityCell.unknown");
    }

    @SideOnly(Side.CLIENT)
    public void addCheckedInformation(final ItemStack stack, final EntityPlayer player, final List<String> lines,
        final boolean displayMoreInfo) {
        if (GuiScreen.isCtrlKeyDown()) {
            if (stack.hasTagCompound()) {
                var c = getChannel(stack);
                if (c != null) {
                    var isItem = c == StorageChannel.ITEMS;
                    lines.add(
                        StatCollector
                            .translateToLocal(isItem ? "Tooltip_InfinityCell_Contents" : TOOLTIP_CELL_CONTENTS));
                    var list = getRecord(stack, c);
                    if (!list.isEmpty()) {
                        for (IAEStack<?> s : list) {
                            lines.add(
                                String.format(
                                    "  %s %s",
                                    StatCollector.translateToLocal(TOOLTIP_INFINITY_FLUID_STORAGE),
                                    isItem ? ((IAEItemStack) s).getItemStack()
                                        .getDisplayName()
                                        : ((IAEFluidStack) s).getFluidStack()
                                            .getLocalizedName()));
                        }
                        return;
                    }
                }
            }
            lines.add(StatCollector.translateToLocal(TOOLTIP_CELL_EMPTY));
        } else {
            lines.add(StatCollector.translateToLocal(NameConst.TT_CTRL_FOR_MORE));
        }
    }

    @Override
    public boolean isBlackListed(IAEStack<?> requestedAddition) {
        if (!(requestedAddition instanceof IAEFluidStack fluidStack)) {
            return false;
        }
        return fluidStack.getFluid() == null || FluidCraftAPI.instance()
            .isBlacklistedInStorage(
                fluidStack.getFluid()
                    .getClass());
    }

    @Override
    public double getIdleDrain(ItemStack is) {
        return 0;
    }

    @Override
    public int getTotalTypes(ItemStack cellItem) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public static class InfinityCellHandler implements ICellHandler {

        @Override
        public boolean isCell(ItemStack is) {
            return is != null && is.getItem() instanceof ItemInfinityCell;
        }

        @Override
        public IMEInventoryHandler<?> getCellInventory(ItemStack item, ISaveProvider host, StorageChannel s) {
            if (s != null) {
                var stack = infinityCell.getRecord(item, s);
                if (!stack.isEmpty()) {
                    return switch (s) {
                        case ITEMS -> new InfinityItemCellHandler((List<IAEItemStack>) stack);
                        case FLUIDS -> new InfinityFluidCellHandler(item, host, (List<IAEFluidStack>) stack);
                    };
                }
            }
            return null;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getTopTexture_Light() {
            return null;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getTopTexture_Medium() {
            return null;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getTopTexture_Dark() {
            return null;
        }

        @Override
        public void openChestGui(EntityPlayer player, IChestOrDrive chest, ICellHandler cellHandler,
            IMEInventoryHandler inv, ItemStack is, StorageChannel chan) {
            switch (inv.getChannel()) {
                case ITEMS -> Platform.openGUI(player, (TileEntity) chest, chest.getUp(), GuiBridge.GUI_ME);
                case FLUIDS -> InventoryHandler.openGui(
                    player,
                    ((TileEntity) chest).getWorldObj(),
                    new BlockPos((TileEntity) chest),
                    chest.getUp(),
                    GuiType.LEVEL_TERMINAL);
            }
        }

        @Override
        public int getStatusForCell(ItemStack is, IMEInventory handler) {
            return 2;
        }

        @Override
        public double cellIdleDrain(ItemStack is, IMEInventory handler) {
            return 0;
        }
    }

    public static class InfinityFluidCellHandler implements IMEInventoryHandler<IAEFluidStack> {

        private final List<IAEFluidStack> record;

        public InfinityFluidCellHandler(ItemStack ignoredItem, ISaveProvider ignoredContainer,
            List<IAEFluidStack> stack) {
            this.record = stack;
            this.record.forEach(fluid -> fluid.setStackSize(STORAGE_SIZE));
        }

        @Override
        public StorageChannel getChannel() {
            return StorageChannel.FLUIDS;
        }

        @Override
        public IAEStackType<?> getStackType() {
            return AEFluidStackType.FLUID_STACK_TYPE;
        }

        @Override
        public AccessRestriction getAccess() {
            return AccessRestriction.READ_WRITE;
        }

        public boolean matches(IAEFluidStack left, IAEFluidStack right) {
            return left != null && right != null && left.getFluid() == right.getFluid();
        }

        @Override
        public boolean isPrioritized(IAEFluidStack stack) {
            return record.stream()
                .anyMatch(fluid -> matches(fluid, stack));
        }

        @Override
        public boolean canAccept(IAEFluidStack stack) {
            return record.stream()
                .anyMatch(fluid -> matches(fluid, stack));
        }

        @Override
        public IItemList<IAEFluidStack> getAvailableItems(IItemList<IAEFluidStack> out, int iteration) {
            record.forEach(fluid -> out.add(fluid.copy()));
            return out;
        }

        @Override
        public IAEFluidStack getAvailableItem(@NotNull IAEFluidStack request, int iteration) {
            for (IAEFluidStack fluid : this.record) {
                if (matches(fluid, request)) {
                    return fluid.copy();
                }
            }
            return null;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public boolean validForPass(final int i) {
            return true;
        }

        @Override
        public IAEFluidStack injectItems(IAEFluidStack stack, Actionable mode, BaseActionSource src) {
            for (IAEFluidStack fluid : this.record) {
                if (matches(fluid, stack)) {
                    return null;
                }
            }
            return stack;
        }

        @Override
        public IAEFluidStack extractItems(IAEFluidStack stack, Actionable mode, BaseActionSource src) {
            for (IAEFluidStack fluid : this.record) {
                if (matches(fluid, stack)) {
                    return stack.copy();
                }
            }
            return null;
        }
    }

    public static class InfinityItemCellHandler implements IMEInventoryHandler<IAEItemStack> {

        private final List<IAEItemStack> record;

        private InfinityItemCellHandler(List<IAEItemStack> stack) {
            this.record = stack;
            this.record.forEach(i -> i.setStackSize(STORAGE_SIZE));
        }

        @Override
        public StorageChannel getChannel() {
            return StorageChannel.ITEMS;
        }

        @Override
        public AccessRestriction getAccess() {
            return AccessRestriction.READ_WRITE;
        }

        public boolean matches(IAEItemStack left, IAEItemStack right) {
            if (left == null || right == null) {
                return false;
            }
            ItemStack leftStack = left.getItemStack();
            ItemStack rightStack = right.getItemStack();
            return GTUtility.areStacksEqual(leftStack, rightStack);
        }

        @Override
        public boolean isPrioritized(IAEItemStack stack) {
            for (IAEItemStack item : this.record) {
                if (matches(item, stack)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canAccept(IAEItemStack stack) {
            for (IAEItemStack item : this.record) {
                if (matches(item, stack)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> out, int iteration) {
            record.forEach(item -> out.add(item.copy()));
            return out;
        }

        @Override
        public IAEItemStack getAvailableItem(@NotNull IAEItemStack request, int iteration) {
            for (IAEItemStack item : this.record) {
                if (matches(item, request)) {
                    return item.copy();
                }
            }
            return null;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public boolean validForPass(final int i) {
            return true;
        }

        @Override
        public IAEItemStack injectItems(IAEItemStack stack, Actionable mode, BaseActionSource src) {
            for (IAEItemStack item : this.record) {
                if (matches(item, stack)) {
                    return null;
                }
            }
            return stack;
        }

        @Override
        public IAEItemStack extractItems(final IAEItemStack stack, final Actionable mode, final BaseActionSource src) {
            for (IAEItemStack item : this.record) {
                if (matches(item, stack)) {
                    return stack.copy();
                }
            }
            return null;
        }
    }
}
