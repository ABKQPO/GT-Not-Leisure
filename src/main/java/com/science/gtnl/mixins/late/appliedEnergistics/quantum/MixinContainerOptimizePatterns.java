package com.science.gtnl.mixins.late.appliedEnergistics.quantum;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingJob;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumPattern;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingPlan;

import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.IInterfaceViewable;
import appeng.container.ContainerSubGui;
import appeng.container.implementations.ContainerOptimizePatterns;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.core.sync.packets.PacketOptimizePatterns;
import codechicken.nei.ItemStackSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

@Mixin(value = ContainerOptimizePatterns.class, remap = false)
public abstract class MixinContainerOptimizePatterns extends ContainerSubGui {

    @Unique
    private static Constructor<?> gtnl$patternConstructor;

    @Shadow
    private ICraftingJob result;

    @Shadow
    HashMap<IAEStack<?>, Object> patterns;

    public MixinContainerOptimizePatterns(InventoryPlayer ip, Object anchor) {
        super(ip, anchor);
    }

    @Shadow
    private IGrid getGrid() {
        throw new AssertionError();
    }

    @Shadow
    private Set<IGrid> collectReachableGrids(IGrid grid) {
        throw new AssertionError();
    }

    @Inject(method = "setResult", at = @At("HEAD"), cancellable = true)
    private void gtnl$setQuantumResult(ICraftingJob job, CallbackInfo ci) {
        if (!(job instanceof QuantumCraftingJob<?>quantumJob)) return;
        ci.cancel();

        this.result = job;
        this.patterns.clear();

        final CraftingPlan plan = quantumJob.getPlan();
        final IGrid grid = getGrid();
        if (plan == null || grid == null) return;

        final ItemStackSet blacklistedPatterns = gtnl$blacklistedPatterns(grid);
        for (Object2LongMap.Entry<IPatternDetails> entry : plan.patternTimes()
            .object2LongEntrySet()) {
            final ICraftingPatternDetails details = ((QuantumPattern) entry.getKey()).getDetails();
            final ItemStack patternStack = details.getPattern();
            if (patternStack != null && blacklistedPatterns.contains(patternStack)) continue;
            ((AccessorOptimizePattern) this.patterns
                .computeIfAbsent(details.getCondensedAEOutputs()[0], k -> gtnl$newPattern()))
                    .gtnl$addCraftingTask(details, entry.getLongValue());
        }
        this.patterns.values()
            .removeIf(value -> {
                final AccessorOptimizePattern pattern = (AccessorOptimizePattern) value;
                return pattern.gtnl$getPatternDetails()
                    .size() != 1 || pattern.gtnl$getPattern()
                        .isCraftable();
            });

        gtnl$sendPatterns();
    }

    @Unique
    private ItemStackSet gtnl$blacklistedPatterns(IGrid grid) {
        final ItemStackSet blacklistedPatterns = new ItemStackSet();
        final var supported = AEApi.instance()
            .registries()
            .interfaceTerminal()
            .getSupportedClasses();
        for (final IGrid currentGrid : this.collectReachableGrids(grid)) {
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
    @Unique
    private void gtnl$sendPatterns() {
        try {
            final PacketMEInventoryUpdate patternsUpdate = new PacketMEInventoryUpdate((byte) 0);
            for (Map.Entry<IAEStack<?>, Object> entry : this.patterns.entrySet()) {
                final AccessorOptimizePattern pattern = (AccessorOptimizePattern) entry.getValue();
                final IAEStack<?> stack = entry.getKey()
                    .copy();
                stack.setCountRequestableCrafts(pattern.gtnl$getRequestedCrafts());
                final long perCraft = pattern.gtnl$getCraftAmountForItem(stack);
                final long hash = entry.getKey()
                    .hashCode();
                final long multiplier = pattern.gtnl$getMaxBitMultiplier() & PacketOptimizePatterns.MULTIPLIER_BIT_MASK;
                long highbits = (Math.abs(hash) << 1) | ((hash < 0) ? 1 : 0);
                highbits = highbits << PacketOptimizePatterns.MULTIPLIER_BITS;
                stack.setStackSize(highbits | multiplier);
                stack.setCountRequestable(perCraft);
                patternsUpdate.appendItem(stack);
            }

            for (final Object player : this.crafters) {
                if (player instanceof EntityPlayerMP playerMP) {
                    NetworkHandler.instance.sendTo(patternsUpdate, playerMP);
                }
            }
        } catch (IOException ignored) {}
    }

    @Unique
    private static Object gtnl$newPattern() {
        try {
            if (gtnl$patternConstructor == null) {
                final Constructor<?> constructor = Class
                    .forName("appeng.container.implementations.ContainerOptimizePatterns$Pattern")
                    .getDeclaredConstructor();
                constructor.setAccessible(true);
                gtnl$patternConstructor = constructor;
            }
            return gtnl$patternConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create a pattern entry of the pattern optimizer", e);
        }
    }
}
