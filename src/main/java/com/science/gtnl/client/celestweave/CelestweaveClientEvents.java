package com.science.gtnl.client.celestweave;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import com.gtnllib.client.fx.world.FxArcRenderer;
import com.gtnllib.client.fx.world.FxShockwaveRenderer;
import com.gtnllib.client.fx.world.FxVisibility;
import com.gtnllib.utils.ColorMath;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveReach;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class CelestweaveClientEvents {

    private static final Random RNG = new Random();
    private static final CelestweaveClientEvents INSTANCE = new CelestweaveClientEvents();
    private static final List<Scheduled> SCHEDULED = new ObjectArrayList<>();
    private static final IntSet REMOTE_FLYING = new IntOpenHashSet();
    private static final double HIT_AIM_HEIGHT = 0.72, HIT_AIM_MIN_OFFSET = 0.3, HIT_AIM_MIN_RATIO = 0.35;
    private static final int LOOK_REFRESH_TICKS = 20, WING_SOUND_MIN_AGE = 10, PRUNE_INTERVAL_TICKS = 100,
        MAX_SCHEDULED = 256, DASH_JET_TICKS = 8;
    private static long frame;
    private static long ticks;
    private static World lastWorld;

    private record Scheduled(long due, Runnable task) {}

    public record HitOrigin(double x, double y, double z) {

        Vec3d offset(EntityLivingBase entity, double heightFraction) {
            return new Vec3d(
                x - entity.posX,
                y - (entity.boundingBox.minY + entity.height * heightFraction),
                z - entity.posZ);
        }

        static double forward(Vec3d offset, float yawDegrees) {
            double yaw = Math.toRadians(yawDegrees);
            return offset.x() * -Math.sin(yaw) + offset.z() * Math.cos(yaw);
        }

        static double lateral(Vec3d offset, float yawDegrees) {
            double yaw = Math.toRadians(yawDegrees);
            return offset.x() * -Math.cos(yaw) + offset.z() * -Math.sin(yaw);
        }
    }

    private CelestweaveClientEvents() {}

    public static void register() {
        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(new CelestweaveShieldFlash());
        MinecraftForge.EVENT_BUS.register(new CelestweaveScreenGlow());
        MinecraftForge.EVENT_BUS.register(new CelestweaveHud());
        CelestweaveHudElement.register();
        CelestweaveObjModel.register();
        CelestweaveClientInput.register();
        CelestweaveBackpackClient.registerTabs();
        CelestweaveThemeAccent.register();
    }

    static long renderFrame() {
        return frame;
    }

    static CelestweaveLook look(EntityLivingBase entity, CelestweaveClientState state) {
        int age = entity.ticksExisted - state.lookTick;
        if (state.look == null || age < 0
            || age >= LOOK_REFRESH_TICKS
            || !CelestweaveLook.sameStacks(state.look, entity)) {
            state.look = CelestweaveLook.compute(entity);
            state.lookTick = entity.ticksExisted;
        }
        return state.look;
    }

    static boolean fxAllowed(Entity entity, double distance) {
        return MainConfig.client.celestweaveFxLevel > 0 && FxVisibility.allowed(
            FxVisibility.isViewer(entity),
            distance,
            MainConfig.client.enableCelestweaveShowOthersFx,
            MainConfig.client.celestweaveFxDistance);
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        CelestweaveSkinLayers.hide(event.renderer.modelBipedMain, event.entityPlayer);
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        CelestweaveSkinLayers.restore();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderSpecials(RenderPlayerEvent.Specials.Pre event) {
        if (CelestweaveData.piece(event.entityPlayer, CelestweaveArmor.CHEST) != null) event.renderCape = false;
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) frame++;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.theWorld;
        if (world != lastWorld) {
            CelestweaveClientState.clear();
            CelestweaveShieldFlash.clear();
            CelestweaveScreenGlow.clear();
            CelestweaveHud.clear();
            CelestweaveThemeAccent.clear();
            SCHEDULED.clear();
            REMOTE_FLYING.clear();
            if (world == null) CelestweaveReach.clearClient();
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
            CelestweaveClientState state = CelestweaveClientState.peek(player.getEntityId());
            if (state == null && !CelestweaveData.wearsAny(player)) continue;
            if (state == null) state = CelestweaveClientState.of(player);
            CelestweaveLook look = look(player, state);
            boolean flying = player == minecraft.thePlayer ? look.flightActive && player.capabilities.isFlying
                : look.flightInstalled && REMOTE_FLYING.contains(player.getEntityId());
            if (CelestweaveAnimator.wingToggle(state, flying, now) && look.visible(CelestweaveModule.FLIGHT)
                && player.ticksExisted > WING_SOUND_MIN_AGE) CelestweaveSounds.wings(player, flying);
            boolean self = player == minecraft.thePlayer;
            boolean process = FxVisibility.allowed(
                self,
                FxVisibility.distanceToViewer(player),
                MainConfig.client.enableCelestweaveShowOthersFx,
                MainConfig.client.celestweaveFxDistance)
                && (!self || minecraft.gameSettings.thirdPersonView != 0
                    || MainConfig.client.enableCelestweaveFirstPersonFx);
            CelestweaveArcs.tick(player, state, look, now, process ? level : 0);
            total += state.arcs.size();
        }
        CelestweaveArcs.total = total;
        if (ticks % PRUNE_INTERVAL_TICKS == 0) prune(world);
    }

    private static void prune(World world) {
        for (IntIterator ids = REMOTE_FLYING.iterator(); ids.hasNext();)
            if (world.getEntityByID(ids.nextInt()) == null) ids.remove();
        Iterator<Int2ObjectMap.Entry<CelestweaveClientState>> iterator = CelestweaveClientState.STATES
            .int2ObjectEntrySet()
            .iterator();
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry<CelestweaveClientState> entry = iterator.next();
            Entity entity = world.getEntityByID(entry.getIntKey());
            if (entity == null || entity.isDead
                || !(entity instanceof EntityLivingBase living)
                || !CelestweaveData.wearsAny(living) && entry.getValue().arcs.isEmpty()) iterator.remove();
        }
    }

    private static void runScheduled() {
        Iterator<Scheduled> iterator = SCHEDULED.iterator();
        List<Runnable> due = new ObjectArrayList<>();
        while (iterator.hasNext()) {
            Scheduled scheduled = iterator.next();
            if (scheduled.due() > ticks) continue;
            due.add(scheduled.task());
            iterator.remove();
        }
        for (Runnable task : due) task.run();
    }

    private static void schedule(int delay, Runnable task) {
        if (SCHEDULED.size() < MAX_SCHEDULED) SCHEDULED.add(new Scheduled(ticks + delay, task));
    }

    private static void dashJet(EntityLivingBase living, int rgb) {
        double yaw = Math.toRadians(living.rotationYaw);
        Vec3d forward = new Vec3d(-Math.sin(yaw), 0, Math.cos(yaw));
        Vec3d right = Vec3d.rightOf(forward);
        for (int tick = 0; tick < DASH_JET_TICKS; tick++) {
            final int step = tick;
            schedule(tick, () -> {
                if (living.isDead) return;
                double feet = living.boundingBox.minY;
                Vec3d back = new Vec3d(living.posX, feet + living.height * 0.62, living.posZ).add(forward.scale(-0.35));
                int bolts = step == 0 ? 4 : 2;
                double reach = step == 0 ? 5.5 : 4.2 - step * 0.3;
                for (int i = 0; i < bolts; i++) {
                    Vec3d to = back.add(forward.scale(-reach * CelestweaveArcs.rand(0.75F, 1F)))
                        .add(right.scale(CelestweaveArcs.rand(-0.7F, 0.7F)))
                        .add(0, CelestweaveArcs.rand(-0.9F, 0.25F), 0);
                    FxArcRenderer.spawnColored(back, to, 12, 0.35, 4 + RNG.nextInt(3), rgb);
                }
                if (step > 3) return;
                for (int side : new int[] { 1, -1 }) {
                    Vec3d foot = new Vec3d(living.posX, feet + 0.05, living.posZ).add(right.scale(side * 0.15));
                    Vec3d trail = foot.add(forward.scale(-CelestweaveArcs.rand(1.5F, 2.8F)))
                        .add(right.scale(side * CelestweaveArcs.rand(0.1F, 0.5F)))
                        .add(0, -0.02, 0);
                    FxArcRenderer.spawnColored(foot, trail, 8, 0.12, 4 + RNG.nextInt(2), rgb);
                }
            });
        }
    }

    private static void aimHit(CelestweaveClientState state, EntityLivingBase living, HitOrigin origin, boolean omni) {
        Vec3d offset = origin.offset(living, HIT_AIM_HEIGHT);
        double side = -HitOrigin.lateral(offset, living.renderYawOffset), down = -offset.y();
        double length = Math.sqrt(side * side + down * down);
        state.hitDirectional = !omni && length > HIT_AIM_MIN_OFFSET && length > offset.length() * HIT_AIM_MIN_RATIO;
        if (!state.hitDirectional) return;
        state.hitDirectionX = (float) (side / length);
        state.hitDirectionY = (float) (down / length);
    }

    private static void lightning(float x, float y, float z, int rgb) {
        Vec3d ground = new Vec3d(x, y + 0.05, z);
        Vec3d sky = ground.add(CelestweaveArcs.rand(-1.5F, 1.5F), 16, CelestweaveArcs.rand(-1.5F, 1.5F));
        Vec3d drop = ground.subtract(sky);
        int bright = ColorMath.mixWhite(rgb, 0.4F);
        for (int flash = 0; flash < 3; flash++) {
            final int step = flash;
            schedule(step * 2, () -> {
                FxArcRenderer.spawnColored(sky, ground, 32, 1.1, 5, step == 0 ? bright : rgb, step == 0 ? 3.2 : 2.2);
                if (step > 0) return;
                for (int i = 0; i < 3; i++) {
                    Vec3d from = sky.add(drop.scale(CelestweaveArcs.rand(0.25F, 0.7F)));
                    double angle = CelestweaveArcs.rand(0F, (float) (Math.PI * 2)),
                        reach = CelestweaveArcs.rand(1.5F, 3.5F);
                    Vec3d to = from
                        .add(Math.cos(angle) * reach, -CelestweaveArcs.rand(1.5F, 4F), Math.sin(angle) * reach);
                    FxArcRenderer.spawnColored(from, to, 10, 0.5, 4, rgb, 1.2);
                }
                for (int i = 0; i < 8; i++) {
                    double angle = CelestweaveArcs.rand(0F, (float) (Math.PI * 2)),
                        radius = CelestweaveArcs.rand(0.6F, 1.4F);
                    FxArcRenderer.spawnColored(
                        ground,
                        ground.add(Math.cos(angle) * radius, 0.02, Math.sin(angle) * radius),
                        8,
                        0.12,
                        5,
                        bright);
                }
                FxShockwaveRenderer.spawn(ground, 1.8, 10, rgb);
            });
        }
    }

    public static void setRemoteFlying(int entityId, boolean flying) {
        if (flying) REMOTE_FLYING.add(entityId);
        else REMOTE_FLYING.remove(entityId);
    }

    public static void onFx(int entityId, CelestweaveFxPacket.Type type, float x, float y, float z) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null || !(world.getEntityByID(entityId) instanceof EntityLivingBase living)) return;
        CelestweaveClientState state = CelestweaveClientState.of(living);
        CelestweaveLook look = look(living, state);
        CelestweaveCosmetic cosmetic = look.cosmetic;
        long now = Minecraft.getSystemTime();
        boolean allowed = fxAllowed(living, FxVisibility.distanceToViewer(living));
        switch (type) {
            case SHIELD_HIT, SHIELD_AMBIENT, SAVE -> {
                boolean save = type == CelestweaveFxPacket.Type.SAVE,
                    omni = type != CelestweaveFxPacket.Type.SHIELD_HIT;
                HitOrigin origin = new HitOrigin(x, y, z);
                if (save) CelestweaveSounds.shieldSave(living);
                else CelestweaveSounds.shieldHit(living);
                state.hitAt = now;
                state.hitPower = save ? 1.8F : 1F;
                aimHit(state, living, origin, omni);
                if (living == Minecraft.getMinecraft().thePlayer) CelestweaveHud.hit(origin, save, omni);
                if (!allowed || !cosmetic.event(CelestweaveCosmetic.EVENT_HIT)) return;
                CelestweaveArcs.hit(state, look, now);
                CelestweaveShieldFlash.spawn(living, x, y, z, look.glowColor, save, omni);
                if (living == Minecraft.getMinecraft().thePlayer)
                    CelestweaveScreenGlow.hit(origin, look.glowColor, save, omni);
            }
            case LIGHTNING -> {
                CelestweaveSounds.lightning(x, y, z);
                if (allowed && cosmetic.event(CelestweaveCosmetic.EVENT_REFLECT)) lightning(x, y, z, look.glowColor);
            }
            case DASH -> {
                CelestweaveSounds.dash(living);
                state.dashAt = now;
                if (allowed && cosmetic.event(CelestweaveCosmetic.EVENT_DASH) && look.wears(CelestweaveArmor.BOOTS)) {
                    CelestweaveArcs.dash(state, look, now);
                    dashJet(living, ColorMath.mixWhite(look.glowColor, 0.3F));
                }
            }
            case LAND -> {
                CelestweaveSounds.land(x, y, z);
                if (!allowed || !cosmetic.event(CelestweaveCosmetic.EVENT_LAND)) return;
                for (int i = 0; i < 9; i++) {
                    float angle = CelestweaveArcs.rand(0F, (float) (Math.PI * 2)),
                        radius = CelestweaveArcs.rand(0.3F, 0.6F);
                    Vec3d from = new Vec3d(
                        x + CelestweaveArcs.rand(-0.1F, 0.1F),
                        y + 0.03,
                        z + CelestweaveArcs.rand(-0.1F, 0.1F));
                    Vec3d to = new Vec3d(x + Math.cos(angle) * radius, y + 0.03, z + Math.sin(angle) * radius);
                    schedule(
                        RNG.nextInt(2),
                        () -> FxArcRenderer.spawnColored(from, to, 8, 0.06, 5 + RNG.nextInt(2), look.glowColor));
                }
            }
            case REFLECT -> {
                CelestweaveSounds.reflect(x, y, z);
                if (!allowed || !cosmetic.event(CelestweaveCosmetic.EVENT_REFLECT)) return;
                Vec3d target = new Vec3d(x, y, z);
                for (int i = 0; i < 3; i++) schedule(i * 3, () -> {
                    if (living.isDead) return;
                    Vec3d chest = new Vec3d(living.posX, living.boundingBox.minY + living.height * 0.72, living.posZ);
                    FxArcRenderer.spawnColored(chest, target, 14, 0.12, 5, look.glowColor);
                });
            }
        }
    }
}
