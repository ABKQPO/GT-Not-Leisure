/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package com.science.gtnl.common.crafting.quantum.crafting.inv;

import java.util.Map;

import com.google.common.collect.Iterables;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;

import appeng.api.config.FuzzyMode;

/**
 * Currently, extracts the whole network contents when the job starts. Lazily extracting is unfortunately not possible
 * as long as the crafting simulation operates from a separate thread: any world access from this thread will deadlock
 * the server.
 */
public class NetworkCraftingSimulationState extends CraftingSimulationState {

    /**
     * Networks with at most this many distinct available keys are copied in full: the copy is cheap and avoids the
     * graph-peek/subset machinery entirely.
     */
    public static final int SNAPSHOT_SUBSET_THRESHOLD = 512;

    private final KeyCounter list;

    /**
     * @param copy a copy of the network inventory, which the state then owns
     */
    public NetworkCraftingSimulationState(KeyCounter copy) {
        this.list = copy;
    }

    /**
     * Number of entries in the snapshot, for performance logging.
     */
    public int getSnapshotEntryCount() {
        return this.list.size();
    }

    @Override
    protected long simulateExtractParent(AEKey what) {
        return Math.min(list.get(what), Long.MAX_VALUE);
    }

    @Override
    protected Iterable<AEKey> findFuzzyParent(AEKey input) {
        return Iterables.transform(list.findFuzzy(input, FuzzyMode.IGNORE_ALL), Map.Entry::getKey);
    }
}
