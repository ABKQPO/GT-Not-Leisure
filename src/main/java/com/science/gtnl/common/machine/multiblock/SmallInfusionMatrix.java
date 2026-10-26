package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.recipe.gtnl.InfusionCraftingRecipes.INFUSION_ASPECTS;
import static com.science.gtnl.common.recipe.gtnl.InfusionCraftingRecipes.INFUSION_RESEARCH;
import static thaumcraft.common.config.ConfigBlocks.blockCosmeticOpaque;
import static thaumcraft.common.config.ConfigBlocks.blockCosmeticSolid;
import static thaumcraft.common.config.ConfigBlocks.blockStoneDevice;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.thaumcraft.EssentiaSupply;
import com.science.gtnl.common.recipe.thaumcraft.OwnerResearchCache;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;
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
public class SmallInfusionMatrix extends MultiMachineBase<SmallInfusionMatrix> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":multiblock/small_infusion_matrix";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE_PATH);

    private static final int HORIZONTAL_OFFSET = 3;
    private static final int VERTICAL_OFFSET = 3;
    private static final int DEPTH_OFFSET = 0;
    private static final int CASING_TEXTURE_ID = 1536;

    private final EssentiaSupply essentiaSupply = new EssentiaSupply("SmallInfusionMatrix");
    private final OwnerResearchCache researchCache = new OwnerResearchCache();

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
                            HatchElement.Energy.or(HatchElement.MultiAmpEnergy),
                            HatchElement.Maintenance)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility.onElementPass(machine -> ++machine.mCountCasing, Casings.MagicCasing.asElement()),
                    EssentiaSupply.hatchElement(SmallInfusionMatrix::addEssentiaHatch, Loaders.magicCasing, 0),
                    EssentiaSupply.providerElement(SmallInfusionMatrix::addInfusionProvider, Loaders.magicCasing, 0)))
            .addElement('B', StructureUtility.ofBlock(blockCosmeticOpaque, 0))
            .addElement('C', StructureUtility.ofBlock(blockCosmeticSolid, 6))
            .addElement('D', StructureUtility.ofBlock(blockCosmeticOpaque, 2))
            .addElement('E', StructureUtility.ofBlock(blockStoneDevice, 1))
            .build();
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
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.IndustrialInfusionCraftingRecipes;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        CheckRecipeResult result = super.checkProcessing();
        if (result.wasSuccessful()) {
            mOutputItems = TCRecipeTools.appendPrimordialPearlReturns(mOutputItems, processingLogic);
        }
        return result;
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

                String research = recipe.getMetadata(INFUSION_RESEARCH);
                AspectList requiredAspects = recipe.getMetadata(INFUSION_ASPECTS);

                if (research == null || requiredAspects == null) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }

                if (!isResearchComplete(research)) {
                    return SimpleCheckRecipeResult.ofFailure("missing_infusion_research");
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
            public GTNLParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                int affordable = essentiaSupply
                    .getAffordableCrafts(recipe.getMetadata(INFUSION_ASPECTS), Integer.MAX_VALUE);
                return super.createParallelHelper(recipe).setMaxTotalParallel(affordable);
            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                AspectList requiredAspects = recipe.getMetadata(INFUSION_ASPECTS);

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

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public boolean getPerfectOC() {
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
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SmallInfusionMatrix(mName);
    }
}
