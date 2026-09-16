package com.gtnewhorizons.modularui.common.widget;

import java.util.function.Supplier;

import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.drawable.TextRenderer;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.internal.Theme;

/**
 * Draws text. Accepts some text formatting rules. See also {@link Text}
 */
public class TextWidget extends Widget {

    private Text text;
    protected String localised;
    protected Supplier<Text> textSupplier = null;
    protected Integer defaultColor;
    protected EnumChatFormatting defaultFormat;
    private int maxWidth = -1;
    private Alignment textAlignment = Alignment.Center;
    private final TextRenderer textRenderer = new TextRenderer();

    protected boolean isDynamic = false;

    public TextWidget() {
        this(new Text(""));
    }

    public TextWidget(Text text) {
        this.text = text;
    }

    public TextWidget(String text) {
        this(new Text(text));
    }

    public static DynamicTextWidget dynamicText(Supplier<Text> supplier) {
        return new DynamicTextWidget(supplier);
    }

    public static DynamicTextWidget dynamicString(Supplier<String> supplier) {
        return new DynamicTextWidget(() -> new Text(supplier.get()));
    }

    public static TextWidget localised(String key, Object... data) {
        return new TextWidget(Text.localised(key, data));
    }

    @Override
    public void readJson(JsonObject json, String type) {
        super.readJson(json, type);
    }

    @Override
    public void onRebuild() {
        if (localised == null) {
            this.localised = getText().getFormatted();
        }
    }

    @Override
    protected @NotNull Size determineSize(int maxWidth, int maxHeight) {
        this.localised = getText().getFormatted();
        int width = this.maxWidth > 0 ? this.maxWidth : maxWidth - getPos().x;
        textRenderer.setSimulate(true);
        textRenderer.setAlignment(textAlignment, width, maxHeight);
        textRenderer.draw(localised);
        textRenderer.setSimulate(false);
        return textRenderer.getLastSize().grow(1, 1);
    }

    @Override
    public void onScreenUpdate() {
        if (textSupplier != null) {
            text = textSupplier.get();
            if (defaultColor != null) {
                text.color(defaultColor);
            }
            if (defaultFormat != null) {
                text.format(defaultFormat);
            }
        }
        if (isDynamic || isAutoSized()) {
            String l = getText().getFormatted();
            if (!l.equals(localised)) {
                checkNeedsRebuild();
                localised = l;
            }
        }
    }

    @Override
    public void draw(float partialTicks) {
        Text text = getText();
        if (localised == null) {
            localised = text.getFormatted();
        }
        textRenderer.setPos(0, 0);
        textRenderer.setShadow(text.hasShadow());
        textRenderer.setAlignment(textAlignment, size.width, size.height);
        textRenderer.setColor(text.hasColor() ? text.getColor() : Theme.INSTANCE.getText());
        textRenderer.draw(localised);
    }

    public Text getText() {
        return text;
    }

    /**
     * The textSupplier will ONLY be called on the client. It must have access to all the data it needs to build the
     * text from the client side.
     */
    public TextWidget setTextSupplier(Supplier<Text> textSupplier) {
        this.textSupplier = textSupplier;
        this.isDynamic = textSupplier != null;
        return this;
    }

    /**
     * The stringSupplier will ONLY be called on the client. It must have access to all the data it needs to build the
     * text from the client side.
     */
    public TextWidget setStringSupplier(Supplier<String> stringSupplier) {
        if (stringSupplier != null) {
            this.textSupplier = () -> new Text(stringSupplier.get());
            this.isDynamic = true;
        } else {
            this.isDynamic = false;
        }
        return this;
    }

    public TextWidget setDefaultColor(int color) {
        this.defaultColor = color;
        this.text.color(color);
        return this;
    }

    public TextWidget setDefaultColor(EnumChatFormatting color) {
        this.defaultFormat = color;
        this.text.format(color);
        return this;
    }

    public TextWidget setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public TextWidget setTextAlignment(Alignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public TextWidget setScale(float scale) {
        this.textRenderer.setScale(scale);
        return this;
    }
}
