package com.science.gtnl.api.casing;

import java.util.Objects;

import net.minecraft.block.Block;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.data.BlockSupplier;
import com.science.gtnl.common.block.casings.casing.MetaCasing;
import com.science.gtnl.loader.BlockLoader;

import gregtech.api.casing.ICasing;
import gregtech.api.util.GTUtility;

public enum GTNLCasings implements ICasing {

    // Machine casings of the first meta casing block.
    TestCasing(() -> BlockLoader.metaCasing, 0, casingTexture(BlockLoader.metaCasing, 0)),
    SteamAssemblyCasing(() -> BlockLoader.metaCasing, 1, casingTexture(BlockLoader.metaCasing, 1)),
    HeatVent(() -> BlockLoader.metaCasing, 2, casingTexture(BlockLoader.metaCasing, 2)),
    SlicingBlades(() -> BlockLoader.metaCasing, 3, casingTexture(BlockLoader.metaCasing, 3)),
    NeutroniumPipeCasing(() -> BlockLoader.metaCasing, 4, casingTexture(BlockLoader.metaCasing, 4)),
    NeutroniumGearbox(() -> BlockLoader.metaCasing, 5, casingTexture(BlockLoader.metaCasing, 5)),
    Laser_Cooling_Casing(() -> BlockLoader.metaCasing, 6, casingTexture(BlockLoader.metaCasing, 6)),
    Antifreeze_Heatproof_Machine_Casing(() -> BlockLoader.metaCasing, 7, casingTexture(BlockLoader.metaCasing, 7)),
    MolybdenumDisilicideCoil(() -> BlockLoader.metaCasing, 8, casingTexture(BlockLoader.metaCasing, 8)),
    EnergeticPhotovoltaicBlock(() -> BlockLoader.metaCasing, 9, casingTexture(BlockLoader.metaCasing, 9)),
    AdvancedPhotovoltaicBlock(() -> BlockLoader.metaCasing, 10, casingTexture(BlockLoader.metaCasing, 10)),
    VibrantPhotovoltaicBlock(() -> BlockLoader.metaCasing, 11, casingTexture(BlockLoader.metaCasing, 11)),
    TungstensteelGearbox(() -> BlockLoader.metaCasing, 12, casingTexture(BlockLoader.metaCasing, 12)),
    DimensionallyStableCasing(() -> BlockLoader.metaCasing, 13, casingTexture(BlockLoader.metaCasing, 13)),
    PressureBalancedCasing(() -> BlockLoader.metaCasing, 14, casingTexture(BlockLoader.metaCasing, 14)),
    ABSUltraSolidCasing(() -> BlockLoader.metaCasing, 15, casingTexture(BlockLoader.metaCasing, 15)),
    GravitationalFocusingLensBlock(() -> BlockLoader.metaCasing, 16, casingTexture(BlockLoader.metaCasing, 16)),
    GaiaStabilizedForceFieldCasing(() -> BlockLoader.metaCasing, 17, casingTexture(BlockLoader.metaCasing, 17)),
    HyperCore(() -> BlockLoader.metaCasing, 18, casingTexture(BlockLoader.metaCasing, 18)),
    ChemicallyResistantCasing(() -> BlockLoader.metaCasing, 19, casingTexture(BlockLoader.metaCasing, 19)),
    UltraPoweredCasing(() -> BlockLoader.metaCasing, 20, casingTexture(BlockLoader.metaCasing, 20)),
    SteamgateRingBlock(() -> BlockLoader.metaCasing, 21, casingTexture(BlockLoader.metaCasing, 21)),
    SteamgateChevronBlock(() -> BlockLoader.metaCasing, 22, casingTexture(BlockLoader.metaCasing, 22)),
    IronReinforcedWood(() -> BlockLoader.metaCasing, 23, casingTexture(BlockLoader.metaCasing, 23)),
    BronzeReinforcedWood(() -> BlockLoader.metaCasing, 24, casingTexture(BlockLoader.metaCasing, 24)),
    SteelReinforcedWood(() -> BlockLoader.metaCasing, 25, casingTexture(BlockLoader.metaCasing, 25)),
    BreelPipeCasing(() -> BlockLoader.metaCasing, 26, casingTexture(BlockLoader.metaCasing, 26)),
    StronzeWrappedCasing(() -> BlockLoader.metaCasing, 27, casingTexture(BlockLoader.metaCasing, 27)),
    HydraulicAssemblingCasing(() -> BlockLoader.metaCasing, 28, casingTexture(BlockLoader.metaCasing, 28)),
    HyperPressureBreelCasing(() -> BlockLoader.metaCasing, 29, casingTexture(BlockLoader.metaCasing, 29)),
    BreelPlatedCasing(() -> BlockLoader.metaCasing, 30, casingTexture(BlockLoader.metaCasing, 30)),
    SteamCompactPipeCasing(() -> BlockLoader.metaCasing, 31, casingTexture(BlockLoader.metaCasing, 31)),

    // Machine casings of the second meta casing block.
    VibrationSafeCasing(() -> BlockLoader.metaCasing02, 0, casingTexture(BlockLoader.metaCasing02, 0)),
    IndustrialSteamCasing(() -> BlockLoader.metaCasing02, 1, casingTexture(BlockLoader.metaCasing02, 1)),
    AdvancedIndustrialSteamCasing(() -> BlockLoader.metaCasing02, 2, casingTexture(BlockLoader.metaCasing02, 2)),
    StainlessSteelGearBox(() -> BlockLoader.metaCasing02, 3, casingTexture(BlockLoader.metaCasing02, 3)),
    AssemblerMatrixFrame(() -> BlockLoader.metaCasing02, 4, casingTexture(BlockLoader.metaCasing02, 4)),
    AssemblerMatrixWall(() -> BlockLoader.metaCasing02, 5, casingTexture(BlockLoader.metaCasing02, 5)),
    AssemblerMatrixPatternCore(() -> BlockLoader.metaCasing02, 6, casingTexture(BlockLoader.metaCasing02, 6)),
    AssemblerMatrixCrafterCore(() -> BlockLoader.metaCasing02, 7, casingTexture(BlockLoader.metaCasing02, 7)),
    AssemblerMatrixSingularityCrafterCore(() -> BlockLoader.metaCasing02, 8,
        casingTexture(BlockLoader.metaCasing02, 8)),
    AssemblerMatrixSpeedCore(() -> BlockLoader.metaCasing02, 9, casingTexture(BlockLoader.metaCasing02, 9)),
    QuantumComputerCasing(() -> BlockLoader.metaCasing02, 10, casingTexture(BlockLoader.metaCasing02, 10)),
    QuantumComputerUnit(() -> BlockLoader.metaCasing02, 11, casingTexture(BlockLoader.metaCasing02, 11)),
    QuantumComputerCraftingStorage128M(() -> BlockLoader.metaCasing02, 12, casingTexture(BlockLoader.metaCasing02, 12)),
    QuantumComputerCraftingStorage256M(() -> BlockLoader.metaCasing02, 13, casingTexture(BlockLoader.metaCasing02, 13)),
    QuantumComputerDataEntangler(() -> BlockLoader.metaCasing02, 14, casingTexture(BlockLoader.metaCasing02, 14)),
    QuantumComputerAccelerator(() -> BlockLoader.metaCasing02, 15, casingTexture(BlockLoader.metaCasing02, 15)),
    QuantumComputerMultiThreader(() -> BlockLoader.metaCasing02, 16, casingTexture(BlockLoader.metaCasing02, 16)),
    QuantumComputerCore(() -> BlockLoader.metaCasing02, 17, casingTexture(BlockLoader.metaCasing02, 17)),
    AssemblerMatrixDebugCrafterCore(() -> BlockLoader.metaCasing02, 18, casingTexture(BlockLoader.metaCasing02, 18)),
    QuantumComputerSingularityCore(() -> BlockLoader.metaCasing02, 19, casingTexture(BlockLoader.metaCasing02, 19)),
    CompressedFurnaceCasing(() -> BlockLoader.metaCasing02, 20, casingTexture(BlockLoader.metaCasing02, 20)),

    // Column shaped casings and machine frames.
    BronzeBrickCasing(() -> BlockLoader.metaBlockColumn, 0, -1),
    SteelBrickCasing(() -> BlockLoader.metaBlockColumn, 1, -1),
    CrushingWheels(() -> BlockLoader.metaBlockColumn, 2, -1),
    SolarBoilingCell(() -> BlockLoader.metaBlockColumn, 3, -1),
    BronzeMachineFrame(() -> BlockLoader.metaBlockColumn, 4, -1),
    SteelMachineFrame(() -> BlockLoader.metaBlockColumn, 5, -1),

    // Glass casings.
    GaiaGlass(() -> BlockLoader.metaBlockGlass, 0, -1),
    TerraGlass(() -> BlockLoader.metaBlockGlass, 1, -1),
    FusionGlass(() -> BlockLoader.metaBlockGlass, 2, -1),
    ConcentratingSieveMesh(() -> BlockLoader.metaBlockGlass, 3, -1),

    // Glowing lamp blocks.
    FortifyGlowstone(() -> BlockLoader.metaBlockGlow, 0, -1),
    BlackLamp(() -> BlockLoader.metaBlockGlow, 1, -1),
    BlackLampBorderless(() -> BlockLoader.metaBlockGlow, 2, -1),
    PinkLamp(() -> BlockLoader.metaBlockGlow, 3, -1),
    PinkLampBorderless(() -> BlockLoader.metaBlockGlow, 4, -1),
    RedLamp(() -> BlockLoader.metaBlockGlow, 5, -1),
    RedLampBorderless(() -> BlockLoader.metaBlockGlow, 6, -1),
    OrangeLamp(() -> BlockLoader.metaBlockGlow, 7, -1),
    OrangeLampBorderless(() -> BlockLoader.metaBlockGlow, 8, -1),
    YellowLamp(() -> BlockLoader.metaBlockGlow, 9, -1),
    YellowLampBorderless(() -> BlockLoader.metaBlockGlow, 10, -1),
    GreenLamp(() -> BlockLoader.metaBlockGlow, 11, -1),
    GreenLampBorderless(() -> BlockLoader.metaBlockGlow, 12, -1),
    LimeLamp(() -> BlockLoader.metaBlockGlow, 13, -1),
    LimeLampBorderless(() -> BlockLoader.metaBlockGlow, 14, -1),
    BlueLamp(() -> BlockLoader.metaBlockGlow, 15, -1),
    BlueLampBorderless(() -> BlockLoader.metaBlockGlow, 16, -1),
    LightBlueLamp(() -> BlockLoader.metaBlockGlow, 17, -1),
    LightBlueLampBorderless(() -> BlockLoader.metaBlockGlow, 18, -1),
    CyanLamp(() -> BlockLoader.metaBlockGlow, 19, -1),
    CyanLampBorderless(() -> BlockLoader.metaBlockGlow, 20, -1),
    BrownLamp(() -> BlockLoader.metaBlockGlow, 21, -1),
    BrownLampBorderless(() -> BlockLoader.metaBlockGlow, 22, -1),
    MagentaLamp(() -> BlockLoader.metaBlockGlow, 23, -1),
    MagentaLampBorderless(() -> BlockLoader.metaBlockGlow, 24, -1),
    PurpleLamp(() -> BlockLoader.metaBlockGlow, 25, -1),
    PurpleLampBorderless(() -> BlockLoader.metaBlockGlow, 26, -1),
    GrayLamp(() -> BlockLoader.metaBlockGlow, 27, -1),
    GrayLampBorderless(() -> BlockLoader.metaBlockGlow, 28, -1),
    LightGrayLamp(() -> BlockLoader.metaBlockGlow, 29, -1),
    LightGrayLampBorderless(() -> BlockLoader.metaBlockGlow, 30, -1),
    WhiteLamp(() -> BlockLoader.metaBlockGlow, 31, -1),
    WhiteLampBorderless(() -> BlockLoader.metaBlockGlow, 32, -1),;

    public final BlockSupplier blockGetter;
    private volatile Block block;
    public final int meta;
    public final int textureId;

    GTNLCasings(BlockSupplier blockGetter, int meta, int textureId) {
        this.blockGetter = blockGetter;
        this.meta = meta;
        this.textureId = textureId;
    }

    @Override
    public @NotNull Block getBlock() {
        if (block == null) {
            block = Objects.requireNonNull(blockGetter.get(), "Block for casing " + name() + " was null");
        }

        return block;
    }

    @Override
    public int getBlockMeta() {
        return meta;
    }

    @Override
    public int getTextureId() {
        if (textureId == -1) {
            throw new UnsupportedOperationException(
                "Casing " + name() + " does not have a casing texture; The result of getTextureId() is undefined.");
        }

        return textureId;
    }

    @Override
    public boolean isTiered() {
        return false;
    }

    private static int casingTexture(MetaCasing casing, int meta) {
        return GTUtility.getTextureId(casing.getTexturePageIndex(), casing.getTextureIndexInPage(meta));
    }
}
