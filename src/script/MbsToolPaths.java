import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MbsToolPaths {

    private static final String RESOURCE_PATH = "src/main/resources/assets/sciencenotleisure/multiblock";

    public static Path pythonDirectory() {
        Path workingDirectory = Paths.get("").toAbsolutePath().normalize();
        if (isScriptDirectory(workingDirectory)) {
            return workingDirectory;
        }

        Path candidate = workingDirectory.resolve("src").resolve("script");
        if (Files.isDirectory(candidate)) {
            return candidate.toAbsolutePath().normalize();
        }

        Path legacyCandidate = workingDirectory.resolve(RESOURCE_PATH).resolve("python");
        if (Files.isDirectory(legacyCandidate)) {
            return legacyCandidate.toAbsolutePath().normalize();
        }
        return workingDirectory;
    }

    public static Path resourceMultiblockRoot() {
        return projectRoot().resolve(RESOURCE_PATH);
    }

    public static Path sourceMultiblockRoot() {
        return projectRoot().resolve("multiblock");
    }

    public static Path inputFile() {
        Path scriptInput = pythonDirectory().resolve("input.txt");
        if (Files.isRegularFile(scriptInput)) {
            return scriptInput;
        }
        return projectRoot().resolve(RESOURCE_PATH).resolve("python").resolve("input.txt");
    }

    public static Path replaceExtension(Path path, String extension) {
        String fileName = fileName(path);
        int dot = fileName.lastIndexOf('.');
        String replacement = (dot < 0 ? fileName : fileName.substring(0, dot)) + "." + extension;
        return path.resolveSibling(replacement);
    }

    public static List<Path> findFiles(Path root, String extension) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(extension))
                .sorted(Comparator.comparing(Path::toString))
                .collect(Collectors.toList());
        }
    }

    public static String relative(Path root, Path path) {
        return root.toAbsolutePath().normalize().relativize(path.toAbsolutePath().normalize()).toString();
    }

    private static String fileName(Path path) {
        Path fileName = path == null ? null : path.getFileName();
        return fileName == null ? "" : fileName.toString();
    }

    private static boolean isScriptDirectory(Path path) {
        return "script".equals(fileName(path))
            && "src".equals(fileName(path == null ? null : path.getParent()));
    }

    private static Path projectRoot() {
        Path current = pythonDirectory();
        while (current != null) {
            if (Files.isDirectory(current.resolve(RESOURCE_PATH))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("未找到 GT-Not-Leisure 工程根目录");
    }
}
