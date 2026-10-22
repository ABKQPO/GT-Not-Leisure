package com.science.gtnl.common.crafting.quantum.crafting.graph;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.util.LongMath;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public class CraftingGraphNode {

    public enum Kind {
        NATIVE_PATTERN,
        LOCAL_PATTERN,
        EMITTER,
        EXTERNAL_LEAF
    }

    private final AEKey what;
    private final @Nullable IPatternDetails pattern;
    private final List<GenericStack> outputs;
    private final List<IPatternDetails> patternCandidates;
    private final Kind kind;
    private final @Nullable String localReason;
    private final long outputPerCraft;
    private final ObjectList<CraftingGraphEdge> inputs = new ObjectArrayList<>();
    private final ObjectList<CraftingGraphNode> parents = new ObjectArrayList<>();
    private final List<CraftingGraphEdge> readOnlyInputs = ObjectLists.unmodifiable(inputs);
    private final List<CraftingGraphNode> readOnlyParents = ObjectLists.unmodifiable(parents);

    private int ordinal = -1;
    private long demandAmount = 0;
    private long craftTimes = 0;

    public CraftingGraphNode(AEKey what, @Nullable IPatternDetails pattern, List<GenericStack> outputs,
        long outputPerCraft) {
        this(what, pattern, outputs, outputPerCraft, false, false);
    }

    public CraftingGraphNode(AEKey what, @Nullable IPatternDetails pattern, List<GenericStack> outputs,
        long outputPerCraft, boolean localUnit, boolean externalLeaf) {
        this(
            what,
            pattern,
            pattern == null ? List.of() : List.of(pattern),
            outputs,
            outputPerCraft,
            localUnit,
            externalLeaf);
    }

    public CraftingGraphNode(AEKey what, @Nullable IPatternDetails pattern, List<IPatternDetails> patternCandidates,
        List<GenericStack> outputs, long outputPerCraft, boolean localUnit, boolean externalLeaf) {
        this(
            what,
            pattern,
            patternCandidates,
            outputs,
            outputPerCraft,
            localUnit,
            externalLeaf,
            localUnit ? "legacy-compatible-pattern" : null);
    }

    public CraftingGraphNode(AEKey what, @Nullable IPatternDetails pattern, List<IPatternDetails> patternCandidates,
        List<GenericStack> outputs, long outputPerCraft, boolean localUnit, boolean externalLeaf,
        @Nullable String localReason) {
        this.what = what;
        this.pattern = pattern;
        this.outputs = List.copyOf(outputs);
        this.patternCandidates = List.copyOf(patternCandidates);
        this.outputPerCraft = outputPerCraft;
        if (externalLeaf) {
            if (pattern != null || localUnit) {
                throw new IllegalArgumentException("External graph leaf cannot have a pattern");
            }
            this.kind = Kind.EXTERNAL_LEAF;
        } else if (pattern == null) {
            if (localUnit) {
                throw new IllegalArgumentException("Local graph node must have a pattern");
            }
            this.kind = Kind.EMITTER;
        } else {
            this.kind = localUnit ? Kind.LOCAL_PATTERN : Kind.NATIVE_PATTERN;
        }
        this.localReason = this.kind == Kind.LOCAL_PATTERN ? localReason : null;
    }

    public AEKey getWhat() {
        return what;
    }

    public int getOrdinal() {
        return ordinal;
    }

    void assignOrdinal(int ordinal) {
        if (this.ordinal != -1) {
            throw new IllegalStateException("Crafting graph node already has an ordinal");
        }
        this.ordinal = ordinal;
    }

    @Nullable
    public IPatternDetails getPattern() {
        return pattern;
    }

    public List<GenericStack> getOutputs() {
        return outputs;
    }

    public List<IPatternDetails> getPatternCandidates() {
        return patternCandidates;
    }

    public Kind getKind() {
        return kind;
    }

    public @Nullable String getLocalReason() {
        return localReason;
    }

    public boolean isEmitter() {
        return kind == Kind.EMITTER;
    }

    public boolean isExternalLeaf() {
        return kind == Kind.EXTERNAL_LEAF;
    }

    public boolean isLocalUnit() {
        return kind == Kind.LOCAL_PATTERN;
    }

    public long getOutputPerCraft() {
        return outputPerCraft;
    }

    public List<CraftingGraphEdge> getInputs() {
        return readOnlyInputs;
    }

    public void addInput(CraftingGraphEdge edge) {
        inputs.add(edge);
    }

    public List<CraftingGraphNode> getParents() {
        return readOnlyParents;
    }

    public void addParent(CraftingGraphNode parent) {
        parents.add(parent);
    }

    public long getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(long demandAmount) {
        this.demandAmount = demandAmount;
    }

    public void addDemandAmount(long amount) {
        this.demandAmount = LongMath.saturatedAdd(this.demandAmount, amount);
    }

    public long getCraftTimes() {
        return craftTimes;
    }

    public void setCraftTimes(long craftTimes) {
        this.craftTimes = craftTimes;
    }

    public void resetPlanningAmounts() {
        this.demandAmount = 0;
        this.craftTimes = 0;
        for (var edge : inputs) edge.resetPlanning();
    }
}
