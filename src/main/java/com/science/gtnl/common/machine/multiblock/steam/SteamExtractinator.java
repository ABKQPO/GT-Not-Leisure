package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
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
    public String getLocalNameKey() {
        return "gtnl.machine.steam_extractinator.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity arg0) {
        return new SteamExtractinator(this.mName);
    }

    @Override
    public IStructureDefinition<SteamExtractinator> getStructureDefinition() {
        return StructureDefinition.<SteamExtractinator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTNLCasings.ConcentratingSieveMesh.asElement())
            .addElement('B', GTNLCasings.VibrationSafeCasing.asElement())
            .addElement('C', Casings.SolidSteelMachineCasing.asElement())
            .addElement('D', Casings.SteelGearBoxCasing.asElement())
            .addElement('E', Casings.BronzePipeCasing.asElement())
            .addElement('F', Casings.SteelPipeCasing.asElement())
            .addElement('G', Casings.BronzeFireboxCasing.asElement())
            .addElement('H', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement('I', GTNLCasings.SteelBrickCasing.asElement())
            .addElement(
                'J',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(SteamExtractinator.class)
                        .atLeast(HatchElement.Maintenance, SteamHatchElement.OutputBus_Steam, HatchElement.OutputBus)
                        .casingIndex(10)
                        .hint(2)
                        .buildAndChain(),
                    Casings.BronzePlatedBricks.asElement()))
            .addElement(
                'K',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(SteamExtractinator.class)
                        .atLeast(HatchElement.InputHatch)
                        .casingIndex(10)
                        .hint(3)
                        .buildAndChain(),
                    Casings.BronzePlatedBricks.asElement()))
            .addElement(
                'L',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamExtractinator.class).casingIndex(10)
                        .hint(1)
                        .build(),
                    buildSteamBigInput(SteamExtractinator.class).casingIndex(10)
                        .hint(1)
                        .build(),
                    buildSteamInput(SteamExtractinator.class).casingIndex(10)
                        .hint(1)
                        .build(),
                    Casings.BronzePlatedBricks.asElement()))
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) {
            return;
        }
        checkHatch(errors);
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
            }
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_EXTRACTINATOR)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)) };
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_extractinator.recipe_type");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_extractinator.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_extractinator.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_extractinator.tooltip.2"))
            .beginStructureBlock(15, 10, 17, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.steam_extractinator.tooltip.top_casing"))
            .addOutputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.steam_extractinator.tooltip.bottom_casing"))
            .addEnergyHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.steam_extractinator.tooltip.middle_casing"))
            .toolTipFinisher();
        return tt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_ELECTROFURNACE_LOOP;
    }
}
