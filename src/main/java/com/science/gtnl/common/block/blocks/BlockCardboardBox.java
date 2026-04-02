package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockCardboardBox;
import com.science.gtnl.common.block.blocks.tile.TileEntityCardboardBox;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.CardboardBoxUtils;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Original Author: Mekanism
 * License: MIT
 * Original Code Date: 2014-01-13
 */
public class BlockCardboardBox extends BlockContainer {

    private static boolean testingPlace = false;

    public IIcon[] icons = new IIcon[6];

    public BlockCardboardBox() {
        super(Material.cloth);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        setHardness(0.5F);
        setResistance(1F);
        setBlockName("CardboardBox");
        GameRegistry.registerBlock(this, ItemBlockCardboardBox.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityCardboardBox.class, "CardboardBoxTileEntity");
        GTNLItemList.CardboardBox.set(new ItemStack(this, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons[0] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "CardboardBoxTop");
        icons[1] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "CardboardBoxSide");
        icons[2] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "CardboardBoxSideStorage");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) {
            return icons[0];
        } else {
            return meta == 0 ? icons[1] : icons[2];
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return testingPlace;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int facing, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote && entityplayer.isSneaking()) {
            ItemStack itemStack = new ItemStack(BlockLoader.cardboardBox);
            TileEntityCardboardBox tileEntity = (TileEntityCardboardBox) world.getTileEntity(x, y, z);

            if (tileEntity.storedData != null) {
                CardboardBoxUtils.BlockData data = tileEntity.storedData;

                testingPlace = true;

                if (!data.block.canPlaceBlockAt(world, x, y, z)) {
                    testingPlace = false;
                    return true;
                }

                testingPlace = false;

                if (data.block != null) {
                    data.meta = data.block.onBlockPlaced(world, x, y, z, facing, hitX, hitY, hitZ, data.meta);
                }

                world.setBlock(x, y, z, data.block, data.meta, 3);

                if (data.tileTag != null && world.getTileEntity(x, y, z) != null) {
                    data.updateLocation(x, y, z);
                    world.getTileEntity(x, y, z)
                        .readFromNBT(data.tileTag);
                }

                if (data.block != null) {
                    data.block.onBlockPlacedBy(world, x, y, z, entityplayer, new ItemStack(data.block, 1, data.meta));
                    data.block.onPostBlockPlaced(world, x, y, z, data.meta);
                }

                float motion = 0.7F;
                double motionX = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
                double motionY = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
                double motionZ = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;

                EntityItem entityItem = new EntityItem(world, x + motionX, y + motionY, z + motionZ, itemStack);

                world.spawnEntityInWorld(entityItem);
            }
        }

        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCardboardBox();
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntityCardboardBox tileEntity = (TileEntityCardboardBox) world.getTileEntity(x, y, z);

        ItemStack itemStack = new ItemStack(BlockLoader.cardboardBox, 1, world.getBlockMetadata(x, y, z));

        if (itemStack.getItemDamage() == 1) {
            if (tileEntity.storedData != null) {
                CardboardBoxUtils.setBlockData(itemStack, tileEntity.storedData);
            }
        }

        return itemStack;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (!player.capabilities.isCreativeMode && !world.isRemote && willHarvest) {
            float motion = 0.7F;
            double motionX = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
            double motionY = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
            double motionZ = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;

            EntityItem entityItem = new EntityItem(
                world,
                x + motionX,
                y + motionY,
                z + motionZ,
                getPickBlock(null, world, x, y, z, player));

            world.spawnEntityInWorld(entityItem);
        }

        return world.setBlockToAir(x, y, z);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(int i, Random random, int j) {
        return null;
    }
}
