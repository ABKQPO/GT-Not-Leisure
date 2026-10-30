package com.science.gtnl.common.crafting.quantum.hooks;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingJob;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumPattern;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingPlan;

import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.IInterfaceViewable;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.core.sync.packets.PacketOptimizePatterns;
import codechicken.nei.ItemStackSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

public class OptimizePatternsHooks {

    // Returns false when there is no plan or grid, in which case nothing is sent.
    public static boolean collectPatterns(QuantumCraftingJob<?> job, Map<IAEStack<?>, Object> patterns, IGrid grid,
        Function<IGrid, Set<IGrid>> reachableGrids) {
        final CraftingPlan plan = job.getPlan();
        if (plan == null || grid == null) return false;

        final ItemStackSet blacklistedPatterns = blacklistedPatterns(reachableGrids.apply(grid));
        for (Object2LongMap.Entry<IPatternDetails> entry : plan.patternTimes()
            .object2LongEntrySet()) {
            final ICraftingPatternDetails details = ((QuantumPattern) entry.getKey()).getDetails();
            final ItemStack patternStack = details.getPattern();
            if (patternStack != null && blacklistedPatterns.contains(patternStack)) continue;
            OptimizePatternAccess.addCraftingTask(
                patterns.computeIfAbsent(details.getCondensedAEOutputs()[0], k -> OptimizePatternAccess.newPattern()),
                details,
                entry.getLongValue());
        }
        patterns.values()
            .removeIf(pattern -> !OptimizePatternAccess.isSingleProcessingPattern(pattern));
        return true;
    }

    private static ItemStackSet blacklistedPatterns(Set<IGrid> grids) {
        final ItemStackSet blacklistedPatterns = new ItemStackSet();
        final var supported = AEApi.instance()
            .registries()
            .interfaceTerminal()
            .getSupportedClasses();
        for (final IGrid currentGrid : grids) {
            for (Class<? extends IInterfaceViewable> c : supported) {
                for (IGridNode node : currentGrid.getMachines(c)) {
                    final IInterfaceViewable machine = (IInterfaceViewable) node.getMachine();
                    if (machine.allowsPatternOptimization()) continue;
                    final IInventory patternInv = machine.getPatterns();
                    for (int i = 0; i < patternInv.getSizeInventory(); i++) {
                        final ItemStack stack = patternInv.getStackInSlot(i);
                        if (stack != null) blacklistedPatterns.add(stack);
                    }
                }
            }
        }
        return blacklistedPatterns;
    }

    // Encoded as AE2 does: the hash of the output and the highest multiplier in the stack size
    public static void sendPatterns(Map<IAEStack<?>, Object> patterns, List<?> crafters) {
        try {
            final PacketMEInventoryUpdate patternsUpdate = new PacketMEInventoryUpdate((byte) 0);
            for (Map.Entry<IAEStack<?>, Object> entry : patterns.entrySet()) {
                final Object pattern = entry.getValue();
                final IAEStack<?> stack = entry.getKey()
                    .copy();
                stack.setCountRequestableCrafts(OptimizePatternAccess.requestedCrafts(pattern));
                final long perCraft = OptimizePatternAccess.craftAmountFor(pattern, stack);
                final long hash = entry.getKey()
                    .hashCode();
                final long multiplier = OptimizePatternAccess.maxBitMultiplier(pattern)
                    & PacketOptimizePatterns.MULTIPLIER_BIT_MASK;
                long highbits = (Math.abs(hash) << 1) | ((hash < 0) ? 1 : 0);
                highbits = highbits << PacketOptimizePatterns.MULTIPLIER_BITS;
                stack.setStackSize(highbits | multiplier);
                stack.setCountRequestable(perCraft);
                patternsUpdate.appendItem(stack);
            }

            for (final Object player : crafters) {
                if (player instanceof EntityPlayerMP playerMP) {
                    NetworkHandler.instance.sendTo(patternsUpdate, playerMP);
                }
            }
        } catch (IOException ignored) {}
    }

}
