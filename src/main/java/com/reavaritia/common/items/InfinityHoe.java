package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.ItemStackWrapper;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class InfinityHoe extends ItemHoe {

    public InfinityHoe() {
        super(ToolHelper.INFINITY);
        setUnlocalizedName("InfinityHoe");
        setTextureName(RESOURCE_ROOT_ID + ":" + "InfinityHoe");
        this.setCreativeTab(CreativeTabs.tabTools);
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setMaxDamage(9999);
        ReAvaItemList.InfinityHoe.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_InfinityHoe_00"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                createFarmland(world, player); // 潜行右键生成耕地
            } else {
                handleCropOperation(world, player); // 普通右键操作作物
            }
        }
        return stack;
    }

    public void createFarmland(World world, EntityPlayer player) {
        if (!world.isRemote) {
            int baseX = (int) player.posX;
            int baseY = (int) player.posY - 1;
            int baseZ = (int) player.posZ;
            int radius = 4;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int targetX = baseX + dx;
                    int targetZ = baseZ + dz;

                    Block block = world.getBlock(targetX, baseY, targetZ);
                    if (block == Blocks.grass || block == Blocks.dirt) {
                        world.setBlock(targetX, baseY, targetZ, Blocks.farmland, 0, 3);
                        world.playAuxSFX(2001, targetX, baseY, targetZ, Block.getIdFromBlock(Blocks.farmland));
                    }
                }
            }
        }
    }

    public void handleCropOperation(World world, EntityPlayer player) {
        int range = 10;
        Object2IntMap<ItemStackWrapper> itemCounts = new Object2IntOpenHashMap<>();

        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    int x = (int) player.posX + dx;
                    int z = (int) player.posZ + dz;
                    int y = (int) player.posY + dy;

                    Block block = world.getBlock(x, y, z);
                    int meta = world.getBlockMetadata(x, y, z);

                    if (block instanceof IGrowable) {
                        applyGrowth(world, x, y, z);
                    }

                    if (isHarvestable(block, meta)) {
                        List<ItemStack> drops = harvestAndGetDrops(world, x, y, z);
                        resetBlock(world, x, y, z, block);

                        for (ItemStack stack : drops) {
                            ItemStackWrapper key = new ItemStackWrapper(stack);
                            itemCounts.put(key, itemCounts.getOrDefault(key, 0) + stack.stackSize);
                        }
                    }
                }
            }
        }

        if (!itemCounts.isEmpty()) {
            ToolHelper.generateMatterCluster(world, player, itemCounts);
        }
    }

    public List<ItemStack> harvestAndGetDrops(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        List<ItemStack> drops = new ArrayList<>();

        if (block == Blocks.melon_block) {
            drops.add(new ItemStack(Items.melon, 9));
        } else if (block == Blocks.pumpkin) {
            drops.add(new ItemStack(Blocks.pumpkin, 1));
        } else if (block == Blocks.nether_wart) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta >= 3) {
                drops.add(new ItemStack(Items.nether_wart, 2 + world.rand.nextInt(3)));
            }
        } else {
            drops = block.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        }

        return drops;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumHelper.addRarity("COSMIC", EnumChatFormatting.RED, "Cosmic");
    }

    public boolean isHarvestable(Block block, int meta) {
        // 西瓜/南瓜方块直接可收获
        if (block == Blocks.melon_block || block == Blocks.pumpkin) return true;
        // 可可豆成熟判断
        if (block instanceof BlockCocoa) return (meta & 0x3) >= 2;
        // 普通作物成熟判断
        if (block instanceof BlockCrops) return meta >= 7;
        // 下界疣成熟判断
        if (block == Blocks.nether_wart) return meta >= 3;
        return false;
    }

    public void resetBlock(World world, int x, int y, int z, Block original) {
        if (original instanceof BlockCrops) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 3); // 重置作物
        } else if (original instanceof BlockCocoa || original == Blocks.nether_wart) {
            world.setBlockToAir(x, y, z);
        } else {
            world.setBlockToAir(x, y, z);
        }
    }

    public void applyGrowth(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof IGrowable growable) {
            if (growable.func_149851_a(world, x, y, z, false)) {
                for (int i = 0; i < 3; i++) {
                    if (growable.func_149852_a(world, world.rand, x, y, z)) {
                        growable.func_149853_b(world, world.rand, x, y, z);
                    }
                }
                world.playAuxSFX(2005, x, y, z, 0);
            }
        } else if (block == Blocks.nether_wart) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta < 3) {
                world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
                world.playAuxSFX(2005, x, y, z, 0);
            }
        }
    }
}
