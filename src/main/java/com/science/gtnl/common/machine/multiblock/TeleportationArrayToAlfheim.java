package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

import org.jetbrains.annotations.NotNull;

import com.brandon3055.draconicevolution.common.utils.handlers.ProcessHandler;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.gui.modularui.GTNLMultiBlockBaseGui;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.machine.PortalToAlfheimExplosion;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.TranslatableText;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gtnhlanth.common.register.LanthItemList;
import tectech.thing.casing.TTCasingsContainer;

public class TeleportationArrayToAlfheim extends MultiMachineBase<TeleportationArrayToAlfheim> {

    private static final TranslatableText MANA_INPUT_HATCH_NAME = TranslatableText.lang("FluidManaInputHatch");
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
    public static final ItemStack asgardandelion = ItemUtils
        .getItemStack(Mods.Botania.ID, "specialFlower", 1, 0, "{type:\"asgardandelion\"}", null);

    public boolean enableInfinityMana = false;
    public ArrayList<CustomFluidHatch> mFluidManaInputHatch = new ArrayList<>();
    public final ArrayList<FluidStack> sharedManaRecipeFluids = new ArrayList<>();

    public TeleportationArrayToAlfheim(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public TeleportationArrayToAlfheim(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new TeleportationArrayToAlfheim(this.mName);
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mFluidManaInputHatch.clear();
        enableInfinityMana = false;
    }

    @Override
    public void updateSlots() {
        for (CustomFluidHatch tHatch : GTUtility.validMTEList(mFluidManaInputHatch)) tHatch.updateSlots();
        super.updateSlots();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (this.mStartUpCheck < 0) {
            if (this.mMaxProgresstime > 0 && this.mProgresstime != 0 || this.getBaseMetaTileEntity()
                .hasWorkJustBeenEnabled()) {
                if ((aTick % 20 == 0 || this.getBaseMetaTileEntity()
                    .hasWorkJustBeenEnabled()) && !enableInfinityMana) {
                    if (!this.depleteInputFromRestrictedHatches(this.mFluidManaInputHatch, 100)) {
                        this.causeMaintenanceIssue();
                        this.stopMachine(ShutDownReasonRegistry.outOfFluid(GTNLMaterials.FluidMana.getFluidOrGas(100)));
                    }
                }
            }
        }
    }

    @Override
    public IStructureDefinition<TeleportationArrayToAlfheim> getStructureDefinition() {
        return StructureDefinition.<TeleportationArrayToAlfheim>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 7))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement(
                'E',
                StructureUtility.ofChain(
                    buildHatchAdder(TeleportationArrayToAlfheim.class)
                        .atLeast(
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.InputHatch,
                            HatchElement.OutputHatch,
                            HatchElement.Energy.or(HatchElement.ExoticEnergy),
                            HatchElement.Maintenance)
                        .casingIndex(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10))
                        .hint(1)
                        .build(),
                    StructureUtility
                        .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10)),
                    buildHatchAdder(TeleportationArrayToAlfheim.class)
                        .adder(TeleportationArrayToAlfheim::addFluidManaInputHatch)
                        .hatchId(21501)
                        .shouldReject(x -> !x.mFluidManaInputHatch.isEmpty())
                        .casingIndex(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10))
                        .hint(1)
                        .build()))
            .addElement('F', StructureUtility.ofBlock(TTCasingsContainer.sBlockCasingsTT, 0))
            .addElement('G', StructureUtility.ofBlock(BlockLoader.metaBlockGlass, 0))
            .addElement('H', StructureUtility.ofBlock(BlockLoader.metaBlockGlass, 1))
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkHatchMin(errors, MANA_INPUT_HATCH_NAME, mFluidManaInputHatch.size(), 1);
        checkCasingMin(errors, mCountCasing, 350);
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        if (GTUtility.areStacksEqual(getControllerSlot(), GTModHandler.getModItem(Mods.Botania.ID, "pool", 1, 1), true)
            || GTUtility.areStacksEqual(getControllerSlot(), asgardandelion, true)) {
            enableInfinityMana = true;
        }
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
            case NATURE_MODE -> GTNLRecipeMaps.NatureSpiritArrayRecipes;
            case MANA_MODE -> GTNLRecipeMaps.ManaInfusionRecipes;
            case RUNE_MODE -> GTNLRecipeMaps.RuneAltarRecipes;
            default -> GTNLRecipeMaps.PortalToAlfheimRecipes;
        };
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            GTNLRecipeMaps.NatureSpiritArrayRecipes,
            GTNLRecipeMaps.ManaInfusionRecipes,
            GTNLRecipeMaps.RuneAltarRecipes,
            GTNLRecipeMaps.PortalToAlfheimRecipes);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        boolean shouldExplode = false;
        long strength = 0;
        IGregTechTileEntity aBaseMetaTileEntity = getBaseMetaTileEntity();
        for (ItemStack items : getAllStoredInputs()) {
            if (items.isItemEqual(new ItemStack(Items.bread, 1))) {
                strength += 50L * items.stackSize;
                shouldExplode = true;
                items.stackSize = 0;
            }
        }
        updateSlots();
        if (strength > 500) {
            strength = 500;
        }
        if (shouldExplode) {
            World world = aBaseMetaTileEntity.getWorld();
            world.playSoundEffect(
                aBaseMetaTileEntity.getXCoord(),
                aBaseMetaTileEntity.getYCoord(),
                aBaseMetaTileEntity.getZCoord(),
                RESOURCE_ROOT_ID + ":" + "protal.boom",
                1.0F,
                1.0F);
            triggerExplosion(aBaseMetaTileEntity, strength);
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        return super.checkProcessing();
    }

    @Override
    @NotNull
    public CheckRecipeResult doCheckRecipe() {
        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;

        List<FluidStack> manaHatchStored = collectManaInputFluids();

        // Reuse dual-input buffers first and append mana fluids.
        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            for (var it = dualInputHatch.inventories(); it.hasNext();) {
                IDualInputInventory slot = it.next();

                if (!slot.isEmpty()) {
                    loadDualInputBuffers(sharedItems, slot);
                    appendSharedManaRecipeFluids(manaHatchStored);
                    setProcessingInputFluids(recipeSearchFluidInputs);
                    CheckRecipeResult foundResult = processRecipeSearch();
                    if (foundResult.wasSuccessful()) {
                        return foundResult;
                    }
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        // Keep the most relevant non-empty failure while continuing the search.
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
            replaceRecipeSearchFluids(getStoredFluidsForColor(Optional.of(color)));
            appendSharedManaRecipeFluids(manaHatchStored);
            setProcessingInputFluids(recipeSearchFluidInputs);

            if (isInputSeparationEnabled()) {
                if (mInputBusses.isEmpty()) {
                    replaceRecipeSearchItems(getStoredInputsForColor(Optional.of(color)));
                    addControllerSlotIfNeeded(recipeSearchItemInputs);
                    setProcessingInputItems(recipeSearchItemInputs);
                    CheckRecipeResult foundResult = processRecipeSearch();
                    if (foundResult.wasSuccessful()) return foundResult;
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                } else {
                    for (MTEHatchInputBus bus : mInputBusses) {
                        if (bus instanceof MTEHatchCraftingInputME || bus instanceof SuperCraftingInputHatchME)
                            continue;
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        recipeSearchItemInputs.clear();
                        recipeSearchItemInputs.ensureCapacity(bus.getSizeInventory() + 1);
                        collectBusInputs(bus, recipeSearchItemInputs);
                        addControllerSlotIfNeeded(recipeSearchItemInputs);
                        setProcessingInputItems(recipeSearchItemInputs);
                        CheckRecipeResult foundResult = processRecipeSearch();
                        if (foundResult.wasSuccessful()) return foundResult;
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                }
            } else {
                replaceRecipeSearchItems(getStoredInputsForColor(Optional.of(color)));
                addControllerSlotIfNeeded(recipeSearchItemInputs);
                setProcessingInputItems(recipeSearchItemInputs);
                CheckRecipeResult foundResult = processRecipeSearch();
                if (foundResult.wasSuccessful()) return foundResult;
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
            }
        }
        return result;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public GTNLParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                if (enableInfinityMana && inputFluids != null && inputFluids.length > 0) {
                    inputFluids[0] = GTNLMaterials.FluidMana.getFluidOrGas(Integer.MAX_VALUE);
                }
                return super.createParallelHelper(recipe).setFluidInputs(inputFluids);
            }

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return super.validateRecipe(recipeWithMultiplier(recipe));
            }

            @Override
            public @NotNull GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setHeatOC(getHeatOC())
                    .setMachineHeat(getMachineHeat())
                    .setHeatDiscount(getHeatDiscount())
                    .setAmperageOC(getAmperageOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    public GTRecipe recipeWithMultiplier(GTRecipe recipe) {
        if (recipe == null) {
            return null;
        }
        if (enableInfinityMana) {
            GTRecipe tRecipe = recipe.copy();
            tRecipe.mFluidInputs = null;
            return tRecipe;
        }

        return recipe;
    }

    public List<FluidStack> collectManaInputFluids() {
        sharedManaRecipeFluids.clear();
        for (CustomFluidHatch tHatch : mFluidManaInputHatch) {
            FluidStack fillableStack = tHatch.getFillableStack();
            if (fillableStack != null) {
                sharedManaRecipeFluids.add(fillableStack);
            }
        }
        return sharedManaRecipeFluids;
    }

    public void appendSharedManaRecipeFluids(List<FluidStack> manaHatchStored) {
        recipeSearchFluidInputs.ensureCapacity(recipeSearchFluidInputs.size() + manaHatchStored.size() + 1);
        if (!manaHatchStored.isEmpty()) {
            recipeSearchFluidInputs.addAll(manaHatchStored);
        }
        if (enableInfinityMana) {
            recipeSearchFluidInputs.add(GTNLMaterials.FluidMana.getFluidOrGas(Integer.MAX_VALUE));
        }
    }

    @Override
    public boolean depleteInput(FluidStack aLiquid, boolean simulate) {
        if (aLiquid == null) return false;
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
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
        for (CustomFluidHatch tHatch : GTUtility.validMTEList(mFluidManaInputHatch)) {
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("TeleportationArrayToAlfheimRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TeleportationArrayToAlfheim_03"))
            .addPerfectOCInfo()
            .addTecTechHatchInfo()
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
    public String getMachineModeName() {
        return StatCollector.translateToLocal("TeleportationArrayToAlfheim_Mode_" + machineMode);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTNLMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_BENDING);
    }

    @Override
    @Deprecated
    public void setMachineModeIcons() {
        // TODO: Remove this mui1 fallback after this GUI no longer supports mui1 startup paths.
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_BENDING);
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == PORTAL_MODE) return NATURE_MODE;
        else if (machineMode == NATURE_MODE) return MANA_MODE;
        else if (machineMode == MANA_MODE) return RUNE_MODE;
        else return PORTAL_MODE;
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 4;
        GTUtility.sendChatTrans(aPlayer, "TeleportationArrayToAlfheim_Mode_" + this.machineMode);
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

    public void triggerExplosion(IGregTechTileEntity aBaseMetaTileEntity, float strength) {
        if (MainConfig.machine.portal_to_alfheim.bigBoom) {
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
}
