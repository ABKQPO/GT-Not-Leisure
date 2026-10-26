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
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;

import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;
import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;

// Development tool: when GTNL_COMPAT_SNAPSHOT names a file, the names saves depend on are written there sorted,
// so a diff of two snapshots shows every registry name, tile entity id, machine id or config key that moved.
public class CompatSnapshot {

    private static final String[] PACKAGES = { "com.science.gtnl.", "com.reavaritia.", "com.gtnllib." };

    private CompatSnapshot() {}

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
                        .getSimpleName());
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
                            .getSimpleName()));
        }
        Map<Class<?>, String> tiles = ReflectionHelper
            .getPrivateValue(TileEntity.class, null, "classToNameMap", "field_145853_j");
        for (Map.Entry<Class<?>, String> entry : tiles.entrySet()) {
            if (ours(entry.getKey())) lines.add(
                "tile " + entry.getValue()
                    + " class="
                    + entry.getKey()
                        .getSimpleName());
        }
        for (Object entry : EntityList.classToStringMapping.entrySet()) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) entry;
            if (ours((Class<?>) e.getKey())) lines.add("entity " + e.getValue());
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
