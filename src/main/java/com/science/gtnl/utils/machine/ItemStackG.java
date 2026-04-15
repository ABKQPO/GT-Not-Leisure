package com.science.gtnl.utils.machine;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.jetbrains.annotations.NotNull;

import appeng.api.storage.data.IAEItemStack;

// G is for group
public class ItemStackG {

    public ArrayList<ItemStack> arr = new ArrayList<>();

    public boolean isEmpty() {
        return !arr.stream()
            .filter(s -> s.stackSize > 0)
            .findFirst()
            .isPresent();
    }

    public static ItemStackG neo(ItemStack is) {
        if (is == null) return null;
        return new ItemStackG(is);
    }

    private ItemStackG() {}

    private ItemStackG(ItemStack is) {
        if (is != null) is = is.copy();
        arr.add(is);
    }

    public ItemStack getZero() {
        ItemStack is = arr.get(0);
        return is;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound p_77955_1_) {

        arr.get(0)
            .writeToNBT(p_77955_1_);
        p_77955_1_.setInteger("ICount", arr.get(0).stackSize);

        NBTTagList lst = new NBTTagList();
        for (int i = 1; i < arr.size(); i++) {

            NBTTagCompound t = new NBTTagCompound();
            arr.get(i)
                .writeToNBT(t);
            t.setInteger("ICount", arr.get(i).stackSize);
            lst.appendTag(t);

        }

        p_77955_1_.setTag("therest", lst);

        return p_77955_1_;
    }

    public static ItemStackG readFromNBT(NBTTagCompound p_77963_1_) {

        ItemStack is = ItemStack.loadItemStackFromNBT(p_77963_1_);
        if (is == null) return null;
        is.stackSize = p_77963_1_.getInteger("ICount");
        ItemStackG to = neo(is);
        if (to != null) {

            if (p_77963_1_.hasKey("therest")) {

                NBTTagList lst = (NBTTagList) p_77963_1_.getTag("therest");
                for (int ix = 0; ix < lst.tagCount(); ix++) {
                    NBTTagCompound TAG = lst.getCompoundTagAt(ix);

                    ItemStack isX = ItemStack.loadItemStackFromNBT(TAG);
                    if (isX != null) {
                        isX.stackSize = TAG.getInteger("ICount");
                        to.arr.add(isX);
                    }

                }

            }

        }

        return to;

    }

    public long stackSize() {
        return arr.stream()
            .mapToLong(s -> s.stackSize)
            .sum();
    }

    public Item getItem() {

        return arr.get(0)
            .getItem();
    }

    public ItemStack getStack() {
        ItemStack ret = arr.get(0)
            .copy();
        ret.stackSize = (int) Math.min(stackSize(), Integer.MAX_VALUE);
        return ret;
    }

    public void stackSizeInc(int todo) {

        for (ItemStack is : arr) {
            int cando = Math.min(

                Integer.MAX_VALUE
                    // 64

                    - is.stackSize,
                todo);
            todo -= cando;
            is.stackSize += cando;
            if (todo <= 0) return;
        }
        ItemStack i = arr.get(0)
            .copy();
        i.stackSize = todo;
        arr.add(i);

    }

    public static ItemStackG loadItemStackFromNBT(NBTTagCompound tag) {

        return readFromNBT(tag);
    }

    public ItemStack[] flat() {
        adjust();
        return arr.toArray(new ItemStack[arr.size()]);
    }

    public void stackSize(int i) {

        if (arr.size() > 1) {
            arr.remove(1);
        }
        arr.get(0).stackSize = i;
    }

    public static ItemStackG setZero(ItemStackG itemStackG, ItemStack copyStackWithSize) {

        // return neo(copyStackWithSize);
        if (itemStackG == null) {
            return neo(copyStackWithSize);
        }
        if (copyStackWithSize == null) itemStackG.arr.get(0).stackSize = 0;

        else itemStackG.arr.get(0).stackSize = copyStackWithSize.stackSize;
        itemStackG.adjust();
        if (itemStackG.stackSize() <= 0) return null;
        return itemStackG;
    }

    public void adjust() {
        boolean dirty = false;
        for (int i = 0; i < arr.size() - 1; i++) {
            if (arr.get(i).stackSize < Integer.MAX_VALUE) {
                if (arr.get(i + 1).stackSize > 0) {
                    int todo = Math.min(Integer.MAX_VALUE - arr.get(i).stackSize, arr.get(i + 1).stackSize);
                    arr.get(i).stackSize += todo;
                    arr.get(i + 1).stackSize -= todo;
                    dirty = true;

                }
            }

        }

        if (dirty) {
            Iterator<ItemStack> it = arr.iterator();
            it.next();
            while (it.hasNext()) {
                if (it.next().stackSize <= 0) {
                    it.remove();
                }

            }

        }
    }

    public static ItemStackG fromAE(@NotNull IAEItemStack possible, int intmaxs) {
        ItemStackG ret = new ItemStackG();

        long all = possible.getStackSize();
        all = Math.min(all, intmaxs * 1L * Integer.MAX_VALUE);

        long maxs = all / (1L * Integer.MAX_VALUE);
        long remain = all - maxs * Integer.MAX_VALUE;
        for (int i = 0; i < maxs; i++) {
            ItemStack is = possible.getItemStack();
            is.stackSize = Integer.MAX_VALUE;
            ret.arr.add(is);
        }
        if (remain > 0) {
            ItemStack is = possible.getItemStack();
            is.stackSize = (int) Math.min(remain, Integer.MAX_VALUE);
            ret.arr.add(is);
        }

        return ret;
    }

}
