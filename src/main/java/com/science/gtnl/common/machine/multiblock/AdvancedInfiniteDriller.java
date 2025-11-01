package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL_ACTIVE;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.math.MainAxisAlignment;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.Column;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedRow;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.machine.VMTweakHelper;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.loader.Loaders;
import gregtech.GTMod;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.VoidingMode;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.objects.GTUODimension;
import gregtech.api.objects.GTUOFluid;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class AdvancedInfiniteDriller extends MultiMachineBase<AdvancedInfiniteDriller>
    implements ISurvivalConstructable {

    public int excessFuel = 0;
    public int drillTier = 0;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String AID_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/advanced_infinite_driller";
    private static final String[][] shape = StructureUtils.readStructureFromFile(AID_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 12;
    private static final int VERTICAL_OFF_SET = 39;
    private static final int DEPTH_OFF_SET = 0;

    public AdvancedInfiniteDriller(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public AdvancedInfiniteDriller(String aName) {
        super(aName);
    }

    @Override
    public boolean getPerfectOC() {
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("AdvancedInfiniteDrillerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_11"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(25, 41, 25, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .addInputHatch(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<AdvancedInfiniteDriller> getStructureDefinition() {
        return StructureDefinition.<AdvancedInfiniteDriller>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(Loaders.MAR_Casing, 0))
            .addElement('B', ofBlock(BlockLoader.metaCasing, 5))
            .addElement('C', ofBlock(BlockLoader.metaCasing, 16))
            .addElement('D', ofBlock(BlockLoader.metaCasing, 18))
            .addElement('E', ofBlock(sBlockCasings1, 14))
            .addElement('F', ofBlock(sSolenoidCoilCasings, 5))
            .addElement('G', ofBlock(sBlockCasings3, 11))
            .addElement('H', ofBlock(sBlockCasings8, 1))
            .addElement('I', ofBlock(sBlockCasings8, 7))
            .addElement(
                'J',
                buildHatchAdder(AdvancedInfiniteDriller.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(Maintenance, InputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 10))))
            .addElement('K', ofFrame(Materials.Neutronium))
            .addElement('L', ofBlock(sBlockMetal8, 0))
            .build();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ORE_DRILL_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ORE_DRILL)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings8, 10);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (excessFuel < 300) {
            excessFuel = 300;
        }

        if (excessFuel > 10000) {
            excessFuel = 10000;
        }

        if (drillTier == 0) {
            excessFuel = Math.max(300, excessFuel - 4);
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        ArrayList<FluidStack> storedFluids = getStoredFluids();
        if (storedFluids.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (excessFuel < 2000) {
            int consumptionCount = 0;

            for (FluidStack tFluid : storedFluids) {
                if (GTUtility.areFluidsEqual(tFluid, new FluidStack(GTPPFluids.Pyrotheum, 1))) {
                    int consumption = (int) Math.pow(excessFuel, 1.3);
                    if (tFluid.amount >= consumption) {
                        tFluid.amount -= consumption;
                        consumptionCount++;
                    }
                }
            }

            if (consumptionCount > 0) {
                excessFuel += consumptionCount;
                this.mMaxProgresstime = 32;
                this.lEUt = (int) -TierEU.RECIPE_ZPM;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            } else {
                excessFuel = Math.max(300, excessFuel - 4);
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        } else {
            int needEu = 0;
            List<FluidStack> outputFluids = new ArrayList<>();
            for (ItemStack item : getAllStoredInputs()) {
                if (item.getItem() instanceof ItemDimensionDisplay) {
                    int dimID = VMTweakHelper.dimMapping.inverse()
                        .getOrDefault(ItemDimensionDisplay.getDimension(item), 0);
                    GTUODimension dimension = GTMod.proxy.mUndergroundOil.GetDimension(dimID);
                    if (dimension == null) continue;

                    XSTR tVeinRNG = new XSTR(System.nanoTime());
                    int count = 0;
                    int attempts = 0;

                    while (count < 5 && attempts < 100) {
                        attempts++;
                        GTUOFluid uoFluid = dimension.getRandomFluid(tVeinRNG);

                        if (uoFluid == null || uoFluid.getFluid() == null) {
                            continue;
                        }

                        int amount = 1_000_000 + tVeinRNG.nextInt(Integer.MAX_VALUE / 10_000_000) * 10_000_000;
                        outputFluids.add(new FluidStack(uoFluid.getFluid(), amount));

                        needEu += amount / 200;
                        count++;
                    }
                }
            }
            mOutputFluids = outputFluids.toArray(new FluidStack[0]);
            this.mMaxProgresstime = (int) (((5750000 / excessFuel) - 475) * mConfigSpeedBoost);
            this.lEUt = -needEu;
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return GTNL_OverclockCalculator.ofNoOverclock(recipe)
                    .setExtraDurationModifier(mConfigSpeedBoost);
            }
        };
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("excessFuel", excessFuel);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("excessFuel")) {
            excessFuel = aNBT.getInteger("excessFuel");
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        startRecipeProcessing();

        if (excessFuel > 2000 && mProgresstime > 0 && mProgresstime % 20 == 0) {
            excessFuel += (int) Math.floor(excessFuel / 2000.0);
        }

        if (excessFuel > 10000) {
            mInventory[getControllerSlotIndex()] = null;
            this.stopMachine(ShutDownReasonRegistry.POWER_LOSS);
        }

        if (mProgresstime > 0) {
            if (mProgresstime % 5 == 0 && excessFuel >= 2000) {
                for (FluidStack tFluid : getStoredFluids()) {
                    if (tFluid == null || tFluid.getFluid() == null) continue;
                    int amount = tFluid.amount;
                    if (GTUtility.areFluidsEqual(tFluid, new FluidStack(GTPPFluids.Pyrotheum, 1))) {
                        int consumption = (int) Math.pow(excessFuel, 1.3);
                        if (amount >= consumption) {
                            tFluid.amount -= consumption;
                            excessFuel += 1;
                        }
                    } else if (GTUtility.areFluidsEqual(tFluid, FluidRegistry.getFluidStack("ic2distilledwater", 1))) {
                        int multiplier = amount / 200_000;
                        if (multiplier > 0) {
                            tFluid.amount -= multiplier * 200_000;
                            excessFuel -= multiplier;
                        }
                    } else if (GTUtility.areFluidsEqual(tFluid, Materials.LiquidOxygen.getFluid(1))) {
                        int multiplier = amount / 200_000;
                        if (multiplier > 0) {
                            tFluid.amount -= multiplier * 200_000;
                            excessFuel -= multiplier * 2;
                        }
                    } else if (GTUtility.areFluidsEqual(tFluid, WerkstoffLoader.LiquidHelium.getFluidOrGas(1))) {
                        int multiplier = amount / 200_000;
                        if (multiplier > 0) {
                            tFluid.amount -= multiplier * 200_000;
                            excessFuel -= multiplier * 4;
                        }
                    }
                }
            }
        } else if (aTick % 20 == 0 && !aBaseMetaTileEntity.isActive()) {
            excessFuel -= 4;
            if (excessFuel < 300) {
                excessFuel = 300;
            }
        }

        endRecipeProcessing();
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()
            || !mOutputHatches.isEmpty()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 570;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        drillTier = 0;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        drillTier = checkDrillTier();
    }

    public int checkDrillTier() {
        ItemStack controllerSlot = getControllerSlot();
        if (controllerSlot != null) {
            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.Neutronium, 1L))) {
                return 1;
            }

            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.CosmicNeutronium, 1L))) {
                return 2;
            }

            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.Infinity, 1L))) {
                return 3;
            }

            if (controllerSlot.isItemEqual(
                GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, MaterialsUEVplus.TranscendentMetal, 1L))) {
                return 4;
            }
        }
        return 0;
    }

    public int checkEnergyHatchTier() {
        int tier = 0;
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        return tier;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new AdvancedInfiniteDriller(this.mName);
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 1;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("excessFuel", excessFuel);

    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("excessFuel")) {
            currentTip.add(
                StatCollector.translateToLocal("Info_AdvancedInfiniteDriller_00") + EnumChatFormatting.YELLOW
                    + tag.getInteger("excessFuel")
                    + "K");
        }
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(4, 4)
                .setSize(190, 85));

        slotWidgets.clear();
        createInventorySlots();

        Column slotsColumn = new Column();
        for (int i = slotWidgets.size() - 1; i >= 0; i--) {
            slotsColumn.widget(slotWidgets.get(i));
        }
        builder.widget(
            slotsColumn.setAlignment(MainAxisAlignment.END)
                .setPos(173, 167 - 1));

        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        drawTexts(screenElements, !slotWidgets.isEmpty() ? slotWidgets.get(0) : null);
        screenElements
            .widget(
                new TextWidget().setStringSupplier(
                    () -> StatCollector.translateToLocalFormatted("Info_AdvancedInfiniteDriller_00") + excessFuel + "K")
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setEnabled(true))
            .widget(
                new FakeSyncWidget.IntegerSyncer(() -> excessFuel, Fuel -> excessFuel = Fuel).setSynced(true, false));
        builder.widget(
            new Scrollable().setVerticalScroll()
                .widget(screenElements.setPos(10, 0))
                .setPos(0, 7)
                .setSize(190, 79));

        builder.widget(createPowerSwitchButton(builder))
            .widget(createVoidExcessButton(builder))
            .widget(createInputSeparationButton(builder))
            .widget(createBatchModeButton(builder))
            .widget(createLockToSingleRecipeButton(builder))
            .widget(createStructureUpdateButton(builder));

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public VoidingMode getVoidingMode() {
        return VoidingMode.VOID_NONE;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
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
}
