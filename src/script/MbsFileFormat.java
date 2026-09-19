import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MbsFileFormat {

    private static final byte[] MAGIC = { 'M', 'B', 'S', '1' };

    public static List<List<String>> parseCsvText(String text) {
        List<List<String>> structure = new ArrayList<>();
        for (String line : text.split("\\R", -1)) {
            if (!line.isEmpty()) {
                List<String> row = new ArrayList<>();
                for (String cell : line.split(",", -1)) {
                    row.add(cell);
                }
                structure.add(row);
            }
        }
        return structure;
    }

    public static void write(Path path, List<List<String>> structure) throws IOException {
        Map<String, Integer> indexes = new LinkedHashMap<>();
        for (List<String> row : structure) {
            for (String cell : row) {
                indexes.computeIfAbsent(cell, ignored -> indexes.size());
            }
        }

        try (DataOutputStream output = new DataOutputStream(
            new BufferedOutputStream(Files.newOutputStream(path)))) {
            output.write(MAGIC);
            output.writeInt(indexes.size());
            for (String cell : indexes.keySet()) {
                byte[] encoded = cell.getBytes(StandardCharsets.UTF_8);
                output.writeInt(encoded.length);
                output.write(encoded);
            }
            output.writeInt(structure.size());
            for (List<String> row : structure) {
                output.writeInt(row.size());
                for (String cell : row) {
                    output.writeInt(indexes.get(cell));
                }
            }
        }
    }

    public static List<List<String>> read(Path path) throws IOException {
        try (DataInputStream input = new DataInputStream(
            new BufferedInputStream(Files.newInputStream(path)))) {
            byte[] magic = new byte[MAGIC.length];
            input.readFully(magic);
            for (int index = 0; index < MAGIC.length; index++) {
                if (magic[index] != MAGIC[index]) {
                    throw new IOException(path + " 不是有效的 .mbs 文件");
                }
            }

            int stringCount = readCount(input, "字符串表");
            List<String> stringTable = new ArrayList<>(stringCount);
            for (int index = 0; index < stringCount; index++) {
                int byteLength = readCount(input, "字符串");
                byte[] encoded = new byte[byteLength];
                input.readFully(encoded);
                stringTable.add(new String(encoded, StandardCharsets.UTF_8));
            }

            int rowCount = readCount(input, "结构行");
            List<List<String>> structure = new ArrayList<>(rowCount);
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                int columnCount = readCount(input, "结构列");
                List<String> row = new ArrayList<>(columnCount);
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    int stringIndex = input.readInt();
                    if (stringIndex < 0 || stringIndex >= stringTable.size()) {
                        throw new IOException(".mbs 文件包含无效的字符串索引：" + stringIndex);
                    }
                    row.add(stringTable.get(stringIndex));
                }
                structure.add(row);
            }
            return structure;
        } catch (EOFException exception) {
            throw new IOException(".mbs 文件提前结束", exception);
        }
    }

    public static String toCsvText(List<List<String>> structure) {
        StringBuilder text = new StringBuilder();
        for (List<String> row : structure) {
            text.append(String.join(",", row)).append('\n');
        }
        return text.toString();
    }

    private static int readCount(DataInputStream input, String field) throws IOException {
        int count = input.readInt();
        if (count < 0) {
            throw new IOException(".mbs 文件包含无效的" + field + "数量：" + count);
        }
        return count;
    }
}
