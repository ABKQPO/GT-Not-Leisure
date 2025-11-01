package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.util.GTUtility.validMTEList;
import static gtPlusPlus.core.block.ModBlocks.blockCasings5Misc;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.utils.recipes.IsaMillTierKey;

import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.item.chemistry.general.ItemGenericChemBase;
import gtPlusPlus.core.util.math.MathUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.nbthandlers.MTEHatchMillingBalls;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class IsaMill extends GTMMultiMachineBase<IsaMill> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String IM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/isa_mill";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(IM_STRUCTURE_FILE_PATH);

    private final ArrayList<MTEHatchMillingBalls> mMillingBallBuses = new ArrayList<>();

    public IsaMill(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public IsaMill(String aName) {
        super(aName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("IsaMillRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IsaMill_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IsaMill_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(5, 5, 9, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_IsaMill_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_IsaMill_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_IsaMill_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_IsaMill_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_IsaMill_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<IsaMill> getStructureDefinition() {
        return StructureDefinition.<IsaMill>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement(
                'B',
                ofChain(
                    buildHatchAdder(IsaMill.class).adder(IsaMill::addMillingBallsHatch)
                        .hatchClass(MTEHatchMillingBalls.class)
                        .shouldReject(t -> !t.mMillingBallBuses.isEmpty())
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    buildHatchAdder(IsaMill.class)
                        .atLeast(InputBus, OutputBus, InputHatch, Maintenance, Energy.or(ExoticEnergy), ParallelCon)
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    onElementPass(x -> ++x.mCountCasing, ofBlock(blockCasings5Misc, 0))))
            .addElement('C', ofBlock(blockCasings5Misc, 1))
            .addElement('D', ofBlock(blockCasings5Misc, 2))
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
        return mCountCasing >= 48;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mMillingBallBuses.clear();
    }

    @Override
    public boolean checkHatch() {
        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (mGlassTier < VoltageIndex.UHV & mEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        for (MTEHatch mExoticEnergyHatch : this.mExoticEnergyHatches) {
            if (mGlassTier < VoltageIndex.UHV && mExoticEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        return super.checkHatch() && mMillingBallBuses.size() == 1;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return getMaxEfficiency(aStack) > 0;
    }

    private boolean addMillingBallsHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchMillingBalls) {
                return addToMachineListInternal(mMillingBallBuses, aMetaTileEntity, aBaseCasingIndex);
            }
        }
        return false;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {

        final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchMillingBalls) {
            return addToMachineListInternal(mMillingBallBuses, aMetaTileEntity, aBaseCasingIndex);
        }
        return super.addToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.IsaMillRecipes;
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

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(2);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (this.mUpdate == 1 || this.mStartUpCheck == 1) {
                this.mMillingBallBuses.clear();
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new IsaMill(this.mName);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 1;
    }

    @Override
    public ArrayList<ItemStack> getStoredInputs() {
        ArrayList<ItemStack> tItems = super.getStoredInputs();
        for (MTEHatchMillingBalls tHatch : validMTEList(mMillingBallBuses)) {
            ArrayList<ItemStack> aHatchContent = tHatch.getContentUsageSlots();
            if (!aHatchContent.isEmpty()) {
                tItems.addAll(aHatchContent);
            }
        }
        return tItems;
    }

    public int getMaxBallDurability(ItemStack aStack) {
        return ItemGenericChemBase.getMaxBallDurability(aStack);
    }

    private ItemStack findMillingBall(int recipeReq) {
        final ItemStack AluminaMillingBall = GregtechItemList.Milling_Ball_Alumina.get(1);
        final ItemStack SoapstoneMillingBall = GregtechItemList.Milling_Ball_Soapstone.get(1);
        if (mMillingBallBuses.size() != 1) {
            return null;
        }
        MTEHatchMillingBalls aBus = mMillingBallBuses.get(0);
        if (aBus != null) {
            ArrayList<ItemStack> aAvailableItems = aBus.getContentUsageSlots();
            if (!aAvailableItems.isEmpty()) {
                for (ItemStack aBall : aAvailableItems) {
                    if (aBall != null) {
                        switch (recipeReq) {
                            case 1:
                                if (aBall.isItemEqual(AluminaMillingBall)) {
                                    return aBall;
                                }
                                break;
                            case 2:
                                if (aBall.isItemEqual(SoapstoneMillingBall)) {
                                    return aBall;
                                }
                                break;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void damageMillingBall(ItemStack aStack) {
        if (MathUtils.randFloat(0, 10000000) / 10000000f < (1.2f - (0.2 * 1))) {
            int damage = getMillingBallDamage(aStack) + 1;
            if (damage >= getMaxBallDurability(aStack)) {
                aStack.stackSize -= 1;
            } else {
                setDamage(aStack, damage);
            }
        }
    }

    private int getMillingBallDamage(ItemStack aStack) {
        return ItemGenericChemBase.getMillingBallDamage(aStack);
    }

    private void setDamage(ItemStack aStack, int aAmount) {
        ItemGenericChemBase.setMillingBallDamage(aStack, aAmount);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            ItemStack millingBall;

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {

                int recipeReq = recipe.getMetadataOrDefault(IsaMillTierKey.INSTANCE, 0);
                millingBall = findMillingBall(recipeReq);

                if (millingBall == null) {
                    return SimpleCheckRecipeResult.ofFailure("no_milling_ball ");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                CheckRecipeResult result = super.process();
                if (result.wasSuccessful()) {
                    damageMillingBall(millingBall);
                }
                return result;
            }

            @NotNull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setPerfectOC(getPerfectOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

        }.enablePerfectOverclock()
            .setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public double getEUtDiscount() {
        return 1 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1 - (Math.max(0, mParallelTier - 1) / 50.0);
    }
}
