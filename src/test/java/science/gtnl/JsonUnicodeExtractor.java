package science.gtnl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonUnicodeExtractor {

    public static String decodeUnicodeOnly(String s) {
        if (s == null) return null;
        Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher matcher = pattern.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                int code = Integer.parseInt(matcher.group(1), 16);
                matcher.appendReplacement(sb, Character.toString((char) code));
            } catch (NumberFormatException e) {
                matcher.appendReplacement(sb, matcher.group(0));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    // 从文本中提取 name:8 和 desc:8
    public static String[] extractFieldsFromText(String text) {
        if (text == null) return new String[] { "", "" };

        Pattern namePattern = Pattern.compile("\"name:8\"\\s*:\\s*\"([^\"]*)\"");
        Pattern descPattern = Pattern.compile("\"desc:8\"\\s*:\\s*\"([^\"]*)\"");

        Matcher nameMatcher = namePattern.matcher(text);
        Matcher descMatcher = descPattern.matcher(text);

        String name = nameMatcher.find() ? decodeUnicodeOnly(
            nameMatcher.group(1)
                .trim())
            : "";
        String desc = descMatcher.find() ? decodeUnicodeOnly(
            descMatcher.group(1)
                .trim())
            : "";

        return new String[] { name, desc };
    }

    public static String[] processJsonFile(File file) {
        String filename = file.getName();
        if (!filename.contains("-")) {
            System.out.println("[跳过] 文件名没有 '-': " + filename);
            return null;
        }

        String keyPart = filename.split("-", 2)[1].split("=")[0].replace(".json", "")
            .trim();

        String content;
        try {
            content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("[跳过] 无法读取 " + file.getAbsolutePath() + ": " + e.getMessage());
            return null;
        }

        String[] fields = extractFieldsFromText(content);
        return new String[] { keyPart, fields[0], fields[1] };
    }

    // 扫描文件夹
    public static void processFolder(String folderPath, String outputFile) {
        File root = new File(folderPath);
        if (!root.exists() || !root.isDirectory()) {
            System.out.println("路径无效: " + folderPath);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {

            int count = 0;

            for (File file : Files.walk(root.toPath())
                .map(Path::toFile)
                .collect(Collectors.toList())) {
                if (file.isFile() && file.getName()
                    .endsWith(".json")) {
                    String[] result = processJsonFile(file);
                    if (result != null) {
                        String key = result[0];
                        String name = result[1];
                        String desc = result[2];

                        writer.write("betterquesting.quest." + key + ".name=" + name);
                        writer.newLine();
                        writer.write("betterquesting.quest." + key + ".desc=" + desc);
                        writer.newLine();
                        count++;
                    }
                }
            }

            System.out.println("✅ 处理完成，共生成 " + count + " 条记录，输出到 " + outputFile);

        } catch (IOException e) {
            System.err.println("写入文件出错: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.print("请输入要扫描的文件夹路径: ");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String folder = reader.readLine()
                .trim();
            processFolder(folder, "output_lang.txt");
        } catch (IOException e) {
            System.err.println("读取输入出错: " + e.getMessage());
        }
    }
}
