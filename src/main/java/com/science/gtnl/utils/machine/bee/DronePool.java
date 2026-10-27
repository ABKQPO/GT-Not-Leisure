package com.science.gtnl.utils.machine.bee;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class DronePool {

    private static final int DATA_VERSION = 2;

    private final Set<String> unlockedSpecies = new LinkedHashSet<>();

    public boolean addDrone(ItemStack droneStack) {
        if (droneStack == null || !BeeBreedingHelper.isDrone(droneStack)) return false;
        return unlockSpecies(BeeBreedingHelper.getBeeUID(droneStack));
    }

    public boolean unlockSpecies(String species) {
        if (species == null || species.isEmpty()) return false;
        String canonicalUID = BeeBreedingHelper.getCanonicalUID(species);
        if (canonicalUID == null || canonicalUID.isEmpty()) return false;
        return unlockedSpecies.add(canonicalUID);
    }

    public boolean hasDrone(String species) {
        if (species == null || species.isEmpty()) return false;
        return unlockedSpecies.contains(species);
    }

    public int getDroneCount(String species) {
        return hasDrone(species) ? 1 : 0;
    }

    public Set<String> getAvailableSpecies() {
        return Collections.unmodifiableSet(unlockedSpecies);
    }

    public void clear() {
        unlockedSpecies.clear();
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("version", DATA_VERSION);

        NBTTagList speciesList = new NBTTagList();
        for (String uid : unlockedSpecies) {
            NBTTagCompound speciesTag = new NBTTagCompound();
            speciesTag.setString("uid", uid);
            speciesList.appendTag(speciesTag);
        }
        tag.setTag("unlockedSpecies", speciesList);
        return tag;
    }

    public static DronePool fromNBT(NBTTagCompound tag) {
        DronePool pool = new DronePool();
        if (tag == null) return pool;

        if (tag.hasKey("unlockedSpecies", 9)) {
            NBTTagList speciesList = tag.getTagList("unlockedSpecies", 10);
            for (int i = 0; i < speciesList.tagCount(); i++) {
                pool.unlockSpecies(
                    speciesList.getCompoundTagAt(i)
                        .getString("uid"));
            }
            return pool;
        }

        NBTTagList oldInventory = tag.getTagList("inventory", 10);
        for (int i = 0; i < oldInventory.tagCount(); i++) {
            pool.unlockSpecies(
                oldInventory.getCompoundTagAt(i)
                    .getString("species"));
        }
        return pool;
    }
}
