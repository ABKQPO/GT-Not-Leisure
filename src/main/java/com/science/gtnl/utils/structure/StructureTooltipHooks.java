package com.science.gtnl.utils.structure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.structure.error.StructureError;

public class StructureTooltipHooks {

    public static final int UNLIMITED = Integer.MAX_VALUE;
    private static final ThreadLocal<Recording> RECORDING = new ThreadLocal<>();

    public record PieceCall(String piece, int horizontalOffset, int verticalOffset, int depthOffset) {}

    public record HatchRule(List<String> hatchKeys, int min, int max) {}

    public record CheckRecord(List<Integer> casingMinimums, List<HatchRule> hatchRules) {}

    private StructureTooltipHooks() {}

    public static boolean onBuildPiece(Object controller, String piece, int horizontalOffset, int verticalOffset,
        int depthOffset) {
        Recording recording = activeFor(controller);
        if (recording == null) return false;
        recording.pieces.add(new PieceCall(piece, horizontalOffset, verticalOffset, depthOffset));
        return true;
    }

    public static boolean onCheckPiece(Object controller) {
        return activeFor(controller) != null;
    }

    public static boolean onCheckCasingMin(Object controller, int required) {
        Recording recording = activeFor(controller);
        if (recording == null) return false;
        recording.casingMinimums.add(required);
        return true;
    }

    public static boolean onCheckHatches(Object controller, int min, int max, IHatchElement<?>... elements) {
        Recording recording = activeFor(controller);
        if (recording == null) return false;
        List<String> keys = new ArrayList<>();
        for (IHatchElement<?> element : elements) keys.addAll(element.getDescriptionLangKeys());
        recording.hatchRules.add(new HatchRule(keys, min, max));
        return true;
    }

    public static List<PieceCall> recordConstruct(MTEEnhancedMultiBlockBase<?> controller, int stackSize) {
        return record(
            controller,
            "structure pieces",
            () -> controller.construct(new ItemStack(Items.stick, stackSize), true)).pieces;
    }

    public static CheckRecord recordChecks(MTEEnhancedMultiBlockBase<?> controller) {
        List<StructureError> errors = new ArrayList<>();
        Recording recording = record(
            controller,
            "structure checks",
            () -> controller.checkMachine(controller.getBaseMetaTileEntity(), null, errors));
        return new CheckRecord(recording.casingMinimums, recording.hatchRules);
    }

    private static Recording activeFor(Object controller) {
        Recording recording = RECORDING.get();
        return recording != null && recording.controller == controller ? recording : null;
    }

    private static Recording record(MTEEnhancedMultiBlockBase<?> controller, String subject, Runnable dryRun) {
        Recording recording = new Recording(controller);
        RECORDING.set(recording);
        try {
            dryRun.run();
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn(
                "Cannot read the {} of {} for its tooltip",
                subject,
                controller.getClass()
                    .getSimpleName(),
                e);
        } finally {
            RECORDING.remove();
        }
        return recording;
    }

    private static class Recording {

        private final Object controller;
        private final List<PieceCall> pieces = new ArrayList<>();
        private final List<Integer> casingMinimums = new ArrayList<>();
        private final List<HatchRule> hatchRules = new ArrayList<>();

        private Recording(Object controller) {
            this.controller = controller;
        }
    }
}
