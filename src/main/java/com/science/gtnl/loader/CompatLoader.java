package com.science.gtnl.loader;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.bsideup.jabel.Desugar;
import com.gtnewhorizon.gtnhlib.util.data.IMod;
import com.gtnllib.mod.ModList;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.compat.IModCompat;
import com.science.gtnl.compat.avaritia.AvaritiaCompat;
import com.science.gtnl.compat.betterquesting.QuestLoader;
import com.science.gtnl.compat.bogosorter.BogoSorterCompat;
import com.science.gtnl.compat.craftingtweaks.CraftingTweaksCompat;
import com.science.gtnl.compat.galaxyspace.GalaxySpaceCompat;
import com.science.gtnl.compat.mobsinfo.ExtremeExtremeEntityCrusherRecipes;
import com.science.gtnl.compat.nei.NEICompat;
import com.science.gtnl.compat.nutrition.NutrientLoader;
import com.science.gtnl.compat.thaumcraft.TCResearches;
import com.science.gtnl.compat.thaumicenergistics.ThaumicEnergisticsCompat;
import com.science.gtnl.compat.waila.WailaLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Mods;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class CompatLoader implements IContentLoader {

    // Lambdas, not constructor references: a constructor reference loads the compat class even when its mod is missing.
    public static final List<Entry> ENTRIES = List.of(
        new Entry(Mods.BetterQuesting, () -> new QuestLoader(), ModList.BetterQuestingAPI),
        new Entry(Mods.Waila, () -> new WailaLoader()),
        new Entry(Mods.Thaumcraft, () -> new TCResearches()),
        new Entry(Mods.ThaumicEnergistics, () -> new ThaumicEnergisticsCompat()),
        new Entry(Mods.MobsInfo, () -> new ExtremeExtremeEntityCrusherRecipes()),
        new Entry(
            Mods.GalaxySpace,
            () -> new GalaxySpaceCompat(),
            Mods.StevesCarts2,
            Mods.Railcraft,
            Mods.IronTanks,
            Mods.GraviSuite),
        new Entry(ModList.CraftingTweaks, () -> new CraftingTweaksCompat()),
        new Entry(Mods.InventoryBogoSorter, () -> new BogoSorterCompat()),
        new Entry(Mods.Nutrition, () -> new NutrientLoader()),
        new Entry(ModList.NotEnoughItems, () -> new NEICompat()),
        new Entry(Mods.Avaritia, () -> new AvaritiaCompat()));

    private final List<Loaded> loaded = new ObjectArrayList<>();

    @Override
    public String name() {
        return "compat";
    }

    @Override
    public void preInit() {
        for (Entry entry : ENTRIES) {
            if (!entry.modsLoaded()) continue;
            loaded.add(
                new Loaded(
                    entry.mod(),
                    entry.factory()
                        .get()));
            ScienceNotLeisure.LOG.info(
                "GTNL compat for {} is active",
                entry.mod()
                    .getID());
        }
        each("preInit", IModCompat::preInit);
    }

    @Override
    public void init() {
        each("init", IModCompat::init);
    }

    @Override
    public void postInit() {
        each("postInit", IModCompat::postInit);
    }

    @Override
    public void loadComplete() {
        each("loadComplete", IModCompat::loadComplete);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        for (Loaded entry : loaded) {
            try {
                entry.compat()
                    .clientPreInit();
            } catch (RuntimeException e) {
                throw failure(entry, "clientPreInit", e);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientInit() {
        for (Loaded entry : loaded) {
            try {
                entry.compat()
                    .clientInit();
            } catch (RuntimeException e) {
                throw failure(entry, "clientInit", e);
            }
        }
    }

    private void each(String phase, Consumer<IModCompat> action) {
        for (Loaded entry : loaded) {
            try {
                action.accept(entry.compat());
            } catch (RuntimeException e) {
                throw failure(entry, phase, e);
            }
        }
    }

    private static IllegalStateException failure(Loaded entry, String phase, RuntimeException cause) {
        return new IllegalStateException(
            "GTNL compat for " + entry.mod()
                .getID() + " failed in " + phase,
            cause);
    }

    @Desugar
    public record Entry(IMod mod, Supplier<IModCompat> factory, List<IMod> alsoRequired) {

        public Entry(IMod mod, Supplier<IModCompat> factory, IMod... alsoRequired) {
            this(mod, factory, List.of(alsoRequired));
        }

        public boolean modsLoaded() {
            return mod.isModLoaded() && alsoRequired.stream()
                .allMatch(IMod::isModLoaded);
        }
    }

    @Desugar
    private record Loaded(IMod mod, IModCompat compat) {}
}
