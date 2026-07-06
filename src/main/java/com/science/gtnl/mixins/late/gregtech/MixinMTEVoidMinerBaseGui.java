package com.science.gtnl.mixins.late.gregtech;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.PanelSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.api.mixinHelper.IVoidMinerDimensionOverride;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import bwcrossmod.galacticgreg.VoidMinerUtility;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.api.enums.DimensionDef;
import gregtech.common.gui.modularui.multiblock.MTEVoidMinerBaseGui;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;
import gtneioreplugin.util.DimensionHelper;

@Mixin(value = MTEVoidMinerBaseGui.class, remap = false)
public abstract class MixinMTEVoidMinerBaseGui extends MTEMultiBlockBaseGui<MTEVoidMinerBase> {

    @Unique
    private static boolean gtnl$enableMixin = !ModList.VMTweak.isModLoaded() && MainConfig.machine.enableVoidMinerTweak;

    @Unique
    private int gtnl$lastSeenDimensionVersion = -1;

    @Unique
    private int gtnl$filterRefreshState = 0;

    private MixinMTEVoidMinerBaseGui() {
        super(null);
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        ListWidget<IWidget, ?> list = super.createTerminalTextWidget(syncManager, parent);
        if (!gtnl$enableMixin) return list;

        IVoidMinerDimensionOverride override = (IVoidMinerDimensionOverride) this.multiblock;
        StringSyncValue overrideTextSyncer = new StringSyncValue(override::getGtnl$overrideDisplayText);
        syncManager.syncValue("gtnl$voidMinerOverrideText", overrideTextSyncer);

        list.child(
            IKey.dynamic(() -> gtnl$getOverrideText(overrideTextSyncer.getValue()))
                .color(Color.WHITE.main)
                .asWidget()
                .setEnabledIf(widget -> {
                    String raw = overrideTextSyncer.getValue();
                    return raw != null && !raw.isEmpty();
                })
                .marginBottom(2)
                .widthRel(1));

        return list;
    }

    @Inject(method = "createRightPanelGapRow", at = @At("RETURN"), require = 1, remap = false)
    private void gtnl$onCreateRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager,
        CallbackInfoReturnable<Flow> cir) {
        if (!gtnl$enableMixin) return;

        IPanelHandler filterPanel = syncManager.findPanelHandlerNullable("filter");
        if (!(filterPanel instanceof PanelSyncHandler panelSyncHandler)) return;

        IVoidMinerDimensionOverride override = (IVoidMinerDimensionOverride) this.multiblock;
        gtnl$lastSeenDimensionVersion = override.getGtnl$dimensionChangeVersion();

        syncManager.onServerTick(() -> {
            switch (gtnl$filterRefreshState) {
                case 1 -> {
                    if (!filterPanel.isPanelOpen()) {
                        gtnl$forceDisposePanel(panelSyncHandler);
                        gtnl$filterRefreshState = 2;
                    }
                    return;
                }
                case 2 -> {
                    filterPanel.openPanel();
                    panelSyncHandler.syncToClient(PanelSyncHandler.SYNC_OPEN);
                    gtnl$filterRefreshState = 0;
                    return;
                }
                default -> {}
            }

            int currentVersion = override.getGtnl$dimensionChangeVersion();
            if (currentVersion == gtnl$lastSeenDimensionVersion) return;

            gtnl$lastSeenDimensionVersion = currentVersion;
            if (filterPanel.isPanelOpen()) {
                filterPanel.closePanel();
                gtnl$filterRefreshState = 1;
            } else {
                gtnl$forceDisposePanel(panelSyncHandler);
            }
        });
    }

    @Inject(method = "createFilterPopup", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void gtnl$onCreateFilterPopup(PanelSyncManager syncManager, IPanelHandler panelHandler,
        CallbackInfoReturnable<ModularPanel> cir) {
        if (!gtnl$enableMixin) return;

        gtnl$refreshClientDropMap();
        VoidMinerUtility.DropMap resolvedDropMap = this.multiblock.dropMap;
        if (resolvedDropMap == null) {
            cir.setReturnValue(gtnl$createEmptyFilterPanel());
            return;
        }

        resolvedDropMap.isDistributionCached(this.multiblock.extraDropMap);
        var ores = resolvedDropMap.getOres();
        if (ores == null || ores.length == 0) {
            cir.setReturnValue(gtnl$createEmptyFilterPanel());
            return;
        }

        if (this.multiblock.selected != null && this.multiblock.selected.getSlots() < ores.length) {
            this.multiblock.selected.setSize(ores.length);
        }
    }

    @Unique
    private String gtnl$getOverrideText(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        if (raw.startsWith("!")) {
            return StatCollector.translateToLocal(raw.substring(1));
        }

        String internalName = DimensionHelper.ABBR_TO_INTERNAL.getOrDefault(raw, raw);
        String displayName = DimensionHelper.getDimLocalizedName(internalName);
        if (displayName == null || displayName.isEmpty()) {
            displayName = raw;
        }
        return StatCollector.translateToLocal("Info_Dimension_Override") + displayName;
    }

    @Unique
    private void gtnl$refreshClientDropMap() {
        ItemStack slotStack = this.multiblock.mInventory[1];
        if (slotStack == null || !(slotStack.getItem() instanceof ItemDimensionDisplay)) {
            gtnl$refreshCurrentDimensionDropMap();
            return;
        }

        String dimensionAbbr = ItemDimensionDisplay.getDimension(slotStack);
        String dimensionName = DimensionHelper.ABBR_TO_INTERNAL.get(dimensionAbbr);
        if (dimensionName == null) {
            gtnl$refreshCurrentDimensionDropMap();
            return;
        }

        VoidMinerUtility.DropMap resolvedDropMap = VoidMinerUtility.dropMapsByDimName.get(dimensionName);
        if (resolvedDropMap == null) {
            gtnl$refreshCurrentDimensionDropMap();
            return;
        }

        gtnl$setDropMaps(dimensionName, resolvedDropMap);
    }

    @Unique
    private void gtnl$refreshCurrentDimensionDropMap() {
        if (this.multiblock.getBaseMetaTileEntity() == null) {
            return;
        }

        if (this.multiblock.getBaseMetaTileEntity()
            .getWorld() == null) {
            return;
        }

        ModDimensionDef dimensionDef = DimensionDef.getDefForWorld(
            this.multiblock.getBaseMetaTileEntity()
                .getWorld());
        if (dimensionDef == null || !dimensionDef.canBeVoidMined()) {
            gtnl$setEmptyDropMaps();
            return;
        }

        String dimensionName = dimensionDef.getDimensionName();
        VoidMinerUtility.DropMap resolvedDropMap = VoidMinerUtility.dropMapsByDimName
            .getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        gtnl$setDropMaps(dimensionName, resolvedDropMap);
    }

    @Unique
    private void gtnl$setDropMaps(String dimensionName, VoidMinerUtility.DropMap resolvedDropMap) {
        VoidMinerUtility.DropMap extraDropMap = VoidMinerUtility.extraDropsByDimName
            .getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        resolvedDropMap.isDistributionCached(extraDropMap);
        this.multiblock.dropMap = resolvedDropMap;
        this.multiblock.extraDropMap = extraDropMap;

        var ores = resolvedDropMap.getOres();
        if (ores != null && this.multiblock.selected != null && this.multiblock.selected.getSlots() < ores.length) {
            this.multiblock.selected.setSize(ores.length);
        }
    }

    @Unique
    private void gtnl$setEmptyDropMaps() {
        this.multiblock.dropMap = new VoidMinerUtility.DropMap();
        this.multiblock.extraDropMap = new VoidMinerUtility.DropMap();
    }

    @Unique
    private static void gtnl$forceDisposePanel(PanelSyncHandler panelSyncHandler) {
        try {
            Method disposeMethod = PanelSyncHandler.class.getDeclaredMethod("disposePanel");
            disposeMethod.setAccessible(true);
            disposeMethod.invoke(panelSyncHandler);
            panelSyncHandler.sync(PanelSyncHandler.SYNC_DISPOSE);
        } catch (ReflectiveOperationException ignored) {}
    }

    @Unique
    private ModularPanel gtnl$createEmptyFilterPanel() {
        return new ModularPanel("gt:vm:filter").child(ButtonWidget.panelCloseButton())
            .child(
                IKey.lang("vmtweak.gui.filter.no_ores")
                    .color(Color.WHITE.main)
                    .asWidget()
                    .margin(8))
            .coverChildren();
    }
}
