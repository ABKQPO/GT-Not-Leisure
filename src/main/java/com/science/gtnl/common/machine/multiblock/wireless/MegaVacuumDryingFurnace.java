package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import bartworks.util.BWUtil;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import tectech.thing.casing.BlockGTCasingsTT;

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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MegaVacuumDryingFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(19, 14, 27, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_MegaVacuumDryingFurnace_Casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
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
                    .dot(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 0))))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 6))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement(
                'E',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility
                            .ofCoil(MegaVacuumDryingFurnace::setMCoilLevel, MegaVacuumDryingFurnace::getMCoilLevel))))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 15))
            .addElement('G', StructureUtility.ofBlock(sBlockCasingsTT, 1))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockMetal4, 12))
            .addElement('J', StructureUtility.ofBlock(sBlockCasingsTT, 2))
            .addElement('K', StructureUtility.ofBlock(ModBlocks.blockCasings4Misc, 10))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 11))
            .addElement('M', StructureUtility.ofBlock(BlockLoader.metaCasing, 12))
            .addElement('N', GTStructureUtility.ofFrame(Materials.Tungsten))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 10))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing > 1;
    }

    @Override
    public void setMachineModeIcons() {
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
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("MegaVacuumDryingFurnace_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("MegaVacuumDryingFurnace_Mode_" + machineMode);
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
            case MACHINEMODE_DEHYDRATOR -> GTPPRecipeMaps.chemicalDehydratorNonCellRecipes;
            case MACHINEMODE_COLD_TRAP -> GTPPRecipeMaps.coldTrapRecipes;
            case MACHINEMODE_NUCLEAR_SALT -> GTPPRecipeMaps.nuclearSaltProcessingPlantRecipes;
            default -> GTPPRecipeMaps.vacuumFurnaceRecipes;
        };
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            GTPPRecipeMaps.chemicalDehydratorNonCellRecipes,
            GTPPRecipeMaps.vacuumFurnaceRecipes,
            GTPPRecipeMaps.coldTrapRecipes,
            GTPPRecipeMaps.nuclearSaltProcessingPlantRecipes);
    }

}
