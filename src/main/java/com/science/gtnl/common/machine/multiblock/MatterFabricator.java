package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings3;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.AppliedEnergistics2;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.getIntegratedCircuit;
import static gtnhlanth.common.register.LanthItemList.ELECTRODE_CASING;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipeRegister;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import tectech.thing.casing.BlockGTCasingsTT;

public class MatterFabricator extends GTMMultiMachineBase<MatterFabricator> implements ISurvivalConstructable {

    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<MatterFabricator> STRUCTURE_DEFINITION = null;
    public static final String MF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/matter_fabricator";
    public static final int CASING_INDEX = BlockGTCasingsTT.textureOffset;
    public final int HORIZONTAL_OFF_SET = 4;
    public final int VERTICAL_OFF_SET = 2;
    public final int DEPTH_OFF_SET = 0;
    public static String[][] shape = StructureUtils.readStructureFromFile(MF_STRUCTURE_FILE_PATH);

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
        return CASING_INDEX;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.MatterFabricatorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MatterFabricatorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
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
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MatterFabricator>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(BlockLoader.MetaCasing, 4))
                .addElement('B', ofBlockAnyMeta(ELECTRODE_CASING))
                .addElement('C', ofBlock(sBlockCasings1, 7))
                .addElement('D', ofBlock(sBlockCasings1, 15))
                .addElement('E', ofBlock(sBlockCasings3, 11))
                .addElement('F', ofBlock(sBlockCasings8, 10))
                .addElement(
                    'G',
                    buildHatchAdder(MatterFabricator.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(OutputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasingsTT, 0))))
                .addElement('H', ofFrame(Materials.Naquadria))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        mParallelTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()) {
            return false;
        }

        energyHatchTier = checkEnergyHatchTier();
        mParallelTier = getParallelTier(aStack);
        return tCountCasing >= 115 && this.mEnergyHatches.size() == 1;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        ItemStack controllerItem = getControllerSlot();
        this.mParallelTier = getParallelTier(controllerItem);
        boolean foundValidInput = false;
        long outputAmount = 0;
        final Item MatterBall = GameRegistry.findItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial");
        ItemStack outputItem = new ItemStack(MatterBall, 1, 6);
        List<FluidStack> outputFluids = new ArrayList<>();

        boolean hasCircuit1 = false;
        boolean hasCircuit2 = false;
        int maxParallelRecipes = getMaxParallelRecipes();

        for (ItemStack item : getAllStoredInputs()) {
            if (item != null) {
                if (item.getItem() == getIntegratedCircuit(1).getItem()
                    && item.getItemDamage() == getIntegratedCircuit(1).getItemDamage()) {
                    if (hasCircuit2) return CheckRecipeResultRegistry.NO_RECIPE;
                    hasCircuit1 = true;
                }
                if (item.getItem() == getIntegratedCircuit(2).getItem()
                    && item.getItemDamage() == getIntegratedCircuit(2).getItemDamage()) {
                    if (hasCircuit1) return CheckRecipeResultRegistry.NO_RECIPE;
                    hasCircuit2 = true;
                }
            }
        }

        if (!hasCircuit1 && !hasCircuit2) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        for (ItemStack item : getAllStoredInputs()) {
            if (GTUtility.isStackInvalid(item)) continue;

            ItemData itemData = GTOreDictUnificator.getItemData(item);
            if (itemData == null) continue;

            if (itemData.mPrefix == OrePrefixes.gem || itemData.mPrefix == OrePrefixes.ingot) {
                long itemCount = Math.min(item.stackSize, maxParallelRecipes);
                outputAmount += itemCount;
                item.stackSize -= itemCount;
                foundValidInput = true;
            } else if (itemData.mPrefix == OrePrefixes.block) {
                long itemCount = Math.min(item.stackSize * 9L, maxParallelRecipes * 9L);
                outputAmount += itemCount;
                item.stackSize -= (itemCount / 9L);
                foundValidInput = true;
            }

            if (outputAmount >= maxParallelRecipes) break;
        }

        updateSlots();

        if (!foundValidInput) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (hasCircuit1) {
            List<ItemStack> outputItems = new ArrayList<>();
            while (outputAmount > 0) {
                int stackSize = (int) (640 * Math.min(outputAmount, Integer.MAX_VALUE));
                outputItems.add(new ItemStack(outputItem.getItem(), stackSize, outputItem.getItemDamage()));
                outputAmount -= stackSize;
            }
            mOutputItems = outputItems.toArray(new ItemStack[0]);
        } else if (hasCircuit2) {
            long fluidAmount = outputAmount * 100000;
            while (fluidAmount > 0) {
                int stackSize = (int) Math.min(fluidAmount, Integer.MAX_VALUE);
                outputFluids.add(new FluidStack(Materials.UUAmplifier.getFluid(1000), stackSize));
                fluidAmount -= stackSize;
            }
            mOutputFluids = outputFluids.toArray(new FluidStack[0]);
        }

        // 计算每tick消耗的EU
        int euConsumption = (int) Math.min(outputAmount * 4, Integer.MAX_VALUE);

        // 存储每tick消耗的EU，供onPostTick使用
        this.mEUt = -euConsumption;

        // 设置进度时间
        this.mEfficiency = 10000;
        this.mProgresstime = 0;
        this.mMaxProgresstime = 200;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);

        if (aBaseMetaTileEntity.isServerSide()) {
            if (this.mProgresstime > 0) {
                if (!consumeEnergy(-this.mEUt)) {
                    stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                }
            }
        }
    }

    private boolean consumeEnergy(int amount) {
        for (MTEHatchEnergy energyHatch : mEnergyHatches) {
            if (energyHatch.getEUVar() >= amount) {
                energyHatch.setEUVar(energyHatch.getEUVar() - amount);
                return true;
            }
        }
        for (MTEHatch exoEnergyHatch : mExoticEnergyHatches) {
            if (exoEnergyHatch.getEUVar() >= amount) {
                exoEnergyHatch.setEUVar(exoEnergyHatch.getEUVar() - amount);
                return true;
            }
        }
        return false;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setEUtDiscount(1)
                    .setDurationModifier(1);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
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
