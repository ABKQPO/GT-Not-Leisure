package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class PrimitiveBrickKiln extends SteamMultiMachineBase<PrimitiveBrickKiln> implements ISurvivalConstructable {

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PrimitiveBrickKiln(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("PrimitiveBrickKilnRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PBK_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/primitive_brick_kiln";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PBK_STRUCTURE_FILE_PATH);

    public PrimitiveBrickKiln(String aName) {
        super(aName);
    }

    public PrimitiveBrickKiln(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 5;
    private static final int DEPTH_OFF_SET = 0;

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_INACTIVE)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 15);
    }

    @Override
    public IStructureDefinition<PrimitiveBrickKiln> getStructureDefinition() {
        return StructureDefinition.<PrimitiveBrickKiln>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(PrimitiveBrickKiln.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    buildSteamBigInput(PrimitiveBrickKiln.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    buildSteamInput(PrimitiveBrickKiln.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    buildHatchAdder(PrimitiveBrickKiln.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.InputHatch,
                            HatchElement.Maintenance)
                        .buildAndChain(
                            StructureUtility.onElementPass(
                                x -> ++x.mCountCasing,
                                StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 15)))))
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        PrimitiveBrickKiln::getTierMachineCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockCasings1, 10), Pair.of(GregTechAPI.sBlockCasings2, 0)),
                        -1,
                        (t, m) -> t.tierMachineCasing = m,
                        t -> t.tierMachineCasing)))
            .addElement(
                'C',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        PrimitiveBrickKiln::getTierFireboxCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockCasings3, 13), Pair.of(GregTechAPI.sBlockCasings3, 14)),
                        -1,
                        (t, m) -> t.tierFireboxCasing = m,
                        t -> t.tierFireboxCasing)))
            .addElement(
                'D',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        PrimitiveBrickKiln::getTierPipeCasing,
                        ImmutableList
                            .of(Pair.of(GregTechAPI.sBlockCasings2, 12), Pair.of(GregTechAPI.sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing)))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatches())
            return false;
        if (tierMachineCasing == 1 && tierFireboxCasing == 1 && tierPipeCasing == 1 && mCountCasing >= 40) {
            tierMachine = 1;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        if (tierMachineCasing == 2 && tierFireboxCasing == 2 && tierPipeCasing == 2 && mCountCasing >= 40) {
            tierMachine = 2;
            getCasingTextureID();
            updateHatchTexture();
            return true;
        }
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (tierMachine == 1) {
            return 8;
        } else if (tierMachine == 2) {
            return 16;
        }
        return 8;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.PrimitiveBrickKilnRecipes;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * tierMachine;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / tierMachine;
    }

    @Override
    public int getTierRecipes() {
        if (tierMachine == 2) {
            return 2;
        } else return 1;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("PrimitiveBrickKilnRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PrimitiveBrickKiln_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PrimitiveBrickKiln_01"))
            .addInfo(StatCollector.translateToLocal("HighPressureTooltipNotice"))
            .beginStructureBlock(5, 7, 5, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamFormingPress_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamFormingPress_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .toolTipFinisher();
        return tt;
    }
}
