package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase.ParallelControllerElement.ParallelCon;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW;
import static gregtech.api.util.GTStructureUtility.*;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;

import bartworks.util.BWUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
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
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings8;
import gtPlusPlus.core.block.ModBlocks;

public class MegaBlastFurnace extends GTMMultiMachineBase<MegaBlastFurnace> implements ISurvivalConstructable {

    private HeatingCoilLevel mCoilLevel;
    private int mHeatingCapacity = 0;
    private static IStructureDefinition<MegaBlastFurnace> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String MBF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/mega_blast_furnace";
    public static String[][] shape = StructureUtils.readStructureFromFile(MBF_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 11;
    public final int VERTICAL_OFF_SET = 41;
    public final int DEPTH_OFF_SET = 0;

    public MegaBlastFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MegaBlastFurnace(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MegaBlastFurnace(this.mName);
    }

    @Override
    public IStructureDefinition<MegaBlastFurnace> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MegaBlastFurnace>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    buildHatchAdder(MegaBlastFurnace.class)
                        .atLeast(
                            InputHatch,
                            OutputHatch,
                            InputBus,
                            OutputBus,
                            Maintenance,
                            Energy.or(ExoticEnergy),
                            ParallelCon)
                        .casingIndex(TAE.GTPP_INDEX(15))
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(ModBlocks.blockCasingsMisc, 15))))
                .addElement('B', ofBlock(sBlockCasings2, 0))
                .addElement('S', Muffler.newAny(((BlockCasings8) sBlockCasings8).getTextureIndex(10), 2))
                .addElement('C', ofBlock(sBlockCasings2, 12))
                .addElement('D', ofBlock(sBlockCasings2, 13))
                .addElement('E', ofBlock(sBlockCasings2, 14))
                .addElement('F', ofBlock(sBlockCasings2, 15))
                .addElement('G', ofBlock(sBlockCasings3, 13))
                .addElement('H', ofBlock(sBlockCasings3, 14))
                .addElement('I', ofBlock(sBlockCasings3, 15))
                .addElement('J', ofBlock(sBlockCasings4, 3))
                .addElement('K', ofBlock(sBlockCasings4, 13))
                .addElement(
                    'L',
                    withChannel("coil", ofCoil(MegaBlastFurnace::setCoilLevel, MegaBlastFurnace::getCoilLevel)))
                .addElement('M', ofBlock(sBlockCasings8, 1))
                .addElement('N', ofBlock(sBlockCasings8, 2))
                .addElement('O', ofBlock(Loaders.FRF_Casings, 0))
                .addElement('P', ofBlock(sBlockCasings8, 4))
                .addElement('Q', ofBlock(sBlockCasings8, 10))
                .addElement('R', ofFrame(Materials.Naquadah))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("Tooltip_MegaBlastFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(23, 44, 23, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_Casing_00"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_Casing_00"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_Casing_00"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_Casing_00"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_Casing_00"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_Casing_00"))
            .addMufflerHatch(StatCollector.translateToLocal("Tooltip_MegaBlastFurnace_Casing_01"))
            .toolTipFinisher();
        return tt;
    }

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        this.mCoilLevel = aCoilLevel;
    }

    public HeatingCoilLevel getCoilLevel() {
        return this.mCoilLevel;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (!aNBT.hasKey(INPUT_SEPARATION_NBT_KEY)) {
            this.inputSeparation = aNBT.getBoolean("isBussesSeparate");
        }
        if (!aNBT.hasKey(BATCH_MODE_NBT_KEY)) {
            this.batchMode = aNBT.getBoolean("mUseMultiparallelMode");
        }
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return mParallelTier >= 9;
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (!aPlayer.isSneaking()) {
            this.inputSeparation = !this.inputSeparation;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal("GT5U.machines.separatebus") + " " + this.inputSeparation);
            return true;
        }
        this.batchMode = !this.batchMode;
        if (this.batchMode) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("misc.BatchModeTextOn"));
        } else {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("misc.BatchModeTextOff"));
        }
        return true;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(15);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(MegaBlastFurnace.this.mHeatingCapacity)
                    .setHeatOC(true)
                    .setHeatDiscount(true)
                    .setEUtDiscount(0.8 - (mParallelTier / 50.0))
                    .setDurationModifier(Math.max(0.005, 1.0 / 5.0 - (mParallelTier / 200.0)));
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                return recipe.mSpecialValue <= MegaBlastFurnace.this.mHeatingCapacity
                    ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        if (mParallelTier <= 1) {
            return 8;
        } else {
            return (int) Math.pow(4, mParallelTier - 2) * 4;
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
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
        if (mMachine) return -1;
        this.setCoilLevel(HeatingCoilLevel.None);
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
    public boolean checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack) {
        this.mHeatingCapacity = 0;
        tCountCasing = 0;
        mParallelTier = 0;
        this.setCoilLevel(HeatingCoilLevel.None);

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()) {
            return false;
        }

        if (getCoilLevel() == HeatingCoilLevel.None) return false;

        if (mMaintenanceHatches.size() != 1 && mMufflerHatches.size() != 1) return false;

        this.mHeatingCapacity = (int) this.getCoilLevel()
            .getHeat() + 100 * (BWUtil.getTier(this.getMaxInputEu()) - 2);

        energyHatchTier = checkEnergyHatchTier();
        mParallelTier = getParallelTier(aStack);
        return tCountCasing >= 3500;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.blastFurnaceRecipes;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MEGA_BLAST_FURNACE_LOOP;
    }

}
