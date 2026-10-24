package com.science.gtnl.client.portalgun;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.utils.Alignment;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.wheel.WheelEntry;
import com.gtnllib.gui.wheel.WheelMenuBuilder;
import com.gtnllib.gui.wheel.WheelMenuModel;
import com.gtnllib.gui.wheel.WheelOpenMode;
import com.gtnllib.gui.wheel.WheelScreen;
import com.gtnllib.gui.wheel.WheelStyle;
import com.gtnllib.gui.widget.LayoutTheme;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.gui.portalgun.PortalGunEditScreen;
import com.science.gtnl.common.gui.portalgun.PortalGunLookScreen;
import com.science.gtnl.common.item.items.PortalGun;
import com.science.gtnl.common.packet.PortalGunFavoritePacket;
import com.science.gtnl.common.packet.PortalGunFavoritePacket.Change;
import com.science.gtnl.common.packet.PortalGunSelectPacket;
import com.science.gtnl.common.portalgun.PortalGunData;
import com.science.gtnl.common.portalgun.PortalGunDestination;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PortalGunWheelScreen extends WheelScreen {

    private static final String LANG = "item.gtnl.portal_gun.wheel.";
    private static final int DETAIL_GAP = 8, DETAIL_WIDTH = 320, DETAIL_HEIGHT = 10, FIRST_ROW = 24, SECOND_ROW = 46,
        NEAR_ROW = 8, FAR_ROW = 30, BUTTON_WIDTH = 72;
    private static final int EMPTY_COLOR = 0xFF80858F, FILLED_COLOR = 0xFFFFFFFF;
    private static final float INNER_RADIUS = 0.125F, OUTER_RADIUS = 0.24F;

    private final State state;

    private PortalGunWheelScreen(KeyBinding key, State state) {
        super(menu(state), WheelOpenMode.HOLD, key);
        this.state = state;
        state.screen = this;
        keepOpenOnConfirm();
        closeOnKey(key);
        useTheme(EquipmentTheme.selected());
        LayoutTheme style = EquipmentTheme.layout();
        below(
            IKey.dynamic(this::detail)
                .alignment(Alignment.Center)
                .asWidget()
                .size(DETAIL_WIDTH, DETAIL_HEIGHT),
            DETAIL_GAP);
        int width = Layout.buttonWidth(
            style,
            BUTTON_WIDTH,
            tr("add"),
            tr("remove"),
            tr("edit"),
            tr("stay_open.on"),
            tr("stay_open.off"),
            tr("look"));
        abovePair(
            Layout.button(IKey.lang(LANG + "add"), width, style, () -> change(Change.ADD)),
            Layout.button(IKey.lang(LANG + "remove"), width, style, () -> change(Change.REMOVE)),
            FAR_ROW,
            width,
            style.spacingMedium);
        abovePair(
            Layout.button(IKey.lang(LANG + "edit"), width, style, () -> edit(state.selected)),
            Layout.button(
                IKey.dynamic(() -> tr(state.stayOpen ? "stay_open.on" : "stay_open.off")),
                width,
                style,
                this::toggleStayOpen),
            NEAR_ROW,
            width,
            style.spacingMedium);
        roomAbove(FAR_ROW + style.buttonHeight);
        String[] effects = new String[PortalGunPortalEffect.values().length];
        for (PortalGunPortalEffect effect : PortalGunPortalEffect.values())
            effects[effect.ordinal()] = effectCaption(effect);
        int wide = Layout.buttonWidth(style, 2 * width + style.spacingMedium, effects);
        below(
            Layout.button(
                IKey.dynamic(() -> effectCaption(MainConfig.client.portalGunPortalEffect)),
                wide,
                style,
                PortalGunWheelScreen::cycleEffect),
            FIRST_ROW);
        below(
            Layout.button(IKey.lang(LANG + "look"), wide, style, () -> openNext(PortalGunLookScreen::open)),
            SECOND_ROW);
        roomBelow(SECOND_ROW + style.buttonHeight);
    }

    public static void open(KeyBinding key) {
        ItemStack gun = PortalGun.held(Minecraft.getMinecraft().thePlayer);
        if (gun != null)
            new PortalGunWheelScreen(key, new State(PortalGunData.selectedSlot(gun), PortalGunData.stayOpen(gun)))
                .open();
    }

    private static WheelMenuModel menu(State state) {
        WheelMenuBuilder builder = WheelMenuBuilder.create()
            .slotsPerPage(PortalGunData.MAX_FAVORITES)
            .style(
                WheelStyle.builder()
                    .radii(INNER_RADIUS, OUTER_RADIUS, WheelStyle.DEFAULT.minInnerRadius)
                    .highlightColor(EquipmentTheme.colors().accent)
                    .build());
        for (int slot = 0; slot < PortalGunData.MAX_FAVORITES; slot++) {
            int index = slot;
            builder.entry(
                WheelEntry.action("favorite_" + index, "", icon(index, state), context -> state.screen.choose(index))
                    .labelFrom(() -> label(index))
                    .onRightClick(context -> state.screen.edit(index)));
        }
        return builder.build();
    }

    private static DynamicDrawable icon(int slot, State state) {
        return new DynamicDrawable(() -> {
            PortalGunDestination favorite = favorite(slot);
            IKey number = IKey.str(String.valueOf(slot + 1))
                .color(
                    slot == state.selected ? EquipmentTheme.colors().accent
                        : favorite != null ? FILLED_COLOR : EMPTY_COLOR);
            IDrawable item = favorite == null || favorite.icon() == null ? IDrawable.EMPTY
                : state.icon(favorite.icon());
            if (item == IDrawable.EMPTY) return number;
            return (context, x, y, width, height, theme) -> {
                item.draw(context, x, y, width, height, theme);
                number.draw(context, x + width / 2, y + height / 2, width / 2, height / 2, theme);
            };
        });
    }

    private static PortalGunDestination favorite(int slot) {
        ItemStack gun = PortalGun.held(Minecraft.getMinecraft().thePlayer);
        return gun == null ? null : PortalGunData.favorite(gun, slot);
    }

    private static String label(int slot) {
        PortalGunDestination favorite = favorite(slot);
        return favorite == null
            ? StatCollector.translateToLocalFormatted(LANG + "empty", slot + 1) + "\n" + tr("empty_hint")
            : favorite.shownName() + "\n" + favorite.dimensionLabel() + "\n" + coordinates(favorite);
    }

    private static String coordinates(PortalGunDestination favorite) {
        return EnumChatFormatting.RED + String.valueOf(MathHelper.floor_double(favorite.x()))
            + EnumChatFormatting.RESET
            + ", "
            + EnumChatFormatting.GREEN
            + MathHelper.floor_double(favorite.y())
            + EnumChatFormatting.RESET
            + ", "
            + EnumChatFormatting.BLUE
            + MathHelper.floor_double(favorite.z());
    }

    private String detail() {
        PortalGunDestination favorite = favorite(state.selected);
        return favorite == null ? StatCollector.translateToLocalFormatted(LANG + "selected_empty", state.selected + 1)
            : StatCollector.translateToLocalFormatted(LANG + "selected", favorite.describe());
    }

    private static String tr(String key) {
        return StatCollector.translateToLocal(LANG + key);
    }

    private void choose(int slot) {
        boolean saved = favorite(slot) != null;
        state.selected = slot;
        send(true);
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        if (saved) dismiss();
    }

    private void edit(int slot) {
        if (favorite(slot) != null) openNext(() -> PortalGunEditScreen.open(slot));
    }

    private void change(Change change) {
        ScienceNotLeisure.network.sendToServer(new PortalGunFavoritePacket(state.selected, change));
    }

    private static String effectCaption(PortalGunPortalEffect effect) {
        return StatCollector
            .translateToLocalFormatted(LANG + "effect", StatCollector.translateToLocal(effect.langKey()));
    }

    private static void cycleEffect() {
        MainConfig.client.portalGunPortalEffect = MainConfig.client.portalGunPortalEffect.next();
        ConfigurationManager.save(MainConfig.class);
    }

    private void toggleStayOpen() {
        state.stayOpen = !state.stayOpen;
        send(false);
    }

    private void send(boolean clicked) {
        ScienceNotLeisure.network.sendToServer(new PortalGunSelectPacket(state.selected, state.stayOpen, clicked));
    }

    @Override
    protected void handleHoldRelease() {
        if (!state.stayOpen) super.handleHoldRelease();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (PortalGun.held(Minecraft.getMinecraft().thePlayer) == null) close();
    }

    private static class State {

        private int selected;
        private boolean stayOpen;
        private PortalGunWheelScreen screen;
        private final Map<ItemId, IDrawable> icons = new HashMap<>();

        private State(int selected, boolean stayOpen) {
            this.selected = selected;
            this.stayOpen = stayOpen;
        }

        private IDrawable icon(ItemId id) {
            return icons.computeIfAbsent(id, key -> new ItemDrawable(key.toStack()));
        }
    }
}
