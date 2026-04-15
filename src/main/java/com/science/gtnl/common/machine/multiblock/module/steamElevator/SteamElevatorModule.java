package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.StructureError;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import lombok.Getter;
import lombok.Setter;

public abstract class SteamElevatorModule extends SteamMultiMachineBase<SteamElevatorModule>
    implements ISurvivalConstructable {

    public int mTier;
    @Getter
    @Setter
    public long steamBufferSize;
    public boolean isConnected = false;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SEM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_elevator_module";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SEM_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 0;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;

    public SteamElevatorModule(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional);
        mTier = aTier;
        steamBufferSize = 640000 * (1L << (aTier));
    }

    public SteamElevatorModule(String aName, int aTier) {
        super(aName);
        mTier = aTier;
        steamBufferSize = 640000 * (1L << (aTier));
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings2, 0);
    }

    @Override
    public IStructureDefinition<SteamElevatorModule> getStructureDefinition() {
        return StructureDefinition.<SteamElevatorModule>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamElevatorModule.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamElevatorModule.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamElevatorModule.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    buildHatchAdder(SteamElevatorModule.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.InputHatch,
                            HatchElement.OutputHatch,
                            HatchElement.Maintenance)
                        .buildAndChain(
                            StructureUtility
                                .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasings2, 0)))))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        updateHatchTexture();
        return true;
    }

    @Override
    public void validateStructure(Collection<StructureError> errors, NBTTagCompound context) {}

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && isConnected) {
            super.onPostTick(aBaseMetaTileEntity, aTick);
            if (mEfficiency < 0) mEfficiency = 0;
            if (aBaseMetaTileEntity.getStoredEU() <= 0 && mMaxProgresstime > 0) {
                stopMachine(ShutDownReasonRegistry.POWER_LOSS);
            }
        }
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (lEUt > 0) {
            lEUt = -lEUt;
        }
        if (lEUt < 0) {
            long aSteamVal = ((-lEUt * 10000) / Math.max(1000, mEfficiency));
            if (!tryConsumeSteam((int) aSteamVal)) {
                stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                return false;
            }
        }
        return true;
    }

    @Override
    public long getTotalSteamCapacityLong() {
        return maxEUStore();
    }

    @Override
    public long getLongTotalSteamStored() {
        return getEUVar();
    }

    @Override
    public boolean tryConsumeSteam(int aAmount) {
        if (getEUVar() > aAmount) {
            setEUVar(getEUVar() - aAmount);
            return true;
        }
        return false;
    }

    public long increaseStoredEU(long maximumIncrease) {
        if (getBaseMetaTileEntity() == null) {
            return 0;
        }
        connect();
        long increasedEU = Math
            .min(getBaseMetaTileEntity().getEUCapacity() - getBaseMetaTileEntity().getStoredEU(), maximumIncrease);
        return getBaseMetaTileEntity().increaseStoredEnergyUnits(increasedEU, false) ? increasedEU : 0;
    }

    @Override
    public long maxEUStore() {
        return steamBufferSize;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    public void connect() {
        isConnected = true;
    }

    public void disconnect() {
        isConnected = false;
    }

    public int getMachineEffectRange() {
        return 0;
    };

}
