package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.Arrays;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class SteamRockBreaker extends SteamMultiMachineBase<SteamRockBreaker> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_MAIN_SURVIVAL = "main_survival";
    private static final String SRB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_rock_breaker";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SRB_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;

    public SteamRockBreaker(String aName) {
        super(aName);
    }

    public SteamRockBreaker(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity arg0) {
        return new SteamRockBreaker(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamRockBreakerRecipeType");
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = tierMachine == 2 ? StructureUtils.getTextureIndex(sBlockCasings2, 0)
            : StructureUtils.getTextureIndex(sBlockCasings1, 10);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR_ACTIVE)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public IStructureDefinition<SteamRockBreaker> getStructureDefinition() {
        return StructureDefinition.<SteamRockBreaker>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addShape(
                STRUCTURE_PIECE_MAIN_SURVIVAL,
                Arrays.stream(transpose(shape))
                    .map(
                        sa -> Arrays.stream(sa)
                            .map(s -> s.replaceAll("E", " "))
                            .map(s -> s.replaceAll("G", " "))
                            .toArray(String[]::new))
                    .toArray(String[][]::new))
            .addElement(
                'A',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofChain(
                        buildSteamWirelessInput(SteamRockBreaker.class).casingIndex(10)
                            .dot(1)
                            .build(),
                        buildSteamBigInput(SteamRockBreaker.class).casingIndex(10)
                            .dot(1)
                            .build(),
                        buildSteamInput(SteamRockBreaker.class).casingIndex(10)
                            .dot(1)
                            .build(),
                        buildHatchAdder(SteamRockBreaker.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                InputBus,
                                SteamHatchElement.OutputBus_Steam,
                                OutputBus,
                                Maintenance)
                            .casingIndex(10)
                            .dot(1)
                            .buildAndChain(
                                onElementPass(
                                    x -> ++x.mCountCasing,
                                    ofBlocksTiered(
                                        LargeSteamFurnace::getTierMachineCasing,
                                        ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                        -1,
                                        (t, m) -> t.tierMachineCasing = m,
                                        t -> t.tierMachineCasing))))))
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        SteamRockBreaker::getTierPipeCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 12), Pair.of(sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing)))
            .addElement('C', ofBlock(sBlockCasings4, 15))
            .addElement('D', ofBlock(Blocks.iron_block, 0))
            .addElement('E', ofChain(ofBlockAnyMeta(Blocks.lava), ofBlockAnyMeta(Blocks.flowing_lava)))
            .addElement('F', ofBlock(Blocks.cobblestone, 0))
            .addElement('G', ofAnyWater(true))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {

        int built = survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_SURVIVAL,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            true);
        if (built == -1) {
            GTUtility.sendChatToPlayer(
                env.getActor(),
                EnumChatFormatting.GOLD + "Auto placing done ! Now go place the water and lava by yourself !");
            return 0;
        }
        return built;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatches())
            return false;
        if (tierPipeCasing == 1 && tierMachineCasing == 1 && mCountCasing >= 14) {
            updateHatchTexture();
            tierMachine = 1;
            return true;
        }
        if (tierPipeCasing == 2 && tierMachineCasing == 2 && mCountCasing >= 14) {
            updateHatchTexture();
            tierMachine = 2;
            return true;
        }
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 8;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.RockBreakerRecipes;
    }

    @Override
    public double getEUtDiscount() {
        return 1.25 * tierMachine * (1 << (2 * Math.min(4, recipeOcCount)));
    }

    @Override
    public double getDurationModifier() {
        return 1.6 / tierMachine / (1 << Math.min(4, recipeOcCount));
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && f.isNotFlipped();
    }

    @Override
    public int getTierRecipes() {
        return 1;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamRockBreaker_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamRockBreaker_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamRockBreaker_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamRockBreaker_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamRockBreaker_04"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(11, 6, 11, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SteamRockBreaker_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SteamRockBreaker_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .toolTipFinisher();
        return tt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_ELECTROFURNACE_LOOP;
    }

}
