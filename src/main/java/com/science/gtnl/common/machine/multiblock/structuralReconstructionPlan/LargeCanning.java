package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gtPlusPlus.core.block.ModBlocks.blockCasings2Misc;

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
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class LargeCanning extends GTMMultiMachineBase<LargeCanning> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_canning";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LA_STRUCTURE_FILE_PATH);
    public static final int MACHINEMODE_CANNER = 0;
    public static final int MACHINEMODE_FLUIDCANNER = 1;

    public LargeCanning(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeCanning(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeCanning(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_CANNER_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_CANNER)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(11);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_FLUIDCANNER) ? RecipeMaps.fluidCannerRecipes : RecipeMaps.cannerRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.fluidCannerRecipes, RecipeMaps.cannerRecipes);
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 2;
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("LargeCanning_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("LargeCanning_Mode_" + machineMode);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public double getEUtDiscount() {
        return 0.5 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1 / 3.33 - (Math.max(0, mParallelTier - 1) / 50.0);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeCanningRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeCanning_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeCanning_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addMultiAmpHatchInfo()
            .addPerfectOCInfo()
            .beginStructureBlock(5, 5, 7, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeCanning_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeCanning_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeCanning_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeCanning_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeCanning_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeCanning_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeCanning> getStructureDefinition() {
        return StructureDefinition.<LargeCanning>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofBlockAnyMeta(GameRegistry.findBlock(Mods.IndustrialCraft2.ID, "blockAlloyGlass")))
            .addElement('B', StructureUtility.ofBlock(sBlockCasings2, 13))
            .addElement('C', GTStructureUtility.ofFrame(Materials.StainlessSteel))
            .addElement(
                'D',
                GTStructureUtility.buildHatchAdder(LargeCanning.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(blockCasings2Misc, 4))))
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
