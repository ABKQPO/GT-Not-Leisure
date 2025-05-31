package com.science.gtnl.common.recipe.GTNL;

import static gregtech.api.enums.Mods.EnderIO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import com.brandon3055.draconicevolution.common.ModItems;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.event.PostMobRegistrationEvent;
import com.science.gtnl.loader.RecipeRegister;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import kubatech.loaders.MobHandlerLoader;

public class ExtremeExtremeEntityCrusherRecipes {

    private final RecipeMap<?> EEEC = RecipeRegister.ExtremeExtremeEntityCrusherRecipes;

    private static final Set<String> registeredSpawnerTypes = new HashSet<>();

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ExtremeExtremeEntityCrusherRecipes());
        registeredSpawnerTypes.clear();
    }

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
        spawner.setTagCompound(nbt);

        List<ItemStack> outputs = new ArrayList<>();
        List<Integer> chances = new ArrayList<>();

        for (MobDrop drop : drops) {
            ItemStack output = drop.stack.copy();
            int chance = drop.chance;

            if (output.isItemEqual(new ItemStack(ModItems.mobSoul))) {
                chance = 10;
            }

            outputs.add(output);
            chances.add(chance);
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, spawner))
            .itemOutputs(outputs.toArray(new ItemStack[0]))
            .fluidOutputs(FluidRegistry.getFluidStack("xpjuice", 120))
            .outputChances(
                chances.stream()
                    .mapToInt(i -> i)
                    .toArray())
            .duration(eecRecipe.mDuration)
            .eut(eecRecipe.mEUt)
            .addTo(EEEC);
    }
}
