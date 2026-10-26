package com.gtnllib.gui.wheel;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import com.gtnllib.client.fx.FxAngle;
import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.fx.screen.FxFrostedBackdrop;
import com.gtnllib.client.fx.screen.FxShapes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class WheelWidget extends Widget<WheelWidget> implements Interactable {

    private static final float DETAIL_REFERENCE_RADIUS = 64F;
    private static final float FRAME_MS = 1000F / 60F;
    private static final float MAX_FRAME_STEPS = 10F;
    private static final int TEXT_SHADOW_ALPHA = 0x99;
    private static final float TITLE_WIDTH = 0.82F, TITLE_HEIGHT = 0.6F;
    private static final float ICON_RATIO = 0.9F;
    private static final int MIN_ICON_SIZE = 8;
    private static final int MAX_ICON_SIZE = 32;
    private static final float SEPARATOR_GAP = 4F;
    private static final float HIGHLIGHT_INNER_INSET = 8F;
    private static final float HIGHLIGHT_OUTER_INSET = 2F;
    private static final float SETTLE_EXPAND = 2F;
    private static final long SETTLE_DELAY_MS = 500L;
    private static final float SETTLE_ANIMATION_MS = 200F;
    private static final float HOVER_CHEVRON_SIZE = 9F;
    private static final float HOVER_CHEVRON_THICKNESS = 4F;
    private static final float HOVER_CHEVRON_GAP = 4F;
    private static final float PAGE_CHEVRON_SIZE = 8F;
    private static final float PAGE_CHEVRON_THICKNESS = 3F;
    private static final float PAGE_ARROW_OFFSET = 26F;
    // Why: the hotbar is still drawn under a wheel screen, so whatever sits below the wheel has to end above it.
    private static final float HOTBAR_CLEARANCE = 26F, TOP_CLEARANCE = 6F;
    private static final float MIN_FITTED_RADIUS = 48F;

    private final WheelNavigator navigator;
    private final WheelStyle style;
    private final FxFrostedBackdrop frostedBackdrop = new FxFrostedBackdrop();
    private boolean dismissOnConfirm;
    private BiPredicate<Integer, Integer> selectionBlocker = (x, y) -> false;
    private KeyBinding holdKey;
    private Runnable holdRelease = this::confirm;
    private int room, roomAbove;
    private boolean holdReleased;
    private long displayTime;
    private boolean animationStarted;
    private boolean dismissing;
    private boolean finished;
    private int selected = -1;
    private float highlightAngle;
    private long selectedSince;
    private float mouseAngle = Float.NaN;
    private float progress;
    private long frameTime;
    private long lastFrameTime;
    private float centerX;
    private float centerY;
    private float innerRadius;
    private float outerRadius;
    private float detailScale = 1;
    private ModularGuiContext drawContext;
    private WidgetTheme drawTheme;

    public static float slotAngle(int index, int count) {
        return FxAngle.TAU * index / count;
    }

    public static int slotAt(float angle, int count) {
        if (count <= 0) return -1;
        float step = FxAngle.TAU / count;
        return (int) Math.floor(FxAngle.normalizePositive(angle + step / 2) / step) % count;
    }

    public static float fitSize(float width, float height, int room, WheelStyle style) {
        return fitSize(width, height, room, 0, style);
    }

    /**
     * The wheel size that leaves {@code below} pixels under the wheel, clear of the hotbar, and {@code above}
     * pixels over it, clear of the screen top. The wheel stays centred, so the tighter side decides.
     */
    public static float fitSize(float width, float height, int below, int above, WheelStyle style) {
        float size = Math.min(width, height);
        if (below <= 0 && above <= 0) return size;
        float reserve = Math.max(below > 0 ? HOTBAR_CLEARANCE + below : 0, above > 0 ? TOP_CLEARANCE + above : 0);
        float fitted = Math.max(height / 2F - reserve, MIN_FITTED_RADIUS);
        return Math.min(size, fitted / style.outerRadiusScale);
    }

    public WheelWidget(WheelMenuModel model, WheelOpenMode openMode) {
        this.navigator = new WheelNavigator(model, openMode);
        this.style = model.style();
        restart();
    }

    public WheelWidget dismissOnConfirm(boolean dismissOnConfirm) {
        this.dismissOnConfirm = dismissOnConfirm;
        return this;
    }

    public WheelWidget selectionBlocker(BiPredicate<Integer, Integer> selectionBlocker) {
        this.selectionBlocker = Objects.requireNonNull(selectionBlocker, "selectionBlocker");
        return this;
    }

    public WheelWidget holdKey(KeyBinding holdKey) {
        this.holdKey = holdKey;
        return this;
    }

    public WheelWidget onHoldRelease(Runnable holdRelease) {
        this.holdRelease = Objects.requireNonNull(holdRelease, "holdRelease");
        return this;
    }

    public WheelWidget room(int room) {
        this.room = Math.max(0, room);
        return this;
    }

    public int room() {
        return room;
    }

    public WheelWidget roomAbove(int roomAbove) {
        this.roomAbove = Math.max(0, roomAbove);
        return this;
    }

    public int roomAbove() {
        return roomAbove;
    }

    public WheelNavigator navigator() {
        return navigator;
    }

    public boolean isDismissed() {
        return dismissing || finished;
    }

    public boolean isFinished() {
        return finished;
    }

    public float progress() {
        return progress;
    }

    public void dismiss() {
        if (finished) return;
        long now = Minecraft.getSystemTime();
        if (dismissing || !shouldRender(now)) {
            finished = true;
            return;
        }
        displayTime = now;
        dismissing = true;
    }

    public void confirm() {
        if (isDismissed()) return;
        WheelNavigator.Outcome outcome = navigator.confirm(selected);
        if (outcome == WheelNavigator.Outcome.SUBMENU) restart();
        else if (dismissOnConfirm) dismiss();
    }

    public boolean press(int mouseButton) {
        if (isDismissed()) return false;
        if (mouseButton == 1) return navigator.rightClick(selected);
        if (mouseButton != 0) return false;
        if (navigator.openMode() == WheelOpenMode.HOLD && selected < 0) return false;
        confirm();
        return true;
    }

    public boolean back() {
        if (isDismissed()) return dismissOnConfirm;
        if (navigator.back()) {
            restart();
            return true;
        }
        if (!dismissOnConfirm) return false;
        dismiss();
        return true;
    }

    public boolean scroll(int direction) {
        if (navigator.pageCount() <= 1) return false;
        if (!isDismissed() && navigator.turnPage(direction)) restart();
        return true;
    }

    public void freeBackdrop() {
        frostedBackdrop.close();
    }

    @Override
    public void dispose() {
        freeBackdrop();
        super.dispose();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (holdKey == null || holdReleased || isDismissed() || GameSettings.isKeyDown(holdKey)) return;
        holdReleased = true;
        holdRelease.run();
    }

    @Override
    public Result onMousePressed(int mouseButton) {
        return press(mouseButton) ? Result.SUCCESS : Result.IGNORE;
    }

    @Override
    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
        return scroll(scrollDirection.modifier);
    }

    @Override
    public Result onKeyPressed(char typedChar, int keyCode) {
        return keyCode == Keyboard.KEY_ESCAPE && back() ? Result.SUCCESS : Result.IGNORE;
    }

    private void restart() {
        long now = Minecraft.getSystemTime();
        displayTime = now;
        animationStarted = false;
        dismissing = false;
        finished = false;
        selected = -1;
        highlightAngle = 0;
        selectedSince = now;
        lastFrameTime = now;
        mouseAngle = Float.NaN;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        drawContext = context;
        drawTheme = widgetTheme.getTheme();
        lastFrameTime = frameTime == 0 ? Minecraft.getSystemTime() : frameTime;
        frameTime = Minecraft.getSystemTime();
        if (navigator.refresh()) selected = -1;
        layout();
        if (!dismissing && !finished) updateSelection(context.getMouseX(), context.getMouseY());
        progress = 0;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_TEXTURE_BIT
                | GL11.GL_LIGHTING_BIT);
        try {
            FxShapes.prepareState();
            render();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void layout() {
        float width = getArea().w(), height = getArea().h();
        float size = fitSize(width, height, room, roomAbove, style);
        centerX = width / 2F;
        centerY = height / 2F;
        outerRadius = size * style.outerRadiusScale;
        detailScale = Math.min(1F, outerRadius / DETAIL_REFERENCE_RADIUS);
        innerRadius = Math.max(size * style.innerRadiusScale, style.minInnerRadius * detailScale);
    }

    private void updateSelection(int mouseX, int mouseY) {
        float dx = mouseX - centerX, dy = mouseY - centerY;
        float deadZone = style.deadZone * detailScale;
        boolean inside = mouseX >= 0 && mouseY >= 0 && mouseX < getArea().w() && mouseY < getArea().h();
        if (!inside || selectionBlocker.test(mouseX, mouseY) || dx * dx + dy * dy < deadZone * deadZone) {
            mouseAngle = Float.NaN;
            setSelected(-1);
            return;
        }
        mouseAngle = FxAngle.angleOf(dx, dy);
        int index = slotAt(mouseAngle, navigator.slotCount());
        setSelected(navigator.isSelectable(index) ? index : -1);
    }

    private void setSelected(int index) {
        if (selected == index) return;
        if (selected < 0 && index >= 0) highlightAngle = slotAngle(index, navigator.slotCount());
        selected = index;
        selectedSince = frameTime;
    }

    private boolean shouldRender(long now) {
        return animationStarted || displayTime + style.openDelayMs <= now;
    }

    private void render() {
        if (dismissing) {
            float remaining = (displayTime + style.closeAnimationMs - frameTime) / style.closeAnimationMs;
            if (remaining >= 1 || remaining <= 0) {
                dismissing = false;
                finished = true;
                return;
            }
            renderFrame(FxEasing.easeOutQuad(remaining), false);
            return;
        }
        if (finished || !shouldRender(frameTime)) return;
        if (!animationStarted) {
            animationStarted = true;
            displayTime = frameTime;
        }
        float delta = displayTime + style.openAnimationMs - frameTime;
        if (delta > 0)
            renderFrame(FxEasing.easeOutQuad(FxEasing.easeOutQuad(1 - delta / style.openAnimationMs)), false);
        else renderFrame(1, true);
    }

    private void renderFrame(float p, boolean fullyOpen) {
        if (p <= 0) return;
        progress = p;
        renderFrosted(p);
        FxShapes.disc(centerX, centerY, outerRadius * p, style.discColor);
        renderSeparator(p);
        if (fullyOpen) renderHoverChevron();
        if (navigator.isSelectable(selected)) renderHighlight(p);
        renderIcons(p);
        renderTitle(p);
        if (fullyOpen) renderPageArrows();
    }

    private void renderFrosted(float p) {
        float radius = outerRadius * p;
        if (!style.frosted || radius <= 0) return;
        boolean captured = frostedBackdrop.capture(centerX, centerY, radius);
        FxShapes.prepareState();
        if (captured) frostedBackdrop.disc(centerX, centerY, radius, style.frostedTint);
    }

    private void renderSeparator(float p) {
        float radius = (innerRadius + SEPARATOR_GAP * detailScale) * p;
        float half = style.separatorThickness * detailScale / 2F;
        FxShapes.ring(centerX, centerY, radius - half, radius + half, style.lineColor);
    }

    private void renderHoverChevron() {
        if (Float.isNaN(mouseAngle)) return;
        float size = HOVER_CHEVRON_SIZE * detailScale, thickness = HOVER_CHEVRON_THICKNESS * detailScale;
        float dirX = FxAngle.dirX(mouseAngle), dirY = FxAngle.dirY(mouseAngle);
        float tipDistance = innerRadius - HOVER_CHEVRON_GAP * detailScale;
        float tipX = centerX + dirX * tipDistance, tipY = centerY + dirY * tipDistance;
        float baseX = tipX - dirX * size, baseY = tipY - dirY * size;
        float wing = size * 0.8F;
        FxShapes.segment(baseX - dirY * wing, baseY + dirX * wing, tipX, tipY, thickness, style.lineColor);
        FxShapes.segment(baseX + dirY * wing, baseY - dirX * wing, tipX, tipY, thickness, style.lineColor);
    }

    private void renderHighlight(float p) {
        int slots = navigator.slotCount();
        float target = slotAngle(selected, slots);
        float frames = Math.min(MAX_FRAME_STEPS, (frameTime - lastFrameTime) / FRAME_MS);
        float follow = 1 - (float) Math.pow(1 - 1 / style.highlightLag, frames);
        highlightAngle = FxAngle
            .normalizePositive(highlightAngle + FxAngle.normalizeSigned(target - highlightAngle) * follow);
        int color = style.highlightColor;
        if (style.highlight == WheelHighlight.ANNULAR_SECTOR) {
            float settle = FxEasing.smoothStep((frameTime - selectedSince - SETTLE_DELAY_MS) / SETTLE_ANIMATION_MS);
            float expand = settle * SETTLE_EXPAND * detailScale;
            int alpha = Math.round((color >>> 24) * (0.2F + 0.8F * settle));
            FxShapes.sector(
                centerX,
                centerY,
                (innerRadius + HIGHLIGHT_INNER_INSET * detailScale + expand) * p,
                (outerRadius - HIGHLIGHT_OUTER_INSET * detailScale + expand) * p,
                highlightAngle,
                (float) Math.PI / slots,
                alpha << 24 | color & 0xFFFFFF);
            return;
        }
        float radius = iconRadius() * p;
        FxShapes.glow(
            centerX + FxAngle.dirX(highlightAngle) * radius,
            centerY + FxAngle.dirY(highlightAngle) * radius,
            ringWidth() * ICON_RATIO * p,
            color);
    }

    private void renderIcons(float p) {
        int size = Math.max(1, Math.round(ringWidth() * ICON_RATIO * p));
        int edge = Math.max(MIN_ICON_SIZE, Math.min(MAX_ICON_SIZE, size / 2));
        float radius = iconRadius() * p;
        int slots = navigator.slotCount();
        for (int i = 0; i < slots; i++) {
            IDrawable icon = navigator.entry(i)
                .icon();
            if (icon == null) continue;
            float angle = slotAngle(i, slots);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glPushMatrix();
            GL11.glTranslatef(centerX + FxAngle.dirX(angle) * radius, centerY + FxAngle.dirY(angle) * radius, 0);
            icon.draw(drawContext, -edge / 2, -edge / 2, edge, edge, drawTheme);
            GL11.glPopMatrix();
        }
        FxShapes.prepareState();
    }

    /**
     * The scale that fits every line of a title inside a box, never above the preferred scale.
     */
    public static float fitScale(float preferred, int widest, int lines, int lineHeight, float boxWidth,
        float boxHeight) {
        if (widest <= 0 || lines <= 0) return preferred;
        return Math.min(preferred, Math.min(boxWidth / widest, boxHeight / (lines * (float) lineHeight)));
    }

    private void renderTitle(float p) {
        WheelEntry entry = navigator.entry(selected);
        if (entry == null) return;
        String title = entry.label();
        int alpha = Math.round(p * 0xFF);
        if (title.isEmpty() || alpha < 4) return;
        int shadowAlpha = Math.round(p * TEXT_SHADOW_ALPHA);
        float preferred = style.titleScale * detailScale, boxWidth = 2 * innerRadius * TITLE_WIDTH,
            boxHeight = 2 * innerRadius * TITLE_HEIGHT;
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        List<String> lines = new ObjectArrayList<>();
        int widest = 0;
        for (String line : title.split("\n"))
            for (Object part : font.listFormattedStringToWidth(line, Math.max(1, (int) (boxWidth / preferred)))) {
                lines.add((String) part);
                widest = Math.max(widest, font.getStringWidth((String) part));
            }
        float scale = fitScale(preferred, widest, lines.size(), font.FONT_HEIGHT, boxWidth, boxHeight);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPushMatrix();
        GL11.glTranslatef(centerX, centerY, 0);
        GL11.glScalef(scale, scale, 1);
        int top = -lines.size() * font.FONT_HEIGHT / 2;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int x = -font.getStringWidth(line) / 2, y = top + i * font.FONT_HEIGHT;
            // Why: colour codes in the line would tint the shadow too, so the shadow is drawn from the plain text.
            if (shadowAlpha >= 4) font
                .drawString(EnumChatFormatting.getTextWithoutFormattingCodes(line), x + 1, y + 1, shadowAlpha << 24);
            font.drawString(line, x, y, alpha << 24 | style.textColor & 0xFFFFFF);
        }
        GL11.glPopMatrix();
        FxShapes.prepareState();
    }

    private void renderPageArrows() {
        int pageIndex = navigator.pageIndex();
        boolean previous = pageIndex > 0, next = pageIndex < navigator.pageCount() - 1;
        if (!previous && !next) return;
        float bob = (float) Math.sin(frameTime / 1000F * 2.2F) * 4F * detailScale;
        float corner = outerRadius * 0.7071F + PAGE_ARROW_OFFSET * detailScale;
        float y = centerY + corner;
        if (previous) pageChevron(centerX - corner + bob, y, true);
        if (next) pageChevron(centerX + corner - bob, y, false);
    }

    private void pageChevron(float x, float y, boolean left) {
        float height = PAGE_CHEVRON_SIZE * detailScale, thickness = PAGE_CHEVRON_THICKNESS * detailScale;
        float size = left ? height : -height;
        FxShapes.segment(x + size, y - height, x - size, y, thickness, style.lineColor);
        FxShapes.segment(x - size, y, x + size, y + height, thickness, style.lineColor);
    }

    private float ringWidth() {
        return outerRadius - innerRadius;
    }

    private float iconRadius() {
        return (outerRadius + innerRadius) / 2F;
    }
}
