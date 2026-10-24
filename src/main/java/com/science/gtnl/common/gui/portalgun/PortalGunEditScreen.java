package com.science.gtnl.common.gui.portalgun;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.layout.Size;
import com.gtnllib.gui.settings.BlockPosFields;
import com.gtnllib.gui.widget.CloseButton;
import com.gtnllib.gui.widget.GhostItemSlot;
import com.gtnllib.gui.widget.LayoutTheme;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.item.items.PortalGun;
import com.science.gtnl.common.packet.PortalGunFavoritePacket;
import com.science.gtnl.common.packet.PortalGunFavoritePacket.Change;
import com.science.gtnl.common.portalgun.PortalGunData;
import com.science.gtnl.common.portalgun.PortalGunDestination;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class PortalGunEditScreen extends ModularScreen {

    private static final String LANG = "item.gtnl.portal_gun.edit.";
    private static final int WIDTH = 240, TEXT_HEIGHT = 10;

    private PortalGunEditScreen(int slot, PortalGunDestination saved) {
        super(ScienceNotLeisure.MODID, panel(slot, saved));
        useTheme(EquipmentTheme.selected());
    }

    public static void open(int slot) {
        ItemStack gun = PortalGun.held(Minecraft.getMinecraft().thePlayer);
        PortalGunDestination saved = gun == null || !PortalGunData.validSlot(slot) ? null
            : PortalGunData.favorite(gun, slot);
        if (saved != null) GhostItemSlot.open(new PortalGunEditScreen(slot, saved));
    }

    private static ModularPanel panel(int slot, PortalGunDestination saved) {
        LayoutTheme style = EquipmentTheme.layout();
        int pad = style.padding, inner = WIDTH - pad * 2, gap = style.spacingMedium;
        Layout rows = Layout.rows(style, inner)
            .inset(0);
        String[] captions = { tr("name"), tr("position"), tr("dimension"), tr("icon") };
        int captionWidth = 0;
        for (String caption : captions) captionWidth = Math.max(captionWidth, Layout.textWidth(caption, style));
        Size caption = Size.fixed(captionWidth);

        TextFieldWidget name = new TextFieldWidget().setMaxLength(PortalGunData.MAX_NAME_LENGTH)
            .value(new StringValue(saved.name()));
        BlockPosFields place = new BlockPosFields(
            saved.x(),
            saved.y(),
            saved.z(),
            PortalGunData.MAX_HORIZONTAL,
            PortalGunData.MIN_HEIGHT,
            PortalGunData.MAX_HEIGHT);
        List<Size> placeSizes = new ObjectArrayList<>(List.of(caption));
        List<Widget<?>> placeCells = new ObjectArrayList<>(List.of(label(captions[1], style)));
        place.addTo(placeSizes, placeCells, style);
        GhostItemSlot icon = new GhostItemSlot().item(saved.icon());
        String clear = tr("icon_clear");

        int iconRow = Math.max(style.rowHeight, GhostItemSlot.SIZE), top = pad + TEXT_HEIGHT + style.spacingLarge;
        int content = style.rowHeight * 4 + iconRow;
        ModularPanel panel = new ModularPanel("portal_gun_edit").size(WIDTH, top + content + pad);
        List<IWidget> lines = Arrays.asList(
            rows.line(gap, List.of(caption, Size.fill()), List.of(label(captions[0], style), name)),
            rows.line(gap, placeSizes, placeCells),
            rows.line(
                gap,
                List.of(caption, Size.fill()),
                List.of(
                    label(captions[2], style),
                    Layout.label(IKey.str(saved.dimensionLabel()), style)
                        .color(EquipmentTheme.colors().hint))),
            rows.line(
                gap,
                List.of(
                    caption,
                    Size.fixed(GhostItemSlot.SIZE),
                    Size.content(Layout.buttonWidth(style, GhostItemSlot.SIZE * 2, clear)),
                    Size.fill()),
                List.of(
                    label(captions[3], style),
                    icon,
                    Layout.button(IKey.str(clear), 0, style, icon::clear),
                    Layout.label(IKey.lang(LANG + "icon_hint"), style)
                        .color(EquipmentTheme.colors().hint)))
                .height(iconRow),
            rows.line(
                gap,
                List.of(Size.fill(), Size.fill()),
                List.of(Layout.button(IKey.lang(LANG + "save"), 0, style, () -> {
                    ScienceNotLeisure.network.sendToServer(
                        new PortalGunFavoritePacket(
                            slot,
                            Change.EDIT,
                            name.getText(),
                            place.x(),
                            place.y(),
                            place.z(),
                            icon.item()));
                    panel.closeIfOpen();
                }), Layout.button(IKey.lang(LANG + "cancel"), 0, style, panel::closeIfOpen))));
        panel.child(
            Layout.label(IKey.str(StatCollector.translateToLocalFormatted(LANG + "title", slot + 1)), style)
                .pos(pad, pad)
                .size(inner - CloseButton.SIZE - gap, TEXT_HEIGHT));
        panel.child(new CloseButton().corner(pad, style));
        panel.child(
            Flow.column()
                .pos(pad, top)
                .size(inner, content)
                .children(lines));
        return panel;
    }

    private static Widget<?> label(String text, LayoutTheme style) {
        return Layout.label(IKey.str(text), style);
    }

    private static String tr(String key) {
        return StatCollector.translateToLocal(LANG + key);
    }
}
