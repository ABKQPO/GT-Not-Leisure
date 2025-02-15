package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.dreammaster.fluids.FluidList;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.Utils.item.TextUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;

import bartworks.util.BWUtil;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings4;
import gtPlusPlus.api.objects.Logger;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;

public class Digester extends GTMMultiMachineBase<Digester> implements ISurvivalConstructable {

    public static final int CASING_INDEX = ((BlockCasings4) GregTechAPI.sBlockCasings4).getTextureIndex(0);
    private int mHeatingCapacity = 0;
    private HeatingCoilLevel heatLevel;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<Digester> STRUCTURE_DEFINITION = null;
    public static final String D_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/digester";
    public final int horizontalOffSet = 3;
    public final int verticalOffSet = 3;
    public final int depthOffSet = 0;
    public static String[][] shape;

    public Digester(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        shape = StructureUtils.readStructureFromFile(D_STRUCTURE_FILE_PATH);
    }

    public Digester(String aName) {
        super(aName);
        shape = StructureUtils.readStructureFromFile(D_STRUCTURE_FILE_PATH);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new Digester(this.mName);
    }

    public ITexture[] getTexture(IGregTechTileEntity te, ForgeDirection side, ForgeDirection facing, int colorIndex,
        boolean active, boolean redstone) {
        if (side == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_OIL_CRACKER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_OIL_CRACKER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return LanthanidesRecipeMaps.digesterRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.DigesterRecipeType)
            .addInfo(TextLocalization.Tooltip_Digester_00)
            .addInfo(TextLocalization.Tooltip_Digester_01)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_00)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_01)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_02)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_03)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(5, 5, 5, true)
            .addInputHatch(TextLocalization.Tooltip_Digester_Casing)
            .addOutputHatch(TextLocalization.Tooltip_Digester_Casing)
            .addInputBus(TextLocalization.Tooltip_Digester_Casing)
            .addOutputBus(TextLocalization.Tooltip_Digester_Casing)
            .addEnergyHatch(TextLocalization.Tooltip_Digester_Casing)
            .addMaintenanceHatch(TextLocalization.Tooltip_Digester_Casing)
            .toolTipFinisher(TextUtils.SNL + TextUtils.SRP);
        return tt;
    }

    @Override
    public IStructureDefinition<Digester> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<Digester>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(sBlockCasings1, 11))
                .addElement(
                    'B',
                    buildHatchAdder(Digester.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance, Energy)
                        .buildAndChain(onElementPass(x -> ++x.mCasing, ofBlock(sBlockCasings4, 0))))
                .addElement('C', ofBlock(sBlockCasings4, 1))
                .addElement('D', ofCoil(Digester::setCoilLevel, Digester::getCoilLevel))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCasing = 0;
        ParallelTier = 0;

        if (checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet) && checkHatch()
            && mCasing >= 45) {
            this.mHeatingCapacity = (int) this.getCoilLevel()
                .getHeat() + 100 * (BWUtil.getTier(this.getMaxInputEu()) - 2);
            ParallelTier = getParallelTier(aStack);
            return true;
        } else {
            return false;
        }
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

    public HeatingCoilLevel getCoilLevel() {
        return this.heatLevel;
    }

    public void setCoilLevel(HeatingCoilLevel level) {
        this.heatLevel = level;
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setMachineHeat(Digester.this.mHeatingCapacity)
                    .setHeatOC(true)
                    .setHeatDiscount(true)
                    .enablePerfectOC()
                    .setEUtDiscount(0.8 - ((ParallelTier + mHeatingCapacity / 1800.0) / 50.0))
                    .setSpeedBoost(0.6 - ((ParallelTier + mHeatingCapacity / 1800.0) / 200.0));
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (checkForNitricAcid()) {
                    return recipe.mSpecialValue <= Digester.this.getCoilLevel()
                        .getHeat() ? CheckRecipeResultRegistry.SUCCESSFUL
                            : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
                }
                return SimpleCheckRecipeResult.ofFailure("no_nitricacid");
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    public boolean checkForNitricAcid() {
        IGregTechTileEntity aBaseMetaTileEntity = this.getBaseMetaTileEntity();
        ForgeDirection backFacing = aBaseMetaTileEntity.getBackFacing();
        ForgeDirection leftDir = backFacing.getRotation(ForgeDirection.UP);

        int tAmount = 0;

        for (int stepBack = 5; stepBack >= 1; stepBack--) {
            int mainX = backFacing.offsetX * stepBack;
            int mainZ = backFacing.offsetZ * stepBack;

            for (int stepLeft = -2; stepLeft <= 2; stepLeft++) {
                int sideX = leftDir.offsetX * stepLeft;
                int sideZ = leftDir.offsetZ * stepLeft;

                for (int stepUp = 1; stepUp <= 2; stepUp++) {
                    int x = aBaseMetaTileEntity.getXCoord() + mainX + sideX;
                    int y = aBaseMetaTileEntity.getYCoord() + stepUp;
                    int z = aBaseMetaTileEntity.getZCoord() + mainZ + sideZ;

                    Block tBlock = aBaseMetaTileEntity.getBlock(x, y, z);
                    int metadata = aBaseMetaTileEntity.getMetaID(x, y, z);

                    if (tBlock == Blocks.air || (tBlock == FluidList.NitricAcid.Fluid && metadata != 0)) {
                        if (this.getStoredFluids() != null) {
                            for (FluidStack stored : this.getStoredFluids()) {
                                if (stored.isFluidEqual(FluidUtils.getFluidStack("nitricacid", 1))) {
                                    if (stored.amount >= 1000) {
                                        stored.amount -= 1000;
                                        Block fluidUsed = null;
                                        if (tBlock == Blocks.air
                                            || (tBlock == FluidList.NitricAcid.Fluid && metadata != 0)) {
                                            fluidUsed = FluidList.NitricAcid.Fluid;
                                        } else if (tBlock == Blocks.water) {
                                            fluidUsed = BlocksItems.getFluidBlock(InternalName.fluidDistilledWater);
                                        }
                                        aBaseMetaTileEntity.getWorld()
                                            .setBlock(x, y, z, fluidUsed, 0, 3);
                                    }
                                }
                            }
                        }
                    }
                    if (tBlock == FluidList.NitricAcid.Fluid && metadata == 0) {
                        ++tAmount;
                    }
                }
            }
        }

        boolean isValidFluid = tAmount >= 42;
        if (isValidFluid) {
            Logger.WARNING("Filled structure.");
        } else {
            Logger.WARNING("Did not fill structure.");
        }
        return isValidFluid;
    }
}
