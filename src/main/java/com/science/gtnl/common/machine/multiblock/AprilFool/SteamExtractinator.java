package com.science.gtnl.common.machine.multiblock.AprilFool;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

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
import com.science.gtnl.common.machine.multiMachineClasses.SteamMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipeRegister;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
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

public class SteamExtractinator extends SteamMultiMachineBase<SteamExtractinator> implements ISurvivalConstructable {

    public static final String TEXTURE_OVERLAY_EXTRACTINATOR = RESOURCE_ROOT_ID + ":"
        + "iconsets/OVERLAY_EXTRACTINATOR";
    public static final String TEXTURE_OVERLAY_EXTRACTINATOR_ACTIVE = RESOURCE_ROOT_ID + ":"
        + "iconsets/OVERLAY_EXTRACTINATOR_ACTIVE";
    public static Textures.BlockIcons.CustomIcon OVERLAY_EXTRACTINATOR = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_EXTRACTINATOR);
    public static Textures.BlockIcons.CustomIcon OVERLAY_EXTRACTINATOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_EXTRACTINATOR_ACTIVE);
    private static IStructureDefinition<SteamExtractinator> STRUCTURE_DEFINITION = null;
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
        ITexture[] rTexture;
        if (side == facing) {
            if (aActive) {
                rTexture = new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_EXTRACTINATOR_ACTIVE)
                        .extFacing()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_EXTRACTINATOR)
                        .extFacing()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] { Textures.BlockIcons
                .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)) };
        }
        return rTexture;
    }

    @Override
    public IStructureDefinition<SteamExtractinator> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<SteamExtractinator>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(BlockLoader.MetaBlockGlass, 3))
                .addElement('B', ofBlock(BlockLoader.MetaCasing02, 0))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 0))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings2, 3))
                .addElement('E', ofBlock(GregTechAPI.sBlockCasings2, 12))
                .addElement('F', ofBlock(GregTechAPI.sBlockCasings2, 13))
                .addElement('G', ofBlock(GregTechAPI.sBlockCasings3, 13))
                .addElement('H', ofFrame(Materials.Steel))
                .addElement('I', ofBlock(BlockLoader.MetaBlockColumn, 1))
                .addElement(
                    'J',
                    ofChain(
                        buildHatchAdder(SteamExtractinator.class).atLeast(SteamHatchElement.OutputBus_Steam, OutputBus)
                            .casingIndex(10)
                            .dot(2)
                            .buildAndChain(),
                        ofBlock(GregTechAPI.sBlockCasings1, 10)))
                .addElement(
                    'K',
                    ofChain(
                        buildHatchAdder(SteamExtractinator.class).atLeast(InputHatch)
                            .casingIndex(10)
                            .dot(1)
                            .buildAndChain(),
                        ofBlock(GregTechAPI.sBlockCasings1, 10)))
                .addElement(
                    'L',
                    ofChain(
                        buildSteamWirelessInput(SteamExtractinator.class).casingIndex(10)
                            .dot(1)
                            .build(),
                        buildSteamBigInput(SteamExtractinator.class).casingIndex(10)
                            .dot(1)
                            .build(),
                        buildSteamInput(SteamExtractinator.class).casingIndex(10)
                            .dot(3)
                            .build(),
                        ofBlock(GregTechAPI.sBlockCasings1, 10)))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        if (checkHatches()) {
            return true;
        }
        return false;
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
        return RecipeRegister.SteamExtractinatorRecipes;
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

            // note that a basic steam machine has .setEUtDiscount(2F).setDurationModifier(2F). So these here are
            // bonuses.
            @Override
            @Nonnull
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setMaxOverclocks(Math.min(4, recipeOcCount))
                    .setDurationModifier(1f);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamExtractinator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamExtractinator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamExtractinator_02"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(15, 10, 17, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SteamExtractinator_Casing_00"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_SteamExtractinator_Casing_01"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SteamExtractinator_Casing_02"))
            .toolTipFinisher();
        return tt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_ELECTROFURNACE_LOOP;
    }
}
