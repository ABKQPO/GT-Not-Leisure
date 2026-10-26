package com.science.gtnl.api.mixinHelper;

/**
 * What GTNL's mixin adds to TecTech's Eye of Harmony so a linked GTNL injector can read and fill its stored fluids.
 */
public interface IEyeOfHarmonyControllerLink {

    void gtnl$unlinkController();

    long gtnl$getHydrogenStored();

    long gtnl$getHeliumStored();

    long gtnl$getStellarPlasmaStored();

    long gtnl$getAstralArrayAmount();

    long gtnl$getParallelAmount();

    void gtnl$setHydrogenStored(long amount);

    void gtnl$setHeliumStored(long amount);

    void gtnl$setStellarPlasmaStored(long amount);
}
