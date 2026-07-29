package com.science.gtnl.api.stellar;

public class StellarIrisUpgradeCatalog {

    public static boolean defaultsRegistered;

    public static void registerDefaults() {
        if (defaultsRegistered) {
            return;
        }
        defaultsRegistered = true;
        registerBranches();
        registerIgnition();
        registerFusion();
        registerCollapse();
        registerVoid();
        registerRepeatables();
    }

    public static void registerBranches() {
        StellarIrisUpgradeRegistry.registerBranch(
            new StellarIrisUpgradeBranch("ignition", "gtnl.stellar_iris.branch.ignition", -2.3561945F, false));
        StellarIrisUpgradeRegistry.registerBranch(
            new StellarIrisUpgradeBranch("fusion", "gtnl.stellar_iris.branch.fusion", -0.7853982F, false));
        StellarIrisUpgradeRegistry.registerBranch(
            new StellarIrisUpgradeBranch("collapse", "gtnl.stellar_iris.branch.collapse", 2.3561945F, false));
        StellarIrisUpgradeRegistry
            .registerBranch(new StellarIrisUpgradeBranch("void", "gtnl.stellar_iris.branch.void", 0.7853982F, false));
        StellarIrisUpgradeRegistry.registerBranch(
            new StellarIrisUpgradeBranch("repeatable", "gtnl.stellar_iris.branch.repeatable", -1.5707964F, true));
    }

    public static void registerIgnition() {
        register("thermal_stabilizer", "ignition", 1, 15, 1);
        register("plasma_conduits", "ignition", 1, 15, 1);
        register("fusion_catalyst", "ignition", 2, 25, 1, "thermal_stabilizer", "plasma_conduits");
        register("magnetic_confinement", "ignition", 2, 25, 1, "thermal_stabilizer", "plasma_conduits");
        register("core_harmonics", "ignition", 3, 40, 1, "magnetic_confinement");
        register("proton_recycler", "ignition", 3, 40, 1, "fusion_catalyst");
        register("stellar_regeneration", "ignition", 4, 60, 1, "core_harmonics", "proton_recycler");
        register("eternal_ember", "ignition", 4, 60, 1, "core_harmonics", "proton_recycler");
        register("phoenix_protocol", "ignition", 5, 80, 1, "stellar_regeneration");
        register("solar_dominion", "ignition", 5, 80, 1, "eternal_ember");
        register("perpetual_ignition", "ignition", 6, 100, 1, "phoenix_protocol", "solar_dominion");
        register("supernova_core", "ignition", 7, 130, 1, "perpetual_ignition");
        register("plasma_hurricane", "ignition", 7, 130, 1, "perpetual_ignition");
        register("stellar_nursery", "ignition", 8, 170, 1, "supernova_core");
        register("corona_expansion", "ignition", 8, 170, 1, "plasma_hurricane");
        register("helios_forge", "ignition", 9, 220, 1, "stellar_nursery", "corona_expansion");
        register("fusion_overdrive", "ignition", 9, 220, 1, "stellar_nursery", "corona_expansion");
        register("dyson_lattice", "ignition", 10, 280, 1, "helios_forge");
        register("solar_genesis", "ignition", 10, 280, 1, "fusion_overdrive");
        register("primordial_flame", "ignition", 11, 350, 1, "dyson_lattice", "solar_genesis");
    }

    public static void registerFusion() {
        register("graviton_lens", "fusion", 1, 15, 1);
        register("superconducting_grid", "fusion", 1, 15, 1);
        register("temporal_acceleration", "fusion", 2, 25, 1, "graviton_lens", "superconducting_grid");
        register("parallel_manifold", "fusion", 2, 25, 1, "graviton_lens", "superconducting_grid");
        register("stellar_compression", "fusion", 3, 40, 1, "temporal_acceleration");
        register("mass_efficiency", "fusion", 3, 40, 1, "superconducting_grid", "parallel_manifold");
        register("relativistic_processing", "fusion", 4, 60, 1, "stellar_compression");
        register("quantum_tunneling", "fusion", 4, 60, 1, "stellar_compression", "mass_efficiency");
        register("hyperdense_core", "fusion", 5, 80, 1, "relativistic_processing");
        register("tachyon_weave", "fusion", 5, 80, 1, "quantum_tunneling");
        register("singularity_engine", "fusion", 6, 100, 1, "hyperdense_core", "tachyon_weave");
        register("neutron_cascade", "fusion", 7, 130, 1, "singularity_engine");
        register("warp_field_matrix", "fusion", 7, 130, 1, "singularity_engine");
        register("particle_storm", "fusion", 8, 170, 1, "neutron_cascade");
        register("subspace_harmonics", "fusion", 8, 170, 1, "warp_field_matrix");
        register("antimatter_injection", "fusion", 9, 220, 1, "particle_storm", "subspace_harmonics");
        register("zero_point_tap", "fusion", 9, 220, 1, "particle_storm", "subspace_harmonics");
        register("quark_gluon_plasma", "fusion", 10, 280, 1, "antimatter_injection");
        register("planck_resonance", "fusion", 10, 280, 1, "zero_point_tap");
        register("omega_compression", "fusion", 11, 350, 1, "quark_gluon_plasma", "planck_resonance");
    }

    public static void registerCollapse() {
        register("shard_collector", "collapse", 1, 15, 1);
        register("resonant_sacrifice", "collapse", 1, 15, 1);
        register("early_harvest", "collapse", 2, 25, 1, "shard_collector", "resonant_sacrifice");
        register("efficient_consumption", "collapse", 2, 25, 1, "shard_collector", "resonant_sacrifice");
        register("point_amplifier", "collapse", 3, 40, 1, "shard_collector", "early_harvest");
        register("dual_sacrifice", "collapse", 3, 40, 1, "resonant_sacrifice", "efficient_consumption");
        register("prestige_momentum", "collapse", 4, 60, 1, "point_amplifier");
        register("echo_of_collapse", "collapse", 5, 80, 1, "prestige_momentum");
        register("entropy_harvest", "collapse", 5, 80, 1, "dual_sacrifice");
        register("infinite_recursion", "collapse", 6, 100, 1, "echo_of_collapse", "entropy_harvest");
        register("cascading_collapse", "collapse", 7, 130, 1, "infinite_recursion");
        register("temporal_echo", "collapse", 7, 130, 1, "infinite_recursion");
        register("mass_conversion", "collapse", 8, 170, 1, "cascading_collapse");
        register("stellar_debt", "collapse", 8, 170, 1, "temporal_echo");
        register("entropy_engine", "collapse", 9, 220, 1, "mass_conversion", "stellar_debt");
        register("sacrifice_amplifier", "collapse", 9, 220, 1, "mass_conversion", "stellar_debt");
        register("cosmic_tithe", "collapse", 10, 280, 1, "entropy_engine");
        register("annihilation_yield", "collapse", 10, 280, 1, "sacrifice_amplifier");
        register("heat_death", "collapse", 11, 350, 1, "cosmic_tithe", "annihilation_yield");
    }

    public static void registerVoid() {
        register("hawking_radiator", "void", 1, 15, 1);
        register("chromatic_tuning", "void", 1, 15, 1);
        register("exotic_matter_tap", "void", 2, 25, 1, "hawking_radiator", "chromatic_tuning");
        register("void_whispers", "void", 2, 25, 1, "hawking_radiator");
        register("event_horizon_lock", "void", 3, 40, 1, "void_whispers");
        register("singularity_siphon", "void", 3, 40, 1, "exotic_matter_tap");
        register("gravitational_mastery", "void", 4, 60, 1, "singularity_siphon");
        register("void_harvester", "void", 4, 60, 1, "event_horizon_lock");
        register("eldritch_insight", "void", 5, 80, 1, "gravitational_mastery");
        register("abyss_walker", "void", 5, 80, 1, "void_harvester");
        register("eternal_void", "void", 6, 100, 1, "eldritch_insight", "abyss_walker");
        register("dark_matter_lens", "void", 7, 130, 1, "eternal_void");
        register("negative_mass", "void", 7, 130, 1, "eternal_void");
        register("vacuum_decay", "void", 8, 170, 1, "dark_matter_lens");
        register("photon_sphere", "void", 8, 170, 1, "negative_mass");
        register("schwarzschild_radius", "void", 9, 220, 1, "vacuum_decay", "photon_sphere");
        register("ergosphere_tap", "void", 9, 220, 1, "vacuum_decay", "photon_sphere");
        register("penrose_process", "void", 10, 280, 1, "schwarzschild_radius");
        register("kerr_extraction", "void", 10, 280, 1, "ergosphere_tap");
        register("false_vacuum", "void", 11, 350, 1, "penrose_process", "kerr_extraction");
    }

    public static void registerRepeatables() {
        register("stellar_efficiency", "repeatable", 0, 10, 10);
        register("parallel_threading", "repeatable", 0, 15, 8);
        register("energy_optimization", "repeatable", 0, 12, 10);
        register("fuel_efficiency", "repeatable", 0, 10, 10);
        register("prestige_amplifier", "repeatable", 0, 8, 10);
        register("decay_resistance", "repeatable", 0, 10, 10);
        register("growth_catalyst", "repeatable", 0, 10, 10);
        register("void_attunement", "repeatable", 0, 12, 10);
    }

    public static void register(String id, String branchId, int row, int cost, int maxLevel, String... prerequisites) {
        StellarIrisUpgradeDefinition.Builder builder = StellarIrisUpgradeDefinition.builder(id)
            .branch(branchId)
            .row(row)
            .baseCost(cost)
            .maxLevel(maxLevel);
        for (String prerequisite : prerequisites) {
            builder.prerequisite(prerequisite);
        }
        StellarIrisUpgradeRegistry.registerUpgrade(builder.build());
    }
}
