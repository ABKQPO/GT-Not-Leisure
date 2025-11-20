package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.enums.Mods.EnderIO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.commons.lang3.tuple.Pair;

import com.brandon3055.draconicevolution.common.ModItems;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.event.PostMobRegistrationEvent;
import com.science.gtnl.common.material.RecipePool;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import kubatech.loaders.MobHandlerLoader;

public class ExtremeExtremeEntityCrusherRecipes {

    public RecipeMap<?> EEEC = RecipePool.ExtremeExtremeEntityCrusherRecipes;

    public static Set<String> registeredSpawnerTypes = new HashSet<>();

    public static Random FIXED_RANDOM = new Random(1145141919810L);

    @SubscribeEvent
    public void onPostMobRegistration(PostMobRegistrationEvent event) {
        String mobType = event.currentMob;
        MobRecipe mobRecipe = event.recipe;
        ArrayList<MobDrop> drops = event.drops;

        if (drops.isEmpty() || !mobRecipe.isUsableInVial) return;

        if (!registeredSpawnerTypes.add(mobType)) {
            return;
        }

        MobHandlerLoader.MobEECRecipe eecRecipe = new MobHandlerLoader.MobEECRecipe(drops, mobRecipe);

        ItemStack spawner = GTModHandler.getModItem(EnderIO.ID, "blockPoweredSpawner", 1);
        if (spawner == null) return;

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("mobType", mobType);
        nbt.setBoolean("eio.abstractMachine", true);
        spawner.setTagCompound(nbt);

        List<Pair<ItemStack, Integer>> merged = new ArrayList<>();

        outer: for (MobDrop drop : drops) {
            ItemStack output = drop.stack.copy();
            int chance = drop.chance;

            if (output.isItemEqual(new ItemStack(ModItems.mobSoul))) {
                chance = 10;
            }

            if (chance == 0) {
                chance = 100 + FIXED_RANDOM.nextInt(901);
            }

            for (Pair<ItemStack, Integer> pair : merged) {
                if (pair.getRight() == chance && GTUtility.areStacksEqual(pair.getLeft(), output)) {
                    pair.getLeft().stackSize += output.stackSize;
                    continue outer;
                }
            }

            merged.add(Pair.of(output, chance));
        }

        List<ItemStack> outputs = new ArrayList<>();
        List<Integer> chances = new ArrayList<>();

        for (Pair<ItemStack, Integer> pair : merged) {
            outputs.add(pair.getLeft());
            chances.add(pair.getRight());
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, spawner))
            .itemOutputs(outputs.toArray(new ItemStack[0]))
            .fluidOutputs(FluidRegistry.getFluidStack("xpjuice", 120))
            .outputChances(
                chances.stream()
                    .mapToInt(i -> i)
                    .toArray())
            .nbtSensitive()
            .duration(eecRecipe.mDuration)
            .eut(eecRecipe.mEUt)
            .addTo(EEEC);
    }
}
