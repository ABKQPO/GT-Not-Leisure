package com.reavaritia.utils.item;

import java.util.Objects;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record ItemStackWrapper(ItemStack stack) {

    @Override
    public boolean equals(Object otherobj) {
        if (!(otherobj instanceof ItemStackWrapper(ItemStack stack1))) return false;

        if (!Objects.equals(this.stack.getItem(), stack1.getItem())) return false;
        if (this.stack.getItemDamage() != stack1.getItemDamage()) return false;

        if (this.stack.stackTagCompound == null && stack1.stackTagCompound == null) return true;
        if (this.stack.stackTagCompound == null || stack1.stackTagCompound == null) return false;

        return this.stack.stackTagCompound.equals(stack1.stackTagCompound);
    }

    @Override
    public int hashCode() {
        int h = Objects.requireNonNull(this.stack.getItem())
            .hashCode();
        h = 31 * h + this.stack.getItemDamage();
        if (this.stack.stackTagCompound != null) {
            h = 31 * h + this.stack.stackTagCompound.hashCode();
        }
        return h;
    }

    @Override
    public @NotNull String toString() {
        return this.stack.toString();
    }
}
