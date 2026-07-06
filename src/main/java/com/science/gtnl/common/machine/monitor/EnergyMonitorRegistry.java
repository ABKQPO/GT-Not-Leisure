package com.science.gtnl.common.machine.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.science.gtnl.common.machine.basicMachine.EnergyMonitor;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

public class EnergyMonitorRegistry {

    private static final Set<MetaTileEntity> TRACKED = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void register(MetaTileEntity metaTileEntity) {
        if (!shouldTrack(metaTileEntity)) {
            return;
        }
        TRACKED.add(metaTileEntity);
    }

    public static void unregister(MetaTileEntity metaTileEntity) {
        if (metaTileEntity == null) {
            return;
        }
        TRACKED.remove(metaTileEntity);
    }

    public static List<MetaTileEntity> snapshot() {
        return new ArrayList<>(TRACKED);
    }

    public static void cleanupInvalidEntries() {
        TRACKED.removeIf(metaTileEntity -> !shouldTrack(metaTileEntity) || !metaTileEntity.isValid());
    }

    public static boolean isInvalid(MetaTileEntity metaTileEntity) {
        return !shouldTrack(metaTileEntity) || !metaTileEntity.isValid();
    }

    private static boolean shouldTrack(MetaTileEntity metaTileEntity) {
        if (metaTileEntity == null || metaTileEntity instanceof EnergyMonitor) {
            return false;
        }
        if (metaTileEntity.getBaseMetaTileEntity() == null || metaTileEntity.getBaseMetaTileEntity()
            .getWorld() == null
            || metaTileEntity.getBaseMetaTileEntity()
                .getWorld().isRemote) {
            return false;
        }
        return metaTileEntity instanceof MTEBasicMachine || metaTileEntity instanceof MTEHatch
            || metaTileEntity instanceof MTEMultiBlockBase;
    }
}
