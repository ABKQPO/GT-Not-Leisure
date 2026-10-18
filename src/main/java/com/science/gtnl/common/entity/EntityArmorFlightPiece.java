package com.science.gtnl.common.entity;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.science.gtnl.api.armorflight.ArmorFlightAnchor;
import com.science.gtnl.api.armorflight.ArmorFlightPiece;
import com.science.gtnl.common.armorflight.ArmorFlight;
import com.science.gtnl.common.armorflight.ArmorFlightService;
import com.science.gtnl.common.armorflight.FlightVec;
import com.science.gtnl.common.armorflight.IArmorFlightEffects;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class EntityArmorFlightPiece extends Entity implements IEntityAdditionalSpawnData {

    public static final byte MODE_DELIVER = 0, MODE_RETURN = 1;
    private static final int OWNER_GRACE_TICKS = 60;
    private static final int MAX_EXTRA_LIFETIME = 600;
    private static final int DATA_ITEM = 10;
    private static final int DATA_TARGET = 11;

    public static volatile IArmorFlightEffects clientEffects;

    private ArmorFlightPiece piece = ArmorFlightPiece.FEET;
    private byte mode = MODE_DELIVER;
    private FlightVec start = FlightVec.ZERO;
    private FlightVec end = FlightVec.ZERO;
    private long launch;
    private int duration = 20;
    private boolean behind;

    private UUID owner;
    private ArmorFlightAnchor anchor;
    private int missingOwnerTicks;
    private boolean finished;
    private boolean purged;

    private FlightVec purgeEnd;
    public int fxFlags;

    public EntityArmorFlightPiece(World world) {
        super(world);
        setSize(0.6F, 0.6F);
        noClip = true;
        ignoreFrustumCheck = true;
        isImmuneToFire = true;
    }

    public static EntityArmorFlightPiece deliver(EntityPlayerMP owner, ItemStack item, ArmorFlightPiece piece,
        FlightVec start, ArmorFlightAnchor anchor, boolean behind, int delayTicks, int durationTicks) {
        EntityArmorFlightPiece e = new EntityArmorFlightPiece(owner.worldObj);
        e.owner = owner.getUniqueID();
        e.anchor = anchor;
        e.behind = behind;
        e.setup(
            item,
            piece,
            MODE_DELIVER,
            owner.getEntityId(),
            start,
            start,
            owner.worldObj.getTotalWorldTime() + delayTicks,
            durationTicks);
        return e;
    }

    public static EntityArmorFlightPiece echo(EntityPlayerMP from, ItemStack copy, ArmorFlightPiece piece,
        FlightVec start, FlightVec end) {
        EntityArmorFlightPiece e = new EntityArmorFlightPiece(from.worldObj);
        int duration = ArmorFlight.PURGE_TICKS + ArmorFlight.returnTicksFor(ArmorFlight.length(start, end));
        e.setup(copy, piece, MODE_RETURN, from.getEntityId(), start, end, from.worldObj.getTotalWorldTime(), duration);
        return e;
    }

    private void setup(ItemStack item, ArmorFlightPiece piece, byte mode, int target, FlightVec start, FlightVec end,
        long launch, int duration) {
        dataWatcher.updateObject(DATA_ITEM, item);
        dataWatcher.updateObject(DATA_TARGET, target);
        this.piece = piece;
        this.mode = mode;
        this.start = start;
        this.end = end;
        this.launch = launch;
        this.duration = Math.max(1, duration);
        setPosition(start.x(), start.y(), start.z());
        lastTickPosX = prevPosX = posX;
        lastTickPosY = prevPosY = posY;
        lastTickPosZ = prevPosZ = posZ;
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObjectByDataType(DATA_ITEM, 5);
        dataWatcher.addObject(DATA_TARGET, -1);
    }

    public ItemStack item() {
        return dataWatcher.getWatchableObjectItemStack(DATA_ITEM);
    }

    public ArmorFlightPiece piece() {
        return piece;
    }

    public boolean isReturn() {
        return mode == MODE_RETURN;
    }

    public FlightVec start() {
        return start;
    }

    public int duration() {
        return duration;
    }

    public UUID owner() {
        return owner;
    }

    public Entity target() {
        return worldObj.getEntityByID(dataWatcher.getWatchableObjectInt(DATA_TARGET));
    }

    public float wearerYaw(Entity target, float partialTick) {
        IArmorFlightEffects fx = clientEffects;
        return worldObj.isRemote && fx != null ? fx.wearerYaw(target, partialTick)
            : ArmorFlight.bodyYaw(target, partialTick);
    }

    public float age(float partialTick) {
        return (float) (worldObj.getTotalWorldTime() - launch) + partialTick;
    }

    public int orbitStart() {
        return duration + ArmorFlight.HOVER_TICKS;
    }

    public int snapStart() {
        return orbitStart() + ArmorFlight.orbitTicks(behind);
    }

    public int snapEnd() {
        return snapStart() + ArmorFlight.SNAP_TICKS;
    }

    public boolean orbiting(float age) {
        return behind && age >= orbitStart() && age < snapStart();
    }

    public float latchAge(float partialTick) {
        return age(partialTick) - snapEnd();
    }

    public FlightVec flightPosition(float partialTick) {
        if (isReturn()) return returnPosition(partialTick);
        float a = age(partialTick);
        if (a <= 0) return hatchPosition(a);
        Entity target = target();
        if (target == null) return start;
        float yaw = wearerYaw(target, partialTick);
        float side = ArmorFlight.sideAngle(piece, behind);
        FlightVec dir = ArmorFlight.forward(yaw + side);
        FlightVec dock = ArmorFlight.dockPoint(target, partialTick, piece);
        FlightVec hold = ArmorFlight.holdPoint(dock, dir);
        if (a < duration) {
            return ArmorFlight.approach(start, hold, dir, piece, ArmorFlight.flightProfile(a / duration));
        }
        float hover = a - duration;
        if (hover < ArmorFlight.HOVER_TICKS) return hold.add(0, bob(hover), 0);
        double settled = bob(ArmorFlight.HOVER_TICKS);
        float orbit = a - orbitStart();
        int orbitTicks = ArmorFlight.orbitTicks(behind);
        if (orbit < orbitTicks) {
            return ArmorFlight.orbitPoint(dock, yaw, side, orbit / orbitTicks)
                .add(0, settled, 0);
        }
        float snap = a - snapStart();
        if (snap < ArmorFlight.SNAP_TICKS) {
            double u = Math.pow(snap / ArmorFlight.SNAP_TICKS, 2.6);
            FlightVec from = (behind ? ArmorFlight.orbitPoint(dock, yaw, side, 1) : hold).add(0, settled, 0);
            return from.add(
                dock.subtract(from)
                    .scale(u));
        }
        return dock;
    }

    private static double bob(float ticks) {
        double settle = Math.min(1, ticks / 3.0);
        return Math.sin(ticks * 0.75) * 0.055 * settle - 0.04 * Math.exp(-ticks / 1.5);
    }

    private FlightVec hatchPosition(float a) {
        double k = ArmorFlight.clamp(1 + a / ArmorFlight.HATCH_TICKS, 0, 1);
        double rise = -0.55 * Math.pow(1 - k, 2);
        double shake = k > 0.5 ? Math.sin(a * 13.0) * 0.015 * k : 0;
        return start.add(shake, rise, Math.cos(a * 11.0) * shake);
    }

    private FlightVec burstOffset(float yaw, double k) {
        float scatter = (getEntityId() * 37) % 61 - 30;
        return ArmorFlight.purgeOffset(piece, yaw + scatter, ArmorFlight.BURST_DISTANCE * k);
    }

    private FlightVec returnPosition(float partialTick) {
        float a = age(partialTick);
        Entity target = target();
        if (a < ArmorFlight.PURGE_TICKS) {
            if (target == null) return start;
            float yaw = wearerYaw(target, partialTick);
            return ArmorFlight.dockPoint(target, partialTick, piece)
                .add(burstOffset(yaw, ArmorFlight.burstCurve(Math.max(0, a))));
        }
        if (purgeEnd == null) {
            if (target != null) {
                float yaw = wearerYaw(target, 1.0F);
                purgeEnd = ArmorFlight.dockPoint(target, 1.0F, piece)
                    .add(burstOffset(yaw, ArmorFlight.burstCurve(ArmorFlight.PURGE_TICKS)));
            } else {
                purgeEnd = start;
            }
        }
        double u = (a - ArmorFlight.PURGE_TICKS) / Math.max(1.0, duration - ArmorFlight.PURGE_TICKS);
        return ArmorFlight.bezier(purgeEnd, end, ArmorFlight.boost(u));
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        ticksExisted++;
        FlightVec at = flightPosition(0);
        setPosition(at.x(), at.y(), at.z());
        if (worldObj.isRemote) {
            IArmorFlightEffects fx = clientEffects;
            if (fx != null) fx.tick(this);
            return;
        }
        if (isReturn()) {
            if (age(0) >= duration + 2 || ticksExisted > duration + 60) super.setDead();
            return;
        }
        EntityPlayerMP player = ArmorFlightService.findPlayer(owner);
        boolean present = player != null && player.isEntityAlive() && player.worldObj == worldObj;
        if (present && dataWatcher.getWatchableObjectInt(DATA_TARGET) != player.getEntityId()) {
            dataWatcher.updateObject(DATA_TARGET, player.getEntityId());
        }
        if (!present) {
            if (++missingOwnerTicks > OWNER_GRACE_TICKS) giveBack(player);
            return;
        }
        missingOwnerTicks = 0;
        float a = age(0);
        if (!purged && a >= duration) purge(player);
        if (a >= snapEnd() + ArmorFlight.LATCH_TICKS) dock(player);
        else if (ticksExisted > snapEnd() + MAX_EXTRA_LIFETIME) giveBack(player);
    }

    private void purge(EntityPlayerMP player) {
        purged = true;
        ItemStack old = piece.worn(player);
        if (old == null) return;
        piece.setWorn(player, null);
        ArmorFlightService.sendHome(anchor, player, piece, old);
    }

    private void dock(EntityPlayerMP player) {
        if (finished) return;
        finished = true;
        ItemStack incoming = item();
        ItemStack old = piece.worn(player);
        piece.setWorn(player, incoming == null ? null : incoming.copy());
        dataWatcher.updateObject(DATA_ITEM, null);
        FlightVec at = ArmorFlight.dockPoint(player, 1.0F, piece);
        if (worldObj instanceof WorldServer server) {
            server.func_147487_a("magicCrit", at.x(), at.y(), at.z(), 8, 0.25, 0.2, 0.25, 0.05);
        }
        if (old != null) ArmorFlightService.sendHome(anchor, player, piece, old);
        super.setDead();
    }

    private void giveBack(EntityPlayerMP player) {
        if (finished) return;
        finished = true;
        ItemStack stack = item();
        dataWatcher.updateObject(DATA_ITEM, null);
        if (stack != null) {
            ItemStack rest = ArmorFlightService.storeHome(anchor, player, piece, stack.copy());
            if (rest != null && player != null) {
                ArmorFlightService.giveToPlayer(player, rest);
                rest = null;
            }
            if (rest != null) entityDropItem(rest, 0.0F);
        }
        super.setDead();
    }

    @Override
    public void setDead() {
        if (!worldObj.isRemote && !isReturn() && !finished) {
            giveBack(ArmorFlightService.findPlayer(owner));
            return;
        }
        super.setDead();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {}

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tag) {
        return !isReturn() && !finished && super.writeToNBTOptional(tag);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
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

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 256 * 256;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        ItemStack stack = item();
        if (stack != null) tag.setTag("Item", stack.writeToNBT(new NBTTagCompound()));
        tag.setInteger("Piece", piece.ordinal());
        tag.setDouble("SX", start.x());
        tag.setDouble("SY", start.y());
        tag.setDouble("SZ", start.z());
        tag.setLong("Launch", launch);
        tag.setInteger("Duration", duration);
        tag.setBoolean("Purged", purged);
        tag.setBoolean("Behind", behind);
        if (owner != null) {
            tag.setLong("OwnerMost", owner.getMostSignificantBits());
            tag.setLong("OwnerLeast", owner.getLeastSignificantBits());
        }
        if (anchor != null) tag.setTag("Anchor", anchor.writeToNBT());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        ItemStack stack = tag.hasKey("Item") ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Item")) : null;
        dataWatcher.updateObject(DATA_ITEM, stack);
        piece = ArmorFlightPiece.byOrdinal(tag.getInteger("Piece"));
        mode = MODE_DELIVER;
        start = new FlightVec(tag.getDouble("SX"), tag.getDouble("SY"), tag.getDouble("SZ"));
        end = start;
        launch = tag.getLong("Launch");
        duration = Math.max(1, tag.getInteger("Duration"));
        purged = tag.getBoolean("Purged");
        behind = tag.getBoolean("Behind");
        owner = tag.hasKey("OwnerMost") ? new UUID(tag.getLong("OwnerMost"), tag.getLong("OwnerLeast")) : null;
        anchor = tag.hasKey("Anchor") ? ArmorFlightAnchor.readFromNBT(tag.getCompoundTag("Anchor")) : null;
        if (stack == null) finished = true;
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeByte(piece.ordinal());
        buf.writeByte(mode);
        buf.writeDouble(start.x());
        buf.writeDouble(start.y());
        buf.writeDouble(start.z());
        buf.writeDouble(end.x());
        buf.writeDouble(end.y());
        buf.writeDouble(end.z());
        buf.writeLong(launch);
        buf.writeInt(duration);
        buf.writeBoolean(behind);
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        piece = ArmorFlightPiece.byOrdinal(buf.readByte());
        mode = buf.readByte();
        start = new FlightVec(buf.readDouble(), buf.readDouble(), buf.readDouble());
        end = new FlightVec(buf.readDouble(), buf.readDouble(), buf.readDouble());
        launch = buf.readLong();
        duration = Math.max(1, buf.readInt());
        behind = buf.readBoolean();
        FlightVec at = flightPosition(0);
        setPosition(at.x(), at.y(), at.z());
        lastTickPosX = prevPosX = posX;
        lastTickPosY = prevPosY = posY;
        lastTickPosZ = prevPosZ = posZ;
    }
}
