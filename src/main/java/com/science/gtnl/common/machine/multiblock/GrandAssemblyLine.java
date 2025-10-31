package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static com.science.gtnl.utils.Utils.NEGATIVE_ONE;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.validMTEList;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

import com.github.bsideup.jabel.Desugar;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.casing.BlockGTCasingsTT;

public class GrandAssemblyLine extends GTMMultiMachineBase<GrandAssemblyLine> implements ISurvivalConstructable {

    public static final Map<Integer, List<GTRecipe.RecipeAssemblyLine>> recipeCache = new HashMap<>();
    public final String ZERO_STRING = "0";
    public String costingEUText = ZERO_STRING;
    public UUID ownerUUID;
    public boolean wirelessMode = false;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String GAL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/grand_assembly_line";
    public static final String[][] shape = StructureUtils.readStructureFromFile(GAL_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 46;
    public final int VERTICAL_OFF_SET = 2;
    public final int DEPTH_OFF_SET = 0;
    public final ArrayList<MTEHatchDataAccess> mDataAccessHatches = new ArrayList<>();
    public boolean isDualInputHatch = false;
    public boolean useSingleAmp = true;
    public int minRecipeTime = 20;

    public GrandAssemblyLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GrandAssemblyLine(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GrandAssemblyLine(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("GrandAssemblyLineRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_11"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_12"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .beginStructureBlock(48, 5, 5, true)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_TECTECH_MULTIBLOCK)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblylineVisualRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.assemblylineVisualRecipes, RecipePool.GrandAssemblyLineRecipes);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        // 第一步：初始化参数
        ItemStack controllerItem = getControllerSlot();
        this.mParallelTier = getParallelTier(controllerItem);
        long energyEU = wirelessMode ? Integer.MAX_VALUE
            : GTValues.VP[mEnergyHatchTier] * (useSingleAmp ? 1 : getMaxInputAmps() / 2); // 能源仓最大输入功率
        int maxParallel = getTrueParallel(); // 最大并行数

        if (energyEU <= 0) return CheckRecipeResultRegistry.POWER_OVERFLOW;

        // 构建输入仓列表
        ArrayList<IDualInputInventory> inputInventories = new ArrayList<>();

        // 如果 isDualInputHatch 为 true，遍历每个槽位
        if (isDualInputHatch) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                Iterator<? extends IDualInputInventory> inventoryIterator = dualInputHatch.inventories();
                while (inventoryIterator.hasNext()) {
                    IDualInputInventory inventory = inventoryIterator.next();
                    inputInventories.add(inventory); // 将每个槽位加入列表
                }
            }
        } else {
            // 非 DualInputHatch 模式，将常规输入仓/总线包装成 IDualInputInventory
            IDualInputInventory wrappedInventory = new WrappedInventory(getAllStoredInputs(), getStoredFluids());
            inputInventories.add(wrappedInventory);
        }

        // 执行配方处理逻辑
        return processRecipeLogic(inputInventories, energyEU, maxParallel, minRecipeTime);
    }

    public CheckRecipeResult processRecipeLogic(ArrayList<IDualInputInventory> inputInventories, long energyEU,
        int maxParallel, int limit) {
        long totalNeedEUt = 0; // 累加的总功率
        int totalMaxProgressTime = 0; // 累加的最大时间
        int powerParallel = 0;
        int circuitOC = -1; // 电路板限制超频次数
        int perfectOCTime = (mParallelTier >= 11) ? 4 : 2;
        costingEUText = ZERO_STRING;
        BigInteger costingEU = BigInteger.ZERO;
        ArrayList<ItemStack> totalOutputs = new ArrayList<>(); // 累加的输出物品

        for (ItemStack item : getAllStoredInputs()) {
            if (item.getItem() == ItemList.Circuit_Integrated.getItem()) {
                circuitOC = item.getItemDamage();
                break;
            }
        }

        List<IDualInputInventory> validInventories = inputInventories.stream()
            .filter(Objects::nonNull)
            .filter(inv -> inv.getItemInputs() != null && inv.getItemInputs().length > 0)
            .collect(Collectors.toList());

        if (validInventories.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        List<GTRecipe.RecipeAssemblyLine> validRecipes = new ArrayList<>();
        findRecipe(validRecipes, energyEU);
        if (validRecipes.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;
        validRecipes.sort(Comparator.comparingInt(recipe -> recipe.mEUt));

        List<GTRecipe> overclockedRecipes = new ArrayList<>();

        double maxBatchFactor = 1.0;
        for (GTRecipe.RecipeAssemblyLine recipe : validRecipes) {
            ItemStack[] inputItems;
            FluidStack[] inputFluids;
            ItemStack outputItem;

            try {
                inputItems = Arrays
                    .stream(
                        Objects.requireNonNull(recipe.mInputs, "Inputs is null: " + Arrays.toString(recipe.mInputs)))
                    .map(Objects::requireNonNull)
                    .map(ItemStack::copy)
                    .toArray(ItemStack[]::new);

                inputFluids = Arrays
                    .stream(
                        Objects.requireNonNull(
                            recipe.mFluidInputs,
                            "FluidInputs is null: " + Arrays.toString(recipe.mFluidInputs)))
                    .map(Objects::requireNonNull)
                    .map(FluidStack::copy)
                    .toArray(FluidStack[]::new);

                outputItem = Objects.requireNonNull(recipe.mOutput, "Output is null: " + recipe.mOutput)
                    .copy();
            } catch (Throwable t) {
                System.err.println("[GTNL] Failed to copy recipe: " + recipe);
                t.printStackTrace();
                continue;
            }

            int overclockCount = 0;
            long energyRatio = energyEU / Math.max(1, recipe.mEUt);
            long threshold = 1;
            int adjustedTime;
            int adjustedPower;
            BigInteger adjustedPowerBigInt;

            if (wirelessMode) {
                adjustedTime = minRecipeTime;
                adjustedPowerBigInt = BigInteger.valueOf(recipe.mEUt)
                    .multiply(BigInteger.valueOf(recipe.mDuration))
                    .divide(BigInteger.valueOf(adjustedTime));
                while (adjustedPowerBigInt.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                    adjustedPowerBigInt = adjustedPowerBigInt.divide(BigInteger.valueOf(4));
                    adjustedTime *= 4;
                }
                adjustedPower = adjustedPowerBigInt.min(BigInteger.valueOf(Integer.MAX_VALUE))
                    .intValue();
            } else {
                while (energyRatio >= threshold * 4) {
                    overclockCount++;
                    threshold *= 4;
                }
                if (circuitOC >= 0) {
                    overclockCount = Math.min(overclockCount, circuitOC);
                }
                adjustedPower = recipe.mEUt;
                adjustedTime = recipe.mDuration;
                while ((adjustedPower * 4L > Integer.MAX_VALUE || adjustedTime / perfectOCTime > 0)
                    && overclockCount > 0) {
                    overclockCount--;
                    adjustedPower *= 4;
                    adjustedTime /= perfectOCTime;
                }
            }

            adjustedTime = Math.max(1, adjustedTime);
            adjustedPower = Math.max(1, adjustedPower);

            long totalEnergy = (long) adjustedPower * adjustedTime;
            double d = (double) totalEnergy / energyEU;

            if (d >= limit) {
                if (energyEU > Integer.MAX_VALUE) {
                    adjustedPower = Integer.MAX_VALUE;
                    d = (double) totalEnergy / Integer.MAX_VALUE;
                } else {
                    adjustedPower = (int) energyEU;
                }
                adjustedTime = (int) Math.max(limit, d);
            } else {
                adjustedPower = (int) ((energyEU * d) / limit);
                adjustedTime = limit;
            }

            adjustedTime = Math.max(1, adjustedTime);
            adjustedPower = Math.max(1, adjustedPower);

            double batchFactor = 1.0;
            if (adjustedTime < 128 && batchMode) {
                double timeFactor = 128.0 / adjustedTime;
                double energyFactor = (double) energyEU / adjustedPower;
                double newPower = adjustedPower * timeFactor;
                if (newPower > energyEU) {
                    batchFactor = energyFactor;
                    adjustedPower = (int) Math.min(Integer.MAX_VALUE, adjustedPower * batchFactor);
                    adjustedTime = (int) Math.max(1, adjustedTime * batchFactor);
                } else {
                    batchFactor = timeFactor;
                    adjustedPower = (int) Math.min(Integer.MAX_VALUE, newPower);
                    adjustedTime = 128;
                }
            }

            maxBatchFactor = Math.max(maxBatchFactor, batchFactor);

            GTRecipe overclockedRecipe = new GTRecipe(
                true,
                inputItems,
                new ItemStack[] { outputItem },
                null,
                Arrays.stream(new int[inputItems.length])
                    .map(i -> 10000)
                    .toArray(),
                inputFluids,
                null,
                adjustedTime,
                adjustedPower,
                0);

            overclockedRecipes.add(overclockedRecipe);
            powerParallel = (int) Math.min(Integer.MAX_VALUE, energyEU / recipe.mEUt);
        }

        // 遍历每个输入仓
        for (IDualInputInventory inventory : inputInventories) {
            // 获取当前输入仓的物品和流体
            ItemStack[] itemInputs = inventory.getItemInputs();
            FluidStack[] fluidInputs = inventory.getFluidInputs();

            // 如果当前输入仓没有物品或流体，跳过
            if (itemInputs == null || itemInputs.length == 0) {
                continue;
            }

            // 将当前输入仓的物品和流体转换为列表
            ArrayList<ItemStack> allInputs = new ArrayList<>(Arrays.asList(itemInputs));
            ArrayList<FluidStack> allFluids = new ArrayList<>(Arrays.asList(fluidInputs));

            maxParallel = (int) (maxParallel * maxBatchFactor);

            // 第四步：处理配方并行逻辑
            Map<GTRecipe, Integer> recipeParallelMap = new HashMap<>();
            int remainingMaxParallel = maxParallel; // 剩余的最大并行数
            boolean hasValidRecipe = false;

            // 初始化模拟消耗的上下文
            Map<GTUtility.ItemId, Integer> itemAllocated = new HashMap<>();
            Map<Fluid, Integer> fluidAllocated = new HashMap<>();

            for (GTRecipe recipe : overclockedRecipes) {
                // 提取所需的物品和流体
                ItemStack[] requiredItems = recipe.mInputs;
                FluidStack[] requiredFluids = recipe.mFluidInputs;

                // 计算物品并行数 (ItemParallel)，考虑已分配的数量和矿辞匹配
                long itemParallel = Long.MAX_VALUE;
                for (ItemStack input : requiredItems) {
                    int required = input.stackSize;
                    if (required <= 0) continue;

                    long available;

                    // 如果没有矿辞，直接检查原始物品
                    GTUtility.ItemId itemId = GTUtility.ItemId.createNoCopy(input);
                    long availableOriginal = depleteInputLong(input, 1, allInputs, true);
                    long allocated = itemAllocated.getOrDefault(itemId, 0);
                    available = Math.max(0, availableOriginal - allocated);

                    // 计算当前物品的并行数
                    long parallelForItem = available / required;
                    itemParallel = Math.min(itemParallel, parallelForItem);
                }

                // 检查 itemParallel 是否超过 int 最大值
                if (itemParallel > Integer.MAX_VALUE) {
                    itemParallel = Integer.MAX_VALUE;
                }

                // 计算流体并行数 (FluidParallel)，考虑已分配的数量
                long fluidParallel = Long.MAX_VALUE;
                for (FluidStack fluid : requiredFluids) {
                    Fluid fluidType = fluid.getFluid();
                    long availableOriginal = depleteInputLong(fluid, 1, allFluids, true);
                    long allocated = fluidAllocated.getOrDefault(fluidType, 0);
                    long available = Math.max(0, availableOriginal - allocated);
                    int required = fluid.amount;
                    if (required <= 0) continue;
                    long parallelForFluid = available / required;
                    fluidParallel = Math.min(fluidParallel, parallelForFluid);
                }

                // 检查 fluidParallel 是否超过 int 最大值
                if (fluidParallel > Integer.MAX_VALUE) {
                    fluidParallel = Integer.MAX_VALUE;
                }

                // 最终结果转换为 int
                int finalItemParallel = (int) itemParallel;
                int finalFluidParallel = (int) fluidParallel;

                // 取较小的并行数作为当前配方的并行数 (RecipeParallel)
                int recipeParallel = Math.min(finalItemParallel, finalFluidParallel);

                // 使用功率并行数限制 RecipeParallel
                if (!wirelessMode) recipeParallel = Math.min(recipeParallel, powerParallel);

                // 检查剩余的最大并行数
                if (recipeParallel > remainingMaxParallel) {
                    recipeParallel = remainingMaxParallel; // 如果超出剩余并行数，则设置为剩余并行数
                }

                if (recipeParallel <= 0) {
                    continue; // 跳过并行数为0的情况
                }

                // 更新模拟消耗的上下文
                for (ItemStack input : requiredItems) {
                    int required = input.stackSize * recipeParallel;
                    // 如果没有矿辞，直接消耗原始物品
                    GTUtility.ItemId itemId = GTUtility.ItemId.createNoCopy(input);
                    int consumed = (int) Math
                        .min(required, getAvailableItemCount(input, allInputs) - itemAllocated.getOrDefault(itemId, 0));
                    if (consumed > 0) {
                        itemAllocated.put(itemId, itemAllocated.getOrDefault(itemId, 0) + consumed);
                    }
                }

                for (FluidStack fluid : requiredFluids) {
                    Fluid fluidType = fluid.getFluid();
                    int consumed = fluid.amount * recipeParallel;
                    fluidAllocated.put(fluidType, fluidAllocated.getOrDefault(fluidType, 0) + consumed);
                }

                // 更新剩余的最大并行数
                remainingMaxParallel -= recipeParallel;

                // 将当前配方的并行数添加到结果中
                recipeParallelMap.put(recipe, recipeParallel);
                hasValidRecipe = true;

                if (remainingMaxParallel <= 0) {
                    break; // 如果剩余并行数为 0，跳出循环
                }
            }

            // 如果没有有效的配方，则跳过当前输入仓
            if (!hasValidRecipe) {
                continue;
            }

            // 第五步：计算总耗电和时间
            long needEU = 0;
            int needTime = 0;

            for (Map.Entry<GTRecipe, Integer> entry : recipeParallelMap.entrySet()) {
                GTRecipe recipe = entry.getKey();
                long parallel = entry.getValue();
                if (wirelessMode) {
                    costingEU = BigInteger.valueOf(recipe.mEUt)
                        .multiply(BigInteger.valueOf(recipe.mDuration))
                        .multiply(BigInteger.valueOf(parallel));

                } else {
                    needEU += (long) recipe.mEUt * recipe.mDuration * parallel;
                }
                needTime += recipe.mDuration;
            }

            if (wirelessMode && !addEUToGlobalEnergyMap(ownerUUID, costingEU.multiply(NEGATIVE_ONE))) {
                return CheckRecipeResultRegistry.insufficientPower(costingEU.longValue());
            }

            if (!wirelessMode && !batchMode) {
                long needEUt = needEU / needTime;

                while (needEU / needTime > energyEU) {
                    if ((long) needTime * 2 > Integer.MAX_VALUE) {
                        break;
                    }
                    needEU /= 2;
                    needTime *= 2;
                }

                while (needTime / perfectOCTime > 0 && needEUt < Long.MAX_VALUE / 4
                    && (needEU / needTime) * 8 < energyEU) {
                    needEUt *= 4;
                    needTime /= perfectOCTime;
                }

                totalNeedEUt = needEUt;
                totalMaxProgressTime = needTime;
            }

            if (totalNeedEUt > energyEU * 0.95) {
                double scale = (energyEU * 0.95) / totalNeedEUt;
                totalNeedEUt = (long) Math.floor(totalNeedEUt * scale);
                totalMaxProgressTime = (int) Math.ceil(totalMaxProgressTime / scale);
            }

            for (Map.Entry<GTRecipe, Integer> entry : recipeParallelMap.entrySet()) {
                GTRecipe recipe = entry.getKey();
                int parallel = entry.getValue();

                ItemStack output = recipe.mOutputs[0].copy();
                output.stackSize *= parallel;

                if (output.stackSize > 0) {
                    totalOutputs.add(output);
                }
            }

            if (totalOutputs.isEmpty()) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }

            if (!canOutputAll(totalOutputs.toArray(new ItemStack[0]))) {
                return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
            }

            for (Map.Entry<GTRecipe, Integer> entry : recipeParallelMap.entrySet()) {
                GTRecipe recipe = entry.getKey();
                int parallel = entry.getValue();

                for (ItemStack input : recipe.mInputs) {
                    depleteInputLong(input, (long) input.stackSize * parallel, allInputs, false);
                }

                for (FluidStack fluid : recipe.mFluidInputs) {
                    depleteInputLong(fluid, (long) fluid.amount * parallel, allFluids, false);
                }
            }
        }

        if (totalOutputs.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        mOutputItems = totalOutputs.toArray(new ItemStack[0]);

        updateSlots();

        if (wirelessMode) {
            costingEUText = GTUtility.formatNumbers(costingEU);
            this.lEUt = 0;
            this.mMaxProgresstime = batchMode ? 128 : minRecipeTime;
        } else {
            this.lEUt = -totalNeedEUt;
            this.mMaxProgresstime = totalMaxProgressTime;
        }

        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;

        if (MainConfig.enableDebugMode) {
            ScienceNotLeisure.LOG.info("Recipe successful");
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public void findRecipe(List<GTRecipe.RecipeAssemblyLine> validRecipes, long energyEU) {
        ArrayList<GTRecipe.RecipeAssemblyLine> availableRecipes = new ArrayList<>();
        if (AssemblyLineUtils.isItemDataStick(mInventory[1])) {
            availableRecipes.addAll(AssemblyLineUtils.findALRecipeFromDataStick(mInventory[1]));
        }
        for (MTEHatchDataAccess dataAccess : validMTEList(mDataAccessHatches)) {
            availableRecipes.addAll(dataAccess.getAssemblyLineRecipes());
        }

        for (GTRecipe.RecipeAssemblyLine recipe : availableRecipes) {
            if (recipe == null || recipe.mInputs == null || recipe.mEUt > energyEU) continue;

            int cacheKey = recipe.getPersistentHash();

            if (recipeCache.containsKey(cacheKey)) {
                validRecipes.addAll(recipeCache.get(cacheKey));
                continue;
            }

            List<GTRecipe.RecipeAssemblyLine> recipeList = new ArrayList<>();
            recipeList.add(recipe);
            validRecipes.add(recipe);

            recipeCache.put(cacheKey, recipeList);

            ItemStack[] tInputs = recipe.mInputs;
            ItemStack[][] tOreDictAlt = recipe.mOreDictAlt;

            boolean hasValidAlt = false;
            if (tOreDictAlt != null) {
                for (ItemStack[] altArray : tOreDictAlt) {
                    if (altArray != null && altArray.length > 1) {
                        hasValidAlt = true;
                        break;
                    }
                }
            }

            if (hasValidAlt) {
                Map<String, Set<Integer>> groupedSlots = new HashMap<>();
                for (int i = 0; i < tOreDictAlt.length; i++) {
                    ItemStack[] alts = tOreDictAlt[i];
                    if (alts == null || alts.length <= 1) continue;

                    String key = Arrays.stream(alts)
                        .filter(Objects::nonNull)
                        .map(stack -> stack.getUnlocalizedName() + "@" + stack.getItemDamage())
                        .sorted()
                        .collect(Collectors.joining("|"));

                    groupedSlots.computeIfAbsent(key, k -> new HashSet<>())
                        .add(i);
                }

                List<ItemStack[]> combinations = new ArrayList<>();
                combinations.add(Arrays.copyOf(tInputs, tInputs.length));

                for (Map.Entry<String, Set<Integer>> entry : groupedSlots.entrySet()) {
                    Set<Integer> slotGroup = entry.getValue();
                    int referenceSlot = slotGroup.iterator()
                        .next();
                    ItemStack[] alternatives = tOreDictAlt[referenceSlot];

                    if (alternatives == null || alternatives.length == 0) continue;

                    List<ItemStack[]> newCombinations = new ArrayList<>();
                    for (ItemStack altItem : alternatives) {
                        for (ItemStack[] prevCombo : combinations) {
                            ItemStack[] newCombo = Arrays.copyOf(prevCombo, prevCombo.length);
                            for (int slot : slotGroup) {
                                newCombo[slot] = altItem.copy();
                            }
                            newCombinations.add(newCombo);
                        }
                    }
                    combinations = newCombinations;
                }

                for (ItemStack[] inputs : combinations) {
                    GTRecipe.RecipeAssemblyLine altRecipe = new GTRecipe.RecipeAssemblyLine(
                        recipe.mResearchItem,
                        recipe.mResearchTime,
                        recipe.mResearchVoltage,
                        inputs,
                        recipe.mFluidInputs,
                        recipe.mOutput,
                        recipe.mDuration,
                        recipe.mEUt,
                        tOreDictAlt);
                    validRecipes.add(altRecipe);
                    recipeCache.computeIfAbsent(cacheKey, k -> new ArrayList<>())
                        .add(altRecipe);
                }
            }
        }
    }

    // 包装常规输入仓/总线的实现
    @Desugar
    public record WrappedInventory(ArrayList<ItemStack> itemInputs, ArrayList<FluidStack> fluidInputs)
        implements IDualInputInventory {

        @Override
        public boolean isEmpty() {
            return itemInputs == null || fluidInputs == null;
        }

        @Override
        public ItemStack[] getItemInputs() {
            return itemInputs.toArray(new ItemStack[0]);
        }

        @Override
        public FluidStack[] getFluidInputs() {
            return fluidInputs.toArray(new FluidStack[0]);
        }
    }

    // 自定义方法：获取可用物品数量
    public long getAvailableItemCount(ItemStack required, ArrayList<ItemStack> allInputs) {
        long count = 0;

        // 优先检查完全匹配的物品
        for (ItemStack input : allInputs) {
            if (input != null) {
                if ((required.getItemDamage() == GTRecipeBuilder.WILDCARD && input.getItem() == required.getItem())
                    || (input.isItemEqual(required) && ItemStack.areItemStackTagsEqual(input, required))) {
                    count += input.stackSize;
                }
            }
        }

        // 如果没有完全匹配的物品，尝试检查矿辞匹配的物品
        if (count == 0) {
            // 获取矿辞名称
            int[] oreIDs = OreDictionary.getOreIDs(required);
            // 遍历所有矿辞匹配的物品
            for (int oreID : oreIDs) {
                String oreName = OreDictionary.getOreName(oreID);
                List<ItemStack> oreDictItems = OreDictionary.getOres(oreName);

                // 遍历输入槽位中的所有物品
                for (ItemStack input : allInputs) {
                    if (input != null) {
                        // 检查输入物品是否与矿辞匹配
                        for (ItemStack oreDictItem : oreDictItems) {
                            if (OreDictionary.itemMatches(oreDictItem, input, false)) {
                                count += input.stackSize;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return count;
    }

    public long depleteInputLong(ItemStack required, long amount, ArrayList<ItemStack> allInputs, boolean simulate) {
        if (required == null || amount <= 0 || allInputs == null) return 0;

        long matchedCount = 0;

        for (ItemStack input : allInputs) {
            if (input != null) {
                boolean match = (required.getItemDamage() == GTRecipeBuilder.WILDCARD
                    && input.getItem() == required.getItem())
                    || (input.isItemEqual(required) && ItemStack.areItemStackTagsEqual(input, required));

                if (match) {
                    matchedCount += input.stackSize;

                    if (!simulate) {
                        int toConsume = (int) Math.min(input.stackSize, Math.min(amount, Integer.MAX_VALUE));
                        input.stackSize -= toConsume;
                        if (input.stackSize < 0) input.stackSize = 0;
                        amount -= toConsume;
                        if (amount <= 0) return matchedCount;
                    }
                }
            }
        }

        int[] oreIDs = OreDictionary.getOreIDs(required);
        if (oreIDs.length > 0) {
            outer: for (int oreID : oreIDs) {
                String oreName = OreDictionary.getOreName(oreID);
                List<ItemStack> oreDictItems = OreDictionary.getOres(oreName);

                for (ItemStack oreDictItem : oreDictItems) {
                    for (ItemStack input : allInputs) {
                        if (input != null && OreDictionary.itemMatches(oreDictItem, input, false)) {
                            matchedCount += input.stackSize;

                            if (!simulate) {
                                int toConsume = (int) Math.min(input.stackSize, Math.min(amount, Integer.MAX_VALUE));
                                input.stackSize -= toConsume;
                                if (input.stackSize < 0) input.stackSize = 0;
                                amount -= toConsume;
                                if (amount <= 0) break outer;
                            }
                        }
                    }
                }
            }
        }

        return matchedCount;
    }

    public long depleteInputLong(FluidStack required, long amount, ArrayList<FluidStack> allFluids, boolean simulate) {
        long fluidAmount = 0;
        for (FluidStack fluid : allFluids) {
            if (fluid != null && fluid.isFluidEqual(required)) {
                fluidAmount += fluid.amount;
                if (!simulate) {
                    int available = fluid.amount;

                    int toConsumeNow = (int) Math.min(available, Math.min(amount, Integer.MAX_VALUE));

                    fluid.amount -= toConsumeNow;
                    amount -= toConsumeNow;

                    if (fluid.amount <= 0) {
                        fluid.amount = 0;
                    }

                    if (amount <= 0) {
                        return fluidAmount;
                    }
                }
            }
        }
        return fluidAmount;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        for (MTEHatchDataAccess hatch_dataAccess : mDataAccessHatches) {
            hatch_dataAccess.setActive(true);
        }
        return super.onRunningTick(aStack);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("wirelessMode", wirelessMode);
        aNBT.setInteger("parallelTier", mParallelTier);
        aNBT.setInteger("minRecipeTime", minRecipeTime);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        wirelessMode = aNBT.getBoolean("wirelessMode");
        mParallelTier = aNBT.getInteger("parallelTier");
        minRecipeTime = aNBT.getInteger("minRecipeTime");
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public IStructureDefinition<GrandAssemblyLine> getStructureDefinition() {
        return StructureDefinition.<GrandAssemblyLine>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings2, 5))
            .addElement(
                'B',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(InputBus)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
            .addElement('C', ofBlock(sBlockCasingsTT, 2))
            .addElement(
                'D',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(OutputBus)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
            .addElement(
                'E',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        InputHatch,
                        InputBus,
                        OutputBus,
                        Energy.or(ExoticEnergy),
                        ParallelCon,
                        DataHatchElement.DataAccess)
                    .buildAndChain(
                        onElementPass(
                            x -> ++x.mCountCasing,
                            ofBlockAnyMeta(GameRegistry.findBlock(IndustrialCraft2.ID, "blockAlloyGlass")))))
            .addElement(
                'F',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        InputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon,
                        DataHatchElement.DataAccess)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
            .addElement('G', ofBlock(sBlockCasings2, 9))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!this.checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
            || !checkHatch()) return false;
        return mCountCasing >= 590;
    }

    @Override
    public boolean checkHatch() {
        setupParameters();
        if (mParallelTier < 9 && !checkEnergyHatch()) return false;
        if (mParallelTier >= 12 && mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty()) {
            wirelessMode = true;
            useSingleAmp = false;
            mEnergyHatchTier = 14;
            return mDataAccessHatches.size() <= 1 && mMaintenanceHatches.size() <= 1;
        } else if (mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty()) return false;

        if (!mDualInputHatches.isEmpty()) {
            isDualInputHatch = true;
            if (!mInputBusses.isEmpty() || !mInputHatches.isEmpty()) return false;
        }
        return !(mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty()) && mDataAccessHatches.size() <= 1
            && mMaintenanceHatches.size() <= 1;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mDataAccessHatches.clear();
        isDualInputHatch = false;
        useSingleAmp = true;
        wirelessMode = false;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 4;
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
    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset + 3;
    }

    @Override
    public Set<VoidingMode> getAllowedVoidingModes() {
        return VoidingMode.ITEM_ONLY_MODES;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            protected GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return GTNL_OverclockCalculator.ofNoOverclock(recipe)
                    .setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setAmperage(wirelessMode ? Long.MAX_VALUE : useSingleAmp ? 1 : getMaxInputAmps() / 2)
                    .setEUt(wirelessMode ? Long.MAX_VALUE : getMachineVoltageLimit());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0) * ((mParallelTier >= 12) ? 0.2 : 1);
    }

    @Override
    public double getDurationModifier() {
        return (1 / 1.67 - (Math.max(0, mParallelTier - 1) / 50.0)) * ((mParallelTier >= 12) ? 1.0 / 20.0 : 1);
    }

    public static final int PARALLEL_WINDOW_ID = 10;

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        buildContext.addSyncedWindow(PARALLEL_WINDOW_ID, this::createParallelWindow);
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(PARALLEL_WINDOW_ID);
            }
        })
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                ret.add(GTUITextures.OVERLAY_BUTTON_BATCH_MODE_ON);
                return ret.toArray(new IDrawable[0]);
            })
            .addTooltip(translateToLocal("Info_GrandAssemblyLine_00"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(174, 112)
            .setSize(16, 16));
        super.addUIWidgets(builder, buildContext);
    }

    protected ModularWindow createParallelWindow(final EntityPlayer player) {
        final int WIDTH = 158;
        final int HEIGHT = 52;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.BottomRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)
                        .subtract(0, 10)));
        builder.widget(
            TextWidget.localised("Info_GrandAssemblyLine_00")
                .setPos(3, 4)
                .setSize(150, 20))
            .widget(
                new NumericWidget().setSetter(val -> minRecipeTime = (int) val)
                    .setGetter(() -> minRecipeTime)
                    .setBounds(1, Integer.MAX_VALUE)
                    .setDefaultValue(1)
                    .setScrollValues(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(150, 18)
                    .setPos(4, 25)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .attachSyncer(
                        new FakeSyncWidget.IntegerSyncer(() -> minRecipeTime, (val) -> minRecipeTime = val),
                        builder));
        return builder.build();
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("wirelessMode", wirelessMode);
            if (wirelessMode) tag.setString("costingEUText", costingEUText);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            currentTip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    @Override
    public String[] getInfoData() {
        List<String> ret = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (wirelessMode) {
            ret.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            ret.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + costingEUText
                    + EnumChatFormatting.RESET
                    + " EU");
        }
        return ret.toArray(new String[0]);
    }

    public boolean addDataAccessToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof MTEHatchDataAccess) {
            ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            return mDataAccessHatches.add((MTEHatchDataAccess) aMetaTileEntity);
        }
        return false;
    }

    public enum DataHatchElement implements IHatchElement<GrandAssemblyLine> {

        DataAccess;

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return Collections.singletonList(MTEHatchDataAccess.class);
        }

        @Override
        public IGTHatchAdder<GrandAssemblyLine> adder() {
            return GrandAssemblyLine::addDataAccessToMachineList;
        }

        @Override
        public long count(GrandAssemblyLine t) {
            return t.mDataAccessHatches.size();
        }
    }

}
