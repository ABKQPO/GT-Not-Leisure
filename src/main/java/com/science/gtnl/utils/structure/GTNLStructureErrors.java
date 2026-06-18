package com.science.gtnl.utils.structure;

import gregtech.api.structure.error.ErrorType;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.structure.error.TranslatableText;

public class GTNLStructureErrors {

    private static final TranslatableText PARALLEL_CONTROLLER_HATCH = TranslatableText
        .lang("GTNL.gui.text.structure_error.parallel_controller_hatch");

    private GTNLStructureErrors() {}

    public static StructureError parallelControllerHatchCount(ErrorType type, int current, int target) {
        return StructureErrors.hatchCount(type, PARALLEL_CONTROLLER_HATCH, current, target);
    }

    public static StructureError invalidHatchConfiguration() {
        return StructureErrors.of("GTNL.gui.text.structure_error.invalid_hatch_configuration");
    }

    public static StructureError invalidEnergyHatchConfiguration() {
        return StructureErrors.of("GTNL.gui.text.structure_error.invalid_energy_hatch_configuration");
    }

    public static StructureError laserEnergyTunnelDisabled() {
        return StructureErrors.of("GTNL.gui.text.structure_error.laser_energy_tunnel_disabled");
    }

    public static StructureError energyInputAmperageTooHigh() {
        return StructureErrors.of("GTNL.gui.text.structure_error.energy_input_amperage_too_high");
    }

    public static StructureError missingDistillationLayerOutputHatch() {
        return StructureErrors.of("GTNL.gui.text.structure_error.missing_distillation_layer_output_hatch");
    }

    public static StructureError unknownLegacyCheckFailure() {
        return StructureErrors.of("GTNL.gui.text.structure_error.legacy_check_failed");
    }
}
