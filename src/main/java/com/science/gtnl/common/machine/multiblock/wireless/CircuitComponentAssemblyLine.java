package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static goodgenerator.loader.Loaders.FRF_Coil_1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.blockCasings3Misc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.BlockGTCasingsTT;

public class CircuitComponentAssemblyLine extends WirelessEnergyMultiMachineBase<CircuitComponentAssemblyLine>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String ACAL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/advanced_circuit_assembly_line";
    private static final String[][] shape = StructureUtils.readStructureFromFile(ACAL_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 0;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private float speedup = 1;
    public int casingTier;
    private int runningTickCounter = 0;

    public CircuitComponentAssemblyLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public CircuitComponentAssemblyLine(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new CircuitComponentAssemblyLine(this.mName);
    }

    @Override
    public IStructureDefinition<CircuitComponentAssemblyLine> getStructureDefinition() {
        return StructureDefinition.<CircuitComponentAssemblyLine>builder()
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
                buildHatchAdder(CircuitComponentAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        Maintenance,
                        InputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
            .addElement('H', ofBlock(sBlockCasingsTT, 7))
            .addElement('I', ofBlock(sBlockCasingsTT, 8))
            .addElement('J', ofBlock(blockCasings3Misc, 15))
            .addElement('K', ofBlock(BlockQuantumGlass.INSTANCE, 0))
            .build();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("CircuitComponentAssemblyLineRecipes"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(32, 5, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .toolTipFinisher();
        return tt;
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
        return BlockGTCasingsTT.textureOffset + 3;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public double getDurationModifier() {
        return 1F / speedup;
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 36.0);
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
        mCountCasing = 0;
        mEnergyHatchTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }

        mParallelTier = getParallelTier(aStack);
        mEnergyHatchTier = checkEnergyHatchTier();

        return mCountCasing >= 30;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.CircuitComponentAssemblyLineRecipes;
    }

}
