package com.science.gtnl.common.machine.multiblock.wireless;

import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtnhlanth.common.register.LanthItemList;
import tectech.thing.casing.TTCasingsContainer;

public class TransliminalOasis extends WirelessEnergyMultiMachineBase<TransliminalOasis> {

    private static final int HORIZONTAL_OFF_SET = 21;
    private static final int VERTICAL_OFF_SET = 22;
    private static final int DEPTH_OFF_SET = 2;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String TO_STRUCTURE_FILE_PATH = ScienceNotLeisure.RESOURCE_ROOT_ID + ":"
        + "multiblock/transliminal_oasis";
    private static final String[][] shape = StructureUtils.readStructureFromFile(TO_STRUCTURE_FILE_PATH);

    public TransliminalOasis(String aName) {
        super(aName);
    }

    public TransliminalOasis(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new TransliminalOasis(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("TransliminalOasisRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TransliminalOasis_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(43, 24, 39, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_TransliminalOasis_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_TransliminalOasis_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_TransliminalOasis_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_TransliminalOasis_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings10, 3);
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
    public IStructureDefinition<TransliminalOasis> getStructureDefinition() {
        return StructureDefinition.<TransliminalOasis>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 12))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 11))
            .addElement('D', StructureUtility.ofBlock(BlockLoader.metaCasing, 18))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 11))
            .addElement(
                'F',
                GTStructureUtility.buildHatchAdder(TransliminalOasis.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        CustomHatchElement.ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 6))
            .addElement('H', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 2))
            .addElement('L', StructureUtility.ofBlock(TTCasingsContainer.sBlockCasingsTT, 0))
            .addElement(
                'M',
                Mods.RandomThings.isModLoaded()
                    ? StructureUtility.ofChain(
                        StructureUtility.ofBlockAnyMeta(GameRegistry.findBlock(Mods.RandomThings.ID, "fertilizedDirt")),
                        StructureUtility
                            .ofBlockAnyMeta(GameRegistry.findBlock(Mods.RandomThings.ID, "fertilizedDirt_tilled")))
                    : StructureUtility.ofBlockAnyMeta(Blocks.dirt))
            .addElement('N', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 9))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockTintedGlass, 0))
            .addElement('P', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 31))
            .addElement('Q', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 1))
            .addElement('R', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('S', StructureUtility.ofBlock(GregTechAPI.sBlockGlass1, 0))
            .addElement('T', GTStructureUtility.ofFrame(Materials.Polytetrafluoroethylene))
            .addElement('U', StructureUtility.ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .build();
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing > 100;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.95, mGlassTier);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.95, mGlassTier);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.WoodcutterRecipes;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        maxParallelStored = -1;
        resetParallelTier();
        costingEU = BigInteger.ZERO;
        costingEUText = Utils.ZERO_STRING;
        totalOverclockedDuration = 0;
        cycleNow = 0;

        if (!wirelessMode) return super.checkProcessing();

        List<ItemStack> original = getAllStoredInputs();
        List<ItemStack> merged = new ArrayList<>();

        outer: for (ItemStack stack : original) {
            if (stack == null) continue;

            for (ItemStack existing : merged) {
                if (GTUtility.areStacksEqual(existing, stack)) {
                    continue outer;
                }
            }

            ItemStack copy = stack.copy();
            copy.stackSize = 1;
            merged.add(copy);
        }

        if (merged.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

        boolean succeeded = false;
        CheckRecipeResult finalResult = CheckRecipeResultRegistry.SUCCESSFUL;
        for (ItemStack stack : merged) {
            CheckRecipeResult r = wirelessModeProcessOnce(stack);

            if (!r.wasSuccessful()) {
                finalResult = r;
                break;
            }
            succeeded = true;
        }

        if (!succeeded) {
            return finalResult;
        }
        updateSlots();
        costingEUText = GTUtility.formatNumbers(costingEU);

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = totalOverclockedDuration;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public CheckRecipeResult wirelessModeProcessOnce(ItemStack stack) {
        if (!isRecipeProcessing) startRecipeProcessing();
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe(stack);
        if (!result.wasSuccessful()) {
            return result;
        }

        BigInteger costEU = BigInteger.valueOf(processingLogic.getCalculatedEut())
            .multiply(BigInteger.valueOf(processingLogic.getDuration()));

        if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(Utils.NEGATIVE_ONE))) {
            return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
        }

        costingEU = costingEU.add(costEU);
        mOutputItems = Utils.mergeArray(mOutputItems, processingLogic.getOutputItems());
        mOutputFluids = Utils.mergeArray(mOutputFluids, processingLogic.getOutputFluids());
        totalOverclockedDuration += processingLogic.getDuration();

        endRecipeProcessing();
        return result;
    }

    @NotNull
    public CheckRecipeResult doCheckRecipe(ItemStack stack) {
        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;
        processingLogic.setInputItems(stack);
        CheckRecipeResult foundResult = processingLogic.process();
        if (foundResult.wasSuccessful()) return foundResult;
        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
        return result;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mGlassTier", mGlassTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mGlassTier = aNBT.getInteger("mGlassTier");
    }
}
