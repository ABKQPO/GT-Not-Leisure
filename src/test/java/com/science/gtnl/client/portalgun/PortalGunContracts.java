package com.science.gtnl.client.portalgun;

import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.compact;
import static com.gtnllib.GTNLLibContractTest.field;
import static com.gtnllib.GTNLLibContractTest.near;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.RESOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.roundTrip;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.common.item.items.PortalGun;
import com.science.gtnl.common.packet.PortalGunFavoritePacket;
import com.science.gtnl.common.packet.PortalGunFavoritePacket.Change;
import com.science.gtnl.common.packet.PortalGunSelectPacket;
import com.science.gtnl.common.portalgun.PortalGunData;
import com.science.gtnl.common.portalgun.PortalGunDestination;
import com.science.gtnl.common.portalgun.PortalGunGeometry;
import com.science.gtnl.common.portalgun.PortalGunPairs;
import com.science.gtnl.common.portalgun.PortalGunPairs.Opened;
import com.science.gtnl.common.portalgun.PortalGunPlacement;
import com.science.gtnl.common.render.item.ItemPortalGunRender;
import com.science.gtnl.common.render.item.ItemPortalGunRender.Core;
import com.science.gtnl.common.render.item.ItemPortalGunRender.Edition;
import com.science.gtnl.common.render.item.PortalGunCoreArcs;
import com.science.gtnl.common.render.item.PortalGunCoreArcs.Arc;
import com.science.gtnl.config.ClientConfig;
import com.science.gtnl.config.ItemConfig;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.network.ByteBufUtils;
import ic2.api.item.IElectricItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class PortalGunContracts {

    private static final ForgeDirection[] SIDES = { ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH,
        ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };

    private PortalGunContracts() {}

    public static void run() throws Exception {
        portalGunCost();
        portalGunBlacklist();
        portalGunFavorites();
        portalRegistry();
        portalChunkLoading();
        portalGunPackets();
        portalTransform();
        portalView();
        portalGeometry();
        portalCooldown();
        portalCarriedVelocity();
        PortalGunLookContracts.run();
        portalGunModel();
        portalGunC137Model();
        portalGunLang();
    }

    private static void portalGunC137Core(Core core, List<String> lines) {
        check(core != null && Edition.TECH.core() == null, "only the C-137 has a spinning cube");
        if (core == null) return;
        List<double[]> cube = groupVertices(lines, "core"), glass = groupVertices(lines, "glass");
        check(!cube.isEmpty() && !glass.isEmpty(), "the C-137 cube and glass are separate parts");
        if (cube.isEmpty() || glass.isEmpty()) return;
        double[] center = { core.x(), core.y(), core.z() };
        double reach = 0, room = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            double low = Double.MAX_VALUE, high = -Double.MAX_VALUE, glassLow = Double.MAX_VALUE,
                glassHigh = -Double.MAX_VALUE;
            for (double[] v : cube) {
                low = Math.min(low, v[i]);
                high = Math.max(high, v[i]);
            }
            for (double[] v : glass) {
                glassLow = Math.min(glassLow, v[i]);
                glassHigh = Math.max(glassHigh, v[i]);
            }
            check(Math.abs(center[i] - (low + high) / 2) < 0.002, "the cube spins about its own center, axis " + i);
            room = Math.min(room, Math.min(center[i] - glassLow, glassHigh - center[i]));
        }
        for (double[] v : cube) reach = Math.max(
            reach,
            Math.sqrt(
                (v[0] - center[0]) * (v[0] - center[0]) + (v[1] - center[1]) * (v[1] - center[1])
                    + (v[2] - center[2]) * (v[2] - center[2])));
        double shake = PortalGunCoreArcs.JOLT * Math.sqrt(3);
        check(reach + shake < room, "the shaking, spinning cube never pokes through the glass");
        for (long millis = 0; millis < PortalGunCoreArcs.LOOP_MILLIS; millis += 37) {
            double[] offset = PortalGunCoreArcs.shake(millis);
            if (Math.sqrt(offset[0] * offset[0] + offset[1] * offset[1] + offset[2] * offset[2]) > shake + 1.0E-9) {
                check(false, "the shake stays within the jolt size at " + millis + " ms");
                break;
            }
        }
        double[] start = PortalGunCoreArcs.angles(0), end = PortalGunCoreArcs.angles(PortalGunCoreArcs.LOOP_MILLIS);
        for (int i = 0; i < 3; i++) near(start[i], end[i], "the spin loops without a jump, axis " + i);
        for (double speed : new double[] { PortalGunCoreArcs.SPIN_X, PortalGunCoreArcs.SPIN_Y,
            PortalGunCoreArcs.SPIN_Z })
            near(
                0,
                speed * PortalGunCoreArcs.LOOP_MILLIS / 1000 % 360,
                "each axis turns whole circles per loop, so the spin never jumps");
        double[] room3 = { core.roomX(), core.roomY(), core.roomZ() };
        boolean inside = true;
        Set<String> shapes = new ObjectOpenHashSet<>();
        for (long millis = 0; millis < 4000; millis += PortalGunCoreArcs.STEP_MILLIS) {
            List<Arc> arcs = PortalGunCoreArcs
                .arcs(millis, core.half(), room3, PortalGunCoreArcs.angles(millis), PortalGunCoreArcs.shake(millis));
            shapes.add(String.valueOf(arcs.size()));
            for (Arc arc : arcs) for (double[] point : arc.points())
                for (int i = 0; i < 3; i++) inside &= Math.abs(point[i]) <= room3[i] + 0.006;
        }
        check(inside, "no lightning leaves the glass");
        check(shapes.size() > 2, "the lightning count changes over time");
        near(
            ItemPortalGunRender.corePulse(0),
            ItemPortalGunRender.corePulse(ItemPortalGunRender.CORE_PULSE_MILLIS),
            "the glow pulse loops");
        check(
            PortalGunCoreArcs.LOOP_MILLIS % ItemPortalGunRender.CORE_PULSE_MILLIS == 0,
            "the glow pulse fits the spin loop");
        check(!groupVertices(lines, "ring").isEmpty(), "the C-137 draws its outline rings on the body");
    }

    private static List<double[]> groupVertices(List<String> lines, String group) {
        List<double[]> vertices = new ObjectArrayList<>();
        boolean inside = false;
        for (String line : lines) {
            if (line.startsWith("o ")) inside = line.equals("o " + group);
            else if (inside && line.startsWith("v ")) {
                String[] parts = line.split(" ");
                vertices.add(
                    new double[] { Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]) });
            }
        }
        return vertices;
    }

    private static void centred(Edition edition, List<String> lines) {
        double[] low = { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE },
            high = { -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE };
        for (String line : lines) if (line.startsWith("v ")) {
            String[] parts = line.split(" ");
            for (int i = 0; i < 3; i++) {
                low[i] = Math.min(low[i], Double.parseDouble(parts[i + 1]));
                high[i] = Math.max(high[i], Double.parseDouble(parts[i + 1]));
            }
        }
        float[] center = { edition.centerX(), edition.centerY(), edition.centerZ() };
        for (int i = 0; i < 3; i++) check(
            Math.abs(center[i] - (low[i] + high[i]) / 2) < 0.002,
            "the dropped and inventory " + edition.folder() + " is centred on its model, axis " + i);
    }

    private static void portalGunC137Model() throws IOException {
        Edition edition = Edition.C137;
        Path root = RESOURCES.resolve("models/" + edition.folder());
        Path obj = root.resolve(edition.folder() + ".obj");
        check(Files.isRegularFile(obj), "the C-137 ships its own model " + obj);
        if (!Files.isRegularFile(obj)) return;
        List<String> lines = Files.readAllLines(obj, StandardCharsets.UTF_8);
        for (String group : new String[] { "solid", "glow", "screen", "glass", "halo" })
            check(lines.contains("o " + group), "the C-137 model has its " + group + " part");
        check(!lines.contains("o fluid"), "the C-137 vial holds a glowing cube instead of a fluid level");
        centred(edition, lines);
        portalGunC137Core(edition.core(), lines);
        for (String texture : new String[] { "base.png", "glow.png", "fx.png", "screen.png" })
            check(Files.exists(root.resolve(texture)), "the C-137 model ships " + texture);
        BufferedImage screen = ImageIO.read(
            root.resolve("screen.png")
                .toFile());
        check(
            screen.getHeight() % ItemPortalGunRender.SCREEN_FRAMES == 0
                && screen.getHeight() / ItemPortalGunRender.SCREEN_FRAMES < screen.getWidth(),
            "the C-137 readout holds " + ItemPortalGunRender.SCREEN_FRAMES + " wide frames stacked vertically");
        check(
            Files.exists(RESOURCES.resolve("textures/items/" + edition.folder() + ".png")),
            "the C-137 has its own item icon");
    }

    private static void portalGunModel() throws IOException {
        Path root = RESOURCES.resolve("models/portal_gun");
        List<String> lines = Files.readAllLines(root.resolve("portal_gun.obj"), StandardCharsets.UTF_8);
        for (String group : new String[] { "solid", "glow", "fluid", "screen", "glass", "ring", "halo" })
            check(lines.contains("o " + group), "the portal gun model has its " + group + " part");
        double fluidBottom = Double.MAX_VALUE;
        boolean inFluid = false;
        for (String line : lines) {
            if (line.startsWith("o ")) inFluid = line.equals("o fluid");
            else if (inFluid && line.startsWith("v "))
                fluidBottom = Math.min(fluidBottom, Double.parseDouble(line.split(" ")[2]));
        }
        near(
            ItemPortalGunRender.FLUID_BOTTOM,
            fluidBottom,
            "the renderer fills the vial from the bottom of the exported fluid");
        for (String texture : new String[] { "base.png", "glow.png", "fx.png", "screen.png" })
            check(Files.exists(root.resolve(texture)), "the portal gun model ships " + texture);
        centred(Edition.TECH, lines);
        BufferedImage screen = ImageIO.read(
            root.resolve("screen.png")
                .toFile());
        check(
            screen.getHeight() % ItemPortalGunRender.SCREEN_FRAMES == 0
                && screen.getHeight() / ItemPortalGunRender.SCREEN_FRAMES < screen.getWidth(),
            "the screen strip holds " + ItemPortalGunRender.SCREEN_FRAMES + " wide frames stacked vertically");
        near(0, ItemPortalGunRender.screenFrame(0), "the screen starts on its first frame");
        near(1, ItemPortalGunRender.screenFrame(100), "the screen moves one frame every 2 ticks");
        near(0, ItemPortalGunRender.screenFrame(1600), "the screen wave loops after 8 frames");
        near(0, ItemPortalGunRender.fluidLevel(new ItemStack(new Item())), "other items show no shimmer");
    }

    private static void portalGunCost() {
        var config = MainConfig.item.portal_gun;
        check(
            !IElectricItem.class.isAssignableFrom(PortalGun.class),
            "the portal gun runs on shimmer alone, it is no EU item to charge");
        check(
            Arrays.asList(PortalGun.class.getInterfaces())
                .equals(List.of(IEnergyContainerItem.class)),
            "the portal gun reports its shimmer as RF only so DuraDisplay shows a fill percentage");
        for (Field field : ItemConfig.PortalGun.class.getFields()) check(
            !field.getName()
                .matches("capacity|tier|euPerShot"),
            "the portal gun config has no energy setting left, found " + field.getName());
        int savedBlocks = config.blocksPerShimmer, savedMax = config.maxShimmerPerShot;
        config.blocksPerShimmer = 4;
        config.maxShimmerPerShot = 100;
        near(0, PortalGun.shimmerCost(true, 0), "a shot to where you stand costs no shimmer");
        near(0, PortalGun.shimmerCost(true, -5), "a negative distance costs no shimmer");
        near(1, PortalGun.shimmerCost(true, 0.5), "any distance costs at least 1 mB");
        near(3, PortalGun.shimmerCost(true, 10), "same dimension costs 1 mB per 4 blocks, rounded up");
        near(100, PortalGun.shimmerCost(true, 400), "400 blocks reaches the per-shot cap");
        near(100, PortalGun.shimmerCost(true, 100000), "far shots stay at the per-shot cap");
        near(100, PortalGun.shimmerCost(false, 1), "another dimension always costs the cap");
        config.maxShimmerPerShot = 0;
        near(0, PortalGun.shimmerCost(false, 1), "a cap of 0 makes every shot free");
        config.blocksPerShimmer = savedBlocks;
        config.maxShimmerPerShot = savedMax;
    }

    private static void portalGunBlacklist() {
        var config = MainConfig.item.portal_gun;
        int[] saved = config.dimensionBlacklist;
        config.dimensionBlacklist = new int[] { -1, 7 };
        check(PortalGun.blocked(-1) && PortalGun.blocked(7), "listed dimensions are blocked");
        check(!PortalGun.blocked(0) && !PortalGun.blocked(1), "unlisted dimensions are open");
        config.dimensionBlacklist = new int[0];
        check(!PortalGun.blocked(-1), "an empty blacklist blocks nothing");
        DimensionManager.init();
        try {
            check(
                DimensionManager.getWorld(-1) == null && PortalGun.reachable(-1),
                "a shot can head for a registered dimension that is not loaded right now");
            check(!PortalGun.reachable(Integer.MAX_VALUE), "a shot cannot head for a dimension that is not registered");
            config.dimensionBlacklist = new int[] { -1 };
            check(!PortalGun.reachable(-1), "a shot cannot head for a blacklisted dimension");
        } catch (RuntimeException e) {
            check(false, "checking where a shot can head for does not throw: " + e);
        }
        config.dimensionBlacklist = saved;
    }

    private static void portalGunFavorites() {
        ItemStack stack = new ItemStack(new Item());
        check(PortalGunData.favorite(stack, 0) == null, "a new gun has no destination");
        check(PortalGunData.previous(stack) == null, "a new gun has no previous position");
        near(0, PortalGunData.selectedSlot(stack), "a new gun selects slot 0");
        check(!stack.hasTagCompound(), "reading destinations does not create item NBT");

        check(!PortalGunData.stayOpen(stack), "a new gun does not keep its wheel open");
        check(!stack.hasTagCompound(), "reading destinations does not create item NBT");

        PortalGunDestination home = new PortalGunDestination(
            "Home",
            -1,
            "Nether",
            12.5,
            64,
            -3.25,
            ForgeDirection.WEST);
        PortalGunDestination mine = new PortalGunDestination(
            "",
            7,
            "Twilight Forest",
            -100.5,
            11,
            40.75,
            ForgeDirection.UP);
        PortalGunData.setFavorite(stack, 0, home);
        PortalGunData.setFavorite(stack, 3, mine);
        check(home.equals(PortalGunData.favorite(stack, 0)), "slot 0 reads back the saved destination with its name");
        check(mine.equals(PortalGunData.favorite(stack, 3)), "slot 3 is stored next to slot 0, facing up");
        check(PortalGunData.favorite(stack, 1) == null, "untouched slots stay empty");
        PortalGunDestination moved = new PortalGunDestination("Base", 0, "Overworld", 1, 2, 3, ForgeDirection.NORTH);
        PortalGunData.setFavorite(stack, 0, moved);
        check(moved.equals(PortalGunData.favorite(stack, 0)), "saving a slot again replaces it");
        near(
            2,
            PortalGunData.root(stack)
                .getTagList("Favorites", 10)
                .tagCount(),
            "replacing a slot does not add a second entry");
        check(
            stack.getTagCompound()
                .func_150296_c()
                .equals(Set.of("PortalGun")),
            "the portal gun keeps its data in its own tag, like the armor and the railgun");
        PortalGunData.editFavorite(stack, 0, "  Main Base  ", moved.x(), moved.y(), moved.z(), null);
        check(
            moved.withName("Main Base")
                .equals(PortalGunData.favorite(stack, 0)),
            "renaming a favorite keeps its position and trims the name");
        PortalGunData.editFavorite(stack, 0, "A very long favorite name", moved.x(), moved.y(), moved.z(), null);
        near(
            PortalGunData.MAX_NAME_LENGTH,
            PortalGunData.favorite(stack, 0)
                .name()
                .length(),
            "favorite names are cut to 15 characters, as in JDT");
        PortalGunData.removeFavorite(stack, 3);
        check(PortalGunData.favorite(stack, 3) == null, "removing a favorite empties its slot");
        check(PortalGunData.favorite(stack, 0) != null, "removing a favorite keeps the others");
        PortalGunData.removeFavorite(stack, 99);
        check(!PortalGunData.editFavorite(stack, -1, "x", 0, 64, 0, null), "out of range slots are ignored");
        check(PortalGunData.favorite(stack, 0) != null, "out of range slots are ignored");
        check(
            !PortalGunData.editFavorite(stack, 1, "x", 0, 64, 0, null) && PortalGunData.favorite(stack, 1) == null,
            "an empty favorite cannot be edited, it is saved by clicking it");
        PortalGunData.editFavorite(stack, 0, "Main Base", 100.5, 300, -40_000_000, null);
        PortalGunDestination edited = PortalGunData.favorite(stack, 0);
        check(
            edited.x() == 100.5 && edited.y() == PortalGunData.MAX_HEIGHT
                && edited.z() == -PortalGunData.MAX_HORIZONTAL
                && edited.dimension() == moved.dimension()
                && edited.facing() == moved.facing(),
            "editing moves a favorite, kept inside the world and in its own dimension");
        PortalGunData.editFavorite(stack, 0, "Main Base", Double.NaN, 64, 2, null);
        check(
            PortalGunData.favorite(stack, 0)
                .x() == 100.5,
            "a broken coordinate keeps the old one");
        ItemId bed = ItemId.createNoCopy(new Item(), 0, null);
        PortalGunDestination marked = home.edited("Home", home.x(), home.y(), home.z(), bed);
        check(
            bed.equals(
                marked.withName("Bed")
                    .icon())
                && home.icon() == null
                && !home.write()
                    .hasKey("Icon"),
            "renaming keeps the icon, and a favorite without one stores none");
        NBTTagCompound gone = home.write(), missing = new NBTTagCompound();
        missing.setInteger("id", -1);
        gone.setTag("Icon", missing);
        check(
            PortalGunDestination.read(gone)
                .icon() == null,
            "an icon whose item no longer exists is dropped");
        PortalGunData.setPrevious(stack, mine);
        check(mine.equals(PortalGunData.previous(stack)), "the previous position reads back");
        PortalGunDestination saved = PortalGunData.favorite(stack, 0);
        check(
            PortalGunData.fillIfEmpty(stack, 3, mine) && mine.equals(PortalGunData.favorite(stack, 3)),
            "clicking an empty favorite on the wheel saves where you stand into it");
        check(
            !PortalGunData.fillIfEmpty(stack, 0, mine) && saved.equals(PortalGunData.favorite(stack, 0)),
            "clicking a saved favorite only selects it and keeps what it holds");
        check(!PortalGunData.fillIfEmpty(stack, 12, mine), "an out of range slot is never filled");

        PortalGunData.select(stack, 5, true);
        near(5, PortalGunData.selectedSlot(stack), "selecting a slot stores it");
        check(PortalGunData.stayOpen(stack), "the stay open choice is stored with the selection");
        PortalGunData.select(stack, 12, false);
        near(5, PortalGunData.selectedSlot(stack), "an out of range selection is ignored");
        PortalGunData.root(stack)
            .setByte("Selected", (byte) 40);
        near(0, PortalGunData.selectedSlot(stack), "a broken stored selection falls back to slot 0");

        NBTTagCompound tag = home.write();
        tag.setByte("Facing", (byte) ForgeDirection.DOWN.ordinal());
        check(
            PortalGunDestination.read(tag)
                .facing() == ForgeDirection.NORTH,
            "a stored downward facing falls back to north");
        tag.setByte("Facing", (byte) 99);
        check(
            PortalGunDestination.read(tag)
                .facing() == ForgeDirection.NORTH,
            "an unknown stored facing falls back to north");
        near(
            5,
            new PortalGunDestination("", 0, "", 3, 4, 0, ForgeDirection.NORTH).distanceTo(0, 0, 0),
            "destination distance");
        check("abc".equals(PortalGunData.cleanName("  abc ")), "names are trimmed");
        check("".equals(PortalGunData.cleanName(null)), "a missing name becomes empty");

        ItemStack gun = new ItemStack(new Item()), other = new ItemStack(new Item());
        check(
            PortalGunData.gunId(gun)
                .equals(PortalGunData.gunId(gun)),
            "a gun keeps the id it got on its first shot");
        check(
            !PortalGunData.gunId(gun)
                .equals(PortalGunData.gunId(other)),
            "two guns get different ids");

        check(
            PortalGunDestination.facingOf(0, -60) == ForgeDirection.UP,
            "looking up saves an upward favorite, as in JDT");
        check(PortalGunDestination.facingOf(0, 60) == ForgeDirection.SOUTH, "looking down saves the horizontal facing");
        check(PortalGunDestination.facingOf(0, 0) == ForgeDirection.SOUTH, "yaw 0 faces south");
        check(PortalGunDestination.facingOf(90, 0) == ForgeDirection.WEST, "yaw 90 faces west");
        check(PortalGunDestination.facingOf(180, 0) == ForgeDirection.NORTH, "yaw 180 faces north");
        check(PortalGunDestination.facingOf(-90, 0) == ForgeDirection.EAST, "yaw -90 faces east");
    }

    private static void portalRegistry() {
        PortalGunPairs registry = new PortalGunPairs();
        UUID gun = UUID.randomUUID(), other = UUID.randomUUID();
        registry.remember(gun, new Opened(0, 10, 1));
        registry.remember(gun, new Opened(-1, 11, 1));
        registry.remember(other, new Opened(0, 20, 2));
        near(
            2,
            registry.pair(gun, 1)
                .size(),
            "both portals of a pair are found");
        near(
            0,
            registry.pair(gun, 2)
                .size(),
            "another pair's number finds nothing for this gun");
        registry.forget(gun, 10);
        near(
            1,
            registry.pair(gun, 1)
                .size(),
            "a removed portal is forgotten");
        registry.remember(gun, new Opened(0, 12, 3));
        near(
            2,
            registry.take(gun)
                .size(),
            "a new shot takes every portal this gun still has open");
        near(
            0,
            registry.take(gun)
                .size(),
            "taking clears the gun's portals");
        near(
            1,
            registry.pair(other, 2)
                .size(),
            "another gun's portals are left alone");
        int first = registry.request(gun), second = registry.request(gun), elsewhere = registry.request(other);
        check(!registry.claim(gun, first), "a shot whose chunk loads after a newer shot of the same gun opens nothing");
        check(registry.claim(gun, second), "the newest shot of a gun opens once its destination chunk is loaded");
        check(!registry.claim(gun, second), "a shot opens its portals only once");
        check(registry.claim(other, elsewhere), "another gun's waiting shot is left alone");
        int stopped = registry.request(gun);
        registry.clear();
        check(!registry.claim(gun, stopped), "stopping the server forgets the shots still waiting for a chunk");
    }

    private static void portalChunkLoading() throws IOException {
        String pairs = compact(
            Files.readString(SOURCES.resolve("common/portalgun/PortalGunPairs.java"), StandardCharsets.UTF_8));
        check(
            pairs.contains("target.theChunkProviderServer.loadChunk(")
                && pairs.contains("()->{if(!claim(gunId,request))return;"),
            "opening a pair loads the destination chunk with Forge's callback, so a chunk on disk is read off the server thread");
        String render = compact(
            Files.readString(
                SOURCES.resolve("common/render/entity/PortalGunPortalRender.java"),
                StandardCharsets.UTF_8));
        int thrown = render.indexOf("PortalGunPortalFx.throwDots("), culled = render.indexOf("if(!FxCamera.inView(");
        check(culled >= 0, "a portal out of view is not drawn");
        check(
            thrown >= 0 && thrown < culled,
            "a portal out of view still throws its opening dots, so they are there when you turn around");
    }

    private static void portalGunPackets() throws ReflectiveOperationException {
        PortalGunSelectPacket select = roundTrip(
            new PortalGunSelectPacket(7, true, true),
            PortalGunSelectPacket.class.getConstructor());
        near(7, (int) field(select, "slot"), "select packet slot round trip");
        check((boolean) field(select, "stayOpen"), "select packet stay open round trip");
        check((boolean) field(select, "clicked"), "select packet click round trip");
        check(
            !(boolean) field(
                roundTrip(new PortalGunSelectPacket(7, true, false), PortalGunSelectPacket.class.getConstructor()),
                "clicked"),
            "toggling stay open is not a click, so it never saves or fires");
        PortalGunFavoritePacket favorite = roundTrip(
            new PortalGunFavoritePacket(4, Change.EDIT, "A very long favorite name", 1.5, 64, -2.5, null),
            PortalGunFavoritePacket.class.getConstructor());
        near(4, (int) field(favorite, "slot"), "favorite packet slot round trip");
        check(field(favorite, "change") == Change.EDIT, "favorite packet change round trip");
        check(
            (double) field(favorite, "x") == 1.5 && (double) field(favorite, "y") == 64
                && (double) field(favorite, "z") == -2.5
                && field(favorite, "icon") == null,
            "favorite packet position and icon round trip");
        check(
            "A very long fav".equals(field(favorite, "name")),
            "favorite packet names are cut to 15 characters on both ends");
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(1);
        buffer.writeByte(9);
        ByteBufUtils.writeUTF8String(buffer, "x");
        buffer.writeDouble(0);
        buffer.writeDouble(0);
        buffer.writeDouble(0);
        ByteBufUtils.writeItemStack(buffer, null);
        PortalGunFavoritePacket broken = new PortalGunFavoritePacket();
        broken.fromBytes(buffer);
        check(field(broken, "change") == null, "an unknown favorite change is ignored instead of crashing");
    }

    private static void portalTransform() {
        for (ForgeDirection in : SIDES) {
            for (ForgeDirection out : SIDES) {
                Vec3d entering = Vec3d.of(in)
                    .scale(-1);
                Vec3d leaving = PortalGunGeometry.transform(entering, in, out.getOpposite());
                check(
                    same(leaving, Vec3d.of(out)),
                    "walking into a " + in + " portal leaves a " + out + " portal along its facing, got " + leaving);
                Vec3d x = PortalGunGeometry.transform(new Vec3d(1, 0, 0), in, out.getOpposite()),
                    y = PortalGunGeometry.transform(new Vec3d(0, 1, 0), in, out.getOpposite()),
                    z = PortalGunGeometry.transform(new Vec3d(0, 0, 1), in, out.getOpposite());
                check(same(x.cross(y), z), "the " + in + " to " + out + " turn is a rotation, not a mirror");
                Vec3d any = new Vec3d(0.3, -0.7, 0.2);
                near(
                    any.length(),
                    PortalGunGeometry.transform(any, in, out.getOpposite())
                        .length(),
                    "the " + in + " to " + out + " turn keeps speed");
                if (in.offsetY == 0 && out.offsetY == 0) check(
                    same(PortalGunGeometry.transform(new Vec3d(0, 1, 0), in, out.getOpposite()), new Vec3d(0, 1, 0)),
                    "between two wall portals " + in + " to " + out + " up stays up");
            }
        }
    }

    private static void portalView() throws IOException {
        String portal = compact(
            Files.readString(SOURCES.resolve("common/entity/EntityPortalGunPortal.java"), StandardCharsets.UTF_8));
        check(
            portal.contains("setPlayerLocation(spot.x(),spot.y(),spot.z(),entity.rotationYaw,entity.rotationPitch)")
                && portal.contains(
                    "setLocationAndAngles(spot.x(),spot.y(),spot.z(),entity.rotationYaw,entity.rotationPitch)")
                && !portal.contains("rotationYaw=")
                && !portal.contains("rotationPitch="),
            "going through a portal keeps where you look; only the place and the speed change");
    }

    private static void portalGeometry() {
        expect(
            PortalGunGeometry.halfExtents(ForgeDirection.NORTH, ForgeDirection.UP),
            0.5,
            1.0,
            0.1,
            "north wall portal");
        expect(
            PortalGunGeometry.halfExtents(ForgeDirection.EAST, ForgeDirection.UP),
            0.1,
            1.0,
            0.5,
            "east wall portal");
        expect(
            PortalGunGeometry.halfExtents(ForgeDirection.UP, ForgeDirection.SOUTH),
            0.5,
            0.1,
            1.0,
            "floor portal along z");
        expect(
            PortalGunGeometry.halfExtents(ForgeDirection.DOWN, ForgeDirection.EAST),
            1.0,
            0.1,
            0.5,
            "ceiling portal along x");

        PortalGunPlacement east = new PortalGunPlacement(0, 10, 65, 20, ForgeDirection.EAST, ForgeDirection.UP);
        check(
            same(PortalGunGeometry.exitSpot(east, 0.6, 1.8, 0), new Vec3d(10.45, 64, 20)),
            "a standing player leaves an east wall portal on its floor, just in front of it");
        check(
            same(PortalGunGeometry.exitSpot(east, 0.6, 1.8, 1), new Vec3d(10.45, 64.2, 20)),
            "a player at the top of the entry portal leaves at the top of the exit");
        check(
            same(PortalGunGeometry.exitSpot(east, 0.6, 1.8, 7), new Vec3d(10.45, 64.2, 20)),
            "the height fraction is clamped");
        PortalGunPlacement floor = new PortalGunPlacement(0, 0, 64.1, 0, ForgeDirection.UP, ForgeDirection.SOUTH);
        check(
            same(PortalGunGeometry.exitSpot(floor, 0.6, 1.8, 0), new Vec3d(0, 64.25, 0)),
            "a floor portal lets you out just above it");
        PortalGunPlacement ceiling = new PortalGunPlacement(0, 0, 70, 0, ForgeDirection.DOWN, ForgeDirection.SOUTH);
        check(
            same(PortalGunGeometry.exitSpot(ceiling, 0.6, 1.8, 0), new Vec3d(0, 68.05, 0)),
            "a ceiling portal drops you out under it");
        PortalGunPlacement standing = PortalGunPlacement
            .standing(new PortalGunDestination("", 3, "", 1, 64, 2, ForgeDirection.WEST));
        check(
            standing.equals(new PortalGunPlacement(3, 1, 65, 2, ForgeDirection.WEST, ForgeDirection.UP)),
            "the destination portal stands upright on the saved feet position, facing the saved way");
        PortalGunPlacement upward = PortalGunPlacement
            .standing(new PortalGunDestination("", 3, "", 1, 64, 2, ForgeDirection.UP));
        check(
            upward.equals(new PortalGunPlacement(3, 1, 64.1, 2, ForgeDirection.UP, ForgeDirection.SOUTH)),
            "a favorite saved looking up opens a floor portal at the feet, long side north to south as in JDT");
    }

    private static void portalCooldown() {
        check(EntityPortalGunPortal.coolingDown(110, 100), "an entity that just arrived is cooling down");
        check(!EntityPortalGunPortal.coolingDown(100, 100), "the cooldown ends on its last tick");
        check(!EntityPortalGunPortal.coolingDown(0, 100), "an entity that never used a portal is free");
        check(
            !EntityPortalGunPortal.coolingDown(100 + EntityPortalGunPortal.ARRIVAL_COOLDOWN + 1, 100),
            "a cooldown longer than possible is stale");
        check(!EntityPortalGunPortal.coolingDown(1_000_000, 5), "a cooldown from before a server restart is ignored");
        near(20, new ItemConfig.PortalGun().pairCooldownTicks, "a pair of portals cools down for a second by default");
        check(EntityPortalGunPortal.coolingDown(130, 100, 40), "a pair stays closed to an entity during its cooldown");
        check(!EntityPortalGunPortal.coolingDown(100, 100, 40), "the pair cooldown ends on its last tick");
        check(!EntityPortalGunPortal.coolingDown(141, 100, 40), "a pair cooldown longer than possible is stale");
        UUID gun = new UUID(1, 2), other = new UUID(3, 4);
        check(
            EntityPortalGunPortal.pairKey(gun, 2)
                .equals(EntityPortalGunPortal.pairKey(gun, 2))
                && !EntityPortalGunPortal.pairKey(gun, 2)
                    .equals(EntityPortalGunPortal.pairKey(gun, 3))
                && !EntityPortalGunPortal.pairKey(gun, 2)
                    .equals(EntityPortalGunPortal.pairKey(other, 2))
                && !EntityPortalGunPortal.pairKey(null, 2)
                    .isEmpty(),
            "each pair of portals keeps its own cooldown");
    }

    private static void portalCarriedVelocity() {
        check(
            same(EntityPortalGunPortal.carried(new Vec3d(0.1, 0, -0.1)), Vec3d.ZERO),
            "slow walking is not carried through");
        check(
            same(EntityPortalGunPortal.carried(new Vec3d(0, -0.5, 0)), new Vec3d(0, -0.5, 0)),
            "falling speed is carried through");
        check(
            same(EntityPortalGunPortal.carried(new Vec3d(0, 0.05, 0)), new Vec3d(0, 0.05, 0)),
            "jumping is carried through");
        check(
            same(EntityPortalGunPortal.carried(new Vec3d(0.3, 0, 0)), new Vec3d(0.3, 0, 0)),
            "sprinting is carried through");
    }

    private static void portalGunLang() throws IOException {
        Map<String, String> english = lang("en_US.lang"), chinese = lang("zh_CN.lang");
        StringBuilder sources = new StringBuilder(
            Files.readString(SOURCES.resolve("common/item/items/PortalGun.java"), StandardCharsets.UTF_8));
        sources
            .append(Files.readString(SOURCES.resolve("common/entity/EntityPortalGunOrb.java"), StandardCharsets.UTF_8));
        try (DirectoryStream<Path> files = Files.newDirectoryStream(SOURCES.resolve("common/portalgun"), "*.java")) {
            for (Path file : files) sources.append(Files.readString(file, StandardCharsets.UTF_8));
        }
        Matcher used = Pattern.compile("\"(item\\.gtnl\\.portal_gun\\.[a-z_.]+)\"")
            .matcher(sources);
        int keys = 0;
        while (used.find()) {
            keys++;
            check(english.containsKey(used.group(1)), "en_US.lang has " + used.group(1));
            check(chinese.containsKey(used.group(1)), "zh_CN.lang has " + used.group(1));
        }
        check(keys >= 14, "the portal gun source uses its lang keys, found " + keys);
        for (String key : new String[] { "wheel.add", "wheel.remove", "wheel.edit", "wheel.stay_open.on",
            "wheel.stay_open.off", "wheel.empty", "wheel.selected", "wheel.selected_empty", "edit.title", "edit.name",
            "edit.position", "edit.dimension", "edit.icon", "edit.icon_clear", "edit.icon_hint", "edit.save",
            "edit.cancel", "unnamed", "place", "wheel.effect" }) {
            check(english.containsKey("item.gtnl.portal_gun." + key), "en_US.lang has the wheel key " + key);
            check(chinese.containsKey("item.gtnl.portal_gun." + key), "zh_CN.lang has the wheel key " + key);
        }
        for (String name : new String[] { "portal_gun", "portal_gun_c137" }) {
            check(english.containsKey("item.gtnl." + name + ".name"), "en_US.lang names the item " + name);
            check(chinese.containsKey("item.gtnl." + name + ".name"), "zh_CN.lang names the item " + name);
        }
        for (PortalGunPortalEffect effect : PortalGunPortalEffect.values()) {
            check(english.containsKey(effect.langKey()), "en_US.lang names the portal effect " + effect);
            check(chinese.containsKey(effect.langKey()), "zh_CN.lang names the portal effect " + effect);
        }
        Config.LangKey effectKey = Arrays.stream(ClientConfig.class.getFields())
            .filter(field -> field.getType() == PortalGunPortalEffect.class)
            .map(field -> field.getAnnotation(Config.LangKey.class))
            .findFirst()
            .orElse(null);
        check(effectKey != null, "the portal effect setting has a lang key");
        if (effectKey != null) for (String key : new String[] { effectKey.value(), effectKey.value() + ".tooltip" }) {
            check(english.containsKey(key), "en_US.lang has " + key);
            check(chinese.containsKey(key), "zh_CN.lang has " + key);
        }
        check(
            !english.containsKey("item.gtnl.portal_gun.saved")
                && !chinese.containsKey("item.gtnl.portal_gun.tooltip.save"),
            "the old sneak-to-save keys are gone");
        for (Map.Entry<String, String> entry : english.entrySet()) {
            String key = entry.getKey();
            if (!key.contains("portal_gun") && !key.contains("PortalGun")) continue;
            check(chinese.containsKey(key), "zh_CN.lang has " + key);
            if (chinese.containsKey(key)) check(
                placeholders(entry.getValue()).equals(placeholders(chinese.get(key))),
                key + " has the same placeholders in both languages");
        }
    }

    private static Map<String, String> lang(String file) throws IOException {
        Map<String, String> map = new Object2ObjectLinkedOpenHashMap<>();
        for (String line : Files.readAllLines(RESOURCES.resolve("lang/" + file), StandardCharsets.UTF_8)) {
            int at = line.indexOf('=');
            if (at > 0) map.put(line.substring(0, at), line.substring(at + 1));
        }
        return map;
    }

    private static String placeholders(String text) {
        StringBuilder found = new StringBuilder();
        Matcher matcher = Pattern.compile("%(\\d+\\$)?s")
            .matcher(text);
        while (matcher.find()) found.append(matcher.group())
            .append(' ');
        return found.toString();
    }

    private static boolean same(Vec3d a, Vec3d b) {
        return a.distanceToSqr(b) < 1.0E-9;
    }

    private static void expect(double[] half, double x, double y, double z, String name) {
        near(x, half[0], name + " half width along x");
        near(y, half[1], name + " half height along y");
        near(z, half[2], name + " half depth along z");
    }
}
