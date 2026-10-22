package com.science.gtnl.common.crafting.quantum.hooks.ticking;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.world.World;

import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;

public final class TickHandler {

    private static final TickHandler INSTANCE = new TickHandler();

    private final Set<CraftingCalculation> running = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private volatile long sessionEpoch;
    private volatile boolean accepting = true;

    public static TickHandler instance() {
        return INSTANCE;
    }

    public long getCraftingSessionEpoch() {
        return this.sessionEpoch;
    }

    public boolean registerCraftingSimulation(World world, CraftingCalculation calculation, long sessionEpoch) {
        if (!this.accepting || sessionEpoch != this.sessionEpoch) {
            calculation.cancel();
            return false;
        }
        this.running.add(calculation);
        return true;
    }

    public void unregisterCraftingSimulation(World world, CraftingCalculation calculation) {
        this.running.remove(calculation);
    }

    public void openSession() {
        this.sessionEpoch++;
        this.accepting = true;
    }

    public void closeSession() {
        this.accepting = false;
        this.sessionEpoch++;
        for (CraftingCalculation calculation : this.running.toArray(new CraftingCalculation[0])) {
            calculation.cancel();
        }
        this.running.clear();
    }
}
