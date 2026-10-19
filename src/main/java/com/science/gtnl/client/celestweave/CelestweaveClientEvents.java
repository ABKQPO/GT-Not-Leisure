package com.science.gtnl.client.celestweave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.client.railgun.RailgunArcRenderer;
import com.science.gtnl.client.railgun.RailgunShockwaveRenderer;
import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveReach;
import com.science.gtnl.common.celestweave.CelestweaveServerEvents;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.common.railgun.RailgunVec;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

@SideOnly(Side.CLIENT)
public final class CelestweaveClientEvents {

    private static final CelestweaveClientEvents INSTANCE = new CelestweaveClientEvents();
    private static final List<Scheduled> SCHEDULED = new ArrayList<>();
    private static final IntSet REMOTE_FLYING = new IntOpenHashSet();
    private static long frame;
    private static long ticks;
    private static World lastWorld;

    private record Scheduled(long due, Runnable task) {}

    private CelestweaveClientEvents() {}

    public static void register() {
        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(new CwShieldFlash());
        MinecraftForge.EVENT_BUS.register(new CwScreenGlow());
        MinecraftForge.EVENT_BUS.register(new CwHud());
        CwModel.register();
        CelestweaveServerEvents.local = player -> player == Minecraft.getMinecraft().thePlayer;
        CelestweaveClientInput.register();
        CelestweaveBackpackTabs.register();
    }

    static long renderFrame() {
        return frame;
    }

    static CwLook look(EntityLivingBase entity, CwClientState state) {
        int age = entity.ticksExisted - state.lookTick;
        if (state.look == null || age < 0 || age >= 20 || !CwLook.sameStacks(state.look, entity)) {
            state.look = CwLook.compute(entity);
            state.lookTick = entity.ticksExisted;
        }
        return state.look;
    }

    static double distanceToViewer(Entity entity) {
        Entity viewer = Minecraft.getMinecraft().renderViewEntity;
        return viewer == null || viewer == entity ? 0 : viewer.getDistanceToEntity(entity);
    }

    static boolean fxVisible(Entity entity, double distance) {
        if (MainConfig.client.celestweaveFxLevel <= 0) return false;
        if (entity != Minecraft.getMinecraft().thePlayer && !MainConfig.client.celestweaveShowOthersFx) return false;
        return distance <= MainConfig.client.celestweaveFxDistance;
    }

    @SubscribeEvent
    public void onRenderSpecials(RenderPlayerEvent.Specials.Pre event) {
        if (CelestweaveServerEvents.piece(event.entityPlayer, CwLook.CHEST) != null) event.renderCape = false;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) frame++;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.theWorld;
        if (world != lastWorld) {
            CwClientState.clear();
            CwShieldFlash.clear();
            CwScreenGlow.clear();
            CwHud.clear();
            SCHEDULED.clear();
            if (world == null) {
                CelestweaveReach.forgetClient();
                REMOTE_FLYING.clear();
            }
            lastWorld = world;
        }
        if (world == null || minecraft.isGamePaused()) return;
        ticks++;
        runScheduled();
        long now = Minecraft.getSystemTime();
        int level = MainConfig.client.celestweaveFxLevel;
        int total = 0;
        for (Object object : world.playerEntities) {
            if (!(object instanceof EntityPlayer player)) continue;
            CwClientState state = CwClientState.peek(player.getEntityId());
            if (state == null && !CelestweaveServerEvents.wearsAny(player)) continue;
            if (state == null) state = CwClientState.of(player);
            CwLook look = look(player, state);
            boolean flying = player == minecraft.thePlayer ? look.flightActive && player.capabilities.isFlying
                : look.flightInstalled && REMOTE_FLYING.contains(player.getEntityId());
            if (CwAnimator.wingToggle(state, flying, now) && look.wings && player.ticksExisted > 10)
                CelestweaveSounds.wings(player, flying);
            boolean self = player == minecraft.thePlayer;
            boolean process = distanceToViewer(player) <= MainConfig.client.celestweaveFxDistance
                && (self ? minecraft.gameSettings.thirdPersonView != 0 || MainConfig.client.celestweaveFirstPersonFx
                    : MainConfig.client.celestweaveShowOthersFx);
            CwArcs.tick(player, state, look, now, process ? level : 0);
            total += state.arcs.size();
        }
        CwArcs.total = total;
        if (ticks % 100 == 0) prune(world);
    }

    private static void prune(World world) {
        for (IntIterator ids = REMOTE_FLYING.iterator(); ids.hasNext();)
            if (world.getEntityByID(ids.nextInt()) == null) ids.remove();
        Iterator<Int2ObjectMap.Entry<CwClientState>> iterator = CwClientState.STATES.int2ObjectEntrySet()
            .iterator();
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry<CwClientState> entry = iterator.next();
            Entity entity = world.getEntityByID(entry.getIntKey());
            if (entity == null || entity.isDead
                || !(entity instanceof EntityLivingBase living)
                || !CelestweaveServerEvents.wearsAny(living) && entry.getValue().arcs.isEmpty()) iterator.remove();
        }
    }

    private static void runScheduled() {
        Iterator<Scheduled> iterator = SCHEDULED.iterator();
        List<Runnable> due = new ArrayList<>();
        while (iterator.hasNext()) {
            Scheduled scheduled = iterator.next();
            if (scheduled.due() > ticks) continue;
            due.add(scheduled.task());
            iterator.remove();
        }
        for (Runnable task : due) task.run();
    }

    private static void schedule(int delay, Runnable task) {
        if (SCHEDULED.size() < 256) SCHEDULED.add(new Scheduled(ticks + delay, task));
    }

    private static void dashJet(EntityLivingBase living, int color) {
        double yaw = Math.toRadians(living.rotationYaw);
        RailgunVec forward = new RailgunVec(-Math.sin(yaw), 0, Math.cos(yaw));
        RailgunVec right = RailgunVec.rightOf(forward);
        for (int tick = 0; tick < 8; tick++) {
            final int step = tick;
            schedule(tick, () -> {
                if (living.isDead) return;
                double feet = living.boundingBox.minY;
                RailgunVec back = new RailgunVec(living.posX, feet + living.height * 0.62, living.posZ)
                    .add(forward.scale(-0.35));
                int bolts = step == 0 ? 4 : 2;
                double reach = step == 0 ? 5.5 : 4.2 - step * 0.3;
                for (int i = 0; i < bolts; i++) {
                    RailgunVec to = back.add(forward.scale(-reach * CwArcs.rand(0.75F, 1F)))
                        .add(right.scale(CwArcs.rand(-0.7F, 0.7F)))
                        .add(0, CwArcs.rand(-0.9F, 0.25F), 0);
                    RailgunArcRenderer.spawnTinted(back, to, 12, 0.35, 4 + CwArcs.RNG.nextInt(3), color);
                }
                if (step > 3) return;
                for (int side : new int[] { 1, -1 }) {
                    RailgunVec foot = new RailgunVec(living.posX, feet + 0.05, living.posZ)
                        .add(right.scale(side * 0.15));
                    RailgunVec trail = foot.add(forward.scale(-CwArcs.rand(1.5F, 2.8F)))
                        .add(right.scale(side * CwArcs.rand(0.1F, 0.5F)))
                        .add(0, -0.02, 0);
                    RailgunArcRenderer.spawnTinted(foot, trail, 8, 0.12, 4 + CwArcs.RNG.nextInt(2), color);
                }
            });
        }
    }

    private static void aimHit(CwClientState state, EntityLivingBase living, float x, float y, float z, boolean omni) {
        double dx = x - living.posX, dy = y - (living.boundingBox.minY + living.height * 0.72), dz = z - living.posZ;
        double yaw = Math.toRadians(living.renderYawOffset);
        double side = dx * Math.cos(yaw) + dz * Math.sin(yaw), down = -dy;
        double length = Math.sqrt(side * side + down * down), distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        state.hitDirectional = !omni && length > 0.3 && length > distance * 0.35;
        if (!state.hitDirectional) return;
        state.hitDirX = (float) (side / length);
        state.hitDirY = (float) (down / length);
    }

    private static void lightning(float x, float y, float z, int color) {
        RailgunVec ground = new RailgunVec(x, y + 0.05, z);
        RailgunVec sky = ground.add(CwArcs.rand(-1.5F, 1.5F), 16, CwArcs.rand(-1.5F, 1.5F));
        RailgunVec drop = ground.subtract(sky);
        int bright = CwLook.mixWhite(color, 0.4F);
        for (int flash = 0; flash < 3; flash++) {
            final int step = flash;
            schedule(step * 2, () -> {
                RailgunArcRenderer
                    .spawnTinted(sky, ground, 32, 1.1, 5, step == 0 ? bright : color, step == 0 ? 3.2 : 2.2);
                if (step > 0) return;
                for (int i = 0; i < 3; i++) {
                    RailgunVec from = sky.add(drop.scale(CwArcs.rand(0.25F, 0.7F)));
                    double angle = CwArcs.rand(0F, (float) (Math.PI * 2)), reach = CwArcs.rand(1.5F, 3.5F);
                    RailgunVec to = from.add(Math.cos(angle) * reach, -CwArcs.rand(1.5F, 4F), Math.sin(angle) * reach);
                    RailgunArcRenderer.spawnTinted(from, to, 10, 0.5, 4, color, 1.2);
                }
                for (int i = 0; i < 8; i++) {
                    double angle = CwArcs.rand(0F, (float) (Math.PI * 2)), radius = CwArcs.rand(0.6F, 1.4F);
                    RailgunArcRenderer.spawnTinted(
                        ground,
                        ground.add(Math.cos(angle) * radius, 0.02, Math.sin(angle) * radius),
                        8,
                        0.12,
                        5,
                        bright);
                }
                RailgunShockwaveRenderer.spawn(ground, 1.8, 10, color);
            });
        }
    }

    public static void setRemoteFlying(int entityId, boolean flying) {
        if (flying) REMOTE_FLYING.add(entityId);
        else REMOTE_FLYING.remove(entityId);
    }

    public static void onFx(int entityId, int type, float x, float y, float z) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null || !(world.getEntityByID(entityId) instanceof EntityLivingBase living)) return;
        CwClientState state = CwClientState.of(living);
        CwLook look = look(living, state);
        CelestweaveCosmetic cosmetic = look.cosmetic;
        long now = Minecraft.getSystemTime();
        boolean allowed = fxVisible(living, distanceToViewer(living));
        switch (type) {
            case CelestweaveFxPacket.SHIELD_HIT, CelestweaveFxPacket.SHIELD_AMBIENT, CelestweaveFxPacket.SAVE -> {
                boolean save = type == CelestweaveFxPacket.SAVE, omni = type != CelestweaveFxPacket.SHIELD_HIT;
                if (save) CelestweaveSounds.shieldSave(living);
                else CelestweaveSounds.shieldHit(living);
                state.hitAt = now;
                state.hitPower = save ? 1.8F : 1F;
                aimHit(state, living, x, y, z, omni);
                if (living == Minecraft.getMinecraft().thePlayer) CwHud.hit(x, z, save, omni);
                if (!allowed || !cosmetic.event(CelestweaveCosmetic.EVENT_HIT)) return;
                CwArcs.hit(state, look, now);
                CwShieldFlash.spawn(living, x, y, z, look.theme, save, omni);
                if (living == Minecraft.getMinecraft().thePlayer) CwScreenGlow.trigger(x, y, z, look.theme, save, omni);
            }
            case CelestweaveFxPacket.LIGHTNING -> {
                CelestweaveSounds.lightning(x, y, z);
                if (allowed && cosmetic.event(CelestweaveCosmetic.EVENT_REFLECT)) lightning(x, y, z, look.theme);
            }
            case CelestweaveFxPacket.DASH -> {
                CelestweaveSounds.dash(living);
                state.dashAt = now;
                if (allowed && cosmetic.event(CelestweaveCosmetic.EVENT_DASH) && look.wears(CwLook.BOOTS)) {
                    CwArcs.dash(state, look, now);
                    dashJet(living, CwLook.mixWhite(look.theme, 0.3F));
                }
            }
            case CelestweaveFxPacket.LAND -> {
                CelestweaveSounds.land(x, y, z);
                if (!allowed || !cosmetic.event(CelestweaveCosmetic.EVENT_LAND)) return;
                for (int i = 0; i < 9; i++) {
                    float angle = CwArcs.rand(0F, (float) (Math.PI * 2)), radius = CwArcs.rand(0.3F, 0.6F);
                    RailgunVec from = new RailgunVec(
                        x + CwArcs.rand(-0.1F, 0.1F),
                        y + 0.03,
                        z + CwArcs.rand(-0.1F, 0.1F));
                    RailgunVec to = new RailgunVec(
                        x + Math.cos(angle) * radius,
                        y + 0.03,
                        z + Math.sin(angle) * radius);
                    schedule(
                        CwArcs.RNG.nextInt(2),
                        () -> RailgunArcRenderer.spawnTinted(from, to, 8, 0.06, 5 + CwArcs.RNG.nextInt(2), look.theme));
                }
            }
            case CelestweaveFxPacket.REFLECT -> {
                CelestweaveSounds.reflect(x, y, z);
                if (!allowed || !cosmetic.event(CelestweaveCosmetic.EVENT_REFLECT)) return;
                RailgunVec target = new RailgunVec(x, y, z);
                for (int i = 0; i < 3; i++) schedule(i * 3, () -> {
                    if (living.isDead) return;
                    RailgunVec chest = new RailgunVec(
                        living.posX,
                        living.boundingBox.minY + living.height * 0.72,
                        living.posZ);
                    RailgunArcRenderer.spawnTinted(chest, target, 14, 0.12, 5, look.theme);
                });
            }
            default -> {}
        }
    }
}
