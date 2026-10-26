package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

@IMetaTileEntity.SkipGenerateDescription
public class MegaMixer extends GTMMultiMachineBase<MegaMixer> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/mega_mixer";
    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 7;
    private static final int DEPTH_OFF_SET = 0;
    private static final int SPEED_BOOST_INTERVAL = 10;
    private static final double SPEED_BOOST_STEP = 0.05, MAX_SPEED_BOOST = 2;
    private static final double LOCKED_EU_USAGE = 0.8;
    private static final String[][] shape = StructureUtils.readStructureFromFile(MM_STRUCTURE_FILE_PATH);

    public double runningSpeedBoost;
    public int runningTickCounter = 0;

    public MegaMixer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MegaMixer(String aName) {
        super(aName);
    }

    @Override
    public IStructureDefinition<MegaMixer> getStructureDefinition() {
        return StructureDefinition.<MegaMixer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', Casings.HighPowerCasing.asElement())
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(MegaMixer.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            Casings.AdvancedIridiumPlatedMachineCasing.asElement())))
            .addElement('C', Casings.MolecularCasing.asElement())
            .addElement(
                'D',
                GTStructureUtility.buildHatchAdder(MegaMixer.class)
                    .casingIndex(TAE.GTPP_INDEX(11))
                    .hint(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, Casings.MultitankExteriorCasing.asElement())))
            .addElement('E', Casings.RadiantNaquadahAlloyCasing.asElement())
            .addElement('F', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement('G', GTNLCasings.NeutroniumPipeCasing.asElement())
            .addElement('H', GTNLCasings.NeutroniumGearbox.asElement())
            .build();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MegaMixer(this.mName);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 40);
    }

    @Override
    public void checkEnergyHatch(List<StructureError> errors) {}

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (mLockedToSingleRecipe) {
            runningTickCounter++;
            if (runningTickCounter % SPEED_BOOST_INTERVAL == 0 && runningSpeedBoost < MAX_SPEED_BOOST) {
                runningTickCounter = 0;
                runningSpeedBoost = Math.min(MAX_SPEED_BOOST, runningSpeedBoost + SPEED_BOOST_STEP);
            }
        } else {
            runningSpeedBoost = 0;
        }
        return super.onRunningTick(aStack);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setDouble("runningSpeedBoost", runningSpeedBoost);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        runningSpeedBoost = aNBT.getDouble("runningSpeedBoost");
    }

    @Override
    public boolean getHeatOC() {
        return true;
    }

    @Override
    public int getMachineHeat() {
        return mLockedToSingleRecipe ? 3600 : 0;
    }

    @Override
    public double getEUtDiscount() {
        return mLockedToSingleRecipe ? LOCKED_EU_USAGE : 0.6 - (mParallelTier / 50.0);
    }

    @Override
    public Map<String, Object> getTooltipVars() {
        return Collections.singletonMap("locked_eu_usage", TooltipHelper.percentageFormat.format(LOCKED_EU_USAGE));
    }

    @Override
    public double getDurationModifier() {
        return Math.max(0.005, 1.0 / (5 + runningSpeedBoost) - (Math.max(0, mParallelTier - 1) / 50.0));
    }

    @Override
    public int getMaxTierSkip() {
        return UNLIMITED_TIER_SKIPS;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(getMaxInputEu());
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(true);
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
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.mixerNonCellRecipes;
    }

    @Override
    public int getCasingTextureID() {
        return Casings.AdvancedIridiumPlatedMachineCasing.getTextureId();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialMixerActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialMixer)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }
}
