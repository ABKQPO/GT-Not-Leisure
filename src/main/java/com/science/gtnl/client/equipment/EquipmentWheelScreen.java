package com.science.gtnl.client.equipment;

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
import com.science.gtnl.client.celestweave.CelestweaveClientEvents;
import com.science.gtnl.common.equipment.IEquipmentItem;
import com.science.gtnl.common.gui.equipment.EquipmentGui;
import com.science.gtnl.common.gui.equipment.EquipmentSoundScreen;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnl.common.packet.OpenEquipmentGuiPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

@SideOnly(Side.CLIENT)
public class EquipmentWheelScreen extends WheelScreen {

    private static final String LANG = "item.gtnl.equipment.wheel.";
    private static final int TITLE_GAP = 8, MIN_SLOTS = 4, MAX_SLOTS = 8;
    private static final int BUTTON_WIDTH = 72, BUTTON_GAP = 22;
    private static final String HUD = HudEditor.OPEN_LANG, SOUND = LANG + "sound", HIDE_ARMOR = LANG + "hide_armor",
        SHOW_ARMOR = LANG + "show_armor";

    private final int[] slots;
    private boolean concealSent;

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
        if (hasCelestweave(slots)) concealButton(style, width);
    }

    private static boolean hasCelestweave(int[] slots) {
        for (int slot : slots) {
            ItemStack stack = stackAt(slot);
            if (stack != null && stack.getItem() instanceof CelestweaveArmor) return true;
        }
        return false;
    }

    private void concealButton(LayoutTheme style, int pairWidth) {
        int width = Layout.buttonWidth(
            style,
            2 * pairWidth + style.spacingMedium,
            EquipmentGui.tr(HIDE_ARMOR),
            EquipmentGui.tr(SHOW_ARMOR));
        int row = BUTTON_GAP + style.buttonHeight + style.spacingMedium;
        below(
            Layout
                .button(
                    IKey.dynamic(() -> EquipmentGui.tr(concealed() ? SHOW_ARMOR : HIDE_ARMOR)),
                    width,
                    style,
                    this::toggleConceal)
                .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(HIDE_ARMOR + ".tooltip"))),
            row);
        roomBelow(row + style.buttonHeight);
    }

    private void toggleConceal() {
        // In hold mode a mouse click and the key release both press this button.
        if (concealSent) return;
        concealSent = true;
        ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.CONCEAL));
        dismiss();
    }

    private static boolean concealed() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        return player != null && CelestweaveClientEvents.concealed(player);
    }

    public static int[] equipmentSlots(EntityPlayer player) {
        if (player == null) return new int[0];
        InventoryPlayer inventory = player.inventory;
        IntArrayList found = new IntArrayList();
        int armorStart = inventory.mainInventory.length;
        for (int slot = inventory.getSizeInventory() - 1; slot >= armorStart; slot--) add(found, inventory, slot);
        add(found, inventory, inventory.currentItem);
        for (int slot = 0; slot < armorStart; slot++) add(found, inventory, slot);
        return found.toIntArray();
    }

    private static void add(IntList found, InventoryPlayer inventory, int slot) {
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
