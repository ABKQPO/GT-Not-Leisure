package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.machine.VMTweakHelper;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.loader.Loaders;
import gregtech.GTMod;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.VoidingMode;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GTUODimension;
import gregtech.api.objects.GTUOFluid;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.multi.compressor.MTEHeatSensor;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class AdvancedInfiniteDriller extends MultiMachineBase<AdvancedInfiniteDriller>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String AID_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/advanced_infinite_driller";
    private static final String[][] shape = StructureUtils.readStructureFromFile(AID_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 12;
    private static final int VERTICAL_OFF_SET = 39;
    private static final int DEPTH_OFF_SET = 0;

    public double excessFuel = 0;
    public int drillTier = 0;
    public ArrayList<MTEHeatSensor> sensorHatches = new ArrayList<>();

    public AdvancedInfiniteDriller(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public AdvancedInfiniteDriller(String aName) {
        super(aName);
    }

    @Override
    public boolean getPerfectOC() {
        return false;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
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
            .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedInfiniteDriller_12"))
            .addTecTechHatchInfo()
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
        return StructureDefinition.<AdvancedInfiniteDriller>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(Loaders.MAR_Casing, 0))
            .addElement('B', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement('C', StructureUtility.ofBlock(BlockLoader.metaCasing, 16))
            .addElement('D', StructureUtility.ofBlock(BlockLoader.metaCasing, 18))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sSolenoidCoilCasings, 5))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 11))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement(
                'J',
                GTStructureUtility.buildHatchAdder(AdvancedInfiniteDriller.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        SpecialHatchElement.HeatSensor)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))))
            .addElement('K', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockMetal8, 0))
            .build();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        drillTier = checkDrillTier();
        if (drillTier == 0) {
            excessFuel = Math.max(300, excessFuel - 4);
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (excessFuel < 300) {
            excessFuel = 300;
        }

        if (excessFuel > 10000) {
            excessFuel = 10000;
        }

        ArrayList<FluidStack> storedFluids = getStoredFluids();
        if (storedFluids.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (excessFuel < 2000) {
            int consumptionCount = 0;

            for (FluidStack tFluid : storedFluids) {
                if (GTUtility.areFluidsEqual(tFluid, new FluidStack(GTPPFluids.Pyrotheum, 1))) {
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
            int needEu = 0;
            List<FluidStack> outputFluids = new ArrayList<>();
            for (ItemStack item : getAllStoredInputs()) {
                if (item.getItem() instanceof ItemDimensionDisplay) {
                    int dimID = VMTweakHelper.dimMapping.inverse()
                        .getOrDefault(ItemDimensionDisplay.getDimension(item), 0);
                    GTUODimension dimension = GTMod.proxy.mUndergroundOil.GetDimension(dimID);
                    if (dimension == null) continue;

                    XSTR tVeinRNG = new XSTR(System.nanoTime());
                    int count = 0;
                    int attempts = 0;

                    while (count < 5 && attempts < 100) {
                        attempts++;
                        GTUOFluid uoFluid = dimension.getRandomFluid(tVeinRNG);

                        if (uoFluid == null || uoFluid.getFluid() == null) {
                            continue;
                        }

                        int amount = (int) (2_000_000 + tVeinRNG.nextInt(100) * 2000 * excessFuel * drillTier);
                        outputFluids.add(new FluidStack(uoFluid.getFluid(), amount));

                        needEu += amount / 200;
                        count++;
                    }
                }
            }
            mOutputFluids = outputFluids.toArray(new FluidStack[0]);
            this.mMaxProgresstime = (int) ((((double) 5750000 / excessFuel) - 475) * mConfigSpeedBoost);
            this.lEUt = -needEu;
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setDouble("excessFuel", excessFuel);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("excessFuel")) {
            excessFuel = aNBT.getDouble("excessFuel");
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        startRecipeProcessing();

        if (excessFuel > 2000 && mProgresstime > 0 && mProgresstime % 20 == 0) {
            excessFuel += (int) Math.floor(drillTier * excessFuel / 2000.0);
        }

        if (excessFuel > 10000) {
            mInventory[getControllerSlotIndex()] = null;
            this.stopMachine(ShutDownReasonRegistry.POWER_LOSS);
        }

        if (mProgresstime > 0) {
            if (mProgresstime % 5 == 0 && excessFuel >= 2000) {
                for (FluidStack tFluid : getStoredFluids()) {
                    if (tFluid == null || tFluid.getFluid() == null) continue;
                    int amount = tFluid.amount;
                    if (GTUtility.areFluidsEqual(tFluid, new FluidStack(GTPPFluids.Pyrotheum, 1))) {
                        int consumption = (int) Math.pow(excessFuel, 1.3);
                        if (amount >= consumption) {
                            tFluid.amount -= consumption;
                            excessFuel += 1;
                        }
                    } else if (GTUtility.areFluidsEqual(tFluid, GTModHandler.getDistilledWater(1))) {
                        int multiplier = amount / 200_000;
                        if (multiplier > 0) {
                            tFluid.amount -= multiplier * 200_000;
                            excessFuel -= multiplier;
                        }
                    } else if (GTUtility.areFluidsEqual(tFluid, Materials.LiquidOxygen.getFluid(1))) {
                        int multiplier = amount / 200_000;
                        if (multiplier > 0) {
                            tFluid.amount -= multiplier * 200_000;
                            excessFuel -= multiplier * 2;
                        }
                    } else if (GTUtility.areFluidsEqual(tFluid, WerkstoffLoader.LiquidHelium.getFluidOrGas(1))) {
                        int multiplier = amount / 200_000;
                        if (multiplier > 0) {
                            tFluid.amount -= multiplier * 200_000;
                            excessFuel -= multiplier * 4;
                        }
                    } else if (GTUtility.areFluidsEqual(tFluid, new FluidStack(GTPPFluids.Cryotheum, 1))) {
                        int multiplier = amount / 200_000;
                        if (multiplier > 0) {
                            tFluid.amount -= multiplier * 200_000;
                            excessFuel -= multiplier * 40;
                        }
                    }
                }
            }
        } else if (aTick % 20 == 0 && !aBaseMetaTileEntity.isActive()) {
            excessFuel -= 4;
        }

        if (excessFuel < 300) {
            excessFuel = 300;
        }

        double percent = excessFuel / 100.0f;
        if (percent > 100.0d) {
            percent = 100.0d;
        }

        for (MTEHeatSensor hatch : sensorHatches) {
            hatch.updateRedstoneOutput((float) percent);
        }

        endRecipeProcessing();
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()
            || mOutputHatches.isEmpty()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 500;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
    }

    public int checkDrillTier() {
        ItemStack controllerSlot = getControllerSlot();
        if (controllerSlot != null) {
            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.CosmicNeutronium, 1L))) {
                return 1;
            }

            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.Infinity, 1L))) {
                return 2;
            }

            if (controllerSlot.isItemEqual(
                GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, MaterialsUEVplus.TranscendentMetal, 1L))) {
                return 3;
            }

            if (controllerSlot
                .isItemEqual(GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, MaterialsUEVplus.SpaceTime, 1L))) {
                return 4;
            }
        }
        return 0;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new AdvancedInfiniteDriller(this.mName);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setDouble("excessFuel", excessFuel);

    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("excessFuel")) {
            currentTip.add(
                StatCollector
                    .translateToLocalFormatted("Info_AdvancedInfiniteDriller_00", tag.getDouble("excessFuel")));
        }
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        screenElements
            .widget(
                new TextWidget()
                    .setStringSupplier(
                        () -> StatCollector.translateToLocalFormatted("Info_AdvancedInfiniteDriller_00", excessFuel))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setEnabled(true))
            .widget(
                new FakeSyncWidget.DoubleSyncer(() -> excessFuel, fuel -> excessFuel = fuel).setSynced(true, false));
    }

    @Override
    public String[] getInfoData() {
        List<String> ret = new ArrayList<>(Arrays.asList(super.getInfoData()));
        ret.add(StatCollector.translateToLocalFormatted("Info_AdvancedInfiniteDriller_00", excessFuel));
        return ret.toArray(new String[0]);
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

    public boolean addSensorHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity instanceof MTEHeatSensor sensor) {
            sensor.updateTexture(aBaseCasingIndex);
            return this.sensorHatches.add(sensor);
        }
        return false;
    }

    public enum SpecialHatchElement implements IHatchElement<AdvancedInfiniteDriller> {

        HeatSensor(AdvancedInfiniteDriller::addSensorHatchToMachineList, MTEHeatSensor.class) {

            @Override
            public long count(AdvancedInfiniteDriller machine) {
                return machine.sensorHatches.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<AdvancedInfiniteDriller> adder;

        @SafeVarargs
        SpecialHatchElement(IGTHatchAdder<AdvancedInfiniteDriller> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super AdvancedInfiniteDriller> adder() {
            return adder;
        }
    }
}
