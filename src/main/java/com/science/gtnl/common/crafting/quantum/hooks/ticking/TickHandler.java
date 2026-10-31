package com.science.gtnl.common.crafting.quantum.hooks.ticking;

import java.util.List;
import java.util.Set;

import net.minecraft.world.World;

import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class TickHandler {

    private static final TickHandler INSTANCE = new TickHandler();

    private final Object lock = new Object();
    private final Set<CraftingCalculation> running = new ReferenceOpenHashSet<>();
    private long sessionEpoch;
    private boolean accepting = true;

    public static TickHandler instance() {
        return INSTANCE;
    }

    public long getCraftingSessionEpoch() {
        synchronized (this.lock) {
            return this.sessionEpoch;
        }
    }

    public boolean isCurrentSession(long sessionEpoch) {
        synchronized (this.lock) {
            return this.accepting && sessionEpoch == this.sessionEpoch;
        }
    }

    public boolean registerCraftingSimulation(World world, CraftingCalculation calculation, long sessionEpoch) {
        synchronized (this.lock) {
            if (this.accepting && sessionEpoch == this.sessionEpoch) {
                this.running.add(calculation);
                return true;
            }
        }
        calculation.cancel();
        return false;
    }

    public void unregisterCraftingSimulation(World world, CraftingCalculation calculation) {
        synchronized (this.lock) {
            this.running.remove(calculation);
        }
    }

    public void openSession() {
        synchronized (this.lock) {
            this.sessionEpoch++;
            this.accepting = true;
        }
    }

    public void closeSession() {
        List<CraftingCalculation> cancelled;
        synchronized (this.lock) {
            this.accepting = false;
            this.sessionEpoch++;
            cancelled = new ObjectArrayList<>(this.running);
            this.running.clear();
        }
        // Cancel outside the lock: a cancelled calculation unregisters itself from its worker thread.
        for (CraftingCalculation calculation : cancelled) {
            calculation.cancel();
        }
    }
}
