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

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;

public class MegaBrewer extends WirelessEnergyMultiMachineBase<MegaBrewer> {

    private static final int MACHINEMODE_FREWERY = 0;
    private static final int MACHINEMODE_FERMENTER = 1;
    private static final int HORIZONTAL_OFF_SET = 20;
    private static final int VERTICAL_OFF_SET = 20;
    private static final int DEPTH_OFF_SET = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/mega_brewer";
    private static final String[][] shape = StructureUtils.readStructureFromFile(MB_STRUCTURE_FILE_PATH);

    public MegaBrewer(String aName) {
        super(aName);
    }

    public MegaBrewer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MegaBrewer(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MegaBrewerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaBrewer_00"))
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
            .beginStructureBlock(28, 22, 15, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_MegaBrewer_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MegaBrewer_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_MegaBrewer_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_MegaBrewer_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MegaBrewer_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(1);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_BREWERY_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_BREWERY_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_BREWERY)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_BREWERY_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<MegaBrewer> getStructureDefinition() {
        return StructureDefinition.<MegaBrewer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(sBlockCasingsTT, 0))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 10))
            .addElement('C', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('D', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement(
                'F',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility
                        .activeCoils(GTStructureUtility.ofCoil(MegaBrewer::setMCoilLevel, MegaBrewer::getMCoilLevel))))
            .addElement('G', StructureUtility.ofBlock(BlockLoader.metaCasing, 7))
            .addElement(
                'H',
                GTStructureUtility.buildHatchAdder(MegaBrewer.class)
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
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 11))))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement('J', GTStructureUtility.ofFrame(Materials.Neutronium))
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
    public boolean checkHatch() {
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_FREWERY) ? RecipeMaps.brewingRecipes : RecipeMaps.fermentingRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.brewingRecipes, RecipeMaps.fermentingRecipes);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 2;
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("MegaBrewer_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("MegaBrewer_Mode_" + machineMode);
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount();
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.85, getMCoilLevel().getTier());
    }

}
