package com.science.gtnl.common.recipe.script;

import static gregtech.api.enums.Mods.*;
import static gregtech.api.enums.Mods.GregTech;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.dreammaster.scripts.IScriptLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import tconstruct.library.TConstructRegistry;

public class ScriptTinkersConstruct implements IScriptLoader {

    @Override
    public String getScriptName() {
        return "TinkersConstruct";
    }

    @Override
    public List<String> getDependencies() {
        return Arrays.asList(TinkerConstruct.ID, GregTech.ID);
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
