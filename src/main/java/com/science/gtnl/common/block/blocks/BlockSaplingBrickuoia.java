package com.science.gtnl.common.block.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenTrees;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.world.WorldGenBrickuoia;

import gtPlusPlus.xmod.bop.blocks.base.SaplingBase;

public class BlockSaplingBrickuoia extends SaplingBase {

    public BlockSaplingBrickuoia() {
        super("Giant Brickuoia Sapling", "brickuoia", new String[] { "brickuoia" });
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
    }

    @Override
    public void func_149878_d(World world, int x, int y, int z, Random rand) {
        if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(world, rand, x, y, z)) return;
        int l = world.getBlockMetadata(x, y, z) & 7;
        rand.nextInt(10);
        new WorldGenBigTree(true);
        new WorldGenTrees(true);
        int i1 = 0;
        int j1 = 0;

        final Block air = Blocks.air;

        world.setBlock(x, y, z, air, 0, 4);
        WorldGenBrickuoia o = new WorldGenBrickuoia(Blocks.brick_block, Blocks.clay, 0, 0, true, 50, 75);

        if (!o.generate(world, rand, x + i1, y, z + j1)) {
            world.setBlock(x, y, z, this, l, 4);
        }
    }
}
