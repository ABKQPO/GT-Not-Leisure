package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;

import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import static com.science.gtnl.common.recipe.gtnl.InfusionCraftingRecipes.INFUSION_ASPECTS;
import static com.science.gtnl.common.recipe.gtnl.InfusionCraftingRecipes.INFUSION_RESEARCH;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
public class SmallInfusionMatrix extends MultiMachineBase<SmallInfusionMatrix>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":multiblock/small_infusion_matrix";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE_PATH);

    private static final int HORIZONTAL_OFFSET = 1;
    private static final int VERTICAL_OFFSET = 1;
    private static final int DEPTH_OFFSET = 0;
    private static final int CASING_TEXTURE_ID = 1536;

    public final List<TileEntityEssentiaHatch> mEssentiaHatches = new ArrayList<>();

    public SmallInfusionMatrix(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public SmallInfusionMatrix(String name) {
        super(name);
    }

    @Override
    public IStructureDefinition<SmallInfusionMatrix> getStructureDefinition() {
        return StructureDefinition.<SmallInfusionMatrix>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(SHAPE))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(SmallInfusionMatrix.class)
                        .atLeast(
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.Energy,
                            HatchElement.Maintenance)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility.onElementPass(
                        machine -> ++machine.mCountCasing,
                        StructureUtility.ofBlock(Loaders.magicCasing, 0)),
                    StructureUtility.ofSpecificTileAdder(
                        SmallInfusionMatrix::addEssentiaHatch,
                        TileEntityEssentiaHatch.class,
                        Loaders.magicCasing,
                        0)))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack,
                             List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) return;

        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 4);
    }

    @Override
    public void checkHatch(List<StructureError> errors) {
        super.checkHatch(errors);
        if (mEssentiaHatches.isEmpty()) {
            errors.add(GTNLStructureErrors.invalidHatchConfiguration());
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mEssentiaHatches.clear();
    }

    public boolean addEssentiaHatch(TileEntityEssentiaHatch tileEntity) {
        return mEssentiaHatches.add(tileEntity);
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stack, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int elementBudget, ISurvivalBuildEnvironment environment) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stack,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            environment,
            false,
            true);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.IndustrialInfusionCraftingRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                CheckRecipeResult baseResult = super.validateRecipe(recipe);
                if (!baseResult.wasSuccessful()) {
                    return baseResult;
                }

                String research = recipe.getMetadataOrDefault(INFUSION_RESEARCH, "");

                if (!research.isEmpty()
                    && !ThaumcraftApiHelper.isResearchComplete(
                    getBaseMetaTileEntity().getOwnerName(),
                    research)) {

                    return SimpleCheckRecipeResult.ofFailure(
                        "missing_infusion_research");
                }

                AspectList requiredAspects = recipe.getMetadataOrDefault(
                    INFUSION_ASPECTS,
                    new AspectList());

                if (!hasRequiredEssentia(requiredAspects, 1)) {
                    return SimpleCheckRecipeResult.ofFailure(
                        "insufficient_essentia");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                AspectList requiredAspects = recipe.getMetadataOrDefault(
                    INFUSION_ASPECTS,
                    new AspectList());

                int crafts = Math.max(1, calculatedParallels);

                if (!hasRequiredEssentia(requiredAspects, crafts)) {
                    return SimpleCheckRecipeResult.ofFailure(
                        "insufficient_essentia");
                }

                consumeEssentia(requiredAspects, crafts);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(
                @NotNull GTRecipe recipe) {

                return super.createOverclockCalculator(recipe)
                    .setExtraDurationModifier(mConfigSpeedBoost)
                    .setHeatOC(getHeatOC())
                    .setMachineHeat(getMachineHeat())
                    .setHeatDiscount(getHeatDiscount())
                    .setAmperageOC(getAmperageOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    private boolean hasRequiredEssentia(
        AspectList requiredAspects,
        int crafts) {

        if (requiredAspects == null || crafts <= 0) {
            return true;
        }

        for (Aspect aspect : requiredAspects.getAspects()) {
            if (aspect == null) {
                continue;
            }

            long required = (long) requiredAspects.getAmount(aspect) * crafts;
            long stored = 0;

            for (TileEntityEssentiaHatch hatch : mEssentiaHatches) {
                if (hatch == null || hatch.isInvalid()) {
                    continue;
                }

                stored += hatch.containerContains(aspect);

                if (stored >= required) {
                    break;
                }
            }

            if (stored < required) {
                return false;
            }
        }

        return true;
    }

    private void consumeEssentia(
        AspectList requiredAspects,
        int crafts) {

        if (requiredAspects == null || crafts <= 0) {
            return;
        }

        for (Aspect aspect : requiredAspects.getAspects()) {
            if (aspect == null) {
                continue;
            }

            long remaining =
                (long) requiredAspects.getAmount(aspect) * crafts;

            for (TileEntityEssentiaHatch hatch : mEssentiaHatches) {
                if (remaining <= 0) {
                    break;
                }

                if (hatch == null || hatch.isInvalid()) {
                    continue;
                }

                int available = hatch.containerContains(aspect);
                int removed = (int) Math.min(remaining, available);

                if (removed > 0
                    && hatch.reduceStoredEssentia(aspect, removed)) {

                    remaining -= removed;
                }
            }
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public int getCasingTextureID() {
        return CASING_TEXTURE_ID;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side,
                                 ForgeDirection facing, int colorIndex, boolean active, boolean redstoneLevel) {
        if (side == facing) {
            if (active) {
                return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            }

            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }

        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tooltip = new MultiblockTooltipBuilder();
        tooltip.addMachineType(StatCollector.translateToLocal("SmallInfusionMatrixRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmallInfusionMatrix_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmallInfusionMatrix_01"))
            .beginStructureBlock(3, 3, 3, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SmallInfusionMatrix_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SmallInfusionMatrix_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SmallInfusionMatrix_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_SmallInfusionMatrix_Casing"), 1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("Tooltip_SmallInfusionMatrix_EssentiaHatch"),
                StatCollector.translateToLocal("Tooltip_SmallInfusionMatrix_Casing"),
                1)
            .toolTipFinisher();
        return tooltip;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SmallInfusionMatrix(mName);
    }
}
