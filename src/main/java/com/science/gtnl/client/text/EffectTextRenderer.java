package com.science.gtnl.client.text;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import com.gtnewhorizon.gtnhlib.util.font.IFontParameters;
import com.science.gtnl.client.text.EffectTextLayout.DrawRun;
import com.science.gtnl.client.text.EffectTextLayout.Layout;
import com.science.gtnl.client.text.TextMaskCache.Mask;
import com.science.gtnl.client.text.compat.AngelicaTextAdapter;
import com.science.gtnl.client.text.compat.AngelicaTextAdapter.FontSettings;
import com.science.gtnl.client.text.compat.FontBatchBridge;
import com.science.gtnl.utils.text.effect.EffectTextParser;
import com.science.gtnl.utils.text.effect.TextEffectStyle;

/** Draws marked spans while leaving glyph generation to the current font. */
public class EffectTextRenderer implements IResourceManagerReloadListener {

    public static final EffectTextRenderer INSTANCE = new EffectTextRenderer();
    private static final Logger LOGGER = LogManager.getLogger("GTNLTextEffects");
    private static final long START_TIME = System.nanoTime();
    private static int captureDepth;
    private static int nativeDepth;
    private final TextMaskCache masks = new TextMaskCache();
    private final Map<LayoutKey, Layout> layouts = new LinkedHashMap<>(32, 0.75f, true);
    private final Set<String> failed = new HashSet<>();

    public static boolean isCapturing() {
        return captureDepth > 0;
    }

    public static void beginCapture() {
        captureDepth++;
    }

    public static void endCapture() {
        captureDepth--;
    }

    public static boolean handles(String text) {
        return !isBypassingEffects() && EffectTextParser.containsMarkers(text);
    }

    public static boolean isBypassingEffects() {
        return isCapturing() || nativeDepth > 0;
    }

    public Layout layout(FontRenderer font, String text) {
        IFontParameters parameters = (IFontParameters) font;
        LayoutKey key = new LayoutKey(
            font,
            text,
            font.getUnicodeFlag(),
            font.FONT_HEIGHT,
            parameters.getGlyphScaleX(),
            parameters.getGlyphScaleY(),
            parameters.getGlyphSpacing(),
            parameters.getWhitespaceScale(),
            FontRendering.preprocessText("&q&z&v"),
            FontRendering.hexColorResetsStyles(),
            AngelicaTextAdapter.fontSettings());
        Layout layout = layouts.get(key);
        if (layout == null) {
            layout = EffectTextLayout.create(font, text);
            if (text.length() < 16384) {
                layouts.put(key, layout);
                if (layouts.size() > 256) layouts.remove(
                    layouts.keySet()
                        .iterator()
                        .next());
            }
        }
        return layout;
    }

    public int draw(FontRenderer font, String text, float x, float y, int color, boolean shadow) {
        if (text == null) return 0;
        if ((color & 0xFC000000) == 0) color |= 0xFF000000;
        Layout layout = layout(font, text);
        boolean styled = false;
        for (DrawRun run : layout.runs()) {
            if (run.style() != null) {
                styled = true;
                break;
            }
        }
        if (!styled) {
            // Invalid declarations must not flush another renderer's deferred font or model batches.
            for (DrawRun run : layout.runs()) drawPlain(font, run.text(), x + run.x(), y + run.y(), color, shadow);
            return endX(font, layout, x, shadow);
        }
        if (!DeferredTextEffects.isFlushing() && AngelicaTextAdapter.shouldDeferEffects()) {
            DeferredTextEffects.enqueue(font, text, x, y, color, shadow);
            return endX(font, layout, x, shadow);
        }
        FontBatchBridge bridge = AngelicaTextAdapter.bridge(font);
        int depth = bridge == null ? 0 : bridge.gtnl$suspendBatch();
        try {
            AngelicaTextAdapter.beginForeignDraw();
            return drawLayout(font, layout, x, y, color, shadow);
        } finally {
            try {
                AngelicaTextAdapter.endForeignDraw();
            } finally {
                if (bridge != null) bridge.gtnl$resumeBatch(depth);
            }
        }
    }

    private int drawLayout(FontRenderer font, Layout layout, float x, float y, int color, boolean shadow) {
        try (TextRenderState ignored = new TextRenderState()) {
            for (DrawRun run : layout.runs()) {
                drawRun(
                    font,
                    run.text(),
                    run.style(),
                    x + run.x(),
                    y + run.y(),
                    run.width(),
                    layout.height(),
                    color,
                    shadow);
            }
        }
        return endX(font, layout, x, shadow);
    }

    private static int endX(FontRenderer font, Layout layout, float x, boolean shadow) {
        return (int) Math.ceil(x + layout.width() + (shadow ? ((IFontParameters) font).getShadowOffset() : 0));
    }

    private void drawRun(FontRenderer font, String text, TextEffectStyle style, float x, float y, float width,
        float height, int color, boolean shadow) {
        TextEffect effect = style == null ? null : TextEffectRegistry.get(style.rendererId());
        if (effect == null || width <= 0
            || failed.contains(style.rendererId())
            || !AngelicaTextAdapter.supportsEffects(font)
            || !OpenGlHelper.isFramebufferEnabled()
            || !GLContext.getCapabilities().OpenGL20) {
            drawPlain(font, text, x, y, fallbackColor(style, effect, color), shadow);
            return;
        }
        try (TextRenderState ignored = new TextRenderState()) {
            float padding = Math.max(height, effect.padding(width, height));
            Mask mask = masks.get(font, text, width, height, padding);
            if (mask == null) {
                drawPlain(font, text, x, y, fallbackColor(style, effect, color), shadow);
                return;
            }
            double seconds = (System.nanoTime() - START_TIME) * 1e-9;
            if (shadow) {
                float offset = ((IFontParameters) font).getShadowOffset();
                effect.render(new TextRenderContext(mask, style, x + offset, y + offset, color, seconds, true));
            }
            effect.render(new TextRenderContext(mask, style, x, y, color, seconds, false));
        } catch (RuntimeException exception) {
            if (failed.add(style.rendererId()))
                LOGGER.warn("Text effect unavailable until resource reload: {}", style.rendererId(), exception);
            drawPlain(font, text, x, y, fallbackColor(style, effect, color), shadow);
        }
    }

    private static int fallbackColor(TextEffectStyle style, TextEffect effect, int color) {
        if (style == null) return color;
        int rgb = !style.colors()
            .isEmpty() ? style.colors()
                .get(0) : effect == null ? color & 0xFFFFFF : effect.fallbackColor();
        return color & 0xFF000000 | rgb;
    }

    private static void drawPlain(FontRenderer font, String text, float x, float y, int color, boolean shadow) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        nativeDepth++;
        try {
            font.drawString(text, 0, 0, color, shadow);
        } finally {
            nativeDepth--;
            GL11.glPopMatrix();
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        DeferredTextEffects.clear();
        layouts.clear();
        failed.clear();
        if (GLContext.getCapabilities().OpenGL20) {
            try (TextRenderState ignored = new TextRenderState()) {
                masks.clear();
                TextEffectRegistry.reload();
            }
        }
    }

    public record LayoutKey(FontRenderer font, String text, boolean unicode, int fontHeight, float scaleX, float scaleY,
        float spacing, float whitespace, String preprocessing, boolean hexResetsStyles, FontSettings fontSettings) {

        public LayoutKey(FontRenderer font, String text, boolean unicode, int fontHeight, float scaleX, float scaleY,
            float spacing, float whitespace, String preprocessing, boolean hexResetsStyles) {
            this(
                font,
                text,
                unicode,
                fontHeight,
                scaleX,
                scaleY,
                spacing,
                whitespace,
                preprocessing,
                hexResetsStyles,
                null);
        }
    }
}
