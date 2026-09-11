package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.casing.Casings;
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
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import kekztech.common.Blocks;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class PetrochemicalPlant extends MultiMachineBase<PetrochemicalPlant> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/petrochemical_plant";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PP_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 22;
    private static final int VERTICAL_OFF_SET = 56;
    private static final int DEPTH_OFF_SET = 0;

    public PetrochemicalPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.petrochemical_plant.name";
    }

    public PetrochemicalPlant(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PetrochemicalPlant(this.mName);
    }

    @Override
    public IStructureDefinition<PetrochemicalPlant> getStructureDefinition() {
        return StructureDefinition.<PetrochemicalPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlockAnyMeta(Blocks.yszUnit))
            .addElement('B', HatchElement.Muffler.newAny(getCasingTextureID(), 8))
            .addElement('C', Casings.SolidSteelMachineCasing.asElement())
            .addElement('D', Casings.BronzePipeCasing.asElement())
            .addElement('E', Casings.SteelPipeCasing.asElement())
            .addElement('F', Casings.TitaniumPipeCasing.asElement())
            .addElement('G', Casings.StableTitaniumMachineCasing.asElement())
            .addElement('H', Casings.CleanStainlessSteelMachineCasing.asElement())
            .addElement('I', Casings.TurbineCasing.asElement())
            .addElement('J', Casings.StainlessSteelTurbineCasing.asElement())
            .addElement('K', Casings.MultiUseCasing.asElement())
            .addElement(
                'L',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility
                            .ofCoil(PetrochemicalPlant::setMCoilLevel, PetrochemicalPlant::getMCoilLevel))))
            .addElement('M', Casings.PTFEPipeCasing.asElement())
            .addElement('N', Casings.IntegralEncasementEV.asElement())
            .addElement(
                'O',
                GTStructureUtility.buildHatchAdder(PetrochemicalPlant.class)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, Casings.PressureContainmentCasing.asElement())))
            .addElement('P', Casings.ElectricCompressorCasing.asElement())
            .addElement('Q', Casings.BlastSmelterHeatContainmentCoil.asElement())
            .addElement('R', Casings.PBIPipeCasing.asElement())
            .addElement('S', GTStructureUtility.ofFrame(Materials.NiobiumTitanium))
            .addElement('T', GTStructureUtility.ofFrame(Materials.StainlessSteel))
            .addElement('U', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement('V', GTStructureUtility.ofFrame(Materials.RedstoneAlloy))
            .addElement('W', GTStructureUtility.ofFrame(Materials.Vanadium))
            .addElement('X', Casings.WashPlantCasing.asElement())
            .addElement('Y', Casings.MultitankExteriorCasing.asElement())
            .addElement('Z', Casings.SturdyAluminiumMachineCasing.asElement())
            .addElement('0', Casings.ReinforcedGlass.asElement())
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 5);
        checkHatchExact(errors, HatchElement.Muffler, 8);
    }

    @Override
    protected boolean requiresCoilStructureCheck() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.PetrochemicalPlantRecipes;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        ItemStack[] outputItems = processingLogic.getOutputItems();
        if (outputItems != null) {
            for (ItemStack itemStack : outputItems) {
                if (itemStack != null) {
                    itemStack.stackSize *= (getMCoilLevel().getTier() + 1)
                        * GTUtility.getTier(this.getMaxInputVoltage())
                        * 6;
                }
            }
        }
        mOutputItems = outputItems;

        FluidStack[] outputFluids = processingLogic.getOutputFluids();

        mOutputFluids = outputFluids;
        if (outputFluids != null) {
            List<FluidStack> expandedFluids = new ArrayList<>();
            for (FluidStack fluidStack : outputFluids) {
                if (fluidStack != null) {
                    long totalAmount = (long) fluidStack.amount * (getMCoilLevel().getTier() + 1)
                        * GTUtility.getTier(this.getMaxInputVoltage())
                        * 6;

                    while (totalAmount > 0) {
                        int stackSize = (int) Math.min(totalAmount, Integer.MAX_VALUE);
                        expandedFluids.add(new FluidStack(fluidStack.getFluid(), stackSize));
                        totalAmount -= stackSize;
                    }
                }
            }
            mOutputFluids = expandedFluids.toArray(new FluidStack[0]);
        }

        return result;
    }

    @Override
    public int getMaxParallelRecipes() {
        return getMCoilLevel().getTier() * 40;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return Casings.PressureContainmentCasing.getTextureId();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.2"))
            .addPerfectOCInfo()
            .addSupportAny()
            .beginStructureBlock(28, 60, 65, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.casing"))
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.casing"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.casing"))
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.casing"))
            .addMaintenanceHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.casing"))
            .addMufflerHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.petrochemical_plant.tooltip.muffler"),
                8)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }
}
