package com.science.gtnl.utils.text;

import java.util.function.Supplier;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.ArrayUtils;

public class AnimatedText {

    public static final Supplier<String> SNL_EDEN_GARDEN = () -> AnimatedText.SCIENCE_NOT_LEISURE.get()
        + AnimatedText.EDEN_GARDEN.get()
        + AnimatedTooltipHandler.RESET;
    public static final Supplier<String> SNL_SRP = () -> AnimatedText.SCIENCE_NOT_LEISURE.get()
        + AnimatedTooltipHandler.RESET
        + ": "
        + AnimatedText.STRUCTURAL_RECONSTRUCTION_PLAN.get()
        + AnimatedTooltipHandler.RESET;
    public static final Supplier<String> SNL_QYZG = () -> AnimatedText.SCIENCE_NOT_LEISURE
        .get() + AnimatedTooltipHandler.RESET + ": " + AnimatedText.QYZG.get() + AnimatedTooltipHandler.RESET;
    public static final Supplier<String> SNL_QYZG_SRP = () -> AnimatedText.SCIENCE_NOT_LEISURE.get()
        + AnimatedTooltipHandler.RESET
        + ": "
        + AnimatedText.QYZG.get()
        + AnimatedTooltipHandler.RESET
        + " X "
        + AnimatedText.STRUCTURAL_RECONSTRUCTION_PLAN.get()
        + AnimatedTooltipHandler.RESET;
    public static final Supplier<String> SNL_NLXCJH = () -> AnimatedText.SCIENCE_NOT_LEISURE
        .get() + AnimatedTooltipHandler.RESET + ": " + AnimatedText.NLXCJH.get() + AnimatedTooltipHandler.RESET;
    public static final Supplier<String> SNL_TOTTO = () -> AnimatedText.SCIENCE_NOT_LEISURE
        .get() + AnimatedTooltipHandler.RESET + ": " + AnimatedText.TOTTO.get() + AnimatedTooltipHandler.RESET;
    public static final Supplier<String> SNL_PBTR = () -> AnimatedText.SCIENCE_NOT_LEISURE
        .get() + AnimatedTooltipHandler.RESET + ": " + AnimatedText.PBTR.get() + AnimatedTooltipHandler.RESET;
    public static final Supplier<String> SNL_SCCR = () -> AnimatedText.SCIENCE_NOT_LEISURE
        .get() + AnimatedTooltipHandler.RESET + ": " + AnimatedText.SCCR.get() + AnimatedTooltipHandler.RESET;
    public static final Supplier<String> SNL_SKYINR = () -> AnimatedText.SCIENCE_NOT_LEISURE.get()
        + AnimatedTooltipHandler.RESET
        + ": "
        + AnimatedTooltipHandler.AQUA
        + "Created By: Skyinr"
        + AnimatedTooltipHandler.RESET;

    public static final Supplier<String> SNL_LONEI = () -> AnimatedText.SCIENCE_NOT_LEISURE
        .get() + AnimatedTooltipHandler.RESET + ": " + AnimatedText.LONEI.get() + AnimatedTooltipHandler.RESET;

    public static final Supplier<String> SCIENCE_NOT_LEISURE = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.text(StatCollector.translateToLocal("Tooltip_Adder")),
        AnimatedTooltipHandler.animatedText(
            "Science Not Leisure",
            1,
            80,
            AnimatedTooltipHandler.RED,
            AnimatedTooltipHandler.GOLD,
            AnimatedTooltipHandler.YELLOW,
            AnimatedTooltipHandler.GREEN,
            AnimatedTooltipHandler.AQUA,
            AnimatedTooltipHandler.BLUE,
            AnimatedTooltipHandler.LIGHT_PURPLE));

    public static final Supplier<String> SCIENCE_NOT_LEISURE_CHANGE = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.text(StatCollector.translateToLocal("Tooltip_ChangeAdder")),
        AnimatedTooltipHandler.animatedText(
            "Science Not Leisure",
            1,
            80,
            AnimatedTooltipHandler.RED,
            AnimatedTooltipHandler.GOLD,
            AnimatedTooltipHandler.YELLOW,
            AnimatedTooltipHandler.GREEN,
            AnimatedTooltipHandler.AQUA,
            AnimatedTooltipHandler.BLUE,
            AnimatedTooltipHandler.LIGHT_PURPLE));

    public static final Supplier<String> STRUCTURAL_RECONSTRUCTION_PLAN = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.animatedText(
            StatCollector.translateToLocal("StructuralReconstructionPlan"),
            1,
            50,
            AnimatedTooltipHandler.BLUE,
            AnimatedTooltipHandler.BLUE,
            AnimatedTooltipHandler.BLUE,
            AnimatedTooltipHandler.WHITE,
            AnimatedTooltipHandler.BLUE,
            AnimatedTooltipHandler.WHITE,
            AnimatedTooltipHandler.WHITE,
            AnimatedTooltipHandler.BLUE,
            AnimatedTooltipHandler.WHITE,
            AnimatedTooltipHandler.WHITE,
            AnimatedTooltipHandler.BLUE,
            AnimatedTooltipHandler.RED,
            AnimatedTooltipHandler.WHITE,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY,
            AnimatedTooltipHandler.GRAY));

    public static final Supplier<String> TIDAL = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.animatedText(
            "<<<<<<Tidal<<<<<<",
            -1,
            50,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GRAY + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD));

    public static final Supplier<String> WAVE = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.animatedText(
            ">>>>>>Wave>>>>>>",
            1,
            50,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GRAY + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD));

    public static final Supplier<String> QYZG = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.animatedText(
            "犰狳重工 GT-Odyssey",
            1,
            80,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD
                + AnimatedTooltipHandler.UNDERLINE
                + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.ITALIC));

    public static final Supplier<String> NLXCJH = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.animatedText(
            "年",
            1,
            100,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "轮",
            1,
            100,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "新",
            1,
            200,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "城",
            1,
            150,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "计",
            1,
            150,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "划",
            1,
            150,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            " b",
            1,
            200,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "y ",
            1,
            100,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "咸",
            1,
            150,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "到",
            1,
            100,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "老",
            1,
            150,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "时",
            1,
            100,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "变",
            1,
            150,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "成",
            1,
            100,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "鱼",
            1,
            150,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE));

    public static final Supplier<String> TOTTO = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.text("Author: "),
        AnimatedTooltipHandler.animatedText(
            "Totto",
            1,
            100,
            AnimatedTooltipHandler.LIGHT_PURPLE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GREEN + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE));

    public static final Supplier<String> EDEN_GARDEN = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.text(" X "),
        AnimatedTooltipHandler.animatedText(
            "伊甸生态园",
            1,
            120,
            AnimatedTooltipHandler.LIGHT_PURPLE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GREEN + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            " by ",
            1,
            100,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE),
        AnimatedTooltipHandler.animatedText(
            "茯角",
            1,
            100,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE));

    public static final Supplier<String> PBTR = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.animatedText(
            "zero_CM VS Fen'sorbed",
            1,
            100,
            AnimatedTooltipHandler.LIGHT_PURPLE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GREEN + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE));

    public static final Supplier<String> SCCR = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.animatedText(
            "zero_CM",
            1,
            50,
            AnimatedTooltipHandler.RED + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GREEN + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.DARK_GREEN + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.AQUA + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLUE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.DARK_PURPLE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.LIGHT_PURPLE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLACK + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLACK + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLACK + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLACK + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.BLACK + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.YELLOW + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD + AnimatedTooltipHandler.UNDERLINE));

    public static final Supplier<String> LONEI = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.animatedText(
            "Lonei",
            1,
            90,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GRAY + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GRAY + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GRAY + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GRAY + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.WHITE + AnimatedTooltipHandler.BOLD));

    public static final String AuthorHighPressureRaven = EnumChatFormatting.WHITE + "High"
        + EnumChatFormatting.GRAY
        + "Pres"
        + EnumChatFormatting.DARK_GRAY
        + "sure"
        + EnumChatFormatting.LIGHT_PURPLE
        + "Raven";

    public static final Supplier<String> SteamgateCredits = AnimatedTooltipHandler.chain(
        AnimatedTooltipHandler.text(EnumChatFormatting.WHITE + "Created By:\n"),
        getAuthorSerenibyssLetter(
            "S",
            30,
            3,
            AnimatedTooltipHandler.LIGHT_PURPLE,
            11,
            AnimatedTooltipHandler.WHITE,
            25,
            AnimatedTooltipHandler.AQUA),
        getAuthorSerenibyssLetter(
            "t",
            30,
            12,
            AnimatedTooltipHandler.AQUA,
            18,
            AnimatedTooltipHandler.LIGHT_PURPLE,
            29,
            AnimatedTooltipHandler.WHITE),
        getAuthorSerenibyssLetter(
            "e",
            30,
            0,
            AnimatedTooltipHandler.WHITE,
            10,
            AnimatedTooltipHandler.LIGHT_PURPLE,
            20,
            AnimatedTooltipHandler.AQUA),
        getAuthorSerenibyssLetter(
            "a",
            30,
            9,
            AnimatedTooltipHandler.LIGHT_PURPLE,
            17,
            AnimatedTooltipHandler.AQUA,
            22,
            AnimatedTooltipHandler.WHITE),
        getAuthorSerenibyssLetter(
            "m",
            30,
            6,
            AnimatedTooltipHandler.WHITE,
            14,
            AnimatedTooltipHandler.AQUA,
            27,
            AnimatedTooltipHandler.LIGHT_PURPLE),
        getAuthorSerenibyssLetter(
            "i",
            30,
            1,
            AnimatedTooltipHandler.AQUA,
            15,
            AnimatedTooltipHandler.WHITE,
            21,
            AnimatedTooltipHandler.LIGHT_PURPLE),
        getAuthorSerenibyssLetter(
            "b",
            30,
            13,
            AnimatedTooltipHandler.WHITE,
            19,
            AnimatedTooltipHandler.LIGHT_PURPLE,
            23,
            AnimatedTooltipHandler.WHITE),
        getAuthorSerenibyssLetter(
            "y",
            30,
            2,
            AnimatedTooltipHandler.AQUA,
            8,
            AnimatedTooltipHandler.LIGHT_PURPLE,
            24,
            AnimatedTooltipHandler.WHITE),
        getAuthorSerenibyssLetter(
            "s",
            30,
            5,
            AnimatedTooltipHandler.AQUA,
            16,
            AnimatedTooltipHandler.WHITE,
            26,
            AnimatedTooltipHandler.LIGHT_PURPLE),
        getAuthorSerenibyssLetter(
            "s",
            30,
            4,
            AnimatedTooltipHandler.LIGHT_PURPLE,
            7,
            AnimatedTooltipHandler.WHITE,
            28,
            AnimatedTooltipHandler.AQUA),
        AnimatedTooltipHandler.text("\n"),
        AnimatedTooltipHandler.animatedText(
            "Brass",
            0,
            500,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.DARK_GREEN + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.DARK_GREEN + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.DARK_GREEN + AnimatedTooltipHandler.BOLD),
        AnimatedTooltipHandler.animatedText(
            "Noccles",
            0,
            500,
            AnimatedTooltipHandler.DARK_GREEN + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.DARK_GREEN + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.GOLD + AnimatedTooltipHandler.BOLD,
            AnimatedTooltipHandler.DARK_GREEN + AnimatedTooltipHandler.BOLD),
        AnimatedTooltipHandler.text(
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
        AnimatedTooltipHandler.chain(
            createPipeBluezLetter(0),
            createPipeBluezLetter(1),
            createPipeBluezLetter(2),
            createPipeBluezLetter(3),
            createPipeBluezLetter(4),
            createPipeBluezLetter(5),
            createPipeBluezLetter(6),
            createPipeBluezLetter(7),
            createPipeBluezLetter(8)),
        AnimatedTooltipHandler.text("\n" + AuthorHighPressureRaven + "\n"),
        AnimatedTooltipHandler.text(EnumChatFormatting.GOLD + "Gear" + EnumChatFormatting.DARK_PURPLE + "ix"));

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
                color = colors[switchIndex] + AnimatedTooltipHandler.ITALIC;
            } else if (obfuscated) {
                obfuscated = false;
                switchIndex++;
                if (switchIndex == colors.length) switchIndex = 0;
                color = colors[switchIndex] + AnimatedTooltipHandler.ITALIC;
            } else {
                color = colors[switchIndex] + AnimatedTooltipHandler.ITALIC;
            }
            colorAlternator[index] = color;
            index++;
            if (index == length) index = 0;
        } while (index != switchIntervals[0]);

        return AnimatedTooltipHandler.animatedText(letter, 1, 250, colorAlternator);
    }

    private static Supplier<String> createPipeBluezLetter(int letterIndex) {
        char[] letters = "PipeBluez".toCharArray();
        String[] colors = { AnimatedTooltipHandler.WHITE, AnimatedTooltipHandler.WHITE, AnimatedTooltipHandler.WHITE,
            AnimatedTooltipHandler.WHITE, AnimatedTooltipHandler.AQUA, AnimatedTooltipHandler.DARK_AQUA,
            AnimatedTooltipHandler.BLUE, AnimatedTooltipHandler.DARK_BLUE, AnimatedTooltipHandler.DARK_BLUE };
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
        return AnimatedTooltipHandler.animatedText(letter, 1, 250, colorAlternator);
    }
}
