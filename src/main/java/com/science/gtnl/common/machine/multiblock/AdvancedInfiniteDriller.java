package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL_ACTIVE;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.math.MainAxisAlignment;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.Column;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedRow;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.VoidingMode;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.blocks.BlockCasings8;
import gtneioreplugin.plugin.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class AdvancedInfiniteDriller extends MultiMachineBase<AdvancedInfiniteDriller>
    implements ISurvivalConstructable {

    private static int excessFuel = 0;
    private static int drillTier = 0;
    private static int needEu = 0;

    private static IStructureDefinition<AdvancedInfiniteDriller> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String AID_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/advanced_infinite_driller";
    public static String[][] shape = StructureUtils.readStructureFromFile(AID_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 12;
    public final int VERTICAL_OFF_SET = 39;
    public final int DEPTH_OFF_SET = 0;
    private static final int CASING_INDEX = ((BlockCasings8) sBlockCasings8).getTextureIndex(10);

    public AdvancedInfiniteDriller(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public AdvancedInfiniteDriller(String aName) {
        super(aName);
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public float getSpeedBonus() {
        return 1F;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("AdvancedInfiniteDrillerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_11"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(25, 41, 25, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .addInputHatch(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<AdvancedInfiniteDriller> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<AdvancedInfiniteDriller>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(Loaders.MAR_Casing, 0))
                .addElement('B', ofBlock(BlockLoader.MetaCasing, 5))
                .addElement('C', ofBlock(BlockLoader.MetaCasing, 16))
                .addElement('D', ofBlock(BlockLoader.MetaCasing, 18))
                .addElement('E', ofBlock(sBlockCasings1, 14))
                .addElement('F', ofBlock(sSolenoidCoilCasings, 5))
                .addElement('G', ofBlock(sBlockCasings3, 11))
                .addElement('H', ofBlock(sBlockCasings8, 1))
                .addElement('I', ofBlock(sBlockCasings8, 7))
                .addElement(
                    'J',
                    buildHatchAdder(AdvancedInfiniteDriller.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings8, 10))))
                .addElement('K', ofFrame(Materials.Neutronium))
                .addElement('L', ofBlock(sBlockMetal8, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ORE_DRILL_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ORE_DRILL)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (excessFuel < 300) {
            excessFuel = 300;
        }

        if (excessFuel > 10000) {
            excessFuel = 10000;
        }

        if (drillTier == 0) {
            excessFuel = Math.max(300, excessFuel - 4);
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        ArrayList<FluidStack> storedFluids = getStoredFluids();
        if (storedFluids.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (excessFuel < 2000) {
            int consumptionCount = 0;

            for (FluidStack tFluid : storedFluids) {
                if (tFluid != null && tFluid.getFluid()
                    .getName()
                    .equals("pyrotheum")) {
                    int consumption = (int) Math.pow(excessFuel, 1.3);
                    if (tFluid.amount >= consumption) {
                        tFluid.amount -= consumption;
                        consumptionCount++;
                    }
                }
            }

            if (consumptionCount > 0) {
                excessFuel += consumptionCount;
                this.mMaxProgresstime = 32;
                this.lEUt = (int) -TierEU.RECIPE_ZPM;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            } else {
                excessFuel = Math.max(300, excessFuel - 4);
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        } else {
            needEu = 0;
            List<FluidStack> outputFluids = new ArrayList<>();
            for (ItemStack item : getAllStoredInputs()) {
                if (item.getItem() != null) {
                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Ow")))) {
                        outputFluids.add(calculateOutput(0.4, 625, Materials.Oil.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.4, 350, Materials.OilHeavy.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.4, 625, Materials.OilLight.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.4, 625, Materials.OilMedium.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.4, 350, Materials.NatruralGas.getFluid(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Mo")))) {
                        outputFluids.add(calculateOutput(0.4, 200, Materials.SaltWater.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.85, 425, Materials.Helium_3.getGas(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Ma")))) {
                        outputFluids.add(calculateOutput(0.5, 400, Materials.SaltWater.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.5, 400, Materials.Chlorobenzene.getFluid(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Eu")))) {
                        outputFluids.add(calculateOutput(0.5, 800, Materials.SaltWater.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.4, 200, Materials.OilExtraHeavy.getFluid(1L)));
                        outputFluids
                            .add(calculateOutput(0.4, 3500, FluidRegistry.getFluidStack("ic2distilledwater", 1)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Ca")))) {
                        outputFluids.add(calculateOutput(0.5, 200, Materials.Oxygen.getGas(1L)));
                        outputFluids.add(calculateOutput(0.5, 200, Materials.LiquidOxygen.getFluid(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Rb")))) {
                        outputFluids.add(calculateOutput(0.4, 625, Materials.NatruralGas.getGas(1L)));
                        outputFluids.add(calculateOutput(0.4, 625, Materials.OilExtraHeavy.getFluid(1L)));
                        outputFluids
                            .add(calculateOutput(0.5, 5000, FluidRegistry.getFluidStack("ic2distilledwater", 1)));
                        outputFluids.add(calculateOutput(0.4, 820, Materials.Lava.getFluid(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Io")))) {
                        outputFluids.add(calculateOutput(0.45, 350, Materials.SulfuricAcid.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.45, 750, Materials.CarbonDioxide.getGas(1L)));
                        outputFluids.add(calculateOutput(0.4, 650, Materials.Lead.getMolten(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Me")))) {
                        outputFluids.add(calculateOutput(0.769, 800, Materials.Helium_3.getGas(1L)));
                        outputFluids.add(calculateOutput(0.4, 400, Materials.Iron.getMolten(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Ve")))) {
                        outputFluids.add(calculateOutput(0.4, 250, Materials.SulfuricAcid.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.4, 1500, Materials.CarbonDioxide.getGas(1L)));
                        outputFluids.add(calculateOutput(0.4, 1600, Materials.Lead.getMolten(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Mi")))) {
                        outputFluids.add(calculateOutput(1.0, 900, Materials.HydricSulfide.getGas(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Ob")))) {
                        outputFluids.add(calculateOutput(1.0, 2000, Materials.CarbonMonoxide.getGas(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Ti")))) {
                        outputFluids.add(calculateOutput(0.5, 800, Materials.Ethane.getGas(1L)));
                        outputFluids.add(calculateOutput(0.5, 200, Materials.Methane.getGas(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Ra")))) {
                        outputFluids.add(calculateOutput(0.4, 1250, Materials.SaltWater.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.6, 1250, Materials.Helium_3.getGas(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Pr")))) {
                        outputFluids.add(calculateOutput(1.0, 700, Materials.Deuterium.getGas(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Tr")))) {
                        outputFluids.add(calculateOutput(0.5, 800, Materials.Ethylene.getGas(1L)));
                        outputFluids.add(calculateOutput(0.5, 800, Materials.Nitrogen.getGas(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("MM")))) {
                        outputFluids.add(calculateOutput(1.0, 300, Materials.HydrofluoricAcid.getFluid(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("Pl")))) {
                        outputFluids.add(calculateOutput(0.4, 800, Materials.Nitrogen.getGas(1L)));
                        outputFluids.add(calculateOutput(0.4, 300, Materials.Fluorine.getGas(1L)));
                        outputFluids.add(calculateOutput(0.4, 800, Materials.Oxygen.getGas(1L)));
                        outputFluids.add(calculateOutput(0.4, 800, Materials.LiquidAir.getFluid(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("BC")))) {
                        outputFluids.add(calculateOutput(0.5, 800, Materials.OilExtraHeavy.getFluid(1L)));
                        outputFluids.add(calculateOutput(0.5, 300, Materials.Unknown.getFluid(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("BE")))) {
                        outputFluids.add(calculateOutput(1.0, 400, Materials.LiquidAir.getFluid(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("BF")))) {
                        outputFluids.add(calculateOutput(1.0, 400, Materials.Tin.getMolten(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("CB")))) { // CentauriA is α Centauri Bb
                        outputFluids.add(calculateOutput(1.0, 300, Materials.Copper.getMolten(1L)));
                    }

                    if (item.isItemEqual(new ItemStack(ModBlocks.getBlock("TE")))) {
                        outputFluids.add(calculateOutput(0.4, 200, Materials.Copper.getMolten(1L)));
                        outputFluids.add(calculateOutput(0.4, 10000, Materials.OilExtraHeavy.getFluid(1L)));
                        outputFluids
                            .add(calculateOutput(0.4, 700, FluidRegistry.getFluidStack("ic2distilledwater", 1)));
                    }

                }
            }

            if (!outputFluids.isEmpty()) {
                mOutputFluids = outputFluids.toArray(new FluidStack[0]);
            }

            if (mOutputFluids != null) {
                this.mMaxProgresstime = (5750000 / excessFuel) - 475;
                this.lEUt = -needEu;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public FluidStack calculateOutput(double probability, long oilFieldReserve, FluidStack fluidStack) {

        FluidStack fluidStacks = null;

        if (Math.random() < probability) {
            Fluid fluidType = fluidStack.getFluid();
            long baseOutput = (long) ((1000 * oilFieldReserve * (oilFieldReserve + 1))
                * (0.5 + 0.25 * (GTValues.V[energyHatchTier - 7]))
                * (excessFuel / 1000.0)
                * drillTier);

            needEu += (int) (baseOutput / 10000);

            if (baseOutput > Integer.MAX_VALUE) {
                long totalAmount = baseOutput;

                while (totalAmount > 0) {
                    int stackSize = (int) Math.min(totalAmount, Integer.MAX_VALUE);
                    fluidStacks = new FluidStack(fluidType, stackSize);
                    totalAmount -= stackSize;
                }
            } else {
                fluidStacks = new FluidStack(fluidType, (int) baseOutput);
            }
        }

        return fluidStacks;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe);
            }
        };
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("excessFuel", excessFuel);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("excessFuel")) {
            excessFuel = aNBT.getInteger("excessFuel");
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        startRecipeProcessing();

        if (excessFuel > 2000 && mProgresstime != 0 && mProgresstime % 20 == 0) {
            excessFuel += (int) Math.floor(excessFuel / 2000.0);
        }

        if (excessFuel > 10000) {
            mInventory[getControllerSlotIndex()] = null;
            this.stopMachine(ShutDownReasonRegistry.POWER_LOSS);
        }

        if (mProgresstime != 0) {
            if (this.mProgresstime % 5 == 0 && excessFuel >= 2000) {
                ArrayList<FluidStack> storedFluids = getStoredFluids();
                for (FluidStack tFluid : storedFluids) {
                    if (tFluid != null) {
                        if (tFluid.getFluid()
                            .getName()
                            .equals("pyrotheum")) {
                            int consumption = (int) Math.pow(excessFuel, 1.3);
                            if (tFluid.amount >= consumption) {
                                tFluid.amount -= consumption;
                                excessFuel += 1;
                            }
                        }

                        if (tFluid.getFluid()
                            .getName()
                            .equals("ic2distilledwater")) {
                            if (tFluid.amount >= 200000) {
                                tFluid.amount -= 200000;
                                excessFuel -= 1;
                            }
                        }

                        if (tFluid.getFluid()
                            .getName()
                            .equals("liquidoxygen")) {
                            if (tFluid.amount >= 200000) {
                                tFluid.amount -= 200000;
                                excessFuel -= 2;
                            }
                        }

                        if (tFluid.getFluid()
                            .getName()
                            .equals("liquid helium")) {
                            if (tFluid.amount >= 200000) {
                                tFluid.amount -= 200000;
                                excessFuel -= 4;
                            }
                        }
                    }
                }
            }
        } else if (aTick % 20 == 0 && !aBaseMetaTileEntity.isActive()) {
            excessFuel -= 4;
            if (excessFuel < 300) {
                excessFuel = 300;
            }
        }

        endRecipeProcessing();
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        drillTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()
            && !mOutputHatches.isEmpty()) {
            return false;
        }

        energyHatchTier = checkEnergyHatchTier();
        drillTier = checkDrillTier();

        return tCountCasing >= 570;
    }

    public int checkDrillTier() {
        ItemStack controllerSlot = getControllerSlot();
        if (controllerSlot != null) {
            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.Neutronium, 1L))) {
                return 1;
            }

            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.CosmicNeutronium, 1L))) {
                return 2;
            }

            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.Infinity, 1L))) {
                return 3;
            }

            if (controllerSlot.isItemEqual(
                GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, MaterialsUEVplus.TranscendentMetal, 1L))) {
                return 4;
            }
        }
        return 0;
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
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new AdvancedInfiniteDriller(this.mName);
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 1;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("excessFuel", excessFuel);

    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("excessFuel")) {
            currentTip.add(
                StatCollector.translateToLocal("Info_AdvancedInfiniteDriller_00") + EnumChatFormatting.YELLOW
                    + tag.getInteger("excessFuel")
                    + "K");
        }
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(4, 4)
                .setSize(190, 85));

        slotWidgets.clear();
        createInventorySlots();

        Column slotsColumn = new Column();
        for (int i = slotWidgets.size() - 1; i >= 0; i--) {
            slotsColumn.widget(slotWidgets.get(i));
        }
        builder.widget(
            slotsColumn.setAlignment(MainAxisAlignment.END)
                .setPos(173, 167 - 1));

        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        drawTexts(screenElements, !slotWidgets.isEmpty() ? slotWidgets.get(0) : null);
        screenElements
            .widget(
                new TextWidget().setStringSupplier(
                    () -> StatCollector.translateToLocalFormatted("Info_AdvancedInfiniteDriller_00") + excessFuel + "K")
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setEnabled(true))
            .widget(
                new FakeSyncWidget.IntegerSyncer(() -> excessFuel, Fuel -> excessFuel = Fuel).setSynced(true, false));
        builder.widget(
            new Scrollable().setVerticalScroll()
                .widget(screenElements.setPos(10, 0))
                .setPos(0, 7)
                .setSize(190, 79));

        builder.widget(createPowerSwitchButton(builder))
            .widget(createVoidExcessButton(builder))
            .widget(createInputSeparationButton(builder))
            .widget(createBatchModeButton(builder))
            .widget(createLockToSingleRecipeButton(builder))
            .widget(createStructureUpdateButton(builder));

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public VoidingMode getVoidingMode() {
        return VoidingMode.VOID_NONE;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
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
}
