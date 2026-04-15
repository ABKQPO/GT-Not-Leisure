package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtPlusPlus.core.block.ModBlocks.blockCasings3Misc;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import bartworks.API.recipe.BartWorksRecipeMaps;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.material.MaterialsAlloy;

public class ExtremeCompressor extends WirelessEnergyMultiMachineBase<ExtremeCompressor> {

    private static final int HORIZONTAL_OFF_SET = 8;
    private static final int VERTICAL_OFF_SET = 13;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/extreme_compressor";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EC_STRUCTURE_FILE_PATH);
    public static final int MACHINEMODE_ELECTRIC = 0;
    public static final int MACHINEMODE_NEUTRONIUM = 1;

    public ExtremeCompressor(String aName) {
        super(aName);
    }

    public ExtremeCompressor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ExtremeCompressor(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ExtremeCompressorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(17, 16, 43, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_ExtremeCompressor_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ExtremeCompressor_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ExtremeCompressor_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ExtremeCompressor_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ExtremeCompressor_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 7);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<ExtremeCompressor> getStructureDefinition() {
        return StructureDefinition.<ExtremeCompressor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 5))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 9))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 15))
            .addElement(
                'E',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.STABALLOY.getFrameBox(1)
                            .getItem())))
            .addElement(
                'F',
                buildHatchAdder(ExtremeCompressor.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))))
            .addElement('G', StructureUtility.ofBlock(blockCasings3Misc, 1))
            .addElement('H', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings11, 4))
            .addElement('J', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('K', HatchElement.Muffler.newAny(getCasingTextureID(), 5))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 1))
            .addElement('N', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 9))
            .addElement(
                'O',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.INCOLOY_MA956.getFrameBox(1)
                            .getItem())))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mCountCasing > 200;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && mMufflerHatches.size() == 5;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_ELECTRIC) ? BartWorksRecipeMaps.electricImplosionCompressorRecipes
            : RecipeMaps.neutroniumCompressorRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays
            .asList(BartWorksRecipeMaps.electricImplosionCompressorRecipes, RecipeMaps.neutroniumCompressorRecipes);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 2;
        GTUtility
            .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("ExtremeCompressor_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("ExtremeCompressor_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

}
