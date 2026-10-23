package com.science.gtnl.common.crafting.quantum.ae2;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.container.ContainerNull;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public final class QuantumPattern implements IPatternDetails {

    private final Shape shape;
    private final IInput[] inputs;

    private QuantumPattern(Shape shape, @Nullable StackKey[][] variants) {
        this.shape = shape;
        this.inputs = new IInput[shape.encoded.length];
        for (int i = 0; i < this.inputs.length; i++) {
            final StackKey[] keys = variants == null || variants[i] == null ? new StackKey[] { shape.encoded[i] }
                : variants[i];
            final GenericStack[] possible = new GenericStack[keys.length];
            for (int v = 0; v < keys.length; v++) {
                possible[v] = keys[v].toGeneric(shape.amounts[i]);
            }
            this.inputs[i] = shape.details.isCraftable() ? new CraftingInput(i, possible)
                : new ProcessingInput(i, possible);
        }
    }

    public ICraftingPatternDetails getDetails() {
        return this.shape.details;
    }

    @Override
    public IInput[] getInputs() {
        return this.inputs;
    }

    @Override
    public List<GenericStack> getOutputs() {
        return this.shape.outputs;
    }

    @Override
    public boolean canSubstitute() {
        return this.shape.details.canSubstitute();
    }

    @Override
    public String toString() {
        return "QuantumPattern" + this.shape.outputs;
    }

    static final class Shape {

        private static final int MAX_VARIANT_INSTANCES = 8;

        private final ICraftingPatternDetails details;
        private final List<GenericStack> outputs;
        private final int[] slots;
        private final StackKey[] encoded;
        private final long[] amounts;
        private final boolean usable;
        private final boolean substitutes;
        private final StackKeyFactory keys;
        private final WeakReference<World> world;
        private final Map<SlotTemplate, Optional<StackKey>> remaining = new ConcurrentHashMap<>();
        @Nullable
        private QuantumPattern plain;
        private final List<QuantumPattern> variantInstances = new ArrayList<>(1);

        Shape(ICraftingPatternDetails details, @Nullable IAEStack<?>[] processingInputs, StackKeyFactory keys,
            World world) {
            this.details = details;
            this.keys = keys;
            this.world = new WeakReference<>(world);

            final List<GenericStack> outputs = new ArrayList<>();
            for (IAEStack<?> output : details.getCondensedAEOutputs()) {
                final StackKey key = keys.of(output);
                if (key != null && output.getStackSize() > 0) {
                    outputs.add(key.toGeneric(output.getStackSize()));
                }
            }
            this.outputs = Collections.unmodifiableList(outputs);

            final List<Integer> slots = new ArrayList<>();
            final List<StackKey> encoded = new ArrayList<>();
            final List<Long> amounts = new ArrayList<>();
            boolean usable = !outputs.isEmpty();
            final IAEStack<?>[] stacks;
            if (details.isCraftable()) {
                stacks = details.getAEInputs();
            } else if (processingInputs == null) {
                stacks = new IAEStack<?>[0];
                usable = false;
            } else {
                stacks = processingInputs;
            }
            for (int slot = 0; slot < stacks.length; slot++) {
                final IAEStack<?> stack = stacks[slot];
                final StackKey key = keys.of(stack);
                // Empty stacks are placeholders of patterns without inputs, like those of Programmable Hatches
                if (key != null && stack.getStackSize() > 0) {
                    slots.add(details.isCraftable() ? slot : -1);
                    encoded.add(key);
                    amounts.add(stack.getStackSize());
                }
            }
            this.slots = slots.stream()
                .mapToInt(Integer::intValue)
                .toArray();
            this.encoded = encoded.toArray(new StackKey[0]);
            this.amounts = amounts.stream()
                .mapToLong(Long::longValue)
                .toArray();
            this.usable = usable;

            boolean substitutes = false;
            if (details.canSubstitute()) {
                for (StackKey key : this.encoded) {
                    if (key.getStack() instanceof IAEItemStack) {
                        substitutes = true;
                        break;
                    }
                }
            }
            this.substitutes = substitutes;
        }

        boolean isUsable() {
            return this.usable;
        }

        boolean substitutes() {
            return this.substitutes;
        }

        StackKey[] getEncodedInputs() {
            return this.encoded;
        }

        synchronized QuantumPattern instanceFor(@Nullable StackKey[][] variants) {
            if (!this.substitutes) {
                if (this.plain == null) {
                    this.plain = new QuantumPattern(this, null);
                }
                return this.plain;
            }
            for (QuantumPattern instance : this.variantInstances) {
                if (sameVariants(instance, variants)) {
                    return instance;
                }
            }
            final QuantumPattern created = new QuantumPattern(this, variants);
            if (this.variantInstances.size() >= MAX_VARIANT_INSTANCES) {
                this.variantInstances.remove(0);
            }
            this.variantInstances.add(created);
            return created;
        }

        private static boolean sameVariants(QuantumPattern instance, @Nullable StackKey[][] variants) {
            for (int i = 0; i < instance.inputs.length; i++) {
                final GenericStack[] possible = instance.inputs[i].possibleInputs();
                final StackKey[] wanted = variants == null || variants[i] == null ? null : variants[i];
                final int length = wanted == null ? 1 : wanted.length;
                if (possible.length != length) return false;
                for (int v = 0; v < length; v++) {
                    final AEKey key = wanted == null ? instance.shape.encoded[i] : wanted[v];
                    if (possible[v].what() != key) return false;
                }
            }
            return true;
        }

        @Nullable
        StackKey remainingKey(int input, StackKey template) {
            if (!(template.getStack() instanceof IAEItemStack itemStack)) {
                return null;
            }
            final SlotTemplate key = new SlotTemplate(this.slots[input], template);
            Optional<StackKey> result = this.remaining.get(key);
            if (result == null) {
                // Not computeIfAbsent: waiting for the server thread while holding a lock of the map could block it
                final ItemStack container = CraftingCalculation.callOnSteppingThread(
                    () -> containerAfterCrafting(key.slot(), itemStack),
                    () -> plainContainer(itemStack));
                result = Optional.ofNullable(container == null ? null : this.keys.of(AEItemStack.create(container)));
                final Optional<StackKey> raced = this.remaining.putIfAbsent(key, result);
                if (raced != null) result = raced;
            }
            return result.orElse(null);
        }

        @Nullable
        private ItemStack containerAfterCrafting(int slot, IAEItemStack template) {
            final World level = this.world.get();
            final IAEStack<?>[] slots = this.details.getAEInputs();
            if (!(level instanceof WorldServer server) || slot < 0 || slots.length > 9) {
                return plainContainer(template);
            }
            try {
                final InventoryCrafting grid = new InventoryCrafting(new ContainerNull(), 3, 3);
                for (int i = 0; i < slots.length; i++) {
                    if (slots[i] instanceof IAEItemStack stack) {
                        grid.setInventorySlotContents(i, stack.getItemStack());
                    }
                }
                final ItemStack input = template.getItemStack();
                input.stackSize = 1;
                grid.setInventorySlotContents(slot, input);
                FMLCommonHandler.instance()
                    .firePlayerCraftingEvent(Platform.getPlayer(server), this.details.getOutput(grid, server), grid);
                final ItemStack left = grid.getStackInSlot(slot);
                return left == null ? null : Platform.getContainerItem(left);
            } catch (RuntimeException e) {
                return plainContainer(template);
            }
        }

        @Nullable
        private static ItemStack plainContainer(IAEItemStack template) {
            final ItemStack input = template.getItemStack();
            input.stackSize = 1;
            return Platform.getContainerItem(input);
        }

        private record SlotTemplate(int slot, StackKey template) {}
    }

    private final class CraftingInput implements IInput {

        private final int index;
        private final GenericStack[] possibleInputs;

        private CraftingInput(int index, GenericStack[] possibleInputs) {
            this.index = index;
            this.possibleInputs = possibleInputs;
        }

        @Override
        public GenericStack[] possibleInputs() {
            return this.possibleInputs;
        }

        @Override
        public long getMultiplier() {
            return 1;
        }

        @Override
        public boolean isValid(AEKey input, World level) {
            if (input.equals(shape.encoded[this.index])) {
                return true;
            }
            if (!shape.details.canSubstitute() || !(input instanceof StackKey key)
                || !(key.getStack() instanceof IAEItemStack itemStack)) {
                return false;
            }
            return shape.details.isValidItemForSlot(shape.slots[this.index], itemStack.copy(), level);
        }

        @Nullable
        @Override
        public AEKey getRemainingKey(AEKey template) {
            return template instanceof StackKey key ? shape.remainingKey(this.index, key) : null;
        }
    }

    private final class ProcessingInput implements IInput {

        private final int index;
        private final GenericStack[] possibleInputs;
        private final Set<Object> fuzzyGroups;

        private ProcessingInput(int index, GenericStack[] possibleInputs) {
            this.index = index;
            this.possibleInputs = possibleInputs;
            this.fuzzyGroups = new ReferenceOpenHashSet<>(possibleInputs.length);
            for (GenericStack possibleInput : possibleInputs) {
                this.fuzzyGroups.add(
                    possibleInput.what()
                        .getPrimaryKey());
            }
        }

        @Override
        public GenericStack[] possibleInputs() {
            return this.possibleInputs;
        }

        @Override
        public long getMultiplier() {
            return 1;
        }

        @Override
        public boolean isValid(AEKey input, World level) {
            final StackKey encoded = shape.encoded[this.index];
            if (input.equals(encoded)) {
                return true;
            }
            // The CPU only searches fuzzy variants of items, see CraftingCPUCluster#getExtractItems
            return shape.details.canSubstitute() && encoded.getStack() instanceof IAEItemStack
                && this.fuzzyGroups.contains(input.getPrimaryKey());
        }

        @Nullable
        @Override
        public AEKey getRemainingKey(AEKey template) {
            return null;
        }
    }
}
