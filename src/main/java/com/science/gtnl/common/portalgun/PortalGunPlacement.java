package com.science.gtnl.common.portalgun;

import net.minecraftforge.common.util.ForgeDirection;

public record PortalGunPlacement(int dimension, double x, double y, double z, ForgeDirection facing,
    ForgeDirection up) {

    public static PortalGunPlacement standing(PortalGunDestination destination) {
        if (destination.facing() == ForgeDirection.UP) return new PortalGunPlacement(
            destination.dimension(),
            destination.x(),
            destination.y() + PortalGunGeometry.THICKNESS / 2,
            destination.z(),
            ForgeDirection.UP,
            ForgeDirection.SOUTH);
        return new PortalGunPlacement(
            destination.dimension(),
            destination.x(),
            destination.y() + PortalGunGeometry.HEIGHT / 2,
            destination.z(),
            destination.facing(),
            ForgeDirection.UP);
    }
}
