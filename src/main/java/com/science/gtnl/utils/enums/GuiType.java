package com.science.gtnl.utils.enums;

public enum GuiType {

    DetravScannerGUI,
    PortableBasicWorkBenchGUI,
    PortableAdvancedWorkBenchGUI,
    PortableFurnaceGUI,
    PortableAnvilGUI,
    PortableEnderChestGUI,
    PortableEnchantingGUI,
    PortableCompressedChestGUI,
    PortableInfinityChestGUI,
    PortableCopperChestGUI,
    PortableIronChestGUI,
    PortableSilverChestGUI,
    PortableSteelChestGUI,
    PortableGoldenChestGUI,
    PortableDiamondChestGUI,
    PortableCrystalChestGUI,
    PortableObsidianChestGUI,
    PortableNetheriteChestGUI,
    PortableDarkSteelChestGUI,
    DirePatternEncoderGUI;

    public static GuiType getGuiType(int id) {
        return GuiType.values()[id];
    }

    public int getID() {
        return this.ordinal();
    }

}
