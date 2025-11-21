package com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.blockCasings4Misc;
import static gtnhlanth.common.register.LanthItemList.ELECTRODE_CASING;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.utils.recipes.data.NanitesIntegratedProcessingRecipesData;
import com.science.gtnl.utils.recipes.metadata.NanitesIntegratedProcessingMetadata;

import bartworks.util.BWUtil;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtnhlanth.common.register.LanthItemList;

public class NanitesIntegratedProcessingCenter
    extends WirelessEnergyMultiMachineBase<NanitesIntegratedProcessingCenter> {

    private static final int HORIZONTAL_OFF_SET = 15;
    private static final int VERTICAL_OFF_SET = 20;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String NIPC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/nanites_integrated_processing_center";
    private static final String[][] shape = StructureUtils.readStructureFromFile(NIPC_STRUCTURE_FILE_PATH);

    public ArrayList<NanitesBaseModule<?>> moduleMachine = new ArrayList<>();
    public boolean isOreModule = false;
    public boolean isBioModule = false;
    public boolean isPolModule = false;

    public NanitesIntegratedProcessingCenter(String aName) {
        super(aName);
    }

    public NanitesIntegratedProcessingCenter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NanitesIntegratedProcessingCenter(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("NanitesIntegratedProcessingCenterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NanitesIntegratedProcessingCenter_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NanitesIntegratedProcessingCenter_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_NanitesIntegratedProcessingCenter_02"))
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
            .beginStructureBlock(31, 22, 46, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_NanitesIntegratedProcessingCenter_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_NanitesIntegratedProcessingCenter_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_NanitesIntegratedProcessingCenter_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_NanitesIntegratedProcessingCenter_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_NanitesIntegratedProcessingCenter_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings8, 10);
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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (!moduleMachine.isEmpty() && moduleMachine.size() <= 3) {
                for (NanitesBaseModule<?> module : moduleMachine) {
                    if (allowModuleConnection(module, this)) {
                        module.connect();
                        module.setEUtDiscount(getEUtDiscount());
                        module.setDurationModifier(getDurationModifier());
                        module.setMaxParallel(getTrueParallel());
                        module.setHeatingCapacity(mHeatingCapacity);
                    } else {
                        module.disconnect();
                        module.setEUtDiscount(1);
                        module.setDurationModifier(1);
                        module.setMaxParallel(0);
                        module.setHeatingCapacity(0);
                    }
                }
            } else if (moduleMachine.size() > 3) {
                for (NanitesBaseModule<?> module : moduleMachine) {
                    module.disconnect();
                }
            }
            if (mEfficiency < 0) mEfficiency = 0;

        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public IStructureDefinition<NanitesIntegratedProcessingCenter> getStructureDefinition() {
        return StructureDefinition.<NanitesIntegratedProcessingCenter>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(NanitesIntegratedProcessingCenter.class)
                    .atLeast(Maintenance, InputBus, OutputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 10))))
            .addElement('B', ofBlock(sBlockCasingsTT, 0))
            .addElement('C', ofBlockAnyMeta(ELECTRODE_CASING))
            .addElement('D', ofBlock(sBlockCasings3, 10))
            .addElement('E', ofBlock(sBlockMetal5, 1))
            .addElement('F', ofBlock(BlockLoader.metaCasing, 5))
            .addElement(
                'G',
                GTStructureChannels.HEATING_COIL.use(
                    activeCoils(
                        ofCoil(
                            NanitesIntegratedProcessingCenter::setMCoilLevel,
                            NanitesIntegratedProcessingCenter::getMCoilLevel))))
            .addElement('H', ofBlock(sBlockCasings4, 10))
            .addElement('I', ofBlock(sBlockCasings10, 3))
            .addElement('J', ofBlock(sBlockCasingsSE, 9))
            .addElement('K', ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('L', ofBlock(blockCasings4Misc, 4))
            .addElement('M', ofBlock(sBlockCasings2, 5))
            .addElement('N', ofFrame(Materials.CosmicNeutronium))
            .addElement('O', ofBlock(sBlockCasings8, 1))
            .addElement('P', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement(
                'Q',
                ofChain(
                    HatchElementBuilder.<NanitesIntegratedProcessingCenter>builder()
                        .atLeast(moduleElement.Module)
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .buildAndChain(sBlockCasings8, 10),
                    ofBlock(sBlockCasings8, 7),
                    ofBlock(sBlockCasings8, 0),
                    ofBlock(sBlockCasings4, 0),
                    ofBlock(sBlockCasings10, 8),
                    ofBlock(sBlockReinforced, 2)))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mHeatingCapacity > 0;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        mHeatingCapacity = (int) this.getMCoilLevel()
            .getHeat() + 100 * (BWUtil.getTier(this.getMaxInputEu()) - 2);

        for (NanitesBaseModule<?> module : moduleMachine) {
            if (module instanceof BioengineeringModule) {
                isBioModule = module.isBioModule;
            }

            if (module instanceof PolymerTwistingModule) {
                isPolModule = module.isPolModule;
            }

            if (module instanceof OreExtractionModule) {
                isOreModule = module.isOreModule;
            }
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        moduleMachine.clear();
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None && mGlassTier > 0 && mCountCasing > 1;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.NanitesIntegratedProcessingRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Override
            public @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (wirelessMode && recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                NanitesIntegratedProcessingRecipesData data = recipe.getMetadataOrDefault(
                    NanitesIntegratedProcessingMetadata.INSTANCE,
                    new NanitesIntegratedProcessingRecipesData(false, false, false));

                if (data.bioengineeringModule && !isBioModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_bio_module");
                }
                if (data.oreExtractionModule && !isOreModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_ore_module");
                }
                if (data.polymerTwistingModule && !isPolModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_pol_module");
                }

                return recipe.mSpecialValue <= mHeatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }

            @Nonnull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(mHeatingCapacity)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return 1 - (mParallelTier / 50.0) * Math.pow(0.80, getMCoilLevel().getTier());
    }

    @Override
    public double getDurationModifier() {
        return Math.pow(0.75, mParallelTier) * Math.pow(0.80, getMCoilLevel().getTier());
    }

    public boolean addModuleToMachineList(IGregTechTileEntity tileEntity, int baseCasingIndex) {
        if (tileEntity == null) {
            return false;
        }
        IMetaTileEntity metaTileEntity = tileEntity.getMetaTileEntity();
        if (metaTileEntity == null) {
            return false;
        }
        if (metaTileEntity instanceof NanitesBaseModule<?>module) {
            return moduleMachine.add(module);
        }
        return false;
    }

    public enum moduleElement implements IHatchElement<NanitesIntegratedProcessingCenter> {

        Module(NanitesIntegratedProcessingCenter::addModuleToMachineList, NanitesBaseModule.class) {

            @Override
            public long count(NanitesIntegratedProcessingCenter tileEntity) {
                return tileEntity.moduleMachine.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<NanitesIntegratedProcessingCenter> adder;

        @SafeVarargs
        moduleElement(IGTHatchAdder<NanitesIntegratedProcessingCenter> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super NanitesIntegratedProcessingCenter> adder() {
            return adder;
        }
    }

    public static boolean allowModuleConnection(NanitesBaseModule<?> module, NanitesIntegratedProcessingCenter center) {

        if (module instanceof BioengineeringModule) {
            return true;
        }

        if (module instanceof PolymerTwistingModule) {
            return true;
        }

        if (module instanceof OreExtractionModule && center.mGlassTier > 8) {
            return true;
        }

        return false;
    }

    @Override
    public void onRemoval() {
        if (moduleMachine != null && !moduleMachine.isEmpty()) {
            for (NanitesBaseModule<?> module : moduleMachine) {
                module.disconnect();
            }
        }
        super.onRemoval();
    }

}
