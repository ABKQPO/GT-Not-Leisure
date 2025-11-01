package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.loader.BlockLoader.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.*;

import net.minecraft.init.Blocks;
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

public class LargeSteamCutting extends SteamMultiMachineBase<LargeSteamCutting> implements ISurvivalConstructable {

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamCutting(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamCuttingRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LSC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_steam_cutting";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LSC_STRUCTURE_FILE_PATH);

    public LargeSteamCutting(String aName) {
        super(aName);
    }

    public LargeSteamCutting(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = tierMachine == 2 ? StructureUtils.getTextureIndex(sBlockCasings2, 0)
            : StructureUtils.getTextureIndex(sBlockCasings1, 10);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(oMCDIndustrialCuttingMachineActive)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(oMCDIndustrialCuttingMachine)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public IStructureDefinition<LargeSteamCutting> getStructureDefinition() {
        return StructureDefinition.<LargeSteamCutting>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCutting::getTierIndustrialCasing,
                        ImmutableList.of(Pair.of(metaCasing02, 1), Pair.of(metaCasing02, 2)),
                        -1,
                        (t, m) -> t.tierIndustrialCasing = m,
                        t -> t.tierIndustrialCasing)))
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofChain(
                        buildSteamWirelessInput(LargeSteamCutting.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamBigInput(LargeSteamCutting.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamInput(LargeSteamCutting.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildHatchAdder(LargeSteamCutting.class).casingIndex(getCasingTextureID())
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
                                        LargeSteamCutting::getTierMachineCasing,
                                        ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamFormingPress::getTierGearCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 2), Pair.of(sBlockCasings2, 3)),
                        -1,
                        (t, m) -> t.tierGearCasing = m,
                        t -> t.tierGearCasing)))
            .addElement(
                'D',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCutting::getTierPipeCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 12), Pair.of(sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing)))
            .addElement(
                'E',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCutting::getTierFrameCasing,
                        ImmutableList.of(Pair.of(sBlockFrames, 300), Pair.of(sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing)))
            .addElement(
                'F',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCutting::getTierBrickCasing,
                        ImmutableList
                            .of(Pair.of(BlockLoader.metaBlockColumn, 0), Pair.of(BlockLoader.metaBlockColumn, 1)),
                        -1,
                        (t, m) -> t.tierBrickCasing = m,
                        t -> t.tierBrickCasing)))
            .addElement(
                'G',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCutting::getTierMachineFrame,
                        ImmutableList.of(Pair.of(metaBlockColumn, 4), Pair.of(metaBlockColumn, 5)),
                        -1,
                        (t, m) -> t.tierMachineFrame = m,
                        t -> t.tierMachineFrame)))
            .addElement('H', ofBlockAnyMeta(Blocks.diamond_block))
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
            && tierIndustrialCasing == 1
            && tierBrickCasing == 1
            && tierMachineFrame == 1
            && mCountCasing >= 5) {
            tierMachine = 1;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        if (tierGearCasing == 2 && tierMachineCasing == 2
            && tierFrameCasing == 2
            && tierIndustrialCasing == 2
            && tierBrickCasing == 2
            && tierMachineFrame == 2
            && mCountCasing >= 5) {
            tierMachine = 2;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        updateHatchTexture();
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (tierMachine == 1) {
            return 8;
        } else if (tierMachine == 2) {
            return 16;
        }
        return 8;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.cutterRecipes;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / 1.11 / tierMachine;
    }

    @Override
    public int getTierRecipes() {
        return 2;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamCuttingRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCutting_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCutting_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCutting_02"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 4, 5, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCutting_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCutting_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }
}
