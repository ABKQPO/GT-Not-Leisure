package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.network;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.util.Vec3Impl;
import com.science.gtnl.common.packet.SyncHPCAVariablesPacket;
import com.science.gtnl.common.render.tile.HighPerformanceComputationArrayRenderer;
import com.science.gtnl.utils.enums.HPCAModifier;

import cpw.mods.fml.common.network.NetworkRegistry;
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
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.common.WirelessComputationPacket;
import gregtech.common.render.IMTERenderer;
import lombok.Getter;
import tectech.mechanics.dataTransport.QuantumDataPacket;
import tectech.thing.casing.BlockGTCasingsTT;
import tectech.thing.casing.TTCasingsContainer;
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

public class HighPerformanceComputationArray extends TTMultiblockBase implements ISurvivalConstructable, IMTERenderer {

    public UUID randomUUID = UUID.randomUUID();
    public int[][] randomColor;

    public int totalLens = 0;

    public Table<Integer, Integer, MTEHatchRack> rackTable = HashBasedTable.create();
    public ArrayList<MTEHatchRack> mRackHatchs = new ArrayList<>();
    public ArrayList<MTEHatchWirelessComputationOutput> mWirelessComputationOutputHatchs = new ArrayList<>();

    public Parameters.Group.ParameterOut maxCurrentTemp, availableData, machineLens, coolantUse;

    public boolean wirelessMode = false;

    public static final INameFunction<HighPerformanceComputationArray> LENS_NAME = (base, p) -> StatCollector
        .translateToLocal("HPCA_Info_00");
    public static final INameFunction<HighPerformanceComputationArray> MAX_TEMP_NAME = (base, p) -> StatCollector
        .translateToLocal("HPCA_Info_01");
    public static final INameFunction<HighPerformanceComputationArray> COMPUTE_NAME = (base, p) -> StatCollector
        .translateToLocal("HPCA_Info_02");
    public static final INameFunction<HighPerformanceComputationArray> COOLANT_NAME = (base, p) -> StatCollector
        .translateToLocal("HPCA_Info_03");
    public static final IStatusFunction<HighPerformanceComputationArray> LENS_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 4, 8, 16);
    public static final IStatusFunction<HighPerformanceComputationArray> MAX_TEMP_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 2000, 8000, 10000);
    public static final IStatusFunction<HighPerformanceComputationArray> COMPUTE_STATUS = (base, p) -> {
        if (base.eAvailableData < 0) {
            return LedStatus.STATUS_TOO_LOW;
        }
        if (base.eAvailableData == 0) {
            return LedStatus.STATUS_NEUTRAL;
        }
        return LedStatus.STATUS_OK;
    };
    public static final IStatusFunction<HighPerformanceComputationArray> COOLANT_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 20000, 80000, 100000);

    public static FluidStack superCoolant = Materials.SuperCoolant.getFluid(1);

    public HighPerformanceComputationArray(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eCertainMode = 5;
        eCertainStatus = -128; // no-brain value
    }

    public HighPerformanceComputationArray(String aName) {
        super(aName);
        eCertainMode = 5;
        eCertainStatus = -128; // no-brain value
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.quantumComputerFakeRecipes;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HighPerformanceComputationArray(mName);
    }

    @Override
    public void parametersInstantiation_EM() {
        Parameters.Group hatch_0 = parametrization.getGroup(0);
        Parameters.Group hatch_1 = parametrization.getGroup(1);
        machineLens = hatch_0.makeOutParameter(0, 0, LENS_NAME, LENS_STATUS);
        maxCurrentTemp = hatch_0.makeOutParameter(1, 0, MAX_TEMP_NAME, MAX_TEMP_STATUS);
        availableData = hatch_1.makeOutParameter(0, 0, COMPUTE_NAME, COMPUTE_STATUS);
        coolantUse = hatch_1.makeOutParameter(1, 0, COOLANT_NAME, COOLANT_STATUS);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        this.totalLens = 0;
        for (MTEHatchRack rack : validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
        mRackHatchs.clear();
        rackTable.clear();

        if (!structureCheck_EM("front", 1, 2, 0)) return false;
        if (!structureCheck_EM("cap", 1, 2, -1)) return false;

        byte offset = -2;
        while (offset > -18) {
            if (!structureCheck_EM("slice", 1, 2, offset)) break;
            offset--;
            this.totalLens++;
        }

        if (!structureCheck_EM("cap", 1, 2, ++offset)) return false;
        if (!structureCheck_EM("back", 1, 2, --offset)) return false;

        totalLens--;
        eCertainMode = (byte) Math.min(this.totalLens / 3, 5);
        for (MTEHatchRack rack : validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(iGregTechTileEntity.isActive());
        }
        machineLens.set(this.totalLens);
        randomColor = generateTwoModifierIndexGroups(randomUUID, this.totalLens);
        return eUncertainHatches.size() == 1 && this.totalLens >= 3;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("totalLen", totalLens);
        aNBT.setString("randomUUID", String.valueOf(randomUUID));
        aNBT.setDouble("computation", availableData.get());
        aNBT.setBoolean("wirelessModeEnabled", wirelessMode);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        totalLens = aNBT.getInteger("totalLen");
        randomUUID = UUID.fromString(aNBT.getString("randomUUID"));
        if (availableData != null) {
            availableData.set(aNBT.getDouble("computation"));
            this.eAvailableData = (long) availableData.get();
        }
        if (aNBT.hasKey("wirelessModeEnabled")) {
            wirelessMode = aNBT.getBoolean("wirelessModeEnabled");
            if (wirelessMode) {
                WirelessComputationPacket.enableWirelessNetWork(getBaseMetaTileEntity());
            }
        } else {
            wirelessMode = false;
        }
        super.loadNBTData(aNBT);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        setRenderRotation(getDirection());
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (!aBaseMetaTileEntity.isServerSide()) return;
        if (aTick % 20 == 0) {
            IGregTechTileEntity hpca = this.getBaseMetaTileEntity();
            network.sendToAllAround(
                new SyncHPCAVariablesPacket(
                    this.randomUUID,
                    this.totalLens,
                    hpca.getXCoord(),
                    hpca.getYCoord(),
                    hpca.getZCoord(),
                    this.mMachine),
                new NetworkRegistry.TargetPoint(
                    hpca.getWorld().provider.dimensionId,
                    hpca.getXCoord() + 0.5,
                    hpca.getYCoord() + 0.5,
                    hpca.getZCoord() + 0.5,
                    512.0));
        }

        if (mUpdated) {
            if (mUpdate <= 0) mUpdate = 20;
            mUpdated = false;
        }
        if (aTick % 20 == CommonValues.MULTI_CHECK_AT) {
            int allCoolantUs = 0;
            this.eAvailableData = 0;
            double maxTemp = 0;
            for (int x = 0; x < this.totalLens; x++) {
                for (int y = 0; y < 3; y++) {
                    MTEHatchRack rack = rackTable.get(x, y);
                    if (rack != null) {
                        if (rack.heat > maxTemp) {
                            maxTemp = rack.heat;
                        }
                    }
                }
            }

            maxCurrentTemp.set(maxTemp);
            if (mMaxProgresstime > 0) {
                ArrayList<FluidStack> storedFluids = getStoredFluids();
                int totalSuperCoolant = 0;

                if (storedFluids != null) {
                    for (FluidStack fs : storedFluids) {
                        if (fs != null && GTUtility.areFluidsEqual(fs, superCoolant, true)) {
                            totalSuperCoolant += fs.amount;
                        }
                    }
                }

                for (int x = 0; x < this.totalLens; x++) {
                    for (int y = 0; y < 3; y++) {
                        MTEHatchRack rack = rackTable.get(x, y);
                        if (rack != null) {
                            int modX = (randomColor[0] != null && randomColor[0].length > x) ? randomColor[0][x] : 0;
                            int modY = (randomColor[1] != null && randomColor[1].length > y) ? randomColor[1][y] : 0;

                            HPCAModifier modXVal = HPCAModifier.values()[(modX) % HPCAModifier.values().length];
                            HPCAModifier modYVal = HPCAModifier.values()[(modY) % HPCAModifier.values().length];

                            double computationFactor = modXVal.computationCoefficientX
                                * modYVal.computationCoefficientY;
                            double coolantFactor = modXVal.coolantCoefficientX * modYVal.coolantCoefficientY;
                            double heatFactor = modXVal.heatCoefficientX * modYVal.heatCoefficientY;

                            int rackComputation = rack.tickComponents(1, 1) * 5;
                            if (rackComputation > 0) {
                                int coolantUse = (int) (rackComputation * coolantFactor / 20);

                                double coolantRatio = totalSuperCoolant > 0
                                    ? Math.min(1.0, (double) totalSuperCoolant / coolantUse)
                                    : 0.0;

                                rack.heat += coolantRatio > 0
                                    ? (int) (-rackComputation / 20d * heatFactor * coolantRatio)
                                    : (int) (150 * heatFactor);

                                rack.heat = Math.max(100, rack.heat);
                                this.eAvailableData += (long) (rackComputation * computationFactor) / 4;

                                int usedCoolant = (int) (coolantUse * coolantRatio);
                                allCoolantUs += usedCoolant;

                                totalSuperCoolant -= usedCoolant;
                            }
                        }
                    }
                }

                coolantUse.set(allCoolantUs);
                availableData.set(this.eAvailableData);
            }
        }

        if (!eOutputData.isEmpty() && mMaxProgresstime > 0 && (mProgresstime + 1) % 10 == 0) {
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

            QuantumDataPacket pack = new QuantumDataPacket((this.eAvailableData + eHatchData) / eOutputData.size())
                .unifyTraceWith(pos);
            if (pack == null) {
                return;
            }

            for (MTEHatchDataOutput o : eOutputData) {
                o.providePacket(pack);
            }
        }

    }

    public int[][] generateTwoModifierIndexGroups(UUID uuid, int totalLen) {
        int enumLen = HPCAModifier.values().length;
        int[][] result = new int[2][Math.max(3, totalLen)];

        Random randomA = new Random(uuid.getMostSignificantBits());
        Random randomB = new Random(uuid.getLeastSignificantBits());

        for (int i = 0; i < totalLen; i++) {
            result[0][i] = randomA.nextInt(enumLen);
        }
        for (int i = 0; i < Math.max(3, totalLen); i++) {
            result[1][i] = randomB.nextInt(enumLen);
        }

        return result;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing_EM() {
        parametrization.setToDefaults(false, true);
        double maxTemp = 0;
        mEUt = -(int) V[7];
        int thingsActive = 0;
        int rackComputation;

        for (MTEHatchRack rack : validMTEList(mRackHatchs)) {

            if (rack.heat > maxTemp) {
                maxTemp = rack.heat;
            }
            rackComputation = rack.tickComponents(1, 1);
            if (rackComputation > 0) {
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

        mMaxProgresstime = 40;
        mEfficiencyIncrease = 10000;
        maxCurrentTemp.set(maxTemp);
        if (thingsActive > 0 && eCertainStatus == 0) {
            thingsActive += eOutputData.size();
            eAmpereFlow = 1 + (thingsActive >> 2);
            return SimpleCheckRecipeResult.ofSuccess("computing");
        } else {
            eAmpereFlow = 1;
            availableData.set(0);
            return SimpleCheckRecipeResult.ofSuccess("no_computing");
        }
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("HighPerformanceComputationArrayRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginVariableStructureBlock(2, 2, 4, 4, 5, 16, false)
            .addOtherStructurePart(
                StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_Casing_00"),
                StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_Casing_01"),
                1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_Casing_02"),
                StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_Casing_01"),
                1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_Casing_03"),
                StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_Casing_04"),
                2)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_Casing_01"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_HighPerformanceComputationArray_Casing_01"), 1)
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
            return new ITexture[] { Textures.BlockIcons.casingTexturePages[BlockGTCasingsTT.texturePage][3],
                new TTRenderedExtendedFacingTexture(
                    aActive ? OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE : OVERLAY_FRONT_TECTECH_MULTIBLOCK) };
        }
        return new ITexture[] { Textures.BlockIcons.casingTexturePages[BlockGTCasingsTT.texturePage][3] };
    }

    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_HIGH_FREQ;
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        for (MTEHatchRack rack : validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
    }

    @Override
    public void onMachineBlockUpdate() {
        super.onMachineBlockUpdate();
    }

    @Override
    public void extraExplosions_EM() {
        for (MetaTileEntity tTileEntity : mRackHatchs) {
            tTileEntity.getBaseMetaTileEntity()
                .doExplosion(V[8]);
        }
    }

    @Override
    public long getAvailableData_EM() {
        return this.eAvailableData;
    }

    @Override
    public void stopMachine(@Nonnull ShutDownReason reason) {
        super.stopMachine(reason);
        this.eAvailableData = 0;
        for (MTEHatchRack rack : validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
    }

    @Override
    public void afterRecipeCheckFailed() {
        super.afterRecipeCheckFailed();
        for (MTEHatchRack rack : validMTEList(mRackHatchs)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM("front", 1, 2, 0, stackSize, hintsOnly);
        structureBuild_EM("cap", 1, 2, -1, stackSize, hintsOnly);

        byte offset = -2;
        for (int rackSlices = Math.min(Math.max(stackSize.stackSize, 3), 15); rackSlices > 0; rackSlices--) {
            structureBuild_EM("slice", 1, 2, offset--, stackSize, hintsOnly);
        }

        structureBuild_EM("cap", 1, 2, offset--, stackSize, hintsOnly);
        structureBuild_EM("back", 1, 2, offset, stackSize, hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int built;
        built = survivalBuildPiece("front", stackSize, 1, 2, 0, elementBudget, env, false, true);
        if (built >= 0) return built;
        built = survivalBuildPiece("cap", stackSize, 1, 2, -1, elementBudget, env, false, true);
        if (built >= 0) return built;

        byte offset = -2;
        for (int rackSlices = Math.min(Math.max(stackSize.stackSize, 3), 15); rackSlices > 0; rackSlices--) {
            built = survivalBuildPiece("slice", stackSize, 1, 2, offset--, elementBudget, env, false, true);
            if (built >= 0) return built;
        }
        built = survivalBuildPiece("cap", stackSize, 1, 2, offset--, elementBudget, env, false, true);
        if (built >= 0) return built;
        return survivalBuildPiece("back", stackSize, 1, 2, offset, elementBudget, env, false, true);
    }

    @Override
    public IStructureDefinition<HighPerformanceComputationArray> getStructure_EM() {
        return StructureDefinition.<HighPerformanceComputationArray>builder()
            .addShape("front", transpose(new String[][] { { " AA" }, { " AA" }, { " ~A" }, { " AA" }, { " AA" } }))
            .addShape("cap", transpose(new String[][] { { "-CB" }, { " DD" }, { " DD" }, { " DD" }, { "-CB" } }))
            .addShape("slice", transpose(new String[][] { { "-CB" }, { " ED" }, { " FD" }, { " GD" }, { "-CB" } }))
            .addShape("back", transpose(new String[][] { { " AA" }, { " AA" }, { " AA" }, { " AA" }, { " AA" } }))
            .addElement(
                'A',
                buildHatchAdder(HighPerformanceComputationArray.class)
                    .atLeast(
                        Energy.or(ExoticEnergy),
                        Maintenance,
                        InputHatch,
                        HatchElement.Uncertainty,
                        HatchElement.InputData,
                        HatchElement.OutputData,
                        WirelessComputationHatchElement.WirelessComputationHatch)
                    .casingIndex(BlockGTCasingsTT.textureOffset + 1)
                    .dot(1)
                    .buildAndChain(ofBlock(TTCasingsContainer.sBlockCasingsTT, 1)))
            .addElement('B', ofBlock(TTCasingsContainer.sBlockCasingsTT, 1))
            .addElement('C', ofBlock(TTCasingsContainer.sBlockCasingsTT, 2))
            .addElement('D', ofBlock(TTCasingsContainer.sBlockCasingsTT, 3))
            .addElement(
                'E',
                buildHatchAdder(HighPerformanceComputationArray.class).atLeast(RackHatchElement.RackHatch_0)
                    .casingIndex(BlockGTCasingsTT.textureOffset + 3)
                    .dot(1)
                    .buildAndChain(ofBlock(TTCasingsContainer.sBlockCasingsTT, 3)))
            .addElement(
                'F',
                buildHatchAdder(HighPerformanceComputationArray.class).atLeast(RackHatchElement.RackHatch_1)
                    .casingIndex(BlockGTCasingsTT.textureOffset + 3)
                    .dot(1)
                    .buildAndChain(ofBlock(TTCasingsContainer.sBlockCasingsTT, 3)))
            .addElement(
                'G',
                buildHatchAdder(HighPerformanceComputationArray.class).atLeast(RackHatchElement.RackHatch_2)
                    .casingIndex(BlockGTCasingsTT.textureOffset + 3)
                    .dot(1)
                    .buildAndChain(ofBlock(TTCasingsContainer.sBlockCasingsTT, 3)))
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

    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        HighPerformanceComputationArrayRenderer.renderTileEntity(this, x, y, z, timeSinceLastTick);
    }

    @Getter
    private float rotAngle = 0, rotAxisX = 1, rotAxisY = 0, rotAxisZ = 0, offsetX = 0, offsetY = 0, offsetZ = 0;

    public void setRenderRotation(ForgeDirection direction) {
        switch (direction) {
            case SOUTH -> rotAngle = 90;
            case NORTH -> rotAngle = 90;
            case WEST -> rotAngle = 0;
            case EAST -> rotAngle = 180;
            case UP -> rotAngle = -90;
            case DOWN -> rotAngle = -90;
        }
        rotAxisX = 0;
        rotAxisY = direction.offsetZ + direction.offsetX;
        rotAxisZ = direction.offsetY;
        offsetX = direction.offsetX;
        offsetY = direction.offsetY;
        offsetZ = direction.offsetZ;
        updateToClient();
    }

    public void updateToClient() {
        getBaseMetaTileEntity().getWorld()
            .markBlockForUpdate(
                getBaseMetaTileEntity().getXCoord(),
                getBaseMetaTileEntity().getYCoord(),
                getBaseMetaTileEntity().getZCoord());
    }

    public boolean addRackHatch0(IGregTechTileEntity tile, int casing) {
        return addRackToXY(tile, casing, 0);
    }

    public boolean addRackHatch1(IGregTechTileEntity tile, int casing) {
        return addRackToXY(tile, casing, 1);
    }

    public boolean addRackHatch2(IGregTechTileEntity tile, int casing) {
        return addRackToXY(tile, casing, 2);
    }

    private boolean addRackToXY(IGregTechTileEntity aTile, int casing, int adderIndex) {
        if (aTile == null) return false;
        IMetaTileEntity mte = aTile.getMetaTileEntity();
        if (mte instanceof MTEHatchRack rack) {
            rack.updateTexture(casing);
            rack.updateCraftingIcon(this.getMachineCraftingIcon());

            rackTable.put(this.totalLens, adderIndex, rack);
            return this.mRackHatchs.add(rack);
        }
        return false;
    }

    public enum RackHatchElement implements IHatchElement<HighPerformanceComputationArray> {

        RackHatch_0(HighPerformanceComputationArray::addRackHatch0, MTEHatchRack.class),
        RackHatch_1(HighPerformanceComputationArray::addRackHatch1, MTEHatchRack.class),
        RackHatch_2(HighPerformanceComputationArray::addRackHatch2, MTEHatchRack.class),
        RackHatch(HighPerformanceComputationArray::addRackHatchToMachineList, MTEHatchRack.class);

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<HighPerformanceComputationArray> adder;

        @SafeVarargs
        RackHatchElement(IGTHatchAdder<HighPerformanceComputationArray> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super HighPerformanceComputationArray> adder() {
            return adder;
        }

        @Override
        public long count(HighPerformanceComputationArray t) {
            return t.mRackHatchs.size();
        }
    }

    public boolean addRackHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchRack hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            return mRackHatchs.add(hatch);
        }
        return false;
    }

    public enum WirelessComputationHatchElement implements IHatchElement<HighPerformanceComputationArray> {

        WirelessComputationHatch(HighPerformanceComputationArray::addWirelessDataOutputToMachineList,
            MTEHatchWirelessComputationOutput.class);

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<HighPerformanceComputationArray> adder;

        @SafeVarargs
        WirelessComputationHatchElement(IGTHatchAdder<HighPerformanceComputationArray> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public IGTHatchAdder<? super HighPerformanceComputationArray> adder() {
            return adder;
        }

        @Override
        public long count(HighPerformanceComputationArray t) {
            return t.mWirelessComputationOutputHatchs.size();
        }
    }

    public final boolean addWirelessDataOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchWirelessComputationOutput hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            return mWirelessComputationOutputHatchs.add(hatch) && eOutputData.add(hatch);
        }
        return false;
    }

}
