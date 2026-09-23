package com.science.gtnl.common.render.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.JsonParseException;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.ModelElement;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.ModelElement.Face;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.ModelElement.Rotation;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position.ModelDisplay;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuad;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;

/** Bakes geometry, UV rotations and lighting once, following MyCTMLib's block model approach. */
public class JsonBlockModel extends JSONModel {

    private static final float UV_INSET_PIXELS = 1.0F / 32.0F;

    public JsonBlockModel(JSONModel model) {
        super(model);
        for (String key : textures.keySet()) {
            Set<String> visited = new HashSet<>();
            String texture = textures.get(key);
            while (texture.startsWith("#")) {
                if (!visited.add(texture) || !textures.containsKey(texture)) {
                    throw new JsonParseException("Unresolved or cyclic model texture: " + key);
                }
                texture = textures.get(texture);
            }
            textures.put(key, texture);
        }
    }

    public Geometry bake(ForgeDirection front, ForgeDirection up) {
        if (front == ForgeDirection.UNKNOWN || up == ForgeDirection.UNKNOWN
            || front == up
            || front == up.getOpposite()) {
            throw new IllegalArgumentException("Invalid model orientation");
        }
        ForgeDirection back = front.getOpposite();
        int rightX = front.offsetY * up.offsetZ - front.offsetZ * up.offsetY;
        int rightY = front.offsetZ * up.offsetX - front.offsetX * up.offsetZ;
        int rightZ = front.offsetX * up.offsetY - front.offsetY * up.offsetX;
        Matrix4f orientation = new Matrix4f().translation(0.5F, 0.5F, 0.5F)
            .mul(
                new Matrix4f().set(
                    rightX,
                    rightY,
                    rightZ,
                    0,
                    up.offsetX,
                    up.offsetY,
                    up.offsetZ,
                    0,
                    back.offsetX,
                    back.offsetY,
                    back.offsetZ,
                    0,
                    0,
                    0,
                    0,
                    1))
            .translate(-0.5F, -0.5F, -0.5F);
        ArrayList<ModelQuadView> quads = new ArrayList<>();
        ArrayList<ForgeDirection> culls = new ArrayList<>();
        for (ModelElement element : elements) {
            Matrix4f transform = new Matrix4f(orientation).mul(elementTransform(element.rotation()));
            for (Face face : element.faces()) {
                ModelQuad quad = new ModelQuad();
                Vector4f uv = face.uv() == null ? defaultUv(element, face) : face.uv();
                int uvRotation = Math.floorMod(face.rotation() / 90, 4);
                for (int i = 0; i < 4; i++) {
                    Vector3f vertex = mapSideToVertex(element.from(), element.to(), i, face.name())
                        .mulPosition(transform);
                    quad.setX(i, vertex.x);
                    quad.setY(i, vertex.y);
                    quad.setZ(i, vertex.z);
                    int uvIndex = (i + uvRotation) & 3;
                    setUV(quad, i, uvIndex < 2 ? uv.x : uv.z, uvIndex == 0 || uvIndex == 3 ? uv.y : uv.w);
                }
                String texture = face.texture();
                bakeFaceSprite(
                    quad,
                    texture.startsWith("#") ? textures.getOrDefault(texture, "minecraft:missing") : texture);
                quad.setColorIndex(face.tintIndex());
                quad.setDirectionalShading(element.shade());
                quad.setEmissiveness(element.lightEmission());
                quad.setHasAmbientOcclusion(useAO);
                quad.setLightFace(ModelQuadFacing.fromForgeDir(rotate(face.name(), orientation)));
                quads.add(quad);
                culls.add(rotate(face.cullFace(), orientation));
            }
        }
        float[] shades = new float[quads.size()];
        int[] lightSides = new int[quads.size()];
        for (int i = 0; i < quads.size(); i++) {
            ModelQuadView quad = quads.get(i);
            shades[i] = quad.hasDirectionalShading() ? ModelISBRH.diffuseLight(quad.getComputedFaceNormal()) : 1;
            ForgeDirection face = quad.getLightFace()
                .toForgeDir();
            float boundary = 0;
            for (int vertex = 0; vertex < 4; vertex++) {
                boundary += face.offsetX * quad.getX(vertex) + face.offsetY * quad.getY(vertex)
                    + face.offsetZ * quad.getZ(vertex);
            }
            int edge = face.offsetX + face.offsetY + face.offsetZ > 0 ? 4 : 0;
            lightSides[i] = boundary >= edge - 0.0001F ? face.ordinal() : ForgeDirection.UNKNOWN.ordinal();
        }
        return new Geometry(
            quads.toArray(new ModelQuadView[0]),
            shades,
            culls.toArray(new ForgeDirection[0]),
            lightSides);
    }

    public ModelDisplay getDisplay(Position position) {
        return display.getOrDefault(position, ModelDisplay.DEFAULT);
    }

    private void bakeFaceSprite(ModelQuad quad, String texture) {
        IIcon sprite = Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getAtlasSprite(texture);
        quad.setSprite(sprite);
        float minU = Float.POSITIVE_INFINITY;
        float minV = Float.POSITIVE_INFINITY;
        float maxU = Float.NEGATIVE_INFINITY;
        float maxV = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            minU = Math.min(minU, quad.getTexU(i));
            minV = Math.min(minV, quad.getTexV(i));
            maxU = Math.max(maxU, quad.getTexU(i));
            maxV = Math.max(maxV, quad.getTexV(i));
        }
        for (int i = 0; i < 4; i++) {
            // Keep each UV island inside its texels, including rotated and mirrored faces.
            float u = insetUv(quad.getTexU(i), minU, maxU, sprite.getIconWidth());
            float v = insetUv(quad.getTexV(i), minV, maxV, sprite.getIconHeight());
            quad.setTexU(i, sprite.getInterpolatedU(u));
            quad.setTexV(i, sprite.getInterpolatedV(v));
        }
    }

    private static float insetUv(float coordinate, float min, float max, int textureSize) {
        if (min == max) return coordinate;
        float pixelsPerUnit = textureSize / 16.0F;
        int firstPixel = (int) Math.floor(min * pixelsPerUnit + 0.0001F);
        int lastPixel = (int) Math.ceil(max * pixelsPerUnit - 0.0001F) - 1;
        if (firstPixel == lastPixel) {
            // Sampling the center protects one-texel trim from transparent neighbors.
            return (firstPixel + 0.5F) / pixelsPerUnit;
        }
        float inset = Math.min(UV_INSET_PIXELS / pixelsPerUnit, (max - min) * 0.5F);
        return coordinate == min ? min + inset : max - inset;
    }

    private static ForgeDirection rotate(ForgeDirection face, Matrix4f orientation) {
        if (face == ForgeDirection.UNKNOWN) return face;
        Vector3f vector = new Vector3f(face.offsetX, face.offsetY, face.offsetZ).mulDirection(orientation);
        for (ForgeDirection result : ForgeDirection.VALID_DIRECTIONS) {
            if (Math.round(vector.x) == result.offsetX && Math.round(vector.y) == result.offsetY
                && Math.round(vector.z) == result.offsetZ) return result;
        }
        throw new IllegalArgumentException("Invalid model face");
    }

    private static Matrix4f elementTransform(Rotation rotation) {
        if (rotation == null) return new Matrix4f();
        Matrix4f transform = rotation.getAffineMatrix();
        if (rotation.rescale()) {
            float scale = 1.0F / (float) Math.cos(rotation.angle());
            Vector3f origin = rotation.origin();
            transform.translate(origin);
            switch (rotation.axis()) {
                case X -> transform.scale(1, scale, scale);
                case Y -> transform.scale(scale, 1, scale);
                case Z -> transform.scale(scale, scale, 1);
            }
            transform.translate(-origin.x, -origin.y, -origin.z);
        }
        return transform;
    }

    private static Vector4f defaultUv(ModelElement element, Face face) {
        Vector3f from = element.from();
        Vector3f to = element.to();
        return switch (face.name()) {
            case DOWN -> new Vector4f(from.x, 1 - to.z, to.x, 1 - from.z).mul(16);
            case UP -> new Vector4f(from.x, from.z, to.x, to.z).mul(16);
            case NORTH -> new Vector4f(1 - to.x, 1 - to.y, 1 - from.x, 1 - from.y).mul(16);
            case SOUTH -> new Vector4f(from.x, 1 - to.y, to.x, 1 - from.y).mul(16);
            case WEST -> new Vector4f(from.z, 1 - to.y, to.z, 1 - from.y).mul(16);
            case EAST -> new Vector4f(1 - to.z, 1 - to.y, 1 - from.z, 1 - from.y).mul(16);
            default -> throw new IllegalArgumentException("A model face must have a direction");
        };
    }

    @Desugar
    public record Geometry(ModelQuadView[] quads, float[] shades, ForgeDirection[] cullFaces, int[] lightSides) {}
}
