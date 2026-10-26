package com.science.gtnl.common.yogsothoth;

import static com.gtnllib.GTNLLibContractTest.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.injection.Inject;

import com.science.gtnl.mixins.late.wirelessCraftingTerminal.MixinRandomUtilsYogTerminal;

import baubles.common.container.InventoryBaubles;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class YogSothothContracts {

    public static void run() throws Exception {
        baublesViewCasts();
        baublesViewKeepsNoPlayer();
        baublesViewForwardsToBaubles();
        craftingTerminalLookups();
    }

    private static void baublesViewCasts() {
        check(
            InventoryBaubles.class.isAssignableFrom(YogBaublesView.class),
            "the Yog baubles view survives casts to InventoryBaubles, as Witching Gadgets' Kama does on equip");
        for (Method method : InventoryBaubles.class.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) continue;
            check(declares(method), "the Yog baubles view forwards InventoryBaubles." + method.getName());
        }
    }

    private static boolean declares(Method method) {
        try {
            YogBaublesView.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (NoSuchMethodException exception) {
            return false;
        }
    }

    private static void baublesViewKeepsNoPlayer() {
        for (Field field : YogBaublesView.class.getDeclaredFields()) {
            check(
                !EntityPlayer.class.isAssignableFrom(field.getType()),
                "the Yog baubles view holds no strong player reference: " + field.getName());
            check(
                !Modifier.isStatic(field.getModifiers()) || !Map.class.isAssignableFrom(field.getType()),
                "the Yog baubles view keeps no static player cache: " + field.getName());
        }
    }

    private static void baublesViewForwardsToBaubles() throws ReflectiveOperationException {
        InventoryBaubles baubles = new InventoryBaubles(null);
        Constructor<YogBaublesView> constructor = YogBaublesView.class
            .getDeclaredConstructor(EntityPlayer.class, InventoryBaubles.class);
        constructor.setAccessible(true);
        YogBaublesView view = constructor.newInstance(null, baubles);
        check(view.stackList == baubles.stackList, "the Yog baubles view reads Baubles' own slot array");
        check(
            view.getSizeInventory() == baubles.getSizeInventory(),
            "without a live player the Yog baubles view adds no virtual slots");
        Container container = new Container() {

            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return true;
            }
        };
        view.setEventHandler(container);
        check(
            baubles.getEventHandler() == container && view.getEventHandler() == container,
            "the Yog baubles view sets the event handler on Baubles' inventory");
    }

    private static void craftingTerminalLookups() {
        Set<String> targets = new ObjectOpenHashSet<>();
        for (Method method : MixinRandomUtilsYogTerminal.class.getDeclaredMethods()) {
            Inject inject = method.getAnnotation(Inject.class);
            if (inject == null) continue;
            for (String target : inject.method()) {
                int open = target.indexOf('(');
                targets.add(open < 0 ? target : target.substring(0, open));
            }
        }
        check(
            targets.contains("getWirelessTerm"),
            "the opened Yog-Sothoth crafting terminal answers WCT's getWirelessTerm");
        check(
            targets.contains("getWirelessTermWithSlot"),
            "WCT builds its screen from getWirelessTermWithSlot, so the opened Yog-Sothoth terminal answers it too");
    }
}
