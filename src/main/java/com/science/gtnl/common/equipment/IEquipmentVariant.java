package com.science.gtnl.common.equipment;

public interface IEquipmentVariant {

    /**
     * Lower-case identifier used in language keys, texture names and NBT. Must never change once released.
     */
    String id();

    /**
     * Item damage value of this variant. Must never change once released; new variants take a new value.
     */
    int meta();

    /**
     * How many modules of this variant may be installed in one host at the same time.
     */
    default int limit() {
        return 1;
    }
}
