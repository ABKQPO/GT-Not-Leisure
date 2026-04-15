package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.CommonElements;
import com.science.gtnl.utils.enums.GTNLStructureChannels;

import crazypants.enderio.EnderIO;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
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

public class GenerationEarthEngine extends MultiMachineBase<GenerationEarthEngine> implements ISurvivalConstructable {

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
            .beginStructureBlock(643, 218, 643, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_GenerationEarthEngine_Casing"), 1)
            .addSubChannelUsage(GTNLStructureChannels.STRUCTURE_RENDER)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 12);
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
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement('B', StructureUtility.ofBlock(Loaders.compactFusionCoil, 3))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sSolenoidCoilCasings, 8))
            .addElement('D', StructureUtility.ofBlock(TTCasingsContainer.StabilisationFieldGenerators, 8))
            .addElement('E', StructureUtility.ofBlock(Loaders.magneticFluxCasing, 0))
            .addElement('F', StructureUtility.ofBlock(BlockQuantumGlass.INSTANCE, 0))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 13))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 2))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSE, 1))
            .addElement('K', StructureUtility.ofBlock(BlockLoader.defcCasingBlock, 11))
            .addElement(
                'L',
                buildHatchAdder(GenerationEarthEngine.class)
                    .atLeast(
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .casingIndex(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 5))
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 12))))
            .addElement('M', CommonElements.BlockBeacon.get())
            .addElement('N', StructureUtility.ofBlock(EnderIO.blockIngotStorageEndergy, 3))
            .build();
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d == ForgeDirection.UP;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        if (!GTNLStructureChannels.STRUCTURE_RENDER.hasValue(stackSize)) return;
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
        if (!GTNLStructureChannels.STRUCTURE_RENDER.hasValue(stackSize)) return -1;
        int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);
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
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mCountCasing >= 5;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.RecombinationFusionReactorRecipes;
    }
}
