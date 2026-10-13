package com.science.gtnl.client.metoolbox;

import java.util.function.Consumer;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.widget.IFocusedWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.api.widget.ModernInteractable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;

import appeng.client.gui.ScreenColor;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.core.localization.ButtonToolTips;
import appeng.integration.modules.NEI;
import cpw.mods.fml.common.Optional;
import me.eigenraven.lwjgl3ify.api.InputEvents;

final class ToolboxSearchField extends Widget<ToolboxSearchField>
    implements Interactable, IFocusedWidget, ModernInteractable {

    private static final UITexture BACKGROUND = UITexture.builder()
        .location("appliedenergistics2", "guis/terminal")
        .imageSize(256, 256)
        .xy(80, 4, 90, 12)
        .adaptable(1)
        .build();

    private final NativeField field;

    ToolboxSearchField(Consumer<String> onChange) {
        field = new NativeField(onChange);
        field.setMaxStringLength(128);
        NEI.searchField.putFormatter(field);
        invisible();
        tooltipBuilder(tooltip -> tooltip.addLine(field.getMessage()));
    }

    void setText(String text) {
        if (text.equals(field.getText())) return;
        field.setText(text, true);
        field.setCursorPositionEnd();
    }

    MEGuiTextField nativeField() {
        return field;
    }

    void applySearch(String text) {
        field.setText(text);
        field.setCursorPositionEnd();
    }

    @Override
    public void onResized() {
        super.onResized();
        field.resize(getArea().width, getArea().height);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        ScreenColor.setGuiColor();
        BACKGROUND.draw(0, 0, getArea().width, getArea().height);
        ScreenColor.resetGuiColor();
        field.drawTextBox();
        ScreenColor.resetGuiColor();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        field.tickCursor();
    }

    @Override
    public Result onMousePressed(int button) {
        if (!isHovering()) return Result.IGNORE;
        getContext().focus(this);
        field.mouseClicked(getContext().getMouseX(), getContext().getMouseY(), button);
        return Result.SUCCESS;
    }

    @Override
    public Result onKeyPressed(char character, int keyCode) {
        if (!isFocused()) return Result.IGNORE;

        if (ModularUI.Mods.LWJGL3IFY.isLoaded()) {
            if (Interactable.isKeyComboCtrlA(keyCode)) character = 1;
            else if (Interactable.isKeyComboCtrlC(keyCode)) character = 3;
            else if (Interactable.isKeyComboCtrlV(keyCode)) character = 22;
            else if (Interactable.isKeyComboCtrlX(keyCode)) character = 24;
            else character = 0;
        }
        field.textboxKeyTyped(character, keyCode);

        if (!field.isFocused()) getContext().removeFocus();
        return Result.SUCCESS;
    }

    @Override
    @Optional.Method(modid = ModularUI.ModIds.LWJGL3IFY)
    public Result onKeyEvent(InputEvents.KeyEvent event) {

        return isFocused() ? Result.SUCCESS : Result.IGNORE;
    }

    @Override
    @Optional.Method(modid = ModularUI.ModIds.LWJGL3IFY)
    public boolean onTextInput(InputEvents.TextEvent event) {
        if (!isFocused()) return false;
        field.insertText(event.text);
        return true;
    }

    @Override
    public boolean isFocused() {
        return isValid() && getContext().isFocused(this);
    }

    @Override
    public void onFocus(ModularGuiContext context) {
        field.setFocused(true);
        if (ModularUI.Mods.LWJGL3IFY.isLoaded()) InputEvents.beginTextInput();
    }

    @Override
    public void onRemoveFocus(ModularGuiContext context) {
        field.setFocused(false);
        if (ModularUI.Mods.LWJGL3IFY.isLoaded()) InputEvents.endTextInput();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) releaseFocus();
        super.setEnabled(enabled);
    }

    @Override
    public void dispose() {
        releaseFocus();
        super.dispose();
    }

    private void releaseFocus() {
        if (isFocused()) getContext().removeFocus();
        else field.setFocused(false);
    }

    private static final class NativeField extends MEGuiTextField {

        private final Consumer<String> onChange;

        NativeField(Consumer<String> onChange) {
            super(ToolboxLayout.SEARCH_WIDTH, ToolboxLayout.HEADER_SIZE, ButtonToolTips.SearchStringTooltip.getLocal());
            this.onChange = onChange;
        }

        @Override
        public void onTextChange(String oldText) {
            if (!oldText.equals(getText())) onChange.accept(getText());
        }

        void resize(int width, int height) {
            if (w == width && h == height) return;
            w = width;
            h = height;
            setDimensionsAndColor();
            field.setSelectionPos(field.getSelectionEnd());
        }

        void tickCursor() {
            field.updateCursorCounter();
        }

        void insertText(String text) {
            String oldText = getText();

            field.writeText(text);
            onTextChange(oldText);
        }
    }
}
