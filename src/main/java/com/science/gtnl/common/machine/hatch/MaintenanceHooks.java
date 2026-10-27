package com.science.gtnl.common.machine.hatch;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IConfigurationMaintenance;

import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

public class MaintenanceHooks {

    private MaintenanceHooks() {}

    // The first configuration maintenance hatch of the machine scales the recipe duration.
    public static int configuredDuration(MTEMultiBlockBase machine, int duration) {
        try {
            for (MTEHatchMaintenance maintenance : machine.mMaintenanceHatches) {
                if (maintenance instanceof IConfigurationMaintenance customMaintenance
                    && customMaintenance.isConfiguration()) {
                    return (int) Math.max(1, duration * customMaintenance.getConfigTime() / 100.0);
                }
            }
        } catch (Exception e) {
            ScienceNotLeisure.LOG.warn("Error reading IConfigurationMaintenance from MTE: {}", machine, e);
        }
        return duration;
    }
}
