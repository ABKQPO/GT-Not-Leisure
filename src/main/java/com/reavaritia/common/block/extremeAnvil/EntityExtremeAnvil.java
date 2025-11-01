package com.reavaritia.common.block.extremeAnvil;

import static com.reavaritia.common.block.BlockRegister.ExtremeAnvil;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityExtremeAnvil extends Entity {

    private Block blockType;
    public int blockMetadata;
    public int timeSinceFalling;
    public boolean dropItem;
    private boolean hurtEntities;
    private boolean hasBlockFallen;
    private int maxFallDamage;
    private float fallDamageMultiplier;
    public NBTTagCompound tileEntityData;

    public EntityExtremeAnvil(World world) {
        super(world);
        this.dropItem = true;
        this.maxFallDamage = 40;
        this.fallDamageMultiplier = 2.0F;
        this.blockType = ExtremeAnvil;
    }

    public EntityExtremeAnvil(World world, double x, double y, double z) {
        this(world, x, y, z, 0);
        this.dropItem = true;
        this.maxFallDamage = 40;
        this.fallDamageMultiplier = 2.0F;
        this.blockType = ExtremeAnvil;
    }

    public EntityExtremeAnvil(World world, double x, double y, double z, int metadata) {
        super(world);
        this.dropItem = true;
        this.maxFallDamage = 40;
        this.fallDamageMultiplier = 2.0F;
        this.blockType = ExtremeAnvil;
        this.blockMetadata = metadata;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    /**
     * Returns if this entity triggers Block.onEntityWalking on the blocks they walk on.
     */
    public boolean canTriggerWalking() {
        return false;
    }

    public void entityInit() {}

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        if (this.blockType.getMaterial() == Material.air) {
            this.setDead();
        } else {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            ++this.timeSinceFalling;
            this.motionY -= 0.03999999910593033D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;

            if (!this.worldObj.isRemote) {
                int i = MathHelper.floor_double(this.posX);
                int j = MathHelper.floor_double(this.posY);
                int k = MathHelper.floor_double(this.posZ);

                if (this.timeSinceFalling == 1) {
                    if (this.worldObj.getBlock(i, j, k) != this.blockType) {
                        this.setDead();
                        return;
                    }

                    this.worldObj.setBlockToAir(i, j, k);
                }

                if (this.onGround) {
                    this.motionX *= 0.699999988079071D;
                    this.motionZ *= 0.699999988079071D;
                    this.motionY *= -0.5D;

                    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                        this.posX - 0.5,
                        this.posY - 0.5,
                        this.posZ - 0.5,
                        this.posX + 0.5,
                        this.posY + 0.5,
                        this.posZ + 0.5);

                    List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

                    for (EntityLivingBase entity : entities) {
                        try {
                            if (entity instanceof EntityPlayer) {
                                for (int e = 0; e < 100; e++) {
                                    entity.attackEntityFrom(DamageSource.anvil, Integer.MAX_VALUE);
                                }
                            } else {
                                entity.attackEntityFrom(DamageSource.anvil, Integer.MAX_VALUE);
                                entity.setHealth(0.0F);
                                entity.onDeath(DamageSource.anvil);
                                entity.setDead();
                            }
                        } catch (Exception ignored) {}
                    }

                    if (this.worldObj.getBlock(i, j, k) != Blocks.piston_extension) {
                        this.setDead();

                        if (!this.hasBlockFallen
                            && this.worldObj.canPlaceEntityOnSide(this.blockType, i, j, k, true, 1, null, null)
                            && !shouldFall(this.worldObj, i, j - 1, k)) {
                            ((BlockExtremeAnvil) this.blockType).isFalling(this.worldObj, i, j, k, 0);
                            if (this.tileEntityData != null && this.blockType instanceof ITileEntityProvider) {
                                TileEntity tileentity = this.worldObj.getTileEntity(i, j, k);

                                if (tileentity != null) {
                                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                                    tileentity.writeToNBT(nbttagcompound);

                                    for (String s : this.tileEntityData.func_150296_c()) {
                                        NBTBase nbtbase = this.tileEntityData.getTag(s);

                                        if (!s.equals("x") && !s.equals("y") && !s.equals("z")) {
                                            nbttagcompound.setTag(s, nbtbase.copy());
                                        }
                                    }

                                    tileentity.readFromNBT(nbttagcompound);
                                    tileentity.markDirty();
                                }
                            }
                        } else if (this.dropItem && !this.hasBlockFallen) {
                            this.entityDropItem(
                                new ItemStack(this.blockType, 1, this.blockType.damageDropped(0)),
                                0.0F);
                        }
                    }
                } else if (this.timeSinceFalling > 100 && !this.worldObj.isRemote && (j < 1 || j > 256)
                    || this.timeSinceFalling > 600) {
                        if (this.dropItem) {
                            this.entityDropItem(
                                new ItemStack(this.blockType, 1, this.blockType.damageDropped(0)),
                                0.0F);
                        }

                        this.setDead();
                    }
            }
        }
    }

    public boolean shouldFall(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        return below.isAir(world, x, y - 1, z) || below.getMaterial()
            .isLiquid() || below == Blocks.fire;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    @Override
    public void fall(float distance) {
        if (this.hurtEntities) {
            int i = MathHelper.ceiling_float_int(distance - 1.0F);

            if (i > 0) {
                ArrayList<Entity> arraylist = new ArrayList<>(
                    this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));
                boolean flag = this.blockType == Blocks.anvil;
                DamageSource damagesource = flag ? DamageSource.anvil : DamageSource.fallingBlock;

                for (Entity entity : arraylist) {
                    entity.attackEntityFrom(
                        damagesource,
                        (float) Math
                            .min(MathHelper.floor_float((float) i * this.fallDamageMultiplier), this.maxFallDamage));
                }

                if (flag && (double) this.rand.nextFloat() < 0.05000000074505806D + (double) i * 0.05D) {
                    int j = this.blockMetadata >> 2;
                    int k = this.blockMetadata & 3;
                    ++j;

                    if (j > 2) {
                        this.hasBlockFallen = true;
                    } else {
                        this.blockMetadata = k | j << 2;
                    }
                }
            }
        }
    }

    /**
     * Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("Tile", (byte) Block.getIdFromBlock(this.blockType));
        tagCompound.setInteger("TileID", Block.getIdFromBlock(this.blockType));
        tagCompound.setByte("Data", (byte) this.blockMetadata);
        tagCompound.setByte("Time", (byte) this.timeSinceFalling);
        tagCompound.setBoolean("DropItem", this.dropItem);
        tagCompound.setBoolean("HurtEntities", this.hurtEntities);
        tagCompound.setFloat("FallHurtAmount", this.fallDamageMultiplier);
        tagCompound.setInteger("FallHurtMax", this.maxFallDamage);

        if (this.tileEntityData != null) {
            tagCompound.setTag("TileEntityData", this.tileEntityData);
        } else {
            tagCompound.setTag("TileEntityData", new NBTTagCompound());
        }
    }

    /**
     * Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey("TileID", 99)) {
            this.blockType = Block.getBlockById(tagCompound.getInteger("TileID"));
        } else {
            this.blockType = Block.getBlockById(tagCompound.getByte("Tile") & 255);
        }

        this.blockMetadata = tagCompound.getByte("Data") & 255;
        this.timeSinceFalling = tagCompound.getByte("Time") & 255;

        if (tagCompound.hasKey("DropItem", 99)) {
            this.dropItem = tagCompound.getBoolean("DropItem");
        }

        if (tagCompound.hasKey("HurtEntities", 99)) {
            this.hurtEntities = tagCompound.getBoolean("HurtEntities");
        }

        if (tagCompound.hasKey("FallHurtAmount", 99)) {
            this.fallDamageMultiplier = tagCompound.getFloat("FallHurtAmount");
        }

        if (tagCompound.hasKey("FallHurtMax", 99)) {
            this.maxFallDamage = tagCompound.getInteger("FallHurtMax");
        }

        if (tagCompound.hasKey("TileEntityData", 10)) {
            this.tileEntityData = tagCompound.getCompoundTag("TileEntityData");
        }
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    @SideOnly(Side.CLIENT)
    public World getClientWorld() {
        return this.worldObj;
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

    public Block getBlock() {
        return this.blockType;
    }

    public static void registerEntity() {
        EntityRegistry
            .registerModEntity(EntityExtremeAnvil.class, "EntityExtremeAnvil", 2, ModList.ReAvaritia.ID, 64, 1, true);
    }
}
