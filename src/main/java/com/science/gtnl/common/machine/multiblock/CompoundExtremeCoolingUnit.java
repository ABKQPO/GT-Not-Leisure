package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase.ParallelControllerElement.ParallelCon;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.Utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.common.machine.multiMachineClasses.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipePool;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings2;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.material.MaterialsAlloy;

public class CompoundExtremeCoolingUnit extends WirelessEnergyMultiMachineBase<CompoundExtremeCoolingUnit> {

    private static final int MACHINEMODE_VACUUM_FREEZE = 0;
    private static final int MACHINEMODE_PLSAMA_CONDENSATION = 1;
    private static final int HORIZONTAL_OFF_SET = 15;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String UECN_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/compound_extreme_cooling_unit";
    public static final String[][] shape = StructureUtils.readStructureFromFile(UECN_STRUCTURE_FILE_PATH);

    public CompoundExtremeCoolingUnit(String aName) {
        super(aName);
    }

    public CompoundExtremeCoolingUnit(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new CompoundExtremeCoolingUnit(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("CompoundExtremeCoolingUnitRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CompoundExtremeCoolingUnit_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(31, 9, 15, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_CompoundExtremeCoolingUnit_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_CompoundExtremeCoolingUnit_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_CompoundExtremeCoolingUnit_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_CompoundExtremeCoolingUnit_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_CompoundExtremeCoolingUnit_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return ((BlockCasings2) sBlockCasings2).getTextureIndex(1);
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
    public IStructureDefinition<CompoundExtremeCoolingUnit> getStructureDefinition() {
        return StructureDefinition.<CompoundExtremeCoolingUnit>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', ofBlock(BlockLoader.metaCasing, 4))
            .addElement('C', ofBlock(BlockLoader.metaCasing, 6))
            .addElement('D', ofBlock(BlockLoader.metaCasing, 7))
            .addElement(
                'E',
                ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.AQUATIC_STEEL.getFrameBox(1)
                            .getItem())))
            .addElement('F', ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('G', ofBlock(sBlockCasings10, 1))
            .addElement('H', ofBlock(sBlockCasings2, 1))
            .addElement('I', ofBlock(sBlockCasings2, 15))
            .addElement('J', ofBlock(sBlockCasings3, 11))
            .addElement('K', ofBlock(sBlockCasingsTT, 0))
            .addElement('L', ofBlock(sBlockCasingsTT, 8))
            .addElement('M', ofFrame(Materials.Neutronium))
            .addElement(
                'N',
                buildHatchAdder(CompoundExtremeCoolingUnit.class)
                    .atLeast(Maintenance, InputBus, OutputBus, InputHatch, Energy.or(ExoticEnergy), ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings2, 1))))
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCountCasing = 0;
        wirelessMode = false;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        mEnergyHatchTier = checkEnergyHatchTier();
        mParallelTier = getParallelTier(aStack);
        wirelessMode = mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
        return mCountCasing > 1;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4 && wirelessMode) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                enablePerfectOverclock();
                return super.process();
            }

            @Nonnull
            @Override
            protected GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(0.4 - (mParallelTier / 50.0) * Math.pow(0.95, mGlassTier))
                    .setDurationModifier(0.1 * Math.pow(0.75, mParallelTier) * Math.pow(0.95, mGlassTier));
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_VACUUM_FREEZE) ? RecipeMaps.vacuumFreezerRecipes
            : RecipePool.PlasmaCondensationRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.vacuumFreezerRecipes, RecipePool.PlasmaCondensationRecipes);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mGlassTier", mGlassTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mGlassTier = aNBT.getInteger("mGlassTier");
    }

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 1];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        return ret;
    }

    @Override
    public int nextMachineMode() {
        return (machineMode == MACHINEMODE_VACUUM_FREEZE) ? MACHINEMODE_PLSAMA_CONDENSATION : MACHINEMODE_VACUUM_FREEZE;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_UNPACKAGER);
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
            StatCollector.translateToLocal("CompoundExtremeCoolingUnit_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("CompoundExtremeCoolingUnit_Mode_" + machineMode);
    }

}
