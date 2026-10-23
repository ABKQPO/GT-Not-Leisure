package com.science.gtnl.common.crafting.quantum.ae2;

import net.minecraft.item.Item;

import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKeyType;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;

public final class StackKey extends AEKey {

    private final IAEStack<?> stack;
    private final AEKeyType type;
    private final Object primaryKey;
    private final int fuzzySearchValue;
    private final int fuzzySearchMaxValue;
    private final int hash;
    private String tag;

    StackKey(IAEStack<?> stack, AEKeyType type, Object primaryKey) {
        this.stack = stack;
        this.type = type;
        this.primaryKey = primaryKey == null ? this : primaryKey;
        this.hash = stack.hashCode();

        if (stack instanceof IAEItemStack itemStack && itemStack.getItem() != null
            && itemStack.getItem()
                .isDamageable()) {
            this.fuzzySearchValue = itemStack.getItemDamage();
            this.fuzzySearchMaxValue = itemStack.getItem()
                .getMaxDamage();
        } else {
            this.fuzzySearchValue = 0;
            this.fuzzySearchMaxValue = 0;
        }
    }

    static Object computePrimaryKey(IAEStack<?> stack, StackKeyFactory factory) {
        if (stack instanceof IAEItemStack itemStack) {
            final Item item = itemStack.getItem();
            // Damageable items group by item so durability ranges can be searched; every other meta is its own item
            return item.isDamageable() ? item : factory.itemMeta(item, itemStack.getItemDamage());
        }
        if (stack instanceof IAEFluidStack fluidStack) {
            return fluidStack.getFluid();
        }
        return null;
    }

    public IAEStack<?> getStack() {
        return this.stack;
    }

    public IAEStack<?> toStack(long amount) {
        final IAEStack<?> copy = this.stack.copy();
        copy.setStackSize(amount);
        return copy;
    }

    public GenericStack toGeneric(long amount) {
        return new GenericStack(this, amount);
    }

    @Override
    public AEKeyType getType() {
        return this.type;
    }

    @Override
    public Object getPrimaryKey() {
        return this.primaryKey;
    }

    @Override
    public int getFuzzySearchValue() {
        return this.fuzzySearchValue;
    }

    @Override
    public int getFuzzySearchMaxValue() {
        return this.fuzzySearchMaxValue;
    }

    @Override
    public Object toTag() {
        String result = this.tag;
        if (result == null) {
            result = this.stack.toNBTGeneric()
                .toString();
            this.tag = result;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof StackKey other && other.hash == this.hash && other.stack.equals(this.stack);
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public String toString() {
        return this.type.getId() + ":"
            + this.stack.getUnlocalizedName()
            + (this.stack instanceof IAEItemStack itemStack ? "@" + itemStack.getItemDamage() : "");
    }
}
