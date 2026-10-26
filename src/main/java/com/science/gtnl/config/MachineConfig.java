package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultDouble;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.Name;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;

@Sync
@Comment("Machine section")
public class MachineConfig {

    @Comment("Default energy consumption per operation (in AE/t)")
    @LangKey("gtnl.gui.config.general.machine.beam_former_energy_consume")
    @DefaultDouble(10)
    public double beamFormerEnergyConsume = 10;

    @Comment("Default maximum beam length (in blocks)")
    @LangKey("gtnl.gui.config.general.machine.beam_former_length")
    @DefaultInt(256)
    public int beamFormerLength = 256;

    @Comment("Allow interaction with machines from their main facing (similar to GTM behavior)")
    @LangKey("gtnl.gui.config.general.machine.allow_main_facing_interaction")
    @DefaultBoolean(false)
    public boolean allowMainFacingInteraction = false;

    @Comment("Set Machine Can Use Laser Hatch")
    @Name("enableLaserHatch")
    @DefaultBoolean(false)
    public boolean enableLaserHatch = false;

    @Comment("Enable Purification Plant Buff, add Wireless mode and max long parallels")
    @DefaultBoolean(false)
    @RequiresMcRestart
    public boolean enablePurificationPlantBuff = false;

    @Comment("Enable Output Change Function")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public boolean enableRecipeOutputChance = true;

    @Comment("Enable Void Miner Tweak, allows you to override target dimension that Void Miner mines")
    @DefaultBoolean(true)
    public boolean enableVoidMinerTweak = true;

    @Comment("Change Recipe Item Output, like QFT")
    @DefaultDouble(2.5)
    public double recipeOutputChance = 2.5;

    @Name("quantum_computer")
    public final QuantumComputer quantum_computer = new QuantumComputer();

    @Comment("Quantum Computer")
    public static class QuantumComputer {

        @Comment("Maximum size allowed for a Quantum Computer multiblock structure")
        @RangeInt(min = 3)
        @DefaultInt(7)
        public int maxMultiblockSize = 7;

        @Comment("Maximum number of multi threader allowed in a Quantum Computer")
        @RangeInt(min = 1)
        @DefaultInt(1)
        public int maxMultiThreader = 1;

        @Comment("Maximum number of data entangler allowed in a Quantum Computer")
        @RangeInt(min = 1)
        @DefaultInt(1)
        public int maxDataEntangler = 1;

        @Comment("Log the step breakdown of every quantum crafting calculation")
        @DefaultBoolean(false)
        public boolean enableCalculationLog = false;

        @Comment("Log stage timings and counters of every quantum crafting calculation")
        @DefaultBoolean(false)
        public boolean enableCalculationPerformanceLog = false;

        @Comment("Seconds of calculation time a quantum crafting plan with missing items may spend on trying alternative patterns one by one, after which the best plan found so far is kept. 0 never tries them")
        @RangeInt(min = 0)
        @DefaultInt(5)
        public int calculationFallbackSeconds = 5;

        @Comment("Seconds of calculation time one quantum crafting calculation may take on a dedicated server before it fails, like MaxCraftingSteps of AE2. 0 disables the limit")
        @RangeInt(min = 0)
        @DefaultInt(30)
        public int calculationLimitSeconds = 30;
    }

    @Name("meteor_miner")
    public final MeteorMiner meteor_miner = new MeteorMiner();

    @Comment("Meteor Miner")
    public static class MeteorMiner {

        @Comment("Set the Meteor Miner how many every cycle break a block")
        @DefaultInt(1)
        @RequiresMcRestart
        public int meteorMinerMaxBlockPerCycle = 1;

        @Comment("Set the Meteor Miner how many every cycle break row blocks")
        @DefaultInt(1)
        @RequiresMcRestart
        public int meteorMinerMaxRowPerCycle = 1;
    }

    @Name("artificial_star")
    public final ArtificialStar artificial_star = new ArtificialStar();

    @Comment("Artificial Star")
    public static class ArtificialStar {

        @Comment("Set the power generation of EU Every Depleted Excited Naquadah FuelRod")
        @DefaultInt(4125000)
        @RequiresMcRestart
        public int euEveryDepletedExcitedNaquadahFuelRod = 4125000;

        @Comment("Open RenderDefaultArtificialStar rendering")
        @Name("EnableDefaultRender")
        @DefaultBoolean(true)
        public boolean enableRenderDefaultArtificialStar = true;

        @Comment("Set the power generation of EU Every Enhancement Core")
        @DefaultInt(100)
        @RequiresMcRestart
        public int euEveryEnhancementCore = 100;

        @Comment("Set secondsOfArtificialStarProgressCycleTime running time")
        @DefaultDouble(6.4)
        public double secondsOfArtificialStarProgressCycleTime = 6.4;
    }

    @Name("eternal_gregtech_workshop")
    public final EternalWorkshop eternal_gregtech_workshop = new EternalWorkshop();

    @Comment("Eternal Workshop")
    public static class EternalWorkshop {

        @Comment("Enable Eternal GregTech Workshop Spiral Render, like DNA")
        @DefaultBoolean(false)
        public boolean spiralRender = false;
    }

    @Name("portal_to_alfheim")
    public final PortalAlfheim portal_to_alfheim = new PortalAlfheim();

    @Comment("Portal Alfheim")
    public static class PortalAlfheim {

        @Comment("Setting this to false will reduce the Portal To Alfheim explosion to little more then a tnt blast")
        @DefaultBoolean(true)
        public boolean bigBoom = true;
    }
}
