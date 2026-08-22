package com.science.gtnl.common.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.MCHelper;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;

import gregtech.api.modularui2.GTGuis;
import gregtech.common.modularui2.factory.SelectItemGuiBuilder;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class MultiEssentiaJarGui {

    private static final String SELECT_ASPECT_ACTION_KEY = "multiEssentiaJarSelectAspect";
    private static final int MAX_ASPECT_TAG_BYTES = 64;

    private final ItemStack jarStack;
    private final AspectList storedAspects;
    private final List<Aspect> aspects;
    private final List<ItemStack> selections;
    private final TileEntityMultiEssentiaJar placedJar;
    private final boolean filterMode;
    private final PanelSyncManager syncManager;

    public MultiEssentiaJarGui(ItemStack jarStack, PanelSyncManager syncManager) {
        this(jarStack, null, false, null, syncManager);
    }

    public MultiEssentiaJarGui(TileEntityMultiEssentiaJar jar, EntityPlayer viewer, PanelSyncManager syncManager) {
        this(createJarStack(jar), jar, jar.getTotalAmount() == 0, viewer, syncManager);
    }

    private MultiEssentiaJarGui(ItemStack jarStack, TileEntityMultiEssentiaJar placedJar, boolean filterMode,
        EntityPlayer viewer, PanelSyncManager syncManager) {

        this.jarStack = jarStack;
        this.storedAspects = TileEntityMultiEssentiaJar.getStoredAspects(jarStack);
        this.aspects = filterMode ? getAllKnownSortedAspects(viewer) : getSortedAspects(storedAspects);
        this.selections = createSelections(aspects);
        this.placedJar = placedJar;
        this.filterMode = filterMode;
        this.syncManager = syncManager;
    }

    public ModularPanel build() {
        int currentSelected = getCurrentSelected();

        syncManager.registerServerSyncedAction(SELECT_ASPECT_ACTION_KEY, buffer -> {
            boolean requestedFilterMode = buffer.readBoolean();
            String aspectTag = NetworkUtils.readStringSafe(buffer);
            onAspectSelectedOnServer(requestedFilterMode, aspectTag);
        });

        return new SelectItemGuiBuilder(GTGuis.createPopUpPanel("multi_essentia_jar"), selections)
            .setHeaderItem(jarStack)
            .setTitle(
                IKey.lang(
                    filterMode ? "GTNL.gui.multi_essentia_jar.filter_title" : "GTNL.gui.multi_essentia_jar.title"))
            .setAllowDeselected(filterMode)
            .setSelected(currentSelected)
            .setOnSelectedClientAction((selected, $) -> {
                String aspectTag = selected >= 0 && selected < aspects.size() ? aspects.get(selected)
                    .getTag() : "";
                syncManager.callSyncedAction(SELECT_ASPECT_ACTION_KEY, buffer -> {
                    buffer.writeBoolean(filterMode);
                    NetworkUtils.writeStringSafe(buffer, aspectTag, MAX_ASPECT_TAG_BYTES, true);
                });
                playSelectionSound(selected);
                MCHelper.closeScreen();
            })
            .setCurrentItemWidgetCustomizer(
                widget -> widget.tooltipBuilder(
                    tooltip -> tooltip.clearText()
                        .add(getTooltip(currentSelected))))
            .setChoiceWidgetCustomizer((index, widget) -> {
                widget.playClickSound(false);
                widget.tooltipBuilder(
                    tooltip -> tooltip.clearText()
                        .add(getTooltip(index)));
            })
            .build();
    }

    private void onAspectSelectedOnServer(boolean requestedFilterMode, String aspectTag) {
        if (requestedFilterMode != filterMode || aspectTag == null || aspectTag.length() > MAX_ASPECT_TAG_BYTES) return;

        if (aspectTag.isEmpty()) {
            if (placedJar != null && filterMode) placedJar.removeFilterLabel();
            return;
        }

        Aspect selectedAspect = Aspect.getAspect(aspectTag);
        if (selectedAspect == null) return;

        if (placedJar == null) {
            if (TileEntityMultiEssentiaJar.setActiveAspect(jarStack, selectedAspect)) {
                EntityPlayer player = syncManager.getPlayer();
                if (player != null) player.inventoryContainer.detectAndSendChanges();
            }
            return;
        }

        if (filterMode) {
            EntityPlayer player = syncManager.getPlayer();
            if (player == null) return;
            if (!ThaumcraftApiHelper.hasDiscoveredAspect(player.getCommandSenderName(), selectedAspect)) return;
            placedJar.installFilterLabel(selectedAspect);
        } else if (!placedJar.hasFilterLabel()) {
            placedJar.setActiveAspect(selectedAspect);
        }
    }

    private int getCurrentSelected() {
        Aspect selectedAspect = filterMode ? TileEntityMultiEssentiaJar.getFilterAspect(jarStack)
            : TileEntityMultiEssentiaJar.getActiveAspect(jarStack);

        int selected = aspects.indexOf(selectedAspect);
        return selected >= 0 ? selected : SelectItemGuiBuilder.DESELECTED;
    }

    private String getTooltip(int index) {
        if (index < 0 || index >= aspects.size()) return "";

        Aspect aspect = aspects.get(index);

        if (filterMode) {
            return StatCollector.translateToLocalFormatted(
                "GTNL.gui.multi_essentia_jar.filter_aspect",
                aspect.getLocalizedDescription(),
                aspect.getTag());
        }

        return AspectGuiUtils.getClientAspectDisplay(aspect, storedAspects.getAmount(aspect));
    }

    private static List<Aspect> getSortedAspects(AspectList storedAspects) {
        List<Aspect> result = new ArrayList<>();

        if (storedAspects == null) return result;

        for (Aspect aspect : storedAspects.getAspects()) {
            if (aspect != null && storedAspects.getAmount(aspect) > 0) {
                result.add(aspect);
            }
        }

        result.sort(Comparator.comparing(Aspect::getTag));
        return result;
    }

    private static List<Aspect> getAllKnownSortedAspects(EntityPlayer viewer) {
        List<Aspect> result = new ArrayList<>();
        if (viewer == null) return result;

        String playerName = viewer.getCommandSenderName();

        for (Aspect aspect : Aspect.aspects.values()) {
            if (aspect != null && ThaumcraftApiHelper.hasDiscoveredAspect(playerName, aspect)) {
                result.add(aspect);
            }
        }

        result.sort(Comparator.comparing(Aspect::getTag));
        return result;
    }

    private static List<ItemStack> createSelections(List<Aspect> aspects) {
        List<ItemStack> result = new ArrayList<>(aspects.size());

        for (Aspect aspect : aspects) {
            ItemStack stack = new ItemStack(ModItems.itemAspect);
            ItemAspect.setAspect(stack, aspect);
            result.add(stack);
        }

        return result;
    }

    private static ItemStack createJarStack(TileEntityMultiEssentiaJar jar) {
        ItemStack stack = new ItemStack(jar.getBlockType(), 1, jar.getBlockMetadata());
        jar.writeToItemStack(stack);
        return stack;
    }

    private void playSelectionSound(int selected) {
        if (selected < 0 || selected >= aspects.size()) return;

        Aspect selectedAspect = aspects.get(selected);
        Aspect currentAspect = filterMode ? TileEntityMultiEssentiaJar.getFilterAspect(jarStack)
            : TileEntityMultiEssentiaJar.getActiveAspect(jarStack);

        // 选择当前已有的源质或标签时不播放
        if (selectedAspect == currentAspect) return;

        EntityPlayer player = MCHelper.getPlayer();
        if (player == null || player.worldObj == null) {
            return;
        }

        String soundName;
        float volume;
        float pitch;

        if (filterMode) {
            soundName = "thaumcraft:jar";
            volume = 0.4F;
            pitch = 1.0F;
        } else {
            soundName = "game.neutral.swim";
            volume = 0.5F;
            pitch = 1.0F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.3F;
        }

        if (placedJar != null) {
            player.worldObj.playSound(
                placedJar.xCoord + 0.5D,
                placedJar.yCoord + 0.5D,
                placedJar.zCoord + 0.5D,
                soundName,
                volume,
                pitch,
                false);
        } else {
            player.playSound(soundName, volume, pitch);
        }
    }

}
