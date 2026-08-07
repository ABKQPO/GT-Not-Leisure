package com.science.gtnl.common.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.MCHelper;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.packet.OpenMultiEssentiaJarGuiPacket;

import gregtech.api.enums.GTValues;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.net.GTPacketUpdateItem;
import gregtech.common.modularui2.factory.SelectItemGuiBuilder;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class MultiEssentiaJarGui {

    private final ItemStack jarStack;
    private final AspectList storedAspects;
    private final List<Aspect> aspects;
    private final List<ItemStack> selections;
    private final boolean placedJar;
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final boolean filterMode;

    public MultiEssentiaJarGui(ItemStack jarStack) {
        this(jarStack, false, false, null, 0, 0, 0);
    }

    public MultiEssentiaJarGui(TileEntityMultiEssentiaJar jar, EntityPlayer viewer) {
        this(createJarStack(jar), true, jar.getTotalAmount() == 0, viewer, jar.xCoord, jar.yCoord, jar.zCoord);
    }

    private MultiEssentiaJarGui(ItemStack jarStack, boolean placedJar, boolean filterMode, EntityPlayer viewer,
        int blockX, int blockY, int blockZ) {

        this.jarStack = jarStack;
        this.storedAspects = TileEntityMultiEssentiaJar.getStoredAspects(jarStack);
        this.aspects = filterMode ? getAllKnownSortedAspects(viewer) : getSortedAspects(storedAspects);
        this.selections = createSelections(aspects);
        this.placedJar = placedJar;
        this.filterMode = filterMode;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }

    public ModularPanel build() {
        int currentSelected = getCurrentSelected();

        return new SelectItemGuiBuilder(GTGuis.createPopUpPanel("multi_essentia_jar"), selections)
            .setHeaderItem(jarStack)
            .setTitle(
                IKey.lang(
                    filterMode ? "GTNL.gui.multi_essentia_jar.filter_title" : "GTNL.gui.multi_essentia_jar.title"))
            .setAllowDeselected(filterMode)
            .setSelected(currentSelected)
            .setOnSelectedClientAction((selected, $) -> {
                selectAspect(selected);
                playSelectionSound(selected);
                MCHelper.closeScreen();
            })
            .setCurrentItemWidgetCustomizer(
                widget -> widget.tooltipBuilder(
                    tooltip -> tooltip.clearText()
                        .add(getTooltip(currentSelected))))
            .setChoiceWidgetCustomizer(
                (index, widget) -> {
                    widget.playClickSound(false);
                    widget.tooltipBuilder(
                        tooltip -> tooltip.clearText()
                            .add(getTooltip(index)));
                })
            .build();
    }

    private void selectAspect(int selected) {
        if (selected < 0 || selected >= aspects.size()) return;

        Aspect selectedAspect = aspects.get(selected);

        if (placedJar) {
            ScienceNotLeisure.network.sendToServer(
                filterMode
                    ? OpenMultiEssentiaJarGuiPacket.selectBlockFilter(blockX, blockY, blockZ, selectedAspect.getTag())
                    : OpenMultiEssentiaJarGuiPacket.selectBlockAspect(blockX, blockY, blockZ, selectedAspect.getTag()));
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(ItemBlockMultiEssentiaJar.SELECTED_ASPECT_PACKET_KEY, selectedAspect.getTag());
        GTValues.NW.sendToServer(new GTPacketUpdateItem(tag));
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
        ItemStack selection = selections.get(index);

        EntityPlayer player = MCHelper.getPlayer();
        boolean discovered = player != null
            && ThaumcraftApiHelper.hasDiscoveredAspect(player.getCommandSenderName(), aspect);

        if (!discovered) {
            return selection.getDisplayName();
        }

        if (filterMode) {
            return StatCollector.translateToLocalFormatted(
                "GTNL.gui.multi_essentia_jar.filter_aspect",
                aspect.getLocalizedDescription(),
                aspect.getTag());
        }

        return StatCollector.translateToLocalFormatted(
            "GTNL.gui.multi_essentia_jar.aspect",
            aspect.getLocalizedDescription(),
            aspect.getTag(),
            storedAspects.getAmount(aspect));
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
        if (player == null || player.worldObj == null || !player.worldObj.isRemote) {
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

        if (placedJar) {
            player.worldObj.playSound(blockX + 0.5D, blockY + 0.5D, blockZ + 0.5D, soundName, volume, pitch, false);
        } else {
            player.playSound(soundName, volume, pitch);
        }
    }

}
