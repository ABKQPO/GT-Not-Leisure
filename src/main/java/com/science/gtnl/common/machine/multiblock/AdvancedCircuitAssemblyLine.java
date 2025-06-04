package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static goodgenerator.loader.Loaders.FRF_Coil_1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.blockCasings3Misc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.loader.RecipeRegister;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.BlockGTCasingsTT;

public class AdvancedCircuitAssemblyLine extends GTMMultiMachineBase<AdvancedCircuitAssemblyLine>
    implements ISurvivalConstructable {

    private static IStructureDefinition<AdvancedCircuitAssemblyLine> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String ACAL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/advanced_circuit_assembly_line";
    private static final int CASING_INDEX = BlockGTCasingsTT.textureOffset + 3;
    private static final String[][] shape = StructureUtils.readStructureFromFile(ACAL_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 0;
    public final int VERTICAL_OFF_SET = 2;
    public final int DEPTH_OFF_SET = 0;
    private float speedup = 1;
    public int casingTier;
    private int runningTickCounter = 0;

    public AdvancedCircuitAssemblyLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public AdvancedCircuitAssemblyLine(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new AdvancedCircuitAssemblyLine(this.mName);
    }

    @Override
    public IStructureDefinition<AdvancedCircuitAssemblyLine> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<AdvancedCircuitAssemblyLine>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(FRF_Coil_1, 0))
                .addElement(
                    'B',
                    ofBlocksTiered(
                        (block, meta) -> block == Loaders.componentAssemblylineCasing ? meta : -1,
                        IntStream.range(0, 14)
                            .mapToObj(i -> Pair.of(Loaders.componentAssemblylineCasing, i))
                            .collect(Collectors.toList()),
                        -2,
                        (t, meta) -> t.casingTier = meta,
                        t -> t.casingTier))
                .addElement('C', ofBlock(sBlockCasings2, 5))
                .addElement('D', ofBlock(sBlockCasingsTT, 0))
                .addElement('E', ofBlock(sBlockCasingsTT, 1))
                .addElement('F', ofBlock(sBlockCasingsTT, 2))
                .addElement(
                    'G',
                    buildHatchAdder(AdvancedCircuitAssemblyLine.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasingsTT, 3))))
                .addElement('H', ofBlock(sBlockCasingsTT, 7))
                .addElement('I', ofBlock(sBlockCasingsTT, 8))
                .addElement('J', ofBlock(blockCasings3Misc, 15))
                .addElement('K', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("AdvancedCircuitAssemblyLineRecipes"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(32, 5, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_Casing_00"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_Casing_00"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_Casing_00"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_Casing_00"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_AdvancedCircuitAssemblyLine_Casing_00"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setAmperageOC(true)
                    .setDurationDecreasePerOC(2)
                    .setEUtIncreasePerOC(4)
                    .setAmperage(availableAmperage)
                    .setRecipeEUt(recipe.mEUt)
                    .setEUt(availableVoltage)
                    .setEUtDiscount(0.8 - (mParallelTier / 36.0));
                // .setDurationModifier(Math.max(0.05, 1.0 / 3.75 - (ParallelTier / 200.0)));
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                setSpeedBonus(1F / speedup);
                return super.validateRecipe(recipe);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes)
            .setEuModifier(0.8F);

    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        runningTickCounter++;
        if (runningTickCounter % 10 == 0 && speedup < 10) {
            runningTickCounter = 0;
            speedup += 0.15F;
        }
        return super.onRunningTick(aStack);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (!aBaseMetaTileEntity.isServerSide()) return;
        if (mMaxProgresstime == 0 && speedup > 1) {
            if (aTick % 5 == 0) {
                speedup = (float) Math.max(1, speedup - 0.025);
            }
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        return GTUtility.getTier(this.getMaxInputVoltage()) * 32 + 8;
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
    public boolean checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack) {
        mParallelTier = 0;
        tCountCasing = 0;
        energyHatchTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()) {
            return false;
        }

        mParallelTier = getParallelTier(aStack);

        return tCountCasing >= 30;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.AdvancedCircuitAssemblyLineRecipes;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 2;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(false);
    }

}
