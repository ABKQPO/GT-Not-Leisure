package com.science.gtnl.client.galacticraft;

import java.util.List;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.entity.EntitySteamRocket;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

@SideOnly(Side.CLIENT)
public final class SteamRocketClientHooks {

    private SteamRocketClientHooks() {}

    public static EnumRarity rarity() {
        return ClientProxyCore.galacticraftItem;
    }

    public static void addTooltip(ItemStack itemStack, List<String> toolTip) {
        final EnumRocketType type = EnumRocketType.values()[itemStack.getItemDamage()];

        if (!type.getTooltip()
            .isEmpty()) {
            toolTip.add(type.getTooltip());
        }

        if (type.getPreFueled()) {
            toolTip.add(EnumColor.RED + "§o" + GCCoreUtil.translate("gui.creativeOnly.desc"));
        }

        if (itemStack.hasTagCompound() && itemStack.getTagCompound()
            .hasKey("RocketFuel")) {
            final EntitySteamRocket rocket = new EntitySteamRocket(
                FMLClientHandler.instance()
                    .getWorldClient(),
                0,
                0,
                0,
                EnumRocketType.values()[itemStack.getItemDamage()]);
            toolTip.add(
                GCCoreUtil.translate("gui.message.fuel.name") + ": "
                    + itemStack.getTagCompound()
                        .getInteger("RocketFuel")
                    + " / "
                    + rocket.fuelTank.getCapacity());
        }

        toolTip.add(StatCollector.translateToLocal("item.gtnl.steam_rocket.tooltip.0"));
        toolTip.add("§o" + StatCollector.translateToLocal("item.gtnl.steam_rocket.tooltip.1"));

    }

    public static boolean onPlacement(EntityPlayer player) {
        if (!(player instanceof EntityPlayerSP localPlayer)) return false;
        ClientProxyCore.playerClientHandler.onBuild(8, localPlayer);
        return true;
    }
}
