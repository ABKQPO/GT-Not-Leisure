package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_STEAM_GATE_ASSEMBLER;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

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
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class SteamGateAssembler extends SteamMultiMachineBase<SteamGateAssembler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SGA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_gate_assembler";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SGA_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 10;
    private static final int VERTICAL_OFF_SET = 11;
    private static final int DEPTH_OFF_SET = 10;

    public SteamGateAssembler(String aName) {
        super(aName);
    }

    public SteamGateAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGateAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGateAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGateAssembler_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGateAssembler_03"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(21, 20, 21, true)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamGateAssemblerRecipeType");
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int realBudget = elementBudget >= 200 ? elementBudget : Math.min(200, elementBudget * 5);
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            realBudget,
            env,
            false,
            true);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        ITexture[] rTexture;
        if (side == facing) {
            rTexture = new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_STEAM_GATE_ASSEMBLER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_STEAM_GATE_ASSEMBLER)
                    .extFacing()
                    .glow()
                    .build() };
        } else {
            rTexture = new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)) };
        }
        return rTexture;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.SteamGateAssemblerRecipes;
    }

    @Override
    public IStructureDefinition<SteamGateAssembler> getStructureDefinition() {
        return StructureDefinition.<SteamGateAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                ofChain(
                    buildSteamWirelessInput(SteamGateAssembler.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings1, 10))
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamGateAssembler.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings1, 10))
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamGateAssembler.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings1, 10))
                        .dot(1)
                        .build(),
                    buildHatchAdder(SteamGateAssembler.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings1, 10))
                        .dot(1)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            InputBus,
                            OutputBus,
                            Maintenance)
                        .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(GregTechAPI.sBlockCasings1, 10)))))
            .addElement(
                'B',
                ofChain(
                    buildSteamWirelessInput(SteamGateAssembler.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings2, 0))
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamGateAssembler.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings2, 0))
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamGateAssembler.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings2, 0))
                        .dot(1)
                        .build(),
                    buildHatchAdder(SteamGateAssembler.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings2, 0))
                        .dot(1)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            InputBus,
                            OutputBus,
                            Maintenance)
                        .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings2, 0)))))
            .addElement('C', ofBlock(sBlockCasings2, 2))
            .addElement('D', ofBlock(sBlockCasings2, 3))
            .addElement('E', ofBlock(sBlockCasings2, 12))
            .addElement('F', ofBlock(sBlockCasings2, 13))
            .addElement('G', ofBlock(GregTechAPI.sBlockCasings3, 13))
            .addElement('H', ofBlock(GregTechAPI.sBlockCasings3, 14))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatches();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamGateAssembler(this.mName);
    }
}
