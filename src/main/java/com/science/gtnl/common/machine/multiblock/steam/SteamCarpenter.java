package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_STEAM_CARPENTER;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_STEAM_CARPENTER_ACTIVE;
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
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class SteamCarpenter extends SteamMultiMachineBase<SteamCarpenter> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_carpenter";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SC_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;

    public SteamCarpenter(String aName) {
        super(aName);
    }

    public SteamCarpenter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamCarpenterRecipeType");
    }

    @Override
    public IStructureDefinition<SteamCarpenter> getStructureDefinition() {
        return StructureDefinition.<SteamCarpenter>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                ofChain(
                    buildSteamWirelessInput(SteamCarpenter.class).casingIndex(10)
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamCarpenter.class).casingIndex(10)
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamCarpenter.class).casingIndex(10)
                        .dot(1)
                        .build(),
                    buildHatchAdder(SteamCarpenter.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            InputBus,
                            SteamHatchElement.OutputBus_Steam,
                            OutputBus,
                            Maintenance)
                        .casingIndex(10)
                        .dot(1)
                        .buildAndChain(),
                    ofBlock(GregTechAPI.sBlockCasings1, 10)))
            .addElement('B', ofBlock(GregTechAPI.sBlockCasings2, 2))
            .addElement('C', ofFrame(Materials.Wood))
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
        return RecipePool.SteamCarpenterRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamCarpenter_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamCarpenter_01"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(3, 3, 3, true)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        ITexture[] rTexture;
        if (side == facing) {
            if (aActive) {
                rTexture = new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_STEAM_CARPENTER_ACTIVE)
                        .extFacing()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_STEAM_CARPENTER)
                        .extFacing()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] { Textures.BlockIcons
                .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)) };
        }
        return rTexture;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatches();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamCarpenter(this.mName);
    }
}
