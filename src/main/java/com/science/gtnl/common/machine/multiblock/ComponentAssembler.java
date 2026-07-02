package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gtPlusPlus.core.block.ModBlocks.blockCasings3Misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLStructureChannels;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class ComponentAssembler extends MultiMachineBase<ComponentAssembler> implements ISurvivalConstructable {

    private static final String CA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/component_assembler";
    private static final String[][] shape = StructureUtils.readStructureFromFile(CA_STRUCTURE_FILE_PATH);
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;
    public static final List<Pair<Block, Integer>> COMPONENT_CASING_VARIANTS = createComponentCasingVariants();

    public int mCasingTier;

    public static List<Pair<Block, Integer>> createComponentCasingVariants() {
        List<Pair<Block, Integer>> casingVariants = new ArrayList<>(8);
        for (int tier = 0; tier < 8; tier++) {
            casingVariants.add(Pair.of(Loaders.componentAssemblylineCasing, tier));
        }
        return casingVariants;
    }

    public ComponentAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ComponentAssembler(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ComponentAssembler(mName);
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mCasingTier = -2;
    }

    @Override
    public IStructureDefinition<ComponentAssembler> getStructureDefinition() {
        return StructureDefinition.<ComponentAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement(
                'B',
                GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        (block, meta) -> block == Loaders.componentAssemblylineCasing ? meta : -1,
                        COMPONENT_CASING_VARIANTS,
                        -2,
                        (t, meta) -> t.mCasingTier = meta,
                        t -> t.mCasingTier)))
            .addElement(
                'C',
                GTStructureUtility.buildHatchAdder(ComponentAssembler.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 6))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 10))
            .addElement('G', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement('H', StructureUtility.ofBlock(blockCasings3Misc, 2))
            .build();
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 50);
        checkHatchExact(errors, HatchElement.Maintenance, 1);
        checkHatchMax(errors, HatchElement.Energy, 2);
        if (mCasingTier >= 8) {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
        }
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mSpecialValue > mCasingTier + 1) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipe.mSpecialValue);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Override
            public @NotNull GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GoodGeneratorRecipeMaps.componentAssemblyLineRecipes;
    }

    @Override
    public double getEUtDiscount() {
        return 0.8;
    }

    @Override
    public double getDurationModifier() {
        return 1.0 / 2.0;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 4;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(!useSingleAmp);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 16;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings2, 0);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 1];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = StatCollector.translateToLocal("scanner.info.CASS.tier")
            + (mCasingTier >= 0 ? GTValues.VN[mCasingTier + 1] : "None!");
        return ret;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ComponentAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ComponentAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ComponentAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ComponentAssembler_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ComponentAssembler_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ComponentAssembler_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ComponentAssembler_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ComponentAssembler_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ComponentAssembler_07"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(7, 5, 5, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_ComponentAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ComponentAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ComponentAssembler_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ComponentAssembler_Casing"))
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ComponentAssembler_Casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        inputSeparation = !inputSeparation;
        GTUtility.sendChatTrans(aPlayer, "GT5U.machines.separatebus." + inputSeparation);
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        batchMode = !batchMode;
        if (batchMode) {
            GTUtility.sendChatTrans(aPlayer, "misc.BatchModeTextOn");
        } else {
            GTUtility.sendChatTrans(aPlayer, "misc.BatchModeTextOff");
        }
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("casingTier", mCasingTier);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mCasingTier = aNBT.getInteger("casingTier");
        if (!aNBT.hasKey(INPUT_SEPARATION_NBT_KEY)) {
            inputSeparation = aNBT.getBoolean("mSeparate");
        }
    }

    @Override
    protected int getGlassEnergyTierLimit() {
        return VoltageIndex.UV;
    }
}
