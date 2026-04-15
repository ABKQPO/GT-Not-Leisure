package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;

import bartworks.util.BWUtil;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class MegaAlloyBlastSmelter extends GTMMultiMachineBase<MegaAlloyBlastSmelter>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String MABS_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/mega_alloy_blast_smelter";
    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 15;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(MABS_STRUCTURE_FILE_PATH);

    public static final Random random = new XSTR();

    public MegaAlloyBlastSmelter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MegaAlloyBlastSmelter(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MegaAlloyBlastSmelter(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDAlloyBlastSmelterActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDAlloyBlastSmelter)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(15);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.alloyBlastSmelterRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MegaAlloyBlastSmelterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_05"))
            .addPerfectOCInfo()
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(11, 18, 11, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_MegaAlloyBlastSmelter_Casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<MegaAlloyBlastSmelter> getStructureDefinition() {
        return StructureDefinition.<MegaAlloyBlastSmelter>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 15))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 14))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 15))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 3))
            .addElement(
                'F',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility
                            .ofCoil(MegaAlloyBlastSmelter::setMCoilLevel, MegaAlloyBlastSmelter::getMCoilLevel))))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 4))
            .addElement('H', StructureUtility.ofBlock(blockCasingsMisc, 14))
            .addElement(
                'I',
                GTStructureUtility.buildHatchAdder(MegaAlloyBlastSmelter.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(blockCasingsMisc, 15))))
            .addElement('J', HatchElement.Muffler.newAny(getCasingTextureID(), 1))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 290;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None && mMufflerHatches.size() == 1;
    }

    @Override
    public boolean checkEnergyHatch() {
        return true;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        this.mHeatingCapacity = (int) this.getMCoilLevel()
            .getHeat() + 100 * (BWUtil.getTier(this.getMaxInputEu()) - 2);
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
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public int getMachineHeat() {
        return mHeatingCapacity;
    }

    @Override
    public boolean getHeatOC() {
        return true;
    }

    @Override
    public double getEUtDiscount() {
        return Math.max(0.005, 0.8 - (mParallelTier / 50.0));
    }

    @Override
    public double getDurationModifier() {
        return Math.max(0.005, 1.0 / 2.0 - (Math.max(0, mParallelTier - 1) / 50.0));
    }

    @Override
    public void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setUnlimitedTierSkips();
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(getMaxInputEu());
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(true);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        CheckRecipeResult result = super.checkProcessing();
        if (!result.wasSuccessful()) return result;

        if (mOutputItems != null) {
            ArrayList<ItemStack> itemList = new ArrayList<>();

            for (ItemStack itemStack : mOutputItems) {
                if (itemStack == null) continue;

                if (random.nextInt(101) < (mGlassTier * 2)) {

                    long doubledAmount = (long) itemStack.stackSize * 2L;

                    if (itemStack.stackSize > Integer.MAX_VALUE / 2) {
                        GTNLParallelHelper.addItemsLong(itemList, itemStack, doubledAmount);
                    } else {
                        ItemStack copy = itemStack.copy();
                        copy.stackSize = (int) doubledAmount;
                        itemList.add(copy);
                    }

                } else {
                    itemList.add(itemStack.copy());
                }
            }

            mOutputItems = itemList.toArray(new ItemStack[0]);
        }

        if (mOutputFluids != null) {
            ArrayList<FluidStack> fluidList = new ArrayList<>();

            for (FluidStack fluidStack : mOutputFluids) {
                if (fluidStack == null) continue;

                if (random.nextInt(101) < (mGlassTier * 2)) {

                    long doubledAmount = (long) fluidStack.amount * 2L;

                    if (fluidStack.amount > Integer.MAX_VALUE / 2) {
                        GTNLParallelHelper.addFluidsLong(fluidList, fluidStack, doubledAmount);
                    } else {
                        FluidStack copy = fluidStack.copy();
                        copy.amount = (int) doubledAmount;
                        fluidList.add(copy);
                    }

                } else {
                    fluidList.add(fluidStack.copy());
                }
            }

            mOutputFluids = fluidList.toArray(new FluidStack[0]);
        }

        return result;
    }
}
