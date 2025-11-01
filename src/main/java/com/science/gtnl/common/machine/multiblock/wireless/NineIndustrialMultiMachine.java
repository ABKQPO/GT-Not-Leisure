package com.science.gtnl.common.machine.multiblock.wireless;

import static bartworks.common.loaders.ItemRegistry.bw_realglas2;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.Utils.NEGATIVE_ONE;
import static com.science.gtnl.utils.Utils.mergeArray;
import static goodgenerator.loader.Loaders.FRF_Coil_4;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static kubatech.loaders.BlockLoader.defcCasingBlock;
import static tectech.thing.casing.TTCasingsContainer.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.machine.NineIndustrialMultiMachineManager;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ParallelHelper;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class NineIndustrialMultiMachine extends WirelessEnergyMultiMachineBase<NineIndustrialMultiMachine> {

    public NineIndustrialMultiMachineManager modeManager = new NineIndustrialMultiMachineManager();
    public static final String[] aToolTipNames = new String[108];
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String NIMM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/nine_industrial_multi_machine";
    private static final String[][] shape = StructureUtils.readStructureFromFile(NIMM_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 14;
    private static final int VERTICAL_OFF_SET = 27;
    private static final int DEPTH_OFF_SET = 0;

    static {
        for (int id = 0; id < 108; id++) {
            RecipeMap<?> recipeMap = getRecipeMap(id);
            if (recipeMap != null) {
                String aNEI = GTLanguageManager.getTranslation(getRecipeMap(id).unlocalizedName);
                aToolTipNames[id] = aNEI != null ? aNEI : "BAD NEI NAME";
            }
        }
    }

    public NineIndustrialMultiMachine(final int aID, final String aName, final String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public NineIndustrialMultiMachine(final String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new NineIndustrialMultiMachine(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        String[] aBuiltStrings = new String[36];
        for (int i = 0; i < 36; i++) {
            int baseIndex = i * 3;
            if (baseIndex + 2 < aToolTipNames.length) {
                aBuiltStrings[i] = String
                    .join(", ", aToolTipNames[baseIndex], aToolTipNames[baseIndex + 1], aToolTipNames[baseIndex + 2]);
            } else if (baseIndex + 1 < aToolTipNames.length) {
                aBuiltStrings[i] = String.join(", ", aToolTipNames[baseIndex], aToolTipNames[baseIndex + 1]);
            } else if (baseIndex < aToolTipNames.length) {
                aBuiltStrings[i] = aToolTipNames[baseIndex];
            } else {
                aBuiltStrings[i] = "";
            }
        }

        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("NineIndustrialMultiMachineRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_07"));
        for (int i = 0; i < 36; i++) {
            tt.addInfo(
                StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_Mode_" + i) + " - "
                    + EnumChatFormatting.YELLOW
                    + aBuiltStrings[i]
                    + EnumChatFormatting.RESET);
        }
        tt.beginStructureBlock(29, 29, 29, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_Casing"))
            .addInputHatch(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_NineIndustrialMultiMachine_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<NineIndustrialMultiMachine> getStructureDefinition() {
        return StructureDefinition.<NineIndustrialMultiMachine>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(bw_realglas2, 0))
            .addElement('B', ofBlock(FRF_Coil_4, 0))
            .addElement('C', ofBlock(BlockLoader.metaCasing, 5))
            .addElement('D', ofBlock(defcCasingBlock, 12))
            .addElement(
                'E',
                buildHatchAdder(NineIndustrialMultiMachine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                    .buildAndChain(onElementPass(x -> ++this.mCountCasing, ofBlock(sBlockCasings1, 12))))
            .addElement('F', ofBlock(sBlockCasings1, 13))
            .addElement('G', ofBlock(sBlockCasings1, 14))
            .addElement('H', ofBlock(sBlockCasings10, 6))
            .addElement('I', ofBlock(sBlockCasings10, 7))
            .addElement('J', ofBlock(sBlockCasings10, 11))
            .addElement('K', ofBlock(sBlockCasings5, 13))
            .addElement('L', ofBlock(sBlockCasingsNH, 12))
            .addElement('M', ofBlock(sBlockCasingsTT, 4))
            .addElement('N', ofFrame(Materials.Neutronium))
            .addElement('O', ofBlock(StabilisationFieldGenerators, 2))
            .addElement('P', ofBlock(TimeAccelerationFieldGenerator, 8))
            .build();
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return this.mCountCasing > 256;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings1, 12);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialMixerActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialMixer)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    private ItemStack getCircuit(ItemStack[] t) {
        for (ItemStack j : t) {
            if (j.getItem() == GTUtility.getIntegratedCircuit(0)
                .getItem()) {
                if (j.getItemDamage() >= 20 && j.getItemDamage() <= 22) {
                    return j;
                }
            }
        }
        return null;
    }

    private int getCircuitID(ItemStack circuit) {
        int H = circuit.getItemDamage();
        int T = (H == 20 ? 0 : (H == 21 ? 1 : (H == 22 ? 2 : -1)));
        if (T == -1) {
            throw new IllegalArgumentException("Invalid circuit item damage: " + H);
        }
        return NineIndustrialMultiMachineManager.getModeMapIndex(machineMode, T);
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return NineIndustrialMultiMachineManager.getAllRecipeMaps();
    }

    private static RecipeMap<?> getRecipeMap(int mode) {
        return NineIndustrialMultiMachineManager.getRecipeMap(mode);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            private ItemStack lastCircuit = null;
            private int lastMode = -1;

            @Nonnull
            @Override
            public Stream<GTRecipe> findRecipeMatches(@Nullable RecipeMap<?> map) {
                ItemStack circuit = getCircuit(inputItems);
                if (circuit == null) {
                    return Stream.empty();
                }
                if (!GTUtility.areStacksEqual(circuit, lastCircuit)) {
                    lastRecipe = null;
                    lastCircuit = circuit;
                }
                if (machineMode != lastMode) {
                    lastRecipe = null;
                    lastMode = machineMode;
                }
                RecipeMap<?> foundMap = getRecipeMap(getCircuitID(circuit));
                if (foundMap == null) {
                    return Stream.empty();
                }
                return super.findRecipeMatches(foundMap);
            }

            @Nonnull
            @Override
            public GTNL_ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
                return new GTNL_ParallelHelper().setRecipe(recipe)
                    .setAvailableEUt(Long.MAX_VALUE)
                    .setMachine(machine, protectItems, protectFluids)
                    .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
                    .setEUtModifier(0.0000000001)
                    .setMaxParallel(Integer.MAX_VALUE)
                    .setConsumption(true)
                    .setOutputCalculation(true);
            }

            @Override
            public double calculateDuration(@Nonnull GTRecipe recipe, @Nonnull GTNL_ParallelHelper helper,
                @Nonnull GTNL_OverclockCalculator calculator) {
                if (batchMode) {
                    return 1;
                } else {
                    return 128;
                }
            }
        };
    }

    @Override
    public double getEUtDiscount() {
        return 0.000000000001;
    }

    @Override
    public double getDurationModifier() {
        return 0.000000000001;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("NineIndustrialMultiMachine_Mode_" + machineMode);
    }

    @Override
    public String[] getInfoData() {
        String[] data = super.getInfoData();
        ArrayList<String> mInfo = new ArrayList<>(Arrays.asList(data));
        String mode = StatCollector
            .translateToLocal(NineIndustrialMultiMachineManager.getModeLocalization(machineMode));
        mInfo.add(mode);
        return mInfo.toArray(new String[0]);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public int nextMachineMode() {
        machineMode = modeManager.getNextMachineMode(machineMode);
        return machineMode;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
        for (int i = 0; i <= 35; i++) {
            machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
        }
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        costingEU = BigInteger.ZERO;
        costingEUText = ZERO_STRING;

        if (!wirelessMode) return handleNonWirelessModeProcessing();

        boolean succeeded = false;
        CheckRecipeResult finalResult = CheckRecipeResultRegistry.SUCCESSFUL;
        for (int i = 0; i < cycleNum; i++) {
            CheckRecipeResult r = wirelessModeProcessOnce();
            if (!r.wasSuccessful()) {
                finalResult = r;
                break;
            }
            succeeded = true;
        }

        updateSlots();
        if (!succeeded) return finalResult;
        costingEUText = GTUtility.formatNumbers(costingEU);

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = 1;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public CheckRecipeResult wirelessModeProcessOnce() {
        if (!isRecipeProcessing) startRecipeProcessing();
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        if (!result.wasSuccessful()) {
            return result;
        }

        BigInteger costEU = BigInteger.valueOf(processingLogic.getCalculatedEut())
            .multiply(BigInteger.valueOf(processingLogic.getDuration()));

        if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(NEGATIVE_ONE))) {
            return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
        }

        costingEU = costingEU.add(costEU);

        ItemStack[] outputItems = processingLogic.getOutputItems();
        if (outputItems != null) {
            for (ItemStack itemStack : outputItems) {
                if (itemStack != null) {
                    if (batchMode) {
                        itemStack.stackSize = Integer.MAX_VALUE;
                    }
                }
            }
        }
        mOutputItems = mergeArray(mOutputItems, outputItems);

        FluidStack[] outputFluids = processingLogic.getOutputFluids();
        if (outputFluids != null) {
            for (FluidStack fluidStack : outputFluids) {
                if (fluidStack != null) {
                    if (batchMode) {
                        fluidStack.amount = Integer.MAX_VALUE;
                    }
                }
            }
        }
        mOutputFluids = mergeArray(mOutputFluids, outputFluids);

        endRecipeProcessing();
        return result;
    }

    @Nonnull
    private CheckRecipeResult handleNonWirelessModeProcessing() {
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        if (batchMode) {
            mMaxProgresstime = 1;
        } else {
            mMaxProgresstime = 128;
        }
        setEnergyUsage(processingLogic);

        ItemStack[] outputItems = processingLogic.getOutputItems();
        if (outputItems != null) {
            for (ItemStack itemStack : outputItems) {
                if (itemStack != null) {
                    if (batchMode) {
                        itemStack.stackSize = Integer.MAX_VALUE;
                    }
                }
            }
        }
        mOutputItems = outputItems;

        FluidStack[] outputFluids = processingLogic.getOutputFluids();
        if (outputFluids != null) {
            for (FluidStack fluidStack : outputFluids) {
                if (fluidStack != null) {
                    if (batchMode) {
                        fluidStack.amount = Integer.MAX_VALUE;
                    }
                }
            }
        }
        mOutputFluids = outputFluids;
        return result;
    }
}
