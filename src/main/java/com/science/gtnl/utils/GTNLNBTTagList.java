package com.science.gtnl.utils;

import net.minecraft.nbt.NBTBase;

public interface GTNLNBTTagList extends Iterable<NBTBase> {

    NBTBase[] snl$getNbtList();
}
