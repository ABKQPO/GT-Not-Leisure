package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.blocks.BlockCasings2;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public class ColdIceFreezer extends MultiMachineBase<ColdIceFreezer> implements ISurvivalConstructable {

    public static final int CASING_INDEX = ((BlockCasings2) sBlockCasings2).getTextureIndex(1);
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String CIF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/cold_ice_freeze";
    public static String[][] shape = StructureUtils.readStructureFromFile(CIF_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 2;
    public final int VERTICAL_OFF_SET = 2;
    public final int DEPTH_OFF_SET = 0;
    private static IStructureDefinition<ColdIceFreezer> STRUCTURE_DEFINITION = null;

    public ColdIceFreezer(final int aID, final String aName, final String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ColdIceFreezer(final String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new ColdIceFreezer(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ColdIceFreezerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .beginStructureBlock(5, 5, 9, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_Casing_00"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_Casing_00"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_Casing_00"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_Casing_00"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_Casing_00"), 1)
            .addMufflerHatch(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_Casing_01"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ColdIceFreezer_Casing_00"), 1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("FluidIceInputHatch"),
                StatCollector.translateToLocal("Tooltip_ColdIceFreezer_Casing_00"),
                1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<ColdIceFreezer> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<ColdIceFreezer>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlockAnyMeta(GameRegistry.findBlock(IndustrialCraft2.ID, "blockAlloyGlass")))
                .addElement(
                    'B',
                    ofChain(
                        buildHatchAdder(ColdIceFreezer.class)
                            .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy), Maintenance)
                            .dot(1)
                            .casingIndex(CASING_INDEX)
                            .build(),
                        onElementPass(x -> ++x.tCountCasing, ofBlock(GregTechAPI.sBlockCasings2, 1)),
                        buildHatchAdder(ColdIceFreezer.class).adder(ColdIceFreezer::addFluidIceInputHatch)
                            .hatchId(21502)
                            .shouldReject(x -> !x.FluidIceInputHatch.isEmpty())
                            .casingIndex(CASING_INDEX)
                            .dot(1)
                            .build()))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 15))
                .addElement('D', ofFrame(Materials.Aluminium))
                .addElement('E', ofBlock(ModBlocks.blockCasings3Misc, 10))
                .addElement('F', Muffler.newAny(TAE.getIndexFromPage(2, 10), 1))
                .build();
        }
        return STRUCTURE_DEFINITION;
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        FluidIceInputHatch.clear();

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;

        energyHatchTier = checkEnergyHatchTier();
        if (MainConfig.enableMachineAmpLimit) {
            for (MTEHatch hatch : getExoticEnergyHatches()) {
                if (hatch instanceof MTEHatchEnergyTunnel) {
                    return false;
                }
            }
            if (getMaxInputAmps() > 64) return false;
        }

        return tCountCasing >= 50 && checkHatch() && this.mMufflerHatches.size() == 1;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && !FluidIceInputHatch.isEmpty();
    }

    @Override
    public void updateSlots() {
        for (CustomFluidHatch tHatch : validMTEList(FluidIceInputHatch)) tHatch.updateSlots();
        super.updateSlots();
    }

    protected float getSpeedBonus() {
        return 1F;
    }

    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    public int getCasingTextureID() {
        return TAE.getIndexFromPage(2, 10);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAIndustrialVacuumFreezerActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAIndustrialVacuumFreezer)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.advancedFreezerRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().setSpeedBonus(1.0 / 2.5)
            .setEuModifier(0.8)
            .setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 128;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);

        if (this.mStartUpCheck < 0) {
            startRecipeProcessing();
            if (this.mMaxProgresstime > 0 && this.mProgresstime != 0 || this.getBaseMetaTileEntity()
                .hasWorkJustBeenEnabled()) {
                if (aTick % 20 == 0 || this.getBaseMetaTileEntity()
                    .hasWorkJustBeenEnabled()) {
                    if (!this.depleteInputFromRestrictedHatches(
                        this.FluidIceInputHatch,
                        (int) (10 * getInputVoltageTier() * getInputVoltageTier()))) {
                        this.causeMaintenanceIssue();
                        this.stopMachine(
                            ShutDownReasonRegistry.outOfFluid(
                                Objects.requireNonNull(
                                    FluidUtils.getFluidStack(
                                        "ice",
                                        (int) (10 * getInputVoltageTier() * getInputVoltageTier())))));
                        endRecipeProcessing();
                    }
                }
            }
            endRecipeProcessing();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_ADV_FREEZER_LOOP;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 2;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(!mExoticEnergyHatches.isEmpty() || mEnergyHatches.size() != 1);
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
