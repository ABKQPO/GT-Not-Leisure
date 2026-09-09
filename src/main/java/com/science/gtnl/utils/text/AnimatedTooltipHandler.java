package com.science.gtnl.utils.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.util.map.ItemStackMap;
import com.science.gtnl.common.item.items.Stick;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
public class AnimatedTooltipHandler {

    public static Map<ItemStack, List<Supplier<String>>> TOOLTIP_MAP = new ItemStackMap<>(false);
    public static Map<ItemStack, List<Supplier<String>>> TOOLTIP_MAP_SHIFT = new ItemStackMap<>(false);
    public static Map<ItemStack, List<Supplier<String>>> TOOLTIP_MAP_CTRL = new ItemStackMap<>(false);

    public static final String BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY,
        BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET;

    public static final Supplier<String> NEW_LINE;

    @SafeVarargs
    public static Supplier<String> buildTextWithAnimatedEnd(Supplier<String>... texts) {
        if (texts == null || texts.length == 0) {
            throw new IllegalArgumentException("Required at least one text!");
        }
        Supplier<String> prefixText = chain(Arrays.copyOf(texts, texts.length - 1));
        Supplier<String> lastText = texts[texts.length - 1];
        Supplier<String> animatedText = animatedText(
            lastText.get(),
            1,
            80,
            RED,
            GOLD,
            YELLOW,
            GREEN,
            AQUA,
            BLUE,
            LIGHT_PURPLE);
        return chain(prefixText, animatedText);
    }

    @SafeVarargs
    public static Supplier<String> chain(Supplier<String>... parts) {
        return () -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (Supplier<String> text : parts) {
                stringBuilder.append(text.get());
            }
            return stringBuilder.toString();
        };
    }

    public static Supplier<String> text(String text) {
        return () -> text;
    }

    public static Supplier<String> text(String format, Object... args) {
        return () -> String.format(Locale.ROOT, format, args);
    }

    public static Supplier<String> translatedText(String translationKey) {
        return () -> StatCollector.translateToLocal(translationKey);
    }

    public static Supplier<String> translatedText(String translationKey, Object... args) {
        return () -> StatCollector.translateToLocalFormatted(translationKey, args);
    }

    public static Supplier<String> animatedText(String text, int posstep, int delay, String... formattingArray) {
        if (text == null || text.isEmpty() || formattingArray == null || formattingArray.length == 0) return () -> "";

        final int finalDelay = Math.max(delay, 1);
        final int finalPosstep = posstep == 0 ? 1 : posstep;

        return () -> {
            StringBuilder sb = new StringBuilder(text.length() * 3);
            int len = formattingArray.length;
            int offset = (int) ((System.currentTimeMillis() / finalDelay) % len);

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);

                int indexColorArray = Math.floorMod(i * finalPosstep - offset, len);

                sb.append(formattingArray[indexColorArray]);
                sb.append(c);
            }
            return sb.toString();
        };
    }

    public static Supplier<String> animatedText(String format, Object[] args, int posstep, int delay,
        String... formattingArray) {
        return animatedText(String.format(Locale.ROOT, format, args), posstep, delay, formattingArray);
    }

    public static Supplier<String> translatedAnimatedText(String translationKey, int posstep, int delay,
        String... formattingArray) {
        return animatedText(StatCollector.translateToLocal(translationKey), posstep, delay, formattingArray);
    }

    public static Supplier<String> translatedAnimatedText(String translationKey, Object[] args, int posstep, int delay,
        String... formattingArray) {
        return animatedText(
            StatCollector.translateToLocalFormatted(translationKey, args),
            posstep,
            delay,
            formattingArray);
    }

    public static void addOredictTooltip(String oredictName, Supplier<String> tooltip) {
        for (ItemStack item : OreDictionary.getOres(oredictName)) {
            addItemTooltip(item, tooltip);
        }
    }

    public static void addItemTooltip(String modID, String registryName, int meta, Supplier<String> tooltip) {
        Item item = GameRegistry.findItem(modID, registryName);
        if (item == null || tooltip == null) return;
        addItemTooltip(new ItemStack(item, 1, meta), tooltip);
    }

    public static void addItemTooltip(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = TOOLTIP_MAP.computeIfAbsent(item, k -> new ArrayList<>());
        list.add(tooltip);
    }

    public static void removeItemTooltip(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = TOOLTIP_MAP.get(item);
        if (list != null) {
            list.removeIf(t -> t.equals(tooltip));
            if (list.isEmpty()) {
                TOOLTIP_MAP.remove(item);
            }
        }
    }

    public static void clearItemTooltips(ItemStack item) {
        if (item == null) return;
        TOOLTIP_MAP.remove(item);
    }

    public static void addItemTooltipShift(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = TOOLTIP_MAP_SHIFT.computeIfAbsent(item, k -> new ArrayList<>());
        list.add(tooltip);
    }

    public static void removeItemTooltipShift(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = TOOLTIP_MAP_SHIFT.get(item);
        if (list != null) {
            list.removeIf(t -> t.equals(tooltip));
            if (list.isEmpty()) {
                TOOLTIP_MAP_SHIFT.remove(item);
            }
        }
    }

    public static void clearItemTooltipsShift(ItemStack item) {
        if (item == null) return;
        TOOLTIP_MAP_SHIFT.remove(item);
    }

    public static void addItemTooltipCtrl(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = TOOLTIP_MAP_CTRL.computeIfAbsent(item, k -> new ArrayList<>());
        list.add(tooltip);
    }

    public static void removeItemTooltipCtrl(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = TOOLTIP_MAP_CTRL.get(item);
        if (list != null) {
            list.removeIf(t -> t.equals(tooltip));
            if (list.isEmpty()) {
                TOOLTIP_MAP_CTRL.remove(item);
            }
        }
    }

    public static void clearItemTooltipsCtrl(ItemStack item) {
        if (item == null) return;
        TOOLTIP_MAP_CTRL.remove(item);
    }

    public static void appendTooltipLines(List<String> tooltips, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        int lineStart = 0;
        int newLineIndex = text.indexOf('\n');
        while (newLineIndex >= 0) {
            tooltips.add(text.substring(lineStart, newLineIndex));
            lineStart = newLineIndex + 1;
            newLineIndex = text.indexOf('\n', lineStart);
        }
        tooltips.add(text.substring(lineStart));
    }

    public static void appendTooltipText(List<String> tooltips, List<Supplier<String>> tooltipSuppliers) {
        for (Supplier<String> tooltip : tooltipSuppliers) {
            appendTooltipLines(tooltips, tooltip.get());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void renderTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack.getItem() instanceof Stick stick && !stick.isShiftDown() && MainConfig.item.stick.enableStickItem) {
            stack = Stick.getDisguisedStack(stack);
            if (stack == null) return;
        }

        List<Supplier<String>> baseTooltips = TOOLTIP_MAP.get(stack);
        if (baseTooltips != null) {
            appendTooltipText(event.toolTip, baseTooltips);
        }

        List<Supplier<String>> shiftTooltips = TOOLTIP_MAP_SHIFT.get(stack);
        if (shiftTooltips != null && !shiftTooltips.isEmpty()) {
            if (GuiScreen.isShiftKeyDown()) {
                appendTooltipText(event.toolTip, shiftTooltips);
            } else {
                event.toolTip.add(StatCollector.translateToLocal("gtnl.tooltip.press_shift"));
            }
        }

        List<Supplier<String>> ctrlTooltips = TOOLTIP_MAP_CTRL.get(stack);
        if (ctrlTooltips != null && !ctrlTooltips.isEmpty()) {
            if (GuiScreen.isCtrlKeyDown()) {
                appendTooltipText(event.toolTip, ctrlTooltips);
            } else {
                event.toolTip.add(StatCollector.translateToLocal("gtnl.tooltip.press_ctrl"));
            }
        }
    }

    static {
        AQUA = EnumChatFormatting.AQUA.toString();
        BLACK = EnumChatFormatting.BLACK.toString();
        BLUE = EnumChatFormatting.BLUE.toString();
        BOLD = EnumChatFormatting.BOLD.toString();
        DARK_AQUA = EnumChatFormatting.DARK_AQUA.toString();
        DARK_BLUE = EnumChatFormatting.DARK_BLUE.toString();
        DARK_GRAY = EnumChatFormatting.DARK_GRAY.toString();
        DARK_GREEN = EnumChatFormatting.DARK_GREEN.toString();
        DARK_PURPLE = EnumChatFormatting.DARK_PURPLE.toString();
        DARK_RED = EnumChatFormatting.DARK_RED.toString();
        GOLD = EnumChatFormatting.GOLD.toString();
        GRAY = EnumChatFormatting.GRAY.toString();
        GREEN = EnumChatFormatting.GREEN.toString();
        ITALIC = EnumChatFormatting.ITALIC.toString();
        LIGHT_PURPLE = EnumChatFormatting.LIGHT_PURPLE.toString();
        OBFUSCATED = EnumChatFormatting.OBFUSCATED.toString();
        RED = EnumChatFormatting.RED.toString();
        RESET = EnumChatFormatting.RESET.toString();
        STRIKETHROUGH = EnumChatFormatting.STRIKETHROUGH.toString();
        UNDERLINE = EnumChatFormatting.UNDERLINE.toString();
        WHITE = EnumChatFormatting.WHITE.toString();
        YELLOW = EnumChatFormatting.YELLOW.toString();

        NEW_LINE = () -> "\n";
    }
}
