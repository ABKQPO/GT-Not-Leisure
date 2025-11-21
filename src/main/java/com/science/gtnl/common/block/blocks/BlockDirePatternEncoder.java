package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDirePatternEncoder extends BlockContainer {

    private static IIcon top, sides, bottom;

    public BlockDirePatternEncoder() {
        super(Material.iron);
        setHardness(50.0F);
        setResistance(2000.0F);
        setBlockName("DirePatternEncoder");
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, getUnlocalizedName());
        GTNLItemList.DirePatternEncoder.set(new ItemStack(this, 1));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        top = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "DirePatternEncoderTop");
        sides = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "DirePatternEncoderSide");
        bottom = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "DirePatternEncoderBottom");
    }

    @Override
    public IIcon getIcon(int side, int metadata) {
        if (side == 0) return bottom;
        if (side == 1) return top;
        return sides;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
        float par8, float par9) {
        if (!world.isRemote) {
            player.openGui(ScienceNotLeisure.instance, GuiType.DirePatternEncoderGUI.getID(), world, x, y, z);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDirePatternEncoder();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int wut) {
        TileEntityDirePatternEncoder craft = (TileEntityDirePatternEncoder) world.getTileEntity(x, y, z);

        if (craft != null) {
            for (int i = 1; i < 3; i++) {
                ItemStack itemstack = craft.getStackInSlot(i);

                if (itemstack != null && itemstack.stackSize > 0) {
                    float f = world.rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityitem = new EntityItem(
                        world,
                        x + f,
                        y + f1,
                        z + f2,
                        new ItemStack(itemstack.getItem(), itemstack.stackSize, itemstack.getItemDamage()));

                    if (itemstack.hasTagCompound()) {
                        entityitem.getEntityItem()
                            .setTagCompound(
                                (NBTTagCompound) itemstack.getTagCompound()
                                    .copy());
                    }

                    float f3 = 0.05F;
                    entityitem.motionX = world.rand.nextGaussian() * f3;
                    entityitem.motionY = world.rand.nextGaussian() * f3 + 0.2F;
                    entityitem.motionZ = world.rand.nextGaussian() * f3;
                    world.spawnEntityInWorld(entityitem);

                    itemstack.stackSize = 0;
                }

                world.func_147453_f(x, y, z, block); // updateNeighborsAboutBlockChange
            }
        }

        super.breakBlock(world, x, y, z, block, wut);
    }
}
