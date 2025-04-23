package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtPlusPlus.core.block.ModBlocks.blockCustomMachineCasings;

import javax.annotation.Nonnull;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.common.machine.multiMachineClasses.SteamMultiMachineBase;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings1;
import gregtech.common.blocks.BlockCasings2;

public class LargeSteamSifter extends SteamMultiMachineBase<LargeSteamSifter> implements ISurvivalConstructable {

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamSifter(this.mName);
    }

    @Override
    public String getMachineType() {
        return TextLocalization.LargeSteamSifterRecipeType;
    }

    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<LargeSteamSifter> STRUCTURE_DEFINITION = null;
    public static final String LSC_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/large_steam_sifter";
    public static String[][] shape = StructureUtils.readStructureFromFile(LSC_STRUCTURE_FILE_PATH);

    public LargeSteamSifter(String aName) {
        super(aName);
    }

    public LargeSteamSifter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public static final int HORIZONTAL_OFF_SET = 2;
    public static final int VERTICAL_OFF_SET = 5;
    public static final int DEPTH_OFF_SET = 0;

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = tierMachine == 2 ? ((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0)
            : ((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(10);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_CENTRIFUGE_ACTIVE)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_CENTRIFUGE)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public IStructureDefinition<LargeSteamSifter> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeSteamSifter>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    ofChain(
                        buildSteamBigInput(LargeSteamSifter.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamInput(LargeSteamSifter.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildHatchAdder(LargeSteamSifter.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                InputHatch,
                                OutputHatch)
                            .buildAndChain(
                                onElementPass(
                                    x -> ++x.tCountCasing,
                                    withChannel(
                                        "tier",
                                        ofBlocksTiered(
                                            LargeSteamSifter::getTierMachineCasing,
                                            ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                            -1,
                                            (t, m) -> t.tierMachineCasing = m,
                                            t -> t.tierMachineCasing))))))
                .addElement(
                    'B',
                    ofBlocksTiered(
                        LargeSteamSifter::getTierGearCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 2), Pair.of(sBlockCasings2, 3)),
                        -1,
                        (t, m) -> t.tierGearCasing = m,
                        t -> t.tierGearCasing))
                .addElement(
                    'C',
                    ofBlocksTiered(
                        LargeSteamSifter::getTierFrameCasing,
                        ImmutableList.of(Pair.of(sBlockFrames, 300), Pair.of(sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing))
                .addElement(
                    'D',
                    ofBlocksTiered(
                        LargeSteamSifter::getTierPlatedCasing,
                        ImmutableList.of(Pair.of(blockCustomMachineCasings, 0), Pair.of(sBlockCasings2, 0)),
                        -1,
                        (t, m) -> t.tierMaterialBlock = m,
                        t -> t.tierMaterialBlock))
                .addElement('E', ofBlockAnyMeta(Blocks.glass))
                .build();

        }
        return STRUCTURE_DEFINITION;
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
        return this.survivialBuildPiece(
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
        tierMachine = 0;
        tierMaterialBlock = -1;
        tierMachineCasing = -1;
        tierFrameCasing = -1;
        tierGearCasing = -1;
        tCountCasing = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        if (tierMaterialBlock < 0 && tierMachineCasing < 0 && tierFrameCasing < 0 && tierGearCasing < 0) return false;
        if (tierMaterialBlock == 1 && tierMachineCasing == 1
            && tierFrameCasing == 1
            && tierGearCasing == 1
            && tCountCasing >= 45
            && checkHatches()) {
            tierMachine = 1;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        if (tierMaterialBlock == 2 && tierMachineCasing == 2
            && tierFrameCasing == 2
            && tierGearCasing == 2
            && tCountCasing >= 45
            && checkHatches()) {
            tierMachine = 2;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        getCasingTextureID();
        updateHatchTexture();
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
        return RecipeMaps.sifterRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (availableVoltage < recipe.mEUt) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                } else return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Override
            @Nonnull
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).limitOverclockCount(Math.min(4, RecipeOcCount))
                    .setEUtDiscount(0.8 * tierMachine)
                    .setSpeedBoost(1 / 1.33 / tierMachine);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getTierRecipes() {
        return 3;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.LargeSteamSifterRecipeType)
            .addInfo(TextLocalization.Tooltip_LargeSteamSifter_00)
            .addInfo(TextLocalization.Tooltip_LargeSteamSifter_01)
            .addInfo(TextLocalization.Tooltip_LargeSteamSifter_02)
            .addInfo(TextLocalization.HighPressureTooltipNotice)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(5, 7, 5, false)
            .addInputBus(TextLocalization.Tooltip_LargeSteamSifter_Casing, 1)
            .addOutputBus(TextLocalization.Tooltip_LargeSteamSifter_Casing, 1)
            .addInputHatch(TextLocalization.Tooltip_LargeSteamSifter_Casing, 1)
            .addOutputHatch(TextLocalization.Tooltip_LargeSteamSifter_Casing, 1)
            .toolTipFinisher();
        return tt;
    }
}
