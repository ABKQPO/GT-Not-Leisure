package com.science.gtnl.utils.machine;

import java.util.Collection;

import bartworks.API.recipe.BartWorksRecipeMaps;
import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kubatech.loaders.DEFCRecipes;
import lombok.Getter;
import tectech.recipe.TecTechRecipeMaps;

public class NineIndustrialMultiMachineManager {

    public static final MachineMode[] MACHINE_MODES = MachineMode.VALUES;
    public static final int MACHINE_MODE_COUNT = MACHINE_MODES.length;

    public static String getModeLocalization(int machineMode) {
        MachineMode mode = MachineMode.fromId(machineMode);
        return mode != null ? mode.getLocalizationKey() : "";
    }

    public int getNextMachineMode(int currentMode) {
        final MachineMode mode = MachineMode.fromId(currentMode);
        if (mode == null) return 0;
        return MACHINE_MODES[(mode.ordinal() + 1) % MACHINE_MODE_COUNT].getId();
    }

    public static Collection<RecipeMap<?>> getAllRecipeMaps() {
        return MachineMode.getAllRecipeMaps();
    }

    public static RecipeMap<?> getRecipeMap(int aMode) {
        MachineMode mode = MachineMode.fromId(aMode);
        return mode != null ? mode.getRecipeMap() : null;
    }

    public static int getModeMapIndex(int machineMode, int column) {
        final int row = machineMode / 3;
        final int col = column % 3;
        if (row * 3 + col >= MACHINE_MODE_COUNT) return -1;
        return row * 3 + col;
    }

    @Getter
    public enum MachineMode {

        COMPRESSOR(RecipeMaps.compressorRecipes),
        LATHE(RecipeMaps.latheRecipes),
        MAGNETIC(RecipeMaps.polarizerRecipes),
        FERMENTER(RecipeMaps.fermentingRecipes),
        FLUIDEXTRACT(RecipeMaps.fluidExtractionRecipes),
        EXTRACTOR(RecipeMaps.extractorRecipes),
        LASER(RecipeMaps.laserEngraverRecipes),
        AUTOCLAVE(RecipeMaps.autoclaveRecipes),
        FLUIDSOLIDIFY(RecipeMaps.fluidSolidifierRecipes),
        OREWASHER(RecipeMaps.oreWasherRecipes),
        THERMALCENTRIFUGE(RecipeMaps.thermalCentrifugeRecipes),
        NEUTRONIUMCOMPRESSOR(RecipeMaps.neutroniumCompressorRecipes),
        RECYCLER(RecipeMaps.recyclerRecipes),
        FURNACE(RecipeMaps.furnaceRecipes),
        MICROWAVE(RecipeMaps.microwaveRecipes),
        REPLICATOR(RecipeMaps.replicatorRecipes),
        ARCFURNACE(RecipeMaps.arcFurnaceRecipes),
        PRINTER(RecipeMaps.printerRecipes),
        SIFTER(RecipeMaps.sifterRecipes),
        FORMINGPRESS(RecipeMaps.formingPressRecipes),
        MACERATOR(RecipeMaps.maceratorRecipes),
        CHEMICALBATH(RecipeMaps.chemicalBathRecipes),
        BREWING(RecipeMaps.brewingRecipes),
        FLUIDHEATER(RecipeMaps.fluidHeaterRecipes),
        DISTILLERY(RecipeMaps.distilleryRecipes),
        PACKAGER(RecipeMaps.packagerRecipes),
        UNPACKAGER(RecipeMaps.unpackagerRecipes),
        FUSION(RecipeMaps.fusionRecipes),
        BLASTFURNACE(RecipeMaps.blastFurnaceRecipes),
        PLASMAFORGE(RecipeMaps.plasmaForgeRecipes),
        TRANSCENDENTPLASMAMIXER(RecipeMaps.transcendentPlasmaMixerRecipes),
        PRIMITIVEBLAST(RecipeMaps.primitiveBlastRecipes),
        IMPLOSION(RecipeMaps.implosionRecipes),
        VACUUMFREEZER(RecipeMaps.vacuumFreezerRecipes),
        MULTIBLOCKCHEMICALREACTOR(RecipeMaps.multiblockChemicalReactorRecipes),
        DISTILLATIONTOWER(RecipeMaps.distillationTowerRecipes),
        CRACKING(RecipeMaps.crackingRecipes),
        PYROLYSE(RecipeMaps.pyrolyseRecipes),
        WIREMILL(RecipeMaps.wiremillRecipes),
        BENDER(RecipeMaps.benderRecipes),
        ALLOYSMELTER(RecipeMaps.alloySmelterRecipes),
        ASSEMBLER(RecipeMaps.assemblerRecipes),
        CIRCUITASSEMBLER(RecipeMaps.circuitAssemblerRecipes),
        CUTTER(RecipeMaps.cutterRecipes),
        EXTRUDER(RecipeMaps.extruderRecipes),
        HAMMER(RecipeMaps.hammerRecipes),
        AMPLIFIER(RecipeMaps.amplifierRecipes),
        EXTREMEDIESELFUELS(RecipeMaps.extremeDieselFuels),
        HOTFUELS(RecipeMaps.hotFuels),
        DENSELIQUIDFUELS(RecipeMaps.denseLiquidFuels),
        PLASMAFUELS(RecipeMaps.plasmaFuels),
        MAGICFUELS(RecipeMaps.magicFuels),
        SMALLNAQUADAHREACTORFUELS(RecipeMaps.smallNaquadahReactorFuels),
        LARGENAQUADAHREACTORFUELS(RecipeMaps.largeNaquadahReactorFuels),
        HUGENAQUADAHREACTORFUELS(RecipeMaps.hugeNaquadahReactorFuels),
        EXTREMENAQUADAHREACTORFUELS(RecipeMaps.extremeNaquadahReactorFuels),
        ULTRAHUGENAQUADAHREACTORFUELS(RecipeMaps.ultraHugeNaquadahReactorFuels),
        NANOFORGE(RecipeMaps.nanoForgeRecipes),
        PCBFACTORY(RecipeMaps.pcbFactoryRecipes),
        COKEOVEN(RecipeMaps.industrialCokeOvenRecipes),
        ROCKETFUELS(RecipeMaps.rocketFuels),
        QUANTUMFORCETRANSFORMER(RecipeMaps.quantumForceTransformerRecipes),
        VACUUMFURNACE(RecipeMaps.vacuumFurnaceRecipes),
        ALLOYBLASTSMELTER(RecipeMaps.alloyBlastSmelterRecipes),
        LIQUIDFLUORINETHORIUMREACTOR(RecipeMaps.liquidFluorineThoriumReactorRecipes),
        NUCLEARSALTPROCESSINGPLANT(RecipeMaps.nuclearSaltProcessingPlantRecipes),
        MILLING(RecipeMaps.millingRecipes),
        FISSIONFUELPROCESSING(RecipeMaps.fissionFuelProcessingRecipes),
        COLDTRAP(RecipeMaps.coldTrapRecipes),
        REACTORPROCESSINGUNIT(RecipeMaps.reactorProcessingUnitRecipes),
        SIMPLEWASHER(RecipeMaps.simpleWasherRecipes),
        MOLECULARTRANSFORMER(RecipeMaps.molecularTransformerRecipes),
        CHEMICALPLANT(RecipeMaps.chemicalPlantRecipes),
        RTG(RecipeMaps.rtgFuels),
        THERMALBOILER(RecipeMaps.thermalBoilerRecipes),
        SOLARTOWER(RecipeMaps.solarTowerRecipes),
        CYCLOTRON(RecipeMaps.cyclotronRecipes),
        FISHPOND(RecipeMaps.fishPondRecipes),
        CENTRIFUGENONCELL(RecipeMaps.centrifugeNonCellRecipes),
        ELECTROLYZERNONCELL(RecipeMaps.electrolyzerNonCellRecipes),
        MIXERNONCELL(RecipeMaps.mixerNonCellRecipes),
        CHEMICALDEHYDRATORNONCELL(RecipeMaps.chemicalDehydratorNonCellRecipes),
        SEMIFLUIDFUELS(RecipeMaps.semiFluidFuels),
        FLOTATIONCELL(RecipeMaps.flotationCellRecipes),
        EYEOFHARMONY(TecTechRecipeMaps.eyeOfHarmonyRecipes),
        GODFORGEPLASMA(TecTechRecipeMaps.godforgePlasmaRecipes),
        GODFORGEEXOTICMATTER(TecTechRecipeMaps.godforgeExoticMatterRecipes),
        GODFORGEMOLTEN(TecTechRecipeMaps.godforgeMoltenRecipes),
        PRECISEASSEMBLERRECIPES(GoodGeneratorRecipeMaps.preciseAssemblerRecipes),
        FUSIONCRAFTING(DEFCRecipes.fusionCraftingRecipes),
        BIOLAB(BartWorksRecipeMaps.bioLabRecipes),
        BACTERIALVAT(BartWorksRecipeMaps.bacterialVatRecipes),
        ACIDGENFUELS(BartWorksRecipeMaps.acidGenFuels),
        CIRCUITASSEMBLYLINE(BartWorksRecipeMaps.circuitAssemblyLineRecipes),
        ELECTRICIMPLOSIONCOMPRESSOR(BartWorksRecipeMaps.electricImplosionCompressorRecipes),
        COMPONENTASSEMBLYLINERECIPES(GoodGeneratorRecipeMaps.componentAssemblyLineRecipes),
        EXTREMEHEATEXCHANGERFUELS(GoodGeneratorRecipeMaps.extremeHeatExchangerFuels),
        NEUTRONACTIVATORRECIPES(GoodGeneratorRecipeMaps.neutronActivatorRecipes),
        NAQUADAHFUELREFINEFACTORYRECIPES(GoodGeneratorRecipeMaps.naquadahFuelRefineFactoryRecipes),
        NAQUADAHREACTORFUELS(GoodGeneratorRecipeMaps.naquadahReactorFuels),
        DIGESTERRECIPES(LanthanidesRecipeMaps.digesterRecipes),
        DISSOLUTIONTANKRECIPES(LanthanidesRecipeMaps.dissolutionTankRecipes),
        ASSEMBLYLINEVISUALRECIPES(RecipeMaps.assemblylineVisualRecipes),
        CANNERRECIPES(RecipeMaps.cannerRecipes),
        GASTURBINEFUELS(RecipeMaps.gasTurbineFuels);

        private final RecipeMap<?> recipeMap;

        MachineMode(RecipeMap<?> recipeMap) {
            this.recipeMap = recipeMap;
        }

        public int getId() {
            return ordinal();
        }

        public String getLocalizationKey() {
            return "NineIndustrialMultiMachine_Mode_" + ordinal();
        }

        public static final MachineMode[] VALUES = values();
        public static final Int2ObjectMap<MachineMode> BY_ID = new Int2ObjectOpenHashMap<>(VALUES.length);
        public static final Collection<RecipeMap<?>> ALL_RECIPE_MAPS = new ObjectArrayList<>(VALUES.length);

        static {
            for (MachineMode mode : VALUES) {
                BY_ID.put(mode.ordinal(), mode);
                ALL_RECIPE_MAPS.add(mode.recipeMap);
            }
        }

        public static MachineMode fromId(int id) {
            return BY_ID.get(id);
        }

        public static Collection<RecipeMap<?>> getAllRecipeMaps() {
            return ALL_RECIPE_MAPS;
        }
    }

}
