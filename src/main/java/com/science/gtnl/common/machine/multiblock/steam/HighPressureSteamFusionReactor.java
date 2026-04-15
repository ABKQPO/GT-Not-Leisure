package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class HighPressureSteamFusionReactor extends SteamMultiMachineBase<HighPressureSteamFusionReactor>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/high_pressure_steam_fusion_reactor";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SE_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 23;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 40;

    public HighPressureSteamFusionReactor(String aName) {
        super(aName);
    }

    public HighPressureSteamFusionReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("HighPressureSteamFusionReactorRecipeType");
    }

    @Override
    public IStructureDefinition<HighPressureSteamFusionReactor> getStructureDefinition() {
        return StructureDefinition.<HighPressureSteamFusionReactor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 31))
            .addElement('B', StructureUtility.ofBlock(BlockLoader.metaCasing02, 0))
            .addElement('C', GTStructureUtility.chainAllGlasses())
            .addElement('D', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement(
                'E',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(HighPressureSteamFusionReactor.class)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 32))
                        .dot(1)
                        .build(),
                    buildSteamBigInput(HighPressureSteamFusionReactor.class)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 32))
                        .dot(1)
                        .build(),
                    buildSteamInput(HighPressureSteamFusionReactor.class)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 32))
                        .dot(1)
                        .build(),
                    GTStructureUtility.buildHatchAdder(HighPressureSteamFusionReactor.class)
                        .atLeast(HatchElement.Maintenance, HatchElement.InputHatch, HatchElement.OutputHatch)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 32))
                        .dot(1)
                        .buildAndChain(),
                    GTStructureUtility.chainAllGlasses()))
            .build();
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
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.SteamFusionReactorRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public int getMaxParallelRecipes() {
        // Max call to prevent seeing -16 parallels in waila for unformed multi
        return 256;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPressureSteamFusionReactor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPressureSteamFusionReactor_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPressureSteamFusionReactor_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPressureSteamFusionReactor_03"))
            .beginStructureBlock(47, 7, 47, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_HighPressureSteamFusionReactor_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_HighPressureSteamFusionReactor_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) {
                return new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 32)),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR_ACTIVE)
                        .extFacing()
                        .build() };
            } else {
                return new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 32)),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR)
                        .extFacing()
                        .build() };
            }
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 32)) };

    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatches();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HighPressureSteamFusionReactor(this.mName);
    }
}
