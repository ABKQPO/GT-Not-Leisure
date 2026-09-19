package com.science.gtnl.client.nei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.utils.enums.GTNLItemList;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class NuclearReactorGuideHandler extends TemplateRecipeHandler {

    private static final String GUI_TEXTURE = "nei:textures/gui/recipebg.png";

    private static final int TEXT_MARGIN = 4;
    private static final int TEXT_TOP = 24;
    private static final int LINE_HEIGHT = 10;
    private static final int TEXT_WIDTH = 156;
    private static final int MAX_LINES_PER_PAGE = 6;
    private static final String LINE_START_PUNCTUATION = "。，、；：？！）》】”’…·,.;:!?)]}\"'";

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("gtnl.nei.nuclear_reactor.guide.name");
    }

    @Override
    public String getOverlayIdentifier() {
        return "gtnl_nuclear_reactor_guide";
    }

    @Override
    public String getGuiTexture() {
        return GUI_TEXTURE;
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        ItemStack controller = GTNLItemList.NuclearReactor.get(1);
        if (controller == null || ingredient == null || !GTUtility.areStacksEqual(controller, ingredient)) return;

        for (GuidePageData page : buildPages()) {
            for (List<String> lines : splitPages(wrap(page.infoKey))) {
                arecipes.add(new GuidePage(page.stack, lines));
            }
        }
    }

    private static List<String> wrap(String infoKey) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String info = StatCollector.translateToLocal(infoKey)
            .replace("\\n", "\n");
        List<String> lines = font.listFormattedStringToWidth(info, TEXT_WIDTH);
        avoidLineStartPunctuation(lines);
        return lines;
    }

    private static void avoidLineStartPunctuation(List<String> lines) {
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int codeEnd = 0;
            while (codeEnd + 1 < line.length() && line.charAt(codeEnd) == '\u00a7') codeEnd += 2;
            int punctEnd = codeEnd;
            while (punctEnd < line.length() && LINE_START_PUNCTUATION.indexOf(line.charAt(punctEnd)) >= 0) {
                punctEnd++;
            }
            if (punctEnd == codeEnd) continue;
            lines.set(i - 1, lines.get(i - 1) + line.substring(codeEnd, punctEnd));
            lines.set(i, line.substring(0, codeEnd) + line.substring(punctEnd));
        }
    }

    private static List<List<String>> splitPages(List<String> lines) {
        List<List<String>> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += MAX_LINES_PER_PAGE) {
            pages.add(new ArrayList<>(lines.subList(i, Math.min(i + MAX_LINES_PER_PAGE, lines.size()))));
        }
        if (pages.isEmpty()) pages.add(new ArrayList<>());
        return pages;
    }

    private static List<GuidePageData> buildPages() {
        List<GuidePageData> pages = new ArrayList<>(4);
        addPage(pages, GTNLItemList.NuclearReactor.get(1), "gtnl.nei.nuclear_reactor");
        addPage(pages, ItemList.RodUranium.get(1), "gtnl.nei.nuclear_reactor.guide.fuel_rod");
        addPage(pages, GTModHandler.getIC2Item("reactorReflector", 1L), "gtnl.nei.nuclear_reactor.guide.reflector");
        addPage(
            pages,
            GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Invar, 1),
            "gtnl.nei.nuclear_reactor.guide.heat_plate");
        return pages;
    }

    private static void addPage(List<GuidePageData> pages, ItemStack stack, String infoKey) {
        if (stack == null || stack.getItem() == null) return;
        pages.add(new GuidePageData(stack, infoKey));
    }

    @Override
    public void drawExtras(int recipe) {
        GuidePage page = (GuidePage) this.arecipes.get(recipe);
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int y = TEXT_TOP;
        for (String line : page.getLines()) {
            font.drawString(line, TEXT_MARGIN, y, 0);
            y += LINE_HEIGHT;
        }
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 7, 13, 166, 65);
    }

    @Override
    public int getRecipeHeight(int recipe) {
        return TEXT_TOP + ((GuidePage) this.arecipes.get(recipe)).getLines()
            .size() * LINE_HEIGHT;
    }

    private static class GuidePageData {

        final ItemStack stack;
        final String infoKey;

        GuidePageData(ItemStack stack, String infoKey) {
            this.stack = stack;
            this.infoKey = infoKey;
        }
    }

    public class GuidePage extends CachedRecipe {

        private final PositionedStack stack;
        private final List<String> lines;

        public GuidePage(ItemStack item, List<String> lines) {
            this.lines = lines;
            this.stack = new PositionedStack(item, 75, 2);
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return Collections.singletonList(this.stack);
        }

        public List<String> getLines() {
            return this.lines;
        }
    }
}
