package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class MassFabricator extends GTMMultiMachineBase<MassFabricator> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/mass_fabricator";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(MF_STRUCTURE_FILE_PATH);

    public MassFabricator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MassFabricator(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MassFabricator(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_MatterFab_Active_Animated)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_MatterFab_Animated)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings1, 7);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.multiblockMassFabricatorRecipes;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MassFabricatorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addMultiAmpHatchInfo()
            .addPerfectOCInfo()
            .beginStructureBlock(5, 5, 8, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_MassFabricator_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_MassFabricator_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_MassFabricator_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MassFabricator_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MassFabricator_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_MassFabricator_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<MassFabricator> getStructureDefinition() {
        return StructureDefinition.<MassFabricator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(MassFabricator.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasings1, 7))))
            .addElement('B', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 8))
            .addElement('C', StructureUtility.ofBlock(BlockLoader.metaCasing, 2))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 10;
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
}
