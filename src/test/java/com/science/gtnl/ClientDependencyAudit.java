package com.science.gtnl;

import static com.gtnltest.TestSupport.check;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Exact legacy references cannot be traded for new dependencies by staying below a total count. */
public final class ClientDependencyAudit {

    private static final Path BASELINE = Paths.get("src/test/resources/com/science/gtnl/client-dependencies.tsv");
    private static final Pattern CLIENT_TYPE = Pattern
        .compile("\\bnet\\.minecraft\\.client\\.(?:[a-z][\\w$]*\\.)*(?:[A-Z][\\w$]*|\\*)");

    private ClientDependencyAudit() {}

    public static void run(Map<String, String> sources) throws IOException {
        Set<String> allowed = new TreeSet<>();
        for (String line : Files.readAllLines(BASELINE)) {
            if (line.isBlank() || line.startsWith("#")) continue;
            String[] parts = line.split("\\t", 3);
            check(parts.length == 3 && !parts[2].isBlank(), "client dependency needs a reason: " + line);
            if (parts.length == 3) check(allowed.add(parts[0] + "\t" + parts[1]), "duplicate dependency: " + line);
        }
        Set<String> actual = references(sources);
        Set<String> added = new TreeSet<>(actual);
        added.removeAll(allowed);
        Set<String> removed = new TreeSet<>(allowed);
        removed.removeAll(actual);
        check(added.isEmpty(), "isolate new common-to-client dependencies or review an explicit exception: " + added);
        check(removed.isEmpty(), "remove resolved client dependency exceptions from " + BASELINE + ": " + removed);

        // A removed dependency must not buy permission for an unrelated new one.
        Set<String> old = references(Map.of("common/Old.java", "import net.minecraft.client.Minecraft;"));
        Set<String> swapped = references(Map.of("common/New.java", "net.minecraft.client.Minecraft.getMinecraft();"));
        check(old.size() == swapped.size() && !old.containsAll(swapped), "equal dependency counts do not hide swaps");

        Set<String> wildcard = references(
            Map.of("common/New.java", "import net.minecraft.client.*; import net.minecraft.client.renderer.*;"));
        check(
            wildcard.equals(
                Set.of("common/New.java\tnet.minecraft.client.*", "common/New.java\tnet.minecraft.client.renderer.*")),
            "wildcard imports cannot hide new client dependencies");
        check(
            references(Map.of("common/New.java", "import static net.minecraft.client.Minecraft.getMinecraft;"))
                .equals(Set.of("common/New.java\tnet.minecraft.client.Minecraft")),
            "static imports still record their client owner");
    }

    private static Set<String> references(Map<String, String> sources) {
        Set<String> references = new TreeSet<>();
        sources.forEach((path, source) -> {
            if (!path.startsWith("common/") || path.startsWith("common/render/") || path.startsWith("common/gui/"))
                return;
            Matcher matcher = CLIENT_TYPE.matcher(source);
            while (matcher.find()) references.add(path + "\t" + matcher.group());
        });
        return references;
    }
}
