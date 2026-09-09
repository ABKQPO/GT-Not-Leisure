package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperDenseEnergyCell;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.config.PowerMultiplier;
import appeng.block.networking.BlockEnergyCell;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSuperDenseEnergyCell extends BlockEnergyCell {

    @SideOnly(Side.CLIENT)
    protected IIcon baseIcon;
    @SideOnly(Side.CLIENT)
    protected IIcon[] chargeLevelIcons;

    public BlockSuperDenseEnergyCell() {
        setBlockName("gtnl.block.super_dense_energy_cell");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        setTileEntity(TileEntitySuperDenseEnergyCell.class);
        GameRegistry.registerBlock(this, getItemBlockClass(), "super_dense_energy_cell");
        GameRegistry.registerTileEntity(TileEntitySuperDenseEnergyCell.class, "super_dense_energy_cell_tile_entity");
        GTNLItemList.SuperDenseEnergyCell.set(new ItemStack(this));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.super_dense_energy_cell";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        baseIcon = register.registerIcon(RESOURCE_ROOT_ID + ":block_super_dense_energy_cell");
        chargeLevelIcons = new IIcon[8];
        for (int level = 0; level < chargeLevelIcons.length; level++) {
            chargeLevelIcons[level] = register
                .registerIcon(RESOURCE_ROOT_ID + ":block_super_dense_energy_cell" + level);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int direction, int metadata) {
        if (metadata >= 0 && metadata < chargeLevelIcons.length) {
            return chargeLevelIcons[metadata];
        }
        return baseIcon;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntitySuperDenseEnergyCell energyCell = getTileEntity(world, x, y, z);
        return energyCell == null ? 0 : energyCell.getLightLevel();
    }

    @Override
    public double getMaxPower() {
        return 200000.0 * 8.0 * 8.0 * PowerMultiplier.CONFIG.multiplier;
    }
}
