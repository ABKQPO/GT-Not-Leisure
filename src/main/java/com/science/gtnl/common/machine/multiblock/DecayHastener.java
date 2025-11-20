package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_DECAY_HASTENER;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_DECAY_HASTENER_ACTIVE;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;

public class DecayHastener extends GTMMultiMachineBase<DecayHastener> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String DH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/decay_hastener";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 11;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(DH_STRUCTURE_FILE_PATH);

    public DecayHastener(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public DecayHastener(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DecayHastener(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DECAY_HASTENER_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DECAY_HASTENER)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings8, 10);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.DecayHastenerRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("DecayHastenerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_DecayHastener_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_DecayHastener_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(5, 13, 5, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_DecayHastener_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_DecayHastener_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_DecayHastener_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_DecayHastener_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<DecayHastener> getStructureDefinition() {
        return StructureDefinition.<DecayHastener>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings1, 15))
            .addElement('B', ofBlock(sBlockCasings10, 3))
            .addElement('C', ofBlock(sBlockCasings4, 6))
            .addElement('D', ofBlock(sBlockCasings6, 8))
            .addElement(
                'E',
                buildHatchAdder(DecayHastener.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        InputBus,
                        OutputBus,
                        InputHatch,
                        OutputHatch,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 10))))
            .addElement('F', ofFrame(Materials.BlackSteel))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 80;
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
