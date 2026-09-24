package com.science.gtnl.client.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MajoBroomModel {

    private static final String[] FACE_NAMES = { "north", "east", "south", "west", "up", "down" };
    private static final float[][] NORMALS = { { 0, 0, -1 }, { 1, 0, 0 }, { 0, 0, 1 }, { -1, 0, 0 }, { 0, 1, 0 },
        { 0, -1, 0 } };
    public static final MajoBroomModel INSTANCE = new MajoBroomModel("majo_broom.json");
    public static final MajoBroomModel ELAINA_INSTANCE = new MajoBroomModel("elaina_broom.json");
    private final List<Element> elements = new ArrayList<>();

    private MajoBroomModel(String modelFile) {
        String path = "/assets/sciencenotleisure/models/entity/" + modelFile;
        try (InputStream stream = MajoBroomModel.class.getResourceAsStream(path)) {
            if (stream == null) throw new IllegalStateException("Missing broom model: " + path);
            JsonObject root = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .getAsJsonObject();
            for (JsonElement entry : root.getAsJsonArray("elements")) {
                elements.add(new Element(entry.getAsJsonObject()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read broom model: " + path, e);
        }
    }

    public void render() {
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / 16.0F, 1.0F / 16.0F, 1.0F / 16.0F);
        GL11.glTranslatef(-8.0F, 0.0F, -8.0F);
        GL11.glColor4f(1, 1, 1, 1);
        boolean culling = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean normalize = GL11.glIsEnabled(GL11.GL_NORMALIZE);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_NORMALIZE);
        for (Element element : elements) element.render();
        if (culling) GL11.glEnable(GL11.GL_CULL_FACE);
        if (!normalize) GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glPopMatrix();
    }

    private static float[] vector(JsonArray value) {
        return new float[] { value.get(0).getAsFloat(), value.get(1).getAsFloat(), value.get(2).getAsFloat() };
    }

    private static final class Element {

        private final float[] from;
        private final float[] to;
        private final float[] pivot;
        private final float rotationX;
        private final float rotationY;
        private final float rotationZ;
        private final float[][] uv = new float[6][];

        private Element(JsonObject definition) {
            from = vector(definition.getAsJsonArray("from"));
            to = vector(definition.getAsJsonArray("to"));
            JsonObject rotation = definition.getAsJsonObject("rotation");
            pivot = rotation == null ? new float[] { 0, 0, 0 } : vector(rotation.getAsJsonArray("origin"));
            if (rotation != null && rotation.has("axis")) {
                float angle = rotation.get("angle")
                    .getAsFloat();
                String axis = rotation.get("axis")
                    .getAsString();
                rotationX = axis.equals("x") ? angle : 0;
                rotationY = axis.equals("y") ? angle : 0;
                rotationZ = axis.equals("z") ? angle : 0;
            } else {
                rotationX = rotation == null ? 0 : rotation.get("x")
                    .getAsFloat();
                rotationY = rotation == null ? 0 : rotation.get("y")
                    .getAsFloat();
                rotationZ = rotation == null ? 0 : rotation.get("z")
                    .getAsFloat();
            }
            JsonObject faces = definition.getAsJsonObject("faces");
            for (int i = 0; i < FACE_NAMES.length; i++) {
                if (!faces.has(FACE_NAMES[i])) continue;
                JsonArray coords = faces.getAsJsonObject(FACE_NAMES[i])
                    .getAsJsonArray("uv");
                uv[i] = new float[] { coords.get(0).getAsFloat() / 16.0F, coords.get(1).getAsFloat() / 16.0F,
                    coords.get(2).getAsFloat() / 16.0F, coords.get(3).getAsFloat() / 16.0F };
            }
        }

        private void render() {
            GL11.glPushMatrix();
            GL11.glTranslatef(pivot[0], pivot[1], pivot[2]);
            if (rotationZ != 0) GL11.glRotatef(rotationZ, 0, 0, 1);
            if (rotationY != 0) GL11.glRotatef(rotationY, 0, 1, 0);
            if (rotationX != 0) GL11.glRotatef(rotationX, 1, 0, 0);
            GL11.glTranslatef(-pivot[0], -pivot[1], -pivot[2]);

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            for (int face = 0; face < uv.length; face++) {
                if (uv[face] == null) continue;
                tessellator.setNormal(NORMALS[face][0], NORMALS[face][1], NORMALS[face][2]);
                drawFace(tessellator, face, uv[face]);
            }
            tessellator.draw();
            GL11.glPopMatrix();
        }

        private void drawFace(Tessellator t, int face, float[] tex) {
            float x1 = from[0], y1 = from[1], z1 = from[2];
            float x2 = to[0], y2 = to[1], z2 = to[2];
            float u1 = tex[0], v1 = tex[1], u2 = tex[2], v2 = tex[3];
            switch (face) {
                case 0 -> {
                    vertex(t, x1, y2, z1, u1, v1);
                    vertex(t, x2, y2, z1, u2, v1);
                    vertex(t, x2, y1, z1, u2, v2);
                    vertex(t, x1, y1, z1, u1, v2);
                }
                case 1 -> {
                    vertex(t, x2, y2, z1, u1, v1);
                    vertex(t, x2, y2, z2, u2, v1);
                    vertex(t, x2, y1, z2, u2, v2);
                    vertex(t, x2, y1, z1, u1, v2);
                }
                case 2 -> {
                    vertex(t, x2, y2, z2, u1, v1);
                    vertex(t, x1, y2, z2, u2, v1);
                    vertex(t, x1, y1, z2, u2, v2);
                    vertex(t, x2, y1, z2, u1, v2);
                }
                case 3 -> {
                    vertex(t, x1, y2, z2, u1, v1);
                    vertex(t, x1, y2, z1, u2, v1);
                    vertex(t, x1, y1, z1, u2, v2);
                    vertex(t, x1, y1, z2, u1, v2);
                }
                case 4 -> {
                    vertex(t, x1, y2, z2, u1, v1);
                    vertex(t, x2, y2, z2, u2, v1);
                    vertex(t, x2, y2, z1, u2, v2);
                    vertex(t, x1, y2, z1, u1, v2);
                }
                case 5 -> {
                    vertex(t, x1, y1, z1, u1, v1);
                    vertex(t, x2, y1, z1, u2, v1);
                    vertex(t, x2, y1, z2, u2, v2);
                    vertex(t, x1, y1, z2, u1, v2);
                }
                default -> throw new IllegalArgumentException("Unknown broom face: " + face);
            }
        }

        private static void vertex(Tessellator t, float x, float y, float z, float u, float v) {
            t.addVertexWithUV(x, y, z, u, v);
        }
    }
}
