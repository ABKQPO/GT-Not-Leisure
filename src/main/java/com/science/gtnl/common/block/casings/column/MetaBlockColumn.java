package com.science.gtnl.common.block.casings.column;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.block.casings.column.ItemBlockColumn.metaSet;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.science.gtnl.client.GTNLCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class MetaBlockColumn extends Block {

    public String[] textureName = new String[] { "Side", "Top", "Bottom" };
    public Int2ObjectMap<IIcon[]> textureMap = new Int2ObjectOpenHashMap<>();

    public MetaBlockColumn() {
        super(Material.iron);
        this.setHardness(1.0F);
        this.setResistance(6000000.0F);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
    }

    public MetaBlockColumn(String unlocalizedName) {
        this();
        this.setBlockName(unlocalizedName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        IIcon[] textures = this.textureMap.get(meta);
        if (textures == null) {
            textures = this.textureMap.get(0);
        }
        return side == 1 ? textures[1] : (side == 0 ? textures[2] : textures[0]);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        for (int meta : metaSet) {
            IIcon[] textures = new IIcon[textureName.length];
            for (int i = 0; i < this.textureName.length; ++i) {
                textures[i] = reg
                    .registerIcon(RESOURCE_ROOT_ID + ":" + "MetaBlockColumn/" + meta + "_" + textureName[i]);
            }
            this.textureMap.put(meta, textures);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int meta : metaSet) {
            list.add(new ItemStack(item, 1, meta));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
}
