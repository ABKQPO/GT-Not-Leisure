package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static thaumcraft.common.config.ConfigBlocks.blockCosmeticOpaque;
import static thaumcraft.common.config.ConfigBlocks.blockCosmeticSolid;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.gtnl.CrucibleCraftingRecipes;
import com.science.gtnl.common.recipe.thaumcraft.EssentiaSupply;
import com.science.gtnl.common.recipe.thaumcraft.OwnerResearchCache;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import goodgenerator.loader.Loaders;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import thaumcraft.api.aspects.AspectList;

@IMetaTileEntity.SkipGenerateDescription
public class IndustrialCrucible extends MultiMachineBase<IndustrialCrucible> {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":multiblock/industrial_crucible";
    private static IStructureDefinition<IndustrialCrucible> structureDefinition;

    private static final int HORIZONTAL_OFFSET = 2;
    private static final int VERTICAL_OFFSET = 4;
    private static final int DEPTH_OFFSET = 0;
    private static final int CASING_TEXTURE_ID = 1536;

    private static final int MAX_PARALLEL_RECIPES = 1;

    private final EssentiaSupply essentiaSupply = new EssentiaSupply("IndustrialCrucible");
    private final OwnerResearchCache researchCache = new OwnerResearchCache();

    public IndustrialCrucible(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public IndustrialCrucible(String name) {
        super(name);
    }

    @Override
    public IStructureDefinition<IndustrialCrucible> getStructureDefinition() {
        if (structureDefinition != null) return structureDefinition;

        IStructureElement<IndustrialCrucible> hatchAdder = GTStructureUtility.buildHatchAdder(IndustrialCrucible.class)
            .atLeast(
                HatchElement.InputBus,
                HatchElement.OutputBus,
                HatchElement.Energy.or(HatchElement.MultiAmpEnergy),
                HatchElement.Maintenance)
            .casingIndex(getCasingTextureID())
            .hint(1)
            .build();

        structureDefinition = StructureDefinition.<IndustrialCrucible>builder()
            .addShape(
                STRUCTURE_PIECE_MAIN,
                StructureUtility.transpose(StructureUtils.readStructureFromFile(STRUCTURE_FILE_PATH)))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    hatchAdder,
                    StructureUtility.onElementPass(machine -> ++machine.mCountCasing, Casings.MagicCasing.asElement()),
                    StructureUtility.ofSpecificTileAdder(
                        IndustrialCrucible::addEssentiaHatch,
                        TileEntityEssentiaHatch.class,
                        Loaders.magicCasing,
                        0),
                    StructureUtility.ofTileAdder(IndustrialCrucible::addInfusionProvider, Loaders.magicCasing, 0)))
            .addElement('B', StructureUtility.ofBlock(blockCosmeticSolid, 7))
            .addElement('C', StructureUtility.ofBlock(blockCosmeticOpaque, 2))
            .build();
        return structureDefinition;
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) return;

        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 4);
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        researchCache.writeToNBT(nbt);
        super.saveNBTData(nbt);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        researchCache.readFromNBT(nbt);
        super.loadNBTData(nbt);
    }

    @Override
    public void checkHatch(List<StructureError> errors) {
        super.checkHatch(errors);
        if (essentiaSupply.isEmpty()) {
            errors.add(GTNLStructureErrors.invalidHatchConfiguration());
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        essentiaSupply.clear();
    }

    public boolean addEssentiaHatch(TileEntityEssentiaHatch tileEntity) {
        return essentiaSupply.addHatch(tileEntity);
    }

    public boolean addInfusionProvider(TileEntity tileEntity) {
        return essentiaSupply.addProvider(tileEntity);
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
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                CheckRecipeResult baseResult = super.validateRecipe(recipe);
                if (!baseResult.wasSuccessful()) {
                    return baseResult;
                }

                String research = recipe.getMetadata(CrucibleCraftingRecipes.CRUCIBLE_RESEARCH);
                AspectList requiredAspects = recipe.getMetadata(CrucibleCraftingRecipes.CRUCIBLE_ASPECTS);

                if (research == null || requiredAspects == null) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }

                if (!isResearchComplete(research)) {
                    return SimpleCheckRecipeResult.ofFailure("missing_crucible_research");
                }

                if (essentiaSupply.isCommitBlocked(currentWorldTime())) {
                    return SimpleCheckRecipeResult.ofFailure("essentia_commit_failed");
                }

                if (essentiaSupply.getAffordableCrafts(requiredAspects, 1) <= 0) {
                    essentiaSupply.requestShortfall(requiredAspects, 1);
                    return SimpleCheckRecipeResult.ofFailure("insufficient_essentia");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                AspectList requiredAspects = recipe.getMetadata(CrucibleCraftingRecipes.CRUCIBLE_ASPECTS);

                if (requiredAspects == null) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }

                if (!essentiaSupply.consume(requiredAspects, calculatedParallels, currentWorldTime())) {
                    return SimpleCheckRecipeResult.ofFailure("insufficient_essentia");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public GTNLParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                int affordable = essentiaSupply.getAffordableCrafts(
                    recipe.getMetadata(CrucibleCraftingRecipes.CRUCIBLE_ASPECTS),
                    Integer.MAX_VALUE);
                return super.createParallelHelper(recipe).setMaxTotalParallel(affordable);
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
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

    private boolean isResearchComplete(String research) {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        String ownerName = baseMetaTileEntity == null ? null : baseMetaTileEntity.getOwnerName();
        return researchCache.isComplete(ownerName, research);
    }

    private long currentWorldTime() {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (baseMetaTileEntity == null || baseMetaTileEntity.getWorld() == null) return 0;
        return baseMetaTileEntity.getWorld()
            .getTotalWorldTime();
    }

    @Override
    public int getMaxParallelRecipes() {
        return MAX_PARALLEL_RECIPES;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public int getCasingTextureID() {
        return CASING_TEXTURE_ID;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstoneLevel) {
        if (side == facing) {
            if (active) {
                return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            }

            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }

        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tooltip = new MultiblockTooltipBuilder();
        tooltip.addMachineType(StatCollector.translateToLocal("gtnl.machine.industrial_crucible.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.industrial_crucible.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.industrial_crucible.tooltip.2"))
            .addSupportMultiAmp()
            .addPerfectOCInfo()
            .beginStructureBlock(5, 5, 5, true)
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.industrial_crucible.casing"), 1)
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.industrial_crucible.casing"), 1)
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.industrial_crucible.casing"), 1)
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.industrial_crucible.casing"), 1)
            .toolTipFinisher();
        return tooltip;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.IndustrialCrucibleRecipes;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new IndustrialCrucible(mName);
    }
}
