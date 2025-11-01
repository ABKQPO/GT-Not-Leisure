package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_STEAM_MANUFACTURER;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_STEAM_MANUFACTURER_ACTIVE;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class SteamManufacturer extends SteamMultiMachineBase<SteamManufacturer> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_manufacturer";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SM_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 5;
    private static final int DEPTH_OFF_SET = 0;

    public SteamManufacturer(String aName) {
        super(aName);
    }

    public SteamManufacturer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamManufacturerRecipeType");
    }

    @Override
    public IStructureDefinition<SteamManufacturer> getStructureDefinition() {
        return StructureDefinition.<SteamManufacturer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(BlockLoader.metaCasing, 26))
            .addElement('B', ofBlock(BlockLoader.metaCasing, 28))
            .addElement(
                'C',
                ofChain(
                    buildSteamWirelessInput(SteamManufacturer.class)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 30))
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamManufacturer.class)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 30))
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamManufacturer.class).casingIndex(GTUtility.getTextureId((byte) 116, (byte) 30))
                        .dot(1)
                        .build(),
                    buildHatchAdder(SteamManufacturer.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            InputBus,
                            SteamHatchElement.OutputBus_Steam,
                            OutputBus,
                            Maintenance)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 30))
                        .dot(1)
                        .buildAndChain(),
                    ofBlock(BlockLoader.metaCasing, 30)))
            .addElement('D', ofBlock(GregTechAPI.sBlockCasings2, 3))
            .addElement('E', ofFrame(Materials.Steel))
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
        return RecipePool.SteamManufacturerRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 4;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamManufacturer_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamManufacturer_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamManufacturer_02"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 7, 7, true)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(V[9]);
        // We need to trick the GT_ParallelHelper we have enough amps for all recipe parallels.
        logic.setAvailableAmperage(getTrueParallel());
        logic.setAmperageOC(false);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        ITexture[] rTexture;
        if (side == facing) {
            if (aActive) {
                rTexture = new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_STEAM_MANUFACTURER_ACTIVE)
                        .extFacing()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_STEAM_MANUFACTURER)
                        .extFacing()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] { Textures.BlockIcons
                .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)) };
        }
        return rTexture;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatches();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamManufacturer(this.mName);
    }
}
