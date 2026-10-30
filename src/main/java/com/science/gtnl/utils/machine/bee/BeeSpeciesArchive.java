package com.science.gtnl.utils.machine.bee;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class BeeSpeciesArchive {

    private static final int DATA_VERSION = 2;

    private final Set<String> unlockedSpecies = new LinkedHashSet<>();

    public boolean unlockSpecies(String species) {
        if (species == null || species.isEmpty()) return false;
        String canonicalUID = BeeBreedingHelper.getCanonicalUID(species);
        if (canonicalUID == null || canonicalUID.isEmpty()) return false;
        return unlockedSpecies.add(canonicalUID);
    }

    public boolean hasSpecies(String species) {
        if (species == null || species.isEmpty()) return false;
        return unlockedSpecies.contains(species) && BeeBreedingHelper.getSpeciesByUID(species) != null;
    }

    public Set<String> getSpecies() {
        Set<String> available = new LinkedHashSet<>();
        for (String uid : unlockedSpecies) {
            if (BeeBreedingHelper.getSpeciesByUID(uid) != null) available.add(uid);
        }
        return Collections.unmodifiableSet(available);
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

    public static BeeSpeciesArchive fromNBT(NBTTagCompound tag) {
        BeeSpeciesArchive archive = new BeeSpeciesArchive();
        if (tag == null) return archive;

        if (tag.hasKey("unlockedSpecies", 9)) {
            NBTTagList speciesList = tag.getTagList("unlockedSpecies", 10);
            for (int i = 0; i < speciesList.tagCount(); i++) {
                archive.restoreSpecies(
                    speciesList.getCompoundTagAt(i)
                        .getString("uid"));
            }
            return archive;
        }

        NBTTagList oldInventory = tag.getTagList("inventory", 10);
        for (int i = 0; i < oldInventory.tagCount(); i++) {
            archive.restoreSpecies(
                oldInventory.getCompoundTagAt(i)
                    .getString("species"));
        }
        return archive;
    }

    private void restoreSpecies(String species) {
        if (species == null || species.isEmpty()) return;
        unlockedSpecies.add(species.contains(".") ? species : BeeBreedingHelper.getCanonicalUID(species));
    }
}
