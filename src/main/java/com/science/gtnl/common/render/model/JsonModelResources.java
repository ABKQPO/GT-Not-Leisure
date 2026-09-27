package com.science.gtnl.common.render.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position.ModelDisplay;
import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc.ModelLoc;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuad;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.science.gtnl.common.render.model.JsonBlockModel.Geometry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Shared JSON model loading and item-display baking.
 *
 * <p>
 * Extracted verbatim from {@code BeamFormerModel}, which keeps its original private method signatures and now
 * delegates here. This is the single copy of the algorithm; do not reintroduce a second one.
 */
@SideOnly(Side.CLIENT)
public final class JsonModelResources {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(JSONModel.class, new ModelDeserializer())
        .create();

    private JsonModelResources() {}

    /**
     * Resolves a model and its full parent chain from the resource manager.
     *
     * @param location  model id
     * @param manager   the current resource manager
     * @param models    per-pass parse cache; must not be shared across reloads
     * @param resolving per-pass recursion stack
     * @return the fully resolved JSONModel
     * @throws JsonParseException on I/O failure, an empty model, or a cyclic parent reference; the original
     *                            exception is wrapped
     */
    public static JSONModel load(ModelLoc location, IResourceManager manager, Map<ModelLoc, JSONModel> models,
        Set<ModelLoc> resolving) {
        JSONModel cached = models.get(location);
        if (cached != null) return cached;
        if (!resolving.add(location)) throw new JsonParseException("Cyclic model parent: " + location.path());
        ResourceLocation resource = new ResourceLocation(location.owner(), "models/" + location.path() + ".json");
        try (Reader reader = new InputStreamReader(
            manager.getResource(resource)
                .getInputStream(),
            StandardCharsets.UTF_8)) {
            JSONModel model = GSON.fromJson(reader, JSONModel.class);
            if (model == null) throw new JsonParseException("Empty model: " + resource);
            model.resolveParents(parent -> load(parent, manager, models, resolving));
            models.put(location, model);
            return model;
        } catch (IOException exception) {
            throw new JsonParseException("Cannot read model: " + resource, exception);
        } finally {
            resolving.remove(location);
        }
    }

    /**
     * Bakes a display-transformed copy of a geometry.
     *
     * @param source  original NORTH/UP geometry
     * @param display the resolved display entry
     * @param gui     whether to fit the projected bounds into the fourteen-pixel GUI slot
     * @return an independent transformed Geometry; {@code source} is never modified. Quad emissiveness and
     *         directional shading are copied because GTNHLib's copy constructor omits them.
     */
    public static Geometry bakeItem(Geometry source, ModelDisplay display, boolean gui) {
        Vector3f rotation = display.rotation();
        Matrix4f transform = new Matrix4f().translation(new Vector3f(display.translation()).div(16))
            .rotateX((float) Math.toRadians(rotation.x))
            .rotateY((float) Math.toRadians(rotation.y))
            .rotateZ((float) Math.toRadians(rotation.z))
            .scale(display.scale())
            .translate(-0.5F, -0.5F, -0.5F);
        ModelQuadView[] quads = new ModelQuadView[source.quads().length];
        float[] shades = new float[quads.length];
        Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
        Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);
        Vector3f vertex = new Vector3f();
        for (int i = 0; i < quads.length; i++) {
            ModelQuad quad = new ModelQuad(source.quads()[i]);
            // GTNHLib's copy constructor omits these lighting properties.
            quad.setDirectionalShading(source.quads()[i].hasDirectionalShading());
            quad.setEmissiveness(source.quads()[i].getEmissiveness());
            for (int j = 0; j < 4; j++) {
                vertex.set(quad.getX(j), quad.getY(j), quad.getZ(j))
                    .mulPosition(transform);
                min.min(vertex);
                max.max(vertex);
                quad.setX(j, vertex.x);
                quad.setY(j, vertex.y);
                quad.setZ(j, vertex.z);
            }
            shades[i] = quad.hasDirectionalShading() ? ModelISBRH.diffuseLight(quad.getComputedFaceNormal()) : 1;
            quads[i] = quad;
        }
        if (gui && quads.length > 0) {
            // Fit the actual projected bounds into fourteen pixels, centered in the sixteen-pixel slot.
            float extent = Math.max(max.x - min.x, max.y - min.y);
            float scale = extent > 0 ? 14.0F / 16.0F / extent : 1;
            Vector3f center = new Vector3f(min).add(max)
                .mul(0.5F);
            for (ModelQuadView view : quads) {
                ModelQuad quad = (ModelQuad) view;
                for (int j = 0; j < 4; j++) {
                    quad.setX(j, (quad.getX(j) - center.x) * scale);
                    quad.setY(j, (quad.getY(j) - center.y) * scale);
                    quad.setZ(j, (quad.getZ(j) - center.z) * scale);
                }
            }
        }
        // Items only consume quads and shades; world culling and neighbor lighting remain unused.
        return new Geometry(quads, shades, source.cullFaces(), source.lightSides());
    }
}
