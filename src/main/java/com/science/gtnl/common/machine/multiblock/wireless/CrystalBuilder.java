package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.*;
import static gtnhlanth.common.register.LanthItemList.*;
import static tectech.thing.casing.TTCasingsContainer.*;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.TTCasingsContainer;

public class CrystalBuilder extends WirelessEnergyMultiMachineBase<CrystalBuilder> {

    private static final int HORIZONTAL_OFF_SET = 11;
    private static final int VERTICAL_OFF_SET = 31;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String CB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/crystal_builder";
    private static final String[][] shape = StructureUtils.readStructureFromFile(CB_STRUCTURE_FILE_PATH);

    public CrystalBuilder(String aName) {
        super(aName);
    }

    public CrystalBuilder(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new CrystalBuilder(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("CrystalBuilderRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CrystalBuilder_00"))
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
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(23, 34, 20, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_CrystalBuilder_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_CrystalBuilder_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_CrystalBuilder_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_CrystalBuilder_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_CrystalBuilder_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return 1028;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_AUTOCLAVE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_AUTOCLAVE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_AUTOCLAVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_AUTOCLAVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<CrystalBuilder> getStructureDefinition() {
        return StructureDefinition.<CrystalBuilder>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlockAnyMeta(ELECTRODE_CASING))
            .addElement('B', ofBlock(sBlockCasingsTT, 0))
            .addElement('C', ofBlock(BlockLoader.metaCasing, 18))
            .addElement('D', ofBlock(sBlockCasings10, 3))
            .addElement('E', ofBlock(sBlockCasingsTT, 7))
            .addElement(
                'F',
                GTStructureChannels.HEATING_COIL
                    .use(activeCoils(ofCoil(CrystalBuilder::setMCoilLevel, CrystalBuilder::getMCoilLevel))))
            .addElement('G', ofBlock(sBlockCasings10, 2))
            .addElement('H', ofBlock(blockCasingsMisc, 5))
            .addElement('I', ofBlock(sBlockCasingsDyson, 9))
            .addElement(
                'J',
                buildHatchAdder(CrystalBuilder.class)
                    .atLeast(
                        Maintenance,
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        onElementPass(x -> ++x.mCountCasing, ofBlock(TTCasingsContainer.sBlockCasingsTT, 4))))
            .addElement('K', ofBlock(sBlockCasings9, 12))
            .addElement('L', ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('M', ofBlock(sBlockCasingsTT, 6))
            .addElement('N', ofBlock(sBlockCasings3, 10))
            .addElement('O', ofBlock(sBlockCasings4, 14))
            .addElement('P', ofBlock(BlockQuantumGlass.INSTANCE, 0))
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
        return mCountCasing > 100;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.autoclaveRecipes;
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
