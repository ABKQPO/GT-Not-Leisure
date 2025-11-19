package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ParallelHelper;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import bartworks.API.recipe.BartWorksRecipeMaps;
import bartworks.common.tileentities.tiered.MTERadioHatch;
import bartworks.util.BWUtil;
import bartworks.util.BioCulture;
import bartworks.util.ResultWrongSievert;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.recipe.Sievert;
import gregtech.common.misc.GTStructureChannels;
import gtnhlanth.common.register.LanthItemList;

public class LargeIncubator extends MultiMachineBase<LargeIncubator> implements ISurvivalConstructable {

    public int itemQuantity;
    public ArrayList<MTERadioHatch> mRadHatches = new ArrayList<>();
    public int height = 1;
    public Fluid mFluid = FluidRegistry.LAVA;
    public BioCulture mCulture;
    public int mSievert;
    public int mNeededSievert;
    public boolean isVisibleFluid = false;
    public Sievert defaultSievertData = new Sievert(0, false);
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String L_INCUBATOR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_incubator";
    private static final String[][] shape = StructureUtils.readStructureFromFile(L_INCUBATOR_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 6;
    private static final int VERTICAL_OFF_SET = 7;
    private static final int DEPTH_OFF_SET = 0;

    public LargeIncubator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeIncubator(String aName) {
        super(aName);
    }

    @Override
    public int getCasingTextureID() {
        return 210;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeIncubatorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeIncubator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeIncubator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeIncubator_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeIncubator_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeIncubator_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .beginStructureBlock(13, 9, 13, false)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeIncubator_Casing"), 1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("Tooltip_LargeIncubator_Radio_Hatch"),
                StatCollector.translateToLocal("Tooltip_LargeIncubator_Casing"),
                1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeIncubator_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeIncubator_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeIncubator_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeIncubator_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeIncubator_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeIncubator> getStructureDefinition() {
        return StructureDefinition.<LargeIncubator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('C', ofBlock(sBlockCasings8, 1))
            .addElement('D', ofBlock(sBlockCasings9, 1))
            .addElement(
                'E',
                ofChain(
                    buildHatchAdder(LargeIncubator.class)
                        .atLeast(
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch,
                            Maintenance,
                            Energy.or(ExoticEnergy),
                            RadioHatchElement.RadioHatch)
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    onElementPass(e -> e.mCountCasing++, ofBlock(sBlockReinforced, 2))))
            .addElement(
                'F',
                ofBlock(
                    EtFuturumRequiem.isModLoaded() ? GameRegistry.findBlock(EtFuturumRequiem.ID, "sponge")
                        : Blocks.sponge,
                    1))
            .addElement('G', ofChain(isAir(), ofBlockAnyMeta(Blocks.flowing_water), ofBlockAnyMeta(Blocks.water)))
            .build();
    }

    public int getInputCapacity() {
        return this.mInputHatches.stream()
            .mapToInt(MTEHatchInput::getCapacity)
            .sum();
    }

    @Override
    public int getCapacity() {
        int ret = 0;
        ret += this.getInputCapacity();
        return ret;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return super.fill(resource, doFill);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return BartWorksRecipeMaps.bacterialVatRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                Sievert data = recipe.getMetadataOrDefault(GTRecipeConstants.SIEVERT, defaultSievertData);
                int sievert = data.sievert;
                if (!BWUtil.areStacksEqualOrNull((ItemStack) recipe.mSpecialItems, getControllerSlot()))
                    return CheckRecipeResultRegistry.NO_RECIPE;
                mNeededSievert = sievert;

                if (mSievert < mNeededSievert) {
                    return ResultWrongSievert.insufficientSievert(mNeededSievert);
                }

                ItemStack controllerSlotItem = getControllerSlot();
                itemQuantity = controllerSlotItem != null ? controllerSlotItem.stackSize : 1;

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setPerfectOC(getPerfectOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @NotNull
            @Override
            public GTNL_ParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(
                    recipeWithMultiplier(recipe, inputFluids, mOutputHatches.get(0), getTrueParallel()));
            }
        };
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public double getEUtDiscount() {
        return 0.6;
    }

    @Override
    public double getDurationModifier() {
        return 1.0 / 5.0;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 4 * itemQuantity + 2 * GTUtility.getTier(this.getMaxInputVoltage()) * mGlassTier;
    }

    public static GTRecipe recipeWithMultiplier(GTRecipe recipe, FluidStack[] fluidInputs, MTEHatchOutput output,
        int parallel) {
        if (recipe == null || fluidInputs == null) {
            return recipe;
        }

        if (recipe.mFluidInputs == null || recipe.mFluidInputs.length == 0
            || recipe.mFluidOutputs == null
            || recipe.mFluidOutputs.length == 0) {
            return recipe;
        }

        if (recipe.mFluidInputs[0] == null || recipe.mFluidOutputs[0] == null) {
            return recipe;
        }

        for (FluidStack fluid : fluidInputs) {
            if (fluid == null) {
                return recipe;
            }
        }

        GTRecipe tRecipe = recipe.copy();
        int multiplier;

        long fluidAmount = 0;
        for (FluidStack fluid : fluidInputs) {
            if (recipe.mFluidInputs[0].isFluidEqual(fluid)) {
                fluidAmount += fluid.amount;
            }
        }

        multiplier = (int) fluidAmount / (recipe.mFluidInputs[0].amount * 1001);
        multiplier = Math.max(Math.min(multiplier, parallel), 1);
        multiplier *= getExpectedMultiplier(recipe.getFluidOutput(0), output);

        tRecipe.mFluidInputs[0].amount *= multiplier;
        tRecipe.mFluidOutputs[0].amount *= multiplier;

        return tRecipe;
    }

    public static int getExpectedMultiplier(@Nullable FluidStack recipeFluidOutput, MTEHatchOutput output) {
        FluidStack storedFluidOutputs = output.getFluid();
        if (storedFluidOutputs == null) return 1001;
        if (storedFluidOutputs.isFluidEqual(recipeFluidOutput)) {
            return 1001;
        }
        return 1;
    }

    @Override
    public void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setSpecialSlotItem(this.getControllerSlot());
    }

    public boolean addRadiationInputToMachineList(IGregTechTileEntity aTileEntity, int CasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (!(aMetaTileEntity instanceof MTERadioHatch radioHatch)) {
            return false;
        } else {
            radioHatch.updateTexture(CasingIndex);
            return this.mRadHatches.add(radioHatch);
        }
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack itemStack) {
        if (!this.checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
            || !checkHatch()) {
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
            "G",
            Blocks.water);
        setupParameters();
        return mCountCasing > 19;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && this.mRadHatches.size() <= 1
            && !this.mOutputHatches.isEmpty()
            && !this.mInputHatches.isEmpty()
            && !(this.mEnergyHatches.isEmpty() && this.mExoticEnergyHatches.isEmpty());
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        this.mRadHatches.clear();
    }

    public int reCalculateFluidAmmount() {
        return this.getStoredFluids()
            .stream()
            .mapToInt(fluidStack -> fluidStack.amount)
            .sum();
    }

    public void reCalculateHeight() {
        if (this.reCalculateFluidAmmount() > this.getCapacity() / 4 - 1) {
            this.reCalculateFluidAmmount();
            this.getCapacity();
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        this.reCalculateHeight();
        if (this.getBaseMetaTileEntity()
            .isServerSide() && this.mRadHatches.size() == 1) {
            this.mSievert = this.mRadHatches.get(0)
                .getSievert();
            if (this.getBaseMetaTileEntity()
                .isActive() && this.mNeededSievert > this.mSievert) this.mOutputFluids = null;
        }
        if (aBaseMetaTileEntity.isServerSide() && this.mMaxProgresstime <= 0) {
            this.mMaxProgresstime = 0;
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("mFluidHeight", this.height);
        if (this.mCulture != null && !this.mCulture.getName()
            .isEmpty()) aNBT.setString("mCulture", this.mCulture.getName());
        else if ((this.mCulture == null || this.mCulture.getName()
            .isEmpty()) && !aNBT.getString("mCulture")
                .isEmpty()) {
                    aNBT.removeTag("mCulture");
                }
        if (this.mFluid != null) aNBT.setString("mFluid", this.mFluid.getName());
        aNBT.setInteger("mSievert", this.mSievert);
        aNBT.setInteger("mNeededSievert", this.mNeededSievert);
        aNBT.setBoolean("isVisibleFluid", this.isVisibleFluid);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        this.height = aNBT.getInteger("mFluidHeight");
        this.mCulture = BioCulture.getBioCulture(aNBT.getString("mCulture"));
        if (!aNBT.getString("mFluid")
            .isEmpty()) this.mFluid = FluidRegistry.getFluid(aNBT.getString("mFluid"));
        this.mSievert = aNBT.getInteger("mSievert");
        this.mNeededSievert = aNBT.getInteger("mNeededSievert");
        super.loadNBTData(aNBT);
        this.isVisibleFluid = aNBT.getBoolean("isVisibleFluid");
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new LargeIncubator(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public void construct(ItemStack itemStack, boolean b) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, itemStack, b, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
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
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (aPlayer.isSneaking()) {
            batchMode = !batchMode;
            if (batchMode) {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("misc.BatchModeTextOn"));
            } else {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("misc.BatchModeTextOff"));
            }
            return true;
        }
        return false;
    }

    public enum RadioHatchElement implements IHatchElement<LargeIncubator> {

        RadioHatch(LargeIncubator::addRadiationInputToMachineList, MTERadioHatch.class) {

            @Override
            public long count(LargeIncubator bioVat) {
                return bioVat.mRadHatches.size();
            }
        };

        public final List<Class<? extends IMetaTileEntity>> mteClasses;
        public final IGTHatchAdder<LargeIncubator> adder;

        @SafeVarargs
        RadioHatchElement(IGTHatchAdder<LargeIncubator> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public IGTHatchAdder<? super LargeIncubator> adder() {
            return adder;
        }
    }
}
