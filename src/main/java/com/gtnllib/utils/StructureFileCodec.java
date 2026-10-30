package com.gtnllib.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class StructureFileCodec {

    private static final byte[] MAGIC = new byte[] { 'M', 'B', 'S', '1' };

    public static String[][] readText(InputStream inputStream) throws IOException {
        List<String[]> rows = new ObjectArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line.split(",", -1));
            }
        }
        return rows.toArray(new String[0][]);
    }

    public static String[][] readBinary(InputStream inputStream) throws IOException {
        try (DataInputStream dataInput = new DataInputStream(new BufferedInputStream(inputStream))) {
            for (byte expected : MAGIC) {
                if (dataInput.readByte() != expected) {
                    throw new IOException("Invalid multiblock structure header");
                }
            }

            int stringCount = dataInput.readInt();
            validateSize(stringCount, "string table size");
            String[] stringTable = new String[stringCount];
            for (int index = 0; index < stringCount; index++) {
                int byteLength = dataInput.readInt();
                validateSize(byteLength, "string byte length");
                byte[] bytes = new byte[byteLength];
                dataInput.readFully(bytes);
                stringTable[index] = new String(bytes, StandardCharsets.UTF_8);
            }

            int rowCount = dataInput.readInt();
            validateSize(rowCount, "row count");
            String[][] structure = new String[rowCount][];
            for (int row = 0; row < rowCount; row++) {
                int columnCount = dataInput.readInt();
                validateSize(columnCount, "column count");
                structure[row] = new String[columnCount];
                for (int column = 0; column < columnCount; column++) {
                    int stringIndex = dataInput.readInt();
                    if (stringIndex < 0 || stringIndex >= stringTable.length) {
                        throw new IOException("Invalid string table index in multiblock structure file");
                    }
                    structure[row][column] = stringTable[stringIndex];
                }
            }
            return structure;
        }
    }

    public static void writeBinary(OutputStream outputStream, String[][] structure) throws IOException {
        try (DataOutputStream dataOutput = new DataOutputStream(outputStream)) {
            Object2IntLinkedOpenHashMap<String> stringIndexes = new Object2IntLinkedOpenHashMap<>();
            for (String[] row : structure) {
                for (String cell : row) {
                    if (!stringIndexes.containsKey(cell)) {
                        stringIndexes.put(cell, stringIndexes.size());
                    }
                }
            }

            dataOutput.write(MAGIC);
            dataOutput.writeInt(stringIndexes.size());
            for (String cell : stringIndexes.keySet()) {
                byte[] bytes = cell.getBytes(StandardCharsets.UTF_8);
                dataOutput.writeInt(bytes.length);
                dataOutput.write(bytes);
            }
            dataOutput.writeInt(structure.length);
            for (String[] row : structure) {
                dataOutput.writeInt(row.length);
                for (String cell : row) {
                    dataOutput.writeInt(stringIndexes.getInt(cell));
                }
            }
            dataOutput.flush();
        }
    }

    private static void validateSize(int value, String label) throws IOException {
        if (value < 0) {
            throw new IOException("Negative " + label + " in multiblock structure file");
        }
    }
}
