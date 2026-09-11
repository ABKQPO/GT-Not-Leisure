package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static kubatech.loaders.BlockLoader.defcCasingBlock;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.gui.modularui.GTNLMultiBlockBaseGui;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.GTStructureChannels;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import gtnhlanth.common.register.LanthItemList;

@IMetaTileEntity.SkipGenerateDescription
public class DissolutionCore extends WirelessEnergyMultiMachineBase<DissolutionCore> {

    public static final int MACHINEMODE_DISSOLUTION = 0;
    public static final int MACHINEMODE_DIGESTER = 1;
    private static final int HORIZONTAL_OFF_SET = 15;
    private static final int VERTICAL_OFF_SET = 13;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String DC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/dissolution_core";
    private static final String[][] shape = StructureUtils.readStructureFromFile(DC_STRUCTURE_FILE_PATH);

    public DissolutionCore(String aName) {
        super(aName);
    }

    public DissolutionCore(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DissolutionCore(this.mName);
    }

    @Override
    public IStructureDefinition<DissolutionCore> getStructureDefinition() {
        return StructureDefinition.<DissolutionCore>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', Casings.ActiveNeutroniumCasing.asElement())
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(DissolutionCore.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, Casings.RadiantNaquadahAlloyCasing.asElement())))
            .addElement(
                'C',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(DissolutionCore::setMCoilLevel, DissolutionCore::getMCoilLevel))))
            .addElement('D', GTNLCasings.NeutroniumPipeCasing.asElement())
            .addElement('E', Casings.PTFEPipeCasing.asElement())
            .addElement('F', Casings.HeatResistantTriniumPlatedCasing.asElement())
            .addElement('G', Casings.ElectrumItemPipeCasing.asElement())
            .addElement('H', Casings.ZPMSuperconductorPrimaryTeslaWindings.asElement())
            .addElement('I', StructureUtility.ofBlock(defcCasingBlock, 7))
            .addElement('J', Casings.ThermalProcessingCasing.asElement())
            .addElement('K', Casings.StabilizedNaquadahWaterPlantCasing.asElement())
            .addElement('L', GTNLCasings.FusionGlass.asElement())
            .addElement('M', Casings.ActiveNeutroniumCasing.asElement())
            .addElement('N', StructureUtility.ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .addElement('O', GTStructureUtility.ofFrame(Materials.Polytetrafluoroethylene))
            .addElement('P', Casings.ChemicallyInertMachineCasing.asElement())
            .addElement('Q', GTStructureUtility.ofFrame(Materials.BlackSteel))
            .addElement('R', Casings.NaquadriaReinforcedWaterPlantCasing.asElement())
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
    protected boolean requiresCoilStructureCheck() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_DISSOLUTION) ? LanthanidesRecipeMaps.dissolutionTankRecipes
            : LanthanidesRecipeMaps.digesterRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(LanthanidesRecipeMaps.dissolutionTankRecipes, LanthanidesRecipeMaps.digesterRecipes);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.85, getMCoilLevel().getTier());
    }

    @Override
    public int getCasingTextureID() {
        return Casings.RadiantNaquadahAlloyCasing.getTextureId();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity te, ForgeDirection side, ForgeDirection facing, int colorIndex,
        boolean active, boolean redstone) {
        if (side == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.dissolution_core.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.dissolution_core.info.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.dissolution_core.info.1"))
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
            .beginStructureBlock(31, 16, 31, true)
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.dissolution_core.casing"), 1)
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.dissolution_core.casing"), 1)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.dissolution_core.casing"), 1)
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.dissolution_core.casing"), 1)
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.dissolution_core.casing"), 1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTNLMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_UNPACKAGER);
    }

    @Override
    @Deprecated
    public void setMachineModeIcons() {
        // TODO: Remove this mui1 fallback after this GUI no longer supports mui1 startup paths.
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_UNPACKAGER);
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 2;
        GTUtility.sendChatTrans(aPlayer, getMachineModeKey());
    }

    @Override
    public String getMachineModeKey() {
        return "gtnl.machine.dissolution_core.mode." + machineMode;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }
}
