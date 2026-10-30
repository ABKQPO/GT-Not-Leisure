package com.science.gtnl;

import static com.gtnllib.GTNLLibContractTest.check;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gtnewhorizon.gtnhlib.util.data.IMod;
import com.science.gtnl.loader.CompatLoader;

public class ArchitectureContracts {

    public static final Path SOURCES = Paths.get("src/main/java/com/science/gtnl");

    // Ratchets: each count may only go down. Lower the number in the same change that lowers the count.
    public static final int LONG_METHOD_LINES = 10;
    public static final int MAX_CLIENT_IMPORTS_IN_COMMON = 90;

    // The only machine on GT's tooltip-only base; an adapter for a single machine would only forward.
    public static final Set<String> GT_BASE_ALLOWED = Set.of("common/machine/multiblock/QuantumComputer.java");

    // Recipe maps must be built in GTNLRecipeMaps' own static init so GT and NEI see them; packet registration order
    // fixes packet ids; RecipeBuilder.requireMods receives the mods to check as arguments.
    public static final Set<String> MOD_CHECK_ALLOWED = Set.of(
        "common/material/GTNLRecipeMaps.java",
        "common/packet/NetWorkHandler.java",
        "utils/recipes/RecipeBuilder.java");

    // GT's RecipeMapFrontend still takes ModularUI 1 types; GT machines still on ModularUI 1 open cover tabs through
    // createWindow; the modularUI mixins patch ModularUI 1 itself; the Eternal GregTech Workshop is unfinished.
    public static final List<String> MUI1_ALLOWED = List.of(
        "common/gui/recipe/",
        "common/material/GTNLRecipeMaps.java",
        "common/gui/VoidCoverUIFactory.java",
        "common/gui/WirelessSteamCoverUIFactory.java",
        "common/machine/cover/VoidCover.java",
        "common/machine/cover/WirelessSteamCover.java",
        "mixins/late/modularUI/",
        "common/machine/multiblock/module/eternalGregTechWorkshop/",
        "common/gui/modularui/GTNLMui2ItemHandlerAdapter.java");

    // GTNLLib's WorldListener is one object per world plus a root that owns the map, so the card registers the root.
    public static final String MANUAL_EVENT_REGISTRATION = "loader/WorldLoader.java";

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

    // Every file under utils/ is listed here, so adding shared code is a visible, reviewed change rather than a count.
    public static final Path UTILS_LIST = Paths.get("src/test/resources/com/science/gtnl/utils-files.txt");

    // Registration order fixes block, item and machine ids in saves: released cards keep their place, new ones go last.
    public static final List<String> RELEASED_LOADER_ORDER = List.of(
        "EffectLoader",
        "EntityLoader",
        "BlockLoader.Card",
        "ItemLoader.Card",
        "MaterialLoader",
        "WorldLoader",
        "MachineLoader",
        "MELoader",
        "CompatLoader",
        "RecipeLoader",
        "AchievementsLoader",
        "CelestweaveLoader",
        "RailgunLoader",
        "PortalGunLoader",
        "EquipmentLoader",
        "WirelessLoader",
        "MiniTerminalLoader",
        "YogSothothLoader",
        "QuantumLoader",
        "BeamFormerLoader",
        "SteamLoader",
        "StellarLoader",
        "GreenhouseLoader",
        "DetravLoader",
        "TickrateLoader",
        "VoidMinerLoader",
        "TextEffectLoader",
        "CommandLoader",
        "RenderLoader");

    // Types used in api/ method signatures keep their released names; ApiSignatureContracts guards them.
    public static final Set<String> API_PINNED = Set.of(
        "utils/machine/greenHouseManager/GreenHouseMode.java",
        "utils/machine/greenHouseManager/GreenHouseStoredCrop.java",
        "utils/machine/greenHouseManager/GreenHouseViewMode.java");

    private static final Pattern GT_MULTIBLOCK_BASE = Pattern.compile(
        "^public (abstract )?class \\w+(<[^{]*>)? extends (MTEExtendedPowerMultiBlockBase|MTEEnhancedMultiBlockBase"
            + "|MTEMultiBlockBase|MTETooltipMultiBlockBase|TTMultiblockBase|MTESteamMultiBlockBase|GTPPMultiBlockBase"
            + "|KubaTechGTMultiBlockBase)\\b",
        Pattern.MULTILINE);
    private static final Pattern SELF_REGISTRATION = Pattern
        .compile("GameRegistry\\s*\\.\\s*register(Item|Block)\\(\\s*this");
    private static final Pattern SAVED_DATA_CLASS = Pattern
        .compile("^public class (\\w+) extends WorldSavedData\\b", Pattern.MULTILINE);
    private static final Pattern MIXIN_INTERFACE = Pattern.compile("^public interface ", Pattern.MULTILINE);
    private static final Pattern LOADER_ENTRY = Pattern.compile("new ([\\w.]+)\\(\\)");
    private static final Pattern CARD = Pattern.compile("class (\\w+)[^{]*implements[^{]*\\bIContentLoader\\b");
    private static final Pattern COMPAT = Pattern
        .compile("^public class (\\w+)[^{]*implements[^{]*\\bIModCompat\\b", Pattern.MULTILINE);
    private static final Pattern EVENT_REGISTRATION = Pattern
        .compile("(EVENT_BUS|TERRAIN_GEN_BUS|ORE_GEN_BUS|bus\\(\\))\\s*\\.\\s*register\\(");
    private static final Pattern SUBSCRIBE_METHOD = Pattern.compile(
        "@SubscribeEvent[^\\n]*\\n(?:\\s*@[^\\n]*\\n)*\\s*((?:public|private|protected)?\\s*(?:static\\s+)?)\\w+ (\\w+)\\(");
    private static final Pattern FINAL_CLASS = Pattern.compile(
        "^[ \\t]*(?:(?:public|protected|private|static|abstract)[ \\t]+)*final[ \\t]+(?:(?:static|abstract)[ \\t]+)*class[ \\t]",
        Pattern.MULTILINE);
    private static final Pattern PACKAGE_PRIVATE_TYPE = Pattern.compile(
        "^( {4})?(?:(?:abstract|static|final|sealed|strictfp)[ \\t]+)*(?:class|interface|enum)[ \\t]+(\\w+)",
        Pattern.MULTILINE);
    private static final Pattern INTERFACE_FILE = Pattern.compile("^public @?interface ", Pattern.MULTILINE);
    private static final Pattern MIXIN_ENTRY_START = Pattern.compile("\\n    (?=[A-Z][A-Z_0-9]*\\()");
    private static final Pattern MIXIN_NAME = Pattern.compile("\"([A-Za-z0-9_]+(?:\\.[A-Za-z0-9_$]+)+)\"");
    private static final Pattern SYSTEM_IMPORT = Pattern.compile(
        "^import com\\.science\\.gtnl\\.common\\.(celestweave|railgun|portalgun|equipment|wireless|meminiterminal"
            + "|beamformer|yogsothoth|steam|stellar|greenhouse)\\.",
        Pattern.MULTILINE);

    private ArchitectureContracts() {}

    public static void run() throws IOException {
        Map<String, String> sources = sources();
        check(sources.size() > 1000, "the architecture scan sees the GTNL sources, found " + sources.size());

        Set<String> utilsListed = Files.readAllLines(UTILS_LIST, StandardCharsets.UTF_8)
            .stream()
            .filter(line -> !line.isEmpty())
            .collect(Collectors.toCollection(TreeSet::new));
        Set<String> utilsPresent = sources.keySet()
            .stream()
            .filter(path -> path.startsWith("utils/"))
            .collect(Collectors.toCollection(TreeSet::new));
        Set<String> unlisted = new TreeSet<>(utilsPresent);
        unlisted.removeAll(utilsListed);
        Set<String> stale = new TreeSet<>(utilsListed);
        stale.removeAll(utilsPresent);
        check(
            unlisted.isEmpty(),
            "new utils/ files must be added to " + UTILS_LIST
                + " so the reviewer sees them; shared code only, one system's code lives in that system: "
                + unlisted);
        check(stale.isEmpty(), "remove deleted or moved files from " + UTILS_LIST + ": " + stale);

        Map<String, Integer> registered = mixinRegistrations(sources.get("mixins/Mixins.java"));
        Set<String> mixinClasses = sources.entrySet()
            .stream()
            .filter(
                entry -> (entry.getKey()
                    .startsWith("mixins/early/")
                    || entry.getKey()
                        .startsWith("mixins/late/"))
                    && entry.getValue()
                        .contains("@Mixin("))
            .map(entry -> mixinKey(entry.getKey()))
            .collect(Collectors.toCollection(TreeSet::new));
        check(registered.size() > 200, "the mixin registry scan reads Mixins.java, found " + registered.size());
        check(
            !registered.containsKey("unknown"),
            "every Mixins.java entry sets its phase or uses the early Side constructor");
        Set<String> registeredTwice = registered.entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toCollection(TreeSet::new));
        check(registeredTwice.isEmpty(), "mixins registered more than once in Mixins.java: " + registeredTwice);
        Set<String> withoutClass = new TreeSet<>(registered.keySet());
        withoutClass.removeAll(mixinClasses);
        check(
            withoutClass.isEmpty(),
            "Mixins.java registers mixins that have no class in that phase's package: " + withoutClass);
        Set<String> unregistered = new TreeSet<>(mixinClasses);
        unregistered.removeAll(registered.keySet());
        check(
            unregistered.isEmpty(),
            "mixin classes that Mixins.java never registers, so they never apply: " + unregistered);

        String snapshot = sources.get("loader/CompatSnapshot.java");
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            Matcher savedData = SAVED_DATA_CLASS.matcher(entry.getValue());
            if (savedData.find()) check(
                snapshot.contains(savedData.group(1) + ".class,"),
                entry.getKey() + " is saved under a name; list it in CompatSnapshot.SAVED_DATA so a rename shows up");
        }

        Matcher loaderOrder = LOADER_ENTRY.matcher(
            sources.get("loader/ContentLoaders.java")
                .split("LOADERS = List.of\\(", 2)[1].split("\\);", 2)[0]);
        List<String> order = new ArrayList<>();
        while (loaderOrder.find()) order.add(loaderOrder.group(1));
        check(
            order.size() >= RELEASED_LOADER_ORDER.size() && order.subList(0, RELEASED_LOADER_ORDER.size())
                .equals(RELEASED_LOADER_ORDER),
            "ContentLoaders must keep the released card order and add new cards at the end: " + order);
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String path = entry.getKey();
            if (!path.startsWith("compat/") && !path.startsWith("loader/") && !MOD_CHECK_ALLOWED.contains(path)) check(
                !entry.getValue()
                    .contains(".isModLoaded()"),
                path + " checks whether a mod is loaded; code that only matters with that mod lives in compat/<mod>/,"
                    + " and optional items use ModsItemlist.getOr or getIfLoaded");
        }
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String path = entry.getKey();
            if (MUI1_ALLOWED.stream()
                .noneMatch(path::startsWith))
                check(
                    !entry.getValue()
                        .contains("import com.gtnewhorizons.modularui"),
                    path + " imports ModularUI 1; new screens and storage use ModularUI 2");
        }
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String path = entry.getKey();
            if (path.startsWith("common/machine/") && !path.startsWith("common/machine/multiMachineBase/")
                && !path.startsWith("common/machine/tooltip/")
                && !GT_BASE_ALLOWED.contains(path))
                check(
                    !GT_MULTIBLOCK_BASE.matcher(entry.getValue())
                        .find(),
                    path + " extends a GT multiblock base directly; extend the GTNL base for that family"
                        + " (MultiMachineBase, SteamMultiMachineBase, TTMultiMachineBase, EnhancedMultiMachineBase)");
        }
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            if (isMixinClass(entry.getKey(), entry.getValue())) check(
                longestMethodBody(entry.getValue()) <= LONG_METHOD_LINES,
                entry.getKey() + " has a method longer than "
                    + LONG_METHOD_LINES
                    + " lines; mixins only hook, the logic goes into the system's hooks");
        }
        ratchet(
            sources,
            "common files outside render/ and gui/ importing client classes",
            MAX_CLIENT_IMPORTS_IN_COMMON,
            (path, text) -> path.startsWith("common/") && !path.startsWith("common/render/")
                && !path.startsWith("common/gui/")
                && text.contains("\nimport net.minecraft.client."));

        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String path = entry.getKey(), text = entry.getValue();
            if (!path.equals(MANUAL_EVENT_REGISTRATION)) check(
                !EVENT_REGISTRATION.matcher(text)
                    .find(),
                path + " registers an event handler by hand; annotate the class with @EventBusSubscriber instead");
            check(
                !FINAL_CLASS.matcher(text)
                    .find(),
                path + " declares a final class; the lead allows final on fields and methods only (records are fine)");
            Matcher declaration = PACKAGE_PRIVATE_TYPE.matcher(text);
            boolean interfaceFile = INTERFACE_FILE.matcher(text)
                .find();
            while (declaration.find()) check(
                declaration.group(1) != null && interfaceFile,
                path + " declares "
                    + declaration.group(2)
                    + " without an access modifier; write public, protected or private");
            if (text.contains("@EventBusSubscriber")) {
                Matcher handler = SUBSCRIBE_METHOD.matcher(text);
                while (handler.find()) check(
                    handler.group(1)
                        .contains("static"),
                    path + " handler "
                        + handler.group(2)
                        + " is not static; GTNHLib refuses to start with a non-static @SubscribeEvent method");
            }
            if ((path.startsWith("common/item/") || path.startsWith("common/block/")) && SELF_REGISTRATION.matcher(text)
                .find())
                check(
                    SELF_REGISTERING.contains(path),
                    path + " registers itself in its constructor; register new items and blocks through ItemLoader.ITEMS"
                        + " or BlockLoader.BLOCKS");
            if (path.startsWith("common/machine/cover/") && text.contains(" getCoverGui()")) check(
                text.contains(" createWindow(CoverUIBuildContext "),
                path + " has a settings screen but no ModularUI 1 window; GT machines that still use ModularUI 1"
                    + " open cover tabs through createWindow");
            if (path.startsWith("utils/") && !API_PINNED.contains(path)) check(
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

        CompatLoader.Entry needsMissingMod = new CompatLoader.Entry(
            fakeMod(true),
            () -> { throw new AssertionError("created"); },
            fakeMod(true),
            fakeMod(false));
        check(
            !needsMissingMod.modsLoaded()
                && new CompatLoader.Entry(fakeMod(true), () -> null, fakeMod(true)).modsLoaded(),
            "a compat entry is skipped before its class is created when any mod it lists is missing");
    }

    private static IMod fakeMod(boolean loaded) {
        return new IMod() {

            @Override
            public boolean isModLoaded() {
                return loaded;
            }

            @Override
            public String getID() {
                return "fake";
            }

            @Override
            public String getResourceLocation() {
                return "fake";
            }
        };
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

    // Keys are "early:pkg.Name" or "late:pkg.Name"; the Side constructor registers early mixins.
    private static Map<String, Integer> mixinRegistrations(String registry) {
        String body = registry.split("implements IMixins \\{", 2)[1].split("private final MixinBuilder builder;", 2)[0];
        Map<String, Integer> counts = new TreeMap<>();
        for (String entry : MIXIN_ENTRY_START.split(body)) {
            if (!entry.matches("(?s)[A-Z][A-Z_0-9]*\\(.*")) continue;
            String phase;
            if (entry.matches("(?s)[A-Z][A-Z_0-9]*\\(Side\\..*") || entry.contains("Phase.EARLY")) phase = "early";
            else if (entry.contains("Phase.LATE")) phase = "late";
            else {
                counts.merge("unknown", 1, Integer::sum);
                continue;
            }
            Matcher name = MIXIN_NAME.matcher(entry);
            while (name.find()) counts.merge(phase + ":" + name.group(1), 1, Integer::sum);
        }
        return counts;
    }

    private static String mixinKey(String path) {
        String rest = path.substring("mixins/".length(), path.length() - ".java".length());
        int slash = rest.indexOf('/');
        return rest.substring(0, slash) + ":"
            + rest.substring(slash + 1)
                .replace('/', '.');
    }

    // Accessor and invoker interfaces only declare; the mixin checks are about classes that carry code.
    private static boolean isMixinClass(String path, String text) {
        return path.startsWith("mixins/") && text.contains("@Mixin")
            && !MIXIN_INTERFACE.matcher(text)
                .find();
    }

    // Lines inside the longest class-level method body, in the formatted layout: members start four spaces in and
    // their bodies end on a line that is four spaces and a closing brace.
    private static int longestMethodBody(String text) {
        String[] lines = text.split("\\r?\\n");
        int longest = 0;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.startsWith("    ") || line.startsWith("     ")) continue;
            String member = line.trim();
            if (member.startsWith("@") || member.startsWith("/") || member.startsWith("*") || member.equals("}"))
                continue;
            int open = i;
            while (open < lines.length && !lines[open].endsWith("{") && !lines[open].endsWith(";")) open++;
            if (open >= lines.length || lines[open].endsWith(";") || !lines[open].contains(")")) continue;
            int close = open + 1;
            while (close < lines.length && !lines[close].equals("    }")) close++;
            longest = Math.max(longest, close - open - 1);
            i = close;
        }
        return longest;
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
