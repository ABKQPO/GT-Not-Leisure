package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.science.gtnl.ClientProxy;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.tile.TileEntityWaterCandle;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWaterCandle extends BlockContainer {

    public BlockWaterCandle() {
        super(Material.iron);
        this.setResistance(99999999f);
        this.setHardness(5f);
        this.setBlockName("WaterCandle");
        this.setBlockBounds(0.5F - 0.1875F, 0.0F, 0.5F - 0.1875F, 0.5F + 0.1875F, 0.375F, 0.5F + 0.1875F);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        this.setLightLevel(0.8f);
        this.setBlockTextureName(RESOURCE_ROOT_ID + ":" + "WaterCandle");
        this.setStepSound(soundTypeSnow);
        GameRegistry.registerBlock(this, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityWaterCandle.class, "WaterCandleTileEntity");
        GTNLItemList.WaterCandle.set(new ItemStack(this, 1));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityWaterCandle();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return ClientProxy.waterCandleRenderID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {

        worldIn.spawnParticle("smoke", x + 0.4375, y + 0.5, z + 0.375, 0.0D, 0.0D, 0.0D);
        worldIn.spawnParticle("flame", x + 0.4375, y + 0.5, z + 0.375, 0.0D, 0.0D, 0.0D);

        worldIn.spawnParticle("smoke", x + 0.375, y + 0.4375, z + 0.5625, 0.0D, 0.0D, 0.0D);
        worldIn.spawnParticle("flame", x + 0.375, y + 0.4375, z + 0.5625, 0.0D, 0.0D, 0.0D);

        worldIn.spawnParticle("smoke", x + 0.625, y + 0.4375, z + 0.375, 0.0D, 0.0D, 0.0D);
        worldIn.spawnParticle("flame", x + 0.625, y + 0.4375, z + 0.375, 0.0D, 0.0D, 0.0D);

        worldIn.spawnParticle("smoke", x + 0.5625, y + 0.3125, z + 0.5625, 0.0D, 0.0D, 0.0D);
        worldIn.spawnParticle("flame", x + 0.5625, y + 0.3125, z + 0.5625, 0.0D, 0.0D, 0.0D);
    }
}
