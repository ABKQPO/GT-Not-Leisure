package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtPlusPlus.core.block.ModBlocks.blockCasings3Misc;

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
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.metadata.CompressionTierKey;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;

public class LargeMaterialPress extends GTMMultiMachineBase<LargeMaterialPress> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String LIL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_material_press";
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LIL_STRUCTURE_FILE_PATH);

    public LargeMaterialPress(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeMaterialPress(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeMaterialPress(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(33);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.compressorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeMaterialPressRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeMaterialPress_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeMaterialPress_01"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(7, 3, 3, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeMaterialPress_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeMaterialPress_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeMaterialPress_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeMaterialPress_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeMaterialPress_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeMaterialPress> getStructureDefinition() {
        return StructureDefinition.<LargeMaterialPress>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofBlockAnyMeta(GameRegistry.findBlock(Mods.IndustrialCraft2.ID, "blockAlloyGlass")))
            .addElement('B', StructureUtility.ofBlock(sBlockCasings2, 3))
            .addElement(
                'C',
                buildHatchAdder(LargeMaterialPress.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(blockCasings3Misc, 1))))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 40;
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
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                int tier = recipe.getMetadataOrDefault(CompressionTierKey.INSTANCE, 0);
                if (tier > 1 || (tier == 1 && mParallelTier < 8)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1.0 / 2.5 - (Math.max(0, mParallelTier - 1) / 50.0);
    }
}
