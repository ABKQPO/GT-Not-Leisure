package com.science.gtnl.mixins.early.minecraft;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IAnchorRespawn;
import com.science.gtnl.api.mixinHelper.ILeashedToEntity;

@Mixin(value = EntityPlayer.class, remap = true)
public abstract class MixinEntityPlayer extends EntityLivingBase implements ILeashedToEntity, IAnchorRespawn {

    @Unique
    private int anchorDim = Integer.MAX_VALUE;
    @Unique
    private ChunkCoordinates anchorPos = null;

    @Unique
    private boolean gtnl$isLeashed;

    @Unique
    private Entity gtnl$leashedToEntity;

    @Unique
    private NBTTagCompound gtnl$nbtTagCompound;

    public MixinEntityPlayer(World p_i1594_1_) {
        super(p_i1594_1_);
    }

    @Override
    public void setAnchorRespawn(int dim, int x, int y, int z) {
        this.anchorDim = dim;
        this.anchorPos = new ChunkCoordinates(x, y, z);
    }

    @Override
    public int getAnchorDim() {
        return anchorDim;
    }

    @Override
    public ChunkCoordinates getAnchorPos() {
        return anchorPos;
    }

    @Override
    public void clearAnchorRespawn() {
        anchorDim = Integer.MAX_VALUE;
        anchorPos = null;
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeAnchorNBT(NBTTagCompound tag, CallbackInfo ci) {
        if (anchorPos != null) {
            tag.setInteger("AnchorDim", anchorDim);
            tag.setInteger("AnchorX", anchorPos.posX);
            tag.setInteger("AnchorY", anchorPos.posY);
            tag.setInteger("AnchorZ", anchorPos.posZ);
        }
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readAnchorNBT(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey("AnchorDim")) {
            anchorDim = tag.getInteger("AnchorDim");
            anchorPos = new ChunkCoordinates(
                tag.getInteger("AnchorX"),
                tag.getInteger("AnchorY"),
                tag.getInteger("AnchorZ"));
        }
    }

    @Inject(method = "clonePlayer", at = @At("TAIL"))
    private void cloneAnchor(EntityPlayer player, boolean conqueredEnd, CallbackInfo ci) {
        IAnchorRespawn respawn = (IAnchorRespawn) player;
        anchorDim = respawn.getAnchorDim();
        anchorPos = respawn.getAnchorPos();
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    public void mixin$onUpdate(CallbackInfo ci) {
        if (!this.worldObj.isRemote) {
            this.gtnl$updateLeashedState();
        }
    }

    @Override
    public void gtnl$setLeashedToEntity(Entity entityIn, boolean sendAttachNotification) {
        this.gtnl$isLeashed = true;
        this.gtnl$leashedToEntity = entityIn;

        if (!this.worldObj.isRemote && sendAttachNotification && this.worldObj instanceof WorldServer) {
            ((WorldServer) this.worldObj).getEntityTracker()
                .func_151247_a(this, new S1BPacketEntityAttach(1, this, this.gtnl$leashedToEntity));
        }
    }

    @Override
    public boolean gtnl$getLeashed() {
        return this.gtnl$isLeashed;
    }

    @Override
    public Entity gtnl$getLeashedToEntity() {
        return this.gtnl$leashedToEntity;
    }

    /**
     * Applies logic related to leashes, for example dragging the entity or breaking the leash.
     */
    @Override
    public void gtnl$updateLeashedState() {
        if (this.gtnl$nbtTagCompound != null) {
            this.gtnl$recreateLeash();
        }

        if (this.gtnl$isLeashed) {
            if (this.gtnl$leashedToEntity == null || this.gtnl$leashedToEntity.isDead) {
                this.gtnl$clearLeashed(true, false);
            }
        }
    }

    /**
     * Removes the leash from this entity. Second parameter tells whether to send a packet to surrounding players.
     */
    @Override
    public void gtnl$clearLeashed(boolean p_110160_1_, boolean dropItem) {
        if (this.gtnl$isLeashed) {
            this.gtnl$isLeashed = false;
            this.gtnl$leashedToEntity = null;

            if (!this.worldObj.isRemote && dropItem) {
                this.dropItem(Items.lead, 1);
            }

            if (!this.worldObj.isRemote && p_110160_1_ && this.worldObj instanceof WorldServer) {
                ((WorldServer) this.worldObj).getEntityTracker()
                    .func_151247_a(this, new S1BPacketEntityAttach(1, this, null));
            }
        }
    }

    @Unique
    public void gtnl$recreateLeash() {
        if (this.gtnl$isLeashed && this.gtnl$nbtTagCompound != null) {
            if (this.gtnl$nbtTagCompound.hasKey("UUIDMost", 4) && this.gtnl$nbtTagCompound.hasKey("UUIDLeast", 4)) {
                UUID uuid = new UUID(
                    this.gtnl$nbtTagCompound.getLong("UUIDMost"),
                    this.gtnl$nbtTagCompound.getLong("UUIDLeast"));
                List<EntityLivingBase> list = this.worldObj
                    .getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(10.0D, 10.0D, 10.0D));

                for (EntityLivingBase entitylivingbase : list) {
                    if (entitylivingbase.getUniqueID()
                        .equals(uuid)) {
                        this.gtnl$leashedToEntity = entitylivingbase;
                        break;
                    }
                }
            } else if (this.gtnl$nbtTagCompound.hasKey("X", 99) && this.gtnl$nbtTagCompound.hasKey("Y", 99)
                && this.gtnl$nbtTagCompound.hasKey("Z", 99)) {
                    int i = this.gtnl$nbtTagCompound.getInteger("X");
                    int j = this.gtnl$nbtTagCompound.getInteger("Y");
                    int k = this.gtnl$nbtTagCompound.getInteger("Z");
                    EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForBlock(this.worldObj, i, j, k);

                    if (entityleashknot == null) {
                        entityleashknot = EntityLeashKnot.func_110129_a(this.worldObj, i, j, k);
                    }

                    this.gtnl$leashedToEntity = entityleashknot;
                } else {
                    this.gtnl$clearLeashed(false, true);
                }
        }

        this.gtnl$nbtTagCompound = null;
    }

}
