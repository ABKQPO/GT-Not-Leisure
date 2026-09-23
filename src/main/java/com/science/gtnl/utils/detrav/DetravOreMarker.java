package com.science.gtnl.utils.detrav;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.IIconContainer;

@SideOnly(Side.CLIENT)
public final class DetravOreMarker {

    public final int dimensionId;
    public final int x;
    public final int y;
    public final int z;
    public final String name;
    public final int color;
    public final @Nullable IIconContainer ore;
    public final long expiresAt;

    public DetravOreMarker(int dimensionId, int x, int y, int z, String name, int color, @Nullable IIconContainer ore,
        long expiresAt) {
        this.dimensionId = dimensionId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
        this.color = color;
        this.ore = ore;
        this.expiresAt = expiresAt;
    }
}
