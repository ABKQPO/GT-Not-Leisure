package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import com.github.bsideup.jabel.Desugar;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
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
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseTileEntity;
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
import gregtech.api.util.ParallelHelper;
import gregtech.api.util.VoidProtectionHelper;
import gregtech.common.misc.WirelessNetworkManager;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.casing.BlockGTCasingsTT;

public class GrandAssemblyLine extends GTMMultiMachineBase<GrandAssemblyLine> implements ISurvivalConstructable {

    public static Object2IntMap<GTUtility.ItemId> specialRecipe = new Object2IntOpenHashMap<>();

    public static int PARALLEL_WINDOW_ID = 10;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String GAL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/grand_assembly_line";
    private static final String[][] shape = StructureUtils.readStructureFromFile(GAL_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 46;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    public List<MTEHatchDataAccess> mDataAccessHatches = new ObjectArrayList<>();
    public String costingEUText = Utils.ZERO_STRING;
    public UUID ownerUUID;
    public boolean wirelessMode = false;
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
    public MultiblockTooltipBuilder createTooltip() {
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
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addTecTechHatchInfo()
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
                    .addIcon(BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblylineVisualRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.assemblylineVisualRecipes, GTNLRecipeMaps.GrandAssemblyLineSpecialRecipes);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        resetParallelTier();
        int maxParallel = getTrueParallel();
        long energyEU;

        if (wirelessMode) {
            energyEU = Utils.toLongSafe(WirelessNetworkManager.getUserEU(ownerUUID));
        } else {
            energyEU = getMaxInputEu();
        }

        if (energyEU <= 0) return CheckRecipeResultRegistry.NO_RECIPE;

        ObjectList<IDualInputInventory> inputInventories = new ObjectArrayList<>();

        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            Iterator<? extends IDualInputInventory> inventoryIterator = dualInputHatch.inventories();
            while (inventoryIterator.hasNext()) {
                IDualInputInventory inventory = inventoryIterator.next();
                if (inventory.isEmpty()) continue;
                IDualInputInventory wrappedInventory = new WrappedInventory(
                    ArrayUtils.addAll(sharedItems, inventory.getItemInputs()),
                    inventory.getFluidInputs());
                inputInventories.add(wrappedInventory);
            }
        }

        // 将常规输入仓/总线包装成总成
        short hatchColors = getHatchColors();
        boolean doColorChecking = hatchColors != 0;
        if (!doColorChecking) hatchColors = 0b1;

        for (byte color = 0; color < (doColorChecking ? 16 : 1); color++) {
            if (isColorAbsent(hatchColors, color)) continue;

            List<ItemStack> inputItems = new ArrayList<>(getStoredInputsForColor(Optional.of(color)));
            List<FluidStack> inputFluids = new ArrayList<>(getStoredFluidsForColor(Optional.of(color)));

            if (getControllerSlot() != null) {
                inputItems.add(getControllerSlot());
            }

            IDualInputInventory wrappedInventory = new WrappedInventory(inputItems, inputFluids);
            if (!wrappedInventory.isEmpty()) inputInventories.add(wrappedInventory);
        }

        return processRecipeLogic(inputInventories, energyEU, maxParallel, minRecipeTime);
    }

    public CheckRecipeResult processRecipeLogic(List<IDualInputInventory> inputInventories, long energyEU,
        int maxParallel, int minDuration) {
        CheckRecipeResult result = null;
        ObjectList<GTRecipe.RecipeAssemblyLine> validRecipes = new ObjectArrayList<>();

        if (AssemblyLineUtils.isItemDataStick(mInventory[1])) {
            validRecipes.addAll(AssemblyLineUtils.findALRecipeFromDataStick(mInventory[1]));
        }
        for (MTEHatchDataAccess dataAccess : GTUtility.validMTEList(mDataAccessHatches)) {
            validRecipes.addAll(dataAccess.getAssemblyLineRecipes());
        }
        if (validRecipes.isEmpty()) return CheckRecipeResultRegistry.NO_DATA_STICKS;

        validRecipes.removeIf(
            recipe -> recipe.mInputs == null || recipe.mFluidInputs == null
                || recipe.mOutput == null
                || (!wirelessMode && recipe.mEUt > energyEU));

        validRecipes.sort(Comparator.comparingInt(recipe -> recipe.mEUt));
        if (validRecipes.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

        BigInteger wirelessUserEU = wirelessMode ? WirelessNetworkManager.getUserEU(ownerUUID) : BigInteger.ZERO;
        List<RecipeTask> tasks = new ArrayList<>();
        int remainingGlobalParallel = maxParallel;
        long currentWiredInstantPower = 0; // 有线模式累计瞬时功率
        BigInteger currentWirelessTotalEnergy = BigInteger.ZERO; // 无线模式累计消耗总量

        // --- 第一阶段：优先填满并行 ---
        for (IDualInputInventory inventory : inputInventories) {
            if (remainingGlobalParallel <= 0) break;
            ItemStack[] invItems = inventory.getItemInputs();
            FluidStack[] invFluids = inventory.getFluidInputs();
            if (invItems == null || invItems.length == 0) continue;

            Object2LongOpenHashMap<GTUtility.ItemId> itemMap = getInventoryItemMap(invItems);
            Object2LongOpenHashMap<Fluid> fluidMap = getInventoryFluidMap(invFluids);

            for (GTRecipe.RecipeAssemblyLine oldRecipe : validRecipes) {
                GTRecipe.RecipeAssemblyLine recipe = copyRecipe(oldRecipe);
                int circuit = specialRecipe.getOrDefault(GTUtility.ItemId.create(recipe.mOutput), -1);
                if (circuit != -1) {
                    ItemStack[] inputsArray = recipe.mInputs;
                    ItemStack[] newInputsArray = new ItemStack[inputsArray.length + 1];
                    System.arraycopy(inputsArray, 0, newInputsArray, 0, inputsArray.length);
                    newInputsArray[inputsArray.length] = GTUtility.getIntegratedCircuit(circuit);
                    recipe.mInputs = newInputsArray;
                }

                int localMax = remainingGlobalParallel;

                double pFactor = calculateParallelByItemsUnordered(itemMap, localMax, recipe);
                if (pFactor < 1.0) {
                    result = GTNLParallelHelper.PARALLEL_ZERO;
                    continue;
                }
                pFactor = calculateParallelByFluidsUnordered(fluidMap, pFactor, recipe.mFluidInputs);
                if (pFactor < 1.0) {
                    result = GTNLParallelHelper.PARALLEL_ZERO;
                    continue;
                }

                int finalParallel = (int) pFactor;

                recipe.mDuration = Math.max(1, (int) (recipe.mDuration * getDurationModifier() * mConfigSpeedBoost));
                recipe.mEUt = Math.max(1, (int) (recipe.mEUt * getEUtDiscount()));

                if (wirelessMode) {
                    BigInteger needed = BigInteger.valueOf((long) recipe.mEUt * recipe.mDuration)
                        .multiply(BigInteger.valueOf(finalParallel));
                    if (currentWirelessTotalEnergy.add(needed)
                        .compareTo(wirelessUserEU) > 0) {
                        BigInteger perParallel = BigInteger.valueOf((long) recipe.mEUt * recipe.mDuration);
                        finalParallel = wirelessUserEU.subtract(currentWirelessTotalEnergy)
                            .divide(perParallel)
                            .intValue();
                    }
                } else {
                    long neededInstant = (long) recipe.mEUt * finalParallel;
                    if (neededInstant > energyEU - currentWiredInstantPower) {
                        finalParallel = (int) ((energyEU - currentWiredInstantPower) / recipe.mEUt);
                    }
                }

                if (finalParallel <= 0) {
                    result = GTNLParallelHelper.PARALLEL_ZERO;
                    continue;
                }

                // 溢出保护检查
                if (protectsExcessItem()) {
                    ArrayList<ItemStack> pred = new ArrayList<>();
                    ItemStack out = recipe.mOutput.copy();
                    ParallelHelper.addItemsLong(pred, out, (long) out.stackSize * finalParallel);
                    VoidProtectionHelper vph = new VoidProtectionHelper();
                    vph.setMachine(this)
                        .setItemOutputs(pred.toArray(new ItemStack[0]))
                        .setMaxParallel(finalParallel)
                        .build();
                    finalParallel = Math.min(vph.getMaxParallel(), finalParallel);
                    if (vph.isItemFull()) {
                        result = CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                        finalParallel = 0;
                    }
                }

                if (finalParallel <= 0) continue;

                consumeItemsUnordered(recipe, finalParallel, invItems, itemMap);
                consumeFluidsUnordered(recipe, finalParallel, invFluids);
                tasks.add(new RecipeTask(recipe, finalParallel));
                remainingGlobalParallel -= finalParallel;
                if (wirelessMode) {
                    currentWirelessTotalEnergy = currentWirelessTotalEnergy.add(
                        BigInteger.valueOf((long) recipe.mEUt * recipe.mDuration)
                            .multiply(BigInteger.valueOf(finalParallel)));
                } else {
                    currentWiredInstantPower += (long) recipe.mEUt * finalParallel;
                }
                break;
            }
        }

        if (tasks.isEmpty()) {
            if (result != null) return result;
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        // --- 第二阶段：尝试超频 ---
        int overclockFactor = (mParallelTier >= 11) ? 4 : 2;

        for (RecipeTask task : tasks) {
            BigInteger taskParallelBI = BigInteger.valueOf(task.parallel);
            if (wirelessMode) {
                // 无线模式：基于总量进行超频，尽量压到 minDuration
                while (true) {
                    long nextPower = task.adjustedPower * 4;
                    int nextTime = task.adjustedTime / overclockFactor;
                    if (nextTime < minDuration) break;

                    BigInteger currentTaskTotal = BigInteger.valueOf(task.adjustedPower * task.adjustedTime)
                        .multiply(taskParallelBI);
                    BigInteger nextTaskTotal = BigInteger.valueOf(nextPower * nextTime)
                        .multiply(taskParallelBI);

                    // 检查增加超频后总量是否依然足够
                    if (currentWirelessTotalEnergy.subtract(currentTaskTotal)
                        .add(nextTaskTotal)
                        .compareTo(wirelessUserEU) <= 0) {
                        currentWirelessTotalEnergy = currentWirelessTotalEnergy.subtract(currentTaskTotal)
                            .add(nextTaskTotal);
                        task.adjustedPower = nextPower;
                        task.adjustedTime = nextTime;
                    } else break;
                }
            } else {
                // 有线模式：基于瞬时功率进行超频
                int ocCount = 0;
                long availableInstant = energyEU - (currentWiredInstantPower - (task.adjustedPower * task.parallel));
                long ratio = availableInstant / Math.max(1, (task.adjustedPower * task.parallel));
                long thresh = 1;
                while (ratio >= thresh * 4) {
                    ocCount++;
                    thresh *= 4;
                }

                while (ocCount > 0 && task.adjustedPower * 4 <= Integer.MAX_VALUE
                    && task.adjustedTime / overclockFactor >= minDuration) {
                    long addedInstant = (task.adjustedPower * 3) * task.parallel;
                    if (currentWiredInstantPower + addedInstant <= energyEU) {
                        task.adjustedPower *= 4;
                        task.adjustedTime /= overclockFactor;
                        currentWiredInstantPower += addedInstant;
                        ocCount--;
                    } else break;
                }
            }

            // 批处理逻辑：在功率限制内延长至 128t
            if (task.adjustedTime < 128 && batchMode) {
                double tFactor = 128.0 / task.adjustedTime;
                if (wirelessMode) {
                    // 无线模式只需检查余额
                    BigInteger currentTotal = BigInteger.valueOf(task.adjustedPower * task.adjustedTime)
                        .multiply(taskParallelBI);
                    BigInteger extendedTotal = BigInteger.valueOf((long) (task.adjustedPower * tFactor * 128))
                        .multiply(taskParallelBI);
                    if (currentWirelessTotalEnergy.subtract(currentTotal)
                        .add(extendedTotal)
                        .compareTo(wirelessUserEU) <= 0) {
                        currentWirelessTotalEnergy = currentWirelessTotalEnergy.subtract(currentTotal)
                            .add(extendedTotal);
                        task.adjustedPower = (long) (task.adjustedPower * tFactor);
                        task.adjustedTime = 128;
                    }
                } else {
                    // 有线模式需检查瞬时功率是否超标
                    long maxEUt = energyEU / task.parallel;
                    if (task.adjustedPower * tFactor <= maxEUt) {
                        currentWiredInstantPower += (long) (task.adjustedPower * (tFactor - 1)) * task.parallel;
                        task.adjustedPower = (long) (task.adjustedPower * tFactor);
                        task.adjustedTime = 128;
                    }
                }
            }
            task.adjustedTime = Math.max(1, task.adjustedTime);
        }

        // --- 最终结算 ---
        ArrayList<ItemStack> totalOutputs = new ArrayList<>();

        long weightedDurationSum = 0;
        long totalWeight = 0;
        long totalEU_Long = 0;

        BigInteger totalEU_BI = BigInteger.ZERO;

        for (RecipeTask task : tasks) {
            ItemStack out = task.recipe.mOutput.copy();
            ParallelHelper.addItemsLong(totalOutputs, out, (long) out.stackSize * task.parallel);

            long weight = task.adjustedPower * task.parallel;
            weightedDurationSum += (long) task.adjustedTime * weight;
            totalWeight += weight;

            if (wirelessMode) {
                totalEU_BI = totalEU_BI.add(
                    BigInteger.valueOf(task.adjustedPower)
                        .multiply(BigInteger.valueOf(task.adjustedTime))
                        .multiply(BigInteger.valueOf(task.parallel)));
            } else {
                totalEU_Long += task.adjustedPower * (long) task.adjustedTime * task.parallel;
            }
        }

        int weightedTime = totalWeight > 0 ? (int) Math.max(1, weightedDurationSum / totalWeight) : 1;

        if (wirelessMode) {
            long requiredEUt = totalEU_BI.divide(BigInteger.valueOf(weightedTime))
                .longValue();

            long finalEUt = Math.max(1, Math.min(requiredEUt, energyEU));

            int finalDuration = totalEU_BI.divide(BigInteger.valueOf(finalEUt))
                .intValue();

            if (!WirelessNetworkManager.addEUToGlobalEnergyMap(ownerUUID, totalEU_BI.negate())) {
                return CheckRecipeResultRegistry.insufficientPower(totalEU_BI.longValue());
            }

            costingEUText = GTUtility.formatNumbers(totalEU_BI);

            this.lEUt = 0;
            this.mMaxProgresstime = Math.max(1, finalDuration);

        } else {
            long requiredEUt = totalEU_Long / Math.max(1, weightedTime);

            long finalEUt = Math.max(1, Math.min(requiredEUt, energyEU));

            int finalDuration = (int) Math.max(1, totalEU_Long / finalEUt);

            this.lEUt = -finalEUt;
            this.mMaxProgresstime = finalDuration;
        }

        mOutputItems = totalOutputs.toArray(new ItemStack[0]);
        updateSlots();

        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public static Object2LongOpenHashMap<GTUtility.ItemId> getInventoryItemMap(ItemStack[] inputs) {
        Object2LongOpenHashMap<GTUtility.ItemId> itemMap = new Object2LongOpenHashMap<>();
        if (inputs == null) return itemMap;
        for (ItemStack is : inputs) {
            if (is == null || is.stackSize < 0) continue;

            itemMap.merge(GTUtility.ItemId.createNoCopy(is), is.stackSize, Long::sum);
            itemMap.merge(GTUtility.ItemId.createAsWildcard(is), is.stackSize, Long::sum);
        }
        return itemMap;
    }

    public static Object2LongOpenHashMap<Fluid> getInventoryFluidMap(FluidStack[] inputs) {
        Object2LongOpenHashMap<Fluid> fluidMap = new Object2LongOpenHashMap<>();
        if (inputs == null) return fluidMap;
        for (FluidStack fs : inputs) {
            if (fs == null || fs.amount <= 0) continue;
            fluidMap.merge(fs.getFluid(), fs.amount, Long::sum);
        }
        return fluidMap;
    }

    public static double calculateParallelByItemsUnordered(Object2LongOpenHashMap<GTUtility.ItemId> availableMap,
        int maxParallel, GTRecipe.RecipeAssemblyLine recipe) {
        if (recipe.mInputs == null || recipe.mInputs.length == 0) return 0;

        double currentParallel = maxParallel;

        for (int i = 0; i < recipe.mInputs.length; i++) {
            ItemStack mainReq = recipe.mInputs[i];
            if (mainReq == null) continue;

            GTUtility.ItemId searchKey = (mainReq.getItemDamage() == GTRecipeBuilder.WILDCARD)
                ? GTUtility.ItemId.createAsWildcard(mainReq)
                : GTUtility.ItemId.createNoCopy(mainReq);

            long mainAvailable = availableMap.getOrDefault(searchKey, -1L);

            long maxParallelForThisSlot = 0;

            if (mainAvailable >= 0 && mainReq.stackSize <= 0) {
                maxParallelForThisSlot = Integer.MAX_VALUE;
            } else if (mainAvailable > 0 && mainReq.stackSize > 0) {
                maxParallelForThisSlot = mainAvailable / mainReq.stackSize;
            }

            if (maxParallelForThisSlot == 0 && recipe.mOreDictAlt != null
                && i < recipe.mOreDictAlt.length
                && recipe.mOreDictAlt[i] != null) {
                for (ItemStack alt : recipe.mOreDictAlt[i]) {
                    if (alt == null) continue;

                    GTUtility.ItemId altSearchKey = (alt.getItemDamage() == GTRecipeBuilder.WILDCARD)
                        ? GTUtility.ItemId.createAsWildcard(alt)
                        : GTUtility.ItemId.createNoCopy(alt);

                    long altAvailable = availableMap.getOrDefault(altSearchKey, -1L);

                    if (altAvailable > 0 && alt.stackSize <= 0) {
                        maxParallelForThisSlot = Integer.MAX_VALUE;
                    } else if (altAvailable > 0 && alt.stackSize > 0) {
                        maxParallelForThisSlot = altAvailable / alt.stackSize;
                    }
                    if (maxParallelForThisSlot > 0) break;
                }
            }

            if (maxParallelForThisSlot <= 0) return 0;
            currentParallel = Math.min(currentParallel, (double) maxParallelForThisSlot);
        }
        return currentParallel;
    }

    public static double calculateParallelByFluidsUnordered(Object2LongOpenHashMap<Fluid> availableMap,
        double currentParallel, FluidStack[] recipeFluids) {
        if (recipeFluids == null || recipeFluids.length == 0) return currentParallel;

        for (FluidStack req : recipeFluids) {
            if (req == null) continue;
            long available = availableMap.getOrDefault(req.getFluid(), 0L);
            if (available < req.amount) return 0;

            long maxParallelForThisSlot = 0;

            if (available > 0 && req.amount <= 0) {
                maxParallelForThisSlot = Integer.MAX_VALUE;
            } else if (req.amount > 0) {
                maxParallelForThisSlot = available / req.amount;
            }

            if (maxParallelForThisSlot <= 0) return 0;
            currentParallel = Math.min(currentParallel, maxParallelForThisSlot);
        }
        return currentParallel;
    }

    public void consumeItemsUnordered(GTRecipe.RecipeAssemblyLine recipe, int parallel, ItemStack[] invItems,
        Object2LongOpenHashMap<GTUtility.ItemId> availableMap) {
        if (recipe.mInputs == null) return;

        for (int i = 0; i < recipe.mInputs.length; i++) {
            ItemStack mainReq = recipe.mInputs[i];
            if (mainReq == null || mainReq.stackSize <= 0) continue;

            ItemStack chosenStack = mainReq;

            GTUtility.ItemId mainSearchKey = (mainReq.getItemDamage() == GTRecipeBuilder.WILDCARD)
                ? GTUtility.ItemId.createAsWildcard(mainReq)
                : GTUtility.ItemId.createNoCopy(mainReq);

            long mainAvailable = availableMap.getOrDefault(mainSearchKey, 0L);
            long maxPossible = mainAvailable / mainReq.stackSize;

            if (maxPossible == 0 && recipe.mOreDictAlt != null && recipe.mOreDictAlt[i] != null) {
                for (ItemStack alt : recipe.mOreDictAlt[i]) {
                    if (alt == null || alt.stackSize <= 0) continue;

                    GTUtility.ItemId altSearchKey = (alt.getItemDamage() == GTRecipeBuilder.WILDCARD)
                        ? GTUtility.ItemId.createAsWildcard(alt)
                        : GTUtility.ItemId.createNoCopy(alt);

                    long altAvailable = availableMap.getOrDefault(altSearchKey, 0L);
                    if (altAvailable >= (long) alt.stackSize) {
                        maxPossible = altAvailable / alt.stackSize;
                        chosenStack = alt;
                        break;
                    }
                }
            }

            if (maxPossible > 0) {
                long totalToConsume = (long) chosenStack.stackSize * parallel;
                depleteFromRequirement(chosenStack, totalToConsume, invItems);
            }
        }
    }

    public void depleteFromRequirement(ItemStack requirement, long amountToConsume, ItemStack[] invItems) {
        long remaining = amountToConsume;

        for (int i = 0; i < invItems.length && remaining > 0; i++) {
            ItemStack invStack = invItems[i];
            if (invStack == null || invStack.stackSize <= 0) continue;
            if (areStacksEqual(requirement, invStack)) {
                long toSubtract = Math.min(remaining, invStack.stackSize);
                invStack.stackSize -= (int) toSubtract;
                remaining -= toSubtract;
                if (invStack.stackSize <= 0) {
                    invItems[i] = null;
                }
            }
            if (remaining <= 0) break;
        }
    }

    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) return false;
        if (stack1.getItem() != stack2.getItem()) return false;

        int dmg1 = stack1.getItemDamage();
        int dmg2 = stack2.getItemDamage();

        if (dmg1 != dmg2 && dmg1 != GTRecipeBuilder.WILDCARD && dmg2 != GTRecipeBuilder.WILDCARD) {
            return false;
        }

        NBTTagCompound tag1 = stack1.getTagCompound();

        if (tag1 == null) return true;

        NBTTagCompound tag2 = stack2.getTagCompound();
        return tag1.equals(tag2);
    }

    public void consumeFluidsUnordered(GTRecipe.RecipeAssemblyLine recipe, int parallel, FluidStack[] invFluids) {
        if (recipe.mFluidInputs == null || invFluids == null) return;

        for (FluidStack recipeFluid : recipe.mFluidInputs) {
            if (recipeFluid == null) continue;
            long totalToConsume = (long) recipeFluid.amount * parallel;

            for (FluidStack slotFluid : invFluids) {
                if (slotFluid == null || totalToConsume <= 0) continue;

                if (GTUtility.areFluidsEqual(recipeFluid, slotFluid)) {
                    int canTake = (int) Math.min(slotFluid.amount, totalToConsume);
                    slotFluid.amount -= canTake;
                    totalToConsume -= canTake;
                }
                if (totalToConsume <= 0) break;
            }
        }
    }

    public static GTRecipe.RecipeAssemblyLine copyRecipe(GTRecipe.RecipeAssemblyLine original) {
        if (original == null) return null;

        ItemStack[] inputsCopy = new ItemStack[original.mInputs.length];
        for (int i = 0; i < original.mInputs.length; i++) {
            inputsCopy[i] = original.mInputs[i] != null ? original.mInputs[i].copy() : null;
        }

        FluidStack[] fluidInputsCopy = new FluidStack[original.mFluidInputs.length];
        for (int i = 0; i < original.mFluidInputs.length; i++) {
            fluidInputsCopy[i] = original.mFluidInputs[i] != null ? original.mFluidInputs[i].copy() : null;
        }

        ItemStack[][] oreDictAltCopy;

        if (original.mOreDictAlt == null) {
            oreDictAltCopy = null;
        } else {
            oreDictAltCopy = new ItemStack[original.mOreDictAlt.length][];

            for (int i = 0; i < original.mOreDictAlt.length; i++) {
                ItemStack[] altRow = original.mOreDictAlt[i];

                if (altRow == null || altRow.length == 0) {
                    oreDictAltCopy[i] = altRow == null ? null : new ItemStack[0];
                    continue;
                }

                ItemStack[] rowCopy = new ItemStack[altRow.length];
                for (int j = 0; j < altRow.length; j++) {
                    ItemStack stack = altRow[j];
                    rowCopy[j] = stack != null ? stack.copy() : null;
                }

                oreDictAltCopy[i] = rowCopy;
            }
        }

        GTRecipe.RecipeAssemblyLine copy = new GTRecipe.RecipeAssemblyLine(
            original.mResearchItem != null ? original.mResearchItem.copy() : null,
            original.mResearchTime,
            original.mResearchVoltage,
            inputsCopy,
            fluidInputsCopy,
            original.mOutput != null ? original.mOutput.copy() : null,
            original.mDuration,
            original.mEUt,
            oreDictAltCopy);

        copy.setPersistentHash(original.getPersistentHash());

        return copy;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        for (MTEHatchDataAccess hatch_dataAccess : GTUtility.validMTEList(mDataAccessHatches)) {
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
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(sBlockCasings2, 5))
            .addElement(
                'B',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(HatchElement.InputBus)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 3))))
            .addElement('C', StructureUtility.ofBlock(sBlockCasingsTT, 2))
            .addElement(
                'D',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(HatchElement.OutputBus)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 3))))
            .addElement(
                'E',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon,
                        DataHatchElement.DataAccess)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility
                                .ofBlockAnyMeta(GameRegistry.findBlock(Mods.IndustrialCraft2.ID, "blockAlloyGlass")))))
            .addElement(
                'F',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon,
                        DataHatchElement.DataAccess)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 3))))
            .addElement('G', StructureUtility.ofBlock(sBlockCasings2, 9))
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
            mEnergyHatchTier = 14;
            return mMaintenanceHatches.size() <= 1;
        }

        return !(mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty()) && mMaintenanceHatches.size() <= 1;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mDataAccessHatches.clear();
        wirelessMode = false;
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
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0) * ((mParallelTier >= 12) ? 0.2 : 1);
    }

    @Override
    public double getDurationModifier() {
        return (1 / 1.67 - (Math.max(0, mParallelTier - 1) / 50.0)) * ((mParallelTier >= 12) ? 1.0 / 20.0 : 1);
    }

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
                List<UITexture> ret = new ObjectArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                ret.add(GTUITextures.OVERLAY_BUTTON_BATCH_MODE_ON);
                return ret.toArray(new IDrawable[0]);
            })
            .addTooltip(StatCollector.translateToLocal("Info_GrandAssemblyLine_00"))
            .setTooltipShowUpDelay(BaseTileEntity.TOOLTIP_DELAY)
            .setPos(174, 112)
            .setSize(16, 16));
        super.addUIWidgets(builder, buildContext);
    }

    public ModularWindow createParallelWindow(final EntityPlayer player) {
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
        List<String> ret = new ObjectArrayList<>(Arrays.asList(super.getInfoData()));
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

    @Desugar
    public record WrappedInventory(ItemStack[] itemInputs, FluidStack[] fluidInputs) implements IDualInputInventory {

        public WrappedInventory(List<ItemStack> itemInputs, List<FluidStack> fluidInputs) {
            this(itemInputs.toArray(new ItemStack[0]), fluidInputs.toArray(new FluidStack[0]));
        }

        @Override
        public boolean isEmpty() {
            return itemInputs == null || fluidInputs == null || itemInputs.length == 0 || fluidInputs.length == 0;
        }

        @Override
        public ItemStack[] getItemInputs() {
            return itemInputs;
        }

        @Override
        public FluidStack[] getFluidInputs() {
            return fluidInputs;
        }
    }

    public static class RecipeTask {

        public GTRecipe.RecipeAssemblyLine recipe;
        public int parallel;
        public int adjustedTime;
        public long adjustedPower;

        RecipeTask(GTRecipe.RecipeAssemblyLine r, int p) {
            this.recipe = r;
            this.parallel = p;
            this.adjustedTime = r.mDuration;
            this.adjustedPower = r.mEUt;
        }
    }

}
