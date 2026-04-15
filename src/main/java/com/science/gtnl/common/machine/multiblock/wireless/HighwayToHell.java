package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
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
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import bartworks.common.loaders.FluidLoader;
import bartworks.system.material.WerkstoffLoader;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.MetaGeneratedTool;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchTurbine;

public class HighwayToHell extends WirelessEnergyMultiMachineBase<HighwayToHell> {

    private static final int HORIZONTAL_OFF_SET = 9;
    private static final int VERTICAL_OFF_SET = 15;
    private static final int DEPTH_OFF_SET = 5;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String HTH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/highway_to_hell";
    private static final String[][] shape = StructureUtils.readStructureFromFile(HTH_STRUCTURE_FILE_PATH);

    public ArrayList<MTEHatchTurbine> mTurbineHatches = new ArrayList<>();
    public boolean staticAnimations = false;
    public int turbineTier;

    public HighwayToHell(String aName) {
        super(aName);
    }

    public HighwayToHell(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HighwayToHell(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("HighwayToHellRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighwayToHell_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighwayToHell_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighwayToHell_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighwayToHell_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(19, 18, 17, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_HighwayToHell_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_HighwayToHell_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_HighwayToHell_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_HighwayToHell_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_HighwayToHell_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_HighwayToHell_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
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
    public IStructureDefinition<HighwayToHell> getStructureDefinition() {
        return StructureDefinition.<HighwayToHell>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 15))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 2))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 4))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('H', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement('I', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 6))
            .addElement('J', StructureUtility.ofBlock(ModBlocks.blockSpecialMultiCasings, 15))
            .addElement('K', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 14))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 9))
            .addElement(
                'M',
                StructureUtility
                    .ofChain(StructureUtility.isAir(), StructureUtility.ofBlockAnyMeta(FluidLoader.bioFluidBlock)))
            .addElement('N', HatchElement.Muffler.newAny(getCasingTextureID(), 1))
            .addElement(
                'O',
                GTStructureUtility.buildHatchAdder(HighwayToHell.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))))
            .addElement('P', StructureUtility.ofBlock(WerkstoffLoader.BWBlockCasingsAdvanced, 31895))
            .addElement('Q', CustomHatchElement.ROTOR_ASSEMBLY.newAny(getCasingTextureID(), 2))
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
        boolean isFlipped = this.getFlip()
            .isHorizontallyFlipped();
        StructureUtils.setStringBlockXZ(
            aBaseMetaTileEntity,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            shape,
            isFlipped,
            "M",
            FluidLoader.bioFluidBlock);
        setupParameters();
        rotateTurbines();
        return true;
    }

    @Override
    public boolean checkHatch() {
        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (mGlassTier < VoltageIndex.UMV && mEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        for (MTEHatch mExoticEnergyHatch : this.mExoticEnergyHatches) {
            if (mGlassTier < VoltageIndex.UMV && mExoticEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        if (mMufflerHatches.size() != 1 || mTurbineHatches.size() != 4) return false;
        for (MTEHatchTurbine h : GTUtility.validMTEList(this.mTurbineHatches)) {
            if (!h.hasTurbine()) return false;
        }
        return super.checkHatch();
    }

    @Override
    public void onRemoval() {
        boolean isFlipped = this.getFlip()
            .isHorizontallyFlipped();
        StructureUtils.setStringBlockXZ(
            getBaseMetaTileEntity(),
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            shape,
            isFlipped,
            "M",
            Blocks.air);
        super.onRemoval();
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mTurbineHatches.clear();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 100 == 0) {
            if (!getBaseMetaTileEntity().isActive() && !this.mTurbineHatches.isEmpty()) {
                setTurbineInactive();
            }
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        staticAnimations = aNBT.getBoolean("turbineAnimationsStatic");
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("turbineAnimationsStatic", staticAnimations);
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        staticAnimations = !staticAnimations;
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal(staticAnimations ? "Info_HighwayToHell_00" : "Info_HighwayToHell_01"));
        for (MTEHatchTurbine h : GTUtility.validMTEList(this.mTurbineHatches)) {
            h.mUsingAnimation = staticAnimations;
        }
    }

    public void rotateTurbines() {
        for (int i = 0; i < mTurbineHatches.size(); i++) {
            if (mTurbineHatches.get(i) == null) continue;
            MTEHatchTurbine turbine = mTurbineHatches.get(i);
            ForgeDirection direction = this.getDirection();
            IGregTechTileEntity te = turbine.getBaseMetaTileEntity();
            switch (i) {
                case 0 -> te.setFrontFacing(direction);
                case 1 -> te.setFrontFacing(direction.getRotation(ForgeDirection.UP));
                case 2 -> te.setFrontFacing(direction.getRotation(ForgeDirection.DOWN));
                case 3 -> te.setFrontFacing(direction.getOpposite());
            }
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        int base = super.getMaxParallelRecipes();
        base >>= 4;
        if (base < 1) base = 1;
        return base;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.ExtremeExtremeEntityCrusherRecipes;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        resetParallelTier();
        costingEU = BigInteger.ZERO;
        costingEUText = Utils.ZERO_STRING;
        totalOverclockedDuration = 0;
        turbineTier = 0;

        for (MTEHatchTurbine h : GTUtility.validMTEList(this.mTurbineHatches)) {
            if (!h.hasTurbine()) return CheckRecipeResultRegistry.NO_RECIPE;
            turbineTier = GTUtility.min(turbineTier, MetaGeneratedTool.getPrimaryMaterial(h.getTurbine()).mToolQuality);
        }

        if (!wirelessMode) {
            setupProcessingLogic(processingLogic);

            CheckRecipeResult result = doCheckRecipe();
            result = postCheckRecipe(result, processingLogic);
            // inputs are consumed at this point
            updateSlots();
            if (!result.wasSuccessful()) return result;

            mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = processingLogic.getDuration();
            setEnergyUsage(processingLogic);

            mOutputItems = processingLogic.getOutputItems();
            mOutputFluids = processingLogic.getOutputFluids();
            return result;
        }

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
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (wirelessMode) {
                    int voltageTier;
                    if (mGlassTier < VoltageIndex.UMV) {
                        voltageTier = GTUtility.min(mGlassTier, GTUtility.min(mParallelTier + 1, 14));
                    } else {
                        voltageTier = GTUtility.min(mParallelTier + 1, 14);
                    }

                    if (recipe.mEUt > GTValues.V[voltageTier] * 4) {
                        return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                    }
                }

                return super.validateRecipe(recipe);
            }

            @Override
            public @NotNull CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                setTurbineActive();
                return super.onRecipeStart(recipe);
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @NotNull
            @Override
            public GTNLParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(recipeWithTurbine(recipe, turbineTier));
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
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
        // Recipe failed in interesting way, so remember that
        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
        return result;
    }

    public static GTRecipe recipeWithTurbine(GTRecipe recipe, int turbineTier) {
        if (recipe == null || turbineTier == 0) {
            return recipe;
        }

        if (recipe.mOutputs == null || recipe.mOutputs.length == 0) {
            return recipe;
        }

        if (recipe.mChances == null) {
            return recipe;
        }

        GTRecipe tRecipe = recipe.copy();

        if (tRecipe.mChances == null) {
            return recipe;
        }

        for (int i = 0; i < tRecipe.mChances.length; i++) {
            tRecipe.mChances[i] = GTUtility.min(10000, tRecipe.mChances[i] + turbineTier * 100);
        }

        return tRecipe;
    }

    public void setTurbineActive() {
        if (staticAnimations) return;
        for (MTEHatchTurbine h : GTUtility.validMTEList(this.mTurbineHatches)) {
            h.setActive(true);
            h.onTextureUpdate();
        }
    }

    public void setTurbineInactive() {
        for (MTEHatchTurbine h : GTUtility.validMTEList(this.mTurbineHatches)) {
            h.setActive(false);
            h.onTextureUpdate();
        }
    }

    public boolean addTurbineHatch(final IGregTechTileEntity aTileEntity, final int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity instanceof MTEHatchTurbine turbine) {
            turbine.updateTexture(aBaseCasingIndex);
            mTurbineHatches.add(turbine);
            return true;
        }
        return false;
    }

    public enum CustomHatchElement implements IHatchElement<HighwayToHell> {

        ROTOR_ASSEMBLY(HighwayToHell::addTurbineHatch, MTEHatchTurbine.class) {

            @Override
            public long count(HighwayToHell HighwayToHell) {
                return HighwayToHell.mTurbineHatches.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<HighwayToHell> adder;

        @SafeVarargs
        CustomHatchElement(IGTHatchAdder<HighwayToHell> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super HighwayToHell> adder() {
            return adder;
        }
    }
}
