package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
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
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class LargeBrewer extends GTMMultiMachineBase<LargeBrewer> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_brewer";
    private static final int MACHINEMODE_FREWERY = 0;
    private static final int MACHINEMODE_FERMENTER = 1;
    private static final int MACHINEMODE_FLUID_HEATER = 2;
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LB_STRUCTURE_FILE_PATH);

    public LargeBrewer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeBrewer(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeBrewer(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialCuttingMachineActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCDIndustrialCuttingMachine)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(1);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case MACHINEMODE_FERMENTER -> RecipeMaps.fermentingRecipes;
            case MACHINEMODE_FLUID_HEATER -> RecipeMaps.fluidHeaterRecipes;
            default -> RecipeMaps.brewingRecipes;
        };
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.brewingRecipes, RecipeMaps.fermentingRecipes, RecipeMaps.fluidHeaterRecipes);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeBrewerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeBrewer_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(5, 5, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeBrewer_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeBrewer_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeBrewer_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeBrewer_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeBrewer_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeBrewer_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeBrewer> getStructureDefinition() {
        return StructureDefinition.<LargeBrewer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 8))
            .addElement('B', StructureUtility.ofBlock(sBlockCasings2, 13))
            .addElement(
                'C',
                buildHatchAdder(LargeBrewer.class).casingIndex(getCasingTextureID())
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
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(blockCasingsMisc, 1))))
            .addElement('D', HatchElement.Muffler.newAny(TAE.GTPP_INDEX(1), 1))
            .build();
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 3;
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("LargeBrewer_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("LargeBrewer_Mode_" + machineMode);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 45;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && mMufflerHatches.size() == 1;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == MACHINEMODE_FREWERY) return MACHINEMODE_FERMENTER;
        else if (machineMode == MACHINEMODE_FERMENTER) return MACHINEMODE_FLUID_HEATER;
        else if (machineMode == MACHINEMODE_FLUID_HEATER) return MACHINEMODE_FREWERY;
        else return MACHINEMODE_FREWERY;
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
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1.0 / 2.25 - (Math.max(0, mParallelTier - 1) / 50.0);
    }
}
