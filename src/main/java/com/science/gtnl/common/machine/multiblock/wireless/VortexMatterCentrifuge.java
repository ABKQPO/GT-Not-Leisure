package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.casing.BlockGTCasingsTT;

@IMetaTileEntity.SkipGenerateDescription
public class VortexMatterCentrifuge extends WirelessEnergyMultiMachineBase<VortexMatterCentrifuge> {

    private static final int HORIZONTAL_OFF_SET = 15;
    private static final int VERTICAL_OFF_SET = 7;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String VMC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/vortex_matter_centrifuge";
    private static final String[][] shape = StructureUtils.readStructureFromFile(VMC_STRUCTURE_FILE_PATH);

    public VortexMatterCentrifuge(String aName) {
        super(aName);
    }

    public VortexMatterCentrifuge(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new VortexMatterCentrifuge(this.mName);
    }

    @Override
    public IStructureDefinition<VortexMatterCentrifuge> getStructureDefinition() {
        return StructureDefinition.<VortexMatterCentrifuge>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTNLCasings.NeutroniumGearbox.asElement())
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 9))
            .addElement('C', Casings.ContainmentFieldGenerator.asElement())
            .addElement('D', Casings.HighPowerCasing.asElement())
            .addElement('E', Casings.PressureContainmentCasing.asElement())
            .addElement('F', Casings.UHVMachineCasing.asElement())
            .addElement('G', Casings.HollowCasing.asElement())
            .addElement('H', Casings.NeutroniumStabilizationCasing.asElement())
            .addElement('I', GTNLCasings.Antifreeze_Heatproof_Machine_Casing.asElement())
            .addElement('J', Casings.ActiveNeutroniumCasing.asElement())
            .addElement(
                'K',
                GTStructureUtility.buildHatchAdder(VortexMatterCentrifuge.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(x -> ++x.mCountCasing, Casings.MolecularCasing.asElement())))
            .addElement('L', Casings.RadiantNaquadahAlloyCasing.asElement())
            .addElement('M', Casings.DimensionalInjectionCasing.asElement())
            .addElement('N', GTStructureUtility.ofFrame(Materials.EnrichedHolmium))
            .addElement('O', Casings.ShieldedAcceleratorCasing.asElement())
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
        checkCasingMin(errors, mCountCasing, 251);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.centrifugeNonCellRecipes;
    }

    @Override
    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset + 4;
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
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.vortex_matter_centrifuge.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.5"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.6"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.7"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.8"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.9"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.10"))
            .addSupportAny()
            .beginStructureBlock(31, 10, 31, true)
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.vortex_matter_centrifuge.casing"), 1)
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.vortex_matter_centrifuge.casing"), 1)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.vortex_matter_centrifuge.casing"), 1)
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.vortex_matter_centrifuge.casing"), 1)
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.vortex_matter_centrifuge.casing"), 1)
            .toolTipFinisher();
        return tt;
    }
}
