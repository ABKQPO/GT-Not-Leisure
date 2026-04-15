package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gtPlusPlus.core.block.ModBlocks.blockCasings2Misc;

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
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TAE;
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
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtnhlanth.common.register.LanthItemList;

public class FastNeutronBreederReactor extends WirelessEnergyMultiMachineBase<FastNeutronBreederReactor> {

    private static final int MACHINEMODE_DECAY = 0;
    private static final int MACHINEMODE_NEUTRON = 1;
    private static final int MACHINEMODE_PARTICLE = 2;
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 22;
    private static final int DEPTH_OFF_SET = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String FNBR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/fast_neutron_breeder_reactor";
    private static final String[][] shape = StructureUtils.readStructureFromFile(FNBR_STRUCTURE_FILE_PATH);

    public FastNeutronBreederReactor(String aName) {
        super(aName);
    }

    public FastNeutronBreederReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FastNeutronBreederReactor(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("FastNeutronBreederReactorRecipeType"))
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
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(15, 24, 15, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_FastNeutronBreederReactor_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_FastNeutronBreederReactor_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_FastNeutronBreederReactor_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_FastNeutronBreederReactor_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_FastNeutronBreederReactor_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return TAE.getIndexFromPage(0, 10);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_NEUTRON_ACTIVATOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_NEUTRON_ACTIVATOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_NEUTRON_ACTIVATOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_NEUTRON_ACTIVATOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<FastNeutronBreederReactor> getStructureDefinition() {
        return StructureDefinition.<FastNeutronBreederReactor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 7))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSE, 1))
            .addElement('C', StructureUtility.ofBlockAnyMeta(LanthItemList.COOLANT_DELIVERY_CASING))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 7))
            .addElement(
                'E',
                GTStructureUtility.buildHatchAdder(FastNeutronBreederReactor.class)
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
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(blockCasings2Misc, 12))))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement('H', StructureUtility.ofBlock(Loaders.MAR_Casing, 0))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 9))
            .addElement('J', StructureUtility.ofBlockAnyMeta(LanthItemList.SHIELDED_ACCELERATOR_CASING))
            .addElement('K', StructureUtility.ofBlock(blockCasings2Misc, 9))
            .addElement('L', StructureUtility.ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 15))
            .addElement('N', GTStructureUtility.ofFrame(Materials.TungstenCarbide))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 7))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mCountCasing > 50;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (machineMode == MACHINEMODE_DECAY) {
            return GTNLRecipeMaps.DecayHastenerRecipes;
        } else if (machineMode == MACHINEMODE_NEUTRON) {
            return GTNLRecipeMaps.ElectricNeutronActivatorRecipes;
        } else {
            return GTPPRecipeMaps.cyclotronRecipes;
        }
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            GTNLRecipeMaps.DecayHastenerRecipes,
            GTNLRecipeMaps.ElectricNeutronActivatorRecipes,
            GTPPRecipeMaps.cyclotronRecipes);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == MACHINEMODE_DECAY) {
            return MACHINEMODE_NEUTRON;
        } else if (machineMode == MACHINEMODE_NEUTRON) {
            return MACHINEMODE_PARTICLE;
        } else {
            return MACHINEMODE_DECAY;
        }
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 3;
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("FastNeutronBreederReactor_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("FastNeutronBreederReactor_Mode_" + machineMode);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_UNPACKAGER);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

}
