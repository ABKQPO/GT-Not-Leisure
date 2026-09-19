import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CompileMbs {

    public static void main(String[] args) throws IOException {
        Path root = MbsToolPaths.resourceMultiblockRoot();
        String target = args.length > 0 ? args[0] : readTarget();
        if (target == null || target.trim().isEmpty()) {
            throw new IllegalArgumentException("必须输入要处理的目标路径");
        }

        target = target.trim();
        if ("*".equals(target)) {
            Path pythonDirectory = MbsToolPaths.pythonDirectory().toAbsolutePath().normalize();
            for (Path source : MbsToolPaths.findFiles(root, ".txt")) {
                if (source.toAbsolutePath().normalize().startsWith(pythonDirectory)) {
                    continue;
                }
                Path output = compileFile(source);
                System.out.println("已编译：" + MbsToolPaths.relative(root, source) + " -> "
                    + MbsToolPaths.relative(root, output));
            }
            return;
        }

        Path source = root.resolve(target).toAbsolutePath().normalize();
        Path output = compileFile(source);
        System.out.println("已编译：" + source.getFileName() + " -> " + output.getFileName());
    }

    public static Path compileFile(Path source) throws IOException {
        List<List<String>> structure = MbsFileFormat.parseCsvText(
            new String(Files.readAllBytes(source), StandardCharsets.UTF_8));
        Path output = MbsToolPaths.replaceExtension(source, "mbs");
        MbsFileFormat.write(output, structure);
        return output;
    }

    private static String readTarget() throws IOException {
        System.out.println("该工具会把 decompile_mbs.py 输出的文本解析为 .mbs 二进制文件。");
        System.out.print("请输入相对 multiblock 资源目录的 .txt 路径，或输入 * 处理全部：");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            return input.readLine();
        }
    }
}
