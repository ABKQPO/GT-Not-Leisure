package com.science.gtnl.common.block.casings.casing;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.science.gtnl.api.IMetaBlock;
import com.science.gtnl.client.GTNLCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public abstract class MetaCasingBase extends Block implements IMetaBlock {

    public IntSet usedMetaSet = new IntOpenHashSet(16);
    public Int2ObjectMap<String[]> tooltipsMap = new Int2ObjectOpenHashMap<>(16);
    public Int2ObjectMap<IIcon> iconMap = new Int2ObjectOpenHashMap<>(16);
    public String unlocalizedName;

    public MetaCasingBase(String unlocalizedName) {
        super(Material.iron);
        this.unlocalizedName = unlocalizedName;
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
    }

    @Override
    public abstract boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z);

    @Override
    public abstract boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z);

    @Override
    public abstract boolean isNormalCube(IBlockAccess world, int x, int y, int z);

    @Override
    public IntSet getUsedMetaSet() {
        return usedMetaSet;
    }

    @Override
    public Int2ObjectMap<String[]> getTooltipsMap() {
        return tooltipsMap;
    }

    @Override
    public Int2ObjectMap<IIcon> getIconMap() {
        return iconMap;
    }

    @Override
    public String getUnlocalizedName() {
        return "tile." + this.unlocalizedName;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return iconMap.get(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        Int2ObjectMap<IIcon> iconMap = this.iconMap;
        IntSet usedMetaSet = this.usedMetaSet;

        if (iconMap == null || usedMetaSet == null) {
            throw new NullPointerException("Null in " + this.unlocalizedName);
        }

        String root = RESOURCE_ROOT_ID + ":" + this.unlocalizedName + "/";

        this.blockIcon = reg.registerIcon(root + "0");

        for (int meta : usedMetaSet) {
            iconMap.put(meta, reg.registerIcon(root + meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> list) {
        IntSet usedMetaSet;
        if ((usedMetaSet = this.usedMetaSet) == null) {
            throw new NullPointerException("Null in " + this.unlocalizedName);
        }
        for (int meta : usedMetaSet) {
            list.add(new ItemStack(aItem, 1, meta));
        }
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public int getDamageValue(World aWorld, int aX, int aY, int aZ) {
        return aWorld.getBlockMetadata(aX, aY, aZ);
    }

}
