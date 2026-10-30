package com.science.gtnl.common.machine.multiblock;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.SoundResource;
import gregtech.api.recipe.RecipeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ProcessingArrayManager {

    private static final Object2ObjectOpenHashMap<String, RecipeMap<?>> mRecipeSaves = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, SoundResource> machineSounds = new Object2ObjectOpenHashMap<>();

    // Adds recipe Maps to the PA using the machines unlocalized name.
    // Example: basicmachine.electrolyzer, with its recipe map will add the electrolyzer's recipe map to the PA
    public static void addRecipeMapToPA(String aMachineName, RecipeMap<?> aMap) {
        if (aMachineName != null) {
            mRecipeSaves.put(aMachineName, aMap);
        }
    }

    // Allows the PA to extract the recipe map for the machine inside it.
    public static RecipeMap<?> giveRecipeMap(String aMachineName) {
        if (aMachineName != null) {
            return mRecipeSaves.get(aMachineName);
        }
        return null;
    }

    public static void addSoundResourceToPA(String machineName, SoundResource soundResource) {
        if (machineName != null) {
            machineSounds.put(machineName, soundResource);
        }
    }

    public static SoundResource getSoundResource(String machineName) {
        if (machineName != null) {
            return machineSounds.get(machineName);
        }
        return null;
    }

    public static String getMachineName(ItemStack stack) {
        int length = stack.getUnlocalizedName()
            .length();
        return stack.getUnlocalizedName()
            .substring(17, length - 8); // trim "gt.blockmachines." and ".tier.xx"
    }

    public static String getFullMachineName(ItemStack stack) {
        String unlocalizedName = stack.getUnlocalizedName();
        return unlocalizedName.substring(17); // trim "gt.blockmachines."
    }

    public static String getRealFullMachineName(ItemStack stack) {
        return stack.getUnlocalizedName();
    }
}
