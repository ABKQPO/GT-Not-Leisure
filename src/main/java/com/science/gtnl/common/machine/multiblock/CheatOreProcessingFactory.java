package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class CheatOreProcessingFactory extends MultiMachineBase<CheatOreProcessingFactory> {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String COPF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/cheat_ore_processing_factory";
    private static final String[][] shape = StructureUtils.readStructureFromFile(COPF_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 20;
    private static final int VERTICAL_OFF_SET = 24;
    private static final int DEPTH_OFF_SET = 0;
    public static Object2ObjectOpenHashMap<ItemId, ArrayList<GTRecipe>> RECIPE_INDEX = new Object2ObjectOpenHashMap<>();
    public static boolean recipeIndexInitialized = false;

    public CheatOreProcessingFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public CheatOreProcessingFactory(String aName) {
        super(aName);
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
        Object2ObjectOpenHashMap<ItemId, ArrayList<GTRecipe>> recipeIndex = getRecipeIndex();
        ArrayList<ItemStack> inputs = getStoredInputs();
        ArrayList<ItemStack> outputs = new ArrayList<>(inputs.size());
        for (ItemStack items : inputs) {
            boolean hasNotFound = true;
            ArrayList<GTRecipe> matchingRecipes = recipeIndex.get(ItemId.createNoCopy(items));
            if (matchingRecipes != null) {
                for (GTRecipe recipe : matchingRecipes) {
                    if (recipe.mInputs == null || recipe.mInputs.length < 1) continue;
                    ItemStack recipeInput = recipe.mInputs[0];
                    if (recipeInput == null || recipeInput.stackSize <= 0) continue;
                    if (!GTUtility.areStacksEqual(recipeInput, items) || items.stackSize < recipeInput.stackSize) {
                        continue;
                    }
                    hasNotFound = false;
                    int parallel = items.stackSize / recipeInput.stackSize;
                    items.stackSize -= parallel * recipeInput.stackSize;
                    for (ItemStack recipeOutput : recipe.mOutputs) {
                        if (recipeOutput == null || recipeOutput.stackSize <= 0) {
                            continue;
                        }
                        if (Integer.MAX_VALUE / parallel >= recipeOutput.stackSize) {
                            outputs.add(
                                GTUtility.copyAmountUnsafe(recipeOutput.stackSize * parallel, recipeOutput.copy()));
                        } else {
                            long outputAmount = (long) parallel * recipeOutput.stackSize;
                            while (outputAmount > 0) {
                                if (outputAmount >= Integer.MAX_VALUE) {
                                    outputs.add(GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, recipeOutput.copy()));
                                    outputAmount -= Integer.MAX_VALUE;
                                } else {
                                    outputs.add(GTUtility.copyAmountUnsafe((int) outputAmount, recipeOutput.copy()));
                                    outputAmount = 0;
                                }
                            }
                        }
                    }
                }
            }
            if (hasNotFound) {
                if (Objects.equals(items.getUnlocalizedName(), "gt.blockores")) {
                    ScienceNotLeisure.LOG.info("OP system recipe has not write this material's: {}", items);
                }
                outputs.add(items.copy());
                items.stackSize = 0;
            }
        }
        if (outputs.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;
        mOutputItems = outputs.toArray(new ItemStack[outputs.size()]);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public Object2ObjectOpenHashMap<ItemId, ArrayList<GTRecipe>> getRecipeIndex() {
        if (!recipeIndexInitialized) {
            indexRecipes();
        }
        return RECIPE_INDEX;
    }

    public void indexRecipes() {
        RECIPE_INDEX.clear();
        RecipeMap<?> recipeMap = getRecipeMap();
        for (GTRecipe recipe : recipeMap.getAllRecipes()) {
            if (recipe.mInputs == null || recipe.mInputs.length < 1 || recipe.mInputs[0] == null) {
                continue;
            }
            ItemId itemId = ItemId.createNoCopy(recipe.mInputs[0]);
            ArrayList<GTRecipe> indexedRecipes = RECIPE_INDEX.computeIfAbsent(itemId, key -> new ArrayList<>());
            indexedRecipes.add(recipe);
        }
        recipeIndexInitialized = true;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        return checkProcessing_wirelessMode();
    }

    public CheckRecipeResult checkProcessing_wirelessMode() {

        CheckRecipeResult result = OP_Process_Wireless();
        if (!result.wasSuccessful()) return result;
        boolean noRecipe = mOutputItems == null || mOutputItems.length < 1;
        updateSlots();
        if (noRecipe) return CheckRecipeResultRegistry.NO_RECIPE;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = 1;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.CheatOreProcessingRecipes;
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
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsCraftingMEBuffer() {
        return false;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        checkPieceAndHatch(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
    public IStructureDefinition<CheatOreProcessingFactory> getStructureDefinition() {
        return StructureDefinition.<CheatOreProcessingFactory>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(WerkstoffLoader.BWBlockCasings, 32066))
            .addElement('B', GTStructureUtility.ofFrame(Materials.Bronze))
            .addElement('C', StructureUtility.ofBlock(blockCasingsMisc, 2))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 2))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 12))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 13))
            .addElement(
                'G',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(CheatOreProcessingFactory.class)
                        .atLeast(HatchElement.Maintenance, HatchElement.InputBus, HatchElement.OutputBus)
                        .casingIndex(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 10))
                        .hint(1)
                        .build(),
                    StructureUtility.ofBlock(WerkstoffLoader.BWBlockCasingsAdvanced, 32066)))
            .addElement('H', GTStructureUtility.chainAllGlasses())
            .build();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("CheatOreProcessingFactoryRecipeType"))
            .beginStructureBlock(41, 26, 18, false)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
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

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 10);
    }
}
