package com.science.gtnl.common.machine.hatch;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.block.tile.TileSpecialFlower;
import vazkii.botania.common.block.tile.mana.TilePool;

public class ManaScan {

    private ManaScan() {}

    public static List<TilePool> pools(IGregTechTileEntity base, int radius) {
        World world = base.getWorld();
        int x = base.getXCoord(), y = base.getYCoord(), z = base.getZCoord();
        List<TilePool> pools = new ObjectArrayList<>();
        for (int dx = -radius; dx <= radius; dx++)
            for (int dy = -radius; dy <= radius; dy++) for (int dz = -radius; dz <= radius; dz++)
                if (world.getTileEntity(x + dx, y + dy, z + dz) instanceof TilePool pool) pools.add(pool);
        return pools;
    }

    public static List<SubTileGenerating> generatingFlowers(IGregTechTileEntity base, int radius) {
        World world = base.getWorld();
        int x = base.getXCoord(), y = base.getYCoord(), z = base.getZCoord();
        List<SubTileGenerating> flowers = new ObjectArrayList<>();
        for (int dx = -radius; dx <= radius; dx++)
            for (int dy = -radius; dy <= radius; dy++) for (int dz = -radius; dz <= radius; dz++) {
                TileEntity te = world.getTileEntity(x + dx, y + dy, z + dz);
                if (te instanceof TileSpecialFlower flowerTile) {
                    SubTileEntity subTile = flowerTile.getSubTile();
                    if (subTile instanceof SubTileGenerating generating) flowers.add(generating);
                }
            }
        return flowers;
    }

    public static boolean isDropMetaValid(TilePool pool) {
        return pool.getBlockMetadata() == 1;
    }
}
