package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.ExtraUtilities;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.INEIPreviewModifier;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;

public class LapotronChip extends MultiMachineBase<LapotronChip>
    implements ISurvivalConstructable, INEIPreviewModifier {

    public int tierLapisCaelestis = -1;
    public int tierGlass1 = -1;
    public int tierGlass2 = -1;

    private static final int HORIZONTAL_OFF_SET = 88;
    private static final int VERTICAL_OFF_SET = 97;
    private static final int DEPTH_OFF_SET = 11;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/lapotron_chip";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LC_STRUCTURE_FILE_PATH);

    public LapotronChip(String aName) {
        super(aName);
    }

    public LapotronChip(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LapotronChip(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LapotronChipRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LapotronChip_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LapotronChip_01"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(177, 121, 177, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LapotronChip_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LapotronChip_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LapotronChip_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LapotronChip_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LapotronChip_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LapotronChip_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings1, 11);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<LapotronChip> getStructureDefinition() {
        return StructureDefinition.<LapotronChip>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlockAnyMeta(Blocks.iron_block))
            .addElement(
                'B',
                withChannel(
                    "tierLapisCaelestis",
                    ofBlocksTiered(
                        LapotronChip::getLapisCaelestisTier,
                        ExtraUtilities.isModLoaded() ? getGreenScreenVariants() : getGlass(),
                        -1,
                        (t, m) -> t.tierLapisCaelestis = m,
                        t -> t.tierLapisCaelestis)))
            .addElement('C', ofBlock(sBlockCasings9, 7))
            .addElement('D', ofBlock(sBlockCasings1, 11))
            .addElement(
                'E',
                buildHatchAdder(LapotronChip.class)
                    .atLeast(Maintenance, InputBus, OutputBus, InputHatch, Maintenance, Energy, Energy.or(ExoticEnergy))
                    .casingIndex(StructureUtils.getTextureIndex(sBlockCasings8, 10))
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 10))))
            .addElement('F', ofBlock(BlockLoader.metaBlockGlow, 0))
            .addElement(
                'G',
                withChannel(
                    "tierGlass1",
                    ofBlocksTiered(
                        LapotronChip::getTierGlass1,
                        getGlass(),
                        -1,
                        (t, m) -> t.tierGlass1 = m,
                        t -> t.tierGlass1)))
            .addElement(
                'H',
                withChannel(
                    "tierGlass2",
                    ofBlocksTiered(
                        LapotronChip::getTierGlass2,
                        getGlass(),
                        -1,
                        (t, m) -> t.tierGlass2 = m,
                        t -> t.tierGlass2)))
            .addElement('I', ofBlock(sBlockCasings1, 15))
            .addElement('K', ofBlockAnyMeta(Blocks.beacon))
            .build();
    }

    public static ImmutableList<Pair<Block, Integer>> getGreenScreenVariants() {
        ImmutableList.Builder<Pair<Block, Integer>> builder = ImmutableList.builder();
        Block greenScreen = Block.getBlockFromName("ExtraUtilities:greenscreen");

        for (int i = 0; i < 16; i++) {
            builder.add(Pair.of(greenScreen, i));
        }

        return builder.build();
    }

    public static ImmutableList<Pair<Block, Integer>> getGlass() {
        ImmutableList.Builder<Pair<Block, Integer>> builder = ImmutableList.builder();
        Block greenScreen = Blocks.stained_glass;

        for (int i = 0; i < 16; i++) {
            builder.add(Pair.of(greenScreen, i));
        }

        return builder.build();
    }

    @Nullable
    public static Integer getLapisCaelestisTier(Block block, int meta) {
        if (block == null) return null;
        if (block == Block.getBlockFromName("ExtraUtilities:greenscreen")) return meta + 1;
        return -1;
    }

    @Nullable
    public static Integer getTierGlass1(Block block, int meta) {
        if (block == null) return null;
        if (block == Blocks.stained_glass) return meta + 1;
        return -1;
    }

    @Nullable
    public static Integer getTierGlass2(Block block, int meta) {
        if (block == null) return null;
        if (block == Blocks.stained_glass) return meta + 1;
        return -1;
    }

    @Override
    public void onPreviewConstruct(@NotNull ItemStack trigger) {
        if (trigger.stackSize > 1) {
            buildPiece(STRUCTURE_PIECE_MAIN, trigger, false, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
        }
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
        return -1;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 10000;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.LapotronChipRecipes;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

}
