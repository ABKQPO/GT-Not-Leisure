package com.science.gtnl.api;

import gregtech.api.interfaces.metatileentity.IFluidLockableMui2;

/**
 * Implement this interface if your MetaTileEntity supports fluid lock mechanism.
 */
@SuppressWarnings({ "BooleanMethodIsAlwaysInverted" })
public interface IFluidsLockable extends IFluidLockableMui2 {

    /**
     * Multi-fluid version: replace all locked fluid names.
     */
    void setLockedFluidNames(String[] names);

    /**
     * Multi-fluid version: replace all locked fluid names.
     */
    void setLockedFluidNames(int index, String names);

    /**
     * Returns all locked fluid names.
     */
    String[] getLockedFluidNames();

    /**
     * Returns locked fluid name.
     */
    String getLockedFluidNames(int index);

    /**
     * Set fluid lock state. Useful when you don't necessarily want to change mode when locked fluid is
     * changed.
     */
    void lockFluids(boolean lock, int index);

    boolean isFluidsLocked();

    /**
     * Check whether a given fluid name is accepted by the lock.
     */
    boolean acceptsFluidsLock(String[] name);

    /**
     * Check whether a given fluid name is accepted by the lock.
     */
    boolean acceptsFluidsLock(String name, int index);
}
