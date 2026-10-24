package com.science.gtnl.common.entity;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.equipment.EquipmentNet;
import com.science.gtnl.common.portalgun.PortalGunDestination;
import com.science.gtnl.common.portalgun.PortalGunGeometry;
import com.science.gtnl.common.portalgun.PortalGunPairs;
import com.science.gtnl.common.portalgun.PortalGunPlacement;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityPortalGunOrb extends EntityThrowable {

    private static final float SPEED = 1.0F;
    private static final String DESTINATION = "Destination", GUN_ID_MOST = "GunIdMost", GUN_ID_LEAST = "GunIdLeast";

    private PortalGunDestination destination;
    private UUID gunId;

    public EntityPortalGunOrb(World world) {
        super(world);
    }

    public EntityPortalGunOrb(World world, EntityLivingBase shooter, PortalGunDestination destination, UUID gunId) {
        super(world, shooter);
        this.destination = destination;
        this.gunId = gunId;
    }

    @Override
    protected float getGravityVelocity() {
        return (float) MainConfig.item.portal_gun.orbGravity;
    }

    @Override
    protected float func_70182_d() {
        return SPEED;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!worldObj.isRemote && !isDead && ticksExisted > MainConfig.item.portal_gun.airOpenTicks) openInAir();
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (worldObj.isRemote || isDead) return;
        if (hit.typeOfHit == MovingObjectType.BLOCK) openOnBlock(hit);
        else openInAir();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    private void openOnBlock(MovingObjectPosition hit) {
        ForgeDirection side = ForgeDirection.getOrientation(hit.sideHit);
        int fx = hit.blockX + side.offsetX, fz = hit.blockZ + side.offsetZ;
        if (side.offsetY == 0) {
            int bottom = standingBottom(fx, hit.blockY, fz);
            if (bottom == Integer.MIN_VALUE) {
                fizzle();
                return;
            }
            double offset = 0.5 + PortalGunGeometry.THICKNESS / 2;
            open(
                hit.blockX + 0.5 + side.offsetX * offset,
                bottom + PortalGunGeometry.HEIGHT / 2,
                hit.blockZ + 0.5 + side.offsetZ * offset,
                side,
                ForgeDirection.UP);
            return;
        }
        ForgeDirection along = horizontalHeading();
        open(
            fx + 0.5 + along.offsetX * 0.5,
            hit.blockY + 0.5 + side.offsetY * (0.5 + PortalGunGeometry.THICKNESS / 2),
            fz + 0.5 + along.offsetZ * 0.5,
            side,
            along);
    }

    private void openInAir() {
        int bx = MathHelper.floor_double(posX), by = MathHelper.floor_double(posY), bz = MathHelper.floor_double(posZ);
        ForgeDirection heading = heading();
        if (heading.offsetY != 0) {
            open(bx + 0.5, by + 0.5, bz + 0.5, heading.getOpposite(), horizontalHeading());
            return;
        }
        int bottom = standingBottom(bx, by, bz);
        if (bottom == Integer.MIN_VALUE) {
            bx -= heading.offsetX;
            bz -= heading.offsetZ;
            bottom = standingBottom(bx, by, bz);
        }
        if (bottom == Integer.MIN_VALUE) {
            fizzle();
            return;
        }
        open(bx + 0.5, bottom + PortalGunGeometry.HEIGHT / 2, bz + 0.5, heading.getOpposite(), ForgeDirection.UP);
    }

    private int standingBottom(int x, int y, int z) {
        if (passable(x, y - 1, z) && passable(x, y, z)) return y - 1;
        if (passable(x, y, z) && passable(x, y + 1, z)) return y;
        return Integer.MIN_VALUE;
    }

    private boolean passable(int x, int y, int z) {
        return worldObj.getBlock(x, y, z)
            .getCollisionBoundingBoxFromPool(worldObj, x, y, z) == null;
    }

    private void open(double x, double y, double z, ForgeDirection facing, ForgeDirection up) {
        setDead();
        if (destination == null || gunId == null) return;
        PortalGunPlacement here = new PortalGunPlacement(worldObj.provider.dimensionId, x, y, z, facing, up);
        EntityLivingBase shooter = getThrower();
        PortalGunPairs.OPENED.open(
            worldObj,
            here,
            PortalGunPlacement.standing(destination),
            MainConfig.item.portal_gun.portalLifetime,
            gunId,
            () -> tellFailed(shooter));
    }

    private void fizzle() {
        setDead();
        tellFailed(getThrower());
    }

    private static void tellFailed(EntityLivingBase shooter) {
        if (shooter instanceof EntityPlayerMP player) EquipmentNet.hotbar(player, "item.gtnl.portal_gun.open_failed");
    }

    private ForgeDirection heading() {
        double ax = Math.abs(motionX), ay = Math.abs(motionY), az = Math.abs(motionZ);
        if (ay > ax && ay > az) return motionY > 0 ? ForgeDirection.UP : ForgeDirection.DOWN;
        return horizontalHeading();
    }

    private ForgeDirection horizontalHeading() {
        if (Math.abs(motionX) > Math.abs(motionZ)) return motionX > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
        return motionZ > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (destination != null) tag.setTag(DESTINATION, destination.write());
        if (gunId != null) {
            tag.setLong(GUN_ID_MOST, gunId.getMostSignificantBits());
            tag.setLong(GUN_ID_LEAST, gunId.getLeastSignificantBits());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey(DESTINATION, Constants.NBT.TAG_COMPOUND))
            destination = PortalGunDestination.read(tag.getCompoundTag(DESTINATION));
        if (tag.hasKey(GUN_ID_MOST, Constants.NBT.TAG_LONG))
            gunId = new UUID(tag.getLong(GUN_ID_MOST), tag.getLong(GUN_ID_LEAST));
    }
}
