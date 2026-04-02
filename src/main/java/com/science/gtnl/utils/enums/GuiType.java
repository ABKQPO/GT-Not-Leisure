package com.science.gtnl.utils.enums;

public enum GuiType {

    DetravScannerGUI(0),
    PortableBasicWorkBenchGUI(1),
    PortableAdvancedWorkBenchGUI(2),
    PortableFurnaceGUI(3),
    PortableAnvilGUI(4),
    PortableEnderChestGUI(5),
    PortableEnchantingGUI(6),
    PortableCompressedChestGUI(7),
    PortableInfinityChestGUI(8),
    PortableCopperChestGUI(9),
    PortableIronChestGUI(10),
    PortableSilverChestGUI(11),
    PortableSteelChestGUI(12),
    PortableGoldenChestGUI(13),
    PortableDiamondChestGUI(14),
    PortableCrystalChestGUI(15),
    PortableObsidianChestGUI(16),
    PortableNetheriteChestGUI(17),
    PortableDarkSteelChestGUI(18),
    DirePatternEncoderGUI(19),
    MEChiselGUI(20),
    SuperInterfaceGUI(21),
    CustomPriorityGUI(22),
    ActiveFormationPlaneGUI(23);

    private final int id;

    GuiType(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public static GuiType getGuiType(int id) {
        for (GuiType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown GUI ID: " + id);
    }
}
