package com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gtPlusPlus.core.block.ModBlocks.blockCasings4Misc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.recipes.data.NanitesIntegratedProcessingRecipesData;
import com.science.gtnl.utils.recipes.metadata.NanitesIntegratedProcessingMetadata;

import bartworks.util.BWUtil;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
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
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
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
    public boolean oreModule = false;
    public boolean bioModule = false;
    public boolean polModule = false;

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
            .addTecTechHatchInfo()
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
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10);
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
        if (aBaseMetaTileEntity.isServerSide() && aTick % 100 == 0) {
            if (!moduleMachine.isEmpty() && moduleMachine.size() <= 3) {
                for (NanitesBaseModule<?> module : GTUtility.validMTEList(moduleMachine)) {
                    if (allowModuleConnection(module, this)) {
                        module.connect();
                        module.setEUtDiscount(getEUtDiscount());
                        module.setDurationModifier(getDurationModifier());
                        module.setMaxParallel(getMaxParallelRecipes());
                        module.setHeatingCapacity(mHeatingCapacity);
                    } else {
                        module.disconnect();
                        module.setEUtDiscount(1);
                        module.setDurationModifier(1);
                        module.setMaxParallel(0);
                        module.setHeatingCapacity(0);
                    }

                    if (!module.isConnected) continue;
                    if (module instanceof BioengineeringModule) {
                        bioModule = module.mMachine;
                    }

                    if (module instanceof PolymerTwistingModule) {
                        polModule = module.mMachine;
                    }

                    if (module instanceof OreExtractionModule) {
                        oreModule = module.mMachine;
                    }
                }
            } else if (moduleMachine.size() > 3) {
                for (NanitesBaseModule<?> module : GTUtility.validMTEList(moduleMachine)) {
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
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureUtility.buildHatchAdder(NanitesIntegratedProcessingCenter.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))))
            .addElement('B', StructureUtility.ofBlock(sBlockCasingsTT, 0))
            .addElement('C', StructureUtility.ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 10))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockMetal5, 1))
            .addElement('F', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement(
                'G',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(
                            NanitesIntegratedProcessingCenter::setMCoilLevel,
                            NanitesIntegratedProcessingCenter::getMCoilLevel))))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 10))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 9))
            .addElement('K', StructureUtility.ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('L', StructureUtility.ofBlock(blockCasings4Misc, 4))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('N', GTStructureUtility.ofFrame(Materials.CosmicNeutronium))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('P', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement(
                'Q',
                StructureUtility.ofChain(
                    HatchElementBuilder.<NanitesIntegratedProcessingCenter>builder()
                        .atLeast(moduleElement.Module)
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .buildAndChain(GregTechAPI.sBlockCasings8, 10),
                    StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7),
                    StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0),
                    StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 0),
                    StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 8),
                    StructureUtility.ofBlock(GregTechAPI.sBlockReinforced, 2)))
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
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        moduleMachine.clear();
        bioModule = false;
        polModule = false;
        oreModule = false;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None && mGlassTier > 0 && mCountCasing > 1;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.NanitesIntegratedProcessingRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @Override
            public @NotNull CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (wirelessMode && recipe.mEUt > GTValues.V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                NanitesIntegratedProcessingRecipesData data = recipe.getMetadataOrDefault(
                    NanitesIntegratedProcessingMetadata.INSTANCE,
                    new NanitesIntegratedProcessingRecipesData(false, false, false));

                if (data.bioengineeringModule && !bioModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_bio_module");
                }
                if (data.oreExtractionModule && !oreModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_ore_module");
                }
                if (data.polymerTwistingModule && !polModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_pol_module");
                }

                return recipe.mSpecialValue <= mHeatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
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
            for (NanitesBaseModule<?> module : GTUtility.validMTEList(moduleMachine)) {
                module.disconnect();
            }
        }
        super.onRemoval();
    }

}
