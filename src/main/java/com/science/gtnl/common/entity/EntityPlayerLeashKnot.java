package com.science.gtnl.common.entity;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
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

        iLeashedPlayer.gtnl$setLeashedToEntity(this, true);

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

    // Whether a player's saved leash names what it was tied to: a living entity by UUID, or a fence by position.
    public static boolean namesHolder(NBTTagCompound tag) {
        return tag.hasKey("UUIDMost", 4) && tag.hasKey("UUIDLeast", 4)
            || tag.hasKey("X", 99) && tag.hasKey("Y", 99) && tag.hasKey("Z", 99);
    }

    // The living entity within 10 blocks the saved leash names, or the knot on its fence, made again if gone.
    public static Entity findHolder(EntityPlayer player, NBTTagCompound tag) {
        if (tag.hasKey("UUIDMost", 4) && tag.hasKey("UUIDLeast", 4)) {
            UUID uuid = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
            List<EntityLivingBase> list = player.worldObj
                .getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox.expand(10.0D, 10.0D, 10.0D));
            for (EntityLivingBase entitylivingbase : list) {
                if (entitylivingbase.getUniqueID()
                    .equals(uuid)) {
                    return entitylivingbase;
                }
            }
            return null;
        }
        int i = tag.getInteger("X");
        int j = tag.getInteger("Y");
        int k = tag.getInteger("Z");
        EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForBlock(player.worldObj, i, j, k);
        if (entityleashknot == null) {
            entityleashknot = EntityLeashKnot.func_110129_a(player.worldObj, i, j, k);
        }
        return entityleashknot;
    }
}
