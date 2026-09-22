package com.science.gtnl.utils.text.effect;

import java.util.List;
import java.util.Objects;

import com.science.gtnl.ScienceNotLeisure;

public class TextEffects {

    public static final TextEffectStyle INFERNUM_RED_RARITY = preset("infernum_red_rarity");
    public static final TextEffectStyle GENESIS_COMPONENT_RARITY_SHADER = preset("genesis_component_rarity_shader");
    public static final TextEffectStyle PULSE_CIRCLE = preset("pulse_circle");
    public static final TextEffectStyle NAMELESS_BOSS_BAR_SHADER = preset("nameless_boss_bar_shader");
    public static final TextEffectStyle PULSE_UPWARDS = preset("pulse_upwards");
    public static final TextEffectStyle CALAMITY_RED = preset("calamity_red");
    public static final TextEffectStyle EXOTIC_RAINBOW = preset("exotic_rainbow");
    public static final TextEffectStyle SUPERBOSS_RARITY = preset("superboss_rarity");
    public static final TextEffectStyle INFERNUM_SPARK_RARITY = preset("infernum_spark_rarity");
    public static final TextEffectStyle BURNISHED_AURIC = preset("burnished_auric");
    public static final TextEffectStyle EVERCOLD_CYAN = preset("evercold_cyan");
    public static final TextEffectStyle STARSILVER_RARITY = preset("starsilver_rarity");

    private TextEffects() {}

    private static TextEffectStyle preset(String name) {
        return new TextEffectStyle(ScienceNotLeisure.MODID + ":" + name, List.of(), 1);
    }

    /**
     * Opens an inline effect until the next declaration or {@code §r}. Native color codes replace its palette;
     * native style codes retain their usual meaning. The same codes also accept an ampersand prefix. Example:
     * {@code §{pulse_upwards;colors=#FFD700;speed=1.2}§oText§r}. Compact input accepts aliases such as
     * {@code &{pu}}, {@code &{ba;#fc0;2}}, and the parameter names {@code c} and {@code s}.
     */
    public static String format(TextEffectStyle style) {
        Objects.requireNonNull(style, "style");
        String id = style.rendererId();
        String namespace = ScienceNotLeisure.MODID + ":";
        if (id.startsWith(namespace)) id = id.substring(namespace.length());
        StringBuilder result = new StringBuilder(TextEffectFormat.INLINE_OPEN).append(id);
        if (!style.colors()
            .isEmpty()) {
            result.append(";colors=");
            for (int i = 0; i < style.colors()
                .size(); i++) {
                if (i > 0) result.append(',');
                String hex = Integer.toHexString(
                    style.colors()
                        .get(i));
                result.append('#')
                    .append("000000", 0, 6 - hex.length())
                    .append(hex);
            }
        }
        if (style.speed() != 1) result.append(";speed=")
            .append(style.speed());
        return result.append('}')
            .toString();
    }

    public static String apply(String text, TextEffectStyle style) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(style, "style");
        return text.isEmpty() ? text : opening(style) + text + EffectTextParser.CLOSE;
    }

    public static String opening(TextEffectStyle style) {
        StringBuilder result = new StringBuilder(EffectTextParser.OPEN).append(style.rendererId())
            .append(';')
            .append(style.speed())
            .append(';');
        for (int i = 0; i < style.colors()
            .size(); i++) {
            if (i > 0) result.append(',');
            result.append(
                Integer.toHexString(
                    style.colors()
                        .get(i)));
        }
        return result.append(EffectTextParser.END)
            .toString();
    }

    public static String escapeLiteral(String text) {
        return Objects.requireNonNull(text, "text")
            .replace("\u2063", "\u2063\u2063");
    }

    public static String plainText(String text) {
        return EffectTextParser.parse(text)
            .plainText();
    }
}
