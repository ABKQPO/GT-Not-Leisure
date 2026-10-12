package com.science.gtnl.client;

import static com.science.gtnl.common.wireless.WirelessCardActions.RENAME;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.wireless.WirelessCardContainer;

import appeng.core.localization.ColorUtils;
import appeng.core.localization.GuiText;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WirelessCardRenameDialog extends Dialog<Void> {

    public final TextFieldWidget name;
    private final WirelessCardDetailsPanel owner;
    private final WirelessCardContainer container;
    private final Minecraft mc = Minecraft.getMinecraft();
    private String target;
    private long nameRevision;
    private boolean finished;

    WirelessCardRenameDialog(WirelessCardDetailsPanel owner) {
        super("wireless_card_rename");
        this.owner = owner;
        this.container = owner.container;
        this.name = owner.textField(32);
        size(260, 96);
        background(owner.getBackground());
        child(ButtonWidget.panelCloseButton());
        child(
            IKey.str(GuiText.Renamer.getLocal())
                .asWidget()
                .color(ColorUtils.guiTextColorGray.getColor())
                .pos(10, 10)
                .size(220, 18));
        child(
            name.pos(10, 34)
                .size(240, 16));
        child(dialogButton("gui.cancel", this::closeIfOpen).pos(10, 64));
        child(dialogButton("gui.done", this::submit).pos(134, 64));
    }

    private ButtonWidget<?> dialogButton(String key, Runnable action) {
        return new ButtonWidget<>().size(116, 20)
            .background(WirelessCardButton.BUTTON_TEXTURES[1])
            .hoverBackground(WirelessCardButton.BUTTON_TEXTURES[2])
            .overlay(
                IKey.lang(key)
                    .color(0xffe0e0e0)
                    .shadow(true))
            .onMousePressed(button -> {
                if (button != 0 || finished) return false;
                action.run();
                return true;
            });
    }

    @Override
    public void onOpen(ModularScreen screen) {
        NBTTagCompound data = container.display;
        target = data.getString("detailTarget");
        nameRevision = data.getLong("nameRevision");
        finished = false;
        String text = data.getBoolean("multipleNames") ? "" : data.getString("name");
        name.getStringValue()
            .setStringValue(text);
        name.setText(text);
        super.onOpen(screen);
        getContext().focus(name);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!isOpen() || isClosing()) return;
        if (finished) closeIfOpen();
        else validateTarget();
    }

    private boolean validateTarget() {
        NBTTagCompound data = container.display;
        if (data.getBoolean("detail") && data.getBoolean("renameable")
            && target.equals(data.getString("detailTarget"))
            && nameRevision == data.getLong("nameRevision")) return true;
        closeIfOpen();
        mc.thePlayer.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.stale"));
        return false;
    }

    private void submit() {
        if (finished || !owner.layoutReady() || !validateTarget()) return;
        container.sync.request(RENAME, container.display.getLong("detailId"), name.getText(), nameRevision);
        closeIfOpen();
    }

    @Override
    public boolean onKeyPressed(char character, int key) {
        if (finished) return true;
        if (key == Keyboard.KEY_ESCAPE) {
            closeIfOpen();
            return true;
        }
        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
            submit();
            return true;
        }
        return super.onKeyPressed(character, key);
    }

    @Override
    public void closeIfOpen() {
        // Retry closing after the opening animation, without allowing another submission.
        finished = true;
        super.closeIfOpen();
    }

    @Override
    public void onClose() {
        if (name.isFocused()) getContext().removeFocus();
        super.onClose();
    }
}
