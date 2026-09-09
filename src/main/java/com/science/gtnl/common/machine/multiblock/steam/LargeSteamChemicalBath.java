package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

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

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class LargeSteamChemicalBath extends SteamMultiMachineBase<LargeSteamChemicalBath>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LSCB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/large_steam_chemical_bath";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LSCB_STRUCTURE_FILE_PATH);

    public LargeSteamChemicalBath(String aName) {
        super(aName);
    }

    public LargeSteamChemicalBath(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.large_steam_chemical_bath.name";
    }

    @Override
    public IStructureDefinition<LargeSteamChemicalBath> getStructureDefinition() {
        return StructureDefinition.<LargeSteamChemicalBath>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses())
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofChain(
                        buildSteamWirelessInput(LargeSteamChemicalBath.class).casingIndex(getCasingTextureID())
                            .hint(1)
                            .build(),
                        buildSteamBigInput(LargeSteamChemicalBath.class).casingIndex(getCasingTextureID())
                            .hint(1)
                            .build(),
                        buildSteamInput(LargeSteamChemicalBath.class).casingIndex(getCasingTextureID())
                            .hint(1)
                            .build(),
                        GTStructureUtility.buildHatchAdder(LargeSteamChemicalBath.class)
                            .casingIndex(getCasingTextureID())
                            .hint(1)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                HatchElement.InputBus,
                                HatchElement.OutputBus,
                                HatchElement.InputHatch,
                                HatchElement.OutputHatch,
                                HatchElement.Maintenance)
                            .buildAndChain(
                                StructureUtility.onElementPass(
                                    x -> ++x.mCountCasing,
                                    StructureUtility.ofBlocksTiered(
                                        LargeSteamChemicalBath::getTierMachineCasing,
                                        ImmutableList.of(
                                            Pair.of(GregTechAPI.sBlockCasings1, 10),
                                            Pair.of(GregTechAPI.sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamChemicalBath::getTierPipeCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockCasings2, 12), Pair.of(GregTechAPI.sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing)))
            .addElement(
                'D',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeSteamChemicalBath::getTierFrameCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockFrames, 300), Pair.of(GregTechAPI.sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing)))
            .build();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamChemicalBath(mName);
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
            43,
            tierMachineCasing == 1 && tierPipeCasing == 1 && tierFrameCasing == 1,
            tierMachineCasing == 2 && tierPipeCasing == 2 && tierFrameCasing == 2);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.chemicalBathRecipes;
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
    public double getEUtDiscount() {
        return super.getEUtDiscount() * 0.8 * tierMachine;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / 1.25 / tierMachine;
    }

    @Override
    public int getTierRecipes() {
        return 6;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = tierMachine == 2 ? StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings2, 0)
            : StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 10);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.recipe_type");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam.high_pressure.tooltip"))
            .beginStructureBlock(9, 5, 10, false)
            .addInputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.tooltip.casing"),
                1)
            .addOutputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.tooltip.casing"),
                1)
            .addInputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.tooltip.casing"),
                1)
            .addOutputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.large_steam_chemical_bath.tooltip.casing"),
                1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }
}
