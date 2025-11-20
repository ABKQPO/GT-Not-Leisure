package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class SteamWoodcutter extends SteamMultiMachineBase<SteamWoodcutter> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SW_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_wood_cutter";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SW_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 6;
    private static final int DEPTH_OFF_SET = 0;

    public SteamWoodcutter(String aName) {
        super(aName);
    }

    public SteamWoodcutter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamWoodcutterRecipeType");
    }

    @Override
    public IStructureDefinition<SteamWoodcutter> getStructureDefinition() {
        return StructureDefinition.<SteamWoodcutter>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(BlockLoader.metaCasing, 23))
            .addElement(
                'B',
                ofChain(
                    buildSteamWirelessInput(SteamWoodcutter.class)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 24))
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamWoodcutter.class).casingIndex(GTUtility.getTextureId((byte) 116, (byte) 24))
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamWoodcutter.class).casingIndex(GTUtility.getTextureId((byte) 116, (byte) 24))
                        .dot(1)
                        .build(),
                    buildHatchAdder(SteamWoodcutter.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            InputBus,
                            SteamHatchElement.OutputBus_Steam,
                            OutputBus,
                            Maintenance)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 24))
                        .dot(1)
                        .buildAndChain(),
                    ofBlock(BlockLoader.metaCasing, 24)))
            .addElement('C', ofBlock(BlockLoader.metaCasing, 25))
            .addElement('D', chainAllGlasses())
            .addElement('E', ofBlock(Blocks.dirt, 0))
            .build();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.SteamWoodcutterRecipes;
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamWoodcutter_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamWoodcutter_01"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(7, 8, 7, true)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
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
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 24)),
                    TextureFactory.builder()
                        .addIcon(TexturesGtBlock.oMCATreeFarmActive)
                        .extFacing()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 24)),
                    TextureFactory.builder()
                        .addIcon(TexturesGtBlock.oMCATreeFarm)
                        .extFacing()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 24)) };
        }
        return rTexture;
    }

    @Override
    public int getCasingTextureID() {
        return GTUtility.getTextureId((byte) 116, (byte) 24);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatches();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamWoodcutter(this.mName);
    }
}
