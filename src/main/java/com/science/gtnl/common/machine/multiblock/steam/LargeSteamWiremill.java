package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

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
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class LargeSteamWiremill extends SteamMultiMachineBase<LargeSteamWiremill> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LSW_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_steam_wiremill";

    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;

    private static final String[][] shape = StructureUtils.readStructureFromFile(LSW_STRUCTURE_FILE_PATH);

    public LargeSteamWiremill(String aName) {
        super(aName);
    }

    public LargeSteamWiremill(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IStructureDefinition<LargeSteamWiremill> getStructureDefinition() {
        return StructureDefinition.<LargeSteamWiremill>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))

            // A = Plated Bronze Bricks / Steel Machine Casing
            // Can be replaced by steam input, input bus, output bus, maintenance hatch.
            .addElement(
                'A',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofChain(
                        buildSteamWirelessInput(LargeSteamWiremill.class).casingIndex(getCasingTextureID())
                            .hint(1)
                            .build(),
                        buildSteamBigInput(LargeSteamWiremill.class).casingIndex(getCasingTextureID())
                            .hint(1)
                            .build(),
                        buildSteamInput(LargeSteamWiremill.class).casingIndex(getCasingTextureID())
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamWiremill.class).casingIndex(getCasingTextureID())
                            .hint(1)
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
                                        LargeSteamWiremill::getTierMachineCasing,
                                        ImmutableList.of(
                                            Pair.of(GregTechAPI.sBlockCasings1, 10),
                                            Pair.of(GregTechAPI.sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))

            // B = Bronze Pipe Machine Casing / Steel Pipe Machine Casing
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamWiremill::getTierPipeCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockCasings2, 12), Pair.of(GregTechAPI.sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing)))

            // C = Bronze Frame / Steel Frame
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamWiremill::getTierFrameCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockFrames, 300), Pair.of(GregTechAPI.sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing)))

            // D = Any valid GregTech glass
            .addElement('D', GTStructureUtility.chainAllGlasses())

            .build();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamWiremill(mName);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;

        checkHatch(errors);

        checkMachineTier(
            errors,
            20,
            tierMachineCasing == 1 && tierPipeCasing == 1 && tierFrameCasing == 1,
            tierMachineCasing == 2 && tierPipeCasing == 2 && tierFrameCasing == 2);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.wiremillRecipes;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (tierMachine == 1) {
            return 4;
        } else if (tierMachine == 2) {
            return 8;
        }
        return 4;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * 0.85 * tierMachine;
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
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {

        int id = tierMachine == 2 ? StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings2, 0)
            : StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 10);

        if (side == aFacing) {
            if (aActive) {
                return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialWireMillActive)
                    .extFacing()
                    .build() };
            }

            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(TexturesGtBlock.oMCDIndustrialWireMill)
                .extFacing()
                .build() };
        }

        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamWiremillRecipeType");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();

        tt.addMachineType(StatCollector.translateToLocal("LargeSteamWiremillRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamWiremill_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamWiremill_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamWiremill_02"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .beginStructureBlock(6, 5, 5, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamWiremill_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamWiremill_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();

        return tt;
    }
}
