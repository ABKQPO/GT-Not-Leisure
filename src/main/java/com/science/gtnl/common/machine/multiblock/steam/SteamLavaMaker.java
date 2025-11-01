package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_STEAM_LAVA_MAKER;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_STEAM_LAVA_MAKER_ACTIVE;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;

import net.minecraft.init.Blocks;
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

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class SteamLavaMaker extends SteamMultiMachineBase<SteamLavaMaker> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SLM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_lava_marker";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SLM_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;

    public SteamLavaMaker(String aName) {
        super(aName);
    }

    public SteamLavaMaker(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamLavaMaker_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamLavaMaker_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamLavaMaker_02"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(3, 5, 3, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SteamLavaMaker_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_SteamLavaMaker_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamLavaMakerRecipeType");
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
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        ITexture[] rTexture;
        if (side == facing) {
            if (aActive) {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 27)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_STEAM_LAVA_MAKER_ACTIVE)
                        .extFacing()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 27)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_STEAM_LAVA_MAKER)
                        .extFacing()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] { Textures.BlockIcons
                .getCasingTextureForId(GTUtility.getCasingTextureIndex(BlockLoader.metaCasing, 27)) };
        }
        return rTexture;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.LavaMakerRecipes;
    }

    @Override
    public IStructureDefinition<SteamLavaMaker> getStructureDefinition() {
        return StructureDefinition.<SteamLavaMaker>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                ofChain(
                    buildSteamWirelessInput(SteamLavaMaker.class)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 27))
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamLavaMaker.class).casingIndex(GTUtility.getTextureId((byte) 116, (byte) 27))
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamLavaMaker.class).casingIndex(GTUtility.getTextureId((byte) 116, (byte) 27))
                        .dot(1)
                        .build(),
                    buildHatchAdder(SteamLavaMaker.class)
                        .atLeast(SteamHatchElement.InputBus_Steam, InputBus, OutputHatch, Maintenance)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 27))
                        .dot(1)
                        .buildAndChain(),
                    ofBlock(BlockLoader.metaCasing, 27)))
            .addElement('B', chainAllGlasses())
            .addElement('C', ofChain(ofBlockAnyMeta(Blocks.lava), ofBlockAnyMeta(Blocks.flowing_lava)))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatches();
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public int getMaxParallelRecipes() {
        // Max call to prevent seeing -16 parallels in waila for unformed multi
        return 4;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamLavaMaker(this.mName);
    }
}
