package com.science.gtnl.common.block.casings.casing;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.science.gtnl.api.ICasing;

import gregtech.api.GregTechAPI;
import gregtech.api.util.GTUtility;

/**
 * A base block class of GregTech casings that textures will be called for update hatches' textures.
 */
public abstract class MetaBlockCasingBase extends MetaCasingBase implements ICasing {

    public static final byte TEXTURE_PAGE_INDEX = 116;

    static {
        GTUtility.addTexturePage(TEXTURE_PAGE_INDEX);
    }

    public MetaBlockCasingBase(String unlocalizedName) {
        super(unlocalizedName);
        this.setHardness(9.0F);
        this.setResistance(5.0F);
        GregTechAPI.registerMachineBlock(this, -1);
    }

    @Override
    public int getTextureIndex(int aMeta) {
        return getTextureId(getTexturePageIndex(), getTextureIndexInPage(aMeta));
    }

    @Override
    public String getHarvestTool(int aMeta) {
        return "wrench";
    }

    @Override
    public int getHarvestLevel(int aMeta) {
        return 1;
    }

    @Override
    public float getBlockHardness(World aWorld, int aX, int aY, int aZ) {
        return Blocks.iron_block.getBlockHardness(aWorld, aX, aY, aZ);
    }

    @Override
    public float getExplosionResistance(Entity aTNT) {
        return Blocks.iron_block.getExplosionResistance(aTNT);
    }

    @Override
    public boolean canSilkHarvest() {
        return false;
    }

    @Override
    public void onBlockAdded(World aWorld, int aX, int aY, int aZ) {
        if (GregTechAPI.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTechAPI.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
    }

    @Override
    public void breakBlock(World aWorld, int aX, int aY, int aZ, Block aBlock, int aMetaData) {
        if (GregTechAPI.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTechAPI.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

}
