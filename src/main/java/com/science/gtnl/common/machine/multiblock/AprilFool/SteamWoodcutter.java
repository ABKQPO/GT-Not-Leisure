package com.science.gtnl.common.machine.multiblock.AprilFool;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.common.block.Casings.BasicBlocks.MetaCasing;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.multitileentity.multiblock.casing.Glasses.chainAllGlasses;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import javax.annotation.Nonnull;

import gregtech.api.util.OverclockCalculator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.common.machine.multiMachineClasses.SteamMultiMachineBase;
import com.science.gtnl.common.recipe.RecipeRegister;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import org.jetbrains.annotations.NotNull;

public class SteamWoodcutter extends SteamMultiMachineBase<SteamWoodcutter> implements ISurvivalConstructable {

    private static IStructureDefinition<SteamWoodcutter> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SW_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/steam_wood_cutter";
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
        return TextLocalization.SteamWoodcutterRecipeType;
    }

    @Override
    public IStructureDefinition<SteamWoodcutter> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<SteamWoodcutter>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(MetaCasing, 23))
                .addElement(
                    'B',
                    ofChain(
                        buildSteamBigInput(SteamWoodcutter.class)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 24))
                            .dot(1)
                            .build(),
                        buildSteamInput(SteamWoodcutter.class)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 24))
                            .dot(1)
                            .build(),
                        buildHatchAdder(SteamWoodcutter.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                InputBus,
                                SteamHatchElement.OutputBus_Steam,
                                OutputBus)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 24))
                            .dot(1)
                            .buildAndChain(),
                        ofBlock(MetaCasing, 24)))
                .addElement('C', ofBlock(MetaCasing, 25))
                .addElement('D', chainAllGlasses())
                .addElement('E', ofBlock(Blocks.dirt, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.SteamWoodcutterRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(
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
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (availableVoltage < recipe.mEUt) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Override
            @Nonnull
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setEUtDiscount(1)
                    .setSpeedBoost(1);
            }
        };
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamWoodcutter_00)
            .addInfo(TextLocalization.Tooltip_SteamWoodcutter_01)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(7, 8, 7, true)
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamWoodcutter(this.mName);
    }
}
