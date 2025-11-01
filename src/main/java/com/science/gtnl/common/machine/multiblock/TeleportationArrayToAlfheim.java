package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Botania;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import com.brandon3055.brandonscore.common.handlers.ProcessHandler;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.machine.PortalToAlfheimExplosion;
import com.science.gtnl.utils.recipes.GTNL_ParallelHelper;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gtnhlanth.common.register.LanthItemList;
import tectech.thing.casing.TTCasingsContainer;

public class TeleportationArrayToAlfheim extends MultiMachineBase<TeleportationArrayToAlfheim> {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String TATA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/teleportation_array_to_alfheim";
    private static final int HORIZONTAL_OFF_SET = 11;
    private static final int VERTICAL_OFF_SET = 15;
    private static final int DEPTH_OFF_SET = 2;
    private static final String[][] shape = StructureUtils.readStructureFromFile(TATA_STRUCTURE_FILE_PATH);
    public static final int PORTAL_MODE = 0;
    public static final int NATURE_MODE = 1;
    public static final int MANA_MODE = 2;
    public static final int RUNE_MODE = 3;
    public boolean enableInfinityMana = false;
    public static final ItemStack asgardandelion = ItemUtils
        .getItemStack(Botania.ID, "specialFlower", 1, 0, "{type:\"asgardandelion\"}", null);

    public ArrayList<CustomFluidHatch> mFluidManaInputHatch = new ArrayList<>();

    public TeleportationArrayToAlfheim(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public TeleportationArrayToAlfheim(String aName) {
        super(aName);
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case NATURE_MODE -> RecipePool.NatureSpiritArrayRecipes;
            case MANA_MODE -> RecipePool.ManaInfusionRecipes;
            case RUNE_MODE -> RecipePool.RuneAltarRecipes;
            default -> RecipePool.PortalToAlfheimRecipes;
        };
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            RecipePool.NatureSpiritArrayRecipes,
            RecipePool.ManaInfusionRecipes,
            RecipePool.RuneAltarRecipes,
            RecipePool.PortalToAlfheimRecipes);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("mode", machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_BENDING);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == PORTAL_MODE) return NATURE_MODE;
        else if (machineMode == NATURE_MODE) return MANA_MODE;
        else if (machineMode == MANA_MODE) return RUNE_MODE;
        else return PORTAL_MODE;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (byte) ((this.machineMode + 1) % 4);
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("TeleportationArrayToAlfheim_Mode_" + this.machineMode));
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        boolean shouldExplode = false;
        long Strength = 0;
        IGregTechTileEntity aBaseMetaTileEntity = getBaseMetaTileEntity();
        for (ItemStack items : getAllStoredInputs()) {
            if (items.isItemEqual(new ItemStack(Items.bread, 1))) {
                Strength += 50L * items.stackSize;
                shouldExplode = true;
                items.stackSize = 0;
            }
        }
        updateSlots();
        if (Strength > 500) {
            Strength = 500;
        }
        if (shouldExplode) {
            World world = aBaseMetaTileEntity.getWorld();
            world.playSoundEffect(0, 0, 0, RESOURCE_ROOT_ID + ":" + "protal.boom", 1.0F, 1.0F);
            triggerExplosion(aBaseMetaTileEntity, Strength);
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        return super.checkProcessing();
    }

    @Override
    @Nonnull
    public CheckRecipeResult doCheckRecipe() {
        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;

        ArrayList<FluidStack> manaHatchStored = new ArrayList<>();
        for (CustomFluidHatch tHatch : mFluidManaInputHatch) {
            FluidStack fillableStack = tHatch.getFillableStack();
            if (fillableStack != null) {
                manaHatchStored.add(fillableStack);
            }
        }

        // check crafting input hatches first
        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            for (var it = dualInputHatch.inventories(); it.hasNext();) {
                IDualInputInventory slot = it.next();

                if (!slot.isEmpty()) {
                    // try to cache the possible recipes from pattern
                    processingLogic.setInputItems(ArrayUtils.addAll(sharedItems, slot.getItemInputs()));

                    List<FluidStack> fluids = new ArrayList<>(Arrays.asList(slot.getFluidInputs()));
                    if (!manaHatchStored.isEmpty()) fluids.addAll(manaHatchStored);
                    if (enableInfinityMana) {
                        fluids.add(MaterialPool.FluidMana.getFluidOrGas(Integer.MAX_VALUE));
                    }
                    processingLogic.setInputFluids(fluids);

                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) {
                        return foundResult;
                    }
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        // Recipe failed in interesting way, so remember that and continue searching
                        result = foundResult;
                    }
                }
            }
        }

        result = checkRecipeForCustomHatches(result);
        if (result.wasSuccessful()) {
            return result;
        }

        // Use hatch colors if any; fallback to color 1 otherwise.
        short hatchColors = getHatchColors();
        boolean doColorChecking = hatchColors != 0;
        if (!doColorChecking) hatchColors = 0b1;

        for (byte color = 0; color < (doColorChecking ? 16 : 1); color++) {
            if (isColorAbsent(hatchColors, color)) continue;
            List<FluidStack> fluids = new ArrayList<>(getStoredFluidsForColor(Optional.of(color)));
            if (!manaHatchStored.isEmpty()) fluids.addAll(manaHatchStored);
            if (enableInfinityMana) {
                fluids.add(MaterialPool.FluidMana.getFluidOrGas(Integer.MAX_VALUE));
            }
            processingLogic.setInputFluids(fluids);

            if (isInputSeparationEnabled()) {
                if (mInputBusses.isEmpty()) {
                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) return foundResult;
                    // Recipe failed in interesting way, so remember that and continue searching
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                } else {
                    for (MTEHatchInputBus bus : mInputBusses) {
                        if (bus instanceof MTEHatchCraftingInputME || bus instanceof SuperCraftingInputHatchME)
                            continue;
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        List<ItemStack> inputItems = new ArrayList<>();
                        for (int i = bus.getSizeInventory() - 1; i >= 0; i--) {
                            ItemStack stored = bus.getStackInSlot(i);
                            if (stored != null) inputItems.add(stored);
                        }
                        if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                            inputItems.add(getControllerSlot());
                        }
                        processingLogic.setInputItems(inputItems);
                        CheckRecipeResult foundResult = processingLogic.process();
                        if (foundResult.wasSuccessful()) return foundResult;
                        // Recipe failed in interesting way, so remember that and continue searching
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                }
            } else {
                List<ItemStack> inputItems = getStoredInputsForColor(Optional.of(color));
                if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                    inputItems.add(getControllerSlot());
                }
                processingLogic.setInputItems(inputItems);
                CheckRecipeResult foundResult = processingLogic.process();
                if (foundResult.wasSuccessful()) return foundResult;
                // Recipe failed in interesting way, so remember that
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
            }
        }
        return result;
    }

    @Override
    public boolean depleteInput(FluidStack aLiquid, boolean simulate) {
        if (aLiquid == null) return false;
        for (MTEHatchInput tHatch : validMTEList(mInputHatches)) {
            setHatchRecipeMap(tHatch);
            FluidStack tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, aLiquid, false);
            if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                if (simulate) {
                    return true;
                }
                tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, aLiquid, true);
                return tLiquid != null && tLiquid.amount >= aLiquid.amount;
            }
        }
        for (CustomFluidHatch tHatch : validMTEList(mFluidManaInputHatch)) {
            FluidStack tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, aLiquid, false);
            if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                if (simulate) {
                    return true;
                }
                tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, aLiquid, true);
                return tLiquid != null && tLiquid.amount >= aLiquid.amount;
            }
        }
        return false;
    }

    public void triggerExplosion(IGregTechTileEntity aBaseMetaTileEntity, float strength) {
        if (MainConfig.enablePortalToAlfheimBigBoom) {
            ProcessHandler.addProcess(
                new PortalToAlfheimExplosion(
                    aBaseMetaTileEntity.getWorld(),
                    aBaseMetaTileEntity.getXCoord(),
                    aBaseMetaTileEntity.getYCoord(),
                    aBaseMetaTileEntity.getZCoord(),
                    strength));
        } else {
            triggerExplosion(aBaseMetaTileEntity, 5);
        }
        aBaseMetaTileEntity.getWorld()
            .setBlockToAir(
                aBaseMetaTileEntity.getXCoord(),
                aBaseMetaTileEntity.getYCoord(),
                aBaseMetaTileEntity.getZCoord());
        World world = aBaseMetaTileEntity.getWorld();
        int x = aBaseMetaTileEntity.getXCoord();
        int y = aBaseMetaTileEntity.getYCoord();
        int z = aBaseMetaTileEntity.getZCoord();
        world.createExplosion(null, x, y, z, strength * 20, true);
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
    public IStructureDefinition<TeleportationArrayToAlfheim> getStructureDefinition() {
        return StructureDefinition.<TeleportationArrayToAlfheim>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('B', ofBlock(sBlockCasings10, 3))
            .addElement('C', ofBlock(sBlockCasings4, 7))
            .addElement('D', ofBlock(sBlockCasings8, 7))
            .addElement(
                'E',
                ofChain(
                    buildHatchAdder(TeleportationArrayToAlfheim.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy), Maintenance)
                        .dot(1)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings8, 10))
                        .build(),
                    onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 10)),
                    buildHatchAdder(TeleportationArrayToAlfheim.class)
                        .adder(TeleportationArrayToAlfheim::addFluidManaInputHatch)
                        .hatchId(21501)
                        .shouldReject(x -> !x.mFluidManaInputHatch.isEmpty())
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings8, 10))
                        .dot(1)
                        .build()))
            .addElement('F', ofBlock(TTCasingsContainer.sBlockCasingsTT, 0))
            .addElement('G', ofBlock(BlockLoader.metaBlockGlass, 0))
            .addElement('H', ofBlock(BlockLoader.metaBlockGlass, 1))
            .build();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("TeleportationArrayToAlfheimRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(23, 18, 23, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_Casing"), 1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("FluidManaInputHatch"),
                StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_Casing"),
                1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new TeleportationArrayToAlfheim(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings8, 10);
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mCountCasing >= 350;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        if (GTUtility.areStacksEqual(getControllerSlot(), GTModHandler.getModItem(Botania.ID, "pool", 1, 1))
            || GTUtility.areStacksEqual(getControllerSlot(), asgardandelion)) {
            enableInfinityMana = true;
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mFluidManaInputHatch.clear();
        enableInfinityMana = false;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && !mFluidManaInputHatch.isEmpty();
    }

    @Override
    public void updateSlots() {
        for (CustomFluidHatch tHatch : validMTEList(mFluidManaInputHatch)) tHatch.updateSlots();
        super.updateSlots();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (this.mStartUpCheck < 0) {
            if (this.mMaxProgresstime > 0 && this.mProgresstime != 0 || this.getBaseMetaTileEntity()
                .hasWorkJustBeenEnabled()) {
                if (aTick % 20 == 0 || this.getBaseMetaTileEntity()
                    .hasWorkJustBeenEnabled() && !enableInfinityMana) {
                    if (!this.depleteInputFromRestrictedHatches(this.mFluidManaInputHatch, 100)) {
                        this.causeMaintenanceIssue();
                        this.stopMachine(ShutDownReasonRegistry.outOfFluid(MaterialPool.FluidMana.getFluidOrGas(100)));
                    }
                }
            }
        }
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Nonnull
            @Override
            public GTNL_ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
                if (enableInfinityMana && inputFluids != null && inputFluids.length > 0) {
                    inputFluids[0] = MaterialPool.FluidMana.getFluidOrGas(Integer.MAX_VALUE);
                }
                return super.createParallelHelper(recipe).setFluidInputs(inputFluids);
            }

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return super.validateRecipe(recipeWithMultiplier(recipe, inputFluids));
            }
        }.setMaxTierSkips(getTrueParallel());
    }

    public GTRecipe recipeWithMultiplier(GTRecipe recipe, FluidStack[] fluidInputs) {
        if (recipe == null || fluidInputs == null) {
            return recipe;
        }

        if (recipe.mFluidInputs == null || recipe.mFluidInputs.length == 0) {
            return recipe;
        }

        if (recipe.mFluidInputs[0] == null) {
            return recipe;
        }

        for (FluidStack fluid : fluidInputs) {
            if (fluid == null) {
                return recipe;
            }
        }

        GTRecipe tRecipe = recipe.copy();
        if (enableInfinityMana) {
            tRecipe.mFluidInputs = null;
        }

        return tRecipe;
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("TeleportationArrayToAlfheim_Mode_" + machineMode);
    }

    public boolean addFluidManaInputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof CustomFluidHatch hatch && aMetaTileEntity.getBaseMetaTileEntity()
                .getMetaTileID() == 21501) {
                hatch.updateTexture(aBaseCasingIndex);
                hatch.updateCraftingIcon(this.getMachineCraftingIcon());
                return addToMachineListInternal(mFluidManaInputHatch, aTileEntity, aBaseCasingIndex);
            }
        }
        return false;
    }
}
