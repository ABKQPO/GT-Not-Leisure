package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtPlusPlus.core.block.ModBlocks.blockCustomMachineCasings;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class LargeSteamCrusher extends SteamMultiMachineBase<LargeSteamCrusher> implements ISurvivalConstructable {

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamCrusher(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamCrusherRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LSC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_steam_crusher";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LSC_STRUCTURE_FILE_PATH);

    public LargeSteamCrusher(String aName) {
        super(aName);
    }

    public LargeSteamCrusher(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 6;
    private static final int DEPTH_OFF_SET = 0;

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = tierMachine == 2 ? StructureUtils.getTextureIndex(sBlockCasings2, 0)
            : StructureUtils.getTextureIndex(sBlockCasings1, 10);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR_ACTIVE)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public IStructureDefinition<LargeSteamCrusher> getStructureDefinition() {
        return StructureDefinition.<LargeSteamCrusher>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofChain(
                        buildSteamWirelessInput(LargeSteamCrusher.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamBigInput(LargeSteamCrusher.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamInput(LargeSteamCrusher.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildHatchAdder(LargeSteamCrusher.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                Maintenance)
                            .buildAndChain(
                                onElementPass(
                                    x -> ++x.mCountCasing,
                                    ofBlocksTiered(
                                        LargeSteamCrusher::getTierMachineCasing,
                                        ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCrusher::getTierGearCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 2), Pair.of(sBlockCasings2, 3)),
                        -1,
                        (t, m) -> t.tierGearCasing = m,
                        t -> t.tierGearCasing)))
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCrusher::getTierFrameCasing,
                        ImmutableList.of(Pair.of(sBlockFrames, 300), Pair.of(sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing)))
            .addElement(
                'D',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCrusher::getTierPlatedCasing,
                        ImmutableList.of(Pair.of(blockCustomMachineCasings, 0), Pair.of(sBlockCasings2, 0)),
                        -1,
                        (t, m) -> t.tierPlatedCasing = m,
                        t -> t.tierPlatedCasing)))
            .addElement(
                'E',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCrusher::getTierBrickCasing,
                        ImmutableList
                            .of(Pair.of(BlockLoader.metaBlockColumn, 0), Pair.of(BlockLoader.metaBlockColumn, 1)),
                        -1,
                        (t, m) -> t.tierBrickCasing = m,
                        t -> t.tierBrickCasing)))
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
        if (tierGearCasing == 1 && tierMachineCasing == 1
            && tierFrameCasing == 1
            && tierPlatedCasing == 1
            && tierBrickCasing == 1
            && mCountCasing >= 100) {
            tierMachine = 1;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        if (tierGearCasing == 2 && tierMachineCasing == 2
            && tierFrameCasing == 2
            && tierPlatedCasing == 2
            && tierBrickCasing == 2
            && mCountCasing >= 100) {
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
            return 128;
        } else if (tierMachine == 2) {
            return 256;
        }
        return 128;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.maceratorRecipes;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * 0.8 * tierMachine;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / 10.0 / tierMachine;
    }

    @Override
    public int getTierRecipes() {
        return (tierMachine == 2) ? 3 : 2;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamCrusherRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrusher_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrusher_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrusher_02"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(7, 8, 11, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrusher_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrusher_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .toolTipFinisher();
        return tt;
    }
}
