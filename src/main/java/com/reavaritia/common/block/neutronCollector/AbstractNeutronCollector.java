package com.reavaritia.common.block.neutronCollector;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;
import static net.minecraft.block.BlockPistonBase.getPistonOrientation;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.reavaritia.ReAvaCreativeTabs;
import com.reavaritia.ReAvaritia;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractNeutronCollector extends BlockContainer {

    public IIcon bottom, front, sideLeft, sideRight, side, topFacingNorth, topFacingSouth, topFacingWest, topFacingEast;
    public final String blockName;

    public AbstractNeutronCollector(String blockName) {
        super(Material.iron);
        this.blockName = blockName;
        setStepSound(Block.soundTypeMetal);
        setHardness(20.0F);
        setBlockName(blockName);
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
    }

    public abstract String getTexturePrefix(); // e.g., "DenseNeutronCollector"

    public abstract TileEntity createCollectorTileEntity();

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return createCollectorTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityNeutronCollector machine) {
            int l = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            switch (l) {
                case 0 -> machine.setFacing(2);
                case 1 -> machine.setFacing(5);
                case 2 -> machine.setFacing(3);
                case 3 -> machine.setFacing(4);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
        float par8, float par9) {
        if (!world.isRemote) {
            player.openGui(ReAvaritia.instance, 0, world, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntityNeutronCollector collector = (TileEntityNeutronCollector) world.getTileEntity(x, y, z);
        if (collector != null) {
            ItemStack itemstack = collector.getStackInSlot(0);
            if (itemstack != null) {
                Random rand = world.rand;
                float f = rand.nextFloat() * 0.8F + 0.1F;
                float f1 = rand.nextFloat() * 0.8F + 0.1F;
                float f2 = rand.nextFloat() * 0.8F + 0.1F;

                while (itemstack.stackSize > 0) {
                    int j1 = rand.nextInt(21) + 10;
                    if (j1 > itemstack.stackSize) j1 = itemstack.stackSize;
                    itemstack.stackSize -= j1;

                    ItemStack spawnStack = new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage());
                    if (itemstack.hasTagCompound()) {
                        spawnStack.setTagCompound(
                            (NBTTagCompound) itemstack.getTagCompound()
                                .copy());
                    }

                    EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, spawnStack);
                    float f3 = 0.05F;
                    entityitem.motionX = rand.nextGaussian() * f3;
                    entityitem.motionY = rand.nextGaussian() * f3 + 0.2F;
                    entityitem.motionZ = rand.nextGaussian() * f3;
                    world.spawnEntityInWorld(entityitem);
                }
            }
            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        String prefix = getTexturePrefix();
        this.bottom = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_Bottom");
        this.front = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_Front");
        this.sideLeft = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_SideLeft");
        this.sideRight = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_SideRight");
        this.side = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_Side");
        this.topFacingNorth = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_Top_North");
        this.topFacingSouth = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_Top_South");
        this.topFacingWest = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_Top_West");
        this.topFacingEast = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + prefix + "_Top_East");
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityNeutronCollector machine = (TileEntityNeutronCollector) world.getTileEntity(x, y, z);
        int facing = (machine != null) ? machine.getFacing() : 2;

        if (side == 0) return bottom;
        if (side == 1) return getTopIconByFacing(facing);
        return switch (facing) {
            case 2 -> getFacingIcon(side, front, this.side, sideLeft, sideRight);
            case 3 -> getFacingIcon(side, this.side, front, sideRight, sideLeft);
            case 4 -> getFacingIcon(side, sideRight, sideLeft, front, this.side);
            case 5 -> getFacingIcon(side, sideLeft, sideRight, this.side, front);
            default -> this.side;
        };
    }

    @Override
    public IIcon getIcon(int side, int metadata) {
        int facing = getPistonOrientation(metadata);
        if (side == 0) return bottom;
        if (side == 1) return getTopIconByFacing(facing);
        return switch (side) {
            case 3 -> front;
            case 4 -> sideLeft;
            case 5 -> sideRight;
            default -> this.side;
        };
    }

    private IIcon getTopIconByFacing(int facing) {
        return switch (facing) {
            case 3 -> topFacingSouth;
            case 4 -> topFacingWest;
            case 5 -> topFacingEast;
            default -> topFacingNorth;
        };
    }

    private IIcon getFacingIcon(int side, IIcon front, IIcon back, IIcon left, IIcon right) {
        return switch (side) {
            case 2 -> front;
            case 4 -> left;
            case 5 -> right;
            default -> back;
        };
    }
}
