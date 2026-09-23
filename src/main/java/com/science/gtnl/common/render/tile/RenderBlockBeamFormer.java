package com.science.gtnl.common.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.api.util.NormI8;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.science.gtnl.common.block.blocks.BlockBeamFormer;
import com.science.gtnl.common.block.blocks.tile.TileEntityBeamFormer;
import com.science.gtnl.common.render.beamformer.BeamFormerModel;
import com.science.gtnl.common.render.beamformer.BeamFormerRenderHelper;
import com.science.gtnl.common.render.model.JsonBlockModel.Geometry;

import appeng.client.render.BaseBlockRender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockBeamFormer extends BaseBlockRender<BlockBeamFormer, TileEntityBeamFormer> {

    private static final ThreadLocal<int[]> BRIGHTNESS = ThreadLocal.withInitial(() -> new int[7]);

    public RenderBlockBeamFormer() {
        super(true, Double.MAX_VALUE);
    }

    @Override
    public boolean renderInWorld(BlockBeamFormer block, IBlockAccess world, int x, int y, int z,
        RenderBlocks renderer) {
        TileEntityBeamFormer tile = block.getTileEntity(world, x, y, z);
        if (tile == null) return false;
        Geometry model = BeamFormerModel.INSTANCE.get(tile.getForward(), tile.getUp());
        if (model == null) return renderer.renderStandardBlock(block, x, y, z);
        int[] brightness = BRIGHTNESS.get();
        brightness[6] = block.getMixedBrightnessForBlock(world, x, y, z);
        int visible = 1 << 6;
        for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
            int nx = x + face.offsetX;
            int ny = y + face.offsetY;
            int nz = z + face.offsetZ;
            brightness[face.ordinal()] = block.getMixedBrightnessForBlock(world, nx, ny, nz);
            if (renderer.renderAllFaces || !world.getBlock(nx, ny, nz)
                .isOpaqueCube()) visible |= 1 << face.ordinal();
        }
        Tessellator tessellator = TessellatorManager.get();
        IIcon override = renderer.overrideBlockTexture;
        for (int i = 0; i < model.quads().length; i++) {
            if ((visible & 1 << model.cullFaces()[i].ordinal()) == 0) continue;
            ModelQuadView quad = model.quads()[i];
            int light = brightness[model.lightSides()[i]];
            int emission = quad.getEmissiveness();
            tessellator
                .setBrightness(Math.max(light & 0xF00000, emission << 20) | Math.max(light & 0xF0, emission << 4));
            float shade = model.shades()[i];
            tessellator.setColorOpaque_F(shade, shade, shade);
            emit(tessellator, quad, x, y, z, override);
        }
        return true;
    }

    @Override
    public void renderTile(BlockBeamFormer block, TileEntityBeamFormer tile, Tessellator tessellator, double x,
        double y, double z, float partialTicks, RenderBlocks renderer) {
        BeamFormerRenderHelper.renderDynamic(tile, x, y, z, partialTicks);
    }

    @Override
    public void renderInventory(BlockBeamFormer block, ItemStack item, RenderBlocks renderer,
        IItemRenderer.ItemRenderType type, Object[] data) {
        Geometry model = BeamFormerModel.INSTANCE.get(ForgeDirection.NORTH, ForgeDirection.UP);
        if (model == null) {
            super.renderInventory(block, item, renderer, type, data);
            return;
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            Tessellator tessellator = TessellatorManager.get();
            tessellator.startDrawingQuads();
            for (ModelQuadView quad : model.quads()) {
                var normal = quad.getComputedFaceNormal();
                tessellator.setNormal(NormI8.unpackX(normal), NormI8.unpackY(normal), NormI8.unpackZ(normal));
                tessellator.setColorOpaque_F(1, 1, 1);
                emit(tessellator, quad, 0, 0, 0, null);
            }
            tessellator.draw();
        } finally {
            GL11.glPopMatrix();
        }
    }

    private static void emit(Tessellator tessellator, ModelQuadView quad, int x, int y, int z, IIcon override) {
        for (int vertex = 0; vertex < 4; vertex++) {
            float u = quad.getTexU(vertex);
            float v = quad.getTexV(vertex);
            if (override != null) {
                IIcon sprite = (IIcon) quad.celeritas$getSprite();
                u = override.getInterpolatedU((u - sprite.getMinU()) / (sprite.getMaxU() - sprite.getMinU()) * 16);
                v = override.getInterpolatedV((v - sprite.getMinV()) / (sprite.getMaxV() - sprite.getMinV()) * 16);
            }
            tessellator.addVertexWithUV(
                x + (double) quad.getX(vertex),
                y + (double) quad.getY(vertex),
                z + (double) quad.getZ(vertex),
                u,
                v);
        }
    }
}
