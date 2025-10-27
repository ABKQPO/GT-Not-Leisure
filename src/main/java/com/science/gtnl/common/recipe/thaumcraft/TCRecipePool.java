package com.science.gtnl.common.recipe.thaumcraft;

import static com.reavaritia.common.ItemLoader.ChronarchsClock;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;

import fox.spiteful.avaritia.items.LudicrousItems;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import tectech.thing.CustomItemList;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

public class TCRecipePool implements IRecipePool {

    public static InfusionRecipe infusionRecipeTimeStopPocketWatch;

    @Override
    public void loadRecipes() {

        infusionRecipeTimeStopPocketWatch = ThaumcraftApi.addInfusionCraftingRecipe(
            "timeStopPocketWatch",
            GTNLItemList.TimeStopPocketWatch.get(1),
            500,
            (new AspectList()).merge(Aspect.getAspect("terminus"), Integer.MAX_VALUE)
                .merge(Aspect.MAGIC, Integer.MAX_VALUE)
                .merge(Mods.ThaumicBoots.isModLoaded() ? Aspect.getAspect("tabernus") : Aspect.VOID, Integer.MAX_VALUE)
                .merge(Aspect.getAspect("custom3"), Integer.MAX_VALUE)
                .merge(Aspect.EXCHANGE, Integer.MAX_VALUE)
                .merge(Mods.ThaumicBoots.isModLoaded() ? Aspect.getAspect("caelum") : Aspect.ORDER, Integer.MAX_VALUE)
                .merge(Mods.MagicBees.isModLoaded() ? Aspect.getAspect("tempus") : Aspect.DEATH, Integer.MAX_VALUE)
                .merge(Aspect.ELDRITCH, Integer.MAX_VALUE)
                .merge(Aspect.ENERGY, Integer.MAX_VALUE),
            new ItemStack(ChronarchsClock),
            new ItemStack[] { GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Universium, 1),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.SpaceTime, 1),
                GTOreDictUnificator.get(OrePrefixes.gem, MaterialsUEVplus.GravitonShard, 1),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.MagMatter, 1), ItemList.Timepiece.get(1),
                ItemList.Transdimensional_Alignment_Matrix.get(1), ItemList.EnergisedTesseract.get(1),
                ItemList.Field_Generator_UXV.get(1), ItemList.GigaChad.get(1),
                ItemUtils.getItemStack(ItemList.ZPM6.get(1), "{GT.ItemCharge:" + Long.MAX_VALUE + "}", null),
                CustomItemList.astralArrayFabricator.get(1), CustomItemList.Machine_Multi_EyeOfHarmony.get(1),
                CustomItemList.Machine_Multi_ForgeOfGods.get(1), ItemList.SpaceElevatorModuleAssemblerT3.get(1),
                ItemList.SpaceElevatorModulePumpT3.get(1), new ItemStack(LudicrousItems.bigPearl, 1),
                ItemUtils.getItemStack(
                    Mods.EnderIO.ID,
                    "blockCapBank",
                    1,
                    0,
                    "{storedEnergyRF:2500000,type:\"CREATIVE\"}",
                    null),
                Mods.TaintedMagic.isModLoaded() ? GTModHandler.getModItem(Mods.TaintedMagic.ID, "ItemFocusTime", 1)
                    : GTModHandler.getModItem(Mods.Thaumcraft.ID, "FocusPrimal", 1),
                GregtechItemList.SynchrotronCapableCatalyst.get(1), GTNLItemList.UMVParallelControllerCore.get(1),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_storage.Universe", 1),
                GTModHandler.getModItem(Mods.AppliedEnergistics2.ID, "item.ItemExtremeStorageCell.Universe", 1),
                Mods.SGCraft.isModLoaded() ? GTModHandler.getModItem(Mods.SGCraft.ID, "ic2Capacitor", 1)
                    : new ItemStack(Blocks.dirt),
                Mods.Computronics.isModLoaded()
                    ? GTModHandler.getModItem(Mods.Computronics.ID, "computronics.ocSpecialParts", 1)
                    : new ItemStack(Items.feather) });
    }
}
