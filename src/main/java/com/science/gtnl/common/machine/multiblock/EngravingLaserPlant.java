package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase.CustomHatchElement.ParallelCon;
import static goodgenerator.loader.Loaders.compactFusionCoil;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.validMTEList;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.Utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.common.machine.multiMachineClasses.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class EngravingLaserPlant extends WirelessEnergyMultiMachineBase<EngravingLaserPlant> {

    private int casingTier;
    private static final int HORIZONTAL_OFF_SET = 10;
    private static final int VERTICAL_OFF_SET = 9;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String ELP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/engraving_laser_plant";
    public static final String[][] shape = StructureUtils.readStructureFromFile(ELP_STRUCTURE_FILE_PATH);

    public EngravingLaserPlant(String aName) {
        super(aName);
    }

    public EngravingLaserPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EngravingLaserPlant(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("EngravingLaserPlantRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_01"))
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
            .beginStructureBlock(21, 12, 22, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EngravingLaserPlant_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 7);
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
    public IStructureDefinition<EngravingLaserPlant> getStructureDefinition() {
        return StructureDefinition.<EngravingLaserPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings10, 8))
            .addElement('B', ofBlock(sBlockCasingsTT, 0))
            .addElement('C', ofBlock(sBlockCasings6, 9))
            .addElement(
                'D',
                buildHatchAdder(EngravingLaserPlant.class)
                    .atLeast(
                        Maintenance,
                        InputBus,
                        OutputBus,
                        InputHatch,
                        OutputHatch,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 7))))
            .addElement('E', ofBlock(sBlockCasings8, 12))
            .addElement('F', ofBlock(sBlockCasings9, 1))
            .addElement('G', ofBlock(BlockLoader.metaCasing, 5))
            .addElement('H', ofBlock(BlockLoader.metaCasing, 4))
            .addElement('I', ofBlock(compactFusionCoil, 2))
            .addElement(
                'J',
                ofBlocksTiered(
                    (block, meta) -> block == Loaders.componentAssemblylineCasing ? meta : -1,
                    IntStream.range(0, 13)
                        .mapToObj(i -> Pair.of(Loaders.componentAssemblylineCasing, i))
                        .collect(Collectors.toList()),
                    -2,
                    (t, meta) -> t.casingTier = meta,
                    t -> t.casingTier))
            .addElement('K', ofBlock(sBlockCasings10, 11))
            .addElement('L', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('M', ofFrame(Materials.Neutronium))
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        casingTier = -2;
        mCountCasing = 0;
        wirelessMode = false;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        mEnergyHatchTier = checkEnergyHatchTier();
        mParallelTier = getParallelTier(aStack);
        setWirelessMode(mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty());
        return mCountCasing > 750;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

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
            protected GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(
                        0.4 - (mParallelTier / 50.0) * Math.pow(0.95, mGlassTier) * Math.pow(0.95, casingTier))
                    .setDurationModifier(
                        0.1 * Math.pow(0.75, mParallelTier) * Math.pow(0.95, mGlassTier) * Math.pow(0.95, casingTier));
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    public long getMachineVoltageLimit() {
        if (casingTier < 0) return 0;
        if (wirelessMode) {
            if (casingTier >= 11) {
                return V[Math.min(mParallelTier + 1, 14)];
            } else {
                return V[Math.min(Math.min(mParallelTier + 1, casingTier + 3), 14)];
            }
        } else if (casingTier >= 11) {
            return V[mEnergyHatchTier];
        } else {
            return V[Math.min(casingTier + 3, mEnergyHatchTier)];
        }
    }

    public int checkEnergyHatchTier() {
        int tier = 0;
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        return tier;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.laserEngraverRecipes;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mGlassTier", mGlassTier);
        aNBT.setInteger("casingTier", casingTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mGlassTier = aNBT.getInteger("mGlassTier");
        casingTier = aNBT.getInteger("casingTier");
    }

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 1];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = StatCollector.translateToLocal("scanner.info.CASS.tier")
            + (casingTier >= 0 ? GTValues.VN[casingTier + 1] : "None!");
        return ret;
    }

}
