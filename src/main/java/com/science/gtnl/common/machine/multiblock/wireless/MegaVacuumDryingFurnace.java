package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

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

import bartworks.util.BWUtil;
import gregtech.api.GregTechAPI;
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
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import tectech.thing.casing.BlockGTCasingsTT;

@IMetaTileEntity.SkipGenerateDescription
public class MegaVacuumDryingFurnace extends WirelessEnergyMultiMachineBase<MegaVacuumDryingFurnace> {

    private static final int HORIZONTAL_OFF_SET = 14;
    private static final int VERTICAL_OFF_SET = 10;
    private static final int DEPTH_OFF_SET = 2;
    private static final int MACHINEMODE_VACUUMFURNACE = 0;
    private static final int MACHINEMODE_DEHYDRATOR = 1;
    private static final int MACHINEMODE_COLD_TRAP = 2;
    private static final int MACHINEMODE_NUCLEAR_SALT = 3;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MVDF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/mega_vacuum_drying_furnace";
    private static final String[][] shape = StructureUtils.readStructureFromFile(MVDF_STRUCTURE_FILE_PATH);

    public MegaVacuumDryingFurnace(String aName) {
        super(aName);
    }

    public MegaVacuumDryingFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MegaVacuumDryingFurnace(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAIndustrialDehydratorActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAIndustrialDehydrator)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<MegaVacuumDryingFurnace> getStructureDefinition() {
        return StructureDefinition.<MegaVacuumDryingFurnace>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureUtility.buildHatchAdder(MegaVacuumDryingFurnace.class)
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
                        StructureUtility.onElementPass(x -> ++x.mCountCasing, Casings.HighPowerCasing.asElement())))
            .addElement('B', Casings.HermeticCasing6.asElement())
            .addElement('C', Casings.CleanStainlessSteelMachineCasing.asElement())
            .addElement('D', Casings.AdvancedIridiumPlatedMachineCasing.asElement())
            .addElement(
                'E',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility
                            .ofCoil(MegaVacuumDryingFurnace::setMCoilLevel, MegaVacuumDryingFurnace::getMCoilLevel))))
            .addElement('F', Casings.TungstensteelPipeCasing.asElement())
            .addElement('G', Casings.ComputerCasing.asElement())
            .addElement('H', Casings.PressureContainmentCasing.asElement())
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockMetal4, 12))
            .addElement('J', Casings.ComputerHeatVent.asElement())
            .addElement('K', Casings.VacuumCasing.asElement())
            .addElement('L', Casings.FilterMachineCasing.asElement())
            .addElement('M', GTNLCasings.TungstensteelGearbox.asElement())
            .addElement('N', GTStructureUtility.ofFrame(Materials.Tungsten))
            .addElement('O', Casings.GrateMachineCasing.asElement())
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
        checkCasingMin(errors, mCountCasing, 2);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTNLMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_STEAM,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_WASHPLANT);
    }

    @Override
    @Deprecated
    public void setMachineModeIcons() {
        // TODO: Remove this mui1 fallback after this GUI no longer supports mui1 startup paths.
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_STEAM);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_WASHPLANT);
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == MACHINEMODE_VACUUMFURNACE) return MACHINEMODE_DEHYDRATOR;
        else if (machineMode == MACHINEMODE_DEHYDRATOR) return MACHINEMODE_COLD_TRAP;
        else if (machineMode == MACHINEMODE_COLD_TRAP) return MACHINEMODE_NUCLEAR_SALT;
        else return MACHINEMODE_VACUUMFURNACE;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        this.mHeatingCapacity = (int) this.getMCoilLevel()
            .getHeat() + 100 * (BWUtil.getTier(this.getMaxInputEu()) - 2);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 4;
        GTUtility.sendChatTrans(aPlayer, getMachineModeKey());
    }

    @Override
    public String getMachineModeKey() {
        return "gtnl.machine.mega_vacuum_drying_furnace.mode." + machineMode;
    }

    @Override
    public boolean getHeatOC() {
        return true;
    }

    @Override
    public int getMachineHeat() {
        return mHeatingCapacity;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case MACHINEMODE_DEHYDRATOR -> RecipeMaps.chemicalDehydratorNonCellRecipes;
            case MACHINEMODE_COLD_TRAP -> RecipeMaps.coldTrapRecipes;
            case MACHINEMODE_NUCLEAR_SALT -> RecipeMaps.nuclearSaltProcessingPlantRecipes;
            default -> RecipeMaps.vacuumFurnaceRecipes;
        };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.info.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.info.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.info.2"))
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
            .beginStructureBlock(19, 14, 27, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.casing"))
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.casing"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.casing"))
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.casing"))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            RecipeMaps.chemicalDehydratorNonCellRecipes,
            RecipeMaps.vacuumFurnaceRecipes,
            RecipeMaps.coldTrapRecipes,
            RecipeMaps.nuclearSaltProcessingPlantRecipes);
    }

}
