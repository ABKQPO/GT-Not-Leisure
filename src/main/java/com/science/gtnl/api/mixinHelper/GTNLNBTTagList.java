package com.science.gtnl.api.mixinHelper;

import net.minecraft.nbt.NBTBase;

public interface GTNLNBTTagList extends Iterable<NBTBase> {

    NBTBase[] snl$getNbtList();
}
