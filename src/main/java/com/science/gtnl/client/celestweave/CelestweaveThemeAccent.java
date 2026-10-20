package com.science.gtnl.client.celestweave;

import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.gui.modularui.theme.LivePalette;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveThemeAccent {

    private static final int OPAQUE = 0xFF000000, RGB = 0xFFFFFF;
    private static ModularPanel editedPanel;
    private static IntSupplier editedGlow;

    private CelestweaveThemeAccent() {}

    public static void register() {
        EquipmentTheme.LIVE.accentFrom(CelestweaveThemeAccent::accent);
    }

    public static void editing(ModularPanel panel, IntSupplier glow) {
        editedPanel = panel;
        editedGlow = glow;
        EquipmentTheme.LIVE.refresh();
    }

    public static int accent() {
        if (editedPanel != null && editedPanel.isOpen()) return OPAQUE | editedGlow.getAsInt() & RGB;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        return accent(player == null ? null : CelestweaveData.piece(player, CelestweaveArmor.CHEST));
    }

    public static int accent(ItemStack chest) {
        if (chest == null) return LivePalette.NO_ACCENT;
        return OPAQUE | CelestweaveData.cosmetic(chest)
            .getInt(CelestweaveCosmeticOption.GLOW_COLOR) & RGB;
    }
}
