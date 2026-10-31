package com.science.gtnl.common.wireless;

import static com.gtnltest.TestSupport.check;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.science.gtnl.common.item.items.WirelessFrequencyCard;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessLinkData.Claim;
import com.science.gtnl.common.wireless.WirelessLinkData.Slot;

import appeng.api.parts.IPart;
import appeng.tile.networking.TileCableBus;
import cpw.mods.fml.common.registry.RegistryDelegate;

@Tag("wireless")
public class WirelessCopiedLinksTest {

    private static final UUID PLAYER = UUID.randomUUID();
    private static final WirelessFrequency A = WirelessFrequency.of(new UUID(0, 1000)),
        B = WirelessFrequency.of(new UUID(0, 2000));

    @Test
    void restoresOnlyAuthorizedCompatibleLinks() throws ReflectiveOperationException {
        try (Fixture fixture = new Fixture()) {
            currentTargets(fixture);
            partialPaste(fixture);
            boundCards();
        }
    }

    private static void boundCards() throws ReflectiveOperationException {
        Item item = allocate(WirelessFrequencyCard.class);
        field(Item.class, "delegate").set(item, new RegistryDelegate.Delegate<>(item, Item.class));
        ItemStack card = new ItemStack(item);
        card.setTagCompound(new NBTTagCompound());
        new WirelessCardBinding(A, position(1000), null, PLAYER, "owner").write(card.getTagCompound());
        check(
            WirelessCardInventory.hasBoundCard(PLAYER, A, List.of(card)),
            "a personally bound card authorizes restoration even with automatic mode off");
        check(
            !WirelessCardInventory.hasBoundCard(UUID.randomUUID(), A, List.of(card)),
            "carrying somebody else's card does not authorize restoration");
        check(!WirelessCardInventory.hasBoundCard(PLAYER, B, List.of(card)), "the copied frequency must match");
        check(!WirelessCardInventory.hasBoundCard(PLAYER, A, List.of()), "no card means no restoration");
        ItemStack wrongItem = new ItemStack(new Item());
        wrongItem.setTagCompound(card.getTagCompound());
        check(!WirelessCardInventory.hasBoundCard(PLAYER, A, List.of(wrongItem)), "NBT on an ordinary item is no card");
        card.stackSize = 0;
        check(!WirelessCardInventory.hasBoundCard(PLAYER, A, List.of(card)), "empty card stacks do not authorize");
    }

    private static void currentTargets(Fixture fixture) throws ReflectiveOperationException {
        Address first = position(1), second = position(2);
        TileEntity firstTile = fixture.put(new TileEntity(), 1);
        TestHost secondTile = fixture.host(2);
        IPart original = part(new ItemStack(new Item()));
        secondTile.parts.put(ForgeDirection.UNKNOWN, original);
        WirelessCopiedLinks.enqueue(PLAYER, first, firstTile, null, A);
        WirelessCopiedLinks.enqueue(PLAYER, second, secondTile, original, B);
        List<Address> network = List.of(first, second);
        check(WirelessCopiedLinks.clusterAgrees(A, List.of(first)), "one copied frequency agrees");
        check(!WirelessCopiedLinks.clusterAgrees(A, network), "two live copied frequencies conflict");

        secondTile.invalidate();
        check(WirelessCopiedLinks.clusterAgrees(A, network), "invalidated devices no longer vote");
        secondTile.validate();
        check(!WirelessCopiedLinks.clusterAgrees(A, network), "the same device at its original position still votes");

        // FMP moves retain the tile and parts and validate them at their new position.
        secondTile.invalidate();
        fixture.put(secondTile, 3);
        secondTile.validate();
        fixture.put(new TileEntity(), 2);
        check(WirelessCopiedLinks.clusterAgrees(A, network), "a moved device cannot vote at its old position");

        fixture.put(secondTile, 2);
        check(!WirelessCopiedLinks.clusterAgrees(A, network), "a restored live target still detects mixed frequencies");
        fixture.put(new TileEntity(), 2);
        check(WirelessCopiedLinks.clusterAgrees(A, network), "a replaced but still valid tile cannot vote");

        fixture.put(secondTile, 2);
        secondTile.parts.put(ForgeDirection.UNKNOWN, part(new ItemStack(new Item())));
        check(WirelessCopiedLinks.clusterAgrees(A, network), "a replaced part in the same host cannot vote");
        secondTile.parts.put(ForgeDirection.UNKNOWN, original);
        fixture.world.loaded = false;
        check(WirelessCopiedLinks.clusterAgrees(A, network), "unloaded targets are not resolved or counted");
        fixture.world.loaded = true;
        World otherWorld = allocate(TestWorld.class);
        secondTile.setWorldObj(otherWorld);
        check(WirelessCopiedLinks.clusterAgrees(A, network), "a tile belonging to another world cannot vote");
        secondTile.setWorldObj(fixture.world);
        check(!WirelessCopiedLinks.clusterAgrees(A, network), "identity checks retain genuine conflicts");
        WirelessCopiedLinks.clear();
    }

    private static void partialPaste(Fixture fixture) throws ReflectiveOperationException {
        Item item = new Item();
        ItemStack expectedNorth = new ItemStack(item, 1, 7);
        TestHost source = fixture.host(10);
        source.parts.put(ForgeDirection.NORTH, part(expectedNorth));
        source.parts.put(ForgeDirection.SOUTH, part(new ItemStack(item, 1, 4)));
        WirelessLinkData.write(
            source.data,
            Map.of(
                new Slot(ForgeDirection.NORTH, false),
                new Claim("part", Set.of(A), Set.of(), Set.of()),
                new Slot(ForgeDirection.SOUTH, false),
                new Claim("part", Set.of(B), Set.of(), Set.of())));
        var links = WirelessCopiedLinks.capture(source);

        TestHost target = fixture.host(20);
        // Missing replacement materials can leave an old part of the same class but a different item variant.
        target.parts.put(ForgeDirection.NORTH, part(new ItemStack(item, 1, 6)));
        target.parts.put(ForgeDirection.SOUTH, part(new ItemStack(item, 1, 4)));
        WirelessCopiedLinks.pasted(fixture.player, target, links);
        check(WirelessCopiedLinks.waiting() == 1, "only the successfully applied slot enters the restore queue");
        check(
            WirelessCopiedLinks
                .clusterAgrees(B, List.of(atSide(20, ForgeDirection.NORTH), atSide(20, ForgeDirection.SOUTH))),
            "a residual wrong part does not introduce its intended frequency into conflict checks");
        WirelessCopiedLinks.clear();

        target.parts.put(ForgeDirection.NORTH, part(new ItemStack(new Item(), 1, 7)));
        WirelessCopiedLinks.pasted(fixture.player, target, links);
        check(WirelessCopiedLinks.waiting() == 1, "a different item with matching metadata is also rejected");
        WirelessCopiedLinks.clear();

        expectedNorth.setItemDamage(8);
        target.parts.put(ForgeDirection.NORTH, part(new ItemStack(item, 1, 7)));
        WirelessCopiedLinks.pasted(fixture.player, target, links);
        check(WirelessCopiedLinks.waiting() == 2, "captured identities are immutable and both matching slots restore");
        check(
            !WirelessCopiedLinks
                .clusterAgrees(A, List.of(atSide(20, ForgeDirection.NORTH), atSide(20, ForgeDirection.SOUTH))),
            "matching copied parts keep their distinct frequencies");
        WirelessCopiedLinks.clear();

        var rotated = WirelessCopiedLinks
            .rotate(links, side -> side == ForgeDirection.NORTH ? ForgeDirection.EAST : ForgeDirection.WEST);
        target.parts.put(ForgeDirection.EAST, target.parts.get(ForgeDirection.NORTH));
        target.parts.put(ForgeDirection.WEST, target.parts.get(ForgeDirection.SOUTH));
        WirelessCopiedLinks.pasted(fixture.player, target, rotated);
        check(
            WirelessCopiedLinks.waiting() == 2,
            "rotation carries the expected part identities with their frequencies");
        WirelessCopiedLinks.clear();
    }

    private static Address position(int x) {
        return atSide(x, ForgeDirection.UNKNOWN);
    }

    private static Address atSide(int x, ForgeDirection side) {
        return new Address(0, x, 64, 0, side);
    }

    private static IPart part(ItemStack stack) {
        return (IPart) Proxy.newProxyInstance(
            IPart.class.getClassLoader(),
            new Class<?>[] { IPart.class },
            (proxy, method, args) -> method.getName()
                .equals("getItemStack") ? stack : null);
    }

    private static class Fixture implements AutoCloseable {

        final TestWorld world;
        final TestPlayer player;
        final Map<Integer, WorldServer> worlds;
        final WorldServer previous;

        @SuppressWarnings("unchecked")
        Fixture() throws ReflectiveOperationException {
            world = allocate(TestWorld.class);
            world.tiles = new HashMap<>();
            world.loaded = true;
            field(World.class, "provider").set(world, new WorldProviderSurface());
            player = allocate(TestPlayer.class);
            player.worldObj = world;
            worlds = (Map<Integer, WorldServer>) field(DimensionManager.class, "worlds").get(null);
            previous = worlds.put(0, world);
            WirelessCopiedLinks.clear();
        }

        <T extends TileEntity> T put(T tile, int x) {
            world.tiles.values()
                .removeIf(existing -> existing == tile);
            tile.setWorldObj(world);
            tile.xCoord = x;
            tile.yCoord = 64;
            tile.zCoord = 0;
            world.tiles.put(position(x), tile);
            return tile;
        }

        TestHost host(int x) throws ReflectiveOperationException {
            TestHost host = allocate(TestHost.class);
            host.parts = new EnumMap<>(ForgeDirection.class);
            host.data = new NBTTagCompound();
            return put(host, x);
        }

        @Override
        public void close() {
            WirelessCopiedLinks.clear();
            WirelessChannelManager.invalidateFrequencies();
            if (previous == null) worlds.remove(0);
            else worlds.put(0, previous);
        }
    }

    public static class TestWorld extends WorldServer {

        Map<Address, TileEntity> tiles;
        boolean loaded;

        public TestWorld() {
            super(null, null, null, 0, null, null);
        }

        @Override
        public boolean blockExists(int x, int y, int z) {
            return loaded;
        }

        @Override
        public TileEntity getTileEntity(int x, int y, int z) {
            if (!loaded) throw new AssertionError("A pending copy tried to access an unloaded block");
            return tiles.get(new Address(0, x, y, z, ForgeDirection.UNKNOWN));
        }
    }

    public static class TestPlayer extends EntityPlayerMP {

        public TestPlayer() {
            super(null, null, null, null);
        }

        @Override
        public UUID getUniqueID() {
            return PLAYER;
        }
    }

    public static class TestHost extends TileCableBus implements IWirelessTileData {

        Map<ForgeDirection, IPart> parts;
        NBTTagCompound data;

        @Override
        public IPart getPart(ForgeDirection side) {
            return parts.get(side);
        }

        @Override
        public NBTTagCompound gtnl$wirelessData() {
            return data;
        }

        @Override
        public boolean gtnl$hasWirelessData() {
            return data.hasKey(KEY, 10);
        }

        @Override
        public boolean gtnl$hasFrequencyData() {
            return data.hasKey(FREQUENCY_KEY, 10);
        }

        // Keep the vanilla identity lifecycle without requiring an initialized AE runtime.
        @Override
        public void invalidate() {
            tileEntityInvalid = true;
        }

        @Override
        public void validate() {
            tileEntityInvalid = false;
        }
    }

    private static Field field(Class<?> type, String name) throws ReflectiveOperationException {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @SuppressWarnings("unchecked")
    static <T> T allocate(Class<T> type) throws ReflectiveOperationException {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        return (T) unsafe.getMethod("allocateInstance", Class.class)
            .invoke(field(unsafe, "theUnsafe").get(null), type);
    }
}
