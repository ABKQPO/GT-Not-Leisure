package com.science.gtnl.common.block.Casings;

import net.minecraft.block.Block;

import com.science.gtnl.common.block.IMetaBlock;
import com.science.gtnl.common.block.MetaItemBlockBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MetaItemBlockCasing extends MetaItemBlockBase {

    public MetaItemBlockCasing(Block block) {
        super(block);
    }

    @Override
    public boolean canCreatureSpawn() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String[] getTooltips(int meta) {
        if (getThisBlock() instanceof IMetaBlock thisBlock) {
            return thisBlock.getTooltips(meta);
        }
        return new String[0];
    }

}
