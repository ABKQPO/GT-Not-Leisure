package com.science.gtnl.common.block;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFluidBase extends BlockFluidClassic {

    public Fluid fluid;

    public BlockFluidBase(Fluid fluid, Material material) {
        super(fluid, material);
        this.fluid = fluid;
        this.setBlockName(fluid.getUnlocalizedName());
    }

    @SideOnly(Side.CLIENT)
    public IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.icons[1] : this.icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[] { iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + fluidName + "_still"),
            iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + fluidName + "_flow") };

        fluid.setIcons(icons[0], icons[1]);
    }

    @Override
    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
        if (world.getBlock(x, y, z)
            .getMaterial()
            .isLiquid()) {
            return false;
        }
        return super.canDisplace(world, x, y, z);
    }

    @Override
    public boolean displaceIfPossible(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z)
            .getMaterial()
            .isLiquid()) {
            return false;
        }
        return super.displaceIfPossible(world, x, y, z);
    }
}
