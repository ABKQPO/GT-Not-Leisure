package com.science.gtnl.utils.machine;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import bartworks.API.recipe.BartWorksRecipeMaps;
import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kubatech.loaders.DEFCRecipes;
import lombok.Getter;
import tectech.recipe.TecTechRecipeMaps;

public class NineIndustrialMultiMachineManager {

    public static String getModeLocalization(int machineMode) {
        MachineMode mode = MachineMode.fromId(machineMode);
        return mode != null ? mode.getLocalizationKey() : "";
    }

    public int getNextMachineMode(int currentMode) {
        MachineMode mode = MachineMode.fromId(currentMode);
        if (mode == null) return 0;
        MachineMode[] modes = MachineMode.values();
        return modes[(mode.ordinal() + 1) % modes.length].getId();
    }

    public static Collection<RecipeMap<?>> getAllRecipeMaps() {
        return MachineMode.getAllRecipeMaps();
    }

    public static RecipeMap<?> getRecipeMap(int aMode) {
        MachineMode mode = MachineMode.fromId(aMode);
        return mode != null ? mode.getRecipeMap() : null;
    }

    public static int getModeMapIndex(int machineMode, int column) {
        int totalModes = MachineMode.values().length;
        int row = machineMode / 3;
        int col = column % 3;
        if (row * 3 + col >= totalModes) return -1;
        return row * 3 + col;
    }

    @Getter
    public enum MachineMode {

        COMPRESSOR(0, RecipeMaps.compressorRecipes),
        LATHE(1, RecipeMaps.latheRecipes),
        MAGNETIC(2, RecipeMaps.polarizerRecipes),
        FERMENTER(3, RecipeMaps.fermentingRecipes),
        FLUIDEXTRACT(4, RecipeMaps.fluidExtractionRecipes),
        EXTRACTOR(5, RecipeMaps.extractorRecipes),
        LASER(6, RecipeMaps.laserEngraverRecipes),
        AUTOCLAVE(7, RecipeMaps.autoclaveRecipes),
        FLUIDSOLIDIFY(8, RecipeMaps.fluidSolidifierRecipes),
        OREWASHER(9, RecipeMaps.oreWasherRecipes),
        THERMALCENTRIFUGE(10, RecipeMaps.thermalCentrifugeRecipes),
        NEUTRONIUMCOMPRESSOR(11, RecipeMaps.neutroniumCompressorRecipes),
        RECYCLER(12, RecipeMaps.recyclerRecipes),
        FURNACE(13, RecipeMaps.furnaceRecipes),
        MICROWAVE(14, RecipeMaps.microwaveRecipes),
        REPLICATOR(15, RecipeMaps.replicatorRecipes),
        PLASMAARCFURNACE(16, RecipeMaps.plasmaArcFurnaceRecipes),
        ARCFURNACE(17, RecipeMaps.arcFurnaceRecipes),
        PRINTER(18, RecipeMaps.printerRecipes),
        SIFTER(19, RecipeMaps.sifterRecipes),
        FORMINGPRESS(20, RecipeMaps.formingPressRecipes),
        MACERATOR(21, RecipeMaps.maceratorRecipes),
        CHEMICALBATH(22, RecipeMaps.chemicalBathRecipes),
        FLUIDCANNER(23, RecipeMaps.fluidCannerRecipes),
        BREWING(24, RecipeMaps.brewingRecipes),
        FLUIDHEATER(25, RecipeMaps.fluidHeaterRecipes),
        DISTILLERY(26, RecipeMaps.distilleryRecipes),
        PACKAGER(27, RecipeMaps.packagerRecipes),
        UNPACKAGER(28, RecipeMaps.unpackagerRecipes),
        FUSION(29, RecipeMaps.fusionRecipes),
        BLASTFURNACE(30, RecipeMaps.blastFurnaceRecipes),
        PLASMAFORGE(31, RecipeMaps.plasmaForgeRecipes),
        TRANSCENDENTPLASMAMIXER(32, RecipeMaps.transcendentPlasmaMixerRecipes),
        PRIMITIVEBLAST(33, RecipeMaps.primitiveBlastRecipes),
        IMPLOSION(34, RecipeMaps.implosionRecipes),
        VACUUMFREEZER(35, RecipeMaps.vacuumFreezerRecipes),
        MULTIBLOCKCHEMICALREACTOR(36, RecipeMaps.multiblockChemicalReactorRecipes),
        DISTILLATIONTOWER(37, RecipeMaps.distillationTowerRecipes),
        CRACKING(38, RecipeMaps.crackingRecipes),
        PYROLYSE(39, RecipeMaps.pyrolyseRecipes),
        WIREMILL(40, RecipeMaps.wiremillRecipes),
        BENDER(41, RecipeMaps.benderRecipes),
        ALLOYSMELTER(42, RecipeMaps.alloySmelterRecipes),
        ASSEMBLER(43, RecipeMaps.assemblerRecipes),
        CIRCUITASSEMBLER(44, RecipeMaps.circuitAssemblerRecipes),
        CUTTER(45, RecipeMaps.cutterRecipes),
        SLICER(46, RecipeMaps.slicerRecipes),
        EXTRUDER(47, RecipeMaps.extruderRecipes),
        HAMMER(48, RecipeMaps.hammerRecipes),
        AMPLIFIER(49, RecipeMaps.amplifierRecipes),
        EXTREMEDIESELFUELS(50, RecipeMaps.extremeDieselFuels),
        HOTFUELS(51, RecipeMaps.hotFuels),
        DENSELIQUIDFUELS(52, RecipeMaps.denseLiquidFuels),
        PLASMAFUELS(53, RecipeMaps.plasmaFuels),
        MAGICFUELS(54, RecipeMaps.magicFuels),
        SMALLNAQUADAHREACTORFUELS(55, RecipeMaps.smallNaquadahReactorFuels),
        LARGENAQUADAHREACTORFUELS(56, RecipeMaps.largeNaquadahReactorFuels),
        HUGENAQUADAHREACTORFUELS(57, RecipeMaps.hugeNaquadahReactorFuels),
        EXTREMENAQUADAHREACTORFUELS(58, RecipeMaps.extremeNaquadahReactorFuels),
        ULTRAHUGENAQUADAHREACTORFUELS(59, RecipeMaps.ultraHugeNaquadahReactorFuels),
        NANOFORGE(60, RecipeMaps.nanoForgeRecipes),
        PCBFACTORY(61, RecipeMaps.pcbFactoryRecipes),
        COKEOVEN(62, GTPPRecipeMaps.cokeOvenRecipes),
        ROCKETFUELS(63, GTPPRecipeMaps.rocketFuels),
        QUANTUMFORCETRANSFORMER(64, GTPPRecipeMaps.quantumForceTransformerRecipes),
        VACUUMFURNACE(65, GTPPRecipeMaps.vacuumFurnaceRecipes),
        ALLOYBLASTSMELTER(66, GTPPRecipeMaps.alloyBlastSmelterRecipes),
        LIQUIDFLUORINETHORIUMREACTOR(67, GTPPRecipeMaps.liquidFluorineThoriumReactorRecipes),
        NUCLEARSALTPROCESSINGPLANT(68, GTPPRecipeMaps.nuclearSaltProcessingPlantRecipes),
        MILLING(69, GTPPRecipeMaps.millingRecipes),
        FISSIONFUELPROCESSING(70, GTPPRecipeMaps.fissionFuelProcessingRecipes),
        COLDTRAP(71, GTPPRecipeMaps.coldTrapRecipes),
        REACTORPROCESSINGUNIT(72, GTPPRecipeMaps.reactorProcessingUnitRecipes),
        SIMPLEWASHER(73, GTPPRecipeMaps.simpleWasherRecipes),
        MOLECULARTRANSFORMER(74, GTPPRecipeMaps.molecularTransformerRecipes),
        CHEMICALPLANT(75, GTPPRecipeMaps.chemicalPlantRecipes),
        RTG(76, GTPPRecipeMaps.rtgFuels),
        THERMALBOILER(77, GTPPRecipeMaps.thermalBoilerRecipes),
        SOLARTOWER(78, GTPPRecipeMaps.solarTowerRecipes),
        CYCLOTRON(79, GTPPRecipeMaps.cyclotronRecipes),
        FISHPOND(80, GTPPRecipeMaps.fishPondRecipes),
        CENTRIFUGENONCELL(81, GTPPRecipeMaps.centrifugeNonCellRecipes),
        ELECTROLYZERNONCELL(82, GTPPRecipeMaps.electrolyzerNonCellRecipes),
        MIXERNONCELL(83, GTPPRecipeMaps.mixerNonCellRecipes),
        CHEMICALDEHYDRATORNONCELL(84, GTPPRecipeMaps.chemicalDehydratorNonCellRecipes),
        SEMIFLUIDFUELS(85, GTPPRecipeMaps.semiFluidFuels),
        FLOTATIONCELL(86, GTPPRecipeMaps.flotationCellRecipes),
        EYEOFHARMONY(87, TecTechRecipeMaps.eyeOfHarmonyRecipes),
        GODFORGEPLASMA(88, TecTechRecipeMaps.godforgePlasmaRecipes),
        GODFORGEEXOTICMATTER(89, TecTechRecipeMaps.godforgeExoticMatterRecipes),
        GODFORGEMOLTEN(90, TecTechRecipeMaps.godforgeMoltenRecipes),
        PRECISEASSEMBLERRECIPES(91, GoodGeneratorRecipeMaps.preciseAssemblerRecipes),
        FUSIONCRAFTING(92, DEFCRecipes.fusionCraftingRecipes),
        BIOLAB(93, BartWorksRecipeMaps.bioLabRecipes),
        BACTERIALVAT(94, BartWorksRecipeMaps.bacterialVatRecipes),
        ACIDGENFUELS(95, BartWorksRecipeMaps.acidGenFuels),
        CIRCUITASSEMBLYLINE(96, BartWorksRecipeMaps.circuitAssemblyLineRecipes),
        ELECTRICIMPLOSIONCOMPRESSOR(97, BartWorksRecipeMaps.electricImplosionCompressorRecipes),
        COMPONENTASSEMBLYLINERECIPES(98, GoodGeneratorRecipeMaps.componentAssemblyLineRecipes),
        EXTREMEHEATEXCHANGERFUELS(99, GoodGeneratorRecipeMaps.extremeHeatExchangerFuels),
        NEUTRONACTIVATORRECIPES(100, GoodGeneratorRecipeMaps.neutronActivatorRecipes),
        NAQUADAHFUELREFINEFACTORYRECIPES(101, GoodGeneratorRecipeMaps.naquadahFuelRefineFactoryRecipes),
        NAQUADAHREACTORFUELS(102, GoodGeneratorRecipeMaps.naquadahReactorFuels),
        DIGESTERRECIPES(103, LanthanidesRecipeMaps.digesterRecipes),
        DISSOLUTIONTANKRECIPES(104, LanthanidesRecipeMaps.dissolutionTankRecipes),
        ASSEMBLYLINEVISUALRECIPES(105, RecipeMaps.assemblylineVisualRecipes),
        CANNERRECIPES(106, RecipeMaps.cannerRecipes),
        GASTURBINEFUELS(107, RecipeMaps.gasTurbineFuels);

        private final int id;
        private final RecipeMap<?> recipeMap;

        MachineMode(int id, RecipeMap<?> recipeMap) {
            this.id = id;
            this.recipeMap = recipeMap;
        }

        public String getLocalizationKey() {
            return "NineIndustrialMultiMachine_Mode_" + id;
        }

        public static final Int2ObjectMap<MachineMode> BY_ID = new Int2ObjectOpenHashMap<>();

        static {
            for (MachineMode mode : values()) {
                BY_ID.put(mode.id, mode);
            }
        }

        public static MachineMode fromId(int id) {
            return BY_ID.get(id);
        }

        public static Collection<RecipeMap<?>> getAllRecipeMaps() {
            return Arrays.stream(values())
                .map(MachineMode::getRecipeMap)
                .collect(Collectors.toList());
        }
    }

}
