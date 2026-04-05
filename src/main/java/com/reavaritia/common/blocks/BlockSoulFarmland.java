package com.reavaritia.common.blocks;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.common.blocks.item.ItemBlockSoulFarmland;
import com.reavaritia.utils.enums.ReAvaItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockSoulFarmland extends BlockFarmland {

    public IIcon topIcon;
    public IIcon sideIcon;

    public BlockSoulFarmland() {
        super();
        this.setBlockName("BlockSoulFarmland");
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setHardness(0.6F);
        this.setStepSound(soundTypeSand);
        this.setBlockTextureName(RESOURCE_ROOT_ID + ":" + "BlockSoulFarmland");
        GameRegistry.registerBlock(this, ItemBlockSoulFarmland.class, getUnlocalizedName());
        ReAvaItemList.SoulFarmland.set(new ItemStack(this, 1));
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.topIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":" + "BlockSoulFarmland_Top");
        this.sideIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":" + "BlockSoulFarmland_Side");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? topIcon : sideIcon;
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {}

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        world.scheduleBlockUpdate(x, y, z, this, 1);
        Block Block = world.getBlock(x, y + 1, z);

        for (int i = 1; i <= 3; i++) {
            int currentY = y + i;
            Block aboveBlock = world.getBlock(x, currentY, z);

            if (aboveBlock instanceof IGrowable growable && aboveBlock != Blocks.cactus && aboveBlock != Blocks.reeds) {
                if (growable.func_149851_a(world, x, currentY, z, world.isRemote)) {
                    growable.func_149853_b(world, rand, x, currentY, z);
                }
            }

            int accelerateTimes = 100;
            long maxTime = System.nanoTime() + 1000000;
            for (int j = 0; j < accelerateTimes; j++) {
                aboveBlock.updateTick(world, x, currentY, z, rand);
                if (System.nanoTime() > maxTime) {
                    break;
                }
            }
        }

        if (Block == Blocks.cactus || Block == Blocks.reeds) {
            growTallCrop(world, x, y + 1, z, Block);
        } else if (Block == Blocks.nether_wart) {
            growNetherWart(world, x, y + 1, z);
        }
    }

    @Override
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction,
        IPlantable plantable) {
        return true;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Blocks.soul_sand.getItemDropped(0, random, fortune);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {}

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {}

    private void growTallCrop(World world, int x, int y, int z, Block crop) {
        int height = 1;
        while (world.getBlock(x, y + height, z) == crop) {
            height++;
        }
        if (height < world.getHeight()) {
            world.setBlock(x, y + height, z, crop);
        }
    }

    private void growNetherWart(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 3) {
            world.setBlockMetadataWithNotify(x, y, z, meta + 1, 2);
        }
    }
}
