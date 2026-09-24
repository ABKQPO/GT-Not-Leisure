package com.science.gtnl.common.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.science.gtnl.loader.ItemLoader;

public class EntityMajoBroom extends Entity {

    public static final double MODEL_LIFT = 0.25D;
    public static final int MAX_FLIGHT_TICKS = 20 * 30;
    public static final double MAX_HORIZONTAL_SPEED = 15.0D / 20.0D;
    public static final double HORIZONTAL_RESPONSE = 0.42D;
    public static final double HORIZONTAL_IDLE_DRAG = 0.96D;
    public static final double FORWARD_ACCELERATION = 0.035D;
    public static final double BRAKE_ACCELERATION = 0.075D;
    public static final double MAX_REVERSE_SPEED = 0.12D;
    public static final int MAX_REVERSE_TICKS = 8;
    public static final double UNMOUNTED_GRAVITY = 0.006D;
    public static final double UNMOUNTED_MAX_FALL_SPEED = 0.08D;
    public static final double UNMOUNTED_VERTICAL_DRAG = 0.92D;
    public static final double VERTICAL_DRAG = 0.9D;
    public static final double HOVER_DRAG = 0.65D;
    public static final double HOVER_BOB_ACCELERATION = 0.0015D;
    public static final int HOVER_BOB_DELAY_TICKS = 20 * 3;
    public static final int HOVER_BOB_PERIOD_TICKS = 72;
    public static final double GRAVITY = 0.03D;
    public static final double ASCEND_THRUST = 0.09D;
    public static final double DESCEND_THRUST = 0.03D;
    public static final int FLIGHT_EXHAUSTED_WATCHER = 20;
    public static final float MAX_TURN_DEGREES_PER_TICK = 6.0F;
    public static final float TURN_ACCELERATION = 1.35F;
    public static final float TURN_DRAG = 0.78F;
    public static final float VISUAL_PITCH_PER_VERTICAL_SPEED = 8.0F;
    public static final float MAX_FORWARD_VISUAL_PITCH = 4.0F;
    public static final float MAX_VISUAL_PITCH = 8.0F;
    public static final float VISUAL_PITCH_RESPONSE = 0.25F;

    public ItemStack broomStack;
    public int flightTicks;
    public int hoverIdleTicks;
    public byte forwardInput;
    public byte strafeInput;
    public byte verticalInput;
    public double forwardSpeed;
    public int reverseTicks;
    public float turnVelocity;
    public float visualPitch;
    public float prevVisualPitch;
    public int interpolationSteps;
    public double targetX;
    public double targetY;
    public double targetZ;
    public float targetYaw;
    public float targetPitch;
    public boolean wasClientRidden;

    public EntityMajoBroom(World world) {
        super(world);
        setSize(0.7F, 0.4F);
        yOffset = 0.0F;
        preventEntitySpawning = true;
    }

    protected boolean hasUnlimitedFlight() {
        return false;
    }

    protected double getMaxHorizontalSpeed() {
        return MAX_HORIZONTAL_SPEED;
    }

    protected ItemStack getDefaultBroomStack() {
        return new ItemStack(ItemLoader.majoBroom);
    }

    public void setBroomStack(ItemStack stack) {
        broomStack = stack.copy();
        broomStack.stackSize = 1;
    }

    public void setVerticalInput(byte verticalInput) {
        this.verticalInput = (byte) Math.max(-1, Math.min(1, verticalInput));
    }

    public void setRiderInput(byte forwardInput, byte strafeInput, byte verticalInput) {
        this.forwardInput = forwardInput;
        this.strafeInput = strafeInput;
        setVerticalInput(verticalInput);
    }

    public boolean isSupportedByGround() {
        return !worldObj.func_147461_a(
            boundingBox.copy()
                .offset(0.0D, -0.01D, 0.0D))
            .isEmpty();
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(FLIGHT_EXHAUSTED_WATCHER, (byte) 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) {
            updateClient();
            return;
        }

        simulateMotion(riddenByEntity);
        pushNearbyEntities();
    }

    public void updateClient() {
        EntityPlayer localRider = riddenByEntity instanceof EntityPlayer player && player.isClientWorld() ? player
            : null;
        boolean clientRidden = localRider != null;
        if (wasClientRidden && !clientRidden) {
            // Any target received while riding belongs to an old prediction frame.
            interpolationSteps = 0;
            targetX = posX;
            targetY = posY;
            targetZ = posZ;
            targetYaw = rotationYaw;
            targetPitch = rotationPitch;
        }
        wasClientRidden = clientRidden;
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
        double oldX = posX;
        double oldY = posY;
        double oldZ = posZ;
        if (localRider != null) {
            interpolationSteps = 0;
            simulateMotion(localRider);
        } else {
            interpolatePosition();
            turnVelocity = 0.0F;
            verticalInput = 0;
            hoverIdleTicks = 0;
            motionX = motionY = motionZ = 0.0D;
            forwardInput = 0;
            strafeInput = 0;
            forwardSpeed = 0.0D;
            reverseTicks = 0;
        }
        updateVisualPitch(posX - oldX, posY - oldY, posZ - oldZ);
    }

    public void simulateMotion(Entity rider) {
        if (!(rider instanceof EntityPlayer)) {
            simulateUnmountedMotion();
            return;
        }
        boolean flightAvailable = hasUnlimitedFlight()
            || (worldObj.isRemote ? dataWatcher.getWatchableObjectByte(FLIGHT_EXHAUSTED_WATCHER) == 0
                : flightTicks < MAX_FLIGHT_TICKS);
        boolean canFly = rider instanceof EntityPlayer && flightAvailable;
        boolean supported = isSupportedByGround();
        if (canFly && verticalInput == 0) {
            motionY *= HOVER_DRAG;
            if (Math.abs(motionY) < 0.0001D) motionY = 0.0D;
        } else {
            motionY = motionY * VERTICAL_DRAG - GRAVITY;
            if (canFly && verticalInput > 0) motionY += ASCEND_THRUST;
            else if (rider instanceof EntityPlayer && verticalInput < 0) motionY -= DESCEND_THRUST;
        }
        if (rider instanceof EntityPlayer player) {
            player.fallDistance = 0;
            fallDistance = 0;
            prevRotationYaw = rotationYaw;
            float strafe = strafeInput / 127.0F;
            float forward = forwardInput / 127.0F;
            updateSteering(strafe);
            rotationPitch = prevRotationPitch = 0.0F;

            updateHorizontalMotion(forward);
        } else {
            forwardInput = 0;
            strafeInput = 0;
            verticalInput = 0;
            turnVelocity = 0.0F;
            updateHorizontalMotion(0.0F);
        }
        limitHorizontalSpeed();
        updateHoverBobbing(rider, canFly, supported);

        if (supported && motionY <= 0.0D) motionY = Math.min(motionY, -0.001D);
        moveEntity(motionX, motionY, motionZ);
        if (onGround) {
            motionY = 0;
            if (!worldObj.isRemote && flightTicks != 0) {
                flightTicks = 0;
                dataWatcher.updateObject(FLIGHT_EXHAUSTED_WATCHER, (byte) 0);
            }
        } else if (!worldObj.isRemote && !hasUnlimitedFlight()
            && rider instanceof EntityPlayer
            && flightTicks < MAX_FLIGHT_TICKS) {
                if (++flightTicks == MAX_FLIGHT_TICKS) {
                    dataWatcher.updateObject(FLIGHT_EXHAUSTED_WATCHER, (byte) 1);
                }
            }
    }

    public void simulateUnmountedMotion() {
        forwardInput = 0;
        strafeInput = 0;
        verticalInput = 0;
        turnVelocity = 0.0F;
        forwardSpeed = 0.0D;
        reverseTicks = 0;
        motionX *= 0.8D;
        motionZ *= 0.8D;
        motionY = Math.max(motionY * UNMOUNTED_VERTICAL_DRAG - UNMOUNTED_GRAVITY, -UNMOUNTED_MAX_FALL_SPEED);
        moveEntity(motionX, motionY, motionZ);
        if (onGround) motionY = 0.0D;
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int steps) {
        if (worldObj.isRemote && riddenByEntity instanceof EntityPlayer player && player.isClientWorld()) return;
        targetX = x;
        targetY = y;
        targetZ = z;
        targetYaw = yaw;
        targetPitch = pitch;
        double dx = x - posX;
        double dy = y - posY;
        double dz = z - posZ;
        int smoothSteps = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy + dz * dz) / 0.35D);
        interpolationSteps = Math.max(3, Math.min(20, Math.max(steps, smoothSteps)));
    }

    public void interpolatePosition() {
        if (interpolationSteps <= 0) return;
        double x = posX + (targetX - posX) / interpolationSteps;
        double y = posY + (targetY - posY) / interpolationSteps;
        double z = posZ + (targetZ - posZ) / interpolationSteps;
        rotationYaw += MathHelper.wrapAngleTo180_float(targetYaw - rotationYaw) / interpolationSteps;
        rotationPitch += (targetPitch - rotationPitch) / interpolationSteps;
        setPosition(x, y, z);
        setRotation(rotationYaw, rotationPitch);
        interpolationSteps--;
    }

    public void updateHorizontalMotion(float forward) {
        forward = MathHelper.clamp_float(forward, -1.0F, 1.0F);
        if (forward > 0.01F) {
            forwardSpeed = Math.min(getMaxHorizontalSpeed(), forwardSpeed + FORWARD_ACCELERATION * forward);
            reverseTicks = 0;
        } else if (forward < -0.01F) {
            if (forwardSpeed > 0.0D) {
                forwardSpeed = Math.max(0.0D, forwardSpeed - BRAKE_ACCELERATION);
                reverseTicks = 0;
            } else if (reverseTicks++ < MAX_REVERSE_TICKS) {
                forwardSpeed = Math.max(-MAX_REVERSE_SPEED, forwardSpeed - FORWARD_ACCELERATION * 0.5D);
            } else {
                forwardSpeed = Math.min(0.0D, forwardSpeed + BRAKE_ACCELERATION * 0.5D);
            }
        } else {
            reverseTicks = 0;
            forwardSpeed *= HORIZONTAL_IDLE_DRAG;
            if (Math.abs(forwardSpeed) < 0.001D) forwardSpeed = 0.0D;
        }
        float yaw = rotationYaw * (float) Math.PI / 180.0F;
        float sin = MathHelper.sin(yaw);
        float cos = MathHelper.cos(yaw);
        double targetX = -sin * forwardSpeed;
        double targetZ = cos * forwardSpeed;
        motionX += (targetX - motionX) * HORIZONTAL_RESPONSE;
        motionZ += (targetZ - motionZ) * HORIZONTAL_RESPONSE;
    }

    public void updateHoverBobbing(Entity rider, boolean canFly, boolean supported) {
        if (!(rider instanceof EntityPlayer) || !canFly
            || supported
            || verticalInput != 0
            || forwardInput != 0
            || strafeInput != 0
            || motionX != 0.0D
            || motionZ != 0.0D
            || turnVelocity != 0.0F
            || Math.abs(motionY) >= (hoverIdleTicks >= HOVER_BOB_DELAY_TICKS ? 0.02D : 0.0001D)) {
            hoverIdleTicks = 0;
            return;
        }
        hoverIdleTicks++;
        if (hoverIdleTicks >= HOVER_BOB_DELAY_TICKS) {
            if (hoverIdleTicks >= HOVER_BOB_DELAY_TICKS + HOVER_BOB_PERIOD_TICKS)
                hoverIdleTicks = HOVER_BOB_DELAY_TICKS;
            double phase = (hoverIdleTicks - HOVER_BOB_DELAY_TICKS) * 2.0D * Math.PI / HOVER_BOB_PERIOD_TICKS;
            motionY += HOVER_BOB_ACCELERATION * Math.sin(phase);
        }
    }

    public void updateVisualPitch(double horizontalX, double verticalStep, double horizontalZ) {
        prevVisualPitch = visualPitch;
        float yaw = rotationYaw * (float) Math.PI / 180.0F;
        double forwardStep = -horizontalX * MathHelper.sin(yaw) + horizontalZ * MathHelper.cos(yaw);
        float forwardPitch = MathHelper.clamp_float((float) (forwardStep / getMaxHorizontalSpeed()), -1.0F, 1.0F)
            * MAX_FORWARD_VISUAL_PITCH;
        float targetPitch = MathHelper.clamp_float(
            (float) verticalStep * VISUAL_PITCH_PER_VERTICAL_SPEED - forwardPitch,
            -MAX_VISUAL_PITCH,
            MAX_VISUAL_PITCH);
        visualPitch += (targetPitch - visualPitch) * VISUAL_PITCH_RESPONSE;
    }

    public float getVisualPitch(float partialTicks) {
        return prevVisualPitch + (visualPitch - prevVisualPitch) * partialTicks;
    }

    public void limitHorizontalSpeed() {
        double speedSquared = motionX * motionX + motionZ * motionZ;
        double maxSpeed = getMaxHorizontalSpeed();
        double limitSquared = maxSpeed * maxSpeed;
        if (speedSquared > limitSquared) {
            double scale = maxSpeed / Math.sqrt(speedSquared);
            motionX *= scale;
            motionZ *= scale;
        }
    }

    public void updateSteering(float strafe) {
        turnVelocity = MathHelper.clamp_float(
            turnVelocity * TURN_DRAG - strafe * TURN_ACCELERATION,
            -MAX_TURN_DEGREES_PER_TICK,
            MAX_TURN_DEGREES_PER_TICK);
        if (Math.abs(turnVelocity) < 0.01F) turnVelocity = 0.0F;
        rotationYaw += turnVelocity;
    }

    public void pushNearbyEntities() {
        List<Entity> nearby = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.2D, 0.0D, 0.2D));
        for (Entity entity : nearby) {
            if (entity != riddenByEntity && entity.canBePushed()) entity.applyEntityCollision(this);
        }
    }

    @Override
    public double getMountedYOffset() {
        return 0.15D + MODEL_LIFT;
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (player.isSneaking()) {
            if (!worldObj.isRemote && riddenByEntity == null) recover(player);
            return true;
        }
        if (riddenByEntity == null || riddenByEntity == player) {
            if (!worldObj.isRemote) player.mountEntity(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    public void recover(EntityPlayer player) {
        ItemStack result = broomStack == null ? getDefaultBroomStack() : broomStack.copy();
        if (!player.inventory.addItemStackToInventory(result)) entityDropItem(result, 0.0F);
        setDead();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead && !(riddenByEntity instanceof EntityPlayer);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        // The broom is deliberately unaffected by entity collisions and explosions.
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void fall(float distance) {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        flightTicks = Math.max(0, Math.min(MAX_FLIGHT_TICKS, tag.getInteger("FlightTicks")));
        dataWatcher.updateObject(FLIGHT_EXHAUSTED_WATCHER, (byte) (flightTicks >= MAX_FLIGHT_TICKS ? 1 : 0));
        if (tag.hasKey("BroomStack", 10)) broomStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("BroomStack"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("FlightTicks", flightTicks);
        if (broomStack != null) tag.setTag("BroomStack", broomStack.writeToNBT(new NBTTagCompound()));
    }
}
