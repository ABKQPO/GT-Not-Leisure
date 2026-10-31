package com.science.gtnl;

import static com.gtnltest.TestSupport.check;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

/** Mutable collection allocations require a reviewed API or behavioral compatibility reason. */
@Tag("architecture")
public final class CollectionUsageTest {

    private static final String TYPES = "ArrayList|HashMap|HashSet|LinkedHashMap|LinkedHashSet|TreeMap|TreeSet|LinkedList"
        + "|ArrayDeque|IdentityHashMap|PriorityQueue|Vector|Hashtable|TIntIntHashMap";
    private static final Pattern ALLOCATION = Pattern.compile(
        "\\bnew\\s+(?:java\\.util\\.)?(" + TYPES
            + ")\\b"
            + "|\\b("
            + TYPES
            + ")\\s*::\\s*new"
            + "|\\b(?:Lists|Sets|Maps)\\s*\\.\\s*new("
            + TYPES
            + ")(?:WithCapacity|WithExpectedSize)?\\s*\\("
            + "|\\bCollectors\\s*\\.\\s*to(List|Set|Map)\\s*\\("
            + "|\\b(MultimapBuilder)\\s*\\.\\s*hashKeys\\s*\\(");

    @Test
    void usesFastutilExceptAtDocumentedCompatibilityBoundaries() throws IOException {
        for (String source : List.of(
            "new ArrayList<>()",
            "new java.util.HashMap<>()",
            "ArrayList::new",
            "Lists.newArrayListWithCapacity(10)",
            "Sets.newHashSet()",
            "Collectors.toList()",
            "new TIntIntHashMap()",
            "MultimapBuilder.hashKeys().arrayListValues().build()")) {
            check(
                ALLOCATION.matcher(source)
                    .find(),
                "mutable collection allocation is audited: " + source);
        }
        check(
            !ALLOCATION.matcher("new ObjectArrayList<>()")
                .find(),
            "fastutil allocations are accepted");
        Map<String, Integer> expected = new Object2ObjectOpenHashMap<>();
        for (String line : Files.readAllLines(Path.of("src/test/resources/compat/collections-compatibility.txt"))) {
            if (line.isBlank() || line.startsWith("#")) continue;
            String[] columns = line.split("\t", 4);
            check(columns.length == 4 && !columns[3].isBlank(), "each remaining allocation has an explicit reason");
            check(
                expected.put(columns[0] + "\t" + columns[1], Integer.parseInt(columns[2])) == null,
                "collection exceptions are unique");
        }
        Map<String, Integer> actual = new Object2ObjectOpenHashMap<>();
        for (Path root : List.of(Path.of("src/main/java"), Path.of("gtnllib/src/main/java"))) {
            try (var sources = Files.walk(root)) {
                for (Path path : sources.filter(
                    p -> p.toString()
                        .endsWith(".java"))
                    .toList()) {
                    String source = Files.readString(path)
                        .replaceAll("(?s)/\\*.*?\\*/", "")
                        .replaceAll("(?m)^\\s*//.*$", "");
                    var matches = ALLOCATION.matcher(source);
                    while (matches.find()) {
                        String kind = matches.group(1) != null ? matches.group(1)
                            : matches.group(2) != null ? matches.group(2)
                                : matches.group(3) != null ? matches.group(3)
                                    : matches.group(4) != null ? "Collector" + matches.group(4) : matches.group(5);
                        String key = path.toString()
                            .replace('\\', '/') + "\t"
                            + kind;
                        actual.merge(key, 1, Integer::sum);
                    }
                }
            }
        }
        check(
            actual.equals(expected),
            "mutable collection exceptions changed; review compatibility reasons: " + actual);
    }
}
