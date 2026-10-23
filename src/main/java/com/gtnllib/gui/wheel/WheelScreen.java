package com.gtnllib.gui.wheel;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.RecipeViewerSettingsImpl;
import com.cleanroommc.modularui.widget.Widget;
import com.gtnllib.GTNLLib;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WheelScreen extends ModularScreen {

    private static boolean closerRegistered;

    private final WheelWidget widget;
    private final List<Widget<?>> extras = new ArrayList<>();
    private Runnable next;

    protected WheelScreen(WheelMenuModel model, WheelOpenMode openMode, KeyBinding holdKey) {
        this(new WheelWidget(model, openMode), holdKey);
    }

    private WheelScreen(WheelWidget widget, KeyBinding holdKey) {
        super(GTNLLib.MODID, panel(widget));
        this.widget = widget;
        drawDarkBackground(false);
        widget.dismissOnConfirm(true)
            .selectionBlocker((x, y) -> hoveredExtra() != null);
        if (holdKey != null) widget.holdKey(holdKey)
            .onHoldRelease(this::handleHoldRelease);
    }

    private static ModularPanel panel(WheelWidget widget) {
        ModularPanel panel = new ModularPanel("wheel") {

            @Override
            public boolean shouldAnimate() {
                return false;
            }
        };
        return panel.full()
            .disableThemeBackground(true)
            .disableHoverBackground()
            .child(widget.full());
    }

    public static WheelScreen tap(WheelMenuModel model) {
        return new WheelScreen(model, WheelOpenMode.TAP, null);
    }

    public static WheelScreen hold(WheelMenuModel model, KeyBinding holdKey) {
        return new WheelScreen(model, WheelOpenMode.HOLD, holdKey);
    }

    public void open() {
        if (!closerRegistered) {
            closerRegistered = true;
            FMLCommonHandler.instance()
                .bus()
                .register(new Closer());
        }
        RecipeViewerSettingsImpl recipeViewer = new RecipeViewerSettingsImpl();
        recipeViewer.disable();
        ClientGUI.open(this, recipeViewer);
    }

    public WheelScreen below(Widget<?> extra, int offset) {
        return below(extra, offset, 0);
    }

    public WheelScreen below(Widget<?> extra, int offset, int shift) {
        extra.leftRelOffset(0.5F, shift)
            .topRel(this::belowWheel, offset, 0)
            .setEnabledIf(w -> widget.progress() > 0);
        extras.add(extra);
        getMainPanel().child(extra);
        return this;
    }

    private double belowWheel() {
        float width = getMainPanel().getArea()
            .w(),
            height = getMainPanel().getArea()
                .h();
        if (height <= 0) return 0;
        return (height / 2D + Math.min(width, height) * widget.navigator()
            .model()
            .style().outerRadiusScale) / height;
    }

    protected Widget<?> hoveredExtra() {
        for (Widget<?> extra : extras) if (extra.isEnabled() && extra.isHovering()) return extra;
        return null;
    }

    protected void handleHoldRelease() {
        if (hoveredExtra() instanceof Interactable interactable) interactable.onMousePressed(0);
        else widget.confirm();
    }

    public void closeIfFinished() {
        if (widget.isFinished() && isActive()) close();
    }

    public void openNext(Runnable open) {
        if (next == null) next = open;
    }

    private void afterFrame() {
        Runnable open = next;
        next = null;
        if (open != null) open.run();
        else closeIfFinished();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        closeIfFinished();
    }

    @Override
    public void onClose() {
        widget.freeBackdrop();
    }

    @Override
    public boolean onMousePressed(int mouseButton) {
        if (hoveredExtra() != null) return super.onMousePressed(mouseButton);
        return widget.press(mouseButton) || super.onMousePressed(mouseButton);
    }

    @Override
    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
        widget.scroll(scrollDirection.modifier);
        return true;
    }

    @Override
    public boolean onKeyPressed(char typedChar, int keyCode) {
        if (keyCode != Keyboard.KEY_ESCAPE) return super.onKeyPressed(typedChar, keyCode);
        widget.back();
        return true;
    }

    public static class Closer {

        @SubscribeEvent
        public void afterFrame(TickEvent.RenderTickEvent event) {
            if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().currentScreen instanceof IMuiScreen mui
                && mui.getScreen() instanceof WheelScreen wheel) wheel.afterFrame();
        }
    }
}
