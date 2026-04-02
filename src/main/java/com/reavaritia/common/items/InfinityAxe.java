package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.ItemStackWrapper;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class InfinityAxe extends ItemAxe {

    public static final int LEAF_RADIUS = 16;

    public InfinityAxe() {
        super(ToolHelper.INFINITY);
        setUnlocalizedName("InfinityAxe");
        setTextureName(RESOURCE_ROOT_ID + ":" + "InfinityAxe");
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        setMaxDamage(9999);
        ReAvaItemList.InfinityAxe.set(new ItemStack(this, 1));
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_InfinityAxe_00"));
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (player.isSneaking()) {
            World world = player.worldObj;
            Block targetBlock = world.getBlock(x, y, z);

            if (isLog(targetBlock)) {
                harvestTree(world, x, y, z, targetBlock, player);
                return true;
            }
        }
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking() && !world.isRemote) {
            processLeaves(world, player);
        }
        return stack;
    }

    public void harvestTree(World world, int x, int y, int z, Block logBlock, EntityPlayer player) {
        Object2IntMap<ItemStackWrapper> logCounts = new Object2IntOpenHashMap<>();
        Set<BlockPos> logs = new ObjectOpenHashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(new BlockPos(x, y, z));

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (logs.contains(pos)) continue;

            if (world.getBlock(pos.x, pos.y, pos.z) == logBlock) {
                List<ItemStack> drops = logBlock.getDrops(
                    world,
                    pos.x,
                    pos.y,
                    pos.z,
                    world.getBlockMetadata(pos.x, pos.y, pos.z),
                    EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, player.getHeldItem()));

                for (ItemStack stack : drops) {
                    ItemStackWrapper key = new ItemStackWrapper(stack);
                    logCounts.put(key, logCounts.getInt(key) + stack.stackSize);
                }

                world.setBlockToAir(pos.x, pos.y, pos.z);
                logs.add(pos);

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            queue.add(new BlockPos(pos.x + dx, pos.y + dy, pos.z + dz));
                        }
                    }
                }
            }
        }

        if (!logCounts.isEmpty()) {
            ToolHelper.generateMatterCluster(world, player, logCounts);
        }
    }

    public void processLeaves(World world, EntityPlayer player) {
        if (world.isRemote) return;

        Object2IntMap<ItemStackWrapper> leavesCount = new Object2IntOpenHashMap<>();
        ItemStack tool = player.getHeldItem();
        int silkTouchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, tool);

        int baseX = MathHelper.floor_double(player.posX);
        int baseY = MathHelper.floor_double(player.posY);
        int baseZ = MathHelper.floor_double(player.posZ);

        for (int dx = -LEAF_RADIUS; dx <= LEAF_RADIUS; dx++) {
            for (int dy = -LEAF_RADIUS; dy <= LEAF_RADIUS; dy++) {
                for (int dz = -LEAF_RADIUS; dz <= LEAF_RADIUS; dz++) {
                    int x = baseX + dx;
                    int y = baseY + dy;
                    int z = baseZ + dz;

                    Block block = world.getBlock(x, y, z);
                    int meta = world.getBlockMetadata(x, y, z);

                    if (block instanceof BlockLeaves) {
                        int harvestLevel = (int) this.efficiencyOnProperMaterial;
                        List<ItemStack> drops = block.getDrops(world, x, y, z, meta, harvestLevel);
                        world.setBlockToAir(x, y, z);

                        for (ItemStack stack : drops) {
                            if (shouldKeepItem(stack, silkTouchLevel)) {
                                ItemStackWrapper key = new ItemStackWrapper(stack);
                                leavesCount.put(key, leavesCount.getInt(key) + stack.stackSize);
                            }
                        }
                    }
                }
            }
        }

        if (!leavesCount.isEmpty()) {
            ToolHelper.generateMatterCluster(world, player, leavesCount);
        }
    }

    public boolean shouldKeepItem(ItemStack stack, int silkLevel) {
        if (silkLevel > 0) {
            return stack.getItem() == Item.getItemFromBlock(Blocks.leaves);
        } else {
            return true;
        }
    }

    public boolean isLog(Block block) {
        return block == Blocks.log || block == Blocks.log2;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumHelper.addRarity("COSMIC", EnumChatFormatting.RED, "Cosmic");
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }
}
