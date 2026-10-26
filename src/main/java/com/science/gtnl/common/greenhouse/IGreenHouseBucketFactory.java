package com.science.gtnl.common.greenhouse;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.api.IGreenHouse;

public interface IGreenHouseBucketFactory {

    String getNBTIdentifier();

    GreenHouseBucket tryCreateBucket(IGreenHouse greenhouse, ItemStack stack);

    GreenHouseBucket restore(NBTTagCompound nbt);
}
