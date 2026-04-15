package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

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
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class LargeSteamExtruder extends SteamMultiMachineBase<LargeSteamExtruder> implements ISurvivalConstructable {

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamExtruder(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamExtruderRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LSE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_steam_extruder";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LSE_STRUCTURE_FILE_PATH);

    public LargeSteamExtruder(String aName) {
        super(aName);
    }

    public LargeSteamExtruder(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 7;
    private static final int DEPTH_OFF_SET = 0;

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = tierMachine == 2 ? StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings2, 0)
            : StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 10);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(TexturesGtBlock.oMCDIndustrialExtruderActive)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(TexturesGtBlock.oMCDIndustrialExtruder)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public IStructureDefinition<LargeSteamExtruder> getStructureDefinition() {
        return StructureDefinition.<LargeSteamExtruder>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofChain(
                        buildSteamWirelessInput(LargeSteamExtruder.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamBigInput(LargeSteamExtruder.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamInput(LargeSteamExtruder.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        GTStructureUtility.buildHatchAdder(LargeSteamExtruder.class)
                            .casingIndex(getCasingTextureID())
                            .dot(1)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                HatchElement.InputBus,
                                HatchElement.OutputBus,
                                HatchElement.Maintenance)
                            .buildAndChain(
                                StructureUtility.onElementPass(
                                    x -> ++x.mCountCasing,
                                    StructureUtility.ofBlocksTiered(
                                        LargeSteamExtruder::getTierMachineCasing,
                                        ImmutableList.of(
                                            Pair.of(GregTechAPI.sBlockCasings1, 10),
                                            Pair.of(GregTechAPI.sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamExtruder::getTierGearCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockCasings2, 2), Pair.of(GregTechAPI.sBlockCasings2, 3)),
                        -1,
                        (t, m) -> t.tierGearCasing = m,
                        t -> t.tierGearCasing)))
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamExtruder::getTierPipeCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockCasings2, 12), Pair.of(GregTechAPI.sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing)))
            .addElement(
                'D',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamExtruder::getTierFrameCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockFrames, 300), Pair.of(GregTechAPI.sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing)))
            .addElement(
                'E',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamExtruder::getTierBrickCasing,
                        ImmutableList
                            .of(Pair.of(BlockLoader.metaBlockColumn, 0), Pair.of(BlockLoader.metaBlockColumn, 1)),
                        -1,
                        (t, m) -> t.tierBrickCasing = m,
                        t -> t.tierBrickCasing)))
            .addElement(
                'F',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamExtruder::getTierMaterialBlockCasing,
                        ImmutableList.of(Pair.of(Blocks.iron_block, 0), Pair.of(GregTechAPI.sBlockMetal6, 13)),
                        -1,
                        (t, m) -> t.tierMaterialBlock = m,
                        t -> t.tierMaterialBlock)))
            .addElement('G', GTStructureUtility.chainAllGlasses())
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
            && tierMaterialBlock == 1
            && tierBrickCasing == 1
            && tierPipeCasing == 1
            && mCountCasing >= 20) {
            tierMachine = 1;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        if (tierGearCasing == 2 && tierMachineCasing == 2
            && tierFrameCasing == 2
            && tierMaterialBlock == 2
            && tierBrickCasing == 2
            && tierPipeCasing == 2
            && mCountCasing >= 20) {
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
            return 8;
        } else if (tierMachine == 2) {
            return 16;
        }
        return 8;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.extruderRecipes;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * 0.95 * tierMachine;
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
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamExtruderRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamExtruder_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamExtruder_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamExtruder_02"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .beginStructureBlock(5, 8, 5, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamExtruder_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamExtruder_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }
}
