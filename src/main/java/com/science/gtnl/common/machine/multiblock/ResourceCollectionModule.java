package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasingsSE;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static net.minecraft.item.ItemStack.areItemStacksEqual;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.gui.modularui.multiblock.ResourceCollectionModuleGui;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.recipes.metadata.ResourceCollectionModuleMetadata;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.structure.error.ErrorType;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtnhintergalactic.tile.multi.elevator.TileEntitySpaceElevator;
import gtnhintergalactic.tile.multi.elevatormodules.TileEntityModuleBase;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class ResourceCollectionModule extends TileEntityModuleBase {

    private static final INameFunction<ResourceCollectionModule> PARALLEL_SETTING_NAME = (base, p) -> GCCoreUtil
        .translate("gt.blockmachines.multimachine.project.ig.assembler.cfgi.0");
    private static final IStatusFunction<ResourceCollectionModule> PARALLEL_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 1, 100, base.getMaxParallelRecipes());
    private static final int MACHINEMODE_MINER = 0;
    private static final int MACHINEMODE_DRILL = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/space_module";
    private static final String BASE_CASING_NAME_KEY = "gt.blockcasingsSE.0.name";
    private static final String PARALLEL_CONTROLLER_NAME_KEY = "gtnl.structure.hatch.parallel_controller";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SM_STRUCTURE_FILE_PATH);

    public static final ItemStack MiningDroneMkVIII = ItemList.MiningDroneUV.get(16);
    public static final ItemStack MiningDroneMkIX = ItemList.MiningDroneUHV.get(16);
    public static final ItemStack MiningDroneMkX = ItemList.MiningDroneUEV.get(16);
    public static final ItemStack MiningDroneMkXI = ItemList.MiningDroneUIV.get(16);
    public static final ItemStack MiningDroneMkXII = ItemList.MiningDroneUMV.get(16);
    public static final ItemStack MiningDroneMkXIII = ItemList.MiningDroneUXV.get(16);

    private long processingLogicEU;
    private int mParallelTier;

    public Parameters.Group.ParameterIn parallelSetting;
    public ObjectArrayList<ParallelControllerHatch> mParallelControllerHatches = new ObjectArrayList<>();

    public ResourceCollectionModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 25, 5, 1);
    }

    public ResourceCollectionModule(String aName) {
        super(aName, 25, 5, 1);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.resource_collection_module.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ResourceCollectionModule(mName);
    }

    @Override
    public long getMaxInputVoltage() {
        return GTValues.V[GTUtility.getTier(tTier)];
    }

    @Override
    public IStructureDefinition<? extends TTMultiblockBase> getStructure_EM() {
        return StructureDefinition.<ResourceCollectionModule>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'H',
                StructureUtility.withDescription(
                    Arrays.asList(
                        BASE_CASING_NAME_KEY,
                        CustomHatchElement.ParallelCon.getDescriptionLangKey(),
                        InputBus.getDescriptionLangKey(),
                        InputHatch.getDescriptionLangKey(),
                        OutputBus.getDescriptionLangKey(),
                        OutputHatch.getDescriptionLangKey()),
                    GTStructureUtility.ofHatchAdderOptional(
                        ResourceCollectionModule::addAllHatchToMachineList,
                        4096,
                        1,
                        sBlockCasingsSE,
                        0)))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 0, 1, 0);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        fixAllIssues();
        mParallelTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, 0, 1, 0, errors)) return;

        for (ParallelControllerHatch module : GTUtility.filterValidMTEs(mParallelControllerHatches)) {
            mParallelTier = module.mTier;
            break;
        }

        if (mParallelControllerHatches.size() > 1) {
            errors.add(
                GTNLStructureErrors
                    .parallelControllerHatchCount(ErrorType.TOO_MANY, mParallelControllerHatches.size(), 1));
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mParallelControllerHatches.clear();
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        setMachineMode((machineMode + 1) % 2);
        GTUtility.sendChatTrans(aPlayer, getMachineModeKey());
    }

    @Override
    public String getMachineModeKey() {
        return "gtnl.machine.resource_collection_module.mode." + machineMode;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == MACHINEMODE_MINER ? GTNLRecipeMaps.SpaceMinerRecipes : GTNLRecipeMaps.SpaceDrillRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(GTNLRecipeMaps.SpaceMinerRecipes, GTNLRecipeMaps.SpaceDrillRecipes);
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        long recipePower = processingLogicEU <= 0 ? Integer.MAX_VALUE : processingLogicEU;
        logic.setAvailableVoltage(recipePower);
        logic.setAvailableAmperage((long) parallelSetting.get());
        logic.setAmperageOC(false);
    }

    public int getMaxParallelRecipes() {
        if (mParallelControllerHatches != null) {
            for (ParallelControllerHatch module : GTUtility.filterValidMTEs(mParallelControllerHatches)) {
                mParallelTier = module.mTier;
                return module.getParallel();
            }
        }

        if (mParallelTier <= 1) {
            return 8;
        }

        return 1 << (2 * (mParallelTier - 2));
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (lastRecipe == recipe) {
                    processingLogicEU = recipe.mEUt;
                    setProcessingLogicPower(processingLogic);
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }

                int recipeReq = recipe.getMetadataOrDefault(ResourceCollectionModuleMetadata.INSTANCE, 0);
                ItemStack miningDrone = findMiningDrone();
                if (miningDrone == null) {
                    return SimpleCheckRecipeResult.ofFailure("no_mining_drone");
                }

                if (miningDrone.stackSize > 16) {
                    miningDrone.stackSize = 16;
                }

                if (recipeReq >= 1 && recipeReq <= 6) {
                    ItemStack requiredDrone = switch (recipeReq) {
                        case 1 -> MiningDroneMkVIII;
                        case 2 -> MiningDroneMkIX;
                        case 3 -> MiningDroneMkX;
                        case 4 -> MiningDroneMkXI;
                        case 5 -> MiningDroneMkXII;
                        case 6 -> MiningDroneMkXIII;
                        default -> null;
                    };

                    if (areItemStacksEqual(requiredDrone, miningDrone)) {
                        lastRecipe = recipe;
                        processingLogicEU = recipe.mEUt;
                        setProcessingLogicPower(processingLogic);
                        return CheckRecipeResultRegistry.SUCCESSFUL;
                    }

                    return SimpleCheckRecipeResult.ofFailure("no_mining_drone");
                }

                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setAmperageOC(false)
                    .setDurationDecreasePerOC(2)
                    .setEUtIncreasePerOC(4)
                    .setAmperage(availableAmperage)
                    .setEUtDiscount(1.0D - (mParallelTier / 50.0D))
                    .setDurationModifier(1.0D - (Math.max(0, mParallelTier - 1) / 50.0D));
            }
        }.setMaxParallelSupplier(() -> Math.min((int) parallelSetting.get(), getMaxParallelRecipes()));
    }

    @Override
    public void parametersInstantiation_EM() {
        super.parametersInstantiation_EM();
        Parameters.Group hatch_0 = parametrization.getGroup(0, false);
        parallelSetting = hatch_0.makeInParameter(0, getMaxParallelRecipes(), PARALLEL_SETTING_NAME, PARALLEL_STATUS);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE),
                new TTRenderedExtendedFacingTexture(aActive ? TTMultiblockBase.ScreenON : TTMultiblockBase.ScreenOFF) };
        }
        if (facing.getRotation(ForgeDirection.UP) == side || facing.getRotation(ForgeDirection.DOWN) == side) {
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE) };
    }

    @Override
    public boolean protectsExcessItem() {
        return !eSafeVoid;
    }

    @Override
    public boolean protectsExcessFluid() {
        return !eSafeVoid;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new ResourceCollectionModuleGui(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    public boolean addAllHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return addParallelControllerToMachineList(aTileEntity, aBaseCasingIndex)
            || addClassicToMachineList(aTileEntity, aBaseCasingIndex);
    }

    public boolean addParallelControllerToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof ParallelControllerHatch hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            hatch.updateCraftingIcon(getMachineCraftingIcon());
            return mParallelControllerHatches.add(hatch);
        }
        return false;
    }

    private ItemStack findMiningDrone() {
        for (ItemStack item : getAllStoredInputs()) {
            if (item != null) {
                if (item.isItemEqual(MiningDroneMkVIII) || item.isItemEqual(MiningDroneMkIX)
                    || item.isItemEqual(MiningDroneMkX)
                    || item.isItemEqual(MiningDroneMkXI)
                    || item.isItemEqual(MiningDroneMkXII)
                    || item.isItemEqual(MiningDroneMkXIII)) {
                    return item;
                }
            }
        }
        return null;
    }

    public enum CustomHatchElement implements IHatchElement<ResourceCollectionModule> {

        ParallelCon(ResourceCollectionModule::addParallelControllerToMachineList, ParallelControllerHatch.class) {

            @Override
            public long count(ResourceCollectionModule tileEntity) {
                return 0;
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<ResourceCollectionModule> adder;

        @SafeVarargs
        CustomHatchElement(IGTHatchAdder<ResourceCollectionModule> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super ResourceCollectionModule> adder() {
            return adder;
        }

        @Override
        public String getDescriptionLangKey() {
            return PARALLEL_CONTROLLER_NAME_KEY;
        }

        @Override
        public String getDisplayName() {
            return StatCollector.translateToLocal(PARALLEL_CONTROLLER_NAME_KEY);
        }
    }
}
