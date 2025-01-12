package com.science.gtnl.common.machine.multiblock;

import static bartworks.system.material.WerkstoffLoader.BWBlockCasings;
import static bartworks.system.material.WerkstoffLoader.BWBlockCasingsAdvanced;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.Utils.Utils.metaItemEqual;
import static com.science.gtnl.Utils.Utils.setStackSize;
import static com.science.gtnl.common.machine.OreProcessing.OP_Values.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.TextLocalization;
import com.science.gtnl.Utils.TextUtils;
import com.science.gtnl.common.RecipeRegister;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IWirelessEnergyHatchInformation;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings1;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class CheatOreProcessingFactory extends MultiMachineBase<CheatOreProcessingFactory>
    implements IWirelessEnergyHatchInformation {

    public CheatOreProcessingFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        this.shape = StructureUtils.readStructureFromFile(COPF_STRUCTURE_FILE_PATH);
    }

    public CheatOreProcessingFactory(String aName) {
        super(aName);
        this.shape = StructureUtils.readStructureFromFile(COPF_STRUCTURE_FILE_PATH);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new CheatOreProcessingFactory(this.mName);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }

    public CheckRecipeResult OP_Process_Wireless() {
        RecipeMap<?> recipeMap = getRecipeMap();
        ArrayList<ItemStack> inputs = getStoredInputs();
        ArrayList<ItemStack> outputs = new ArrayList<>();
        long EUt = 0;
        // check every inputs
        for (ItemStack items : inputs) {
            boolean hasNotFound = true;
            for (GTRecipe recipe : recipeMap.getAllRecipes()) {
                if (recipe.mInputs == null || recipe.mInputs.length < 1) continue;
                if (metaItemEqual(recipe.mInputs[0], items) && items.stackSize >= recipe.mInputs[0].stackSize) {
                    // found the recipe
                    hasNotFound = false;
                    ItemStack recipeInput = recipe.mInputs[0];
                    int parallel = items.stackSize / recipeInput.stackSize;

                    // decrease the input stack amount
                    items.stackSize -= parallel * recipeInput.stackSize;

                    // add EU cost
                    EUt += (long) recipe.mEUt * parallel;

                    // process output stacks
                    for (ItemStack recipeOutput : recipe.mOutputs) {
                        if (Integer.MAX_VALUE / parallel >= recipeOutput.stackSize) {
                            // direct output
                            outputs.add(setStackSize(recipeOutput.copy(), recipeOutput.stackSize * parallel));
                        } else {
                            // separate to any integer max stack
                            long outputAmount = (long) parallel * recipeOutput.stackSize;
                            while (outputAmount > 0) {
                                if (outputAmount >= Integer.MAX_VALUE) {
                                    outputs.add(setStackSize(recipeOutput.copy(), Integer.MAX_VALUE));
                                    outputAmount -= Integer.MAX_VALUE;
                                } else {
                                    outputs.add(setStackSize(recipeOutput.copy(), (int) outputAmount));
                                    outputAmount = 0;
                                }
                            }
                        }
                    }
                }
            }
            // If is gt ore but not in recipe map
            // Handle it specially
            if (hasNotFound) {
                if (Objects.equals(items.getUnlocalizedName(), "gt.blockores")) {
                    ScienceNotLeisure.LOG.info("OP system recipe has not write this material's: " + items);
                    outputs.add(items.copy());
                    items.stackSize = 0;
                } else if (moveUnprocessedItemsToOutputs) {
                    outputs.add(items.copy());
                    items.stackSize = 0;
                }
            }
        }
        if (outputs.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;
        // set these to machine outputs
        mOutputItems = outputs.toArray(new ItemStack[0]);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        return checkProcessing_wirelessMode();
    }

    protected CheckRecipeResult checkProcessing_wirelessMode() {

        CheckRecipeResult result = OP_Process_Wireless();
        if (!result.wasSuccessful()) return result;
        boolean noRecipe = mOutputItems == null || mOutputItems.length < 1;
        updateSlots();
        if (noRecipe) return CheckRecipeResultRegistry.NO_RECIPE;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = OreProcessRecipeDuration;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(false);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe);
            }

        }.setMaxParallel(Integer.MAX_VALUE);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.CheatOreProcessingRecipes;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    protected boolean supportsCraftingMEBuffer() {
        return false;
    }

    public boolean checkHatches() {
        return !mInputBusses.isEmpty() && !mOutputBusses.isEmpty();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet)) return false;
        if (checkHatches()) {
            updateHatchTexture();
            return true;
        }
        return true;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);
    }

    public static final String STRUCTURE_PIECE_MAIN = "main";
    public IStructureDefinition<CheatOreProcessingFactory> STRUCTURE_DEFINITION = null;
    public static final String COPF_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/cheat_ore_processing_factory"; // 文件路径
    public String[][] shape;
    public final int horizontalOffSet = 20;
    public final int verticalOffSet = 24;
    public final int depthOffSet = 0;

    @Override
    public IStructureDefinition<CheatOreProcessingFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<CheatOreProcessingFactory>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(BWBlockCasings, 32066))
                .addElement('B', ofFrame(Materials.Bronze))
                .addElement('C', ofBlock(blockCasingsMisc, 2))
                .addElement('D', ofBlock(sBlockCasings2, 2))
                .addElement('E', ofBlock(sBlockCasings2, 12))
                .addElement('F', ofBlock(sBlockCasings3, 13))
                .addElement(
                    'G',
                    ofChain(
                        buildHatchAdder(CheatOreProcessingFactory.class).atLeast(InputBus, OutputBus)
                            .casingIndex(((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(10))
                            .dot(1)
                            .build(),
                        ofBlock(BWBlockCasingsAdvanced, 32066)))
                .addElement('H', ofBlockAnyMeta(Blocks.glass))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.CheatOreProcessingFactoryRecipeType)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(41, 26, 18, false)
            .toolTipFinisher(TextUtils.SCIENCE_NOT_LEISURE + TextUtils.SQY);
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_TOP_STEAM_MACERATOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    protected void updateHatchTexture() {
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputBusses) h.updateTexture(getCasingTextureID());
    }

    public int getCasingTextureID() {
        return ((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(10);
    }
}
