import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConvertToMbs {

    private static final BufferedReader INPUT = new BufferedReader(
        new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public static void main(String[] args) throws IOException {
        String choice = args.length > 0 ? args[0] : readLine(
            "1. 将 input.txt 转成新的 .mb 源文件和 .mbs 二进制文件\n"
                + "2. 仅批量刷新已有 .mb 源文件对应的 .mbs 文件\n"
                + "请选择模式 [1/2，默认 2]：");
        choice = choice == null ? "" : choice.trim();
        if (choice.isEmpty()) choice = "2";

        if ("1".equals(choice)) {
            String outputName = args.length > 1 ? args[1] : readLine("请输入输出文件名（不带扩展名）：");
            Path[] outputs = convertInputFile(outputName == null ? "" : outputName.trim());
            System.out.println("已生成源文件：" + outputs[0]);
            System.out.println("已生成二进制文件：" + outputs[1]);
        }

        int converted = batchConvertExistingMbFiles();
        System.out.println("已刷新 " + converted + " 个 .mbs 文件");
    }

    public static Path[] convertInputFile(String outputName) throws IOException {
        if (outputName == null || outputName.isEmpty()) {
            throw new IllegalArgumentException("输出文件名不能为空");
        }

        List<List<String>> structure = parseInputStructure(
            new String(Files.readAllBytes(MbsToolPaths.inputFile()), StandardCharsets.UTF_8));
        Path mbPath = MbsToolPaths.sourceMultiblockRoot().resolve(outputName + ".mb");
        Path mbsPath = MbsToolPaths.resourceMultiblockRoot().resolve(outputName + ".mbs");
        Files.createDirectories(mbPath.getParent());
        Files.createDirectories(mbsPath.getParent());
        Files.write(mbPath, MbsFileFormat.toCsvText(structure).getBytes(StandardCharsets.UTF_8));
        MbsFileFormat.write(mbsPath, structure);
        return new Path[] { mbPath, mbsPath };
    }

    public static int batchConvertExistingMbFiles() throws IOException {
        Path sourceRoot = MbsToolPaths.sourceMultiblockRoot();
        if (!Files.isDirectory(sourceRoot)) {
            throw new IOException("未找到源文件目录：" + sourceRoot);
        }

        int converted = 0;
        for (Path source : MbsToolPaths.findFiles(sourceRoot, ".mb")) {
            Path relative = sourceRoot.toAbsolutePath().normalize().relativize(source.toAbsolutePath().normalize());
            Path output = MbsToolPaths.resourceMultiblockRoot().resolve(
                MbsToolPaths.replaceExtension(relative, "mbs"));
            Files.createDirectories(output.getParent());
            MbsFileFormat.write(output, MbsFileFormat.parseCsvText(
                new String(Files.readAllBytes(source), StandardCharsets.UTF_8)));
            converted++;
        }
        return converted;
    }

    public static List<List<String>> parseInputStructure(String inputText) {
        String[] lines = inputText.split("\\R", -1);
        int startIndex = -1;
        int endIndex = -1;
        for (int index = 0; index < lines.length; index++) {
            if (lines[index].contains("new String[][]{{")) {
                startIndex = index + 1;
                continue;
            }
            if (startIndex >= 0 && lines[index].trim().equals("}}")) {
                endIndex = index;
                break;
            }
        }
        if (startIndex < 0 || endIndex < 0 || endIndex <= startIndex) {
            throw new IllegalArgumentException("input.txt does not contain a valid structure scan block");
        }

        Character specialLetter = findSpecialLetter(inputText);
        List<List<String>> blocks = new ArrayList<>();
        List<String> currentBlock = new ArrayList<>();
        for (int index = startIndex; index < endIndex; index++) {
            String stripped = lines[index].trim();
            if (stripped.isEmpty()) continue;
            if (stripped.equals("},{")) {
                if (!currentBlock.isEmpty()) {
                    blocks.add(currentBlock);
                    currentBlock = new ArrayList<>();
                }
                continue;
            }

            String cleaned = stripped.endsWith(",") ? stripped.substring(0, stripped.length() - 1) : stripped;
            if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
                String row = cleaned.substring(1, cleaned.length() - 1);
                currentBlock.add(replaceSpecialMarker(row, specialLetter));
            }
        }
        if (!currentBlock.isEmpty()) blocks.add(currentBlock);
        if (blocks.isEmpty()) {
            throw new IllegalArgumentException("No structure rows were parsed from input.txt");
        }

        int rowCount = blocks.get(0).size();
        for (List<String> block : blocks) {
            if (block.size() != rowCount) {
                throw new IllegalArgumentException("Inconsistent layer heights found in input.txt");
            }
        }

        List<List<String>> structure = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            List<String> row = new ArrayList<>();
            for (List<String> block : blocks) {
                row.add(block.get(rowIndex));
            }
            structure.add(row);
        }
        return structure;
    }

    private static Character findSpecialLetter(String inputText) {
        boolean inSpecialTiles = false;
        for (String rawLine : inputText.split("\\R", -1)) {
            String stripped = rawLine.trim();
            if (stripped.startsWith("Special Tiles:")) {
                inSpecialTiles = true;
                continue;
            }
            if (inSpecialTiles && stripped.startsWith("Offsets:")) break;
            if (inSpecialTiles && stripped.contains("ofSpecialTileAdder") && stripped.contains("->")) {
                return stripped.substring(0, stripped.indexOf("->")).trim().charAt(0);
            }
        }
        return null;
    }

    private static String replaceSpecialMarker(String row, Character specialLetter) {
        if (specialLetter == null) return row;
        StringBuilder result = new StringBuilder(row.length());
        char letter = specialLetter;
        for (int index = 0; index < row.length(); index++) {
            char current = row.charAt(index);
            result.append(current == letter || current == Character.toLowerCase(letter) ? '~' : current);
        }
        return result.toString();
    }

    private static String readLine(String prompt) throws IOException {
        System.out.print(prompt);
        return INPUT.readLine();
    }
}
