package com.science.gtnl.common.recipe.gtnl;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

import com.brandon3055.draconicevolution.common.ModItems;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.event.PostMobRegistrationEvent;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import it.unimi.dsi.fastutil.objects.ObjectList;
import kubatech.loaders.MobHandlerLoader;

public class ExtremeExtremeEntityCrusherRecipes {

    public RecipeMap<?> EEEC = GTNLRecipeMaps.ExtremeExtremeEntityCrusherRecipes;

    public static IntSet registeredSpawnerTypes = new IntOpenHashSet();

    public static XSTR FIXED_RANDOM = new XSTR(1145141919810L);

    @SubscribeEvent
    public void onPostMobRegistration(PostMobRegistrationEvent event) {
        String mobType = event.currentMob;
        MobRecipe mobRecipe = event.recipe;
        ArrayList<MobDrop> drops = event.drops;

        if (drops.isEmpty() || !mobRecipe.isUsableInVial) return;

        if (!registeredSpawnerTypes.add(mobType.hashCode())) {
            return;
        }

        MobHandlerLoader.MobEECRecipe eecRecipe = new MobHandlerLoader.MobEECRecipe(drops, mobRecipe);

        ItemStack spawner = ModsItemlist.EnderIOBlockPoweredSpawner.get(1);
        if (spawner == null) return;

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("mobType", mobType);
        nbt.setBoolean("eio.abstractMachine", true);
        spawner.setTagCompound(nbt);

        ObjectList<ObjectIntPair<ItemStack>> merged = new ObjectArrayList<>();

        outer: for (MobDrop drop : drops) {
            ItemStack output = drop.stack.copy();
            int chance = drop.chance;

            if (output.isItemEqual(new ItemStack(ModItems.mobSoul))) {
                chance = 10;
            }

            if (chance == 0) {
                chance = 100 + FIXED_RANDOM.nextInt(901);
            }

            for (ObjectIntPair<ItemStack> pair : merged) {
                if (pair.rightInt() == chance && GTUtility.areStacksEqual(pair.left(), output)) {
                    pair.left().stackSize += output.stackSize;
                    continue outer;
                }
            }

            merged.add(ObjectIntPair.of(output, chance));
        }

        ObjectList<ItemStack> outputs = new ObjectArrayList<>();
        IntArrayList chances = new IntArrayList();

        for (ObjectIntPair<ItemStack> pair : merged) {
            outputs.add(pair.left());
            chances.add(pair.rightInt());
        }

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, spawner))
            .itemOutputs(outputs.toArray(new ItemStack[0]))
            .fluidOutputs(FluidRegistry.getFluidStack("xpjuice", 120))
            .outputChances(chances.toIntArray())
            .nbtSensitive()
            .duration(eecRecipe.mDuration)
            .eut(eecRecipe.mEUt)
            .addTo(EEEC);
    }
}
