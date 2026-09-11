package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

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
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.material.MaterialsAlloy;
import tectech.thing.casing.BlockGTCasingsTT;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class NeutroniumWireCutting extends WirelessEnergyMultiMachineBase<NeutroniumWireCutting> {

    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 10;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String NWC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/neutronium_wire_cutting";
    private static final String[][] shape = StructureUtils.readStructureFromFile(NWC_STRUCTURE_FILE_PATH);

    public NeutroniumWireCutting(String aName) {
        super(aName);
    }

    public NeutroniumWireCutting(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.neutronium_wire_cutting.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NeutroniumWireCutting(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return Casings.NaquadriaReinforcedWaterPlantCasing.getTextureId();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive)
                return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(BlockGTCasingsTT.textureOffset),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                        .extFacing()
                        .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(BlockGTCasingsTT.textureOffset),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(BlockGTCasingsTT.textureOffset) };
    }

    @Override
    public IStructureDefinition<NeutroniumWireCutting> getStructureDefinition() {
        return StructureDefinition.<NeutroniumWireCutting>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', GTNLCasings.HeatVent.asElement())
            .addElement('C', Casings.ReinforcedGlass.asElement())
            .addElement('D', Casings.NeutroniumCasing.asElement())
            .addElement('E', Casings.ActiveNeutroniumCasing.asElement())
            .addElement('F', Casings.ExtremeDensitySpaceBendingCasing.asElement())
            .addElement('G', Casings.FilterMachineCasing.asElement())
            .addElement('H', Casings.StainlessSteelTurbineCasing.asElement())
            .addElement('I', Casings.AdvancedIridiumPlatedMachineCasing.asElement())
            .addElement('J', Casings.SuperplasticizerTreatedHighStrengthConcrete.asElement())
            .addElement('K', Casings.SlickSterileFlocculationCasing.asElement())
            .addElement(
                'L',
                GTStructureUtility.buildHatchAdder(NeutroniumWireCutting.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            Casings.NaquadriaReinforcedWaterPlantCasing.asElement())))
            .addElement('M', Casings.HighPowerCasing.asElement())
            .addElement('N', Casings.ContainmentFieldGenerator.asElement())
            .addElement('O', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement(
                'P',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_N.getFrameBox(1)
                            .getItem())))
            .addElement('Q', GTNLCasings.NeutroniumPipeCasing.asElement())
            .addElement('R', GTNLCasings.NeutroniumGearbox.asElement())
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
        checkCasingMin(errors, mCountCasing, 101);
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.95, mGlassTier);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.95, mGlassTier);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.cutterRecipes;
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.neutronium_wire_cutting.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.neutronium_wire_cutting.tooltip.0"))
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
            .beginStructureBlock(31, 14, 15, true)
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.neutronium_wire_cutting.tooltip.casing"), 1)
            .addOutputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.neutronium_wire_cutting.tooltip.casing"),
                1)
            .addInputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.neutronium_wire_cutting.tooltip.casing"),
                1)
            .addEnergyHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.neutronium_wire_cutting.tooltip.casing"),
                1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

}
