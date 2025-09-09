package com.science.gtnl;

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
    PortableDarkSteelChestGUI;

    public static GuiType getGuiType(int id) {
        return GuiType.values()[id];
    }

    public int getID() {
        return this.ordinal();
    }

}
