package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import bartworks.util.BWUtil;
import cpw.mods.fml.common.Optional;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Mods;
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
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
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

    @Override
    public ITexture[] getTexture(IGregTechTileEntity te, ForgeDirection side, ForgeDirection facing, int colorIndex,
        boolean active, boolean redstone) {
        if (side == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_GLOW)
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
            .addMultiAmpHatchInfo()
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
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 11))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(Digester.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 0))))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement(
                'D',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility
                        .activeCoils(GTStructureUtility.ofCoil(Digester::setMCoilLevel, Digester::getMCoilLevel))))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPieceAndHatch(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors))
            return;
        setupParameters();
        checkStructureCondition(errors, mCountCasing >= 45);
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
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setMachineHeat(getMachineHeat())
                    .setHeatOC(getHeatOC())
                    .setHeatDiscount(getHeatDiscount())
                    .setPerfectOC(getPerfectOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @Override
            public @NotNull CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (Mods.NewHorizonsCoreMod.isModLoaded() && !checkForNitricAcid())
                    return SimpleCheckRecipeResult.ofFailure("no_nitricacid");
                return recipe.mSpecialValue <= getMCoilLevel().getHeat() ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
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
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 0);
    }

    @Optional.Method(modid = "dreamcraft")
    public boolean checkForNitricAcid() {
        IGregTechTileEntity aBaseMetaTileEntity = this.getBaseMetaTileEntity();
        ForgeDirection backFacing = aBaseMetaTileEntity.getBackFacing();
        ForgeDirection leftDir = backFacing.getRotation(ForgeDirection.UP);
        FluidStack nitricAcidTemplate = getNitricAcidTemplate();
        Block nitricAcidBlock = getNitricAcidBlock();
        List<FluidStack> storedFluids = getStoredFluids();

        if (nitricAcidTemplate == null || nitricAcidBlock == null) {
            return false;
        }

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

                    if (tBlock == Blocks.air || (tBlock == nitricAcidBlock && metadata != 0)) {
                        if (storedFluids != null) {
                            for (FluidStack stored : storedFluids) {
                                if (stored.isFluidEqual(nitricAcidTemplate)) {
                                    if (stored.amount >= 1000) {
                                        stored.amount -= 1000;
                                        Block fluidUsed = null;
                                        if (tBlock == Blocks.air || (tBlock == nitricAcidBlock && metadata != 0)) {
                                            fluidUsed = nitricAcidBlock;
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
                    if (tBlock == nitricAcidBlock && metadata == 0) {
                        ++tAmount;
                    }
                }
            }
        }

        return tAmount >= 42;
    }

    public FluidStack getNitricAcidTemplate() {
        return FluidRegistry.getFluidStack("nitricacid", 1);
    }

    public Block getNitricAcidBlock() {
        // 按名称延迟解析可避免可选模组缺失时的类加载崩溃 / Resolve by registry name lazily to avoid classloading crashes when the optional mod
        // is absent.
        Fluid nitricAcid = FluidRegistry.getFluid("nitricacid");
        return nitricAcid == null ? null : nitricAcid.getBlock();
    }
}
