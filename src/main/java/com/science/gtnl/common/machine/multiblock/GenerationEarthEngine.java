package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static goodgenerator.loader.Loaders.compactFusionCoil;
import static goodgenerator.loader.Loaders.magneticFluxCasing;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.loader.RecipeRegister;

import crazypants.enderio.EnderIO;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
import gregtech.common.blocks.BlockCasings8;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.GTPPMultiBlockBase;
import kubatech.loaders.BlockLoader;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.TTCasingsContainer;

public class GenerationEarthEngine extends GTPPMultiBlockBase<GenerationEarthEngine> implements ISurvivalConstructable {

    protected GTRecipe lastRecipeToBuffer;

    public static final int HORIZONTAL_OFF_SET = 321;
    public static final int VERTICAL_OFF_SET = 321;
    public static final int DEPTH_OFF_SET = 17;
    public int tCountCasing = 0;
    private static IStructureDefinition<GenerationEarthEngine> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String GEE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/generation_earth_engine"; // 文件路径
    public static String[][] shape = StructureUtils.readStructureFromFile(GEE_STRUCTURE_FILE_PATH);

    public GenerationEarthEngine(String aName) {
        super(aName);
    }

    public GenerationEarthEngine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("GenerationEarthEngineRecipeType");
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GenerationEarthEngine(this.mName);
    }

    @Override
    public int getMaxEfficiency(ItemStack itemStack) {
        return 10000;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
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

    protected void updateHatchTexture() {
        for (MTEHatch h : mInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
    }

    public int getCasingTextureID() {
        return ((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(12);
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
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GenerationEarthEngine>builder()
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
                        .casingIndex(((BlockCasings8) GregTechAPI.sBlockCasings8).getTextureIndex(5))
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(ModBlocks.blockCasings2Misc, 12))))
                .addElement('M', ofBlock(Blocks.beacon, 1))
                .addElement('N', ofBlock(EnderIO.blockIngotStorageEndergy, 3))
                .build();
        }
        return STRUCTURE_DEFINITION;
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
        tCountCasing = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        return tCountCasing >= 5;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.RecombinationFusionReactorRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().setSpeedBonus(1F)
            .setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public String[] getInfoData() {
        final String running = (this.mMaxProgresstime > 0 ? "Fusion running" : "Fusion stopped");
        final String maintenance = (this.getIdealStatus() == this.getRepairStatus() ? "No Maintenance issues"
            : "Needs Maintenance");
        String tSpecialText;

        if (lastRecipeToBuffer != null && lastRecipeToBuffer.mOutputs[0].getDisplayName() != null) {
            tSpecialText = "Currently processing: " + lastRecipeToBuffer.mOutputs[0].getDisplayName();
        } else {
            tSpecialText = "Currently processing: Nothing";
        }

        return new String[] { "Generation Earth Engine", running, maintenance, tSpecialText };
    }

}
