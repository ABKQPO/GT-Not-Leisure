package com.science.gtnl.common.item.items.armor;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.text.effect.TextEffects;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MajoHat extends MajoArmor {

    public MajoHat() {
        super("majo_hat", 0, 10, GTNLItemList.MajoHat);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        for (int line = 0; line < 4; line++) {
            tooltip.add(
                TextEffects.apply(
                    StatCollector.translateToLocal("item.gtnl.majo_hat.lore." + line),
                    TextEffects.GENESIS_COMPONENT_RARITY_SHADER));
        }
        tooltip.add("");
        tooltip.add(
            TextEffects.apply(StatCollector.translateToLocal("item.gtnl.majo_hat.worn"), TextEffects.EVERCOLD_CYAN));
        for (int line = 0; line < 4; line++) {
            tooltip.add(
                TextEffects.apply(
                    StatCollector.translateToLocal("item.gtnl.majo_hat.tooltip." + line),
                    TextEffects.EVERCOLD_CYAN));
        }
        tooltip.add(TextEffects.apply(getVisDiscountTooltipText(), TextEffects.EVERCOLD_CYAN));
    }
}
