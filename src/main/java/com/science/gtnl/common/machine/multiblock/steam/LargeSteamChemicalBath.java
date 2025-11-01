package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.GTPlusPlus;
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

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class LargeSteamChemicalBath extends SteamMultiMachineBase<LargeSteamChemicalBath>
    implements ISurvivalConstructable {

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamChemicalBath(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamChemicalBathRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LSCB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/large_steam_chemical_bath";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LSCB_STRUCTURE_FILE_PATH);

    public LargeSteamChemicalBath(String aName) {
        super(aName);
    }

    public LargeSteamChemicalBath(int aID, String aName, String aNameRegional) {
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
    public IStructureDefinition<LargeSteamChemicalBath> getStructureDefinition() {
        return StructureDefinition.<LargeSteamChemicalBath>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(GameRegistry.findBlock(GTPlusPlus.ID, "blockBlockPotin"), 0))
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofChain(
                        buildSteamWirelessInput(LargeSteamChemicalBath.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamBigInput(LargeSteamChemicalBath.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildSteamInput(LargeSteamChemicalBath.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .build(),
                        buildHatchAdder(LargeSteamChemicalBath.class).casingIndex(getCasingTextureID())
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
                                        LargeSteamChemicalBath::getTierMachineCasing,
                                        ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        LargeSteamChemicalBath::getTierFrameCasing,
                        ImmutableList.of(Pair.of(sBlockFrames, 300), Pair.of(sBlockFrames, 305)),
                        -1,
                        (t, m) -> t.tierFrameCasing = m,
                        t -> t.tierFrameCasing)))
            .addElement('D', chainAllGlasses())
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
        if (tierMachineCasing == 1 && tierFrameCasing == 1 && mCountCasing >= 150) {
            tierMachine = 1;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        if (tierMachineCasing == 2 && tierFrameCasing == 2 && mCountCasing >= 150) {
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
        return RecipeMaps.chemicalBathRecipes;
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamChemicalBathRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamChemicalBath_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamChemicalBath_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamChemicalBath_02"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 5, 10, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamChemicalBath_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamChemicalBath_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamChemicalBath_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamChemicalBath_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }
}
