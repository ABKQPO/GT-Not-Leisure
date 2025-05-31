package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase.ParallelControllerElement.ParallelCon;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;
import static tectech.util.TTUtility.replaceLetters;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.machine.multiMachineClasses.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
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
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings9;
import gtnhlanth.common.register.LanthItemList;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.metaTileEntity.multi.godforge.color.ForgeOfGodsStarColor;
import tectech.thing.metaTileEntity.multi.godforge.color.StarColorStorage;

public class NanoPhagocytosisPlant extends WirelessEnergyMultiMachineBase<NanoPhagocytosisPlant> {

    private static final int DEFAULT_STAR_SIZE = 20;
    private final StarColorStorage starColors = new StarColorStorage();
    private static final String DEFAULT_STAR_COLOR = ForgeOfGodsStarColor.DEFAULT.getName();
    private String selectedStarColor = DEFAULT_STAR_COLOR;
    private int starSize = DEFAULT_STAR_SIZE;
    private boolean isRendererDisabled;
    private boolean isRenderActive;
    private static final int HORIZONTAL_OFF_SET = 10;
    private static final int VERTICAL_OFF_SET = 22;
    private static final int DEPTH_OFF_SET = 0;
    private static final int HORIZONTAL_OFF_SET_RING_ONE = 1;
    private static final int VERTICAL_OFF_SET_RING_ONE = 22;
    private static final int DEPTH_OFF_SET_RING_ONE = 0;
    private static final int HORIZONTAL_OFF_SET_RING_TWO = 8;
    private static final int VERTICAL_OFF_SET_RING_TWO = 14;
    private static final int DEPTH_OFF_SET_RING_TWO = -1;
    private static final int HORIZONTAL_OFF_SET_RING_THREE = 1;
    private static final int VERTICAL_OFF_SET_RING_THREE = 19;
    private static final int DEPTH_OFF_SET_RING_THREE = -3;
    private static IStructureDefinition<NanoPhagocytosisPlant> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_MAIN_RING_ONE = "main_ring_one";
    private static final String STRUCTURE_PIECE_MAIN_RING_TWO = "main_ring_two";
    private static final String STRUCTURE_PIECE_MAIN_RING_THREE = "main_ring_three";
    private static final String STRUCTURE_PIECE_MAIN_RING_ONE_AIR = "main_ring_one_air";
    private static final String STRUCTURE_PIECE_MAIN_RING_TWO_AIR = "main_ring_two_air";
    private static final String STRUCTURE_PIECE_MAIN_RING_THREE_AIR = "main_ring_three_air";
    private static final String NPP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/nano_phagocytosis_plant";
    private static final String NPPRO_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/nano_phagocytosis_plant_ring_one";
    private static final String NPPRT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/nano_phagocytosis_plant_ring_two";
    private static final String NPPRTh_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/nano_phagocytosis_plant_ring_three";
    public static final String[][] shape = StructureUtils.readStructureFromFile(NPP_STRUCTURE_FILE_PATH);
    public static final String[][] shapeRingOne = StructureUtils.readStructureFromFile(NPPRO_STRUCTURE_FILE_PATH);
    public static final String[][] shapeRingTwo = StructureUtils.readStructureFromFile(NPPRT_STRUCTURE_FILE_PATH);
    public static final String[][] shapeRingThree = StructureUtils.readStructureFromFile(NPPRTh_STRUCTURE_FILE_PATH);
    public static final String[][] shapeRingOneAir = replaceLetters(shapeRingOne, "Z");
    public static final String[][] shapeRingTwoAir = replaceLetters(shapeRingTwo, "Z");
    public static final String[][] shapeRingThreeAir = replaceLetters(shapeRingThree, "Z");

    public NanoPhagocytosisPlant(String aName) {
        super(aName);
    }

    public NanoPhagocytosisPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NanoPhagocytosisPlant(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("NanoPhagocytosisPlantRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NanoPhagocytosisPlant_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NanoPhagocytosisPlant_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(21, 24, 38, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_NanoPhagocytosisPlant_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_NanoPhagocytosisPlant_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_NanoPhagocytosisPlant_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return ((BlockCasings9) GregTechAPI.sBlockCasings9).getTextureIndex(12);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<NanoPhagocytosisPlant> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<NanoPhagocytosisPlant>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addShape(STRUCTURE_PIECE_MAIN_RING_ONE, transpose(shapeRingOne))
                .addShape(STRUCTURE_PIECE_MAIN_RING_TWO, transpose(shapeRingTwo))
                .addShape(STRUCTURE_PIECE_MAIN_RING_THREE, transpose(shapeRingThree))
                .addShape(STRUCTURE_PIECE_MAIN_RING_ONE_AIR, transpose(shapeRingOneAir))
                .addShape(STRUCTURE_PIECE_MAIN_RING_TWO_AIR, transpose(shapeRingTwoAir))
                .addShape(STRUCTURE_PIECE_MAIN_RING_THREE_AIR, transpose(shapeRingThreeAir))
                .addElement('A', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement('B', ofBlock(BlockLoader.MetaCasing, 2))
                .addElement('C', ofBlock(BlockLoader.MetaCasing, 4))
                .addElement('D', ofBlock(BlockLoader.MetaCasing, 18))
                .addElement('E', ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
                .addElement('F', ofBlock(sBlockCasings1, 15))
                .addElement('G', ofBlock(sBlockCasings10, 3))
                .addElement('H', ofBlock(sBlockCasings10, 7))
                .addElement('I', ofBlock(sBlockCasings10, 8))
                .addElement('J', ofBlock(sBlockCasings3, 10))
                .addElement('K', ofBlock(sBlockCasings4, 11))
                .addElement('L', ofBlock(sBlockCasings4, 12))
                .addElement('M', ofBlock(sBlockCasings8, 7))
                .addElement('N', ofBlock(sBlockCasings8, 10))
                .addElement('O', ofBlock(sBlockCasings8, 11))
                .addElement('P', ofBlock(sBlockCasings9, 12))
                .addElement('Q', ofBlock(sBlockCasings9, 13))
                .addElement('R', ofBlock(sBlockCasingsTT, 0))
                .addElement('S', ofBlock(sBlockCasingsTT, 6))
                .addElement('T', ofFrame(Materials.EnrichedHolmium))
                .addElement('U', ofBlock(sBlockMetal5, 1))
                .addElement('V', ofBlock(blockCasingsMisc, 5))
                .addElement('W', ofBlock(sBlockCasings4, 7))
                .addElement('X', ofBlock(Loaders.compactFusionCoil, 2))
                .addElement('Y', ofBlock(Loaders.compactFusionCoil, 0))
                .addElement('Z', ofBlock(Blocks.air, 0))
                .addElement(
                    'a',
                    buildHatchAdder(NanoPhagocytosisPlant.class)
                        .atLeast(InputBus, OutputBus, Energy.or(ExoticEnergy), ParallelCon)
                        .casingIndex(((BlockCasings9) GregTechAPI.sBlockCasings9).getTextureIndex(12))
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings9, 12))))
                .build();
        }
        return STRUCTURE_DEFINITION;
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
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_ONE,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET_RING_ONE,
            VERTICAL_OFF_SET_RING_ONE,
            DEPTH_OFF_SET_RING_ONE);
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_TWO,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET_RING_TWO,
            VERTICAL_OFF_SET_RING_TWO,
            DEPTH_OFF_SET_RING_TWO);
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_THREE,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET_RING_THREE,
            VERTICAL_OFF_SET_RING_THREE,
            DEPTH_OFF_SET_RING_THREE);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        int realBudget = elementBudget >= 2000 ? elementBudget : Math.min(2000, elementBudget * 5);

        int built = 0;
        built = survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            realBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        built += survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_RING_ONE,
            stackSize,
            HORIZONTAL_OFF_SET_RING_ONE,
            VERTICAL_OFF_SET_RING_ONE,
            DEPTH_OFF_SET_RING_ONE,
            realBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        built += this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_RING_TWO,
            stackSize,
            HORIZONTAL_OFF_SET_RING_TWO,
            VERTICAL_OFF_SET_RING_TWO,
            DEPTH_OFF_SET_RING_TWO,
            realBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        built += this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_RING_THREE,
            stackSize,
            HORIZONTAL_OFF_SET_RING_THREE,
            VERTICAL_OFF_SET_RING_THREE,
            DEPTH_OFF_SET_RING_THREE,
            realBudget,
            env,
            false,
            true);
        return built;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (isRendererDisabled) {
            isRendererDisabled = false;
            // let the renderer automatically rebuild itself as needed through normal logic
        } else {
            isRendererDisabled = true;
            if (isRenderActive) destroyRenderer();
        }
        aPlayer.addChatMessage(
            new ChatComponentTranslation(
                isRendererDisabled ? "Render_NanoPhagocytosisPlant_Disabled" : "Render_NanoPhagocytosisPlant_Enabled"));
    }

    private TileEntityNanoPhagocytosisPlant getRenderer() {
        ChunkCoordinates renderPos = getRenderPos();
        TileEntity tile = this.getBaseMetaTileEntity()
            .getWorld()
            .getTileEntity(renderPos.posX, renderPos.posY, renderPos.posZ);

        if (tile instanceof TileEntityNanoPhagocytosisPlant nanoTile) {
            return nanoTile;
        }
        return null;
    }

    private void updateRenderer() {
        TileEntityNanoPhagocytosisPlant tile = getRenderer();
        if (tile == null) return;

        tile.setStarRadius(starSize);
        tile.setColor(starColors.getByName(selectedStarColor));

        tile.updateToClient();
    }

    private void createRenderer() {
        ChunkCoordinates renderPos = getRenderPos();

        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, Blocks.air);
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, BlockLoader.BlockNanoPhagocytosisPlantRender);
        TileEntityNanoPhagocytosisPlant rendererTileEntity = (TileEntityNanoPhagocytosisPlant) this
            .getBaseMetaTileEntity()
            .getWorld()
            .getTileEntity(renderPos.posX, renderPos.posY, renderPos.posZ);

        destroyFirstRing();
        destroySecondRing();
        destroyThirdRing();

        rendererTileEntity.setRenderRotation(getDirection());
        updateRenderer();

        isRenderActive = true;
        enableWorking();
    }

    private void destroyRenderer() {
        ChunkCoordinates renderPos = getRenderPos();
        this.getBaseMetaTileEntity()
            .getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, Blocks.air);

        buildFirstRing();
        buildSecondRing();
        buildThirdRing();

        isRenderActive = false;
        disableWorking();
    }

    private ChunkCoordinates getRenderPos() {
        IGregTechTileEntity tile = this.getBaseMetaTileEntity();
        int x = tile.getXCoord();
        int y = tile.getYCoord();
        int z = tile.getZCoord();

        ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();
        ForgeDirection up = getExtendedFacing().getRelativeUpInWorld();

        double xOffset = 9 * back.offsetX + 13 * up.offsetX;
        double yOffset = 9 * back.offsetY + 13 * up.offsetY;
        double zOffset = 9 * back.offsetZ + 13 * up.offsetZ;

        return new ChunkCoordinates((int) (x + xOffset), (int) (y + yOffset), (int) (z + zOffset));
    }

    private void destroyFirstRing() {
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_ONE_AIR,
            null,
            false,
            HORIZONTAL_OFF_SET_RING_ONE,
            VERTICAL_OFF_SET_RING_ONE,
            DEPTH_OFF_SET_RING_ONE);
    }

    private void destroySecondRing() {
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_TWO_AIR,
            null,
            false,
            HORIZONTAL_OFF_SET_RING_TWO,
            VERTICAL_OFF_SET_RING_TWO,
            DEPTH_OFF_SET_RING_TWO);
    }

    private void destroyThirdRing() {
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_THREE_AIR,
            null,
            false,
            HORIZONTAL_OFF_SET_RING_THREE,
            VERTICAL_OFF_SET_RING_THREE,
            DEPTH_OFF_SET_RING_THREE);
    }

    private void buildFirstRing() {
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_ONE,
            null,
            false,
            HORIZONTAL_OFF_SET_RING_ONE,
            VERTICAL_OFF_SET_RING_ONE,
            DEPTH_OFF_SET_RING_ONE);
    }

    private void buildSecondRing() {
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_TWO,
            null,
            false,
            HORIZONTAL_OFF_SET_RING_TWO,
            VERTICAL_OFF_SET_RING_TWO,
            DEPTH_OFF_SET_RING_TWO);
    }

    private void buildThirdRing() {
        buildPiece(
            STRUCTURE_PIECE_MAIN_RING_THREE,
            null,
            false,
            HORIZONTAL_OFF_SET_RING_THREE,
            VERTICAL_OFF_SET_RING_THREE,
            DEPTH_OFF_SET_RING_THREE);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        wirelessMode = false;
        if (isRenderActive) {
            if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
                || !checkPiece(
                    STRUCTURE_PIECE_MAIN_RING_ONE_AIR,
                    HORIZONTAL_OFF_SET_RING_ONE,
                    VERTICAL_OFF_SET_RING_ONE,
                    DEPTH_OFF_SET_RING_ONE)
                || !checkPiece(
                    STRUCTURE_PIECE_MAIN_RING_TWO_AIR,
                    HORIZONTAL_OFF_SET_RING_TWO,
                    VERTICAL_OFF_SET_RING_TWO,
                    DEPTH_OFF_SET_RING_TWO)
                || !checkPiece(
                    STRUCTURE_PIECE_MAIN_RING_THREE_AIR,
                    HORIZONTAL_OFF_SET_RING_THREE,
                    VERTICAL_OFF_SET_RING_THREE,
                    DEPTH_OFF_SET_RING_THREE)) {
                destroyRenderer();
                return false;
            }
        } else if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
            || !checkPiece(
                STRUCTURE_PIECE_MAIN_RING_ONE,
                HORIZONTAL_OFF_SET_RING_ONE,
                VERTICAL_OFF_SET_RING_ONE,
                DEPTH_OFF_SET_RING_ONE)
            || !checkPiece(
                STRUCTURE_PIECE_MAIN_RING_TWO,
                HORIZONTAL_OFF_SET_RING_TWO,
                VERTICAL_OFF_SET_RING_TWO,
                DEPTH_OFF_SET_RING_TWO)
            || !checkPiece(
                STRUCTURE_PIECE_MAIN_RING_THREE,
                HORIZONTAL_OFF_SET_RING_THREE,
                VERTICAL_OFF_SET_RING_THREE,
                DEPTH_OFF_SET_RING_THREE)) {
                    return false;
                }

        if (!isRenderActive && !isRendererDisabled && mTotalRunTime > 0) {
            createRenderer();
        }

        energyHatchTier = checkEnergyHatchTier();
        wirelessMode = mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
        return tCountCasing > 1;
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        if (isRenderActive) {
            destroyRenderer();
        }
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4 && wirelessMode) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                enablePerfectOverclock();
                return super.process();
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe)
                    .setEUtDiscount(0.4 - (mParallelTier / 50.0) * Math.pow(0.95, getMaxInputVoltage()))
                    .setDurationModifier(0.1 * Math.pow(0.75, mParallelTier) * Math.pow(0.95, getMaxInputVoltage()));
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.maceratorRecipes;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("isRenderActive", isRenderActive);
        aNBT.setBoolean("isRendererDisabled", isRendererDisabled);
        aNBT.setString("selectedStarColor", selectedStarColor);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("starSize")) starSize = aNBT.getInteger("starSize");
        if (aNBT.hasKey("selectedStarColor")) selectedStarColor = aNBT.getString("selectedStarColor");
        isRenderActive = aNBT.getBoolean("isRenderActive");
        isRendererDisabled = aNBT.getBoolean("isRendererDisabled");
    }

}
