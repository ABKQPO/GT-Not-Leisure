package com.gtnltest.runtime;

import java.util.List;

import com.science.gtnl.utils.enums.GTNLMachineID;

import gregtech.api.enums.Mods;
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
        cases.addAll(machines());
        cases.add(new SteamHatchScenario());
        cases.add(new SteamFurnaceProductionScenario());
        cases.add(new SteamWorldSaveScenario(false));
        return cases;
    }

    static List<MachinePersistenceScenario> machines() {
        List<MachinePersistenceScenario> cases = new ObjectArrayList<>();
        for (GTNLMachineID type : GTNLMachineID.values()) {
            int variants = switch (type) {
                case STAR_GATE_WIRE -> 12;
                case BLUE_ALLOY_PIPE, COMPRESSED_STEAM_PIPE, STRONZE_PIPE, BREEL_PIPE -> 5;
                default -> 1;
            };
            for (int variant = 0; variant < variants; variant++)
                cases.add(new MachinePersistenceScenario(type, variant));
        }
        return cases;
    }

    static String skipReason(RuntimeScenario scenario) {
        for (int variant = 6; variant < 12; variant++) {
            if (scenario.id()
                .equals("machine.star_gate_wire.variant_" + variant + ".persistence")
                && gregtech.api.GregTechAPI.METATILEENTITIES[GTNLMachineID.STAR_GATE_WIRE.ID + variant] == null)
                return "Insulated Stargate cable IDs are reserved; registration currently disables insulation";
        }
        if (scenario.id()
            .equals("machine.blood_soul_sacrificial_array.persistence") && !Mods.BloodMagic.isModLoaded())
            return "BloodMagic is not installed";
        if (scenario.id()
            .equals("machine.loot_bag_redemption.persistence") && !Mods.EnhancedLootBags.isModLoaded())
            return "EnhancedLootBags is not installed";
        return null;
    }
}
