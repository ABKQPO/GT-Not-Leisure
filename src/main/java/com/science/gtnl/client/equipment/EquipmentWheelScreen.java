package com.science.gtnl.client.equipment;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.gtnllib.client.hud.HudEditor;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.wheel.WheelEntry;
import com.gtnllib.gui.wheel.WheelMenuBuilder;
import com.gtnllib.gui.wheel.WheelMenuModel;
import com.gtnllib.gui.wheel.WheelOpenMode;
import com.gtnllib.gui.wheel.WheelScreen;
import com.gtnllib.gui.wheel.WheelStyle;
import com.gtnllib.gui.widget.LayoutTheme;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.IEquipmentItem;
import com.science.gtnl.common.gui.equipment.EquipmentGui;
import com.science.gtnl.common.gui.equipment.EquipmentSoundScreen;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.packet.OpenEquipmentGuiPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EquipmentWheelScreen extends WheelScreen {

    private static final String LANG = "item.gtnl.equipment.wheel.";
    private static final int TITLE_GAP = 8, MIN_SLOTS = 4, MAX_SLOTS = 8;
    private static final int BUTTON_WIDTH = 72, BUTTON_GAP = 22;
    private static final String HUD = HudEditor.OPEN_LANG, SOUND = LANG + "sound";

    private final int[] slots;

    public EquipmentWheelScreen(KeyBinding key, int[] slots) {
        super(menu(slots), WheelOpenMode.HOLD, key);
        this.slots = slots;
        useTheme(EquipmentTheme.selected());
        below(
            IKey.str(EquipmentGui.tr(LANG + "title"))
                .asWidget(),
            TITLE_GAP);
        LayoutTheme style = EquipmentTheme.layout();
        int width = Layout.buttonWidth(style, BUTTON_WIDTH, EquipmentGui.tr(HUD), EquipmentGui.tr(SOUND));
        belowPair(
            button(HUD, HudEditor::open, width, style),
            button(SOUND, EquipmentSoundScreen::open, width, style),
            BUTTON_GAP,
            width,
            style.spacingMedium);
    }

    public static int[] equipmentSlots(EntityPlayer player) {
        if (player == null) return new int[0];
        InventoryPlayer inventory = player.inventory;
        List<Integer> found = new ArrayList<>();
        int armorStart = inventory.mainInventory.length;
        for (int slot = inventory.getSizeInventory() - 1; slot >= armorStart; slot--) add(found, inventory, slot);
        add(found, inventory, inventory.currentItem);
        for (int slot = 0; slot < armorStart; slot++) add(found, inventory, slot);
        int[] slots = new int[found.size()];
        for (int i = 0; i < slots.length; i++) slots[i] = found.get(i);
        return slots;
    }

    private static void add(List<Integer> found, InventoryPlayer inventory, int slot) {
        if (equipment(inventory.getStackInSlot(slot)) && !found.contains(slot)) found.add(slot);
    }

    private static boolean equipment(ItemStack stack) {
        return stack != null && stack.getItem() instanceof IEquipmentItem;
    }

    private static ItemStack stackAt(int slot) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        return player == null ? null : player.inventory.getStackInSlot(slot);
    }

    private static boolean present(int slot) {
        return equipment(stackAt(slot));
    }

    private static WheelMenuModel menu(int[] slots) {
        WheelMenuBuilder builder = WheelMenuBuilder.create()
            .slotsPerPage(Math.max(MIN_SLOTS, Math.min(MAX_SLOTS, slots.length)))
            .style(
                WheelStyle.builder()
                    .highlightColor(EquipmentTheme.colors().accent)
                    .build());
        for (int slot : slots) builder.entry(
            WheelEntry.action("slot_" + slot, stackAt(slot).getDisplayName(), icon(slot), context -> open(slot))
                .visibleWhen(() -> present(slot)));
        return builder.build();
    }

    private static DynamicDrawable icon(int slot) {
        ItemStack[] shown = new ItemStack[1];
        ItemDrawable[] drawable = new ItemDrawable[1];
        return new DynamicDrawable(() -> {
            ItemStack stack = stackAt(slot);
            if (drawable[0] == null || stack != shown[0]) {
                shown[0] = stack;
                drawable[0] = new ItemDrawable(stack);
            }
            return drawable[0];
        });
    }

    private static void open(int slot) {
        if (present(slot)) ScienceNotLeisure.network.sendToServer(new OpenEquipmentGuiPacket(slot));
    }

    private ButtonWidget<?> button(String lang, Runnable open, int width, LayoutTheme style) {
        return Layout.button(IKey.str(EquipmentGui.tr(lang)), width, style, () -> openNext(open));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        for (int slot : slots) if (present(slot)) return;
        close();
    }
}
