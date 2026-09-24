package com.science.gtnl.client.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;

/** Matches the original GeckoLib UV orientation on the two flat hat-brim layers. */
final class MajoHatBrimBox extends ModelBox {

    private final TexturedQuad top;
    private final TexturedQuad bottom;

    MajoHatBrimBox(ModelRenderer renderer, int u, int v, float x, float y, float z, int width, int depth) {
        super(renderer, u, v, x, y, z, width, 0, depth, 0);

        PositionTextureVertex[] corners = new PositionTextureVertex[] {
            vertex(x + width, y, z),
            vertex(x + width, y, z + depth),
            vertex(x, y, z + depth),
            vertex(x, y, z) };
        top = new TexturedQuad(
            corners.clone(),
            u + depth,
            v,
            u + depth + width,
            v + depth,
            renderer.textureWidth,
            renderer.textureHeight);
        bottom = new TexturedQuad(
            corners.clone(),
            u + depth + width,
            v + depth,
            u + depth + width + width,
            v,
            renderer.textureWidth,
            renderer.textureHeight);
        bottom.flipFace();
    }

    private static PositionTextureVertex vertex(float x, float y, float z) {
        return new PositionTextureVertex(x, y, z, 0, 0);
    }

    @Override
    public void render(Tessellator tessellator, float scale) {
        top.draw(tessellator, scale);
        bottom.draw(tessellator, scale);
    }
}
