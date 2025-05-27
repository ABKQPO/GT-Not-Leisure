package com.science.gtnl.common.block.Casings.Base;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.Utils.item.TextHandler.texter;
import static com.science.gtnl.common.block.Casings.Base.ItemBlockBase.MetaBlockSet;

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
import com.science.gtnl.common.block.Casings.BlockStaticDataClientOnly;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MetaBlockBase extends Block {

    // region Constructors
    protected MetaBlockBase(Material materialIn) {
        super(materialIn);
    }

    public MetaBlockBase() {
        this(Material.iron);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
    }

    public MetaBlockBase(String unlocalizedName, String localName) {
        this();
        this.unlocalizedName = unlocalizedName;
        texter(localName, "MetaBlocks." + unlocalizedName + ".name");
    }

    // endregion
    // -----------------------
    // region member variables

    public String unlocalizedName;

    // endregion
    // -----------------------
    // region getters

    @Override
    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    // endregion
    // -----------------------
    // region setters

    public void setUnlocalizedName(String aUnlocalizedName) {
        this.unlocalizedName = aUnlocalizedName;
    }

    // endregion
    // -----------------------
    // region Overrides
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return meta < BlockStaticDataClientOnly.iconsBlockMapBase.size()
            ? BlockStaticDataClientOnly.iconsBlockMapBase.get(meta)
            : BlockStaticDataClientOnly.iconsBlockMapBase.get(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":" + "MetaBlocks/0");
        for (int Meta : MetaBlockSet) {
            BlockStaticDataClientOnly.iconsBlockMapBase
                .put(Meta, reg.registerIcon(RESOURCE_ROOT_ID + ":" + "MetaBlocks/" + Meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item aItem, CreativeTabs aCreativeTabs, List list) {
        for (int Meta : MetaBlockSet) {
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

    // endregion
}
