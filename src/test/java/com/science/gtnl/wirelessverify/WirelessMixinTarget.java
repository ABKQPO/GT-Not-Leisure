package com.science.gtnl.wirelessverify;

import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.common.wireless.ChannelBudgetTest;
import com.science.gtnl.common.wireless.WirelessDiscoveryTest;
import com.science.gtnl.common.wireless.WirelessPathingAccess;
import com.science.gtnl.common.wireless.WirelessPathingState;
import com.science.gtnl.common.wireless.WirelessTileData;

/** Target loaded after the Mixin DEFAULT phase, so this verifies real injection including local capture. */
public final class WirelessMixinTarget {

    public static void main(String[] args) throws Exception {
        Class<?> tile = Class
            .forName("net.minecraft.tileentity.TileEntity", false, WirelessMixinTarget.class.getClassLoader());
        if (!WirelessTileData.class.isAssignableFrom(tile)) {
            throw new AssertionError("Wireless NBT mixin did not transform Minecraft TileEntity");
        }
        verifyTileSave(tile);
        WirelessDiscoveryTest.run();
        ChannelBudgetTest.retainedMachineClasses();
        verifyVisualiserHooks();
        Class<?> node = Class.forName("appeng.me.GridNode", false, WirelessMixinTarget.class.getClassLoader());
        for (String hook : new String[] { "gtnl$nodeUpdated", "gtnl$nodeDestroyed" }) {
            if (java.util.Arrays.stream(node.getDeclaredMethods())
                .noneMatch(
                    method -> method.getName()
                        .contains(hook)))
                throw new AssertionError("Missing AE node lifecycle hook: " + hook);
        }
        Class<?> parts = Class
            .forName("appeng.parts.CableBusContainer", false, WirelessMixinTarget.class.getClassLoader());
        boolean placementHook = java.util.Arrays.stream(parts.getDeclaredMethods())
            .anyMatch(
                method -> method.getName()
                    .contains("gtnl$placedPart"));
        if (!placementHook)
            throw new AssertionError("Wireless placement mixin did not transform AE2 CableBusContainer");
        Class<?> target = Class
            .forName("appeng.me.pathfinding.PathingCalculation", false, WirelessMixinTarget.class.getClassLoader());
        if (!WirelessPathingAccess.class.isAssignableFrom(target)) {
            throw new AssertionError("Wireless channel mixin did not transform AE2 PathingCalculation");
        }
        if (System.getProperty("gtnl.wireless.compatMixin") != null) {
            boolean foreignHook = java.util.Arrays.stream(target.getDeclaredMethods())
                .anyMatch(
                    method -> method.getName()
                        .contains("gtswn$independentFaceForUnknownDirection"));
            if (!foreignHook) throw new AssertionError("GTSWN compatibility mixin was not applied");
            System.out.println("GTSWN and GTNL pathing hooks are both present on the transformed AE2 class.");
        }
        Class<?> cache = Class
            .forName("appeng.me.cache.PathGridCache", false, WirelessMixinTarget.class.getClassLoader());
        if (!WirelessPathingState.class.isAssignableFrom(cache)) {
            throw new AssertionError("Wireless pending-pathing mixin did not transform AE2 PathGridCache");
        }
        Class<?> grid = Class.forName("appeng.api.networking.IGrid", false, cache.getClassLoader());
        WirelessPathingState instance = (WirelessPathingState) cache.getConstructor(grid)
            .newInstance((Object) null);
        if (!instance.gtnl$isRepathPending())
            throw new AssertionError("A new cache has controller/pathing work queued");
        for (String field : new String[] { "updateNetwork", "recalculateControllerNextTick" }) {
            var flag = cache.getDeclaredField(field);
            flag.setAccessible(true);
            flag.setBoolean(instance, false);
        }
        if (instance.gtnl$isRepathPending()) throw new AssertionError("Completed cache should be settled");
        cache.getMethod("repath")
            .invoke(instance);
        if (!instance.gtnl$isRepathPending())
            throw new AssertionError("repath must immediately invalidate a settled result");
        System.out
            .println("WirelessMixinBootstrapTest: AE2 pathing and Minecraft tile NBT hooks transformed successfully.");
    }

    private static void verifyVisualiserHooks() throws Exception {
        for (String[] target : new String[][] {
            { "appeng.items.tools.ToolNetworkVisualiser", "gtnl$viewerDimension", "gtnl$cardSnapshot",
                "gtnl$refreshCadence" },
            { "appeng.client.render.NetworkVisualiserRender", "gtnl$cardVisualiser", "gtnl$invalidateSnapshot" } }) {
            Class<?> type = Class.forName(target[0], false, WirelessMixinTarget.class.getClassLoader());
            for (int i = 1; i < target.length; i++) {
                String hook = target[i];
                if (java.util.Arrays.stream(type.getDeclaredMethods())
                    .noneMatch(
                        method -> method.getName()
                            .contains(hook)))
                    throw new AssertionError("Missing visualiser hook: " + hook);
            }
        }
    }

    private static void verifyTileSave(Class<?> tile) throws Exception {
        tile.getMethod("addMapping", Class.class, String.class)
            .invoke(null, tile, "GTNLWirelessTestTile");
        WirelessTileData original = (WirelessTileData) tile.getConstructor()
            .newInstance();
        if (original.gtnl$hasWirelessData()) throw new AssertionError("New tiles have no inherited wireless data");
        NBTTagCompound payload = new NBTTagCompound();
        payload.setInteger("version", 1);
        payload.setString("marker", "saved");
        original.gtnl$wirelessData()
            .setTag(WirelessTileData.KEY, payload);
        tile.getMethod("invalidate")
            .invoke(original);
        NBTTagCompound saved = new NBTTagCompound();
        saved.setString("other", "preserved");
        tile.getMethod("writeToNBT", NBTTagCompound.class)
            .invoke(original, saved);
        if (!saved.getCompoundTag(WirelessTileData.KEY)
            .equals(payload)) {
            throw new AssertionError("An invalidated tile must still serialize wireless provenance");
        }
        WirelessTileData restored = (WirelessTileData) tile.getConstructor()
            .newInstance();
        tile.getMethod("readFromNBT", NBTTagCompound.class)
            .invoke(restored, saved);
        saved.getCompoundTag(WirelessTileData.KEY)
            .setString("marker", "mutated");
        if (!"saved".equals(
            restored.gtnl$wirelessData()
                .getCompoundTag(WirelessTileData.KEY)
                .getString("marker"))) {
            throw new AssertionError("Restored tile data must not alias the input NBT");
        }
        restored.gtnl$wirelessData()
            .removeTag(WirelessTileData.KEY);
        tile.getMethod("writeToNBT", NBTTagCompound.class)
            .invoke(restored, saved);
        if (saved.hasKey(WirelessTileData.KEY) || !"preserved".equals(saved.getString("other"))) {
            throw new AssertionError("Unlink must remove only our field even when the output compound is reused");
        }
        tile.getMethod("readFromNBT", NBTTagCompound.class)
            .invoke(original, new NBTTagCompound());
        if (original.gtnl$hasWirelessData()) throw new AssertionError("Reading an unbound tile resets stale data");
    }
}
