package com.science.gtnl.common.machine.multiblock.AprilFool;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR_ACTIVE;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.recipes.SteamFusionTierKey;
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
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;

public class SteamFusionReactor extends SteamMultiMachineBase<SteamFusionReactor> implements ISurvivalConstructable {

    private static IStructureDefinition<SteamFusionReactor> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SFR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_fusion_reactor";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SFR_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 12;

    public SteamFusionReactor(String aName) {
        super(aName);
    }

    public SteamFusionReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamFusionReactorRecipeType");
    }

    @Override
    public IStructureDefinition<SteamFusionReactor> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<SteamFusionReactor>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(BlockLoader.MetaCasing, 26))
                .addElement('B', ofBlock(BlockLoader.MetaCasing, 29))
                .addElement('C', chainAllGlasses())
                .addElement(
                    'D',
                    ofChain(
                        buildSteamWirelessInput(SteamFusionReactor.class)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 29))
                            .dot(1)
                            .build(),
                        buildSteamBigInput(SteamFusionReactor.class)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 29))
                            .dot(1)
                            .build(),
                        buildSteamInput(SteamFusionReactor.class)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 29))
                            .dot(1)
                            .build(),
                        buildHatchAdder(SteamFusionReactor.class).atLeast(InputHatch, OutputHatch)
                            .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 29))
                            .dot(1)
                            .buildAndChain(),
                        ofBlock(BlockLoader.MetaCasing, 29)))
                .build();
        }
        return STRUCTURE_DEFINITION;
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
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.getMetadataOrDefault(SteamFusionTierKey.INSTANCE, 0) != 0) {
                    return SimpleCheckRecipeResult.ofFailure("metadata.steamfusion");
                }
                return super.validateRecipe(recipe);
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
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.SteamFusionReactorRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public int getMaxParallelRecipes() {
        // Max call to prevent seeing -16 parallels in waila for unformed multi
        return 1;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamFusionReactor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamFusionReactor_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamFusionReactor_02"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(15, 3, 15, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SteamFusionReactor_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_SteamFusionReactor_Casing"), 1)
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
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 29)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_TOP_STEAM_MACERATOR_ACTIVE)
                        .extFacing()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 29)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_TOP_STEAM_MACERATOR)
                        .extFacing()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getTextureId((byte) 116, (byte) 29)) };
        }
        return rTexture;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamFusionReactor(this.mName);
    }
}
