package com.science.gtnl.common.celestweave;

public interface CelestweaveBehavior {

    /**
     * 每 tick 调用，两端都会执行，需要时自行判断 isRemote。
     */
    default void tick(CelestweaveContext ctx) {}

    /**
     * 穿上、登录、重生或模块被启用后调用，两端都会执行。
     */
    default void equip(CelestweaveContext ctx) {}

    /**
     * 脱下、模块被停用或移除时调用，两端都会执行。
     */
    default void unequip(CelestweaveContext ctx) {}
}
