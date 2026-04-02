package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalHoe extends ItemHoe {

    public static final int RANGE = 8;

    public CrystalHoe() {
        super(ToolHelper.CRYSTAL);
        this.setUnlocalizedName("CrystalHoe");
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "CrystalHoe");
        this.setMaxDamage(8888);
        ReAvaItemList.CrystalHoe.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_CrystalHoe_00"));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        Block targetBlock = world.getBlock(x, y, z);

        if (player.isSneaking()) {
            handleAreaEffect(world, player);
            return true;
        } else {
            if (targetBlock instanceof IGrowable) {
                applyGrowth(world, x, y, z);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            handleAreaEffect(world, player);
        }
        return stack;
    }

    private void handleAreaEffect(World world, EntityPlayer player) {
        if (!world.isRemote) {
            boolean hasEffect = false;
            int range = RANGE;
            int baseY = (int) player.posY - 1;

            for (int dx = -range; dx <= range; dx++) {
                for (int dz = -range; dz <= range; dz++) {
                    for (int yOffset = 0; yOffset <= 1; yOffset++) {
                        int x = (int) player.posX + dx;
                        int z = (int) player.posZ + dz;
                        int y = baseY + yOffset;

                        Block block = world.getBlock(x, y, z);

                        if (block instanceof IGrowable) {
                            for (int i = 0; i < 3; i++) {
                                applyGrowth(world, x, y, z);
                            }
                        }

                        if (block instanceof BlockCrops) {
                            int meta = world.getBlockMetadata(x, y, z);
                            if (isMature(meta)) {
                                harvestCrop(world, x, y, z, player);
                                resetCrop(world, x, y, z);
                                hasEffect = true;
                            }
                        }
                    }
                }
            }

            if (hasEffect) {
                world.playAuxSFX(2005, (int) player.posX, baseY, (int) player.posZ, 0);
            }
        }
    }

    private boolean isMature(int meta) {
        return meta >= 7;
    }

    private void harvestCrop(World world, int x, int y, int z, EntityPlayer player) {
        Block crop = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        List<ItemStack> drops = crop.getDrops(world, x, y, z, meta, 0);

        if (crop == Blocks.wheat && meta >= 7) {
            drops.add(new ItemStack(Items.wheat_seeds, world.rand.nextInt(2) + 1));
        }

        for (ItemStack stack : drops) {
            if (!player.inventory.addItemStackToInventory(stack)) {
                EntityItem entityItem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
                world.spawnEntityInWorld(entityItem);
            }
        }

        world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(crop));
    }

    private void resetCrop(World world, int x, int y, int z) {
        Block crop = world.getBlock(x, y, z);
        if (crop instanceof BlockCrops) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }
    }

    private void applyGrowth(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof IGrowable growable) {
            if (growable.func_149851_a(world, x, y, z, world.isRemote)) {
                for (int i = 0; i < 3; i++) {
                    if (growable.func_149852_a(world, world.rand, x, y, z)) {
                        growable.func_149853_b(world, world.rand, x, y, z);
                    }
                }
                world.playAuxSFX(2005, x, y, z, 0);
            }
        }
    }
}
