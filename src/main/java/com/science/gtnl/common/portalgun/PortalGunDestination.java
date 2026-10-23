package com.science.gtnl.common.portalgun;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public record PortalGunDestination(String name, int dimension, String dimensionName, double x, double y, double z,
    ForgeDirection facing, ItemId icon) {

    private static final float STEEP_PITCH = 45;
    private static final ForgeDirection[] YAW_FACINGS = { ForgeDirection.SOUTH, ForgeDirection.WEST,
        ForgeDirection.NORTH, ForgeDirection.EAST };
    private static final String NAME = "Name", DIMENSION = "Dimension", DIMENSION_NAME = "DimensionName", X = "X",
        Y = "Y", Z = "Z", FACING = "Facing", ICON = "Icon";

    public PortalGunDestination(String name, int dimension, String dimensionName, double x, double y, double z,
        ForgeDirection facing) {
        this(name, dimension, dimensionName, x, y, z, facing, null);
    }

    public static PortalGunDestination of(EntityPlayer player, String name) {
        return new PortalGunDestination(
            name,
            player.worldObj.provider.dimensionId,
            player.worldObj.provider.getDimensionName(),
            player.posX,
            player.boundingBox.minY,
            player.posZ,
            facingOf(player.rotationYaw, player.rotationPitch));
    }

    public static ForgeDirection facingOf(float yaw, float pitch) {
        if (pitch < -STEEP_PITCH) return ForgeDirection.UP;
        return YAW_FACINGS[MathHelper.floor_double(yaw * 4.0 / 360.0 + 0.5) & 3];
    }

    public static PortalGunDestination read(NBTTagCompound tag) {
        ForgeDirection facing = ForgeDirection.getOrientation(tag.getByte(FACING));
        if (facing == ForgeDirection.UNKNOWN || facing == ForgeDirection.DOWN) facing = ForgeDirection.NORTH;
        ItemId icon = tag.hasKey(ICON, Constants.NBT.TAG_COMPOUND) ? ItemId.create(tag.getCompoundTag(ICON)) : null;
        return new PortalGunDestination(
            tag.getString(NAME),
            tag.getInteger(DIMENSION),
            tag.getString(DIMENSION_NAME),
            tag.getDouble(X),
            tag.getDouble(Y),
            tag.getDouble(Z),
            facing,
            icon == null || icon.getItem() == null ? null : icon);
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(NAME, name);
        tag.setInteger(DIMENSION, dimension);
        tag.setString(DIMENSION_NAME, dimensionName);
        tag.setDouble(X, x);
        tag.setDouble(Y, y);
        tag.setDouble(Z, z);
        tag.setByte(FACING, (byte) facing.ordinal());
        if (icon != null) tag.setTag(ICON, icon.write(new NBTTagCompound()));
        return tag;
    }

    public PortalGunDestination withName(String newName) {
        return new PortalGunDestination(newName, dimension, dimensionName, x, y, z, facing, icon);
    }

    public PortalGunDestination edited(String newName, double newX, double newY, double newZ, ItemId newIcon) {
        return new PortalGunDestination(newName, dimension, dimensionName, newX, newY, newZ, facing, newIcon);
    }

    public double distanceTo(double px, double py, double pz) {
        double dx = x - px, dy = y - py, dz = z - pz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @SideOnly(Side.CLIENT)
    public String shownName() {
        return name.isEmpty() ? StatCollector.translateToLocal("item.gtnl.portal_gun.unnamed") : name;
    }

    @SideOnly(Side.CLIENT)
    public String describe() {
        return StatCollector.translateToLocalFormatted("item.gtnl.portal_gun.place", shownName(), position());
    }

    public String dimensionLabel() {
        return dimensionName.isEmpty() ? String.valueOf(dimension) : dimensionName;
    }

    @SideOnly(Side.CLIENT)
    public String position() {
        return StatCollector.translateToLocalFormatted(
            "item.gtnl.portal_gun.position",
            dimensionLabel(),
            MathHelper.floor_double(x),
            MathHelper.floor_double(y),
            MathHelper.floor_double(z));
    }
}
