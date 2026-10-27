package com.science.gtnl.common.crafting.quantum.ae2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;
import com.science.gtnl.common.crafting.quantum.crafting.graph.CraftingGraph;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.helpers.PatternHelper;
import appeng.me.cache.CraftingGridCache;
import appeng.util.TunnelPatternExpander;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class QuantumCraftingCache {

    private static final int MAX_KEYS = 262_144;

    private static final int MAX_GRAPHS = 64;

    private static final Map<CraftingGridCache, QuantumCraftingCache> CACHES = new WeakHashMap<>();

    @Nullable
    private Revision current;
    private long revisions;

    public static Revision revisionOf(CraftingGridCache grid) {
        final QuantumCraftingCache cache;
        synchronized (CACHES) {
            cache = CACHES.computeIfAbsent(grid, g -> new QuantumCraftingCache());
        }
        return cache.refresh(grid);
    }

    public static void clearAll() {
        synchronized (CACHES) {
            CACHES.clear();
        }
    }

    private synchronized Revision refresh(CraftingGridCache grid) {
        final ImmutableMap<IAEStack<?>, ImmutableList<ICraftingPatternDetails>> patterns = grid
            .getCraftingMultiPatterns();
        final ImmutableSet<IAEStack<?>> emitables = grid.getEmitableItems();
        final Revision revision = this.current;
        // AE2 replaces the pattern map whenever a pattern changes, tunnel patterns included
        if (revision != null && revision.patterns == patterns && revision.emitableStacks.equals(emitables)) {
            return revision;
        }
        final StackKeyFactory keys = revision == null || revision.keys.size() > MAX_KEYS ? new StackKeyFactory()
            : revision.keys;
        this.current = new Revision(++this.revisions, patterns, emitables, keys);
        return this.current;
    }

    public static class Revision {

        private final long id;
        private final ImmutableMap<IAEStack<?>, ImmutableList<ICraftingPatternDetails>> patterns;
        private final ImmutableSet<IAEStack<?>> emitableStacks;
        private final Set<AEKey> emitables = new ObjectOpenHashSet<>();
        private final StackKeyFactory keys;
        private final Map<ICraftingPatternDetails, QuantumPattern.Shape> shapes = new Reference2ObjectOpenHashMap<>();
        private final Map<AEKey, CraftingGraph> graphs = new LinkedHashMap<>(16, 0.75f, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry<AEKey, CraftingGraph> eldest) {
                return size() > MAX_GRAPHS;
            }
        };
        @Nullable
        private KeyCounter substitutableOutputs;
        @Nullable
        private IItemList<IAEItemStack> substitutableItems;

        private Revision(long id, ImmutableMap<IAEStack<?>, ImmutableList<ICraftingPatternDetails>> patterns,
            ImmutableSet<IAEStack<?>> emitableStacks, StackKeyFactory keys) {
            this.id = id;
            this.patterns = patterns;
            this.emitableStacks = emitableStacks;
            this.keys = keys;
            for (IAEStack<?> emitable : emitableStacks) {
                final StackKey key = keys.of(emitable);
                if (key != null) {
                    this.emitables.add(key);
                }
            }
        }

        public long getId() {
            return this.id;
        }

        public StackKeyFactory getKeys() {
            return this.keys;
        }

        public ImmutableMap<IAEStack<?>, ImmutableList<ICraftingPatternDetails>> getPatterns() {
            return this.patterns;
        }

        public boolean canEmit(AEKey key) {
            return this.emitables.contains(key);
        }

        synchronized QuantumPattern.Shape shapeOf(ICraftingPatternDetails details, CraftingGridCache grid,
            World world) {
            QuantumPattern.Shape shape = this.shapes.get(details);
            if (shape == null) {
                shape = new QuantumPattern.Shape(
                    details,
                    details.isCraftable() ? null : expandTunnels(details, grid),
                    this.keys,
                    world);
                this.shapes.put(details, shape);
            }
            return shape;
        }

        @Nullable
        private static IAEStack<?>[] expandTunnels(ICraftingPatternDetails details, CraftingGridCache grid) {
            final List<IAEStack<?>> expanded = TunnelPatternExpander
                .expandInputs(details.getCondensedAEInputs(), grid, null);
            if (expanded == null) {
                return null;
            }
            return PatternHelper.convertToCondensedAEList(expanded.toArray(new IAEStack<?>[0]));
        }

        synchronized KeyCounter getSubstitutableOutputs() {
            if (this.substitutableOutputs == null) {
                final KeyCounter result = new KeyCounter();
                for (IAEStack<?> output : substitutableStacks()) {
                    final StackKey key = this.keys.of(output);
                    if (key != null) {
                        result.add(key, 1);
                    }
                }
                this.substitutableOutputs = result;
            }
            return this.substitutableOutputs;
        }

        synchronized IItemList<IAEItemStack> getSubstitutableItems() {
            if (this.substitutableItems == null) {
                final IItemList<IAEItemStack> result = AEApi.instance()
                    .storage()
                    .createItemList();
                for (IAEStack<?> output : substitutableStacks()) {
                    if (output instanceof IAEItemStack item) {
                        final IAEItemStack copy = item.copy();
                        copy.reset();
                        copy.setStackSize(1);
                        result.add(copy);
                    }
                }
                this.substitutableItems = result;
            }
            return this.substitutableItems;
        }

        private List<IAEStack<?>> substitutableStacks() {
            final List<IAEStack<?>> result = new ObjectArrayList<>();
            for (Map.Entry<IAEStack<?>, ImmutableList<ICraftingPatternDetails>> entry : this.patterns.entrySet()) {
                for (ICraftingPatternDetails details : entry.getValue()) {
                    if (details.canBeSubstitute()) {
                        result.add(entry.getKey());
                        break;
                    }
                }
            }
            return result;
        }

        @Nullable
        synchronized CraftingGraph takeGraph(AEKey output) {
            return this.graphs.remove(output);
        }

        @Nullable
        synchronized CraftingGraph peekGraph(AEKey output) {
            return this.graphs.get(output);
        }

        synchronized void putGraph(AEKey output, CraftingGraph graph) {
            if (graph.cacheRevision() == this.id) {
                this.graphs.put(output, graph);
            }
        }
    }
}
