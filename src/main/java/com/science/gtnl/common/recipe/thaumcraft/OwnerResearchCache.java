package com.science.gtnl.common.recipe.thaumcraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.lib.research.ResearchManager;

public class OwnerResearchCache {

    private static final String RESEARCH_KEY = "Research";
    private static final String RESEARCH_NAME_KEY = "ResearchName";

    private final Set<String> completedResearch = new HashSet<>();

    public boolean isComplete(String ownerName, String research) {
        if (research == null) return false;
        if (research.isEmpty()) return true;
        if (!research.startsWith("@") && ResearchCategories.getResearch(research) == null) return false;

        refresh(ownerName);
        return completedResearch.contains(research);
    }

    public void refresh(String ownerName) {
        if (ownerName == null || ownerName.isEmpty()) return;

        ArrayList<String> research = ResearchManager.getResearchForPlayer(ownerName);
        if (research == null || research.isEmpty() || research.size() == completedResearch.size()) return;

        completedResearch.clear();
        completedResearch.addAll(research);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (String research : completedResearch) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(RESEARCH_NAME_KEY, research);
            list.appendTag(tag);
        }
        nbt.setTag(RESEARCH_KEY, list);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        completedResearch.clear();
        NBTTagList list = nbt.getTagList(RESEARCH_KEY, 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            if (tag.hasKey(RESEARCH_NAME_KEY)) completedResearch.add(tag.getString(RESEARCH_NAME_KEY));
        }
    }
}
