package com.science.gtnl.common.machine.tooltip;

import java.util.List;

import gregtech.api.structure.IStructureChannels;

public record StructureSummary(Size firstTier, Size allTiers, boolean hollow, ControllerPosition controller,
    List<HatchLine> hatchLines, List<String> hatchKeys, List<Section> sections, List<IStructureChannels> channels,
    List<String> casingTierNames, String casingName, boolean layered) {

    public boolean hasMaterialTiers() {
        for (Section section : sections) {
            if (!section.materialTiers()
                .isEmpty()) return true;
        }
        return false;
    }

    public boolean hasIndependentTiers() {
        return sections.stream()
            .anyMatch(section -> section.details() != null);
    }

    public record Size(int width, int height, int length) {}

    public record ControllerPosition(int layer, boolean frontCenter) {}

    public record HatchLine(String count, List<String> names, List<String> locations) {}

    public record BlockLine(String count, String name, boolean tiered) {}

    public record MaterialTier(String header, List<BlockLine> blocks) {}

    public record TierDetails(Size size, boolean hollow, ControllerPosition controller, List<HatchLine> hatchLines) {}

    public record Section(List<BlockLine> blocks, List<MaterialTier> materialTiers, TierDetails details,
        List<String> casingRequirements) {}
}
