package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.recipe.RecipeMaps.compressorRecipes;
import static gregtech.api.util.GTStructureUtility.*;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.tileentities.render.TileEntityBlackhole;

public class MegaSteamCompressor extends SteamMultiMachineBase<MegaSteamCompressor> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SMC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_mega_compressor";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SMC_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 17;
    private static final int VERTICAL_OFF_SET = 27;
    private static final int DEPTH_OFF_SET = 10;

    public MegaSteamCompressor(String aName) {
        super(aName);
    }

    public MegaSteamCompressor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("MegaSteamCompressorRecipeType");
    }

    @Override
    public int getTierRecipes() {
        return 4;
    }

    @Override
    public IStructureDefinition<MegaSteamCompressor> getStructureDefinition() {
        return StructureDefinition.<MegaSteamCompressor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses())
            .addElement(
                'B',
                ofChain(
                    buildSteamWirelessInput(MegaSteamCompressor.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings2, 0))
                        .dot(1)
                        .build(),
                    buildSteamInput(MegaSteamCompressor.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings2, 0))
                        .dot(1)
                        .build(),
                    buildHatchAdder(MegaSteamCompressor.class)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings2, 0))
                        .dot(1)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch,
                            Maintenance)
                        .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings2, 0)))))
            .addElement('C', ofBlock(GregTechAPI.sBlockCasings1, 10))
            .addElement('D', ofFrame(Materials.Steel))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int realBudget = elementBudget >= 200 ? elementBudget : Math.min(200, elementBudget * 5);
        return survivalBuildPiece(
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

    @Override
    public RecipeMap<?> getRecipeMap() {
        return compressorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaSteamCompressor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaSteamCompressor_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaSteamCompressor_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaSteamCompressor_03"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(35, 33, 35, true)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        ITexture[] rTexture;
        if (side == facing) {
            if (aActive) {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_ACTIVE_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)) };
        }
        return rTexture;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatches();
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(configSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                createRenderBlock();
                return super.onRecipeStart(recipe);
            }

            @NotNull
            @Override
            public GTNL_ProcessingLogic clear() {
                destroyRenderBlock();
                return super.clear();
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return 1 << (2 * Math.min(4, recipeOcCount));
    }

    @Override
    public double getDurationModifier() {
        return 1.0 / (1 << Math.min(4, recipeOcCount));
    }

    @Override
    public int getMaxParallelRecipes() {
        return 256;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(V[3]);
        // We need to trick the GT_ParallelHelper we have enough amps for all recipe parallels.
        logic.setAvailableAmperage(getTrueParallel());
        logic.setAmperageOC(false);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MegaSteamCompressor(this.mName);
    }

    @Override
    public void onBlockDestroyed() {
        destroyRenderBlock();
        super.onBlockDestroyed();
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d != ForgeDirection.UP && d != ForgeDirection.DOWN;
    }

    @Override
    public boolean isRotationChangeAllowed() {
        return false;
    }

    private TileEntityBlackhole rendererTileEntity = null;

    // Returns true if render was actually created
    private boolean createRenderBlock() {
        if (!mMachine) return false;
        IGregTechTileEntity base = this.getBaseMetaTileEntity();
        ForgeDirection opposite = getDirection().getOpposite();
        int x = 7 * opposite.offsetX;
        int z = 7 * opposite.offsetZ;
        int y = 11;

        base.getWorld()
            .setBlock(base.getXCoord() + x, base.getYCoord() + y, base.getZCoord() + z, Blocks.air);
        base.getWorld()
            .setBlock(base.getXCoord() + x, base.getYCoord() + y, base.getZCoord() + z, GregTechAPI.sBlackholeRender);
        rendererTileEntity = (TileEntityBlackhole) base.getWorld()
            .getTileEntity(base.getXCoord() + x, base.getYCoord() + y, base.getZCoord() + z);

        rendererTileEntity.startScaleChange(true);
        rendererTileEntity.toggleLaser(true);
        rendererTileEntity.setLaserColor(6, 6, 6);
        rendererTileEntity.setStability(0);
        return true;
    }

    private void destroyRenderBlock() {
        IGregTechTileEntity base = this.getBaseMetaTileEntity();
        ForgeDirection opposite = getDirection().getOpposite();
        int x = 7 * opposite.offsetX;
        int z = 7 * opposite.offsetZ;
        int y = 11;

        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(base.getXCoord() + x, base.getYCoord() + y, base.getZCoord() + z, Blocks.air);
    }
}
