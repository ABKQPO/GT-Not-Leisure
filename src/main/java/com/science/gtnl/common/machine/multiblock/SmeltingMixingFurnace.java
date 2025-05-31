package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase.ParallelControllerElement.ParallelCon;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.GregTechAPI.sBlockCasings9;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.core.block.ModBlocks.*;
import static kubatech.loaders.BlockLoader.defcCasingBlock;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

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
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.Utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.common.machine.multiMachineClasses.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipeRegister;

import bartworks.common.loaders.ItemRegistry;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.casing.BlockGTCasingsTT;

public class SmeltingMixingFurnace extends WirelessEnergyMultiMachineBase<SmeltingMixingFurnace> {

    protected GTRecipe lastRecipeToBuffer;
    public boolean hasRequiredItem = false;
    public static final int HORIZONTAL_OFF_SET = 8;
    public static final int VERTICAL_OFF_SET = 14;
    public static final int DEPTH_OFF_SET = 0;
    private static final int MACHINEMODE_SMF = 0;
    private static final int MACHINEMODE_DTPF = 1;
    protected static final int CASING_INDEX = BlockGTCasingsTT.textureOffset;
    private static IStructureDefinition<SmeltingMixingFurnace> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String SMF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/smelting_mixing_furnace";
    public static String[][] shape = StructureUtils.readStructureFromFile(SMF_STRUCTURE_FILE_PATH);

    public SmeltingMixingFurnace(String aName) {
        super(aName);
    }

    public SmeltingMixingFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SmeltingMixingFurnace(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SmeltingMixingFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(17, 17, 33, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .toolTipFinisher();
        return tt;
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
        return CASING_INDEX;
    }

    @Override
    public IStructureDefinition<SmeltingMixingFurnace> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<SmeltingMixingFurnace>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(ItemRegistry.bw_realglas2, 0))
                .addElement('B', ofBlock(BlockLoader.MetaCasing, 5))
                .addElement('C', ofBlock(BlockLoader.MetaCasing, 7))
                .addElement('D', ofBlock(defcCasingBlock, 7))
                .addElement('E', ofBlock(defcCasingBlock, 10))
                .addElement('F', ofBlock(sBlockCasings1, 12))
                .addElement('G', ofBlock(sBlockCasings1, 13))
                .addElement('H', ofBlock(sBlockCasings10, 7))
                .addElement('I', ofBlock(sBlockCasings10, 13))
                .addElement('J', ofBlock(sBlockCasings8, 7))
                .addElement('K', ofBlock(sBlockCasings9, 12))
                .addElement('L', ofBlock(sBlockCasingsTT, 6))
                .addElement('M', ofBlock(sBlockCasingsTT, 8))
                .addElement('N', ofFrame(Materials.Infinity))
                .addElement('O', ofBlock(blockCasings2Misc, 4))
                .addElement('P', ofBlock(blockSpecialMultiCasings, 11))
                .addElement('Q', ofBlock(blockCasingsMisc, 12))
                .addElement(
                    'R',
                    buildHatchAdder(SmeltingMixingFurnace.class)
                        .atLeast(
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch,
                            Energy,
                            Energy.or(ExoticEnergy),
                            ParallelCon)
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++this.tCountCasing, ofBlock(sBlockCasingsTT, 0))))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
        this.tCountCasing = 0;
        wirelessMode = false;
        hasRequiredItem = false;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        if (this.tCountCasing <= 15) {
            return false;
        }

        ItemStack requiredItem = ItemList.Transdimensional_Alignment_Matrix.get(1);
        for (ItemStack item : getAllStoredInputs()) {
            if (item != null && item.isItemEqual(requiredItem)) {
                hasRequiredItem = true;
                break;
            }
        }

        energyHatchTier = checkEnergyHatchTier();
        wirelessMode = mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_SMF) ? RecipeRegister.SmeltingMixingFurnaceRecipes
            : RecipeMaps.plasmaForgeRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeRegister.SmeltingMixingFurnaceRecipes, RecipeMaps.plasmaForgeRecipes);
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        if (wirelessMode) {
            if (machineMode == MACHINEMODE_DTPF) {
                logic.setAvailableVoltage(Integer.MAX_VALUE);
                logic.setAvailableAmperage((long) Math.pow(4, mParallelTier) * 8L - 2L);
                logic.setAmperageOC(false);
                logic.enablePerfectOverclock();
                return;
            }
            logic.setAvailableVoltage(V[Math.min(mParallelTier + 1, 14)]);
            logic.setAvailableAmperage((long) Math.pow(4, mParallelTier) * 8L - 2L);
            logic.setAmperageOC(false);
            logic.enablePerfectOverclock();
        } else {
            boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty();
            logic.setAvailableVoltage(getMachineVoltageLimit());
            logic.setAvailableAmperage(useSingleAmp ? 1 : getMaxInputAmps());
            logic.setAmperageOC(!mExoticEnergyHatches.isEmpty() || mEnergyHatches.size() != 1);
        }
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        hasRequiredItem = false;

        if (this.getRecipeMap() == RecipeMaps.plasmaForgeRecipes) {
            ItemStack requiredItem = ItemList.Transdimensional_Alignment_Matrix.get(1);
            ItemStack item = getControllerSlot();
            if (item != null && item.isItemEqual(requiredItem)) {
                hasRequiredItem = true;
            }

            if (!hasRequiredItem) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        }
        return super.checkProcessing();
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4 && machineMode != MACHINEMODE_DTPF
                    && wirelessMode) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                enablePerfectOverclock();
                return super.process();
            }

            @Nonnull
            @Override
            protected GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {

                return super.createOverclockCalculator(recipe).setEUtDiscount(0.4 - (mParallelTier / 50.0))
                    .setDurationModifier(1 * Math.pow(0.75, mParallelTier))
                    .setExtraDurationModifier(configSpeedBoost);
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (byte) ((this.machineMode + 1) % 2);
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("SmeltingMixingFurnace_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("SmeltingMixingFurnace_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mode", machineMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        machineMode = aNBT.getInteger("mode");
    }

    @Override
    public String[] getInfoData() {
        final String running = (this.mMaxProgresstime > 0 ? "Machine running" : "Machine stopped");
        final String maintenance = (this.getIdealStatus() == this.getRepairStatus() ? "No Maintenance issues"
            : "Needs Maintenance");
        String tSpecialText;

        if (lastRecipeToBuffer != null && lastRecipeToBuffer.mOutputs[0].getDisplayName() != null) {
            tSpecialText = "Currently processing: " + lastRecipeToBuffer.mOutputs[0].getDisplayName();
        } else {
            tSpecialText = "Currently processing: Nothing";
        }

        return new String[] { "Industrial Cutting Factory", running, maintenance, tSpecialText };
    }

}
