package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.ArrayList;
import java.util.List;

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
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.gtnl.MatterFabricatorRecipes;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtnhlanth.common.register.LanthItemList;
import tectech.thing.casing.BlockGTCasingsTT;

public class MatterFabricator extends GTMMultiMachineBase<MatterFabricator> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/matter_fabricator";
    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(MF_STRUCTURE_FILE_PATH);

    public MatterFabricator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MatterFabricator(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MatterFabricator(this.mName);
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
        return BlockGTCasingsTT.textureOffset;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.MatterFabricatorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MatterFabricatorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(15, 5, 6, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_MatterFabricator_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MatterFabricator_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MatterFabricator_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_MatterFabricator_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<MatterFabricator> getStructureDefinition() {
        return StructureDefinition.<MatterFabricator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('B', StructureUtility.ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 7))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 15))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 11))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement(
                'G',
                GTStructureUtility.buildHatchAdder(MatterFabricator.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 0))))
            .addElement('H', GTStructureUtility.ofFrame(Materials.Naquadria))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 115;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && (mEnergyHatches.size() == 1 || mExoticEnergyHatches.size() == 1);
    }

    @Override
    public boolean checkEnergyHatch() {
        return true;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public VoidingMode getVoidingMode() {
        return VoidingMode.VOID_ALL;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        resetParallelTier();

        final ItemStack outputItem = MatterFabricatorRecipes.PAINT_BALL.copy();
        final int maxParallel = getTrueParallel();

        boolean hasCircuit1 = false;
        boolean hasCircuit2 = false;

        for (ItemStack item : getAllStoredInputs()) {
            if (item == null) continue;
            if (GTUtility.areStacksEqual(item, GTUtility.getIntegratedCircuit(1))) hasCircuit1 = true;
            if (GTUtility.areStacksEqual(item, GTUtility.getIntegratedCircuit(2))) hasCircuit2 = true;
        }

        if (hasCircuit1 == hasCircuit2) return CheckRecipeResultRegistry.NO_RECIPE;

        boolean foundValidInput = false;
        long totalOutput = 0;

        for (ItemStack item : getAllStoredInputs()) {
            if (GTUtility.isStackInvalid(item)) continue;

            ItemData data = GTOreDictUnificator.getItemData(item);
            if (data == null) continue;

            long count;
            switch (data.mPrefix) {
                case gem, ingot -> {
                    count = Math.min(item.stackSize, maxParallel - totalOutput);
                    item.stackSize -= (int) count;
                    totalOutput += count;
                    foundValidInput = true;
                }
                case block -> {
                    count = Math.min(item.stackSize * 9L, (maxParallel - totalOutput) * 9L);
                    long blocksUsed = count / 9L;
                    item.stackSize -= (int) blocksUsed;
                    totalOutput += count;
                    foundValidInput = true;
                }
            }

            if (totalOutput >= maxParallel) break;
        }

        updateSlots();
        if (!foundValidInput || totalOutput == 0) return CheckRecipeResultRegistry.NO_RECIPE;

        if (hasCircuit1) {
            List<ItemStack> outputItems = new ArrayList<>();
            long remaining = totalOutput;
            while (remaining > 0) {
                int stackSize = (int) Math.min(remaining, Integer.MAX_VALUE);
                outputItems.add(new ItemStack(outputItem.getItem(), stackSize, outputItem.getItemDamage()));
                remaining -= stackSize;
            }
            mOutputItems = outputItems.toArray(new ItemStack[0]);
        } else {
            List<FluidStack> outputFluids = new ArrayList<>();
            long fluidAmount = totalOutput * 100000L;
            while (fluidAmount > 0) {
                int amount = (int) Math.min(fluidAmount, Integer.MAX_VALUE);
                outputFluids.add(new FluidStack(Materials.UUAmplifier.getFluid(1), amount));
                fluidAmount -= amount;
            }
            mOutputFluids = outputFluids.toArray(new FluidStack[0]);
        }

        this.lEUt = -totalOutput * 4L;
        this.mProgresstime = 0;
        this.mEfficiency = 10000;
        this.mMaxProgresstime = (int) (128 * mConfigSpeedBoost);

        return CheckRecipeResultRegistry.SUCCESSFUL;
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
