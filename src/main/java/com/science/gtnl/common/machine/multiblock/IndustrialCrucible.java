package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static thaumcraft.common.config.ConfigBlocks.blockCosmeticOpaque;
import static thaumcraft.common.config.ConfigBlocks.blockCosmeticSolid;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.gtnl.CrucibleCraftingRecipes;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import cpw.mods.fml.common.Optional;
import goodgenerator.loader.Loaders;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Mods;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.lib.research.ResearchManager;
import thaumicenergistics.common.tiles.TileInfusionProvider;

@IMetaTileEntity.SkipGenerateDescription
public class IndustrialCrucible extends MultiMachineBase<IndustrialCrucible> {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":multiblock/industrial_crucible";
    private static IStructureDefinition<IndustrialCrucible> structureDefinition;

    private static final int HORIZONTAL_OFFSET = 2;
    private static final int VERTICAL_OFFSET = 4;
    private static final int DEPTH_OFFSET = 0;
    private static final int CASING_TEXTURE_ID = 1536;

    private static final int ESSENTIA_COMMIT_COOLDOWN_TICKS = 200;

    private static final int MAX_PARALLEL_RECIPES = 1;

    public final List<TileEntityEssentiaHatch> mEssentiaHatches = new ArrayList<>();
    private final List<TileEntity> mInfusionProviders = new ArrayList<>();

    private long essentiaCommitBlockedUntil;

    private int craftsCeiling = 1;

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
    public void checkHatch(List<StructureError> errors) {
        super.checkHatch(errors);
        checkHasInputBus(errors);
        checkHasOutputBus(errors);
        checkHatchMin(errors, HatchElement.Energy.or(HatchElement.MultiAmpEnergy), 1);
        checkHasMaintenanceHatch(errors);
        if (mEssentiaHatches.isEmpty() && mInfusionProviders.isEmpty()) {
            errors.add(GTNLStructureErrors.invalidHatchConfiguration());
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mEssentiaHatches.clear();
        mInfusionProviders.clear();
    }

    public boolean addEssentiaHatch(TileEntityEssentiaHatch tileEntity) {
        if (tileEntity == null || tileEntity.isInvalid()) return false;
        if (mEssentiaHatches.contains(tileEntity)) return false;
        return mEssentiaHatches.add(tileEntity);
    }

    public boolean addInfusionProvider(TileEntity tileEntity) {
        if (tileEntity == null || tileEntity.isInvalid()) return false;
        return Mods.ThaumicEnergistics.isModLoaded() && addInfusionProviderCompat(tileEntity);
    }

    @Optional.Method(modid = "thaumicenergistics")
    private boolean addInfusionProviderCompat(TileEntity tileEntity) {
        if (!(tileEntity instanceof TileInfusionProvider)) return false;
        if (mInfusionProviders.contains(tileEntity)) return false;
        return mInfusionProviders.add(tileEntity);
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

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        return super.checkProcessing();
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

                if (!isResearchAvailable(research)) {
                    return SimpleCheckRecipeResult.ofFailure("missing_crucible_research");
                }

                if (isEssentiaCommitBlocked()) {
                    return SimpleCheckRecipeResult.ofFailure("essentia_commit_failed");
                }

                int affordable = affordableCrafts(requiredAspects);
                if (affordable <= 0) {
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

                int crafts = clampCrafts(calculatedParallels, craftsCeiling);

                if (!hasRequiredEssentia(requiredAspects, crafts)) {
                    return SimpleCheckRecipeResult.ofFailure("insufficient_essentia");
                }

                if (!consumeEssentia(requiredAspects, crafts)) {
                    return SimpleCheckRecipeResult.ofFailure("insufficient_essentia");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public GTNLParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                int affordable = affordableCrafts(recipe.getMetadata(CrucibleCraftingRecipes.CRUCIBLE_ASPECTS));
                int batchFactor = isBatchModeEnabled() ? Math.max(1, Math.min(getMaxBatchSize(), affordable)) : 1;
                int base = Math.max(1, Math.min(affordable / batchFactor, getMaxParallelRecipes()));

                craftsCeiling = base * batchFactor;
                setMaxParallel(base);

                return super.createParallelHelper(recipe).enableBatchMode(batchFactor);
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

    private int affordableCrafts(AspectList requiredAspects) {
        if (requiredAspects == null) return 0;

        int affordable = Integer.MAX_VALUE;

        try {
            for (Aspect aspect : requiredAspects.getAspects()) {
                if (aspect == null) continue;

                int amount = requiredAspects.getAmount(aspect);
                if (amount <= 0) continue;

                long available = getStoredEssentiaInHatches(aspect, Long.MAX_VALUE)
                    + getMaximumProviderEssentia(aspect);
                affordable = (int) Math.min(affordable, available / amount);
                if (affordable <= 0) return 0;
            }
        } catch (ConcurrentModificationException e) {
            ScienceNotLeisure.LOG
                .warn("IndustrialCrucible: concurrent modification in recipe aspects, treating as insufficient", e);
            return 0;
        }

        return affordable == Integer.MAX_VALUE ? getMaxParallelRecipes() : affordable;
    }

    private boolean hasRequiredEssentia(AspectList requiredAspects, int crafts) {

        if (requiredAspects == null || crafts <= 0) {
            return false;
        }

        try {
            for (Aspect aspect : requiredAspects.getAspects()) {
                if (aspect == null || requiredAspects.getAmount(aspect) < 0) {
                    return false;
                }

                long required = (long) requiredAspects.getAmount(aspect) * crafts;
                long storedInHatches = getStoredEssentiaInHatches(aspect, required);
                if (storedInHatches < required && getMaximumProviderEssentia(aspect) < required - storedInHatches) {
                    return false;
                }
            }
        } catch (ConcurrentModificationException e) {
            ScienceNotLeisure.LOG
                .warn("IndustrialCrucible: concurrent modification in recipe aspects, treating as insufficient", e);
            return false;
        }

        return true;
    }

    private boolean consumeEssentia(AspectList requiredAspects, int crafts) {

        if (requiredAspects == null || crafts <= 0) {
            return false;
        }

        boolean withdrawn = false;

        try {
            for (Aspect aspect : requiredAspects.getAspects()) {
                if (aspect == null || requiredAspects.getAmount(aspect) < 0) {
                    return abortPartialConsumption(withdrawn, aspect);
                }

                long remaining = (long) requiredAspects.getAmount(aspect) * crafts;
                long storedInHatches = getStoredEssentiaInHatches(aspect, remaining);
                long providerAmount = Math.max(0, remaining - storedInHatches);

                if (providerAmount > 0) {
                    TileEntity provider = findInfusionProvider(aspect, providerAmount);
                    if (provider == null || providerAmount > Integer.MAX_VALUE
                        || !takeFromInfusionProvider(provider, aspect, (int) providerAmount)) {
                        return abortPartialConsumption(withdrawn, aspect);
                    }
                    withdrawn = true;
                    remaining -= providerAmount;
                }

                for (TileEntityEssentiaHatch hatch : mEssentiaHatches) {
                    if (remaining <= 0) {
                        break;
                    }

                    if (hatch == null || hatch.isInvalid()) {
                        continue;
                    }

                    int available = hatch.containerContains(aspect);
                    int removed = (int) Math.min(remaining, available);

                    if (removed > 0 && hatch.reduceStoredEssentia(aspect, removed)) {
                        withdrawn = true;
                        remaining -= removed;
                    }
                }

                if (remaining > 0) return abortPartialConsumption(withdrawn, aspect);
            }
        } catch (ConcurrentModificationException e) {
            essentiaCommitBlockedUntil = currentWorldTime() + ESSENTIA_COMMIT_COOLDOWN_TICKS;
            ScienceNotLeisure.LOG
                .warn("IndustrialCrucible: concurrent modification in recipe aspects, aborting consumption", e);
            return false;
        }

        return true;
    }

    private boolean abortPartialConsumption(boolean withdrawn, Aspect aspect) {
        if (withdrawn) {
            essentiaCommitBlockedUntil = currentWorldTime() + ESSENTIA_COMMIT_COOLDOWN_TICKS;
            ScienceNotLeisure.LOG.warn(
                "IndustrialCrucible: partial essentia withdrawal failed on {}, pausing crafts for {} ticks",
                aspect == null ? "unknown aspect" : aspect.getTag(),
                ESSENTIA_COMMIT_COOLDOWN_TICKS);
        }
        return false;
    }

    private boolean isEssentiaCommitBlocked() {
        return currentWorldTime() < essentiaCommitBlockedUntil;
    }

    private long currentWorldTime() {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (baseMetaTileEntity == null || baseMetaTileEntity.getWorld() == null) return 0;
        return baseMetaTileEntity.getWorld()
            .getTotalWorldTime();
    }

    private static int clampCrafts(int calculatedParallels, int maxParallelRecipes) {
        if (calculatedParallels <= 0 || maxParallelRecipes <= 0) return 1;
        return Math.min(calculatedParallels, maxParallelRecipes);
    }

    private long getStoredEssentiaInHatches(Aspect aspect, long required) {
        if (aspect == null || required <= 0) return 0;

        long stored = 0;
        for (TileEntityEssentiaHatch hatch : mEssentiaHatches) {
            if (hatch == null || hatch.isInvalid()) continue;

            stored += hatch.containerContains(aspect);
            if (stored >= required) break;
        }
        return Math.min(stored, required);
    }

    private long getMaximumProviderEssentia(Aspect aspect) {
        long maximum = 0;
        for (TileEntity provider : mInfusionProviders) {
            if (provider == null || provider.isInvalid()) continue;
            maximum = Math.max(maximum, getProviderAspectAmount(provider, aspect));
        }
        return maximum;
    }

    private TileEntity findInfusionProvider(Aspect aspect, long required) {
        TileEntity selected = null;
        long maximum = 0;
        for (TileEntity provider : mInfusionProviders) {
            if (provider == null || provider.isInvalid()) continue;

            long available = getProviderAspectAmount(provider, aspect);
            if (available >= required && available > maximum) {
                selected = provider;
                maximum = available;
            }
        }
        return selected;
    }

    private long getProviderAspectAmount(TileEntity provider, Aspect aspect) {
        if (!Mods.ThaumicEnergistics.isModLoaded()) return 0;
        return getProviderAspectAmountCompat(provider, aspect);
    }

    @Optional.Method(modid = "thaumicenergistics")
    private long getProviderAspectAmountCompat(TileEntity provider, Aspect aspect) {
        return provider instanceof TileInfusionProvider
            ? ((TileInfusionProvider) provider).getAspectAmountInNetwork(aspect)
            : 0;
    }

    private boolean takeFromInfusionProvider(TileEntity provider, Aspect aspect, int amount) {
        return Mods.ThaumicEnergistics.isModLoaded() && takeFromInfusionProviderCompat(provider, aspect, amount);
    }

    @Optional.Method(modid = "thaumicenergistics")
    private boolean takeFromInfusionProviderCompat(TileEntity provider, Aspect aspect, int amount) {
        return provider instanceof TileInfusionProvider
            && ((TileInfusionProvider) provider).takeFromContainer(aspect, amount);
    }

    private boolean isResearchAvailable(String research) {
        if (research == null) return false;
        if (research.isEmpty()) return true;

        if (!research.startsWith("@") && ResearchCategories.getResearch(research) == null) {
            return false;
        }

        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        String ownerName = baseMetaTileEntity == null ? null : baseMetaTileEntity.getOwnerName();
        if (ownerName == null || ownerName.isEmpty()) return false;

        return ResearchManager.isResearchComplete(ownerName, research);
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
