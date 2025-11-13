package com.science.gtnl.utils.text;

import static com.science.gtnl.utils.text.AnimatedTooltipHandler.*;

import java.util.function.Supplier;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.ArrayUtils;

public class AnimatedText {

    public static final Supplier<String> SNL_EDEN_GARDEN = () -> AnimatedText.SCIENCE_NOT_LEISURE.get()
        + AnimatedText.EDEN_GARDEN.get()
        + RESET;
    public static final Supplier<String> SNL_SRP = () -> AnimatedText.SCIENCE_NOT_LEISURE.get() + RESET
        + ": "
        + AnimatedText.STRUCTURAL_RECONSTRUCTION_PLAN.get()
        + RESET;
    public static final Supplier<String> SNL_QYZG = () -> AnimatedText.SCIENCE_NOT_LEISURE.get() + RESET
        + ": "
        + AnimatedText.QYZG.get()
        + RESET;
    public static final Supplier<String> SNL_QYZG_SRP = () -> AnimatedText.SCIENCE_NOT_LEISURE.get() + RESET
        + ": "
        + AnimatedText.QYZG.get()
        + RESET
        + " X "
        + AnimatedText.STRUCTURAL_RECONSTRUCTION_PLAN.get()
        + RESET;
    public static final Supplier<String> SNL_NLXCJH = () -> AnimatedText.SCIENCE_NOT_LEISURE.get() + RESET
        + ": "
        + AnimatedText.NLXCJH.get()
        + RESET;
    public static final Supplier<String> SNL_TOTTO = () -> AnimatedText.SCIENCE_NOT_LEISURE.get() + RESET
        + ": "
        + AnimatedText.TOTTO.get()
        + RESET;
    public static final Supplier<String> SNL_PBTR = () -> AnimatedText.SCIENCE_NOT_LEISURE.get() + RESET
        + ": "
        + AnimatedText.PBTR.get()
        + RESET;
    public static final Supplier<String> SNL_SCCR = () -> AnimatedText.SCIENCE_NOT_LEISURE.get() + RESET
        + ": "
        + AnimatedText.SCCR.get()
        + RESET;
    public static final Supplier<String> SNL_SKYINR = () -> AnimatedText.SCIENCE_NOT_LEISURE
        .get() + RESET + ": " + AQUA + "Created By: Skyinr" + RESET;

    public static final Supplier<String> SCIENCE_NOT_LEISURE = chain(
        text(StatCollector.translateToLocal("Tooltip_Adder")),
        animatedText("Science Not Leisure", 1, 80, RED, GOLD, YELLOW, GREEN, AQUA, BLUE, LIGHT_PURPLE));

    public static final Supplier<String> SCIENCE_NOT_LEISURE_CHANGE = chain(
        text(StatCollector.translateToLocal("Tooltip_ChangeAdder")),
        animatedText("Science Not Leisure", 1, 80, RED, GOLD, YELLOW, GREEN, AQUA, BLUE, LIGHT_PURPLE));

    public static final Supplier<String> STRUCTURAL_RECONSTRUCTION_PLAN = chain(
        animatedText(
            StatCollector.translateToLocal("StructuralReconstructionPlan"),
            1,
            50,
            BLUE,
            BLUE,
            BLUE,
            WHITE,
            BLUE,
            WHITE,
            WHITE,
            BLUE,
            WHITE,
            WHITE,
            BLUE,
            RED,
            WHITE,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY,
            GRAY));

    public static final Supplier<String> QYZG = chain(
        animatedText(
            "犰狳重工 GT-Odyssey",
            1,
            80,
            YELLOW + BOLD + UNDERLINE + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC,
            GOLD + ITALIC));

    public static final Supplier<String> NLXCJH = chain(
        animatedText(
            "年",
            1,
            100,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "轮",
            1,
            100,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "新",
            1,
            200,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "城",
            1,
            150,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "计",
            1,
            150,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "划",
            1,
            150,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            " b",
            1,
            200,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "y ",
            1,
            100,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "咸",
            1,
            150,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "到",
            1,
            100,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "老",
            1,
            150,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "时",
            1,
            100,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "变",
            1,
            150,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "成",
            1,
            100,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "鱼",
            1,
            150,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE));

    public static final Supplier<String> TOTTO = chain(
        text("Author: "),
        animatedText(
            "Totto",
            1,
            100,
            LIGHT_PURPLE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE,
            GREEN + BOLD + UNDERLINE,
            AQUA + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE));

    public static final Supplier<String> EDEN_GARDEN = chain(
        text(" X "),
        animatedText(
            "伊甸生态园",
            1,
            120,
            LIGHT_PURPLE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE,
            GREEN + BOLD + UNDERLINE,
            AQUA + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE),
        animatedText(
            " by ",
            1,
            100,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE),
        animatedText(
            "茯角",
            1,
            100,
            RED + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE));

    public static final Supplier<String> PBTR = chain(
        animatedText(
            "zero_CM VS Fen'sorbed",
            1,
            100,
            LIGHT_PURPLE + BOLD + UNDERLINE,
            RED + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE,
            GREEN + BOLD + UNDERLINE,
            AQUA + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE));

    public static final Supplier<String> SCCR = chain(
        animatedText(
            "zero_CM",
            1,
            50,
            RED + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE,
            GREEN + BOLD + UNDERLINE,
            DARK_GREEN + BOLD + UNDERLINE,
            AQUA + BOLD + UNDERLINE,
            BLUE + BOLD + UNDERLINE,
            DARK_PURPLE + BOLD + UNDERLINE,
            LIGHT_PURPLE + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            BLACK + BOLD + UNDERLINE,
            WHITE + BOLD + UNDERLINE,
            BLACK + BOLD + UNDERLINE,
            WHITE + BOLD + UNDERLINE,
            BLACK + BOLD + UNDERLINE,
            WHITE + BOLD + UNDERLINE,
            BLACK + BOLD + UNDERLINE,
            WHITE + BOLD + UNDERLINE,
            BLACK + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            YELLOW + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE,
            GOLD + BOLD + UNDERLINE));

    public static final String AuthorHighPressureRaven = EnumChatFormatting.WHITE + "High"
        + EnumChatFormatting.GRAY
        + "Pres"
        + EnumChatFormatting.DARK_GRAY
        + "sure"
        + EnumChatFormatting.LIGHT_PURPLE
        + "Raven";

    public static final Supplier<String> SteamgateCredits = chain(
        text(EnumChatFormatting.WHITE + "Created By:\n"),
        getAuthorSerenibyssLetter("S", 30, 3, LIGHT_PURPLE, 11, WHITE, 25, AQUA),
        getAuthorSerenibyssLetter("t", 30, 12, AQUA, 18, LIGHT_PURPLE, 29, WHITE),
        getAuthorSerenibyssLetter("e", 30, 0, WHITE, 10, LIGHT_PURPLE, 20, AQUA),
        getAuthorSerenibyssLetter("a", 30, 9, LIGHT_PURPLE, 17, AQUA, 22, WHITE),
        getAuthorSerenibyssLetter("m", 30, 6, WHITE, 14, AQUA, 27, LIGHT_PURPLE),
        getAuthorSerenibyssLetter("i", 30, 1, AQUA, 15, WHITE, 21, LIGHT_PURPLE),
        getAuthorSerenibyssLetter("b", 30, 13, WHITE, 19, LIGHT_PURPLE, 23, WHITE),
        getAuthorSerenibyssLetter("y", 30, 2, AQUA, 8, LIGHT_PURPLE, 24, WHITE),
        getAuthorSerenibyssLetter("s", 30, 5, AQUA, 16, WHITE, 26, LIGHT_PURPLE),
        getAuthorSerenibyssLetter("s", 30, 4, LIGHT_PURPLE, 7, WHITE, 28, AQUA),
        text("\n"),
        animatedText(
            "Brass",
            0,
            500,
            GOLD + BOLD,
            DARK_GREEN + BOLD,
            GOLD + BOLD,
            DARK_GREEN + BOLD,
            DARK_GREEN + BOLD),
        animatedText(
            "Noccles",
            0,
            500,
            DARK_GREEN + BOLD,
            GOLD + BOLD,
            DARK_GREEN + BOLD,
            GOLD + BOLD,
            DARK_GREEN + BOLD),
        text(
            "\n" + EnumChatFormatting.LIGHT_PURPLE
                + EnumChatFormatting.ITALIC
                + "Steam"
                + EnumChatFormatting.WHITE
                + EnumChatFormatting.ITALIC
                + "Is"
                + EnumChatFormatting.LIGHT_PURPLE
                + EnumChatFormatting.ITALIC
                + "The"
                + EnumChatFormatting.WHITE
                + EnumChatFormatting.ITALIC
                + "Number\n"),
        chain(
            createPipeBluezLetter(0),
            createPipeBluezLetter(1),
            createPipeBluezLetter(2),
            createPipeBluezLetter(3),
            createPipeBluezLetter(4),
            createPipeBluezLetter(5),
            createPipeBluezLetter(6),
            createPipeBluezLetter(7),
            createPipeBluezLetter(8)),
        text("\n" + AuthorHighPressureRaven + "\n"),
        text(EnumChatFormatting.GOLD + "Gear" + EnumChatFormatting.DARK_PURPLE + "ix"));

    private static Supplier<String> getAuthorSerenibyssLetter(String letter, int length, Object... switchParams) {
        int[] switchIntervals = new int[switchParams.length / 2];
        String[] colors = new String[switchParams.length / 2];
        for (int i = 0; i < switchParams.length; i += 2) {
            switchIntervals[i / 2] = (int) switchParams[i];
            colors[i / 2] = (String) switchParams[i + 1];
        }

        String[] colorAlternator = new String[length];
        int index = switchIntervals[0];
        int switchIndex = 0;
        boolean obfuscated = false;
        do {
            String color;
            if (ArrayUtils.contains(switchIntervals, index)) {
                obfuscated = true;
                color = colors[switchIndex] + ITALIC;
            } else if (obfuscated) {
                obfuscated = false;
                switchIndex++;
                if (switchIndex == colors.length) switchIndex = 0;
                color = colors[switchIndex] + ITALIC;
            } else {
                color = colors[switchIndex] + ITALIC;
            }
            colorAlternator[index] = color;
            index++;
            if (index == length) index = 0;
        } while (index != switchIntervals[0]);

        return animatedText(letter, 1, 250, colorAlternator);
    }

    private static Supplier<String> createPipeBluezLetter(int letterIndex) {
        char[] letters = "PipeBluez".toCharArray();
        String[] colors = { WHITE, WHITE, WHITE, WHITE, AQUA, DARK_AQUA, BLUE, DARK_BLUE, DARK_BLUE };
        int[] order = new int[] { 0, 6, 3, 8, 5, 7, 2, 4, 1 };
        int length = letters.length * 5 * 4;
        String letter = Character.toString(letters[letterIndex]);

        String[] colorAlternator = new String[length];
        int index = 0;
        int orderIndex = 0;
        do {
            String color = colors[Math.floorMod(letterIndex - order[orderIndex], colors.length)];
            if ((index + 1) % 4 == 0) {
                orderIndex++;
            }
            colorAlternator[index] = color;
            index++;
            if (orderIndex > 8) orderIndex = 0;
        } while (index != length);
        return animatedText(letter, 1, 250, colorAlternator);
    }
}
