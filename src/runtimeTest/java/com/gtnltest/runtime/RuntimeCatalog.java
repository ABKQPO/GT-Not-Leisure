package com.gtnltest.runtime;

import java.util.List;

import com.science.gtnl.utils.enums.GTNLMachineID;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

final class RuntimeCatalog {

    private RuntimeCatalog() {}

    static List<RuntimeScenario> scenarios(String phase) {
        List<RuntimeScenario> cases = new ObjectArrayList<>();
        if ("restart".equals(phase)) {
            cases.add(new SteamWorldSaveScenario(true));
            return cases;
        }
        if (!"server".equals(phase)) throw new IllegalArgumentException("Unknown runtime phase: " + phase);
        for (MachineCatalogEntry machine : machines()) cases.add(new MachinePersistenceScenario(machine));
        cases.add(new SteamHatchScenario());
        cases.add(new SteamFurnaceProductionScenario());
        cases.add(new SteamWorldSaveScenario(false));
        return cases;
    }

    static List<MachineCatalogEntry> machines() {
        List<MachineCatalogEntry> cases = new ObjectArrayList<>();
        for (GTNLMachineID type : GTNLMachineID.values()) {
            int variants = switch (type) {
                case STAR_GATE_WIRE -> 12;
                case BLUE_ALLOY_PIPE, COMPRESSED_STEAM_PIPE, STRONZE_PIPE, BREEL_PIPE -> 5;
                default -> 1;
            };
            for (int variant = 0; variant < variants; variant++) cases.add(new MachineCatalogEntry(type, variant));
        }
        return cases;
    }

}
