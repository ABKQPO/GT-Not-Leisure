package com.science.gtnl.common.crafting.quantum.api.networking.storage;

import java.util.Collection;
import java.util.Set;

import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;

public interface IStorageService {

    int getInventorySize();

    KeyCounter copyInventory();

    KeyCounter copyInventory(Collection<AEKey> keys, Set<AEKey> fuzzyKeys);
}
