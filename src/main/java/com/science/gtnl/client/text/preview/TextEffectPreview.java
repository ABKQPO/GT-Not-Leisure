package com.science.gtnl.client.text.preview;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.text.EffectTextLayout;
import com.science.gtnl.client.text.TextEffectRegistry;
import com.science.gtnl.utils.text.effect.TextEffectFormat;
import com.science.gtnl.utils.text.effect.TextEffectStyle;
import com.science.gtnl.utils.text.effect.TextEffects;

/** Interactive rendering sample using the same entry points as ordinary GUI text. */
public class TextEffectPreview extends GuiScreen {

    private static final int PAGE_SIZE = 7;
    private List<Entry> entries = List.of();
    private GuiTextField sample;
    private boolean customPalette;
    private boolean bold;
    private boolean italic;
    private int page;

    @Override
    public void initGui() {
        List<Entry> registered = new ArrayList<>();
        for (String identifier : TextEffectRegistry.identifiers()) {
            TextEffectStyle style = new TextEffectStyle(identifier, List.of(), 1);
            String alias = TextEffectFormat.aliasFor(identifier);
            String name = effectName(style);
            registered.add(new Entry(style, alias.isEmpty() ? name : "[" + alias + "] " + name));
        }
        entries = List.copyOf(registered);
        page = Math.min(page, pageCount() - 1);
        String previous = sample == null ? "Animated text / 动态文字" : sample.getText();
        sample = new GuiTextField(fontRendererObj, 18, 28, Math.max(80, width - 36), 18);
        sample.setMaxStringLength(160);
        sample.setText(previous);
        sample.setFocused(true);
        buttonList.clear();
        int buttonWidth = Math.min(115, Math.max(1, (width - 46) / 3));
        buttonList.add(
            new GuiButton(
                0,
                18,
                height - 25,
                buttonWidth,
                20,
                StatCollector.translateToLocal("gtnl.text_effect.palette")));
        buttonList.add(
            new GuiButton(
                1,
                23 + buttonWidth,
                height - 25,
                buttonWidth,
                20,
                StatCollector.translateToLocal("gtnl.text_effect.bold")));
        buttonList.add(
            new GuiButton(
                4,
                28 + buttonWidth * 2,
                height - 25,
                buttonWidth,
                20,
                StatCollector.translateToLocal("gtnl.text_effect.italic")));
        buttonList.add(new GuiButton(2, width - 70, 50, 24, 20, "<"));
        buttonList.add(new GuiButton(3, width - 42, 50, 24, 20, ">"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(
            fontRendererObj,
            StatCollector.translateToLocal("gtnl.text_effect.title"),
            width / 2,
            10,
            0xFFFFFF);
        sample.drawTextBox();
        fontRendererObj.drawString((page + 1) + " / " + pageCount(), width - 112, 56, 0xB0B0B0);
        float rowHeight = Math.max(20, EffectTextLayout.fontHeight(fontRendererObj) * 2.8f);
        int count = Math.min(PAGE_SIZE, entries.size() - page * PAGE_SIZE);
        float scale = Math.min(1, Math.max(0.2f, (height - 118f) / (rowHeight * Math.max(1, count))));
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(0, 77, 0);
            GL11.glScalef(scale, scale, 1);
            drawEffects(rowHeight, width / scale);
        } finally {
            GL11.glPopMatrix();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawEffects(float rowHeight, float availableWidth) {
        int labelWidth = 0;
        for (int i = page * PAGE_SIZE; i < Math.min(entries.size(), (page + 1) * PAGE_SIZE); i++) {
            labelWidth = Math.max(
                labelWidth,
                fontRendererObj.getStringWidth(
                    entries.get(i)
                        .label()));
        }
        int textX = (int) Math.min(labelWidth + 36, availableWidth * 0.6f);
        String value = (bold ? "\u00a7l" : "") + (italic ? "\u00a7o" : "") + sample.getText() + "\u00a7r";
        float y = 0;
        for (int i = page * PAGE_SIZE; i < Math.min(entries.size(), (page + 1) * PAGE_SIZE); i++) {
            Entry entry = entries.get(i);
            TextEffectStyle preset = entry.style();
            TextEffectStyle style = customPalette ? preset.withColors(0x33CCFF, 0xFFAA33, 0xDD77FF) : preset;
            String name = fontRendererObj.trimStringToWidth(entry.label(), textX - 30);
            fontRendererObj.drawString(name, 18, (int) y, 0xB0B0B0);
            String rendered = TextEffects.format(style) + value;
            fontRendererObj.drawStringWithShadow(
                fontRendererObj.trimStringToWidth(rendered, (int) availableWidth - textX - 20),
                textX,
                (int) y,
                0xFFFFFF);
            y += rowHeight;
        }
        if (!entries.isEmpty() && y + 77 + 30 < height - 25) {
            String mixed = "Plain + " + TextEffects.apply(
                value,
                entries.get(page * PAGE_SIZE)
                    .style())
                + " + plain";
            fontRendererObj.drawSplitString(mixed, 18, (int) y, width - 36, 0xFFFFFF);
        }
    }

    private static String effectName(TextEffectStyle style) {
        String identifier = style.rendererId();
        String namespace = ScienceNotLeisure.MODID + ":";
        return identifier.startsWith(namespace) ? identifier.substring(namespace.length()) : identifier;
    }

    private int pageCount() {
        return Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    public record Entry(TextEffectStyle style, String label) {}

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) customPalette = !customPalette;
        if (button.id == 1) bold = !bold;
        if (button.id == 4) italic = !italic;
        if (button.id == 2) page = Math.floorMod(page - 1, pageCount());
        if (button.id == 3) page = (page + 1) % pageCount();
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        if (!sample.textboxKeyTyped(character, keyCode)) super.keyTyped(character, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        sample.mouseClicked(x, y, button);
    }

    @Override
    public void updateScreen() {
        sample.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
