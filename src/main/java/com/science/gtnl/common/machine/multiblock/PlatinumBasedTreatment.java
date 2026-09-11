package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class PlatinumBasedTreatment extends MultiMachineBase<PlatinumBasedTreatment> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PBT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/platinum_based_treatment";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PBT_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 15;
    private static final int DEPTH_OFF_SET = 0;

    public PlatinumBasedTreatment(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PlatinumBasedTreatment(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.platinum_based_treatment.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new PlatinumBasedTreatment(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.recipe.platinum_based_treatment"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.5"))
            .addPerfectOCInfo()
            .addSupportAny()
            .beginStructureBlock(15, 17, 18, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.casing"))
            .addOutputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.casing"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.casing"))
            .addEnergyHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.casing"))
            .addMaintenanceHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.casing"))
            .addMufflerHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.platinum_based_treatment.tooltip.muffler"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<PlatinumBasedTreatment> getStructureDefinition() {
        return StructureDefinition.<PlatinumBasedTreatment>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', Casings.HeatProofMachineCasing.asElement())
            .addElement('C', Casings.IVSolenoidSuperconductorCoil.asElement())
            .addElement('D', Casings.SolidifierCasing.asElement())
            .addElement('E', Casings.SolidifierRadiator.asElement())
            .addElement('F', Casings.RobustTungstenSteelMachineCasing.asElement())
            .addElement('G', Casings.CleanStainlessSteelMachineCasing.asElement())
            .addElement(
                'H',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility
                            .ofCoil(PlatinumBasedTreatment::setMCoilLevel, PlatinumBasedTreatment::getMCoilLevel))))
            .addElement('I', Casings.ChemicallyInertMachineCasing.asElement())
            .addElement('J', Casings.PTFEPipeCasing.asElement())
            .addElement('K', GTStructureUtility.ofFrame(Materials.BlackSteel))
            .addElement('L', Casings.IndustrialSieveCasing.asElement())
            .addElement('M', Casings.LargeSieveGrate.asElement())
            .addElement('N', Casings.ThermalContainmentCasing.asElement())
            .addElement(
                'O',
                GTStructureUtility.buildHatchAdder(PlatinumBasedTreatment.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputHatch,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .buildAndChain(
                        StructureUtility.onElementPass(x -> ++x.mCountCasing, Casings.MultiUseCasing.asElement())))
            .addElement('P', Casings.CentrifugeCasing.asElement())
            .addElement('Q', Casings.ElectrolyzerCasing.asElement())
            .addElement('R', HatchElement.Muffler.newAny(Casings.HeatProofMachineCasing.getTextureId(), 6))
            .build();
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
    public void checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 30);
        checkHatchExact(errors, HatchElement.Muffler, 6);
    }

    @Override
    protected boolean requiresCoilStructureCheck() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.PlatinumBasedTreatmentRecipes;
    }

    @Override
    public double getEUtDiscount() {
        return 1.0 - getMCoilLevel().getTier() * 0.05;
    }

    @Override
    public double getDurationModifier() {
        return 1.0 - getMCoilLevel().getTier() * 0.05;
    }

    @Override
    public int getMaxParallelRecipes() {
        return GTUtility.getTier(this.getMaxInputVoltage()) * 4 + 8;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.getIndexFromPage(2, 2);
    }

    @Override
    public void updateHatchTexture() {
        super.updateHatchTexture();
        for (MTEHatch h : mMufflerHatches) {
            h.updateTexture(Casings.HeatProofMachineCasing.getTextureId());
        }
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    protected int getGlassEnergyTierLimit() {
        return VoltageIndex.UHV;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MEGA_BLAST_FURNACE_LOOP;
    }
}
