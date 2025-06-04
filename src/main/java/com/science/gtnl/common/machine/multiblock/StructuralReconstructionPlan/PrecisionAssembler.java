package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

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
import gregtech.api.util.OverclockCalculator;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class PrecisionAssembler extends MultiMachineBase<PrecisionAssembler> implements ISurvivalConstructable {

    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<PrecisionAssembler> STRUCTURE_DEFINITION = null;
    public static final String LPA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/precise_assembler";
    private static final int MACHINEMODE_ASSEMBLER = 0;
    private static final int MACHINEMODE_PRECISION = 1;
    private static final int CASING_INDEX = 1540;
    public final int HORIZONTAL_OFF_SET = 4;
    public final int VERTICAL_OFF_SET = 4;
    public final int DEPTH_OFF_SET = 0;
    public static String[][] shape = StructureUtils.readStructureFromFile(LPA_STRUCTURE_FILE_PATH);
    protected int casingTier = 0;
    protected int machineTier = -1;

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
        int id = Math.max(CASING_INDEX, CASING_INDEX + casingTier);
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id),
                TextureFactory.of(TexturesGtBlock.oMCDIndustrialCuttingMachineActive), TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialCuttingMachineActive)
                    .glow()
                    .build() };
            else return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id),
                TextureFactory.of(TexturesGtBlock.oMCDIndustrialCuttingMachine), TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialCuttingMachine)
                    .glow()
                    .build() };
        } else return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    @Override
    public void onValueUpdate(byte aValue) {
        if ((byte) casingTier != aValue) {
            casingTier = (byte) (aValue & 0x0F);
        }
    }

    @Override
    public byte getUpdateData() {
        if (casingTier <= -1) return 0;
        return (byte) casingTier;
    }

    @Override
    public int getCasingTextureID() {
        if (casingTier >= 0) {
            return CASING_INDEX + casingTier;
        } else return CASING_INDEX;
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
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<PrecisionAssembler> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<PrecisionAssembler>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
                .addElement(
                    'B',
                    withChannel(
                        "tiercasing",
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
                            (t, m) -> t.machineTier = m,
                            t -> t.machineTier)))
                .addElement('C', ofFrame(Materials.TungstenSteel))
                .addElement(
                    'D',
                    ofChain(
                        buildHatchAdder(PrecisionAssembler.class).casingIndex(getCasingTextureID())
                            .dot(1)
                            .atLeast(InputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                            .buildAndChain(
                                onElementPass(
                                    x -> ++x.tCountCasing,
                                    withChannel(
                                        "precisecasing",
                                        ofBlocksTiered(
                                            PrecisionAssembler::getCasingTier,
                                            ImmutableList.of(
                                                Pair.of(Loaders.impreciseUnitCasing, 0),
                                                Pair.of(Loaders.preciseUnitCasing, 0),
                                                Pair.of(Loaders.preciseUnitCasing, 1),
                                                Pair.of(Loaders.preciseUnitCasing, 2),
                                                Pair.of(Loaders.preciseUnitCasing, 3)),
                                            -1,
                                            (t, m) -> t.casingTier = m,
                                            t -> t.casingTier))))))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return casingTier >= 4;
    }

    @Nullable
    public static Integer getCasingTier(Block block, int meta) {
        if (block == null) return null;
        if (block == Loaders.impreciseUnitCasing) return 0;
        if (block == Loaders.preciseUnitCasing) return meta + 1;
        return null;
    }

    @Nullable
    public static Integer getMachineTier(Block block, int meta) {
        if (block == null) return null;
        if (block == sBlockCasings1) return meta;
        return null;
    }

    @Override
    public float getSpeedBonus() {
        return 1;
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSING);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        casingTier = aNBT.getInteger("casingTier");
        machineTier = aNBT.getInteger("machineTier");
        machineMode = aNBT.getInteger("mode");
        super.loadNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("casingTier", casingTier);
        aNBT.setInteger("machineTier", machineTier);
        aNBT.setInteger("mode", machineMode);
        super.saveNBTData(aNBT);
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
        tCountCasing = 0;
        casingTier = -1;
        machineTier = -1;
        mGlassTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()) {
            return false;
        }

        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (machineTier < VoltageIndex.UHV & mEnergyHatch.mTier > machineTier) {
                return false;
            }
        }

        for (MTEHatch mExoEnergyHatch : this.mExoticEnergyHatches) {
            if (machineTier < VoltageIndex.UHV & mExoEnergyHatch.mTier > machineTier) {
                return false;
            }
        }

        energyHatchTier = checkEnergyHatchTier();

        updateTexture(aBaseMetaTileEntity, getCasingTextureID());
        if (aBaseMetaTileEntity instanceof MTEHatch mteHatch) {
            mteHatch.updateTexture(getCasingTextureID());
            return true;
        }
        return tCountCasing >= 30 && casingTier >= 0;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 2;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(true);
    }

    public long getMachineVoltageLimit() {
        if (machineTier < 0) return 0;
        if (machineTier >= 9) return GTValues.V[energyHatchTier];
        else return GTValues.V[Math.min(machineTier, energyHatchTier)];
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
        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (machineMode == 1) {
                    if (recipe.mSpecialValue > (Math.max(0, casingTier + 1))) {
                        return CheckRecipeResultRegistry.insufficientMachineTier(recipe.mSpecialValue);
                    }
                }
                if (availableVoltage < recipe.mEUt) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUtDiscount(0.8)
                    .setDurationModifier(1 / 2.25);
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        if (mGlassTier > 0) {
            return (int) Math.pow(2, mGlassTier) + casingTier * 64;
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

    @Override
    public long getMaxInputAmps() {
        return getMaxInputAmpsHatch(getExoticAndNormalEnergyHatchList());
    }

    public static long getMaxInputAmpsHatch(Collection<? extends MTEHatch> hatches) {
        List<Long> ampsList = new ArrayList<>();
        for (MTEHatch tHatch : validMTEList(hatches)) {
            long currentAmp = tHatch.getBaseMetaTileEntity()
                .getInputAmperage();
            ampsList.add(currentAmp);
        }

        if (ampsList.isEmpty()) {
            return 0L;
        }

        return Collections.max(ampsList);
    }
}
