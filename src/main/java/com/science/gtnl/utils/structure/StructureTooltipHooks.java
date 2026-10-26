package com.science.gtnl.utils.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.structurelib.alignment.constructable.ChannelDataAccessor;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IMultiblockTooltipRules;
import com.science.gtnl.utils.enums.GTNLStructureChannels;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.structure.error.StructureError;

public class StructureTooltipHooks {

    public static final int UNLIMITED = Integer.MAX_VALUE;
    // StructureLib's ChannelDataAccessor keeps sub-channel values under this tag without exposing it.
    private static final String CHANNEL_TAG = "channels";
    private static final ThreadLocal<Recording> RECORDING = new ThreadLocal<>();
    private static final ThreadLocal<Object> DESCRIBING = new ThreadLocal<>();
    private static final Set<String> HATCH_BLOCK_KEYS = ConcurrentHashMap.newKeySet();

    public record PieceCall(String piece, int horizontalOffset, int verticalOffset, int depthOffset) {}

    public record HatchRule(List<String> hatchKeys, int min, int max, int perLayer) {}

    public record CheckRecord(List<Integer> casingMinimums, List<HatchRule> hatchRules, String failure) {}

    private StructureTooltipHooks() {}

    public static <T> IStructureElement<T> describeAsHatch(IStructureElement<T> element, String... nameKeys) {
        List<String> keys = Arrays.asList(nameKeys);
        HATCH_BLOCK_KEYS.addAll(keys);
        return StructureUtility.withDescription(keys, element);
    }

    public static boolean isHatchBlockKey(String key) {
        return HATCH_BLOCK_KEYS.contains(key);
    }

    public static <T> List<String> describe(T controller, IStructureElement<T> element) {
        Object previous = DESCRIBING.get();
        DESCRIBING.set(controller);
        try {
            return element.getDescription(controller);
        } finally {
            if (previous == null) DESCRIBING.remove();
            else DESCRIBING.set(previous);
        }
    }

    public static boolean isDescribing(Object context) {
        return context != null && DESCRIBING.get() == context;
    }

    public static boolean onBuildPiece(Object controller, String piece, int horizontalOffset, int verticalOffset,
        int depthOffset) {
        Recording recording = activeFor(controller);
        if (recording == null) return false;
        recording.pieces.add(new PieceCall(piece, horizontalOffset, verticalOffset, depthOffset));
        return true;
    }

    public static Boolean onCheckPiece(Object controller, String piece, int horizontalOffset, int verticalOffset,
        int depthOffset) {
        Recording recording = activeFor(controller);
        if (recording == null) return null;
        return recording.pieceResults
            .getOrDefault(new PieceCall(piece, horizontalOffset, verticalOffset, depthOffset), true);
    }

    public static boolean onCheckCasingMin(Object controller, int required) {
        Recording recording = activeFor(controller);
        if (recording == null) return false;
        recording.casingMinimums.add(required);
        return true;
    }

    public static boolean onCheckHatches(Object controller, int min, int max, IHatchElement<?>... elements) {
        if (activeFor(controller) == null) return false;
        List<String> keys = new ArrayList<>();
        for (IHatchElement<?> element : elements) keys.addAll(element.getDescriptionLangKeys());
        return onCheckHatchKeys(controller, min, max, keys);
    }

    public static boolean onCheckHatchKeys(Object controller, int min, int max, List<String> hatchKeys) {
        Recording recording = activeFor(controller);
        if (recording == null) return false;
        recording.hatchRules.add(new HatchRule(hatchKeys, min, max, 0));
        return true;
    }

    public static boolean onCheckLayerHatches(Object controller, int minLayers, int perLayer,
        IHatchElement<?>... elements) {
        Recording recording = activeFor(controller);
        if (recording == null) return false;
        List<String> keys = new ArrayList<>();
        for (IHatchElement<?> element : elements) keys.addAll(element.getDescriptionLangKeys());
        HatchRule rule = new HatchRule(keys, minLayers * perLayer, UNLIMITED, perLayer);
        if (!recording.hatchRules.contains(rule)) recording.hatchRules.add(rule);
        return true;
    }

    public static List<PieceCall> recordConstruct(MTEEnhancedMultiBlockBase<?> controller, int stackSize,
        Set<String> channels) {
        ItemStack trigger = channelProbe(stackSize, channels);
        ChannelDataAccessor.setChannelData(trigger, GTNLStructureChannels.STRUCTURE_RENDER.get(), 1);
        return record(controller, "structure pieces", () -> controller.construct(trigger, true)).pieces;
    }

    public static <T> void recordChannels(T controller, IStructureElement<T> element, Set<String> channels) {
        try {
            element.getBlocksToPlace(controller, null, 0, 0, 0, channelProbe(1, channels), null);
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.debug(
                "Cannot probe a structure element of {} for its channels",
                controller.getClass()
                    .getSimpleName(),
                e);
        }
    }

    private static ItemStack channelProbe(int stackSize, Set<String> channels) {
        ItemStack trigger = new ItemStack(Items.stick, stackSize);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(CHANNEL_TAG, new ChannelRecorder(channels));
        trigger.setTagCompound(tag);
        return trigger;
    }

    public static CheckRecord recordChecks(MTEEnhancedMultiBlockBase<?> controller) {
        return recordChecks(controller, Collections.emptyMap());
    }

    public static CheckRecord recordChecks(MTEEnhancedMultiBlockBase<?> controller,
        Map<PieceCall, Boolean> pieceResults) {
        List<StructureError> errors = new ArrayList<>();
        Runnable check = () -> controller.checkMachine(controller.getBaseMetaTileEntity(), null, errors);
        Recording recording = record(controller, "structure checks", pieceResults, () -> {
            if (controller instanceof IMultiblockTooltipRules rules) rules.runStructureTooltipCheck(check);
            else check.run();
        });
        return new CheckRecord(
            recording.casingMinimums,
            recording.hatchRules,
            recording.failure == null ? null : recording.failure.toString());
    }

    private static Recording activeFor(Object controller) {
        Recording recording = RECORDING.get();
        return recording != null && recording.controller == controller ? recording : null;
    }

    private static Recording record(MTEEnhancedMultiBlockBase<?> controller, String subject, Runnable dryRun) {
        return record(controller, subject, Collections.emptyMap(), dryRun);
    }

    private static Recording record(MTEEnhancedMultiBlockBase<?> controller, String subject,
        Map<PieceCall, Boolean> pieceResults, Runnable dryRun) {
        Recording previous = RECORDING.get();
        Recording recording = new Recording(controller, pieceResults);
        RECORDING.set(recording);
        try {
            dryRun.run();
        } catch (RuntimeException e) {
            recording.failure = e;
            ScienceNotLeisure.LOG.warn(
                "Cannot read the {} of {} for its tooltip",
                subject,
                controller.getClass()
                    .getSimpleName(),
                e);
        } finally {
            if (previous == null) RECORDING.remove();
            else RECORDING.set(previous);
        }
        return recording;
    }

    private static class ChannelRecorder extends NBTTagCompound {

        private final Set<String> channels;

        private ChannelRecorder(Set<String> channels) {
            this.channels = channels;
        }

        @Override
        public boolean hasKey(String key, int type) {
            channels.add(key);
            return super.hasKey(key, type);
        }
    }

    private static class Recording {

        private final Object controller;
        private final Map<PieceCall, Boolean> pieceResults;
        private final List<PieceCall> pieces = new ArrayList<>();
        private final List<Integer> casingMinimums = new ArrayList<>();
        private final List<HatchRule> hatchRules = new ArrayList<>();
        private RuntimeException failure;

        private Recording(Object controller, Map<PieceCall, Boolean> pieceResults) {
            this.controller = controller;
            this.pieceResults = pieceResults;
        }
    }
}
