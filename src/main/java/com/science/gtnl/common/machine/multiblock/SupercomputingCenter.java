package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings10;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.GregTechAPI.sBlockCasings9;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static gtPlusPlus.core.block.ModBlocks.blockCasings5Misc;
import static gtPlusPlus.core.block.ModBlocks.blockCasingsMisc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.util.Vec3Impl;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.enums.BlockIcons;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.common.WirelessComputationPacket;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialsAlloy;
import tectech.mechanics.dataTransport.QuantumDataPacket;
import tectech.thing.casing.BlockGTCasingsTT;
import tectech.thing.metaTileEntity.hatch.MTEHatchDataInput;
import tectech.thing.metaTileEntity.hatch.MTEHatchDataOutput;
import tectech.thing.metaTileEntity.hatch.MTEHatchRack;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessComputationOutput;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;
import tectech.util.CommonValues;

public class SupercomputingCenter extends TTMultiblockBase implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/supercomputing_center";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SC_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 22;
    private static final int VERTICAL_OFF_SET = 57;
    private static final int DEPTH_OFF_SET = 1;

    public static FluidStack cryotheum = new FluidStack(GTPPFluids.Cryotheum, 1);

    public static INameFunction<SupercomputingCenter> OC_NAME = (base, p) -> StatCollector
        .translateToLocal("gt.blockmachines.multimachine.em.computer.cfgi.0"); // Overclock ratio
    public static INameFunction<SupercomputingCenter> OV_NAME = (base, p) -> StatCollector
        .translateToLocal("gt.blockmachines.multimachine.em.computer.cfgi.1"); // Overvoltage ratio
    public static INameFunction<SupercomputingCenter> MAX_TEMP_NAME = (base, p) -> StatCollector
        .translateToLocal("gt.blockmachines.multimachine.em.computer.cfgo.0"); // Current max. heat
    public static INameFunction<SupercomputingCenter> COMPUTE_NAME = (base, p) -> StatCollector
        .translateToLocal("gt.blockmachines.multimachine.em.computer.cfgo.1"); // Produced computation
    public static IStatusFunction<SupercomputingCenter> OC_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 1, 3, 5);
    public static IStatusFunction<SupercomputingCenter> OV_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 1, 3, 5);
    public static IStatusFunction<SupercomputingCenter> MAX_TEMP_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 2000, 8000, 10000);
    public static IStatusFunction<SupercomputingCenter> COMPUTE_STATUS = (base, p) -> {
        if (base.eAvailableData < 0) {
            return LedStatus.STATUS_TOO_LOW;
        }
        if (base.eAvailableData == 0) {
            return LedStatus.STATUS_NEUTRAL;
        }
        return LedStatus.STATUS_OK;
    };

    public ArrayList<MTEHatchRack> mRackHatchs = new ArrayList<>();
    public ArrayList<MTEHatchWirelessComputationOutput> mWirelessComputationOutputHatchs = new ArrayList<>();
    public Parameters.Group.ParameterIn overclock, overvolt;
    public Parameters.Group.ParameterOut maxCurrentTemp, availableData;

    public boolean wirelessMode = false;
    public boolean energyWirelessMode = false;
    public UUID ownerUUID;

    public SupercomputingCenter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eCertainMode = 5;
        eCertainStatus = -128;
        useLongPower = true;
    }

    public SupercomputingCenter(String aName) {
        super(aName);
        eCertainMode = 5;
        eCertainStatus = -128;
        useLongPower = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SupercomputingCenter(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.quantumComputerFakeRecipes;
    }

    @Override
    public void parametersInstantiation_EM() {
        Parameters.Group hatch_0 = parametrization.getGroup(0);
        overclock = hatch_0.makeInParameter(0, 1, OC_NAME, OC_STATUS);
        overvolt = hatch_0.makeInParameter(1, 1, OV_NAME, OV_STATUS);
        maxCurrentTemp = hatch_0.makeOutParameter(0, 0, MAX_TEMP_NAME, MAX_TEMP_STATUS);
        availableData = hatch_0.makeOutParameter(1, 0, COMPUTE_NAME, COMPUTE_STATUS);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        energyWirelessMode = false;
        for (MTEHatchRack rack : GTUtility.validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
        mRackHatchs.clear();

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) {
            return false;
        }

        for (MTEHatchRack rack : GTUtility.validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(iGregTechTileEntity.isActive());
        }

        if (mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty()) energyWirelessMode = true;
        return eUncertainHatches.size() == 1;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setDouble("computation", availableData.get());
        aNBT.setBoolean("wirelessMode", wirelessMode);
        aNBT.setBoolean("energyWirelessMode", energyWirelessMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        energyWirelessMode = aNBT.getBoolean("energyWirelessMode");
        if (availableData != null) {
            availableData.set(aNBT.getDouble("computation"));
            eAvailableData = (long) availableData.get();
        }
        if (aNBT.hasKey("wirelessMode")) {
            wirelessMode = aNBT.getBoolean("wirelessMode");
            if (wirelessMode) {
                WirelessComputationPacket.enableWirelessNetWork(getBaseMetaTileEntity());
            }
        } else {
            wirelessMode = false;
        }
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick_EM(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && mMachine
            && !aBaseMetaTileEntity.isActive()
            && aTick % 20 == CommonValues.MULTI_CHECK_AT) {
            double maxTemp = 0;
            for (MTEHatchRack rack : GTUtility.validMTEList(mRackHatchs)) {
                if (rack.heat > maxTemp) {
                    maxTemp = rack.heat;
                }
            }
            maxCurrentTemp.set(maxTemp);
        }
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing_EM() {
        parametrization.setToDefaults(false, true);
        eAvailableData = 0;
        double maxTemp = 0;
        double overClockRatio = overclock.get();
        double overVoltageRatio = overvolt.get();
        if (Double.isNaN(overClockRatio) || Double.isNaN(overVoltageRatio)) {
            return SimpleCheckRecipeResult.ofFailure("no_computing");
        }
        if (overclock.getStatus(true).isOk && overvolt.getStatus(true).isOk) {
            boolean useCryotheum = false;
            for (FluidStack fluidStack : getStoredFluids()) {
                if (GTUtility.areFluidsEqual(fluidStack, cryotheum)) {
                    useCryotheum = true;
                    for (MTEHatchRack rack : GTUtility.validMTEList(mRackHatchs)) {
                        if (fluidStack.amount <= 0) break;
                        if (rack.heat < 1) continue;
                        int consume = Math.min(fluidStack.amount, Math.max(0, rack.heat / 20));
                        if (consume > 0) {
                            fluidStack.amount -= consume;
                            rack.heat -= consume * 20;
                        }
                    }
                }
            }

            lEUt = (long) Math.max(GTValues.V[7], GTValues.V[7] * overClockRatio * overVoltageRatio);
            int thingsActive = 0;
            int rackComputation;

            for (MTEHatchRack rack : GTUtility.validMTEList(mRackHatchs)) {
                if (rack.heat > maxTemp) {
                    maxTemp = rack.heat;
                }
                rackComputation = rack.tickComponents((float) overClockRatio, (float) overVoltageRatio);
                if (rackComputation > 0) {
                    rack.heat += rackComputation / 256;
                    if (useCryotheum) rackComputation *= 16;
                    eAvailableData += rackComputation * 16L;
                    thingsActive += 4;
                }
                rack.getBaseMetaTileEntity()
                    .setActive(true);
            }

            for (MTEHatchDataInput di : eInputData) {
                if (di.q != null) // ok for power losses
                {
                    thingsActive++;
                }
            }

            if (energyWirelessMode) {
                BigInteger costEU = BigInteger.valueOf(lEUt);
                if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(Utils.NEGATIVE_ONE))) {
                    return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
                }
                lEUt = 0;
            }

            if (thingsActive > 0 && eCertainStatus == 0) {
                thingsActive += eOutputData.size();
                eAmpereFlow = 1 + (thingsActive >> 2);
                mMaxProgresstime = 20;
                mEfficiencyIncrease = 10000;
                maxCurrentTemp.set(maxTemp);
                availableData.set(eAvailableData);
                return SimpleCheckRecipeResult.ofSuccess("computing");
            } else {
                eAvailableData = 0;
                eAmpereFlow = 1;
                mMaxProgresstime = 20;
                mEfficiencyIncrease = 10000;
                maxCurrentTemp.set(maxTemp);
                availableData.set(eAvailableData);
                return SimpleCheckRecipeResult.ofSuccess("no_computing");
            }
        }
        return SimpleCheckRecipeResult.ofFailure("no_computing");
    }

    @Override
    public void outputAfterRecipe_EM() {
        if (!eOutputData.isEmpty()) {
            Vec3Impl pos = new Vec3Impl(
                getBaseMetaTileEntity().getXCoord(),
                getBaseMetaTileEntity().getYCoord(),
                getBaseMetaTileEntity().getZCoord());

            int eHatchData = 0;

            for (MTEHatchDataInput hatch : eInputData) {
                if (hatch.q == null || hatch.q.contains(pos)) {
                    continue;
                }
                eHatchData += hatch.q.getContent();
            }

            QuantumDataPacket pack = new QuantumDataPacket((eAvailableData + eHatchData) / eOutputData.size())
                .unifyTraceWith(pos);
            if (pack == null) {
                return;
            }

            for (MTEHatchDataOutput o : eOutputData) {
                o.providePacket(pack);
            }
        }
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SupercomputingCenterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_06"))
            .addTecTechHatchInfo()
            .beginStructureBlock(28, 59, 21, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_SupercomputingCenter_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            wirelessMode = !wirelessMode;
            if (wirelessMode) {
                GTUtility.sendChatToPlayer(aPlayer, "Wireless mode enabled");
                WirelessComputationPacket.enableWirelessNetWork(getBaseMetaTileEntity());
            } else {
                GTUtility.sendChatToPlayer(aPlayer, "Wireless mode disabled");
                WirelessComputationPacket.disableWirelessNetWork(getBaseMetaTileEntity());
            }
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(StructureUtils.getTextureIndex(sBlockCasings9, 7)),
                new TTRenderedExtendedFacingTexture(
                    aActive ? BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE
                        : BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK) };
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(StructureUtils.getTextureIndex(sBlockCasings9, 7)) };
    }

    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_HIGH_FREQ;
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        for (MTEHatchRack rack : GTUtility.validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
    }

    @Override
    public void extraExplosions_EM() {
        for (MetaTileEntity tTileEntity : mRackHatchs) {
            tTileEntity.getBaseMetaTileEntity()
                .doExplosion(GTValues.V[8]);
        }
    }

    @Override
    public long getAvailableData_EM() {
        return eAvailableData;
    }

    @Override
    public void stopMachine(@NotNull ShutDownReason reason) {
        super.stopMachine(reason);
        eAvailableData = 0;
        for (MTEHatchRack rack : GTUtility.validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
    }

    @Override
    public void afterRecipeCheckFailed() {
        super.afterRecipeCheckFailed();
        for (MTEHatchRack rack : GTUtility.validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
    }

    public boolean addRackToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchRack rack) {
            rack.updateTexture(aBaseCasingIndex);
            return mRackHatchs.add(rack);
        }
        return false;
    }

    public boolean addWirelessDataOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchWirelessComputationOutput output) {
            output.updateTexture(aBaseCasingIndex);
            // Add to wireless computation outputs, so we can detect these and turn on wireless mode,
            // but also add to regular outputs, so they are used as output data hatches by the quantum computer
            return mWirelessComputationOutputHatchs.add(output) && eOutputData.add(output);
        }
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

    @Override
    public IStructureDefinition<SupercomputingCenter> getStructure_EM() {
        return StructureDefinition.<SupercomputingCenter>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings10, 3))
            .addElement('B', ofBlock(sBlockCasings8, 10))
            .addElement(
                'C',
                buildHatchAdder(SupercomputingCenter.class).atLeast(CustomHatchElement.RackHatch)
                    .casingIndex(BlockGTCasingsTT.textureOffset + 1)
                    .shouldReject(t -> !t.mRackHatchs.isEmpty())
                    .dot(1)
                    .buildAndChain(sBlockCasingsTT, 1))
            .addElement('D', ofBlock(sBlockCasingsTT, 3))
            .addElement('E', ofBlock(sBlockCasingsTT, 1))
            .addElement('F', ofBlock(sBlockCasingsTT, 2))
            .addElement(
                'G',
                ofChain(
                    buildHatchAdder(SupercomputingCenter.class)
                        .atLeast(
                            InputHatch,
                            OutputHatch,
                            Maintenance,
                            Energy.or(ExoticEnergy),
                            HatchElement.Uncertainty,
                            HatchElement.InputData,
                            HatchElement.OutputData)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings9, 7))
                        .dot(1)
                        .buildAndChain(sBlockCasings9, 7),
                    buildHatchAdder(SupercomputingCenter.class)
                        .adder(SupercomputingCenter::addWirelessDataOutputToMachineList)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings9, 7))
                        .dot(1)
                        .buildAndChain(sBlockCasings9, 7)))
            .addElement('H', ofBlock(sBlockCasings8, 7))
            .addElement('I', ofBlock(sBlockCasingsTT, 0))
            .addElement('J', ofBlock(sBlockCasings8, 5))
            .addElement('K', ofBlock(BlockLoader.metaCasing, 7))
            .addElement('L', ofBlock(blockCasingsMisc, 5))
            .addElement('M', ofBlock(sBlockCasings9, 15))
            .addElement('N', ofBlock(sBlockCasings2, 7))
            .addElement('O', ofBlock(sBlockCasings10, 8))
            .addElement('P', ofBlock(blockCasings5Misc, 15))
            .addElement(
                'Q',
                ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_N.getFrameBox(1)
                            .getItem())))
            .addElement('R', ofFrame(Materials.PulsatingIron))
            .addElement('S', ofBlock(sBlockCasings1, 9))
            .build();
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (wirelessMode) {
            WirelessComputationPacket wirelessComputationPacket = WirelessComputationPacket
                .getPacketByUserId(getBaseMetaTileEntity().getOwnerUuid());
            data.add(StatCollector.translateToLocal("tt.infodata.qc.wireless_mode.enabled"));
            data.add(
                StatCollector.translateToLocalFormatted(
                    "tt.infodata.qc.total_wireless_computation",
                    "" + EnumChatFormatting.YELLOW + wirelessComputationPacket.getAvailableComputationStored()));
        } else {
            data.add(StatCollector.translateToLocal("tt.infodata.qc.wireless_mode.disabled"));
        }
        return data.toArray(new String[] {});
    }

    public enum CustomHatchElement implements IHatchElement<SupercomputingCenter> {

        RackHatch(SupercomputingCenter::addRackToMachineList, MTEHatchRack.class) {

            @Override
            public long count(SupercomputingCenter tileEntity) {
                return tileEntity.mRackHatchs.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<SupercomputingCenter> adder;

        @SafeVarargs
        CustomHatchElement(IGTHatchAdder<SupercomputingCenter> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public IGTHatchAdder<? super SupercomputingCenter> adder() {
            return adder;
        }
    }
}
