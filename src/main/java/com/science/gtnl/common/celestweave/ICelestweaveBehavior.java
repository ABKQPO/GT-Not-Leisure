package com.science.gtnl.common.celestweave;

public interface ICelestweaveBehavior {

    /**
     * Called every tick on both sides; check isRemote where it matters.
     */
    default void tick(CelestweaveContext ctx) {}

    /**
     * Called on both sides after the piece is put on, the player logs in or respawns, or the module is enabled.
     */
    default void equip(CelestweaveContext ctx) {}

    /**
     * Called on both sides when the piece is taken off, or the module is disabled or removed.
     */
    default void unequip(CelestweaveContext ctx) {}
}
