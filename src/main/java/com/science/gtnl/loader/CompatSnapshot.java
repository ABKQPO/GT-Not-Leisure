package com.science.gtnl.loader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldSavedData;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.celestweave.CelestweaveBackpackData;
import com.science.gtnl.common.steam.GlobalSteamWorldSavedData;
import com.science.gtnl.common.stellar.StellarIrisWorldSavedData;
import com.science.gtnl.common.wireless.WirelessFrequencyWorldSavedData;
import com.science.gtnl.common.world.VoidWorldSavedData;
import com.science.gtnl.common.yogsothoth.YogTerminalStorage;
import com.science.gtnl.utils.enums.GuiType;
import com.science.gtnl.utils.world.teams.TeamMigrationWorldSavedData;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import gnu.trove.map.hash.TObjectByteHashMap;
import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;

// Development tool: when GTNL_COMPAT_SNAPSHOT names a file, the names saves depend on are written there sorted,
// so a diff of two snapshots shows every registry name, tile entity id, machine id or config key that moved.
public class CompatSnapshot {

    private static final String[] PACKAGES = { "com.science.gtnl.", "com.reavaritia.", "com.gtnllib." };

    // MapStorage finds saved data by these names; ArchitectureContracts checks every WorldSavedData is listed here.
    private static final Map<Class<? extends WorldSavedData>, String> SAVED_DATA = Map.of(
        CelestweaveBackpackData.class,
        CelestweaveBackpackData.NAME,
        GlobalSteamWorldSavedData.class,
        GlobalSteamWorldSavedData.DATA_NAME,
        StellarIrisWorldSavedData.class,
        StellarIrisWorldSavedData.DATA_NAME,
        WirelessFrequencyWorldSavedData.class,
        WirelessFrequencyWorldSavedData.DATA_NAME,
        VoidWorldSavedData.class,
        VoidWorldSavedData.DATA_NAME,
        YogTerminalStorage.class,
        YogTerminalStorage.DATA_NAME,
        TeamMigrationWorldSavedData.class,
        TeamMigrationWorldSavedData.DATA_NAME);

    public static void writeIfRequested() {
        String target = System.getenv("GTNL_COMPAT_SNAPSHOT");
        if (target == null || target.isEmpty()) return;
        List<String> lines = new ArrayList<>();
        String prefix = ScienceNotLeisure.MODID + ":";
        for (Object key : Item.itemRegistry.getKeys()) {
            String name = String.valueOf(key);
            if (!name.startsWith(prefix) && !name.startsWith("reavaritia:")) continue;
            Item item = (Item) Item.itemRegistry.getObject(name);
            if (item instanceof ItemBlock) continue;
            lines.add(
                "item " + name
                    + " lang="
                    + item.getUnlocalizedName()
                    + " class="
                    + item.getClass()
                        .getSimpleName()
                    + " defaultTab="
                    + tabName(ReflectionHelper.getPrivateValue(Item.class, item, "tabToDisplayOn", "field_77701_a")));
        }
        for (Object key : Block.blockRegistry.getKeys()) {
            String name = String.valueOf(key);
            if (!name.startsWith(prefix) && !name.startsWith("reavaritia:")) continue;
            Block block = (Block) Block.blockRegistry.getObject(name);
            Item form = Item.getItemFromBlock(block);
            lines.add(
                "block " + name
                    + " lang="
                    + block.getUnlocalizedName()
                    + " class="
                    + block.getClass()
                        .getSimpleName()
                    + " itemForm="
                    + (form == null ? "none"
                        : form.getClass()
                            .getSimpleName())
                    + " defaultTab="
                    + tabName(
                        ReflectionHelper
                            .getPrivateValue(Block.class, block, "displayOnCreativeTab", "field_149772_a")));
        }
        // The inverse map keeps only one name per class and would miss removed legacy aliases.
        Map<String, Class<?>> tiles = ReflectionHelper
            .getPrivateValue(TileEntity.class, null, "nameToClassMap", "field_145855_i");
        for (Map.Entry<String, Class<?>> entry : tiles.entrySet()) {
            if (ours(entry.getValue())) lines.add(
                "tile " + entry.getKey()
                    + " class="
                    + entry.getValue()
                        .getSimpleName());
        }
        for (Object entry : EntityList.classToStringMapping.entrySet()) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) entry;
            if (ours((Class<?>) e.getKey())) lines.add("entity " + e.getValue());
        }
        Map<Class<?>, EntityRegistry.EntityRegistration> entities = ReflectionHelper
            .getPrivateValue(EntityRegistry.class, EntityRegistry.instance(), "entityClassRegistrations");
        for (Map.Entry<Class<?>, EntityRegistry.EntityRegistration> entry : entities.entrySet()) {
            if (!ours(entry.getKey())) continue;
            EntityRegistry.EntityRegistration registration = entry.getValue();
            lines.add(
                "entity-id " + registration.getContainer()
                    .getModId()
                    + ":"
                    + registration.getModEntityId()
                    + " "
                    + registration.getEntityName()
                    + " class="
                    + entry.getKey()
                        .getSimpleName());
        }
        for (int id = 0; id < GregTechAPI.METATILEENTITIES.length; id++) {
            IMetaTileEntity machine = GregTechAPI.METATILEENTITIES[id];
            if (machine != null && ours(machine.getClass())) lines.add(
                "machine " + id
                    + " "
                    + machine.getMetaName()
                    + " class="
                    + machine.getClass()
                        .getSimpleName());
        }
        for (Potion potion : Potion.potionTypes) {
            if (potion != null && ours(potion.getClass())) lines.add("potion " + potion.id + " " + potion.getName());
        }
        for (Map.Entry<Class<? extends WorldSavedData>, String> entry : SAVED_DATA.entrySet()) lines.add(
            "saveddata " + entry.getValue()
                + " class="
                + entry.getKey()
                    .getSimpleName());
        for (GuiType gui : GuiType.values()) lines.add("gui " + gui.getID() + " " + gui.name());
        lines.addAll(packets());
        lines.addAll(configKeys());
        Collections.sort(lines);
        try {
            Files.write(new File(target).toPath(), lines, StandardCharsets.UTF_8);
            ScienceNotLeisure.LOG.info("GTNL compat snapshot written to {} ({} lines)", target, lines.size());
        } catch (IOException e) {
            ScienceNotLeisure.LOG.error("Failed to write the GTNL compat snapshot to {}", target, e);
        }
    }

    private static boolean ours(Class<?> type) {
        if (type == null) return false;
        String name = type.getName();
        for (String pkg : PACKAGES) if (name.startsWith(pkg)) return true;
        return false;
    }

    // Read the registration-time default without loading client-only getters. Client overrides need a client check.
    private static String tabName(CreativeTabs tab) {
        return tab == null ? "none"
            : ReflectionHelper.getPrivateValue(CreativeTabs.class, tab, "tabLabel", "field_78034_o");
    }

    // Client and server must agree on the packet ids, which come from the registration order.
    private static List<String> packets() {
        List<String> packets = new ArrayList<>();
        try {
            Object codec = ReflectionHelper
                .getPrivateValue(SimpleNetworkWrapper.class, ScienceNotLeisure.network, "packetCodec");
            TObjectByteHashMap<Class<?>> types = ReflectionHelper.getPrivateValue(
                FMLIndexedMessageToMessageCodec.class,
                (FMLIndexedMessageToMessageCodec<?>) codec,
                "types");
            types.forEachEntry((type, id) -> {
                packets.add(String.format("packet %03d %s", id & 0xFF, type.getSimpleName()));
                return true;
            });
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.error("Failed to read the GTNL packet ids for the compat snapshot", e);
            packets.add("packet unreadable");
        }
        return packets;
    }

    private static List<String> configKeys() {
        List<String> keys = new ArrayList<>();
        File file = new File(
            Loader.instance()
                .getConfigDir(),
            "GTNotLeisure/GTNotLeisure.cfg");
        if (!file.isFile()) return keys;
        List<String> path = new ArrayList<>();
        try {
            for (String raw : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.endsWith("{")) {
                    path.add(
                        line.substring(0, line.length() - 1)
                            .trim()
                            .replace("\"", ""));
                } else if (line.equals("}")) {
                    if (!path.isEmpty()) path.remove(path.size() - 1);
                } else if (line.contains("=")) {
                    String key = line.substring(0, line.indexOf('='))
                        .trim();
                    keys.add("config " + String.join(".", path) + "." + key);
                }
            }
        } catch (IOException e) {
            ScienceNotLeisure.LOG.error("Failed to read the GTNL config for the compat snapshot", e);
        }
        return keys;
    }
}
