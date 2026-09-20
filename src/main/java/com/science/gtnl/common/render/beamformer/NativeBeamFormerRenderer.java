package com.science.gtnl.common.render.beamformer;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBeamFormerRenderer;

public class NativeBeamFormerRenderer implements IBeamFormerRenderer {

    private NativeBeamFormerRenderer() {}

    public static IBeamFormerRenderer create() {
        return new NativeBeamFormerRenderer();
    }

    @Override
    public boolean shouldRenderDynamic(IBeamFormer partBeamFormer) {
        return true;
    }

    @Override
    public void renderDynamic(IBeamFormer partBeamFormer, double x, double y, double z, float partialTicks) {
        if (partBeamFormer == null || !partBeamFormer.shouldRenderBeam()) return;
        double start = partBeamFormer.getRenderOffset();
        double length = partBeamFormer.getBeamLength() - start - partBeamFormer.getClientOtherOffset();
        if (length <= 0) return;

        BeamFormerRenderHelper.StaticBloomMetadata metadata = BeamFormerRenderHelper.getBloomMetadata(partBeamFormer);

        float[] rgb = BeamFormerRenderHelper.getColor(partBeamFormer);

        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

            GL11.glRotatef(metadata.yaw(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(metadata.pitch(), 1.0F, 0.0F, 0.0F);

            GL11.glTranslated(-0.5, start, -0.5);

            BeaconRenderHelper.renderBeamSegment(
                0,
                0,
                0,
                partialTicks,
                1,
                (double) partBeamFormer.getWorld()
                    .getTotalWorldTime(),
                0,
                length,
                rgb,
                0.075 * 1.6,
                0.075 * 2);

        } finally {
            GL11.glPopMatrix();
        }
    }
}
