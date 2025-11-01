package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.casing.BlockGTCasingsTT;
import tectech.thing.metaTileEntity.multi.MTEEyeOfHarmony;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class EyeOfHarmonyInjector extends TTMultiblockBase implements IConstructable, ISurvivalConstructable {

    public static final double maxFluidAmount = Long.MAX_VALUE;
    public final ArrayList<MTEEyeOfHarmony> mEHO = new ArrayList<>();
    public Parameters.Group.ParameterIn maxFluidAmountSetting;
    public int tCountCasing;

    public EyeOfHarmonyInjector(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected EyeOfHarmonyInjector(String aName) {
        super(aName);
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing_EM() {
        this.onMachineBlockUpdate();
        NBTTagCompound nbt = new NBTTagCompound();
        mEHO.get(0)
            .onMachineBlockUpdate();
        mEHO.get(0)
            .saveNBTData(nbt);
        long InputHelium = 0;
        long InputHydrogen = 0;
        long InputRawstarmatter = 0;
        long helium = nbt.getLong("stored.fluid.helium");
        long hydrogen = nbt.getLong("stored.fluid.hydrogen");
        long rawstarmatter = nbt.getLong("stored.fluid.rawstarmatter");
        if (helium == Math.min(maxFluidAmount, maxFluidAmountSetting.get())
            && hydrogen == Math.min(maxFluidAmount, maxFluidAmountSetting.get())
            && rawstarmatter == Math.min(maxFluidAmount, maxFluidAmountSetting.get())) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        List<ItemStack> InputItemStack = getStoredInputs();
        List<ItemStack> OutputItemStack = new ArrayList<>();
        if (InputItemStack.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        for (ItemStack itemStack : InputItemStack) {
            if (itemStack.getItem() instanceof ItemFluidPacket) {
                NBTTagCompound NBT = itemStack.getTagCompound()
                    .getCompoundTag("FluidStack");
                switch (NBT.getString("FluidName")) {
                    case "hydrogen" -> {
                        if (hydrogen == Math.min(maxFluidAmount, maxFluidAmountSetting.get()) || hydrogen < 0
                            || Math.min(maxFluidAmount, maxFluidAmountSetting.get()) - hydrogen <= 0) break;
                        if (hydrogen + NBT.getLong("Amount") >= 0) {
                            if (hydrogen + NBT.getLong("Amount")
                                <= Math.min(maxFluidAmount, maxFluidAmountSetting.get())) {
                                InputHydrogen += NBT.getLong("Amount");
                                hydrogen += NBT.getLong("Amount");
                            } else {
                                int Input = (int) (Math.min(maxFluidAmount, maxFluidAmountSetting.get()) - hydrogen);
                                hydrogen = (long) Math.min(maxFluidAmount, maxFluidAmountSetting.get());
                                OutputItemStack.add(
                                    ItemFluidPacket.newStack(
                                        new FluidStack(
                                            Materials.Hydrogen.mGas,
                                            (int) (NBT.getLong("Amount") - Input))));
                            }
                        } else {
                            int Input = (int) (Math.min(maxFluidAmount, maxFluidAmountSetting.get()) - hydrogen);
                            InputHydrogen += Input;
                            OutputItemStack.add(
                                ItemFluidPacket.newStack(
                                    new FluidStack(Materials.Hydrogen.mGas, NBT.getInteger("Amount") - Input)));
                            hydrogen += Input;
                        }
                        itemStack.stackSize--;
                    }
                    case "helium" -> {
                        if (helium == Math.min(maxFluidAmount, maxFluidAmountSetting.get()) || helium < 0
                            || Math.min(maxFluidAmount, maxFluidAmountSetting.get()) - helium <= 0) break;
                        if (helium + NBT.getLong("Amount") >= 0) {
                            if (helium + NBT.getLong("Amount")
                                <= Math.min(maxFluidAmount, maxFluidAmountSetting.get())) {
                                InputHelium += NBT.getLong("Amount");
                                helium += NBT.getLong("Amount");
                            } else {
                                int Input = (int) (Math.min(maxFluidAmount, maxFluidAmountSetting.get()) - helium);
                                helium = (long) Math.min(maxFluidAmount, maxFluidAmountSetting.get());
                                OutputItemStack.add(
                                    ItemFluidPacket.newStack(
                                        new FluidStack(Materials.Helium.mGas, (int) (NBT.getLong("Amount") - Input))));
                            }
                        } else {
                            int Input = (int) (Math.min(maxFluidAmount, maxFluidAmountSetting.get()) - helium);
                            InputHelium += Input;
                            OutputItemStack.add(
                                ItemFluidPacket
                                    .newStack(new FluidStack(Materials.Helium.mGas, NBT.getInteger("Amount") - Input)));
                            helium += Input;
                        }
                        itemStack.stackSize--;
                    }
                    case "rawstarmatter" -> {
                        if (rawstarmatter == Math.min(maxFluidAmount, maxFluidAmountSetting.get()) || rawstarmatter < 0
                            || Math.min(maxFluidAmount, maxFluidAmountSetting.get()) - rawstarmatter <= 0) break;
                        if (rawstarmatter + NBT.getLong("Amount") >= 0) {
                            if (rawstarmatter + NBT.getLong("Amount")
                                <= Math.min(maxFluidAmount, maxFluidAmountSetting.get())) {
                                InputRawstarmatter += NBT.getLong("Amount");
                                rawstarmatter += NBT.getLong("Amount");
                            } else {
                                int Input = (int) (Math.min(maxFluidAmount, maxFluidAmountSetting.get())
                                    - rawstarmatter);
                                rawstarmatter = (long) Math.min(maxFluidAmount, maxFluidAmountSetting.get());
                                OutputItemStack.add(
                                    ItemFluidPacket.newStack(
                                        new FluidStack(
                                            MaterialsUEVplus.RawStarMatter.mFluid,
                                            (int) (NBT.getLong("Amount") - Input))));
                            }
                        } else {
                            int Input = (int) (Math.min(maxFluidAmount, maxFluidAmountSetting.get()) - rawstarmatter);
                            InputRawstarmatter += Input;
                            OutputItemStack.add(
                                ItemFluidPacket.newStack(
                                    new FluidStack(
                                        MaterialsUEVplus.RawStarMatter.mFluid,
                                        NBT.getInteger("Amount") - Input)));
                            rawstarmatter += Input;
                        }
                        itemStack.stackSize--;
                    }
                }

            }
        }
        updateSlots();
        if (InputHelium != 0 || InputHydrogen != 0 || InputRawstarmatter != 0 || !OutputItemStack.isEmpty()) {
            nbt.setLong("stored.fluid.helium", helium);
            nbt.setLong("stored.fluid.hydrogen", hydrogen);
            nbt.setLong("stored.fluid.rawstarmatter", rawstarmatter);
            mEHO.get(0)
                .loadNBTData(nbt);
            mEHO.get(0)
                .onMachineBlockUpdate();
            mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = 20;
            mOutputItems = getOutputItemStack(OutputItemStack);
            return CheckRecipeResultRegistry.SUCCESSFUL;
        } else return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public ItemStack[] getOutputItemStack(List<ItemStack> OutputItemStack) {
        ItemStack[] Output = new ItemStack[OutputItemStack.size()];
        int index = 0;
        for (ItemStack itemStack : OutputItemStack) {
            Output[index] = itemStack;
            index++;
        }
        return Output;
    }

    private static final int HORIZONTAL_OFF_SET = 26;
    private static final int VERTICAL_OFF_SET = 6;
    private static final int DEPTH_OFF_SET = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EOHI_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/eye_of_harmony_injector";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EOHI_STRUCTURE_FILE_PATH);

    @Override
    public IStructureDefinition<? extends EyeOfHarmonyInjector> getStructure_EM() {
        return StructureDefinition.<EyeOfHarmonyInjector>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasingsTT, 6))
            .addElement('B', ofBlock(sBlockCasingsTT, 7))
            .addElement('C', ofBlock(sBlockCasings1, 13))
            .addElement('D', ofBlock(sBlockCasings9, 14))
            .addElement('E', ofBlock(sBlockCasingsTT, 8))
            .addElement('F', ofBlock(sBlockCasings10, 12))
            .addElement('G', ofBlock(sBlockCasingsTT, 4))
            .addElement('H', ofBlock(sBlockCasings10, 8))
            .addElement('I', ofBlock(sBlockCasings1, 12))
            .addElement('J', ofFrame(Materials.CosmicNeutronium))
            .addElement('K', ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('L', ofBlock(sBlockCasings1, 14))
            .addElement('M', ofBlock(sBlockCasings10, 7))
            .addElement('N', ofBlock(sBlockCasings3, 12))
            .addElement('O', ofBlock(sBlockCasings9, 11))
            .addElement(
                'P',
                ofChain(
                    buildHatchAdder(EyeOfHarmonyInjector.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Energy.or(ExoticEnergy))
                        .buildAndChain(),
                    onElementPass(e -> e.tCountCasing++, ofBlock(sBlockCasingsTT, 0))))
            .addElement(
                'Q',
                HatchElementBuilder.<EyeOfHarmonyInjector>builder()
                    .atLeast(EOH.EOH)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .build())
            .build();
    }

    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset;
    }

    public final boolean addEHO(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEEyeOfHarmony) {
            return mEHO.add((MTEEyeOfHarmony) aMetaTileEntity);
        }
        return false;
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) {
            return -1;
        } else {
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

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM(
            STRUCTURE_PIECE_MAIN,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            stackSize,
            hintsOnly);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mEHO.clear();
        return structureCheck_EM(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    public static final INameFunction<EyeOfHarmonyInjector> MAX_FLUID_AMOUNT_SETTING_NAME = (base, p) -> StatCollector
        .translateToLocal("Tooltip_EyeOfHarmonyInjector_Parametrization");

    public static final IStatusFunction<EyeOfHarmonyInjector> MAX_FLUID_AMOUNT_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, maxFluidAmount / 2, maxFluidAmount, maxFluidAmount);

    @Override
    protected void parametersInstantiation_EM() {
        super.parametersInstantiation_EM();
        Parameters.Group hatch_0 = parametrization.getGroup(0, false);
        maxFluidAmountSetting = hatch_0
            .makeInParameter(0, maxFluidAmount, MAX_FLUID_AMOUNT_SETTING_NAME, MAX_FLUID_AMOUNT_STATUS);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("EyeOfHarmonyInjectorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_05"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(53, 13, 62, false)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EyeOfHarmonyInjector(mName);
    }

    public enum EOH implements IHatchElement<EyeOfHarmonyInjector> {

        EOH(EyeOfHarmonyInjector::addEHO, MTEEyeOfHarmony.class) {

            @Override
            public long count(EyeOfHarmonyInjector gtTieEntityMagesTower) {
                return 0;
            }
        };

        public final List<Class<? extends IMetaTileEntity>> mteClasses;
        public final IGTHatchAdder<EyeOfHarmonyInjector> adder;

        @SafeVarargs
        EOH(IGTHatchAdder<EyeOfHarmonyInjector> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return this.mteClasses;
        }

        @Override
        public IGTHatchAdder<? super EyeOfHarmonyInjector> adder() {
            return this.adder;
        }

    }
}
