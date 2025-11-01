package com.science.gtnl.common.entity;

import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.science.gtnl.api.mixinHelper.ILeashedToEntity;
import com.science.gtnl.common.block.blocks.BlockPlayerLeash;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityPlayerLeashKnot extends EntityLeashKnot {

    private EntityPlayer leashedPlayer;
    private String leashedPlayerName;

    public EntityPlayerLeashKnot(World world) {
        super(world);
        this.setSize(0.375F, 0.5F);
    }

    public EntityPlayerLeashKnot(World world, double x, double y, double z, EntityPlayerMP player) {
        this(world);
        this.setPosition(x, y, z);
        this.leashedPlayer = player;
        this.leashedPlayerName = player.getDisplayName();

        if (player != null) {
            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
        }
    }

    @Override
    public void onUpdate() {
        if (worldObj.isRemote) return;

        if (leashedPlayer == null && leashedPlayerName != null) {
            leashedPlayer = worldObj.getPlayerEntityByName(leashedPlayerName);
        }

        if (leashedPlayer == null || leashedPlayer.isDead) return;
        if (!(leashedPlayer instanceof ILeashedToEntity iLeashedPlayer)) return;

        iLeashedPlayer.setLeashedToEntity(this, true);

        double dx = leashedPlayer.posX - posX;
        double dy = leashedPlayer.posY - posY;
        double dz = leashedPlayer.posZ - posZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;

        int blockX = MathHelper.floor_double(posX);
        int blockY = MathHelper.floor_double(posY);
        int blockZ = MathHelper.floor_double(posZ);

        if (distanceSq > 100.0 || !(worldObj.getBlock(blockX, blockY, blockZ) instanceof BlockPlayerLeash)) {
            leashedPlayer.capabilities.allowFlying = false;
            leashedPlayer.capabilities.isFlying = false;
            leashedPlayer.sendPlayerAbilities();
            worldObj.removeEntity(this);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 1024.0D;
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void entityInit() {}

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        leashedPlayerName = tag.getString("player");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        tag.setString("player", leashedPlayerName);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }
}
