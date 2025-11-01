package com.reavaritia.common.block.neutronCollector;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.reavaritia.ReAvaItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class NeutronCollector extends AbstractNeutronCollector {

    private final int energy;
    private final int tier;
    private final String texturePrefix;
    private final String tileEntityName;

    public NeutronCollector(String name, int energy, int tier, String texturePrefix, String tileEntityName,
        Class<? extends ItemBlock> itemBlockClass, ReAvaItemList itemListEntry) {
        super(name);
        this.energy = energy;
        this.tier = tier;
        this.texturePrefix = texturePrefix;
        this.tileEntityName = tileEntityName;

        GameRegistry.registerBlock(this, itemBlockClass, getUnlocalizedName());
        itemListEntry.set(new ItemStack(this, 1));
    }

    @Override
    public String getTexturePrefix() {
        return texturePrefix;
    }

    @Override
    public TileEntity createCollectorTileEntity() {
        return new TileEntityNeutronCollector(energy, tier, tileEntityName);
    }
}
