package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.Utils.enums.BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK;
import static com.science.gtnl.Utils.enums.BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE;
import static com.science.gtnl.Utils.recipes.RecipeUtil.*;
import static com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtnhintergalactic.recipe.IGRecipeMaps;
import tectech.thing.casing.BlockGTCasingsTT;

public class SpaceAssembler extends GTMMultiMachineBase<SpaceAssembler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String SA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/space_assembler";
    public static final String[][] shape = StructureUtils.readStructureFromFile(SA_STRUCTURE_FILE_PATH);
    protected final int HORIZONTAL_OFF_SET = 5;
    protected final int VERTICAL_OFF_SET = 3;
    protected final int DEPTH_OFF_SET = 0;

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
                    .addIcon(OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_TECTECH_MULTIBLOCK)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings1, 13);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return IGRecipeMaps.spaceAssemblerRecipes;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (isValidForSpaceStation(getBaseMetaTileEntity().getWorld().provider.dimensionId)
            || isValidForMothership(getBaseMetaTileEntity().getWorld().provider.dimensionId)) {
            return super.checkProcessing();
        }
        return NOT_IN_SPACE_STATION;
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
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
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
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', ofBlock(sBlockCasings1, 13))
            .addElement('C', ofBlock(sBlockCasingsSEMotor, 2))
            .addElement('D', ofBlock(sBlockCasingsTT, 2))
            .addElement(
                'E',
                buildHatchAdder(SpaceAssembler.class).casingIndex(BlockGTCasingsTT.textureOffset + 3)
                    .dot(1)
                    .atLeast(InputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy), ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
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
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0) * Math.pow(0.90, mGlassTier);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier();
    }
}
