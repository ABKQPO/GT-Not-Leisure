package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.ResourceLocation;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DrawableStack;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamBeaconModule;
import com.science.gtnl.utils.enums.ModList;

import gregtech.api.enums.Mods;
import gregtech.api.modularui2.GTGuiTextures;

public class SteamBeaconModuleGui extends GTNLSteamMultiBlockBaseGui {

    private static final String CONFIG_PANEL_KEY = "gtnl_steam_beacon_config";
    private static final String SPEED_SYNC_KEY = "steamBeaconSpeed";
    private static final String STRENGTH_SYNC_KEY = "steamBeaconStrength";
    private static final String JUMP_BOOST_SYNC_KEY = "steamBeaconJumpBoost";
    private static final String RESISTANCE_SYNC_KEY = "steamBeaconResistance";
    private static final String HEALTH_REGENERATION_SYNC_KEY = "steamBeaconHealthRegeneration";
    private static final String NIGHT_VISION_SYNC_KEY = "steamBeaconNightVision";
    private static final String HASTE_SYNC_KEY = "steamBeaconHaste";
    private static final String FIRE_RESISTANCE_SYNC_KEY = "steamBeaconFireResistance";
    private static final String WATER_BREATHING_SYNC_KEY = "steamBeaconWaterBreathing";
    private static final String WARP_WARD_SYNC_KEY = "steamBeaconWarpWard";
    private static final String FEATHER_FEET_SYNC_KEY = "steamBeaconFeatherFeet";
    private static final String VIS_REGEN_SYNC_KEY = "steamBeaconVisRegen";
    private static final String CAN_WORK_SYNC_KEY = "steamBeaconCanWork";

    private static final UITexture INVENTORY_EFFECTS = UITexture
        .fullImage(Mods.Minecraft.resourceDomain, "gui/container/inventory");
    private static final UITexture THAUMCRAFT_POTIONS = UITexture
        .fullImage(Mods.Thaumcraft.resourceDomain, "misc/potions");
    private static final UITexture BOTANIA_POTIONS = UITexture.fullImage(Mods.Botania.resourceDomain, "gui/potions");
    private static final UITexture THAUMIC_HORIZONS_POTIONS = UITexture
        .fullImage(Mods.ThaumicHorizons.resourceDomain, "misc/potions");
    private static final UITexture SPEED_EFFECT = fromAtlas(INVENTORY_EFFECTS, 0, 198, 18, 216);
    private static final UITexture STRENGTH_EFFECT = fromAtlas(INVENTORY_EFFECTS, 72, 198, 90, 216);
    private static final UITexture JUMP_BOOST_EFFECT = fromAtlas(INVENTORY_EFFECTS, 36, 216, 54, 234);
    private static final UITexture RESISTANCE_EFFECT = fromAtlas(INVENTORY_EFFECTS, 108, 216, 126, 234);
    private static final UITexture HEALTH_REGENERATION_EFFECT = fromAtlas(INVENTORY_EFFECTS, 126, 198, 144, 216);
    private static final UITexture NIGHT_VISION_EFFECT = fromAtlas(INVENTORY_EFFECTS, 72, 216, 90, 234);
    private static final UITexture HASTE_EFFECT = fromAtlas(INVENTORY_EFFECTS, 36, 198, 54, 216);
    private static final UITexture WATER_BREATHING_EFFECT = fromAtlas(INVENTORY_EFFECTS, 0, 234, 18, 252);
    private static final UITexture FIRE_RESISTANCE_EFFECT = fromAtlas(INVENTORY_EFFECTS, 126, 216, 144, 234);
    private static final UITexture WARP_WARD_EFFECT = fromAtlas(THAUMCRAFT_POTIONS, 54, 234, 72, 252);
    private static final UITexture FEATHER_FEET_EFFECT = fromAtlas(BOTANIA_POTIONS, 18, 198, 36, 216);
    private static final UITexture VIS_REGEN_EFFECT = fromAtlas(THAUMIC_HORIZONS_POTIONS, 54, 198, 72, 216);
    private static final UITexture BEACON_MATERIAL = UITexture
        .fullImage(new ResourceLocation(ModList.ScienceNotLeisure.resourceDomain, "gui/picture/steam_beacon"));

    private final SteamBeaconModule beaconModule;

    public SteamBeaconModuleGui(SteamBeaconModule beaconModule) {
        super(beaconModule);
        this.beaconModule = beaconModule;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            SPEED_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasSpeedEffect, beaconModule::setSpeedEffect).allowC2S());
        syncManager.syncValue(
            STRENGTH_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasStrengthEffect, beaconModule::setStrengthEffect).allowC2S());
        syncManager.syncValue(
            JUMP_BOOST_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasJumpBoostEffect, beaconModule::setJumpBoostEffect).allowC2S());
        syncManager.syncValue(
            RESISTANCE_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasResistanceEffect, beaconModule::setResistanceEffect).allowC2S());
        syncManager.syncValue(
            HEALTH_REGENERATION_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasHealthRegenerationEffect, beaconModule::setHealthRegenerationEffect)
                .allowC2S());
        syncManager.syncValue(
            NIGHT_VISION_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasNightVisionEffect, beaconModule::setNightVisionEffect).allowC2S());
        syncManager.syncValue(
            HASTE_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasHasteEffect, beaconModule::setHasteEffect).allowC2S());
        syncManager.syncValue(
            FIRE_RESISTANCE_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasFireResistanceEffect, beaconModule::setFireResistanceEffect)
                .allowC2S());
        syncManager.syncValue(
            WATER_BREATHING_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasWaterBreathingEffect, beaconModule::setWaterBreathingEffect)
                .allowC2S());
        syncManager.syncValue(
            WARP_WARD_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasWarpWardEffect, beaconModule::setWarpWardEffect).allowC2S());
        syncManager.syncValue(
            FEATHER_FEET_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasFeatherFeetEffect, beaconModule::setFeatherFeetEffect).allowC2S());
        syncManager.syncValue(
            VIS_REGEN_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasVisRegenEffect, beaconModule::setVisRegenEffect).allowC2S());
        syncManager.syncValue(
            CAN_WORK_SYNC_KEY,
            new BooleanSyncValue(beaconModule::hasMachineCanWork, beaconModule::setMachineCanWork).allowC2S());
    }

    @Override
    protected void initPanelMap(ModularPanel parent, PanelSyncManager syncManager) {
        super.initPanelMap(parent, syncManager);
        panelMap.put(
            CONFIG_PANEL_KEY,
            syncManager.syncedPanel(
                CONFIG_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createConfigPanel(parent, syncManager)));
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createRightPanelGapRow(parent, syncManager).child(createConfigButton());
    }

    private IWidget createConfigButton() {
        return new ButtonWidget<>().size(16, 16)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_BATCH_MODE_ON)
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseButton -> {
                beaconModule.prepareBeaconConfigForGui();
                IPanelHandler panel = panelMap.get(CONFIG_PANEL_KEY);
                if (panel != null) {
                    if (panel.isPanelOpen()) panel.closePanel();
                    else panel.openPanel();
                }
            }))
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_SteamBeaconModule_00")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ModularPanel createConfigPanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(CONFIG_PANEL_KEY, null);
        panel.relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(100, 116)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            IKey.lang("Info_SteamBeaconModule_00")
                .asWidget()
                .pos(0, 0)
                .size(100, 18));
        panel.child(
            BEACON_MATERIAL.asWidget()
                .pos(8, 90)
                .size(83, 18));
        addEffectButtons(panel, syncManager);
        panel.child(createConfirmButton(syncManager));
        panel.child(createPaymentSlot(syncManager));
        return panel;
    }

    private void addEffectButtons(Dialog<?> panel, PanelSyncManager syncManager) {
        panel.child(
            createEffectButton(
                syncManager,
                SPEED_SYNC_KEY,
                SPEED_EFFECT,
                "Info_SteamBeaconModule_Effect_00",
                6,
                18,
                beaconModule::toggleSpeedEffect));
        panel.child(
            createEffectButton(
                syncManager,
                STRENGTH_SYNC_KEY,
                STRENGTH_EFFECT,
                "Info_SteamBeaconModule_Effect_01",
                24,
                18,
                beaconModule::toggleStrengthEffect));
        panel.child(
            createEffectButton(
                syncManager,
                JUMP_BOOST_SYNC_KEY,
                JUMP_BOOST_EFFECT,
                "Info_SteamBeaconModule_Effect_02",
                42,
                18,
                beaconModule::toggleJumpBoostEffect));
        panel.child(
            createEffectButton(
                syncManager,
                RESISTANCE_SYNC_KEY,
                RESISTANCE_EFFECT,
                "Info_SteamBeaconModule_Effect_03",
                6,
                36,
                beaconModule::toggleResistanceEffect));
        panel.child(
            createEffectButton(
                syncManager,
                HEALTH_REGENERATION_SYNC_KEY,
                HEALTH_REGENERATION_EFFECT,
                "Info_SteamBeaconModule_Effect_04",
                24,
                36,
                beaconModule::toggleHealthRegenerationEffect));
        panel.child(
            createEffectButton(
                syncManager,
                NIGHT_VISION_SYNC_KEY,
                NIGHT_VISION_EFFECT,
                "Info_SteamBeaconModule_Effect_05",
                42,
                36,
                beaconModule::toggleNightVisionEffect));

        if (beaconModule.getBeaconTierForGui() > 1) {
            panel.child(
                createEffectButton(
                    syncManager,
                    HASTE_SYNC_KEY,
                    HASTE_EFFECT,
                    "Info_SteamBeaconModule_Effect_06",
                    6,
                    54,
                    beaconModule::toggleHasteEffect));
            panel.child(
                createEffectButton(
                    syncManager,
                    FIRE_RESISTANCE_SYNC_KEY,
                    FIRE_RESISTANCE_EFFECT,
                    "Info_SteamBeaconModule_Effect_07",
                    24,
                    54,
                    beaconModule::toggleFireResistanceEffect));
            panel.child(
                createEffectButton(
                    syncManager,
                    WATER_BREATHING_SYNC_KEY,
                    WATER_BREATHING_EFFECT,
                    "Info_SteamBeaconModule_Effect_08",
                    42,
                    54,
                    beaconModule::toggleWaterBreathingEffect));
        }

        if (beaconModule.getBeaconTierForGui() > 2) {
            panel.child(
                createEffectButton(
                    syncManager,
                    WARP_WARD_SYNC_KEY,
                    WARP_WARD_EFFECT,
                    "Info_SteamBeaconModule_Effect_09",
                    6,
                    72,
                    beaconModule::toggleWarpWardEffect));
            panel.child(
                createEffectButton(
                    syncManager,
                    FEATHER_FEET_SYNC_KEY,
                    FEATHER_FEET_EFFECT,
                    "Info_SteamBeaconModule_Effect_10",
                    24,
                    72,
                    beaconModule::toggleFeatherFeetEffect));
            if (Mods.ThaumicHorizons.isModLoaded()) {
                panel.child(
                    createEffectButton(
                        syncManager,
                        VIS_REGEN_SYNC_KEY,
                        VIS_REGEN_EFFECT,
                        "Info_SteamBeaconModule_Effect_11",
                        42,
                        72,
                        beaconModule::toggleVisRegenEffect));
            }
        }
    }

    private IWidget createEffectButton(PanelSyncManager syncManager, String syncKey, UITexture icon, String tooltipKey,
        int x, int y, Runnable toggleAction) {
        BooleanSyncValue effectSyncer = syncManager.findSyncHandler(syncKey, BooleanSyncValue.class);
        return new ButtonWidget<>().size(16, 16)
            .pos(x, y)
            .background(
                new DynamicDrawable(
                    () -> new DrawableStack(
                        effectSyncer.getBoolValue() ? GTGuiTextures.BUTTON_STANDARD_PRESSED
                            : GTGuiTextures.BUTTON_STANDARD,
                        icon)))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseButton -> {
                toggleAction.run();
                effectSyncer.setBoolValue(!effectSyncer.getBoolValue(), false, true);
            }))
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(tooltipKey)))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createConfirmButton(PanelSyncManager syncManager) {
        BooleanSyncValue canWorkSyncer = syncManager.findSyncHandler(CAN_WORK_SYNC_KEY, BooleanSyncValue.class);
        return new ButtonWidget<>().size(16, 16)
            .pos(66, 37)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_CHECKMARK)
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseButton -> {
                boolean applied = beaconModule.applyBeaconConfigFromGui();
                canWorkSyncer.setBoolValue(beaconModule.hasMachineCanWork(), false, true);
                if (applied) {
                    IPanelHandler panel = panelMap.get(CONFIG_PANEL_KEY);
                    if (panel != null) {
                        panel.closePanel();
                        panel.openPanel();
                    }
                }
            }))
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_SteamBeaconModule_01")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createPaymentSlot(PanelSyncManager syncManager) {
        ModularSlot slot = new ModularSlot(
            new GTNLMui2ItemHandlerAdapter(beaconModule.getBeaconInputSlotHandlerForGui()),
            0).filter(beaconModule::isValidBeaconPaymentItemForGui)
                .singletonSlotGroup();
        return new ItemSlot().slot(slot)
            .background(GTGuiTextures.SLOT_ITEM_STANDARD)
            .pos(65, 18);
    }

    private static UITexture fromAtlas(UITexture atlas, int x0, int y0, int x1, int y1) {
        return atlas.getSubArea(x0 / 256.0F, y0 / 256.0F, x1 / 256.0F, y1 / 256.0F);
    }
}
