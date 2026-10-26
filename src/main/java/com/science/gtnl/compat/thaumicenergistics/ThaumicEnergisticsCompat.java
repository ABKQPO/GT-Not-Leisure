package com.science.gtnl.compat.thaumicenergistics;

import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaInputHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaTube;
import com.science.gtnl.compat.IModCompat;

import thaumicenergistics.api.ThEApi;

public class ThaumicEnergisticsCompat implements IModCompat {

    @Override
    public void postInit() {
        ThEApi api = ThEApi.instance();
        if (api == null) return;

        api.transportPermissions()
            .addAspectContainerTileToInjectPermissions(
                TileEntityEssentiaHatch.class,
                TileEntityEssentiaHatch.MAX_STORED);
        api.transportPermissions()
            .addAspectContainerTileToInjectPermissions(
                TileEntityMultiEssentiaInputHatch.class,
                TileEntityMultiEssentiaInputHatch.MAX_CAPACITY);
        api.transportPermissions()
            .addAspectContainerTileToBothPermissions(
                TileEntityMultiEssentiaJar.class,
                TileEntityMultiEssentiaJar.MAX_CAPACITY);
        api.transportPermissions()
            .addAspectContainerTileToInjectPermissions(
                TileEntityMultiEssentiaTube.class,
                TileEntityMultiEssentiaTube.MAX_CAPACITY);
    }
}
