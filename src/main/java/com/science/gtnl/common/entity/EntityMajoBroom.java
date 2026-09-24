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
    private static final double MAX_HORIZONTAL_SPEED = 15.0D / 20.0D;
    private static final double HORIZONTAL_RESPONSE = 0.25D;
    private static final double HORIZONTAL_IDLE_DRAG = 0.75D;
    private static final double VERTICAL_DRAG = 0.9D;
    private static final double HOVER_DRAG = 0.65D;
    private static final double HOVER_BOB_ACCELERATION = 0.0015D;
    private static final int HOVER_BOB_DELAY_TICKS = 20 * 3;
    private static final int HOVER_BOB_PERIOD_TICKS = 72;
    private static final double GRAVITY = 0.03D;
    private static final double ASCEND_THRUST = 0.09D;
    private static final double DESCEND_THRUST = 0.03D;
    private static final double ACTIVE_POSITION_ERROR = 4.0D;
    private static final double IDLE_HORIZONTAL_ERROR = 1.0D;
    private static final double IDLE_VERTICAL_ERROR = 0.75D;
    private static final double POSITION_CORRECTION_RESPONSE = 0.2D;
    private static final int FLIGHT_EXHAUSTED_WATCHER = 20;
    private static final float MAX_TURN_DEGREES_PER_TICK = 3.5F;
    private static final float TURN_ACCELERATION = 0.7F;
    private static final float TURN_DRAG = 0.8F;
    private static final float VISUAL_PITCH_PER_VERTICAL_SPEED = 8.0F;
    private static final float MAX_FORWARD_VISUAL_PITCH = 4.0F;
    private static final float MAX_VISUAL_PITCH = 8.0F;
    private static final float VISUAL_PITCH_RESPONSE = 0.25F;

    private ItemStack broomStack;
    private int flightTicks;
    private int hoverIdleTicks;
    private byte verticalInput;
    private float turnVelocity;
    private float visualPitch;
    private float prevVisualPitch;
    private int interpolationSteps;
    private double targetX;
    private double targetY;
    private double targetZ;
    private float targetYaw;
    private float targetPitch;
    private boolean hasServerPosition;

    public EntityMajoBroom(World world) {
        super(world);
        setSize(0.7F, 0.4F);
        yOffset = 0.0F;
        preventEntitySpawning = true;
    }

    public void setBroomStack(ItemStack stack) {
        broomStack = stack.copy();
        broomStack.stackSize = 1;
    }

    public void setVerticalInput(byte verticalInput) {
        this.verticalInput = (byte) Math.max(-1, Math.min(1, verticalInput));
    }

    public boolean isSupportedByGround() {
        return !worldObj.func_147461_a(boundingBox.copy().offset(0.0D, -0.01D, 0.0D))
            .isEmpty();
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(FLIGHT_EXHAUSTED_WATCHER, Byte.valueOf((byte) 0));
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

    private void updateClient() {
        EntityPlayer localRider = riddenByEntity instanceof EntityPlayer player && player.isClientWorld() ? player
            : null;
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
            hasServerPosition = false;
        }
        updateVisualPitch(posX - oldX, posY - oldY, posZ - oldZ);
        if (localRider != null) reconcileServerPosition(localRider);
    }

    private void simulateMotion(Entity rider) {
        boolean flightAvailable = worldObj.isRemote ? dataWatcher.getWatchableObjectByte(FLIGHT_EXHAUSTED_WATCHER) == 0
            : flightTicks < MAX_FLIGHT_TICKS;
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
            updateSteering(player.moveStrafing);
            rotationPitch = prevRotationPitch = 0.0F;

            updateHorizontalMotion(player.moveForward);
        } else {
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
                dataWatcher.updateObject(FLIGHT_EXHAUSTED_WATCHER, Byte.valueOf((byte) 0));
            }
        } else if (!worldObj.isRemote && rider instanceof EntityPlayer && flightTicks < MAX_FLIGHT_TICKS) {
            if (++flightTicks == MAX_FLIGHT_TICKS) {
                dataWatcher.updateObject(FLIGHT_EXHAUSTED_WATCHER, Byte.valueOf((byte) 1));
            }
        }
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int steps) {
        targetX = x;
        targetY = y;
        targetZ = z;
        targetYaw = yaw;
        targetPitch = pitch;
        if (riddenByEntity instanceof EntityPlayer player && player.isClientWorld()) {
            hasServerPosition = true;
            interpolationSteps = 0;
            return;
        }
        interpolationSteps = Math.max(1, steps);
    }

    private void interpolatePosition() {
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

    private void reconcileServerPosition(EntityPlayer player) {
        if (!hasServerPosition) return;
        hasServerPosition = false;

        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        boolean hasInput = Math.abs(player.moveForward) > 0.01F || Math.abs(player.moveStrafing) > 0.01F
            || verticalInput != 0;
        double horizontalThreshold = hasInput ? ACTIVE_POSITION_ERROR : IDLE_HORIZONTAL_ERROR;
        double verticalThreshold = hasInput ? ACTIVE_POSITION_ERROR : IDLE_VERTICAL_ERROR;
        boolean correctHorizontal = dx * dx + dz * dz > horizontalThreshold * horizontalThreshold;
        boolean correctVertical = Math.abs(dy) > verticalThreshold;
        if (!correctHorizontal && !correctVertical) return;

        setPosition(posX + (correctHorizontal ? dx * POSITION_CORRECTION_RESPONSE : 0.0D),
            posY + (correctVertical ? dy * POSITION_CORRECTION_RESPONSE : 0.0D),
            posZ + (correctHorizontal ? dz * POSITION_CORRECTION_RESPONSE : 0.0D));
    }

    private void updateHorizontalMotion(float forward) {
        forward = MathHelper.clamp_float(forward, -1.0F, 1.0F);
        if (Math.abs(forward) < 0.001F) {
            motionX *= HORIZONTAL_IDLE_DRAG;
            motionZ *= HORIZONTAL_IDLE_DRAG;
            if (Math.abs(motionX) < 0.001D) motionX = 0.0D;
            if (Math.abs(motionZ) < 0.001D) motionZ = 0.0D;
            return;
        }
        float yaw = rotationYaw * (float) Math.PI / 180.0F;
        float sin = MathHelper.sin(yaw);
        float cos = MathHelper.cos(yaw);
        double targetX = -forward * sin * MAX_HORIZONTAL_SPEED;
        double targetZ = forward * cos * MAX_HORIZONTAL_SPEED;
        motionX += (targetX - motionX) * HORIZONTAL_RESPONSE;
        motionZ += (targetZ - motionZ) * HORIZONTAL_RESPONSE;
    }

    private void updateHoverBobbing(Entity rider, boolean canFly, boolean supported) {
        if (!(rider instanceof EntityPlayer player) || !canFly || supported || verticalInput != 0
            || Math.abs(player.moveForward) > 0.01F || Math.abs(player.moveStrafing) > 0.01F || motionX != 0.0D
            || motionZ != 0.0D || turnVelocity != 0.0F
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

    private void updateVisualPitch(double horizontalX, double verticalStep, double horizontalZ) {
        prevVisualPitch = visualPitch;
        float yaw = rotationYaw * (float) Math.PI / 180.0F;
        double forwardStep = -horizontalX * MathHelper.sin(yaw) + horizontalZ * MathHelper.cos(yaw);
        float forwardPitch = MathHelper.clamp_float((float) (forwardStep / MAX_HORIZONTAL_SPEED), -1.0F, 1.0F)
            * MAX_FORWARD_VISUAL_PITCH;
        float targetPitch = MathHelper.clamp_float(
            (float) verticalStep * VISUAL_PITCH_PER_VERTICAL_SPEED - forwardPitch, -MAX_VISUAL_PITCH,
            MAX_VISUAL_PITCH);
        visualPitch += (targetPitch - visualPitch) * VISUAL_PITCH_RESPONSE;
    }

    public float getVisualPitch(float partialTicks) {
        return prevVisualPitch + (visualPitch - prevVisualPitch) * partialTicks;
    }

    private void limitHorizontalSpeed() {
        double speedSquared = motionX * motionX + motionZ * motionZ;
        double limitSquared = MAX_HORIZONTAL_SPEED * MAX_HORIZONTAL_SPEED;
        if (speedSquared > limitSquared) {
            double scale = MAX_HORIZONTAL_SPEED / Math.sqrt(speedSquared);
            motionX *= scale;
            motionZ *= scale;
        }
    }

    private void updateSteering(float strafe) {
        turnVelocity = MathHelper.clamp_float(turnVelocity * TURN_DRAG - strafe * TURN_ACCELERATION,
            -MAX_TURN_DEGREES_PER_TICK, MAX_TURN_DEGREES_PER_TICK);
        if (Math.abs(turnVelocity) < 0.01F) turnVelocity = 0.0F;
        rotationYaw += turnVelocity;
    }

    private void pushNearbyEntities() {
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
        if (source.getEntity() instanceof EntityPlayer player && player.isSneaking()) {
            if (!worldObj.isRemote && riddenByEntity == null) recover(player);
            return true;
        }
        return false;
    }

    private void recover(EntityPlayer player) {
        ItemStack result = broomStack == null ? new ItemStack(ItemLoader.majoBroom) : broomStack.copy();
        if (!player.inventory.addItemStackToInventory(result)) entityDropItem(result, 0.0F);
        setDead();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead && !(riddenByEntity instanceof EntityPlayer);
    }

    @Override
    public boolean canBePushed() {
        return !isDead;
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
        dataWatcher.updateObject(FLIGHT_EXHAUSTED_WATCHER, Byte.valueOf((byte) (flightTicks >= MAX_FLIGHT_TICKS ? 1 : 0)));
        if (tag.hasKey("BroomStack", 10)) broomStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("BroomStack"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("FlightTicks", flightTicks);
        if (broomStack != null) tag.setTag("BroomStack", broomStack.writeToNBT(new NBTTagCompound()));
    }
}
