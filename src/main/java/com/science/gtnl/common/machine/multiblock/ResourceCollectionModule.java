package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasingsSE;
import static net.minecraft.item.ItemStack.areItemStacksEqual;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicTextWidget;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.Utils.recipes.ResourceCollectionModuleTierKey;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gtnhintergalactic.tile.multi.elevator.TileEntitySpaceElevator;
import gtnhintergalactic.tile.multi.elevatormodules.TileEntityModuleBase;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class ResourceCollectionModule extends TileEntityModuleBase {

    private long processingLogicEU;
    Parameters.Group.ParameterIn parallelSetting;
    private static final INameFunction<ResourceCollectionModule> PARALLEL_SETTING_NAME = (base, p) -> GCCoreUtil
        .translate("gt.blockmachines.multimachine.project.ig.assembler.cfgi.0");
    private static final IStatusFunction<ResourceCollectionModule> PARALLEL_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 1, 100, base.getMaxParallelRecipes());
    private int ParallelTier;
    private static final int MACHINEMODE_MINER = 0;
    private static final int MACHINEMODE_DRILL = 1;
    private static IStructureDefinition<ResourceCollectionModule> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/space_module";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SM_STRUCTURE_FILE_PATH);
    public final ItemStack MiningDroneMkVIII = ItemList.MiningDroneUV.get(16);
    public final ItemStack MiningDroneMkIX = ItemList.MiningDroneUHV.get(16);
    public final ItemStack MiningDroneMkX = ItemList.MiningDroneUEV.get(16);
    public final ItemStack MiningDroneMkXI = ItemList.MiningDroneUIV.get(16);
    public final ItemStack MiningDroneMkXII = ItemList.MiningDroneUMV.get(16);
    public final ItemStack MiningDroneMkXIII = ItemList.MiningDroneUXV.get(16);

    public ResourceCollectionModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 14, 5, 1);
    }

    public ResourceCollectionModule(String aName) {
        super(aName, 14, 5, 1);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_MINER) ? RecipeRegister.SpaceMinerRecipes : RecipeRegister.SpaceDrillRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeRegister.SpaceMinerRecipes, RecipeRegister.SpaceDrillRecipes);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mode", machineMode);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        machineMode = aNBT.getInteger("mode");
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(
            new DynamicTextWidget(
                () -> new Text(
                    StatCollector.translateToLocal("Interaction_DESCRIPTION_Index_400")
                        + StatCollector.translateToLocal("ResourceCollectionModule_Mode_" + this.machineMode))
                            .color(Color.WHITE.normal)).setPos(10, 77));

        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public Pos2d getMachineModeSwitchButtonPos() {
        return new Pos2d(174, 97);
    }

    @Override
    public IStructureDefinition<? extends TTMultiblockBase> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<ResourceCollectionModule>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'H',
                    GTStructureUtility.ofHatchAdderOptional(
                        ResourceCollectionModule::addClassicToMachineList,
                        4096,
                        1,
                        sBlockCasingsSE,
                        0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM(STRUCTURE_PIECE_MAIN, 0, 1, 0, stackSize, hintsOnly);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        fixAllIssues();
        ParallelTier = 0;

        if (!structureCheck_EM(STRUCTURE_PIECE_MAIN, 0, 1, 0)) return false;

        ParallelTier = getParallelTier(aStack);

        return true;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (byte) ((this.machineMode + 1) % 2);
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("ResourceCollectionModule_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("ResourceCollectionModule_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        long recipePower;

        if (processingLogicEU <= 0) {
            recipePower = Integer.MAX_VALUE;
        } else {
            recipePower = processingLogicEU;
        }

        logic.setAvailableVoltage(recipePower);
        logic.setAvailableAmperage((long) parallelSetting.get());
        logic.setAmperageOC(false);
    }

    public int getMaxParallelRecipes() {
        if (ParallelTier <= 1) {
            return 8;
        } else {
            return (int) Math.pow(4, ParallelTier - 2);
        }
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {

                if (parallelSetting.get() > getMaxParallelRecipes()) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }

                if (lastRecipe == recipe) {
                    processingLogicEU = recipe.mEUt;
                    setProcessingLogicPower(processingLogic);
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }

                int recipeReq = recipe.getMetadataOrDefault(ResourceCollectionModuleTierKey.INSTANCE, 0);

                ItemStack miningDrone = findMiningDrone();

                if (miningDrone == null) {
                    return SimpleCheckRecipeResult.ofFailure("no_mining_drone");
                }

                if (miningDrone.stackSize > 16) {
                    miningDrone.stackSize = 16;
                }

                if (recipeReq >= 1 && recipeReq <= 6) {
                    ItemStack requiredDrone = null;
                    switch (recipeReq) {
                        case 1:
                            requiredDrone = MiningDroneMkVIII;
                            break;
                        case 2:
                            requiredDrone = MiningDroneMkIX;
                            break;
                        case 3:
                            requiredDrone = MiningDroneMkX;
                            break;
                        case 4:
                            requiredDrone = MiningDroneMkXI;
                            break;
                        case 5:
                            requiredDrone = MiningDroneMkXII;
                            break;
                        case 6:
                            requiredDrone = MiningDroneMkXIII;
                            break;
                    }

                    if (areItemStacksEqual(requiredDrone, miningDrone)) {
                        lastRecipe = recipe;
                        processingLogicEU = recipe.mEUt;
                        setProcessingLogicPower(processingLogic);
                        return CheckRecipeResultRegistry.SUCCESSFUL;
                    } else {

                        return SimpleCheckRecipeResult.ofFailure("no_mining_drone");
                    }
                }

                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setAmperageOC(false)
                    .setDurationDecreasePerOC(2)
                    .setEUtIncreasePerOC(4)
                    .setAmperage(availableAmperage)
                    .setEUtDiscount(1 - (ParallelTier / 50.0))
                    .setDurationModifier(1 - (ParallelTier / 200.0));
            }
        }.setMaxParallelSupplier(() -> Math.min((int) parallelSetting.get(), getMaxParallelRecipes()));
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
        } else if (facing.getRotation(ForgeDirection.UP) == side || facing.getRotation(ForgeDirection.DOWN) == side) {
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
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ResourceCollectionModule(mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ResourceCollectionModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ResourceCollectionModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ResourceCollectionModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(1, 5, 2, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_ResourceCollectionModule_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ResourceCollectionModule_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ResourceCollectionModule_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ResourceCollectionModule_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    public int getParallelTier(ItemStack inventory) {
        if (inventory == null) return 0;
        if (inventory.isItemEqual(GTNLItemList.LVParallelControllerCore.getInternalStack_unsafe())) {
            return 1;
        } else if (inventory.isItemEqual(GTNLItemList.MVParallelControllerCore.getInternalStack_unsafe())) {
            return 2;
        } else if (inventory.isItemEqual(GTNLItemList.HVParallelControllerCore.getInternalStack_unsafe())) {
            return 3;
        } else if (inventory.isItemEqual(GTNLItemList.EVParallelControllerCore.getInternalStack_unsafe())) {
            return 4;
        } else if (inventory.isItemEqual(GTNLItemList.IVParallelControllerCore.getInternalStack_unsafe())) {
            return 5;
        } else if (inventory.isItemEqual(GTNLItemList.LuVParallelControllerCore.getInternalStack_unsafe())) {
            return 6;
        } else if (inventory.isItemEqual(GTNLItemList.ZPMParallelControllerCore.getInternalStack_unsafe())) {
            return 7;
        } else if (inventory.isItemEqual(GTNLItemList.UVParallelControllerCore.getInternalStack_unsafe())) {
            return 8;
        } else if (inventory.isItemEqual(GTNLItemList.UHVParallelControllerCore.getInternalStack_unsafe())) {
            return 9;
        } else if (inventory.isItemEqual(GTNLItemList.UEVParallelControllerCore.getInternalStack_unsafe())) {
            return 10;
        } else if (inventory.isItemEqual(GTNLItemList.UIVParallelControllerCore.getInternalStack_unsafe())) {
            return 11;
        } else if (inventory.isItemEqual(GTNLItemList.UMVParallelControllerCore.getInternalStack_unsafe())) {
            return 12;
        } else if (inventory.isItemEqual(GTNLItemList.UXVParallelControllerCore.getInternalStack_unsafe())) {
            return 13;
        } else if (inventory.isItemEqual(GTNLItemList.MAXParallelControllerCore.getInternalStack_unsafe())) {
            return 14;
        }
        return 0;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing_EM() {
        ItemStack controllerItem = getControllerSlot();
        this.ParallelTier = getParallelTier(controllerItem);
        return super.checkProcessing_EM();
    }
}
