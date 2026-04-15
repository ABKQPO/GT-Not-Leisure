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
import com.science.gtnl.utils.enums.BlockIcons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class SteamExtractinator extends SteamMultiMachineBase<SteamExtractinator> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_extractinator";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SE_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 10;

    public SteamExtractinator(String aName) {
        super(aName);
    }

    public SteamExtractinator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity arg0) {
        return new SteamExtractinator(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamExtractinatorRecipeType");
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_EXTRACTINATOR_ACTIVE)
                        .extFacing()
                        .build() };
            } else {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_EXTRACTINATOR)
                        .extFacing()
                        .build() };
            }
        }
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)) };
    }

    @Override
    public IStructureDefinition<SteamExtractinator> getStructureDefinition() {
        return StructureDefinition.<SteamExtractinator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaBlockGlass, 3))
            .addElement('B', StructureUtility.ofBlock(BlockLoader.metaCasing02, 0))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 3))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 12))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 13))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 13))
            .addElement('H', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement('I', StructureUtility.ofBlock(BlockLoader.metaBlockColumn, 1))
            .addElement(
                'J',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(SteamExtractinator.class)
                        .atLeast(HatchElement.Maintenance, SteamHatchElement.OutputBus_Steam, HatchElement.OutputBus)
                        .casingIndex(10)
                        .dot(2)
                        .buildAndChain(),
                    StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 10)))
            .addElement(
                'K',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(SteamExtractinator.class)
                        .atLeast(HatchElement.InputHatch)
                        .casingIndex(10)
                        .dot(1)
                        .buildAndChain(),
                    StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 10)))
            .addElement(
                'L',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamExtractinator.class).casingIndex(10)
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamExtractinator.class).casingIndex(10)
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamExtractinator.class).casingIndex(10)
                        .dot(3)
                        .build(),
                    StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 10)))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatches();
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
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.SteamExtractinatorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamExtractinator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamExtractinator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamExtractinator_02"))
            .beginStructureBlock(15, 10, 17, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SteamExtractinator_Casing_00"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_SteamExtractinator_Casing_01"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SteamExtractinator_Casing_02"))
            .toolTipFinisher();
        return tt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_ELECTROFURNACE_LOOP;
    }
}
