package com.science.gtnl.mixins.early.Minecraft;

import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.utils.GTNLNBTTagList;

@Mixin(NBTTagList.class)
public class MixinNBTTagList implements GTNLNBTTagList {

    @Shadow
    private List<NBTBase> tagList;

    @Override
    public @NotNull Iterator<NBTBase> iterator() {
        return tagList.iterator();
    }

    @Override
    public @NotNull NBTBase[] snl$getNbtList() {
        return tagList.toArray(new NBTBase[0]);
    }

}
