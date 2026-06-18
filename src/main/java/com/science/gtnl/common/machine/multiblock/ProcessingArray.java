package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.machine.ProcessingArrayManager;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.GTMod;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTETieredMachineBlock;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.recipe.metadata.CompressionTierKey;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.ItemMachines;
import gregtech.common.misc.GTStructureChannels;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class ProcessingArray extends MultiMachineBase<ProcessingArray> implements ISurvivalConstructable {

    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/processing_array";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PA_STRUCTURE_FILE_PATH);

    public RecipeMap<?> mLastRecipeMap;
    public ItemStack lastControllerStack;
    public int tTier = 0;
    public boolean controllerStateDirty = true;
    public boolean hatchRecipeMapDirty = true;

    public ProcessingArray(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ProcessingArray(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ProcessingArray(this.mName);
    }

    @Override
    public IStructureDefinition<ProcessingArray> getStructureDefinition() {
        return StructureDefinition.<ProcessingArray>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureUtility.buildHatchAdder(ProcessingArray.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 2))))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 14))
            .addElement(
                'C',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(ProcessingArray::setMCoilLevel, ProcessingArray::getMCoilLevel))))
            .addElement('D', GTStructureUtility.ofFrame(Materials.Titanium))
            .addElement('E', HatchElement.Muffler.newAny(getCasingTextureID(), 1))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        if (GTUtility.getTier(this.getMaxInputVoltage()) <= tTier + 4) {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
        }
        checkCasingMin(errors, mCountCasing, 40);
        checkHatchExact(errors, HatchElement.Muffler, 1);
    }

    @Override
    public void checkHatch(List<StructureError> errors) {
        refreshControllerStateIfNeeded();
        super.checkHatch(errors);
    }

    @Override
    public void construct(ItemStack aStack, boolean aHintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, aStack, aHintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
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
    public boolean getPerfectOC() {
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (getControllerSlot() == null
            || getControllerSlot().getItem() != Item.getItemFromBlock(GregTechAPI.sBlockMachines)) {
            return 0;
        }
        return getControllerSlot().stackSize * 2 + GTUtility.getTier(getMaxInputVoltage()) * 4
            + getMCoilLevel().getTier() * 4;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return mLastRecipeMap;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return aStack != null && aStack.getUnlocalizedName()
            .startsWith("gt.blockmachines.");
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        refreshControllerStateIfNeeded();
        if (mLastRecipeMap == null) return SimpleCheckRecipeResult.ofFailure("no_machine");
        if (mLockedToSingleRecipe && mSingleRecipeCheck != null
            && mSingleRecipeCheck.getRecipeMap() != mLastRecipeMap) {
            return SimpleCheckRecipeResult.ofFailure("machine_mismatch");
        }

        syncInputRecipeMapsIfNeeded();
        return super.checkProcessing();
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.getMetadataOrDefault(CompressionTierKey.INSTANCE, 0) > 0) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
                if (GTMod.proxy.mLowGravProcessing && (recipe.mSpecialValue == -100 || recipe.mSpecialValue == -300)
                    && !MTEBasicMachine
                        .isValidForLowGravity(recipe, getBaseMetaTileEntity().getWorld().provider.dimensionId)) {
                    return SimpleCheckRecipeResult.ofFailure("high_gravity");
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Override
            public @NotNull GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setMaxTierSkips(0);
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return 0.9;
    }

    @Override
    public double getDurationModifier() {
        return 1 / 1.11 - (3.0 * (getMCoilLevel().getTier() + 1)) / 100;
    }

    @Override
    public boolean canUseControllerSlotForRecipe() {
        return false;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && getMaxInputAmps() <= 4;
        logic.setAvailableVoltage(GTValues.V[tTier] * (mLastRecipeMap != null ? mLastRecipeMap.getAmperage() : 1));
        logic.setAvailableAmperage(getControllerSlot().stackSize);
        logic.setAmperageOC(!useSingleAmp);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        syncInputRecipeMapsIfNeeded();
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 2);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public void sendStartMultiBlockSoundLoop() {
        SoundResource sound = ProcessingArrayManager
            .getSoundResource(ProcessingArrayManager.getMachineName(getControllerSlot()));

        if (sound == null) {
            sound = ProcessingArrayManager
                .getSoundResource(ProcessingArrayManager.getFullMachineName(getControllerSlot()));
        }

        if (sound != null) {
            sendLoopStart((byte) sound.id);
        }
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        SoundResource sound = SoundResource.get(aIndex < 0 ? aIndex + 256 : 0);
        if (sound != null) {
            GTUtility.doSoundAtClient(sound, getTimeBetweenProcessSounds(), 1.0F, aX, aY, aZ);
        }
    }

    public void invalidateControllerState() {
        controllerStateDirty = true;
        hatchRecipeMapDirty = true;
    }

    public RecipeMap<?> resolveRecipeMap(ItemStack controllerSlot) {
        if (!isCorrectMachinePart(controllerSlot)) {
            return null;
        }

        RecipeMap<?> recipeMap = ProcessingArrayManager
            .giveRecipeMap(ProcessingArrayManager.getMachineName(controllerSlot));
        if (recipeMap == null) {
            recipeMap = ProcessingArrayManager.giveRecipeMap(ProcessingArrayManager.getFullMachineName(controllerSlot));
        }
        return recipeMap;
    }

    public void refreshMachineTier(ItemStack controllerSlot) {
        IMetaTileEntity aMachine = ItemMachines.getMetaTileEntity(controllerSlot);
        if (aMachine instanceof MTETieredMachineBlock tieredMachineBlock) {
            tTier = tieredMachineBlock.mTier;
            return;
        }
        tTier = 0;
    }

    public void refreshControllerStateIfNeeded() {
        ItemStack controllerSlot = getControllerSlot();
        if (!controllerStateDirty && GTUtility.areStacksEqual(lastControllerStack, controllerSlot)) {
            return;
        }

        lastControllerStack = controllerSlot == null ? null : controllerSlot.copy();
        mLastRecipeMap = resolveRecipeMap(controllerSlot);
        refreshMachineTier(controllerSlot);
        controllerStateDirty = false;
        hatchRecipeMapDirty = true;
    }

    public void syncInputRecipeMapsIfNeeded() {
        if (!mMachine || !hatchRecipeMapDirty) {
            return;
        }

        for (MTEHatchInputBus inputBus : mInputBusses) {
            inputBus.mRecipeMap = mLastRecipeMap;
        }
        for (MTEHatchInput inputHatch : mInputHatches) {
            inputHatch.mRecipeMap = mLastRecipeMap;
        }
        hatchRecipeMapDirty = false;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ProcessingArrayRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ProcessingArray_08"))
            .beginStructureBlock(5, 5, 5, true)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ProcessingArray_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ProcessingArray_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_ProcessingArray_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ProcessingArray_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ProcessingArray_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ProcessingArray_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("coilTier", mCoilLevel.getTier());
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mCoilLevel = HeatingCoilLevel.getFromTier(aNBT.getByte("coilTier"));
        if (aNBT.hasKey("mSeparate")) {
            // Keep compatibility with legacy save data.
            inputSeparation = aNBT.getBoolean("mSeparate");
        }
        if (aNBT.hasKey("mUseMultiparallelMode")) {
            // Keep compatibility with legacy save data.
            batchMode = aNBT.getBoolean("mUseMultiparallelMode");
        }
    }

    @Override
    protected boolean requiresCoilStructureCheck() {
        return true;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        tTier = 0;
        mLastRecipeMap = null;
        lastControllerStack = null;
        invalidateControllerState();
    }

    @Override
    public void onMachineModeSwitchClick() {
        invalidateControllerState();
        super.onMachineModeSwitchClick();
    }

    @Override
    public String[] getInfoData() {
        long storedEnergy = 0;
        long maxEnergy = 0;
        for (MTEHatch tHatch : GTUtility.validMTEList(mExoticEnergyHatches)) {
            storedEnergy += tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            maxEnergy += tHatch.getBaseMetaTileEntity()
                .getEUCapacity();
        }

        return new String[] {
            StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                + EnumChatFormatting.GREEN
                + NumberFormatUtil.formatNumber(mProgresstime / 20)
                + EnumChatFormatting.RESET
                + " s / "
                + EnumChatFormatting.YELLOW
                + NumberFormatUtil.formatNumber(mMaxProgresstime / 20)
                + EnumChatFormatting.RESET
                + " s",
            StatCollector.translateToLocal("GT5U.multiblock.energy") + ": "
                + EnumChatFormatting.GREEN
                + NumberFormatUtil.formatNumber(storedEnergy)
                + EnumChatFormatting.RESET
                + " EU / "
                + EnumChatFormatting.YELLOW
                + NumberFormatUtil.formatNumber(maxEnergy)
                + EnumChatFormatting.RESET
                + " EU",
            StatCollector.translateToLocal("GT5U.multiblock.usage") + ": "
                + EnumChatFormatting.RED
                + NumberFormatUtil.formatNumber(-lEUt)
                + EnumChatFormatting.RESET
                + " EU/t",
            StatCollector.translateToLocal("GT5U.multiblock.mei") + ": "
                + EnumChatFormatting.YELLOW
                + NumberFormatUtil
                    .formatNumber(ExoticEnergyInputHelper.getMaxInputVoltageMulti(getExoticAndNormalEnergyHatchList()))
                + EnumChatFormatting.RESET
                + " EU/t(*"
                + NumberFormatUtil
                    .formatNumber(ExoticEnergyInputHelper.getMaxInputAmpsMulti(getExoticAndNormalEnergyHatchList()))
                + "A) "
                + StatCollector.translateToLocal("GT5U.machines.tier")
                + ": "
                + EnumChatFormatting.YELLOW
                + GTValues.VN[GTUtility
                    .getTier(ExoticEnergyInputHelper.getMaxInputVoltageMulti(getExoticAndNormalEnergyHatchList()))]
                + EnumChatFormatting.RESET,
            StatCollector.translateToLocal("GT5U.multiblock.problems") + ": "
                + EnumChatFormatting.RED
                + (getIdealStatus() - getRepairStatus())
                + EnumChatFormatting.RESET
                + " "
                + StatCollector.translateToLocal("GT5U.multiblock.efficiency")
                + ": "
                + EnumChatFormatting.YELLOW
                + mEfficiency / 100.0F
                + EnumChatFormatting.RESET
                + " %",
            StatCollector.translateToLocal("GT5U.PA.machinetier") + ": "
                + EnumChatFormatting.GREEN
                + tTier
                + EnumChatFormatting.RESET
                + " "
                + StatCollector.translateToLocal("GT5U.PA.discount")
                + ": "
                + EnumChatFormatting.GREEN
                + getEUtDiscount()
                + EnumChatFormatting.RESET
                + " x",
            StatCollector.translateToLocal("GT5U.PA.parallel") + ": "
                + EnumChatFormatting.GREEN
                + NumberFormatUtil.formatNumber(getTrueParallel())
                + EnumChatFormatting.RESET };
    }

    @Override
    public boolean supportsSlotAutomation(int aSlot) {
        return aSlot == getControllerSlotIndex();
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (mLastRecipeMap != null && getControllerSlot() != null) {
            tag.setString("type", getControllerSlot().getDisplayName());
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("type")) {
            currentTip.add("Machine: " + EnumChatFormatting.YELLOW + tag.getString("type"));
        } else {
            currentTip.add("Machine: " + EnumChatFormatting.YELLOW + "None");
        }
    }
}
