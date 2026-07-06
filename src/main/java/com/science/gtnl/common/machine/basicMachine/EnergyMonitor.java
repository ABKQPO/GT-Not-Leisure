package com.science.gtnl.common.machine.basicMachine;

import java.math.BigInteger;
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
import lombok.Getter;

public class EnergyMonitor extends MTEBasicTank {

    public static final int DEFAULT_VISIBLE_ROWS = 40;
    public static final int LOAD_MORE_ROWS = 40;
    public static final long REFRESH_INTERVAL_TICKS = 10L;
    private static final long DIRTY_SNAPSHOT_TICK = -REFRESH_INTERVAL_TICKS;

    @Getter
    private UUID monitorOwnerUuid;
    @Getter
    private EnergyMonitorMode totalEnergyMode = EnergyMonitorMode.ALL;
    @Getter
    private EnergyMonitorMode statisticsMode = EnergyMonitorMode.ALL;
    @Getter
    private int visibleRowCount = DEFAULT_VISIBLE_ROWS;
    private long lastSnapshotTick = DIRTY_SNAPSHOT_TICK;
    private EnergyMonitorSnapshot cachedSnapshot = EnergyMonitorSnapshot.empty();
    private EnergyMonitorSummarySnapshot cachedSummary = EnergyMonitorSummarySnapshot.empty();
    private List<EnergyMonitorRowSnapshot> cachedVisibleRows = Collections.emptyList();
    private boolean cachedHasMoreRows;
    private boolean summaryDirty = true;
    private boolean visibleRowsDirty = true;
    @Getter
    private long visibleRowsRevision;

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
        cachedSummary = EnergyMonitorSummarySnapshot.empty();
        cachedVisibleRows = Collections.emptyList();
        cachedHasMoreRows = false;
        summaryDirty = true;
        visibleRowsDirty = true;
        EnergyMonitorRegistry.cleanupInvalidEntries();
        refreshSnapshotIfNeeded();
        return new EnergyMonitorGui(this).build(data, syncManager, uiSettings);
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

    public void setTotalEnergyMode(EnergyMonitorMode mode) {
        if (mode != null && totalEnergyMode != mode) {
            totalEnergyMode = mode;
            markSummaryDirty();
        }
    }

    public void setStatisticsMode(EnergyMonitorMode mode) {
        if (mode == null) {
            return;
        }
        if (statisticsMode != mode) {
            visibleRowCount = DEFAULT_VISIBLE_ROWS;
        }
        statisticsMode = mode;
        markVisibleRowsDirty();
        markSummaryDirty();
    }

    public void setVisibleRowCount(int count) {
        int clampedCount = Math.max(DEFAULT_VISIBLE_ROWS, count);
        if (visibleRowCount != clampedCount) {
            visibleRowCount = clampedCount;
            markVisibleRowsDirty();
        }
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

    public EnergyMonitorSummarySnapshot getSummarySnapshot() {
        if (summaryDirty) {
            cachedSummary = EnergyMonitorCollector
                .createSummary(ensureCachedSnapshot(), totalEnergyMode, statisticsMode);
            summaryDirty = false;
        }
        return cachedSummary;
    }

    public boolean hasMoreRowsForGui() {
        refreshVisibleRowsIfNeeded();
        return cachedHasMoreRows;
    }

    public List<EnergyMonitorRowSnapshot> getVisibleRowsForGui() {
        refreshVisibleRowsIfNeeded();
        return cachedVisibleRows;
    }

    public EnergyMonitorSnapshot getSnapshotForSync() {
        refreshSnapshotIfNeeded();
        return ensureCachedSnapshot();
    }

    public void setSnapshotFromSync(EnergyMonitorSnapshot snapshot) {
        cachedSnapshot = snapshot == null ? EnergyMonitorSnapshot.empty() : snapshot.copy();
        markVisibleRowsDirty();
        markSummaryDirty();
    }

    public void loadMoreRows() {
        setVisibleRowCount(visibleRowCount + LOAD_MORE_ROWS);
    }

    public void markSnapshotDirty() {
        lastSnapshotTick = DIRTY_SNAPSHOT_TICK;
    }

    public List<EnergyMonitorRowSnapshot> getCachedRows() {
        return cachedSnapshot == null ? Collections.emptyList() : cachedSnapshot.getRows();
    }

    public BigInteger getCachedWiredStored() {
        return ensureCachedSnapshot().getWiredStored();
    }

    public BigInteger getCachedWiredCapacity() {
        return ensureCachedSnapshot().getWiredCapacity();
    }

    public BigInteger getCachedWirelessStored() {
        return ensureCachedSnapshot().getWirelessStored();
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
        if (base.getWorld().isRemote) {
            return;
        }

        long worldTick = base.getWorld()
            .getTotalWorldTime();
        if (cachedSnapshot != null && worldTick - lastSnapshotTick < REFRESH_INTERVAL_TICKS) {
            return;
        }

        cachedSnapshot = EnergyMonitorCollector.collect(monitorOwnerUuid);
        markVisibleRowsDirty();
        markSummaryDirty();
        lastSnapshotTick = worldTick;
    }

    private EnergyMonitorSnapshot ensureCachedSnapshot() {
        if (cachedSnapshot == null) {
            cachedSnapshot = EnergyMonitorSnapshot.empty();
        }
        if (cachedSnapshot.getRows() == null) {
            cachedSnapshot.setRows(Collections.emptyList());
        }
        if (cachedSnapshot.getWiredStored() == null) {
            cachedSnapshot.setWiredStored(BigInteger.ZERO);
        }
        if (cachedSnapshot.getWiredCapacity() == null) {
            cachedSnapshot.setWiredCapacity(BigInteger.ZERO);
        }
        if (cachedSnapshot.getWirelessStored() == null) {
            cachedSnapshot.setWirelessStored(BigInteger.ZERO);
        }
        return cachedSnapshot;
    }

    private void refreshVisibleRowsIfNeeded() {
        if (!visibleRowsDirty) {
            return;
        }
        List<EnergyMonitorRowSnapshot> sourceRows = ensureCachedSnapshot().getRows();
        EnergyMonitorCollector.VisibleRowsResult visibleRowsResult = EnergyMonitorCollector
            .getVisibleRowsResult(sourceRows, statisticsMode, visibleRowCount);
        cachedVisibleRows = visibleRowsResult.getRows();
        cachedHasMoreRows = visibleRowsResult.hasMoreRows();
        visibleRowsDirty = false;
    }

    private void markSummaryDirty() {
        summaryDirty = true;
    }

    private void markVisibleRowsDirty() {
        visibleRowsDirty = true;
        visibleRowsRevision++;
    }

    private static BigInteger parseBigInteger(String value) {
        if (value == null || value.isEmpty()) {
            return BigInteger.ZERO;
        }
        try {
            return new BigInteger(value);
        } catch (NumberFormatException ignored) {
            return BigInteger.ZERO;
        }
    }
}
