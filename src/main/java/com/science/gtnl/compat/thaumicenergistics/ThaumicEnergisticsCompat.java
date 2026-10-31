package com.science.gtnl.compat.thaumicenergistics;

import com.science.gtnl.common.block.blocks.tile.IMultiEssentiaContainer;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaInputHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaTube;
import com.science.gtnl.compat.IModCompat;

import appeng.api.config.Actionable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumicenergistics.api.IThETransportPermissions;
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

    public static long injectIntoMultiContainer(IAspectContainer container, IMultiEssentiaContainer multiContainer,
        int amountToFill, Aspect aspectToFill, Actionable mode, IThETransportPermissions perms) {
        if (amountToFill <= 0 || aspectToFill == null
            || !perms.canInjectToAspectContainerTile(container)
            || !container.doesContainerAccept(aspectToFill)) return 0L;
        int accepted = Math.min(amountToFill, multiContainer.getEssentiaCapacity() - multiContainer.getTotalAmount());
        if (accepted > 0 && mode == Actionable.MODULATE) {
            accepted -= container.addToContainer(aspectToFill, accepted);
        }
        return Math.max(0, accepted);
    }
}
