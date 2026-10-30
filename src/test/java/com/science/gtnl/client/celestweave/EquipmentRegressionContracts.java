package com.science.gtnl.client.celestweave;

import static com.gtnllib.GTNLLibContractTest.call;
import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.compact;
import static com.gtnllib.GTNLLibContractTest.field;
import static com.gtnllib.GTNLLibContractTest.near;
import static com.gtnllib.GTNLLibContractTest.read;
import static com.science.gtnl.client.celestweave.CelestweaveArmorContracts.allocate;
import static com.science.gtnl.client.celestweave.CelestweaveArmorContracts.setField;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.roundTrip;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.celestweave.CelestweaveBehaviors.Reflect;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.common.item.items.PortalGun;
import com.science.gtnl.common.packet.CelestweaveFireballPacket;
import com.science.gtnl.common.portalgun.PortalGunGeometry;
import com.science.gtnl.common.portalgun.PortalGunPlacement;
import com.science.gtnl.common.railgun.RailgunChainResolver;
import com.science.gtnl.common.railgun.RailgunChainResolver.Hit;
import com.science.gtnl.common.railgun.RailgunChainResolver.Kind;
import com.science.gtnl.common.railgun.RailgunDamage.DamageContext;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.eventhandler.ListenerList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import vazkii.botania.common.entity.EntityManaBurst;

public class EquipmentRegressionContracts {

    static void run() throws Exception {
        reflectedProjectiles();
        reflectedFireballSync();
        penetration();
        portalExit();
        shimmerPermissions();
    }

    private static TestWorld world() throws ReflectiveOperationException {
        TestWorld world = allocate(TestWorld.class);
        world.entities = new ObjectArrayList<>();
        world.obstacles = new ObjectArrayList<>();
        world.loaded = new ObjectOpenHashSet<>();
        TestChunks chunks = allocate(TestChunks.class);
        chunks.loaded = world.loaded;
        world.theChunkProviderServer = chunks;
        world.mine = true;
        return world;
    }

    private static <T extends Entity> T entity(Class<T> type, double x, double y, double z)
        throws ReflectiveOperationException {
        T entity = allocate(type);
        entity.width = 0.6F;
        entity.height = 1.8F;
        setField(Entity.class, entity, "boundingBox", AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0));
        entity.setPosition(x, y, z);
        return entity;
    }

    private static EntityLivingBase mob(int id, double x, double y, double z) throws ReflectiveOperationException {
        EntityCow mob = entity(EntityCow.class, x, y, z);
        mob.setEntityId(id);
        DataWatcher watcher = new DataWatcher(mob);
        watcher.addObject(6, 20F);
        setField(Entity.class, mob, "dataWatcher", watcher);
        return mob;
    }

    private static void reflectedProjectiles() throws Exception {
        TestWorld world = world();
        EntityPlayer player = entity(TestPlayer.class, 0, 0, 0);
        player.worldObj = world;
        List<Entity> expected = List.of(
            entity(EntityLargeFireball.class, 1, 1, 0),
            entity(EntitySmallFireball.class, 1, 1, 0),
            entity(EntityWitherSkull.class, 1, 1, 0),
            entity(EntityArrow.class, 1, 1, 0),
            entity(EntitySnowball.class, 1, 1, 0));
        world.entities.addAll(expected);
        world.entities.add(entity(EntityManaBurst.class, 1, 1, 0));
        world.entities.add(entity(EntityItem.class, 1, 1, 0));
        world.entities.add(entity(EntityLargeFireball.class, 20, 1, 0));
        world.entities.add(player);
        List<?> found = (List<?>) call(
            Reflect.class,
            "nearbyProjectiles",
            null,
            new Class<?>[] { EntityPlayer.class },
            player);
        check(
            found.equals(expected),
            "reflection finds vanilla fireballs, arrows and throwables, excludes mana bursts, items and distant shots");
    }

    private static void reflectedFireballSync() throws Exception {
        EntityLargeFireball server = entity(EntityLargeFireball.class, 1, 1, 0);
        server.setEntityId(47);
        server.accelerationX = 0.08;
        server.accelerationY = -0.06;
        server.accelerationZ = 0;
        CelestweaveFireballPacket packet = roundTrip(
            new CelestweaveFireballPacket(server),
            CelestweaveFireballPacket.class.getConstructor());
        check((int) field(packet, "entityId") == 47, "the reflection packet addresses the reflected fireball");
        for (Class<? extends EntityFireball> type : List
            .of(EntityLargeFireball.class, EntitySmallFireball.class, EntityWitherSkull.class)) {
            EntityFireball client = entity(type, 1, 1, 0);
            call(CelestweaveFireballPacket.class, "apply", packet, new Class<?>[] { Entity.class }, client);
            near(0.08, client.accelerationX, "reflected fireball acceleration x reaches the client");
            near(-0.06, client.accelerationY, "reflected fireball acceleration y reaches the client");
            near(0, client.accelerationZ, "reflected fireball acceleration z reaches the client");
        }
        call(CelestweaveFireballPacket.class, "apply", packet, new Class<?>[] { Entity.class }, (Object) null);
        call(
            CelestweaveFireballPacket.class,
            "apply",
            packet,
            new Class<?>[] { Entity.class },
            entity(EntityArrow.class, 0, 0, 0));
        ByteBuf invalid = Unpooled.buffer();
        try {
            invalid.writeInt(47)
                .writeDouble(Double.NaN)
                .writeDouble(0)
                .writeDouble(0);
            packet.fromBytes(invalid);
            call(CelestweaveFireballPacket.class, "apply", packet, new Class<?>[] { Entity.class }, server);
            near(0.08, server.accelerationX, "invalid acceleration is ignored");
        } finally {
            invalid.release();
        }
        String network = compact(read(SOURCES.resolve("common/packet/NetWorkHandler.java")));
        check(
            network.contains("registerClientbound(CelestweaveFireballPacket.class)"),
            "fireball updates are registered as clientbound");
    }

    private static void penetration() throws Exception {
        TestWorld world = world();
        EntityPlayerMP shooter = entity(TestPlayer.class, 0, 0, 0);
        EntityLivingBase first = mob(1, 5, 0, 0), second = mob(2, 20, 0, 0), third = mob(3, 30, 0, 0);
        world.entities
            .addAll(List.of(third, second, first, mob(4, 2, 0, 0), mob(5, 20, 0, 4), mob(6, 41, 0, 0), shooter));
        DamageContext max = new DamageContext(100, 0.5, 0.9, 0, 0, 0, true, false);
        Vec3d impact = new Vec3d(4.5, 1.62, 0), forward = new Vec3d(1, 0, 0);
        List<Hit> hits = RailgunChainResolver.penetration(world, shooter, first, impact, forward, max, 5);
        check(
            hits.size() == 2 && hits.get(0)
                .target() == second
                && hits.get(1)
                    .target() == third,
            "a horizontal eye-level shot continues through bodies in distance order, without targets behind, off-axis or out of range");
        for (Hit hit : hits) {
            check(hit.kind() == Kind.PENETRATION, "continuation uses penetration damage");
            near(100, hit.damage(), "penetration retains full charged damage");
        }
        check(
            RailgunChainResolver.penetration(world, shooter, first, impact, forward, max, 1)
                .size() == 1,
            "penetration respects the target cap");
        check(
            RailgunChainResolver.penetration(world, shooter, first, impact, forward, max, 0)
                .isEmpty(),
            "a zero cap disables penetration");
        DamageContext partial = new DamageContext(100, 0.5, 0.9, 0, 0, 0, false, false);
        check(
            RailgunChainResolver.penetration(world, shooter, first, impact, forward, partial, 5)
                .isEmpty(),
            "only EHV3 penetrates");
        first.setPosition(40, 0, 0);
        second.setPosition(60, 0, 0);
        world.entities = new ObjectArrayList<>(List.of(first, second));
        hits = RailgunChainResolver.penetration(world, shooter, first, new Vec3d(39.5, 1.62, 0), forward, max, 5);
        check(
            hits.size() == 1 && hits.getFirst()
                .target() == second,
            "the extra 32 blocks start at impact even when the primary is more than 32 blocks from the shooter");
        second.setPosition(60, 20, 0);
        hits = RailgunChainResolver
            .penetration(world, shooter, first, new Vec3d(40, 1.62, 0), new Vec3d(1, 1, 0), max, 5);
        check(
            hits.size() == 1 && hits.getFirst()
                .target() == second,
            "an upward shot keeps its original direction");
        second.setPosition(60, -20, 0);
        hits = RailgunChainResolver
            .penetration(world, shooter, first, new Vec3d(40, 1.62, 0), new Vec3d(1, -1, 0), max, 5);
        check(
            hits.size() == 1 && hits.getFirst()
                .target() == second,
            "a downward shot keeps its original direction");
        String fire = compact(read(SOURCES.resolve("common/railgun/RailgunFireService.java")));
        check(
            fire.contains("penetration(world,player,primary,impact,direction,context,PENETRATION_MAX_TARGETS)"),
            "charged fire passes the ray's actual impact and direction into penetration");
    }

    private static void portalExit() throws Exception {
        TestWorld world = world();
        Entity entity = entity(TestPlayer.class, 0, 64, 0);
        PortalGunPlacement east = new PortalGunPlacement(0, 0.5, 65, 0.5, ForgeDirection.EAST, ForgeDirection.UP);
        Vec3d spot = PortalGunGeometry.exitSpot(east, entity.width, entity.height, 0);
        world.obstacles.add(AxisAlignedBB.getBoundingBox(1, 64, 0, 2, 66, 1));
        check(
            !EntityPortalGunPortal.canExit(world, entity, spot),
            "a destination beside a wall cannot push a player into the wall");
        world.obstacles.clear();
        world.obstacles.add(AxisAlignedBB.getBoundingBox(0, 63, 0, 2, 64, 1));
        check(
            EntityPortalGunPortal.canExit(world, entity, spot),
            "a clear exit on top of a solid floor remains usable");
        // setPlayerLocation uses setPositionAndRotation, which clears the old step smoothing offset.
        entity.ySize = 0.6F;
        world.obstacles.add(AxisAlignedBB.getBoundingBox(0, 64, 0, 2, 64.5, 1));
        check(
            !EntityPortalGunPortal.canExit(world, entity, spot),
            "a player's old step offset cannot hide a slab at the landing feet");
        entity.setPositionAndRotation(spot.x(), spot.y(), spot.z(), 0, 0);
        check(
            !world.getCollidingBoundingBoxes(entity, entity.boundingBox)
                .isEmpty(),
            "the real vanilla player landing collides with that slab");
        world.obstacles.removeLast();
        entity.ySize = 0.6F;
        world.obstacles.add(AxisAlignedBB.getBoundingBox(0, 66, 0, 2, 67, 1));
        check(
            EntityPortalGunPortal.canExit(world, entity, spot),
            "old step smoothing does not falsely reject clear headroom");
        entity.setPositionAndRotation(spot.x(), spot.y(), spot.z(), 0, 0);
        check(
            world.getCollidingBoundingBoxes(entity, entity.boundingBox)
                .isEmpty(),
            "the real vanilla player landing fits the headroom");
        world.obstacles.removeLast();
        world.obstacles.add(AxisAlignedBB.getBoundingBox(0, 65.5, 0, 2, 66, 1));
        check(!EntityPortalGunPortal.canExit(world, entity, spot), "headroom is checked as well as the feet");
        world.obstacles.clear();
        entity.height = 0.5F;
        check(EntityPortalGunPortal.canExit(world, entity, spot), "short entities use their own height");
        for (double x : new double[] { 15.9, -0.1 }) {
            world.loaded.clear();
            world.obstacles.add(AxisAlignedBB.getBoundingBox(Math.ceil(x), 64, 0, Math.ceil(x) + 1, 66, 1));
            check(
                !EntityPortalGunPortal.canExit(world, entity, new Vec3d(x, 64, 0.5)),
                "an obstacle across a chunk edge blocks the exit at " + x);
            check(
                world.loaded.contains(x > 0 ? "1,0" : "-1,0") && world.loaded.contains("0,0"),
                "both columns of a chunk-edge exit are loaded before collision checks");
            world.obstacles.clear();
        }
        String portal = compact(read(SOURCES.resolve("common/entity/EntityPortalGunPortal.java")));
        int guard = portal.indexOf("if(!canExit(target,entity,spot))return;");
        check(
            guard >= 0 && guard < portal.indexOf("data.setInteger(COOLDOWN,"),
            "blocked exits return before cooldown, velocity, position or dimension changes");
        check(
            portal.indexOf("entity.ySize=0;", guard) > guard,
            "step smoothing resets only after a safe landing is found");
    }

    private static void shimmerPermissions() throws Exception {
        TestWorld world = world();
        TestPlayer player = entity(TestPlayer.class, 0, 0, 0);
        player.worldObj = world;
        player.capabilities = new PlayerCapabilities();
        player.edit = true;
        ItemStack stack = new ItemStack(new Item());
        MovingObjectPosition hit = new MovingObjectPosition(7, 64, -9, 1, new Vec3d(7.5, 65, -8.5).toMc());
        int[] mode = { 0 }, events = { 0 }, breaks = { 0 };
        IEventListener listener = event -> {
            if (event instanceof BlockEvent.BreakEvent breaking) {
                breaks[0]++;
                check(
                    breaking.x == 7 && breaking.y == 64
                        && breaking.z == -9
                        && breaking.world == world
                        && breaking.getPlayer() == player,
                    "source removal checks editing permission at the fluid block");
                if (mode[0] == 4) cancel(breaking);
                return;
            }
            if (!(event instanceof PlayerInteractEvent interact)) return;
            events[0]++;
            check(
                interact.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && interact.x == 7
                    && interact.y == 64
                    && interact.z == -9
                    && interact.face == 1
                    && interact.world == world
                    && interact.entityPlayer == player,
                "protection receives the actual shimmer source, face, world and player");
            if (mode[0] == 1) interact.useBlock = Result.DENY;
            if (mode[0] == 2) interact.useItem = Result.DENY;
            if (mode[0] == 3) cancel(interact);
        };
        int bus = (int) field(MinecraftForge.EVENT_BUS, "busID");
        new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, 7, 64, -9, 1, world)
            .getListenerList()
            .register(bus, EventPriority.NORMAL, listener);
        try {
            for (boolean creative : new boolean[] { false, true }) {
                player.capabilities.isCreativeMode = creative;
                world.mine = false;
                check(
                    !canCollect(stack, world, player, hit),
                    "spawn/world protection denies shimmer in creative=" + creative);
                world.mine = true;
                player.edit = false;
                check(!canCollect(stack, world, player, hit), "edit permission denies shimmer in creative=" + creative);
                player.edit = true;
                for (mode[0] = 0; mode[0] <= 4; mode[0]++) {
                    check(
                        canCollect(stack, world, player, hit) == (mode[0] == 0),
                        "shimmer honors interaction and block-removal denials, mode=" + mode[0]);
                }
            }
            check(events[0] == 10 && breaks[0] == 4, "each permission stage only runs after earlier checks pass");
        } finally {
            ListenerList.unregisterAll(bus, listener);
        }
        String source = compact(read(SOURCES.resolve("common/item/items/PortalGun.java")));
        int guard = source.indexOf("if(!canCollectShimmer(stack,world,player,hit))returntrue;");
        check(
            guard >= 0 && guard < source.indexOf("fluidBlock.drain("),
            "denied collection consumes the interaction without draining shimmer or falling through to firing");
    }

    private static void cancel(Event event) {
        // This standalone suite runs without Forge's transformer that implements Event.isCancelable().
        try {
            setField(Event.class, event, "isCanceled", true);
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError(failure);
        }
    }

    private static boolean canCollect(ItemStack stack, World world, EntityPlayer player, MovingObjectPosition hit)
        throws ReflectiveOperationException {
        return (boolean) call(
            PortalGun.class,
            "canCollectShimmer",
            null,
            new Class<?>[] { ItemStack.class, World.class, EntityPlayer.class, MovingObjectPosition.class },
            stack,
            world,
            player,
            hit);
    }

    public static class TestPlayer extends EntityPlayerMP {

        boolean edit;

        public TestPlayer() {
            super(null, null, null, null);
        }

        @Override
        public boolean canPlayerEdit(int x, int y, int z, int side, ItemStack stack) {
            return edit;
        }
    }

    public static class TestWorld extends WorldServer {

        List<Entity> entities;
        List<AxisAlignedBB> obstacles;
        Set<String> loaded;
        boolean mine;

        public TestWorld() {
            super(null, null, null, 0, null, null);
        }

        @Override
        public <T> List<T> selectEntitiesWithinAABB(Class<T> type, AxisAlignedBB box, IEntitySelector selector) {
            List<T> result = new ObjectArrayList<>();
            for (Entity entity : entities) if (type.isInstance(entity) && box.intersectsWith(entity.boundingBox)
                && (selector == null || selector.isEntityApplicable(entity))) result.add(type.cast(entity));
            return result;
        }

        @Override
        public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entity, AxisAlignedBB box) {
            return obstacles.stream()
                .filter(box::intersectsWith)
                .toList();
        }

        @Override
        public boolean canMineBlock(EntityPlayer player, int x, int y, int z) {
            check(x == 7 && y == 64 && z == -9, "mine permission uses the fluid coordinates");
            return mine;
        }

        @Override
        public Block getBlock(int x, int y, int z) {
            return null;
        }

        @Override
        public int getBlockMetadata(int x, int y, int z) {
            return 0;
        }
    }

    public static class TestChunks extends ChunkProviderServer {

        Set<String> loaded;

        public TestChunks() {
            super(null, null, null);
        }

        @Override
        public Chunk loadChunk(int x, int z) {
            loaded.add(x + "," + z);
            return null;
        }
    }
}
