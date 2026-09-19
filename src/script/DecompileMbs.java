import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DecompileMbs {

    public static void main(String[] args) throws IOException {
        Path root = MbsToolPaths.resourceMultiblockRoot();
        String target = args.length > 0 ? args[0] : readTarget();
        if (target == null || target.trim().isEmpty()) {
            throw new IllegalArgumentException("必须输入要处理的目标路径");
        }

        target = target.trim();
        if ("*".equals(target)) {
            for (Path source : MbsToolPaths.findFiles(root, ".mbs")) {
                Path output = decompileFile(source);
                System.out.println("已反编译：" + MbsToolPaths.relative(root, source) + " -> "
                    + MbsToolPaths.relative(root, output));
            }
            return;
        }

        Path source = root.resolve(target).toAbsolutePath().normalize();
        Path output = decompileFile(source);
        System.out.println("已反编译：" + source.getFileName() + " -> " + output.getFileName());
    }

    public static Path decompileFile(Path source) throws IOException {
        Path output = MbsToolPaths.replaceExtension(source, "txt");
        Files.write(
            output,
            MbsFileFormat.toCsvText(MbsFileFormat.read(source))
                .getBytes(StandardCharsets.UTF_8));
        return output;
    }

    private static String readTarget() throws IOException {
        System.out.println("该工具会把 .mbs 二进制文件反编译为当前 .mb 样式的文本。");
        System.out.print("请输入相对 multiblock 资源目录的 .mbs 路径，或输入 * 处理全部：");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            return input.readLine();
        }
    }
}
