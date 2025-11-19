package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static tectech.thing.casing.TTCasingsContainer.*;

import javax.annotation.Nonnull;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.machine.multiblock.LargeIncubator;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ParallelHelper;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import bartworks.API.recipe.BartWorksRecipeMaps;
import bartworks.util.BWUtil;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;

public class MicroorganismMaster extends WirelessEnergyMultiMachineBase<MicroorganismMaster> {

    private static final int HORIZONTAL_OFF_SET = 14;
    private static final int VERTICAL_OFF_SET = 23;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/microorganism_master";
    private static final String[][] shape = StructureUtils.readStructureFromFile(MM_STRUCTURE_FILE_PATH);

    public MicroorganismMaster(String aName) {
        super(aName);
    }

    public MicroorganismMaster(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MicroorganismMaster(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("MicroorganismMasterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MicroorganismMaster_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MicroorganismMaster_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MicroorganismMaster_02"))
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
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(29, 25, 30, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_MicroorganismMaster_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_MicroorganismMaster_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_MicroorganismMaster_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_MicroorganismMaster_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_MicroorganismMaster_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings8, 0);
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
    public IStructureDefinition<MicroorganismMaster> getStructureDefinition() {
        return StructureDefinition.<MicroorganismMaster>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings8, 1))
            .addElement('B', ofBlock(sBlockCasingsTT, 6))
            .addElement('C', ofBlock(BlockLoader.metaCasing, 18))
            .addElement('D', ofBlock(sBlockReinforced, 2))
            .addElement('E', ofBlock(sBlockCasings4, 12))
            .addElement('F', ofBlock(sBlockCasings8, 7))
            .addElement('G', ofBlock(sBlockCasings9, 13))
            .addElement('H', ofBlock(sBlockCasings10, 6))
            .addElement('I', ofBlock(sBlockCasings9, 10))
            .addElement('J', ofBlock(sBlockCasings4, 10))
            .addElement('K', ofBlock(ModBlocks.blockSpecialMultiCasings, 11))
            .addElement('L', ofBlock(sBlockCasingsTT, 4))
            .addElement(
                'M',
                buildHatchAdder(MicroorganismMaster.class)
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
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 0))))
            .addElement('N', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('O', ofBlock(sBlockCasings10, 7))
            .addElement('P', ofBlock(Blocks.sponge, 0))
            .addElement('Q', ofFrame(Materials.Naquadria))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing > 50;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (!BWUtil.areStacksEqualOrNull((ItemStack) recipe.mSpecialItems, getControllerSlot()))
                    return CheckRecipeResultRegistry.NO_RECIPE;

                if (wirelessMode && recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                return super.validateRecipe(recipe);
            }

            @Nonnull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

            @NotNull
            @Override
            public GTNL_ParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(
                    LargeIncubator.recipeWithMultiplier(recipe, inputFluids, mOutputHatches.get(0), getTrueParallel()));
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.95, mGlassTier);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.95, mGlassTier);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return BartWorksRecipeMaps.bacterialVatRecipes;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mGlassTier", mGlassTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mGlassTier = aNBT.getInteger("mGlassTier");
    }

}
