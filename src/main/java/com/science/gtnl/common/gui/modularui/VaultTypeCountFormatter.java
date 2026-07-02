package com.science.gtnl.common.gui.modularui;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.utils.Alignment;

import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEStackType;

public class VaultTypeCountFormatter {

    private static final String LINE_SEPARATOR = "\n";
    private static final String FIELD_SEPARATOR = "\t";
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private VaultTypeCountFormatter() {}

    public static List<IWidget> createTypeCountRows(String payload, String translationKey) {
        List<TypeCountLine> lines = parsePayload(payload);
        if (lines.isEmpty()) {
            return Collections.emptyList();
        }

        List<IWidget> rows = new ArrayList<>(lines.size());
        for (TypeCountLine line : lines) {
            rows.add(createTypeCountRow(line, translationKey));
        }
        return rows;
    }

    public static String createTypeCountText(String payload, String translationKey) {
        List<TypeCountLine> lines = parsePayload(payload);
        if (lines.isEmpty()) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (TypeCountLine line : lines) {
            if (text.length() > 0) {
                text.append(LINE_SEPARATOR);
            }
            text.append(formatLine(line, translationKey));
        }
        return text.toString();
    }

    private static IWidget createTypeCountRow(TypeCountLine line, String translationKey) {
        return IKey.dynamic(() -> formatLine(line, translationKey))
            .asWidget()
            .textAlign(Alignment.CenterLeft)
            .fullWidth();
    }

    private static String formatLine(TypeCountLine line, String translationKey) {
        return StatCollector.translateToLocalFormatted(
            translationKey,
            getTypeName(line.typeId()),
            NUMBER_FORMAT.format(line.usedTypes()),
            NUMBER_FORMAT.format(line.maxTypes()));
    }

    private static List<TypeCountLine> parsePayload(String payload) {
        if (payload == null || payload.isEmpty()) {
            return Collections.emptyList();
        }

        String[] rawLines = payload.split(LINE_SEPARATOR);
        List<TypeCountLine> lines = new ArrayList<>(rawLines.length);
        for (String rawLine : rawLines) {
            TypeCountLine line = parseLine(rawLine);
            if (line != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static TypeCountLine parseLine(String rawLine) {
        if (rawLine == null || rawLine.isEmpty()) {
            return null;
        }

        String[] fields = rawLine.split(FIELD_SEPARATOR, -1);
        if (fields.length != 3) {
            return null;
        }

        try {
            return new TypeCountLine(fields[0], Long.parseLong(fields[1]), Long.parseLong(fields[2]));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String getTypeName(String typeId) {
        IAEStackType<?> type = AEStackTypeRegistry.getType(typeId);
        if (type != null) {
            String displayName = type.getDisplayName();
            if (displayName != null && !displayName.isEmpty() && !displayName.equals(typeId)) {
                return displayName;
            }
        }
        if (StatCollector.canTranslate(typeId)) {
            return StatCollector.translateToLocal(typeId);
        }
        if (StatCollector.canTranslate(typeId + ".name")) {
            return StatCollector.translateToLocal(typeId + ".name");
        }
        return typeId;
    }

    public record TypeCountLine(String typeId, long usedTypes, long maxTypes) {}
}
