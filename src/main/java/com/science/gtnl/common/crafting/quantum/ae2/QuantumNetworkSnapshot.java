package com.science.gtnl.common.crafting.quantum.ae2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.networking.crafting.ICraftingService;
import com.science.gtnl.common.crafting.quantum.api.networking.storage.IStorageService;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;
import com.science.gtnl.common.crafting.quantum.crafting.graph.CraftingGraph;

import appeng.api.config.FuzzyMode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import appeng.crafting.MECraftingInventory;
import appeng.me.cache.CraftingGridCache;
import appeng.util.item.AEItemStackType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

public final class QuantumNetworkSnapshot implements ICraftingService, IStorageService {

    private final IStorageGrid storageGrid;
    private final CraftingGridCache craftingGrid;
    private final World world;
    private final QuantumCraftingCache.Revision revision;
    private final StackKeyFactory keyFactory;
    private final ImmutableMap<IAEStack<?>, ImmutableList<ICraftingPatternDetails>> patterns;

    // Filled by the calculation thread
    private final Map<AEKey, List<IPatternDetails>> patternsByOutput = new Object2ObjectOpenHashMap<>();
    private final Map<AEKey, List<StackKey>> fuzzyItemVariants = new Object2ObjectOpenHashMap<>();

    public QuantumNetworkSnapshot(IStorageGrid storageGrid, CraftingGridCache craftingGrid, World world) {
        this.storageGrid = storageGrid;
        this.craftingGrid = craftingGrid;
        this.world = world;
        this.revision = QuantumCraftingCache.revisionOf(craftingGrid);
        this.keyFactory = this.revision.getKeys();
        this.patterns = this.revision.getPatterns();
    }

    public StackKeyFactory getKeyFactory() {
        return this.keyFactory;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private IItemList<IAEStack> storedList(IAEStackType<?> type) {
        return (IItemList) this.storageGrid.getMEMonitor(type)
            .getStorageList();
    }

    @Override
    public int getInventorySize() {
        int size = 0;
        for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
            size += storedList(type).size();
        }
        return size;
    }

    @Override
    public KeyCounter copyInventory() {
        final KeyCounter result = KeyCounter.saturating();
        for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
            for (IAEStack<?> stack : storedList(type)) {
                if (stack.getStackSize() > 0) {
                    final StackKey key = this.keyFactory.of(stack);
                    if (key != null) {
                        result.add(key, stack.getStackSize());
                    }
                }
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public KeyCounter copyInventory(Collection<AEKey> keys, Set<AEKey> fuzzyKeys) {
        final KeyCounter result = KeyCounter.saturating(keys.size() + fuzzyKeys.size());
        for (AEKey wanted : keys) {
            if (!(wanted instanceof StackKey key)) continue;
            final IItemList<IAEStack> list = storedList(
                key.getStack()
                    .getStackType());
            if (fuzzyKeys.contains(key) && key.getStack() instanceof IAEItemStack) {
                for (IAEStack<?> variant : list.findFuzzy((IAEStack) key.getStack(), FuzzyMode.IGNORE_ALL)) {
                    final StackKey variantKey = variant.getStackSize() > 0 ? this.keyFactory.of(variant) : null;
                    // set(), not add(): several wanted keys can share one fuzzy group
                    if (variantKey != null) {
                        result.set(variantKey, variant.getStackSize());
                    }
                }
            } else {
                final IAEStack<?> stored = list.findPrecise((IAEStack) key.getStack());
                if (stored != null && stored.getStackSize() > 0) {
                    result.set(key, stored.getStackSize());
                }
            }
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MECraftingInventory createStorageView(Iterable<AEKey> keys) {
        final MECraftingInventory view = new MECraftingInventory();
        for (AEKey wanted : keys) {
            if (!(wanted instanceof StackKey key)) continue;
            final IAEStack<?> stored = storedList(
                key.getStack()
                    .getStackType()).findPrecise((IAEStack) key.getStack());
            if (stored != null && stored.getStackSize() > 0) {
                view.injectItems(stored.copy(), appeng.api.config.Actionable.MODULATE);
            }
        }
        return view;
    }

    @Override
    public synchronized Collection<IPatternDetails> getCraftingFor(AEKey whatToCraft) {
        List<IPatternDetails> result = this.patternsByOutput.get(whatToCraft);
        if (result != null) {
            return result;
        }

        result = List.of();
        if (whatToCraft instanceof StackKey key) {
            // Already sorted by priority, highest first
            final ImmutableList<ICraftingPatternDetails> details = this.patterns.get(key.getStack());
            if (details != null && !details.isEmpty()) {
                final List<IPatternDetails> adapted = new ArrayList<>(details.size());
                for (ICraftingPatternDetails detail : details) {
                    final QuantumPattern pattern = adapt(detail);
                    if (pattern != null) {
                        adapted.add(pattern);
                    }
                }
                result = List.copyOf(adapted);
            }
        }
        this.patternsByOutput.put(whatToCraft, result);
        return result;
    }

    @Nullable
    private QuantumPattern adapt(ICraftingPatternDetails details) {
        final QuantumPattern.Shape shape = this.revision.shapeOf(details, this.craftingGrid, this.world);
        if (!shape.isUsable()) {
            return null;
        }
        if (!shape.substitutes()) {
            return shape.instanceFor(null);
        }
        final StackKey[] encoded = shape.getEncodedInputs();
        final StackKey[][] variants = new StackKey[encoded.length][];
        for (int i = 0; i < encoded.length; i++) {
            if (encoded[i].getStack() instanceof IAEItemStack) {
                final Set<StackKey> keys = new ObjectLinkedOpenHashSet<>();
                keys.add(encoded[i]);
                keys.addAll(getFuzzyItemVariants(encoded[i]));
                variants[i] = keys.toArray(new StackKey[0]);
            }
        }
        return shape.instanceFor(variants);
    }

    @Nullable
    @Override
    public synchronized AEKey getFuzzyCraftable(AEKey whatToCraft, Predicate<AEKey> filter) {
        for (var fuzzy : this.revision.getSubstitutableOutputs()
            .findFuzzy(whatToCraft, FuzzyMode.IGNORE_ALL)) {
            if (filter.test(fuzzy.getKey())) {
                return fuzzy.getKey();
            }
        }
        return null;
    }

    @Override
    public boolean canEmitFor(AEKey what) {
        return this.revision.canEmit(what);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    synchronized List<StackKey> getFuzzyItemVariants(StackKey item) {
        List<StackKey> result = this.fuzzyItemVariants.get(item);
        if (result != null) {
            return result;
        }

        final Set<StackKey> variants = new ObjectLinkedOpenHashSet<>();
        final IAEItemStack filter = (IAEItemStack) item.getStack();
        for (IAEStack<?> stored : storedList(AEItemStackType.ITEM_STACK_TYPE)
            .findFuzzy((IAEStack) filter, FuzzyMode.IGNORE_ALL)) {
            if (stored.getStackSize() > 0) {
                addVariant(variants, stored);
            }
        }
        for (IAEItemStack craftable : this.revision.getSubstitutableItems()
            .findFuzzy(filter, FuzzyMode.IGNORE_ALL)) {
            addVariant(variants, craftable);
        }
        result = variants.isEmpty() ? List.of() : new ObjectArrayList<>(variants);
        this.fuzzyItemVariants.put(item, result);
        return result;
    }

    private void addVariant(Set<StackKey> variants, IAEStack<?> stack) {
        final StackKey key = this.keyFactory.of(stack);
        if (key != null) {
            variants.add(key);
        }
    }

    @Override
    public long getGraphCacheRevision() {
        return this.revision.getId();
    }

    @Nullable
    @Override
    public CraftingGraph takeCachedGraph(AEKey output) {
        final CraftingGraph graph = this.revision.takeGraph(output);
        if (graph != null) {
            graph.setCacheRevision(this.revision.getId());
        }
        return graph;
    }

    @Nullable
    @Override
    public CraftingGraph peekCachedGraph(AEKey output) {
        return this.revision.peekGraph(output);
    }

    @Override
    public void putCachedGraph(AEKey output, CraftingGraph graph) {
        this.revision.putGraph(output, graph);
    }
}
