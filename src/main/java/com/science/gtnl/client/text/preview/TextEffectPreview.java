package com.science.gtnl.client.text.preview;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.text.EffectTextLayout;
import com.science.gtnl.utils.text.effect.TextEffectStyle;
import com.science.gtnl.utils.text.effect.TextEffects;

/** Interactive rendering sample using the same entry points as ordinary GUI text. */
public class TextEffectPreview extends GuiScreen {

    private static final int PAGE_SIZE = 7;
    private static final TextEffectStyle[] PRESETS = { TextEffects.INFERNUM_RED_RARITY,
        TextEffects.GENESIS_COMPONENT_RARITY_SHADER, TextEffects.PULSE_CIRCLE, TextEffects.NAMELESS_BOSS_BAR_SHADER,
        TextEffects.PULSE_UPWARDS, TextEffects.CALAMITY_RED, TextEffects.EXOTIC_RAINBOW, TextEffects.BURNISHED_AURIC,
        TextEffects.SUPERBOSS_RARITY, TextEffects.EVERCOLD_CYAN, TextEffects.INFERNUM_SPARK_RARITY,
        TextEffects.STARSILVER_RARITY };
    private GuiTextField sample;
    private boolean customPalette;
    private boolean bold;
    private int page;

    @Override
    public void initGui() {
        String previous = sample == null ? "Animated text / 动态文字" : sample.getText();
        sample = new GuiTextField(fontRendererObj, 18, 28, Math.max(80, width - 36), 18);
        sample.setMaxStringLength(160);
        sample.setText(previous);
        sample.setFocused(true);
        buttonList.clear();
        buttonList.add(
            new GuiButton(0, 18, height - 25, 115, 20, StatCollector.translateToLocal("gtnl.text_effect.palette")));
        buttonList
            .add(new GuiButton(1, 138, height - 25, 90, 20, StatCollector.translateToLocal("gtnl.text_effect.bold")));
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
        int count = Math.min(PAGE_SIZE, PRESETS.length - page * PAGE_SIZE);
        float scale = Math.min(1, Math.max(0.2f, (height - 118f) / (rowHeight * count)));
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
        for (int i = page * PAGE_SIZE; i < Math.min(PRESETS.length, (page + 1) * PAGE_SIZE); i++) {
            TextEffectStyle preset = PRESETS[i];
            labelWidth = Math.max(labelWidth, fontRendererObj.getStringWidth(effectName(preset)));
        }
        int textX = (int) Math.min(labelWidth + 36, availableWidth * 0.6f);
        float y = 0;
        for (int i = page * PAGE_SIZE; i < Math.min(PRESETS.length, (page + 1) * PAGE_SIZE); i++) {
            TextEffectStyle preset = PRESETS[i];
            TextEffectStyle style = customPalette ? preset.withColors(0x33CCFF, 0xFFAA33, 0xDD77FF) : preset;
            String name = fontRendererObj.trimStringToWidth(effectName(preset), textX - 30);
            fontRendererObj.drawString(name, 18, (int) y, 0xB0B0B0);
            String value = (bold ? "\u00a7l" : "") + sample.getText();
            String rendered = TextEffects.apply(value, style);
            fontRendererObj.drawStringWithShadow(
                fontRendererObj.trimStringToWidth(rendered, (int) availableWidth - textX - 20),
                textX,
                (int) y,
                0xFFFFFF);
            y += rowHeight;
        }
        if (y + 77 + 30 < height - 25) {
            String mixed = "Plain + " + TextEffects.apply(sample.getText(), TextEffects.GENESIS_COMPONENT_RARITY_SHADER)
                + " + plain";
            fontRendererObj.drawSplitString(mixed, 18, (int) y, width - 36, 0xFFFFFF);
        }
    }

    private static String effectName(TextEffectStyle style) {
        return style.rendererId()
            .substring(
                style.rendererId()
                    .indexOf(':') + 1);
    }

    private static int pageCount() {
        return (PRESETS.length + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) customPalette = !customPalette;
        if (button.id == 1) bold = !bold;
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
