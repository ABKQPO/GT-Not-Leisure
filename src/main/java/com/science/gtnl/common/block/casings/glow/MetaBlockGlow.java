package com.science.gtnl.common.block.casings.glow;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.block.casings.glow.ItemBlockGlow.metaSet;

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
import com.science.gtnl.common.block.casings.BlockStaticDataClientOnly;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Setter;

@Setter
public class MetaBlockGlow extends Block {

    public String unlocalizedName;

    public MetaBlockGlow(Material materialIn) {
        super(materialIn);
    }

    public MetaBlockGlow() {
        this(Material.iron);
        this.setLightLevel(1);
        this.setHardness(1.0F);
        this.setResistance(5.0F);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
    }

    public MetaBlockGlow(String unlocalizedName) {
        this();
        this.unlocalizedName = unlocalizedName;
    }

    @Override
    public String getUnlocalizedName() {
        return "tile." + this.unlocalizedName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return meta < BlockStaticDataClientOnly.iconsBlockMapGlow.size()
            ? BlockStaticDataClientOnly.iconsBlockMapGlow.get(meta)
            : BlockStaticDataClientOnly.iconsBlockMapGlow.get(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":" + "MetaBlockGlow/0");
        for (int Meta : metaSet) {
            BlockStaticDataClientOnly.iconsBlockMapGlow
                .put(Meta, reg.registerIcon(RESOURCE_ROOT_ID + ":" + "MetaBlockGlow/" + Meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> list) {
        for (int Meta : metaSet) {
            list.add(new ItemStack(aItem, 1, Meta));
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
