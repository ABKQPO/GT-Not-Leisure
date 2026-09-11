package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.monitor.EnergyMonitorCustomWirelessEutProvider;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;

import gregtech.api.casing.Casings;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.WirelessNetworkManager;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class ReactionFurnace extends WirelessEnergyMultiMachineBase<ReactionFurnace>
    implements ISurvivalConstructable, EnergyMonitorCustomWirelessEutProvider {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String RF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/reaction_furnace";
    private static final int HORIZONTAL_OFF_SET = 15;
    private static final int VERTICAL_OFF_SET = 18;
    private static final int DEPTH_OFF_SET = 3;
    private static final String[][] shape = StructureUtils.readStructureFromFile(RF_STRUCTURE_FILE_PATH);

    public ReactionFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ReactionFurnace(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.reaction_furnace.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ReactionFurnace(this.mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.furnaceRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.reaction_furnace.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.reaction_furnace.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.9"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.reaction_furnace.tooltip.1"))
            .addSupportAny()
            .beginStructureBlock(31, 21, 29, true)
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.reaction_furnace.tooltip.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.reaction_furnace.tooltip.casing"))
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.reaction_furnace.tooltip.casing"))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.reaction_furnace.tooltip.casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<ReactionFurnace> getStructureDefinition() {
        return StructureDefinition.<ReactionFurnace>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', Casings.HeatResistantTriniumPlatedCasing.asElement())
            .addElement('B', GTNLCasings.PressureBalancedCasing.asElement())
            .addElement('C', Casings.StabilizedNaquadahWaterPlantCasing.asElement())
            .addElement('D', Casings.PressureContainmentCasing.asElement())
            .addElement('E', Casings.RadiantNaquadahAlloyCasing.asElement())
            .addElement(
                'F',
                buildHatchAdder(ReactionFurnace.class).casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            Casings.AdvancedIridiumPlatedMachineCasing.asElement())))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 115);
    }

    @Override
    public void checkEnergyHatch(List<StructureError> errors) {}

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        costingEU = BigInteger.ZERO;
        costingEUText = Utils.ZERO_STRING;
        List<ItemStack> tInput = getAllStoredInputs();

        if (tInput.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        long maxParallel = wirelessMode ? getMaxParallelRecipesLong() : getTrueParallel();

        long availableEUt;

        if (wirelessMode) {
            availableEUt = Utils.toLongSafe(WirelessNetworkManager.getUserEU(ownerUUID));

            if (availableEUt < 4) {
                return CheckRecipeResultRegistry.insufficientPower(4);
            }

            long maxByEnergy = availableEUt / 4L;

            maxParallel = Math.min(maxParallel, maxByEnergy);

        } else {
            availableEUt = GTUtility.roundUpVoltage(getMaxInputEu());

            if (availableEUt < 4) {
                return CheckRecipeResultRegistry.insufficientPower(4);
            }
        }

        if (maxParallel <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        long maxParallelBeforeBatchMode = maxParallel;

        if (!wirelessMode && isBatchModeEnabled()) {
            maxParallel *= getMaxBatchSize();
        }

        long currentParallel = maxParallel;

        long itemParallel = 0L;

        for (ItemStack item : tInput) {
            if (item == null || item.stackSize <= 0) continue;

            ItemStack smeltedOutput = GTModHandler.getSmeltingOutput(item, false, null);
            if (smeltedOutput == null) continue;

            long parallelsLeft = currentParallel - itemParallel;
            if (parallelsLeft <= 0) break;

            itemParallel += Math.min(item.stackSize, parallelsLeft);
        }

        currentParallel = itemParallel;

        if (currentParallel <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        long finalParallel = currentParallel;

        int progressTime = 128;

        if (!wirelessMode) {
            GTNLOverclockCalculator calculator = new GTNLOverclockCalculator().setEUt(availableEUt)
                .setRecipeEUt(4)
                .setDuration(64)
                .setParallel(GTUtility.safeInt(maxParallelBeforeBatchMode, 0))
                .setExtraDurationModifier(mConfigSpeedBoost)
                .enablePerfectOC();

            calculator.setCurrentParallel(GTUtility.safeInt(currentParallel, 0))
                .calculate();

            progressTime = calculator.getDuration();
            this.lEUt = -GTValues.VP[GTUtility.getTier(calculator.getConsumption())];

        } else {
            costingEU = BigInteger.valueOf(finalParallel)
                .multiply(BigInteger.valueOf(4L));
            WirelessNetworkManager.addEUToGlobalEnergyMap(ownerUUID, costingEU.multiply(Utils.NEGATIVE_ONE));
            this.lEUt = 0;
        }

        ArrayList<ItemStack> smeltedOutputs = new ArrayList<>();
        long toSmelt = finalParallel;

        for (ItemStack item : tInput) {

            if (item == null || item.stackSize <= 0) continue;

            ItemStack smeltedOutput = GTModHandler.getSmeltingOutput(item, false, null);
            if (smeltedOutput == null) continue;

            long remainingToSmelt = Math.min(toSmelt, item.stackSize);
            if (remainingToSmelt <= 0) break;

            long totalOutput = (long) smeltedOutput.stackSize * remainingToSmelt;

            while (totalOutput > 0) {

                long splitLong = Math.min(totalOutput, Integer.MAX_VALUE);
                int splitSize = (int) splitLong;

                ItemStack splitStack = smeltedOutput.copy();
                splitStack.stackSize = splitSize;

                smeltedOutputs.add(splitStack);

                totalOutput -= splitLong;
            }

            long newSize = (long) item.stackSize - remainingToSmelt;
            item.stackSize = newSize <= 0 ? 0 : GTUtility.safeInt(newSize, 0);

            toSmelt -= remainingToSmelt;
            if (toSmelt <= 0) break;
        }

        if (smeltedOutputs.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        this.mOutputItems = smeltedOutputs.toArray(new ItemStack[0]);
        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;
        this.mMaxProgresstime = progressTime;

        this.updateSlots();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public BigInteger getEnergyMonitorWirelessEut() {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (!wirelessMode || baseMetaTileEntity == null || !baseMetaTileEntity.isActive()) {
            return BigInteger.ZERO;
        }
        return costingEU;
    }

    @Override
    public int getMaxParallelRecipes() {
        return GTUtility.safeInt(getMaxParallelRecipesLong(), 0);
    }

    public long getMaxParallelRecipesLong() {
        resetParallelTier();

        long baseParallel;
        ParallelControllerHatch module = getSingleParallelControllerHatch();
        if (module != null) {
            mParallelTier = module.mTier;
            baseParallel = module.getParallel();
        } else if (mParallelTier <= 2) {
            baseParallel = 8;
        } else {
            baseParallel = 1L << (2 * (mParallelTier - 3));
        }

        return baseParallel * 8192L - 1L;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return Casings.AdvancedIridiumPlatedMachineCasing.getTextureId();
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
