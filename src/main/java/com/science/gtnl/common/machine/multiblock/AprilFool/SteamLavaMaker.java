package com.science.gtnl.common.machine.multiblock.AprilFool;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.*;

import javax.annotation.Nonnull;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.SteamMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipeRegister;

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
import gregtech.api.util.OverclockCalculator;

public class SteamLavaMaker extends SteamMultiMachineBase<SteamLavaMaker> implements ISurvivalConstructable {

    public static final String TEXTURE_OVERLAY_LAVAMAKER = RESOURCE_ROOT_ID + ":" + "iconsets/OVERLAY_LAVAMAKER";
    public static final String TEXTURE_OVERLAY_LAVAMAKER_ACTIVE = RESOURCE_ROOT_ID + ":"
        + "iconsets/OVERLAY_LAVAMAKER_ACTIVE";
    public static Textures.BlockIcons.CustomIcon OVERLAY_LAVAMAKER = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_LAVAMAKER);
    public static Textures.BlockIcons.CustomIcon OVERLAY_LAVAMAKER_ACTIVE = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_LAVAMAKER_ACTIVE);
    private static IStructureDefinition<SteamLavaMaker> STRUCTURE_DEFINITION = null;
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
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamLavaMaker_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamLavaMaker_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamLavaMaker_02"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(3, 5, 3, true)
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
                        .addIcon(OVERLAY_LAVAMAKER_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_LAVAMAKER_ACTIVE)
                        .extFacing()
                        .glow()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 27)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_LAVAMAKER)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_LAVAMAKER)
                        .extFacing()
                        .glow()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] { Textures.BlockIcons
                .getCasingTextureForId(GTUtility.getCasingTextureIndex(BlockLoader.MetaCasing, 27)) };
        }
        return rTexture;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.LavaMakerRecipes;
    }

    @Override
    public IStructureDefinition<SteamLavaMaker> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<SteamLavaMaker>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    ofChain(
                        buildSteamWirelessInput(SteamLavaMaker.class)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 27))
                            .dot(1)
                            .build(),
                        buildSteamBigInput(SteamLavaMaker.class)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 27))
                            .dot(1)
                            .build(),
                        buildSteamInput(SteamLavaMaker.class).casingIndex(GTUtility.getTextureId((byte) 116, (byte) 27))
                            .dot(1)
                            .build(),
                        buildHatchAdder(SteamLavaMaker.class)
                            .atLeast(SteamHatchElement.InputBus_Steam, InputBus, OutputHatch)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 27))
                            .dot(1)
                            .buildAndChain(),
                        ofBlock(BlockLoader.MetaCasing, 27)))
                .addElement('B', chainAllGlasses())
                .addElement('C', ofChain(ofBlockAnyMeta(Blocks.lava), ofBlockAnyMeta(Blocks.flowing_lava)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
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
                return super.createOverclockCalculator(recipe).setMaxOverclocks(Math.min(4, recipeOcCount))
                    .setEUtDiscount(1)
                    .setDurationModifier(1);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
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
