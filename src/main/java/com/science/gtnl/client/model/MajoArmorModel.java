package com.science.gtnl.client.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Renders the original Majo's Broom armor geometry with Minecraft 1.7.10 model parts. */
@SideOnly(Side.CLIENT)
public final class MajoArmorModel extends ModelBiped {

    public static final MajoArmorModel HAT = new MajoArmorModel(true);
    public static final MajoArmorModel ROBE = new MajoArmorModel(false);

    private MajoArmorModel(boolean hat) {
        super(0.0F);
        textureWidth = 128;
        textureHeight = 128;

        bipedHead = new ModelRenderer(this);
        bipedHeadwear.showModel = false;
        bipedBody = new ModelRenderer(this);
        bipedRightArm = new ModelRenderer(this);
        bipedRightArm.setRotationPoint(-5, 2, 0);
        bipedLeftArm = new ModelRenderer(this);
        bipedLeftArm.setRotationPoint(5, 2, 0);
        bipedRightLeg.showModel = false;
        bipedLeftLeg.showModel = false;

        bipedHead.showModel = hat;
        bipedBody.showModel = !hat;
        bipedRightArm.showModel = !hat;
        bipedLeftArm.showModel = !hat;
        loadGeometry(hat ? "majo_hat.geo.json" : "majo_cloth.geo.json", hat);
    }

    private void loadGeometry(String fileName, boolean hat) {
        String path = "/assets/sciencenotleisure/geo/" + fileName;
        try (InputStream stream = MajoArmorModel.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Missing Majo armor geometry: " + path);
            }

            JsonObject geometry = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .getAsJsonObject()
                .getAsJsonArray("minecraft:geometry")
                .get(0)
                .getAsJsonObject();
            Map<String, Bone> bones = new HashMap<>();
            for (JsonElement element : geometry.getAsJsonArray("bones")) {
                JsonObject definition = element.getAsJsonObject();
                String name = definition.get("name")
                    .getAsString();
                if (!includeBone(name, hat)) continue;

                float[] pivot = coordinates(definition.getAsJsonArray("pivot"));
                ModelRenderer renderer = rootRenderer(name);
                if (renderer == null) {
                    String parentName = definition.get("parent")
                        .getAsString();
                    Bone parent = bones.get(parentName);
                    if (parent == null) {
                        throw new IllegalStateException("Missing Majo armor parent bone: " + parentName);
                    }
                    renderer = new ModelRenderer(this);
                    position(renderer, pivot, parent.pivot);
                    rotate(renderer, definition.getAsJsonArray("rotation"));
                    parent.renderer.addChild(renderer);
                }

                Bone bone = new Bone(renderer, pivot);
                bones.put(name, bone);
                JsonArray cubes = definition.getAsJsonArray("cubes");
                if (cubes != null) {
                    for (JsonElement cube : cubes) {
                        addCube(bone, cube.getAsJsonObject(), hat);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load Majo armor geometry: " + path, e);
        }
    }

    private static boolean includeBone(String name, boolean hat) {
        if (hat) return name.equals("armorHead") || name.equals("bone19");
        return name.equals("Body") || name.equals("bone") || name.equals("out") || name.equals("inner")
            || name.equals("armorRightArm") || name.equals("armorLeftArm");
    }

    private ModelRenderer rootRenderer(String name) {
        if (name.equals("armorHead")) return bipedHead;
        if (name.equals("Body")) return bipedBody;
        if (name.equals("armorRightArm")) return bipedRightArm;
        if (name.equals("armorLeftArm")) return bipedLeftArm;
        return null;
    }

    private void addCube(Bone bone, JsonObject definition, boolean hat) {
        float[] origin = coordinates(definition.getAsJsonArray("origin"));
        float[] size = coordinates(definition.getAsJsonArray("size"));
        JsonArray pivotValue = definition.getAsJsonArray("pivot");
        float[] pivot = pivotValue == null ? bone.pivot : coordinates(pivotValue);
        JsonArray uv = definition.getAsJsonArray("uv");
        int u = uv.get(0)
            .getAsInt();
        int v = uv.get(1)
            .getAsInt();
        float inflate = definition.has("inflate") ? definition.get("inflate")
            .getAsFloat() : 0;

        ModelRenderer cube = new ModelRenderer(this, u, v);
        cube.mirror = definition.has("mirror") && definition.get("mirror")
            .getAsBoolean();
        position(cube, pivot, bone.pivot);
        rotate(cube, definition.getAsJsonArray("rotation"));

        float x = origin[0] - pivot[0];
        float y = pivot[1] - origin[1] - size[1];
        float z = origin[2] - pivot[2];
        int width = (int) size[0];
        int height = (int) size[1];
        int depth = (int) size[2];
        if (hat && height == 0) {
            cube.cubeList.add(new MajoHatBrimBox(cube, u, v, x, y, z, width, depth));
        } else {
            cube.addBox(x, y, z, width, height, depth, inflate);
        }
        bone.renderer.addChild(cube);
    }

    private static float[] coordinates(JsonArray values) {
        return new float[] { values.get(0).getAsFloat(), values.get(1).getAsFloat(), values.get(2).getAsFloat() };
    }

    private static void position(ModelRenderer renderer, float[] pivot, float[] parentPivot) {
        renderer.setRotationPoint(pivot[0] - parentPivot[0], parentPivot[1] - pivot[1], pivot[2] - parentPivot[2]);
    }

    private static void rotate(ModelRenderer renderer, JsonArray degrees) {
        if (degrees == null) return;
        renderer.rotateAngleX = (float) Math.toRadians(degrees.get(0).getAsFloat());
        renderer.rotateAngleY = (float) Math.toRadians(degrees.get(1).getAsFloat());
        renderer.rotateAngleZ = (float) Math.toRadians(degrees.get(2).getAsFloat());
    }

    private static final class Bone {

        private final ModelRenderer renderer;
        private final float[] pivot;

        private Bone(ModelRenderer renderer, float[] pivot) {
            this.renderer = renderer;
            this.pivot = pivot;
        }
    }
}
