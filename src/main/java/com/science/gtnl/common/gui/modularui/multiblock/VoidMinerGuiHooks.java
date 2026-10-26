package com.science.gtnl.common.gui.modularui.multiblock;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.PanelSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.api.mixinHelper.IVoidMinerDimensionOverride;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import bwcrossmod.galacticgreg.VoidMinerUtility;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.api.enums.DimensionDef;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;
import gtneioreplugin.util.DimensionHelper;

public class VoidMinerGuiHooks {

    private VoidMinerGuiHooks() {}

    public static void addOverrideLine(ListWidget<IWidget, ?> list, MTEVoidMinerBase<?> miner,
        IGregTechTileEntity baseMetaTileEntity, PanelSyncManager syncManager) {
        IVoidMinerDimensionOverride override = (IVoidMinerDimensionOverride) miner;
        StringSyncValue overrideTextSyncer = new StringSyncValue(override::getGtnl$overrideDisplayText);
        syncManager.syncValue("gtnl$voidMinerOverrideText", overrideTextSyncer);

        list.child(
            IKey.dynamic(() -> overrideText(overrideTextSyncer.getValue()))
                .color(Color.YELLOW.main)
                .asWidget()
                .textAlign(Alignment.CenterLeft)
                .setEnabledIf(widget -> {
                    if (!baseMetaTileEntity.isActive()) return false;
                    String raw = overrideTextSyncer.getValue();
                    return raw != null && !raw.isEmpty();
                })
                .marginBottom(2)
                .fullWidth());
    }

    // MUI2 builds the filter panel once, so it is disposed, and reopened if open, when the dimension changes.
    public static void watchDimensionChanges(MTEVoidMinerBase<?> miner, PanelSyncManager syncManager) {
        IPanelHandler filterPanel = syncManager.findPanelHandlerNullable("filter");
        if (!(filterPanel instanceof PanelSyncHandler panelSyncHandler)) return;

        IVoidMinerDimensionOverride override = (IVoidMinerDimensionOverride) miner;
        int[] lastSeenVersion = { override.getGtnl$dimensionChangeVersion() };
        int[] refreshState = { 0 };

        syncManager.onServerTick(() -> {
            switch (refreshState[0]) {
                case 1 -> {
                    if (!filterPanel.isPanelOpen()) {
                        forceDisposePanel(panelSyncHandler);
                        refreshState[0] = 2;
                    }
                    return;
                }
                case 2 -> {
                    filterPanel.openPanel();
                    panelSyncHandler.syncToClient(PanelSyncHandler.SYNC_OPEN);
                    refreshState[0] = 0;
                    return;
                }
                default -> {}
            }

            int currentVersion = override.getGtnl$dimensionChangeVersion();
            if (currentVersion == lastSeenVersion[0]) return;

            lastSeenVersion[0] = currentVersion;
            if (filterPanel.isPanelOpen()) {
                filterPanel.closePanel();
                refreshState[0] = 1;
            } else {
                forceDisposePanel(panelSyncHandler);
            }
        });
    }

    // Returns the panel to show instead of GT's filter when there are no ores, otherwise null.
    public static ModularPanel filterPopup(MTEVoidMinerBase<?> miner) {
        refreshClientDropMap(miner);
        VoidMinerUtility.DropMap resolvedDropMap = miner.dropMap;
        if (resolvedDropMap == null) return emptyFilterPanel();

        resolvedDropMap.isDistributionCached(miner.extraDropMap);
        var ores = resolvedDropMap.getOres();
        if (ores == null || ores.length == 0) return emptyFilterPanel();

        if (miner.selected != null && miner.selected.getSlots() < ores.length) {
            miner.selected.setSize(ores.length);
        }
        return null;
    }

    public static String overrideText(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        if (raw.startsWith("!")) {
            return StatCollector.translateToLocal(raw.substring(1));
        }

        String internalName = DimensionHelper.ABBR_TO_INTERNAL.getOrDefault(raw, raw);
        String displayName = DimensionHelper.getDimLocalizedName(internalName);
        if (displayName == null || displayName.isEmpty()) {
            displayName = raw;
        }
        return StatCollector.translateToLocal("gtnl.gui.void_miner.dimension_override") + displayName;
    }

    public static void refreshClientDropMap(MTEVoidMinerBase<?> miner) {
        ItemStack slotStack = miner.mInventory[1];
        if (slotStack == null || !(slotStack.getItem() instanceof ItemDimensionDisplay)) {
            refreshCurrentDimensionDropMap(miner);
            return;
        }

        String dimensionAbbr = ItemDimensionDisplay.getDimension(slotStack);
        String dimensionName = DimensionHelper.ABBR_TO_INTERNAL.get(dimensionAbbr);
        if (dimensionName == null) {
            refreshCurrentDimensionDropMap(miner);
            return;
        }

        VoidMinerUtility.DropMap resolvedDropMap = VoidMinerUtility.dropMapsByDimName.get(dimensionName);
        if (resolvedDropMap == null) {
            refreshCurrentDimensionDropMap(miner);
            return;
        }

        setDropMaps(miner, dimensionName, resolvedDropMap);
    }

    private static void refreshCurrentDimensionDropMap(MTEVoidMinerBase<?> miner) {
        if (miner.getBaseMetaTileEntity() == null || miner.getBaseMetaTileEntity()
            .getWorld() == null) return;

        ModDimensionDef dimensionDef = DimensionDef.getDefForWorld(
            miner.getBaseMetaTileEntity()
                .getWorld());
        if (dimensionDef == null || !dimensionDef.canBeVoidMined()) {
            miner.dropMap = new VoidMinerUtility.DropMap();
            miner.extraDropMap = new VoidMinerUtility.DropMap();
            return;
        }

        String dimensionName = dimensionDef.getDimensionName();
        VoidMinerUtility.DropMap resolvedDropMap = VoidMinerUtility.dropMapsByDimName
            .getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        setDropMaps(miner, dimensionName, resolvedDropMap);
    }

    private static void setDropMaps(MTEVoidMinerBase<?> miner, String dimensionName,
        VoidMinerUtility.DropMap resolvedDropMap) {
        VoidMinerUtility.DropMap extraDropMap = VoidMinerUtility.extraDropsByDimName
            .getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        resolvedDropMap.isDistributionCached(extraDropMap);
        miner.dropMap = resolvedDropMap;
        miner.extraDropMap = extraDropMap;

        var ores = resolvedDropMap.getOres();
        if (ores != null && miner.selected != null && miner.selected.getSlots() < ores.length) {
            miner.selected.setSize(ores.length);
        }
    }

    private static void forceDisposePanel(PanelSyncHandler panelSyncHandler) {
        try {
            Method disposeMethod = PanelSyncHandler.class.getDeclaredMethod("disposePanel");
            disposeMethod.setAccessible(true);
            disposeMethod.invoke(panelSyncHandler);
            panelSyncHandler.sync(PanelSyncHandler.SYNC_DISPOSE);
        } catch (ReflectiveOperationException ignored) {}
    }

    private static ModularPanel emptyFilterPanel() {
        return new ModularPanel("gt:vm:filter").child(ButtonWidget.panelCloseButton())
            .child(
                IKey.lang("vmtweak.gui.filter.no_ores")
                    .color(Color.WHITE.main)
                    .asWidget()
                    .margin(8))
            .coverChildren();
    }
}
