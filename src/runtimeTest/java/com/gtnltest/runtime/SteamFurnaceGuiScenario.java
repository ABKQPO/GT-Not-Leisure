package com.gtnltest.runtime;

import static com.gtnltest.runtime.GuiTestSupport.find;
import static com.gtnltest.runtime.RuntimeReport.require;

import java.util.concurrent.CompletableFuture;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Exercises live widget callbacks and native C2S synchronization, without claiming physical input. */
@SideOnly(Side.CLIENT)
final class SteamFurnaceGuiScenario implements GuiInteractionScenario {

    private enum Step {
        OPEN,
        WAIT_OPEN,
        WAIT_VALUE,
        WAIT_LIMIT,
        WAIT_RESTORE,
        WAIT_CLOSE
    }

    private static final String PANEL = "gtnl_steam_recipe_oc";
    private final Supplier<CompletableFuture<Integer>> readServerValue;
    private final ScreenshotCapture screenshot;
    private final IntSupplier renderedFrames;
    private Step step = Step.OPEN;
    private int ticks, openingFrame, original, lastServerValue = -1;
    private CompletableFuture<Integer> pending;
    private ButtonWidget<?> button;
    private TextFieldWidget field;

    SteamFurnaceGuiScenario(Supplier<CompletableFuture<Integer>> readServerValue, ScreenshotCapture screenshot,
        IntSupplier renderedFrames) {
        this.readServerValue = readServerValue;
        this.screenshot = screenshot;
        this.renderedFrames = renderedFrames;
    }

    @Override
    public boolean tick(ModularScreen screen, RuntimeReport.Result result) throws Exception {
        require(screen != null, "steam interaction requires its live MUI2 screen");
        ticks++;
        switch (step) {
            case OPEN -> {
                original = screen.getSyncManager()
                    .getMainPSM()
                    .findSyncHandler("gtnlSteamRecipeOcCount", IntSyncValue.class)
                    .getIntValue();
                button = (ButtonWidget<?>) find(screen.getMainPanel(), "gtnlSteamRecipeOcButton");
                require(button != null, "steam overclock button is missing");
                button.onMousePressed(0);
                openingFrame = renderedFrames.getAsInt();
                advance(Step.WAIT_OPEN);
            }
            case WAIT_OPEN -> {
                ModularPanel panel = screen.getPanelManager()
                    .getOpenPanel(PANEL);
                if (panel == null || ticks < 12 || renderedFrames.getAsInt() - openingFrame < 4) return false;
                var area = panel.getArea();
                var viewport = screen.getScreenArea();
                require(
                    area.width > 0 && area.height > 0
                        && area.x >= 0
                        && area.y >= 0
                        && area.x + area.width <= viewport.width
                        && area.y + area.height <= viewport.height,
                    "steam overclock popup is clipped or empty");
                result.additionalScreenshots.add(screenshot.capture("overclock"));
                field = (TextFieldWidget) find(panel, "gtnlSteamRecipeOcField");
                require(field != null, "steam overclock text field is missing");
                commit(screen, "2");
                advance(Step.WAIT_VALUE);
            }
            case WAIT_VALUE -> {
                if (!serverHas(2) || !"2".equals(field.getText())) return false;
                result.checks.add("steam.overclock.client-server");
                commit(screen, "99");
                advance(Step.WAIT_LIMIT);
            }
            case WAIT_LIMIT -> {
                if (!serverHas(4) || !"4".equals(field.getText())) return false;
                result.checks.add("steam.overclock.range");
                commit(screen, Integer.toString(original));
                advance(Step.WAIT_RESTORE);
            }
            case WAIT_RESTORE -> {
                if (!serverHas(original) || !Integer.toString(original)
                    .equals(field.getText())) return false;
                result.checks.add("steam.overclock.restore");
                button.onMousePressed(0);
                advance(Step.WAIT_CLOSE);
            }
            case WAIT_CLOSE -> {
                if (screen.getPanelManager()
                    .getOpenPanel(PANEL) != null) return false;
                result.checks.add("steam.overclock.popup-lifecycle");
                return true;
            }
        }
        return false;
    }

    private void commit(ModularScreen screen, String text) {
        field.setText(text);
        field.onRemoveFocus(screen.getContext());
        pending = readServerValue.get();
    }

    private boolean serverHas(int expected) {
        if (!pending.isDone()) return false;
        lastServerValue = pending.join();
        if (lastServerValue == expected) return true;
        pending = readServerValue.get();
        return false;
    }

    private void advance(Step next) {
        step = next;
        ticks = 0;
    }

    @Override
    public String progress() {
        return step + ", field=" + (field == null ? "<none>" : field.getText()) + ", server=" + lastServerValue;
    }

}
