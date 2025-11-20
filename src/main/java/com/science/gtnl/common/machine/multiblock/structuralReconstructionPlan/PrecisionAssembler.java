package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class PrecisionAssembler extends MultiMachineBase<PrecisionAssembler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String LPA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/precise_assembler";
    private static final int MACHINEMODE_ASSEMBLER = 0;
    private static final int MACHINEMODE_PRECISION = 1;
    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LPA_STRUCTURE_FILE_PATH);
    public int mCasingTier = -1;
    public int mMachineTier = -1;

    public PrecisionAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PrecisionAssembler(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PrecisionAssembler(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.of(TexturesGtBlock.oMCDIndustrialCuttingMachineActive), TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialCuttingMachineActive)
                    .glow()
                    .build() };
            else return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.of(TexturesGtBlock.oMCDIndustrialCuttingMachine), TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialCuttingMachine)
                    .glow()
                    .build() };
        } else return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public void onValueUpdate(byte aValue) {
        if ((byte) mCasingTier != aValue) {
            mCasingTier = (byte) (aValue & 0x0F);
        }
    }

    @Override
    public byte getUpdateData() {
        if (mCasingTier <= -1) return 0;
        return (byte) mCasingTier;
    }

    @Override
    public int getCasingTextureID() {
        if (mCasingTier >= 0) {
            return 1540 + mCasingTier;
        } else return 1540;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_ASSEMBLER) ? RecipeMaps.assemblerRecipes
            : GoodGeneratorRecipeMaps.preciseAssemblerRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.assemblerRecipes, GoodGeneratorRecipeMaps.preciseAssemblerRecipes);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("PreciseAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PreciseAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PreciseAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PreciseAssembler_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PreciseAssembler_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 5, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_PreciseAssembler_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_PreciseAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_PreciseAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_PreciseAssembler_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_PreciseAssembler_Casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTStructureChannels.PRASS_UNIT_CASING)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<PrecisionAssembler> getStructureDefinition() {
        return StructureDefinition.<PrecisionAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement(
                'B',
                StructureUtility.withChannel(
                    "machine_casing",
                    ofBlocksTiered(
                        PrecisionAssembler::getMachineTier,
                        ImmutableList.of(
                            Pair.of(sBlockCasings1, 0),
                            Pair.of(sBlockCasings1, 1),
                            Pair.of(sBlockCasings1, 2),
                            Pair.of(sBlockCasings1, 3),
                            Pair.of(sBlockCasings1, 4),
                            Pair.of(sBlockCasings1, 5),
                            Pair.of(sBlockCasings1, 6),
                            Pair.of(sBlockCasings1, 7),
                            Pair.of(sBlockCasings1, 8),
                            Pair.of(sBlockCasings1, 9)),
                        -1,
                        (t, m) -> t.mMachineTier = m,
                        t -> t.mMachineTier)))
            .addElement('C', ofFrame(Materials.TungstenSteel))
            .addElement(
                'D',
                ofChain(
                    buildHatchAdder(PrecisionAssembler.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(InputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy), ParallelCon)
                        .buildAndChain(
                            onElementPass(
                                x -> ++x.mCountCasing,
                                StructureUtility.withChannel(
                                    "unit_casing",
                                    ofBlocksTiered(
                                        PrecisionAssembler::getCasingTier,
                                        ImmutableList.of(
                                            Pair.of(Loaders.impreciseUnitCasing, 0),
                                            Pair.of(Loaders.preciseUnitCasing, 0),
                                            Pair.of(Loaders.preciseUnitCasing, 1),
                                            Pair.of(Loaders.preciseUnitCasing, 2),
                                            Pair.of(Loaders.preciseUnitCasing, 3)),
                                        -1,
                                        (t, m) -> t.mCasingTier = m,
                                        t -> t.mCasingTier))))))
            .build();
    }

    @Override
    public boolean getPerfectOC() {
        return mCasingTier >= 4;
    }

    @Nullable
    public static Integer getMachineTier(Block block, int meta) {
        if (block == null) return null;
        if (block == sBlockCasings1) return meta;
        return null;
    }

    @Nullable
    public static Integer getCasingTier(Block block, int meta) {
        if (block == null) return null;
        if (block == Loaders.impreciseUnitCasing) return 0;
        if (block == Loaders.preciseUnitCasing) return meta + 1;
        return null;
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSING);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        mCasingTier = aNBT.getInteger("casingTier");
        mMachineTier = aNBT.getInteger("machineTier");
        super.loadNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("casingTier", mCasingTier);
        aNBT.setInteger("machineTier", mMachineTier);
        super.saveNBTData(aNBT);
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (byte) ((this.machineMode + 1) % 2);
        GTUtility
            .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("PreciseAssembler_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("PreciseAssembler_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
        return mCountCasing >= 30;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mCasingTier = -1;
        mMachineTier = -1;
    }

    @Override
    public boolean checkHatch() {
        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (mMachineTier < VoltageIndex.UHV & mEnergyHatch.mTier > mMachineTier) {
                return false;
            }
        }

        for (MTEHatch mExoEnergyHatch : this.mExoticEnergyHatches) {
            if (mMachineTier < VoltageIndex.UHV & mExoEnergyHatch.mTier > mMachineTier) {
                return false;
            }
        }
        return super.checkHatch() && mCasingTier >= 0;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 4;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(true);
    }

    public long getMachineVoltageLimit() {
        if (mMachineTier < 0) return 0;
        if (mMachineTier >= 9) return GTValues.V[mEnergyHatchTier];
        else return GTValues.V[Math.min(mMachineTier, mEnergyHatchTier)];
    }

    public int checkEnergyHatchTier() {
        int tier = 0;
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        return tier;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Nonnull
            @Override
            public CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (machineMode == 1) {
                    if (recipe.mSpecialValue > (Math.max(0, mCasingTier + 1))) {
                        return CheckRecipeResultRegistry.insufficientMachineTier(recipe.mSpecialValue);
                    }
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Nonnull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setPerfectOC(getPerfectOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setMaxTierSkips(getMaxTierSkip());
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return 0.8;
    }

    @Override
    public double getDurationModifier() {
        return 1 / 2.25;
    }

    @Override
    public int getMaxTierSkip() {
        return 0;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (mGlassTier > 0) {
            return (1 << mGlassTier) + mCasingTier * 64;
        } else {
            return 0;
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
}
