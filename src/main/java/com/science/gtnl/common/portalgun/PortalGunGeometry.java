package com.science.gtnl.common.portalgun;

import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;

public class PortalGunGeometry {

    public static final double WIDTH = 1.0, HEIGHT = 2.0, THICKNESS = 0.2;
    private static final double EXIT_GAP = 0.05;
    private static final double[][] UP_BASIS = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } },
        DOWN_BASIS = { { 1, 0, 0 }, { 0, -1, 0 }, { 0, 0, -1 } },
        SOUTH_BASIS = { { 1, 0, 0 }, { 0, 0, -1 }, { 0, 1, 0 } },
        NORTH_BASIS = { { -1, 0, 0 }, { 0, 0, -1 }, { 0, -1, 0 } },
        WEST_BASIS = { { 0, -1, 0 }, { 0, 0, -1 }, { 1, 0, 0 } },
        EAST_BASIS = { { 0, 1, 0 }, { 0, 0, -1 }, { -1, 0, 0 } };

    public static double[] halfExtents(ForgeDirection facing, ForgeDirection up) {
        double[] half = { WIDTH / 2, WIDTH / 2, WIDTH / 2 };
        half[axis(facing)] = THICKNESS / 2;
        half[axis(up)] = HEIGHT / 2;
        return half;
    }

    public static AxisAlignedBB bounds(PortalGunPlacement placement) {
        double[] half = halfExtents(placement.facing(), placement.up());
        return AxisAlignedBB.getBoundingBox(
            placement.x() - half[0],
            placement.y() - half[1],
            placement.z() - half[2],
            placement.x() + half[0],
            placement.y() + half[1],
            placement.z() + half[2]);
    }

    public static Vec3d exitSpot(PortalGunPlacement exit, double width, double height, double fraction) {
        ForgeDirection out = exit.facing();
        if (out == ForgeDirection.UP) return new Vec3d(exit.x(), exit.y() + THICKNESS / 2 + EXIT_GAP, exit.z());
        if (out == ForgeDirection.DOWN)
            return new Vec3d(exit.x(), exit.y() - THICKNESS / 2 - height - EXIT_GAP, exit.z());
        double ahead = THICKNESS / 2 + width / 2 + EXIT_GAP;
        double feet = exit.y() - HEIGHT / 2 + MathUtils.clampUnit(fraction) * Math.max(0, HEIGHT - height);
        return new Vec3d(exit.x() + out.offsetX * ahead, feet, exit.z() + out.offsetZ * ahead);
    }

    public static Vec3d transform(Vec3d vector, ForgeDirection from, ForgeDirection to) {
        double[][] a = basis(from), b = basis(to);
        double[] v = { vector.x(), vector.y(), vector.z() }, local = new double[3], out = new double[3];
        for (int i = 0; i < 3; i++) local[i] = a[0][i] * v[0] + a[1][i] * v[1] + a[2][i] * v[2];
        for (int i = 0; i < 3; i++) out[i] = b[i][0] * local[0] + b[i][1] * local[1] + b[i][2] * local[2];
        return new Vec3d(out[0], out[1], out[2]);
    }

    private static int axis(ForgeDirection direction) {
        return direction.offsetX != 0 ? 0 : direction.offsetY != 0 ? 1 : 2;
    }

    private static double[][] basis(ForgeDirection direction) {
        return switch (direction) {
            case DOWN -> DOWN_BASIS;
            case NORTH -> NORTH_BASIS;
            case SOUTH -> SOUTH_BASIS;
            case WEST -> WEST_BASIS;
            case EAST -> EAST_BASIS;
            default -> UP_BASIS;
        };
    }
}
