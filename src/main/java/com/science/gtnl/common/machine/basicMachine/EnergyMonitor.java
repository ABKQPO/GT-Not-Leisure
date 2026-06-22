package com.science.gtnl.common.machine.basicMachine;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.modularui.EnergyMonitorGui;
import com.science.gtnl.common.machine.monitor.EnergyMonitorCollector;
import com.science.gtnl.common.machine.monitor.EnergyMonitorMode;
import com.science.gtnl.common.machine.monitor.EnergyMonitorRegistry;
import com.science.gtnl.common.machine.monitor.EnergyMonitorRowSnapshot;
import com.science.gtnl.common.machine.monitor.EnergyMonitorSnapshot;
import com.science.gtnl.common.machine.monitor.EnergyMonitorSummarySnapshot;
import com.science.gtnl.utils.enums.BlockIcons;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.render.TextureFactory;

public class EnergyMonitor extends MTEBasicTank {

    public static final int DEFAULT_VISIBLE_ROWS = 40;
    public static final int LOAD_MORE_ROWS = 40;
    public static final long REFRESH_INTERVAL_TICKS = 10L;
    private static final long DIRTY_SNAPSHOT_TICK = -REFRESH_INTERVAL_TICKS;

    private UUID monitorOwnerUuid;
    private EnergyMonitorMode totalEnergyMode = EnergyMonitorMode.ALL;
    private EnergyMonitorMode statisticsMode = EnergyMonitorMode.ALL;
    private int visibleRowCount = DEFAULT_VISIBLE_ROWS;
    private long lastSnapshotTick = DIRTY_SNAPSHOT_TICK;
    private EnergyMonitorSnapshot cachedSnapshot = EnergyMonitorSnapshot.empty();

    public EnergyMonitor(int aID, String aName, String aNameRegional, int aTier, ITexture... aTextures) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            1,
            new String[] { StatCollector.translateToLocal("Tooltip_EnergyMonitor_00") },
            aTextures);
    }

    public EnergyMonitor(String aName, int aTier, int aInvSlotCount, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EnergyMonitor(mName, mTier, mInventory.length, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[1][0], TextureFactory.builder()
                .addIcon(BlockIcons.OVERLAY_ENERGY_MONITOR)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[1][0] };
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        return new ITexture[0][0][0];
    }

    @Override
    public boolean doesFillContainers() {
        return false;
    }

    @Override
    public boolean doesEmptyContainers() {
        return false;
    }

    @Override
    public boolean canTankBeFilled() {
        return false;
    }

    @Override
    public boolean canTankBeEmptied() {
        return false;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        monitorOwnerUuid = base == null ? null : base.getOwnerUuid();
        visibleRowCount = DEFAULT_VISIBLE_ROWS;
        lastSnapshotTick = DIRTY_SNAPSHOT_TICK;
        cachedSnapshot = EnergyMonitorSnapshot.empty();
        EnergyMonitorRegistry.cleanupInvalidEntries();
        refreshSnapshotIfNeeded();
        return new EnergyMonitorGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        openGui(aPlayer);
        return true;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    public EnergyMonitorMode getTotalEnergyMode() {
        return totalEnergyMode;
    }

    public void setTotalEnergyMode(EnergyMonitorMode mode) {
        if (mode == null) {
            return;
        }
        totalEnergyMode = mode;
        markSnapshotDirty();
    }

    public EnergyMonitorMode getStatisticsMode() {
        return statisticsMode;
    }

    public void setStatisticsMode(EnergyMonitorMode mode) {
        if (mode == null) {
            return;
        }
        if (statisticsMode != mode) {
            visibleRowCount = DEFAULT_VISIBLE_ROWS;
        }
        statisticsMode = mode;
        markSnapshotDirty();
    }

    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    public void setVisibleRowCount(int count) {
        visibleRowCount = Math.max(DEFAULT_VISIBLE_ROWS, count);
        markSnapshotDirty();
    }

    public String getOwnerNameForGui() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null) {
            return "";
        }
        String ownerName = base.getOwnerName();
        if (ownerName != null && !ownerName.isEmpty()) {
            return ownerName;
        }
        UUID ownerUuid = base.getOwnerUuid();
        return ownerUuid == null ? "" : ownerUuid.toString();
    }

    public String getTotalEnergyTextForGui() {
        return getSummarySnapshot().getTotalEnergyText();
    }

    public String getAverageEuTextForGui() {
        return getSummarySnapshot().getAverageEuText();
    }

    public String getAmpTextForGui() {
        return getSummarySnapshot().getAmpText();
    }

    public int getVoltageTierForGui() {
        return getSummarySnapshot().getVoltageTier();
    }

    public boolean isOutputModeForGui() {
        return getSummarySnapshot().isOutputMode();
    }

    public boolean isEstimatedEmptyForGui() {
        return getSummarySnapshot().isEstimatedEmpty();
    }

    public String getEstimatedTimeTextForGui() {
        return getSummarySnapshot().getEstimatedTimeText();
    }

    public boolean hasMoreRowsForGui() {
        refreshSnapshotIfNeeded();
        return cachedSnapshot.hasMoreRows();
    }

    public List<EnergyMonitorRowSnapshot> getVisibleRowsForGui() {
        refreshSnapshotIfNeeded();
        return cachedSnapshot.getRows();
    }

    public void setVisibleRowsFromGui(List<EnergyMonitorRowSnapshot> rows) {
        if (rows == null) {
            return;
        }
        cachedSnapshot = new EnergyMonitorSnapshot(getSummarySnapshot(), rows, cachedSnapshot.hasMoreRows());
    }

    public void loadMoreRows() {
        setVisibleRowCount(visibleRowCount + LOAD_MORE_ROWS);
    }

    public EnergyMonitorSummarySnapshot getSummarySnapshot() {
        refreshSnapshotIfNeeded();
        return cachedSnapshot.getSummary();
    }

    public void markSnapshotDirty() {
        lastSnapshotTick = DIRTY_SNAPSHOT_TICK;
    }

    private void refreshSnapshotIfNeeded() {
        if (monitorOwnerUuid == null) {
            cachedSnapshot = EnergyMonitorSnapshot.empty();
            return;
        }

        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || base.getWorld() == null) {
            cachedSnapshot = EnergyMonitorSnapshot.empty();
            return;
        }

        long worldTick = base.getWorld()
            .getTotalWorldTime();
        if (cachedSnapshot != null && worldTick - lastSnapshotTick < REFRESH_INTERVAL_TICKS) {
            return;
        }

        cachedSnapshot = EnergyMonitorCollector
            .collect(monitorOwnerUuid, totalEnergyMode, statisticsMode, visibleRowCount);
        lastSnapshotTick = worldTick;
    }

    public UUID getMonitorOwnerUuid() {
        return monitorOwnerUuid;
    }

    public List<EnergyMonitorRowSnapshot> getCachedRows() {
        return cachedSnapshot == null ? Collections.emptyList() : cachedSnapshot.getRows();
    }
}
