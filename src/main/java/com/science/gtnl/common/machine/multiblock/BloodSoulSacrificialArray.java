package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Avaritia;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.dreammaster.block.BlockList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.loader.RecipeRegister;

import WayofTime.alchemicalWizardry.ModBlocks;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.common.entity.projectile.EntityMeteor;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.recipe.check.SingleRecipeCheck;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import gregtech.common.blocks.BlockCasings8;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class BloodSoulSacrificialArray extends GTMMultiMachineBase<BloodSoulSacrificialArray> {

    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String BSSA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/blood_soul_sacrificial_array"; // 文件路径
    public final int HORIZONTAL_OFF_SET = 16;
    public final int VERTICAL_OFF_SET = 10;
    public final int DEPTH_OFF_SET = 9;
    public static boolean isCreativeOrb = false;
    public static boolean enableRender = true;
    public static int currentEssence = 0;
    public static IStructureDefinition<BloodSoulSacrificialArray> STRUCTURE_DEFINITION = null;
    public static String[][] shape = StructureUtils.readStructureFromFile(BSSA_STRUCTURE_FILE_PATH);
    private static final int MACHINEMODE_BLOOD_DEMON = 0;
    private static final int MACHINEMODE_FALLING_TOWER = 1;
    private static final int MACHINEMODE_ALCHEMIC = 2;

    public BloodSoulSacrificialArray(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public BloodSoulSacrificialArray(String aName) {
        super(aName);
    }

    @Override
    public int getMaxParallelRecipes() {
        if (mParallelTier <= 1) {
            return 8;
        } else {
            if (this.getRecipeMap() == RecipeRegister.FallingTowerRecipes) {
                return (int) Math.pow(4, mParallelTier - 2) / 16;
            } else {
                return (int) Math.pow(4, mParallelTier - 2) * 4;
            }
        }
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case MACHINEMODE_FALLING_TOWER -> RecipeRegister.FallingTowerRecipes;
            case MACHINEMODE_ALCHEMIC -> RecipeRegister.AlchemicChemistrySetRecipes;
            default -> RecipeRegister.BloodDemonInjectionRecipes;
        };
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            RecipeRegister.FallingTowerRecipes,
            RecipeRegister.AlchemicChemistrySetRecipes,
            RecipeRegister.BloodDemonInjectionRecipes);
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == MACHINEMODE_BLOOD_DEMON) return MACHINEMODE_FALLING_TOWER;
        else if (machineMode == MACHINEMODE_FALLING_TOWER) return MACHINEMODE_ALCHEMIC;
        else return MACHINEMODE_BLOOD_DEMON;
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (byte) ((this.machineMode + 1) % 3);
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("BloodSoulSacrificialArray_Mode_" + this.machineMode));

    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (enableRender) {
            enableRender = false;
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("BloodSoulSacrificialArray_Render_Off"));
        } else {
            enableRender = true;
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("BloodSoulSacrificialArray_Render_On"));
        }
        return true;
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("BloodSoulSacrificialArray_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        mParallelTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) {
            return false;
        }

        if (!checkHatch()) {
            return false;
        }

        mParallelTier = getParallelTier(aStack);

        return true;
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
    public IStructureDefinition<BloodSoulSacrificialArray> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<BloodSoulSacrificialArray>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(Loaders.FRF_Casings, 0))
                .addElement('B', ofBlock(sBlockCasings8, 10))
                .addElement('C', ofBlock(gtPlusPlus.core.block.ModBlocks.blockSpecialMultiCasings, 13))
                .addElement('D', ofBlock(gtPlusPlus.core.block.ModBlocks.blockCasingsMisc, 9))
                .addElement('E', ofBlockAnyMeta(BlockList.BloodyIchorium.getBlock()))
                .addElement('F', ofBlockAnyMeta(BlockList.BloodyThaumium.getBlock()))
                .addElement('G', ofBlockAnyMeta(BlockList.BloodyVoid.getBlock()))
                .addElement('H', ofBlock(Blocks.diamond_block, 0))
                .addElement('I', ofBlock(ModBlocks.bloodRune, 0))
                .addElement('J', ofBlock(ModBlocks.bloodRune, 3))
                .addElement('K', ofBlock(ModBlocks.bloodRune, 4))
                .addElement('L', ofBlock(ModBlocks.bloodRune, 5))
                .addElement('M', ofBlock(ModBlocks.bloodRune, 6))
                .addElement('N', ofBlockAnyMeta(com.arc.bloodarsenal.common.block.ModBlocks.blood_lamp))
                .addElement('O', ofBlockAnyMeta(ModBlocks.blockCrystal))
                .addElement('P', ofBlockAnyMeta(ModBlocks.bloodStoneBrick))
                .addElement('Q', ofBlockAnyMeta(Blocks.glowstone))
                .addElement('R', ofBlockAnyMeta(ModBlocks.ritualStone))
                .addElement('S', ofBlockAnyMeta(ModBlocks.runeOfSacrifice))
                .addElement('T', ofBlockAnyMeta(ModBlocks.runeOfSelfSacrifice))
                .addElement('U', ofBlockAnyMeta(ModBlocks.speedRune))
                .addElement('V', ofBlockAnyMeta(Blocks.beacon))
                .addElement('W', ofBlockAnyMeta(com.arc.bloodarsenal.common.block.ModBlocks.lp_materializer))
                .addElement('X', ofFrame(Materials.NaquadahAlloy))
                .addElement('Y', ofBlockAnyMeta(ModBlocks.ritualStone))
                .addElement(
                    'Z',
                    buildHatchAdder(BloodSoulSacrificialArray.class).atLeast(InputBus, OutputBus)
                        .adder(BloodSoulSacrificialArray::addToMachineList)
                        .dot(1)
                        .casingIndex(getCasingTextureID())
                        .buildAndChain(GregTechAPI.sBlockCasings8, 3))
                .addElement('0', ofBlockAnyMeta(ModBlocks.blockAltar))
                .addElement('1', ofBlockAnyMeta(Blocks.hopper))
                .addElement('2', ofFrame(Materials.Plutonium))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mode", machineMode);
        aNBT.setInteger("lp", currentEssence);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        machineMode = aNBT.getInteger("mode");
        currentEssence = aNBT.getInteger("lp");
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        boolean spawnMeteor = false;

        if ((this.mProgresstime + 1) % 20 == 0 && this.mProgresstime > 0
            && this.getRecipeMap() == RecipeRegister.FallingTowerRecipes) {
            if (mProgresstime < 20 && enableRender) {
                spawnMeteor = true;
            }

            if (this.mMaxProgresstime - this.mProgresstime < 250 && spawnMeteor) {
                IGregTechTileEntity aBaseMetaTileEntity = this.getBaseMetaTileEntity();
                World world = aBaseMetaTileEntity.getWorld();
                int baseX = aBaseMetaTileEntity.getXCoord();
                int baseY = aBaseMetaTileEntity.getYCoord();
                int baseZ = aBaseMetaTileEntity.getZCoord();

                ForgeDirection frontFacing = aBaseMetaTileEntity.getFrontFacing();
                ForgeDirection backFacing = frontFacing.getOpposite();

                int offsetX = backFacing.offsetX * 4;
                int offsetZ = backFacing.offsetZ * 4;

                int spawnX = baseX + offsetX;
                int spawnY = 257;
                int spawnZ = baseZ + offsetZ;

                if (!world.isRemote) {
                    EntityMeteor meteor = new EntityMeteor(world, spawnX + 0.5, spawnY, spawnZ + 0.5, 114514);
                    meteor.motionY = -1.0f;

                    world.spawnEntityInWorld(meteor);
                    spawnMeteor = false;
                }
            }
        }

        return super.onRunningTick(stack);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("BloodSoulSacrificialArrayRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BloodSoulSacrificialArray_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BloodSoulSacrificialArray_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BloodSoulSacrificialArray_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BloodSoulSacrificialArray_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BloodSoulSacrificialArray_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BloodSoulSacrificialArray_05"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(33, 14, 30, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_BloodSoulSacrificialArray_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_BloodSoulSacrificialArray_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        ItemStack controllerItem = getControllerSlot();
        this.mParallelTier = getParallelTier(controllerItem);
        isCreativeOrb = false;

        ItemStack requiredItem = GTModHandler.getModItem(Avaritia.ID, "Orb_Armok", 1);

        for (ItemStack item : getAllStoredInputs()) {
            if (item != null && item.isItemEqual(requiredItem)) {
                isCreativeOrb = true;
                break;
            }
        }
        return super.checkProcessing();
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            public CheckRecipeResult process() {
                RecipeMap<?> recipeMap = getCurrentRecipeMap();

                if (inputItems == null) {
                    inputItems = new ItemStack[0];
                }
                if (inputFluids == null) {
                    inputFluids = new FluidStack[0];
                }

                if (isRecipeLocked && recipeLockableMachine != null
                    && recipeLockableMachine.getSingleRecipeCheck() != null) {
                    // Recipe checker is already built, we'll use it
                    SingleRecipeCheck singleRecipeCheck = recipeLockableMachine.getSingleRecipeCheck();
                    // Validate recipe here, otherwise machine will show "not enough output space"
                    // even if recipe cannot be found
                    if (singleRecipeCheck.checkRecipeInputs(false, 1, inputItems, inputFluids) == 0) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    return validateAndCalculateRecipe(
                        recipeLockableMachine.getSingleRecipeCheck()
                            .getRecipe()).checkRecipeResult;
                }
                Stream<GTRecipe> matchedRecipes = findRecipeMatches(recipeMap);
                Iterable<GTRecipe> recipeIterable = matchedRecipes::iterator;
                CheckRecipeResult checkRecipeResult = CheckRecipeResultRegistry.NO_RECIPE;
                for (GTRecipe matchedRecipe : recipeIterable) {
                    CalculationResult foundResult = validateAndCalculateRecipe(matchedRecipe);
                    if (foundResult.checkRecipeResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        // Recipe failed in interesting way, so remember that and continue searching
                        checkRecipeResult = foundResult.checkRecipeResult;
                    }
                    if (foundResult.successfullyConsumedInputs) {
                        // Successfully found and set recipe, so return it
                        if (isCreativeOrb) {
                            return foundResult.checkRecipeResult;
                        } else {
                            currentEssence = SoulNetworkHandler.getCurrentEssence(getOwner());
                            int needEssence = (int) (this.lastRecipe.mSpecialValue * (1 - mParallelTier / 50.0));

                            if (currentEssence >= calculatedParallels * needEssence) {
                                SoulNetworkHandler
                                    .setCurrentEssence(getOwner(), currentEssence - calculatedParallels * needEssence);
                                currentEssence = currentEssence - calculatedParallels * needEssence;
                                return foundResult.checkRecipeResult;
                            }
                            checkRecipeResult = SimpleCheckRecipeResult.ofFailure("metadata.blood");
                        }
                    }
                }
                return checkRecipeResult;
            }

            @Nonnull
            private CalculationResult validateAndCalculateRecipe(@Nonnull GTRecipe recipe) {
                CheckRecipeResult result = validateRecipe(recipe);
                if (!result.wasSuccessful()) {
                    return CalculationResult.ofFailure(result);
                }

                ParallelHelper helper = createParallelHelper(recipe);
                OverclockCalculator calculator = createOverclockCalculator(recipe);
                helper.setCalculator(calculator);
                helper.build();

                if (!helper.getResult()
                    .wasSuccessful()) {
                    return CalculationResult.ofFailure(helper.getResult());
                }

                return CalculationResult.ofSuccess(applyRecipe(recipe, helper, calculator, result));
            }

            @Nonnull
            @Override
            protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {

                currentEssence = SoulNetworkHandler.getCurrentEssence(getOwner());
                int needEssence = (int) (recipe.mSpecialValue * (1 - mParallelTier / 50.0));

                return new ParallelHelper().setRecipe(recipe)
                    .setItemInputs(inputItems)
                    .setFluidInputs(inputFluids)
                    .setAvailableEUt(availableVoltage * availableAmperage)
                    .setMachine(machine, protectItems, protectFluids)
                    .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
                    .setMaxParallel(isCreativeOrb ? maxParallel : Math.min(maxParallel, currentEssence / needEssence))
                    .setEUtModifier(euModifier)
                    .enableBatchMode(batchSize)
                    .setConsumption(true)
                    .setOutputCalculation(true);
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {

                if (isCreativeOrb) {
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                } else {
                    currentEssence = SoulNetworkHandler.getCurrentEssence(getOwner());
                    int needEssence = recipe.mSpecialValue;
                    if (currentEssence > needEssence) {
                        return CheckRecipeResultRegistry.SUCCESSFUL;
                    }
                }

                return SimpleCheckRecipeResult.ofFailure("metadata.blood");
            }

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setAmperage(availableAmperage)
                    .setRecipeEUt(recipe.mEUt)
                    .setEUt(availableVoltage)
                    .setEUtDiscount(1)
                    .setDurationModifier(1 - (mParallelTier / 50.0));
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    public String getOwner() {
        return this.getBaseMetaTileEntity()
            .getOwnerName();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new BloodSoulSacrificialArray(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return ((BlockCasings8) GregTechAPI.sBlockCasings8).getTextureIndex(10);
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
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        currentTip.add(
            StatCollector.translateToLocal("BloodSoulSacrificialArray.LPNetwork") + EnumChatFormatting.WHITE
                + currentEssence
                + EnumChatFormatting.RESET
                + " LP");

    }

    @Override
    public String[] getInfoData() {
        String[] info = super.getInfoData();
        info[4] = StatCollector.translateToLocal("BloodSoulSacrificialArray.LPNetwork") + EnumChatFormatting.RED
            + GTUtility.formatNumbers(Math.abs(currentEssence))
            + EnumChatFormatting.RESET
            + " LP";
        return info;
    }
}
