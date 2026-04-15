package com.science.gtnl.common.machine.multiblock.steam;

import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
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
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.event.SubscribeEventUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.StructureError;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;

public class FurnaceArray extends SteamMultiMachineBase<FurnaceArray> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;
    public static int CASING_INDEX = GTUtility.getTextureId((byte) 116, (byte) 52);

    public long furnaceCount = 1;
    public long coalCount = 0;

    public int tick = 0;

    public long time;

    public static ItemStack furnace = new ItemStack(Blocks.furnace), coal = new ItemStack(Items.coal);

    public FurnaceArray(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FurnaceArray(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FurnaceArray(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE)
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
        return RecipeMaps.furnaceRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("FurnaceArrayRecipeType");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("FurnaceArrayRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_04"))
            .beginStructureBlock(3, 3, 3, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_FurnaceArray_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_FurnaceArray_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_FurnaceArray_Casing"))
            .toolTipFinisher();
        return tt;
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
                buildHatchAdder(FurnaceArray.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        SteamHatchElement.InputBus_Steam,
                        SteamHatchElement.OutputBus_Steam,
                        HatchElement.InputBus,
                        HatchElement.OutputBus)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(BlockLoader.metaCasing02, 20))))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch();
    }

    @Override
    public void validateStructure(Collection<StructureError> errors, NBTTagCompound context) {}

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
        int x = te.getXCoord();
        int y = te.getYCoord();
        int z = te.getZCoord();

        long remainingFurnace = furnaceCount - 1;
        while (remainingFurnace > 0) {
            int dropAmount = (int) Math.min(64, remainingFurnace);
            ItemStack drop = furnace.copy();
            drop.stackSize = dropAmount;
            EntityItem ent = new EntityItem(world, x, y, z, drop);
            world.spawnEntityInWorld(ent);
            remainingFurnace -= dropAmount;
        }

        long remainingCoal = coalCount / 2;
        while (remainingCoal > 0) {
            int dropAmount = (int) Math.min(64, remainingCoal);
            ItemStack drop = coal.copy();
            drop.stackSize = dropAmount;
            EntityItem ent = new EntityItem(world, x, y, z, drop);
            world.spawnEntityInWorld(ent);
            remainingCoal -= dropAmount;
        }
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        time = 0;
        List<ItemStack> tInput = getAllStoredInputs();

        for (ItemStack input : tInput) {
            if (GTUtility.areStacksEqual(input, furnace)) {
                furnaceCount += input.stackSize;
                depleteInput(input);
                continue;
            }
            if (GTUtility.areStacksEqual(input, coal)) {
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

        List<ItemStack> outputSlots = new ArrayList<>();
        for (ItemStack stack : getItemOutputSlots(null)) {
            outputSlots.add(stack != null ? stack.copy() : null);
        }

        boolean hasMEOutputBus = false;
        for (final MTEHatch bus : GTUtility.validMTEList(mOutputBusses)) {
            if (bus instanceof MTEHatchOutputBusME meBus) {
                if (!meBus.isLocked() && meBus.canAcceptItem()) {
                    hasMEOutputBus = true;
                    break;
                }
            }
        }

        ArrayList<ItemStack> smeltedOutputs = new ArrayList<>();
        long toSmelt = maxParallel;

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
                        spaceInSlot = (slot.stackSize >= 64) ? (int) (neededRecipes * itemsPerRecipe)
                            : (slot.getMaxStackSize() - slot.stackSize);
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
            return CheckRecipeResultRegistry.NO_RECIPE;
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
    protected boolean supportsCraftingMEBuffer() {
        return false;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (++tick % 20 == 0) {
            SubscribeEventUtils.sleepTime += time;
        }
        return super.onRunningTick(aStack);
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        screenElements
            .widget(
                new TextWidget()
                    .setStringSupplier(
                        () -> StatCollector.translateToLocalFormatted("Info_FurnaceArray_01", furnaceCount))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setDefaultColor(COLOR_TEXT_WHITE.get()))
            .widget(
                new TextWidget()
                    .setStringSupplier(() -> StatCollector.translateToLocalFormatted("Info_FurnaceArray_02", coalCount))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setDefaultColor(COLOR_TEXT_WHITE.get()))
            .widget(new FakeSyncWidget.LongSyncer(() -> furnaceCount, f -> furnaceCount = f))
            .widget(new FakeSyncWidget.LongSyncer(() -> coalCount, c -> coalCount = c));
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
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }
}
