package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gtPlusPlus.core.block.ModBlocks.blockSpecialMultiCasings2;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
public class LargeBioLab extends GTMMultiMachineBase<LargeBioLab> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LBL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_bio_lab";
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LBL_STRUCTURE_FILE_PATH);

    public LargeBioLab(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeBioLab(String aName) {
        super(aName);
    }

    @Override
    public IStructureDefinition<LargeBioLab> getStructureDefinition() {
        return StructureDefinition.<LargeBioLab>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', Casings.PressureContainmentCasing.asElement())
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(LargeBioLab.class)
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
                            Casings.NaquadriaReinforcedWaterPlantCasing.asElement())))
            .addElement('C', Casings.HighPowerCasing.asElement())
            .addElement('D', GTStructureUtility.ofFrame(Materials.CosmicNeutronium))
            .addElement('E', StructureUtility.ofBlock(blockSpecialMultiCasings2, 2))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 20);
    }

    @Override
    public void checkEnergyHatch(List<StructureError> errors) {}

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
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.LargeBioLabRecipes;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return Casings.NaquadriaReinforcedWaterPlantCasing.getTextureId();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.large_bio_lab.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.large_bio_lab.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.3"))
            .addSupportAny()
            .beginStructureBlock(7, 5, 5, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.large_bio_lab.casing"))
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.large_bio_lab.casing"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.large_bio_lab.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.large_bio_lab.casing"))
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.large_bio_lab.casing"))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.large_bio_lab.casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeBioLab(this.mName);
    }
}
