package com.science.gtnl.common.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.common.block.BlockRegister.Fortify_Glowstone;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.ExtraUtilities;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.TextLocalization;
import com.science.gtnl.Utils.TextUtils;
import com.science.gtnl.common.RecipeRegister;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.objects.GTRenderedTexture;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
import gregtech.common.blocks.BlockCasings8;

public class LapotronChip extends MultiMachineBase<LapotronChip> implements ISurvivalConstructable {

    protected GTRecipe lastRecipeToBuffer;

    public int tierLapisCaelestis = -1;
    public int tierGlass1 = -1;
    public int tierGlass2 = -1;

    public static final int HORIZONTAL_OFF_SET = 88;
    public static final int VERTICAL_OFF_SET = 97;
    public static final int DEPTH_OFF_SET = 11;

    public int tCountCasing = 0;

    public int casing;

    public IStructureDefinition<LapotronChip> STRUCTURE_DEFINITION = null;

    public static final String STRUCTURE_PIECE_MAIN = "main";

    public static final String LC_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/lapotron_chip"; // 文件路径

    public String[][] shape;

    public LapotronChip(String aName) {
        super(aName);
        this.shape = StructureUtils.readStructureFromFile(LC_STRUCTURE_FILE_PATH);
    }

    public LapotronChip(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        this.shape = StructureUtils.readStructureFromFile(LC_STRUCTURE_FILE_PATH);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LapotronChip(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.LapotronChipRecipeType)
            .addInfo(TextLocalization.Tooltip_LapotronChip_00)
            .addInfo(TextLocalization.Tooltip_LapotronChip_01)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(177, 121, 177, true)
            .addInputBus(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addOutputBus(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addInputHatch(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addOutputHatch(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addEnergyHatch(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addMaintenanceHatch(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .toolTipFinisher(TextUtils.SCIENCE_NOT_LEISURE);
        return tt;
    }

    protected void updateHatchTexture() {
        for (MTEHatch h : mInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
    }

    public int getCasingTextureID() {
        return ((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(11);
    }

    protected GTRenderedTexture LCgetFrontOverlay() {
        return new GTRenderedTexture(Textures.BlockIcons.OVERLAY_DTPF_OFF);
    }

    protected GTRenderedTexture LCgetFrontOverlayActive() {
        return new GTRenderedTexture(Textures.BlockIcons.OVERLAY_DTPF_ON);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity aBaseMetaTileEntity, final ForgeDirection side,
        final ForgeDirection facing, final int aColorIndex, final boolean aActive, final boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                aActive ? LCgetFrontOverlayActive() : LCgetFrontOverlay() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<LapotronChip> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LapotronChip>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlockAnyMeta(Blocks.iron_block))
                .addElement(
                    'B',
                    ExtraUtilities.isModLoaded()
                        ? withChannel(
                            "tierLapisCaelestis",
                            ofBlocksTiered(
                                LapotronChip::tierLapisCaelestis,
                                ImmutableList.of(
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 0),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 1),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 2),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 3),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 4),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 5),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 6),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 7),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 8),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 9),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 10),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 11),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 12),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 13),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 14),
                                    Pair.of(Block.getBlockFromName("ExtraUtilities:greenscreen"), 15)),
                                -1,
                                (t, m) -> t.tierLapisCaelestis = m,
                                t -> t.tierLapisCaelestis))
                        : isAir())
                .addElement('C', ofBlock(sBlockCasings9, 7))
                .addElement('D', ofBlock(sBlockCasings1, 11))
                .addElement(
                    'E',
                    buildHatchAdder(LapotronChip.class)
                        .atLeast(InputBus, OutputBus, InputHatch, Maintenance, Energy, Energy.or(ExoticEnergy))
                        .casingIndex(((BlockCasings8) GregTechAPI.sBlockCasings8).getTextureIndex(10))
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++x.casing, ofBlock(sBlockCasings8, 10))))
                .addElement('F', ofBlock(Fortify_Glowstone, 0))
                .addElement(
                    'G',
                    withChannel(
                        "tierGlass1",
                        ofBlocksTiered(
                            LapotronChip::tierGlass1,
                            ImmutableList.of(
                                Pair.of(Blocks.stained_glass, 0),
                                Pair.of(Blocks.stained_glass, 1),
                                Pair.of(Blocks.stained_glass, 2),
                                Pair.of(Blocks.stained_glass, 3),
                                Pair.of(Blocks.stained_glass, 4),
                                Pair.of(Blocks.stained_glass, 5),
                                Pair.of(Blocks.stained_glass, 6),
                                Pair.of(Blocks.stained_glass, 7),
                                Pair.of(Blocks.stained_glass, 8),
                                Pair.of(Blocks.stained_glass, 9),
                                Pair.of(Blocks.stained_glass, 10),
                                Pair.of(Blocks.stained_glass, 11),
                                Pair.of(Blocks.stained_glass, 12),
                                Pair.of(Blocks.stained_glass, 13),
                                Pair.of(Blocks.stained_glass, 14),
                                Pair.of(Blocks.stained_glass, 15)),
                            -1,
                            (t, m) -> t.tierGlass1 = m,
                            t -> t.tierGlass1)))
                .addElement(
                    'H',
                    withChannel(
                        "tierGlass2",
                        ofBlocksTiered(
                            LapotronChip::tierGlass2,
                            ImmutableList.of(
                                Pair.of(Blocks.stained_glass, 0),
                                Pair.of(Blocks.stained_glass, 1),
                                Pair.of(Blocks.stained_glass, 2),
                                Pair.of(Blocks.stained_glass, 3),
                                Pair.of(Blocks.stained_glass, 4),
                                Pair.of(Blocks.stained_glass, 5),
                                Pair.of(Blocks.stained_glass, 6),
                                Pair.of(Blocks.stained_glass, 7),
                                Pair.of(Blocks.stained_glass, 8),
                                Pair.of(Blocks.stained_glass, 9),
                                Pair.of(Blocks.stained_glass, 10),
                                Pair.of(Blocks.stained_glass, 11),
                                Pair.of(Blocks.stained_glass, 12),
                                Pair.of(Blocks.stained_glass, 13),
                                Pair.of(Blocks.stained_glass, 14),
                                Pair.of(Blocks.stained_glass, 15)),
                            -1,
                            (t, m) -> t.tierGlass2 = m,
                            t -> t.tierGlass2)))
                .addElement('I', ofBlock(sBlockCasings1, 15))
                .addElement('K', ofBlockAnyMeta(Blocks.beacon))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public static int tierLapisCaelestis(Block block, int meta) {
        if (block == Block.getBlockFromName("ExtraUtilities:greenscreen")) return meta + 1;
        return -1;
    }

    public static int tierGlass1(Block block, int meta) {
        if (block == Blocks.stained_glass) return meta + 1;
        return -1;
    }

    public static int tierGlass2(Block block, int meta) {
        if (block == Blocks.stained_glass) return meta + 1;
        return -1;
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
        return this.survivialBuildPiece(
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

    public boolean checkHatches() {
        return !mInputHatches.isEmpty() && !mInputBusses.isEmpty()
            && !mOutputBusses.isEmpty()
            && mOutputHatches.isEmpty();
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        if (tCountCasing >= 10000 && checkHatches()) {
            updateHatchTexture();
            return true;
        }
        return false;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.LapotronChipRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().setSpeedBonus(1F)
            .setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String[] getInfoData() {
        final String running = (this.mMaxProgresstime > 0 ? "Machine running" : "Machine stopped");
        final String maintenance = (this.getIdealStatus() == this.getRepairStatus() ? "No Maintenance issues"
            : "Needs Maintenance");
        String tSpecialText;

        if (lastRecipeToBuffer != null && lastRecipeToBuffer.mOutputs[0].getDisplayName() != null) {
            tSpecialText = "Currently processing: " + lastRecipeToBuffer.mOutputs[0].getDisplayName();
        } else {
            tSpecialText = "Currently processing: Nothing";
        }

        return new String[] { "Lapotron Chip", running, maintenance, tSpecialText };
    }

}
