package com.science.gtnl.common.yogsothoth;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class YogMergeRecipe extends ShapelessOreRecipe {

    public YogMergeRecipe(ItemStack yog, ItemStack displayTerminal) {
        super(yog, yog, displayTerminal);
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return split(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        List<ItemStack> parts = split(inventory);
        if (parts == null) return null;
        ItemStack result = parts.get(0)
            .copy();
        result.stackSize = 1;
        ItemStack[] terminals = new ItemStack[parts.size() - 1];
        for (int index = 1; index < parts.size(); index++) terminals[index - 1] = parts.get(index)
            .copy();
        YogToken.addPending(result, batch(parts), terminals);
        return result;
    }

    // Recipe viewers and AE crafting re-run this and compare results exactly, so the batch must be deterministic;
    // the nonce changes after every merge so crafting the same terminals again is still a new batch.
    private static UUID batch(List<ItemStack> parts) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            ItemStack yog = parts.get(0);
            UUID id = YogToken.id(yog);
            out.writeUTF(id == null ? "" : id.toString());
            out.writeInt(YogToken.generation(yog));
            out.writeInt(YogToken.nonce(yog));
            out.writeInt(YogToken.pendingCount(yog));
            for (int index = 1; index < parts.size(); index++) CompressedStreamTools.write(
                parts.get(index)
                    .writeToNBT(new NBTTagCompound()),
                out);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
        return UUID.nameUUIDFromBytes(bytes.toByteArray());
    }

    private static List<ItemStack> split(InventoryCrafting inventory) {
        ItemStack yog = null;
        List<ItemStack> parts = new ArrayList<>();
        parts.add(null);
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            if (YogToken.isYog(stack)) {
                if (yog != null || stack.stackSize != 1) return null;
                yog = stack;
            } else if (YogToken.isStorable(stack)) parts.add(stack);
            else return null;
        }
        if (yog == null || parts.size() < 2) return null;
        parts.set(0, yog);
        return parts;
    }
}
