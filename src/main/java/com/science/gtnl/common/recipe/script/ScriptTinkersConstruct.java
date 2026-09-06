package com.science.gtnl.common.recipe.script;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.dreammaster.scripts.IScriptLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.enums.Mods;
import tconstruct.library.TConstructRegistry;

public class ScriptTinkersConstruct implements IScriptLoader {

    @Override
    public String getScriptName() {
        return "TinkersConstruct";
    }

    @Override
    public List<Mods> getDependencies() {
        return Arrays.asList(Mods.TinkerConstruct, Mods.GregTech);
    }

    @Override
    public void loadRecipes() {
        TConstructRegistry.getTableCasting()
            .addCastingRecipe(
                GTNLItemList.SearedLadder.get(1),
                FluidRegistry.getFluidStack("stone.seared", 180),
                new ItemStack(Blocks.ladder, 1),
                true,
                100);
    }
}
