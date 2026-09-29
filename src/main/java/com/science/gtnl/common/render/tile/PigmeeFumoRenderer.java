// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.render.tile;

import java.util.List;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.science.gtnl.common.block.blocks.BlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoModel;
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoRenderHelper;
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoRenderHelper.FacedQuad;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PigmeeFumoRenderer extends TileEntitySpecialRenderer {

    public final int[] brightness = new int[7];

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
        if (!(tile instanceof TileEntityPigmeeFumo fumo)) return;
        IBlockAccess world = fumo.getWorldObj();
        if (world == null) return;

        boolean spinning = fumo.isSpinning();
        BakedModel model = PigmeeFumoModel.INSTANCE.get(BlockPigmeeFumo.orientationOf(fumo.getBlockMetadata()));
        if (model == null) return;

        PigmeeFumoRenderHelper.fillBrightness(world, tile.xCoord, tile.yCoord, tile.zCoord, brightness);

        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            if (spinning) {
                GL11.glTranslatef(0.5F, 0.0F, 0.5F);
                GL11.glRotatef(fumo.getRenderYRot(), 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
            }
            List<FacedQuad> quads = PigmeeFumoRenderHelper.getAllQuads(model);
            PigmeeFumoRenderHelper.drawWorld(quads, brightness);
        } finally {
            GL11.glPopMatrix();
        }
    }
}
