package com.science.gtnl.common.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;


import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;
import com.cleanroommc.modularui.api.MCHelper;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.packet.OpenMultiEssentiaJarGuiPacket;

import gregtech.api.enums.GTValues;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.net.GTPacketUpdateItem;
import gregtech.common.modularui2.factory.SelectItemGuiBuilder;
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

    public MultiEssentiaJarGui(ItemStack jarStack) {
        this(jarStack, false, 0, 0, 0);
    }

    public MultiEssentiaJarGui(TileEntityMultiEssentiaJar jar) {
        this(createJarStack(jar), true, jar.xCoord, jar.yCoord, jar.zCoord);
    }

    private MultiEssentiaJarGui(ItemStack jarStack, boolean placedJar, int blockX, int blockY, int blockZ) {
        this.jarStack = jarStack;
        this.storedAspects = TileEntityMultiEssentiaJar.getStoredAspects(jarStack);
        this.aspects = getSortedAspects(storedAspects);
        this.selections = createSelections(aspects);
        this.placedJar = placedJar;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }

    public ModularPanel build() {
        int currentSelected = getCurrentSelected();

        return new SelectItemGuiBuilder(GTGuis.createPopUpPanel("multi_essentia_jar"), selections)
            .setHeaderItem(jarStack)
            .setTitle(IKey.lang("GTNL.gui.multi_essentia_jar.title"))
            .setSelected(currentSelected)
            .setOnSelectedClientAction((selected, $) -> {
                selectAspect(selected);
                MCHelper.closeScreen();
            })
            .setCurrentItemWidgetCustomizer(
                widget -> widget.tooltipBuilder(
                    tooltip -> tooltip.clearText()
                        .add(getTooltip(currentSelected))))
            .setChoiceWidgetCustomizer(
                (index, widget) -> widget.tooltipBuilder(
                    tooltip -> tooltip.clearText()
                        .add(getTooltip(index))))
            .build();
    }

    private void selectAspect(int selected) {
        if (selected < 0 || selected >= aspects.size()) return;

        if (placedJar) {
            ScienceNotLeisure.network.sendToServer(
                OpenMultiEssentiaJarGuiPacket.selectBlockAspect(
                    blockX,
                    blockY,
                    blockZ,
                    aspects.get(selected)
                        .getTag()));
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(ItemBlockMultiEssentiaJar.SELECTED_ASPECT_PACKET_KEY, aspects.get(selected).getTag());
        GTValues.NW.sendToServer(new GTPacketUpdateItem(tag));
    }

    private int getCurrentSelected() {
        Aspect activeAspect = TileEntityMultiEssentiaJar.getActiveAspect(jarStack);
        int selected = aspects.indexOf(activeAspect);
        return selected >= 0 ? selected : 0;
    }

    private String getTooltip(int index) {
        if (index < 0 || index >= aspects.size()) return "";

        Aspect aspect = aspects.get(index);
        ItemStack ariAspectStack = selections.get(index);
        int amount = storedAspects.getAmount(aspect);

        // 这里会调用 ARI 的 ItemAspect.getItemStackDisplayName()
        String ariDisplayName = ariAspectStack.getDisplayName();
        String unknownName = StatCollector.translateToLocal("tc.aspect.unknown");

        if (unknownName.equals(ariDisplayName)) {
            return StatCollector.translateToLocalFormatted(
                "GTNL.gui.multi_essentia_jar.aspect_unknown",
                amount);
        }

        return StatCollector.translateToLocalFormatted(
            "GTNL.gui.multi_essentia_jar.aspect",
            aspect.getLocalizedDescription(),
            ariDisplayName,
            amount);
    }

    private static List<Aspect> getSortedAspects(AspectList storedAspects) {
        List<Aspect> result = new ArrayList<>();
        for (Aspect aspect : storedAspects.getAspects()) {
            if (aspect != null && storedAspects.getAmount(aspect) > 0) {
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

}
