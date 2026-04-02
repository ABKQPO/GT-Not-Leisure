package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.common.ItemLoader;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.ItemStackWrapper;
import com.reavaritia.utils.item.ToolHelper;

import codechicken.lib.math.MathHelper;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.render.ICosmicRenderItem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Optional.Interface(iface = "fox.spiteful.avaritia.render.ICosmicRenderItem", modid = "Avaritia")
public class MatterCluster extends Item implements ICosmicRenderItem {

    public static final String MAINTAG = "clusteritems";
    public static final String LISTTAG = "items";
    public static final String ITEMTAG = "item";
    public static final String COUNTTAG = "count";
    public static final String MAINCOUNTTAG = "total";

    public static int capacity = 64 * 1024;

    public IIcon iconFull;
    public IIcon cosmicIcon;
    public IIcon cosmicIconFull;

    public MatterCluster() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("MatterCluster");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "MatterCluster");
        setCreativeTab(CreativeTabs.tabTools);
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        ReAvaItemList.MatterCluster.set(new ItemStack(this, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        super.registerIcons(ir);

        this.cosmicIcon = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "MatterCluster_Mask");

        this.iconFull = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "MatterCluster_Full");
        this.cosmicIconFull = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "MatterCluster_Full_Mask");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean debug) {
        tooltip.add(StatCollector.translateToLocal("Tooltip_MatterCluster_00"));
        if (!stack.hasTagCompound() || !stack.getTagCompound()
            .hasKey(MAINTAG)) {
            return;
        }
        NBTTagCompound clustertag = stack.getTagCompound()
            .getCompoundTag(MAINTAG);

        tooltip.add(
            clustertag.getInteger(MAINCOUNTTAG) + "/"
                + capacity
                + " "
                + StatCollector.translateToLocal("Tooltip_MatterCluster_Counter"));
        tooltip.add("");

        if (GuiScreen.isShiftKeyDown()) {
            NBTTagList list = clustertag.getTagList(LISTTAG, 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack countstack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(ITEMTAG));
                int count = tag.getInteger(COUNTTAG);
                if (countstack != null) {
                    tooltip.add(
                        countstack.getItem()
                            .getRarity(countstack).rarityColor + countstack.getDisplayName()
                            + EnumChatFormatting.GRAY
                            + " x "
                            + count);
                } else {
                    tooltip.add(EnumChatFormatting.RED + "DELETED" + EnumChatFormatting.GRAY + " x " + count);
                }
            }
        } else {
            tooltip.add(StatCollector.translateToLocal("Tooltip_MatterCluster_01"));
        }
    }

    public static ObjectArrayList<ItemStack> makeClusters(List<ItemStack> input) {
        Object2IntOpenHashMap<ItemStackWrapper> items = ToolHelper.collateMatterCluster(input);
        ObjectArrayList<ItemStack> clusters = new ObjectArrayList<>();
        ObjectArrayList<Entry<ItemStackWrapper, Integer>> itemlist = new ObjectArrayList<>(items.object2IntEntrySet());

        int currentTotal = 0;
        Object2IntOpenHashMap<ItemStackWrapper> currentItems = new Object2IntOpenHashMap<>();

        while (!itemlist.isEmpty()) {
            Entry<ItemStackWrapper, Integer> e = itemlist.get(0);
            ItemStackWrapper wrap = e.getKey();
            int wrapcount = e.getValue();

            int count = Math.min(capacity - currentTotal, wrapcount);

            currentItems.put(wrap, currentItems.getInt(wrap) + count);
            currentTotal += count;

            e.setValue(wrapcount - count);

            if (e.getValue() == 0) {
                itemlist.remove(0);
            }

            if (currentTotal == capacity) {
                ItemStack cluster = makeCluster(currentItems);
                clusters.add(cluster);

                currentTotal = 0;
                currentItems.clear();
            }
        }

        if (currentTotal > 0) {
            ItemStack cluster = makeCluster(currentItems);
            clusters.add(cluster);
        }

        return clusters;
    }

    public static ItemStack makeCluster(Object2IntOpenHashMap<ItemStackWrapper> input) {
        ItemStack cluster = new ItemStack(ItemLoader.MatterCluster);
        int total = 0;
        for (int num : input.values()) {
            total += num;
        }
        setClusterData(cluster, input, total);
        return cluster;
    }

    public static Object2IntOpenHashMap<ItemStackWrapper> getClusterData(ItemStack cluster) {
        if (!cluster.hasTagCompound() || !cluster.getTagCompound()
            .hasKey(MAINTAG)) {
            return new Object2IntOpenHashMap<>();
        }
        NBTTagCompound tag = cluster.getTagCompound()
            .getCompoundTag(MAINTAG);
        NBTTagList list = tag.getTagList(LISTTAG, 10);
        Object2IntOpenHashMap<ItemStackWrapper> data = new Object2IntOpenHashMap<>();

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            final ItemStack stack = ItemStack.loadItemStackFromNBT(entry.getCompoundTag(ITEMTAG));
            if (stack == null) {
                continue;
            }
            ItemStackWrapper wrap = new ItemStackWrapper(stack);
            int count = entry.getInteger(COUNTTAG);
            data.put(wrap, count);
        }
        return data;
    }

    public static int getClusterSize(ItemStack cluster) {
        if (!cluster.hasTagCompound() || !cluster.getTagCompound()
            .hasKey(MAINTAG)) {
            return 0;
        }
        return cluster.getTagCompound()
            .getCompoundTag(MAINTAG)
            .getInteger(MAINCOUNTTAG);
    }

    public static void setClusterData(ItemStack stack, Object2IntMap<ItemStackWrapper> data, int count) {
        if (stack == null || data == null) return;

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound clustertag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        var it = data.object2IntEntrySet()
            .iterator();

        while (it.hasNext()) {
            Entry<ItemStackWrapper, Integer> entry = it.next();
            ItemStackWrapper key = entry.getKey();
            int value = entry.getValue();

            if (key == null || key.stack() == null || value <= 0) {
                it.remove();
                continue;
            }

            NBTTagCompound itemtag = new NBTTagCompound();
            itemtag.setTag(
                ITEMTAG,
                key.stack()
                    .writeToNBT(new NBTTagCompound()));
            itemtag.setInteger(COUNTTAG, value);
            list.appendTag(itemtag);
        }

        clustertag.setTag(LISTTAG, list);
        clustertag.setInteger(MAINCOUNTTAG, count);

        stack.getTagCompound()
            .setTag(MAINTAG, clustertag);
    }

    public static void mergeClusters(ItemStack donor, ItemStack recipient) {
        int donorcount = getClusterSize(donor);
        int recipientcount = getClusterSize(recipient);

        if (donorcount == 0 || donorcount == capacity || recipientcount == capacity) {
            return;
        }

        Object2IntOpenHashMap<ItemStackWrapper> donordata = getClusterData(donor);
        Object2IntOpenHashMap<ItemStackWrapper> recipientdata = getClusterData(recipient);

        donordata.object2IntEntrySet()
            .removeIf(
                e -> e.getKey() == null || e.getKey()
                    .stack() == null);
        recipientdata.object2IntEntrySet()
            .removeIf(
                e -> e.getKey() == null || e.getKey()
                    .stack() == null);

        var it = donordata.object2IntEntrySet()
            .iterator();

        while (recipientcount < capacity && donorcount > 0 && it.hasNext()) {
            Entry<ItemStackWrapper, Integer> e = it.next();
            ItemStackWrapper wrap = e.getKey();
            int wrapcount = e.getValue();

            if (wrap == null || wrap.stack() == null || wrapcount <= 0) {
                it.remove();
                continue;
            }

            int move = Math.min(capacity - recipientcount, wrapcount);

            recipientdata.put(wrap, recipientdata.getOrDefault(wrap, 0) + move);

            donorcount -= move;
            recipientcount += move;

            int left = wrapcount - move;
            if (left > 0) {
                e.setValue(left);
            } else {
                it.remove();
            }
        }

        setClusterData(recipient, recipientdata, recipientcount);

        if (donorcount > 0 && !donordata.isEmpty()) {
            setClusterData(donor, donordata, donorcount);
        } else {
            donor.setTagCompound(null);
            donor.stackSize = 0;
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            List<ItemStack> drops = ToolHelper.collateMatterClusterContents(MatterCluster.getClusterData(stack));

            for (ItemStack drop : drops) {
                ToolHelper.dropItem(
                    drop,
                    world,
                    MathHelper.floor_double(player.posX),
                    MathHelper.floor_double(player.posY),
                    MathHelper.floor_double(player.posZ));
            }
        }

        stack.stackSize = 0;
        return stack;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public IIcon getMaskTexture(ItemStack stack, EntityPlayer player) {
        int count = getClusterSize(stack);
        if (count == capacity) {
            return cosmicIconFull;
        }
        return cosmicIcon;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public float getMaskMultiplier(ItemStack stack, EntityPlayer player) {
        int count = getClusterSize(stack);
        return count / (float) capacity;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        int count = getClusterSize(stack);
        if (count == capacity) {
            return iconFull;
        }
        return super.getIcon(stack, pass);
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        return this.getIcon(stack, 0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int count = getClusterSize(stack);
        if (count == capacity) {
            return super.getUnlocalizedName(stack) + ".full";
        }
        return super.getUnlocalizedName(stack);
    }
}
