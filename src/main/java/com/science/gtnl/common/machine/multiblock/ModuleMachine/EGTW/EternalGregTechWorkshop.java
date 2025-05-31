package com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.Util.EternalGregTechWorkshopUpgrade.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.filterValidMTEs;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static java.lang.Math.*;
import static java.lang.Math.floor;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.google.common.math.LongMath;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.FluidNameHolderWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.ProgressBar;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.Util.EGTWUpgradeStorage;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.Util.EternalGregTechWorkshopUI;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.Util.EternalGregTechWorkshopUpgrade;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.Util.UpgradeColor;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipeRegister;

import bartworks.common.loaders.ItemRegistry;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import tectech.TecTech;
import tectech.thing.casing.TTCasingsContainer;
import tectech.thing.gui.TecTechUITextures;
import tectech.thing.metaTileEntity.multi.godforge.util.MilestoneFormatter;
import tectech.thing.metaTileEntity.multi.godforge.util.MilestoneIcon;

public class EternalGregTechWorkshop extends MultiMachineBase<EternalGregTechWorkshop> {

    // 75 x 19 x 75
    public static final String STRUCTURE_PIECE_MAIN_TOP = "main_top";
    private final int HORIZONTAL_OFF_SET_TOP = 37;
    private final int VERTICAL_OFF_SET_TOP = 46;
    private final int DEPTH_OFF_SET_TOP = 11;

    // 53 x 22 x 53
    public static final String STRUCTURE_PIECE_MAIN_UP = "main_up";
    private final int HORIZONTAL_OFF_SET_UP = 26;
    private final int VERTICAL_OFF_SET_UP = 27;
    private final int DEPTH_OFF_SET_UP = 0;

    // 53 x 11 x 53
    public static final String STRUCTURE_PIECE_MAIN = "main";
    private final int HORIZONTAL_OFF_SET = 26;
    private final int VERTICAL_OFF_SET = 5;
    private final int DEPTH_OFF_SET = 1;

    // 53 x 22 x 53
    public static final String STRUCTURE_PIECE_MAIN_DOWN = "main_down";
    private final int HORIZONTAL_OFF_SET_DOWN = 26;
    private final int VERTICAL_OFF_SET_DOWN = -6;
    private final int DEPTH_OFF_SET_DOWN = 0;

    // 75 x 22 x 75
    public static final String STRUCTURE_PIECE_MAIN_BOTTOM = "main_bottom";
    private final int HORIZONTAL_OFF_SET_BOTTOM = 37;
    private final int VERTICAL_OFF_SET_BOTTOM = -28;
    private final int DEPTH_OFF_SET_BOTTOM = 11;

    // 63 x 7 x 63
    public static final String STRUCTURE_PIECE_MAIN_EXTRA = "main_extra";
    public static final String STRUCTURE_PIECE_MAIN_EXTRA_AIR = "main_extra_air";
    private final int HORIZONTAL_OFF_SET_EXTRA = 31;
    private final int VERTICAL_OFF_SET_EXTRA_UP = 14;
    private final int VERTICAL_OFF_SET_EXTRA_DOWN = -8;
    private final int DEPTH_OFF_SET_EXTRA = 5;

    public static final String EGTWT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/top";
    public static final String EGTWU_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/up";
    public static final String EGTWC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/center";
    public static final String EGTWD_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/down";
    public static final String EGTWB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/bottom";
    public static final String EGTWE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/extra";
    public static String[][] shape_top = StructureUtils.readStructureFromFile(EGTWT_STRUCTURE_FILE_PATH);
    public static String[][] shape_up = StructureUtils.readStructureFromFile(EGTWU_STRUCTURE_FILE_PATH);
    public static String[][] shape = StructureUtils.readStructureFromFile(EGTWC_STRUCTURE_FILE_PATH);
    public static String[][] shape_down = StructureUtils.readStructureFromFile(EGTWD_STRUCTURE_FILE_PATH);
    public static String[][] shape_bottom = StructureUtils.readStructureFromFile(EGTWB_STRUCTURE_FILE_PATH);
    public static String[][] shape_extra = StructureUtils.readStructureFromFile(EGTWE_STRUCTURE_FILE_PATH);
    public static final String[][] shape_extra_air = StructureUtils.replaceLetters(shape_extra, "a");

    public final int CASING_INDEX = 960;

    public static final String TEXTURE_OVERLAY_FRONT_SCREEN_ON = Mods.GregTech.ID + ":"
        + "iconsets/GODFORGE_CONTROLLER";
    public static Textures.BlockIcons.CustomIcon ScreenON = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_FRONT_SCREEN_ON);
    public static final String TEXTURE_OVERLAY_FRONT_SCREEN_OFF = Mods.GregTech.ID + ":" + "iconsets/SCREEN_OFF";
    public static Textures.BlockIcons.CustomIcon ScreenOFF = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_FRONT_SCREEN_OFF);

    private int tCountCasing = 0;
    private int mHeatingCapacity = 0;
    private int mMachineTier = 0;
    private int mModuleTier = 1;
    private long mMaxUseEUt = 0;
    private double mEUtDiscount = 1;
    private double mSpeedBoost = 1;
    private UUID ownerUUID;
    private boolean mExtraModule;
    private boolean enableExtraModule;
    private boolean secretUpgrade;
    private boolean isRendererDisabled;
    private boolean isRenderActive;

    private static final MilestoneFormatter DEFAULT_FORMATTING_MODE = MilestoneFormatter.COMMA;
    private static final BigInteger DEFAULT_TOTAL_POWER = BigInteger.ZERO;
    private static final int DEFAULT_FUEL_CONSUMPTION_FACTOR = 1;

    private static final long POWER_MILESTONE_CONSTANT = LongMath.pow(10, 15);
    private static final long RECIPE_MILESTONE_CONSTANT = LongMath.pow(10, 7);
    private static final long FUEL_MILESTONE_CONSTANT = 10_000;
    private static final double POWER_LOG_CONSTANT = Math.log(9);
    private static final double RECIPE_LOG_CONSTANT = Math.log(4);
    private static final double FUEL_LOG_CONSTANT = Math.log(3);

    private final ItemStack[] storedUpgradeWindowItems = new ItemStack[16];
    protected ItemStackHandler inputSlotHandler = new ItemStackHandler(16);

    private BigInteger totalPowerConsumed = DEFAULT_TOTAL_POWER;
    private long totalRecipesProcessed;
    private long totalFuelConsumed;
    private float totalExtensionsBuilt;

    private long fuelConsumption;

    private int fuelConsumptionFactor = DEFAULT_FUEL_CONSUMPTION_FACTOR;
    private int selectedFuelType;

    private int gravitonShardsAvailable = 0;
    private int gravitonShardsSpent;
    private boolean gravitonShardEjection;

    private float powerMilestonePercentage;
    private float recipeMilestonePercentage;
    private float fuelMilestonePercentage;
    private float structureMilestonePercentage;

    private MilestoneFormatter formattingMode = DEFAULT_FORMATTING_MODE;

    private final EGTWUpgradeStorage upgrades = new EGTWUpgradeStorage();
    private EternalGregTechWorkshopUpgrade currentUpgradeWindow;

    public ArrayList<EternalGregTechWorkshopModule> moduleHatches = new ArrayList<>();

    public EternalGregTechWorkshop(String aName) {
        super(aName);
    }

    public EternalGregTechWorkshop(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EternalGregTechWorkshop(this.mName);
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        saveGeneralNBT(aNBT, false);

        super.saveNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        // Upgrade window stored items
        NBTTagCompound upgradeWindowStorageNBTTag = new NBTTagCompound();
        int storageIndex = 0;
        for (ItemStack itemStack : inputSlotHandler.getStacks()) {
            if (itemStack != null) {
                upgradeWindowStorageNBTTag
                    .setInteger(storageIndex + "stacksizeOfStoredUpgradeItems", itemStack.stackSize);
                aNBT.setTag(storageIndex + "storedUpgradeItem", itemStack.writeToNBT(new NBTTagCompound()));
            }
            storageIndex++;
        }
        aNBT.setTag("upgradeWindowStorage", upgradeWindowStorageNBTTag);

        saveGeneralNBT(aNBT, true);
        aNBT.setInteger("mMachineTier", mMachineTier);
        aNBT.setBoolean("enableExtraModule", enableExtraModule);
        aNBT.setBoolean("mExtraModule", mExtraModule);
        aNBT.setBoolean("isRenderActive", isRenderActive);
        aNBT.setBoolean("isRendererDisabled", isRendererDisabled);
    }

    @Override
    public void loadNBTData(final NBTTagCompound NBT) {
        super.loadNBTData(NBT);
        mMachineTier = NBT.getInteger("mMachineTier");
        mModuleTier = NBT.getInteger("mModuleTier");
        enableExtraModule = NBT.getBoolean("enableExtraModule");
        mExtraModule = NBT.getBoolean("mExtraModule");
        gravitonShardsSpent = NBT.getInteger("gravitonShardsSpent");
        isRenderActive = NBT.getBoolean("isRenderActive");
        isRendererDisabled = NBT.getBoolean("isRendererDisabled");

        if (NBT.hasKey("totalPowerConsumed")) {
            totalPowerConsumed = new BigInteger(NBT.getByteArray("totalPowerConsumed"));
        }
        if (NBT.hasKey("formattingMode")) {
            int index = MathHelper.clamp_int(NBT.getInteger("formattingMode"), 0, MilestoneFormatter.VALUES.length);
            formattingMode = MilestoneFormatter.VALUES[index];
        }

        // Stored items
        NBTTagCompound tempItemTag = NBT.getCompoundTag("upgradeWindowStorage");
        for (int index = 0; index < 16; index++) {
            int stackSize = tempItemTag.getInteger(index + "stacksizeOfStoredUpgradeItems");
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(NBT.getCompoundTag(index + "storedUpgradeItem"));
            if (itemStack != null) {
                storedUpgradeWindowItems[index] = itemStack.splitStack(stackSize);
            }
        }

        upgrades.rebuildFromNBT(NBT);
    }

    private void saveGeneralNBT(NBTTagCompound NBT, boolean force) {
        if (force || mModuleTier != 0) NBT.setInteger("mModuleTier", mModuleTier);
        if (force || gravitonShardsSpent != 0) NBT.setInteger("gravitonShardsSpent", gravitonShardsSpent);
        if (force || gravitonShardsAvailable != 0) NBT.setInteger("gravitonShardsAvailable", gravitonShardsAvailable);
        if (force || secretUpgrade) NBT.setBoolean("secretUpgrade", secretUpgrade);

        if (force || gravitonShardsAvailable != 0) NBT.setInteger("gravitonShardsAvailable", gravitonShardsAvailable);
        if (force || gravitonShardsSpent != 0) NBT.setInteger("gravitonShardsSpent", gravitonShardsSpent);
        if (force || totalRecipesProcessed != 0) NBT.setLong("totalRecipesProcessed", totalRecipesProcessed);
        if (force || totalFuelConsumed != 0) NBT.setLong("totalFuelConsumed", totalFuelConsumed);
        if (force || gravitonShardEjection) NBT.setBoolean("gravitonShardEjection", gravitonShardEjection);
        if (force || secretUpgrade) NBT.setBoolean("secretUpgrade", secretUpgrade);

        if (force || !DEFAULT_TOTAL_POWER.equals(totalPowerConsumed)) {
            NBT.setByteArray("totalPowerConsumed", totalPowerConsumed.toByteArray());
        }
        if (force || formattingMode != DEFAULT_FORMATTING_MODE) {
            NBT.setInteger("formattingMode", formattingMode.ordinal());
        }
        upgrades.serializeToNBT(NBT, force);
    }

    public void addTotalPowerConsumed(BigInteger amount) {
        totalPowerConsumed = totalPowerConsumed.add(amount);
    }

    public void addTotalRecipesProcessed(long amount) {
        totalRecipesProcessed += amount;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (isRendererDisabled) {
            isRendererDisabled = false;
            // let the renderer automatically rebuild itself as needed through normal logic
        } else {
            isRendererDisabled = true;
            if (isRenderActive) destroyRenderer();
        }
        aPlayer.addChatMessage(
            new ChatComponentTranslation(
                isRendererDisabled ? "Render_EternalGregTechWorkshop_Disabled"
                    : "Render_EternalGregTechWorkshop_Enabled"));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.EternalGregTechWorkshopUpgradeRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("EternalGregTechWorkshopRecipeType"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(75, 96, 75, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EternalGregTechWorkshop_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EternalGregTechWorkshop_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EternalGregTechWorkshop_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_EternalGregTechWorkshop_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX + 1),
                TextureFactory.builder()
                    .addIcon(ScreenON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(ScreenON)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX + 1),
                TextureFactory.builder()
                    .addIcon(ScreenOFF)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(ScreenOFF)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX + 1) };
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public float getSpeedBonus() {
        return 1;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public IStructureDefinition<EternalGregTechWorkshop> getStructureDefinition() {
        return StructureDefinition.<EternalGregTechWorkshop>builder()
            .addShape(STRUCTURE_PIECE_MAIN_TOP, transpose(shape_top))
            .addShape(STRUCTURE_PIECE_MAIN_UP, transpose(shape_up))
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addShape(STRUCTURE_PIECE_MAIN_DOWN, transpose(shape_down))
            .addShape(STRUCTURE_PIECE_MAIN_BOTTOM, transpose(shape_bottom))
            .addShape(STRUCTURE_PIECE_MAIN_EXTRA, transpose(shape_extra))
            .addShape(STRUCTURE_PIECE_MAIN_EXTRA_AIR, transpose(shape_extra_air))
            .addElement('A', ofBlock(TTCasingsContainer.GodforgeCasings, 0))
            .addElement('B', ofBlock(Loaders.componentAssemblylineCasing, 12))
            .addElement('C', ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('D', ofBlock(GregTechAPI.sBlockCasingsSEMotor, 4))
            .addElement('E', ofBlock(GregTechAPI.sBlockCasings10, 11))
            .addElement('F', ofBlock(GregTechAPI.sBlockCasings9, 12))
            .addElement('G', ofBlock(TTCasingsContainer.GodforgeCasings, 1))
            .addElement('H', ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement('I', ofBlock(ModBlocks.blockCasings5Misc, 14))
            .addElement('J', ofBlock(GregTechAPI.sBlockCasings9, 14))
            .addElement('K', ofFrame(Materials.NaquadahAlloy))
            .addElement('L', ofBlock(GregTechAPI.sBlockGlass1, 2))
            .addElement('M', ofBlock(ItemRegistry.bw_realglas2, 0))
            .addElement(
                'N',
                buildHatchAdder(EternalGregTechWorkshop.class).atLeast(InputBus, OutputBus, InputHatch, OutputHatch)
                    .casingIndex(CASING_INDEX + 1)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(x -> ++x.tCountCasing, ofBlock(TTCasingsContainer.GodforgeCasings, 1))))
            .addElement('O', ofBlock(GregTechAPI.sBlockCasings10, 2))
            .addElement('P', ofBlock(TTCasingsContainer.sBlockCasingsBA0, 10))
            .addElement('Q', ofBlock(TTCasingsContainer.GodforgeCasings, 7))
            .addElement('R', ofBlock(TTCasingsContainer.GodforgeCasings, 4))
            .addElement('S', ofBlock(TTCasingsContainer.GodforgeCasings, 8))
            .addElement('T', ofBlock(Loaders.gravityStabilizationCasing, 0))
            .addElement('U', ofBlock(TTCasingsContainer.SpacetimeCompressionFieldGenerators, 8))
            .addElement('V', ofBlock(TTCasingsContainer.TimeAccelerationFieldGenerator, 8))
            .addElement('W', ofBlock(TTCasingsContainer.sBlockCasingsBA0, 11))
            .addElement('X', ofBlock(TTCasingsContainer.StabilisationFieldGenerators, 8))
            .addElement(
                'Y',
                HatchElementBuilder.<EternalGregTechWorkshop>builder()
                    .atLeast(EternalGregTechWorkshop.moduleElement.Module)
                    .casingIndex(CASING_INDEX)
                    .dot(1)
                    .buildAndChain(TTCasingsContainer.GodforgeCasings, 0))
            .addElement(
                'Z',
                ofChain(
                    ofBlock(GregTechAPI.sBlockCasings1, 14),
                    ofBlock(BlockLoader.BlockEternalGregTechWorkshopRender, 0)))
            .addElement('a', isAir())
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        moduleHatches.clear();
        int checkTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) {
            mMachineTier = 0;
            mExtraModule = false;
            if (isRenderActive) destroyRenderer();
            return false;
        }

        while (checkTier < Integer.MAX_VALUE - 1) {
            if (!checkPiece(
                STRUCTURE_PIECE_MAIN_UP,
                HORIZONTAL_OFF_SET_UP,
                VERTICAL_OFF_SET_UP + checkTier * 22,
                DEPTH_OFF_SET_UP)) {
                break;
            }
            if (!checkPiece(
                STRUCTURE_PIECE_MAIN_DOWN,
                HORIZONTAL_OFF_SET_DOWN,
                VERTICAL_OFF_SET_DOWN - checkTier * 22,
                DEPTH_OFF_SET_DOWN)) {
                break;
            }
            checkTier++;
        }

        if (!checkPiece(
            STRUCTURE_PIECE_MAIN_TOP,
            HORIZONTAL_OFF_SET_TOP,
            VERTICAL_OFF_SET_TOP + (checkTier - 1) * 22,
            DEPTH_OFF_SET_TOP)) {
            mMachineTier = 0;
            mExtraModule = false;
            if (isRenderActive) destroyRenderer();
            return false;
        }

        if (!checkPiece(
            STRUCTURE_PIECE_MAIN_BOTTOM,
            HORIZONTAL_OFF_SET_BOTTOM,
            VERTICAL_OFF_SET_BOTTOM - (checkTier - 1) * 22,
            DEPTH_OFF_SET_BOTTOM)) {
            mMachineTier = 0;
            mExtraModule = false;
            if (isRenderActive) destroyRenderer();
            return false;
        }

        if (enableExtraModule && checkTier > 0) {
            for (int i = 0; i < checkTier; i++) {
                if (isRenderActive) {
                    if (!checkPiece(
                        STRUCTURE_PIECE_MAIN_EXTRA_AIR,
                        HORIZONTAL_OFF_SET_EXTRA,
                        VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                        DEPTH_OFF_SET_EXTRA)) {
                        destroyRenderer();
                        mExtraModule = false;
                        break;
                    }
                    if (!checkPiece(
                        STRUCTURE_PIECE_MAIN_EXTRA_AIR,
                        HORIZONTAL_OFF_SET_EXTRA,
                        VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                        DEPTH_OFF_SET_EXTRA)) {
                        mExtraModule = false;
                        break;
                    }
                } else {
                    if (!checkPiece(
                        STRUCTURE_PIECE_MAIN_EXTRA,
                        HORIZONTAL_OFF_SET_EXTRA,
                        VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                        DEPTH_OFF_SET_EXTRA)) {
                        mExtraModule = false;
                        break;
                    }
                    if (!checkPiece(
                        STRUCTURE_PIECE_MAIN_EXTRA,
                        HORIZONTAL_OFF_SET_EXTRA,
                        VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                        DEPTH_OFF_SET_EXTRA)) {
                        mExtraModule = false;
                        break;
                    }
                }
                mExtraModule = true;
            }
        }
        mMachineTier = checkTier;

        if (enableExtraModule && !isRenderActive && !isRendererDisabled && mTotalRunTime > 0) {
            createRenderer();
        }
        return tCountCasing > 1;
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

        for (int i = 0; i < stackSize.stackSize; i++) {

            this.buildPiece(
                STRUCTURE_PIECE_MAIN_UP,
                stackSize,
                hintsOnly,
                HORIZONTAL_OFF_SET_UP,
                VERTICAL_OFF_SET_UP + i * 22,
                DEPTH_OFF_SET_UP);

            this.buildPiece(
                STRUCTURE_PIECE_MAIN_DOWN,
                stackSize,
                hintsOnly,
                HORIZONTAL_OFF_SET_DOWN,
                VERTICAL_OFF_SET_DOWN - i * 22,
                DEPTH_OFF_SET_DOWN);

            if (stackSize.stackSize > 1) {
                this.buildPiece(
                    STRUCTURE_PIECE_MAIN_EXTRA,
                    stackSize,
                    hintsOnly,
                    HORIZONTAL_OFF_SET_EXTRA,
                    VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                    DEPTH_OFF_SET_EXTRA);

                this.buildPiece(
                    STRUCTURE_PIECE_MAIN_EXTRA,
                    stackSize,
                    hintsOnly,
                    HORIZONTAL_OFF_SET_EXTRA,
                    VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                    DEPTH_OFF_SET_EXTRA);
            }
        }

        this.buildPiece(
            STRUCTURE_PIECE_MAIN_TOP,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET_TOP,
            VERTICAL_OFF_SET_TOP + (stackSize.stackSize - 1) * 22,
            DEPTH_OFF_SET_TOP);

        this.buildPiece(
            STRUCTURE_PIECE_MAIN_BOTTOM,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET_BOTTOM,
            VERTICAL_OFF_SET_BOTTOM - (stackSize.stackSize - 1) * 22,
            DEPTH_OFF_SET_BOTTOM);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;

        int built;

        built = this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        for (int i = 0; i < stackSize.stackSize; i++) {
            built = this.survivalBuildPiece(
                STRUCTURE_PIECE_MAIN_UP,
                stackSize,
                HORIZONTAL_OFF_SET_UP,
                VERTICAL_OFF_SET_UP + i * 22,
                DEPTH_OFF_SET_UP,
                elementBudget,
                env,
                false,
                true);

            if (built >= 0) return built;

            built = this.survivalBuildPiece(
                STRUCTURE_PIECE_MAIN_DOWN,
                stackSize,
                HORIZONTAL_OFF_SET_DOWN,
                VERTICAL_OFF_SET_DOWN - i * 22,
                DEPTH_OFF_SET_DOWN,
                elementBudget,
                env,
                false,
                true);

            if (built >= 0) return built;

            if (stackSize.stackSize > 1) {
                built = this.survivalBuildPiece(
                    STRUCTURE_PIECE_MAIN_EXTRA,
                    stackSize,
                    HORIZONTAL_OFF_SET_EXTRA,
                    VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                    DEPTH_OFF_SET_EXTRA,
                    elementBudget,
                    env,
                    false,
                    true);

                if (built >= 0) return built;

                built = this.survivalBuildPiece(
                    STRUCTURE_PIECE_MAIN_EXTRA,
                    stackSize,
                    HORIZONTAL_OFF_SET_EXTRA,
                    VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                    DEPTH_OFF_SET_EXTRA,
                    elementBudget,
                    env,
                    false,
                    true);

                if (built >= 0) return built;
            }
        }

        built = this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_TOP,
            stackSize,
            HORIZONTAL_OFF_SET_TOP,
            VERTICAL_OFF_SET_TOP + (stackSize.stackSize - 1) * 22,
            DEPTH_OFF_SET_TOP,
            elementBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        built = this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_BOTTOM,
            stackSize,
            HORIZONTAL_OFF_SET_BOTTOM,
            VERTICAL_OFF_SET_BOTTOM - (stackSize.stackSize - 1) * 22,
            DEPTH_OFF_SET_BOTTOM,
            elementBudget,
            env,
            false,
            true);

        return built;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    long ticker = 0;

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && getBaseMetaTileEntity().isAllowedToWork()) {
            ticker++;
            if (ticker % (5 * SECONDS) == 0) {
                startRecipeProcessing();

                if (!moduleHatches.isEmpty() && moduleHatches.size() <= mMachineTier * 4) {
                    for (EternalGregTechWorkshopModule module : moduleHatches) {
                        if (allowModuleConnection(module, this)) {
                            module.connect();
                            module.setEUtDiscount(getEUtDiscount());
                            module.setDurationModifier(getSpeedBoost());
                            module.setMaxParallel(getMaxParallelRecipes());
                            module.setMaxUseEUt(getMaxUseEUt());
                            module.setHeat(getHeatingCapacity());
                            EternalGregTechWorkshopModule.queryMilestoneStats(module, this);
                        } else {
                            module.disconnect();
                            module.setEUtDiscount(1);
                            module.setDurationModifier(1);
                            module.setMaxParallel(0);
                            module.setMaxParallel(0);
                            module.setMaxUseEUt(0);
                        }
                    }
                } else if (moduleHatches.size() > mMachineTier * 4) {
                    for (EternalGregTechWorkshopModule module : moduleHatches) {
                        module.disconnect();
                    }
                }

                drainFuel();
                determineCompositionMilestoneLevel();
                determineMilestoneProgress();
                endRecipeProcessing();
                if (mEfficiency < 0) mEfficiency = 0;
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    private final ArrayList<FluidStack> validFuelList = new ArrayList<>() {

        {
            add(MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(1));
            add(MaterialsUEVplus.RawStarMatter.getFluid(1));
            add(MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(1));
        }
    };

    private void drainFuel() {
        fuelConsumption = (long) Math.max(calculateFuelConsumption(this) * 5, 1);

        FluidStack fuelToDrain = new FluidStack(validFuelList.get(selectedFuelType), (int) fuelConsumption);
        for (MTEHatchInput hatch : filterValidMTEs(mInputHatches)) {
            FluidStack drained = hatch.drain(ForgeDirection.UNKNOWN, fuelToDrain, true);
            if (drained == null) {
                continue;
            }

            fuelToDrain.amount -= drained.amount;

            if (fuelToDrain.amount == 0) {
                totalFuelConsumed += getFuelFactor();
                return;
            }
        }
    }

    public static double calculateFuelConsumption(EternalGregTechWorkshop egtw) {
        double upgradeFactor = 1;
        if (egtw.isUpgradeActive(STEM)) {
            upgradeFactor = 0.8;
        }
        if (egtw.getFuelType() == 0) {
            return egtw.getFuelFactor() * 300 * Math.pow(1.15, egtw.getFuelFactor()) * upgradeFactor;
        }
        if (egtw.getFuelType() == 1) {
            return egtw.getFuelFactor() * 2 * Math.pow(1.08, egtw.getFuelFactor()) * upgradeFactor;
        } else return egtw.getFuelFactor() / 25f * upgradeFactor;
    }

    @Override
    public void onRemoval() {
        if (moduleHatches != null && !moduleHatches.isEmpty()) {
            for (EternalGregTechWorkshopModule module : moduleHatches) {
                module.disconnect();
            }
        }
        super.onRemoval();
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        if (isRenderActive) {
            destroyRenderer();
        }
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        if (getBaseMetaTileEntity().isAllowedToWork()) {
            if (addEUToGlobalEnergyMap(ownerUUID, -moduleHatches.size() * Integer.MAX_VALUE)) {
                setEUtDiscount(Math.pow(0.95, mModuleTier));
                setDurationModifier(Math.pow(0.9, mModuleTier));
                setMaxUseEUt((1L << Math.min(mModuleTier, 28)) * (Integer.MAX_VALUE * 10L));

                mEfficiencyIncrease = 10000;
                mMaxProgresstime = 128;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }

        mEUtDiscount = 1;
        mSpeedBoost = 1;
        mMaxUseEUt = 0;
        mEfficiencyIncrease = 0;
        mMaxProgresstime = 0;
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public static boolean allowModuleConnection(EternalGregTechWorkshopModule module, EternalGregTechWorkshop center) {

        if (module instanceof EternalGregTechWorkshopModule) {
            return true;
        }

        return false;
    }

    public boolean addModuleToMachineList(IGregTechTileEntity tileEntity, int baseCasingIndex) {
        if (tileEntity == null) {
            return false;
        }
        IMetaTileEntity metaTileEntity = tileEntity.getMetaTileEntity();
        if (metaTileEntity == null) {
            return false;
        }
        if (metaTileEntity instanceof EternalGregTechWorkshopModule module) {
            return moduleHatches.add(module);
        }
        return false;
    }

    public enum moduleElement implements IHatchElement<EternalGregTechWorkshop> {

        Module(EternalGregTechWorkshop::addModuleToMachineList, EternalGregTechWorkshopModule.class) {

            @Override
            public long count(EternalGregTechWorkshop tileEntity) {
                return tileEntity.moduleHatches.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<EternalGregTechWorkshop> adder;

        @SafeVarargs
        moduleElement(IGTHatchAdder<EternalGregTechWorkshop> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super EternalGregTechWorkshop> adder() {
            return adder;
        }
    }

    public int getFuelType() {
        return selectedFuelType;
    }

    private void setFuelType(int fuelType) {
        selectedFuelType = fuelType;
    }

    public int getFuelFactor() {
        return fuelConsumptionFactor;
    }

    public double getEUtDiscount() {
        return mEUtDiscount;
    }

    public void setEUtDiscount(double eutDiscount) {
        mEUtDiscount = eutDiscount;
    }

    public double getSpeedBoost() {
        return mSpeedBoost;
    }

    public void setDurationModifier(double speedBoost) {
        mSpeedBoost = speedBoost;
    }

    public int getHeatingCapacity() {
        return mHeatingCapacity;
    }

    public void setHeatingCapacity(int heatingCapacity) {
        mHeatingCapacity = heatingCapacity;
    }

    public long getMaxUseEUt() {
        return mMaxUseEUt;
    }

    public void setMaxUseEUt(long maxUseEUt) {
        mMaxUseEUt = maxUseEUt;
    }

    public boolean getEnableExtraModule() {
        return enableExtraModule;
    }

    public void setEnableExtraModule(boolean enable) {
        enableExtraModule = enable;
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
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean getDefaultInputSeparationMode() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean isRecipeLockingEnabled() {
        return false;
    }

    @Override
    public boolean isInputSeparationEnabled() {
        return false;
    }

    private void determineCompositionMilestoneLevel() {
        int[] uniqueModuleCount = new int[5];
        for (EternalGregTechWorkshopModule module : moduleHatches) {
            if (module instanceof EGTWFusionModule) {
                uniqueModuleCount[2] = 1;
                break;
            }
        }
        totalExtensionsBuilt = Arrays.stream(uniqueModuleCount)
            .sum();
        milestoneProgress[3] = (int) Math.floor(totalExtensionsBuilt);
    }

    private void determineMilestoneProgress() {
        if (milestoneProgress[0] < 7) {
            powerMilestonePercentage = (float) max(
                (Math.log((totalPowerConsumed.divide(BigInteger.valueOf(POWER_MILESTONE_CONSTANT))).longValue())
                    / POWER_LOG_CONSTANT + 1),
                0) / 7;
            milestoneProgress[0] = (int) floor(powerMilestonePercentage * 7);
        }

        if (milestoneProgress[1] < 7) {
            recipeMilestonePercentage = (float) max(
                (Math.log(totalRecipesProcessed * 1f / RECIPE_MILESTONE_CONSTANT) / RECIPE_LOG_CONSTANT + 1),
                0) / 7;
            milestoneProgress[1] = (int) floor(recipeMilestonePercentage * 7);
        }
        if (milestoneProgress[2] < 7) {
            fuelMilestonePercentage = (float) max(
                (Math.log(totalFuelConsumed * 1f / FUEL_MILESTONE_CONSTANT) / FUEL_LOG_CONSTANT + 1),
                0) / 7;
            milestoneProgress[2] = (int) floor(fuelMilestonePercentage * 7);
        }

        if (milestoneProgress[3] <= 7) {
            structureMilestonePercentage = totalExtensionsBuilt / 7f;
        }
    }

    private static final int FUEL_CONFIG_WINDOW_ID = 9;
    private static final int UPGRADE_TREE_WINDOW_ID = 10;
    private static final int INDIVIDUAL_UPGRADE_WINDOW_ID = 11;
    private static final int MILESTONE_WINDOW_ID = 12;
    private static final int INDIVIDUAL_MILESTONE_WINDOW_ID = 13;
    private static final int MANUAL_INSERTION_WINDOW_ID = 14;
    private static final int GENERAL_INFO_WINDOW_ID = 15;

    private int currentMilestoneID = 0;

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        buildContext.addSyncedWindow(FUEL_CONFIG_WINDOW_ID, this::createFuelConfigWindow);
        buildContext.addSyncedWindow(UPGRADE_TREE_WINDOW_ID, this::createUpgradeTreeWindow);
        buildContext.addSyncedWindow(INDIVIDUAL_UPGRADE_WINDOW_ID, this::createIndividualUpgradeWindow);
        buildContext.addSyncedWindow(MILESTONE_WINDOW_ID, this::createMilestoneWindow);
        buildContext.addSyncedWindow(GENERAL_INFO_WINDOW_ID, this::createGeneralInfoWindow);
        buildContext.addSyncedWindow(INDIVIDUAL_MILESTONE_WINDOW_ID, this::createIndividualMilestoneWindow);
        buildContext.addSyncedWindow(MANUAL_INSERTION_WINDOW_ID, this::createManualInsertionWindow);

        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.BACKGROUND_SCREEN_BLUE)
                .setPos(4, 4)
                .setSize(190, 85))
            .widget(
                TextWidget.dynamicText(this::machineTierHeaderText)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setPos(6, 8)
                    .setSize(185, 10))
            .widget(
                TextWidget.dynamicText(this::machineTier)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setPos(6, 20)
                    .setSize(185, 10))
            .widget(
                TextWidget.dynamicText(this::extraModuleHeaderText)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setPos(6, 32)
                    .setSize(185, 10))
            .widget(
                TextWidget.dynamicText(this::extraModuleState)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setPos(6, 44)
                    .setSize(185, 10))
            .widget(
                TextWidget.dynamicText(this::machineState)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setPos(6, 98)
                    .setSize(185, 10))
            .widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (!widget.isClient()) {
                    widget.getContext()
                        .openSyncedWindow(FUEL_CONFIG_WINDOW_ID);
                }
            })
                .setSize(16, 16)
                .setBackground(() -> {
                    List<UITexture> button = new ArrayList<>();
                    button.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                    button.add(TecTechUITextures.OVERLAY_BUTTON_HEAT_ON);
                    return button.toArray(new IDrawable[0]);
                })
                .addTooltip(translateToLocal("fog.button.fuelconfig.tooltip"))
                .setPos(174, 110)
                .setTooltipShowUpDelay(TOOLTIP_DELAY))
            .widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (isAllowedToWork()) {
                    disableWorking();
                } else {
                    enableWorking();
                }
            })
                .setPlayClickSoundResource(
                    () -> isAllowedToWork() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                        : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                .setBackground(() -> {
                    if (isAllowedToWork()) {
                        return new IDrawable[] { TecTechUITextures.BUTTON_CELESTIAL_32x32,
                            TecTechUITextures.OVERLAY_BUTTON_POWER_SWITCH_ON };
                    } else {
                        return new IDrawable[] { TecTechUITextures.BUTTON_CELESTIAL_32x32,
                            TecTechUITextures.OVERLAY_BUTTON_POWER_SWITCH_OFF };
                    }
                })
                .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::isAllowedToWork, val -> {
                    if (val) enableWorking();
                    else disableWorking();
                }), builder)
                .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.power_switch"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(174, 167)
                .setSize(16, 16))
            .widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (!widget.isClient()) {
                    widget.getContext()
                        .openSyncedWindow(MILESTONE_WINDOW_ID);
                }
            })
                .setSize(16, 16)
                .setBackground(() -> {
                    List<UITexture> button = new ArrayList<>();
                    button.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                    button.add(TecTechUITextures.OVERLAY_BUTTON_FLAG);
                    return button.toArray(new IDrawable[0]);

                })
                .addTooltip(translateToLocal("fog.button.milestones.tooltip"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(174, 91))
            .widget(
                new ButtonWidget().setOnClick(
                    (clickData, widget) -> {
                        if (!widget.isClient()) widget.getContext()
                            .openSyncedWindow(UPGRADE_TREE_WINDOW_ID);
                    })
                    .setSize(16, 16)
                    .setBackground(() -> {
                        List<UITexture> button = new ArrayList<>();
                        button.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                        button.add(TecTechUITextures.OVERLAY_BUTTON_ARROW_BLUE_UP);
                        return button.toArray(new IDrawable[0]);
                    })
                    .addTooltip(translateToLocal("fog.button.upgradetree.tooltip"))
                    .setPos(174, 129)
                    .setTooltipShowUpDelay(TOOLTIP_DELAY))
            .widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (!widget.isClient()) {
                    mMachine = checkMachine(this.getBaseMetaTileEntity(), null);
                }
                enableExtraModule = !enableExtraModule;
            })
                .setPlayClickSound(true)
                .setBackground(() -> {
                    List<UITexture> ret = new ArrayList<>();
                    ret.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_ARROW_BLUE_UP);
                    return ret.toArray(new IDrawable[0]);
                })
                .attachSyncer(
                    new FakeSyncWidget.BooleanSyncer(this::getEnableExtraModule, this::setEnableExtraModule),
                    builder)
                .addTooltip(StatCollector.translateToLocal("EGTW_EnableExtraModule"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(getStructureUpdateButtonPos())
                .setSize(16, 16))
            .widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (!widget.isClient()) {
                    mMachine = checkMachine(this.getBaseMetaTileEntity(), null);
                }
            })
                .setSize(16, 16)
                .setBackground(() -> {
                    List<UITexture> button = new ArrayList<>();
                    button.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                    button.add(TecTechUITextures.OVERLAY_CYCLIC_BLUE);
                    return button.toArray(new IDrawable[0]);
                })
                .addTooltip(translateToLocal("EGTW_UpdateStructureCheck"))
                .setPos(8, 91)
                .setTooltipShowUpDelay(TOOLTIP_DELAY));
    }

    private Text machineTier() {
        return new Text(GTUtility.formatNumbers(mMachineTier));
    }

    private Text machineTierHeaderText() {
        return new Text(translateToLocal("EGTW_MachineTier"));
    }

    private Text machineState() {
        if (mProgresstime > 0) {
            return new Text(translateToLocal("EGTW_MachineRunning"));
        }
        if (mMachine) {
            return new Text(translateToLocal("EGTW_MachineStandby"));
        }
        return new Text(translateToLocal("EGTW_MachineIncomplete"));
    }

    private Text extraModuleHeaderText() {
        if (enableExtraModule) {
            return new Text(translateToLocal("EGTW_ExtraModule"));
        }
        return new Text("");
    }

    private Text extraModuleState() {
        if (enableExtraModule) {
            if (mExtraModule) {
                return new Text(translateToLocal("EGTW_ExtraModule_On"));
            } else {
                return new Text(translateToLocal("EGTW_ExtraModule_Off"));
            }
        }
        return new Text("");
    }

    public boolean isUpgradeActive(EternalGregTechWorkshopUpgrade upgrade) {
        return upgrades.isUpgradeActive(upgrade);
    }

    private Text fuelUsage() {
        return new Text(fuelConsumption + " L/5s");
    }

    protected ModularWindow createGeneralInfoWindow(final EntityPlayer player) {
        return EternalGregTechWorkshopUI.createGeneralInfoWindow();
    }

    protected ModularWindow createFuelConfigWindow(final EntityPlayer player) {
        final int w = 78;
        final int h = 130;
        final int parentW = getGUIWidth();
        final int parentH = getGUIHeight();

        ModularWindow.Builder builder = ModularWindow.builder(w, h);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(parentW, parentH))
                .add(
                    Alignment.TopRight.getAlignedPos(new Size(parentW, parentH), new Size(w, h))
                        .add(w - 3, 0)));

        // Window header
        builder.widget(
            TextWidget.localised("gt.blockmachines.multimachine.FOG.fuelconsumption")
                .setPos(3, 2)
                .setSize(74, 34));

        // Fuel factor textbox
        NumericWidget fuelFactor = new NumericWidget();
        fuelFactor.setSetter(val -> fuelConsumptionFactor = (int) val)
            .setGetter(() -> fuelConsumptionFactor)
            .setBounds(1, calculateMaxFuelFactor(this))
            .setDefaultValue(1)
            .setScrollValues(1, 4, 64)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.normal)
            .setSize(70, 18)
            .setPos(4, 35)
            .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD);
        builder.widget(fuelFactor);

        // Syncers for max fuel factor
        builder.widget(
            upgrades.getSyncer(CFCE)
                .setOnClientUpdate($ -> fuelFactor.setMaxValue(calculateMaxFuelFactor(this))));
        builder.widget(
            upgrades.getSyncer(GEM)
                .setOnClientUpdate($ -> fuelFactor.setMaxValue(calculateMaxFuelFactor(this))));
        builder.widget(
            upgrades.getSyncer(TSE)
                .setOnClientUpdate($ -> fuelFactor.setMaxValue(calculateMaxFuelFactor(this))));

        builder.widget(
            new DrawableWidget().setDrawable(ModularUITextures.ICON_INFO)
                .setPos(64, 24)
                .setSize(10, 10)
                .addTooltip(translateToLocal("gt.blockmachines.multimachine.FOG.fuelinfo.0"))
                .addTooltip(translateToLocal("gt.blockmachines.multimachine.FOG.fuelinfo.1"))
                .addTooltip(translateToLocal("gt.blockmachines.multimachine.FOG.fuelinfo.2"))
                .addTooltip(translateToLocal("gt.blockmachines.multimachine.FOG.fuelinfo.3"))
                .addTooltip(translateToLocal("gt.blockmachines.multimachine.FOG.fuelinfo.4"))
                .addTooltip(translateToLocal("gt.blockmachines.multimachine.FOG.fuelinfo.5"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY))
            .widget(
                TextWidget.localised("gt.blockmachines.multimachine.FOG.fueltype")
                    .setPos(3, 57)
                    .setSize(74, 24))
            .widget(
                TextWidget.localised("gt.blockmachines.multimachine.FOG.fuelusage")
                    .setPos(3, 100)
                    .setSize(74, 20))
            .widget(
                TextWidget.dynamicText(this::fuelUsage)
                    .setPos(3, 115)
                    .setSize(74, 15))
            .widget(
                new MultiChildWidget().addChild(
                    new FluidNameHolderWidget(
                        () -> MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(1)
                            .getUnlocalizedName()
                            .substring(6),
                        (String) -> MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(1)
                            .getUnlocalizedName()) {

                        @Override
                        public void buildTooltip(List<Text> tooltip) {
                            FluidStack fluid = createFluidStack();
                            addFluidNameInfo(tooltip, fluid);
                            addAdditionalFluidInfo(tooltip, fluid);
                        }
                    }.setTooltipShowUpDelay(TOOLTIP_DELAY)
                        .setPos(1, 1)
                        .setSize(16, 16))
                    .addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
                        TecTech.proxy.playSound(getBaseMetaTileEntity(), "fx_click");
                        selectedFuelType = 0;
                    })
                        .setBackground(() -> {
                            if (selectedFuelType == 0) {
                                return new IDrawable[] { TecTechUITextures.SLOT_OUTLINE_GREEN };
                            } else {
                                return new IDrawable[] {};
                            }
                        })
                        .setSize(18, 18)
                        .attachSyncer(new FakeSyncWidget.IntegerSyncer(this::getFuelType, this::setFuelType), builder))

                    .setPos(6, 82)
                    .setSize(18, 18))
            .widget(
                new MultiChildWidget().addChild(
                    new FluidNameHolderWidget(
                        () -> MaterialsUEVplus.RawStarMatter.getFluid(1)
                            .getUnlocalizedName()
                            .substring(6),
                        (String) -> MaterialsUEVplus.RawStarMatter.getFluid(1)
                            .getUnlocalizedName()) {

                        @Override
                        public void buildTooltip(List<Text> tooltip) {
                            FluidStack fluid = createFluidStack();
                            addFluidNameInfo(tooltip, fluid);
                            addAdditionalFluidInfo(tooltip, fluid);
                        }
                    }.setTooltipShowUpDelay(TOOLTIP_DELAY)
                        .setPos(1, 1)
                        .setSize(16, 16))
                    .addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
                        TecTech.proxy.playSound(getBaseMetaTileEntity(), "fx_click");
                        selectedFuelType = 1;
                    })
                        .setBackground(() -> {
                            if (selectedFuelType == 1) {
                                return new IDrawable[] { TecTechUITextures.SLOT_OUTLINE_GREEN };
                            } else {
                                return new IDrawable[] {};
                            }
                        })
                        .setSize(18, 18))
                    .setPos(29, 82)
                    .setSize(18, 18))
            .widget(
                new MultiChildWidget().addChild(
                    new FluidNameHolderWidget(
                        () -> MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(1)
                            .getUnlocalizedName()
                            .substring(6),
                        (String) -> MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(1)
                            .getUnlocalizedName()) {

                        @Override
                        public void buildTooltip(List<Text> tooltip) {
                            FluidStack fluid = createFluidStack();
                            addFluidNameInfo(tooltip, fluid);
                            addAdditionalFluidInfo(tooltip, fluid);
                        }
                    }.setTooltipShowUpDelay(TOOLTIP_DELAY)
                        .setPos(1, 1)
                        .setSize(16, 16))
                    .addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
                        TecTech.proxy.playSound(getBaseMetaTileEntity(), "fx_click");
                        selectedFuelType = 2;
                    })
                        .setBackground(() -> {
                            if (selectedFuelType == 2) {
                                return new IDrawable[] { TecTechUITextures.SLOT_OUTLINE_GREEN };
                            } else {
                                return new IDrawable[] {};
                            }
                        })
                        .setSize(18, 18))
                    .setPos(52, 82)
                    .setSize(18, 18)
                    .attachSyncer(new FakeSyncWidget.IntegerSyncer(this::getFuelType, this::setFuelType), builder));

        return builder.build();
    }

    public static int calculateMaxFuelFactor(EternalGregTechWorkshop egtw) {
        int fuelCap = 5;
        if (egtw.isUpgradeActive(TSE)) {
            fuelCap = Integer.MAX_VALUE;
        } else {
            if (egtw.isUpgradeActive(GEM)) {
                fuelCap += egtw.getTotalActiveUpgrades();
            }
            if (egtw.isUpgradeActive(CFCE)) {
                fuelCap *= 1.2;
            }
        }
        return Math.max(fuelCap, 1);
    }

    public int getTotalActiveUpgrades() {
        return upgrades.getTotalActiveUpgrades();
    }

    protected ModularWindow createManualInsertionWindow(final EntityPlayer player) {
        EternalGregTechWorkshopUpgrade upgrade = currentUpgradeWindow;
        ItemStack[] inputs = upgrade.getExtraCost();
        final int WIDTH = 261;
        final int HEIGHT = 106;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();

        for (int i = 0; i < 16; i++) {
            inputSlotHandler.insertItem(i, storedUpgradeWindowItems[i], false);
            storedUpgradeWindowItems[i] = null;
        }

        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT)))
                .subtract(5, 0)
                .add(0, 4));
        builder.widget(upgrades.getSyncer(upgrade));
        builder.widget(
            SlotGroup.ofItemHandler(inputSlotHandler, 4)
                .startFromSlot(0)
                .endAtSlot(15)
                .phantom(false)
                .background(getGUITextureSet().getItemSlot())
                .build()
                .setPos(184, 6));
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getWindow()
                    .closeWindow();
                EternalGregTechWorkshopUI.reopenWindow(widget, UPGRADE_TREE_WINDOW_ID);
                EternalGregTechWorkshopUI.reopenWindow(widget, INDIVIDUAL_UPGRADE_WINDOW_ID);
            }
        })
            .setBackground(ModularUITextures.VANILLA_BACKGROUND, new Text("x"))
            .setPos(251, 0)
            .setSize(10, 10));
        builder.widget(new MultiChildWidget().addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                upgrades.payCost(upgrade, inputSlotHandler);
                EternalGregTechWorkshopUI.reopenWindow(widget, MANUAL_INSERTION_WINDOW_ID);
            }
        })
            .setPlayClickSound(true)
            .setBackground(GTUITextures.BUTTON_STANDARD)
            .setSize(251, 18))
            .addChild(
                new TextWidget(translateToLocal("gt.blockmachines.multimachine.FOG.consumeUpgradeMats"))
                    .setTextAlignment(Alignment.Center)
                    .setScale(0.75f)
                    .setPos(0, 1)
                    .setSize(251, 18))
            .setPos(5, 82)
            .setSize(251, 16));

        for (int i = 0; i < 20; i++) {
            final int ii = i;
            ItemStack stack = null;
            if (i < inputs.length) {
                stack = inputs[i];
            }
            Widget costWidget = EternalGregTechWorkshopUI
                .createExtraCostWidget(stack, () -> upgrades.getPaidCosts(upgrade)[ii]);
            costWidget.setPos(5 + (36 * (i % 5)), 6 + (18 * (i / 5)));
            builder.widget(costWidget);
        }

        return builder.build();
    }

    private Text inversionStatusText() {
        String inversionStatus = "";
        return new Text(inversionStatus);
    }

    protected ModularWindow createIndividualMilestoneWindow(final EntityPlayer player) {
        final int w = 150;
        final int h = 150;
        final MilestoneIcon icon = MilestoneIcon.VALUES[currentMilestoneID];
        final Size iconSize = icon.getSize();

        ModularWindow.Builder builder = ModularWindow.builder(w, h);
        builder.setBackground(TecTechUITextures.BACKGROUND_GLOW_WHITE);
        builder.setDraggable(true);

        builder.widget(
            ButtonWidget.closeWindowButton(true)
                .setPos(134, 4))
            .widget(
                new DrawableWidget().setDrawable(icon.getSymbol())
                    .setSize(iconSize)
                    .setPos((w - iconSize.width) / 2, (h - iconSize.height) / 2))
            .widget(
                new TextWidget(icon.getNameText()).setDefaultColor(EnumChatFormatting.GOLD)
                    .setTextAlignment(Alignment.Center)
                    .setPos(0, 8)
                    .setSize(150, 15))
            .widget(
                TextWidget.dynamicText(this::inversionStatusText)
                    .setDefaultColor(EnumChatFormatting.AQUA)
                    .setTextAlignment(Alignment.Center)
                    .setScale(0.8f)
                    .setPos(0, 120)
                    .setSize(150, 15))
            .widget(
                TextWidget.dynamicText(() -> totalMilestoneProgress(currentMilestoneID))
                    .setScale(0.7f)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setTextAlignment(Alignment.Center)
                    .setPos(5, 30)
                    .setSize(140, 30))
            .widget(
                TextWidget.dynamicText(() -> currentMilestoneLevel(currentMilestoneID))
                    .setScale(0.7f)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setTextAlignment(Alignment.Center)
                    .setPos(5, 50)
                    .setSize(140, 30))
            .widget(
                TextWidget.dynamicText(() -> milestoneProgressText(currentMilestoneID))
                    .setScale(0.7f)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setSize(140, 30)
                    .setPos(5, 70))
            .widget(
                TextWidget.dynamicText(() -> gravitonShardAmountText(currentMilestoneID))
                    .setScale(0.7f)
                    .setDefaultColor(EnumChatFormatting.WHITE)
                    .setSize(140, 30)
                    .setPos(5, 90))
            .widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                TecTech.proxy.playSound(getBaseMetaTileEntity(), "fx_click");
                if (clickData.mouseButton == 0) {
                    formattingMode = formattingMode.cycle();
                }
            })
                .setSize(10, 10)
                .addTooltip(translateToLocal("fog.button.formatting.tooltip"))
                .setBackground(TecTechUITextures.OVERLAY_CYCLIC_BLUE)
                .setPos(5, 135)
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .attachSyncer(
                    new FakeSyncWidget.ByteSyncer(
                        () -> (byte) formattingMode.ordinal(),
                        val -> formattingMode = MilestoneFormatter.VALUES[MathHelper
                            .clamp_int(val, 0, MilestoneFormatter.VALUES.length - 1)]),
                    builder));

        return builder.build();
    }

    private final int[] milestoneProgress = new int[] { 0, 0, 0, 0 };

    private Text gravitonShardAmountText(int milestoneID) {
        int sum;
        int progress = milestoneProgress[milestoneID];
        sum = progress * (progress + 1) / 2;
        return new Text(
            translateToLocal("gt.blockmachines.multimachine.FOG.shardgain") + ": " + EnumChatFormatting.GRAY + sum);
    }

    private Text totalMilestoneProgress(int milestoneID) {
        Number progress;
        String suffix;
        switch (milestoneID) {
            case 0 -> {
                suffix = translateToLocal("gt.blockmachines.multimachine.FOG.power");
                progress = totalPowerConsumed;
            }
            case 1 -> {
                suffix = translateToLocal("gt.blockmachines.multimachine.FOG.recipes");
                progress = totalRecipesProcessed;
            }
            case 2 -> {
                suffix = translateToLocal("gt.blockmachines.multimachine.FOG.fuelconsumed");
                progress = totalFuelConsumed;
            }
            case 3 -> {
                suffix = translateToLocal("gt.blockmachines.multimachine.FOG.extensions");
                progress = milestoneProgress[3];
            }
            default -> throw new IllegalArgumentException("Invalid Milestone ID");
        }
        return new Text(
            translateToLocal("gt.blockmachines.multimachine.FOG.totalprogress") + ": "
                + EnumChatFormatting.GRAY
                + formattingMode.format(progress)
                + " "
                + suffix);
    }

    private Text currentMilestoneLevel(int milestoneID) {
        int milestoneLevel = Math.min(milestoneProgress[milestoneID], 7);
        return new Text(
            translateToLocal("gt.blockmachines.multimachine.FOG.milestoneprogress") + ": "
                + EnumChatFormatting.GRAY
                + milestoneLevel);
    }

    private Text milestoneProgressText(int milestoneID) {
        Number max;
        String suffix;
        String progressText = translateToLocal("gt.blockmachines.multimachine.FOG.progress");
        Text done = new Text(
            translateToLocal("gt.blockmachines.multimachine.FOG.milestonecomplete")
                + (formattingMode != DEFAULT_FORMATTING_MODE ? EnumChatFormatting.DARK_RED + "?" : ""));

        if (milestoneProgress[milestoneID] >= 7) {
            return done;
        }

        switch (milestoneID) {
            case 0 -> {
                suffix = translateToLocal("gt.blockmachines.multimachine.FOG.power");
                max = BigInteger.valueOf(LongMath.pow(9, milestoneProgress[0]))
                    .multiply(BigInteger.valueOf(LongMath.pow(10, 15)));

            }
            case 1 -> {
                suffix = translateToLocal("gt.blockmachines.multimachine.FOG.recipes");
                max = LongMath.pow(4, milestoneProgress[1]) * LongMath.pow(10, 7);
            }
            case 2 -> {
                suffix = translateToLocal("gt.blockmachines.multimachine.FOG.fuelconsumed");

                max = LongMath.pow(3, milestoneProgress[2]) * LongMath.pow(10, 4);

            }
            case 3 -> {
                suffix = translateToLocal("gt.blockmachines.multimachine.FOG.extensions");
                max = milestoneProgress[3] + 1;
            }
            default -> throw new IllegalArgumentException("Invalid Milestone ID");
        }
        return new Text(progressText + ": " + EnumChatFormatting.GRAY + formattingMode.format(max) + " " + suffix);
    }

    private Widget createMilestoneButton(int milestoneID, int width, int height, Pos2d pos) {
        return new ButtonWidget().setOnClick((clickData, widget) -> {
            currentMilestoneID = milestoneID;
            EternalGregTechWorkshopUI.reopenWindow(widget, INDIVIDUAL_MILESTONE_WINDOW_ID);
        })
            .setSize(width, height)
            .setBackground(() -> switch (milestoneID) {
            case 1 -> new IDrawable[] { TecTechUITextures.PICTURE_GODFORGE_MILESTONE_CONVERSION_GLOW };
            case 2 -> new IDrawable[] { TecTechUITextures.PICTURE_GODFORGE_MILESTONE_CATALYST_GLOW };
            case 3 -> new IDrawable[] { TecTechUITextures.PICTURE_GODFORGE_MILESTONE_COMPOSITION_GLOW };
            default -> new IDrawable[] { TecTechUITextures.PICTURE_GODFORGE_MILESTONE_CHARGE_GLOW };
            })
            .addTooltip(translateToLocal("gt.blockmachines.multimachine.FOG.milestoneinfo"))
            .setPos(pos)
            .setTooltipShowUpDelay(TOOLTIP_DELAY);
    }

    protected ModularWindow createMilestoneWindow(final EntityPlayer player) {
        final int WIDTH = 400;
        final int HEIGHT = 300;
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(TecTechUITextures.BACKGROUND_SPACE);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.widget(createMilestoneButton(0, 80, 100, new Pos2d(62, 24)));
        builder.widget(createMilestoneButton(1, 70, 98, new Pos2d(263, 25)));
        builder.widget(createMilestoneButton(2, 100, 100, new Pos2d(52, 169)));
        builder.widget(createMilestoneButton(3, 100, 100, new Pos2d(248, 169)));
        builder.widget(
            TextWidget.localised("gt.blockmachines.multimachine.FOG.powermilestone")
                .setDefaultColor(EnumChatFormatting.GOLD)
                .setPos(77, 45)
                .setSize(50, 30));
        builder.widget(
            TextWidget.localised("gt.blockmachines.multimachine.FOG.recipemilestone")
                .setDefaultColor(EnumChatFormatting.GOLD)
                .setPos(268, 45)
                .setSize(60, 30));
        builder.widget(
            TextWidget.localised("gt.blockmachines.multimachine.FOG.fuelmilestone")
                .setDefaultColor(EnumChatFormatting.GOLD)
                .setPos(77, 190)
                .setSize(50, 30));
        builder.widget(
            TextWidget.localised("gt.blockmachines.multimachine.FOG.purchasablemilestone")
                .setDefaultColor(EnumChatFormatting.GOLD)
                .setPos(268, 190)
                .setSize(60, 30));
        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.PROGRESSBAR_GODFORGE_MILESTONE_BACKGROUND)
                .setPos(37, 70)
                .setSize(130, 7))
            .widget(
                new DrawableWidget().setDrawable(TecTechUITextures.PROGRESSBAR_GODFORGE_MILESTONE_BACKGROUND)
                    .setPos(233, 70)
                    .setSize(130, 7))
            .widget(
                new DrawableWidget().setDrawable(TecTechUITextures.PROGRESSBAR_GODFORGE_MILESTONE_BACKGROUND)
                    .setPos(37, 215)
                    .setSize(130, 7))
            .widget(
                new DrawableWidget().setDrawable(TecTechUITextures.PROGRESSBAR_GODFORGE_MILESTONE_BACKGROUND)
                    .setPos(233, 215)
                    .setSize(130, 7));
        builder.widget(
            new ProgressBar().setProgress(() -> powerMilestonePercentage)
                .setDirection(ProgressBar.Direction.RIGHT)
                .setTexture(TecTechUITextures.PROGRESSBAR_GODFORGE_MILESTONE_RED, 130)
                .setSynced(true, false)
                .setSize(130, 7)
                .setPos(37, 70))
            .widget(
                new ProgressBar().setProgress(() -> recipeMilestonePercentage)
                    .setDirection(ProgressBar.Direction.RIGHT)
                    .setTexture(TecTechUITextures.PROGRESSBAR_GODFORGE_MILESTONE_PURPLE, 130)
                    .setSynced(true, false)
                    .setSize(130, 7)
                    .setPos(233, 70))
            .widget(
                new ProgressBar().setProgress(() -> fuelMilestonePercentage)
                    .setDirection(ProgressBar.Direction.RIGHT)
                    .setTexture(TecTechUITextures.PROGRESSBAR_GODFORGE_MILESTONE_BLUE, 130)
                    .setSynced(true, false)
                    .setSize(130, 7)
                    .setPos(37, 215))
            .widget(
                new ProgressBar().setProgress(() -> structureMilestonePercentage)
                    .setDirection(ProgressBar.Direction.RIGHT)
                    .setTexture(TecTechUITextures.PROGRESSBAR_GODFORGE_MILESTONE_RAINBOW, 130)
                    .setSynced(true, false)
                    .setSize(130, 7)
                    .setPos(233, 215))
            .widget(
                ButtonWidget.closeWindowButton(true)
                    .setOnClick((data, widget) -> {
                        if (!widget.isClient()) {
                            widget.getWindow()
                                .closeWindow();
                            EternalGregTechWorkshopUI.closeWindow(widget, INDIVIDUAL_MILESTONE_WINDOW_ID);
                        }
                    })
                    .setPos(382, 6));
        return builder.build();
    }

    private Widget createUpgradeConnectorLine(Pos2d pos, int length, float rotationAngle, UpgradeColor color,
        EternalGregTechWorkshopUpgrade startUpgrade, EternalGregTechWorkshopUpgrade endUpgrade) {
        return new DrawableWidget().setDrawable(() -> {
            UITexture texture = color.getConnector();
            if (isUpgradeActive(startUpgrade) && isUpgradeActive(endUpgrade)) {
                texture = color.getOpaqueConnector();
            }
            return texture.withRotationDegree(rotationAngle);
        })
            .setPos(pos)
            .setSize(6, length);
    }

    private void completeUpgrade(EternalGregTechWorkshopUpgrade upgrade) {
        if (isUpgradeActive(upgrade)) return;
        if (!upgrades.checkPrerequisites(upgrade)) return;
        if (!upgrades.checkCost(upgrade, gravitonShardsAvailable)) return;

        upgrades.unlockUpgrade(upgrade);
        gravitonShardsAvailable -= upgrade.getShardCost();
        gravitonShardsSpent += upgrade.getShardCost();
    }

    private void respecUpgrade(EternalGregTechWorkshopUpgrade upgrade) {
        if (!isUpgradeActive(upgrade)) return;
        if (!upgrades.checkDependents(upgrade)) return;

        upgrades.respecUpgrade(upgrade);
        gravitonShardsAvailable += upgrade.getShardCost();
        gravitonShardsSpent -= upgrade.getShardCost();

        if (upgrade == END) {
            gravitonShardEjection = false;
        }
    }

    private Widget createUpgradeBox(EternalGregTechWorkshopUpgrade upgrade, IWidgetBuilder<?> builder) {
        return new MultiChildWidget().addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
            currentUpgradeWindow = upgrade;
            if (clickData.mouseButton == 0) {
                if (clickData.shift) {
                    if (!upgrade.hasExtraCost() || upgrades.isCostPaid(upgrade)) {
                        completeUpgrade(upgrade);
                    } else {
                        EternalGregTechWorkshopUI.reopenWindow(widget, MANUAL_INSERTION_WINDOW_ID);
                        EternalGregTechWorkshopUI.closeWindow(widget, INDIVIDUAL_UPGRADE_WINDOW_ID);
                        EternalGregTechWorkshopUI.closeWindow(widget, UPGRADE_TREE_WINDOW_ID);
                    }
                } else {
                    EternalGregTechWorkshopUI.reopenWindow(widget, INDIVIDUAL_UPGRADE_WINDOW_ID);
                }
            } else if (clickData.mouseButton == 1) {
                respecUpgrade(upgrade);
            }
        })
            .setSize(40, 15)
            .setBackground(() -> {
                if (isUpgradeActive(upgrade)) {
                    return new IDrawable[] { TecTechUITextures.BUTTON_SPACE_PRESSED_32x16 };
                } else {
                    return new IDrawable[] { TecTechUITextures.BUTTON_SPACE_32x16 };
                }
            })
            .addTooltip(upgrade.getNameText())
            .setTooltipShowUpDelay(TOOLTIP_DELAY))
            .addChild(
                new TextWidget(upgrade.getShortNameText()).setScale(0.8f)
                    .setDefaultColor(EnumChatFormatting.GOLD)
                    .setTextAlignment(Alignment.Center)
                    .setSize(34, 9)
                    .setPos(3, 4))
            .setPos(upgrade.getTreePos())
            .attachSyncer(upgrades.getSyncer(upgrade), builder);
    }

    protected ModularWindow createIndividualUpgradeWindow(final EntityPlayer player) {
        EternalGregTechWorkshopUpgrade upgrade = currentUpgradeWindow;

        ModularWindow.Builder builder = ModularWindow.builder(upgrade.getWindowSize());
        builder.setBackground(upgrade.getBackground());

        // Syncers
        builder.widget(
            new FakeSyncWidget.IntegerSyncer(() -> gravitonShardsAvailable, val -> gravitonShardsAvailable = val));
        builder.widget(upgrades.getSyncer(upgrade));

        builder.widget(
            EternalGregTechWorkshopUI.getIndividualUpgradeGroup(
                upgrade,
                () -> gravitonShardsAvailable,
                () -> completeUpgrade(upgrade),
                () -> respecUpgrade(upgrade),
                () -> isUpgradeActive(upgrade)));

        if (upgrade.hasExtraCost()) {
            builder.widget(
                EternalGregTechWorkshopUI
                    .createMaterialInputButton(upgrade, () -> upgrades.isCostPaid(upgrade), (clickData, widget) -> {
                        EternalGregTechWorkshopUI.reopenWindow(widget, MANUAL_INSERTION_WINDOW_ID);
                        EternalGregTechWorkshopUI.closeWindow(widget, INDIVIDUAL_UPGRADE_WINDOW_ID);
                        EternalGregTechWorkshopUI.closeWindow(widget, UPGRADE_TREE_WINDOW_ID);
                    }));
        }

        return builder.build();
    }

    protected ModularWindow createUpgradeTreeWindow(final EntityPlayer player) {
        ModularWindow.Builder builder = ModularWindow.builder(300, 300);
        Scrollable scrollable = new Scrollable().setVerticalScroll();

        // spotless:off
        scrollable
            .widget(createUpgradeConnectorLine(new Pos2d(143, 71),  45,  0,      UpgradeColor.BLUE,   START, IGCC))
            .widget(createUpgradeConnectorLine(new Pos2d(124, 124), 60,  27,     UpgradeColor.BLUE,   IGCC,  STEM))
            .widget(createUpgradeConnectorLine(new Pos2d(162, 124), 60,  333,    UpgradeColor.BLUE,   IGCC,  CFCE))
            .widget(createUpgradeConnectorLine(new Pos2d(94,  184), 60,  27,     UpgradeColor.BLUE,   STEM,  GISS))
            .widget(createUpgradeConnectorLine(new Pos2d(130, 184), 60,  336,    UpgradeColor.BLUE,   STEM,  FDIM))
            .widget(createUpgradeConnectorLine(new Pos2d(156, 184), 60,  24,     UpgradeColor.BLUE,   CFCE,  FDIM))
            .widget(createUpgradeConnectorLine(new Pos2d(192, 184), 60,  333,    UpgradeColor.BLUE,   CFCE,  SA))
            .widget(createUpgradeConnectorLine(new Pos2d(143, 251), 45,  0,      UpgradeColor.BLUE,   FDIM,  GPCI))
            .widget(createUpgradeConnectorLine(new Pos2d(143, 311), 45,  0,      UpgradeColor.BLUE,   GPCI,  GEM))
            .widget(createUpgradeConnectorLine(new Pos2d(78,  250), 110, 5,      UpgradeColor.RED,    GISS,  REC))
            .widget(createUpgradeConnectorLine(new Pos2d(110, 290), 80,  40,     UpgradeColor.RED,    GPCI,  REC))
            .widget(createUpgradeConnectorLine(new Pos2d(208, 250), 110, 355,    UpgradeColor.RED,    SA,    CTCDD))
            .widget(createUpgradeConnectorLine(new Pos2d(176, 290), 80,  320,    UpgradeColor.RED,    GPCI,  CTCDD))
            .widget(createUpgradeConnectorLine(new Pos2d(100, 355), 80,  313,    UpgradeColor.BLUE,   REC,   QGPIU))
            .widget(createUpgradeConnectorLine(new Pos2d(186, 355), 80,  47,     UpgradeColor.BLUE,   CTCDD, QGPIU))
            .widget(createUpgradeConnectorLine(new Pos2d(143, 430), 48,  0,      UpgradeColor.ORANGE, QGPIU, TCT))
            .widget(createUpgradeConnectorLine(new Pos2d(143, 490), 48,  0,      UpgradeColor.ORANGE, TCT,   EPEC))
            .widget(createUpgradeConnectorLine(new Pos2d(143, 550), 48,  0,      UpgradeColor.ORANGE, EPEC,  POS))
            .widget(createUpgradeConnectorLine(new Pos2d(143, 610), 48,  0,      UpgradeColor.ORANGE, POS,   NGMS))
            .widget(createUpgradeConnectorLine(new Pos2d(110, 410), 80,  40,     UpgradeColor.PURPLE, QGPIU, SEFCP))
            .widget(createUpgradeConnectorLine(new Pos2d(83,  490), 48,  0,      UpgradeColor.PURPLE, SEFCP, CNTI))
            .widget(createUpgradeConnectorLine(new Pos2d(83,  550), 48,  0,      UpgradeColor.PURPLE, CNTI,  NDPE))
            .widget(createUpgradeConnectorLine(new Pos2d(101, 590), 80,  320,    UpgradeColor.PURPLE, NDPE,  NGMS))
            .widget(createUpgradeConnectorLine(new Pos2d(53,  536), 35,  45,     UpgradeColor.PURPLE, CNTI,  DOP))
            .widget(createUpgradeConnectorLine(new Pos2d(176, 410), 80,  320,    UpgradeColor.GREEN,  QGPIU, GGEBE))
            .widget(createUpgradeConnectorLine(new Pos2d(203, 490), 48,  0,      UpgradeColor.GREEN,  GGEBE, IMKG))
            .widget(createUpgradeConnectorLine(new Pos2d(203, 550), 48,  0,      UpgradeColor.GREEN,  IMKG,  DOR))
            .widget(createUpgradeConnectorLine(new Pos2d(185, 590), 80,  40,     UpgradeColor.GREEN,  DOR,   NGMS))
            .widget(createUpgradeConnectorLine(new Pos2d(233, 476), 35,  315,    UpgradeColor.GREEN,  GGEBE, TPTP))
            .widget(createUpgradeConnectorLine(new Pos2d(143, 670), 48,  0,      UpgradeColor.BLUE,   NGMS,  SEDS))
            .widget(createUpgradeConnectorLine(new Pos2d(101, 707), 75,  62.3f,  UpgradeColor.BLUE,   SEDS,  PA))
            .widget(createUpgradeConnectorLine(new Pos2d(53,  772), 78,  0,      UpgradeColor.BLUE,   PA,    CD))
            .widget(createUpgradeConnectorLine(new Pos2d(95,  837), 75,  297.7f, UpgradeColor.BLUE,   CD,    TSE))
            .widget(createUpgradeConnectorLine(new Pos2d(191, 837), 75,  62.3f,  UpgradeColor.BLUE,   TSE,   TBF))
            .widget(createUpgradeConnectorLine(new Pos2d(233, 772), 78,  0,      UpgradeColor.BLUE,   TBF,   EE))
            .widget(createUpgradeConnectorLine(new Pos2d(191, 747), 75,  62.3f,  UpgradeColor.BLUE,   EE,    END));
        // spotless:on

        for (EternalGregTechWorkshopUpgrade upgrade : upgrades.getAllUpgrades()) {
            scrollable.widget(createUpgradeBox(upgrade, scrollable));
        }

        scrollable.widget(
            new MultiChildWidget().addChild(
                new ButtonWidget().setOnClick(((clickData, widget) -> secretUpgrade = !secretUpgrade))
                    .setSize(40, 15)
                    .setBackground(() -> {
                        if (secretUpgrade) {
                            return new IDrawable[] { TecTechUITextures.BUTTON_SPACE_PRESSED_32x16 };
                        }
                        return new IDrawable[0];
                    })
                    .addTooltip(translateToLocal("fog.upgrade.tt.secret"))
                    .setTooltipShowUpDelay(20))
                .addChild(
                    new TextWidget(translateToLocal("fog.upgrade.tt.short.secret")).setScale(0.8f)
                        .setDefaultColor(EnumChatFormatting.GOLD)
                        .setTextAlignment(Alignment.Center)
                        .setSize(34, 9)
                        .setPos(3, 4)
                        .setEnabled((widget -> secretUpgrade)))
                .addChild(
                    new DrawableWidget().setDrawable(TecTechUITextures.PICTURE_UPGRADE_CONNECTOR_BLUE_OPAQUE)
                        .setEnabled(widget -> secretUpgrade)
                        .setPos(40, 4)
                        .setSize(20, 6))
                .setPos(new Pos2d(66, 56)))
            .widget(new TextWidget("").setPos(0, 945));

        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.BACKGROUND_STAR)
                .setPos(0, 0)
                .setSize(300, 300))
            .widget(
                scrollable.setSize(292, 292)
                    .setPos(4, 4))
            .widget(
                ButtonWidget.closeWindowButton(true)
                    .setOnClick((data, widget) -> {
                        if (!widget.isClient()) {
                            widget.getWindow()
                                .closeWindow();
                            EternalGregTechWorkshopUI.closeWindow(widget, INDIVIDUAL_UPGRADE_WINDOW_ID);
                        }
                    })
                    .setPos(282, 4));
        if (MainConfig.enableDebugMode) {
            builder.widget(
                new MultiChildWidget()
                    .addChild(
                        new ButtonWidget().setOnClick((clickData, widget) -> upgrades.resetAll())
                            .setSize(40, 15)
                            .setBackground(GTUITextures.BUTTON_STANDARD)
                            .addTooltip(translateToLocal("fog.debug.resetbutton.tooltip"))
                            .setTooltipShowUpDelay(TOOLTIP_DELAY))
                    .addChild(
                        new TextWidget(translateToLocal("fog.debug.resetbutton.text"))
                            .setTextAlignment(Alignment.Center)
                            .setScale(0.57f)
                            .setMaxWidth(36)
                            .setPos(3, 3))
                    .addChild(
                        new NumericWidget().setSetter(val -> gravitonShardsAvailable = (int) val)
                            .setGetter(() -> gravitonShardsAvailable)
                            .setBounds(0, 112)
                            .setDefaultValue(0)
                            .setScrollValues(1, 4, 64)
                            .setTextAlignment(Alignment.Center)
                            .setTextColor(Color.WHITE.normal)
                            .setSize(25, 18)
                            .setPos(4, 16)
                            .addTooltip(translateToLocal("fog.debug.gravitonshardsetter.tooltip"))
                            .setTooltipShowUpDelay(TOOLTIP_DELAY)
                            .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD))
                    .addChild(
                        new ButtonWidget().setOnClick((clickData, widget) -> upgrades.unlockAll())
                            .setSize(40, 15)
                            .setBackground(GTUITextures.BUTTON_STANDARD)
                            .addTooltip(translateToLocal("fog.debug.unlockall.text"))
                            .setTooltipShowUpDelay(TOOLTIP_DELAY)
                            .setPos(0, 35))
                    .addChild(
                        new TextWidget(translateToLocal("fog.debug.unlockall.text")).setTextAlignment(Alignment.Center)
                            .setScale(0.57f)
                            .setMaxWidth(36)
                            .setPos(3, 38))
                    .setPos(4, 4));

        }
        return builder.build();
    }

    private void destroyExtraModule() {
        for (int i = 0; i < mMachineTier; i++) {
            buildPiece(
                STRUCTURE_PIECE_MAIN_EXTRA_AIR,
                null,
                false,
                HORIZONTAL_OFF_SET_EXTRA,
                VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                DEPTH_OFF_SET_EXTRA);
            buildPiece(
                STRUCTURE_PIECE_MAIN_EXTRA_AIR,
                null,
                false,
                HORIZONTAL_OFF_SET_EXTRA,
                VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                DEPTH_OFF_SET_EXTRA);
            mExtraModule = true;
        }

    }

    private void buildExtraModule() {
        for (int i = 0; i < mMachineTier; i++) {
            buildPiece(
                STRUCTURE_PIECE_MAIN_EXTRA,
                null,
                false,
                HORIZONTAL_OFF_SET_EXTRA,
                VERTICAL_OFF_SET_EXTRA_UP + i * 22,
                DEPTH_OFF_SET_EXTRA);
            buildPiece(
                STRUCTURE_PIECE_MAIN_EXTRA,
                null,
                false,
                HORIZONTAL_OFF_SET_EXTRA,
                VERTICAL_OFF_SET_EXTRA_DOWN - i * 22,
                DEPTH_OFF_SET_EXTRA);
            mExtraModule = true;
        }
    }

    private void destroyRenderer() {
        ChunkCoordinates renderPos = getRenderPos();
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, Blocks.air);
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, GregTechAPI.sBlockCasings1, 14, 2);

        buildExtraModule();

        isRenderActive = false;
        disableWorking();
    }

    private void createRenderer() {
        ChunkCoordinates renderPos = getRenderPos();

        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, Blocks.air);
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, BlockLoader.BlockEternalGregTechWorkshopRender);
        TileEntityEternalGregTechWorkshop rendererTileEntity = (TileEntityEternalGregTechWorkshop) this
            .getBaseMetaTileEntity()
            .getWorld()
            .getTileEntity(renderPos.posX, renderPos.posY, renderPos.posZ);

        destroyExtraModule();

        rendererTileEntity.setRenderRotation(getRotation(), getDirection());
        updateRenderer();

        isRenderActive = true;
        enableWorking();
    }

    private ChunkCoordinates getRenderPos() {
        IGregTechTileEntity gregTechTileEntity = this.getBaseMetaTileEntity();
        int x = gregTechTileEntity.getXCoord();
        int y = gregTechTileEntity.getYCoord();
        int z = gregTechTileEntity.getZCoord();
        double xOffset = 26 * getExtendedFacing().getRelativeBackInWorld().offsetX;
        double yOffset = 26 * getExtendedFacing().getRelativeBackInWorld().offsetY;
        double zOffset = 26 * getExtendedFacing().getRelativeBackInWorld().offsetZ;
        return new ChunkCoordinates((int) (x + xOffset), (int) (y + yOffset), (int) (z + zOffset));
    }

    private TileEntityEternalGregTechWorkshop getRenderer() {
        ChunkCoordinates renderPos = getRenderPos();
        TileEntity tile = this.getBaseMetaTileEntity()
            .getWorld()
            .getTileEntity(renderPos.posX, renderPos.posY, renderPos.posZ);

        if (tile instanceof TileEntityEternalGregTechWorkshop egtwTile) {
            return egtwTile;
        }
        return null;
    }

    private void updateRenderer() {
        TileEntityEternalGregTechWorkshop tile = getRenderer();
        if (tile == null) return;
        tile.setRenderCount(mMachineTier);
        tile.updateToClient();
    }
}
