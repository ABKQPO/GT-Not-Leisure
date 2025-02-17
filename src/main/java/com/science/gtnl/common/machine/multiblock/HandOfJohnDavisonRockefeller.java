package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.common.block.Casings.BasicBlocks.MetaCasing;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.core.block.ModBlocks.blockCustomMachineCasings;
import static gtnhlanth.common.register.LanthItemList.FOCUS_MANIPULATION_CASING;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.Utils.item.TextUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

import bartworks.API.BorosilicateGlass;
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

public class HandOfJohnDavisonRockefeller extends MultiMachineBase<HandOfJohnDavisonRockefeller>
    implements ISurvivalConstructable {

    public int mCasing;
    public byte glassTier = 0;
    public int EUtDiscount = 0;
    public int SpeedBoost = 0;
    public static IStructureDefinition<HandOfJohnDavisonRockefeller> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static String[][] shape;
    public static final String HODR_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/hand_of_john_davison_rockefeller";
    public final int horizontalOffSet = 20;
    public final int verticalOffSet = 4;
    public final int depthOffSet = 0;
    public static final int CASING_INDEX = ((BlockCasings10) sBlockCasings10).getTextureIndex(3);

    public HandOfJohnDavisonRockefeller(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        shape = StructureUtils.readStructureFromFile(HODR_STRUCTURE_FILE_PATH);
    }

    public HandOfJohnDavisonRockefeller(String aName) {
        super(aName);
        shape = StructureUtils.readStructureFromFile(HODR_STRUCTURE_FILE_PATH);
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return GTUtility.getTier(this.getMaxInputVoltage()) > 11;
    }

    @Override
    public float getSpeedBonus() {
        return 1;
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
        tt.addMachineType(TextLocalization.HandOfJohnDavisonRockefellerRecipeType)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_00)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_01)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_02)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_03)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_04)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_05)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_06)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_07)
            .addInfo(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_08)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(41, 9, 9, true)
            .addInputHatch(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_Casing)
            .addOutputHatch(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_Casing)
            .addInputBus(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_Casing)
            .addOutputBus(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_Casing)
            .addEnergyHatch(TextLocalization.Tooltip_HandOfJohnDavisonRockefeller_Casing)
            .toolTipFinisher(TextUtils.SCIENCE_NOT_LEISURE + TextUtils.SQY);
        return tt;
    }

    @Override
    public IStructureDefinition<HandOfJohnDavisonRockefeller> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<HandOfJohnDavisonRockefeller>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    withChannel(
                        "glass",
                        BorosilicateGlass.ofBoroGlass(
                            (byte) 0,
                            (byte) 1,
                            Byte.MAX_VALUE,
                            (te, t) -> te.glassTier = t,
                            te -> te.glassTier)))
                .addElement('B', ofBlock(MetaCasing, 4))
                .addElement('C', ofBlockAnyMeta(FOCUS_MANIPULATION_CASING))
                .addElement(
                    'D',
                    buildHatchAdder(HandOfJohnDavisonRockefeller.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.mCasing, ofBlock(sBlockCasings10, 3))))
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
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCasing = 0;
        glassTier = 0;
        EUtDiscount = 0;
        SpeedBoost = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet) && checkHatch()) {
            return false;
        }

        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            EUtDiscount = glassTier + GTUtility.getTier(this.getMaxInputVoltage());
            SpeedBoost = glassTier + GTUtility.getTier(this.getMaxInputVoltage());

            if (glassTier < VoltageIndex.UEV & mEnergyHatch.mTier > glassTier - 1) {
                return false;
            }
        }
        return mCasing >= 80;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 16 + 4 * GTUtility.getTier(this.getMaxInputVoltage()) + 2 * glassTier;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUtDiscount(1 - EUtDiscount / 100.0)
                    .setSpeedBoost(1 - SpeedBoost / 50.0);
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
}
