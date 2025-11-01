package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.dreammaster.fluids.FluidList;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import bartworks.util.BWUtil;
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
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;

public class Digester extends GTMMultiMachineBase<Digester> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String D_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/digester";
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(D_STRUCTURE_FILE_PATH);

    public Digester(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public Digester(String aName) {
        super(aName);
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
        tt.addMachineType(StatCollector.translateToLocal("DigesterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Digester_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Digester_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(7, 4, 7, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_Digester_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_Digester_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_Digester_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_Digester_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_Digester_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_Digester_Casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<Digester> getStructureDefinition() {
        return StructureDefinition.<Digester>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings1, 11))
            .addElement(
                'B',
                buildHatchAdder(Digester.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings4, 0))))
            .addElement('C', ofBlock(sBlockCasings4, 1))
            .addElement(
                'D',
                GTStructureChannels.HEATING_COIL
                    .use(activeCoils(ofCoil(Digester::setMCoilLevel, Digester::getMCoilLevel))))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 45;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        this.mHeatingCapacity = (int) this.getMCoilLevel()
            .getHeat() + 100 * (BWUtil.getTier(this.getMaxInputEu()) - 2);
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
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            protected GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setMachineHeat(getMachineHeat())
                    .setHeatOC(getHeatOC())
                    .setHeatDiscount(getHeatDiscount())
                    .setPerfectOC(getPerfectOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (checkForNitricAcid()) {
                    return recipe.mSpecialValue <= getMCoilLevel().getHeat() ? CheckRecipeResultRegistry.SUCCESSFUL
                        : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
                }
                return SimpleCheckRecipeResult.ofFailure("no_nitricacid");
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 - ((mParallelTier + mHeatingCapacity / 1800.0) / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1 / 1.67 - ((mParallelTier + mHeatingCapacity / 1800.0) / 200.0);
    }

    @Override
    public int getMachineHeat() {
        return mHeatingCapacity;
    }

    @Override
    public boolean getHeatOC() {
        return true;
    }

    @Override
    public boolean getHeatDiscount() {
        return true;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings4, 0);
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
            ScienceNotLeisure.LOG.warn("Filled structure.");
        } else {
            ScienceNotLeisure.LOG.warn("Did not fill structure.");
        }
        return isValidFluid;
    }
}
