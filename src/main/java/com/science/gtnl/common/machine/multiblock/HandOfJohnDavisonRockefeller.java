package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.blockCustomMachineCasings;
import static gtnhlanth.common.register.LanthItemList.FOCUS_MANIPULATION_CASING;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings10;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class HandOfJohnDavisonRockefeller extends WirelessEnergyMultiMachineBase<HandOfJohnDavisonRockefeller>
    implements ISurvivalConstructable {

    public int mSpeedCount = 0;
    public static IStructureDefinition<HandOfJohnDavisonRockefeller> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String HODR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/hand_of_john_davison_rockefeller";
    public static String[][] shape = StructureUtils.readStructureFromFile(HODR_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 20;
    public final int VERTICAL_OFF_SET = 4;
    public final int DEPTH_OFF_SET = 0;
    public static final int CASING_INDEX = ((BlockCasings10) sBlockCasings10).getTextureIndex(3);

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
        return CASING_INDEX;
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
            .addInfo(StatCollector.translateToLocal("Tooltip_HandOfJohnDavisonRockefeller_05"))
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
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<HandOfJohnDavisonRockefeller> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<HandOfJohnDavisonRockefeller>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
                .addElement('B', ofBlock(BlockLoader.MetaCasing, 4))
                .addElement('C', ofBlockAnyMeta(FOCUS_MANIPULATION_CASING))
                .addElement(
                    'D',
                    buildHatchAdder(HandOfJohnDavisonRockefeller.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings10, 3))))
                .addElement('E', ofBlock(sBlockCasings10, 8))
                .addElement('F', ofBlock(sBlockCasings3, 10))
                .addElement('G', ofBlock(sBlockCasings8, 2))
                .addElement('H', ofFrame(Materials.Tungsten))
                .addElement('I', ofBlock(blockCustomMachineCasings, 3))
                .build();
        }
        return STRUCTURE_DEFINITION;
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
        tCountCasing = 0;
        mGlassTier = 0;
        mSpeedCount = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()) {
            return false;
        }

        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            mSpeedCount = mGlassTier + GTUtility.getTier(this.getMaxInputVoltage());

            if (mGlassTier < VoltageIndex.UEV & mEnergyHatch.mTier > mGlassTier - 1) {
                return false;
            }
        }
        energyHatchTier = checkEnergyHatchTier();
        wirelessMode = mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
        return tCountCasing >= 80;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 16 + 4 * GTUtility.getTier(this.getMaxInputVoltage()) + 2 * mGlassTier;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4 && wirelessMode) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                enablePerfectOverclock();
                return super.process();
            }

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUtDiscount(calculateEUtDiscount(mSpeedCount))
                    .setDurationModifier(calculateSpeedBoost(mSpeedCount));
            }

            private double calculateEUtDiscount(double levels) {
                double discount = 1.0;
                for (int i = 0; i < levels; i++) {
                    discount *= 0.95;
                }
                return (0.4 - (mParallelTier / 50.0)) * discount;
            }

            private double calculateSpeedBoost(double levels) {
                double speedBoost = 1.0;
                for (int i = 0; i < levels; i++) {
                    speedBoost -= 0.025;
                    if (speedBoost < 0.1) {
                        speedBoost = 0.1;
                        break;
                    }
                }
                return (0.1 * Math.pow(0.75, mParallelTier)) * speedBoost;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

}
