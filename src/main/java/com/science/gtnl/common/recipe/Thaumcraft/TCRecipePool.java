package com.science.gtnl.common.recipe.Thaumcraft;

import static com.reavaritia.common.ItemLoader.ChronarchsClock;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.Utils.enums.GTNLItemList;

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

public class TCRecipePool {

    public static InfusionRecipe infusionRecipeTimeStopPocketWatch;

    public static void loadRecipes() {

        ItemStack CreativeCapacitorBank = GTModHandler.getModItem(Mods.EnderIO.ID, "blockCapBank", 1, 0);
        NBTTagCompound CreativeCapacitorBankType = CreativeCapacitorBank.getTagCompound();
        if (CreativeCapacitorBankType != null) {
            CreativeCapacitorBankType.setInteger("storedEnergyRF", 2500000);
            CreativeCapacitorBankType.setString("type", "CREATIVE");
        } else {
            CreativeCapacitorBankType = new NBTTagCompound();
            CreativeCapacitorBankType.setInteger("storedEnergyRF", 2500000);
            CreativeCapacitorBankType.setString("type", "CREATIVE");
            CreativeCapacitorBank.setTagCompound(CreativeCapacitorBankType);
        }

        ItemStack MegaUltimateBattery = ItemList.ZPM6.get(1);
        NBTTagCompound MegaUltimateBatteryType = MegaUltimateBattery.getTagCompound();
        if (MegaUltimateBatteryType != null) {
            MegaUltimateBatteryType.setLong("GT.ItemCharge", 9223372036854775807L);
        } else {
            MegaUltimateBatteryType = new NBTTagCompound();
            MegaUltimateBatteryType.setLong("GT.ItemCharge", 9223372036854775807L);
            MegaUltimateBattery.setTagCompound(MegaUltimateBatteryType);
        }

        infusionRecipeTimeStopPocketWatch = ThaumcraftApi.addInfusionCraftingRecipe(
            "GTNL_TIME_STOP_POCKET_WATCH",
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
                ItemList.Field_Generator_UXV.get(1), ItemList.GigaChad.get(1), MegaUltimateBattery,
                CustomItemList.astralArrayFabricator.get(1), CustomItemList.Machine_Multi_EyeOfHarmony.get(1),
                CustomItemList.Machine_Multi_ForgeOfGods.get(1), ItemList.SpaceElevatorModuleAssemblerT3.get(1),
                ItemList.SpaceElevatorModulePumpT3.get(1), new ItemStack(LudicrousItems.bigPearl, 1),
                CreativeCapacitorBank,
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
