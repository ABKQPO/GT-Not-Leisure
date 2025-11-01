package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static goodgenerator.loader.Loaders.compactFusionCoil;
import static goodgenerator.loader.Loaders.magneticFluxCasing;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.CommonElements;

import crazypants.enderio.EnderIO;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.INEIPreviewModifier;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import kubatech.loaders.BlockLoader;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.TTCasingsContainer;

public class GenerationEarthEngine extends MultiMachineBase<GenerationEarthEngine>
    implements ISurvivalConstructable, INEIPreviewModifier {

    private static final int HORIZONTAL_OFF_SET = 321;
    private static final int VERTICAL_OFF_SET = 321;
    private static final int DEPTH_OFF_SET = 17;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String GEE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/generation_earth_engine";
    private static final String[][] shape = StructureUtils.readStructureFromFile(GEE_STRUCTURE_FILE_PATH);

    public GenerationEarthEngine(String aName) {
        super(aName);
    }

    public GenerationEarthEngine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GenerationEarthEngine(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("GenerationEarthEngineRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_01"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(643, 218, 643, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings1, 12);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public boolean isRotationChangeAllowed() {
        return false;
    }

    @Override
    public IStructureDefinition<GenerationEarthEngine> getStructureDefinition() {
        return StructureDefinition.<GenerationEarthEngine>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings8, 7))
            .addElement('B', ofBlock(compactFusionCoil, 3))
            .addElement('C', ofBlock(sSolenoidCoilCasings, 8))
            .addElement('D', ofBlock(TTCasingsContainer.StabilisationFieldGenerators, 8))
            .addElement('E', ofBlock(magneticFluxCasing, 0))
            .addElement('F', ofBlock(BlockQuantumGlass.INSTANCE, 0))
            .addElement('G', ofBlock(sBlockCasings8, 13))
            .addElement('H', ofBlock(sBlockCasings1, 13))
            .addElement('I', ofBlock(sBlockCasings8, 2))
            .addElement('J', ofBlock(sBlockCasingsSE, 1))
            .addElement('K', ofBlock(BlockLoader.defcCasingBlock, 11))
            .addElement(
                'L',
                buildHatchAdder(GenerationEarthEngine.class)
                    .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Maintenance, Energy.or(ExoticEnergy))
                    .casingIndex(StructureUtils.getTextureIndex(sBlockCasings8, 5))
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(ModBlocks.blockCasings2Misc, 12))))
            .addElement('M', CommonElements.BlockBeacon.get())
            .addElement('N', ofBlock(EnderIO.blockIngotStorageEndergy, 3))
            .build();
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d == ForgeDirection.UP;
    }

    @Override
    public void onPreviewConstruct(@NotNull ItemStack trigger) {
        if (trigger.stackSize > 1) {
            buildPiece(STRUCTURE_PIECE_MAIN, trigger, false, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
        }
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
        int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);

        if (stackSize.stackSize > 1) {
            return this.survivalBuildPiece(
                STRUCTURE_PIECE_MAIN,
                stackSize,
                HORIZONTAL_OFF_SET,
                VERTICAL_OFF_SET,
                DEPTH_OFF_SET,
                realBudget,
                env,
                false,
                true);
        } else {
            return -1;
        }
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mCountCasing >= 5;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.RecombinationFusionReactorRecipes;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }
}
