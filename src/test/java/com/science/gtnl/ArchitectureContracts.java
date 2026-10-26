package com.science.gtnl;

import static com.gtnllib.GTNLLibContractTest.check;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArchitectureContracts {

    public static final Path SOURCES = Paths.get("src/main/java/com/science/gtnl");

    // Ratchets: each count may only go down. Lower the number in the same change that lowers the count.
    public static final int MAX_UTILS_CLASSES = 83;
    public static final int MAX_MOD_CHECKS_OUTSIDE_COMPAT = 74;
    public static final int MAX_MUI1_FILES = 47;
    public static final int MAX_DIRECT_GT_MULTIBLOCK_BASES = 10;
    public static final int MAX_MIXINS_OVER_80_LINES = 58;
    public static final int MAX_CLIENT_IMPORTS_IN_COMMON = 90;

    public static final Set<String> SELF_REGISTERING = Set.of(
        "common/block/blocks/BlockEnderElevator.java",
        "common/block/blocks/BlockEternalGregTechWorkshopRender.java",
        "common/block/blocks/BlockHoneyFluid.java",
        "common/block/blocks/BlockShimmerFluid.java",
        "common/block/blocks/BlocksCompressedStargate.java",
        "common/item/ItemAdderBasic.java",
        "common/item/ItemInfinityItem.java",
        "common/item/items/CircuitIntegratedPlus.java",
        "common/item/items/GTNLItemBucket.java",
        "common/item/items/ItemPartEnergyCellBase.java",
        "common/item/items/ItemPartUniversalBus.java",
        "common/item/items/PortableItem.java",
        "common/item/items/PortalGun.java",
        "common/item/items/armor/CelestweaveArmor.java",
        "common/item/items/armor/SoulCardboardArmor.java",
        "common/item/items/bauble/DraconicArmorProjectionBauble.java",
        "common/item/items/equipment/EquipmentVariantItem.java",
        "common/item/items/fuelRod/RadioactiveItem.java");

    public static final Set<String> TOP_LEVEL = Set
        .of("api", "asm", "client", "common", "compat", "config", "loader", "mixins", "utils");

    private static final Pattern GT_MULTIBLOCK_BASE = Pattern.compile(
        "extends (MTEExtendedPowerMultiBlockBase|MTEEnhancedMultiBlockBase|MTEMultiBlockBase|MTETooltipMultiBlockBase"
            + "|TTMultiblockBase|MTESteamMultiBlockBase|GTPPMultiBlockBase|KubaTechGTMultiBlockBase)\\b");
    private static final Pattern SELF_REGISTRATION = Pattern
        .compile("GameRegistry\\s*\\.\\s*register(Item|Block)\\(\\s*this");
    private static final Pattern CARD = Pattern.compile("class (\\w+)[^{]*implements[^{]*\\bIContentLoader\\b");
    private static final Pattern COMPAT = Pattern
        .compile("^public class (\\w+)[^{]*implements[^{]*\\bIModCompat\\b", Pattern.MULTILINE);
    private static final Pattern SYSTEM_IMPORT = Pattern.compile(
        "^import com\\.science\\.gtnl\\.common\\.(celestweave|railgun|portalgun|equipment|wireless|meminiterminal"
            + "|beamformer|yogsothoth|steam|stellar|greenhouse)\\.",
        Pattern.MULTILINE);

    private ArchitectureContracts() {}

    public static void run() throws IOException {
        Map<String, String> sources = sources();
        check(sources.size() > 1000, "the architecture scan sees the GTNL sources, found " + sources.size());

        ratchet(sources, "utils classes", MAX_UTILS_CLASSES, (path, text) -> path.startsWith("utils/"));
        ratchet(
            sources,
            "files outside compat/ and loader/ that check whether a mod is loaded",
            MAX_MOD_CHECKS_OUTSIDE_COMPAT,
            (path, text) -> text.contains(".isModLoaded()") && !path.startsWith("compat/")
                && !path.startsWith("loader/"));
        ratchet(
            sources,
            "files importing ModularUI 1",
            MAX_MUI1_FILES,
            (path, text) -> text.contains("import com.gtnewhorizons.modularui"));
        ratchet(
            sources,
            "machines extending a GT multiblock base instead of a GTNL base",
            MAX_DIRECT_GT_MULTIBLOCK_BASES,
            (path, text) -> path.startsWith("common/machine/") && !path.startsWith("common/machine/multiMachineBase/")
                && !path.startsWith("common/machine/tooltip/")
                && GT_MULTIBLOCK_BASE.matcher(text)
                    .find());
        ratchet(
            sources,
            "mixins over 80 lines",
            MAX_MIXINS_OVER_80_LINES,
            (path, text) -> path.startsWith("mixins/") && text.contains("@Mixin") && lines(text) > 80);
        ratchet(
            sources,
            "common files outside render/ and gui/ importing client classes",
            MAX_CLIENT_IMPORTS_IN_COMMON,
            (path, text) -> path.startsWith("common/") && !path.startsWith("common/render/")
                && !path.startsWith("common/gui/")
                && text.contains("\nimport net.minecraft.client."));

        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String path = entry.getKey(), text = entry.getValue();
            if ((path.startsWith("common/item/") || path.startsWith("common/block/")) && SELF_REGISTRATION.matcher(text)
                .find())
                check(
                    SELF_REGISTERING.contains(path),
                    path + " registers itself in its constructor; register new items and blocks through ItemLoader.ITEMS"
                        + " or BlockLoader.BLOCKS");
            if (path.startsWith("utils/")) check(
                !SYSTEM_IMPORT.matcher(text)
                    .find(),
                path + " imports a system package; code that belongs to one system lives in that system");
            int slash = path.indexOf('/');
            if (slash > 0) check(
                TOP_LEVEL.contains(path.substring(0, slash)),
                path + " is under a new top-level directory; adding one needs the lead's approval");
        }

        for (String proxy : new String[] { "CommonProxy.java", "ClientProxy.java" }) {
            String text = sources.get(proxy);
            for (String call : new String[] { "EVENT_BUS.register", "registerItemRenderer",
                "bindTileEntitySpecialRenderer", "registerServerCommand", "ClientCommandHandler" })
                check(
                    !text.contains(call),
                    proxy + " only forwards load phases; " + call + " belongs in a registration card");
        }

        String cards = sources.get("loader/ContentLoaders.java");
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            if (!entry.getKey()
                .startsWith("loader/")) continue;
            Matcher matcher = CARD.matcher(entry.getValue());
            while (matcher.find()) {
                String name = matcher.group(1);
                String outer = entry.getKey()
                    .substring(
                        entry.getKey()
                            .lastIndexOf('/') + 1,
                        entry.getKey()
                            .length() - 5);
                String listed = name.equals(outer) ? "new " + name + "()" : "new " + outer + "." + name + "()";
                check(
                    cards.contains(listed),
                    entry.getKey() + " declares the card " + name + ", but ContentLoaders does not list it");
            }
        }

        String compat = sources.get("loader/CompatLoader.java");
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            if (!entry.getKey()
                .startsWith("compat/")) continue;
            Matcher matcher = COMPAT.matcher(entry.getValue());
            if (matcher.find()) check(
                compat.contains("new " + matcher.group(1) + "()"),
                entry.getKey() + " is a mod integration, but CompatLoader does not list it");
        }
    }

    private interface Rule {

        /**
         * Whether the source file at {@code path} (relative to the GTNL package, with forward slashes) counts.
         */
        boolean counts(String path, String text);
    }

    private static void ratchet(Map<String, String> sources, String what, int max, Rule rule) {
        List<String> hits = sources.entrySet()
            .stream()
            .filter(entry -> rule.counts(entry.getKey(), entry.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        check(
            hits.size() <= max,
            what + " may only go down: limit " + max + ", found " + hits.size() + " " + new TreeSet<>(hits));
        if (hits.size() < max)
            System.out.println("ratchet can be lowered: " + what + " is " + hits.size() + ", limit " + max);
    }

    private static int lines(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) if (text.charAt(i) == '\n') count++;
        return count;
    }

    private static Map<String, String> sources() throws IOException {
        Map<String, String> result = new TreeMap<>();
        try (Stream<Path> walk = Files.walk(SOURCES)) {
            for (Path file : (Iterable<Path>) walk.filter(
                path -> path.toString()
                    .endsWith(".java"))::iterator) {
                String key = SOURCES.relativize(file)
                    .toString()
                    .replace('\\', '/');
                result.put(key, new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
            }
        }
        return result;
    }
}
