package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.recipes.RecipeUtil;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtnhintergalactic.recipe.IGRecipeMaps;
import tectech.thing.casing.BlockGTCasingsTT;

public class SpaceAssembler extends GTMMultiMachineBase<SpaceAssembler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/space_assembler";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SA_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;

    public SpaceAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SpaceAssembler(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SpaceAssembler(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 13);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return IGRecipeMaps.spaceAssemblerRecipes;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (RecipeUtil.isValidForSpaceStation(getBaseMetaTileEntity().getWorld().provider.dimensionId)
            || RecipeUtil.isValidForMothership(getBaseMetaTileEntity().getWorld().provider.dimensionId)) {
            return super.checkProcessing();
        }
        return RecipeUtil.NOT_IN_SPACE_STATION;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SpaceAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SpaceAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SpaceAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SpaceAssembler_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(11, 11, 11, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<SpaceAssembler> getStructureDefinition() {
        return StructureDefinition.<SpaceAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSEMotor, 2))
            .addElement('D', StructureUtility.ofBlock(sBlockCasingsTT, 2))
            .addElement(
                'E',
                GTStructureUtility.buildHatchAdder(SpaceAssembler.class)
                    .casingIndex(BlockGTCasingsTT.textureOffset + 3)
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 3))))
            .build();
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 10;
    }

    @Override
    public boolean checkEnergyHatch() {
        return true;
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0) * Math.pow(0.90, mGlassTier);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier();
    }
}
