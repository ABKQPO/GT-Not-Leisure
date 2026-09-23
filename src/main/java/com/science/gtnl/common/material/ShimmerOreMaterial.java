package com.science.gtnl.common.material;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SubTag;
import gregtech.api.enums.TextureSet;
import gregtech.api.interfaces.IOreMaterial;
import gregtech.api.interfaces.IStoneType;
import gregtech.api.interfaces.ISubTagContainer;
import gregtech.api.util.GTOreDictUnificator;

public final class ShimmerOreMaterial implements IOreMaterial {

    public static final String INTERNAL_NAME = "Shimmer";

    private static final List<IStoneType> VALID_STONES = new ArrayList<>();

    private final int id;
    private final TextureSet textureSet;
    private final short[] rgba;

    public ShimmerOreMaterial(int id, TextureSet textureSet, short[] rgba) {
        this.id = id;
        this.textureSet = textureSet;
        this.rgba = rgba.clone();
    }

    @Override
    public void addTooltips(List<String> tooltips) {}

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getInternalName() {
        return INTERNAL_NAME;
    }

    @Override
    public String getDefaultLocalName() {
        return getLocalizedName();
    }

    @Override
    public short[] getRGBA() {
        return rgba.clone();
    }

    @Override
    public TextureSet getTextureSet() {
        return textureSet;
    }

    @Override
    public List<IStoneType> getValidStones() {
        return VALID_STONES;
    }

    @Nullable
    @Override
    public Materials getGTMaterial() {
        return null;
    }

    @Override
    public boolean generatesPrefix(OrePrefixes prefix) {
        return false;
    }

    @Override
    public ItemStack getPart(OrePrefixes prefix, int amount) {
        ItemStack stack = GTOreDictUnificator.get(prefix.get(INTERNAL_NAME), amount);
        return stack == null ? null : stack.copy();
    }

    @Override
    public boolean contains(SubTag subTag) {
        return false;
    }

    @Override
    public ISubTagContainer add(SubTag... subTags) {
        return this;
    }

    @Override
    public boolean remove(SubTag subTag) {
        return false;
    }
}
