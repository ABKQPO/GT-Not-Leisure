package com.science.gtnl.common.machine.multiblock.steam;

import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.reavaritia.utils.item.ToolHelper;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.gui.modularui.multiblock.FurnaceArrayGui;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.common.tickrate.TickrateEvents;
import com.science.gtnl.utils.machine.MachineUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@IMetaTileEntity.SkipGenerateDescription
public class FurnaceArray extends SteamMultiMachineBase<FurnaceArray> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;
    public static int CASING_INDEX = GTNLCasings.CompressedFurnaceCasing.getTextureId();

    public long furnaceCount = 1;
    public long coalCount = 0;

    public int tick = 0;

    public long time;

    public static final ItemStack FURNACE_STACK = new ItemStack(Blocks.furnace);
    public static final ItemStack COAL_STACK = new ItemStack(Items.coal);

    public FurnaceArray(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FurnaceArray(String aName) {
        super(aName);
    }

    public long getFurnaceCountForGui() {
        return furnaceCount;
    }

    public void setFurnaceCountFromGui(long furnaceCount) {
        this.furnaceCount = furnaceCount;
    }

    public long getCoalCountForGui() {
        return coalCount;
    }

    public void setCoalCountFromGui(long coalCount) {
        this.coalCount = coalCount;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FurnaceArray(this.mName);
    }

    @Override
    public IStructureDefinition<FurnaceArray> getStructureDefinition() {
        return StructureDefinition.<FurnaceArray>builder()
            .addShape(
                STRUCTURE_PIECE_MAIN,
                StructureUtility.transpose(
                    new String[][] { { "AAA", "AAA", "AAA" }, { "A~A", "A A", "AAA" }, { "AAA", "AAA", "AAA" }, }))
            .addElement(
                'A',
                buildHatchAdder(FurnaceArray.class).casingIndex(CASING_INDEX)
                    .hint(1)
                    .atLeast(
                        SteamHatchElement.InputBus_Steam,
                        SteamHatchElement.OutputBus_Steam,
                        HatchElement.InputBus,
                        HatchElement.OutputBus)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, GTNLCasings.CompressedFurnaceCasing.asElement())))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        time = 0;
        List<ItemStack> tInput = getAllStoredInputs();

        for (ItemStack input : tInput) {
            if (GTUtility.areStacksEqual(input, FURNACE_STACK)) {
                furnaceCount += input.stackSize;
                depleteInput(input);
                continue;
            }
            if (GTUtility.areStacksEqual(input, COAL_STACK)) {
                coalCount += input.stackSize;
                depleteInput(input);
            }
        }

        if (tInput.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        long maxParallel = Math.min(furnaceCount, coalCount / 32);
        if (maxParallel <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        MinecraftServer server = MinecraftServer.getServer();
        double msptDiff = 25.0 - (int) (MathHelper.average(server.tickTimeArray) * 1.0E-6D);
        double adjustmentFactor = msptDiff > 0 ? Math.min(1.0, msptDiff) : Math.max(-100.0, msptDiff * 5.0);

        time = (long) (maxParallel / 5d + adjustmentFactor);
        if (time < 0) time = 0;

        List<ItemStack> outputSlots = new ObjectArrayList<>();
        for (ItemStack stack : getItemOutputSlots(null)) {
            outputSlots.add(stack != null ? stack.copy() : null);
        }

        boolean hasMEOutputBus = false;
        for (final IOutputBus bus : GTUtility.validMTEList(mOutputBusses)) {
            if (MachineUtils.isMEOutputBus(bus) && !bus.isFiltered()
                && bus.createTransaction()
                    .hasAvailableSpace()) {
                hasMEOutputBus = true;
                break;
            }
        }

        ObjectArrayList<ItemStack> smeltedOutputs = new ObjectArrayList<>();
        long toSmelt = maxParallel;
        boolean outputFull = false;

        for (ItemStack item : tInput) {
            ItemStack smeltedOutput = GTModHandler.getSmeltingOutput(item, false, null);
            if (smeltedOutput == null) continue;

            int itemsPerRecipe = smeltedOutput.stackSize;
            long remainingToSmelt = Math.min(toSmelt, item.stackSize);
            long maxRecipesPossible = 0;

            if (hasMEOutputBus) {
                maxRecipesPossible = remainingToSmelt;
            } else {
                long neededRecipes = remainingToSmelt;
                for (int i = 0; i < outputSlots.size(); i++) {
                    ItemStack slot = outputSlots.get(i);
                    int spaceInSlot;

                    if (slot == null) {
                        spaceInSlot = smeltedOutput.getMaxStackSize();
                    } else if (GTUtility.areStacksEqual(slot, smeltedOutput)) {
                        spaceInSlot = slot.getMaxStackSize() - slot.stackSize;
                    } else {
                        continue;
                    }

                    int recipesThatFit = spaceInSlot / itemsPerRecipe;
                    int actualFit = (int) Math.min(neededRecipes, recipesThatFit);

                    if (actualFit > 0) {
                        maxRecipesPossible += actualFit;
                        neededRecipes -= actualFit;
                        if (slot == null) {
                            ItemStack newStack = smeltedOutput.copy();
                            newStack.stackSize = actualFit * itemsPerRecipe;
                            outputSlots.set(i, newStack);
                        } else {
                            slot.stackSize += actualFit * itemsPerRecipe;
                        }
                    }
                    if (neededRecipes <= 0) break;
                }
            }

            if (maxRecipesPossible <= 0) {
                outputFull = true;
                continue;
            }

            long toProcess = protectsExcessItem() ? maxRecipesPossible : remainingToSmelt;
            if (toProcess <= 0) continue;

            ItemStack finalOutput = smeltedOutput.copy();
            finalOutput.stackSize = (int) (toProcess * itemsPerRecipe);

            smeltedOutputs.add(finalOutput);

            item.stackSize -= (int) toProcess;
            toSmelt -= toProcess;

            if (toSmelt <= 0) break;
        }

        if (smeltedOutputs.isEmpty()) {
            return outputFull ? CheckRecipeResultRegistry.ITEM_OUTPUT_FULL : CheckRecipeResultRegistry.NO_RECIPE;
        }

        this.mOutputItems = smeltedOutputs.toArray(new ItemStack[0]);
        this.mEfficiency = 10000 - (getIdealStatus() - getRepairStatus()) * 1000;
        this.mEfficiencyIncrease = 10000;
        this.mMaxProgresstime = 100;
        this.lEUt = 0;
        this.updateSlots();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (++tick % 20 == 0) {
            TickrateEvents.queueServerSleep(time);
        }
        return super.onRunningTick(aStack);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("furnaceCount", furnaceCount);
        aNBT.setLong("coalCount", coalCount);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        furnaceCount = aNBT.getLong("furnaceCount");
        coalCount = aNBT.getLong("coalCount");
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        IGregTechTileEntity te = getBaseMetaTileEntity();
        if (te.isClientSide()) return;

        World world = te.getWorld();
        double x = te.getXCoord();
        double y = te.getYCoord();
        double z = te.getZCoord();

        Object2LongOpenHashMap<ItemStackWrapper> mergedDrops = new Object2LongOpenHashMap<>();

        if (furnaceCount > 1) {
            mergedDrops.addTo(new ItemStackWrapper(FURNACE_STACK), furnaceCount - 1);
        }

        if (coalCount >= 2) {
            mergedDrops.addTo(new ItemStackWrapper(COAL_STACK), coalCount / 2);
        }

        if (!mergedDrops.isEmpty()) {
            ToolHelper.generateMatterCluster(world, x + 0.5, y + 0.5, z + 0.5, mergedDrops);
        }
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
    public void checkMaintenance() {}

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.furnaceRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.furnace_array.recipe_type");
    }

    @Override
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new FurnaceArrayGui(this);
    }

    @Override
    public boolean supportsSteamOC() {
        return false;
    }

    @Override
    public boolean supportsSteamCapacityUI() {
        return false;
    }

    @Override
    protected boolean supportsCraftingMEBuffer() {
        return false;
    }
}
