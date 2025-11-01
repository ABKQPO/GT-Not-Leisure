package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.blockCustomMachineCasings;
import static gtnhlanth.common.register.LanthItemList.FOCUS_MANIPULATION_CASING;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class HandOfJohnDavisonRockefeller extends WirelessEnergyMultiMachineBase<HandOfJohnDavisonRockefeller>
    implements ISurvivalConstructable {

    public int mSpeedCount = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String HODR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/hand_of_john_davison_rockefeller";
    private static final String[][] shape = StructureUtils.readStructureFromFile(HODR_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 20;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;

    public HandOfJohnDavisonRockefeller(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public HandOfJohnDavisonRockefeller(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HandOfJohnDavisonRockefeller(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAChemicalPlantActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAChemicalPlant)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings10, 3);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.chemicalPlantRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("HandOfJohnDavisonRockefellerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_04"))
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
            .beginStructureBlock(41, 9, 9, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_Casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<HandOfJohnDavisonRockefeller> getStructureDefinition() {
        return StructureDefinition.<HandOfJohnDavisonRockefeller>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', ofBlock(BlockLoader.metaCasing, 4))
            .addElement('C', ofBlockAnyMeta(FOCUS_MANIPULATION_CASING))
            .addElement(
                'D',
                buildHatchAdder(HandOfJohnDavisonRockefeller.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        Maintenance,
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings10, 3))))
            .addElement('E', ofBlock(sBlockCasings10, 8))
            .addElement('F', ofBlock(sBlockCasings3, 10))
            .addElement('G', ofBlock(sBlockCasings8, 2))
            .addElement('H', ofFrame(Materials.Tungsten))
            .addElement('I', ofBlock(blockCustomMachineCasings, 3))
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 80;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mSpeedCount = 0;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        mSpeedCount = mGlassTier + GTUtility.getTier(this.getMaxInputVoltage());
    }

    @Override
    public double getEUtDiscount() {
        double discount = 1.0;
        for (int i = 0; i < mSpeedCount; i++) {
            discount *= 0.95;
        }
        return super.getEUtDiscount() * discount;
    }

    @Override
    public double getDurationModifier() {
        double speedBoost = 1.0;
        for (int i = 0; i < mSpeedCount; i++) {
            speedBoost -= 0.025;
            if (speedBoost < 0.1) {
                speedBoost = 0.1;
                break;
            }
        }
        return super.getDurationModifier() * speedBoost;
    }

}
