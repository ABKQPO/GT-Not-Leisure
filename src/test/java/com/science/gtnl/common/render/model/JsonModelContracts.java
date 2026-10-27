package com.science.gtnl.common.render.model;

import static com.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.RESOURCES;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;

public class JsonModelContracts {

    private static final Path ASSETS = RESOURCES.getParent();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(JSONModel.class, new ModelDeserializer())
        .create();

    private JsonModelContracts() {}

    public static void run() throws IOException {
        List<Path> models = modelFiles();
        check(!models.isEmpty(), "the jar ships JSON block models under assets/<domain>/models");
        for (Path file : models) {
            String name = ASSETS.relativize(file)
                .toString()
                .replace('\\', '/');
            JSONModel model;
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                model = GSON.fromJson(reader, JSONModel.class);
            } catch (RuntimeException exception) {
                check(
                    false,
                    name + " parses as a GTNHLib block model, otherwise GTNHLib skips the textures of every"
                        + " model in the jar and they render as missing textures: "
                        + exception);
                continue;
            }
            String domain = name.substring(0, name.indexOf('/'));
            for (String texture : model.getTextures()
                .values()) {
                if (texture.startsWith("#")) continue;
                int colon = texture.indexOf(':');
                String textureDomain = colon < 0 ? "minecraft" : texture.substring(0, colon);
                String texturePath = texture.substring(colon + 1);
                if (!textureDomain.equals(domain)) continue;
                if (texturePath.indexOf(':') >= 0) {
                    check(false, name + " texture " + texture + " is written as domain:path");
                    continue;
                }
                Path png = ASSETS.resolve(domain)
                    .resolve("textures")
                    .resolve(texturePath + ".png");
                check(Files.isRegularFile(png), name + " uses texture " + texture + " that exists");
            }
        }
    }

    private static List<Path> modelFiles() throws IOException {
        try (Stream<Path> files = Files.walk(ASSETS)) {
            return files.filter(path -> isModelJson(ASSETS.relativize(path)))
                .sorted()
                .collect(Collectors.toList());
        }
    }

    private static boolean isModelJson(Path relative) {
        return relative.getNameCount() >= 3 && "models".equals(
            relative.getName(1)
                .toString())
            && relative.toString()
                .endsWith(".json");
    }
}
