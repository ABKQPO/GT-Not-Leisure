package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.item.ItemStack;

import com.dreammaster.item.NHItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.EyeOfHarmonyRecipeFactory;

import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gtPlusPlus.core.material.MaterialsElements;
import gtnhlanth.common.register.WerkstoffMaterialPool;
import tectech.util.FluidStackLong;
import tectech.util.ItemStackLong;

public class EyeOfHarmonyRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        EyeOfHarmonyRecipeFactory.addCustomRecipeEntry(
            ModsItemlist.VoidSingularity.get(1),
            new ItemStackLong[] { new ItemStackLong(WerkstoffMaterialPool.Gangue.get(OrePrefixes.dust, 1), 1519274962L),
                new ItemStackLong(WerkstoffLoader.Bismutite.get(OrePrefixes.dust, 1), 161842735L),
                new ItemStackLong(WerkstoffLoader.VanadioOxyDravit.get(OrePrefixes.dust, 1), 154923846L),
                new ItemStackLong(WerkstoffLoader.Arsenopyrite.get(OrePrefixes.dust, 1), 148375192L),
                new ItemStackLong(WerkstoffLoader.Ferberite.get(OrePrefixes.dust, 1), 142106483L),
                new ItemStackLong(WerkstoffLoader.Loellingit.get(OrePrefixes.dust, 1), 136159274L),
                new ItemStackLong(WerkstoffLoader.Roquesit.get(OrePrefixes.dust, 1), 129847315L),
                new ItemStackLong(WerkstoffLoader.RedZircon.get(OrePrefixes.dust, 1), 124036198L),
                new ItemStackLong(WerkstoffLoader.Fayalit.get(OrePrefixes.dust, 1), 118294756L),
                new ItemStackLong(WerkstoffLoader.DescloiziteCUVO4.get(OrePrefixes.dust, 1), 112958423L),
                new ItemStackLong(WerkstoffLoader.FuchsitAL.get(OrePrefixes.dust, 1), 107482931L),
                new ItemStackLong(WerkstoffLoader.FuchsitCR.get(OrePrefixes.dust, 1), 102165847L),
                new ItemStackLong(WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.dust, 1), 96947215L),
                new ItemStackLong(WerkstoffLoader.BArTiMaEuSNeK.get(OrePrefixes.dust, 1), 91583642L),
                new ItemStackLong(WerkstoffLoader.Tiberium.get(OrePrefixes.dust, 1), 86429173L),
                new ItemStackLong(WerkstoffMaterialPool.SamariumOreConcentrate.get(OrePrefixes.dust, 1), 81856394L),
                new ItemStackLong(GTNLMaterials.Technetium.get(OrePrefixes.dust, 1), 77294815L),
                new ItemStackLong(WerkstoffLoader.Zirconium.get(OrePrefixes.dust, 1), 72918463L),
                new ItemStackLong(WerkstoffLoader.Ruthenium.get(OrePrefixes.dust, 1), 68472531L),
                new ItemStackLong(WerkstoffLoader.Rhodium.get(OrePrefixes.dust, 1), 63819472L),
                new ItemStackLong(WerkstoffLoader.Atheneite.get(OrePrefixes.dust, 1), 59283746L),
                new ItemStackLong(MaterialsElements.getInstance().HAFNIUM.getDust(1), 55104827L),
                new ItemStackLong(WerkstoffMaterialPool.Hafnia.get(OrePrefixes.dust, 1), 50928471L),
                new ItemStackLong(WerkstoffMaterialPool.Iodine.get(OrePrefixes.dust, 1), 46739158L),
                new ItemStackLong(Materials.Draconium.getDust(1), 42581639L),
                new ItemStackLong(Materials.Beryllium.getDust(1), 38927415L),
                new ItemStackLong(Materials.Iron.getDust(1), 35284617L),
                new ItemStackLong(Materials.Zinc.getDust(1), 31846592L),
                new ItemStackLong(Materials.Samarium.getDust(1), 28471935L),
                new ItemStackLong(Materials.Neutronium.getDust(1), 25293846L),
                new ItemStackLong(Materials.Rutile.getDust(1), 22184753L),
                new ItemStackLong(Materials.Ardite.getDust(1), 19472638L),
                new ItemStackLong(Materials.Ledox.getDust(1), 16829471L),
                new ItemStackLong(Materials.InfinityCatalyst.getDust(1), 14385926L),
                new ItemStackLong(Materials.MysteriousCrystal.getDust(1), 12174853L),
                new ItemStackLong(Materials.Rubracium.getDust(1), 10284719L),
                new ItemStackLong(Materials.Vulcanite.getDust(1), 8572931L),
                new ItemStackLong(Materials.GreenSapphire.getDust(1), 7103846L),
                new ItemStackLong(Materials.Olivine.getDust(1), 5847192L),
                new ItemStackLong(Materials.NetherStar.getDust(1), 4729183L),
                new ItemStackLong(Materials.Topaz.getDust(1), 3819472L),
                new ItemStackLong(Materials.Tanzanite.getDust(1), 3104825L),
                new ItemStackLong(Materials.Opal.getDust(1), 2518374L),
                new ItemStackLong(Materials.BlueTopaz.getDust(1), 1938472L),
                new ItemStackLong(Materials.Forcicium.getDust(1), 1492837L),
                new ItemStackLong(Materials.Vinteum.getDust(1), 1128475L),
                new ItemStackLong(Materials.TricalciumPhosphate.getDust(1), 947182L),
                new ItemStackLong(Materials.InfusedAir.getDust(1), 819274L),
                new ItemStackLong(Materials.InfusedFire.getDust(1), 728361L),
                new ItemStackLong(Materials.InfusedEarth.getDust(1), 651928L),
                new ItemStackLong(Materials.InfusedWater.getDust(1), 592834L),
                new ItemStackLong(Materials.InfusedEntropy.getDust(1), 541283L),
                new ItemStackLong(Materials.SiliconDioxide.getDust(1), 498271L),
                new ItemStackLong(MaterialsElements.getInstance().RHENIUM.getDust(1), 462193L),
                new ItemStackLong(MaterialsElements.getInstance().THALLIUM.getDust(1), 431827L),
                new ItemStackLong(MaterialsElements.getInstance().GERMANIUM.getDust(1), 408273L),
                new ItemStackLong(MaterialsElements.getInstance().SELENIUM.getDust(1), 385912L),
                new ItemStackLong(
                    Mods.NewHorizonsCoreMod.isModLoaded() ? getChargedCertusQuartzDust()
                        : Materials.CertusQuartz.getDust(1),
                    364182L),
                new ItemStackLong(Materials.Salt.getDust(1), 341928L),
                new ItemStackLong(Materials.Mica.getDust(1), 325817L),
                new ItemStackLong(Materials.Bastnasite.getDust(1), 311928L),
                new ItemStackLong(Materials.Lepidolite.getDust(1), 298471L),
                new ItemStackLong(Materials.Realgar.getDust(1), 287319L),
                new ItemStackLong(Materials.Redstone.getDust(1), 279182L),
                new ItemStackLong(Materials.CosmicNeutronium.getDust(1), 274819L),
                new ItemStackLong(Materials.QuartzSand.getDust(1), 270182L),
                new ItemStackLong(Materials.Asbestos.getDust(1), 267491L),
                new ItemStackLong(Materials.Vyroxeres.getDust(1), 265182L),
                new ItemStackLong(Materials.Orichalcum.getDust(1), 263918L),
                new ItemStackLong(Materials.Glowstone.getDust(1), 262473L),
                new ItemStackLong(Materials.Diamond.getGems(1), 262105L), },
            new FluidStackLong[] { new FluidStackLong(Materials.Radon.getPlasma(56000000), 56000000L),
                new FluidStackLong(Materials.Bismuth.getPlasma(56000000), 56000000L),
                new FluidStackLong(Materials.Oxygen.getPlasma(56000000), 56000000L),
                new FluidStackLong(Materials.Tin.getPlasma(56000000), 56000000L),
                new FluidStackLong(Materials.Lead.getPlasma(56000000), 56000000L),
                new FluidStackLong(Materials.Thorium.getPlasma(56000000), 56000000L),
                new FluidStackLong(Materials.Naquadria.getPlasma(56000000), 56000000L),
                new FluidStackLong(Materials.DTR.getFluid(112000000), 112000000L),
                new FluidStackLong(Materials.RawStarMatter.getFluid(700000), 700000L),
                new FluidStackLong(Materials.WhiteDwarfMatter.getMolten(4608), 4608L),
                new FluidStackLong(Materials.BlackDwarfMatter.getMolten(4608), 4608L),
                new FluidStackLong(Materials.Universium.getMolten(1152), 1152L) },
            7,
            568000000000000000L,
            7090000000000000L,
            10000000000L,
            10000000000L,
            189744,
            0.65);
    }

    @Optional.Method(modid = "dreamcraft")
    public ItemStack getChargedCertusQuartzDust() {
        return NHItemList.ChargedCertusQuartzDust.get(1);
    }
}
