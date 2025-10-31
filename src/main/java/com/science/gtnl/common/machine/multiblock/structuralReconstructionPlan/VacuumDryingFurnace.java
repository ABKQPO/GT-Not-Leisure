package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import bartworks.util.BWUtil;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class VacuumDryingFurnace extends GTMMultiMachineBase<VacuumDryingFurnace> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String VDF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/vacuum_drying_furnace";
    private static final int MACHINEMODE_VACUUMFURNACE = 0;
    private static final int MACHINEMODE_DEHYDRATOR = 1;
    private static final int MACHINEMODE_COLD_TRAP = 2;
    private static final int MACHINEMODE_NUCLEAR_SALT = 3;
    protected final int HORIZONTAL_OFF_SET = 1;
    protected final int VERTICAL_OFF_SET = 4;
    protected final int DEPTH_OFF_SET = 0;
    public static final String[][] shape = StructureUtils.readStructureFromFile(VDF_STRUCTURE_FILE_PATH);

    public VacuumDryingFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public VacuumDryingFurnace(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new VacuumDryingFurnace(this.mName);
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
    public int getCasingTextureID() {
        return TAE.getIndexFromPage(3, 10);
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

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            GTPPRecipeMaps.chemicalDehydratorNonCellRecipes,
            GTPPRecipeMaps.vacuumFurnaceRecipes,
            GTPPRecipeMaps.coldTrapRecipes,
            GTPPRecipeMaps.nuclearSaltProcessingPlantRecipes);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("VacuumDryingFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(3, 5, 3, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_VacuumDryingFurnace_Casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<VacuumDryingFurnace> getStructureDefinition() {
        return StructureDefinition.<VacuumDryingFurnace>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.HEATING_COIL
                    .use(activeCoils(ofCoil(VacuumDryingFurnace::setMCoilLevel, VacuumDryingFurnace::getMCoilLevel))))
            .addElement(
                'B',
                buildHatchAdder(VacuumDryingFurnace.class)
                    .atLeast(
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(ModBlocks.blockCasings4Misc, 10))))
            .addElement('C', Muffler.newAny(getCasingTextureID(), 1))
            .build();
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
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (byte) ((this.machineMode + 1) % 4);
        GTUtility
            .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("VacuumDryingFurnace_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("VacuumDryingFurnace_Mode_" + machineMode);
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
    public double getEUtDiscount() {
        return 0.6 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1 / 2.5 - (Math.max(0, mParallelTier - 1) / 50.0) * ((machineMode >= 2) ? 1 : 0.1);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 5;
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
}
