package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

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
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class LargeSteamCentrifuge extends SteamMultiMachineBase<LargeSteamCentrifuge>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String LSCen_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_steam_centrifuge";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LSCen_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 9;
    private static final int DEPTH_OFF_SET = 0;

    public LargeSteamCentrifuge(String aName) {
        super(aName);
    }

    public LargeSteamCentrifuge(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamCentrifuge(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamCentrifugeRecipeType");
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = tierMachine == 2 ? StructureUtils.getTextureIndex(sBlockCasings2, 0)
            : StructureUtils.getTextureIndex(sBlockCasings1, 10);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(TexturesGtBlock.oMCDIndustrialThermalCentrifugeActive)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(TexturesGtBlock.oMCDIndustrialThermalCentrifuge)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public IStructureDefinition<LargeSteamCentrifuge> getStructureDefinition() {
        return StructureDefinition.<LargeSteamCentrifuge>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofChain(
                        buildSteamWirelessInput(LargeSteamCentrifuge.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamBigInput(LargeSteamCentrifuge.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamInput(LargeSteamCentrifuge.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildHatchAdder(LargeSteamCentrifuge.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                InputHatch,
                                OutputHatch,
                                Maintenance)
                            .buildAndChain(
                                onElementPass(
                                    x -> ++x.mCountCasing,
                                    ofBlocksTiered(
                                        LargeSteamCentrifuge::getTierMachineCasing,
                                        ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCentrifuge::getTierGearCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 2), Pair.of(sBlockCasings2, 3)),
                        -1,
                        (t, m) -> t.tierGearCasing = m,
                        t -> t.tierGearCasing)))
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCentrifuge::getTierPipeCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 12), Pair.of(sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing)))
            .addElement(
                'D',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamCentrifuge::getTierFrameCasing,
                        ImmutableList.of(Pair.of(sBlockFrames, 300), Pair.of(sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing)))
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
        if (tierPipeCasing == 1 && tierMachineCasing == 1
            && tierFrameCasing == 1
            && tierGearCasing == 1
            && mCountCasing >= 60) {
            tierMachine = 1;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        if (tierPipeCasing == 2 && tierMachineCasing == 2
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
        return GTPPRecipeMaps.centrifugeNonCellRecipes;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * tierMachine;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / 1.43 / tierMachine;
    }

    @Override
    public int getTierRecipes() {
        return 2;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamCentrifugeRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_02"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(7, 10, 7, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }
}
