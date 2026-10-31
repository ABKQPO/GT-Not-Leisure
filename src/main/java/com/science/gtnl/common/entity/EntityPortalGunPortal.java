package com.science.gtnl.common.entity;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.portalgun.PortalGunGeometry;
import com.science.gtnl.common.portalgun.PortalGunPairs;
import com.science.gtnl.common.portalgun.PortalGunPlacement;
import com.science.gtnl.config.ItemConfig;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.GTUtility;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class EntityPortalGunPortal extends Entity implements IEntityAdditionalSpawnData {

    public static final int OPEN_TICKS = 6, NEVER_PASSED = Integer.MIN_VALUE;
    // Why: the slowest close the look settings allow, so every viewer sees its close play out.
    public static final int CLOSE_LIMIT = 14;
    public static final int ARRIVAL_COOLDOWN = 10;
    private static final int VELOCITY_MEMORY = 10, CLOSING_WATCHER = 20, PASSED_WATCHER = 21;
    private static final double VELOCITY_REACH = 2.5, MIN_CARRIED_SPEED = 0.2;
    private static final String COOLDOWN = "gtnl.portalGunCooldown", PAIR = "gtnl.portalGunPair",
        PAIR_UNTIL = "gtnl.portalGunPairUntil";

    private ForgeDirection facing = ForgeDirection.NORTH, up = ForgeDirection.UP;
    private PortalGunPlacement link;
    private UUID gunId;
    private int pair, lifetime, closingTicks;
    private final Int2ObjectOpenHashMap<Track> tracks = new Int2ObjectOpenHashMap<>();

    public EntityPortalGunPortal(World world) {
        super(world);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;
        this.renderDistanceWeight = 4.0;
    }

    public EntityPortalGunPortal(World world, PortalGunPlacement here, PortalGunPlacement link, int lifetime,
        UUID gunId, int pair) {
        this(world);
        this.facing = here.facing();
        this.up = here.up();
        this.link = link;
        this.lifetime = lifetime;
        this.gunId = gunId;
        this.pair = pair;
        this.setPosition(here.x(), here.y(), here.z());
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(CLOSING_WATCHER, (byte) 0);
        dataWatcher.addObject(PASSED_WATCHER, NEVER_PASSED);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        if (facing == null) {
            super.setPosition(x, y, z);
            return;
        }
        posX = x;
        posY = y;
        posZ = z;
        double[] half = PortalGunGeometry.halfExtents(facing, up);
        boundingBox.setBounds(x - half[0], y - half[1], z - half[2], x + half[0], y + half[1], z + half[2]);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (closing()) {
            if (++closingTicks > CLOSE_LIMIT && !worldObj.isRemote) setDead();
            return;
        }
        if (worldObj.isRemote) return;
        if (--lifetime <= 0) {
            close();
            return;
        }
        int tick = MinecraftServer.getServer()
            .getTickCounter();
        trackVelocities(tick);
        for (Object found : worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox)) {
            if (found instanceof Entity entity && canTeleport(entity, tick)) teleport(entity, tick);
        }
    }

    private void trackVelocities(int tick) {
        AxisAlignedBB reach = boundingBox.addCoord(
            facing.offsetX * VELOCITY_REACH,
            facing.offsetY * VELOCITY_REACH,
            facing.offsetZ * VELOCITY_REACH);
        for (Object found : worldObj.getEntitiesWithinAABBExcludingEntity(this, reach)) {
            if (!(found instanceof Entity entity)) continue;
            Vec3d now = new Vec3d(entity.posX, entity.boundingBox.minY, entity.posZ);
            Track last = tracks.get(entity.getEntityId());
            tracks.put(entity.getEntityId(), new Track(last == null ? now : last.current(), now, tick));
        }
        tracks.values()
            .removeIf(track -> tick - track.seen() > VELOCITY_MEMORY);
    }

    private boolean canTeleport(Entity entity, int tick) {
        if (entity.isDead || entity instanceof EntityPortalGunPortal || entity instanceof EntityPortalGunOrb)
            return false;
        if (entity instanceof IBossDisplayData || entity instanceof EntityHanging || entity.getParts() != null)
            return false;
        NBTTagCompound data = entity.getEntityData();
        if (coolingDown(data.getInteger(COOLDOWN), tick)) return false;
        int pairCooldown = ItemConfig.portal_gun.pairCooldownTicks;
        if (pairCooldown <= 0 || !pairKey(gunId, pair).equals(data.getString(PAIR))
            || !coolingDown(data.getInteger(PAIR_UNTIL), tick, pairCooldown)) return true;
        // Why: while the entity still touches the pair the wait restarts, so nothing standing or falling in a portal
        // keeps bouncing between the two.
        data.setInteger(PAIR_UNTIL, tick + pairCooldown);
        return false;
    }

    public static boolean coolingDown(int until, int tick) {
        return coolingDown(until, tick, ARRIVAL_COOLDOWN);
    }

    public static boolean coolingDown(int until, int tick, int length) {
        int left = until - tick;
        return left > 0 && left <= length;
    }

    public static String pairKey(UUID gunId, int pair) {
        return (gunId == null ? "" : gunId.toString()) + "#" + pair;
    }

    private void teleport(Entity entity, int tick) {
        if (link == null) return;
        WorldServer target = MinecraftServer.getServer()
            .worldServerForDimension(link.dimension());
        if (target == null) return;
        ForgeDirection exitInto = link.facing()
            .getOpposite();
        Vec3d motion = PortalGunGeometry.transform(carriedVelocity(entity), facing, exitInto);
        double fraction = facing.offsetY != 0 ? 0
            : (entity.boundingBox.minY - (posY - PortalGunGeometry.HEIGHT / 2))
                / Math.max(1.0E-3, PortalGunGeometry.HEIGHT - entity.height);
        Vec3d spot = PortalGunGeometry.exitSpot(link, entity.width, entity.height, fraction);
        if (!canExit(target, entity, spot)) return;
        // Player relocation clears this step offset; use the same landing bounds for other entities too.
        entity.ySize = 0;
        NBTTagCompound data = entity.getEntityData();
        data.setInteger(COOLDOWN, tick + ARRIVAL_COOLDOWN);
        data.setString(PAIR, pairKey(gunId, pair));
        data.setInteger(PAIR_UNTIL, tick + ItemConfig.portal_gun.pairCooldownTicks);
        entity.fallDistance = 0;
        entity.motionX = motion.x();
        entity.motionY = motion.y();
        entity.motionZ = motion.z();
        if (link.dimension() != worldObj.provider.dimensionId) {
            if (!GTUtility.moveEntityToDimensionAtCoords(entity, link.dimension(), spot.x(), spot.y(), spot.z()))
                return;
        } else {
            if (entity.ridingEntity != null) entity.mountEntity(null);
            if (entity.riddenByEntity != null) entity.riddenByEntity.mountEntity(null);
            if (entity instanceof EntityPlayerMP player) player.playerNetServerHandler
                .setPlayerLocation(spot.x(), spot.y(), spot.z(), entity.rotationYaw, entity.rotationPitch);
            else entity.setLocationAndAngles(spot.x(), spot.y(), spot.z(), entity.rotationYaw, entity.rotationPitch);
        }
        if (entity instanceof EntityPlayerMP player)
            player.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
        else entity.velocityChanged = true;
        tracks.remove(entity.getEntityId());
        markPassed();
        EntityPortalGunPortal partner = partner(target);
        if (partner != null) partner.markPassed();
        else spawnPartner(target);
        target.playSoundEffect(spot.x(), spot.y(), spot.z(), "mob.endermen.portal", 0.8F, 1.3F);
    }

    public static boolean canExit(WorldServer target, Entity entity, Vec3d spot) {
        double half = entity.width / 2.0, feet = spot.y() - (entity instanceof EntityPlayerMP ? entity.yOffset : 0);
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            spot.x() - half,
            feet,
            spot.z() - half,
            spot.x() + half,
            feet + entity.height,
            spot.z() + half);
        // Collision queries skip unloaded columns. Load the exit footprint, including across chunk edges.
        int minX = MathHelper.floor_double(box.minX) >> 4, maxX = MathHelper.floor_double(box.maxX) >> 4;
        int minZ = MathHelper.floor_double(box.minZ) >> 4, maxZ = MathHelper.floor_double(box.maxZ) >> 4;
        for (int x = minX; x <= maxX; x++)
            for (int z = minZ; z <= maxZ; z++) target.theChunkProviderServer.loadChunk(x, z);
        return target.getCollidingBoundingBoxes(entity, box)
            .isEmpty();
    }

    private Vec3d carriedVelocity(Entity entity) {
        Track track = tracks.get(entity.getEntityId());
        return track == null ? Vec3d.ZERO
            : carried(
                track.current()
                    .subtract(track.previous()));
    }

    public static Vec3d carried(Vec3d velocity) {
        boolean moving = velocity.y() > 0 || Math.abs(velocity.x()) > MIN_CARRIED_SPEED
            || Math.abs(velocity.y()) > MIN_CARRIED_SPEED
            || Math.abs(velocity.z()) > MIN_CARRIED_SPEED;
        return moving ? velocity : Vec3d.ZERO;
    }

    private EntityPortalGunPortal partner(WorldServer target) {
        AxisAlignedBB around = AxisAlignedBB.getBoundingBox(
            link.x() - 0.25,
            link.y() - 0.25,
            link.z() - 0.25,
            link.x() + 0.25,
            link.y() + 0.25,
            link.z() + 0.25);
        List<?> found = target.getEntitiesWithinAABB(EntityPortalGunPortal.class, around);
        return found.isEmpty() ? null : (EntityPortalGunPortal) found.get(0);
    }

    private void spawnPartner(WorldServer target) {
        PortalGunPairs.OPENED.spawn(
            target,
            link,
            new PortalGunPlacement(worldObj.provider.dimensionId, posX, posY, posZ, facing, up),
            lifetime,
            gunId,
            pair);
    }

    private void markPassed() {
        dataWatcher.updateObject(PASSED_WATCHER, (int) worldObj.getTotalWorldTime());
    }

    public void close() {
        if (closing()) return;
        dataWatcher.updateObject(CLOSING_WATCHER, (byte) 1);
        worldObj.playSoundAtEntity(this, "random.fizz", 0.5F, 0.6F);
        if (!worldObj.isRemote && gunId != null) PortalGunPairs.OPENED.close(gunId, pair);
    }

    @Override
    public void setDead() {
        super.setDead();
        if (!worldObj.isRemote && gunId != null) PortalGunPairs.OPENED.forget(gunId, getEntityId());
    }

    public boolean closing() {
        return dataWatcher.getWatchableObjectByte(CLOSING_WATCHER) != 0;
    }

    public float openProgress(float partialTicks) {
        return MathUtils.clampUnit((ticksExisted + partialTicks) / OPEN_TICKS);
    }

    public float closeProgress(float partialTicks, int ticks) {
        return closing() ? MathUtils.clampUnit((closingTicks + partialTicks) / ticks) : 0;
    }

    public int lastPass() {
        return dataWatcher.getWatchableObjectInt(PASSED_WATCHER);
    }

    public float sincePass(float partialTicks) {
        int passed = lastPass();
        return passed == NEVER_PASSED ? Float.POSITIVE_INFINITY
            : (int) worldObj.getTotalWorldTime() - passed + partialTicks;
    }

    public UUID gunId() {
        return gunId;
    }

    public int pair() {
        return pair;
    }

    public ForgeDirection facing() {
        return facing;
    }

    public ForgeDirection up() {
        return up;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeByte(facing.ordinal());
        buffer.writeByte(up.ordinal());
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        facing = ForgeDirection.getOrientation(buffer.readByte());
        up = ForgeDirection.getOrientation(buffer.readByte());
        setPosition(posX, posY, posZ);
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tag) {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {}

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    private record Track(Vec3d previous, Vec3d current, int seen) {}
}
