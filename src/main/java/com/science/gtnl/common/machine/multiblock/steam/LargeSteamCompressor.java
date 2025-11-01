package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import javax.annotation.Nonnull;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.metadata.CompressionTierKey;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class LargeSteamCompressor extends SteamMultiMachineBase<LargeSteamCompressor>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LSC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_steam_compressor";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LSC_STRUCTURE_FILE_PATH);

    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 5;
    private static final int DEPTH_OFF_SET = 0;

    public LargeSteamCompressor(String aName) {
        super(aName);
    }

    public LargeSteamCompressor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamCompressor(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamCompressorRecipeType");
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = tierMachine == 2 ? StructureUtils.getTextureIndex(sBlockCasings2, 0)
            : StructureUtils.getTextureIndex(sBlockCasings1, 10);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR_ACTIVE)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public IStructureDefinition<LargeSteamCompressor> getStructureDefinition() {
        return StructureDefinition.<LargeSteamCompressor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofChain(
                        buildSteamWirelessInput(LargeSteamCompressor.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamBigInput(LargeSteamCompressor.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamInput(LargeSteamCompressor.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildHatchAdder(LargeSteamCompressor.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                InputHatch,
                                Maintenance)
                            .buildAndChain(
                                onElementPass(
                                    x -> ++x.mCountCasing,
                                    ofBlocksTiered(
                                        LargeSteamCompressor::getTierMachineCasing,
                                        ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCompressor::getTierGearCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 2), Pair.of(sBlockCasings2, 3)),
                        -1,
                        (t, m) -> t.tierGearCasing = m,
                        t -> t.tierGearCasing)))
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCompressor::getTierFrameCasing,
                        ImmutableList.of(Pair.of(sBlockFrames, 300), Pair.of(sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing)))
            .addElement(
                'D',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCompressor::getTierMaterialBlockCasing,
                        ImmutableList.of(Pair.of(Blocks.iron_block, 0), Pair.of(sBlockMetal6, 13)),
                        -1,
                        (t, m) -> t.tierMaterialBlock = m,
                        t -> t.tierMaterialBlock)))
            .addElement('E', chainAllGlasses())
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatches())
            return false;
        if (tierMaterialBlock == 1 && tierMachineCasing == 1
            && tierFrameCasing == 1
            && tierGearCasing == 1
            && mCountCasing >= 60) {
            tierMachine = 1;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        if (tierMaterialBlock == 2 && tierMachineCasing == 2
            && tierFrameCasing == 2
            && tierGearCasing == 2
            && mCountCasing >= 60) {
            tierMachine = 2;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (tierMachine == 1) {
            return 16;
        } else if (tierMachine == 2) {
            return 32;
        }
        return 16;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.compressorRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.getMetadataOrDefault(CompressionTierKey.INSTANCE, 0) != 0) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
                return super.validateRecipe(recipe);
            }

            @Override
            @Nonnull
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(configSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * 0.8 * tierMachine;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / 2.0 / tierMachine;
    }

    @Override
    public int getTierRecipes() {
        return 2;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamCompressorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCompressor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCompressor_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCompressor_02"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(7, 7, 7, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCompressor_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCompressor_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCompressor_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCompressor_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }
}
