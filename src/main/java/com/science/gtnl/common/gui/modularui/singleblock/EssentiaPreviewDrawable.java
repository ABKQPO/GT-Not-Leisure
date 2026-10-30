package com.science.gtnl.common.gui.modularui.singleblock;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;
import com.science.gtnl.utils.ColorUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public final class EssentiaPreviewDrawable implements IDrawable {

    private static final int ICON_SIZE = 16;
    private static final int ICON_SPACING = 1;
    private static final int COLUMNS = 3;
    private static final int MAX_ICONS = 9;

    private final Supplier<AspectList> supplier;

    public EssentiaPreviewDrawable(Supplier<AspectList> supplier) {
        this.supplier = supplier;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        AspectList aspects = supplier.get();
        if (aspects == null || aspects.size() == 0) return;

        Aspect[] sortedAspects = aspects.getAspectsSortedAmount();
        int iconCount = Math.min(sortedAspects.length, MAX_ICONS);
        int rows = (iconCount + COLUMNS - 1) / COLUMNS;
        int gridWidth = COLUMNS * (ICON_SIZE + ICON_SPACING) - ICON_SPACING;
        int gridHeight = rows * (ICON_SIZE + ICON_SPACING) - ICON_SPACING;
        int startX = x + (width - gridWidth) / 2;
        int startY = y + (height - gridHeight) / 2;

        Minecraft mc = Minecraft.getMinecraft();
        for (int i = 0; i < iconCount; i++) {
            Aspect aspect = sortedAspects[i];
            if (aspect == null) continue;
            int amount = aspects.getAmount(aspect);
            if (amount <= 0) continue;

            ItemStack icon = new ItemStack(ModItems.itemAspect);
            ItemAspect.setAspect(icon, aspect);

            int iconX = startX + (i % COLUMNS) * (ICON_SIZE + ICON_SPACING);
            int iconY = startY + (i / COLUMNS) * (ICON_SIZE + ICON_SPACING);

            applyColor(widgetTheme.getColor());
            GuiDraw.drawItem(icon, iconX, iconY, ICON_SIZE, ICON_SIZE, context.getCurrentDrawingZ());

            String amountText = String.valueOf(amount);
            mc.fontRenderer.drawStringWithShadow(
                amountText,
                iconX + ICON_SIZE - mc.fontRenderer.getStringWidth(amountText),
                iconY + ICON_SIZE - 9,
                ColorUtils.rgb(ColorUtils.guiTextColorWhite));
        }
    }
}
