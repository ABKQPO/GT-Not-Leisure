package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.blockCasings2Misc;
import static gtPlusPlus.core.block.ModBlocks.blockCasings3Misc;
import static gtnhlanth.common.register.LanthItemList.ELECTRODE_CASING;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.common.recipe.RecipeRegister;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;

public class FuelRefiningComplex extends GTMMultiMachineBase<FuelRefiningComplex> implements ISurvivalConstructable {

    private HeatingCoilLevel mCoilLevel;
    private int mHeatingCapacity = 0;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<FuelRefiningComplex> STRUCTURE_DEFINITION = null;
    public static final String FRC_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/fuel_refining_complex";
    public static final int CASING_INDEX = TAE.GTPP_INDEX(33);
    public final int horizontalOffSet = 8;
    public final int verticalOffSet = 12;
    public final int depthOffSet = 0;
    public static String[][] shape = StructureUtils.readStructureFromFile(FRC_STRUCTURE_FILE_PATH);

    public FuelRefiningComplex(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FuelRefiningComplex(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FuelRefiningComplex(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
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
        return RecipeRegister.FuelRefiningComplexRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.FuelRefiningComplexRecipeType)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_02)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_03)
            .addInfo(TextLocalization.Tooltip_Tectech_Hatch)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(17, 14, 16, true)
            .addInputHatch(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addOutputHatch(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addInputBus(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addOutputBus(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addEnergyHatch(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addMaintenanceHatch(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .toolTipFinisher();
        return tt;
    }

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        this.mCoilLevel = aCoilLevel;
    }

    public HeatingCoilLevel getCoilLevel() {
        return this.mCoilLevel;
    }

    @Override
    public IStructureDefinition<FuelRefiningComplex> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<FuelRefiningComplex>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlockAnyMeta(GameRegistry.findBlock(IndustrialCraft2.ID, "blockAlloyGlass")))
                .addElement('B', ofBlockAnyMeta(ELECTRODE_CASING))
                .addElement('C', ofBlock(sBlockCasings2, 5))
                .addElement('D', ofBlock(sBlockCasings4, 0))
                .addElement('E', ofBlock(sBlockCasings4, 1))
                .addElement('F', ofCoil(FuelRefiningComplex::setCoilLevel, FuelRefiningComplex::getCoilLevel))
                .addElement('G', ofBlock(sBlockCasings6, 6))
                .addElement('H', ofBlock(sBlockCasings8, 0))
                .addElement('I', ofBlock(sBlockCasings8, 1))
                .addElement('J', ofFrame(Materials.TungstenSteel))
                .addElement('K', ofBlock(blockCasings2Misc, 4))
                .addElement(
                    'L',
                    buildHatchAdder(FuelRefiningComplex.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputBus, InputHatch, OutputHatch, Maintenance, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.mCasing, ofBlock(blockCasings3Misc, 1))))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCasing = 0;
        ParallelTier = 0;
        this.mHeatingCapacity = 0;
        this.setCoilLevel(HeatingCoilLevel.None);

        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet) && !checkHatch()) {
            return false;
        }
        if (getCoilLevel() == HeatingCoilLevel.None) return false;
        this.mHeatingCapacity = (int) getCoilLevel().getHeat();

        ParallelTier = getParallelTier(aStack);

        return mCasing >= 245;
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
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(FuelRefiningComplex.this.mHeatingCapacity)
                    .setEUtDiscount(0.8 - (ParallelTier / 50.0))
                    .setSpeedBoost(0.6 - (ParallelTier / 200.0));
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                return recipe.mSpecialValue <= FuelRefiningComplex.this.mHeatingCapacity
                    ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
}
